package com.emms.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.StyleChangeTaskDetailsActivity;
import com.emms.activity.TaskDetailsActivity;
import com.emms.activity.TaskDetailsActivityNew;
import com.emms.activity.TaskNumInteface;
import com.emms.adapter.TaskAdapter;
import com.emms.bean.StyleChangeDataBean;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.ui.CancelTaskDialog;
import com.emms.ui.TaskCancelListener;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.LongToDate;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.j256.ormlite.stmt.query.IsNull;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

//import android.app.Dialog;
//import android.util.Log;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public class PendingOrdersFragmentNew extends BaseFragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas = new ArrayList<>();
    private Context mContext;
    private Handler handler = new Handler();
    private String TaskClass;
    private String TaskSubClass;
    private String logicType;
    private int PAGE_SIZE = 10;
    private int pageIndex = 1;
    private int RecCount = 0;
    private int removeNum = 0;
    private String From_Factory = null;
    private String FactoryId = null;
    private String moduleType;//业务模块Property/Machine
    String startTime;//接单开始时间

    private String UserId;

    public String getOperatorID() {
        return OperatorID;
    }

    public void setUserId(String id) {
        UserId = id;
    }

    public void setOperatorID(String operatorID) {
        OperatorID = operatorID;
    }

    private String OperatorID = null;

    private String equipmentName = "", equipmentNum = "";
    private String qrcode = "", iccard = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        listView.setMode(PullToRefreshListView.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex = 1;
                        removeNum = 0;
                        if (TaskClass.equals("T08")) {
                            getStyleChangeOrderFromServer();
                        } else {
                            getPendingOrderTaskDataFromServer();
                        }

                        listView.onRefreshComplete();
                        //   Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (TaskClass.equals("T08")) {
                            getStyleChangeOrderFromServer();
                        } else {
                            getPendingOrderTaskDataFromServer();
                        }
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                }, 0);
            }
        });
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TaskClass = this.getArguments().getString(Task.TASK_CLASS);
        TaskSubClass = this.getArguments().getString(Task.TASK_SUBCLASS);
        logicType = this.getArguments().getString(Task.logicType);
        moduleType = this.getArguments().getString(Task.ModuleType);
        taskAdapter = new TaskAdapter(datas) {
            private int dynamicLayout(TaskViewHolder holder, int indix, DataElement de1) {
                if (de1 == null || de1.valueAsString() == null || "".equals(de1.valueAsString())) {//后台空对象为""
                    return indix;
                }
                JsonObject jsonData = new JsonParser().parse(de1.valueAsString()).getAsJsonObject();
                LogUtils.e("jsonData---->" + jsonData);
                if (!"".equals(jsonData.get("Key").getAsString())) {
                    switch (indix) {
                        case 1:
                            if (!TaskClass.equals(Task.MOVE_CAR_TASK)) {
                                holder.tv_dynomic1_tag.setText(jsonData.get("Key").getAsString() + " : ");
                                holder.tv_dynomic1_description.setText(jsonData.get("Value").getAsString());
                                holder.tv_dynomic1_tag.setVisibility(View.VISIBLE);
                                holder.tv_dynomic1_description.setVisibility(View.VISIBLE);
                            }

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
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        holder.acceptTaskButton.getLayoutParams();
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
                        params.addRule(RelativeLayout.BELOW, R.id.Task_description);
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
                        params.addRule(RelativeLayout.BELOW, R.id.dynomic1_tag);
                        break;
                    case 3:
                        holder.tv_dynomic3_tag.setVisibility(View.GONE);
                        holder.tv_dynomic3_description.setVisibility(View.GONE);
                        holder.tv_dynomic4_tag.setVisibility(View.GONE);
                        holder.tv_dynomic4_description.setVisibility(View.GONE);
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        params.addRule(RelativeLayout.BELOW, R.id.dynomic2_tag);
                    case 4:
                        holder.tv_dynomic4_tag.setVisibility(View.GONE);
                        holder.tv_dynomic4_description.setVisibility(View.GONE);
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        params.addRule(RelativeLayout.BELOW, R.id.dynomic3_tag);
                    case 5:
                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
                        holder.tv_dynomic5_description.setVisibility(View.GONE);
                        params.addRule(RelativeLayout.BELOW, R.id.dynomic4_tag);
                        break;
                    case 6:
                        params.addRule(RelativeLayout.BELOW, R.id.dynomic5_tag);
                        break;
                }
                holder.acceptTaskButton.setLayoutParams(params);
            }

            private void createDynamicCtrl(ObjectElement taskDetail, TaskViewHolder holder) {//动态生成用户需要的控件
                int indix = 1;
                DataElement de1 = taskDetail.get("SetField1");
                LogUtils.e("de1---->" + de1);
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
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_pendingorder, parent, false);
                    ((TextView) convertView.findViewById(R.id.textView6)).setText(LocaleUtils.getI18nValue("applicanter"));
                    ((TextView) convertView.findViewById(R.id.Task_status)).setText(LocaleUtils.getI18nValue("waitingDeal"));
                    ((TextView) convertView.findViewById(R.id.textView)).setText(LocaleUtils.getI18nValue("group_type"));
                    ((TextView) convertView.findViewById(R.id.target_group_tag)).setText(LocaleUtils.getI18nValue("target_group_tag"));
                    ((TextView) convertView.findViewById(R.id.textView7)).setText(LocaleUtils.getI18nValue("create_time"));
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_tag)).setText(LocaleUtils.getI18nValue("device_name"));
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_Num_tag)).setText(LocaleUtils.getI18nValue("device_num"));
                    ((TextView) convertView.findViewById(R.id.Task_description_tag)).setText(LocaleUtils.getI18nValue("task_descr"));

                    holder = new TaskViewHolder();
