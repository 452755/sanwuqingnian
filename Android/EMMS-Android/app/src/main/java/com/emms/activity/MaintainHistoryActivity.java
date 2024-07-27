package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

public class MaintainHistoryActivity extends BaseActivity {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    Context context;
    private ArrayList<ObjectElement> datas=new ArrayList<>();
    TextView tv_title;
    ImageView btn_right_action;

    private Handler handler=new Handler();

    String EquipmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_history);
        context = this;
        Intent intent = getIntent();
        EquipmentID = intent.getStringExtra("Equipment_ID");
        initView();
        getMaintainHistoryByServer();
    }

    public void initView(){
        listView = (PullToRefreshListView) findViewById(R.id.maintain_history_list);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_right_action = (ImageView) findViewById(R.id.btn_right_action);

        tv_title.setText(LocaleUtils.getI18nValue("maintain_history"));

        btn_right_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMaintainHistoryByServer();
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                refreshView.onRefreshComplete();
            }
        });

        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_machine_history, parent, false);
                    ((TextView)convertView.findViewById(R.id.Warranty_person_tag)).setText(LocaleUtils.getI18nValue("warranty_person"));
                    ((TextView)convertView.findViewById(R.id.id_Warranty_time_description)).setText(LocaleUtils.getI18nValue("repair_time"));
                    ((TextView)convertView.findViewById(R.id.id_start_time_description)).setText(LocaleUtils.getI18nValue("start_time"));
                    ((TextView)convertView.findViewById(R.id.id_end_time_description)).setText(LocaleUtils.getI18nValue("end_time"));
                    ((TextView)convertView.findViewById(R.id.id_task_description)).setText(LocaleUtils.getI18nValue("task_descripbe"));
                    //作用:新增  Jason 2019/9/30 上午10:50
                    ((TextView)convertView.findViewById(R.id.tv_task_class_tag)).setText(LocaleUtils.getI18nValue("task_t"));
                    ((TextView)convertView.findViewById(R.id.tv_brokendown_type)).setText(LocaleUtils.getI18nValue("fault_type"));
                    ((TextView)convertView.findViewById(R.id.tv_maintain_desc_tag)).setText(LocaleUtils.getI18nValue("fault_description"));


                    holder = new TaskViewHolder();
                    //                holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time= (TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_creater=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_start_time_process=(TextView)convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_maintain_desc = (TextView) convertView.findViewById(R.id.tv_maintain_desc);

                    //作用: 新增 Jason 2019/9/30 上午10:52
                    holder.tv_task_class = (TextView) convertView.findViewById(R.id.tv_task_class);
                    holder.tv_brokendown_type_text = (TextView) convertView.findViewById(R.id.tv_brokendown_type_text);

                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_creater.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));

                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                // holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_STATUS)));
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));

                holder.tv_start_time.setText(DataUtil.isDataElementNull(datas.get(position).get("TargetTeam")));

                //作用:新增  Jason 2019/9/30 上午10:54
                holder.tv_task_class.setText(DataUtil.isDataElementNull(datas.get(position).get("TaskClass")));
                holder.tv_start_time_process.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("StartTime"))));
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("FinishTime"))));
                holder.tv_brokendown_type_text.setText(DataUtil.isDataElementNull(datas.get(position).get("TroubleSort")));
                holder.tv_maintain_desc.setText(DataUtil.isDataElementNull(datas.get(position).get("MaintainDesc")));
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // if(getLoginInfo().isMaintenMan()){
                Intent intent=new Intent(context,TaskDetailsActivity.class);
                intent.putExtra("TaskDetail",datas.get(position-1).asObjectElement().toString());
                intent.putExtra(Task.TASK_ID,DataUtil.isDataElementNull(datas.get(position-1).get(Task.TASK_ID)));
                intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(datas.get(position-1).get(Task.TASK_CLASS)));
                intent.putExtra("TaskStatus",0);
                intent.putExtra("IsEvaluated","0");
                intent.putExtra("FromFragment","0");
                intent.putExtra("isTaskHistory",true);
                startActivityForResult(intent, Constants.REQUEST_CODE_TASKHISTORY);
                //startActivity(intent);
            }
//                else {
//                    Intent intent=new Intent(mContext,CommandActivity.class);
//                    intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
//                    startActivity(intent);
//                }
//           }
        });
    }

    /**
    * 说明：获取维修历史
    * 添加时间：2019/9/27 下午5:45
    * 作者：Jason
    */
    private void getMaintainHistoryByServer(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("Equipment_ID",EquipmentID);
        HttpUtils.get(context, "Equipment/GetMaintainHistory", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t==null||TextUtils.isEmpty(t)){
                    LogUtils.e("获取数据为空");
                    return;
                }
                datas.clear();
                LogUtils.e("获取到维修历史数据----->"+t);
                JsonObjectElement jsonObjectElement = new JsonObjectElement(t);

                if(jsonObjectElement.get("PageData")!=null&& jsonObjectElement.get("PageData").isArray()
                        &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {
                    //作用:填充数据  Jason 2019/9/29 上午10:19
                    for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                        datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                    }

                }else{
                    //作用:如果是空的话就显示没数据  Jason 2019/9/29 上午10:18
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                }
                taskAdapter.notifyDataSetChanged();
                dismissCustomDialog();

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                LogUtils.e("获取历史数据失败---->"+errorNo+"---->"+strMsg);
            }
        });
    }
}
