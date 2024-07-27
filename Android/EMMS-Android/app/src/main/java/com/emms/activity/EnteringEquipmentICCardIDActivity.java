package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Equipment;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.ui.VaryView.ICaseViewHelper;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.king.zxing.Intents;
//import com.zxing.android.view.GoogleCaptureActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jaffer.deng on 2016/6/22.
 *
 */

public class EnteringEquipmentICCardIDActivity extends NfcActivity implements View.OnClickListener {
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private TextView menuSearchTitle;
    //作用:增加解除绑定按钮  Jason 2019/9/26 上午10:30
    private Button btn_unbind;
    private Button comfirm;
    private EditText searchBox,iccard_id,qrcode_id;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private Context context=this;
    private ArrayList<ObjectElement> EquipmentList=new ArrayList<>();
    private DropEditText equipment_id;
    private String SelectItem="";
    ArrayList<ObjectElement> reDatas = new ArrayList<>();

    private TextView tvNothingFound;//提示没有找到数据

    private boolean isSelect = false;
    private boolean isicInput = false;
    private boolean isqrcodeInput = false;

    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_equipment_iccard);
        initView();
        initData();
        initSearchView();

    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("entering_equipment"));
        ((TextView)findViewById(R.id.task_subtype_name_id)).setText(LocaleUtils.getI18nValue("equipment_tag_number"));
        ((TextView)findViewById(R.id.tvICCardId)).setText(LocaleUtils.getI18nValue("iccard_id"));
        ((TextView)findViewById(R.id.tvQRCodeId)).setText(LocaleUtils.getI18nValue("qrcode_id"));
        comfirm = (Button) findViewById(R.id.comfirm);
        comfirm.setText(LocaleUtils.getI18nValue("boud"));
        btn_unbind = (Button) findViewById(R.id.unbind);
        btn_unbind.setOnClickListener(this);
        btn_unbind.setText(LocaleUtils.getI18nValue("unbound"));
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        comfirm.setOnClickListener(this);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.filter).setOnClickListener(this);
        ((ImageView)findViewById(R.id.filter)).setImageResource(R.mipmap.sync);
        //findViewById(R.id.equipment_id_scan).setOnClickListener(this);
        findViewById(R.id.qrcode_scan).setOnClickListener(this);
        equipment_id=(DropEditText)findViewById(R.id.equipment_id);
        equipment_id.setHint(LocaleUtils.getI18nValue("select"));
        iccard_id=(EditText)findViewById(R.id.iccard_id);
        iccard_id.setHint(LocaleUtils.getI18nValue("scan"));
        qrcode_id=(EditText)findViewById(R.id.qrcode_id);
        qrcode_id.setHint(LocaleUtils.getI18nValue("scan"));
        findViewById(R.id.qrcode_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent it = new Intent(EnteringEquipmentICCardIDActivity.this, com.zxing.android.CaptureActivity.class);
//                Intent it = new Intent(EnteringEquipmentICCardIDActivity.this, GoogleCaptureActivity.class);
//                startActivityForResult(it, 1);

                Intent it = new Intent(EnteringEquipmentICCardIDActivity.this, com.king.zxing.CaptureActivity.class);
                it.setAction(Intents.Scan.ACTION);
                it.putExtra(Intents.Scan.CAMERA_ID,0);
                it.putExtra(KEY_IS_CONTINUOUS,false);
                startActivityForResult(it, 1);
            }
        });
    }

    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
            if (!isSelect){
                equipment_id.setText("");
            }
            checkICCardID(iccardID,1);
//            MessageUtils.showToast(iccardID,this);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_right_action:{
                finish();
                break;
            }
            case R.id.comfirm:{
                submitEquipmentData(false);
                break;
            }
            case R.id.unbind:
            {
                //作用:解除绑定按钮事件  Jason 2019/9/26 上午10:32
                submitEquipmentData(true);
                break;
            }
