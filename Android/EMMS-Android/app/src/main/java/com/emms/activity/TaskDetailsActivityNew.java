package com.emms.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.adapter.commandAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.ui.ChangeEquipmentDialog;
import com.emms.ui.ChangeEquipmentDialog_YMG;
import com.emms.ui.DropEditText;
import com.emms.ui.EquipmentCompleteListener;
import com.emms.ui.EquipmentSummaryDialog;
import com.emms.ui.ExpandGridView;
import com.emms.ui.HorizontalListView;
import com.emms.ui.MyListView;
import com.emms.ui.NFCDialog;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.ui.ScrollViewWithListView;
import com.emms.util.AnimateFirstDisplayListener;
import com.emms.util.BaseData;
import com.emms.util.Bimp;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.FileUtils;
import com.emms.util.ListViewUtility;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.google.common.collect.ObjectArrays;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2021/7/20.
 * （新）任务详情界面，用以显示任务的设备，图片,参与人，总结，评分，工作量等，并提供设备状态修改动作入口
 * 是app的核心界面之一
 */
public class TaskDetailsActivityNew extends NfcActivity implements View.OnClickListener {

    public class ViewHolder {
        TextView deviceCountTextView;
        TextView dealCountTextView;

        TextView processTextView;
        TextView suspendTextView;
        TextView pendingTextView;
        TextView finishTextView;

    }

    private TextView fault_type, fault_description, repair_status;
    private ViewHolder mHolder = new ViewHolder();
    private ScrollViewWithListView mListview;
    private ExpandGridView noScrollgridview;
    private GridAdapter adapter;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas = new ArrayList<>();
    private Context mContext = this;
    private PopMenuTaskDetail popMenuTaskDetail;
    private String TaskSubClass;
    private String EquipmentID;
    // private ChangeEquipmentDialog changeEquipmentDialog;
    private String TaskDetail = null;//任务详细
    private String TaskClass = null;//任务类型
    private Long taskId = null;//任务ID
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private Map<String, Object> deviceCountMap = new HashMap<>();//设备数量
    private Map<String, Object> taskStatusCountMap = new HashMap<>();//任务状态数量
    private ArrayList<String> TaskDeviceIdList = new ArrayList<>();//设备ID列表
    private Map<String, String> Task_DeviceId_TaskEquipmentId = new HashMap<>();//设备ID对应TaskEquipmentID映射
    private Map<String, String> TaskDeviceID_Name = new HashMap<>();
    private int TaskStatus = -1;
    private boolean getEquipmentListFail = false;//约束网络访问是否成功的tag
    //0-开始，1-暂停，2-领料，3-待料，4-结束

    private final String STATUS_DONE = "2";
    private String Main_person_in_charge_Operator_id;//任务主负责人ID
    private HashMap<String, String> taskEquipmentStatus = new HashMap<>();
    private HashMap<String, String> Equipment_Operator_Status_Name_ID_map = new HashMap<>();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类

    private static final int MSG_UPDATE_DEVICE_SUM_INFO = 10;
    private HashMap<String, HashMap<String, Integer>> TaskEquipment_OperatorID_Status = new HashMap<>();//任务设备参与人状态map
    private HashMap<String, String> Euqipment_ID_STATUS_map = new HashMap<>();
    private boolean isTaskHistory = false;
    private boolean HasTaskEquipment = true;
    private ArrayList<String> OrganiseList = new ArrayList<>();
    private String FromFragment;
    private NFCDialog nfcDialog;
    private boolean nfcDialogTag = false;

    //create by jason 2019/3/21 跳转的position
    private int JumpPostion = 0;
    private int EquipmentStatus_conts = 0;

    TextView textView8;//组别信息

    private boolean canScan = true;

    private ObjectElement equipmentInfo;
    Button btn_search;
    TextView et_queryname;
    //DropEditText et_ststus;
    Context context;
    Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    String moduleType = "";

    /**
     * 动态布局
     *
     * @param indix
     * @param de1
     * @return
     */
    private int dynamicLayout(int indix, DataElement de1) {
        LogUtils.e("跳转对应的值--->" + indix);
        if (de1 == null || de1.valueAsString() == null || "".equals(de1.valueAsString())) {//后台空对象为""
            return indix;
        }
        JsonObject jsonData = new JsonParser().parse(de1.valueAsString()).getAsJsonObject();
        LogUtils.e("动态布局--->" + jsonData);
        if (!"".equals(jsonData.get("Key").getAsString())) {
            switch (indix) {
                case 1: {
                    TextView tv = (TextView) findViewById(R.id.dynomic1_tag);
                    TextView tvd = (TextView) findViewById(R.id.dynomic1_description);
                    tv.setText(jsonData.get("Key").getAsString() + ":");
                    tvd.setText(jsonData.get("Value").getAsString());
                    if (TaskClass.equals(Task.MOVE_CAR_TASK)) {
                        tv.setVisibility(View.GONE);
                        tvd.setVisibility(View.GONE);
                    } else {
                        tv.setVisibility(View.VISIBLE);
                        tvd.setVisibility(View.VISIBLE);
                    }

                    return 2;
                }
                case 2: {
                    TextView tv = (TextView) findViewById(R.id.dynomic2_tag);
                    TextView tvd = (TextView) findViewById(R.id.dynomic2_description);
                    tv.setText(jsonData.get("Key").getAsString() + ":");
                    tvd.setText(jsonData.get("Value").getAsString());
                    if (TaskClass.equals(Task.MOVE_CAR_TASK)) {
                        tv.setVisibility(View.GONE);
                        tvd.setVisibility(View.GONE);
                    } else {
                        tv.setVisibility(View.VISIBLE);
                        tvd.setVisibility(View.VISIBLE);
                    }

                    return 3;
                }
                case 3: {
                    TextView tv = (TextView) findViewById(R.id.dynomic3_tag);
                    TextView tvd = (TextView) findViewById(R.id.dynomic3_description);
                    tv.setText(jsonData.get("Key").getAsString() + ":");
                    tvd.setText(jsonData.get("Value").getAsString());
                    tv.setVisibility(View.VISIBLE);
                    tvd.setVisibility(View.VISIBLE);
                    return 4;
                }
                case 4: {
                    TextView tv = (TextView) findViewById(R.id.dynomic4_tag);
                    TextView tvd = (TextView) findViewById(R.id.dynomic4_description);
                    tv.setText(jsonData.get("Key").getAsString() + ":");
                    tvd.setText(jsonData.get("Value").getAsString());
                    tv.setVisibility(View.VISIBLE);
                    tvd.setVisibility(View.VISIBLE);
                    return 5;
                }
                case 5: {
                    TextView tv = (TextView) findViewById(R.id.dynomic5_tag);
                    TextView tvd = (TextView) findViewById(R.id.dynomic5_description);
                    tv.setText(jsonData.get("Key").getAsString() + ":");
                    tvd.setText(jsonData.get("Value").getAsString());
                    tv.setVisibility(View.VISIBLE);
                    tvd.setVisibility(View.VISIBLE);
                    return 6;
                }
                default:

            }
        }
        return indix;
    }

    private void dynamicLayout2(int indix) {
        TextView tv_task_verify_person_tag = (TextView) findViewById(R.id.task_verify_person_tag);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                tv_task_verify_person_tag.getLayoutParams();
        switch (indix) {
            case 1: {
                TextView tv = (TextView) findViewById(R.id.dynomic1_tag);
                TextView tvd = (TextView) findViewById(R.id.dynomic1_description);
                TextView tv2 = (TextView) findViewById(R.id.dynomic2_tag);
                TextView tvd2 = (TextView) findViewById(R.id.dynomic2_description);
                TextView tv3 = (TextView) findViewById(R.id.dynomic3_tag);
                TextView tvd3 = (TextView) findViewById(R.id.dynomic3_description);
                TextView tv4 = (TextView) findViewById(R.id.dynomic4_tag);
                TextView tvd4 = (TextView) findViewById(R.id.dynomic4_description);
                TextView tv5 = (TextView) findViewById(R.id.dynomic5_tag);
                TextView tvd5 = (TextView) findViewById(R.id.dynomic5_description);
                tv.setVisibility(View.GONE);
                tvd.setVisibility(View.GONE);
                tv2.setVisibility(View.GONE);
                tvd2.setVisibility(View.GONE);
                tv3.setVisibility(View.GONE);
                tvd3.setVisibility(View.GONE);
                tv4.setVisibility(View.GONE);
                tvd4.setVisibility(View.GONE);
                tv5.setVisibility(View.GONE);
                tvd5.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, R.id.task_description);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.task_description);
                break;
            }
            case 2: {
                TextView tv2 = (TextView) findViewById(R.id.dynomic2_tag);
                TextView tvd2 = (TextView) findViewById(R.id.dynomic2_description);
                TextView tv3 = (TextView) findViewById(R.id.dynomic3_tag);
                TextView tvd3 = (TextView) findViewById(R.id.dynomic3_description);
                TextView tv4 = (TextView) findViewById(R.id.dynomic4_tag);
                TextView tvd4 = (TextView) findViewById(R.id.dynomic4_description);
                TextView tv5 = (TextView) findViewById(R.id.dynomic5_tag);
                TextView tvd5 = (TextView) findViewById(R.id.dynomic5_description);
                tv2.setVisibility(View.GONE);
                tvd2.setVisibility(View.GONE);
                tv3.setVisibility(View.GONE);
                tvd3.setVisibility(View.GONE);
                tv4.setVisibility(View.GONE);
                tvd4.setVisibility(View.GONE);
                tv5.setVisibility(View.GONE);
                tvd5.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, R.id.dynomic1_description);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.dynomic1_description);
                break;
            }
            case 3: {
                TextView tv3 = (TextView) findViewById(R.id.dynomic3_tag);
                TextView tvd3 = (TextView) findViewById(R.id.dynomic3_description);
                TextView tv4 = (TextView) findViewById(R.id.dynomic4_tag);
                TextView tvd4 = (TextView) findViewById(R.id.dynomic4_description);
                TextView tv5 = (TextView) findViewById(R.id.dynomic5_tag);
                TextView tvd5 = (TextView) findViewById(R.id.dynomic5_description);
                tv3.setVisibility(View.GONE);
                tvd3.setVisibility(View.GONE);
                tv4.setVisibility(View.GONE);
                tvd4.setVisibility(View.GONE);
                tv5.setVisibility(View.GONE);
                tvd5.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, R.id.dynomic2_tag);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.dynomic2_tag);
                break;
            }
            case 4: {
                TextView tv4 = (TextView) findViewById(R.id.dynomic4_tag);
                TextView tvd4 = (TextView) findViewById(R.id.dynomic4_description);
                TextView tv5 = (TextView) findViewById(R.id.dynomic5_tag);
                TextView tvd5 = (TextView) findViewById(R.id.dynomic5_description);
                tv4.setVisibility(View.GONE);
                tvd4.setVisibility(View.GONE);
                tv5.setVisibility(View.GONE);
                tvd5.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, R.id.dynomic3_tag);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.dynomic3_tag);
                break;
            }
            case 5: {
                TextView tv5 = (TextView) findViewById(R.id.dynomic5_tag);
                TextView tvd5 = (TextView) findViewById(R.id.dynomic5_description);
                tv5.setVisibility(View.GONE);
                tvd5.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, R.id.dynomic4_tag);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.dynomic4_tag);
                break;
            }
            case 6: {
                params.addRule(RelativeLayout.BELOW, R.id.dynomic5_tag);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.dynomic5_tag);
                break;
            }
        }
        tv_task_verify_person_tag.setLayoutParams(params);
    }

    /**
     * 动态生成控件
     *
     * @param taskDetail
     */
    private void createDynamicCtrl(JsonObjectElement taskDetail) {//动态生成用户需要的控件
        int indix = 1;
        DataElement de1 = taskDetail.get("SetField1");
        indix = dynamicLayout(indix, de1);
        DataElement de2 = taskDetail.get("SetField2");
        indix = dynamicLayout(indix, de2);
        DataElement de3 = taskDetail.get("SetField3");
        indix = dynamicLayout(indix, de3);
        DataElement de4 = taskDetail.get("SetField4");
        indix = dynamicLayout(indix, de4);
        DataElement de5 = taskDetail.get("SetField5");
        indix = dynamicLayout(indix, de5);
        dynamicLayout2(indix);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_new);
        try {
            TaskDetail = getIntent().getStringExtra("TaskDetail");
            LogUtils.e("获取到任务详细--->" + TaskDetail);
            TaskClass = getIntent().getStringExtra(Task.TASK_CLASS);
            taskId = Long.valueOf(getIntent().getStringExtra(Task.TASK_ID));
            TaskStatus = getIntent().getIntExtra("TaskStatus", -1);
            LogUtils.e("任务类型---->" + TaskStatus);
            isTaskHistory = getIntent().getBooleanExtra("isTaskHistory", false);
            TaskSubClass = getIntent().getStringExtra(Task.TASK_SUBCLASS);
            LogUtils.e("TaskSubClass--->" + TaskSubClass + "---TaskClass-->" + TaskClass);
            FromFragment = getIntent().getStringExtra("FromFragment");
            if (TaskDetail != null) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement(TaskDetail);
                if (jsonObjectElement.get("IsExsitTaskEquipment") != null) {
                    HasTaskEquipment = jsonObjectElement.get("IsExsitTaskEquipment").valueAsBoolean();
                }
                if (jsonObjectElement.get("OperatorOrganise_ID") != null) {
                    //OrganiseList任务创建人的所属组别
                    Collections.addAll(OrganiseList, DataUtil.isDataElementNull(jsonObjectElement.get("OperatorOrganise_ID")).split(","));
                }
                moduleType = DataUtil.isDataElementNull(jsonObjectElement.get("ModuleType"));
            }
            translateView();

            //kingzhang add for SRF
            //begin SRF
//            context = this;
//            et_ststus = (DropEditText) findViewById(R.id.et_ststus);
//            Map<String,String> map = SharedPreferenceManager.getHashMapData(context,"machine_map");
//            et_ststus.setText(map.get("equipment_status"));
            getStatus();
//            initDropSearchView(null,et_ststus.getmEditText(),LocaleUtils.getI18nValue("device_status"),"DataName",STATUS,LocaleUtils.getI18nValue("please_select"),et_ststus.getDropImage());
            //数据
            data_list = new ArrayList<String>();
            for (int i = 0; i < typeList.size(); i++) {
                data_list.add(datas.get(i).get("DataName").toString());
            }
            data_list.add("全部");
            data_list.add("待处理");
            data_list.add("处理中");
            data_list.add("完成");
            data_list.add("暂停");
            LogUtils.e("<----data_list获取数据成功---->" + data_list.toString());
            spinner = (Spinner) findViewById(R.id.spinner);
            //适配器
            arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            spinner.setAdapter(arr_adapter);

            et_queryname = (TextView) findViewById(R.id.et_queryname);
            btn_search = (Button) findViewById(R.id.btn_search);
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtils.e("<-----进入btn_search按钮事件--->");
                    getTaskEquipmentFromServerByTaskId();
                    initEvent();
                }
            });

            //end SRF
            //初始化imageLoader
            options = new DisplayImageOptions.Builder().cacheInMemory(false) // 设置下载的图片是否缓存在内存中
