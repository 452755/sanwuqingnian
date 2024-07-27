package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.toolbox.Loger;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.push.PushReceiver;
import com.emms.push.PushService;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Factory;
import com.emms.schema.Operator;
import com.emms.schema.System_FunctionSetting;
import com.emms.ui.EquipmentSummaryDialog;
import com.emms.ui.KProgressHUD;
import com.emms.ui.UserRoleDialog;
import com.emms.util.BuildConfig;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.NetworkConnectChangedReceiver;
import com.emms.util.ServiceUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.tencent.bugly.crashreport.CrashReport;
import com.zxing.android.decoding.Intents;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends NfcActivity implements View.OnClickListener {
    private Context mContext=this;
    private EditText inputPassWord;
    private EditText inputname;
    private KProgressHUD hud;
    private static final String FILE_NAME = "emms.apk";
    private Handler pushHandler = PushService.mHandler;
    private AlertDialog dialog ;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppApplication.KeepLive=false;
        ServiceUtils.stopKeepLiveService(this);
//        BroadcastUtils.stopKeepLiveBroadcast(this);
//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
//        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(new ScreenOnAndOffBroadcast(),intentFilter);
        pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, new LinkedHashSet<>()));
        pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, ""));
        setStyleCustom();
        initView();
        selectFactory();
        initLanguageBroadcastReceiver();
    }

    private void initLanguageBroadcastReceiver() {
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       updateView();
                    }
                });
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocaleUtils.LANGUAGE_SETTING_ACTION);
        registerReceiver(receiver, filter);
    }

    private void selectFactory(){
        if(SharedPreferenceManager.getFactory(mContext)==null){
            ChangeServerConnectBaseOnNetwork();
            HttpUtils.getWithoutCookiesByUrl(mContext, BuildConfig.getBaseUrl(mContext) + BuildConfig.FactoryListApi, new HttpParams(), new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    if("".equals(t)||t==null){
                        ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetFactoryList"),mContext);
                    }else {
                        JsonArrayElement jsonArrayElement=new JsonArrayElement(t);
                        ArrayList<ObjectElement> s=new ArrayList<>();
                        for(int i=0;i<jsonArrayElement.size();i++){
                            ObjectElement objectElement=jsonArrayElement.get(i).asObjectElement();
                            objectElement.set(Equipment.EQUIPMENT_NAME,objectElement.get("factoryCode"));
                            s.add(objectElement);
                        }
                        final EquipmentSummaryDialog equipmentSummaryDialog=new EquipmentSummaryDialog(mContext,s);
                        equipmentSummaryDialog.dismissCancelButton();
                        equipmentSummaryDialog.setTitle(LocaleUtils.getI18nValue("SelectFactory"));
                        equipmentSummaryDialog.setCancelable(false);
                        equipmentSummaryDialog.show();
                        equipmentSummaryDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ObjectElement objectElement=equipmentSummaryDialog.getList().get(position);
                                String factory=DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_NAME));
                                SharedPreferenceManager.setFactory(mContext,factory);
                                SharedPreferenceManager.setInteranetUrl(mContext,DataUtil.isDataElementNull(objectElement.get("IntranetURL")));
                                SharedPreferenceManager.setExtranetUrl(mContext,DataUtil.isDataElementNull(objectElement.get("ExtranetURL")));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        equipmentSummaryDialog.dismiss();
                                        getVersion();
                                    }
                                });
                            }
                        });
                    }
                }
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetFactoryList")+"/n"+strMsg,mContext);
                }
            });


        }else {
            initNetWorklistByServe();
//            initNetWorklist(new StoreCallback() {
//                @Override
//                public void success(DataElement element, String resource) {
//                    if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
//                        for (DataElement dataElement : element.asArrayElement()) {
//                            if (!NetworkConnectChangedReceiver.mNetworkList.contains(DataUtil.isDataElementNull(dataElement.asObjectElement().get(DataDictionary.DATA_NAME)))) {
//                                NetworkConnectChangedReceiver.mNetworkList.add(DataUtil.isDataElementNull(dataElement.asObjectElement().get(DataDictionary.DATA_NAME)));
//                            }
//                        }
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ChangeServerConnectBaseOnNetwork();
//                            // BuildConfig.NetWorkSetting(mContext);
//                            getVersion();
//                        }
//                    });
//
//                }
//
//                @Override
//                public void failure(DatastoreException ex, String resource) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ChangeServerConnectBaseOnNetwork();
//                            //BuildConfig.NetWorkSetting(mContext);
//                            getVersion();
//                        }
//                    });
//                }
//            });

        }
    }

    /**
     * create by jason
     */
    private void initNetWorklistByServe(){

        HttpParams params = new HttpParams();
        HttpUtils.post(mContext, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'NetSetting'%20and%20DataDescr%20eq%20'intranet'%20and%20factory_id%20eq%20'" + "GEW" + "'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("null")||TextUtils.isEmpty(t)){
                    LogUtils.e("返回数据为空");
                    return;
                }
                LogUtils.e("initNetWorklistByServe---测试成功--->"+t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray() && jsonArrayElement.asArrayElement().size() > 0) {
                    for (DataElement dataElement : jsonArrayElement.asArrayElement()) {
                        if (!NetworkConnectChangedReceiver.mNetworkList.contains(DataUtil.isDataElementNull(dataElement.asObjectElement().get(DataDictionary.DATA_NAME)))) {
                            NetworkConnectChangedReceiver.mNetworkList.add(DataUtil.isDataElementNull(dataElement.asObjectElement().get(DataDictionary.DATA_NAME)));
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChangeServerConnectBaseOnNetwork();
                        // BuildConfig.NetWorkSetting(mContext);
                        getVersion();
                    }
                });
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChangeServerConnectBaseOnNetwork();
                        //BuildConfig.NetWorkSetting(mContext);
                        getVersion();
                    }
                });
            }
        });

    }

   private void DoInit(){
       if(SharedPreferenceManager.getExtranetUrl(mContext)==null){
           HttpUtils.getWithoutCookiesByUrl(mContext, BuildConfig.getBaseUrl(mContext) + BuildConfig.FactoryListApi, new HttpParams(), new HttpCallback() {
               @Override
               public void onSuccess(String t) {
                   super.onSuccess(t);
                   JsonArrayElement jsonArrayElement=new JsonArrayElement(t);
                   for(int i=0;i<jsonArrayElement.size();i++){
                       ObjectElement objectElement=jsonArrayElement.get(i).asObjectElement();
                       if(SharedPreferenceManager.getFactory(mContext).equals(DataUtil.isDataElementNull(objectElement.get("factoryCode")))){
                           SharedPreferenceManager.setExtranetUrl(mContext,DataUtil.isDataElementNull(objectElement.get("ExtranetURL")));
                           SharedPreferenceManager.setInteranetUrl(mContext,DataUtil.isDataElementNull(objectElement.get("IntranetURL")));
                           break;
                       }
                   }
                   LogUtils.e("原来的doGetDB方法");
//                   doGetDB();
               }

               @Override
               public void onFailure(int errorNo, String strMsg) {
                   super.onFailure(errorNo, strMsg);
                   ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetFactoryList")+"/n"+strMsg,mContext);
               }
           });
       }else {
           LogUtils.e("原来的doGetDB方法");
//           doGetDB();
       }
   }
