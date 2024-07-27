package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.LocaleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.emms.callback.CallBack;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.ui.NFCDialog;
import com.emms.ui.TaskCompleteDialog;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.io.filefilter.FalseFileFilter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/7/25.
 *
 */
public class SummaryActivity extends NfcActivity{
    private DropEditText type;
    private EditText description,repair_status;
    private Context context=this;
    private ResultListAdapter mResultAdapter;
    //create by jason 维护分类查询
    private ResultListAdapter MaintienanceAdapter;
    private DropEditText maintianence;


    private ListView mResultListView;
    private TextView menuSearchTitle;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private boolean isSearchview ;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> typeList=new ArrayList<>();
    //create by jason 2019/3/22 维护故障分类
    private ArrayList<ObjectElement> MaintenanceTypeList = new ArrayList<>();
    private ObjectElement TaskDetail;
    private boolean TaskComplete=false;
    private boolean breakDesc=false;
    private boolean maintenanceCodition=false;
    private boolean taskSummary = true;
    private boolean equipmentTroubleSort = true;
    private String TaskTrouble_ID="";
    private String TaskClass=Task.REPAIR_TASK;
    private String TroubleType;
    //create by jason 2019/3/22 获取MaintienanceTypa
    private String MaintienanceType;
    private HashMap<String,String> map=new HashMap<>();
    //create bu jason 2019/3/22 维护故障分类存入相应的Map
    private HashMap<String,String> MaintenanceMap = new HashMap<>();
    private NFCDialog nfcDialog;
    private boolean nfcDialogTag=false;
    //create by jason 2019/3/25 设备类型id
    private String EquipmentId;
    private String data_id;

    private TextView maintian_tag;
    LinearLayout bbbbbbb;

    private Boolean isSurplusSparePart;//create by Abrahamguo 2021-12-20 是否剩余备料

    private boolean HasTaskEquipment=true;//create by jason 新增是否与设备相关标示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));

        //新增是否与设备相关标示
        if(TaskDetail!=null) {
            if(TaskDetail.get("ModuleType") != null ){
                if (TaskDetail.get("ModuleType").valueAsString().equals("Property")){
                    HasTaskEquipment = false;
                }else if (TaskDetail.get("IsExsitTaskEquipment") != null) {
                    HasTaskEquipment = TaskDetail.get("IsExsitTaskEquipment").valueAsBoolean();
                }
            }else if (TaskDetail.get("IsExsitTaskEquipment") != null) {
                HasTaskEquipment = TaskDetail.get("IsExsitTaskEquipment").valueAsBoolean();
            }
        }
        //HasTaskEquipment = false;
        LogUtils.e("TaskDetail---->"+TaskDetail);
        EquipmentId = DataUtil.isDataElementNull(TaskDetail.get("EquipmentAssetsIDList"));
        TaskComplete=getIntent().getBooleanExtra("TaskComplete",false);
        if(getIntent().getStringExtra(Task.TASK_CLASS)!=null){
            TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        }
        isSurplusSparePart = getIntent().getBooleanExtra("isSurplusSparePart",false);
        initView();
        //测试加载数据 create by jason 2019/3/22
        LoadData();
        initData();
        initSearchView();
        getInputDescLimit();
        //create by jason 2019/3/22 初始化搜索框
        initMaintenanceView();
        nfcDialog=new NFCDialog(context,R.style.MyDialog) {
            @Override
            public void dismissAction() {
                nfcDialogTag=false;
            }

            @Override
            public void showAction() {
                nfcDialogTag=true;
            }
        };

    }
    public void initView(){
        //initTopToolbar
//        if(Task.REPAIR_TASK.equals(TaskClass)){
//            LogUtils.e("<----REPAIR_TASK--->");
//            ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("fault_summary"));
//            ((TextView)findViewById(R.id.type_tag)).setText(LocaleUtils.getI18nValue("fault_type"));
//            ((TextView)findViewById(R.id.description_tag)).setText(LocaleUtils.getI18nValue("fault_description"));
//            ((TextView)findViewById(R.id.status_tag)).setText(LocaleUtils.getI18nValue("repair_status"));
//            //create by jason 2019/04/16
//            findViewById(R.id.maintian_layout).setVisibility(View.VISIBLE);
//        }else {
//            LogUtils.e("<----else--->");
//            ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("task_summary"));
//            findViewById(R.id.ccccc).setVisibility(View.GONE);
//            findViewById(R.id.layout2).setVisibility(View.GONE);
//            findViewById(R.id.status_tag).setVisibility(View.GONE);
//            //create by jason 2019/04/16
//            findViewById(R.id.maintian_layout).setVisibility(View.GONE);
//            ((TextView)findViewById(R.id.description_tag)).setText(LocaleUtils.getI18nValue("task_other_summary_tag"));
//        }

        /**
         * 说明：如果有设备就显示 故障分类 故障类型 故障描述 维修情况 否则隐藏，显示任务总结
         * 添加时间：2020/5/5 上午10:53
         * 作者：Jason
         */
        LogUtils.e("HasTaskEquipment---->"+HasTaskEquipment);
        if (HasTaskEquipment){
            LogUtils.e("<----REPAIR_TASK--->");
            ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("fault_summary"));
            ((TextView)findViewById(R.id.type_tag)).setText(LocaleUtils.getI18nValue("fault_type"));
            ((TextView)findViewById(R.id.description_tag)).setText(LocaleUtils.getI18nValue("fault_description"));
            ((TextView)findViewById(R.id.status_tag)).setText(LocaleUtils.getI18nValue("repair_status"));
            LinearLayout maintian_layout = (LinearLayout) findViewById(R.id.maintian_layout);
            LinearLayout ccccc = (LinearLayout) findViewById(R.id.ccccc);
            LogUtils.e("<----REPAIR_TASK--->"+BaseData.getConfigData().get(BaseData.SHOW_EQUIPMENT_TROUBLE_SORT));
            //作用:是否显示故障分类 故障类型  Jason 2020/5/5 上午11:29
            if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_EQUIPMENT_TROUBLE_SORT)).equals("1")||DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_EQUIPMENT_TROUBLE_SORT)).isEmpty()){
                maintian_layout.setVisibility(View.VISIBLE);
                ccccc.setVisibility(View.VISIBLE);
            }else{
                maintian_layout.setVisibility(View.GONE);
                ccccc.setVisibility(View.GONE);
            }
            //作用:是否显示故障描述  Jason 2020/5/5 上午11:36
            bbbbbbb = (LinearLayout) findViewById(R.id.bbbbbbb);
            if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_BREAKDOWN_DESC)).equals("1")||DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_BREAKDOWN_DESC)).isEmpty()){
                bbbbbbb.setVisibility(View.VISIBLE);
            }else{
                bbbbbbb.setVisibility(View.GONE);
            }
            //作用:是否显示维修情况  Jason 2020/5/5 上午11:38
            LinearLayout repair_condition = (LinearLayout) findViewById(R.id.repair_condition);
            if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_MAINTENANCECONDITION)).equals("1")||DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_EQUIPMENT_TROUBLE_SORT)).isEmpty()){
                repair_condition.setVisibility(View.VISIBLE);
            }else{
                repair_condition.setVisibility(View.GONE);
            }

            //create by jason 2019/04/16