//                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.NONE)
                    .showImageOnLoading(R.mipmap.bg_btn)
                    // .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            imageLoader.init(ImageLoaderConfiguration
                    .createDefault(TaskDetailsActivityNew.this));
            initDataByServe();
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"), this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        getTaskEquipmentFromServerByTaskId();
    }


    private ArrayList<ObjectElement> typeList = new ArrayList<>();

    public void getStatus() {
        LogUtils.e("<------getStatus----->");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        httpParams.put("dataType", "TaskEquipmentStatus");
        HttpUtils.get(this, "DataDictionary/DataDictionaryList", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    try {

                        DataElement jsonObjectElement = new JsonArrayElement(t);
                        LogUtils.e("<----获取数据成功---->" + jsonObjectElement.toString());
                        if (jsonObjectElement.isArray() && jsonObjectElement.asArrayElement().size() > 0) {
                            typeList.clear();
                            final ObjectElement faultData = jsonObjectElement.asArrayElement().get(0).asObjectElement();
                            for (int i = 0; i < jsonObjectElement.asArrayElement().size(); i++) {
                                //翻译DATA_NAME
                                jsonObjectElement.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonObjectElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                                typeList.add(jsonObjectElement.asArrayElement().get(i).asObjectElement());
                            }
                        }
                        LogUtils.e("<----typeList获取数据成功---->" + typeList.toString());
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
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"), context);
                dismissCustomDialog();
            }
        });
    }

    private void initDataByServe() {
        try {


            showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
            HttpParams params = new HttpParams();
//        params.put("Parameter","filter=DataType eq 'TaskEquipmentStatus' and factory_id eq 'GEW'");
            HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'TaskEquipmentStatus'%20and%20factory_id%20eq%20'" + getLoginInfo().getFactoryId() + "'", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    dismissCustomDialog();
                    if (TextUtils.isEmpty(t) || t.equals("null")) {
                        return;
                    }
                    DataElement jsonArrayElment = new JsonArrayElement(t);
                    for (int i = 0; i < jsonArrayElment.asArrayElement().size(); i++) {
                        //翻译DATA_NAME
                        jsonArrayElment.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                        taskEquipmentStatus.put(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                                DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                    }
                    LogUtils.e("initDataByServe---数量--->" + jsonArrayElment.asArrayElement().size());
                    LogUtils.e("initDataByServe--测试成功--->" + t + "数量---->" + jsonArrayElment.asArrayElement().size());
                    initDataByServeInsite();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    LogUtils.e("initDataByServe--测试成功--->" + errorNo + "--->" + strMsg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                        }
                    });
                }
            });
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"), this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }

    private void initDataByServeInsite() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
//        params.put("Parameter","filter=DataType eq 'TaskEquipmentStatus' and factory_id eq 'GEW'");
        HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'TaskOperatorStatus'%20and%20factory_id%20eq%20'" + getLoginInfo().getFactoryId() + "'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElment = new JsonArrayElement(t);
                for (int i = 0; i < jsonArrayElment.asArrayElement().size(); i++) {
                    //翻译DATA_NAME
                    jsonArrayElment.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                    Equipment_Operator_Status_Name_ID_map.put(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                            DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                        initDatas();
                        //initEvent();
                    }
                });
                LogUtils.e("initDataByServeInsite---数量--->" + jsonArrayElment.asArrayElement().size());
                LogUtils.e("initDataByServeInsite--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                LogUtils.e("initDataByServeInsite--测试成功--->" + errorNo + "--->" + strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                    }
                });
            }
        });
    }

    //翻译资源
    private void translateView() {
        LogUtils.e("任务详情页面的创建人---->" + LocaleUtils.getI18nValue("applicanter"));
        ((TextView)findViewById(R.id.textView13)).setText(LocaleUtils.getI18nValue("applicanter"));
        ((TextView) findViewById(R.id.Main_Person_tag)).setText(LocaleUtils.getI18nValue("mainPerson"));
        textView8 = (TextView) findViewById(R.id.textView8);
        textView8.setText(LocaleUtils.getI18nValue("group_type"));
        ((TextView) findViewById(R.id.target_group_tag)).setText(LocaleUtils.getI18nValue("target_group_tag"));
//        ((TextView)findViewById(R.id.ccc)).setText(LocaleUtils.getI18nValue("task_number"));
        ((TextView) findViewById(R.id.task_ID)).setText(LocaleUtils.getI18nValue("task_number"));
        if((TaskSubClass!=null&&TaskSubClass.equals(Task.UPKEEP)&&TaskClass.equals(Task.MAINTAIN_TASK))||TaskClass.equals(Task.REPAIR_TASK)){
            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("task_number", ((TextView)v).getText().toString());
                    clipboardManager.setPrimaryClip(mClipData);
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("copy_succeeded"),mContext);
                    return false;
                }
            };
            //((TextView) findViewById(R.id.ccc)).setOnLongClickListener(longClickListener);
            ((TextView) findViewById(R.id.task_ID)).setOnLongClickListener(longClickListener);
        }
        ((TextView) findViewById(R.id.textView14)).setText(LocaleUtils.getI18nValue("issue_time"));
        ((TextView) findViewById(R.id.textView15)).setText(LocaleUtils.getI18nValue("start_time"));
        ((TextView) findViewById(R.id.textView16)).setText(LocaleUtils.getI18nValue("task_descripbe"));
        ((TextView) findViewById(R.id.dynomic1_tag)).setText(LocaleUtils.getI18nValue("task_descripbe"));
        ((TextView) findViewById(R.id.dynomic2_tag)).setText(LocaleUtils.getI18nValue("task_descripbe"));
        ((TextView) findViewById(R.id.dynomic3_tag)).setText(LocaleUtils.getI18nValue("task_descr"));
        ((TextView) findViewById(R.id.dynomic4_tag)).setText(LocaleUtils.getI18nValue("task_descr"));
        ((TextView) findViewById(R.id.dynomic5_tag)).setText(LocaleUtils.getI18nValue("task_descr"));
        ((TextView) findViewById(R.id.task_verify_person_tag)).setText(LocaleUtils.getI18nValue("Approver"));
        ((TextView) findViewById(R.id.task_verify_time_tag)).setText(LocaleUtils.getI18nValue("ApproverTime"));
        ((TextView) findViewById(R.id.task_verify_reason_tag)).setText(LocaleUtils.getI18nValue("ApproverReason"));
        LogUtils.e("device_count--->" + LocaleUtils.getI18nValue("device_count"));
//        ((TextView)findViewById(R.id.equipment_name_tag)).setText(LocaleUtils.getI18nValue("device_name_order"));
//        ((TextView)findViewById(R.id.equipment_name)).setText(LocaleUtils.getI18nValue("NoEquipment"));
//        ((TextView)findViewById(R.id.taskOperatorStatus_tag)).setText(LocaleUtils.getI18nValue("participant"));
//        ((TextView)findViewById(R.id.changeTaskOperatorStatus)).setText(LocaleUtils.getI18nValue("changeTaskOperatorStatus"));
//        ((TextView)findViewById(R.id.DeviceHistory)).setText(LocaleUtils.getI18nValue("DeveceHistory"));
        LogUtils.e("DeveceHistory---->" + LocaleUtils.getI18nValue("DeveceHistory"));
        LogUtils.e("dealed_count---->" + LocaleUtils.getI18nValue("dealed_count"));
        ((TextView) findViewById(R.id.textView18)).setText(LocaleUtils.getI18nValue("dealed_count"));
//        ((TextView)findViewById(R.id.textView19)).setText(LocaleUtils.getI18nValue("task_picture"));
        ((TextView) findViewById(R.id.fault_title)).setText(LocaleUtils.getI18nValue("fault_summary"));
        ((TextView) findViewById(R.id.fault_type_tag)).setText(LocaleUtils.getI18nValue("fault_t"));
        ((TextView) findViewById(R.id.fault_description_tag)).setText(LocaleUtils.getI18nValue("fault_d"));
        ((TextView) findViewById(R.id.repair_status_tag)).setText(LocaleUtils.getI18nValue("repair_s"));
        ((TextView) findViewById(R.id.workload_title)).setText(LocaleUtils.getI18nValue("workload_title"));
        ((TextView) findViewById(R.id.workload_tag)).setText(LocaleUtils.getI18nValue("total_workload"));
        LogUtils.e("total_workload---->" + LocaleUtils.getI18nValue("total_workload"));
        ((TextView) findViewById(R.id.task_command)).setText(LocaleUtils.getI18nValue("task_command"));
        ((TextView) findViewById(R.id.response_speed_tag)).setText(LocaleUtils.getI18nValue("response_speed"));
        ((TextView) findViewById(R.id.service_attitude_tag)).setText(LocaleUtils.getI18nValue("service_attitude"));
        ((TextView) findViewById(R.id.repair_speed_tag)).setText(LocaleUtils.getI18nValue("repair_speed"));
        ((TextView) findViewById(R.id.mes)).setText(LocaleUtils.getI18nValue("command_standard"));
        ((Button) findViewById(R.id.submitCommand)).setText(LocaleUtils.getI18nValue("sure"));

        if (moduleType.equals("Property")) {
            ((TextView) findViewById(R.id.et_queryname)).setHint(LocaleUtils.getI18nValue("facility_Name"));
            ((TextView) findViewById(R.id.textView17)).setText(LocaleUtils.getI18nValue("facility_Total"));
            ((TextView) findViewById(R.id.textView14)).setText(LocaleUtils.getI18nValue("create_time"));
            ((TextView) findViewById(R.id.textView15)).setText(LocaleUtils.getI18nValue("receive_time"));

        } else {
            ((TextView) findViewById(R.id.et_queryname)).setHint(LocaleUtils.getI18nValue("EquipmentName"));
            ((TextView) findViewById(R.id.textView17)).setText(LocaleUtils.getI18nValue("device_count"));
        }

        ((Button) findViewById(R.id.btn_search)).setText(LocaleUtils.getI18nValue("but_search"));
        ((TextView) findViewById(R.id.textView98)).setText(LocaleUtils.getI18nValue("dealing_count"));
        ((TextView) findViewById(R.id.textView97)).setText(LocaleUtils.getI18nValue("dealed_count"));
        ((TextView) findViewById(R.id.textView99)).setText(LocaleUtils.getI18nValue("pending_count"));
        ((TextView) findViewById(R.id.textView96)).setText(LocaleUtils.getI18nValue("suspend_count"));

    }

    private void initEvent() {
        Bimp.bmp.clear();
        LogUtils.e("<-----进入初始化事件initEvent--->");
        taskAdapter = new TaskAdapter(datas) {

            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final TaskViewHolder holder;
                //  if (convertView == null) {

                // 嵌入子界面
                convertView = LayoutInflater.from(TaskDetailsActivityNew.this).inflate(R.layout.item_order_details, parent, false);

                if (moduleType.equals("Property")) {
                    ((TextView) convertView.findViewById(R.id.id_group)).setText(LocaleUtils.getI18nValue("facility_Num"));
                    ((TextView) convertView.findViewById(R.id.id_num_device)).setText(LocaleUtils.getI18nValue("facility_Name_1"));
                    ((TextView) convertView.findViewById(R.id.id_status)).setText(LocaleUtils.getI18nValue("facility_status"));

                    ((TextView) convertView.findViewById(R.id.tv_oracleID_input)).setVisibility(View.GONE);
                    ((TextView) convertView.findViewById(R.id.tv_oracleID)).setVisibility(View.GONE);

                } else {
                    ((TextView) convertView.findViewById(R.id.id_group)).setText(LocaleUtils.getI18nValue("device_num"));
                    ((TextView) convertView.findViewById(R.id.id_num_device)).setText(LocaleUtils.getI18nValue("device_name"));
                    ((TextView) convertView.findViewById(R.id.id_status)).setText(LocaleUtils.getI18nValue("facility_status"));
                }

                LogUtils.e("设备状态---->" + LocaleUtils.getI18nValue("equipment_status"));
                ((TextView) convertView.findViewById(R.id.id_status)).setText(LocaleUtils.getI18nValue("equipment_status"));
                ((TextView) convertView.findViewById(R.id.tv_using_department)).setText(LocaleUtils.getI18nValue("machine_use_department"));
                ((TextView) convertView.findViewById(R.id.id_start_time_description)).setText(LocaleUtils.getI18nValue("start_time"));
                ((TextView) convertView.findViewById(R.id.id_end_time_description)).setText(LocaleUtils.getI18nValue("end_time"));
                ((TextView) convertView.findViewById(R.id.textView20)).setText(LocaleUtils.getI18nValue("participant"));



                holder = new TaskViewHolder();

                holder.tv_move_from = (TextView) convertView.findViewById(R.id.tv_move_from);
                holder.tv_movefrom = (TextView) convertView.findViewById(R.id.tv_movefrom);
                holder.rl_equipment = (RelativeLayout) convertView.findViewById(R.id.rl_equipment);
                holder.tv_move_from.setText(LocaleUtils.getI18nValue("MoveFrom"));
                if (TaskClass.equals(Task.MOVE_CAR_TASK)) {
                    holder.tv_move_from.setVisibility(View.VISIBLE);
                    holder.tv_movefrom.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_movefrom.setVisibility(View.GONE);
                    holder.tv_move_from.setVisibility(View.GONE);
                }

                holder.tv_creater = (TextView) convertView.findViewById(R.id.id_participant);
                holder.tv_device_num = (TextView) convertView.findViewById(R.id.tv_device_num_details);
                holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name_details);
                holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_start_time_details);
                holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time_details);
                holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state_details);
                holder.listView = (MyListView) convertView.findViewById(R.id.equipment_opeartor_list);
                holder.tv_oracleID_input = (TextView) convertView.findViewById(R.id.tv_oracleID_input);
                holder.tv_using_department_input = (TextView) convertView.findViewById(R.id.tv_using_department_input);
                if (RootUtil.rootStatus(TaskStatus, 1) && !isTaskHistory) {
                    LogUtils.e("可点击---->");
                    holder.rl_equipment.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            String iccard = DataUtil.isDataElementNull(datas.get(position).get("ICCardID"));
                            String qrcode = DataUtil.isDataElementNull(datas.get(position).get("Equipment_KyID"));
                            LogUtils.e("设备状态---->" + DataUtil.isDataElementNull(datas.get(position).get("Status")) + "---->" + DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_PROCESS_MENU)));


                            if (DataUtil.isDataElementNull(datas.get(position).get("Status")).equals("3")) {
                                if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_PROCESS_MENU)).equals("1") || DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_PROCESS_MENU)).isEmpty()) {
                                    if (!TextUtils.isEmpty(qrcode)) {
                                        LogUtils.e("使用二维码识别---->" + qrcode);
                                        Equipment_ID = qrcode;
                                        addTaskEquipment(qrcode, true);
                                    } else if (!TextUtils.isEmpty(iccard)) {
                                        LogUtils.e("使用条形码识别---->" + iccard);
                                        Equipment_ID = iccard;
                                        addTaskEquipment(iccard, false);
                                    } else {
                                        Toast.makeText(mContext, LocaleUtils.getI18nValue("QRCodeAndICCard_isnull"), Toast.LENGTH_SHORT).show();
                                    }
                                    LogUtils.e("rl_equipment--->长按");
                                }
                            } else if (DataUtil.isDataElementNull(datas.get(position).get("Status")).equals("1")) {
                                if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_COMPLETE_MENU)).equals("1") || DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_COMPLETE_MENU)).isEmpty()) {
                                    if (!TextUtils.isEmpty(qrcode)) {
                                        LogUtils.e("使用二维码识别---->" + qrcode);
                                        Equipment_ID = qrcode;
                                        addTaskEquipment(qrcode, true);
                                    } else if (!TextUtils.isEmpty(iccard)) {
                                        LogUtils.e("使用条形码识别---->" + iccard);
                                        Equipment_ID = iccard;
                                        addTaskEquipment(iccard, false);
                                    } else {
                                        Toast.makeText(mContext, LocaleUtils.getI18nValue("QRCodeAndICCard_isnull"), Toast.LENGTH_SHORT).show();
                                    }
                                    LogUtils.e("rl_equipment--->长按");
                                }
                            } else {
//                                if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_COMPLETE_MENU)).equals("1")||DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_COMPLETE_MENU)).isEmpty()){
                                if (!TextUtils.isEmpty(qrcode)) {
                                    LogUtils.e("使用二维码识别---->" + qrcode);
                                    Equipment_ID = qrcode;
                                    addTaskEquipment(qrcode, true);
                                } else if (!TextUtils.isEmpty(iccard)) {
                                    LogUtils.e("使用条形码识别---->" + iccard);
                                    Equipment_ID = iccard;
                                    addTaskEquipment(iccard, false);
                                } else {
                                    Toast.makeText(mContext, LocaleUtils.getI18nValue("QRCodeAndICCard_isnull"), Toast.LENGTH_SHORT).show();
                                }
                                LogUtils.e("rl_equipment--->长按");
//                                }
                            }
                            return true;