//            case R.id.equipment_id_scan:{
//                break;
//            }
//            case R.id.iccard_scan:{
//                break;
//            }
            case R.id.filter:{
//                getDBDataLastUpdateTime();
                initData();
//                initSearchView();
                break;
            }

        }
    }
    private void submitEquipmentData(final boolean isbind){
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        if(equipment_id.getText().equals("")){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseInputEquipmentNum"),this);
            return;
        }
        if(iccard_id.getText().toString().equals("")&&qrcode_id.getText().toString().equals("")){
            if(iccard_id.getText().toString().equals("")){
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseScanICcardNum"),this);
                return;
            }
            if(qrcode_id.getText().toString().equals("")){
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseScanICcardNum"),this);
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        submitData.set(Equipment.EQUIPMENT_ID,
                SelectItem);
        if(!"".equals(iccard_id.getText().toString())){
            if (isbind){
                submitData.set("ICCardID","");
            }else{
                submitData.set("ICCardID",iccard_id.getText().toString());
            }
        }else{
            submitData.set("ICCardID","");
        }
        if(!"".equals(qrcode_id.getText().toString())){
            if (isbind){
                submitData.set("Equipment_KyID","");
            }else{
                submitData.set("Equipment_KyID",qrcode_id.getText().toString());
            }

        }else{
            submitData.set("Equipment_KyID","");
        }
        LogUtils.e("上传绑定或解绑设备---->"+submitData.toString());
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(this, "Equipment/BindEquipment", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                if(t!=null){
                    LogUtils.e("提交数据成功---->"+t);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()){
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitSuccess"),context);
                                if (isbind){
                                    btn_unbind.setBackgroundResource(R.color.kprogresshud_grey_color);
                                    comfirm.setBackgroundResource(R.color.bind_bg);
                                    comfirm.setEnabled(true);
                                    btn_unbind.setEnabled(false);
                                    //作用:如果有已绑定的就清除  Jason 2019/10/16 下午2:01
                                    String equipment = equipment_id.getText();
                                    String bind = LocaleUtils.getI18nValue("IsBinding");
                                    LogUtils.e("equipment---->"+equipment+"----->"+bind);
                                    if (equipment.contains(bind)){
                                        int lengthE = equipment.length();
                                        int lengthB = bind.length();
                                        int lenth = lengthE - lengthB;
                                        String test = equipment.substring(0,lenth);
                                        LogUtils.e("截取后字符串---->"+test);
                                        equipment_id.setText(test);
                                    }
                                }else{
                                    btn_unbind.setBackgroundResource(R.color.bind_bg);
                                    comfirm.setBackgroundResource(R.color.kprogresshud_grey_color);
                                    comfirm.setEnabled(false);
                                    btn_unbind.setEnabled(true);
                                }

                            }else {
                                if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()){
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("submit_Fail"), context);
                                }else {
                                    TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                }
                            }
                        }
                    });
                }
                dismissCustomDialog();