//            findViewById(R.id.maintian_layout).setVisibility(View.VISIBLE);
        }else{
            LogUtils.e("<----else--->");
            bbbbbbb = (LinearLayout) findViewById(R.id.bbbbbbb);
            ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("task_summary"));
            findViewById(R.id.ccccc).setVisibility(View.GONE);
            findViewById(R.id.layout2).setVisibility(View.GONE);
            findViewById(R.id.status_tag).setVisibility(View.GONE);
            //create by jason 2019/04/16
            findViewById(R.id.maintian_layout).setVisibility(View.GONE);
            bbbbbbb.setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.description_tag)).setText(LocaleUtils.getI18nValue("task_other_summary_tag"));
        }

        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        type=(DropEditText)findViewById(R.id.type);
        type.setHint(LocaleUtils.getI18nValue("select"));
        description=(EditText)findViewById(R.id.description);
        description.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        repair_status=(EditText)findViewById(R.id.repair_status);
        repair_status.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        Button comfirm = (Button) findViewById(R.id.comfirm);
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("把故障总结上传到服务端");
                submitFaultSummaryToServer();
            }
        });
        comfirm.setText(LocaleUtils.getI18nValue("sure"));
        if(TaskComplete) {
            comfirm.setVisibility(View.GONE);
            findViewById(R.id.footer_toolbar).setVisibility(View.VISIBLE);
            //initFooterToolbar
            ((Button)findViewById(R.id.preStep)).setText(LocaleUtils.getI18nValue("preStep"));
            findViewById(R.id.preStep).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            findViewById(R.id.nextStep).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //待写
                    submitFaultSummaryToServer();

                }
            });
            ((Button)findViewById(R.id.nextStep)).setText(LocaleUtils.getI18nValue("taskComplete"));
        }
        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
        findViewById(R.id.layout2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repair_status.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
    }

    /**
     * create by jason 2019/3/22 初始化选择框
     */
    private void initMaintenanceView(){
        LogUtils.e("<-----initMaintenanceView---->");
        maintianence = (DropEditText) findViewById(R.id.maintianence);
        maintianence.setHint(LocaleUtils.getI18nValue("select"));
        maintian_tag = (TextView) findViewById(R.id.maintian_tag);
        maintian_tag.setText(LocaleUtils.getI18nValue("BreakendownKind"));
        /*MaintienanceAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(MaintienanceAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                isSearchview = true ;
                final int inPosition = position;
                String itemNam = MaintienanceAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(MaintienanceAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    maintianence.getmEditText().setText(searchResult);
                                    MaintienanceType=DataUtil.isDataElementNull(MaintienanceAdapter.getItem(position).get(DataDictionary.DATA_CODE));
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
        initDropSearchView(null, maintianence.getmEditText(), LocaleUtils.getI18nValue("faultType"), DataDictionary.DATA_NAME,
                2, LocaleUtils.getI18nValue("getDataFail"),maintianence.getDropImage());*/
        try {
            initDropSearchView(null, maintianence.getmEditText(), context.getResources().getString(R.string.maintenance_type), DataDictionary.DATA_NAME,
                    2, LocaleUtils.getI18nValue("getDataFail"),maintianence.getDropImage());
        }catch (Exception e){
            CrashReport.postCatchedException(e);
        }

    }

    private void initSearchView() {
        LogUtils.e("<-----initSearchView---->");
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
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        ((TextView)emptyView.findViewById(R.id.tvNothingFound)).setText(LocaleUtils.getI18nValue("nothing_found"));
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                isSearchview = true ;
                final int inPosition = position;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    type.getmEditText().setText(searchResult);
                                    TroubleType=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;
                                case 2:
                                    maintianence.getmEditText().setText(searchResult);
                                    MaintienanceType=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    data_id = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_ID));
                                    searchData(data_id,EquipmentId);
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
        initDropSearchView(null, type.getmEditText(), LocaleUtils.getI18nValue("faultType"), DataDictionary.DATA_NAME,
                1, LocaleUtils.getI18nValue("getDataFail"),type.getDropImage());
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
    private ArrayList<ObjectElement> search(String keyword,String  tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    /**
     * create by jason 2019/3/22 维护分类
     * @param condition
     * @param subEditText
     * @param searchTitle
     * @param searchName
     * @param searTag
     * @param tips
     * @param imageView
     */
    private void initMaintienanceDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final String tips,final ImageView imageView){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtils.e("执行查找方法---->"+condition+"--searchTitle-->"+searchTitle+"--searchName-->"+searchName+"--searTag--->"+searTag+"---tips--->"+tips);
                                MiantienanceDropSearch(condition,
                                        searchTitle,searchName,searTag ,tips);
                            }
                        }
                );
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("执行查找方法---->"+condition+"--searchTitle-->"+searchTitle+"--searchName-->"+searchName+"--searTag--->"+searTag+"---tips--->"+tips);
                MiantienanceDropSearch(condition,
                        searchTitle,searchName,searTag ,tips);
            }
        });
    }

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final String tips,final ImageView imageView){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtils.e("执行查找方法---->"+condition+"--searchTitle-->"+searchTitle+"--searchName-->"+searchName+"--searTag--->"+searTag+"---tips--->"+tips);
                                DropSearch(condition,
                                        searchTitle,searchName,searTag ,tips);
                            }
                        }
                );
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("执行查找方法---->"+condition+"--searchTitle-->"+searchTitle+"--searchName-->"+searchName+"--searTag--->"+searTag+"---tips--->"+tips);
                DropSearch(condition,
                        searchTitle,searchName,searTag ,tips);
            }
        });
    }

    /**
     * create by jason
     */
    private void getInputDescLimitByServe(){
        HttpParams params = new HttpParams();
//        params.put("Parameter","filter=DataType eq 'Switch' and factory_id eq 'GEW' and DataCode in('breakdown_desc','maintenance_condition')");
        HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'Switch'%20and%20factory_id%20eq%20'"+getLoginInfo().getFactoryId()+"'%20and%20DataCode%20in('breakdown_desc'%2C'maintenance_condition'%2C'Task_Summary')", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    return;
                }
                DataElement jsonArrayElment = new JsonArrayElement(t);
                if(jsonArrayElment!=null){
                    if(jsonArrayElment.isArray()){
                        for(int i = 0;i<jsonArrayElment.asArrayElement().size();i++){
                            ObjectElement data=jsonArrayElment.asArrayElement().get(i).asObjectElement();
                            LogUtils.e("DataCode--->"+data.get("DataCode").valueAsString()+"----->"+data.get("DataValue1").valueAsString());
                            if (data.get("DataCode").valueAsString().equals("breakdown_desc")){
                                breakDesc = data.get("DataValue1").valueAsString().equals("1")?true:false;
                            }else if(data.get("DataCode").valueAsString().equals("maintenance_condition")){
                                maintenanceCodition = data.get("DataValue1").valueAsString().equals("1")?true:false;
                            }else if (data.get("DataCode").valueAsString().equals("Task_Summary")){
                                LogUtils.e("taskSummary--->"+taskSummary);
                                taskSummary = data.get("DataValue1").valueAsString().equals("1")?true:false;
                                LogUtils.e("taskSummary--->"+taskSummary);
                            }else if (data.get("DataCode").valueAsString().equals("EquipmentTroubleSort")){
                                equipmentTroubleSort = data.get("DataValue1").valueAsString().equals("1")?true:false;
                            }
                        }
                    }
                }
                LogUtils.e("initDataByServe--测试成功--->"+t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                LogUtils.e("initDataByServe--测试成功--->"+errorNo+"--->"+strMsg);
            }
        });
    }


    //jason change
    private void getInputDescLimit(){
        LogUtils.e("<------getInputDescLimit----->");
        getInputDescLimitByServe();
//        String sql="select factory_id,DataType,DataCode,DataValue1,DataName,DataDescr from DataDictionary " +
//                "    where factory_id='" + getLoginInfo().getFactoryId() + "'" +
//                "    and DataType = 'Switch' and DataCode in ('breakdown_desc','maintenance_condition')";
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
//            @Override
//            public void success(final DataElement element, String resource) {
//                if(element!=null){
//                    if(element.isArray()){
//                        for(int i = 0;i<element.asArrayElement().size();i++){
//                            ObjectElement data=element.asArrayElement().get(i).asObjectElement();
//                            if (data.get("DataCode").toString().equals("breakdown_desc")){
//                                breakDesc = data.get("DataValue1").toString().equals("1")?true:false;
//                            }else if(data.get("DataCode").toString().equals("maintenance_condition")){
//                                maintenanceCodition = data.get("DataValue1").toString().equals("1")?true:false;
//                            }
//                        }
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

    private void submitFaultSummaryToServer(){
        LogUtils.e("<------submitFaultSummaryToServer----->"+description.getText().toString().equals("")+"----->"+breakDesc);
        if(HasTaskEquipment) {
            if (type.getText().trim().equals("")&&equipmentTroubleSort) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoFaultSummaryType"),context);
                return;
            }
            if(description.getText().toString().trim().equals("") && breakDesc){
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoFaultSummary"),context);
                return;
            }
            if (repair_status.getText().toString().trim().equals("") && maintenanceCodition) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoRepairStatus"),context);
                return;
            }
        }else {
            //新增判断任务总结是否为必填 true为必填
            if (taskSummary){
                if (description.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoTaskSummary"), context);
                    return;
                }
            }

        }
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams httpParams=new HttpParams();
        JsonObjectElement  FaultSummary=new JsonObjectElement();
        FaultSummary.set(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));

        //如果存在TaskTrouble_ID则填对应，否则填0
        if(TaskTrouble_ID.equals("")){
            FaultSummary.set("TaskTrouble_ID",0);}
        else{
            FaultSummary.set("TaskTrouble_ID",Integer.valueOf(TaskTrouble_ID));
        }
        // FaultSummary.set("TroubleType",type.getText().toString());
        FaultSummary.set("TroubleType",TroubleType);
        FaultSummary.set("TroubleDescribe",description.getText().toString());
        FaultSummary.set("MaintainDescribe",repair_status.getText().toString());
        httpParams.putJsonParams(FaultSummary.toJson());
        LogUtils.e("上传的数据源--->"+ FaultSummary.toJson());
        HttpUtils.post(this, "TaskTrouble", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    LogUtils.e("完成任务上传成功---->"+t);
                    try{
                        JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                        if(jsonObjectElement.get(Data.SUCCESS)!=null&&jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                            dismissCustomDialog();
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitSuccess"),context);
                            if (TaskComplete) {
//                            Intent intent = new Intent(context, CommandActivity.class);
//                            intent.putExtra("TaskComplete", true);
//                            intent.putExtra("TaskDetail", TaskDetail.toString());
//                            startActivity(intent);
//                                if(TaskClass!=null&&TaskClass.equals(Task.TRANSFER_MODEL_TASK)){
//                                if(mAdapter!=null&&mAdapter.isEnabled()) {
//                                    if (nfcDialog != null && !nfcDialog.isShowing()) {
//                                        nfcDialog.show();
//                                    }
//                                }else {
//                                    TaskCompleteDialog taskCompleteDialog=new TaskCompleteDialog(context,R.style.MyDialog);
//                                    taskCompleteDialog.setTask_ID(DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
//                                    taskCompleteDialog.setTaskClass(TaskClass);
//                                    taskCompleteDialog.show();
//                                }}else {
//                                    TaskComplete(null);
//                                }
                                switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_COMPLETE_ACTION))){
                                    case "1":{
                                        if(Task.REPAIR_TASK.equals(TaskClass)
                                                ||Task.MOVE_CAR_TASK.equals(TaskClass)
                                                ||Task.TRANSFER_MODEL_TASK.equals(TaskClass)){
                                            Intent intent=new Intent(SummaryActivity.this,TaskCompleteActivity.class);
                                            intent.putExtra("TaskComplete",true);
                                            intent.putExtra(Task.TASK_CLASS,TaskClass);
                                            intent.putExtra("TaskDetail",TaskDetail.toString());
                                            intent.putExtra("isSurplusSparePart",isSurplusSparePart);
                                            startActivity(intent);
                                        }else {
                                            TaskComplete(null);
                                        }
                                        break;
                                    }
                                    default:{
                                        TaskComplete(null);
                                        break;
                                    }
                                }


                            }else{
                                finish();
                            }
                        }else {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("submit_Fail"),context);
                        }
                    }catch (Throwable throwable){
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("AppError"),context);
                        CrashReport.postCatchedException(throwable);
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("完成任务上传报错---->"+errorNo+"---->"+strMsg);
                dismissCustomDialog();
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"),context);
            }
        });
    }

    private void getSummaryFromServer(){
        LogUtils.e("<------getSummaryFromServer----->");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams=new HttpParams();
        httpParams.put("dataType", "EquipmentTroubleSort");
        HttpUtils.get(this, "DataDictionary/DataDictionaryList", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{

                        DataElement jsonObjectElement=new JsonArrayElement(t);
                        LogUtils.e("<----获取数据成功---->"+jsonObjectElement.toString());
                        if(jsonObjectElement.isArray() && jsonObjectElement.asArrayElement().size()>0){
                            typeList.clear();
                            final ObjectElement faultData=jsonObjectElement.asArrayElement().get(0).asObjectElement();
                            for(int i=0;i<jsonObjectElement.asArrayElement().size();i++){
                                //翻译DATA_NAME
                                jsonObjectElement.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonObjectElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                                typeList.add(jsonObjectElement.asArrayElement().get(i).asObjectElement());
                                map.put(DataUtil.isDataElementNull(jsonObjectElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
                                        DataUtil.isDataElementNull(jsonObjectElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TaskTrouble_ID= DataUtil.isDataElementNull(faultData.get("TaskTrouble_ID"));
                                    type.setText(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                    TroubleType=map.get(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                    description.setText(DataUtil.isDataElementNull(faultData.get("TroubleDescribe")));
                                    repair_status.setText(DataUtil.isDataElementNull(faultData.get("MaintainDescribe")));
                                }
                            });
                        }
                    }catch (Throwable throwable){
                        CrashReport.postCatchedException(throwable);
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
                getHoitryFromServer();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
                dismissCustomDialog();
                getHoitryFromServer();
            }
        });
    }

    private void getHoitryFromServer(){
        LogUtils.e("<------getSummaryFromServer----->");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams=new HttpParams();
        httpParams.put("task_id", DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        HttpUtils.get(this, "TaskTroubleAPI/GetTaskTroubleList", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                        LogUtils.e("<----获取数据成功---->"+jsonObjectElement.toString());
                        if(jsonObjectElement.get("PageData").isArray() && jsonObjectElement.get("PageData").asArrayElement().size()>0){
                            final ObjectElement faultData=jsonObjectElement.get("PageData").asArrayElement().get(0).asObjectElement();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TaskTrouble_ID= DataUtil.isDataElementNull(faultData.get("TaskTrouble_ID"));
                                    type.setText(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                    TroubleType=map.get(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                    description.setText(DataUtil.isDataElementNull(faultData.get("TroubleDescribe")));
                                    repair_status.setText(DataUtil.isDataElementNull(faultData.get("MaintainDescribe")));
                                }
                            });
                        }
                    }catch (Throwable throwable){
                        CrashReport.postCatchedException(throwable);
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
                dismissCustomDialog();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JsonObjectElement FaultSummary=new JsonObjectElement();
            FaultSummary.set("TaskTrouble_ID",TaskTrouble_ID);
            FaultSummary.set("TroubleType",type.getText());
            FaultSummary.set("TroubleDescribe",description.getText().toString());
            FaultSummary.set("MaintainDescribe",repair_status.getText().toString());
            outPersistentState.putString("submitData",FaultSummary.toJson());
            outPersistentState.putString("TaskDetail",TaskDetail.toJson());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initDataByServe(){
        HttpParams params = new HttpParams();
//        JsonObjectElement checkJson =  new JsonObjectElement();
//        checkJson.set("DataType","TaskClass");
//        params.putJsonParams(checkJson.toJson());
//        params.put("Parameter","filter=DataType eq 'TaskClass' and factory_id eq 'GEW'");EquipmentClassTrouble

        HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'EquipmentTroubleSort'%20and%20factory_id%20eq%20'"+getLoginInfo().getFactoryId()+"'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    return;
                }
                typeList.clear();
                LogUtils.e("initDataByServe--测试成功--->"+t);
                DataElement jsonArrayElment = new JsonArrayElement(t);
                LogUtils.e("执行有数据成功---->"+jsonArrayElment.asArrayElement().size());
                LogUtils.e("获取初始化数据成功--->"+jsonArrayElment.asArrayElement().size());
                if(jsonArrayElment!=null&&jsonArrayElment.isArray()&&jsonArrayElment.asArrayElement().size()>0){
                    for(int i=0;i<jsonArrayElment.asArrayElement().size();i++){
                        //翻译DATA_NAME
                        jsonArrayElment.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                        typeList.add(jsonArrayElment.asArrayElement().get(i).asObjectElement());
                        map.put(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
                                DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSummaryFromServer();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                HttpUtils.tips(context,errorNo+"strMsg-->"+strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"),context);
                    }
                });
                LogUtils.e("initDataByServe--测试失败--->"+errorNo+"----"+strMsg);
            }
        });
    }

    //jason change
    private void initData(){
        LogUtils.e("<-----initData---->");
        initDataByServe();
//        DataUtil.getDataFromDataBase(context, "EquipmentTroubleSort", new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                LogUtils.e("获取初始化数据成功--->"+element.asArrayElement().size());
//                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
//                    for(int i=0;i<element.asArrayElement().size();i++){
//                        //翻译DATA_NAME
//                        element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
//                        typeList.add(element.asArrayElement().get(i).asObjectElement());
//                        map.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
//                                DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            getSummaryFromServer();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                   runOnUiThread(new Runnable() {
//                       @Override
//                       public void run() {
//                           ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"),context);
//                       }
//                   });
//            }
//        });
    }

    @Override
    public void resolveNfcMessage(Intent intent) {
//        String action = intent.getAction();
//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            String iccardID = NfcUtils.dumpTagData(tag);
//            if (nfcDialogTag) {
//                showCustomDialog(LocaleUtils.getI18nValue("submitData);
//                HttpParams params = new HttpParams();
//                JsonObjectElement submitData = new JsonObjectElement();
//                submitData.set("ICCardID", iccardID);
//                submitData.set(Task.TASK_ID, DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
//                params.putJsonParams(submitData.toJson());
//                HttpUtils.post(context, "TaskOperatorAPI/CheckUserRoleForICCardID", params, new HttpCallback() {
//                    @Override
//                    public void onSuccess(final String t) {
//                        super.onSuccess(t);
//                        dismissCustomDialog();
//                        if (t != null) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
//                                    if (jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
//                                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessToCheckID, context);
//                                        TaskComplete(jsonObjectElement.get(Data.PAGE_DATA));
//                                    } else {
//                                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailToCheckID, context);
//                                    }
//                                }
//                            });
//                        }
//                        if (nfcDialog != null && nfcDialog.isShowing()) {
//                            nfcDialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int errorNo, String strMsg) {
//                        super.onFailure(errorNo, strMsg);
//                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailToCheckIDCauseByTimeOut, context);
//                        dismissCustomDialog();
//                    }
//                });
//            }
//        }
    }
    private void TaskComplete(final DataElement dataElement){
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        params.putJsonParams(data.toJson());
        LogUtils.e("TaskComplete上传完成参数--->"+data.toJson());
        HttpUtils.post(this, "TaskAPI/TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if(t!=null){
                    LogUtils.e("TaskComplete上传完成返回数据--->"+t);
                    final JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("Success")!=null&&
                            jsonObjectElement.get("Success").valueAsBoolean()){
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("taskComplete"),context);
//                        if(TaskClass!=null
//                                &&TaskClass.equals(Task.TRANSFER_MODEL_TASK)
//                                &&(jsonObjectElement.get("Tag")==null||"1".equals(DataUtil.isDataElementNull(jsonObjectElement.get("Tag"))))
//                                ){//Tag为1即需要弹出对话框询问用户是否需要创建新任务
//                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
//                            builder.setMessage(LocaleUtils.getI18nValue("DoYouNeedToCreateACarMovingTask);
//                            builder.setCancelable(false);
//                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    Intent intent=new Intent(context, CusActivity.class);
//                                    intent.putExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK,Constants.FLAG_CREATE_CAR_MOVING_TASK);
//                                    if(dataElement!=null){
//                                        intent.putExtra("OperatorInfo",dataElement.toString());
//                                    }
//                                    intent.putExtra("FromTask_ID",
//                                            DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
//                                    context.startActivity(intent);
//                                }
//                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    context.startActivity(new Intent(context,CusActivity.class));
//                                }
//                            });
//                            builder.show();
//                        }else {
                        if(isSurplusSparePart&&
                                (TaskClass.equals(Task.REPAIR_TASK)||TaskClass.equals(Task.MAINTAIN_TASK))&&
                                DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.UPMATERIALSBILLALERT)).equals("1")){
                            final String DialogMessage = LocaleUtils.getI18nValue("is_create_spare_part_back");
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(DialogMessage);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context,SparePartRequestActivity.class);
                                    intent.putExtra(Task.TASK_CLASS,Task.SPAREPART_RETURN);
                                    startActivity(new Intent(context,CusActivity.class));
                                    context.startActivity(intent);
                                }
                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(context,CusActivity.class));
                                }
                            });
                            AlertDialog AddEquipmentDialog = builder.create();
                            AddEquipmentDialog.show();
                        }else{
                            startActivity(new Intent(context,CusActivity.class));
                        }