//                            Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(datas.get(position).get("Status")))
                        }
                    });

                } else {
                    LogUtils.e("不可点击---->");
                }
                //      convertView.setTag(holder);
                //  } else {
                //      holder = (TaskViewHolder) convertView.getTag();
                //   }
                //显示设备参与人状态
                if (datas.get(position).get("TaskEquipmentOperatorList") != null &&
                        datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv_creater.setVisibility(View.GONE);
                            final ArrayList<ObjectElement> obj = new ArrayList<>();
                            LogUtils.e("data---->" + datas.toString());
                            LogUtils.e("obj---->" + obj.toString());
                            for (int j = 0; j < datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().size(); j++) {
                                obj.add(datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().get(j).asObjectElement());
                            }
                            if (holder.listView.getAdapter() != null) {
                                ((TaskAdapter) holder.listView.getAdapter()).notifyDataSetChanged();
                            } else {
                                holder.listView.setAdapter(new TaskAdapter(obj) {
                                    @Override
                                    public View getCustomView(View convertView1, int position1, ViewGroup parent1) {
                                        TaskViewHolder holder1;
                                        if (convertView1 == null) {
                                            holder1 = new TaskViewHolder();
                                            convertView1 = LayoutInflater.from(TaskDetailsActivityNew.this).inflate(R.layout.item_equipment_operator_status, parent1, false);
                                            holder1.tv_creater = (TextView) convertView1.findViewById(R.id.operator_name);
                                            holder1.tv_task_state = (TextView) convertView1.findViewById(R.id.operator_status);
                                            convertView1.setTag(holder1);
                                        } else {
                                            holder1 = (TaskViewHolder) convertView1.getTag();
                                        }
                                        holder1.tv_creater.setText(DataUtil.isDataElementNull(obj.get(position1).get("Name")));
                                        holder1.tv_task_state.setText(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(obj.get(position1).get("Status"))));
                                        return convertView1;
                                    }
                                });
                            }
                            ListViewUtility.setListViewHeightBasedOnChildren(holder.listView);
                        }
                    });
                } else {
                    holder.tv_creater.setVisibility(View.VISIBLE);
                    LogUtils.e("测试--->" + LocaleUtils.getI18nValue("no_creater"));
                    holder.tv_creater.setText(LocaleUtils.getI18nValue("no_creater"));
                }
                holder.tv_device_num.setText(DataUtil.isDataElementNull(datas.get(position).get("AssetsID")));
                holder.tv_movefrom.setText(DataUtil.isDataElementNull(datas.get(position).get("MoveFrom")));
                holder.tv_oracleID_input.setText(DataUtil.isDataElementNull(datas.get(position).get("OracleID")));
                holder.tv_using_department_input.setText(DataUtil.isDataElementNull(datas.get(position).get("UseOrganise")));
                holder.tv_device_name.setText(DataUtil.isDataElementNull(datas.get(position).get(Equipment.EQUIPMENT_NAME)));
                //String createTime = LongToDate.longPointDate(datas.get(position).get(Maintain.CREATED_DATE_FIELD_NAME).valueAsLong());
                String createTime = DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("StartTime")));
                holder.tv_create_time.setText(createTime);
                //String endTime = LongToDate.longPointDate(datas.get(position).get(Maintain.MAINTAIN_END_TIME).valueAsLong());

                String equipmentStatus = DataUtil.isDataElementNull(datas.get(position).get("Status"));

                String endTime = "";
                if (STATUS_DONE.equals(equipmentStatus)) {
                    endTime = DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("FinishTime")));
                }
                holder.tv_end_time.setText(endTime);
                //  holder.tv_task_state.setText(equipmentStatus);
                holder.tv_task_state.setText(taskEquipmentStatus.get(equipmentStatus));
                if (TaskSubClass != null && TaskClass != null && TaskStatus == 1 && TaskClass.equals(Task.MAINTAIN_TASK)) {
                    //维护任务且为处理中状态的情况下初始化，点击进入测点列表
                    //crate by jason 2019/3/21 扫描后跳转
                    JumpPostion = position;
                    LogUtils.e("position--->" + position);
                    convertView.setOnClickListener(new AdapterView.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!DataUtil.isDataElementNull(datas.get(position).get("Status")).equals("0")) {
                                LogUtils.e("Clickposition--->" + position);
                                //设备状态为有人参与操作的情况下调用
                                Intent intent = new Intent(mContext, MeasurePointActivityNew.class);
                                intent.putExtra(Task.TASK_ID, taskId.toString());
                                intent.putExtra("TaskStatus", TaskStatus);
                                intent.putExtra("TaskEquipment", datas.get(position).toString());
                                intent.putExtra("isMainPersonInTask", RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()), Main_person_in_charge_Operator_id));
                                intent.putExtra("EquipmentStatus", STATUS_DONE.equals(DataUtil.isDataElementNull(datas.get(position).get("Status"))));
                                intent.putExtra("ModuleType", moduleType);
                                if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                    intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                }
                                startActivity(intent);
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseScanEquipmentCard"), mContext);
                                    }
                                });
                            }
                        }
                    });
                }

                return convertView;
            }

        };

        mListview.setAdapter(taskAdapter);
    }

    /**
     * create by jason 2019/3/20
     * 扫描成功后直接跳转到保养点任务
     */
    private void JumpMeaurePointAcivity(String QRcode) {
        int positon = 0;
        LogUtils.e("datas数据--->" + datas.toString());

        for (int i = 0; i < datas.size(); i++) {
            LogUtils.e("AssetID-->" + datas.get(i).get("AssetsID").toString());
            LogUtils.e("Equipment_ID.toUpperCase()--->" + '"' + Equipment_ID.toUpperCase() + '"');
            if (datas.get(i).get("AssetsID").toString().equals('"' + Equipment_ID.toUpperCase() + '"')) {
                LogUtils.e("获取到Equipment_ID--->" + i);
                positon = i;
                break;
            }

            if (datas.get(i).get("Equipment_KyID").toString().equals('"' + Equipment_ID.toUpperCase() + '"')) {
                LogUtils.e("获取到Equipment_KyID---->" + i);
                positon = i;
                break;
            }

            if (datas.get(i).get("ICCardID").toString().equals('"' + Equipment_ID.toUpperCase() + '"')) {
                LogUtils.e("获取到ICCardID---->" + i);
                positon = i;
                break;
            }

        }
        if (datas.size() > 0 && datas.size() > positon) {
            if (!DataUtil.isDataElementNull(datas.get(positon).get("Status")).equals("0")) {
                //设备状态为有人参与操作的情况下调用
                LogUtils.e("进行跳转到MeasurePointActivityNew--->" + positon);
                Intent intent = new Intent(mContext, MeasurePointActivityNew.class);
                intent.putExtra(Task.TASK_ID, taskId.toString());
                intent.putExtra("TaskStatus", TaskStatus);
                intent.putExtra("TaskEquipment", datas.get(positon).toString());
                intent.putExtra("isMainPersonInTask", RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()), Main_person_in_charge_Operator_id));
                intent.putExtra("EquipmentStatus", STATUS_DONE.equals(DataUtil.isDataElementNull(datas.get(positon).get("Status"))));
                intent.putExtra("ModuleType",moduleType);
                if (TaskSubClass != null && !TaskSubClass.equals("")) {
                    intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                }
                //进入保养点列表后清空标记
                EquipmentStatus_conts = 0;
                startActivity(intent);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseScanEquipmentCard"), mContext);
                    }
                });
            }
        }
    }


    private MyListView TaskOperatorListView;
    private ArrayList<ObjectElement> TaskOperatorList = new ArrayList<>();
    private boolean isGetTaskOperatorListSuccess = false;
    private TaskAdapter TaskOperatorAdapter;

    private void initDatas() {
        //任务有设备
        if (HasTaskEquipment) {
            initEvent();
            getTaskEquipmentFromServerByTaskId();
        } else {
            LogUtils.e("走任务没设备--->" + HasTaskEquipment);
            //任务无设备
            findViewById(R.id.NoEquipmentLayout).setVisibility(View.VISIBLE);
            TaskOperatorListView = (MyListView) findViewById(R.id.taskOperatorStatus);

            LogUtils.e("TaskOperatorList--->" + TaskOperatorList.toString());

            TaskOperatorAdapter = new TaskAdapter(TaskOperatorList) {
                @Override
                public View getCustomView(View convertView, int position1, ViewGroup parent1) {
                    TaskViewHolder holder1;
                    if (convertView == null) {
                        holder1 = new TaskViewHolder();
                        convertView = LayoutInflater.from(TaskDetailsActivityNew.this).inflate(R.layout.item_equipment_operator_status, parent1, false);
                        holder1.tv_creater = (TextView) convertView.findViewById(R.id.operator_name);
                        holder1.tv_task_state = (TextView) convertView.findViewById(R.id.operator_status);
                        convertView.setTag(holder1);
                    } else {
                        holder1 = (TaskViewHolder) convertView.getTag();
                    }
                    holder1.tv_creater.setText(DataUtil.isDataElementNull(TaskOperatorList.get(position1).get("Name")));
                    holder1.tv_task_state.setText(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(TaskOperatorList.get(position1).get("Status"))));
                    LogUtils.e("TaskOperatorList.get(position1)----->" + TaskOperatorList.get(position1).get("Name"));
                    LogUtils.e("Equipment_Operator_Status_Name_ID_map---->" + Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(TaskOperatorList.get(position1).get("Status"))));
                    return convertView;
                }
            };
            TaskOperatorListView.setAdapter(TaskOperatorAdapter);
            getTaskOperatorStatus();
            if (RootUtil.rootStatus(TaskStatus, 1)
                    && getIntent().getStringExtra("FromProcessingFragment") != null) {
                findViewById(R.id.changeTaskOperatorStatus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isGetTaskOperatorListSuccess) {
                            getTaskOperatorStatus();
                            return;
                        }
                        boolean tag = false;
                        int TaskOperator_ID = 0;
                        int TaskOperator_Status = 0;
                        for (int i = 0; i < TaskOperatorList.size(); i++) {
                            if (DataUtil.isDataElementNull(TaskOperatorList.get(i).get(Operator.OPERATOR_ID)).equals(String.valueOf(getLoginInfo().getId()))) {
                                tag = true;
                                TaskOperator_ID = TaskOperatorList.get(i).get("TaskOperator_ID").valueAsInt();
                                TaskOperator_Status = TaskOperatorList.get(i).get("Status").valueAsInt();
                                break;
                            }
                        }
                        if (tag) {
                            if (TaskOperator_Status == 1) {
                                final int TaskOperator_ID1 = TaskOperator_ID;
                                final int TaskOperator_Status1 = TaskOperator_Status;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if((TaskClass.equals(Task.REPAIR_TASK)||(TaskClass.equals(Task.MAINTAIN_TASK)&&TaskSubClass.equals(Task.UPKEEP)))
                                                &&DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.COMPLETEMENUISSHOW)).equals("1")){
                                            if (TaskClass.equals(Task.MAINTAIN_TASK)) {
                                                ChangeEquipmentDialog_YMG dialog = new ChangeEquipmentDialog_YMG(mContext, R.layout.dialog_equipment_status, R.style.MyDialog,
                                                        RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId())
                                                                , Main_person_in_charge_Operator_id),
                                                        true, true, TaskSubClass != null, null, TaskOperator_Status1, 0,moduleType,TaskClass,TaskSubClass);
                                                dialog.setTaskOperatorID(TaskOperator_ID1);
                                                dialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                                    @Override
                                                    public void onsubmit() {
                                                        LogUtils.e("进入状态改变？-->onsubmit");
                                                        getTaskOperatorStatus();
                                                    }
                                                });
                                                dialog.show();
                                            } else {
                                                ChangeEquipmentDialog dialog = new ChangeEquipmentDialog(mContext, R.layout.dialog_equipment_status, R.style.MyDialog,
                                                        RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId())
                                                                , Main_person_in_charge_Operator_id),
                                                        true, true, TaskSubClass != null, null, TaskOperator_Status1, 0, moduleType,TaskClass,TaskSubClass);
                                                dialog.setTaskOperatorID(TaskOperator_ID1);
                                                dialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                                    @Override
                                                    public void onsubmit() {
                                                        LogUtils.e("进入状态改变？-->onsubmit");
                                                        getTaskOperatorStatus();
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        }else{
                                            ToastUtil.showToastLong(LocaleUtils.getI18nValue("CanNotChangeOperatorStatus"), mContext);
                                        }
                                    }
                                });
                                return;
                            }
                            if (TaskClass.equals(Task.MAINTAIN_TASK)) {
                                ChangeEquipmentDialog_YMG dialog = new ChangeEquipmentDialog_YMG(mContext, R.layout.dialog_equipment_status, R.style.MyDialog,
                                        RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId())
                                                , Main_person_in_charge_Operator_id),
                                        true, true, TaskSubClass != null, null, TaskOperator_Status, 0,moduleType,TaskClass,TaskSubClass);
                                dialog.setTaskOperatorID(TaskOperator_ID);
                                dialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                    @Override
                                    public void onsubmit() {
                                        LogUtils.e("进入状态改变？-->onsubmit");
                                        getTaskOperatorStatus();
                                    }
                                });
                                dialog.show();
                            } else {
                                ChangeEquipmentDialog dialog = new ChangeEquipmentDialog(mContext, R.layout.dialog_equipment_status, R.style.MyDialog,
                                        RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId())
                                                , Main_person_in_charge_Operator_id),
                                        true, true, TaskSubClass != null, null, TaskOperator_Status, 0, moduleType,TaskClass,TaskSubClass);
                                dialog.setTaskOperatorID(TaskOperator_ID);
                                dialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                    @Override
                                    public void onsubmit() {
                                        LogUtils.e("进入状态改变？-->onsubmit");
                                        getTaskOperatorStatus();
                                    }
                                });
                                dialog.show();
                            }

                        } else {
                            ChangeTaskOperatorStatus();
                        }
                    }
                });
            } else {
                findViewById(R.id.changeTaskOperatorStatus).setVisibility(View.GONE);
            }
        }
        getTaskAttachmentDataFromServerByTaskId();
    }

    private void initView() {

        mHolder.deviceCountTextView = (TextView) findViewById(R.id.device_count);
        mHolder.dealCountTextView = (TextView) findViewById(R.id.deal_count);
        mHolder.dealCountTextView.setVisibility(View.INVISIBLE);
        findViewById(R.id.textView18).setVisibility(View.INVISIBLE);

        mHolder.processTextView = (TextView) findViewById(R.id.dealing_count);
        mHolder.suspendTextView = (TextView) findViewById(R.id.suspend_count);
        mHolder.finishTextView = (TextView) findViewById(R.id.dealed_count);
        mHolder.pendingTextView = (TextView) findViewById(R.id.pending_count);


        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("task_details"));
        ImageView menuImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
        if (RootUtil.rootStatus(TaskStatus, 1) && !isTaskHistory) {
            LogUtils.e("显示---->menuImageView");
            menuImageView.setVisibility(View.VISIBLE);
            findViewById(R.id.btn_bar_left).setVisibility(View.VISIBLE);
        } else {
            LogUtils.e("隐藏---->menuImageView");
            menuImageView.setVisibility(View.GONE);
        }
        menuImageView.setOnClickListener(this);
        mListview = (ScrollViewWithListView) findViewById(R.id.problem_count);
        //noScrollgridview = (ExpandGridView) findViewById(R.id.picture_containt);
        //noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //{
        JsonObjectElement taskDetail = new JsonObjectElement(TaskDetail);
        createDynamicCtrl(taskDetail);
        ((TextView) findViewById(R.id.task_group)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.ORGANISE_NAME)));
        ((TextView) findViewById(R.id.task_ID)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.TASK_ID)));
