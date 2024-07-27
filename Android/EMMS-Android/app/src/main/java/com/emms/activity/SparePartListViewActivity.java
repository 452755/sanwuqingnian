package com.emms.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.SparePartAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.SparePartRequestCheckMsgDialog;
import com.emms.util.BaseData;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Abrahamguo 2021-12-22 增加备件请求单详情页面
 *
 */
public class SparePartListViewActivity extends NfcActivity implements View.OnClickListener {
    private ObjectElement requestDetail;
    private PullToRefreshListView SparePart_ListView;//备件列表
    private ArrayList<ObjectElement> SparePart_list = new ArrayList<>();//备件数据集合
    private SparePartAdapter adapter;//备件数据适配器
    private Context context = this;
    private int pageIndex = 1;//分页索引
    private int RecCount = 0;//总数量
    private Handler handler = new Handler();

    private JsonArrayElement selectSparePartNum;
    private String TaskClass;
    private String Task_ID;
    private String Equipment_Id;
    private String requestNo;
    private JsonObjectElement TaskDetail;
    private boolean TaskComplete;
    private String TaskSubClass;
    private boolean isSurplusSparePart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_part_list_view);
        TaskClass = getIntent().getStringExtra(Task.TASK_CLASS);
        SparePart_ListView = (PullToRefreshListView) findViewById(R.id.spare_part_list);
        LogUtils.e("进入备件列表查看页面");
        LogUtils.e("TaskClass----" + TaskClass);
        initView();
    }

    /**
     * 初始化页面
     * */
    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        switch (TaskClass) {
            case Task.SPAREPART_CONFIRM: {
                LogUtils.e("进入挑选备件确认列表");
                initViewBySparePartConfirm();
                break;
            }
            case Task.SPAREPART_DETAILS:{
                LogUtils.e("进入备件申请单详情页面");
                initViewBySparePartDetails();
                break;
            }
            case Task.SPAREPART_TASK_USED:{
                LogUtils.e("进入任务所使用备件列表");
                initViewBySparePartTaskUsed();
                break;
            }
            case Task.SPAREPART_EQUIPMENT_USED:{
                LogUtils.e("进入机器已使用备件页面");
                initViewBySpartPartEquipmentUsed();
                break;
            }
            case Task.SPAREPART_SURPLUS:{
                LogUtils.e("进入备件剩余页面");
                initViewBySparePartSurplus();
                break;
            }
        }
    }

    //备件确认列表
    private void initViewBySparePartConfirm(){
        findViewById(R.id.summit).setVisibility(View.VISIBLE);
        findViewById(R.id.summit).setOnClickListener(this);
        ((Button) findViewById(R.id.summit)).setText(LocaleUtils.getI18nValue("warning_message_confirm"));
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("spare_part_comfirm_list"));
        String selectSparePart = getIntent().getStringExtra("selectSparePart");
        JsonArrayElement selectSparePartJson = new JsonArrayElement(selectSparePart);
        for(int i = 0 ; i < selectSparePartJson.size();i++){
            SparePart_list.add(selectSparePartJson.get(i).asObjectElement());
        }
        adapter = new SparePartAdapter(context,SparePart_list,TaskClass);
        SparePart_ListView.setMode(PullToRefreshListView.Mode.DISABLED);
        SparePart_ListView.setAdapter(adapter);
    }

    //备件申请单详情页
    private void initViewBySparePartDetails(){
        findViewById(R.id.request_detail_layout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("material_requisition_details"));
        ((TextView) findViewById(R.id.request_codeTxt)).setText(LocaleUtils.getI18nValue("request_no"));
        ((TextView) findViewById(R.id.request_departmentsTxt)).setText(LocaleUtils.getI18nValue("property_using_dept_1"));
        ((TextView) findViewById(R.id.request_creatorTxt)).setText(LocaleUtils.getI18nValue("applicanter"));
        ((TextView) findViewById(R.id.request_typeTxt)).setText(LocaleUtils.getI18nValue("reuqest_type"));
        ((TextView) findViewById(R.id.request_create_dateTxt)).setText(LocaleUtils.getI18nValue("create_time"));

        ((TextView) findViewById(R.id.request_status)).setTextColor(Color.RED);
        requestNo = getIntent().getStringExtra("requestNo");
        LogUtils.e("requestNo---->"+requestNo);
        initRequestData();
        initSparePartDate();
        adapter = new SparePartAdapter(context,SparePart_list,TaskClass);
        SparePart_ListView.setAdapter(adapter);
        SparePart_ListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
        SparePart_ListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SparePart_ListView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initSparePartDate();
                        SparePart_ListView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        findViewById(R.id.request_code).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("requestNo", requestNo);
                clipboardManager.setPrimaryClip(mClipData);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("copy_succeeded"),context);
                return false;
            }
        });
    }

    //备件剩余
    private void initViewBySparePartSurplus(){
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("spare_part_surplus_list"));
        adapter = new SparePartAdapter(context,SparePart_list,TaskClass);
        SparePart_ListView.setAdapter(adapter);
        SparePart_ListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
        SparePart_ListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SparePart_ListView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initSparePartDate();
                        SparePart_ListView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        initSparePartDate();
    }

    //已使用备件查询(机器)
    private void initViewBySpartPartEquipmentUsed(){
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("spare_part_used_list"));
        Task_ID = getIntent().getStringExtra("TaskID");
        Equipment_Id = getIntent().getStringExtra("EquipmentId");
        adapter = new SparePartAdapter(context,SparePart_list,TaskClass);
        SparePart_ListView.setAdapter(adapter);
        SparePart_ListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
        SparePart_ListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SparePart_ListView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initSparePartDate();
                        SparePart_ListView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        initSparePartDate();
    }

    //已使用备件查询(任务)
    private void initViewBySparePartTaskUsed(){
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("spare_part_used_list"));
        TaskDetail = new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        Task_ID = DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID));
        TaskComplete = getIntent().getBooleanExtra("TaskComplete", false);
        TaskClass = getIntent().getStringExtra(Task.TASK_CLASS);
        TaskSubClass = getIntent().getStringExtra(Task.TASK_SUBCLASS);
        isSurplusSparePart = getIntent().getBooleanExtra("isSurplusSparePart",false);
        adapter = new SparePartAdapter(context,SparePart_list,TaskClass);
        SparePart_ListView.setAdapter(adapter);
        SparePart_ListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
        SparePart_ListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SparePart_ListView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initSparePartDate();
                        SparePart_ListView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        findViewById(R.id.footer_toolbar).setVisibility(View.VISIBLE);
        findViewById(R.id.preStep).setOnClickListener(this);
        findViewById(R.id.nextStep).setOnClickListener(this);
        ((Button) findViewById(R.id.preStep)).setText(LocaleUtils.getI18nValue("preStep"));
        ((Button) findViewById(R.id.nextStep)).setText(LocaleUtils.getI18nValue("nextStep"));
        initSparePartDate();
    }

    /**
     * 重写方法
     *
     * */

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1){
            if(resultCode == SparePartActivity.SPAREPART_CHOOSE_CONFIRM_RESULTCODE){
                LogUtils.e("重新挑选备件完成");
                String selectSparePartStr = data.getStringExtra("selectSparePart");
                LogUtils.e("重新挑选的备件"+selectSparePartStr);
                String selectSparePartNumStr = data.getStringExtra("selectSparePartNum");
                selectSparePartNum = new JsonArrayElement(selectSparePartNumStr);
                updateRequestSparePart();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_right_action: {
                LogUtils.e("<-----返回上一个页面----->");
                finish();
                break;
            }
            case R.id.summit:{
                LogUtils.e("<-----确认所挑选的备件----->");
                setResult(SparePartActivity.SPAREPART_CHOOSE_CONFIRM_RESULTCODE);
                finish();
                break;
            }
            case R.id.preStep:{
                LogUtils.e("<-----返回上一步----->");
                finish();
                break;
            }
            case R.id.nextStep:{
                LogUtils.e("<-----进行下一步操作---->");
                //待写
                Intent intent;
                switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_COMPLETE_SHOW_WORKLOAD_ACTION))) {
                    case "1": {
                        LogUtils.e("<-----跳转到SummaryActivity--->");
                        intent = new Intent(context, SummaryActivity.class);
                        break;
                    }
                    default: {
                        LogUtils.e("<-----跳转到WorkLoadActivity--->");
                        intent = new Intent(context, WorkLoadActivity.class);
                        break;
                    }
                }
                intent.putExtra("TaskComplete", TaskComplete);
                intent.putExtra("TaskDetail", TaskDetail.toString());
                intent.putExtra("isSurplusSparePart",isSurplusSparePart);
                intent.putExtra(Task.TASK_CLASS, TaskSubClass);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }


    /**
     * 网络请求
     *
     * */

    //查询申请单详情
    private void initRequestData(){
        LogUtils.e("<-----查询备件申请单详情----->");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("SpareBillNo",requestNo);
        HttpUtils.get(context, "MaterialRequest/GetRequestBillByNo_Head", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到备件请求单详情数据----->"+t);
                        requestDetail = new JsonObjectElement(t);
                        ((TextView) findViewById(R.id.request_codeVal)).setText(DataUtil.isDataElementNull(requestDetail.get("SpareBillNo")));
                        ((TextView) findViewById(R.id.request_departmentsVal)).setText(DataUtil.isDataElementNull(requestDetail.get("OrganiseName")));
                        ((TextView) findViewById(R.id.request_creatorVal)).setText(DataUtil.isDataElementNull(requestDetail.get("OperatorName")));
                        ((TextView) findViewById(R.id.request_create_dateVal)).setText(DataUtil.isDataElementNull(requestDetail.get("Create_Date")));
                        Map<String,String> BillTypes = SharedPreferenceManager.getHashMapData(context,"SpareBillType");
                        ((TextView) findViewById(R.id.request_typeVal)).setText(BillTypes.get(DataUtil.isDataElementNull(requestDetail.get("BillType"))));
                        String RequestStatus = DataUtil.isDataElementNull(requestDetail.get("BillStatus"));
                        Map<String,String> BillStatuses = SharedPreferenceManager.getHashMapData(context,"BillStatus");
                        ((TextView) findViewById(R.id.request_status)).setText(BillStatuses.get(RequestStatus));
                        if(requestDetail.get("CheckMessage")!=null
                                &&!requestDetail.get("CheckMessage").isNull()
                                &&!requestDetail.get("CheckMessage").valueAsString().equals("")
                                &&requestDetail.get("BillStatus").valueAsString().equals("Pending")
                                &&requestDetail.get("BillType").valueAsString().equals("Receive")){
                            LogUtils.e(requestNo + "-----CheckMessage-----" + requestDetail.get("CheckMessage"));
                            final SparePartRequestCheckMsgDialog sparePartRequestCheckMsgDialog = new SparePartRequestCheckMsgDialog(context);
                            sparePartRequestCheckMsgDialog.setTips(LocaleUtils.getI18nValue("the_warehouse_is_missing_a_material_list"),LocaleUtils.getI18nValue("resubmit_after_modification"));
                            JsonArrayElement checkMessage = new JsonArrayElement(DataUtil.isDataElementNull(requestDetail.get("CheckMessage")));
                            sparePartRequestCheckMsgDialog.setCheckMsg(checkMessage);
                            sparePartRequestCheckMsgDialog.setCloseDrawerListener(new CloseDrawerListener() {
                                @Override
                                public void close() {
                                    LogUtils.e("<-----重新挑选备件----->");
                                    sparePartRequestCheckMsgDialog.dismiss();
                                    Intent intent = new Intent(context, SparePartActivity.class);
                                    intent.putExtra("requestNo",requestNo);
                                    intent.putExtra(Task.TASK_CLASS,Task.SPAREPART_CHECK_MESSAGE);
                                    startActivityForResult(intent,1);
                                }
                            });
                            sparePartRequestCheckMsgDialog.show();
                        }
                        if(requestDetail.get("BillStatus").valueAsString().equals("Error")
                                &&!DataUtil.isDataElementNull(requestDetail.get("Remark")).equals("")){
                            final String DialogMessage = DataUtil.isDataElementNull(requestDetail.get("Remark"));
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(DialogMessage);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    }catch (Exception e){
                        LogUtils.e(e.getMessage());
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),context);}
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                LogUtils.e("请求失败----"+strMsg);
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                dismissCustomDialog();
            }
        });
    }

    //获取备件列表数据
    private void initSparePartDate(){
        LogUtils.e("pageindex----->"+pageIndex);
        int PAGE_SIZE = 10;
        if(RecCount!=0){
            if((pageIndex-1)* PAGE_SIZE >=RecCount){
                Toast toast=Toast.makeText(context, LocaleUtils.getI18nValue("noMoreData"),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }
        }

        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到备件请求单数据----->"+t);
                        JsonObjectElement json=new JsonObjectElement(t);
                        if(json.get("Success").valueAsBoolean()){
                            RecCount=json.get("RecCount").valueAsInt();
                            LogUtils.e("RecCount---->"+RecCount);
                            if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                                if(pageIndex==1){
                                    SparePart_list.clear();
                                }
                                LogUtils.e("pageIndexeee---->"+pageIndex);
                                pageIndex++;
                                for (int i = 0;i<json.get("PageData").asArrayElement().size();i++){
                                    SparePart_list.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                }
                            }else{
                                if(pageIndex == 1){
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                                }else{
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData"),context);
                                }
                            }
                            adapter.setListItems(SparePart_list);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),context);
                        }
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                dismissCustomDialog();
            }
        };

        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("pageSize", PAGE_SIZE);
        params.put("pageNumber",pageIndex);

        switch (TaskClass){
            case Task.SPAREPART_DETAILS:{
                LogUtils.e("<-----获取备件申请单 " + requestNo + " 备件集合----->");
                params.put("SpareBillNo",requestNo);
                LogUtils.e(params.getUrlParams().toString());
                HttpUtils.get(context, "MaterialRequest/GetRequestBillByNo_Detail", params, callback);
                break;
            }
            case Task.SPAREPART_EQUIPMENT_USED:{
                LogUtils.e("<-----获取 Task_ID--->" + Task_ID + "  Equipment_Id--->" +Equipment_Id+ " 所使用的备件集合----->");
                params.put("TaskId",Task_ID);
                params.put("EquipmentId",Equipment_Id);
                LogUtils.e(params.getUrlParams().toString());
                HttpUtils.get(context, "MaterialRequest/GetMaterialUsageList", params, callback);
                break;
            }
            case Task.SPAREPART_TASK_USED:{
                LogUtils.e("<----------获取 Task_ID--->" + Task_ID + " 所使用的备件集合---------->");
                params.put("TaskId",Task_ID);
                LogUtils.e(params.getUrlParams().toString());
                HttpUtils.get(context, "MaterialRequest/GetMaterialUsageByTaskId", params, callback);
                break;
            }
            case Task.SPAREPART_SURPLUS:{
                LogUtils.e("<-----获取机修库存剩余备件集合----->");
                params.put("operatorId",(int)getLoginInfo().getId());
                LogUtils.e(params.getUrlParams().toString());
                HttpUtils.get(context, "MaterialRequest/GetMachineRepairStockListByApp", params, callback);
                break;
            }
        }
    }

    private void updateRequestSparePart(){
        LogUtils.e("----修改备件申请单所选备件列表----");
        showCustomDialog(LocaleUtils.getI18nValue("waiting"));
        JsonObjectElement json = new JsonObjectElement();
        json.set("Spare_BillNo",requestNo);
        json.set("Bill_Type",requestDetail.get("BillType").valueAsString());
        json.set("Factory",requestDetail.get("Factory").valueAsString());
        json.set("spare_Details",selectSparePartNum);
        json.set("Id",requestDetail.get("Id"));
        HttpParams params = new HttpParams();
        params.putJsonParams(json.toJson());
        HttpUtils.post(context,"MaterialRequest/ModifyMaterialRequest",params,new HttpCallback(){
            @Override
            public void onSuccess(String t){
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("MaterialRequest/ModifyMaterialRequest-----修改申请单请求结果----->"+t);
                        JsonObjectElement json=new JsonObjectElement(t);
                        if(json.get("Success").valueAsBoolean()){
                            ToastUtil.showToastLong(LocaleUtils.getI18nValue("submitSuccess"),context);
                            pageIndex = 1;
                            initSparePartDate();
                        }
                        else{
                            LogUtils.e(json.get("Msg").toString());
                            ToastUtil.showToastLong(DataUtil.isDataElementNull(json.get("Msg")),context);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),context);
                        }
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg){
                HttpUtils.tips(context, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("MaterialRequest_Create--提交失败--->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastLong(LocaleUtils.getI18nValue("submit_Fail"),context);
                dismissCustomDialog();
            }
        });
    }
}