//                getDBDataLastUpdateTime();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("提交数据失败---->"+errorNo+"--->"+strMsg);
                dismissCustomDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"),context);
                    }
                });
            }
        });
    }

    /**
     *
     */
    private void initDataByServe(){

        HttpParams params = new HttpParams();
//        JsonObjectElement checkJson =  new JsonObjectElement();
//
//        params.putJsonParams(checkJson.toJson());
//        params.put("Parameter","fromfactory eq 'GEW' and OracleID  nn");
//        params.put("fields","fields=OracleID,Equipment_KyID,AssetsID,Equipment_ID,isnull(ICCardID,'') ICCardID &$orderby=ICCardID asc");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.post(this, "Equipment/APPGet?filter=filter%3DStatus%20eq%200%20and%20fromfactory%20eq%20'"+getLoginInfo().getFactoryId()+"'&fields=fields%3DOracleID%2CEquipment_KyID%2CEquipment_ID%2CICCardID", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("null")||TextUtils.isEmpty(t)){
                    return;
                }
                dismissCustomDialog();
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if(jsonArrayElement!=null) {
                    if(jsonArrayElement.asArrayElement().size()>0){
                        LogUtils.e("initData--jsonArrayElement->"+jsonArrayElement.asArrayElement().size());
                        EquipmentList.clear();
                        for (int i=0;i<jsonArrayElement.asArrayElement().size();i++){
                            if(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID)!=null&&
                                    !DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID)).equals("")) {
                                if(!DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.IC_CARD_ID)).isEmpty()
                                        &&!DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.EQUIPMENT_KY_ID)).isEmpty()) {
                                    jsonArrayElement.asArrayElement().get(i).asObjectElement().set(Equipment.ORACLE_ID,
                                            DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID))
                                                    +LocaleUtils.getI18nValue("IsBinding")  );


                                }
                                jsonArrayElement.asArrayElement().get(i).asObjectElement().set(Equipment.EQUIPMENT_ID,
                                        DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)));
                                EquipmentList.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                            }
                        }
                    }
                }
                LogUtils.e("initDataByServe--测试成功--->"+"--->"+EquipmentList.toString());
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                LogUtils.e("initDataByServe--测试失败--->"+errorNo+"----"+strMsg);
            }
        });

    }
    //jason change
    private void initData(){
        //本来是加载数据的
//        initDataByServe();

//        String rawQuery="select OracleID,Equipment_KyID,AssetsID,Equipment_ID,ifnull(ICCardID,'') ICCardID from Equipment where OracleID not null order by ICCardID asc";
//        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
//        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//            @Override
//            public void onSuccess(DataElement element) {
//                if(element!=null) {
//                    if(element.asArrayElement().size()>0){
//                        LogUtils.e("initData--element->"+element);
//                        EquipmentList.clear();
//                        for (int i=0;i<element.asArrayElement().size();i++){
//                            if(element.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID)!=null&&
//                                    !DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID)).equals("")) {
//                                if(!DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(Equipment.IC_CARD_ID)).isEmpty()
//                                        &&!DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(Equipment.EQUIPMENT_KY_ID)).isEmpty()) {
//                                    element.asArrayElement().get(i).asObjectElement().set(Equipment.ORACLE_ID,
//                                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID))
//                                                    +LocaleUtils.getI18nValue("IsBinding")  );
//                                }
//                                EquipmentList.add(element.asArrayElement().get(i).asObjectElement());
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                System.out.println(throwable.getMessage());
//            }
//        });
    }

    //退出时的时间
    private long mExitTime;

    private void initSearchView() {
        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.setHint(LocaleUtils.getI18nValue("hint_search_box"));
        mDrawer_layout = (CustomDrawerLayout) findViewById(R.id.search_page);
        mDrawer_layout.setCloseDrawerListener(new CloseDrawerListener() {
            @Override
            public void close() {
                searchBox.setText("");
            }
        });
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawer_layout.setBackgroundColor(Color.parseColor("#00000000"));
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        clearBtn.setImageResource(R.mipmap.search);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        tvNothingFound =  ((TextView)emptyView.findViewById(R.id.tvNothingFound));
        tvNothingFound.setText(LocaleUtils.getI18nValue("BindingSearchTips"));
        emptyView.setVisibility(View.VISIBLE);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                SelectItem=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    if(!DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.IC_CARD_ID)).equals("")
                                            &&!DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_KY_ID)).equals("")){
                                        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                                        dialog.setMessage(LocaleUtils.getI18nValue("ThisEquipmentIsBinding"));
                                        dialog.setNegativeButton(LocaleUtils.getI18nValue("cancel"),new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SelectItem="";
                                                dialog.dismiss();
                                            }
                                        }).setPositiveButton(LocaleUtils.getI18nValue("sure"),new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        equipment_id.getmEditText().setText(searchResult);
                                                        iccard_id.setText(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.IC_CARD_ID)));
                                                        qrcode_id.setText(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_KY_ID)));
                                                        comfirm.setBackgroundResource(R.color.kprogresshud_grey_color);
                                                        btn_unbind.setBackgroundResource(R.color.bind_bg);
                                                        comfirm.setEnabled(false);
                                                        btn_unbind.setEnabled(true);

                                                        isicInput = false;
                                                        isqrcodeInput = false;
                                                        isSelect = false;
                                                    }
                                                });
                                            }
                                        });
                                        dialog.show();
                                    }else {
                                        equipment_id.getmEditText().setText(searchResult);
                                        if (!TextUtils.isEmpty(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.IC_CARD_ID)))){
                                            iccard_id.setText(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.IC_CARD_ID)));
                                        }
                                        if (!TextUtils.isEmpty(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_KY_ID)))){
                                            qrcode_id.setText(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_KY_ID)));
                                        }
                                        if (!isicInput){
                                            iccard_id.setText("");
                                        }

                                        if (!isqrcodeInput){
                                            qrcode_id.setText("");
                                        }

                                        btn_unbind.setBackgroundResource(R.color.kprogresshud_grey_color);
                                        comfirm.setBackgroundResource(R.color.bind_bg);
                                        comfirm.setEnabled(true);
                                        btn_unbind.setEnabled(false);
                                        isicInput = true;
                                        isqrcodeInput = true;
                                        isSelect = true;
                                    }
                                    isSelect = true;
                                    break;
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"),context);
                }
            }
        });
        initDropSearchView(null, equipment_id.getmEditText(), LocaleUtils.getI18nValue("work_num_dialog"), Equipment.ORACLE_ID,
                1, LocaleUtils.getI18nValue("getDataFail"),equipment_id.getDropImage());
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer_layout.closeDrawer(Gravity.RIGHT);
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // initData(s.toString());
                LogUtils.e("输入监听--->"+s.toString());
                final String keyword = s.toString();
                clearBtn.setVisibility(View.VISIBLE);
                mResultListView.setVisibility(View.VISIBLE);
