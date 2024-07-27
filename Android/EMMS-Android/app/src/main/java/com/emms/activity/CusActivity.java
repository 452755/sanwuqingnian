package com.emms.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.http.VolleyError;
import com.emms.R;
import com.emms.adapter.MainActivityAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Factory;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.BuildConfig;
import com.emms.util.DataUtil;
import com.emms.util.HttpErrorCode;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ServiceUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.flyco.tablayout.widget.MsgView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/8/23.
 * By Leo fix bugs&add module Type logic  2021-08-24
 */
public class CusActivity extends NfcActivity implements View.OnClickListener {
    private Context context = this;
    private ArrayList<ObjectElement> moduleList = new ArrayList<>();
    private static HashMap<String, Integer> TaskClass_moduleID_map = new HashMap<>();
    private HashMap<Integer, ObjectElement> ID_module_map = new HashMap<>();
    private MainActivityAdapter adapter;
    RefreshTaskNumBroadCast refreshTaskNumBroadCast = new RefreshTaskNumBroadCast();
    IntentFilter intentFilter = new IntentFilter("RefreshTaskNum");
    private boolean syncLock = false;//用于限制只能同时进行一次任务数字获取
    private int retryTimes = 1;
    private String factory;//当前登录用户工厂 By Leo

    private Handler mHandler;

    private AlertDialog dialog;
    private static final String FILE_NAME = "emms.apk";

    private boolean canGetVersion = true;

    private AlertDialog AddEquipmentDialog = null;

    //create by jason 2019/5/15 加载失败提示用户重新加载
    TextView tv_reload;

    //更新的id 要及时删除
    String updateId;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);;
    ScheduledFuture<?> scheduledFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus);
        ((Button) findViewById(R.id.button2)).setText(LocaleUtils.getI18nValue("exit"));
        tv_reload = (TextView) findViewById(R.id.tv_reload);
        mHandler = new Handler(getMainLooper());
        factory = getLoginInfo().getFactoryId();
        AppApplication.KeepLive = true;
        ServiceUtils.starKeepLiveService(ServiceUtils.Mode.Only_KeepLiveServiceNo_1, this);
        LogUtils.e("entering_equipment--->" + LocaleUtils.getI18nValue("entering_equipment"));
        LogUtils.e("DeveceHistory--->" + LocaleUtils.getI18nValue("DeveceHistory"));
        LogUtils.e("fault_summary--->" + LocaleUtils.getI18nValue("fault_summary"));
        LogUtils.e("task_details--->" + LocaleUtils.getI18nValue("task_details"));
        LogUtils.e("taskHistory--->" + LocaleUtils.getI18nValue("taskHistory"));
        LogUtils.e("task_info_entering--->" + LocaleUtils.getI18nValue("task_info_entering"));
//        BroadcastUtils.startKeepLiveBroadcast(this);
        if (Factory.FACTORY_EGM.equals(SharedPreferenceManager.getFactory(this))) {
            AppApplication.AppTimeZone = "GMT+8";
//            TimeZone.setDefault(TimeZone.getTimeZone("GMT+7"));
        } else {
            AppApplication.AppTimeZone = "GMT+8";
//            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        }
        {
            TaskClass_moduleID_map.put(Task.REPAIR_TASK, 3);//车间报修
            TaskClass_moduleID_map.put(Task.GROUP_ARRANGEMENT, 2);//组内安排
            TaskClass_moduleID_map.put(Task.TRANSFER_TASK, 4);//转款任务
            TaskClass_moduleID_map.put(Task.TRANSFER_CHECK_IN, 16);//转款入仓
            TaskClass_moduleID_map.put(Task.TRANSFER_REJECT, 17);//转款报废
            TaskClass_moduleID_map.put(Task.OTHER_TASK, 5);//其它任务
            TaskClass_moduleID_map.put(Task.ROUTING_INSPECTION, 11);//点巡检
            TaskClass_moduleID_map.put(Task.UPKEEP, 12);//保养
            TaskClass_moduleID_map.put(Task.MOVE_CAR_TASK, 13);//搬车任务
            TaskClass_moduleID_map.put(Task.TRANSFER_MODEL_TASK, 14);//调车任务
            TaskClass_moduleID_map.put(Task.Lend_TASK, 21);//设备借还
            TaskClass_moduleID_map.put("C2", 8); //工时审核
            TaskClass_moduleID_map.put("C1", 10);//任务审核
            TaskClass_moduleID_map.put("C3", 7); //任务历史
        }
//        String sql="select (case when dd.[DataValue3]='Garment' then 'EGM' else dd.[DataValue3] end) appMode from DataDictionary  dd where dd.DataType = 'AppFactorySetting'"
//                +" and dd.[Factory_ID] ='"+SharedPreferenceManager.getFactory(context)+"'";
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
//                    LogUtils.e("element---->"+element.asArrayElement().toString());
//                        SharedPreferenceManager.setAppMode(context,DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("appMode")));
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        initView();
//                        initData();
//                        getTaskCountFromServer(true);
//                    }
//                });
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                  Log.e("fail",ex.toString());
//            }
//        });
//        Intent intent = getIntent();
//        boolean isFromSplash = intent.getBooleanExtra("isFromSplash",false);
//        if (isFromSplash){
//            //如果是从splash页面跳转过来的就要获取版本更新
//            getVersion();
//        }
        getDataByServe();
        BaseData.setBaseData(context);

        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataByServe();
            }
        });

        //获取工厂对应时区
        getTimeZone();

        getSparePartDictionaryListByServe("BillStatus");
        getSparePartDictionaryListByServe("SpareWarehouse");
        getSparePartDictionaryListByServe("SpareBillType");
    }


    /**
     * create by jason
     */
    private void getDataByServe() {

        try {


            HttpParams params = new HttpParams();
//        JsonObjectElement checkJson =  new JsonObjectElement();
//        checkJson.set("Factory_ID",SharedPreferenceManager.getFactory(context));
//        params.putJsonParams(checkJson.toJson());
            params.put("Factory_ID", SharedPreferenceManager.getFactory(context));

            HttpUtils.get(this, "DataDictionary/GetFactoryMode", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    try {

                        if (TextUtils.isEmpty(t) || t.equals("null")) {
                            tv_reload.setVisibility(View.VISIBLE);
                            return;
                        }
                        tv_reload.setVisibility(View.GONE);
                        DataElement dataElement = new JsonArrayElement(t);
                        if (dataElement != null && dataElement.isArray() && dataElement.asArrayElement().size() > 0) {
                            LogUtils.e("dataElement---->" + dataElement.asArrayElement().toString());
                            SharedPreferenceManager.setAppMode(context, DataUtil.isDataElementNull(dataElement.asArrayElement().get(0).asObjectElement().get("appMode")));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initView();
                                initData();
                                getTaskCountFromServer(true);
                            }
                        });
                        LogUtils.e("getDataByServe--测试成功--->" + t);
                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                        initView();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    tv_reload.setVisibility(View.VISIBLE);
                    String message = HttpErrorCode.Message(errorNo);
                    if (!TextUtils.isEmpty(message)) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue(message), context);
                    } else {
                        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                    }
                    initView();