//        LogUtils.e("时间转换2020.01.06T10:21:39----->" + DataUtil.utc2Local("2020.01.06T10:21:39"));
        ((TextView) findViewById(R.id.task_start_time)).setText(DataUtil.utc2Local(DataUtil.isDataElementNull(taskDetail.get(Task.START_TIME))));
        ((TextView) findViewById(R.id.task_create_time)).setText(DataUtil.utc2Local(DataUtil.isDataElementNull(taskDetail.get(Task.APPLICANT_TIME))));
        ((TextView) findViewById(R.id.task_creater)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.APPLICANT)));
        ((TextView) findViewById(R.id.task_description)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.TASK_DESCRIPTION)));
        ((TextView) findViewById(R.id.target_group)).setText(DataUtil.isDataElementNull(taskDetail.get("TargetTeam")));

        if (TaskStatus > 0 && taskDetail.get("MainTaskOperator") != null) {
            findViewById(R.id.Main_Person_tag).setVisibility(View.VISIBLE);
            findViewById(R.id.Main_Person).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.Main_Person)).setText(DataUtil.isDataElementNull(taskDetail.get("MainTaskOperator")));
        }
        if (TaskClass != null && TaskClass.equals(Task.MOVE_CAR_TASK)) {
            findViewById(R.id.target_group_tag).setVisibility(View.VISIBLE);
            findViewById(R.id.target_group).setVisibility(View.VISIBLE);
            //在这里显示不同的组别提示信息
            textView8.setText(LocaleUtils.getI18nValue("MoveFrom"));
        }
        String checkStatus = DataUtil.isDataElementNull(taskDetail.get("CheckStatus"));
        if (isTaskHistory) {
            if ("2".equals(checkStatus)
                    || "3".equals(checkStatus)
                    || "3".equals(DataUtil.isDataElementNull(taskDetail.get("Status")))) {
                findViewById(R.id.task_verify_person_tag).setVisibility(View.VISIBLE);
                findViewById(R.id.task_verify_person).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.task_verify_person)).setText(DataUtil.isDataElementNull(taskDetail.get("CheckOperator")));
//                    ((TextView)findViewById(R.id.task_verify_time)).setText(DataUtil.isDataElementNull(taskDetail.get("CheckOperator")));
                if (!"3".equals(checkStatus)) {
                    findViewById(R.id.task_verify_reason_tag).setVisibility(View.VISIBLE);
                    findViewById(R.id.task_verify_reason).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.task_verify_reason)).setText(DataUtil.isDataElementNull(taskDetail.get("Summary")));
                }
            }
        }
        Main_person_in_charge_Operator_id = DataUtil.isDataElementNull(taskDetail.get("MainOperator_ID"));
        // }
        //adapter = new GridAdapter(this);
//        adapter.update1();
        // noScrollgridview.setAdapter(adapter);
        //noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//
//                if (arg2 == dataList.size()) {
//                    if(TaskStatus!=1||isTaskHistory){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyDealingTaskCanAddPhoto"),mContext);
//                            }
//                        });
//                        return;
//                    }
//                    if(dataList.size()>=5){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("pictureNumLimit"),mContext);
//                            }
//                        });
//                        return;
//                    }
//                    new PopupWindows(mContext, noScrollgridview);
//                } else {
//                    ImageView image = (ImageView) arg1.findViewById(R.id.item_grida_image);
//                    imageClick(image);
//
////                    Intent intent = new Intent(mContext,
////                            PhotoActivity.class);
////                    intent.putExtra("ID", arg2);
////                    startActivity(intent);
//                }
//            }
//        });
        // noScrollgridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                if(isTaskHistory){
//                    return true;
//                }
//                //弹出确认删除图片对话框，点击确认后删除图片
//                if(position!=dataList.size()){
//                    new AlertDialog.Builder(mContext).setTitle(LocaleUtils.getI18nValue("makeSureDeletePicture"))
//                            .setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    deletePictureFromServer((String) dataList.get(position).get("TaskAttachment_ID"),dataList.get(position));
//                                }
//                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//                }
//                return true;
//            }
//        });
        popMenuTaskDetail = new PopMenuTaskDetail(this, 310, TaskDetail, TaskClass) {

            @Override
            public void onEventDismiss() {

            }
        };
//        nfcDialog=new NFCDialog(mContext,R.style.MyDialog) {
//            @Override
//            public void dismissAction() {
//                nfcDialogTag=false;
//            }
//
//            @Override
//            public void showAction() {
//                nfcDialogTag=true;
//            }
//        };
        if (mAdapter != null && mAdapter.isEnabled()) {
            popMenuTaskDetail.setHasNFC(true);
        }
        popMenuTaskDetail.setNfcDialog(nfcDialog);
        popMenuTaskDetail.setIs_Main_person_in_charge_Operator_id(RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()), Main_person_in_charge_Operator_id));
        String mTitle = "[{ \"code\": \"" + PopMenuTaskDetail.CREATE_NEW_TASK + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_create_new_task") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.SCAN_QR_CODE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_scan_qr_code") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.FAULT_SUMMARY + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_breakdown_summary") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.SUB_TASK_MANAGE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_sub_task") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.INVITE_HELP + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_invite_assistance") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.TRANSFER_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_transfer_order") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.TASK_COMPLETE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_complete_task") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.STYLE_CHANGE_CANCEL_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_return_task") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.REFRESH + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_refresh") + "\"}]";
        if (TaskClass != null) {
            switch (TaskClass) {
                case Task.MAINTAIN_TASK: {
                    mTitle = "[{ \"code\": \"" + PopMenuTaskDetail.CREATE_NEW_TASK + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_create_new_task") + "\"}," +
                            ((TaskSubClass!=null&&TaskSubClass.equals(Task.UPKEEP))?("{\"code\":\""+PopMenuTaskDetail.SPARE_PAER_REQUEST_CREATE+"\",\"name\":\""+LocaleUtils.getI18nValue("create_spare_parts_requisition")+"\"},"):"")+
                            "{ \"code\": \"" + PopMenuTaskDetail.SCAN_QR_CODE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_scan_qr_code") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.WORKLOAD_INPUT + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_workload_input") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.SUB_TASK_MANAGE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_sub_task") + "\"}," +
//                            "{ \"code\": \""+ PopMenuTaskDetail.INVITE_HELP + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_invite_assistance") + "\"},"+
                            "{ \"code\": \"" + PopMenuTaskDetail.INVITE_HELP + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_invite_assistance") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TRANSFER_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_transfer_order") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TASK_COMPLETE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_complete_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.STYLE_CHANGE_CANCEL_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_return_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.REFRESH + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_refresh") + "\"}]";
                    break;
                }
                case Task.MOVE_CAR_TASK:
                case Task.TRANSFER_MODEL_TASK: {
                    mTitle = "[{ \"code\": \"" + PopMenuTaskDetail.CREATE_NEW_TASK + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_move_car_create_new_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.SCAN_QR_CODE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_move_car_scan_qr_code") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.INVITE_HELP + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_move_car_invite_assistance") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TRANSFER_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_move_car_transfer_order") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TASK_COMPLETE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_move_car_complete_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.STYLE_CHANGE_CANCEL_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_return_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.REFRESH + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_move_car_refresh") + "\"}]";
                    break;
                }
                case Task.GROUP_ARRANGEMENT: {
                    mTitle = "[{ \"code\": \"" + PopMenuTaskDetail.CREATE_NEW_TASK + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_create_new_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.SCAN_QR_CODE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_scan_qr_code") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.WORKLOAD_INPUT + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_workload_input") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.SUB_TASK_MANAGE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_sub_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.INVITE_HELP + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_invite_assistance") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TRANSFER_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_transfer_order") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TASK_COMPLETE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_complete_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.STYLE_CHANGE_CANCEL_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_return_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.REFRESH + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_refresh") + "\"}]";
                    break;
                }
                case Task.REPAIR_TASK:{
                    mTitle = "[{ \"code\": \"" + PopMenuTaskDetail.CREATE_NEW_TASK + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_create_new_task") + "\"}," +
                            "{\"code\":\""+PopMenuTaskDetail.SPARE_PAER_REQUEST_CREATE+"\",\"name\":\""+LocaleUtils.getI18nValue("create_spare_parts_requisition")+"\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.SCAN_QR_CODE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_scan_qr_code") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.FAULT_SUMMARY + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_breakdown_summary") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.SUB_TASK_MANAGE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_sub_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.INVITE_HELP + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_invite_assistance") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TRANSFER_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_transfer_order") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.TASK_COMPLETE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_complete_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.STYLE_CHANGE_CANCEL_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_maintain_return_task") + "\"}," +
                            "{ \"code\": \"" + PopMenuTaskDetail.REFRESH + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_refresh") + "\"}]";
                    break;
                }
                default:
                   /* mTitle= "[{ \"code\": \""+ PopMenuTaskDetail.CREATE_NEW_TASK + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_create_new_task") + "\"},"+
                            "{ \"code\": \""+ PopMenuTaskDetail.SCAN_QR_CODE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_scan_qr_code") + "\"}," +
                            "{ \"code\": \""+ PopMenuTaskDetail.FAULT_SUMMARY + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_breakdown_summary") + "\"},"+
                            "{ \"code\": \""+ PopMenuTaskDetail.SUB_TASK_MANAGE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_sub_task") + "\"},"+
                            "{ \"code\": \""+ PopMenuTaskDetail.INVITE_HELP + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_invite_assistance") + "\"},"+
                            "{ \"code\": \""+ PopMenuTaskDetail.TRANSFER_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_transfer_order") + "\"},"+
                            "{ \"code\": \""+ PopMenuTaskDetail.TASK_COMPLETE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_complete_task") + "\"},"+
                            "{ \"code\": \""+ PopMenuTaskDetail.REFRESH + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_refresh") + "\"}]";*/
                    break;
            }
        }
        JsonArrayElement PopMenuTaskDetailArray = new JsonArrayElement(mTitle);
        popMenuTaskDetail.addItems(PopMenuTaskDetailArray);
        popMenuTaskDetail.setHasEquipment(HasTaskEquipment);
//        //作用:如果是搬车任务的话就走这里  Jason 2020/6/2 上午10:56
        popMenuTaskDetail.setOnTaskCompleteListener(new PopMenuTaskDetail.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete() {
                TaskComplete(null);
            }
        });
        popMenuTaskDetail.setOnTaskDetailRefreshListener(new PopMenuTaskDetail.OnTaskDetailRefreshListener() {
            @Override
            public void onRefresh() {
                if (HasTaskEquipment) {
                    getTaskEquipmentFromServerByTaskId();
                } else {
                    getTaskOperatorStatus();
                }
            }
        });

        if (TaskClass != null && !TaskClass.equals(Task.REPAIR_TASK)) {
            findViewById(R.id.serchDeviceHistory).setVisibility(View.GONE);
        }
        findViewById(R.id.serchDeviceHistory).setOnClickListener(this);
        //维修任务并且是已接单任务的情况下加载并显示故障总结
        initViewWhenTaskClassIsRepairAndTaskStatusIsComplete();

    }

    protected void onRestart() {
//        adapter.update1();
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        int id_click = v.getId();
        if (id_click == R.id.btn_right_action) {
            finish();
        } else if (id_click == R.id.btn_bar_left_action) {
            popMenuTaskDetail.showAsDropDown(v);
        } else if (id_click == R.id.serchDeviceHistory) {
            searchDeviceHistory();
        }
    }


    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private boolean shape;

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return dataList.size() + 1;
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

