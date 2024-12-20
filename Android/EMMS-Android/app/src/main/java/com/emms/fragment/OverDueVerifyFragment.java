package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/20.
 *
 */
public class OverDueVerifyFragment extends BaseFragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas=new ArrayList<>();
    private ArrayList<ObjectElement> submitData=new ArrayList<>();
    private Context mContext;
    private Handler handler=new Handler();
    private String TaskClass="";
    private int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private static HashMap<String,String> taskClass_map=new HashMap<>();
    private static HashMap<String,String> taskStatusMap=new HashMap<>();
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
//        taskStatusMap.put("0",getResources().getString(LocaleUtils.getI18nValue("waitingDeal));
//        taskStatusMap.put("1",getResources().getString(LocaleUtils.getI18nValue("start));
//        taskStatusMap.put("2",getResources().getString(LocaleUtils.getI18nValue("NotVerify));
//        taskStatusMap.put("3",getResources().getString(LocaleUtils.getI18nValue("cancel));
//        taskStatusMap.put("4",getResources().getString(LocaleUtils.getI18nValue("isVerity));
//        taskStatusMap.put("5",getResources().getString(LocaleUtils.getI18nValue("MonthlyStatement));
//        taskClass_map.put(Task.REPAIR_TASK,getResources().getString(LocaleUtils.getI18nValue("repair_task));
//        taskClass_map.put(Task.MAINTAIN_TASK,getResources().getString(LocaleUtils.getI18nValue("maintain_task));
//        taskClass_map.put(Task.MOVE_CAR_TASK,getResources().getString(LocaleUtils.getI18nValue("move_car_task));
//        taskClass_map.put(Task.OTHER_TASK,getResources().getString(LocaleUtils.getI18nValue("other_task));
        TaskClass=this.getArguments().getString(Task.TASK_CLASS);
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
                        getCommandListFromServer();
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
                        getCommandListFromServer();
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activity_workload_verify_overdue, parent, false);
                    ((TextView)convertView.findViewById(R.id.id_group)).setText(LocaleUtils.getI18nValue("grouptype"));
                    ((TextView)convertView.findViewById(R.id.target_group_tag)).setText(LocaleUtils.getI18nValue("target_group_tag"));
                    ((TextView)convertView.findViewById(R.id.Warranty_person_tag)).setText(LocaleUtils.getI18nValue("warranty_person"));
                    ((TextView)convertView.findViewById(R.id.id_Warranty_time_description)).setText(LocaleUtils.getI18nValue("repair_time"));
                    ((TextView)convertView.findViewById(R.id.id_start_time_description)).setText(LocaleUtils.getI18nValue("start_time"));
                    ((TextView)convertView.findViewById(R.id.id_end_time_description)).setText(LocaleUtils.getI18nValue("end_time"));
                    ((TextView)convertView.findViewById(R.id.id_task_description)).setText(LocaleUtils.getI18nValue("task_descripbe"));
                    ((TextView)convertView.findViewById(R.id.standard_workload_tag)).setText(LocaleUtils.getI18nValue("standardWorkTime"));
                    ((TextView)convertView.findViewById(R.id.verifyWorkTime_tag)).setText(LocaleUtils.getI18nValue("verifyWorkTime"));
                    holder = new TaskViewHolder();
                    //显示6个内容，组别，报修人，状态，保修时间,开始时间，任务描述
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.standard_workload);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_end_time=(TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_device_name=(TextView) convertView.findViewById(R.id.verifyWorkTime);
                    holder.tv_create_time=(TextView)convertView.findViewById(R.id.status);
                    holder.tv_target_group=(TextView)convertView.findViewById(R.id.target_group);
                    holder.tv_device_num=(TextView)convertView.findViewById(R.id.target_group_tag);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                if(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_CLASS)).equals(Task.MOVE_CAR_TASK)){
                    holder.tv_target_group.setVisibility(View.VISIBLE);
                    holder.tv_device_num.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_target_group.setVisibility(View.GONE);
                    holder.tv_device_num.setVisibility(View.GONE);
                }
                //待修改
                holder.tv_target_group.setText(DataUtil.isDataElementNull(datas.get(position).get("TargetTeam")));
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkTime")));
                holder.tv_device_name.setText(DataUtil.isDataElementNull(datas.get(position).get("Workload")));
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("FinishTime"))));
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.START_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                if(taskStatusMap.get(DataUtil.isDataElementNull(datas.get(position).get("Status")))!=null) {
                    holder.tv_create_time.setText(taskStatusMap.get(DataUtil.isDataElementNull(datas.get(position).get("Status"))));
                }else {
                    holder.tv_create_time.setText(DataUtil.isDataElementNull(datas.get(position).get("Status")));
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                        intent.putExtra(Task.TASK_ID,DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)));
                        intent.putExtra("TaskDetail",datas.get(position).toString());
                        intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_CLASS)));
                        intent.putExtra("TaskStatus",2);
                        startActivity(intent);
                    }
                });
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);

        //getCommandListFromServer();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getCommandListFromServer(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData"),mContext);
                return;
            }}
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
       // params.put("task_class",Task.REPAIR_TASK);

        JsonObjectElement data=new JsonObjectElement();
        if(Verity==4){
            data.set("dateLength",filterTime);
        }else {
            data.set("Verity",Verity);
        }
        data.set("pageSize",PAGE_SIZE);
        data.set("pageIndex",pageIndex);
        params.putJsonParams(data.toJson());
        HttpUtils.post(mContext, "TaskAPI/GetTaskWorkloadVerityList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    // int RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    //  if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()==0){
                    //提示没有处理中的任务
                    //  }
                    if (pageIndex == 1) {
                        datas.clear();
                    }
                    RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                    if(jsonObjectElement.get("PageData")!=null
                            &&jsonObjectElement.get("PageData").isArray()
                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {
                        pageIndex++;
                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                            datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                    }else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),mContext);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapter.setDatas(datas);
                            taskAdapter.notifyDataSetChanged();
                        }
                    });
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
               ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetList"),mContext);
                dismissCustomDialog();
            }
        });
    }
    public static OverDueVerifyFragment newInstance(HashMap<String,String> TaskClass,HashMap<String,String> TaskStatus){
        OverDueVerifyFragment fragment = new OverDueVerifyFragment();
        Bundle bundle = new Bundle();
        //bundle.putString(Task.TASK_CLASS, TaskClass);
        taskClass_map=TaskClass;
        taskStatusMap=TaskStatus;
        fragment.setArguments(bundle);
        return fragment;
    }
    private int Verity=2,filterTime=30;
    public void doRefresh(int Verity,int filterTime){
        pageIndex=1;
        this.Verity=Verity;
        this.filterTime=filterTime;
        getCommandListFromServer();
    }
}
