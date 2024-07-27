package com.emms.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/20.
 *
 */
public class PendingCommandFragment extends BaseFragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> data=new ArrayList<>();
    private Context mContext;
    private Handler handler=new Handler();
    //private String TaskClass="";
    private  int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private boolean iSQueryOnlyMyselfBill=true;
    private String timeCode="";
    private static HashMap<String,String> map=new HashMap<>();
    private static HashMap<String,String> taskStatusMap=new HashMap<>();

    private int removeNum=0;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        //initMap();
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView)v.findViewById(R.id.processing_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex=1;
                        removeNum=0;
                        getTaskHistory();
                        listView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getTaskHistory();
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        taskAdapter = new TaskAdapter(data) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
                    ((TextView)convertView.findViewById(R.id.Warranty_person_tag)).setText(LocaleUtils.getI18nValue("warranty_person"));
                    ((TextView)convertView.findViewById(R.id.id_Warranty_time_description)).setText(LocaleUtils.getI18nValue("repair_time"));
                    ((TextView)convertView.findViewById(R.id.target_group_tag)).setText(LocaleUtils.getI18nValue("target_group_tag"));
                    ((TextView)convertView.findViewById(R.id.tv_task_class_tag)).setText(LocaleUtils.getI18nValue("task_t"));
                    ((TextView)convertView.findViewById(R.id.tv_task_state_tag)).setText(LocaleUtils.getI18nValue("task_state"));
                    ((TextView)convertView.findViewById(R.id.id_start_time_description)).setText(LocaleUtils.getI18nValue("start_time"));
                    ((TextView)convertView.findViewById(R.id.id_end_time_description)).setText(LocaleUtils.getI18nValue("end_time"));
                    ((TextView)convertView.findViewById(R.id.Task_Equipment_tag)).setText(LocaleUtils.getI18nValue("device_name"));
                    ((TextView)convertView.findViewById(R.id.Task_Equipment_Num_tag)).setText(LocaleUtils.getI18nValue("device_num"));
                    ((TextView)convertView.findViewById(R.id.id_task_description)).setText(LocaleUtils.getI18nValue("task_descripbe"));
                    //新增
                    ((TextView)convertView.findViewById(R.id.task_verify_person_tag)).setText(LocaleUtils.getI18nValue("Approver"));
                    ((TextView)convertView.findViewById(R.id.task_verify_reason_tag)).setText(LocaleUtils.getI18nValue("ApproverReason"));
                    holder = new TaskViewHolder();
    //                holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time= (TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_device_name=(TextView)convertView.findViewById(R.id.tv_task_class);
                    holder.tv_creater=(TextView)convertView.findViewById(R.id.command);
                    holder.tv_create_time=(TextView)convertView.findViewById(R.id.Task_Equipment);
                    holder.tv_device_num=(TextView)convertView.findViewById(R.id.Task_Equipment_Num);
                    holder.tv_target_group=(TextView)convertView.findViewById(R.id.target_group);
                    holder.tv_group=(TextView)convertView.findViewById(R.id.target_group_tag);
                    //新增
                    holder.tv_verify_person_tag=(TextView)convertView.findViewById(R.id.task_verify_person_tag);
                    holder.tv_verify_person=(TextView)convertView.findViewById(R.id.task_verify_person);
                    holder.tv_verify_reason_tag=(TextView)convertView.findViewById(R.id.task_verify_reason_tag);
                    holder.tv_verify_reason=(TextView)convertView.findViewById(R.id.task_verify_reason);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //  holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get("Organise_ID")));
                if(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS)).equals(Task.MOVE_CAR_TASK)){
                    holder.tv_target_group.setVisibility(View.VISIBLE);
                    holder.tv_group.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_target_group.setVisibility(View.GONE);
                    holder.tv_group.setVisibility(View.GONE);
                }
                holder.tv_target_group.setText(DataUtil.isDataElementNull(data.get(position).get("TargetTeam")));
                holder.tv_create_time.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentName")));
                holder.tv_device_num.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentAssetsIDList")));