//        public void setSelectedPosition(int position) {
//            selectedPosition = position;
//        }
//
//        public int getSelectedPosition() {
//            return selectedPosition;
//        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //final int coord = position;
            ViewHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setVisibility(View.VISIBLE);

            if (position == dataList.size()) {
//                holder.image.setImageBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.mipmap.icon_addpic_unfocused));

                // String addImageUrl =  "mipmap://" + R.mipmap.icon_addpic_unfocused;
                String imgUrl = "drawable://" + R.drawable.icon_addpic_unfocused;
                //addImageUrlToDataList(imgUrl);
                imageLoader.displayImage(imgUrl, holder.image, options,
                        animateFirstListener);
            } else {
                String imgUrl = (String) dataList.get(position).get("imageUrl");
                imageLoader.displayImage(imgUrl, holder.image, options,
                        animateFirstListener);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

    }

    private void addImageUrlToDataList(String path, String ID) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("imageUrl", path);
        dataMap.put("TaskAttachment_ID", ID);
        dataList.add(dataList.size(), dataMap);
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            LogUtils.e("弹出PopWindow");

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            bt1.setText(LocaleUtils.getI18nValue("take_photo"));
//            Button bt2 = (Button) view
//                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt3.setText(LocaleUtils.getI18nValue("cancel"));
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });

            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {

        //File dir = new File(mContext.getExternalFilesDir(null) + "/btp/");
        //kingzhang 29211129
        File dir = new File( "/storage/emulated/0/btp/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);

//            // 创建Intent，用于打开手机本地图库选择图片
//            Intent intent1 = new Intent(Intent.ACTION_PICK,
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            // 启动intent打开本地图库
//            startActivityForResult(intent1,TAKE_PICTURE);

        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

    }

    //create by jason 2019/3/21 保存设备编号
    private String Equipment_ID = "";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE: {
                if (resultCode == -1) {
                    //Bimp.drr.add(path);
                    // 获取图库所选图片的uri
                    // 获取图库所选图片的uri
//                    Uri uri = data.getData();
                    LogUtils.e("测试图片路径---->" + path);
                    //将图片地址增加到图片列表
                    Bimp.drr.add(path);
                    String path = Bimp.drr.get(Bimp.max);
                    System.out.println(path);
                    try {
                        Bitmap bm = Bimp.revitionImageSize(path);
                        Bimp.bmp.add(bm);
                        String fileName = path.substring(
                                path.lastIndexOf("/") + 1,
                                path.lastIndexOf("."));
                        FileUtils.saveBitmap(mContext, bm, "" + fileName);
                        Bimp.max += 1;

                        //压缩目录的路径--在saveBitmap方法中写死了的
                        String SDPATH = mContext.getExternalFilesDir(null)
                                + "/btp/formats/";

                        addImageUrlToDataList("file://" + SDPATH + fileName + ".JPEG", "0");
                        if (null != adapter) {
//                            adapter.setData(dataList);
                            adapter.notifyDataSetChanged();
                        }

                    } catch (IOException e) {
                        CrashReport.postCatchedException(e);
                    }


                    //在此上传图片到服务器;
                    submitPictureToServer(path);
                }
                break;
            }
            case Constants.REQUEST_CODE_EXCHANGE_ORDER: {
                if (resultCode == 1) {
                    setResult(2);
                    finish();
                }
                break;
            }
            case Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY: {
                if (resultCode == Constants.RESULT_CODE_CAPTURE_ACTIVITY_TO_TASK_DETAIL) {
                    if (data != null) {
                        String result = data.getStringExtra("result");
                        LogUtils.e("这里是扫描设备后返回的结果--->" + result);
                        if (result != null) {
                            //ToastUtil.showToastLong(result,mContext);
                            Equipment_ID = result;
                            addTaskEquipment(result, true);
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap b
     * @return b
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            CrashReport.postCatchedException(e);
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                CrashReport.postCatchedException(e);
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data b
     * @return b
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void submitPictureToServer(String path) {
        try {
            Bitmap bitmap = Bimp.revitionImageSize(path);
            String base64 = bitmapToBase64(bitmap);
            HttpParams params = new HttpParams();
            JsonObjectElement jsonObjectElement = new JsonObjectElement();
            jsonObjectElement.set(Task.TASK_ID, taskId);
            jsonObjectElement.set("TaskAttachment_ID", 0);
            jsonObjectElement.set("ImgBase64", base64);
            jsonObjectElement.set("AttachmentType", "jpg");
            params.putJsonParams(jsonObjectElement.toJson());
            HttpUtils.post(this, "TaskAttachment", params, new HttpCallback() {
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailSubmitPictureCauseByTimeOut"), mContext);
                }

                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    JsonObjectElement json = new JsonObjectElement(t);
                    if (json.get("Success").valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessSubmitPicture"), mContext);
                        getTaskAttachmentDataFromServerByTaskId();
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailSubmitPicture"), mContext);
                    }
                }
            });
            //上传String
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
    }

    private HashMap<String, ObjectElement> TaskEquipment = new HashMap<>();

    /**
     * 从服务器获取设备绑定任务
     */
    private void getTaskEquipmentFromServerByTaskId() {

        if (null == taskId) {
            return;
        }

        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("task_id", taskId.toString());
        params.put("pageSize", 1000);
        params.put("pageIndex", 1);
        String queryname = et_queryname.getText().toString();//kingzhang add 20210708
        LogUtils.e("获取设备名称--->" + queryname);
        if (queryname != "") {
            params.put("queryName", queryname);
        }
//        String status = et_ststus.getText().toString();//kingzhang add 20210708
//        LogUtils.e("获取设备状态--->"+status);
//        if(status!=""){
//            params.put("status",status);
//        }
        String status = spinner.getSelectedItem().toString();//kingzhang add 20210708
        LogUtils.e("获取设备状态--->" + status);
        if (status != "" && status != "全部") {
            if (status == "待处理")
                status = "0";
            else if (status == "处理中")
                status = "1";
            else if (status == "完成")
                status = "2";
            else
                status = "3";
            params.put("status", status);
        }
        params.put("ModuleType", moduleType);
        LogUtils.e("获取设备任务成功--->" + taskId.toString());
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        HttpUtils.get(mContext, "TaskAPI/GetTaskDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                getTaskEachStatusCount();

                if (t != null) {
                    ArrayElement jsonArrayElement = new JsonArrayElement(t);
                    //resetData
                    datas.clear();
                    int dealDeviceCount = 0;
                    boolean taskComplete = true;
                    TaskDeviceIdList.clear();
                    TaskDeviceID_Name.clear();
                    Task_DeviceId_TaskEquipmentId.clear();
                    Euqipment_ID_STATUS_map.clear();
                    TaskEquipment_OperatorID_Status.clear();
                    if (popMenuTaskDetail != null) {
                        popMenuTaskDetail.setTaskComplete(false);
                        popMenuTaskDetail.setEquipmentNum(jsonArrayElement.size());
                    }
                    LogUtils.e("获取设备任务成功--->" + jsonArrayElement.toString());
                    /////
                    if (jsonArrayElement.size() > 0) {

                        for (int i = 0; i < jsonArrayElement.size(); i++) {
                            if (!DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Status")).equals(STATUS_DONE)) {
                                taskComplete = false;
                            }
                            datas.add(jsonArrayElement.get(i).asObjectElement());
                            //作用:设备信息  Jason 2019/12/10 下午2:43
                            equipmentInfo = jsonArrayElement.get(i).asObjectElement();
                            //TaskEquipment——Map,key为EquipmentID,值为对应Equipment详细信息
                            TaskEquipment.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskEquipment_ID")), jsonArrayElement.get(i).asObjectElement());
                            //EquipmentID列表
                            LogUtils.e("保存TaskDeviceIdList---->" + jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID));
                            TaskDeviceIdList.add(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)));
                            //TaskDeviceID_Name——Map,key为Equipment,值为EquipmentName
                            TaskDeviceID_Name.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                    DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_NAME)));
                            //Task_DeviceId_TaskEquipmentId——Map,key为EuqipmentID,值为在任务的中的TaskEquipmentID
                            Task_DeviceId_TaskEquipmentId.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                    DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskEquipment_ID")));
                            //Euqipment_ID_STATUS_map——Map,key为EquipmentID,值为对应的状态值
                            String equipmentStatus = DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Status"));
                            Euqipment_ID_STATUS_map.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                    equipmentStatus);
                            if (jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList") != null &&
                                    jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().size() > 0) {
                                HashMap<String, Integer> Equipment_OperatorID_Status = new HashMap<>();
                                for (int j = 0; j < jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().size(); j++) {
                                    ObjectElement json = jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().get(j).asObjectElement();
                                    Equipment_OperatorID_Status.put(DataUtil.isDataElementNull(json.get("Operator_ID")), Integer.valueOf(DataUtil.isDataElementNull(json.get("Status"))));
                                }
                                //TaskEquipment_OperatorID_Status——Map,key为EquipemntID,值为Map——key为设备参与人OperatorID,值为状态值
                                TaskEquipment_OperatorID_Status.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                        Equipment_OperatorID_Status);
                            }
                            if (STATUS_DONE.equals(equipmentStatus)) {
                                dealDeviceCount++;
                            }
                        }
                    } else {
                        if (popMenuTaskDetail != null) {
                            //popMenuTaskDetail.setTaskComplete(true);
                            taskComplete = false;
                        }

                    }
                    if (popMenuTaskDetail != null) {
                        popMenuTaskDetail.setTaskComplete(taskComplete);
                    }
                    if (null != taskAdapter) {
//                            adapter.setData(dataList);
                        taskAdapter.notifyDataSetChanged();
                    }

                    deviceCountMap.put("deviceCount", String.valueOf(jsonArrayElement.size()));
                    deviceCountMap.put("dealCount", String.valueOf(dealDeviceCount));
                    //在这里刷新设备汇总数据
                    Message message = new Message();
                    message.what = MSG_UPDATE_DEVICE_SUM_INFO;
                    mHandler.sendMessage(message);
                    getEquipmentListFail = false;
                }
                dismissCustomDialog();
                //create by jason 2019/3/21
                if (TaskSubClass != null && TaskClass != null && TaskStatus == 1 && TaskClass.equals(Task.MAINTAIN_TASK)) {
                    //进入保养测点
                    if (EquipmentStatus_conts == 3) {
                        JumpMeaurePointAcivity(Equipment_ID);
                    }
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissCustomDialog();
                        getEquipmentListFail = true;
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetEquipmentList"), mContext);
                    }
                });
            }
        });


    }

    private void getTaskAttachmentDataFromServerByTaskId() {
        if (null == taskId) {
            return;
        }
        HttpParams params = new HttpParams();
        params.put("task_id", taskId.toString());
        HttpUtils.get(mContext, "TaskAPI/GetTaskImgsList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    ArrayElement jsonArrayElement = jsonObjectElement.get("PageData").asArrayElement();
                    if (jsonArrayElement != null && jsonArrayElement.size() > 0) {
                        dataList.clear();
                        for (int i = 0; i < jsonArrayElement.size(); i++) {
                            String path = DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("FileName"));
                            addImageUrlToDataList(path, DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskAttachment_ID")));
                        }
                        //在这里刷新图片列表
                        if (null != adapter) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }


    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
            LogUtils.e("iccardId---->" + iccardID);
//            String iccardID = "70261";
            if (nfcDialogTag) {
                //搬车任务情况下，点击右上角菜单任务完成，显示扫卡对话框，并扫卡的情况下调用
                showCustomDialog(LocaleUtils.getI18nValue("submitData"));
                HttpParams params = new HttpParams();
                JsonObjectElement submitData = new JsonObjectElement();
                submitData.set("ICCardID", iccardID);
                submitData.set(Task.TASK_ID, String.valueOf(taskId));
                params.putJsonParams(submitData.toJson());
                HttpUtils.post(mContext, "TaskOperatorAPI/CheckUserRoleForICCardID", params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        dismissCustomDialog();
                        if (t != null) {
                            JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                            if (jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessToCheckID"), mContext);
                                TaskComplete(jsonObjectElement.get(Data.PAGE_DATA));
                            } else {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailToCheckID"), mContext);
                            }
                        }
                        if (nfcDialog != null && nfcDialog.isShowing()) {
                            nfcDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailToCheckIDCauseByTimeOut"), mContext);
                        dismissCustomDialog();
                    }
                });
                return;
            }
            if (isTaskHistory) {
                return;
            }
            if (TaskStatus != 1) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyDealingTaskCanAddEquipment"), this);
                return;
            }
            if (!HasTaskEquipment) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_add_equipment"), mContext);
                return;
            }
            addTaskEquipment(iccardID, false);
        }
    }

    private ChangeEquipmentDialog changeEquipmentDialog = null;
    private ChangeEquipmentDialog_YMG changeEquipmentDialog_ymg = null;
    private AlertDialog AddEquipmentDialog = null;

    /**
     * 扫描设备成功后，进入绑定设备方法
     *
     * @param iccardID
     * @param isQRCode
     */
    private void addTaskEquipment(String iccardID, boolean isQRCode) {
        LogUtils.e("进入绑定设备的方法--->" + iccardID + "--->" + isQRCode);
        if (getEquipmentListFail) {
            LogUtils.e("进入getTaskEquipmentFromServerByTaskId--->" + getEquipmentListFail);
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("ReGetEquipmentList"), this);
            getTaskEquipmentFromServerByTaskId();
            return;
        }
//20181219 - 为使得非本Factory的设备也能添加，改变原来的取数方式，从原来的从本地数据库查询设备数据，改为从远程服务器获取；
/*
        String rawQuery;
        if(isQRCode){
//            if(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID)==null){
            rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.Equipment_KyID ='" + iccardID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
//            }else {
//                switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID))){
//                    case "1":{
//                        rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.ICCardID ='" + iccardID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
//                        break;
//                    }
//                    case "2":{
//                        rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.AssetsID ='" + iccardID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
//                        break;
//                    }
//                    default:{
//                        rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.AssetsID ='" + iccardID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
//                        break;
//                    }
//                }
//            }
        }else {
            rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.ICCardID ='" + iccardID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
        }
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
*/
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("IDType", isQRCode ? "1" : "2");//1=QRCode Type,2=ID Card Type
        params.put("ModuleType", moduleType);
        String path = "Equipment/GetEquipmentByScan";
        if (moduleType.equals("Property")) {
            path = "PropertyFacility/GetPropertyFacilityEntityBySQL";
            params.put("id", iccardID.trim());
        } else {
            params.put("CodeID", iccardID.trim());
        }
        LogUtils.e("扫描设备上传字段---->" + params.getUrlParams().toString().trim());
        HttpUtils.get(mContext, path, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try {


                    if (t != null) {
                        JsonObjectElement data = null;
                        try {
//                        t = "[{\"TaskEquipmentOperator_ID\":548528,\"TaskEquipment_ID\":561413,\"Equipment_ID\":null,\"EquipmentName\":\"平车\",\"AssetsID\":\"JD1989\",\"Equipment_KyID\":\"371798\",\"MoveFrom\":\"\",\"Task_ID\":668792,\"StartTime\":\"2019.11.19 07:07:29\",\"FinishTime\":\"2019.11.19 07:22:39\",\"Status\":2,\"Name\":\"陆冠中\",\"Factory\":null,\"SetField1\":\"{\\\"Key\\\":\\\"\\\",\\\"Value\\\":\\\"\\\"}\",\"SetField2\":\"{\\\"Key\\\":\\\"\\\",\\\"Value\\\":\\\"\\\"}\",\"SetField3\":\"{\\\"Key\\\":\\\"转款\\\",\\\"Value\\\":\\\"\\\"}\",\"SetField4\":\"{\\\"Key\\\":\\\"\\\",\\\"Value\\\":\\\"\\\"}\",\"SetField5\":\"{\\\"Key\\\":\\\"\\\",\\\"Value\\\":\\\"\\\"}\",\"TaskEquipmentOperatorList\":[{\"Operator_ID\":22438,\"Name\":\"陆冠中\",\"Status\":1,\"Task_ID\":0,\"ApplicantOrg\":null}]}]";
                            data = new JsonObjectElement(t);
                            LogUtils.e("返回设备成功--->" + t);
                        } catch (Exception e) {
                            LogUtils.e("返回设备失败--->" + e.toString() + "t----->" + t);
                            CrashReport.postCatchedException(e);
                        }
                        if (data != null) {
                            //设备信息
                            final ObjectElement objectElement = data;//dataElement.asArrayElement().get(0).asObjectElement();
                            //作用:用于保存设备信息  Jason 2019/12/10 下午2:11
                            equipmentInfo = data;
                            EquipmentID = objectElement.get(Equipment.EQUIPMENT_ID).toString();
                            LogUtils.e("从服务器中请求数据成功---->" + objectElement.toString());
                            LogUtils.e("服务器中的设备Id--->" + EquipmentID);
                            //进行判断，若任务未有该设备号，添加
                            if (!TaskDeviceIdList.contains(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))) {
                                LogUtils.e("进了这里---->TaskDevieIdList--->" + TaskDeviceIdList.size());
                                if (TaskSubClass != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LogUtils.e("出现不可添加设备---->" + LocaleUtils.getI18nValue("can_not_add_equipment") + "--TaskSubClass---->" + TaskSubClass);
                                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("can_not_add_equipment"), mContext);
                                        }
                                    });
                                    return;
                                }
                                //显示设备信息，确认用户是否添加设备
                                final String DialogMessage = LocaleUtils.getI18nValue("AreYouSureToAddEquipment")
                                        + "\n" + LocaleUtils.getI18nValue("equipment_name") + DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_NAME))
                                        + "\n" + LocaleUtils.getI18nValue("equipment_num") + DataUtil.isDataElementNull(objectElement.get(Equipment.ORACLE_ID))
                                        + "\n" + LocaleUtils.getI18nValue("belongGroup") + DataUtil.isDataElementNull(objectElement.get(BaseOrganise.ORGANISENAME));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!canScan) {
                                            LogUtils.e("进入出去重复扫描--->" + canScan);
                                            return;
                                        }
                                        if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.ADD_DEVICE_TIPS)).equals("0")) {
                                            postTaskEquipment(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)));
                                        } else {
                                            //根据后端配置，如果是返回1，就需要提示用户是否要新增设备
                                            if (AddEquipmentDialog == null || !AddEquipmentDialog.isShowing()) {
                                                //显示对话框
                                                LogUtils.e("显示对话框--->" + LocaleUtils.getI18nValue("sure"));
                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                builder.setMessage(DialogMessage);
                                                builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                postTaskEquipment(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)));
                                                            }
                                                        });
                                                    }
                                                }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                AddEquipmentDialog = builder.create();
                                                AddEquipmentDialog.show();
                                            }
                                        }
                                        dismissCustomDialog();
                                    }
                                });
                            } else {
                                LogUtils.e("进行测试");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).equals(STATUS_DONE)
                                                && !TaskClass.equals(Task.MOVE_CAR_TASK)) {
                                            if(!((TaskClass.equals(Task.REPAIR_TASK)||(TaskClass.equals(Task.MAINTAIN_TASK)&&TaskSubClass.equals(Task.UPKEEP)))
                                                    &&DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.COMPLETEMENUISSHOW)).equals("1"))){
                                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("CanNotChangeEquipmentStatus"), mContext);
                                                return;
                                            }
                                        }

                                        //如果操作员未加入该设备，添加为处理中
                                        if (TaskEquipment_OperatorID_Status.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))) != null) {
                                            if (!TaskEquipment_OperatorID_Status.get(
                                                    DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).containsKey(String.valueOf(getLoginInfo().getId()))) {
                                                LogUtils.e("进入添加为处理中--->" + DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)));
                                                postTaskOperatorEquipment(0, DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)),
                                                        Task_DeviceId_TaskEquipmentId.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                                return;
                                            }
                                        } else {
                                            LogUtils.e("进入添加为--->" + DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)));
                                            postTaskOperatorEquipment(0, DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)),
                                                    Task_DeviceId_TaskEquipmentId.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                            return;
                                        }
                                        ////
                                        boolean isOneOperator = false;
                                        if (TaskEquipment_OperatorID_Status.get(
                                                DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).size() == 1) {
                                            isOneOperator = true;
                                        }
                                        int OperatorStatus = TaskEquipment_OperatorID_Status.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).get(String.valueOf(getLoginInfo().getId()));