//    private void doGetDB(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(!getIntent().getBooleanExtra("FromCusActivity", false)) {
//                    if(SharedPreferenceManager.getDatabaseVersion(mContext)==null||Integer.valueOf(SharedPreferenceManager.getDatabaseVersion(mContext))<DBVersion){
//                        getDBFromServer(getDBZipFile());
//                    }else {
//                        getNewDataFromServer();
//                    }
//                }
//                if (mAdapter!=null&&!mAdapter.isEnabled()) {
//                    showWirelessSettingsDialog();
//                }
//            }
//        });
//    }
    @Override
    protected void onRestart() {
        super.onRestart();
//        if(SharedPreferenceManager.getLanguageChange(this)){
            initView();
//            SharedPreferenceManager.setLanguageChange(this,false);
//        }
        LogUtils.e("原有获取数据库更新方法getNewDataFromServer()");
//        if(SharedPreferenceManager.getLoginData(mContext)!=null) {
//            getNewDataFromServer();
//        }
    }

    private void initView() {
        inputPassWord = (EditText) findViewById(R.id.inputPassWord);
        inputname = (EditText) findViewById(R.id.inputUserName);
        inputPassWord.setHint(LocaleUtils.getI18nValue("login_password_hint"));
        inputname.setHint(LocaleUtils.getI18nValue("login_id_hint"));
        updateView();
        inputname.setText(SharedPreferenceManager.getUserName(this));
        inputPassWord.setText(SharedPreferenceManager.getPassWord(this));
        inputPassWord.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        inputPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        findViewById(R.id.login).setOnClickListener(this);
//        machine.setOnClickListener(this);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(LocaleUtils.getI18nValue("logining"))
                .setCancellable(true);
        findViewById(R.id.systemSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,SystemSettingActivity.class));
            }
        });