//                holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT)));
                String checkStatus=DataUtil.isDataElementNull(data.get(position).get("CheckStatus"));
                if(  ("2".equals(checkStatus) || "3".equals(checkStatus))
                        && BaseData.CheckStatus.get(checkStatus)!=null){
                    holder.tv_task_state.setText(BaseData.CheckStatus.get(checkStatus));
                }else {
                    if (taskStatusMap.get(DataUtil.isDataElementNull(data.get(position).get("Status"))) != null) {
                        holder.tv_task_state.setText(taskStatusMap.get(DataUtil.isDataElementNull(data.get(position).get("Status"))));
                    } else {
                        holder.tv_task_state.setText(DataUtil.isDataElementNull(data.get(position).get("Status")));
                    }
                }
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.START_TIME))));
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.FINISH_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_DESCRIPTION)));
                if(data.get(position).get("IsEvaluated").valueAsBoolean()){
                    holder.tv_creater.setText(LocaleUtils.getI18nValue("isCommand"));
                    holder.tv_creater.setTextColor(getResources().getColor(R.color.order_color));
                }else{
                    holder.tv_creater.setText(LocaleUtils.getI18nValue("NoCommand"));
                    holder.tv_creater.setTextColor(getResources().getColor(R.color.esquel_red));
                }
                if(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS)))!=null) {
                    if (DataUtil.isDataElementNull(data.get(position).get("ModuleType")).equals("Property")) {
                        if (DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS)).equals(Task.REPAIR_TASK)) {
                            holder.tv_device_name.setText(LocaleUtils.getI18nValue("facility_repair"));
                        }
                    } else {
                        holder.tv_device_name.setText(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS))));
                    }
                }
                if(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_SUBCLASS)))!=null){
                    holder.tv_device_name.setText(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_SUBCLASS))));
                }
                if("2".equals(checkStatus)
                        ||"3".equals(checkStatus)
                        ||"3".equals(DataUtil.isDataElementNull(data.get(position).get("Status")))){
                    holder.tv_verify_person_tag.setVisibility(View.VISIBLE);
                    holder.tv_verify_person.setVisibility(View.VISIBLE);
                    holder.tv_verify_person.setText(DataUtil.isDataElementNull(data.get(position).get("CheckOperator")));
                    if("3".equals(checkStatus)){
                        holder.tv_verify_reason_tag.setVisibility(View.GONE);
                        holder.tv_verify_reason.setVisibility(View.GONE);
                        holder.tv_verify_reason.setText("");
                    }else {
                        holder.tv_verify_reason_tag.setVisibility(View.VISIBLE);
                        holder.tv_verify_reason.setVisibility(View.VISIBLE);
                        holder.tv_verify_reason.setText(DataUtil.isDataElementNull(data.get(position).get("Summary")));
                    }
                }else {
                    holder.tv_verify_person_tag.setVisibility(View.GONE);
                    holder.tv_verify_person.setVisibility(View.GONE);
                    holder.tv_verify_reason_tag.setVisibility(View.GONE);
                    holder.tv_verify_reason.setVisibility(View.GONE);
                    holder.tv_verify_person.setText("");
                }

                if (DataUtil.isDataElementNull(data.get(position).get("ModuleType")).equals("Property")) {
                    //名称
                    ((TextView)convertView.findViewById(R.id.Task_Equipment_tag)).setText(LocaleUtils.getI18nValue("facility_Name_1"));
                    //编号
                    ((TextView)convertView.findViewById(R.id.Task_Equipment_Num_tag)).setText(LocaleUtils.getI18nValue("facility_Num"));
                }
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // if(getLoginInfo().isMaintenMan()){
                    Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                    intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
                    intent.putExtra(Task.TASK_ID,DataUtil.isDataElementNull(data.get(position-1).get(Task.TASK_ID)));
                    intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(data.get(position-1).get(Task.TASK_CLASS)));
                    intent.putExtra("TaskStatus",data.get(position-1).get("Status").valueAsInt());
                    intent.putExtra("IsEvaluated","0");
                    intent.putExtra("FromFragment","0");
                    intent.putExtra("isTaskHistory",true);
                ((Activity)mContext).startActivityForResult(intent, Constants.REQUEST_CODE_TASKHISTORY);
                    //startActivity(intent);
            }
