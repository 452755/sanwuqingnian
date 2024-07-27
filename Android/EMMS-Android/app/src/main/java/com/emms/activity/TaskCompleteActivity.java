package com.emms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.commandAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.ui.HorizontalListView;
import com.emms.util.BaseData;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/1/9.
 *
 */
public class TaskCompleteActivity extends NfcActivity{
    private enum Mode{
        ACCOUNT_AND_PASSWORD,
        IC_CARD
    }
    private Mode mMode=Mode.IC_CARD;
//    private boolean TaskComplete;
//    private String TaskClass;
    private ObjectElement TaskDetail;
    private Context context=this;
    private String orderNo;
    String TaskClass = "";
    private boolean isSurplusSparePart;
    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(mMode==Mode.IC_CARD){
                //TODO
                String iccardID = NfcUtils.dumpTagData(tag);
                if (TaskClass.equals("T08")){
                    StylChangeOrderTask();
                }else{
//                    GetTaskEvaluationInfo(mMode,iccardID);
                    SubmitData(mMode,iccardID);
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_complete);
        LogUtils.e("进入转款订单完成界面");
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        TaskClass = getIntent().getStringExtra(Task.TASK_CLASS);
        orderNo = getIntent().getStringExtra("orderNo")==null?"":getIntent().getStringExtra("orderNo").toString();
        isSurplusSparePart = getIntent().getBooleanExtra("isSurplusSparePart",false);
//        TaskComplete=getIntent().getBooleanExtra("TaskComplete",false);
//        if(getIntent().getStringExtra(Task.TASK_CLASS)!=null){
//            TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
//        }
        if (mAdapter!=null&&mAdapter.isEnabled()) {
            mMode=Mode.IC_CARD;
        }else {
            mMode=Mode.ACCOUNT_AND_PASSWORD;
        }
        initView();
        initTaskCommand();
//        GetTaskEvaluationInfo();
//        SubmitData(mMode,"000008");
    }
    private void initView(){
        if(mMode==Mode.ACCOUNT_AND_PASSWORD){
            findViewById(R.id.inputInfo_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.SwipeLayout).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvAccount)).setText(LocaleUtils.getI18nValue("account"));
            ((EditText)findViewById(R.id.account)).setHint(LocaleUtils.getI18nValue("login_id_hint"));
            ((TextView)findViewById(R.id.tvPassword)).setText(LocaleUtils.getI18nValue("password"));
            ((EditText)findViewById(R.id.password)).setHint(LocaleUtils.getI18nValue("login_password_hint"));
            ((TextView)findViewById(R.id.Tips)).setText(LocaleUtils.getI18nValue("TaskCompleteTipsForAccountAndPassword"));
            ((Button)findViewById(R.id.comfirm)).setText(LocaleUtils.getI18nValue("sure"));
        }else {
            findViewById(R.id.inputInfo_layout).setVisibility(View.GONE);
            findViewById(R.id.SwipeLayout).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.title)).setText(LocaleUtils.getI18nValue("pleaseSwipeICcardToMakeSureTaskComplete"));
            ((TextView)findViewById(R.id.scan)).setText(LocaleUtils.getI18nValue("scan"));
            findViewById(R.id.comfirm).setVisibility(View.GONE);
            findViewById(R.id.Tips).setVisibility(View.GONE);
        }
        ((TextView)findViewById(R.id.response_speed_tag)).setText(LocaleUtils.getI18nValue("response_speed"));
        ((TextView)findViewById(R.id.service_attitude_tag)).setText(LocaleUtils.getI18nValue("service_attitude"));
        ((TextView)findViewById(R.id.repair_speed_tag)).setText(LocaleUtils.getI18nValue("repair_speed"));
        EditText inputPassWord=(EditText)findViewById(R.id.password);
        inputPassWord.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        inputPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("taskComplete"));
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TaskClass.equals("T08")){
                    StylChangeOrderTask();
                }else{
                    SubmitData(mMode,null);
                }

            }
        });
    }

    /**
     * 转款专用完成任务的接口
     */
    private void StylChangeOrderTask(){
        LogUtils.e("转款专门的取消任务接口---->"+DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set("taskId",DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        submitData.set("status",99);
        submitData.set("quitReason","");
        params.putJsonParams(submitData.toJson());
        LogUtils.e("转款取消单上传参数---->"+submitData.toString());
        HttpUtils.getChangeFormServer(context, "emms/transferTask/updateTransferTaskStatus", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("转款-完成单数据返回失败---->"+errorNo+"---->"+strMsg);
                dismissCustomDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"),context);
                    }
                });
            }

            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                LogUtils.e("转款完成单的返回数据--->"+t);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(t!=null){
                            final JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get("Success")!=null&&
                                    jsonObjectElement.get("Success").valueAsBoolean()){
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("taskComplete"),context);
                                patchStyleChangeOrderStatus("11");
                                startActivity(new Intent(context,CusActivity.class));
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()) {
                                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("canNotSubmitTaskComplete"), context);
                                        }else {
                                            TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                        }
                                    }
                                });
                            }
                        }
                        dismissCustomDialog();
                    }
                });
            }
        });
    }


    private void patchStyleChangeOrderStatus(String status){
        if(orderNo.equals("")){
            return;
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        HttpUtils.patchChangeStyle(context, "1.1/emms/order/"+orderNo+"/"+status, params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailPatchOrderStatus+errorNo"),context);
                dismissCustomDialog();
            }
        });
    }

    private void SubmitData(Mode mMode,String IC_Card_ID){
        try{
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        if (TaskDetail==null){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"),context);
            return;
        }
        submitData.set(Task.TASK_ID, DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        submitData.set("RespondSpeed",command.get("response_speed"));
        submitData.set("ServiceAttitude",command.get("service_attitude"));
        submitData.set("MaintainSpeed",command.get("repair_speed"));
        submitData.set("TaskEvaluation_ID",0);
        switch (mMode){
            case ACCOUNT_AND_PASSWORD:{
                TextView account=(TextView)findViewById(R.id.account);
                TextView password=(TextView)findViewById(R.id.password);
                if(account.getText().toString().equals("")){
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("warning_message_no_user"),context);
                    return;
                }
                if(password.getText().toString().equals("")){
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("warning_message_no_password"),context);
                    return;
                }
                submitData.set("OperatorNo",account.getText().toString().toUpperCase());
                submitData.set("Password",password.getText().toString());
                break;
            }
            default:{
                submitData.set("ICCardID",IC_Card_ID);
                break;
            }
        }
        params.putJsonParams(submitData.toJson());
        LogUtils.e("上传评价请求字段---->"+submitData.toJson());
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpUtils.post(context, "TaskAPI/TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(t!=null){
                            final JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get("Success")!=null&&
                                    jsonObjectElement.get("Success").valueAsBoolean()){
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("taskComplete"),context);
                                patchStyleChangeOrderStatus("11");
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
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()) {
                                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("canNotSubmitTaskComplete"), context);
                                        }else {
                                            TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                        }
                                    }
                                });
                            }
                        }
                        dismissCustomDialog();
                    }
                });
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"),context);
                    }
                });
            }
        });
        }catch (Exception e){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"),context);
            CrashReport.postCatchedException(e);
        }
    }
    private ArrayList<Integer> response_speed_list=new ArrayList<>();
    private ArrayList<Integer> service_attitude_list=new ArrayList<>();
    private ArrayList<Integer> repair_speed_list=new ArrayList<>();
    private commandAdapter response_speed_adapter,service_attitude_adapter,repair_speed_adapter;
    private HorizontalListView response_speed,service_attitude,repair_speed;
    private HashMap<String,Integer> command=new HashMap<>();
    private void initTaskCommand(){
        response_speed=(HorizontalListView)findViewById(R.id.response_speed);
        service_attitude=(HorizontalListView)findViewById(R.id.service_attitude);
        repair_speed=(HorizontalListView)findViewById(R.id.repair_speed);
        for(int i=0;i<5;i++){
            response_speed_list.add(0);
            service_attitude_list.add(0);
            repair_speed_list.add(0);
        }
        command.put("response_speed",0);
        command.put("service_attitude",0);
        command.put("repair_speed",0);
        response_speed_adapter=new commandAdapter(this,response_speed_list);
        response_speed.setAdapter(response_speed_adapter);

        service_attitude_adapter=new commandAdapter(this,service_attitude_list);
        service_attitude.setAdapter(service_attitude_adapter);

        repair_speed_adapter=new commandAdapter(this,repair_speed_list);
        repair_speed.setAdapter(repair_speed_adapter);
        initListViewOnItemClickEvent();
    }
    public void setCommandData(int num, String key, final ArrayList<Integer> numList, final commandAdapter cAdapter){
        command.put(key,num);
        for(int i=0;i<5;i++){
            if(i<num){
                numList.set(i,1);
            }else {
                numList.set(i,0);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cAdapter.setDatas(numList);
                cAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initListViewOnItemClickEvent(){
        response_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"response_speed",response_speed_list,response_speed_adapter);
            }
        });
        service_attitude.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"service_attitude",service_attitude_list,service_attitude_adapter);
            }
        });
        repair_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"repair_speed",repair_speed_list,repair_speed_adapter);
            }
        });
    }

    /**
     * 新增检查接口
     */
    public void GetTaskEvaluationInfo(final Mode mMode, final String IC_Card_ID){
        HttpParams params = new HttpParams();
        params.put("task_id",DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        HttpUtils.get(context, "TaskEvaluationAPI/GetTaskEvaluationInfo", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t==null&&TextUtils.isEmpty(t)){
                    return;
                }
                LogUtils.e("获取评价接口成功---->"+t);
                final JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                if(jsonObjectElement.get("Success")!=null&&
                        jsonObjectElement.get("Success").valueAsBoolean()){
                    LogUtils.e("成功");
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").isArray()){
                        if(jsonObjectElement.get("PageData").asArrayElement().size()>0){
//                            TaskEvaluation_ID=jsonObjectElement.get("PageData").asArrayElement().get(0).asObjectElement().get("TaskEvaluation_ID").valueAsInt();
                            ObjectElement objectElement=jsonObjectElement.get("PageData").asArrayElement().get(0).asObjectElement();
                            setCommandData(objectElement.get("RespondSpeed").valueAsInt(),"response_speed",response_speed_list,response_speed_adapter);
                            setCommandData(objectElement.get("ServiceAttitude").valueAsInt(),"service_attitude",service_attitude_list,service_attitude_adapter);
                            setCommandData(objectElement.get("MaintainSpeed").valueAsInt(),"repair_speed",repair_speed_list,repair_speed_adapter);
                        }else{
                            SubmitData(mMode,IC_Card_ID);
                        }
                    }

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()) {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("canNotSubmitTaskComplete"), context);
                            }else {
                                TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                LogUtils.e("提交失败--->"+errorNo+"---->"+strMsg);
            }
        });
    }
}
