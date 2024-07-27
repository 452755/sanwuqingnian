package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
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
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Factory;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.BaseData;
import com.emms.util.BuildConfig;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/10.
 *
 */
public class SettingActivity extends NfcActivity implements View.OnClickListener{
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private TextView menuSearchTitle;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private boolean isSearchview ;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private Context context=this;
    private ArrayList<ObjectElement> FactoryList=new ArrayList<>();
    private ArrayList<ObjectElement> NetWorkList=new ArrayList<>();
    private ArrayList<ObjectElement> LanguageList=new ArrayList<>();
    private DropEditText Factory,NetWork,Language;
    private final int FACTORY_SETTING=1;
    private final int NETWORK_SETTING=2;
    private final int LANGUAGE_SETTING=3;
    private BroadcastReceiver receiver = null;
    private final String LANGUAGE_CODE= "Language_Code";
    private final String LANGUAGE_NAME= "Language_Name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initLanguageBroadcastReceiver();
        initView();
        initSearchView();
        initSearchViewUI();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("setting"));
        findViewById(R.id.btn_right_action).setOnClickListener(this);
      //  findViewById(R.id.comfirm).setOnClickListener(this);
        ((TextView)findViewById(R.id.factory_tag)).setText(LocaleUtils.getI18nValue("factory"));
        ((TextView)findViewById(R.id.NetWork_tag)).setText(LocaleUtils.getI18nValue("network"));
        ((TextView)findViewById(R.id.Language_tag)).setText(LocaleUtils.getI18nValue("LanguageSetting"));
       // ((Button)findViewById(R.id.comfirm)).setText(LocaleUtils.getI18nValue("sure);
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_right_action:{
                finish();
                break;
            }


        }
    }

    private void initData(){
        initFactory();
        initLanguageList();
        initNetWork();
    }

    /**
     * create by jason
     */
    private void initFactoryByServe(){

        HttpParams params = new HttpParams();
        HttpUtils.getWithoutCookies(this, "DataDictionary/GetFactoryList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    return;
                }
                DataElement dataElement = new JsonArrayElement(t);
                if(dataElement!=null&&dataElement.isArray()&&dataElement.asArrayElement().size()>0){
                    LogUtils.e("dataElement---->"+dataElement);
                    for(int i=0;i<dataElement.asArrayElement().size();i++){
                        FactoryList.add(dataElement.asArrayElement().get(i).asObjectElement());
                    }
                }
                LogUtils.e("initFactoryByServe--测试成功--->"+t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                LogUtils.e("initFactoryByServe--测试失败--->"+errorNo+"--->"+strMsg);
            }
        });
    }

    //jason change
    private void initFactory(){
        FactoryList.clear();
        initFactoryByServe();

//        String sql="select dd.[Factory_ID] OrganiseName,dd.[DataValue1] IntranetURL,dd.[DataValue2] ExtranetURL,(case when dd.[DataValue3]='Garment' then 'EGM' else dd.[DataValue3] end) appMode from DataDictionary  dd where dd.DataType = 'AppFactorySetting' ";
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
//                    LogUtils.e("element---->"+element);
//                    for(int i=0;i<element.asArrayElement().size();i++){
//                        FactoryList.add(element.asArrayElement().get(i).asObjectElement());
//                    }
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//
//            }
//        });
    }
    private void initNetWork(){
        NetWorkList.clear();
        JsonObjectElement json1=new JsonObjectElement();
        json1.set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue("innerNetWork"));
        json1.set(DataDictionary.DATA_CODE,"InnerNetwork");
        JsonObjectElement json2=new JsonObjectElement();
        json2.set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue("outerNetWork"));
        json2.set(DataDictionary.DATA_CODE,"OuterNetwork");
        NetWorkList.add(json1);
        NetWorkList.add(json2);
    }

    private void initLanguageListByServe(){
        HttpParams params = new HttpParams();
//        params.put("LANGUAGE_COD",LANGUAGE_CODE);
//        params.put("LANGUAGE_NAME",LANGUAGE_NAME);
        HttpUtils.getWithoutCookies(this, "Languages", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    LogUtils.e("获取数据为空");
                    return;
                }
                JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                DataElement jsonArrayElement = jsonObjectElement.get("PageData");
                LogUtils.e("initLanguageListByServe--jsonArrayElement--->"+jsonArrayElement.toString());
                LogUtils.e("initLanguageListByServe---测试成功-->"+t);
                if(jsonArrayElement!=null&&jsonArrayElement.isArray()&&jsonArrayElement.asArrayElement().size()>0){
                    LogUtils.e("initLanguageList--->"+jsonArrayElement.asArrayElement().toString());
                    String currentLanguage = LocaleUtils.getLanguage(context);
                    ArrayElement languageArray = jsonArrayElement.asArrayElement();
                    for (int i=0;i<languageArray.size();i++){
                        DataElement languageCode = languageArray.get(i).asObjectElement().get(LANGUAGE_CODE);
                        LogUtils.e("initLanguageListByServe---languageCode--->"+languageCode.valueAsString());
                        final DataElement languageName = languageArray.get(i).asObjectElement().get(LANGUAGE_NAME);
                        if(languageCode!=null && !DataUtil.isDataElementNull(languageCode).equals("") &&
                                languageName!=null && !DataUtil.isDataElementNull(languageName).equals("")) {
                            LogUtils.e("initLanguageListByServe---languageCode.toString-->"+languageCode.toString()+"---->"+currentLanguage);
                            if(languageCode.valueAsString().equals(currentLanguage)){
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Language.getmEditText().setText(languageName.valueAsString());
                                    }
                                });
                            }
                            LanguageList.add(languageArray.get(i).asObjectElement());
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                LogUtils.e("initLanguageListByServe---测试成功-->"+errorNo+"--->"+strMsg);
            }
        });
    }

    //jason change
    private void initLanguageList(){
        LanguageList.clear();
        initLanguageListByServe();
//        String sql="select distinct "+ LANGUAGE_CODE + ","+ LANGUAGE_NAME + " from Languages";
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//
//                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
//                    LogUtils.e("initLanguageList--->"+element.asArrayElement().toString());
//                    String currentLanguage = LocaleUtils.getLanguage(context);
//                    ArrayElement languageArray = element.asArrayElement();
//                    for (int i=0;i<languageArray.size();i++){
//                        DataElement languageCode = languageArray.get(i).asObjectElement().get(LANGUAGE_CODE);
//                        LogUtils.e("initLanguageList---languageCode--->"+languageCode);
//                        final DataElement languageName = languageArray.get(i).asObjectElement().get(LANGUAGE_NAME);
//                        if(languageCode!=null && !DataUtil.isDataElementNull(languageCode).equals("") &&
//                                languageName!=null && !DataUtil.isDataElementNull(languageName).equals("")) {
//                            if(languageCode.toString().equals(currentLanguage)){
//                                ((Activity)context).runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Language.getmEditText().setText(languageName.toString());
//                                    }
//                                });
//                            }
//                            LanguageList.add(languageArray.get(i).asObjectElement());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                Log.e("fail",ex.toString());
//            }
//        });
    }

    private void initSearchView() {
        searchBox = (EditText) findViewById(R.id.et_search);
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
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                isSearchview = true ;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case FACTORY_SETTING:
                                    Factory.getmEditText().setText(searchResult);
                                    String oldFactory=SharedPreferenceManager.getFactory(context);
                                    SharedPreferenceManager.setAppMode(context,DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("appMode")));
                                    SharedPreferenceManager.setExtranetUrl(context,DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("ExtranetURL")));
                                    SharedPreferenceManager.setInteranetUrl(context,DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("IntranetURL")));
                                    DataUtil.FactoryAndNetWorkAddressSetting(context,searchResult);
                                    ChangeServerConnectBaseOnNetwork();
                                    if(!searchResult.equals(oldFactory)) {
//                                        ToastUtil.showToastLong(LocaleUtils.getI18nValue("ChangeFactory"),context);
                                        SharedPreferenceManager.setDatabaseVersion(context,"0");
                                        File dbFile;
                                        switch (BuildConfig.appEnvironment){
                                            case DEVELOPMENT:{
                                                dbFile = new File(DataUtil.getDBDirPath(context), "/EMMS_TEST_" + SharedPreferenceManager.getFactory(context) + ".zip");
                                                break;
                                            }

                                            case UAT:{
                                                dbFile = new File(DataUtil.getDBDirPath(context), "/EMMS_UAT_" + SharedPreferenceManager.getFactory(context) + ".zip");
                                                break;
                                            }
                                            case PROD:{
                                                dbFile = new File(DataUtil.getDBDirPath(context), "/EMMS_" + SharedPreferenceManager.getFactory(context) + ".zip");
                                                break;
                                            }
                                            default:{
                                                dbFile = new File(DataUtil.getDBDirPath(context), "/EMMS_" + SharedPreferenceManager.getFactory(context) + ".zip");
                                                break;
                                            }
                                        }

//                                        getDBFromServer(dbFile);
                                    }

                                    break;
                                case NETWORK_SETTING:
                                    NetWork.getmEditText().setText(searchResult);
                                    SharedPreferenceManager.setNetwork(context,
                                            DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE)));
                                    BuildConfig.NetWorkSetting(context);
                                    break;
                                case LANGUAGE_SETTING:
                                    Language.getmEditText().setText(searchResult);
                                    SharedPreferenceManager.setLanguageChange(context, true);
                                    String newLanguage = mResultAdapter.getItem(position).get(LANGUAGE_CODE).valueAsString();
                                    if(!newLanguage.equals(LocaleUtils.getLanguage(context))) {
                                        LogUtils.e("newLanguage---->"+newLanguage);
                                        LocaleUtils.setLanguage(SettingActivity.this, newLanguage, true);
//                                        ChangeServerConnectBaseOnNetwork();
                                    }
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
                String keyword = s.toString();
                clearBtn.setVisibility(View.VISIBLE);
                mResultListView.setVisibility(View.VISIBLE);
                String itemName = mResultAdapter.getItemName();
                ArrayList<ObjectElement> result = search(keyword, itemName);
                if (result == null || result.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    mResultAdapter.changeData(result, itemName);
                }
            }
        });


        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
            }
        });
    }
    private void initSearchViewUI() {
        searchBox.setHint(LocaleUtils.getI18nValue("hint_search_box"));
        ((TextView)emptyView.findViewById(R.id.tvNothingFound)).setText(LocaleUtils.getI18nValue("nothing_found"));
        Factory=(DropEditText)findViewById(R.id.factory);
        NetWork=(DropEditText)findViewById(R.id.NetWork);
        Language=(DropEditText)findViewById(R.id.Language);
        Factory.getmEditText().setHint(LocaleUtils.getI18nValue("select"));
        NetWork.getmEditText().setHint(LocaleUtils.getI18nValue("select"));
        Language.getmEditText().setHint(LocaleUtils.getI18nValue("select"));
        if(SharedPreferenceManager.getNetwork(context)!=null  &&
                SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")){
            NetWork.getmEditText().setText(LocaleUtils.getI18nValue("innerNetWork"));
        }else {
            NetWork.getmEditText().setText(LocaleUtils.getI18nValue("outerNetWork"));
        }
        if(SharedPreferenceManager.getFactory(context)!=null){
            Factory.getmEditText().setText(SharedPreferenceManager.getFactory(context));
        }
        // 语言初始值在initLanguageList初始化
        initDropSearchView(null, Factory.getmEditText(), LocaleUtils.getI18nValue("factoryTitle"),"OrganiseName",
                FACTORY_SETTING, LocaleUtils.getI18nValue("getDataFail"),Factory.getDropImage());
        initDropSearchView(null,NetWork.getmEditText(), LocaleUtils.getI18nValue("networkTitle"),DataDictionary.DATA_NAME,
                NETWORK_SETTING,LocaleUtils.getI18nValue("getDataFail"),NetWork.getDropImage());
        initDropSearchView(null,Language.getmEditText(), LocaleUtils.getI18nValue("LanguageTitle"),LANGUAGE_NAME,
                LANGUAGE_SETTING,LocaleUtils.getI18nValue("getDataFail"),Language.getDropImage());
    }
    private ArrayList<ObjectElement> search(String keyword,String  tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

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

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final String tips){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag) {
                    case FACTORY_SETTING:{
                        searchDataLists.addAll(FactoryList);
                        break;
                    }
                    case NETWORK_SETTING:{
                        searchDataLists.addAll(NetWorkList);
                        break;
                    }
                    case LANGUAGE_SETTING:{
                        searchDataLists.addAll(LanguageList);
                        break;
                    }
                }
                searchtag = searTag;
                if (condition != null) {
                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips,context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips,context);
                    }
                }
            }
        });
    }
    private void initLanguageBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new updateDateAndUI().execute();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocaleUtils.LANGUAGE_SETTING_ACTION);
        registerReceiver(receiver, filter);
    }

    private class updateDateAndUI extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            showCustomDialog(LocaleUtils.getI18nValue("ChangeLanguagePleaseWait"));
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            SharedPreferenceManager.setLanguageChange(SettingActivity.this, true);
            //改数据不要 如果有异常请考虑这个问题
//            BaseData.setBaseData(SettingActivity.this);
            initData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            //更新UI的操作
            initView();
            initSearchViewUI();
            dismissCustomDialog();
        }

    }

}