//                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.e("TaskComplete上传完成返回数据失败--->");
//                                if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()){
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("canNotSubmitTaskComplete"),context);
//                                }else {
//                                    ToastUtil.showToastLong(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")),context);
//                                }
                            }
                        });
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("TaskComplete上传完成返回数据错误--->"+errorNo+"---->"+strMsg);
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
     * create by jason 2019/3/22
     * @param condition
     * @param searchTitle
     * @param searchName
     * @param searTag
     * @param tips
     */
    private void MiantienanceDropSearch(final EditText condition,
                                        final String searchTitle,final String searchName,final int searTag ,final String tips){
        LogUtils.e("进入MiantienanceDropSearch---->"+searTag);
        LogUtils.e("MaintananceSize---->"+MaintenanceTypeList.size());
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag){
                    case 1:
                        searchDataLists.addAll(MaintenanceTypeList);
                        break;
                }
                searchtag = searTag;
                if (condition != null) {
                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        MaintienanceAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips,context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        MaintienanceAdapter.changeData(searchDataLists, searchName);
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

    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final String tips){
        LogUtils.e("DropSearch--searTag----->"+searTag);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag){
                    case 1:
                        searchDataLists.addAll(typeList);
                        break;
                    case 2:
                        searchDataLists.addAll(MaintenanceTypeList);
                        break;
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
//                        ToastUtil.showToastShort(tips,context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
//                        ToastUtil.showToastShort(tips,context);
                    }
                }

            }
        });
    }


    private void getTaskEquipmentFromServerByTaskId() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("task_id", DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        params.put("pageSize",1000);
        params.put("pageIndex",1);
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        HttpUtils.get(context, "TaskDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    ArrayElement jsonArrayElement = new JsonArrayElement(t);
                    if(jsonArrayElement.size()==0){
                        initData();
                    }else {
                        initData();
                    }
                    dismissCustomDialog();
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                initData();
                dismissCustomDialog();
                // ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetEquipmentList,context);
            }
        });
    }

    private void LoadDataByServe(){
        HttpParams params = new HttpParams();
//        params.put("Parameter","filter=DataType eq 'TaskClass' and factory_id eq 'GEW'");
        HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'TroubleClass'%20and%20factory_id%20eq%20'"+getLoginInfo().getFactoryId()+"'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    return;
                }
                DataElement jsonArrayElment = new JsonArrayElement(t);
                if(jsonArrayElment!=null&&jsonArrayElment.isArray()&&jsonArrayElment.asArrayElement().size()>0){
                    for(int i=0;i<jsonArrayElment.asArrayElement().size();i++){
                        LogUtils.e("获取维护故障分类数据成功--->"+jsonArrayElment.asArrayElement().size()+"----->"+jsonArrayElment.asArrayElement().get(i).asObjectElement());
                        //翻译DATA_NAME
                        jsonArrayElment.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
//                        typeList.add(element.asArrayElement().get(i).asObjectElement());
                        MaintenanceTypeList.add(jsonArrayElment.asArrayElement().get(i).asObjectElement());
                        MaintenanceMap.put(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
                                DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            getSummaryFromServer();
                        }
                    });
                }
                LogUtils.e("initDataByServe--测试成功--->"+t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                LogUtils.e("initDataByServe--测试成功--->"+errorNo+"--->"+strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"),context);
                    }
                });
            }
        });
    }

    /**
     * create by jason 2019/3/22
     * 加载维护故障分类的数据
     */
    private void LoadData(){
        LoadDataByServe();
//        DataUtil.getDataFromDataBase(context, "TroubleClass", new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
//                    for(int i=0;i<element.asArrayElement().size();i++){
//                        LogUtils.e("获取维护故障分类数据成功--->"+element.asArrayElement().size()+"----->"+element.asArrayElement().get(i).asObjectElement());
//                        //翻译DATA_NAME
//                        element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
////                        typeList.add(element.asArrayElement().get(i).asObjectElement());
//                        MaintenanceTypeList.add(element.asArrayElement().get(i).asObjectElement());
//                        MaintenanceMap.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
//                                DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            getSummaryFromServer();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void failure(final DatastoreException ex, String resource) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtils.e("获取加载分类失败---->"+ex.toString());
//                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"),context);
//                    }
//                });
//            }
//        });
    }

    /**
     * create by jason
     * @param Data_ID
     * @param equipmentId
     */
    private void searchDataByServe(String Data_ID,String equipmentId){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("DataType","EquipmentTroubleSort");
        String encodeDataId = HttpUtils.toURLEncoded(Data_ID);
        if(!TextUtils.isEmpty(encodeDataId)){
            params.put("DataValue1",encodeDataId);
        }else{
//            initData();
//            return;
            params.put("DataValue1","null");
        }

        if (!TextUtils.isEmpty(equipmentId)){
            params.put("DataValue2",equipmentId);
        }else{
            params.put("DataValue2","");
        }

//        params.put("DataValue1",encodeDataId);
//        params.put("DataValue2",equipmentId);

        HttpUtils.get(this, "DataRelation/GetDataRelationSimple", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                LogUtils.e("获取到动态数据---->"+t);
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    initData();
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                typeList.clear();
                try {
                    if (jsonArrayElement != null && jsonArrayElement.isArray()
                            && jsonArrayElement.asArrayElement().size() > 0) {
                        LogUtils.e("故障数据--->"+jsonArrayElement.asArrayElement().size());
                        for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                            typeList.add(jsonArrayElement.asArrayElement().get(i)
                                    .asObjectElement());
                            LogUtils.e("动态查询故障类型单条数据成功--->"+jsonArrayElement.asArrayElement().get(i)
                                    .asObjectElement()+"---->"+jsonArrayElement.asArrayElement().size());
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoGroupEquipment"), context);
                                LogUtils.e("查询数据为空");
                                //数据为空 获取默认数据
                                initData();
                            }
                        });

                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                    LogUtils.e("查询数据出错---->"+e.toString());
                    initData();
                }
                LogUtils.e("searchDataByServe---测试成功--->"+t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                LogUtils.e("searchDataByServe---测试成功--->"+errorNo+"--->"+strMsg);
                initData();
            }
        });

    }


    /**
     * jason change
     * create by jason 2019/3/22 动态获取故障类型数据
     * @param Data_ID
     */
    private void searchData(String Data_ID,String equipmentId){
        LogUtils.e("获取到相应的数据--->"+Data_ID+"---->"+equipmentId);
        String rawQuery;
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        if (equipmentId.contains(",")){
            String[] all=equipmentId.split((","));
            for (int i=0;i<all.length;i++){
                LogUtils.e("获取到设备id--->"+all[i]);
                if (i==all.length-1){
                    builder.append("'");
                    builder.append(all[i]);
                    builder.append("'");
                }else{
                    builder.append("'");
                    builder.append(all[i]);
                    builder.append("'");
                    builder.append(",");
                }

            }
            builder.append(")");
            LogUtils.e("拼接完成--->"+builder.toString());
            LogUtils.e("设备类型有两个以上--->"+equipmentId);
            searchDataByServe(builder.toString(),Data_ID);
            rawQuery = "select Data_ID,DataCode,DataName \n" +
                    "from (select Data_ID,DataCode,DataName,PData_ID from DataDictionary where DataType='EquipmentTroubleSort') d\n" +
                    "inner join  (select DataCode2,DataCode1 from DataRelation where RelationCode='03') r on d.DataCode=r.DataCode2\n" +
                    "inner join Equipment e on r.DataCode1=e.EquipmentClass\n"+
                    "where e.AssetsID in" + builder.toString() + " and d.PData_ID=" +"'"+ Data_ID +"'"+ "";
        }else{
            searchDataByServe(EquipmentId,Data_ID);
            rawQuery = "select Data_ID,DataCode,DataName \n" +
                    "from (select Data_ID,DataCode,DataName,PData_ID from DataDictionary where DataType='EquipmentTroubleSort') d\n" +
                    "inner join  (select DataCode2,DataCode1 from DataRelation where RelationCode='03') r on d.DataCode=r.DataCode2\n" +
                    "inner join Equipment e on r.DataCode1=e.EquipmentClass\n"+
                    "where e.AssetsID in" + "("+"'"+EquipmentId+"'"+")"+ " and d.PData_ID=" +"'"+ Data_ID +"'"+ "";
        }

//        String rawQuery = "select Data_ID,DataCode,DataName \n" +
//                "    from (select Data_ID,DataCode,DataName,PData_ID from DataDictionary where DataType='EquipmentTroubleSort') d\n" +
//                "    inner join  (select DataCode2,DataCode1 from DataRelation where RelationCode='03') r on d.DataCode=r.DataCode2\n" +
//                "    inner join Equipment e on r.DataCode1=e.EquipmentClass \n" +
//                "    where e.AssetsID= +"'"+FN1-QP0001+"'"+" and d.PData_ID="+"'"+16547+"'"+"";
        LogUtils.e("查询语句--->"+rawQuery);
//        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, null);
//        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//
//            @Override
//            public void onSuccess(final DataElement element) {
//                System.out.println(element);
//                LogUtils.e("动态查询故障类型数据成功--->"+element.toJson());
//                typeList.clear();
//                try {
//                    if (element != null && element.isArray()
//                            && element.asArrayElement().size() > 0) {
//                        for (int i = 0; i < element.asArrayElement().size(); i++) {
//                            typeList.add(element.asArrayElement().get(i)
//                                    .asObjectElement());
//                            LogUtils.e("动态查询故障类型单条数据成功--->"+element.asArrayElement().get(i)
//                                    .asObjectElement()+"---->"+element.asArrayElement().size());
//                        }
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoGroupEquipment"), context);
//                                LogUtils.e("查询数据为空");
//                                //数据为空 获取默认数据
//                                initData();
//                            }
//                        });
//
//                    }
//                } catch (Exception e) {
//                    CrashReport.postCatchedException(e);
//                    LogUtils.e("查询数据出错---->"+e.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                System.out.println(throwable.getMessage());
//                LogUtils.e("查询数据出错---->"+throwable.getMessage());
//            }
//        });
    }
}