//                                if(OperatorStatus==1
//                                        &&!RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),
//                                        Main_person_in_charge_Operator_id)){
//                                      runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                            ToastUtil.showToastLong(LocaleUtils.getI18nValue("CanNotChangeOperatorStatus,mContext);
//                                            }
//                                        });
//                                        return;
//                                 }
                                        //  if(changeEquipmentDialog==null) {
                                        if (TaskClass.equals(Task.MAINTAIN_TASK)) {
                                            if (changeEquipmentDialog_ymg == null || !changeEquipmentDialog_ymg.isShowing()) {
                                                LogUtils.e("OperatoStatus---->" + DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_PROCESS_MENU)));
//                                                if (OperatorStatus==2){
//
//                                                }
                                                changeEquipmentDialog_ymg = new ChangeEquipmentDialog_YMG(TaskDetailsActivityNew.this, R.layout.dialog_equipment_status, R.style.MyDialog,
                                                        RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),
                                                                Main_person_in_charge_Operator_id),
                                                        isOneOperator,
                                                        false,
                                                        TaskSubClass != null,
                                                        moduleType.equals("Property") ? DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)):DataUtil.isDataElementNull(objectElement.get(Equipment.ASSETSID)),
                                                        OperatorStatus,
                                                        Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))),
                                                        moduleType,TaskClass,TaskSubClass
                                                );
                                                changeEquipmentDialog_ymg.setDatas(String.valueOf(taskId), DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)),
                                                        Task_DeviceId_TaskEquipmentId.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                                // changeEquipmentDialog.setMainPersonInChargeOperatorId(Main_person_in_charge_Operator_id.equals(String.valueOf(getLoginInfo().getId())));
                                                // changeEquipmentDialog.setMainPersonInChargeOperatorId(RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),Main_person_in_charge_Operator_id));
                                                changeEquipmentDialog_ymg.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                                    @Override
                                                    public void onsubmit() {
                                                        LogUtils.e("进入对话框改变按钮onsubmit");
                                                        getTaskEquipmentFromServerByTaskId();
                                                    }
                                                });
                                                changeEquipmentDialog_ymg.setEquipmentCompleteListener(new EquipmentCompleteListener() {
                                                    @Override
                                                    public void EquipmentComplete(boolean isComplete) {
                                                        LogUtils.e("进入对话框改变按钮EquipmentComplete");
                                                        //若已从某个任务创建的该任务，则不再触发dialog,如调车任务创建的搬车任务
                                                        JsonObjectElement TaskData = new JsonObjectElement(TaskDetail);
                                                        if (!"0".equals(DataUtil.isDataElementNull(TaskData.get("FromTask_ID")))) {
                                                            return;
                                                        }
                                                        //TODO
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                LogUtils.e("进入对话框--->");
                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                                builder.setCancelable(false);
                                                                builder.setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                if (TaskClass != null) {
                                                                    switch (TaskClass) {
                                                                        case Task.MOVE_CAR_TASK: {
                                                                            builder.setMessage(LocaleUtils.getI18nValue("DoYouNeedToCreateAShuntingTask"));
                                                                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    try {
                                                                                        Intent intent = new Intent(mContext, CreateTaskActivity.class);
                                                                                        intent.putExtra(Constants.FLAG_CREATE_SHUNTING_TASK, Constants.FLAG_CREATE_SHUNTING_TASK);
                                                                                        JsonObjectElement jsonObjectElement = new JsonObjectElement();
                                                                                        JsonObjectElement detail = new JsonObjectElement(TaskDetail);
                                                                                        jsonObjectElement.set("Name", DataUtil.isDataElementNull(detail.get("Applicant")));
                                                                                        jsonObjectElement.set("Operator_ID", DataUtil.isDataElementNull(detail.get("ApplicantID")));
                                                                                        jsonObjectElement.set("Organise_ID", DataUtil.isDataElementNull(detail.get("TargetTeam_ID")));
                                                                                        jsonObjectElement.set("Organise_Name", DataUtil.isDataElementNull(detail.get("TargetTeam")));
                                                                                        intent.putExtra("OperatorInfo", jsonObjectElement.toString());
                                                                                        intent.putExtra("EquipmentInfo", objectElement.toJson());
                                                                                        intent.putExtra("FromTask_ID",
                                                                                                String.valueOf(taskId));
                                                                                        startActivity(intent);
                                                                                    } catch (Exception e) {
                                                                                        CrashReport.postCatchedException(e);
                                                                                    }
                                                                                }
                                                                            });
                                                                            builder.show();
                                                                            break;
                                                                        }
                                                                        case Task.TRANSFER_MODEL_TASK: {
                                                                            builder.setMessage(LocaleUtils.getI18nValue("DoYouNeedToCreateACarMovingTask"));
                                                                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    try {
                                                                                        Intent intent = new Intent(mContext, CreateTaskActivity.class);
                                                                                        intent.putExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK, Constants.FLAG_CREATE_CAR_MOVING_TASK);
                                                                                        JsonObjectElement jsonObjectElement = new JsonObjectElement();
                                                                                        JsonObjectElement detail = new JsonObjectElement(TaskDetail);
                                                                                        jsonObjectElement.set("Name", DataUtil.isDataElementNull(detail.get("Applicant")));
                                                                                        jsonObjectElement.set("Operator_ID", DataUtil.isDataElementNull(detail.get("ApplicantID")));
                                                                                        jsonObjectElement.set("Organise_ID", DataUtil.isDataElementNull(detail.get("TaskApplicantOrg_ID")));
                                                                                        jsonObjectElement.set("Organise_Name", DataUtil.isDataElementNull(detail.get("TaskApplicantOrg")));
                                                                                        intent.putExtra("OperatorInfo", jsonObjectElement.toString());
                                                                                        intent.putExtra("EquipmentInfo", objectElement.toJson());
                                                                                        intent.putExtra("FromTask_ID",
                                                                                                String.valueOf(taskId));
                                                                                        startActivity(intent);
                                                                                    } catch (Exception e) {
                                                                                        CrashReport.postCatchedException(e);
                                                                                    }
                                                                                }
                                                                            });
                                                                            builder.show();
                                                                            break;
                                                                        }
                                                                        default: {
                                                                            break;
                                                                        }
                                                                    }

                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                                int EquipmentStatus;
                                                EquipmentStatus = Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                                changeEquipmentDialog_ymg.setEquipemntStatus(EquipmentStatus);
                                                changeEquipmentDialog_ymg.show();
                                                //create by jason 2019/3/21
                                                EquipmentStatus_conts = EquipmentStatus;
                                            }
                                        } else {
                                            if (changeEquipmentDialog == null || !changeEquipmentDialog.isShowing()) {
                                                //作用: 如果是OperaorStatus Jason 2020/5/5 下午12:24
                                                LogUtils.e("OperatoStatus---->" + DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_PROCESS_MENU)));
//                                            if (OperatorStatus==2){
//                                                if (!DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SHOW_PROCESS_MENU)).equals("1")){
//                                                    return;
//                                                }
//                                            }
                                                changeEquipmentDialog = new ChangeEquipmentDialog(TaskDetailsActivityNew.this, R.layout.dialog_equipment_status, R.style.MyDialog,
                                                        RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),
                                                                Main_person_in_charge_Operator_id),
                                                        isOneOperator,
                                                        false,
                                                        TaskSubClass != null,
                                                        DataUtil.isDataElementNull(objectElement.get(Equipment.ASSETSID)),
                                                        OperatorStatus,
                                                        Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))),
                                                        moduleType,TaskClass,TaskSubClass
                                                );
                                                changeEquipmentDialog.setDatas(String.valueOf(taskId), DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)),
                                                        Task_DeviceId_TaskEquipmentId.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                                // changeEquipmentDialog.setMainPersonInChargeOperatorId(Main_person_in_charge_Operator_id.equals(String.valueOf(getLoginInfo().getId())));
                                                // changeEquipmentDialog.setMainPersonInChargeOperatorId(RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),Main_person_in_charge_Operator_id));
                                                changeEquipmentDialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                                    @Override
                                                    public void onsubmit() {
                                                        LogUtils.e("进入对话框改变按钮onsubmit");
                                                        getTaskEquipmentFromServerByTaskId();
                                                    }
                                                });
                                                changeEquipmentDialog.setEquipmentCompleteListener(new EquipmentCompleteListener() {
                                                    @Override
                                                    public void EquipmentComplete(boolean isComplete) {
                                                        LogUtils.e("进入对话框改变按钮EquipmentComplete");
                                                        //若已从某个任务创建的该任务，则不再触发dialog,如调车任务创建的搬车任务
                                                        JsonObjectElement TaskData = new JsonObjectElement(TaskDetail);
                                                        if (!"0".equals(DataUtil.isDataElementNull(TaskData.get("FromTask_ID")))) {
                                                            return;
                                                        }
                                                        //TODO
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                LogUtils.e("进入对话框--->");
                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                                builder.setCancelable(false);
                                                                builder.setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                if (TaskClass != null) {
                                                                    switch (TaskClass) {
                                                                        case Task.MOVE_CAR_TASK: {
                                                                            builder.setMessage(LocaleUtils.getI18nValue("DoYouNeedToCreateAShuntingTask"));
                                                                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    try {
                                                                                        Intent intent = new Intent(mContext, CreateTaskActivity.class);
                                                                                        intent.putExtra(Constants.FLAG_CREATE_SHUNTING_TASK, Constants.FLAG_CREATE_SHUNTING_TASK);
                                                                                        JsonObjectElement jsonObjectElement = new JsonObjectElement();
                                                                                        JsonObjectElement detail = new JsonObjectElement(TaskDetail);
                                                                                        jsonObjectElement.set("Name", DataUtil.isDataElementNull(detail.get("Applicant")));
                                                                                        jsonObjectElement.set("Operator_ID", DataUtil.isDataElementNull(detail.get("ApplicantID")));
                                                                                        jsonObjectElement.set("Organise_ID", DataUtil.isDataElementNull(detail.get("TargetTeam_ID")));
                                                                                        jsonObjectElement.set("Organise_Name", DataUtil.isDataElementNull(detail.get("TargetTeam")));
                                                                                        intent.putExtra("OperatorInfo", jsonObjectElement.toString());
                                                                                        intent.putExtra("EquipmentInfo", objectElement.toJson());
                                                                                        intent.putExtra("FromTask_ID",
                                                                                                String.valueOf(taskId));
                                                                                        startActivity(intent);
                                                                                    } catch (Exception e) {
                                                                                        CrashReport.postCatchedException(e);
                                                                                    }
                                                                                }
                                                                            });
                                                                            builder.show();
                                                                            break;
                                                                        }
                                                                        case Task.TRANSFER_MODEL_TASK: {
                                                                            builder.setMessage(LocaleUtils.getI18nValue("DoYouNeedToCreateACarMovingTask"));
                                                                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    try {
                                                                                        Intent intent = new Intent(mContext, CreateTaskActivity.class);
                                                                                        intent.putExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK, Constants.FLAG_CREATE_CAR_MOVING_TASK);
                                                                                        JsonObjectElement jsonObjectElement = new JsonObjectElement();
                                                                                        JsonObjectElement detail = new JsonObjectElement(TaskDetail);
                                                                                        jsonObjectElement.set("Name", DataUtil.isDataElementNull(detail.get("Applicant")));
                                                                                        jsonObjectElement.set("Operator_ID", DataUtil.isDataElementNull(detail.get("ApplicantID")));
                                                                                        jsonObjectElement.set("Organise_ID", DataUtil.isDataElementNull(detail.get("TaskApplicantOrg_ID")));
                                                                                        jsonObjectElement.set("Organise_Name", DataUtil.isDataElementNull(detail.get("TaskApplicantOrg")));
                                                                                        intent.putExtra("OperatorInfo", jsonObjectElement.toString());
                                                                                        intent.putExtra("EquipmentInfo", objectElement.toJson());
                                                                                        intent.putExtra("FromTask_ID",
                                                                                                String.valueOf(taskId));
                                                                                        startActivity(intent);
                                                                                    } catch (Exception e) {
                                                                                        CrashReport.postCatchedException(e);
                                                                                    }
                                                                                }
                                                                            });
                                                                            builder.show();
                                                                            break;
                                                                        }
                                                                        default: {
                                                                            break;
                                                                        }
                                                                    }

                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                                int EquipmentStatus;
                                                EquipmentStatus = Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                                changeEquipmentDialog.setEquipemntStatus(EquipmentStatus);
                                                changeEquipmentDialog.show();
                                                //create by jason 2019/3/21
                                                EquipmentStatus_conts = EquipmentStatus;
                                            }
                                        }
                                    }
                                });
                                dismissCustomDialog();
                            }
                        } else {
                            dismissCustomDialog();
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoEquipmentNum"), mContext);
                        }
                    } else {
                        dismissCustomDialog();
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"), mContext);
                    }

                } catch (Exception e) {
                    dismissCustomDialog();
                    LogUtils.e("报错报错---->" + e.toString() + "t---->" + t);
                    CrashReport.postCatchedException(e);
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"), mContext);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("获取数据失败--->" + strMsg + "---->" + errorNo);
                dismissCustomDialog();
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"), mContext);
            }
        });

