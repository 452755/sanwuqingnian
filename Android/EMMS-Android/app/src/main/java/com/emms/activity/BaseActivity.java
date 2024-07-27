package com.emms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Factory;
import com.emms.schema.Operator;
import com.emms.ui.KProgressHUD;
import com.emms.ui.LoadingDialog;
import com.emms.util.BuildConfig;
import com.emms.util.DataUtil;
import com.emms.util.DownloadCallback;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.NetworkUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/17.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private KProgressHUD hud;
    private LoadingDialog loadingDialog;
    private Handler mHandler;
    private Context mContext = this;
    public final int DBVersion = 8;//需要进行DB更新的时候+1

    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        setTilteColor();

    }

    @Override
    protected void onStart() {
        super.onStart();
        acquireWakeLock();
    }

    @Override
    protected void onDestroy() {
        releaseWakeLock();
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mHandler = new Handler(getMainLooper());
            if (Build.VERSION.SDK_INT > 14) {
                hud = KProgressHUD.create(this);
            } else {
                loadingDialog = new LoadingDialog(this);
            }
        } catch (Throwable throwable) {
            CrashReport.postCatchedException(throwable);
        }
    }

    //create by jason
    public SqliteStore getSqliteStore() {
        return ((AppApplication) getApplication()).getSqliteStore();
    }

    protected Operator getLoginInfo() {
        Operator operator = null;
        String userData = SharedPreferenceManager.getLoginData(this);
        if (StringUtils.isNotBlank(userData)) {
            try {
                operator = new Operator();
                JsonObjectElement json = new JsonObjectElement(userData);
                operator.setId(Long.valueOf(DataUtil.isDataElementNull(json.get("Operator_ID"))));
                operator.setTeamId(DataUtil.isDataElementNull(json.get("Team_ID")));
                operator.setTeamName(DataUtil.isDataElementNull(json.get("TeamName")));
                operator.setName(DataUtil.isDataElementNull(json.get("Name")));
                operator.setFactoryId(DataUtil.isDataElementNull(json.get("FromFactory")));
                operator.setFromFactory(SharedPreferenceManager.getAppMode(mContext));
                operator.setOrganiseID(DataUtil.isDataElementNull(json.get("Organise_ID")));
                operator.setOperator_no(DataUtil.isDataElementNull(json.get("OperatorNo")));
                operator.setMaintenMan(json.get("IsMaintenMan").valueAsBoolean());
                // operator.setUserRole_ID(json.get("UserRole_ID").valueAsInt());
//                operator = Operator.fromJson(userData, null, Operator.class);
//                JsonObjectElement objectElement=new JsonObjectElement(SharedPreferenceManager.getMsg(this));
//                operator.setUserRole_ID(objectElement.get("UserRole_ID").valueAsInt());
//                operator.setModuleList(DataUtil.isDataElementNull(objectElement.get("AppInterfaceList")));
            } catch (Exception e) {
                //Do nothing
                CrashReport.postCatchedException(e);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"), this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        }
        return operator;
    }

    public KProgressHUD initCustomDialog(String label) {
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setCancellable(true);
        return hud;
    }

    public void showCustomDialog(String label) {
        if (Build.VERSION.SDK_INT > 14) {
            initCustomDialog(label);
            if (hud != null && !hud.isShowing() && !isFinishing()) {
                hud.show();
            }
        } else {
            if (loadingDialog != null && !loadingDialog.isShowing() && !isFinishing()) {
                loadingDialog.show();
            }
        }
    }

    public void dismissCustomDialog() {
        try {
            if (Build.VERSION.SDK_INT > 14) {
                if (hud != null && hud.isShowing() && !isFinishing()) {
                    hud.dismiss();
                }
            } else {
                if (loadingDialog != null && loadingDialog.isShowing() && !isFinishing()) {
                    loadingDialog.dismiss();
                }
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }

    public void getDataBaseUpdateFromServer(DataElement dataElement) {

        if (dataElement == null) {
            LogUtils.e("dataElement为空");
            return;
        }

        HttpParams params = new HttpParams();
        JsonObjectElement data = new JsonObjectElement();
        data.set("LastUpdateTime_BaseOrganise", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_BaseOrganise")));
        data.set("LastUpdateTime_DataDictionary", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataDictionary")));
        data.set("LastUpdateTime_DataType", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataType")));
        data.set("LastUpdateTime_Equipment", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Equipment")));
        data.set("LastUpdateTime_TaskOrganiseRelation", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskOrganiseRelation")));
        data.set("LastUpdateTime_DataRelation", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataRelation")));
        data.set("LastUpdateTime_Language_Translation", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Language_Translation")));
        //data.set("LastUpdateTime_Language_Translation","0x000000000003568F");//用于测试大量数据情况下数据更新效率
        data.set("LastUpdateTime_Languages", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Languages")));
        data.set("LastUpdateTime_System_FunctionSetting", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_System_FunctionSetting")));
        data.set("LastUpdateTime_TaskMessage", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskMessage")));
        if (SharedPreferenceManager.getFactory(this) == null) {
            data.set("Factory_ID", Factory.FACTORY_GEW);
        } else {
            data.set("Factory_ID", SharedPreferenceManager.getFactory(this));
        }
        // data.set("IsNewApp",1);
        // params.put("sqlLiteDBModel",data.toJson());
        params.putJsonParams(data.toJson());
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.postWithoutCookie(this, "SqlToSqliteAPI/DBIncrementGet", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    LogUtils.e("获取更新数据成功---->" + t);
                    SharedPreferenceManager.setDataUpdateTime(getApplicationContext(), new Date().getTime());
                    int Count = 0;
                    try {
                        JsonObjectElement json = new JsonObjectElement(t);
                        ArrayElement dataDictionarys = json.get("DataDictionary").asArrayElement();
                        for (int i = 0; i < dataDictionarys.size(); i++) {
                            LogUtils.e("状态status--->" + dataDictionarys.get(i).asObjectElement().get("Status").valueAsBoolean());
                            int it = dataDictionarys.get(i).asObjectElement().get("Status").valueAsBoolean() ? 1 : 0;
                            dataDictionarys.get(i).asObjectElement().set("Status", it);
                        }
                        if (DataUtil.isInt(DataUtil.isDataElementNull(json.get("Count")))) {
                            if (json.get("Count") != null) {
                                Count = json.get("Count").valueAsInt();
                            }
                        }
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("DataType", EPassSqliteStoreOpenHelper.SCHEMA_DATATYPE);
                        map.put("BaseOrganise", EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE);
                        map.put("DataDictionary", EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY);
                        map.put("Equipment", EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT);
                        map.put("TaskOrganiseRelation", EPassSqliteStoreOpenHelper.SCHEMA_TASK_ORGANISE_RELATION);
                        map.put("DataRelation", EPassSqliteStoreOpenHelper.SCHEMA_DATA_RELATION);
                        map.put("Languages", EPassSqliteStoreOpenHelper.SCHEMA_LANGUAGES);
                        map.put("Language_Translation", EPassSqliteStoreOpenHelper.SCHEMA_LANGUAGE_TRANSLATION);
                        map.put("System_FunctionSetting", EPassSqliteStoreOpenHelper.SCHEMA_SYSTEM_FUNCTION_SETTING);
//                        doInsert(json,0,map);
                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                    }
                } else {
                    dismissCustomDialog();
                }
                // ConfigurationManager.getInstance().startToGetNewConfig(mContext);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }

//    public boolean doInsert(final JsonObjectElement jsonObjectElement, final int index, final Map<String,String> map){
////        LogUtils.e("开始插入数据--->"+jsonObjectElement.toString());
//        if(index >= map.keySet().size()) {
//            dismissCustomDialog();
//            return true;
//        }
//        String key=(String) map.keySet().toArray()[index];
//        Log.e("key",key);
//        if(checkNullAndInsert(jsonObjectElement,key)){
//             getSqliteStore().createElement(jsonObjectElement.get(key), map.get(key), new StoreCallback() {
//                 @Override
//                 public void success(DataElement element, String resource) {
////                     Log.e("finishUpdate","finishUpdate");
////                     LogUtils.e("数据插入成功--->"+element.toJson().toString());
//                     doInsert(jsonObjectElement,index+1,map);
//                 }
//
//                 @Override
//                 public void failure(DatastoreException ex, String resource) {
//                     LogUtils.e("数据插入失败--->"+ex.toString()+"--->"+resource);
//                     Log.e("failUpdate","failUpdate");
//                 }
//             });
//        }else {
//            doInsert(jsonObjectElement,index+1,map);
//        }
//        return false;
//    }

//    public boolean checkNullAndInsert(JsonObjectElement jsonObjectElement,String key){
//
//        if (jsonObjectElement.get(key) != null && jsonObjectElement.get(key).isArray() && jsonObjectElement.get(key).asArrayElement().size() > 0){
//            Log.e(key,String.valueOf(jsonObjectElement.get(key).asArrayElement().size()));
//            return true;
//        }else {
//            return false;
//        }
//    }


    //    public void getDBDataLastUpdateTime(){
//        String sql="    select * from ( select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_BaseOrganise from BaseOrganise)," +
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_DataDictionary from DataDictionary)," +
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_DataType from DataType)," +
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_Equipment from Equipment)," +
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_TaskOrganiseRelation from TaskOrganiseRelation),"+
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_DataRelation from DataRelation), "+
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_Languages from Languages), "+
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_Language_Translation from Language_Translation), "+
//                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_System_FunctionSetting from System_FunctionSetting)";
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
//            @Override
//            public void success(final DataElement element, String resource) {
//                //ConfigurationManager.getInstance().startToGetNewConfig(BaseActivity.this);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtils.e("报错的地方---->"+element.asArrayElement().get(0));
//                        getDataBaseUpdateFromServer(element.asArrayElement().get(0));
//                    }
//                });
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                //ConfigurationManager.getInstance().startToGetNewConfig(BaseActivity.this);
//            }
//        });
//    }
//    private void DoUpdate(final  int i,final String key, final DataElement data,final String resource){
//        try {
//            if (i<data.asArrayElement().size()&&data.asArrayElement().get(i) != null) {
//                getSqliteStore().updateElement(DataUtil.isDataElementNull(data.asArrayElement().get(i).asObjectElement().get(key)),
//                        data.asArrayElement().get(i), resource, new StoreCallback() {
//                            @Override
//                            public void success(DataElement element, String resource) {
//                                Log.e("SuccessUpdate", "SuccessUpdate");
//                                DoUpdate(i + 1, key, data, resource);
//                            }
//
//                            @Override
//                            public void failure(DatastoreException ex, String resource) {
//                                Log.e("FailUpdate", "FailUpdate");
//                                DoUpdate(i + 1, key, data, resource);
//                            }
//                        });
//            }
//        }catch (Exception e){
//             CrashReport.postCatchedException(e);
//        }
//    }
    private void RunDelay(int DelayTime) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissCustomDialog();
            }
        }, DelayTime * 1000);
    }

    //    public void getDBFromServer(final File dbFile){
//        // 下载Db文件
//        showCustomDialog(LocaleUtils.getI18nValue("DownloadDataBase"));
//        HttpParams params=new HttpParams();
//        params.put("factory",SharedPreferenceManager.getFactory(this)==null?Factory.FACTORY_GEW:SharedPreferenceManager.getFactory(this));
//        HttpUtils.getWithoutCookies(this, "SqlToSqliteAPI/GetDBDownloadUrl", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                if(t!=null){
//                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t.trim());
//                    downloadDB(dbFile,DataUtil.isDataElementNull(jsonObjectElement.get("DownloadUrl")));
//                }else {
//                    dismissCustomDialog();
//                }
//            }
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                showErrorDownloadDatabaseDialog(strMsg);
//                dismissCustomDialog();
//            }
//        });
//    }
//    public void downloadDB(final File dbFile,String url){
//        LogUtils.e("数据库下载地址---->"+url);
//        HttpUtils.download(this, dbFile.getAbsolutePath(), url, null, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                try{
//                    com.emms.util.FileUtils fileUtil=new com.emms.util.FileUtils();
//                    fileUtil.upZipFile(dbFile, dbFile.getParentFile().getAbsolutePath(), mContext, new DownloadCallback() {
//                        @Override
//                        public void success(boolean hasUpdate) {
//                            SharedPreferenceManager.setDatabaseVersion(mContext,String.valueOf(DBVersion));
//                            getDBDataLastUpdateTime();
//                        }
//                        @Override
//                        public void fail(Exception e) {
//                        }
//                    });
//                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessDownloadDB"),mContext);
//                }catch (Exception e){
//                    showErrorDownloadDatabaseDialog(null);
//                }finally {
//                    dismissCustomDialog();
//                }
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                if(dbFile.exists()) {
//                    if (dbFile.delete()) {
//                        showErrorDownloadDatabaseDialog(strMsg);
//                    }
//                }else {
//                    showErrorDownloadDatabaseDialog(strMsg);
//                }
//                //ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),mContext);
//                dismissCustomDialog();
//            }
//        });
//    }
    private AlertDialog alertDialog;

    //    public void showErrorDownloadDatabaseDialog(String msg) {
//        try {
//            if (alertDialog == null) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                if(msg==null) {
//                    builder.setMessage(LocaleUtils.getI18nValue("FailDownloadDB"));
//                }else {
//                    builder.setMessage(LocaleUtils.getI18nValue("FailDownloadDB")+"\n"+msg);
//                }
//                builder.setPositiveButton(LocaleUtils.getI18nValue("retry"),
//                        new DialogInterface.OnClickListener() {
//                            @SuppressWarnings("ResultOfMethodCallIgnored")
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                File dbFile=getDBZipFile();
//                                if(dbFile.exists()){
//                                    dbFile.delete();
//                                }
//                                final File db = new File(DataUtil.getDBDirPath(mContext), "/EMMS.db");
//                                if(db.exists()){
//                                    db.delete();
//                                }
////                                getDBFromServer(dbFile);
//                            }
//                        });
//                builder.setCancelable(false);
//                alertDialog = builder.create();
//            }
//            if (alertDialog != null && !alertDialog.isShowing()) {
//                alertDialog.show();
//            }
//        }catch (Exception e){
//            CrashReport.postCatchedException(e);
//        }
//    }
//    public void getNewDataFromServer() {
//        //检测数据库文件是否已经存在，若已存在，则调用增量接口
//        final File db = new File(DataUtil.getDBDirPath(mContext), "/EMMS.db");
//
////        final File dbZip;
////        if(BuildConfig.isDebug){
////            dbZip=new File(getExternalFilesDir(null), "/EMMS_TEST_"+SharedPreferenceManager.getFactory(this)+".zip");
////        }else {
////            dbZip = new File(getExternalFilesDir(null), "/EMMS_"+SharedPreferenceManager.getFactory(this)+".zip");
////        }
//        if(db.exists()){
////            getDBDataLastUpdateTime();
//            return;
//        }
//        final File dbFile = getDBZipFile();
//        switch (BuildConfig.appEnvironment){
//            case DEVELOPMENT:{
//                if(dbFile.exists()){
//                    try{
//                        //解压db文件
//                        HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);
//                    }catch (Throwable e){
//                        CrashReport.postCatchedException(e);
//                        if(dbFile.exists()&&dbFile.delete()){
//                            showErrorDownloadDatabaseDialog(null);
//                        }
//                        ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToUnZipDB"),mContext);
//                    }
//                    return;
//                }
////                getDBFromServer(dbFile);
//                break;
//            }
//            case PROD:
//            case UAT:{
//                if(dbFile.exists()){
//                    try{
//                        //解压db文件
//                        HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);
//                    }catch (Throwable e){
//                        CrashReport.postCatchedException(e);
//                        if(dbFile.exists()&&dbFile.delete()){
//                            showErrorDownloadDatabaseDialog(null);
//                        }
//                        ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToUnZipDB"),mContext);
//                    }
//                    return;
//                }
////                getDBFromServer(dbFile);
//                break;
//            }
//        }
//        if(BuildConfig.isDebug){
//            final File dbFile = getDBZipFile(BuildConfig.appEnvironment);
//            if(dbFile.exists()){
//                try{
//                    //解压db文件
//                    HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);
//                }catch (Throwable e){
//                    CrashReport.postCatchedException(e);
//                    if(dbFile.exists()&&dbFile.delete()){
//                        showErrorDownloadDatabaseDialog(null);
//                    }
//                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToUnZipDB"),mContext);
//                }
//                return;
//            }
//            getDBFromServer(dbFile);
//        }else {
//            final File dbFile = getDBZipFile(BuildConfig.appEnvironment);
//            if(dbFile.exists()){
//                try{
//                    //解压db文件
//                    HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);
//                }catch (Throwable e){
//                    CrashReport.postCatchedException(e);
//                    if(dbFile.exists()&&dbFile.delete()){
//                        showErrorDownloadDatabaseDialog(null);
//                    }
//                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToUnZipDB"),mContext);
//                }
//                return;
//            }
//            getDBFromServer(dbFile);
//        }
//    }
//    private void RunDelayToast(int DelayTime){
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ToastUtil.showToastLong(LocaleUtils.getI18nValue("DataTooMorePleaseWait"),mContext);
//            }
//        },DelayTime*1000);
//    }
    //jason change
    public void initNetWorklist(StoreCallback storeCallback) {
        String Factory = SharedPreferenceManager.getFactory(mContext) == null ? "EGM" : SharedPreferenceManager.getFactory(mContext);
        String sql = "select * from DataDictionary where DataType='NetSetting' and DataDescr='intranet' and Factory_ID='" + Factory + "'";
//            getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, storeCallback);
    }

    public void ChangeServerConnectBaseOnNetwork() {
        try {

            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
//        Toast.makeText(context, "mobile:"+mobileInfo.isConnected()+"\n"+"wifi:"+wifiInfo.isConnected()
//                +"\n"+"active:"+activeInfo.getTypeName(), 1).show();
            if (activeInfo == null) {
                return;
            }
            if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE && mobileInfo.isConnected()) {
                ToastUtil.showToastLong(LocaleUtils.i18nLength() > 0 ? LocaleUtils.getI18nValue("CheckForMONET") : mContext.getString(R.string.CheckForMONET), mContext);
                SharedPreferenceManager.setNetwork(mContext.getApplicationContext(), NetworkUtils.initNetWork(false));
//            BuildConfig.NetWorkSetting(mContext.getApplicationContext());
                return;
            }
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI && wifiInfo.isConnected()) {
                NetworkUtils.DoNetworkChange(mContext);
            }
        } catch (Exception e) {
            LogUtils.e("改变网络的时候报错了---->" + e);
            CrashReport.postCatchedException(e);
        }
//        BuildConfig.NetWorkSetting(mContext);
    }

    //    public File getDBZipFile(){
//        switch (BuildConfig.appEnvironment){
//            case DEVELOPMENT: {
//                return new File(DataUtil.getDBDirPath(mContext), "/EMMS_TEST_" + SharedPreferenceManager.getFactory(mContext) + ".zip");
//            }
//            case UAT:{
//                return new File(DataUtil.getDBDirPath(mContext), "/EMMS_UAT_" + SharedPreferenceManager.getFactory(mContext) + ".zip");
//            }
//            case PROD:{
//                return new File(DataUtil.getDBDirPath(mContext), "/EMMS_" + SharedPreferenceManager.getFactory(mContext) + ".zip");
//            }
//            default:{
//                return new File(DataUtil.getDBDirPath(mContext), "/EMMS_" + SharedPreferenceManager.getFactory(mContext) + ".zip");
//            }
//        }
//
//    }
    public void setTilteColor() {
        try {
            switch (BuildConfig.appEnvironment) {
                case UAT: {
                    if (findViewById(R.id.tv_title) != null && findViewById(R.id.tv_title).getParent() != null) {
                        ((View) findViewById(R.id.tv_title).getParent()).setBackgroundColor(getResources().getColor(R.color.main_color_debug));
                    }
                    if (findViewById(R.id.lay_cus) != null) {
                        findViewById(R.id.lay_cus).setBackgroundColor(getResources().getColor(R.color.main_color_debug));
                    }
                    if (findViewById(R.id.login) != null) {
                        findViewById(R.id.login).setBackgroundColor(getResources().getColor(R.color.main_color_debug));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }

    private PowerManager.WakeLock wakeLock = null;

    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
