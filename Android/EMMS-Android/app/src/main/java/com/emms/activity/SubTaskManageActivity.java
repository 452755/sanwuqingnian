package com.emms.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.SubTaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Factory;
import com.emms.schema.Task;
import com.emms.ui.CustomDialog;
import com.emms.util.BaseData;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.j256.ormlite.stmt.query.In;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/6.
 */
public class SubTaskManageActivity extends NfcActivity implements View.OnClickListener {
    private PullToRefreshListView sub_task_listView;
    private SubTaskAdapter adapter;
    private ArrayList<ObjectElement> datas = new ArrayList<>();
    private String taskId;
    private ObjectElement TaskDetail;
    private Handler handler = new Handler();
    private ArrayList<ObjectElement> EquipmentList = new ArrayList<>();
    private boolean TaskComplete = false;
    private Context context = this;

    private String TaskClass = null;
    private static int PAGE_SIZE = 10;
    private int pageIndex = 1;
    private int RecCount = 0;

    private boolean isSurplusSparePart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task);
        TaskDetail = new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        taskId = DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID));
        TaskComplete = getIntent().getBooleanExtra("TaskComplete", false);
        TaskClass = getIntent().getStringExtra(Task.TASK_CLASS);
        LogUtils.e("进入SubTaskManage--tackclass--->" + TaskClass);
        initView();
        //getTaskEquipmentFromServer();
    }

    private void initView() {
        //initFooterToolbar
        if (TaskComplete) {
            LogUtils.e("<-----任务已完成---->");
            findViewById(R.id.footer_toolbar).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.preStep)).setText(LocaleUtils.getI18nValue("preStep"));
            findViewById(R.id.preStep).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            ((Button) findViewById(R.id.nextStep)).setText(LocaleUtils.getI18nValue("nextStep"));
            findViewById(R.id.nextStep).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.e("<-----进行下一步操作---->");
                    //待写
                    Intent intent;
                    if(DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASKUSEMATERIALSACTION)).equals("1")){
                        intent = new Intent(context, SparePartListViewActivity.class);
                    }else{
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
                    }
                    intent.putExtra("isSurplusSparePart",isSurplusSparePart);
                    intent.putExtra("TaskComplete", true);
                    intent.putExtra("TaskDetail", TaskDetail.toString());
                    intent.putExtra(Task.TASK_CLASS, Task.SPAREPART_TASK_USED);
                    intent.putExtra(Task.TASK_SUBCLASS,TaskClass);
                    startActivity(intent);
                }
            });
            getIsSurplusSparePart();
        }
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("sub_task"));
        ((TextView) findViewById(R.id.group_type)).setText(DataUtil.isDataElementNull(TaskDetail.get(Task.ORGANISE_NAME)));
        ((TextView) findViewById(R.id.task_number_tag)).setText(LocaleUtils.getI18nValue("task_number"));
        ((TextView) findViewById(R.id.group_type_tag)).setText(LocaleUtils.getI18nValue("group_type"));
        ((TextView) findViewById(R.id.tvAddSubTask)).setText(LocaleUtils.getI18nValue("add"));
        ((TextView) findViewById(R.id.task_number)).setText(DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        // ((TextView)findViewById(R.id.task_state)).setText(DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_STATUS)));
        sub_task_listView = (PullToRefreshListView) findViewById(R.id.sub_task_list);
        LinearLayout add_sub_task = (LinearLayout) findViewById(R.id.add_sub_task);
        sub_task_listView.setMode(PullToRefreshListView.Mode.BOTH);
        //工作编号以下的listview
        sub_task_listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex = 1;
                        getSubTaskDataFromServer();
                        sub_task_listView.onRefreshComplete();
                        //   Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                }, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSubTaskDataFromServer();
                        sub_task_listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                }, 0);
            }
        });
        add_sub_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加子任务
                CustomDialog customDialog = new CustomDialog(SubTaskManageActivity.this, R.layout.add_sub_task_dialog, R.style.MyDialog, null, EquipmentList);
                customDialog.setTaskId(taskId);
                customDialog.setDialogOnSubmit(new dialogOnSubmitInterface() {
                    @Override
                    public void onsubmit() {
                        pageIndex = 1;
                        getSubTaskDataFromServer();
                    }
                });
                customDialog.show();
             /*   Intent intent=new Intent(SubTaskManageActivity.this,AddSubTaskActivity.class);
                intent.putExtra("taskId",taskId);
                ArrayList<String> list=new ArrayList<String>();
                for(int i=0;i<EquipmentList.size();i++){
                    list.add(EquipmentList.get(i).toString());
                }
                intent.putExtra("taskEquipmentList",list);
                startActivity(intent);*/
                // LayoutInflater.from(SubTaskManageActivity.this).inflate(R.layout.activity_search, null, false);
            }
        });
        adapter = new SubTaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(SubTaskManageActivity.this).inflate(R.layout.sub_task_item, parent, false);
                    ((TextView) convertView.findViewById(R.id.work_num_tag)).setText(LocaleUtils.getI18nValue("work_num"));
                    ((TextView) convertView.findViewById(R.id.equipment_num_tag)).setText(LocaleUtils.getI18nValue("sub_task_equipment_num"));
                    ((TextView) convertView.findViewById(R.id.approve_work_hours_tag)).setText(LocaleUtils.getI18nValue("standardWorkload"));
                    ((TextView) convertView.findViewById(R.id.work_name_tag)).setText(LocaleUtils.getI18nValue("work_name"));
                    ((TextView) convertView.findViewById(R.id.work_description_tag)).setText(LocaleUtils.getI18nValue("work_description"));
                    holder = new SubTaskAdapter.TaskViewHolder();
                    holder.work_num = (TextView) convertView.findViewById(R.id.work_num);
                    holder.approve_work_hours = (TextView) convertView.findViewById(R.id.approve_work_hours);
                    holder.work_name = (TextView) convertView.findViewById(R.id.work_name);
                    // holder.status = (TextView) convertView.findViewById(R.id.status);
                    holder.work_description = (TextView) convertView.findViewById(R.id.work_description);
                    holder.equipment_num = (TextView) convertView.findViewById(R.id.equipment_num);
                    convertView.setTag(holder);
                } else {
                    holder = (SubTaskAdapter.TaskViewHolder) convertView.getTag();
                }
                holder.work_num.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkTimeCode")));
                holder.approve_work_hours.setText(DataUtil.isDataElementNull(datas.get(position).get("PlanManhour")));
                /*holder.work_name.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkName")));
                if( (LocaleUtils.getLanguage(context)!=null
                        &&  LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH )
                        || LocaleUtils.SupportedLanguage.ENGLISH == LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())  ) {
                   //TODO 有待调整
                    if("默认工时".equals(DataUtil.isDataElementNull(datas.get(position).get("WorkName")))) {
                        holder.work_name.setText("Standard Work Hour");
                      }
                    }*/
                holder.work_name.setText(LocaleUtils.getI18nValue(DataUtil.isDataElementNull(datas.get(position).get("WorkName"))));
                //holder.status.setText(DataUtil.isDataElementNull(datas.get(position).get("Status")));
                holder.work_description.setText(DataUtil.isDataElementNull(datas.get(position).get("DataDescr")));
                //holder.equipment_num.setText(DataUtil.isDataElementNull(datas.get(position).get("Equipment_ID")));
                holder.equipment_num.setText(DataUtil.isDataElementNull(datas.get(position).get("AssetsID")));
                return convertView;
            }
        };
        sub_task_listView.setAdapter(adapter);
        sub_task_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CustomDialog customDialog=new CustomDialog(SubTaskManageActivity.this,R.layout.add_sub_task_dialog,R.style.MyDialog,
//                        datas.get(position-1),EquipmentList);
//                customDialog.setDialogOnSubmit(new dialogOnSubmitInterface() {
//                    @Override
//                    public void onsubmit() {
//                        pageIndex=1;
//                        getSubTaskDataFromServer();
//                    }
//                });
//                customDialog.setTaskId(taskId);
//                customDialog.show();
            }
        });
        getSubTaskDataFromServer();
    }

    @Override
    public void onClick(View v) {
        {
            int id_click = v.getId();
            if (id_click == R.id.btn_right_action) {
                finish();
            }
        }

    }

    private void getSubTaskDataFromServer() {
        if (RecCount != 0) {
            if ((pageIndex - 1) * PAGE_SIZE >= RecCount) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData"), context);
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("task_id", taskId);
        params.put("pageSize", PAGE_SIZE);
        params.put("pageIndex", pageIndex);
        HttpUtils.get(this, "TaskItemAPI/GetTaskItemList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    try {
                        JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                        if (jsonObjectElement.get("PageData") != null
                                && jsonObjectElement.get("PageData").isArray()
                                && jsonObjectElement.get("PageData").asArrayElement().size() > 0) {
                            RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                            if (pageIndex == 1) {
                                datas.clear();
                            }
                            pageIndex++;
                            for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                                datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                            }
                            adapter.setDatas(datas);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Throwable throwable) {
                        CrashReport.postCatchedException(throwable);
                    } finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }

    private void getIsSurplusSparePart(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        HttpUtils.post(this, "MaterialRequest/WhetherUserOwnsStock?operatorId="+(int)getLoginInfo().getId(), params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                LogUtils.e("MaterialRequest/WhetherUserOwnsStock--->查询的结果--->"+t);
                JsonObjectElement json = new JsonObjectElement(t);
                if(json.get("Success").valueAsBoolean()){
                    isSurplusSparePart = true;
                }
                else {
                    isSurplusSparePart = false;
                    LogUtils.e(DataUtil.isDataElementNull(json.get("Msg")));
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg){
                LogUtils.e("MaterialRequest/WhetherUserOwnsStock--获取失败--->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastLong(strMsg,context);
                dismissCustomDialog();
            }
        });
    }


//    public void getTaskEquipmentFromServer(){
//            if (null == taskId) {
//                return;
//            }
//
//            HttpParams params = new HttpParams();
//            params.put("task_id", taskId);
//        params.put("pageSize",1000);
//        params.put("pageIndex",1);
//            //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
//            HttpUtils.get(this, "TaskDetailList", params, new HttpCallback() {
//                @Override
//                public void onSuccess(String t) {
//                    super.onSuccess(t);
//                    Log.e("returnString", t);
//                    if (t != null) {
//                  //      JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
//                   //     if (!jsonObjectElement.get("PageData").isNull()) {
//                 //           ArrayElement jsonArrayElement = jsonObjectElement.get("PageData").asArrayElement();
//                        ArrayElement jsonArrayElement=new JsonArrayElement(t);
//                            if ( jsonArrayElement.size() > 0) {
//
//                                for (int i = 0; i < jsonArrayElement.size(); i++) {
//                                    EquipmentList.add(jsonArrayElement.get(i).asObjectElement());
//                                }
//                            }
//                        }
//
//                }
//                @Override
//                public void onFailure(int errorNo, String strMsg) {
//
//                    super.onFailure(errorNo, strMsg);
//                   Toast toast=Toast.makeText(SubTaskManageActivity.this,"获取设备信息失败,请检查网络",Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER,0,0);
//                    toast.show();
//                }
//            });
//
//    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