//                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                    LogUtils.e("getDataByServe--测试失败--->" + errorNo + "----" + strMsg);
                }
            });

        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
            initView();
        }

    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    private void initView() {
        Button btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(this);
        if (getLoginInfo() != null) {

            ((TextView) findViewById(R.id.UserName)).setText(getLoginInfo().getName());
            ((TextView) findViewById(R.id.WorkNum_tag)).setText(getLoginInfo().getOperator_no());
        } else {
            showCustomDialog(LocaleUtils.getI18nValue("logout"));
            HttpParams params = new HttpParams();
            HttpUtils.delete(this, "Token", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    dismissCustomDialog();
                    if (!JPushInterface.isPushStopped(context)) {
                        JPushInterface.stopPush(context);
                    }
                    logout();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    dismissCustomDialog();
                    if (!JPushInterface.isPushStopped(context)) {
                        JPushInterface.stopPush(context);
                    }
                    logout();
                }
            });
        }
        final PullToRefreshGridView module_list = (PullToRefreshGridView) findViewById(R.id.module_list);
        if (moduleList.size() == 0 && moduleList == null) {
            showCustomDialog(LocaleUtils.getI18nValue("logout"));
            HttpParams params = new HttpParams();
            HttpUtils.delete(this, "Token", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    dismissCustomDialog();
                    if (!JPushInterface.isPushStopped(context)) {
                        JPushInterface.stopPush(context);
                    }
                    logout();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    dismissCustomDialog();
                    if (!JPushInterface.isPushStopped(context)) {
                        JPushInterface.stopPush(context);
                    }
                    logout();
                }
            });
        }
        adapter = new MainActivityAdapter(moduleList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                MainActivityAdapter.TaskViewHolder holder;
//                if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_cur, parent, false);
                holder = new MainActivityAdapter.TaskViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.module_image);
                holder.moduleName = (TextView) convertView.findViewById(R.id.module_name);
                holder.msgView = (MsgView) convertView.findViewById(R.id.task_num);
                convertView.setTag(holder);