//                    holder.ll_tag_group = (LinearLayout) convertView.findViewById(R.id.ll_tag_group);
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.tv_creater_order);
                    holder.tv_repair_time = (TextView) convertView.findViewById(R.id.target_group_tag);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.target_group);
                    if (TaskClass.equals(Task.MOVE_CAR_TASK)) {
                        holder.tv_repair_time.setVisibility(View.VISIBLE);
                        holder.tv_start_time.setVisibility(View.VISIBLE);
//                        holder.ll_tag_group.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_repair_time.setVisibility(View.GONE);
                        holder.tv_start_time.setVisibility(View.GONE);
//                        holder.ll_tag_group.setVisibility(View.GONE);
                    }
                    if (TaskSubClass != null) {
                        convertView.findViewById(R.id.textView6).setVisibility(View.GONE);
                        holder.tv_creater.setVisibility(View.GONE);
                    } else {
                        convertView.findViewById(R.id.textView6).setVisibility(View.VISIBLE);
                        holder.tv_creater.setVisibility(View.VISIBLE);
                    }
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group_type);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.Task_description);
                    //holder.tv_task_state = (TextView) convertView.findViewById(R.id.Task_status);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time_order);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.Task_Equipment);
                    holder.tv_device_num = (TextView) convertView.findViewById(R.id.Task_Equipment_Num);
                    holder.acceptTaskButton = (Button) convertView.findViewById(R.id.btn_order);
                    holder.acceptTaskButton.setText(LocaleUtils.getI18nValue("orders"));

                    //动态新增
                    holder.tv_dynomic1_tag = (TextView) convertView.findViewById(R.id.dynomic1_tag);
                    holder.tv_dynomic1_description = (TextView) convertView.findViewById(R.id.dynomic1_description);
                    holder.tv_dynomic2_tag = (TextView) convertView.findViewById(R.id.dynomic2_tag);
                    holder.tv_dynomic2_description = (TextView) convertView.findViewById(R.id.dynomic2_description);
                    holder.tv_dynomic3_tag = (TextView) convertView.findViewById(R.id.dynomic3_tag);
                    holder.tv_dynomic3_description = (TextView) convertView.findViewById(R.id.dynomic3_description);
                    holder.tv_dynomic4_tag = (TextView) convertView.findViewById(R.id.dynomic4_tag);
                    holder.tv_dynomic4_description = (TextView) convertView.findViewById(R.id.dynomic4_description);
                    holder.tv_dynomic5_tag = (TextView) convertView.findViewById(R.id.dynomic5_tag);
                    holder.tv_dynomic5_description = (TextView) convertView.findViewById(R.id.dynomic5_description);

                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                createDynamicCtrl(datas.get(position), holder);

                LogUtils.e("填充数据---->" + datas.toString());

                holder.tv_creater.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                // holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_STATUS)));
                holder.tv_create_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));
                if (datas.get(position).get("IsExsitTaskEquipment").valueAsBoolean()) {
                    holder.tv_device_name.setText(DataUtil.isDataElementNull(datas.get(position).get("EquipmentName")));
                } else {
                    holder.tv_device_name.setText(LocaleUtils.getI18nValue("NoEquipment"));
                }
                holder.tv_device_num.setText(DataUtil.isDataElementNull(datas.get(position).get("EquipmentAssetsIDList")));
                holder.tv_start_time.setText(DataUtil.isDataElementNull(datas.get(position).get("TargetTeam")));
                holder.acceptTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // acceptTask(position);
                        if (TaskClass.equals("T08")) {
                            StyleChangeTaskReceive(position);
                        } else {
                            taskReceive(position);
                        }

                    }
                });

                if (TaskClass.equals(Task.MOVE_CAR_TASK)) {
                    holder.tv_dynomic1_tag.setVisibility(View.GONE);
                }
                if (DataUtil.isDataElementNull(datas.get(position).get("ModuleType")).equals("Property")) {
                    //资产归属
                    ((TextView) convertView.findViewById(R.id.textView)).setText(LocaleUtils.getI18nValue("property_using_dept_1"));
                    //名称
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_tag)).setText(LocaleUtils.getI18nValue("task_facility_type_1"));
                    //数量
                    ((TextView) convertView.findViewById(R.id.Task_Equipment_Num_tag)).setText(LocaleUtils.getI18nValue("facility_Num"));
                }

                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        if (TaskClass.equals("T08")) {
            getStyleChangeOrderFromServer();
//            getPendingOrderTaskDataFromServer();
        } else {
            getPendingOrderTaskDataFromServer();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (TaskClass.equals("T08")) {
                    Intent intent = new Intent(mContext, StyleChangeTaskDetailsActivity.class);
                    intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(datas.get(position - 1).get(Task.TASK_ID)));
                    intent.putExtra("TaskDetail", datas.get(position - 1).toString());
                    intent.putExtra(Task.TASK_CLASS, TaskClass);
                    JsonObject jsonData = new JsonParser().parse(datas.get(position - 1).get("SetField3").valueAsString()).getAsJsonObject();
                    if (!"".equals(jsonData.get("Key").getAsString())) {
                        String orderno = jsonData.get("Value").getAsString();
                        orderno = orderno.split("/")[0];
                        intent.putExtra("OrderNo", orderno);
                    }
                    intent.putExtra("SewingLine", datas.get(position - 1).get("TaskApplicantOrg").valueAsString());
                    intent.putExtra(Task.Receiver, DataUtil.isDataElementNull(datas.get(position - 1).get(Task.Receiver)));//kingzhang add 20210415
                    intent.putExtra(Task.TASK_CLASS, TaskClass);
                    intent.putExtra("FromProcessingFragment", "0");
                    intent.putExtra("TaskStatus", 1);
                    if (TaskSubClass != null && !TaskSubClass.equals("")) {
                        intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                    }
                    //startActivity(intent);
                    ((Activity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL);
                } else if (TaskClass.equals("T02")) {
                    Intent intent = new Intent(mContext, TaskDetailsActivityNew.class);
                    intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(datas.get(position - 1).get(Task.TASK_ID)));
                    intent.putExtra("TaskDetail", datas.get(position - 1).asObjectElement().toString());
                    intent.putExtra(Task.TASK_CLASS, TaskClass);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, TaskDetailsActivity.class);
                    intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(datas.get(position - 1).get(Task.TASK_ID)));
                    intent.putExtra("TaskDetail", datas.get(position - 1).asObjectElement().toString());
                    intent.putExtra(Task.TASK_CLASS, TaskClass);
                    startActivity(intent);
                }
            }
        });
        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                try {


                    boolean isShow = true;
                    LogUtils.e("用户权限id---->" + Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)));
                    String userIds = SharedPreferenceManager.getUserRoleIDS(mContext);
                    String[] ids = userIds.split(",");
                    LogUtils.e("用户权限ids----->" + ids.toString());
                    //TODO
                    if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) > 4) {
                        for (int i = 0; i < ids.length; i++) {
                            LogUtils.e("ids[]--->" + ids[i]);
                            //判断一下权限可以取消单
                            if (ids[i].equals("4") || ids[i].equals("1") || ids[i].equals("2")
                                    || ids[i].equals("3")) {
                                isShow = true;
                                break;
                            } else {
                                isShow = false;
                            }
                        }

                    }
                    if (isShow) {
                        CancelTaskDialog cancleTaskDialog = new CancelTaskDialog(mContext);
                        cancleTaskDialog.setDialogTitle(LocaleUtils.getI18nValue("cancleTask"));
                        cancleTaskDialog.setTaskCancelListener(new TaskCancelListener() {
                            @Override
                            public void submitCancel(String CancelReason) {
                                if (TaskClass.equals("T08")) {
                                    StyleChangeCancelTask(datas.get(position - 1), CancelReason);
                                } else {
                                    CancelTask(datas.get(position - 1), CancelReason);
                                }

                            }
                        });
                        cancleTaskDialog.show();
                        return true;
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                    return false;
                }
            }

        });

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


        JSONObject data = new JSONObject();

        JSONObject condition = new JSONObject();

        JSONArray array = new JSONArray();
        try {
            condition.put("fieldName", "status");
            condition.put("conditionType", "eq");
            condition.put("value", 1);

            array.put(condition);
            data.put("conditions", array);
            data.put("size", PAGE_SIZE);
            data.put("page", pageIndex);
//            data.put("total","");
            params.putJsonParams(data.toString());

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
                    if (styleChangeDataBean.getCode() == 200) {

                        if (pageIndex == 1) {
                            datas.clear();
                        }

                        if (styleChangeDataBean.getData() != null && styleChangeDataBean.getData().size() > 0) {
                            RecCount = styleChangeDataBean.getTotal();
                            LogUtils.e("测试ReCount--->" + RecCount);
                            if (taskNumInteface != null) {
                                taskNumInteface.ChangeTaskNumListener(1, RecCount);
                            }
                            pageIndex++;
                            LogUtils.e("ReCount--->" + RecCount);
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
                                LogUtils.e("待接单Receiver---->" + bean.getReceiver().toString());
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
                                datas.add(jsonObjectElement);
                            }
                            handler.post(new Runnable() {
                                //                        @Override
                                public void run() {
                                    taskAdapter.setDatas(datas);
                                    //taskAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            if (datas.size() == 0) {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData"), mContext);
                                if (taskNumInteface != null) {
                                    taskNumInteface.ChangeTaskNumListener(1, 0);
                                }
                            }
                            handler.post(new Runnable() {
                                //                        @Override
                                public void run() {
                                    taskAdapter.setDatas(datas);
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

    private void getPendingOrderTaskDataFromServer() {
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


        JsonObjectElement data = new JsonObjectElement();
        data.set("status", 0);
        data.set("taskClass", TaskClass);
        if (TaskSubClass != null && !TaskSubClass.equals("")) {
            data.set("taskSubClass", TaskSubClass);
            if (TaskSubClass.equals(Task.UPKEEP)) {
                data.set("logicType", logicType == null ? "Old" : logicType);
            }
        }
        if (!DataUtil.isNullOrEmpty(equipmentName)) {
            data.set("equipmentName", equipmentName);
        }
        if (!DataUtil.isNullOrEmpty(equipmentNum)) {
            data.set("equipmentNum", equipmentNum);
        }
        if (!DataUtil.isNullOrEmpty(qrcode)) {
            data.set("Equipment_KyID", qrcode);
        }
        if (!DataUtil.isNullOrEmpty(iccard)) {
            data.set("ICCardID", iccard);
        }
        data.set("pageSize", PAGE_SIZE);
        data.set("pageIndex", pageIndex);
        params.putJsonParams(data.toJson());
        LogUtils.e("请求的参数---->" + data.toString());
        HttpUtils.post(mContext, "TaskAPI/GetTaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtils.e("请求的数据成功king--->" + t);
                if (t != null) {
                    LogUtils.e("请求的数据成功--->" + t);
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                    if (taskNumInteface != null) {
                        taskNumInteface.ChangeTaskNumListener(1, RecCount);
                    }
                    //作用:预防有重复数据  Jason 2019/11/29 下午2:20
                    if (datas.size() == RecCount) {
                        datas.clear();
                    }
                    if (pageIndex == 1) {
                        datas.clear();
                    }
                    if (jsonObjectElement.get("PageData") != null && jsonObjectElement.get("PageData").isArray()
                            && jsonObjectElement.get("PageData").asArrayElement().size() > 0) {

                        pageIndex++;
                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                            datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapter.setDatas(datas);
                            //taskAdapter.notifyDataSetChanged();
                        }
                    });
                }
                dismissCustomDialog();
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

    public void acceptTask(final int position) {
        //HttpParams params=new HttpParams();
        //params.put();
        // taskAdapter.getDatas()
        // datas.get(position).
        JsonObjectElement task = new JsonObjectElement();
        task.set(Task.TASK_ID, DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)));
        //   task.set("Status", 1);
    /*    JsonObjectElement operator=new JsonObjectElement();

        operator.set("Operator_ID", new JsonObjectElement(SharedPreferenceManager.getLoginData(mContext))
                .get("Operator_ID").valueAsString());

        JsonArray jsonArray=new JsonArray();
        JsonObject JsonObject=new JsonObject();
        JsonObject.addProperty("Operator_ID", new JsonObjectElement(SharedPreferenceManager.getLoginData(mContext))
                .get("Operator_ID").valueAsString());
        jsonArray.add(JsonObject);*/

        JsonObjectElement SubData = new JsonObjectElement();
        SubData.set("Task", task);
        // SubData.set("TaskOperator",jsonArray.toString());
        //   SubData.set("isChangeTaskItem","1");
        HttpParams params = new HttpParams();
        params.putJsonParams(SubData.toJson());
        HttpUtils.post(mContext, "TaskCollection", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    if (jsonObjectElement.get("Success").valueAsBoolean()) {
                        //成功，通知用户接单成功
                        Toast toast = Toast.makeText(mContext, "接单成功", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        if (!TextUtils.isEmpty(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")))) {
                            TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                        } else {
                            //失败，通知用户接单失败，单已经被接
                            Toast toast = Toast.makeText(mContext, "该单已被接", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    datas.remove(position);

                    taskAdapter.setDatas(datas);
                    taskAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }


    public void StyleChangeTaskReceive(final int position) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(mContext);
        confirm.setMessage(LocaleUtils.getI18nValue("orders"));
        confirm.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showCustomDialog(LocaleUtils.getI18nValue("submitData"));
                HttpParams params = new HttpParams();
                JsonObjectElement SubData = new JsonObjectElement();
                SubData.set("taskId", datas.get(position).get(Task.TASK_ID));
                SubData.set("status", 2);
                SubData.set("userId", UserId);
                SubData.set("userName", UserId);
                SubData.set("quitReason", "");
                params.putJsonParams(SubData.toJson());
                LogUtils.e("转款接单上传参数---->" + SubData.toString());
                //  params.put("task_id",Integer.valueOf(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID))));
                HttpUtils.getChangeFormServer(mContext, "emms/transferTask/updateTransferTaskStatus", params, new HttpCallback() {
                    @Override
                    public void onSuccess(final String t) {
                        super.onSuccess(t);
                        LogUtils.e("转款接单成功---->" + t);
                        if (t != null) {
                            try {
                                final JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                                if (jsonObjectElement.get("Success").valueAsBoolean()) {
                                    //成功，通知用户接单成功
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessReceiveTask"), mContext);
                                } else {
                                    //失败，通知用户接单失败，单已经被接
                                    TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                    LogUtils.e("接单状态返回信息--->" + DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                    //Tag=0接单成功，1单已被接，2忙状态，3接单数目达到上限
                                    if (jsonObjectElement.get("Tag").valueAsInt() == 2) {
                                        dismissCustomDialog();
                                        return;
                                    } else if (jsonObjectElement.get("Tag").valueAsInt() == 3) {
                                        dismissCustomDialog();
                                        return;
                                    } else if (jsonObjectElement.get("Tag").valueAsInt() == 1) {
                                        dismissCustomDialog();
                                        return;
                                    }
                                }
                                ObjectElement detail = datas.get(position);
                                datas.remove(position);
                                removeNum++;
                                if (taskNumInteface != null) {
                                    taskNumInteface.ChangeTaskNumListener(1, RecCount - removeNum);
                                    taskNumInteface.refreshProcessingFragment();
                                }
                                taskAdapter.setDatas(datas);
                                taskAdapter.notifyDataSetChanged();
                                switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.RECEIVE_TASK_ACTION))) {
                                    case "1": {
                                        if (TaskClass.equals("T08")) {
                                            Intent intent = new Intent(mContext, StyleChangeTaskDetailsActivity.class);
                                            intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(detail.get(Task.TASK_ID)));
                                            intent.putExtra("TaskDetail", detail.toString());
                                            intent.putExtra(Task.TASK_CLASS, TaskClass);
                                            JsonObject jsonData = new JsonParser().parse(detail.get("SetField3").valueAsString()).getAsJsonObject();
                                            if (!"".equals(jsonData.get("Key").getAsString())) {
                                                intent.putExtra("OrderNo", jsonData.get("Value").getAsString());
                                            }
                                            intent.putExtra("SewingLine", detail.get("TaskApplicantOrg").valueAsString());
                                            intent.putExtra(Task.TASK_CLASS, TaskClass);
                                            intent.putExtra("FromProcessingFragment", "1");
                                            intent.putExtra("TaskStatus", 1);
                                            if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                                intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                            }
                                            //startActivity(intent);
                                            ((Activity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL);
                                        }
                                        break;
                                    }
                                    default: {
                                        //do nothing
                                        break;
                                    }
                                }
                                //针对EGM厂进行兼容
                            } catch (Exception e) {
                                CrashReport.postCatchedException(e);
                                ToastUtil.showToastLong(e.getMessage(), mContext);
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
        }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirm.show();

    }


    public void taskReceive(final int position) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(mContext);
        confirm.setMessage(LocaleUtils.getI18nValue("orders"));
        confirm.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                showCustomDialog(LocaleUtils.getI18nValue("submitData"));
                HttpParams params = new HttpParams();
                //  params.put("task_id",Integer.valueOf(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID))));
                HttpUtils.post(mContext, "TaskAPI/TaskRecieve?task_id=" + DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)), params, new HttpCallback() {
                    @Override
                    public void onSuccess(final String t) {
                        super.onSuccess(t);
                        LogUtils.e("接单成功---->" + t);
                        if (t != null) {
                            try {
                                final JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                                if (jsonObjectElement.get("Success").valueAsBoolean()) {
                                    //成功，通知用户接单成功
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessReceiveTask"), mContext);
                                    startTime = jsonObjectElement.get("StartTime").valueAsString();
                                } else {
                                    //失败，通知用户接单失败，单已经被接
                                    TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                    LogUtils.e("接单状态返回信息--->" + DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                    //Tag=0接单成功，1单已被接，2忙状态，3接单数目达到上限
                                    if (jsonObjectElement.get("Tag").valueAsInt() == 2) {
                                        dismissCustomDialog();
                                        return;
                                    } else if (jsonObjectElement.get("Tag").valueAsInt() == 3) {
                                        dismissCustomDialog();
                                        return;
                                    } else if (jsonObjectElement.get("Tag").valueAsInt() == 1) {
                                        dismissCustomDialog();
                                        return;
                                    }
                                }
                                ObjectElement detail = datas.get(position);
                                datas.remove(position);
                                removeNum++;
                                if (taskNumInteface != null) {
                                    taskNumInteface.ChangeTaskNumListener(1, RecCount - removeNum);
                                    taskNumInteface.refreshProcessingFragment();
                                }
                                taskAdapter.setDatas(datas);
                                taskAdapter.notifyDataSetChanged();
//                                LogUtils.e("BaseData---->"+DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.RECEIVE_TASK_ACTION)));
                                switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.RECEIVE_TASK_ACTION))) {
                                    case "1": {
                                        if (TaskClass.equals("T08")) {
                                            Intent intent = new Intent(mContext, StyleChangeTaskDetailsActivity.class);
                                            intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(detail.get(Task.TASK_ID)));
                                            intent.putExtra("TaskDetail", detail.toString());
                                            intent.putExtra(Task.TASK_CLASS, TaskClass);
                                            JsonObject jsonData = new JsonParser().parse(detail.get("SetField3").valueAsString()).getAsJsonObject();
                                            if (!"".equals(jsonData.get("Key").getAsString())) {
                                                intent.putExtra("OrderNo", jsonData.get("Value").getAsString());
                                            }
                                            intent.putExtra("SewingLine", detail.get("TaskApplicantOrg").valueAsString());
                                            intent.putExtra(Task.TASK_CLASS, TaskClass);
                                            intent.putExtra("FromProcessingFragment", "1");
                                            intent.putExtra("TaskStatus", 1);
                                            if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                                intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                            }
                                            //startActivity(intent);
                                            ((Activity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL);
                                        } else if (TaskClass.equals("T02")) {
                                            Intent intent = new Intent(mContext, TaskDetailsActivityNew.class);
                                            intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(detail.get(Task.TASK_ID)));
                                            detail.set("IsMain", true);//接单即为主负责人
                                            LogUtils.e("本地时间--->" + startTime);
                                            LogUtils.e("UTC时间--->" + DataUtil.Local2utc(LongToDate.stringToDate(String.valueOf(new Date().getTime()))));
                                            detail.set("StartTime", startTime);
                                            if (OperatorID != null) {
                                                detail.set("MainOperator_ID", OperatorID);
                                            }
                                            intent.putExtra("TaskDetail", detail.toString());
                                            LogUtils.e("接单任务详情---->" + detail.toString());
                                            intent.putExtra(Task.TASK_CLASS, TaskClass);
                                            intent.putExtra("FromProcessingFragment", "1");
                                            intent.putExtra("TaskStatus", 1);
                                            if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                                intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                            }
                                            ((Activity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL);
                                        } else {
                                            Intent intent = new Intent(mContext, TaskDetailsActivity.class);
                                            intent.putExtra(Task.TASK_ID, DataUtil.isDataElementNull(detail.get(Task.TASK_ID)));
                                            detail.set("IsMain", true);//接单即为主负责人
                                            LogUtils.e("本地时间--->" + startTime);
                                            LogUtils.e("UTC时间--->" + DataUtil.Local2utc(LongToDate.stringToDate(String.valueOf(new Date().getTime()))));
                                            detail.set("StartTime", startTime);
                                            if (OperatorID != null) {
                                                detail.set("MainOperator_ID", OperatorID);
                                            }
                                            intent.putExtra("TaskDetail", detail.toString());
                                            LogUtils.e("接单任务详情---->" + detail.toString());
                                            intent.putExtra(Task.TASK_CLASS, TaskClass);
                                            intent.putExtra("FromProcessingFragment", "1");
                                            intent.putExtra("TaskStatus", 1);
                                            if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                                intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                            }
                                            ((Activity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL);
                                        }
                                        break;
                                    }
                                    default: {
                                        //do nothing
                                        break;
                                    }
                                }
                                //针对EGM厂进行兼容
                            } catch (Exception e) {
                                CrashReport.postCatchedException(e);
                                ToastUtil.showToastLong(e.getMessage(), mContext);
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
        }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirm.show();
    }

    public static PendingOrdersFragmentNew newInstance(String TaskClass, String TaskSubClass) {
        PendingOrdersFragmentNew fragment = new PendingOrdersFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        if (TaskSubClass != null && !TaskSubClass.equals("")) {
            bundle.putString(Task.TASK_SUBCLASS, TaskSubClass);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PendingOrdersFragmentNew newInstance(String TaskClass, String TaskSubClass, String logicType) {
        PendingOrdersFragmentNew fragment = new PendingOrdersFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        if (TaskSubClass != null && !TaskSubClass.equals("")) {
            bundle.putString(Task.TASK_SUBCLASS, TaskSubClass);
        }
        bundle.putString(Task.logicType, logicType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setTaskNumInteface(TaskNumInteface taskNumInteface) {
        this.taskNumInteface = taskNumInteface;
    }

    private TaskNumInteface taskNumInteface;

    public void setSearchCondition(String equipmentNum, String equipmentName, String qrcode, String iccard) {
        this.equipmentName = equipmentName;
        this.equipmentNum = equipmentNum;
        this.qrcode = qrcode;
        this.iccard = iccard;
    }

    public void doRefresh() {
        removeNum = 0;
        pageIndex = 1;
        if (TaskClass.equals("T08")) {
            getStyleChangeOrderFromServer();
        } else {
            getPendingOrderTaskDataFromServer();
        }

    }

    /**
     * 转款专用的取消任务接口
     *
     * @param task
     * @param reason
     */
    private void StyleChangeCancelTask(ObjectElement task, final String reason) {

        LogUtils.e("转款专门的取消任务接口---->" + DataUtil.isDataElementNull(task.get(Task.TASK_ID)));
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set("taskId", DataUtil.isDataElementNull(task.get(Task.TASK_ID)));
        submitData.set("status", 0);
        submitData.set("quitReason", reason);
        params.putJsonParams(submitData.toJson());
        LogUtils.e("转款取消任务上传参数---->" + submitData.toString());
        HttpUtils.getChangeFormServer(mContext, "emms/transferTask/updateTransferTaskStatus", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("转款-取消任务的返回失败参数--->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTaskCauseByNetWork"), mContext);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtils.e("取消任务的返回参数--->" + t);
                if (t != null) {
                    JsonObjectElement returnData = new JsonObjectElement(t);
                    if (returnData.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessCancelTask"), mContext);
                        pageIndex = 1;
//                        removeNum=0;
                        if (TaskClass.equals("T08")) {
                            getStyleChangeOrderFromServer();
                        } else {
                            getPendingOrderTaskDataFromServer();
                        }

                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTask"), mContext);
                    }
                }
            }
        });

    }

    private void CancelTask(ObjectElement task, final String reason) {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set(Task.TASK_ID, DataUtil.isDataElementNull(task.get(Task.TASK_ID)));
        submitData.set("QuitReason", reason);
        params.putJsonParams(submitData.toJson());
        LogUtils.e("取消任务提交参数---->" + submitData.toJson());
        HttpUtils.post(mContext, "TaskAPI/TaskQuit", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTaskCauseByNetWork"), mContext);
                dismissCustomDialog();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if (t != null) {
                    LogUtils.e("取消任务--->" + t);
                    JsonObjectElement returnData = new JsonObjectElement(t);
                    if (returnData.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessCancelTask"), mContext);
                        pageIndex = 1;
//                        removeNum=0;
                        if (TaskClass.equals("T08")) {
                            getStyleChangeOrderFromServer();
                        } else {
                            getPendingOrderTaskDataFromServer();
                        }
                    } else {
                        if (returnData.get(Data.Msg).toString().isEmpty()) {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTask"), mContext);
                        } else {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue(returnData.get(Data.Msg).valueAsString()), mContext);
                        }
                    }
                }
            }
        });
    }

    public String getFactory() {
        return From_Factory;
    }

    public void setFactory(String factory) {
        From_Factory = factory;
    }

    public void setFactoryId(String factoryId) {

        FactoryId = factoryId;

    }

}