//        initVersionChange();
    }

    private void updateView() {
        ((TextView) findViewById(R.id.login)).setText(LocaleUtils.getI18nValue("login"));
        ((TextView) findViewById(R.id.setting)).setText(LocaleUtils.getI18nValue("systemSetting"));
        ((TextView) findViewById(R.id.sweetTips)).setText(LocaleUtils.getI18nValue("sweetTips"));
        ((TextView) findViewById(R.id.tips)).setText(LocaleUtils.getI18nValue("pleaseInputPasswordOrScanICcard"));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ConfigurationManager.getInstance().startToGetNewConfig(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //点击登录按钮，根据账号密码进行登录
                final String userid = inputname.getText().toString().toUpperCase();
                final String password = inputPassWord.getText().toString().toUpperCase();
                if (!hasNetworkConnection()) {
//                    showDialog(getString(LocaleUtils.getI18nValue("warning_title),
//                            getString(LocaleUtils.getI18nValue("network_error));
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("network_error"),mContext);
                    return;
                }

                if (userid.length() == 0) {
//                    showDialog(getString(LocaleUtils.getI18nValue("warning_title),
//                            getString(LocaleUtils.getI18nValue("warning_message_no_user));
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("warning_message_no_user"),mContext);
                    return;
                }
                if (password.length() == 0) {
//                    showDialog(getString(LocaleUtils.getI18nValue("warning_title),
//                            getString(LocaleUtils.getI18nValue("warning_message_no_password));
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("warning_message_no_password"),mContext);
                    return;
                }
                hud.show();
                HttpUtils.login(LoginActivity.this,userid, password, new HttpCallback() {

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Loger.debug(errorNo + ":strMsg");
                        hud.dismiss();
                        if(strMsg!=null){
                            Toast.makeText(mContext,
                                    LocaleUtils.getI18nValue("network_error")+"\n"+strMsg,
                                    Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(mContext,
                                    LocaleUtils.getI18nValue("network_error"),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        super.onSuccess(headers, t);
                        LogUtils.e("登录成功后 headers--->"+headers);
                        SaveCookies(headers);
                    }

                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        hud.dismiss();
                        SharedPreferenceManager.setUserName(LoginActivity.this, userid);
                        SharedPreferenceManager.setPassWord(LoginActivity.this, password);
                        LoginSuccessEvent(t,true);
                    }
                });
                break;
//            case R.id.machine:
//                Intent intentMachine = new Intent(LoginActivity.this, MachineActivity.class);
//                startActivity(intentMachine);
//                break;
        }
    }



    private boolean hasNetworkConnection() {
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) {
                return false;
            }
            NetworkInfo.State network = info.getState();
            return network == NetworkInfo.State.CONNECTED;
        }catch (Exception e){
            CrashReport.postCatchedException(e);
        }
        return false;
    }

    /**
     * 保存Cookie
     */
    public void SaveCookies( Map<String, String> headers)
    {
        //保存登录信息Cookies
        if (headers == null)
            return;
            String cookie=headers.get("Set-Cookie");
        if(cookie!=null) {
            String[] cookies = cookie.split(";");
            // String[] cookievalues = cookies[0].split("=");
            SharedPreferenceManager.setCookie(LoginActivity.this, cookies[0]);

        }
    }
    //下载DB文件


    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