//                } else {
//                    holder = (MainActivityAdapter.TaskViewHolder) convertView.getTag();
//                }
                if (moduleList.get(position).get("module_image") != null) {
                    holder.image.setImageResource(moduleList.get(position).get("module_image").valueAsInt());
                }
                if (moduleList.get(position).get("module_name") != null) {
                    holder.moduleName.setText(moduleList.get(position).get("module_name").valueAsString());
                }
                if (moduleList.get(position).get("TaskNum") != null) {
                    holder.msgView.setVisibility(View.VISIBLE);
                    switch (moduleList.get(position).get("TaskNumType").valueAsInt()) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            String s[] = DataUtil.isDataElementNull(moduleList.get(position).get("TaskNum")).split("/");
                            if (DataUtil.isInt(s[1]) && Integer.valueOf(s[1]) == 0) {
                                holder.msgView.setBgSelector2();
                            }
                            break;
                        }
                        case 2: {
                            if (DataUtil.isInt(DataUtil.isDataElementNull(moduleList.get(position).get("TaskNum")))
                                    && moduleList.get(position).get("TaskNum").valueAsInt() == 0) {
                                holder.msgView.setBgSelector2();
                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    holder.msgView.setText(DataUtil.isDataElementNull(moduleList.get(position).get("TaskNum")));
                }
                return convertView;
            }
        };
        module_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (moduleList.get(position).get("Class") != null) {
                    try {
                        Class c = Class.forName(DataUtil.isDataElementNull(moduleList.get(position).get("Class")));
                        LogUtils.e("页面路径--->" + moduleList.get(position).get("Class"));
                        LogUtils.e("页面名字--->" + moduleList.get(position).toString());
                        Intent intent = new Intent(context, c);
                        if (moduleList.get(position).get(Task.TASK_CLASS) != null) {
                            intent.putExtra(Task.TASK_CLASS, DataUtil.isDataElementNull(moduleList.get(position).get(Task.TASK_CLASS)));
                        }
                        if (moduleList.get(position).get("TaskNum") != null) {
                            intent.putExtra("TaskNum", DataUtil.isDataElementNull(moduleList.get(position).get("TaskNum")));
                        }
                        if (moduleList.get(position).get(Task.TASK_SUBCLASS) != null) {
                            intent.putExtra(Task.TASK_SUBCLASS, DataUtil.isDataElementNull(moduleList.get(position).get(Task.TASK_SUBCLASS)));
                        }
                        startActivity(intent);
                    } catch (Throwable e) {
                        CrashReport.postCatchedException(e);
                    }
                }
            }
        });
        module_list.setAdapter(adapter);
        module_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        module_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                LogUtils.e("执行下拉刷新--->");
                getTaskCountFromServer(true);
                module_list.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {

            }
        });




        //TODO
        if (SharedPreferenceManager.getUserRoleID(this) != null && Integer.valueOf(SharedPreferenceManager.getUserRoleID(this)) != null) {
            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(this)) == 7) {
                ((ImageView) findViewById(R.id.rootImage)).setImageResource(R.mipmap.applicant);
            } else if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(this)) < 5) {
                ((ImageView) findViewById(R.id.rootImage)).setImageResource(R.mipmap.repairerleader);
            } else {
                ((ImageView) findViewById(R.id.rootImage)).setImageResource(R.mipmap.repairer);
            }
        } else {
            ((ImageView) findViewById(R.id.rootImage)).setImageResource(R.mipmap.repairer);
        }
    }

    private void initData() {
        if (getIntent().getStringExtra("Module_ID_List") != null && !getIntent().getStringExtra("Module_ID_List").equals("")) {
            String module = getIntent().getStringExtra("Module_ID_List");
            LogUtils.e("module测试---->" + module);
            String[] modules = module.split(",");

            for (String module1 : modules) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement();
                jsonObjectElement.set("module_ID", Integer.valueOf(module1));
                jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                ID_module_map.put(Integer.valueOf(module1), jsonObjectElement);
                moduleList.add(jsonObjectElement);
            }
        } else {
            if (!SharedPreferenceManager.getUserModuleList(this).equals("")) {
                String module = SharedPreferenceManager.getUserModuleList(this);
                LogUtils.e("module测试1---->" + module);
                String[] modules = module.split(",");
                for (String module1 : modules) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement();
                    jsonObjectElement.set("module_ID", Integer.valueOf(module1));
                    jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                    ID_module_map.put(Integer.valueOf(module1), jsonObjectElement);
                    moduleList.add(jsonObjectElement);
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    LogUtils.e("module测试3");
                    JsonObjectElement jsonObjectElement = new JsonObjectElement();
                    jsonObjectElement.set("module_ID", i + 1);
                    jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                    ID_module_map.put(i + 1, jsonObjectElement);
                    moduleList.add(jsonObjectElement);
                }
            }
        }

    }

    //个性化开发，根据服务器返回的角色模块ID进行个性化配置
    private JsonObjectElement moduleMatchingRule(JsonObjectElement obj) {
        int module_id = obj.get("module_ID").valueAsInt();
        switch (module_id) {
            case 1: {//createTask
                setModelProperty(obj, R.mipmap.cur_activity_create_task,
                        factory.indexOf("GLE") > -1 ? LocaleUtils.getI18nValue("create_repair_task") : LocaleUtils.getI18nValue("create_task"),
                        null, null, "CreateTaskActivity", null, 0);
                break;
            }
            case 2: {//maintainTask
                setModelProperty(obj, R.mipmap.cur_activity_maintain,
                        LocaleUtils.getI18nValue("GroupArrangement"), Task.GROUP_ARRANGEMENT, null, "TaskListActivity", "0/0", 1);
                break;
            }
            case 3: {//repairTask
                setModelProperty(obj, R.mipmap.cur_activity_repair,
                        factory.indexOf("GLE") > -1 ? LocaleUtils.getI18nValue("RepairReceive") : LocaleUtils.getI18nValue("repair"),
                        Task.REPAIR_TASK, null, "TaskListActivity", "0/0", 1);
                break;
            }
//            case 4:{//moveCarTask
//                setModelProperty(obj,R.mipmap.cur_activity_move_car,
//                        LocaleUtils.getI18nValue("move_car"),Task.MOVE_CAR_TASK,null,"TaskListActivity","0/0",1);
//                break;
//            }
            case 5: {//teamStatus
                setModelProperty(obj, R.mipmap.cur_activity_other,
                        LocaleUtils.getI18nValue("other"), Task.OTHER_TASK, null, "TaskListActivity", "0/0", 1);
                break;
            }
            case 6: {//deviceFaultSummary  故障总结
                setModelProperty(obj, R.mipmap.cur_activity_equipment_summary,
                        LocaleUtils.getI18nValue("DeveceHistory"), null, null, "EquipmentHistory", null, 0);
                break;
            }
            case 7: {//TaskCommand
                setModelProperty(obj, R.mipmap.cur_activity_task_history,
                        LocaleUtils.getI18nValue("taskHistory"), null, null, "TaskHistoryCheck", "0", 2);
                break;
            }
            case 8: {//workloadverify
                setModelProperty(obj, R.mipmap.cur_activity_workload_verify,
                        LocaleUtils.getI18nValue("workloadVerify"), null, null, "WorkloadVerifyActivity", "0", 2);
                break;
            }
            case 9: {//team staff
                setModelProperty(obj, R.mipmap.cur_activity_team,
                        LocaleUtils.getI18nValue("team"), null, null, "TeamStatusActivity", null, 0);
                break;
            }
            case 10: {//taskverify
                setModelProperty(obj, R.mipmap.cur_activity_verify,
                        LocaleUtils.getI18nValue("TaskVerify"), Task.Verify_TASK, null, "TaskVerifyActivity", "0", 2);
                break;
            }
            case 11: {//巡检
                setModelProperty(obj, R.mipmap.module_measure_point,
                        LocaleUtils.getI18nValue("routingInspection"), Task.MAINTAIN_TASK, Task.ROUTING_INSPECTION, "TaskListActivity", "0/0", 1);
                break;
            }
            case 12: {//保养
                setModelProperty(obj, R.mipmap.module_upkeep,
                        LocaleUtils.getI18nValue("upkeep"), Task.MAINTAIN_TASK, Task.UPKEEP, "TaskListActivity", "0/0", 1);
                break;
            }
            case 19: {//新保养（维护任务）
                setModelProperty(obj, R.mipmap.module_upkeep,
                        LocaleUtils.getI18nValue("upkeep_new"), Task.MAINTAIN_TASK, Task.UPKEEP, "TaskListActivityNew", "0/0", 1);
                break;
            }
            case 13: {//搬车
                setModelProperty(obj, R.mipmap.cur_activity_move_car,
                        LocaleUtils.getI18nValue("move_car"), Task.MOVE_CAR_TASK, null, "TaskListActivity", "0/0", 1);
                break;
            }
            case 14: {//调车
                setModelProperty(obj, R.mipmap.model_transfer_model,
                        LocaleUtils.getI18nValue("transfer_model"), Task.TRANSFER_MODEL_TASK, null, "TaskListActivity", "0/0", 1);
                break;
            }
            case 15: {//设备绑定
                setModelProperty(obj, R.mipmap.system_setting_activity_binding,
                        LocaleUtils.getI18nValue("EquipmentBinding"), null, null, "EnteringEquipmentICCardIDActivity", null, 0);
                break;
            }
            case 4: {//创建转款任务
                setModelProperty(obj, R.mipmap.cur_activity_change_style,
                        LocaleUtils.getI18nValue("style_change"), Task.TRANSFER_TASK, null, "TaskListActivity", "0/0", 1);
                break;
            }
            case 16: {//转款入仓
                setModelProperty(obj, R.mipmap.cur_activity_change_style_check_in,
                        LocaleUtils.getI18nValue("style_change_check_in"), Task.TRANSFER_CHECK_IN, null, "StyleChangeCheckInActivity", null, 1);
                break;
            }
            case 17: {//转款报废
                setModelProperty(obj, R.mipmap.cur_activity_change_style_refect,
                        LocaleUtils.getI18nValue("style_change_reject"), Task.TRANSFER_REJECT, null, "StyleChangeCheckInActivity", null, 1);
                break;
            }
            case 18: {
                //作用:机器查询  Jason 2019/9/27 上午10:38
                setModelProperty(obj, R.mipmap.machine_search,
                        LocaleUtils.getI18nValue("machine_search"), Task.TRANSFER_REJECT, null, "MachineSearchActivity", null, 1);
                break;
            }
            case 20: {
                //物料申请 2021-12-07 Abrahamguo 新增备件管理页面入口
                setModelProperty(obj, R.mipmap.cur_activity_sparepart_manager,
                        LocaleUtils.getI18nValue("spare_part_management"), Task.SPAREPART_REQUEST, null, "SparePartRequestActivity", null, 0);
                break;
            }
            case 21: {
                //设备借还 2022-3-7 kingzhang 新增设备借还页面入口 SRF 2022-0106
                setModelProperty(obj, R.mipmap.cur_activity_equipment,
                        LocaleUtils.getI18nValue("Equipment_Borrow_Return"), Task.Lend_TASK, null, "TaskListActivity", "0/0", 1);
                break;
            }
            default: {
                setModelProperty(obj, R.mipmap.model_transfer_model,
                        LocaleUtils.getI18nValue("transfer_model"), Task.TRANSFER_MODEL_TASK, null, "TaskListActivity", "0/0", 1);
            }
        }
        return obj;
    }

    private void setModelProperty(JsonObjectElement obj, int module_image, String module_name, String TaskClass, String TaskSubClass,
                                  String Class, String TaskNum, int TaskNumType) {
        String packageName = "com.emms.activity.";
        obj.set("module_image", module_image);
        obj.set("module_name", module_name);
        if (TaskClass != null && TaskClass.equals("T02")) {
            if (LocaleUtils.getI18nValue("upkeep_new").equals(module_name)) {
                obj.set(Task.logicType, "New");
            } else
                obj.set(Task.logicType, "Old");
        }
        if (TaskClass != null) {
            obj.set(Task.TASK_CLASS, TaskClass);
        }
        if (TaskSubClass != null) {
            obj.set(Task.TASK_SUBCLASS, TaskSubClass);
        }
        obj.set("Class", packageName + Class);
        if (TaskNum != null) {
            obj.set("TaskNum", TaskNum);
        }
        obj.set("TaskNumType", TaskNumType);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BuildConfig.NetWorkSetting(context);
        getTaskCountFromServer(true);
        //可移除
//        getDBDataLastUpdateTime();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //如果有id，证明id删除不成功了，就要删除一下。
        updateId = SharedPreferenceManager.getUpdateId(context);
        if (!TextUtils.isEmpty(updateId)) {
            LogUtils.e("进入删除ID不成功--->" + updateId);
            if (dialog != null) {
                if (dialog.isShowing()) {

                } else {
                    DeleteDownLoadID(updateId, null);
                }
            } else {
                DeleteDownLoadID(updateId, null);
            }

        }

        try {
            if (refreshTaskNumBroadCast != null) {
                context.registerReceiver(refreshTaskNumBroadCast, intentFilter);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

        if (canGetVersion) {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    getVersion();
                }
            } else {
                getVersion();
            }

        }

        LogUtils.e(new SimpleDateFormat("hh:mm:ss:sss").format(new Date()));
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.e(new SimpleDateFormat("hh:mm:ss:sss").format(new Date()));
                    HttpParams params = new HttpParams();
                    JsonObjectElement json = new JsonObjectElement();
                    json.set("OperatorId",(int) getLoginInfo().getId());
                    json.set("OrganiseIdArray",getLoginInfo().getOrganiseID());
                    params.putJsonParams(json.toJson());
                    LogUtils.e(params.getJsonParams().toString());
                    HttpUtils.post(context,"Notification/PushMessageDispose",params,new HttpCallback(){
                        @Override
                        public void onSuccess(String t) {
                            super.onSuccess(t);
                            LogUtils.e("Notification/PushMessageDispose 查询的结果---->" + t);
                            if((new JsonObjectElement(t)).get("Success").valueAsBoolean()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if(AddEquipmentDialog == null || !AddEquipmentDialog.isShowing()) {
                                                final String DialogMessage = LocaleUtils.getI18nValue("有新任务送达，请刷新！");
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setMessage(DialogMessage);
                                                builder.setCancelable(false);
                                                builder.setNegativeButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        getTaskCountFromServer(true);
                                                        dialog.dismiss();
                                                    }
                                                });
                                                AddEquipmentDialog = builder.create();
                                                AddEquipmentDialog.show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onFailure(int errorNo, String strMsg) {
                            LogUtils.e(strMsg);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        scheduledFuture = executorService.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.SECONDS);

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("进入OnStop");
//        if (dialog!=null){
//            if (dialog.isShowing()){
//                dialog.dismiss();
//                LogUtils.e("进入OnStop");
//                updateId = SharedPreferenceManager.getUpdateId(context);
//                if (!TextUtils.isEmpty(updateId)){
//                    DeleteDownLoadID(updateId,null);
//                }
//            }
//        }

        scheduledFuture.cancel(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("进入Ondestroy");
        updateId = SharedPreferenceManager.getUpdateId(context);
        if (!TextUtils.isEmpty(updateId)) {
            DeleteDownLoadID(updateId, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (refreshTaskNumBroadCast != null) {
                unregisterReceiver(refreshTaskNumBroadCast);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }

    /**
     * 获取转款任务的任务数量
     */
    private void getStyleChangeTaskCountFromServer() {

        HttpParams params = new HttpParams();
        JsonObjectElement SubData = new JsonObjectElement();
        if (getLoginInfo().getOperator_no() == null) {
            LogUtils.e("getLoginInfo().getOperator_no()为空");
            return;
        } else if (getLoginInfo().getOperator_no().isEmpty()) {
            LogUtils.e("getLoginInfo().getOperator_no()为空");
            return;
        }
        SubData.set("userId", getLoginInfo().getOperator_no());
        SubData.set("factoryId", getLoginInfo().getFactoryId());
        params.putJsonParams(SubData.toJson());
        LogUtils.e("转款-获取任务单量上传的字段---->" + SubData.toString());
        HttpUtils.getChangeFormServer(context, "emms/transferTask/aggregateStatusByOrderAcceptUserId", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try {
                    if (t != null) {
                        LogUtils.e("转款-获取任务单量返回的数据---->" + t);
                        JsonObjectElement json = new JsonObjectElement(t);
                        //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                        //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                        //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        if (json.get("DoingNo") != null &&
                                json.get("ToDoNo") != null) {
                            optimizationData(json, "ToDoNo", "DoingNo");
                            String taskNumToShow;
                            if (DataUtil.isDataElementNull(json.get("TaskClass")).equals("C1")
                                    || DataUtil.isDataElementNull(json.get("TaskClass")).equals("C2")
                                    || DataUtil.isDataElementNull(json.get("TaskClass")).equals("C3")) {
                                taskNumToShow = DataUtil.isDataElementNull(json.get("ToDoNo"));
                            } else {
                                taskNumToShow = DataUtil.isDataElementNull(json.get("DoingNo")) + "/" +
                                        DataUtil.isDataElementNull(json.get("ToDoNo"));
                            }
                            if (ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(json.get("TaskClass")))) != null) {
                                ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(json.get("TaskClass")))).set("TaskNum", taskNumToShow);
                            }
                        }

                        LogUtils.e("ID_module_map--->" + ID_module_map.toString());
//                        getStyleChangeTaskCountFromServer();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
//                BaseData.setBaseData(context);
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });


    }

    //获取任务数量
    private synchronized void getTaskCountFromServer(boolean showLoadingDialog) {
        try {


            LogUtils.e("开始进入下拉刷新-synLock-->" + syncLock);
            if (syncLock) {
                return;
            }
            syncLock = true;
            if (showLoadingDialog) {
                showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
            }
            HttpParams params = new HttpParams();
            //params.put("id",String.valueOf(getLoginInfo().getId()));
            // String s=SharedPreferenceManager.getUserName(this);
            HttpUtils.get(this, "TaskAPI/TaskNum", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    retryTimes = 1;
                    syncLock = false;
                    try {
                        if (t != null) {
                            LogUtils.e("原来接口返回的数据---->" + t);
                            JsonArrayElement json = new JsonArrayElement(t);
                            //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                            int taskNumToShow_equipmentDoNo=0;
                            int taskNumToShow_equipmentDoingNo=0;
                            for (int i = 0; i < json.size(); i++) {
                                //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                                //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                                if (json.get(i).asObjectElement().get("DoingNo") != null &&
                                        json.get(i).asObjectElement().get("ToDoNo") != null) {
                                    ObjectElement jsonObjectElement = json.get(i).asObjectElement();
                                    optimizationData(jsonObjectElement, "ToDoNo", "DoingNo");
                                    String taskNumToShow;
                                    if (DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")).equals("C1")
                                            || DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")).equals("C2")
                                            || DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")).equals("C3")) {
                                        taskNumToShow = DataUtil.isDataElementNull(jsonObjectElement.get("ToDoNo"));
                                    } else {
                                        taskNumToShow = DataUtil.isDataElementNull(jsonObjectElement.get("DoingNo")) + "/" +
                                                DataUtil.isDataElementNull(jsonObjectElement.get("ToDoNo"));
                                    }
                                    LogUtils.e("ID_module_map logicType--->" + json.get(i).asObjectElement().get("logicType"));
                                    String logicType = jsonObjectElement.get("logicType").toString();
                                    String TaskClass = jsonObjectElement.get("TaskClass").toString();
                                    if(TaskClass.contains("T10") || TaskClass.contains("T09") ){
                                        taskNumToShow_equipmentDoNo=taskNumToShow_equipmentDoNo+Integer.parseInt(jsonObjectElement.get("ToDoNo").toString());
                                        taskNumToShow_equipmentDoingNo=taskNumToShow_equipmentDoingNo+Integer.parseInt(jsonObjectElement.get("DoingNo").toString());
                                        ID_module_map.get(21).set("TaskNum",
                                                taskNumToShow_equipmentDoingNo+"/"+taskNumToShow_equipmentDoNo);
                                    }
                                    else if (logicType.contains("New")) {
                                        LogUtils.e("ID_module_map TaskClass--->" + ID_module_map.get(19));
                                        if (ID_module_map.get(19) != null) {
                                            //新保养app下标（判断是否存在对应模块权限）
                                            ID_module_map.get(19).set("TaskNum", taskNumToShow);
                                        }
                                    } else if (ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")))) != null) {
                                        ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")))).set("TaskNum", taskNumToShow);
                                    }
                                }
                            }
                            LogUtils.e("ID_module_map--->" + ID_module_map.toString());
                            getStyleChangeTaskCountFromServer();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }

                        }
                    } catch (Exception e) {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        CrashReport.postCatchedException(e);
                    }
//                BaseData.setBaseData(context);
                    dismissCustomDialog();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    LogUtils.e("原来接口返回的数据-errorNo--->" + errorNo + "--msg-->" + strMsg);
                    syncLock = false;
                    try {
//                    BaseData.setBaseData(context);
                        if (errorNo == 401) {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("unauthorization"), context);
                            dismissCustomDialog();
                            return;
                        }
                        if (retryTimes < 4) {
                            if (mHandler != null) {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("retry", String.valueOf(retryTimes));
                                        retryTimes++;
                                        getTaskCountFromServer(true);
                                    }
                                }, 1500);
                            } else {
                                mHandler = new Handler(getMainLooper());
                            }
                            return;
                        }
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail") + "\n" + strMsg + "\n" + "returnCode:" + errorNo, context);
                    } catch (Exception e) {
                        //Do nothing
                        dismissCustomDialog();
                        CrashReport.postCatchedException(e);
                    }
                    dismissCustomDialog();
                }
            });
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_exit) {
            LogUtils.e("进入退出登录操作");
            showCustomDialog(LocaleUtils.getI18nValue("logout"));
            HttpParams params = new HttpParams();
            HttpUtils.delete(this, "Token", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    dismissCustomDialog();
                    if (!JPushInterface.isPushStopped(context)) {
                        JPushInterface.stopPush(context);
                    }
                    logout();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    dismissCustomDialog();
                    if (!JPushInterface.isPushStopped(context)) {
                        JPushInterface.stopPush(context);
                    }
                    logout();
                }
            });
        }
    }

    /**
     * When the User Logout ,clear all the User Info from SharePreference except Account
     */
    private void logout() {
        SharedPreferenceManager.setPassWord(CusActivity.this, null);
        SharedPreferenceManager.setCookie(CusActivity.this, null);
        SharedPreferenceManager.setLoginData(CusActivity.this, null);
        SharedPreferenceManager.setUserData(CusActivity.this, null);
        SharedPreferenceManager.setMsg(CusActivity.this, null);
        SharedPreferenceManager.setUserRoleID(CusActivity.this, null);
        Intent intent = new Intent(CusActivity.this, LoginActivity.class);
        intent.putExtra("FromCusActivity", true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void optimizationData(ObjectElement data, String key1, String key2) {
        if (data.get(key1) != null
                && DataUtil.isNum(DataUtil.isDataElementNull(data.get(key1)))
                && data.get(key1).valueAsInt() >= 100) {
            data.set(key1, "99");
        }
        if (data.get(key2) != null
                && DataUtil.isNum(DataUtil.isDataElementNull(data.get(key2)))
                && data.get(key2).valueAsInt() >= 100) {
            data.set(key2, "99");
        }
    }

    private class RefreshTaskNumBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.e("下拉刷新数据进入点--->" + intent.getDataString() + "---->" + intent.getAction());
            if ("RefreshTaskNum".equals(intent.getAction())) {
                getTaskCountFromServer(false);
            }
        }
    }

    /**
     * 获取更新数据
     * 从LoginActivity转移过来
     */
    private void getVersion() {
        LogUtils.e("开始获取版本更新");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);

            int CurrentVersion = packageInfo.versionCode;
            httpParams.put("Version", CurrentVersion);
            httpParams.put("Factory", factory);
            HttpUtils.getWithoutCookies(context, "System_Version/GetAppDownloadInfo", httpParams, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    dismissCustomDialog();
                    if (t != null) {
                        LogUtils.e("获取更新成功---->" + t);
                        handleVersionUpdate(context, t);
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    dismissCustomDialog();
                    DoInit();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            DoInit();
            CrashReport.postCatchedException(e);
        }
    }

    public void handleVersionUpdate(final Context context, String element) {

        try {
            JsonObjectElement json = new JsonObjectElement(element);
            final ObjectElement data = json.get(Data.PAGE_DATA).asArrayElement().get(0).asObjectElement();
            int version = data.get("Version").valueAsInt();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int CurrentVersion = packageInfo.versionCode;
            if (CurrentVersion < version) {
                if (data.get("Content") != null && data.get("Content").isPrimitive()) {
                   /* if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH
                            || LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())==LocaleUtils.SupportedLanguage.ENGLISH) {
                        DataUtil.getDataFromLanguageTranslation(context.getApplicationContext(),DataUtil.isDataElementNull(data.get("Content")), new StoreCallback() {
                            @Override
                            public void success(DataElement e, String resource) {
                                if(e.isArray()&&e.asArrayElement().size()>0) {
                                    showDialog(context,data,DataUtil.isDataElementNull(e.asArrayElement().get(0).asObjectElement().get("Translation_Display"))
                                            + "1.0." + DataUtil.isDataElementNull(data.get("Version")));
                                }else {
                                    showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                            +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                                }
                            }

                            @Override
                            public void failure(DatastoreException ex, String resource) {
                                showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                        +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                            }
                        });
                    }else {
                        showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                    }*/
                    final boolean canUpdate = data.get("CanUpdate").valueAsBoolean();
                    LogUtils.e("canUpdate---->" + canUpdate);
                    if (canUpdate) {
                        showDialog(context, data, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(data.get("Content")))
                                + "1.0." + DataUtil.isDataElementNull(data.get("Version")));
                    }

                } else {
                    DoInit();
                }

            } else {
                DoInit();
            }
        } catch (Exception e) {
            DoInit();
            CrashReport.postCatchedException(e);
        }
    }

    public void showDialog(final Context context, final ObjectElement element, final String message) {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try {
                    if (dialog == null || dialog.getContext() != context) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                context);
                        dialog = builder.create();
                    }
                    DataElement e = element.get("Header");
                    if (e != null && e.isPrimitive()) {
                        dialog.setTitle(e.asPrimitiveElement()
                                .valueAsString());
                    }

                    //e = element.get("Content");
                    dialog.setMessage(message);
                    dialog.setCancelable(false);

                    e = element.get("ConfirmButtonText");
                    if (e != null && e.isPrimitive()) {
//                    final DataElement clickEventUrl = element
//                            .asObjectElement().get("URL");
//                    final Reference url = new Reference(clickEventUrl.asPrimitiveElement().valueAsString());
                        String pathDir = FILE_NAME;
                        if (DataUtil.getDBDirPath(context) != null) {
                            //noinspection ConstantConditions
                            pathDir = DataUtil.getDBDirPath(context) + "/" + FILE_NAME;
                        }
                        final File file = new File(pathDir);
                        //final Reference destination = new Reference(file.getAbsolutePath());
//                    if (downloadTask == null) {
//                        downloadTask = new DownloadTask(context, url, destination);
//                    }
//                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                DoInit();
//                            }
//                        });
                        boolean AllowDismiss = element.get("AllowDismiss").valueAsBoolean();
                        final String UpdateId = element.get("UpdateID").valueAsString();
                        final boolean canUpdate = element.get("CanUpdate").valueAsBoolean();
                        LogUtils.e("UpdateId---->" + UpdateId);
                        SharedPreferenceManager.setUpdateId(context, UpdateId);
//                        AllowDismiss = false;
                        if (AllowDismiss) {
                            //允许用户取消更新
                            dialog.setButton2(LocaleUtils.getI18nValue("CancelUpdate"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (canUpdate) {
                                        //删除id
                                        DeleteDownLoadID(UpdateId, null);
                                    }
                                    canGetVersion = false;
                                }
                            });
                        }

                        dialog.setButton(LocaleUtils.getI18nValue("Update"), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                LogUtils.e("是否可以更新--->" + canUpdate + "dfasf--->" + LocaleUtils.getI18nValue("UpToMaxComplicationUser"));
                                if (!canUpdate) {
                                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("UpToMaxComplicationUser"), context);
                                    return;
                                }

                                ProgressBar progressView = new ProgressBar(context);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setCancelable(false);
                                alertDialog.setView(progressView);
                                alertDialog.setTitle(LocaleUtils.getI18nValue("downloading"));
                                final Dialog d = alertDialog.create();
                                d.show();
                                HttpUtils.download(context, file.getAbsolutePath(), DataUtil.isDataElementNull(element.get("URL")), null, new HttpCallback() {
                                    @Override
                                    public void onSuccess(String t) {
                                        super.onSuccess(t);
                                        d.dismiss();
                                        //下载成功后 删除id。以及更新app
                                        DeleteDownLoadID(UpdateId, file);
                                    }

                                    @Override
                                    public void onFailure(int errorNo, String strMsg) {
                                        if (!isFinishing()) {
                                            //删除id
                                            DeleteDownLoadID(UpdateId, null);
                                            d.dismiss();
                                            DoInit();
                                        }
                                        super.onFailure(errorNo, strMsg);
                                    }
                                });

                            }
                        });
                    }
                    LogUtils.e("有id值---->" + SharedPreferenceManager.getUpdateId(context));
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                    DoInit();
                }
            }
        });
    }

    /**
     * 下载完成后删除id
     */
    private void DeleteDownLoadID(final String DelUpdateID, final File file) {
        try {

            HttpParams httpParams = new HttpParams();
            httpParams.put("UpdateID", DelUpdateID);
            HttpUtils.getWithoutCookies(context, "System_Version/DelUpdateID", httpParams, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    LogUtils.e("删除Update返回数据---->" + t);
                    if (t == null && !TextUtils.isEmpty(t)) {
                        LogUtils.e("t为空");
                        SharedPreferenceManager.setUpdateId(context, DelUpdateID);
                        return;
                    } else {
                        if (t.equals("true")) {
                            SharedPreferenceManager.setUpdateId(context, "");
                            //执行相应的操作。
                            if (file != null) {
                                try {
                                    LogUtils.e("进入更新");
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToDelUpdateID"), context);
                                    CrashReport.postCatchedException(e);
                                }
                            }
                        } else {
                            LogUtils.e("没进入更新");
                            ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToDelUpdateID"), context);
                            SharedPreferenceManager.setUpdateId(context, DelUpdateID);
                        }
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    LogUtils.e("加载失败---->" + errorNo + "---->" + strMsg);
                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToDelUpdateID"), context);
                    SharedPreferenceManager.setUpdateId(context, DelUpdateID);
                }

                @Override
                public void onFailure(VolleyError error) {
                    super.onFailure(error);
                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToDelUpdateID"), context);
                    SharedPreferenceManager.setUpdateId(context, DelUpdateID);
                }
            });
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailToDelUpdateID"), context);
        }
    }

    private void DoInit() {
        if (SharedPreferenceManager.getExtranetUrl(context) == null) {
            HttpUtils.getWithoutCookiesByUrl(context, BuildConfig.getBaseUrl(context) + BuildConfig.FactoryListApi, new HttpParams(), new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    JsonArrayElement jsonArrayElement = new JsonArrayElement(t);
                    for (int i = 0; i < jsonArrayElement.size(); i++) {
                        ObjectElement objectElement = jsonArrayElement.get(i).asObjectElement();
                        if (SharedPreferenceManager.getFactory(context).equals(DataUtil.isDataElementNull(objectElement.get("factoryCode")))) {
                            SharedPreferenceManager.setExtranetUrl(context, DataUtil.isDataElementNull(objectElement.get("ExtranetURL")));
                            SharedPreferenceManager.setInteranetUrl(context, DataUtil.isDataElementNull(objectElement.get("IntranetURL")));
                            break;
                        }
                    }
                    LogUtils.e("原来的doGetDB方法");
//                   doGetDB();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetFactoryList") + "/n" + strMsg, context);
                }
            });
        } else {
            LogUtils.e("原来的doGetDB方法");
//           doGetDB();
        }
    }

    /**
     * 获取对应工厂的时区
     */
    private void getTimeZone() {
        try {


            HttpParams params = new HttpParams();
            HttpUtils.post(context, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'Timezone'%20and%20factory_id%20eq%20'" + getLoginInfo().getFactoryId() + "'", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    if (TextUtils.isEmpty(t) && t == null) {
                        LogUtils.e("返回时区为空");
                        return;
                    }
                    JsonArrayElement jsonArrayElement = new JsonArrayElement(t);
                    if (jsonArrayElement.size() > 0) {
                        if (jsonArrayElement.get(0).asObjectElement().get("DataValue1") != null) {
                            LogUtils.e("获取TimeZone--->" + jsonArrayElement.get(0).asObjectElement().get("DataValue1").valueAsString());
                            SharedPreferenceManager.setTimeZone(context, jsonArrayElement.get(0).asObjectElement().get("DataValue1").valueAsString());
                        }
                    }

                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    LogUtils.e("返回时区失败--->" + errorNo + "---strMsg--->" + strMsg);
                }
            });
        } catch (Exception e) {
            LogUtils.e("返回时区失败--->" + e.toString());
            CrashReport.postCatchedException(e);
        }
    }


    //通用备件信息获取字典数据 Abraham 2022-01-06
    private void getSparePartDictionaryListByServe(final String DataType) {
        try {
            showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
            LogUtils.e("factoryId--->" + getLoginInfo().getFactoryId());

            HttpParams params = new HttpParams();
            HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'" + DataType + "'%20and%20factory_id%20eq%20'" + getLoginInfo().getFactoryId() + "'", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    dismissCustomDialog();
                    if (TextUtils.isEmpty(t) || t.equals("null")) {
                        return;
                    }
                    LogUtils.e("字典接口--DataDictionary/APPGet--获取到的数据："+t);
                    DataElement jsonArrayElement = new JsonArrayElement(t);
                    HashMap<String, String> classMap = new HashMap<>();
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size();i++){
                        classMap.put(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("DataCode").valueAsString(),
                                jsonArrayElement.asArrayElement().get(i).asObjectElement().get("DataName").valueAsString());
                    }
                    SharedPreferenceManager.putHashMapData(context,DataType,classMap);
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    dismissCustomDialog();
                    LogUtils.e("getDictionaryListByServe--测试失败--->" + errorNo + "----" + strMsg);
                }
            });
        } catch (Exception e) {
            dismissCustomDialog();
            CrashReport.postCatchedException(e);
        }
    }

}