//                else {
//                    Intent intent=new Intent(mContext,CommandActivity.class);
//                    intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
//                    startActivity(intent);
//                }
//           }
        });
        getTaskHistory();
        return v;
    }
    public void doRefresh(){
        pageIndex=1;
        getTaskHistory();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public static PendingCommandFragment newInstance(HashMap<String,String> TaskClass,HashMap<String,String> TaskStatus){
        PendingCommandFragment fragment = new PendingCommandFragment();
        Bundle bundle = new Bundle();
        //bundle.putString(Task.TASK_CLASS, TaskClass);
        map=TaskClass;
        taskStatusMap=TaskStatus;
        LogUtils.e("待评价获取成功--taskStatusMap--->"+taskStatusMap);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initMap(){
//        map.put(Task.REPAIR_TASK,LocaleUtils.getI18nValue("repair"));
//        map.put(Task.MAINTAIN_TASK,LocaleUtils.getI18nValue("maintenance"));
//        map.put(Task.MOVE_CAR_TASK,LocaleUtils.getI18nValue("move_car"));
//        map.put(Task.OTHER_TASK,LocaleUtils.getI18nValue("other"));
//
//        taskStatusMap.put(LocaleUtils.getI18nValue("waitingDeal"),0);
//        taskStatusMap.put(LocaleUtils.getI18nValue("start"),1);
//        taskStatusMap.put(LocaleUtils.getI18nValue("linked_order"),2);
//        taskStatusMap.put(LocaleUtils.getI18nValue("cancel"),3);
//        taskStatusMap.put(LocaleUtils.getI18nValue("verity"),4);
//        taskStatusMap.put(LocaleUtils.getI18nValue("MonthlyStatement"),5);
    }
    public void getTaskHistory(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData"),mContext);
                return;
            }}
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set("pageSize",PAGE_SIZE);
        jsonObjectElement.set("pageIndex",pageIndex);
        //jsonObjectElement.set("Status",2);
        jsonObjectElement.set("IsEvaluated",0);
        jsonObjectElement.set("IsQueryOnlyMyself",iSQueryOnlyMyselfBill);
//        if(!timeCode.equals("")){
//            jsonObjectElement.set("DateLength",timeCode);
//        }
        jsonObjectElement.set("Window","1");
        params.putJsonParams(jsonObjectElement.toJson());
        LogUtils.e("待评价订单请求的数据---->"+jsonObjectElement.toString());
        HttpUtils.post(mContext, "TaskAPI/GetTaskHistoryList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    if (pageIndex == 1) {
                        data.clear();
                    }
                    LogUtils.e("待评价获取成功--->"+t);
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").isArray()&&jsonObjectElement.get("PageData").isArray()&&jsonObjectElement.get("PageData").asArrayElement().size()>0){
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        pageIndex++;
                        for(DataElement dataElement:jsonObjectElement.get("PageData").asArrayElement()){
                            if (dataElement.asObjectElement().get("Status").valueAsInt()!=0&&dataElement.asObjectElement().get("Status").valueAsInt()!=1){
                                data.add(dataElement.asObjectElement());
                            }


                        }
                    }else{
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("noCommandData"),mContext);
                    }
                    taskAdapter.notifyDataSetChanged();
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

    public void setData(String TaskClass, String TaskStatus, String Time, String checkStatus,boolean iSQueryOnlyMyselfBill) {
        pageIndex = 1;
//        this.taskClassCode = TaskClass;
//        this.taskStatusCode = TaskStatus;
        this.iSQueryOnlyMyselfBill = iSQueryOnlyMyselfBill;
        this.timeCode = Time;
//        this.checkStatusCode = checkStatus;
    }
}
