package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
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
public class LinkedVerifyFragment extends BaseFragment {

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
        TaskClass=this.getArguments().getString(Task.TASK_CLASS);
        mContext =getActivity();

        View v = inflater.inflate(R.layout.fr_processing, null);
        initView(v);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent(mContext,TaskDetailsActivity.class);
//                intent.putExtra(Task.TASK_ID,datas.get(position-1).get(Task.TASK_ID).valueAsString());
//                intent.putExtra("TaskDetail",datas.get(position-1).toString());
//                intent.putExtra(Task.TASK_CLASS,TaskClass);
//                intent.putExtra("TaskStatus",1);
//                startActivity(intent);
//            }
//        });
        getCommandListFromServer();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void initView(View v){
        listView=(PullToRefreshListView)v.findViewById(R.id.processing_list);
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
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activity_workload_verify, parent, false);
                    ((TextView)convertView.findViewById(R.id.id_group)).setText(LocaleUtils.getI18nValue("grouptype"));
                    ((TextView)convertView.findViewById(R.id.target_group_tag)).setText(LocaleUtils.getI18nValue("target_group_tag"));
                    ((TextView)convertView.findViewById(R.id.Warranty_person_tag)).setText(LocaleUtils.getI18nValue("warranty_person"));
                    ((TextView)convertView.findViewById(R.id.id_Warranty_time_description)).setText(LocaleUtils.getI18nValue("repair_time"));
                    ((TextView)convertView.findViewById(R.id.id_start_time_description)).setText(LocaleUtils.getI18nValue("start_time"));
                    ((TextView)convertView.findViewById(R.id.id_end_time_description)).setText(LocaleUtils.getI18nValue("end_time"));
                    ((TextView)convertView.findViewById(R.id.id_task_description)).setText(LocaleUtils.getI18nValue("task_descripbe"));
                    ((TextView)convertView.findViewById(R.id.standard_workload_tag)).setText(LocaleUtils.getI18nValue("standardWorkTime"));
                    ((TextView)convertView.findViewById(R.id.tvVerifyWorkTime)).setText(LocaleUtils.getI18nValue("verifyWorkTime"));
                    ((TextView)convertView.findViewById(R.id.tvVerifyWorkTimeRemark)).setText(LocaleUtils.getI18nValue("verifyWorkTimeRemark"));
                    holder = new TaskViewHolder();
                    //显示6个内容，组别，报修人，状态，保修时间,开始时间，任务描述
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.standard_workload);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_end_time=(TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.editText=(EditText)convertView.findViewById(R.id.verify_workTime) ;
                    holder.editText2=(EditText)convertView.findViewById(R.id.verify_workTime_remark) ;
                    holder.editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                    holder.editText.setText(DataUtil.isDataElementNull(datas.get(position).get("Workload")));
                    holder.editText2.setText(DataUtil.isDataElementNull(datas.get(position).get("UpdateRemark")));
                    holder.textChanged1=new TaskAdapter.EtTextChanged(position,"Workload");
                    holder.textChanged2=new TaskAdapter.EtTextChanged(position,"UpdateRemark");
                    holder.editText.addTextChangedListener(holder.textChanged1);
                    holder.editText2.addTextChangedListener(holder.textChanged2);
                    holder.image=(ImageView)convertView.findViewById(R.id.image) ;
                    holder.tv_create_time=(TextView)convertView.findViewById(R.id.status);
                    holder.onClickListener=new ExOnClickListener(position,"tag");
                    holder.image.setOnClickListener(holder.onClickListener);
                    holder.tv_target_group=(TextView)convertView.findViewById(R.id.target_group);
                    holder.tv_device_num=(TextView)convertView.findViewById(R.id.target_group_tag);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //待修改
                if(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_CLASS)).equals(Task.MOVE_CAR_TASK)){
                    holder.tv_target_group.setVisibility(View.VISIBLE);
                    holder.tv_device_num.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_target_group.setVisibility(View.GONE);
                    holder.tv_device_num.setVisibility(View.GONE);
                }
                holder.tv_target_group.setText(DataUtil.isDataElementNull(datas.get(position).get("TargetTeam")));
                holder.textChanged1.setPosition(position);
                holder.textChanged2.setPosition(position);
                holder.onClickListener.setPosition(position);
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkTime")));
                holder.editText.setText(DataUtil.isDataElementNull(datas.get(position).get("Workload")));
                holder.editText2.setText(DataUtil.isDataElementNull(datas.get(position).get("UpdateRemark")));
                if(taskStatusMap.get(DataUtil.isDataElementNull(datas.get(position).get("Status")))!=null) {
                    holder.tv_create_time.setText(taskStatusMap.get(DataUtil.isDataElementNull(datas.get(position).get("Status"))));
                }else {
                    holder.tv_create_time.setText(DataUtil.isDataElementNull(datas.get(position).get("Status")));
                }
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("FinishTime"))));
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.START_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                if (datas.get(position).get("tag").valueAsBoolean()) {
                    holder.image.setImageResource(R.mipmap.select_pressed);
                } else {
                    holder.image.setImageResource(R.mipmap.select_normal);
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
//                holder.editText.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        datas.get(position).set("Workload",s.toString());
//                    }
//                });
//                holder.editText2.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        datas.get(position).set("UpdateRemark",s.toString());
//                    }
//                });
//                holder.image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(datas.get(position).get("tag").valueAsBoolean()){
//                            // holder.image.setImageResource(R.mipmap.select_pressed);
//                            datas.get(position).set("tag",false);
//                        }else {
//                            //holder.image.setImageResource(R.mipmap.select_normal);
//                            datas.get(position).set("tag",true);
//                        }
//                        notifyDataSetChanged();
//                        //submitWorkload(datas.get(position),holder.editText.getText().toString());
//                        if(submitData.contains(datas.get(position))){
//                            submitData.remove(datas.get(position));
//                        }else {
//                            submitData.add(datas.get(position));
//                        }
//                    }
//                });
                return convertView;
            }
        };
        taskAdapter.setExArray(submitData);
        listView.setAdapter(taskAdapter);
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
//        params.put("pageSize",PAGE_SIZE);
//        params.put("pageIndex",pageIndex);
//        params.put("Verity",1);//1为已核验
        JsonObjectElement data=new JsonObjectElement();
        data.set("pageSize",PAGE_SIZE);
        data.set("pageIndex",pageIndex);
        data.set("Verity",1);//0为未核验
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
                        submitData.clear();
                    }
                    if(jsonObjectElement.get("PageData")!=null
                            &&jsonObjectElement.get("PageData").isArray()
                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        pageIndex++;
                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                            jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().set("tag",false);
                            datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                taskAdapter.setDatas(datas);
                                taskAdapter.notifyDataSetChanged();
                            }
                        });
                        //      setData(datas);
                    }

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
    public static LinkedVerifyFragment newInstance(HashMap<String,String> TaskClass,HashMap<String,String> TaskStatus){
        LinkedVerifyFragment fragment = new LinkedVerifyFragment();
        Bundle bundle = new Bundle();
        //bundle.putString(Task.TASK_CLASS, TaskClass);
        taskClass_map=TaskClass;
        taskStatusMap=TaskStatus;
        fragment.setArguments(bundle);
        return fragment;
    }
    public void doRefresh(){
        pageIndex=1;

        getCommandListFromServer();
    }


    private void submitWorkload(ObjectElement data,String workload){
        if(!DataUtil.isNum(workload)||workload.equals("")){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseInputNum"),mContext);
            return;
        }
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params=new HttpParams();
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set(Task.TASK_ID,DataUtil.isDataElementNull(data.get(Task.TASK_ID)));
        jsonObjectElement.set("Workload",workload);
        ArrayList<ObjectElement> list=new ArrayList<>();
        list.add(jsonObjectElement);
        JsonArrayElement jsonArrayElement=new JsonArrayElement(list.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        HttpUtils.post(mContext, "TaskWorkloadVerity", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessVerify"),mContext);
                    }else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailVerify"),mContext);
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailVerifyCauseByTimeOut"),mContext);
                dismissCustomDialog();
            }
        });
    }
    public void submitVerifyData(){
        if(submitData.size()<=0){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseSelectSubmitData"),mContext);
            return;
        }
        for(int i=0;i<submitData.size();i++) {
            if (!DataUtil.isNum(DataUtil.isDataElementNull(submitData.get(i).get("Workload")).trim()) ||
                    DataUtil.isDataElementNull(submitData.get(i).get("Workload")).equals("")
                    || !DataUtil.isFloat(DataUtil.isDataElementNull(submitData.get(i).get("Workload")).trim())) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseInputNum"), mContext);
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params=new HttpParams();
        ArrayList<ObjectElement> list=new ArrayList<>();
        for(int i=0;i<submitData.size();i++){
            JsonObjectElement jsonObjectElement=new JsonObjectElement();
            jsonObjectElement.set(Task.TASK_ID,DataUtil.isDataElementNull(submitData.get(i).get(Task.TASK_ID)));
            jsonObjectElement.set("Workload",DataUtil.isDataElementNull(submitData.get(i).get("Workload")));
            jsonObjectElement.set("UpdateRemark",DataUtil.isDataElementNull(submitData.get(i).get("UpdateRemark")));
            list.add(jsonObjectElement);
        }
        final JsonArrayElement jsonArrayElement=new JsonArrayElement(list.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        HttpUtils.post(mContext, "TaskAPI/VerityTaskWorkload", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    dismissCustomDialog();
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessVerify"),mContext);
                        //datas.remove(submitData);
                        //taskAdapter.notifyDataSetChanged();
                        pageIndex=1;
                        getCommandListFromServer();
                    }else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailVerify"),mContext);
                    }
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailVerifyCauseByTimeOut"),mContext);
                dismissCustomDialog();
            }
        });
    }
}