//            String iccardID = "3949476802";
            if (iccardID == null) {
                return;
            } else if (iccardID.equals("")) {
                return;
            }
            //刷卡登录
            getOperatorInfoFromServer(iccardID);
        }
    }

//    private void setStyleBasic(){
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
//        builder.statusBarDrawable = R.drawable.ic_emms;
//        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
//        builder.notificationDefaults = Notification.DEFAULT_ALL;
//        //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
//        JPushInterface.setDefaultPushNotificationBuilder(builder);
//        JPushInterface.setPushNotificationBuilder(4, builder);
//    }

    private void setStyleCustom(){
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(this,R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.ic_emms;
        builder.layoutIconId=R.drawable.ic_emms;
        builder.statusBarDrawable=R.drawable.ic_emms;
//        builder.layoutIconDrawable = R.mipmap.emmsa;
//        builder.layoutIconId=R.mipmap.emmsa;
//        builder.statusBarDrawable=R.mipmap.emmsa;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        //builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
        builder.notificationDefaults = Notification.DEFAULT_ALL;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setDefaultPushNotificationBuilder(builder);
        JPushInterface.setPushNotificationBuilder(2, builder);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void getOperatorInfoFromServer(String iccardID){
        showCustomDialog(LocaleUtils.getI18nValue("logining"));
        HttpParams httpParams=new HttpParams();
        httpParams.put("ICCardID",iccardID);
        HttpUtils.getWithoutCookies(this, "Token", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LoginSuccessEvent(t,false);
            }
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                SaveCookies(headers);
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast toast=Toast.makeText(mContext,LocaleUtils.getI18nValue("scanICCardFail"),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                dismissCustomDialog();
            }
        });
    }
    private void LoginSuccessEvent(String t,boolean isAccountPasswordLogin){
        if(t!=null){
            LogUtils.e("登录成功事件--->"+t);
            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
            if(jsonObjectElement.get("Success")!=null&&jsonObjectElement.get("Success").valueAsBoolean()){
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    int code = Integer.parseInt(jsonObject.get("Result").toString());
                    //boolean isSuccess = jsonObject.get("Success").equals(true);
                    if ((code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS ||
                            code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS_AUTO) ) {
                        String Msg=jsonObject.getString("Msg");
                        SharedPreferenceManager.setMsg(LoginActivity.this,Msg);
                        String userData =jsonObject.getString("UserData");
                        SharedPreferenceManager.setUserData(LoginActivity.this, userData);
                        final String data=jsonObject.getString("Data");
                        JsonObjectElement userD=new JsonObjectElement(data);
                        String fromFactory=DataUtil.isDataElementNull(userD.get("FromFactory"));
                        if(!SharedPreferenceManager.getFactory(mContext).equals(fromFactory)){
                            dismissCustomDialog();
                            ToastUtil.showToastLong(LocaleUtils.getI18nValue("AccountFactoryDifferentFromSelectFactory"),mContext);
                            return;
                        }
                        SharedPreferenceManager.setLoginData(LoginActivity.this,data);
                        JsonObjectElement json=new JsonObjectElement(Msg);
                        final ArrayElement arrayElement=json.get("UserRoles").asArrayElement();
                        if(arrayElement.size()==0){
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoRoleInfo"),mContext);
                        }else{
                            SetRole(arrayElement.get(0).asObjectElement(),data,userData);
                        }
//                        else {
//                            final ArrayList<ObjectElement> list=new ArrayList<>();
//                            for(int i=0;i<arrayElement.size();i++){
//                                list.add(arrayElement.get(i).asObjectElement());
//                            }
//                            UserRoleDialog userRoleDialog=new UserRoleDialog(mContext,list);
//                            userRoleDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    SetRole(list.get(position), data);
//                                }
//                            });
//                            userRoleDialog.show();
//                        }
                    } else if (code == Constants.REQUEST_CODE_FROZEN_ACCOUNT) {
                        Toast.makeText(mContext, LocaleUtils.getI18nValue("warning_message_frozen"), Toast.LENGTH_SHORT).show();
                    } else if (code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_FAIL) {
                        if(isAccountPasswordLogin){
                        Toast.makeText(mContext, LocaleUtils.getI18nValue("warning_message_error"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    CrashReport.postCatchedException(e);
                    if(isAccountPasswordLogin){
                    hud.dismiss();
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("warning_message_error"), Toast.LENGTH_SHORT).show();}
                }catch (Throwable e){
                    CrashReport.postCatchedException(e);
                }
            }else{
                if(isAccountPasswordLogin){
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("AccountOrPasswordFail"),mContext);
                }else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoCardDetail"),mContext);
                }
            }
        }
        dismissCustomDialog();
    }
    private void initPush(String data){
      try {
    //调用JPush API设置Tag\
    String Organise_ID = new JSONObject(data).get("Organise_ID").toString();
    Set<String> tagSet = new LinkedHashSet<>();
    //tagSet.add(userid);
    tagSet.add("1002");
    String[] or = Organise_ID.split(",");
    Collections.addAll(tagSet, or);
    //tagSet.add(new JSONObject(data).get(Operator.OPERATOR_ID).toString());
          if(PushReceiver.PushTagOrAliasList==null){
              PushReceiver.PushTagOrAliasList=new ArrayList<>();
          }
          PushReceiver.PushTagOrAliasList.clear();
          Collections.addAll(PushReceiver.PushTagOrAliasList,or);
          PushReceiver.PushTagOrAliasList.add(new JSONObject(data).get(Operator.OPERATOR_ID).toString());
    JPushInterface.resumePush(mContext);
    //setStyleBasic();
    setStyleCustom();
    pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, tagSet));
    pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, new JSONObject(data).get(Operator.OPERATOR_ID).toString()));
          }catch (Throwable e){
          CrashReport.postCatchedException(e);
          }
    }
    private void SetRole(ObjectElement objectElement,String data,String UserData){
        JsonObjectElement userD=new JsonObjectElement(UserData);
        String userroleId =DataUtil.isDataElementNull(userD.get("UserRoles"));
        LogUtils.e("设置用户权限id---->"+userroleId);
        SharedPreferenceManager.setUserRoleIDS(mContext,userroleId);
        SharedPreferenceManager.setUserRoleID(mContext,DataUtil.isDataElementNull(objectElement.get("UserRole_ID")));
        SharedPreferenceManager.setUserModuleList(mContext,DataUtil.isDataElementNull(objectElement.get("AppInterfaceList")));
        Intent intent=new Intent(LoginActivity.this,CusActivity.class);
        String modelList = DataUtil.isDataElementNull(objectElement.get("AppInterfaceList"));
        intent.putExtra("Module_ID_List",modelList);
        startActivity(intent);
        initPush(data);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void handleVersionUpdate(final Context context, String element) {
        JsonObjectElement json=new JsonObjectElement(element);
        final ObjectElement data=json.get(Data.PAGE_DATA).asArrayElement().get(0).asObjectElement();
        int version=data.get("Version").valueAsInt();
        try {
            PackageInfo packageInfo=context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int CurrentVersion=packageInfo.versionCode;
            if(CurrentVersion<version){
                if (data.get("Content") != null && data.get("Content").isPrimitive()) {
                   /* if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH
                            || LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())==LocaleUtils.SupportedLanguage.ENGLISH) {
                        DataUtil.getDataFromLanguageTranslation(context.getApplicationContext(),DataUtil.isDataElementNull(data.get("Content")), new StoreCallback() {
                            @Override
                            public void success(DataElement e, String resource) {
                                if(e.isArray()&&e.asArrayElement().size()>0) {
                                    showDialog(context,data,DataUtil.isDataElementNull(e.asArrayElement().get(0).asObjectElement().get("Translation_Display"))
                                            + "1.0." + DataUtil.isDataElementNull(data.get("Version")));
                                }else {
                                    showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                            +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                                }
                            }

                            @Override
                            public void failure(DatastoreException ex, String resource) {
                                showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                        +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                            }
                        });
                    }else {
                        showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                    }*/
                    showDialog(context,data,LocaleUtils.getI18nValue(DataUtil.isDataElementNull(data.get("Content")))
                            +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                }else {
                    DoInit();
                }

            }else {
                DoInit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            DoInit();
        }
    }
    public void showDialog(final Context context, final ObjectElement element, final String message) {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try{
                    if (dialog == null || dialog.getContext() != context) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                context);
                        dialog = builder.create();
                    }
                    DataElement e = element.get("Header");
                    if (e != null && e.isPrimitive()) {
                        dialog.setTitle(e.asPrimitiveElement()
                                .valueAsString());
                    }

                    //e = element.get("Content");
                    dialog.setMessage(message);
                    dialog.setCancelable(false);

                    e = element.get("ConfirmButtonText");
                    if (e != null&&e.isPrimitive()) {
//                    final DataElement clickEventUrl = element
//                            .asObjectElement().get("URL");
//                    final Reference url = new Reference(clickEventUrl.asPrimitiveElement().valueAsString());
                        String pathDir = FILE_NAME;
                        if (DataUtil.getDBDirPath(mContext) != null) {
                            //noinspection ConstantConditions
                            pathDir = DataUtil.getDBDirPath(mContext) + "/" + FILE_NAME;
                        }
                        final File file = new File(pathDir);
                        //final Reference destination = new Reference(file.getAbsolutePath());
//                    if (downloadTask == null) {
//                        downloadTask = new DownloadTask(context, url, destination);
//                    }
//                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                DoInit();
//                            }
//                        });
                        dialog.setButton(LocaleUtils.getI18nValue("Update"), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ProgressBar progressView = new ProgressBar(context);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setCancelable(false);
                                alertDialog.setView(progressView);
                                alertDialog.setTitle(LocaleUtils.getI18nValue("downloading"));
                                final Dialog d=alertDialog.create();
                                d.show();
                                HttpUtils.download(context, file.getAbsolutePath(), DataUtil.isDataElementNull(element.get("URL")), null, new HttpCallback() {
                                    @Override
                                    public void onSuccess(String t) {
                                        super.onSuccess(t);
                                        d.dismiss();
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int errorNo, String strMsg) {
                                        if(!isFinishing()) {
                                            d.dismiss();
                                            DoInit();
                                        }
                                        super.onFailure(errorNo, strMsg);
                                    }
                                });

                            }
                        });
                    }
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }catch (Exception e){
                    CrashReport.postCatchedException(e);
                    DoInit();
                }
            }
        });
    }
    private void getVersion(){
//        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
//        HttpParams httpParams=new HttpParams();
//        HttpUtils.getWithoutCookies(mContext, "System_Version/GetAppDownloadInfo", httpParams, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                dismissCustomDialog();
//                if(t!=null) {
//                    handleVersionUpdate(mContext, t);
//                }
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                dismissCustomDialog();
//                DoInit();
//            }
//        });
    }
//    private int ClickTime=0;
//    private Timer timer=new Timer();
//    private TimerTask mTimerTask=null;
//   private void initVersionChange(){
//               findViewById(R.id.log).setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(mTimerTask==null){
//					mTimerTask=new TimerTask() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							ClickTime=0;
//							mTimerTask=null;
//						}
//					};
//					timer.schedule(mTimerTask, 3000);
//				}
//				ClickTime++;
//				if(ClickTime>=5){
//					if(mTimerTask!=null){
//					mTimerTask.cancel();
//					mTimerTask=null;
//					}
//					ClickTime=0;
//					CustomDialog dialog = new com.esquel.epass.ui.CustomDialog(
//							HomeActivity.this, R.layout.customdialog,
//							R.style.dialog);
//					dialog.setItem(R.array.dialog_option_app_envir);
//					dialog.setCancelable(false);
//					dialog.setTag(true);
//					dialog.show();
//				}
//
//			}
//		});
//   }

}