//                String itemName = mResultAdapter.getItemName();
//                search(keyword, itemName);

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final String itemName = mResultAdapter.getItemName();
//                        final ArrayList<ObjectElement> result = search(keyword, itemName);
//                        if (result == null || result.size() == 0) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    emptyView.setVisibility(View.VISIBLE);
//                                }
//                            });
//
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    emptyView.setVisibility(View.GONE);
//                                    mResultAdapter.changeData(result, itemName);
//                                }
//                            });
//
//                        }
//                    }
//                }).start();
                //本地查询
//                if (runnable != null) {
//                    handler.removeCallbacks(runnable);
//                    LogUtils.e("---"+ s.toString());
//                }
//                runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        final String itemName = mResultAdapter.getItemName();
//                        final ArrayList<ObjectElement> result = search(keyword, itemName);
//                        if (result == null || result.size() == 0) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    emptyView.setVisibility(View.VISIBLE);
//                                }
//                            });
//
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    emptyView.setVisibility(View.GONE);
//                                    mResultAdapter.changeData(result, itemName);
//                                }
//                            });
//
//                        }
//                    }
//                };
//
//                handler.postDelayed(runnable, 300);

            }
        });


        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                searchBox.setText("");
//                clearBtn.setVisibility(View.GONE);
                String keyword = searchBox.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)){
                    if (!isFastClick()){
                        searchByServer(keyword);
                    }else {
                        LogUtils.e("重复点击");
                    }

                }else{
                    LogUtils.e("输入设备为空");
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                }


            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable;


    /**
     * 通过网络查询网络数据 加载
     * @param keyword
     * @return
     */
    private ArrayList<ObjectElement> searchByServer(String keyword) {
        reDatas.clear();
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        //使用网络去查找数据
        HttpParams params = new HttpParams();
        params.put("Equipment", keyword);
        HttpUtils.get(context, "Equipment/GetEquipmentInfoForBinding", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if (TextUtils.isEmpty(t)) {
                    LogUtils.e("请求的数据为空");
                    tvNothingFound.setText(LocaleUtils.getI18nValue("nothing_found"));
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                LogUtils.e("查询数据获取成功--->" + t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null) {
                    if (jsonArrayElement.asArrayElement().size() > 0) {
                        LogUtils.e("search--jsonArrayElement->" + jsonArrayElement.asArrayElement().size());
                        for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                            if (jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID) != null &&
                                    !DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID)).equals("")) {
                                if (!DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.IC_CARD_ID)).isEmpty()
                                        || !DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.EQUIPMENT_KY_ID)).isEmpty()) {
                                    jsonArrayElement.asArrayElement().get(i).asObjectElement().set(Equipment.ORACLE_ID,
                                            DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID))
                                                    + LocaleUtils.getI18nValue("IsBinding"));
                                }
                                jsonArrayElement.asArrayElement().get(i).asObjectElement().set(Equipment.EQUIPMENT_ID,
                                        DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)));
                                reDatas.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