/*
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement dataElement) {

                if (dataElement != null && dataElement.isArray()
                        && dataElement.asArrayElement().size() > 0) {
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoEquipmentNum"),mContext);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
*/

    }

    private void postTaskEquipment(String equipmentID) {
        canScan = false;
//        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        JsonObjectElement taskEquepment = new JsonObjectElement();
//创建任务提交数据：任务创建人，任务类型“T01”那些，几台号（数组），
        taskEquepment.set(Task.TASK_ID, taskId);
        //若任务未有设备，则输入为0，表示添加
        taskEquepment.set("TaskEquipment_ID", 0);
        //若已有设备，申请状态变更
        taskEquepment.set("Equipment_ID", equipmentID);
        taskEquepment.set("Status", 0);
        params.putJsonParams(taskEquepment.toJson());
        LogUtils.e("保存上传字段---->" + taskEquepment.toJson().toString());
        HttpUtils.post(this, "TaskEquipmentAPI/AddTaskEquipment", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                canScan = true;
                dismissCustomDialog();
                if (t != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                                LogUtils.e("确定添加设备后请求数据成功---->" + jsonObjectElement.toString());
                                if (jsonObjectElement.get(Data.SUCCESS) != null && jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                                    if (TaskClass.equals(Task.MOVE_CAR_TASK)) {
                                        postTaskOperatorEquipment(1, EquipmentID, Task_DeviceId_TaskEquipmentId.get(EquipmentID));
                                    } else {
                                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("AddEquipmentSuccess"), mContext);
                                        //开始获取设备任务
                                        getTaskEquipmentFromServerByTaskId();
                                    }
                                } else {
                                    if (DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()) {
                                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("err_add_task_equipment"), mContext);
                                    } else {
                                        TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                    }
                                }
                            } catch (Exception e) {
                                CrashReport.postCatchedException(e);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                canScan = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTaskEquipmentFromServerByTaskId();
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("err_add_task_equipment"), mContext);
                    }
                });
            }
        });
    }

    // 点击放大图片
    public void imageClick(View v) {
        Bitmap bmp;
        if (v instanceof LinearLayout) {
            LinearLayout tmpV = (LinearLayout) v;
            bmp = ((BitmapDrawable) tmpV.getBackground()).getBitmap();
        } else {
            ImageView image = (ImageView) v;
            bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
        }

        ShowBigImageActivity.saveTmpBitmap(bmp);
        Intent showBigImageIntent = new Intent(TaskDetailsActivityNew.this,
                ShowBigImageActivity.class);

        startActivity(showBigImageIntent);
    }

    //主线程中的handler
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;

            switch (what) {
                case MSG_UPDATE_DEVICE_SUM_INFO: {

                    try {

                        if (mHolder.suspendTextView != null) {
                            mHolder.suspendTextView.setText(String.valueOf(taskStatusCountMap.get("Pause_Num")));
                        }

                        if (mHolder.finishTextView != null) {
                            mHolder.finishTextView.setText(String.valueOf(taskStatusCountMap.get("Finish_Num")));
                        }

                        if (mHolder.pendingTextView != null) {
                            mHolder.pendingTextView.setText(String.valueOf(taskStatusCountMap.get("Pending_Num")));
                        }

                        if (mHolder.processTextView != null) {
                            mHolder.processTextView.setText(String.valueOf(taskStatusCountMap.get("Progress_Num")));
                        }

                        if (mHolder.deviceCountTextView != null) {
                            mHolder.deviceCountTextView.setText(String.valueOf(taskStatusCountMap.get("Total_Num")));
                        }


                        if (mHolder.dealCountTextView != null && mHolder.deviceCountTextView != null) {
                            //设备数数
                            //mHolder.deviceCountTextView.setText(String.valueOf(deviceCountMap.get("deviceCount")));
                            //已处理数量
                            //mHolder.dealCountTextView.setText(String.valueOf(deviceCountMap.get("dealCount")));
                        }
                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                    }

                    break;
                }

            }
        }

    };

    private void deletePictureFromServer(String picture, final Map<String, Object> data) {
        HttpParams params = new HttpParams();
        HttpUtils.post(this, "TaskAttachment/TaskAttachmentDelete?TaskAttachment_ID=" + picture
                , params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        if (t != null) {
                            JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                            if (jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                                dataList.remove(data);
                                adapter.notifyDataSetChanged();
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("deletePictureSuccess"), mContext);
                            } else {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("deletePictureFail"), mContext);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("deletePicture_fail"), mContext);
                        super.onFailure(errorNo, strMsg);
                    }
                });

    }

    private void searchDeviceHistory() {
        if (TaskDeviceIdList != null) {
            if (TaskDeviceIdList.size() == 0) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseAddEquipment"), mContext);
            } else if (TaskDeviceIdList.size() == 1) {
                Intent intent = new Intent(this, EquipmentHistory.class);
                intent.putExtra(Equipment.EQUIPMENT_ID, TaskDeviceIdList.get(0));
                intent.putExtra(Equipment.EQUIPMENT_NAME, TaskDeviceID_Name.get(TaskDeviceIdList.get(0)));
                startActivity(intent);
            } else {
                ArrayList<ObjectElement> s = new ArrayList<>();
                for (int i = 0; i < TaskDeviceIdList.size(); i++) {
                    if (TaskDeviceID_Name.get(TaskDeviceIdList.get(i)) != null) {
                        JsonObjectElement jsonObjectElement = new JsonObjectElement();
                        jsonObjectElement.set(Equipment.EQUIPMENT_NAME, TaskDeviceID_Name.get(TaskDeviceIdList.get(i)));
                        s.add(jsonObjectElement);
                    }
                }
                final EquipmentSummaryDialog equipmentSummaryDialog = new EquipmentSummaryDialog(this, s);
                equipmentSummaryDialog.show();
                equipmentSummaryDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(mContext, EquipmentHistory.class);
                        // intent.putExtra(Equipment.EQUIPMENT_ID,TaskDeviceIdList.get(position));
                        intent.putExtra(Equipment.EQUIPMENT_NAME, DataUtil.isDataElementNull(equipmentSummaryDialog.getList().get(position).get(Equipment.EQUIPMENT_NAME)));
                        startActivity(intent);
                        equipmentSummaryDialog.dismiss();
                    }
                });
            }
        }
    }

    private void postTaskOperatorEquipment(int status, String EquipmentId, final String TaskEquipmentId) {
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        // JsonObjectElement TaskOperatorDataToSubmit=new JsonObjectElement();
        //   TaskOperatorDataToSubmit.set("task_id",Integer.valueOf(TaskId));
        //   TaskOperatorDataToSubmit.set("equipment_id",Integer.valueOf(EquipmentId));
        //   TaskOperatorDataToSubmit.set("TaskEquipment_ID",Integer.valueOf(TaskEquipmentId));
        //   TaskOperatorDataToSubmit.set("status",status);
        //   params.putJsonParams(TaskOperatorDataToSubmit.toJson());
        HttpUtils.post(this, "TaskOperatorAPI/MotifyTaskOperatorStatus?task_id=" + taskId + "&equipment_id=" + EquipmentId + "&status=" + status,
                params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        if (t != null) {
                            JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                            LogUtils.e("获取保养数据成功--->" + jsonObjectElement.toString());
                            if (jsonObjectElement.get("Success").valueAsBoolean()) {
                                getTaskEquipmentFromServerByTaskId();
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessToChangeStatus"), mContext);
                                if (TaskSubClass != null && TaskClass != null && TaskStatus == 1 && TaskClass.equals(Task.MAINTAIN_TASK)) {
                                    LogUtils.e("进入保养点任务---->" + TaskEquipmentId);
                                    Intent intent = new Intent(mContext, MeasurePointActivityNew.class);
                                    intent.putExtra(Task.TASK_ID, taskId.toString());
                                    intent.putExtra("TaskStatus", TaskStatus);
                                    intent.putExtra("TaskEquipment", TaskEquipment.get(TaskEquipmentId).toString());
                                    intent.putExtra("isMainPersonInTask", RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()), Main_person_in_charge_Operator_id));
                                    intent.putExtra("EquipmentStatus", false);
                                    if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                        intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                    }
                                    startActivity(intent);
                                }
                            } else {
                                if (DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).equals("")) {
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("CanNotChangeStatus"), mContext);
                                } else {
                                    TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                }
                                getTaskEquipmentFromServerByTaskId();
                            }
                        }
                        dismissCustomDialog();
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("failToChangeStatus"), mContext);
                        dismissCustomDialog();
                    }
                });
    }

    private void getSummaryFromServer() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        httpParams.put("task_id", String.valueOf(taskId));
        HttpUtils.get(this, "TaskTroubleAPI/GetTaskTroubleList", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    if (jsonObjectElement.get("PageData") != null
                            && jsonObjectElement.get("PageData").asArrayElement().size() > 0) {
                        final ObjectElement faultData = jsonObjectElement.get("PageData").asArrayElement().get(0).asObjectElement();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fault_type.setText(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                fault_description.setText(DataUtil.isDataElementNull(faultData.get("TroubleDescribe")));
                                repair_status.setText(DataUtil.isDataElementNull(faultData.get("MaintainDescribe")));
                            }
                        });
                    }
                    dismissCustomDialog();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }

    private void initViewWhenTaskClassIsRepairAndTaskStatusIsComplete() {
        LogUtils.e("显示任务总结，任务已完成状态");
        fault_type = (TextView) findViewById(R.id.fault_type);
        fault_description = (TextView) findViewById(R.id.fault_description);
        repair_status = (TextView) findViewById(R.id.repair_status);
        //任务是已完成情况下调用，显示任务总结，工作量分配，任务评价模块
        if (TaskStatus >= 2) {
            findViewById(R.id.task_complete).setVisibility(View.VISIBLE);
            if (TaskClass.equals(Task.REPAIR_TASK)) {
                findViewById(R.id.fault_summary).setVisibility(View.VISIBLE);
                getSummaryFromServer();
            } else if (TaskClass.equals(Task.OTHER_TASK)
                    || TaskClass.equals(Task.TRANSFER_MODEL_TASK)
                    || TaskClass.equals(Task.GROUP_ARRANGEMENT)) {
                findViewById(R.id.fault_summary).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.fault_title)).setText(LocaleUtils.getI18nValue("task_summary"));
                ((TextView) findViewById(R.id.fault_description_tag)).setText(LocaleUtils.getI18nValue("task_summary_tag"));
                findViewById(R.id.fault_type_tag).setVisibility(View.GONE);
                findViewById(R.id.fault_type).setVisibility(View.GONE);
                findViewById(R.id.repair_status_tag).setVisibility(View.GONE);
                findViewById(R.id.repair_status).setVisibility(View.GONE);
                getSummaryFromServer();
            }
            //待权限
            //TODO
            //如果角色为非报修人并且为非搬车或者非调车情况下显示工作量分配
            switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_DETAIL_SHOW_WORKLOAD_ACTION))) {
                case "1": {
                    findViewById(R.id.workload).setVisibility(View.GONE);
                    break;
                }
                default: {
                    if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) != 7) {//若为EGM，则无工作量模块
                        if (TaskClass.equals(Task.MOVE_CAR_TASK)
                                || TaskClass.equals(Task.TRANSFER_MODEL_TASK)) {
                            findViewById(R.id.workload).setVisibility(View.GONE);
                        } else {
                            initWorkload();
                        }
                    } else {
                        findViewById(R.id.workload).setVisibility(View.GONE);
                    }
                    break;
                }
            }

//            if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))!=7
//                    && !Factory.FACTORY_EGM.equals(getLoginInfo().getFromFactory())) {//若为EGM，则无工作量模块
//                if ( TaskClass.equals(Task.MOVE_CAR_TASK)
//                        ||TaskClass.equals(Task.TRANSFER_MODEL_TASK)){
//                    findViewById(R.id.workload).setVisibility(View.GONE);
//                }else {
//                    initWorkload();
//                }
//            }else {
//                findViewById(R.id.workload).setVisibility(View.GONE);
//            }
            //非维护任务显示任务评价
            if (TaskClass != null && !TaskClass.equals(Task.MAINTAIN_TASK)) {
                findViewById(R.id.Command_layout).setVisibility(View.VISIBLE);
                initTaskCommand();
            }
        }
    }

    private TaskAdapter WorkloadAdapter;
    private ArrayList<ObjectElement> workloadData = new ArrayList<>();
    private MyListView workloadList;

    private void initWorkload() {
        //workload_num
        //workloadList
        workloadList = (MyListView) findViewById(R.id.workloadList);
        WorkloadAdapter = new TaskAdapter(workloadData) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activity_taskdetail_workload, parent, false);
                    ((TextView) convertView.findViewById(R.id.name_tag)).setText(LocaleUtils.getI18nValue("UserName"));
                    ((TextView) convertView.findViewById(R.id.workload_value_tag)).setText(LocaleUtils.getI18nValue("workload"));
                    holder = new TaskViewHolder();
                    //显示6个内容，组别，报修人，状态，保修时间,开始时间，任务描述
                    holder.tv_group = (TextView) convertView.findViewById(R.id.name);
                    holder.warranty_person = (TextView) convertView.findViewById(R.id.workload_value);

                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //待修改
                holder.tv_group.setText(DataUtil.isDataElementNull(workloadData.get(position).get("OperatorName")));
                String s = String.valueOf((int) (Float.valueOf(DataUtil.isDataElementNull(workloadData.get(position).get("Coefficient"))) * 100)) + "%";
                holder.warranty_person.setText(s);
                return convertView;
            }
        };
        workloadList.setAdapter(WorkloadAdapter);
        getWorkLoadFromServer();
    }

    private void getWorkLoadFromServer() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        httpParams.put("task_id", String.valueOf(taskId));
        HttpUtils.get(this, "TaskWorkload", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                if (t != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                            SetViewData(jsonObjectElement);
                        }
                    });
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

    private void SetViewData(ObjectElement ViewData) {
        String s = DataUtil.isDataElementNull(ViewData.get("Workload")) + LocaleUtils.getI18nValue("hours");
        ((TextView) findViewById(R.id.workload_num)).setText(s);
        if (ViewData.get("TaskOperator") != null && ViewData.get("TaskOperator").asArrayElement().size() > 0) {
            for (int i = 0; i < ViewData.get("TaskOperator").asArrayElement().size(); i++) {
                workloadData.add(ViewData.get("TaskOperator").asArrayElement().get(i).asObjectElement());
            }
            WorkloadAdapter.notifyDataSetChanged();
            ListViewUtility.setListViewHeightBasedOnChildren(workloadList);
        }
    }

    private ArrayList<Integer> response_speed_list = new ArrayList<>();
    private ArrayList<Integer> service_attitude_list = new ArrayList<>();
    private ArrayList<Integer> repair_speed_list = new ArrayList<>();
    private commandAdapter response_speed_adapter, service_attitude_adapter, repair_speed_adapter;
    private HorizontalListView response_speed, service_attitude, repair_speed;
    private HashMap<String, Integer> command = new HashMap<>();
    private int TaskEvaluation_ID = 0;
    private Button btn_solved;
    private Button btn_unsolved;
    private TextView tv_repair_confirm;
    private int isSolved = -1;//1：已解决 0：未解决 -1：为操作

    private void initTaskCommand() {
        //response_speed
        //service_attitude
        //repair_speed
        response_speed = (HorizontalListView) findViewById(R.id.response_speed);
        service_attitude = (HorizontalListView) findViewById(R.id.service_attitude);
        repair_speed = (HorizontalListView) findViewById(R.id.repair_speed);
        btn_solved = (Button) findViewById(R.id.btn_solved);
        btn_unsolved = (Button) findViewById(R.id.btn_unsolved);
        btn_solved.setEnabled(false);
        btn_unsolved.setEnabled(false);
        tv_repair_confirm = (TextView) findViewById(R.id.tv_repair_confirm);
        for (int i = 0; i < 5; i++) {
            response_speed_list.add(0);
            service_attitude_list.add(0);
            repair_speed_list.add(0);
        }
        btn_solved.setText(LocaleUtils.getI18nValue("FaultResolved"));
        btn_unsolved.setText(LocaleUtils.getI18nValue("FailureNotSolve"));
        tv_repair_confirm.setText(LocaleUtils.getI18nValue("RepairConfirmation"));
        try {

            btn_solved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSolved = 1;
                    btn_unsolved.setBackgroundColor(getResources().getColor(R.color.click_item));
                    btn_solved.setBackgroundColor(getResources().getColor(R.color.sovle_bg));
                }
            });

            btn_unsolved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSolved = 0;
                    btn_unsolved.setBackgroundColor(getResources().getColor(R.color.sovle_bg));
                    btn_solved.setBackgroundColor(getResources().getColor(R.color.click_item));
