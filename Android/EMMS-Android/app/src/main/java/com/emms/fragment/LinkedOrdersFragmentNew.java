package com.emms.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.emms.activity.StyleChangeTaskDetailsActivity;
//import com.emms.activity.TaskDetailsActivity;
import com.emms.activity.TaskDetailsActivityNew;
import com.emms.adapter.TaskAdapter;
import com.emms.bean.StyleChangeDataBean;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public class LinkedOrdersFragmentNew extends BaseFragment {
    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private Context mContext;
    private ArrayList<ObjectElement> data = new ArrayList<>();
    private Handler handler = new Handler();
    private String TaskClass;
    private String TaskSubClass;
    private String logicType;
    private int PAGE_SIZE = 10;
    private int pageIndex = 1;
    private int RecCount = 0;
    private String equipmentName = "", equipmentNum = "";
    private String qrcode = "", iccard = "";
    private String FactoryId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e("进入结单的界面");
        mContext = getActivity();
        LogUtils.e("进入已接单任务");
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        listView.setMode(PullToRefreshListView.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex = 1;
                        if (TaskClass.equals("T08")) {
                            getStyleChangeOrderFromServer();
                        } else {
                            getCompleteTaskDataFromServer();
                        }
                        listView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                }, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (TaskClass.equals("T08")) {
                            getStyleChangeOrderFromServer();
                        } else {
                            getCompleteTaskDataFromServer();
                        }
                        listView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                }, 0);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TaskClass = this.getArguments().getString(Task.TASK_CLASS);
        TaskSubClass = this.getArguments().getString(Task.TASK_SUBCLASS);
        logicType = this.getArguments().getString(Task.logicType);
        taskAdapter = new TaskAdapter(data) {
            private int dynamicLayout(TaskViewHolder holder, int indix, DataElement de1) {
                if (de1 == null || de1.valueAsString() == null || "".equals(de1.valueAsString())) {//后台空对象为""
                    return indix;
                }
                JsonObject jsonData = new JsonParser().parse(de1.valueAsString()).getAsJsonObject();
                if (!"".equals(jsonData.get("Key").getAsString())) {
                    switch (indix) {
                        case 1:
                            holder.tv_dynomic1_tag.setText(jsonData.get("Key").getAsString() + " : ");
                            holder.tv_dynomic1_description.setText(jsonData.get("Value").getAsString());
                            holder.tv_dynomic1_tag.setVisibility(View.VISIBLE);
                            holder.tv_dynomic1_description.setVisibility(View.VISIBLE);
                            return 2;
                        case 2:
                            holder.tv_dynomic2_tag.setText(jsonData.get("Key").getAsString() + " : ");
                            holder.tv_dynomic2_description.setText(jsonData.get("Value").getAsString());
                            holder.tv_dynomic2_tag.setVisibility(View.VISIBLE);
                            holder.tv_dynomic2_description.setVisibility(View.VISIBLE);
                            return 3;
                        case 3:
                            holder.tv_dynomic3_tag.setText(jsonData.get("Key").getAsString() + " : ");
                            holder.tv_dynomic3_description.setText(jsonData.get("Value").getAsString());
                            holder.tv_dynomic3_tag.setVisibility(View.VISIBLE);
                            holder.tv_dynomic3_description.setVisibility(View.VISIBLE);
                            return 4;
                        case 4:
                            holder.tv_dynomic4_tag.setText(jsonData.get("Key").getAsString() + " : ");
                            holder.tv_dynomic4_description.setText(jsonData.get("Value").getAsString());
                            holder.tv_dynomic4_tag.setVisibility(View.VISIBLE);
                            holder.tv_dynomic4_description.setVisibility(View.VISIBLE);
                            return 5;
                        case 5:
                            holder.tv_dynomic5_tag.setText(jsonData.get("Key").getAsString() + " : ");
                            holder.tv_dynomic5_description.setText(jsonData.get("Value").getAsString());
                            holder.tv_dynomic5_tag.setVisibility(View.VISIBLE);
                            holder.tv_dynomic5_description.setVisibility(View.VISIBLE);
                            return 6;
                    }
                }
                return indix;
            }

            private void dynamicLayout2(TaskViewHolder holder, int indix) {
                switch (indix) {
                    case 1:
                        holder.tv_dynomic1_tag.setVisibility(View.GONE);
                        holder.tv_dynomic1_description.setVisibility(View.GONE);
                        holder.tv_dynomic2_tag.setVisibility(View.GONE);
                        holder.tv_dynomic2_description.setVisibility(View.GONE);
                        holder.tv_dynomic3_tag.setVisibility(View.GONE);
                        holder.tv_dynomic3_description.setVisibility(View.GONE);
                        holder.tv_dynomic4_tag.setVisibility(View.GONE);
                        holder.tv_dynomic4_description.setVisibility(View.GONE);
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        break;
                    case 2:
                        holder.tv_dynomic2_tag.setVisibility(View.GONE);
                        holder.tv_dynomic2_description.setVisibility(View.GONE);
                        holder.tv_dynomic3_tag.setVisibility(View.GONE);
                        holder.tv_dynomic3_description.setVisibility(View.GONE);
                        holder.tv_dynomic4_tag.setVisibility(View.GONE);
                        holder.tv_dynomic4_description.setVisibility(View.GONE);
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        break;
                    case 3:
                        holder.tv_dynomic3_tag.setVisibility(View.GONE);
                        holder.tv_dynomic3_description.setVisibility(View.GONE);
                        holder.tv_dynomic4_tag.setVisibility(View.GONE);
                        holder.tv_dynomic4_description.setVisibility(View.GONE);
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        break;
                    case 4:
                        holder.tv_dynomic4_tag.setVisibility(View.GONE);
                        holder.tv_dynomic4_description.setVisibility(View.GONE);
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        break;
                    case 5:
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        break;
                }
            }

            private void createDynamicCtrl(ObjectElement taskDetail, TaskViewHolder holder) {//动态生成用户需要的控件
                int indix = 1;
                DataElement de1 = taskDetail.get("SetField1");
                indix = dynamicLayout(holder, indix, de1);
                DataElement de2 = taskDetail.get("SetField2");
                indix = dynamicLayout(holder, indix, de2);
                DataElement de3 = taskDetail.get("SetField3");
                indix = dynamicLayout(holder, indix, de3);
                DataElement de4 = taskDetail.get("SetField4");
                indix = dynamicLayout(holder, indix, de4);
                DataElement de5 = taskDetail.get("SetField5");
                indix = dynamicLayout(holder, indix, de5);

                dynamicLayout2(holder, indix);
            }

            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_process, parent, false);
                    ((TextView) convertView.findViewById(R.id.id_group)).setText(LocaleUtils.getI18nValue("grouptype"));
                    ((TextView) convertView.findViewById(R.id.target_group_tag)).setText(LocaleUtils.getI18nValue("target_group_tag"));
                    ((TextView) convertView.findViewById(R.id.Warranty_person_tag)).setText(LocaleUtils.getI18nValue("warranty_person"));
                    ((TextView) convertView.findViewById(R.id.id_Warranty_time_description)).setText(LocaleUtils.getI18nValue("repair_time"));
                    ((TextView) convertView.findViewById(R.id.id_start_time_description)).setText(LocaleUtils.getI18nValue("start_time"));
                    ((TextView) convertView.findViewById(R.id.id_end_time_description)).setText(LocaleUtils.getI18nValue("end_time"));
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_tag)).setText(LocaleUtils.getI18nValue("device_name"));
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_Num_tag)).setText(LocaleUtils.getI18nValue("device_num"));
                    ((TextView) convertView.findViewById(R.id.id_task_description)).setText(LocaleUtils.getI18nValue("task_descripbe"));
                    ((TextView) convertView.findViewById(R.id.dynomicp3_tag)).setText(LocaleUtils.getI18nValue("task_descr"));
                    ((TextView) convertView.findViewById(R.id.dynomicp4_tag)).setText(LocaleUtils.getI18nValue("task_descr"));
                    ((TextView) convertView.findViewById(R.id.dynomicp4_description)).setText(LocaleUtils.getI18nValue("task_descr"));
                    holder = new TaskViewHolder();
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.target_group_tag);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.target_group);
                    if (TaskClass.equals(Task.MOVE_CAR_TASK)) {
                        holder.tv_task_state.setVisibility(View.VISIBLE);
                        holder.tv_device_name.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_task_state.setVisibility(View.GONE);
                        holder.tv_device_name.setVisibility(View.GONE);
                    }
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person = (TextView) convertView.findViewById(R.id.Warranty_person);
                    // holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_repair_time = (TextView) convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.Task_Equipment);
                    holder.tv_device_num = (TextView) convertView.findViewById(R.id.Task_Equipment_Num);
                    //动态新增
                    holder.tv_dynomic1_tag = (TextView) convertView.findViewById(R.id.dynomicp1_tag);
                    holder.tv_dynomic1_description = (TextView) convertView.findViewById(R.id.dynomicp1_description);
                    holder.tv_dynomic2_tag = (TextView) convertView.findViewById(R.id.dynomicp2_tag);
                    holder.tv_dynomic2_description = (TextView) convertView.findViewById(R.id.dynomicp2_description);
                    holder.tv_dynomic3_tag = (TextView) convertView.findViewById(R.id.dynomicp3_tag);
                    holder.tv_dynomic3_description = (TextView) convertView.findViewById(R.id.dynomicp3_description);
                    holder.tv_dynomic4_tag = (TextView) convertView.findViewById(R.id.dynomicp4_tag);
                    holder.tv_dynomic4_description = (TextView) convertView.findViewById(R.id.dynomicp4_description);
                    holder.tv_dynomic5_tag = (TextView) convertView.findViewById(R.id.dynomicp5_tag);
                    holder.tv_dynomic5_description = (TextView) convertView.findViewById(R.id.dynomicp5_description);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                createDynamicCtrl(data.get(position), holder);

                holder.tv_create_time.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentName")));
                holder.tv_device_num.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentAssetsIDList")));
                holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT)));
                // holder.tv_task_state.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_STATUS)));
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.START_TIME))));
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.FINISH_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_DESCRIPTION)));
                holder.tv_device_name.setText(DataUtil.isDataElementNull(data.get(position).get("TargetTeam")));


                ((TextView) convertView.findViewById(R.id.TaskOperatorStatus)).setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.tv_TaskOperatorStatus)).setVisibility(View.GONE);

                if (DataUtil.isDataElementNull(data.get(position).get("ModuleType")).equals("Property")) {
                    //资产归属
                    ((TextView) convertView.findViewById(R.id.id_group)).setText(LocaleUtils.getI18nValue("property_using_dept_1"));
                    //名称
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_tag)).setText(LocaleUtils.getI18nValue("task_facility_type_1"));
                    //编号
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_Num_tag)).setText(LocaleUtils.getI18nValue("facility_Num"));
                }

                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        if (TaskClass.equals("T08")) {
            getStyleChangeOrderFromServer();
        } else {
            getCompleteTaskDataFromServer();
        }
        //data.addAll(datas1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (TaskClass.equals("T08")) {
                    Intent intent = new Intent(mContext, StyleChangeTaskDetailsActivity.class);
                    intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(data.get(position - 1).get(Task.TASK_ID)));
                    intent.putExtra("TaskDetail", data.get(position - 1).toString());
                    intent.putExtra(Task.TASK_CLASS, TaskClass);
                    JsonObject jsonData = new JsonParser().parse(data.get(position - 1).get("SetField3").valueAsString()).getAsJsonObject();
                    if (!"".equals(jsonData.get("Key").getAsString())) {
                        String orderno = jsonData.get("Value").getAsString();
                        orderno = orderno.split("/")[0];
                        intent.putExtra("OrderNo", orderno);
                    }
                    intent.putExtra("SewingLine", data.get(position - 1).get("TaskApplicantOrg").valueAsString());
                    intent.putExtra(Task.Receiver, DataUtil.isDataElementNull(data.get(position - 1).get(Task.Receiver)));//kingzhang add 20210415
                    intent.putExtra(Task.TASK_CLASS, TaskClass);
                    intent.putExtra("FromProcessingFragment", "0");
                    intent.putExtra("TaskStatus", 1);
                    if (TaskSubClass != null && !TaskSubClass.equals("")) {
                        intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                    }
                    //startActivity(intent);
                    ((Activity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL);
                } else {
                    Intent intent = new Intent(mContext, TaskDetailsActivityNew.class);
                    intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(data.get(position - 1).get(Task.TASK_ID)));
                    intent.putExtra("TaskDetail", data.get(position - 1).asObjectElement().toString());
                    intent.putExtra("TaskStatus", 2);
                    intent.putExtra(Task.TASK_CLASS, TaskClass);
                    startActivity(intent);
                }
            }
        });
    }

    private void getCompleteTaskDataFromServer() {
        if (RecCount != 0) {
            if ((pageIndex - 1) * PAGE_SIZE >= RecCount) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData"), mContext);
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        //  String s=SharedPreferenceManager.getLoginData(mContext);
        // JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
        // String operator_id=jsonObjectElement.get("Operator_ID").valueAsString();
        // params.put("operator_id",operator_id);

//        params.put("status",2);
//        params.put("taskClass",TaskClass);
//        if(TaskSubClass!=null&&!TaskSubClass.equals("")){
//            params.put("taskSubClass",TaskSubClass);
//        }
//        params.put("pageSize",PAGE_SIZE);
//        params.put("pageIndex",pageIndex);

        JsonObjectElement da = new JsonObjectElement();
        da.set("status", 2);
        da.set("taskClass", TaskClass);
        if (TaskSubClass != null && !TaskSubClass.equals("")) {
            da.set("taskSubClass", TaskSubClass);
            if (TaskSubClass.equals(Task.UPKEEP)) {
                da.set("logicType", logicType == null ? "Old" : logicType);
            }
        }

        if (!DataUtil.isNullOrEmpty(equipmentName)) {
            da.set("equipmentName", equipmentName);
        }
        if (!DataUtil.isNullOrEmpty(equipmentNum)) {
            da.set("equipmentNum", equipmentNum);
        }
        if (!DataUtil.isNullOrEmpty(qrcode)) {
            da.set("Equipment_KyID", qrcode);
        }
        if (!DataUtil.isNullOrEmpty(iccard)) {
            da.set("ICCardID", iccard);
        }
        da.set("pageSize", PAGE_SIZE);
        da.set("pageIndex", pageIndex);
        params.putJsonParams(da.toJson());
        LogUtils.e("请求的参数---->" + da.toString());
        HttpUtils.post(mContext, "TaskAPI/GetTaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    LogUtils.e("已结单任务---->" + t);
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                    //作用:预防有重复数据  Jason 2019/11/29 下午2:20
                    if (data.size() == RecCount) {
                        data.clear();
                    }
                    if (pageIndex == 1) {
                        data.clear();
                    }
                    if (jsonObjectElement.get("PageData") != null
                            && jsonObjectElement.get("PageData").isArray() && jsonObjectElement.get("PageData").asArrayElement().size() > 0) {
                        pageIndex++;
                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                            data.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapter.setDatas(data);
                            taskAdapter.notifyDataSetChanged();
                        }
                    });
                }
                if (isAdded()) {
                    try {
                        dismissCustomDialog();
                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                    }

                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
                try {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetTaskListCauseByTimeOut") + errorNo, mContext);
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
                dismissCustomDialog();
            }
        });
    }

    public static Fragment newInstance(String TaskClass, String TaskSubClass) {
        LinkedOrdersFragmentNew fragment = new LinkedOrdersFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        if (TaskSubClass != null && !TaskSubClass.equals("")) {
            bundle.putString(Task.TASK_SUBCLASS, TaskSubClass);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(String TaskClass, String TaskSubClass, String logicType) {
        LinkedOrdersFragmentNew fragment = new LinkedOrdersFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        if (TaskSubClass != null && !TaskSubClass.equals("")) {
            bundle.putString(Task.TASK_SUBCLASS, TaskSubClass);
        }
        bundle.putString(Task.logicType, logicType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setSearchCondition(String equipmentNum, String equipmentName, String qrcode, String iccard) {
        this.equipmentName = equipmentName;
        this.equipmentNum = equipmentNum;
        this.qrcode = qrcode;
        this.iccard = iccard;
    }

    public void doRefresh() {
        pageIndex = 1;
        if (TaskClass.equals("T08")) {
            getStyleChangeOrderFromServer();
        } else {
            getCompleteTaskDataFromServer();
        }

    }

    /**
     * 转款给专用的查询接口
     */
    private void getStyleChangeOrderFromServer() {
        if (RecCount != 0) {
            if ((pageIndex - 1) * PAGE_SIZE >= RecCount) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData"), mContext);
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        //   String s=SharedPreferenceManager.getLoginData(mContext);
        //  JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
        //  String operator_id=jsonObjectElement.get("Operator_ID").valueAsString();
        //  params.put("operator_id",operator_id);

//        params.put("status",0);
//        params.put("taskClass",TaskClass);
//        if(TaskSubClass!=null&&!TaskSubClass.equals("")){
//            params.put("taskSubClass",TaskSubClass);
//        }
//        params.put("pageSize",PAGE_SIZE);
//        params.put("pageIndex",pageIndex);


        JSONObject datas = new JSONObject();

        JSONObject condition = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            condition.put("fieldName", "status");
            condition.put("conditionType", "eq");
            condition.put("value", 99);
            array.put(condition);
            datas.put("conditions", array);
            datas.put("size", PAGE_SIZE);
            datas.put("page", pageIndex);
//            data.put("total","");
            params.putJsonParams(datas.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.e("请求转款的参数---->" + data.toString());
        HttpUtils.getChangeFormServer(mContext, "emms/transferTask/getList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtils.e("获取转款单数据---->" + t);

                if (t.trim() != null && !TextUtils.isEmpty(t.trim())) {
                    Gson gson = new Gson();
                    StyleChangeDataBean styleChangeDataBean = gson.fromJson(t, StyleChangeDataBean.class);
                    if (pageIndex == 1) {
                        data.clear();
                    }
                    if (styleChangeDataBean.getCode() == 200) {
                        RecCount = styleChangeDataBean.getTotal();
                        pageIndex++;
                        if (styleChangeDataBean.getData() != null && styleChangeDataBean.getData().size() > 0) {
                            for (StyleChangeDataBean.DataBean bean : styleChangeDataBean.getData()) {
                                JsonObjectElement jsonObjectElement = new JsonObjectElement();
                                jsonObjectElement.set("ApplicantID", bean.getCreateuserno());
                                jsonObjectElement.set("Applicant", bean.getCreateusername());
                                jsonObjectElement.set("TaskApplicantOrg", bean.getOrdertask().getSewingline());
                                jsonObjectElement.set("TaskApplicantOrg_ID", "");
                                jsonObjectElement.set("TargetTeam_ID", "");
                                jsonObjectElement.set("TargetTeam", bean.getOrdertask().getSewingline());
                                jsonObjectElement.set("FromTask_ID", 0);//这个临时写成0
                                jsonObjectElement.set("Task_ID", bean.getId());//这个是需要给的，目前后台说没有给到
                                jsonObjectElement.set("Receiver", bean.getReceiver());//kingzhang
                                if (bean.getStatus() == 1) {
                                    jsonObjectElement.set("Status", "待接单");
                                } else if (bean.getStatus() == 2) {
                                    jsonObjectElement.set("Status", "已接单");
                                } else if (bean.getStatus() == 3) {
                                    jsonObjectElement.set("Status", "处理中");
                                } else if (bean.getStatus() == 99) {
                                    jsonObjectElement.set("Status", "完成");
                                }
                                jsonObjectElement.set("ApplicantTime", bean.getCreatetime() == null ? "" : bean.getCreatetime());
                                jsonObjectElement.set("StartTime", bean.getOrdertask().getStarttime());
                                jsonObjectElement.set("FinishTime", bean.getUpdatetime() == null ? "" : bean.getUpdatetime());
                                jsonObjectElement.set("TaskDescr", "");
                                jsonObjectElement.set("IsExsitTaskEquipment", false);
                                jsonObjectElement.set("EquipmentName", "");
                                jsonObjectElement.set("EquipmentAssetsLDist", "");
                                jsonObjectElement.set("IsMain", true);
                                jsonObjectElement.set("MainOperatorName", bean.getCreateusername());
                                jsonObjectElement.set("MainOperator_ID", bean.getCreateuserno());
                                jsonObjectElement.set("StyleChangeDate", bean.getOrdertask().getStarttime());
                                JsonObjectElement setField1 = new JsonObjectElement();
                                setField1.set("Key", bean.getSetField1().getKey());
                                setField1.set("Value", bean.getSetField1().getValue());

                                JsonObjectElement setField2 = new JsonObjectElement();
                                setField2.set("Key", bean.getSetField2().getKey());
                                setField2.set("Value", bean.getSetField2().getValue());

                                JsonObjectElement setField3 = new JsonObjectElement();
                                setField3.set("Key", bean.getSetField3().getKey());
                                setField3.set("Value", bean.getSetField3().getValue());

                                JsonObjectElement setField4 = new JsonObjectElement();
                                setField4.set("Key", bean.getSetField4().getKey());
                                setField4.set("Value", bean.getSetField4().getValue());

                                JsonObjectElement setField5 = new JsonObjectElement();
                                setField5.set("Key", bean.getSetField5().getKey());
                                setField5.set("Value", bean.getSetField5().getValue());
                                String setfield1 = gson.toJson(bean.getSetField1());
                                String setfield2 = gson.toJson(bean.getSetField2());
                                String setfield3 = gson.toJson(bean.getSetField3());
                                String setfield4 = gson.toJson(bean.getSetField4());
                                String setfield5 = gson.toJson(bean.getSetField5());

                                jsonObjectElement.set("SetField1", setfield1);
                                jsonObjectElement.set("SetField2", setfield2);
                                jsonObjectElement.set("SetField3", setfield3);
                                jsonObjectElement.set("SetField4", setfield4);
                                jsonObjectElement.set("SetField5", setfield5);
                                LogUtils.e("最终转换的json---->" + jsonObjectElement.toString());
                                data.add(jsonObjectElement);
                            }
                            handler.post(new Runnable() {
                                //                        @Override
                                public void run() {
                                    taskAdapter.setDatas(data);
                                    //taskAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                }

//                if(t!=null) {
//                    LogUtils.e("请求的数据成功--->"+t);
//                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
//                    RecCount=jsonObjectElement.get("RecCount").valueAsInt();
//                    if(taskNumInteface!=null){
//                        taskNumInteface.ChangeTaskNumListener(1,RecCount);}
//                    if (pageIndex == 1) {
//                        datas.clear();
//                    }
//                    if(jsonObjectElement.get("PageData")!=null&& jsonObjectElement.get("PageData").isArray()
//                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {
//
//                        pageIndex++;
//                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
//                            if (TaskClass.equals("T01")){
//                                datas.add(0,jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
//                            }else{
//                                datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
//                            }
//
//                        }
//                    }
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            taskAdapter.setDatas(datas);
//                            //taskAdapter.notifyDataSetChanged();
//                        }
//                    });
//                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("获取转款接口失败---->" + errorNo + "----->" + strMsg);
                try {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetTaskListCauseByTimeOut") + errorNo, mContext);
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
                dismissCustomDialog();
            }
        });
    }

    public void setFactoryId(String factoryId) {

        FactoryId = factoryId;

    }

}