//                                LogUtils.e("加入reDatas---->" + reDatas.size());
                            }
                        }
                        String itemName = mResultAdapter.getItemName();
                        if (reDatas == null || reDatas.size() == 0) {
                            tvNothingFound.setText(LocaleUtils.getI18nValue("nothing_found"));
                            emptyView.setVisibility(View.VISIBLE);
                        }else {
                            tvNothingFound.setText(LocaleUtils.getI18nValue("BindingSearchTips"));
                            emptyView.setVisibility(View.GONE);
                            mResultAdapter.changeData(reDatas, itemName);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                LogUtils.e("获取数据失败---->" + errorNo + "--->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);

            }
        });
        LogUtils.e("返回的reDatas--->"+reDatas.size());

        return reDatas;

    }

    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }


    private ArrayList<ObjectElement> search(final String keyword, final String  tagString) {
        reDatas.clear();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                LogUtils.e("searchDataLists--->size-->" + searchDataLists.size());
//                for (int i = 0; i < searchDataLists.size(); i++) {
//                    LogUtils.e("需要查询的keyword-->" + keyword);
//                    if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
//                        LogUtils.e("tagString--->" + tagString);
//                        LogUtils.e("查询进入这里--->" + searchDataLists.get(i));
//                        reDatas.add(searchDataLists.get(i));
//
//                    }
//                }
//                Bundle bundle = new Bundle();
//                Message message = new Message();
//                //listitem是一个ArrayList<>
//                bundle.putSerializable("listitem", reDatas);
//                message.setData(bundle);
//                handler.sendMessage(message);
//            }
//        });

        if (keyword.equals("")){
            return searchDataLists;
        }


        LogUtils.e("searchDataLists--->size-->"+searchDataLists.size());
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        int sum = 0;
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                sum++;
                LogUtils.e("tagString--->"+tagString);
                LogUtils.e("查询进入这里--->"+searchDataLists.get(i));
                reDatas.add(searchDataLists.get(i));
            }
        }
//
//        LogUtils.e("查询总数---->"+sum);
        LogUtils.e("最后返回的reDatas--->"+reDatas.size());
        return reDatas;
    }