//                CreateNewTask();
                }
            });
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
        command.put("resolution_status", isSolved);
        command.put("response_speed", 0);
        command.put("service_attitude", 0);
        command.put("repair_speed", 0);
        response_speed_adapter = new commandAdapter(this, response_speed_list);
        response_speed.setAdapter(response_speed_adapter);

        service_attitude_adapter = new commandAdapter(this, service_attitude_list);
        service_attitude.setAdapter(service_attitude_adapter);

        repair_speed_adapter = new commandAdapter(this, repair_speed_list);
        repair_speed.setAdapter(repair_speed_adapter);
        //TODO
        //条件1：可评价任务；条件2未评价任务，条件3报修人或者维修工班组长，条件4属于报修人班组
        if (getIntent().getStringExtra("IsEvaluated") != null
                && getIntent().getStringExtra("IsEvaluated").equals("0")
                && TaskStatus >= 2
                && (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY
                || Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) < 5)
            //&&OrganiseList.contains(getLoginInfo().getOrganiseID())
        ) {
            for (int i = 0; i < getLoginInfo().getOrganiseID().split(",").length; i++) {
                if (OrganiseList.contains(getLoginInfo().getOrganiseID().split(",")[i])) {
//                    if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))==RootUtil.ROOT_WARRANTY){
//                        if(TaskDetail!=null&&!TaskDetail.equals("")) {
//                            JsonObjectElement jsonObjectElement = new JsonObjectElement(TaskDetail);
//                            if(DataUtil.isDataElementNull(jsonObjectElement.get(Task.APPLICANT)).equals(getLoginInfo().getName())){
//                                findViewById(R.id.submitCommand).setVisibility(View.VISIBLE);
//                                initListViewOnItemClickEvent();
//                                findViewById(R.id.submitCommand).setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        postTaskCommandToServer();
//                                    }
//                                });
//                                break;
//                            }
//                        }
//                    }else {
                    findViewById(R.id.submitCommand).setVisibility(View.VISIBLE);
                    initListViewOnItemClickEvent();
                    findViewById(R.id.submitCommand).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postTaskCommandToServer();
                        }
                    });
                    break;
                    //}
                }
            }
        }
        //initListViewOnItemClickEvent();
        getTaskCommandFromServer();
    }

    private void getTaskCommandFromServer() {
        HttpParams params = new HttpParams();
        params.put("task_id", String.valueOf(taskId));
        HttpUtils.get(this, "TaskEvaluationAPI/GetTaskEvaluationInfo", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast toast = Toast.makeText(mContext, LocaleUtils.getI18nValue("getCommandFail"), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    LogUtils.e("获取的评价数据--->" + t);
                    JsonObjectElement CommandData = new JsonObjectElement(t);
                    if (CommandData.get("PageData") != null && CommandData.get("PageData").isArray()) {
                        if (CommandData.get("PageData").asArrayElement().size() > 0) {
                            TaskEvaluation_ID = CommandData.get("PageData").asArrayElement().get(0).asObjectElement().get("TaskEvaluation_ID").valueAsInt();
                            ObjectElement objectElement = CommandData.get("PageData").asArrayElement().get(0).asObjectElement();
                            setCommandData(objectElement.get("RespondSpeed").valueAsInt(), "response_speed", response_speed_list, response_speed_adapter);
                            setCommandData(objectElement.get("ServiceAttitude").valueAsInt(), "service_attitude", service_attitude_list, service_attitude_adapter);
                            setCommandData(objectElement.get("MaintainSpeed").valueAsInt(), "repair_speed", repair_speed_list, repair_speed_adapter);
                            try {


                                if (DataUtil.isDataElementNull(objectElement.get("ResolutionStatus")).equals("0")) {
                                    btn_unsolved.setBackgroundColor(getResources().getColor(R.color.sovle_bg));
                                    btn_solved.setBackgroundColor(getResources().getColor(R.color.click_item));
                                } else if (DataUtil.isDataElementNull(objectElement.get("ResolutionStatus")).equals("1")) {
                                    btn_unsolved.setBackgroundColor(getResources().getColor(R.color.click_item));
                                    btn_solved.setBackgroundColor(getResources().getColor(R.color.sovle_bg));
                                } else {
                                    btn_unsolved.setBackgroundColor(getResources().getColor(R.color.click_item));
                                    btn_solved.setBackgroundColor(getResources().getColor(R.color.click_item));
                                }
                            } catch (Exception e) {
                                CrashReport.postCatchedException(e);
                            }
                        }
                    }
                }
            }
        });
    }

    public void setCommandData(int num, String key, final ArrayList<Integer> numList, final commandAdapter cAdapter) {
        command.put(key, num);
        for (int i = 0; i < 5; i++) {
            if (i < num) {
                numList.set(i, 1);
            } else {
                numList.set(i, 0);
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

    private void initListViewOnItemClickEvent() {
        btn_solved.setEnabled(true);
        btn_unsolved.setEnabled(true);
        response_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position + 1, "response_speed", response_speed_list, response_speed_adapter);
            }
        });
        service_attitude.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position + 1, "service_attitude", service_attitude_list, service_attitude_adapter);
            }
        });
        repair_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position + 1, "repair_speed", repair_speed_list, repair_speed_adapter);
            }
        });
    }

    /**
     * 说明：上传任务评价
     * 添加时间：2019/12/2 下午2:23
     * 作者：Jason
     */
    private void postTaskCommandToServer() {
        if (isSolved == -1) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("PleaseSelectFaultStatus"), mContext);
            return;
        }
        //TODO
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        JsonObjectElement submitCommandData = new JsonObjectElement();
        submitCommandData.set(Task.TASK_ID, String.valueOf(taskId));
        submitCommandData.set("RespondSpeed", command.get("response_speed"));
        submitCommandData.set("ServiceAttitude", command.get("service_attitude"));
        submitCommandData.set("MaintainSpeed", command.get("repair_speed"));
        submitCommandData.set("ResolutionStatus", isSolved);
        //若已有，则对应，否则为0
        submitCommandData.set("TaskEvaluation_ID", TaskEvaluation_ID);
        ArrayList<ObjectElement> submiData = new ArrayList<>();
        submiData.add(submitCommandData);
        JsonArrayElement jsonArrayElement = new JsonArrayElement(submiData.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        LogUtils.e("评价上传字段---->" + jsonArrayElement.toJson());
//        params.putJsonParams(submitCommandData.toJson());
        HttpUtils.post(this, "TaskEvaluationAPI/TaskEvaluationList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                LogUtils.e("评价返回数据---->" + t);
                if (t != null) {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("commandSuccess"), mContext);
                    // TaskComplete(iccardID);
                    if (FromFragment != null) {
                        switch (FromFragment) {
                            case "0":
                                setResult(3);
                                break;
                            case "1":
                                setResult(1);
                                break;
                            default:
                                setResult(2);
                                break;
                        }
                    }
                    //作用:如果单未解决的，则显示弹窗提示用户  Jason 2019/12/10 上午9:29
                    if (isSolved == 0) {
                        LogUtils.e("显示对话框");
                        try {
                            //create by jason 2019/3/26 增加判断 已弹过窗后 不再弹窗
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    mContext);
                            builder.setMessage(LocaleUtils.getI18nValue("FaultNotSolveTips"));
                            builder.setPositiveButton(LocaleUtils.getI18nValue("CreateNewTask"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LogUtils.e("确认创建新任务");
                                    dialog.dismiss();
                                    CreateNewTask();
                                }
                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builder.show();
                        } catch (Exception e) {
                            CrashReport.postCatchedException(e);
                        }

                    } else {
                        finish();
                    }

                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"), mContext);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"), mContext);
                dismissCustomDialog();
            }
        });
    }

    /**
     * 说明：跳转到创建任务页面
     * 添加时间：2019/12/9 下午5:40
     * 作者：Jason
     */
    private void CreateNewTask() {
        try {
            Intent intent = new Intent(mContext, CreateTaskActivity.class);
            intent.putExtra("FromTask_ID", String.valueOf(taskId));
            intent.putExtra(Constants.FLAG_FAULT_NOT_SOLVE, Constants.FLAG_FAULT_NOT_SOLVE);
            intent.putExtra("TaskDetail", TaskDetail);
            if (equipmentInfo != null) {
                if (!equipmentInfo.isNull()) {
                    intent.putExtra("EquipmentInfo", equipmentInfo.toJson());
                } else {
                    intent.putExtra("EquipmentInfo", "");
                }
            }

            startActivity(intent);
            finish();
        } catch (Exception e) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCreateTask"), mContext);
        }

    }

    private void getTaskOperatorStatus() {
        LogUtils.e("----getTaskOperatorStatus---");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("task_id", taskId.toString());
        HttpUtils.get(mContext, "TaskOperatorAPI/GetTaskOperatorDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {

                    JsonObjectElement data = new JsonObjectElement(t);
                    LogUtils.e("获取数据成功--->" + data.toString());
                    if (data.get(Data.SUCCESS).valueAsBoolean()) {
                        //TODO
                        //TaskOperatorList.add()
                        //TaskOperatorListView
                        TaskOperatorList.clear();
                        boolean taskComplete = true;
                        for (int i = 0; i < data.get("PageData").asArrayElement().size(); i++) {
                            LogUtils.e("进入TaskOperatorList--->" + data.get("PageData").asArrayElement().toString());
                            TaskOperatorList.add(data.get("PageData").asArrayElement().get(i).asObjectElement());
                            if (data.get("PageData").asArrayElement().get(i).asObjectElement().get("Status").valueAsInt() != 1) {
                                taskComplete = false;
                            }
                        }
                        if (popMenuTaskDetail != null) {
                            popMenuTaskDetail.setTaskComplete(taskComplete);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TaskOperatorAdapter.notifyDataSetChanged();
                                ListViewUtility.setListViewHeightBasedOnChildren(TaskOperatorListView);
                            }
                        });
                        isGetTaskOperatorListSuccess = true;
                    } else {
                        isGetTaskOperatorListSuccess = false;
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetTaskOperatorList"), mContext);
                    }
                } else {
                    isGetTaskOperatorListSuccess = false;
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetTaskOperatorList"), mContext);
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                isGetTaskOperatorListSuccess = false;
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetTaskOperatorListCauseByTimeOut"), mContext);
            }
        });
    }

    private void ChangeTaskOperatorStatus() {
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set("TaskOperator_ID", 0);
        submitData.set("Status", 0);
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(mContext, "TaskOperatorAPI/MotifyTaskOperatorStatusForSimple", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if (t != null) {
                    JsonObjectElement data = new JsonObjectElement(t);
                    if (!data.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("CanNotChangeStatus"), mContext);
                    } else {
                        if (DataUtil.isDataElementNull(data.get("Msg")).equals("")) {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("CanNotChangeStatus"), mContext);
                        } else {
                            TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(data.get("Msg")));
                        }
                    }
                }
                getTaskOperatorStatus();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("failToChangeStatus"), mContext);
                dismissCustomDialog();
            }
        });
    }

    private void TaskComplete(final DataElement dataElement) {
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        JsonObjectElement data = new JsonObjectElement();
        data.set(Task.TASK_ID, String.valueOf(taskId));
        params.putJsonParams(data.toJson());
        HttpUtils.post(this, "TaskAPI/TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t != null) {
                    LogUtils.e("完成任务返回信息--->" + t);
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    if (jsonObjectElement.get("Success") != null &&
                            jsonObjectElement.get("Success").valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("taskComplete"), mContext);
                        if (jsonObjectElement.get("Tag") == null || "1".equals(DataUtil.isDataElementNull(jsonObjectElement.get("Tag")))) {//Tag为1即需要弹出对话框询问用户是否需要创建新任务
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(LocaleUtils.getI18nValue("DoYouNeedToCreateAShuntingTask"));
                            builder.setCancelable(false);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mContext, CusActivity.class);
                                    intent.putExtra(Constants.FLAG_CREATE_SHUNTING_TASK, Constants.FLAG_CREATE_SHUNTING_TASK);
                                    if (dataElement != null) {
                                        intent.putExtra("OperatorInfo", dataElement.toString());
                                    }
                                    intent.putExtra("FromTask_ID",
                                            String.valueOf(taskId));
                                    mContext.startActivity(intent);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mContext.startActivity(new Intent(mContext, CusActivity.class));
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        } else {
                            dismissCustomDialog();
                            mContext.startActivity(new Intent(mContext, CusActivity.class));
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("canNotSubmitTaskComplete"), mContext);
                            }
                        });
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"), mContext);
                    }
                });
            }
        });
    }


    //Raymond Edit Support SRF2021-0434 order by 叶润章
    private void getTaskEachStatusCount() {
        HttpParams params = new HttpParams();
        params.put("task_id", taskId.toString());
        ///API/TaskAPI/GetbillTasklistNum
        HttpUtils.get(mContext, "TaskAPI/GetbillTasklistNum", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    Log.e("jsonObjectElement", "jsonObjectElement" + jsonObjectElement);

                    if (null != jsonObjectElement) {


                        taskStatusCountMap.put("Total_Num", String.valueOf(jsonObjectElement.get("Total_Num")));
                        taskStatusCountMap.put("Progress_Num", String.valueOf(jsonObjectElement.get("Progress_Num")));
                        taskStatusCountMap.put("Pending_Num", String.valueOf(jsonObjectElement.get("Pending_Num")));
                        taskStatusCountMap.put("Pause_Num", String.valueOf(jsonObjectElement.get("Pause_Num")));
                        taskStatusCountMap.put("Finish_Num", String.valueOf(jsonObjectElement.get("Finish_Num")));
                        Log.e("jsonObjectElement", "no  null");

                        //在这里刷新设备汇总数据
                        Message message = new Message();
                        message.what = MSG_UPDATE_DEVICE_SUM_INFO;
                        mHandler.sendMessage(message);


//                        TextView deviceCountTv=(TextView) findViewById(R.id.device_count);
//                        deviceCountTv.setText(jsonObjectElement.get("Total_Num").toString());
//
//                        //处理中
//                        TextView processingTv=(TextView) findViewById(R.id.dealing_count);
//                        //processingTv.setText(jsonObjectElement.get("Progress_Num").toString());
//                        processingTv.setText(String.valueOf(jsonObjectElement.get("Progress_Num")));
//                        //待处理
//                        TextView pendingTv=(TextView) findViewById(R.id.pending_count);
//                        processingTv.setText(jsonObjectElement.get("Pending_Num").toString());
//                        //暂停
//                        TextView suspendTv=(TextView) findViewById(R.id.suspend_count);
//                        processingTv.setText(jsonObjectElement.get("Pause_Num").toString());
//                        //已处理
//                        TextView deadedTv=(TextView) findViewById(R.id.dealed_count);
//                        processingTv.setText(jsonObjectElement.get("Finish_Num").toString());
                    }


                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }

}