//   private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            LogUtils.e("进入更改界面---->"+msg);
//            Bundle bundle = msg.getData();
//            ArrayList<ObjectElement> reDatas = (ArrayList<ObjectElement>) bundle.getSerializable("listitem");
//            if (reDatas == null || reDatas.size() == 0) {
//                emptyView.setVisibility(View.VISIBLE);
//            } else {
//                emptyView.setVisibility(View.GONE);
//                mResultAdapter.changeData(reDatas, mResultAdapter.getItemName());
//            }
//        }
//    };

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final String tips,ImageView imageView){
        subEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DropSearch(condition,
                                searchTitle,searchName,searTag ,tips);
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DropSearch(condition,
                        searchTitle,searchName,searTag ,tips);
            }
        });
    }
    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final String tips){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag) {
                    case 1:{
                        searchDataLists.addAll(EquipmentList);
                        break;
                    }}
                searchtag = searTag;
                if (condition != null) {
                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        tvNothingFound.setText(LocaleUtils.getI18nValue("BindingSearchTips"));
                        emptyView.setVisibility(View.VISIBLE);
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        tvNothingFound.setText(LocaleUtils.getI18nValue("BindingSearchTips"));
                        emptyView.setVisibility(View.VISIBLE);
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 1:
                if (data != null)
                {
                    final String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (result != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.e("获取到扫描的结果--->"+result);
                                if (!isSelect){
                                    equipment_id.setText("");
                                }

                                checkICCardID(result,2);
                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * create by jason
     * @param IC_Card_ID
     * @param flag
     */
    private void checkICCardIDByServe(final String IC_Card_ID,final int flag){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
//        params.put("IDType","1");
        params.put("CodeID",IC_Card_ID);

        HttpUtils.get(this, "Equipment/GetEquipmentByCardOrQRCode", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);

                LogUtils.e("checkICCardIDByServe--测试成功--->"+t);
                if(t!=null&&!"null".equals(t)){
                    if(jsonArrayElement!=null&&jsonArrayElement.isArray()&&jsonArrayElement.asArrayElement().size()>0){
                        if(!isFinishing()) {
                            //作用:已绑定设备 绑定按钮不能点击  Jason 2019/9/26 上午10:40
                            comfirm.setBackgroundResource(R.color.kprogresshud_grey_color);
                            btn_unbind.setBackgroundResource(R.color.bind_bg);
                            comfirm.setEnabled(false);
                            btn_unbind.setEnabled(true);

                            equipment_id.setText(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.ORACLE_ID)));
                            qrcode_id.setText(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_KY_ID).valueAsString());
                            iccard_id.setText(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.IC_CARD_ID).valueAsString());
                            isSelect = false;
                            isicInput = false;
                            isqrcodeInput = false;
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            String message = LocaleUtils.getI18nValue("ICCardIsBinding")
                                    + "\n" + LocaleUtils.getI18nValue("equipment_name") + DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_NAME))
                                    + "\n" + LocaleUtils.getI18nValue("equipment_num") + DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.ORACLE_ID));
                            builder.setMessage(message);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            SelectItem = DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_ID));
                        }
                    }else {
                        btn_unbind.setBackgroundResource(R.color.kprogresshud_grey_color);
                        comfirm.setBackgroundResource(R.color.bind_bg);
                        comfirm.setEnabled(true);
                        btn_unbind.setEnabled(false);

//                        equipment_id.setText(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_ID)));
//                        SelectItem = DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_ID));
                        if(flag==1){
                            isicInput = true;
                            iccard_id.setText(IC_Card_ID);
                            if (!isqrcodeInput){
                                qrcode_id.setText("");
                            }
                        }else{
                            isqrcodeInput = true;
                            qrcode_id.setText(IC_Card_ID);
                            if (!isicInput){
                                iccard_id.setText("");
                            }

                        }
                    }
                }
//                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                comfirm.setBackgroundResource(R.color.bind_bg);
                btn_unbind.setBackgroundResource(R.color.bind_bg);
                comfirm.setEnabled(true);
                btn_unbind.setEnabled(true);
                if(flag==1){
                    iccard_id.setText(IC_Card_ID);
                    qrcode_id.setText("");
                }else{
                    qrcode_id.setText(IC_Card_ID);
                    iccard_id.setText("");
                }
                LogUtils.e("checkICCardIDByServe--测试失败--->"+errorNo+"----"+strMsg);
//                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
//                dismissCustomDialog();
            }
        });

    }

    //jason change
    private void checkICCardID(final String IC_Card_ID, final int flag){

        checkICCardIDByServe(IC_Card_ID,flag);

//        String rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  (e.ICCardID ='" + IC_Card_ID + "' or e.Equipment_KyID='"+ IC_Card_ID +"') and e.[Organise_ID_Use]=b.[Organise_ID]";
//        getSqliteStore().performRawQuery(rawQuery, EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, new StoreCallback() {
//            @Override
//            public void success(final DataElement element, String resource) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
//                            if(!isFinishing()) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                String message = LocaleUtils.getI18nValue("ICCardIsBinding")
//                                        + "\n" + LocaleUtils.getI18nValue("equipment_name") + DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_NAME))
//                                        + "\n" + LocaleUtils.getI18nValue("equipment_num") + DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get(Equipment.ORACLE_ID));
//                                builder.setMessage(message);
//                                builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.show();
//                            }
//                        }else {
//                            if(flag==1){
//                                iccard_id.setText(IC_Card_ID);
//                            }else{
//                                qrcode_id.setText(IC_Card_ID);
//                            }
//                        }
//                    }
//                });
//            }
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(flag==1){
//                            iccard_id.setText(IC_Card_ID);
//                        }else{
//                            qrcode_id.setText(IC_Card_ID);
//                        }
//                    }
//                });
//            }
//        });
    }
}
