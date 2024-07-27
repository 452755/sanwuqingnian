package com.emms.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.RxVolley;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.http.DefaultRetryPolicy;
import com.datastore_android_sdk.rxvolley.http.VolleyError;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Factory;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.schema.Team;
import com.emms.ui.DateTimePickDialog;
import com.emms.ui.DropEditText;
import com.emms.ui.TimePickerDialog;
import com.emms.util.AnimateFirstDisplayListener;
import com.emms.util.BaseData;
import com.emms.util.Bimp;
import com.emms.util.BuildConfig;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.FileUtils;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.king.zxing.Intents;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.okhttp.OkHttpClient;
import com.tencent.bugly.crashreport.CrashReport;
import com.zxing.android.CaptureActivity;
//import com.zxing.android.view.GoogleCaptureActivity;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.w3c.dom.Text;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static com.emms.R.id.equipment_name;
import static com.emms.R.id.mdropEdit_sew_line;

import com.emms.ui.ExpandGridView;

import pub.devrel.easypermissions.EasyPermissions;
import rx.internal.schedulers.EventLoopsScheduler;

/**
 * Created by jaffer.deng on 2016/6/7.
 */
public class CreateTaskActivity extends NfcActivity implements View.OnClickListener, TimePickerDialog.TimePickerDialogInterface,EasyPermissions.PermissionCallbacks {
    //create by jason 2019/3/28 保存任务类型
    private String TaskType = "";
    private String OldModuleType = "";
    private String moduleType = "";

    private String ORACLE_ID = "";
    private EditText taskStartTime;
    private String initEndDateTime = "";// 初始化结束时间  
    private Context mContext = this;
    private DropEditText module_type, task_type, task_subtype, group, device_name, simple_description, hasEquipment, targetOrganise, mdropEdit_sew_line, mdropEdit_order,equipmentOrganiseFrom,equipmentOrganiseTo,equipmentOrganiseTo_cross,equipmentEmail,equipmentApprove;
    private EditText create_task, device_num, task_description;
    private TextView task_subtype_name_desc;
    private TextView menuSearchTitle;
    private ImageView clearBtn;
    private ImageView receiver_action;//kingzhang 20210414
    private EditText receiver_num;//kingzhang 20210414
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private EditText searchBox;
    private Button btn_sure;
    private AlertDialog mDialog;
    public final static String FORM_TYPE = "formtype";
    public final static String FORM_CONTENT = "content";
    public final static int TASK_TYPE = 1;
    public final static int TASK_SUBTYPE = 2;
    public final static int DEVICE_NAME = 5;
    public final static int GROUP = 4;
    public final static int DEVICE_NUM = 6;
    public final static int SIMPLE_DESCRIPTION = 7;
    public final static int HAS_EQUIPMENT = 9;
    public final static int TARGET_ORGANISE = 10;
    public final static int SEWING_LINE = 11;
    public final static int ORDER = 12;
    public final static int MODULE_TYPE = 13;

    public final static int FACILITY_PARK = 14;
    public final static int FACILITY_AREA = 15;
    public final static int FACILITY_TYPE = 16;
    public final static int FACILITY_NAME = 17;
//kingzhang for srf 202-0106
    //begin
    public final static int TARGET_ORGANISE_Form = 18;
    public final static int TARGET_ORGANISE_To = 19;
    public final static int TARGET_ORGANISE_To_cross = 20;
    public final static int equipment_PeopleInformation_Email = 21;
    public final static int equipment_PeopleInformation_Approve = 22;
    private String TARGET_ORGANISE_Form_ID;
    private String TARGET_ORGANISE_To_ID;
    private String TARGET_ORGANISE_To_cross_ID;
    private String equipment_PeopleInformation_Email_ID;
    private String equipment_PeopleInformation_Approve_ID;

    private ArrayList<ObjectElement> mEquipmentGroup_Email = new ArrayList<>();
    private ArrayList<ObjectElement> mEquipmentGroup_Approve = new ArrayList<>();
    private ArrayList<ObjectElement> mEquipmentOrganise = new ArrayList<>();
    private ArrayList<ObjectElement> mEquipmentOrganise_cross = new ArrayList<>();
    private String equipment_targetOrganiseID;
    private boolean iSCross=false;
    private String FromFactory="";
    //end

    private ArrayList<ObjectElement> mTaskType = new ArrayList<>();
    private ArrayList<ObjectElement> mModuleType = new ArrayList<>();
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> mSubType = new ArrayList<>();
    private ArrayList<ObjectElement> mTeamNamelist = new ArrayList<>();
    private ArrayList<ObjectElement> mDeviceNamelist = new ArrayList<>();
    private ArrayList<ObjectElement> mDeviceNumlist = new ArrayList<>();
    private ArrayList<ObjectElement> mSimpleDescriptionList = new ArrayList<>();
    private ArrayList<ObjectElement> mHasEquipment = new ArrayList<>();
    private ArrayList<ObjectElement> mSewLine = new ArrayList<>();
    private ArrayList<ObjectElement> mOrder = new ArrayList<>();
    private ArrayList<ObjectElement> mTargetGroup = new ArrayList<>();
    private ArrayList<ObjectElement> mGroupArrangeDescriptionList = new ArrayList<>();

    private String FieldValue = "";

    private String list_facility_park_Value = "";
    private String list_facility_area_Value = "";
    private String list_facility_type_Value = "";
    private String list_facility_Name_Value = "";


    private ArrayList<ObjectElement> mList_facility_park = new ArrayList<>();
    private HashMap<String, String> list_facility_park_class = new HashMap<>();

    private ArrayList<ObjectElement> mList_facility_area = new ArrayList<>();
    private HashMap<String, String> list_facility_area_class = new HashMap<>();

    private ArrayList<ObjectElement> mList_facility_type = new ArrayList<>();
    private HashMap<String, String> list_facility_type_class = new HashMap<>();

    private ArrayList<ObjectElement> mList_facility_Name = new ArrayList<>();
    private HashMap<String, String> list_facility_Name_class = new HashMap<>();

    private String creatorId;
    private DrawerLayout mDrawer_layout;
    private int searchtag = 0;
    private String cacheSearchName = "";
    private String teamId = "";
    private String equipmentName = "";
    private String equipmentID = "";
    private String sewingLineID = "";
    private HashMap<String, String> task_type_class = new HashMap<>();
    private HashMap<String, String> module_type_class = new HashMap<>();
    private boolean tag = false;
    private String DeviceName = "";
    private String SimpleDescriptionCode = "";
    private String FromTask_ID;
    private HashMap<String, Integer> HasEquipment_map = new HashMap<>();
    private String IntentTaskSubClass;
    private String IntentTaskItem;
    private String targetOrganiseID;
    private String OperatorInfo = null;//若为从搬车任务或调车任务完成时创建对应任务时有值
    private JsonObjectElement EquipmentInfo;
    private Handler handler;

    private Map<Integer, ObjectElement> dynamicControlData = new HashMap<>();
    private Map<Integer, ObjectElement> dynamicSelectData = new HashMap<>();
    private Map<Integer, JsonObject> dynamicSelectData2 = new HashMap<>();
    private Map<Integer, View> dynamicControl = new HashMap<>();
    private Map<Integer, ArrayList<ObjectElement>> dynamicBusinessData = new HashMap<>();

    private TimePickerDialog mTimePickerDialog;

    //create by jason 2019/3/25 返回新单ID
    private String RelateTask_ID;

    private String isFromMaintain = "false";

    private boolean isCarMove = false;
    private boolean canDelete = true;//根据是否选择组别清空设备，还是扫描不清空设备

    //新的扫描
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";


    private DropEditText list_facility_park, list_facility_area, list_facility_type, list_facility_Name;

    String task = "";

    String oracleId;

    String TaskDetail;//单详细信息
    String teamName;//单详情的里面的部门数据
    String tasddesc_code;//简要描述
    String desc;//备注
    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        boolean easy= EasyPermissions.hasPermissions(this, perms);
        EasyPermissions.requestPermissions(this,"申请文件访问权限",0,perms);

        //基于一个任务创建该任务时FromTask_ID有值，如搬车任务创建调车任务，调车任务创建搬车任务，巡检/保养任务创建异常任务或者是任务详情处创建新任务时
        FromTask_ID = getIntent().getStringExtra("FromTask_ID");

        try {
            if (getIntent().getStringExtra(Constants.FLAG_FAULT_NOT_SOLVE) != null) {
                EquipmentInfo = new JsonObjectElement(getIntent().getStringExtra("EquipmentInfo"));
                //作用:从故障未解决时 跳转过来的单信息  Jason 2019/12/10 下午1:57
                TaskDetail = getIntent().getStringExtra("TaskDetail");
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

        //从搬车任务创建调车任务或者从调车任务创建搬车任务的时候OperatorInfo,EquipmentInfo有值
        if (getIntent().getStringExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK) != null
                || getIntent().getStringExtra(Constants.FLAG_CREATE_SHUNTING_TASK) != null) {
            OperatorInfo = getIntent().getStringExtra("OperatorInfo");
            EquipmentInfo = new JsonObjectElement(getIntent().getStringExtra("EquipmentInfo"));
            LogUtils.e("进入搬车任务---->" + OperatorInfo + "---->" + EquipmentInfo);
        }
        //点巡检或保养创建异常任务的时候TaskSubClass，TaskItem有值
        if (getIntent().getStringExtra("FromMeasurePointActivity") != null) {
            isFromMaintain = "true";
            IntentTaskSubClass = getIntent().getStringExtra("TaskSubClass");
            IntentTaskItem = getIntent().getStringExtra("TaskItem");
            LogUtils.e("获取保养或巡点创建的任务--->" + IntentTaskItem);
            LogUtils.e("从保养点跳转过来的" + getIntent().getStringExtra("TaskSubClass") + "----->" + getIntent().getStringExtra("TaskItem"));
        }
        mTimePickerDialog = new TimePickerDialog(CreateTaskActivity.this);

        TempBillNo = System.currentTimeMillis() + "";
        LogUtils.e("TempBillNo--->" + TempBillNo);


        initData(false);
        initView();
        initSearchView();
        initEvent();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(CreateTaskActivity.this));
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDialog = new AlertDialog.Builder(this).setNeutralButton(LocaleUtils.getI18nValue("warning_message_confirm"), null).create();
//        CrashReport.testJavaCrash();
//        initDynamicCtrlData(0, "");
//        initDynamicCtrlDataByServe("select * from datadictionary where datatype='subpart' and factory_id='gew'");
        //作用:解释任务信息  Jason 2019/12/10 下午2:23
        analysisTaskDetail(TaskDetail);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 将权限的处理交给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    /**
     * 说明：解析任务信息
     * 添加时间：2019/12/10 下午2:23
     * 作者：Jason
     */
    private String assetId;

    private void analysisTaskDetail(String TaskDetail) {
        try {
            if (TaskDetail != null) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement(TaskDetail);
                if (jsonObjectElement.get("TaskClass") != null) {
                    TaskType = jsonObjectElement.get("TaskClass").valueAsString();
                    equipmentID = DataUtil.isDataElementNull(jsonObjectElement.get(Equipment.EQUIPMENT_ID));
                    equipmentName = DataUtil.isDataElementNull(jsonObjectElement.get(Equipment.EQUIPMENT_NAME));
                    device_name.setText(DataUtil.isDataElementNull(jsonObjectElement.get(Equipment.EQUIPMENT_NAME)));
                    device_num.setText(DataUtil.isDataElementNull(jsonObjectElement.get("AssetsIDList")));
                    assetId = DataUtil.isDataElementNull(jsonObjectElement.get("AssetsIDList"));
                    DeviceName = DataUtil.isDataElementNull(jsonObjectElement.get(Equipment.EQUIPMENT_NAME));
                    Operator operator = getLoginInfo();
                    getTeamId(operator);
                    teamName = DataUtil.isDataElementNull(jsonObjectElement.get("TaskApplicantOrg"));
                    LogUtils.e("teamName--->" + teamName);
                    if (!jsonObjectElement.get("TaskDescrCode").isNull()) {
                        tasddesc_code = DataUtil.isDataElementNull(jsonObjectElement.get("TaskDescrCode"));

                    }
                    if (!jsonObjectElement.get("TaskDescr").isNull()) {
                        desc = DataUtil.isDataElementNull(jsonObjectElement.get("TaskDescr"));
                        task_description.setText(desc);
                    }
                    //加载部门
                    for (int i = 0; i < mTeamNamelist.size(); i++) {
                        if (DataUtil.isDataElementNull(mTeamNamelist.get(i).get("TeamName")).equals(teamName)) {
                            group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(i).get("TeamName")));
                            teamId = DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID"));
                        }
                    }
                    LogUtils.e("获取到的mTeamNamelist---->" + mTeamNamelist.size());
                }
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

    }

    private void getmSewingDataFromAPI(String whereJson) {
        HttpParams params = new HttpParams();
        LogUtils.e("getmSewingDataFromAPI--获取数据成功--->" + whereJson + params);
        HttpUtils.getChangeStyle(mContext, "1.1/classes/order/sewingline?order=-sewingline&where=" + whereJson, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                mSewLine.clear();
                if (!Strings.isNullOrEmpty(t)) {
                    LogUtils.e("getmSewingDataFromAPI--获取数据成功--->" + t);
                    JsonObjectElement obj = new JsonObjectElement(t);
                    ObjectElement objEle = obj.getAsObjectElement("data");
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("items").asArrayElement() != null) {
                        ArrayElement items = objEle.get("items").asArrayElement();
                        for (int i = 0; i < items.size(); i++) {
                            JsonObjectElement jsonObj = new JsonObjectElement();
                            jsonObj.set("sewingline", items.get(i).toString().replace("\"", ""));
                            mSewLine.add(jsonObj.asObjectElement());
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("getmSewingDataFromAPI--获取数据失败--->" + errorNo + "---->" + strMsg);
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                dismissCustomDialog();
                Toast.makeText(mContext, LocaleUtils.getI18nValue("init_sewing_sewing_line_error"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMOrderDataFromAPI(String whereJson) {
        LogUtils.e("whereJson--->" + whereJson);
        HttpParams params = new HttpParams();
        HttpUtils.getChangeStyle(mContext, "1.1/classes/order?q=&offset=1&limit=50&oeder=startdate&where=" + whereJson, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                mOrder.clear();
                dismissCustomDialog();
                if (!Strings.isNullOrEmpty(t)) {
                    JsonObjectElement obj = new JsonObjectElement(t);
                    ObjectElement objEle = obj.getAsObjectElement("data");
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("items").asArrayElement() != null) {
                        ArrayElement items = objEle.get("items").asArrayElement();
                        for (int i = 0; i < items.size(); i++) {
                            mOrder.add(items.get(i).asObjectElement());
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                Toast.makeText(mContext, LocaleUtils.getI18nValue("init_sewing_order_error"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSearchView() {
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        clearBtn.setVisibility(View.INVISIBLE);
        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.setHint(LocaleUtils.getI18nValue("hint_search_box"));
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        ((TextView) emptyView.findViewById(R.id.tvNothingFound)).setText(LocaleUtils.getI18nValue("nothing_found"));
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
                mDrawer_layout.closeDrawer(Gravity.RIGHT);
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                clearBtn.setVisibility(View.VISIBLE);
                mResultListView.setVisibility(View.VISIBLE);
                String itemName = mResultAdapter.getItemName();
                ArrayList<ObjectElement> result = search(keyword, itemName);
                if (result == null || result.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    mResultAdapter.changeData(result, itemName);
                }
            }
        });
        mResultAdapter = new ResultListAdapter(this);
        mResultListView.setAdapter(mResultAdapter);

        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                final String searchResult = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                LogUtils.e("searchResult---->" + searchResult + "---searchResult-->" + searchtag);
//                if(searchResult.equals("设施报修")){
//                    imageUpload.setVisibility(View.VISIBLE);
//                }else {
//                    imageUpload.setVisibility(View.INVISIBLE);
//                }
                if (!searchResult.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case TASK_TYPE:

                                    task_type.getmEditText().setText(searchResult);
                                    TaskType = task_type_class.get(searchResult);
                                    LogUtils.e("TaskType---->" + TaskType);
                                    if (moduleType.equals("Property")) {
                                        break;
                                    }
                                    getSubTaskType(searchResult);

                                    getGroupArrangeSimpleDesList();
                                    //若为报修人创建任务，更改任务类型后清除下面所有内容
                                    if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
                                        resetCretor();
                                    }
                                    //如果是转款任务
                                    if (DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE)).equals("T08")) {
                                        adjustOpenTransfer();
                                    } else {
                                        adjustCloseTransfer();
                                    }

                                    //设备借还任务类型 kingzhang for srf 2022-0106
                                    if (DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE)).equals("T09") || DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE)).equals("T10")) {
                                        equipmentOpenTransfer();
                                    } else {
                                        equipmentCloseTransfer();
                                    }

                                    LogUtils.e("进入任务--->" + mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE) + "----->"
                                            + mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE).toString());
                                    task = mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE).valueAsString();

//                                    LogUtils.e("task--->"+task+"---->"+String.valueOf(task));
//                                    if (task.equals("T08")){
//                                        LogUtils.e("进入动态页面隐藏操作");
//                                        view.setVisibility(View.GONE);
//                                    }else{
//                                        view.setVisibility(View.VISIBLE);
//                                    }

                                    if (DataUtil.isDataElementNull(mResultAdapter.getItem(position).
                                            get(DataDictionary.DATA_CODE)).equals(Task.MOVE_CAR_TASK)) {
                                        LogUtils.e("进入搬车任务--->" + mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                        findViewById(R.id.target_group_layout).setVisibility(View.VISIBLE);
                                        ((TextView) findViewById(R.id.organise)).setText("Move From:");
                                        ((TextView) findViewById(R.id.target_organise)).setText("Move To:");
                                        //若创建的是搬车任务，那么修改所属组别列表为全部车间班组
                                        getTeamIdByOrganiseID("ALL", false);
                                    } else {
                                        LogUtils.e("进入其他--->" + SharedPreferenceManager.getUserRoleID(mContext) + "---->" + RootUtil.ROOT_WARRANTY);
                                        findViewById(R.id.target_group_layout).setVisibility(View.GONE);
                                        ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("belongGroup"));
                                        if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
                                            LogUtils.e("进入其他任务--->");
                                            Operator operator = getLoginInfo();
                                            if (null != operator) {
                                                LogUtils.e("operator--->" + operator);
                                                getTeamId(operator);
                                            }
                                        } else {
                                            //维修工才有的功能
                                            ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("ServerTeam"));
                                            LogUtils.e("进入其他任务--->");
                                            Operator operator = getLoginInfo();
                                            if (null != operator) {
                                                LogUtils.e("operator--->" + operator);
                                                getTeamId(operator);
                                            }
                                        }
                                    }


                                    //若为组内安排任务，则修改任务简要描述列表，增加组内安排通用描述，若非，则清除相应描述
                                    if (DataUtil.isDataElementNull(mResultAdapter.getItem(position).
                                            get(DataDictionary.DATA_CODE)).equals(Task.GROUP_ARRANGEMENT)) {
                                        if (mGroupArrangeDescriptionList.size() <= 0) {
                                            getGroupArrangeSimpleDesList();
                                        } else {
                                            if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
                                                mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
                                            }
                                        }
                                    } else {
                                        if (mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
                                            mSimpleDescriptionList.removeAll(mGroupArrangeDescriptionList);
                                        }
                                    }
                                    break;

                                case FACILITY_PARK:
                                    //园区联动获取区域
                                    list_facility_park.getmEditText().setText(searchResult);
                                    list_facility_park_Value = list_facility_park_class.get(searchResult);
                                    FieldValue = "Property_Name";
                                    resetFacilityName(FieldValue);
                                    getRepairsFaclity();
                                    break;
                                case FACILITY_AREA:
                                    //区域联动获取维护项目
                                    list_facility_area.getmEditText().setText(searchResult);
                                    list_facility_area_Value = list_facility_area_class.get(searchResult);
                                    FieldValue = "Property_Type";
                                    resetFacilityName(FieldValue);
                                    getRepairsFaclity();
                                    break;
                                case FACILITY_TYPE:
                                    //区域联动获取设施列表
                                    list_facility_type.getmEditText().setText(searchResult);
                                    list_facility_type_Value = list_facility_type_class.get(searchResult);
                                    FieldValue = "Facility_Name";
                                    resetFacilityName(FieldValue);
                                    getRepairsFaclity();
                                    break;
                                case FACILITY_NAME:
                                    list_facility_Name.getmEditText().setText(searchResult);
                                    list_facility_Name_Value = list_facility_Name_class.get(searchResult);

                                    break;
                                case MODULE_TYPE:
                                    module_type.getmEditText().setText(searchResult);
                                    moduleType = module_type_class.get(searchResult);
                                    if (moduleType != OldModuleType) {
                                        OldModuleType = moduleType;
                                        resetCretor();
                                        resetFacility();
                                        getTaskTypeByServe();

                                        int sxViewIsGone = View.GONE;
                                        int sbViewIsGone = View.VISIBLE;

                                        //设备借还任务类型 kingzhang for srf 2022-0106
                                        equipmentCloseTransfer();

                                        //Property/Machine
                                        //Property = 设施
                                        //Machine = 设备
                                        if (moduleType.equals("Property")) {
                                            sxViewIsGone = View.VISIBLE;
                                            sbViewIsGone = View.GONE;
                                            getDictionaryListByServe("PropertyPark", false);
                                            getDictionaryListByServe("PropertyArea", false);
                                            getDictionaryListByServe("PropertyType", false);
                                            ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("subordinate_departments"));
                                            findViewById(R.id.linear_layout_transfer).setVisibility(View.GONE);
                                            findViewById(R.id.group_id).setVisibility(sxViewIsGone);
                                            findViewById(R.id.organise).setVisibility(sxViewIsGone);
                                            findViewById(R.id.task_description_layout).setVisibility(sxViewIsGone);
                                        } else {

                                            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(CreateTaskActivity.this)) != RootUtil.ROOT_WARRANTY
                                                    && OperatorInfo == null) {
                                                ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("ServerTeam"));
                                            } else {
                                                ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("belongGroup"));
                                            }
                                        }

                                        //让有无设备，设备名称，机器编码,任务描述及描述框那一行消失
                                        findViewById(R.id.linear_layout_hasEquipment).setVisibility(sbViewIsGone);
                                        findViewById(R.id.equipment_name).setVisibility(sbViewIsGone);
                                        findViewById(R.id.equipment_num).setVisibility(sbViewIsGone);
                                        findViewById(R.id.linear_layout_task_description).setVisibility(sbViewIsGone);

//                                        findViewById(R.id.group_id).setVisibility(sxViewIsGone);
//                                        findViewById(R.id.organise).setVisibility(sxViewIsGone);

                                        findViewById(R.id.target_organise).setVisibility(sbViewIsGone);
                                        findViewById(R.id.target_group).setVisibility(sbViewIsGone);
                                        findViewById(R.id.target_group_layout).setVisibility(sbViewIsGone);
                                        //让设施部分展现出来

                                        findViewById(R.id.linear_layout_facility).setVisibility(sxViewIsGone);
                                    }

                                    break;
                                case TASK_SUBTYPE:
                                    task_subtype.getmEditText().setText(searchResult);
//                                    TaskType  = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Task.TASK_TYPE));
                                    break;
                                case GROUP:
                                    teamId = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Team.TEAM_ID));
//                                    if (isCarMove){
//                                        teamId = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(BaseOrganise.ORGANISE_ID));
//                                    }
                                    group.getmEditText().setText(searchResult);
                                    if (canDelete) {
                                        resetTeam();
                                    }


                                    break;
                                case DEVICE_NAME:
                                    equipmentName = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_NAME));
                                    device_name.getmEditText().setText(searchResult);
                                    DeviceName = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_CLASS));
                                    LogUtils.e("点击的DeviceName--->" + DeviceName);
                                    if (!TextUtils.isEmpty(DeviceName)) {
                                        getSimpleDescription(DeviceName);
                                    }
                                    resetDeviceName();
                                    initDeviceNum();
                                    break;
                                case DEVICE_NUM:
                                    device_num.setText(searchResult);
                                    equipmentID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                                    ORACLE_ID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.ORACLE_ID));
                                    LogUtils.e("equipmentID--->" + ORACLE_ID);
                                    getGroupArrangeSimpleDesList();
                                    canDelete = true;
                                    break;
                                case SIMPLE_DESCRIPTION:
                                    simple_description.getmEditText().setText(searchResult);
                                    SimpleDescriptionCode = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;
                                case HAS_EQUIPMENT:
                                    hasEquipment.getmEditText().setText(searchResult);
                                    resetEquipment();
                                    if (HasEquipment_map.get(searchResult) == 0) {
                                        findViewById(equipment_name).setVisibility(View.GONE);
                                        findViewById(R.id.equipment_num).setVisibility(View.GONE);
                                    } else {
                                        findViewById(equipment_name).setVisibility(View.VISIBLE);
                                        findViewById(R.id.equipment_num).setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case TARGET_ORGANISE:
                                    targetOrganiseID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(BaseOrganise.ORGANISE_ID));
                                    targetOrganise.getmEditText().setText(searchResult);
                                    break;
                                    //kingzhang for srf 2022-0106
                                //begin
                                case TARGET_ORGANISE_Form:
                                    TARGET_ORGANISE_Form_ID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(BaseOrganise.ORGANISE_ID_equipment));
                                    equipmentOrganiseFrom.getmEditText().setText(searchResult);
                                    break;
                                case TARGET_ORGANISE_To:
                                    TARGET_ORGANISE_To_ID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(BaseOrganise.ORGANISE_ID_equipment));
                                    equipmentOrganiseTo.getmEditText().setText(searchResult);
                                    FromFactory=searchResult;
                                    GetPeopleInformationByServe_Email();
                                    GetPeopleInformationByServe_Approve();

                                    ((DropEditText) findViewById(R.id.equipment_Email)).setText("");
                                    ((DropEditText) findViewById(R.id.equipment_Approve)).setText("");
                                    equipment_PeopleInformation_Email_ID="";
                                    equipment_PeopleInformation_Approve_ID="";
                                    break;
                                case TARGET_ORGANISE_To_cross:
                                    TARGET_ORGANISE_To_cross_ID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(BaseOrganise.ORGANISE_ID_equipment));
                                    equipmentOrganiseTo_cross.getmEditText().setText(searchResult);
                                    FromFactory=searchResult;
                                    GetPeopleInformationByServe_Email();
                                    GetPeopleInformationByServe_Approve();

                                    ((DropEditText) findViewById(R.id.equipment_Email)).setText("");
                                    ((DropEditText) findViewById(R.id.equipment_Approve)).setText("");
                                    equipment_PeopleInformation_Email_ID="";
                                    equipment_PeopleInformation_Approve_ID="";

                                    break;
                                case equipment_PeopleInformation_Email:
                                    String Email_ID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(BaseOrganise.PeopleInfor_Id_equipment));
                                    String emailData= equipmentEmail.getText();
                                    if(emailData.isEmpty()){
                                        emailData=searchResult;
                                        equipment_PeopleInformation_Email_ID=Email_ID;
                                    }else
                                    {
                                        emailData= emailData+";"+searchResult;
                                        equipment_PeopleInformation_Email_ID=equipment_PeopleInformation_Email_ID+";"+Email_ID;
                                    }
                                    equipmentEmail.getmEditText().setText(emailData);
                                    break;
                                case equipment_PeopleInformation_Approve:
                                    equipment_PeopleInformation_Approve_ID = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(BaseOrganise.PeopleInfor_Id_equipment));
                                    equipmentApprove.getmEditText().setText(searchResult);
                                    break;
                                //end
                                case SEWING_LINE:
                                    mdropEdit_sew_line.getmEditText().setText(searchResult);
                                    mdropEdit_order.getmEditText().setText("");
                                    LogUtils.e("searchResult--->" + searchResult);
                                    checkSewingLineData(searchResult);
                                    break;
                                case ORDER:
                                    mdropEdit_order.getmEditText().setText(searchResult);
                                    break;
                                default: {
                                    ObjectElement selectData = mResultAdapter.getItem(position);
                                    String selectDynamicName = DataUtil.isDataElementNull(selectData.get("DataName"));
                                    dynamicSelectData.put(Integer.valueOf(cacheSearchName), selectData);
                                    View view = dynamicControl.get(Integer.valueOf(cacheSearchName));
                                    ((DropEditText) view.findViewById(R.id.dynomicDropEdit))
                                            .getmEditText().setText(selectDynamicName);
                                }
                            }
                            searchBox.setText("");
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"), mContext);
                }
            }
        });
        clearBtn.setOnClickListener(this);
        searchDataLists = new ArrayList<>();
    }

    /**
     * create by jason
     *
     * @param checkData
     */
    private void checkSewingLineDataFromServe(String checkData) {

        HttpParams params = new HttpParams();
        String name = HttpUtils.toURLEncoded(checkData);

        HttpUtils.post(this, "BaseOrganise/APPGet?Parameter=filter%3Dfromfactory%20eq%20'" + getLoginInfo().getFactoryId() + "'%20and%20OrganiseName%20eq%20'" + name + "'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    LogUtils.e("checkSewingLineDataFromServe-->查出来数据为空");
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null) {
                    if (jsonArrayElement.isArray()) {
                        LogUtils.e("checkSewingLineData-->jsonArrayElement--->" + jsonArrayElement.asArrayElement().toString());
                        if (jsonArrayElement.asArrayElement().size() == 0) {
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        } else {
                            final ObjectElement data = jsonArrayElement.asArrayElement().get(0).asObjectElement();
                            sewingLineID = data.get("Organise_ID").toString();
                        }
                    }
                }
                LogUtils.e("checkSewingLineDataFromServe--数量---->-" + jsonArrayElement.asArrayElement().size());
                LogUtils.e("checkSewingLineDataFromServe--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                LogUtils.e("checkSewingLineDataFromServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    private void checkSewingLineData(String checkData) {
        LogUtils.e("checkData--->" + checkData);

        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
//        getMOrderDataFromAPI("%5B%7B%22_and%22:%5B%7B%22state%22:%7B%22eq%22:2%7D%7D,%7B%22sewingline%22:%7B%22eq%22:%22" + checkData + "%22%7D%7D,%7B%22factory%22:%7B%22eq%22:%22" + SharedPreferenceManager.getFactory(CreateTaskActivity.this) + "%22%7D%7D%5D%7D%5D");
        getMOrderDataFromAPI("%5B%7B%22_and%22:%5B%7B%22state%22:%7B%22in%22:%5B2,10%5D%7D%7D,%7B%22sewingline%22:%7B%22eq%22:%22" + checkData + "%22%7D%7D,%7B%22factory%22:%7B%22eq%22:%22" + SharedPreferenceManager.getFactory(CreateTaskActivity.this) + "%22%7D%7D%5D%7D%5D");
        checkSewingLineDataFromServe(checkData);
        /**
         * 更改为服务端
         * 1.先定义接口，第二写请求接口
         * 2.写请求接口代码
         * 3.数据格式定义
         */
//        String sql = "select Organise_ID from BaseOrganise where OrganiseName = '" + checkData + "'";
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, new StoreCallback() {
//            @Override
//            public void success(final DataElement element, String resource) {
//                if (element != null) {
//                    if (element.isArray()) {
//                        LogUtils.e("checkSewingLineData-->element--->"+element.asArrayElement().toString());
//                        if (element.asArrayElement().size() == 0) {
//                            Message msg = new Message();
//                            msg.what = 1;
//                            handler.sendMessage(msg);
//                        } else {
//                            final ObjectElement data = element.asArrayElement().get(0).asObjectElement();
//                            sewingLineID = data.get("Organise_ID").toString();
//                        }
//                    }
//                }
//            }
//
//
//            public void failure(DatastoreException ex, String resource) {
//                Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
//            }
//        });
//        sewingLineID = checkData;
    }

    private void equipmentOpenTransfer() {
        //让有无设备，设备名称，机器编码,任务描述及描述框那一行消失
        findViewById(R.id.linear_layout_hasEquipment).setVisibility(View.GONE);
        findViewById(R.id.equipment_name).setVisibility(View.GONE);
        findViewById(R.id.equipment_num).setVisibility(View.GONE);
        findViewById(R.id.linear_layout_task_description).setVisibility(View.GONE);
        findViewById(R.id.task_description_layout).setVisibility(View.GONE);
        findViewById(R.id.tvTaskdescription).setVisibility(View.GONE);
        findViewById(R.id.organise).setVisibility(View.GONE);
        findViewById(R.id.group_id).setVisibility(View.GONE);

        for (View entry : dynamicControl.values()) {
            entry.setVisibility(View.GONE);
        }

        //让转款部分展现出来
        findViewById(R.id.equipment_loan).setVisibility(View.VISIBLE);

        try {
            showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
            getmSewingDataFromAPI("%5B%7B%22_and%22%3A%5B%7B%22state%22%3A%7B%22eq%22%3A2%7D%7D%2C%7B%22factory%22%3A%7B%22eq%22%3A%22" + SharedPreferenceManager.getFactory(CreateTaskActivity.this) + "%22%7D%7D%5D%7D%5D");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_sewing_data_error"), Toast.LENGTH_SHORT).show();
        }
    }
    private void equipmentCloseTransfer() {
        //让有无设备，设备名称，机器编码那一行显示
        findViewById(R.id.linear_layout_hasEquipment).setVisibility(View.VISIBLE);
        findViewById(R.id.equipment_name).setVisibility(View.VISIBLE);
        findViewById(R.id.equipment_num).setVisibility(View.VISIBLE);
        findViewById(R.id.linear_layout_task_description).setVisibility(View.VISIBLE);
        findViewById(R.id.task_description_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.tvTaskdescription).setVisibility(View.VISIBLE);
        findViewById(R.id.organise).setVisibility(View.VISIBLE);
        findViewById(R.id.group_id).setVisibility(View.VISIBLE);

        for (View entry : dynamicControl.values()) {
            entry.setVisibility(View.VISIBLE);
        }
        //让转款部分消失
        findViewById(R.id.equipment_loan).setVisibility(View.GONE);
        equipmentClearData();
    }
    private void equipmentClearData() {
        ((DropEditText) findViewById(R.id.equipment_group_from_id)).setText("");
        ((DropEditText) findViewById(R.id.equipment_group_to_id)).setText("");
        ((DropEditText) findViewById(R.id.equipment_group_to_id_cross)).setText("");
        ((DropEditText) findViewById(R.id.equipment_Email)).setText("");
        ((DropEditText) findViewById(R.id.equipment_Approve)).setText("");
        ((EditText) findViewById(R.id.task_equipment_description)).setText("");
        TARGET_ORGANISE_Form_ID="";
        TARGET_ORGANISE_To_cross_ID="";
        TARGET_ORGANISE_To_ID="";
        equipment_PeopleInformation_Email_ID="";
        equipment_PeopleInformation_Approve_ID="";
    }
    //针对转款的视图调整
    private void adjustOpenTransfer() {
        //让有无设备，设备名称，机器编码,任务描述及描述框那一行消失
        findViewById(R.id.linear_layout_hasEquipment).setVisibility(View.GONE);
        findViewById(R.id.equipment_name).setVisibility(View.GONE);
        findViewById(R.id.equipment_num).setVisibility(View.GONE);
        findViewById(R.id.linear_layout_task_description).setVisibility(View.GONE);
        findViewById(R.id.task_description_layout).setVisibility(View.GONE);
        findViewById(R.id.organise).setVisibility(View.GONE);
        findViewById(R.id.group_id).setVisibility(View.GONE);

        //让转款部分展现出来
        findViewById(R.id.linear_layout_transfer).setVisibility(View.VISIBLE);

        try {
            showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
            getmSewingDataFromAPI("%5B%7B%22_and%22%3A%5B%7B%22state%22%3A%7B%22eq%22%3A2%7D%7D%2C%7B%22factory%22%3A%7B%22eq%22%3A%22" + SharedPreferenceManager.getFactory(CreateTaskActivity.this) + "%22%7D%7D%5D%7D%5D");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_sewing_data_error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void adjustCloseTransfer() {
        //让有无设备，设备名称，机器编码那一行显示
        findViewById(R.id.linear_layout_hasEquipment).setVisibility(View.VISIBLE);
        findViewById(R.id.equipment_name).setVisibility(View.VISIBLE);
        findViewById(R.id.equipment_num).setVisibility(View.VISIBLE);
        findViewById(R.id.linear_layout_task_description).setVisibility(View.VISIBLE);
        findViewById(R.id.task_description_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.organise).setVisibility(View.VISIBLE);
        findViewById(R.id.group_id).setVisibility(View.VISIBLE);
        //让转款部分消失
        findViewById(R.id.linear_layout_transfer).setVisibility(View.GONE);

    }

    private ArrayList<ObjectElement> search(String keyword, String tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //初始化事件
    private void initEvent() {
        Operator operator = getLoginInfo();
        if (null != operator) {
            creatorId = String.valueOf(operator.getId());
            resetCretor();
            getTeamId(operator);
        }
        //获取任务类型 基本不用改
//        getTaskType();

        //初始化任务属性列表内容
        initHasEquipmentData();
        //初始化控件点击事件
        initDropSearchView(null, hasEquipment.getmEditText(), LocaleUtils.getI18nValue("IsHasEquipment"), DataDictionary.DATA_NAME,
                HAS_EQUIPMENT, LocaleUtils.getI18nValue("gettingDataPleaseWait"), hasEquipment.getDropImage());
        hasEquipment.getmEditText().setText(DataUtil.isDataElementNull(mHasEquipment.get(0).get(DataDictionary.DATA_NAME)));

        //模块类型 By Leo Ye 2021-08-05
        initDropSearchView(null, module_type.getmEditText(), LocaleUtils.getI18nValue("title_module_type"), DataDictionary.DATA_NAME,
                MODULE_TYPE, LocaleUtils.getI18nValue("getDataFail"), module_type.getDropImage());

        initDropSearchView(null, task_type.getmEditText(), LocaleUtils.getI18nValue("title_search_task_type"), DataDictionary.DATA_NAME,
                TASK_TYPE, LocaleUtils.getI18nValue("getDataFail"), task_type.getDropImage());

        initDropSearchView(null, targetOrganise.getmEditText(), LocaleUtils.getI18nValue("title_target_group"), BaseOrganise.ORGANISENAME,
                TARGET_ORGANISE, LocaleUtils.getI18nValue("getDataFail"), targetOrganise.getDropImage());

        //kingzhang for srf 2022-0106 设备借还
        initDropSearchView(null, equipmentOrganiseFrom.getmEditText(),"Move From", BaseOrganise.ORGANISENAME_equipment,
                TARGET_ORGANISE_Form, LocaleUtils.getI18nValue("getDataFail"), equipmentOrganiseFrom.getDropImage());
        initDropSearchView(null, equipmentOrganiseTo.getmEditText(), "Move To", BaseOrganise.ORGANISENAME_equipment,
                TARGET_ORGANISE_To, LocaleUtils.getI18nValue("getDataFail"), equipmentOrganiseTo.getDropImage());
        initDropSearchView(null, equipmentOrganiseTo_cross.getmEditText(), "Move To", BaseOrganise.ORGANISENAME_equipment_cross,
                TARGET_ORGANISE_To_cross, LocaleUtils.getI18nValue("getDataFail"), equipmentOrganiseTo_cross.getDropImage());

        initDropSearchView(null, equipmentEmail.getmEditText(), LocaleUtils.getI18nValue("mailUser"),BaseOrganise.PeopleInfor_equipment,
                equipment_PeopleInformation_Email, LocaleUtils.getI18nValue("getDataFail"), equipmentEmail.getDropImage());
        initDropSearchView(null, equipmentApprove.getmEditText(), LocaleUtils.getI18nValue("ComfirmMan"), BaseOrganise.PeopleInfor_equipment,
                equipment_PeopleInformation_Approve, LocaleUtils.getI18nValue("getDataFail"), equipmentApprove.getDropImage());


        //Raymond 2021-08-06
        initDropSearchView(null, list_facility_park.getmEditText(), LocaleUtils.getI18nValue("task_facility_park"), DataDictionary.DATA_NAME,
                FACILITY_PARK, LocaleUtils.getI18nValue("getDataFail"), list_facility_park.getDropImage());

        initDropSearchView(null, list_facility_area.getmEditText(), LocaleUtils.getI18nValue("task_facility_area"), DataDictionary.DATA_NAME,
                FACILITY_AREA, LocaleUtils.getI18nValue("getDataFail_Area_list"), list_facility_area.getDropImage());

        initDropSearchView(null, list_facility_type.getmEditText(), LocaleUtils.getI18nValue("task_facility_type"), DataDictionary.DATA_NAME,
                FACILITY_TYPE, LocaleUtils.getI18nValue("getDataFail_MaintainFacility_list"), list_facility_type.getDropImage());

        initDropSearchView(null, list_facility_Name.getmEditText(), LocaleUtils.getI18nValue("task_facility_list"), DataDictionary.DATA_NAME,
                FACILITY_NAME, LocaleUtils.getI18nValue("getDataFail_facility_list"), list_facility_Name.getDropImage());


//        task_type.getmEditText()
//                .addTextChangedListener(
//                        new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                task_subtype.getmEditText().setText("");
//                                getSubTaskType(s.toString());
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable s) {
//                            }
//                        }
//                );
        initDropSearchView(task_type.getmEditText(), task_subtype.getmEditText(),
                LocaleUtils.getI18nValue("title_search_task_subtype"), DataDictionary.DATA_NAME, TASK_SUBTYPE, LocaleUtils.getI18nValue("pleaseSelectTaskClass"), task_subtype.getDropImage());

        initDropSearchView(group.getmEditText(), device_name.getmEditText(),
                LocaleUtils.getI18nValue("title_search_equipment_name"), Equipment.EQUIPMENT_NAME, DEVICE_NAME, LocaleUtils.getI18nValue("pleaseSelectGroup"), device_name.getDropImage());
        LogUtils.e("isCarMove--->" + isCarMove);

        initDropSearchView(create_task, group.getmEditText(),
                LocaleUtils.getI18nValue("title_search_group"), Team.TEAMNAME, GROUP, LocaleUtils.getI18nValue("pleaseScanICcard"), group.getDropImage());

        initDropSearchView(device_name.getmEditText(), device_num,
                LocaleUtils.getI18nValue("title_search_equipment_nun"), Equipment.ORACLE_ID, DEVICE_NUM, LocaleUtils.getI18nValue("pleaseSelectEquipment"), (ImageView) findViewById(R.id.device_num_action));
        initDropSearchView(null, simple_description.getmEditText(), LocaleUtils.getI18nValue("simpleDescription"), "DataName", SIMPLE_DESCRIPTION, LocaleUtils.getI18nValue("NoEquipmentDescription"), simple_description.getDropImage());
        initDropSearchView(null, mdropEdit_sew_line.getmEditText(),
                LocaleUtils.getI18nValue("title_search_sew_line"), "sewingline", SEWING_LINE, LocaleUtils.getI18nValue("pleaseSelectSewingLine"), mdropEdit_sew_line.getDropImage());
        initDropSearchView(null, mdropEdit_order.getmEditText(),
                LocaleUtils.getI18nValue("title_search_order"), "orderno", ORDER, LocaleUtils.getI18nValue("pleaseSelectOrderNo"), mdropEdit_order.getDropImage());
    }

    /**
     * create by jason 2019/04/24
     */
    private void initDeviceNumByServe() {

        HttpParams params = new HttpParams();

        final String filter = HttpUtils.toURLEncoded(equipmentName);
        LogUtils.e("filter---->" + filter);

//        params.put("filter",filter);

//        params.put("equipmentName",equipmentName);
//        params.put("teamId",teamId);

        HttpUtils.post(this, "Equipment/APPGet?filter=filter%3DEquipmentName%20%20eq%20'" + filter + "'%20and%20Organise_ID_use%20eq%20'" + teamId + "'&fields=%22%22", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                mDeviceNumlist.clear();
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    LogUtils.e("initDeviceNum--->" + jsonArrayElement.asArrayElement().toString());
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        if (!DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID)).equals("")) {
                            mDeviceNumlist.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                            LogUtils.e("设备编号---->" + jsonArrayElement.asArrayElement().get(i).asObjectElement().toString());
                        }
                    }
                }
                if (mDeviceNumlist.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoEquipmentNum"), mContext);
                        }
                    });
                }
                try {
                    if (getIntent().getStringExtra(Constants.FLAG_FAULT_NOT_SOLVE) != null) {
                        for (int i = 0; i < mDeviceNumlist.size(); i++) {
                            if (DataUtil.isDataElementNull(mDeviceNumlist.get(i).get(Equipment.ASSETSID)).equals(assetId)) {
                                device_num.setText(assetId);
                                equipmentID = DataUtil.isDataElementNull(mDeviceNumlist.get(i).get(Equipment.EQUIPMENT_ID));
                                ORACLE_ID = DataUtil.isDataElementNull(mDeviceNumlist.get(i).get(Equipment.ORACLE_ID));
                                LogUtils.e("equipmentID--->" + ORACLE_ID);
                                getGroupArrangeSimpleDesList();
                            }
                        }
                    }

                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
                LogUtils.e("initDeviceNumByServe--数量---->-" + jsonArrayElement.asArrayElement().size());
                LogUtils.e("initDeviceNumByServe--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoEquipmentNum"), mContext);
                LogUtils.e("initDeviceNumByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    private void initDeviceNum() {

        initDeviceNumByServe();

//        try {
//            LogUtils.e("equipmentName--->"+equipmentName+"teamId---->"+teamId);
//            //更改为服务端数据来源
//            String rawQuery = "SELECT * FROM Equipment WHERE EquipmentName=" + "'" + equipmentName
//                    + "'" + " AND Organise_ID_use =" + teamId + " order by OracleID asc";
//            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                    EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
//            Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//
//                @Override
//                public void onSuccess(DataElement dataElement) {
//                    mDeviceNumlist.clear();
//                    if (dataElement != null && dataElement.isArray()
//                            && dataElement.asArrayElement().size() > 0) {
//                        LogUtils.e("initDeviceNum--->"+dataElement.asArrayElement().toString());
//                        for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
//                            if (!DataUtil.isDataElementNull(dataElement.asArrayElement().get(i).asObjectElement().get(Equipment.ORACLE_ID)).equals("")) {
//                                mDeviceNumlist.add(dataElement.asArrayElement().get(i).asObjectElement());
//                                LogUtils.e("设备编号---->"+dataElement.asArrayElement().get(i).asObjectElement().toString());
//                            }
//                        }
//                    }
//                    if (mDeviceNumlist.size() == 0) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoEquipmentNum"), mContext);
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable throwable) {
//                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoEquipmentNum"), mContext);
//                }
//            });
//        } catch (Exception e) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoEquipmentNum"), mContext);
//                }
//            });
//        }

    }

    /**
     * create by jason
     */
    private void getDeviceNameByServe() {

//        HttpParams httpParams2=new HttpParams();
//        httpParams2.put("Parameter", "%3F%24filter%3DDataType%20eq%20'TaskClass'%20and%20factory_id%20eq%20'GEW'");
//        HttpUtils.get(this, "DataDictionary", httpParams2, new HttpCallback() {
//            @Override
//            public void onSuccess(final String t) {
//                super.onSuccess(t);
//                LogUtils.e("getDeviceNameByServe3--测试成功--->"+t);
//                if(t!=null&&!"null".equals(t)){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
////                            SetViewData(jsonObjectElement);
//                        }
//                    });
//                }else {
////                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("loading_Fail"),context);
//                }
////                dismissCustomDialog();
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                LogUtils.e("getDeviceNameByServe3--测试失败--->"+errorNo+"----"+strMsg);
////                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
////                dismissCustomDialog();
//            }
//        });
//
//        HttpParams Params=new HttpParams();
//        Params.put("OracleID", "GETPP40002");
//        Params.put("TaskType","T07");
//        HttpUtils.get(this, "DataRelation/GetDescription", Params, new HttpCallback() {
//            @Override
//            public void onSuccess(final String t) {
//                super.onSuccess(t);
//                LogUtils.e("getDeviceNameByServe--测试成功--->"+t);
//                if(t!=null&&!"null".equals(t)){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
////                            SetViewData(jsonObjectElement);
//                        }
//                    });
//                }else {
////                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("loading_Fail"),context);
//                }
////                dismissCustomDialog();
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                LogUtils.e("getDeviceNameByServe--测试失败--->"+errorNo+"----"+strMsg);
////                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
////                dismissCustomDialog();
//            }
//        });

        if (moduleType.equals("Property")) {
            return;
        }

        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        HttpUtils.post(this, "Equipment/APPGet?filter=filter%3DStatus%20eq%200%20and%20%20Organise_ID_use%20eq%20" + teamId + "%20and%20EquipmentName%20nn%20&fields=fields%3Ddistinct%20EquipmentName%2CEquipmentClass", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                mDeviceNamelist.clear();
                DataElement jsonElement = new JsonArrayElement(t);
                if (jsonElement != null && jsonElement.isArray()
                        && jsonElement.asArrayElement().size() > 0) {
                    LogUtils.e("jsonElement--->" + jsonElement.asArrayElement().toString());
                    for (int i = 0; i < jsonElement.asArrayElement().size(); i++) {
                        mDeviceNamelist.add(jsonElement.asArrayElement().get(i)
                                .asObjectElement());
//                            LogUtils.e("设备名称--->"+element.asArrayElement().get(i)
//                                    .asObjectElement());
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoGroupEquipment"), mContext);
                        }
                    });
                }
                LogUtils.e("getDeviceNameByServe--数量---->" + jsonElement.asArrayElement().size());
                LogUtils.e("getDeviceNameByServe--测试成功--->" + t);
                try {
                    if (getIntent().getStringExtra(Constants.FLAG_FAULT_NOT_SOLVE) != null) {
                        for (int i = 0; i < mDeviceNamelist.size(); i++) {

                            if (DataUtil.isDataElementNull(mDeviceNamelist.get(i).get(Equipment.EQUIPMENT_NAME)).equals(equipmentName)) {
                                equipmentName = DataUtil.isDataElementNull(mDeviceNamelist.get(i).get(Equipment.EQUIPMENT_NAME));
                                device_name.getmEditText().setText(DataUtil.isDataElementNull(mDeviceNamelist.get(i).get(Equipment.EQUIPMENT_NAME)));
                                DeviceName = DataUtil.isDataElementNull(mDeviceNamelist.get(i).get(Equipment.EQUIPMENT_CLASS));
                                LogUtils.e("点击的DeviceName--->" + DeviceName);
                                if (!TextUtils.isEmpty(DeviceName)) {
                                    getSimpleDescription(DeviceName);
                                }
                                resetDeviceName();
                                initDeviceNum();
                            }

                        }
                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }

            }

            @Override
            public void onFailure(VolleyError error) {
                dismissCustomDialog();
                LogUtils.e("error--->" + error.getMessage());
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("getDeviceNameByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });


    }

    /**
     * 判断是否是左侧菜单点击还是下拉菜单点击获取
     * isSearchview   true是左侧菜单点击
     */
    private void getDeviceName() {
        //kingzhang for srf 2022-0106
        if (!(TaskType.equals(Task.Lend_TASK)) && !(TaskType.equals(Task.Borrow_TASK))){
            getDeviceNameByServe();
         }
         LogUtils.e("teamId--->" + teamId);
        //更改为服务端数据来源 可能数据格式都要更改。
//        String rawQuery = "select  distinct EquipmentName,EquipmentClass,Equipment_ID from Equipment where Organise_ID_Use =" + teamId + "  and EquipmentName is not null";
//        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
//        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//
//            @Override
//            public void onSuccess(final DataElement element) {
//                System.out.println(element);
//                LogUtils.e("查询设备成功---->"+element.toJson());
//                mDeviceNamelist.clear();
//                try {
//                    if (element != null && element.isArray()
//                            && element.asArrayElement().size() > 0) {
//                        LogUtils.e("getDeviceNameByServe--element--->"+element.asArrayElement().toString());
//                        for (int i = 0; i < element.asArrayElement().size(); i++) {
//                            mDeviceNamelist.add(element.asArrayElement().get(i)
//                                    .asObjectElement());
////                            LogUtils.e("设备名称--->"+element.asArrayElement().get(i)
////                                    .asObjectElement());
//                        }
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoGroupEquipment"), mContext);
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//                    CrashReport.postCatchedException(e);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                System.out.println(throwable.getMessage());
//            }
//        });

    }

    //TODO
    private void getTeamId(Operator operator) {
        if (getIntent().getStringExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK) != null
                || getIntent().getStringExtra(Constants.FLAG_CREATE_SHUNTING_TASK) != null) {
            LogUtils.e("进入查询搬车部门");
            JsonObjectElement jsonObjectElement = new JsonObjectElement(OperatorInfo);
            creatorId = DataUtil.isDataElementNull(jsonObjectElement.get("Operator_ID"));
            create_task.setText(DataUtil.isDataElementNull(jsonObjectElement.get("Name")));
            group.setText(DataUtil.isDataElementNull(jsonObjectElement.get("Organise_Name")));
            teamId = DataUtil.isDataElementNull(jsonObjectElement.get("Organise_ID"));
        } else {
            LogUtils.e("进入查询其他部门");
            create_task.setText(operator.getName());  //创建人名
            LogUtils.e("创建人所属部门--->" + operator.getOrganiseID());
//            getTeamIdByOrganiseID(operator.getOrganiseID(), false);

            getTeamIdByOrganiseID("ALL", false);
        }
    }

    /**
     * create by jason
     *
     * @param organiseID
     * @param isFromMovingCarOrShunting
     */
    private void getTeamIdByOrganiseIDByServe(final String organiseID, boolean isFromMovingCarOrShunting) {

        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    LogUtils.e("获取数据为空t--->" + t);
                    return;
                }
                mTeamNamelist.clear();
                LogUtils.e("getTeamIdByOrganiseIDByServe--获取成功--->" + t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        mTeamNamelist.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
//                        LogUtils.e("jsonArrayElement查询出来搬车任务部门--->"+jsonArrayElement.asArrayElement().size()+"---Name->"+jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }

                    LogUtils.e("jsonArrayElement查询出来的---->" + jsonArrayElement.asArrayElement().toString());
                    LogUtils.e("创建人所属部门---->" + getLoginInfo().getOrganiseID());
                    setOrganise(TaskType);

                    if (1 == mTeamNamelist.size()) {
                        teamId = DataUtil.isDataElementNull(mTeamNamelist.get(0).get("Team_ID"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(0).get("TeamName")));
                                getDeviceName();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoOperatorGroup"), mContext);
                        }
                    });
                }
                if (getIntent().getStringExtra(Constants.FLAG_FAULT_NOT_SOLVE) != null) {
                    for (int i = 0; i < mTeamNamelist.size(); i++) {
                        if (DataUtil.isDataElementNull(mTeamNamelist.get(i).get("TeamName")).equals(teamName)) {
                            group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(i).get("TeamName")));
                            teamId = DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID"));
                        }
                    }
                    getDeviceName();
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("getTeamIdByOrganiseIDByServe--获取失败--->" + errorNo + "---->" + strMsg);
//                CrashReport.postCatchedException(throwable);
            }
        };
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        if ("ALL".equals(organiseID)) {
            LogUtils.e("执行这里--Factory-->" + getLoginInfo().getFactoryId());
            params.put("Factory", getLoginInfo().getFactoryId());
            HttpUtils.get(this, "BaseOrganise/GetFactoryDepartments", params, callback);
        } else {
            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(this)) == RootUtil.ROOT_WARRANTY
                    || isFromMovingCarOrShunting) {
                LogUtils.e("执行这里--organiseID-->" + organiseID);
                params.put("organiseID", organiseID);
//                params.put("Factory",getLoginInfo().getFactoryId());
                HttpUtils.get(this, "BaseOrganise/GetDearptIDDepartments", params, callback);
//                getTargetGroupListMoveCarByServe();
            } else {
                LogUtils.e("执行这里--ServerTeam_ID-->" + organiseID);
                params.put("ServerTeam_ID", organiseID);
                HttpUtils.get(this, "BaseOrganise/GetServiceDepartments", params, callback);
            }

        }

    }

    /**
     * 说明：根据任务类型去设置是否默认显示部门
     * 添加时间：2020/2/27 下午1:23
     * 作者：Jason
     */
    private void setOrganise(String Task_type) {
        String organise = getLoginInfo().getOrganiseID();
        String[] organises = organise.split(",");
        LogUtils.e("默认部门--->" + organises[0] + "--任务类型--->" + Task_type);

        if (Task_type.equals(Task.MOVE_CAR_TASK) || TextUtils.isEmpty(Task_type)) {
            //作用:如果是搬车任务就为空  Jason 2020/2/27 上午9:45
            group.getmEditText().setText("");
        } else {
            if (organises != null && !TextUtils.isEmpty(organises[0])) {
                for (int i = 0; i < mTeamNamelist.size(); i++) {
                    if (DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID")).equals(organises[0])) {
                        group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(i).get("TeamName")));
                        teamId = DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID"));
                    }
                }
                getDeviceName();
            }
        }
    }

    //服务车间的数据来源
    private void getTeamIdByOrganiseID(String organiseID, boolean isFromMovingCarOrShunting) {
        //更改为服务端数据来源
        LogUtils.e("进入数据库查询organiseID--->" + organiseID + "---isFromMovingCarOrShunting---->" + isFromMovingCarOrShunting);
        getTeamIdByOrganiseIDByServe(organiseID, isFromMovingCarOrShunting);
//        String rawQuery;
//        if ("ALL".equals(organiseID)) {
//            //更改为服务端数据来源
//            rawQuery = "select Organise_ID Team_ID,OrganiseName TeamName  from BaseOrganise where OrganiseClass=0 and FromFactory='" + getLoginInfo().getFactoryId() + "' and OrganiseType>1";
//            LogUtils.e("ALL-->rawQuery--->"+rawQuery);
//        } else {
//            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(this)) == RootUtil.ROOT_WARRANTY
//                    || isFromMovingCarOrShunting) {
//                //更改为服务端数据来源
//                rawQuery = "select Organise_ID Team_ID,OrganiseName TeamName  from BaseOrganise where Organise_ID in(" + organiseID + ") and OrganiseClass=0 and OrganiseType>1";
//                LogUtils.e("isFromMovingCar--->"+rawQuery);
//            } else {
//                //更改为服务端数据来源
//                rawQuery = "select distinct b.[Organise_ID] Team_ID,b.[OrganiseName] TeamName from TaskOrganiseRelation a,BaseOrganise b" +
//                        "        where a.[ServerTeam_ID] in (" + organiseID + ")" +
//                        "        and a.[Team_ID]=b.[Organise_ID]" +
//                        "        and b.[OrganiseClass]=0 and b.OrganiseType>1"+
//                        "        and b.Status=1";
//                LogUtils.e("查询执行--->"+rawQuery);
//            }
//        }
//        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, null);
//        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//
//            @Override
//            public void onSuccess(DataElement dataElement) {
//                mTeamNamelist.clear();
//                if (dataElement != null && dataElement.isArray()
//                        && dataElement.asArrayElement().size() > 0) {
//                    for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
//                        LogUtils.e("dataElement查询出来的---->"+dataElement.asArrayElement().toString());
//                        LogUtils.e("查询出来搬车任务部门--->"+dataElement.asArrayElement().size()+"---Name->"+dataElement.asArrayElement().get(i).asObjectElement());
//                        mTeamNamelist.add(dataElement.asArrayElement().get(i).asObjectElement());
//                    }
//
//                    if (1 == mTeamNamelist.size()) {
//                        teamId = DataUtil.isDataElementNull(mTeamNamelist.get(0).get("Team_ID"));
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(0).get("TeamName")));
//                                getDeviceName();
//                            }
//                        });
//                    }
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoOperatorGroup"), mContext);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                CrashReport.postCatchedException(throwable);
//            }
//        });
    }

    /**
     * create by jason
     */
    private void getTaskTypeByServe() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
//        params.put("DataType","TaskClass,TaskSubClass");
        params.put("FromMaintain", isFromMaintain);
        String isProperty = "false";
        if (moduleType.equals("Property")) {
            isProperty = "true";
        }
        params.put("isProperty", isProperty);
        HttpUtils.get(this, "DataDictionary/GetUserTaskClass", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                final HashMap<String, String> map = new HashMap<>();
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    LogUtils.e("getTaskType--数量--->" + jsonArrayElement.asArrayElement().size());
                    LogUtils.e("getTaskType---jsonArrayElement---->" + jsonArrayElement.asArrayElement().toString() + "数量---->" + jsonArrayElement.asArrayElement().size());
                    searchDataLists.clear();
                    mTaskType.clear();
                    if (jsonArrayElement.asArrayElement().size() > 0) {
                        if (jsonArrayElement.asArrayElement().get(0).asObjectElement().get(DataDictionary.DATA_NAME).toString().indexOf("设施报修") > 0) {
                            TempBillNo = System.currentTimeMillis() + "";
                            LogUtils.e("TempBillNo--->" + TempBillNo);
                            imageUpload.setVisibility(View.VISIBLE);
                        } else {
                            TempBillNo = "0";
                            LogUtils.e("TempBillNo--->" + TempBillNo);
                            imageUpload.setVisibility(View.INVISIBLE);
                        }
                    }


                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        String s = DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE));
                        //翻译DATA_NAME
                        jsonArrayElement.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                        if ((!s.equals(Task.MAINTAIN_TASK)))//step1:屏蔽维护
                        {//TODO

                            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
                                //报修人屏蔽组内安排任务
                                if (Factory.FACTORY_GEW.equals(getLoginInfo().getFromFactory())) {
                                    //GEW工厂屏蔽搬车和调车
                                    if (Task.REPAIR_TASK.equals(s)) {
                                        mTaskType.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                                    }
                                } else {
                                    if (Task.REPAIR_TASK.equals(s)
                                            || Task.MOVE_CAR_TASK.equals(s)
                                            || Task.TRANSFER_MODEL_TASK.equals(s)
                                            || Task.TRANSFER_TASK.equals(s))
                                        mTaskType.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                                }
                            } else {
                                //维修工屏蔽搬车、调车、车间报修任务
                                if (Factory.FACTORY_GEW.equals(getLoginInfo().getFromFactory())) {
                                    if (Task.GROUP_ARRANGEMENT.equals(s)|| Task.Lend_TASK.equals(s)|| Task.Borrow_TASK.equals(s)) {
                                        mTaskType.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                                    }
                                } else {
                                    if (Task.OTHER_TASK.equals(s)
                                            || Task.REPAIR_TASK.equals(s)
                                            || Task.MOVE_CAR_TASK.equals(s)
                                            || Task.TRANSFER_MODEL_TASK.equals(s)
                                            || Task.TRANSFER_TASK.equals(s)|| Task.Lend_TASK.equals(s)|| Task.Borrow_TASK.equals(s)){
                                        mTaskType.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                                    }
                                }
                            }
                        }
                        task_type_class.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
                                DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
                        LogUtils.e("任务类型--->" + jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE) + "------>" + jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME));
                        map.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                                DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));


                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.e("执行这里了---->" + mTaskType);
                            task_type.setDatas(mContext, mTaskType, DataDictionary.DATA_NAME);
                            TaskType = "T07";
                            LogUtils.e("TaskType---->" + TaskType);
                            if (moduleType.equals("Property") || Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
                                //若为报修人角色，即UserRoleID==7,创建任务，默认为车间报修任务
                                task_type.setText(map.get(Task.REPAIR_TASK));
                                TaskType = "T01";
                                LogUtils.e("TaskType---->" + TaskType);
                                task_description.setHint(LocaleUtils.getI18nValue("pleaseInput"));
                                ((TextView) findViewById(R.id.tvTaskdescription)).setText(LocaleUtils.getI18nValue("task_description_hint_2"));
                                ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("property_using_dept_1"));

                                //作用:默认显示部门  Jason 2020/2/27 下午1:46
                                setOrganise(TaskType);
                            } else {
                                //设施情况下隐藏
                                ((TextView) findViewById(R.id.tvTaskdescription)).setText(LocaleUtils.getI18nValue(""));
                                task_description.setHint(LocaleUtils.getI18nValue("task_description_hint"));

                                if (Factory.FACTORY_GEW.equals(getLoginInfo().getFromFactory())) {
                                    task_type.setText(map.get(Task.GROUP_ARRANGEMENT));
                                    getGroupArrangeSimpleDesList();
                                    TaskType = "T07";
                                    LogUtils.e("TaskType---->" + TaskType);
                                    //作用:默认显示部门  Jason 2020/2/27 下午1:46
                                    setOrganise(TaskType);
                                } else {
                                    task_type.setText(map.get(Task.MOVE_CAR_TASK));
                                    TaskType = Task.MOVE_CAR_TASK;
                                    //作用:默认显示部门  Jason 2020/2/27 下午1:46
                                    setOrganise(TaskType);
//                                    TaskType = jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE).valueAsString();
                                    if (!moduleType.equals("Property")) {
                                        findViewById(R.id.target_group_layout).setVisibility(View.VISIBLE);
                                        ((TextView) findViewById(R.id.organise)).setText("Move From:");
                                        ((TextView) findViewById(R.id.target_organise)).setText("Move To:");
                                        getTeamIdByOrganiseID("ALL", false);
//                                    getTargetGroupListMoveCarByServe();
//                                    isCarMove = true;
//                                    initDropSearchView(create_task, group.getmEditText(),
//                                            LocaleUtils.getI18nValue("title_search_group"), BaseOrganise.ORGANISENAME, GROUP, LocaleUtils.getI18nValue("pleaseScanICcard"), group.getDropImage());
                                    }
                                }
                            }
                            if (getIntent().getStringExtra("FromMeasurePointActivity") != null) {
                                //从点巡检、保养任务中由于测点异常创建新任务调用此方法填充相应数据
                                if (Task.UPKEEP.equals(IntentTaskSubClass)) {
                                    CreateFromMeasurePoint(map.get(Task.UPKEEP));//创建保养任务
                                    TaskType = "T0201";
                                    LogUtils.e("TaskType---->" + TaskType);
                                } else {
                                    CreateFromMeasurePoint(map.get(Task.ROUTING_INSPECTION));//创建点巡检任务
                                    TaskType = "T0202";
                                    LogUtils.e("TaskType---->" + TaskType);
                                }
                            }
                            if (getIntent().getStringExtra(Constants.FLAG_CREATE_SHUNTING_TASK) != null) {
                                //从搬车任务创建调车任务调用此方法填充相应数据
                                CreateShuntingTask(map.get(Task.TRANSFER_MODEL_TASK));
                                TaskType = "T06";
                                LogUtils.e("TaskType---->" + TaskType);
                            }
                            if (getIntent().getStringExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK) != null) {
                                //从调车任务创建搬车任务调用此方法填充相应数据
                                CreateCarMovingTask(map.get(Task.MOVE_CAR_TASK));
                                TaskType = "T05";
                                LogUtils.e("TaskType---->" + TaskType);
                            }
                            //作用:故障未解决跳转  Jason 2019/12/10 下午2:16
                            if (getIntent().getStringExtra(Constants.FLAG_FAULT_NOT_SOLVE) != null) {
//                                FALGNOTSOLVE(map.get(TaskType));
                                task_type.setText(map.get(TaskType));
                                LogUtils.e("故障未解决跳转TaskType---->" + TaskType);
                            }
                            //getSubTaskType(TaskClass);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"), mContext);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //  获取任务类型
    private void getTaskType() {
//        getDataFromDataBase成功执行之后，会执行回调函数
        ////更改为服务端数据来源
        getTaskTypeByServe();
//        DataUtil.getDataFromDataBase(mContext, "TaskClass", 0, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                System.out.println(element);
//                final HashMap<String, String> map = new HashMap<>();
//                if (element != null && element.isArray()
//                        && element.asArrayElement().size() > 0) {
//                    LogUtils.e("getTaskType---element---->"+element.asArrayElement().toString());
//                    searchDataLists.clear();
//                    for (int i = 0; i < element.asArrayElement().size(); i++) {
//                        String s = DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE));
//                        //翻译DATA_NAME
//                        element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
//                        if ((!s.equals(Task.MAINTAIN_TASK)))//step1:屏蔽维护
//                        {//TODO
//
//                            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
//                                //报修人屏蔽组内安排任务
//                                if (Factory.FACTORY_GEW.equals(getLoginInfo().getFromFactory())) {
//                                    //GEW工厂屏蔽搬车和调车
//                                    if (Task.REPAIR_TASK.equals(s)) {
//                                        mTaskType.add(element.asArrayElement().get(i).asObjectElement());
//                                    }
//                                } else {
//                                    if (Task.REPAIR_TASK.equals(s)
//                                            || Task.MOVE_CAR_TASK.equals(s)
//                                            || Task.TRANSFER_MODEL_TASK.equals(s)
//                                            || Task.TRANSFER_TASK.equals(s))
//                                        mTaskType.add(element.asArrayElement().get(i).asObjectElement());
//                                }
//                            } else {
//                                //维修工屏蔽搬车、调车、车间报修任务
//                                if (Factory.FACTORY_GEW.equals(getLoginInfo().getFromFactory())) {
//                                    if (Task.GROUP_ARRANGEMENT.equals(s)) {
//                                        mTaskType.add(element.asArrayElement().get(i).asObjectElement());
//                                    }
//                                } else {
//                                    if (Task.OTHER_TASK.equals(s)
//                                            || Task.REPAIR_TASK.equals(s)
//                                            || Task.MOVE_CAR_TASK.equals(s)
//                                            || Task.TRANSFER_MODEL_TASK.equals(s)
//                                            || Task.TRANSFER_TASK.equals(s)) {
//                                        mTaskType.add(element.asArrayElement().get(i).asObjectElement());
//                                    }
//                                }
//                            }
//                        }
//                        task_type_class.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
//                                DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
//                        LogUtils.e("任务类型--->"+element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)+"------>"+element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME));
//                        map.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
//                                DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
//
//
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtils.e("执行这里了---->"+mTaskType);
//                            task_type.setDatas(mContext, mTaskType, DataDictionary.DATA_NAME);
//                            TaskType = "T07";
//                            LogUtils.e("TaskType---->"+TaskType);
//                            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
//                                //若为报修人角色，即UserRoleID==7,创建任务，默认为车间报修任务
//                                task_type.setText(map.get(Task.REPAIR_TASK));
//                                TaskType = "T01";
//                                LogUtils.e("TaskType---->"+TaskType);
//                            } else {
//                                if (Factory.FACTORY_GEW.equals(getLoginInfo().getFromFactory())) {
//                                    task_type.setText(map.get(Task.GROUP_ARRANGEMENT));
//                                    getGroupArrangeSimpleDesList();
//                                    TaskType = "T07";
//                                    LogUtils.e("TaskType---->"+TaskType);
//                                } else {
//                                    task_type.setText(map.get(Task.MOVE_CAR_TASK));
//                                    TaskType = "T0301";
//                                    findViewById(R.id.target_group_layout).setVisibility(View.VISIBLE);
//                                    ((TextView) findViewById(R.id.organise)).setText("Move From:");
//                                    ((TextView) findViewById(R.id.target_organise)).setText("Move To:");
//                                }
//                            }
//                            if (getIntent().getStringExtra("FromMeasurePointActivity") != null) {
//                                //从点巡检、保养任务中由于测点异常创建新任务调用此方法填充相应数据
//                                if (Task.UPKEEP.equals(IntentTaskSubClass)) {
//                                    CreateFromMeasurePoint(map.get(Task.UPKEEP));//创建保养任务
//                                    TaskType = "T0201";
//                                    LogUtils.e("TaskType---->"+TaskType);
//                                } else {
//                                    CreateFromMeasurePoint(map.get(Task.ROUTING_INSPECTION));//创建点巡检任务
//                                    TaskType = "T0202";
//                                    LogUtils.e("TaskType---->"+TaskType);
//                                }
//                            }
//                            if (getIntent().getStringExtra(Constants.FLAG_CREATE_SHUNTING_TASK) != null) {
//                                //从搬车任务创建调车任务调用此方法填充相应数据
//                                CreateShuntingTask(map.get(Task.TRANSFER_MODEL_TASK));
//                                TaskType = "T06";
//                                LogUtils.e("TaskType---->"+TaskType);
//                            }
//                            if (getIntent().getStringExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK) != null) {
//                                //从调车任务创建搬车任务调用此方法填充相应数据
//                                CreateCarMovingTask(map.get(Task.MOVE_CAR_TASK));
//                                TaskType = "T05";
//                                LogUtils.e("TaskType---->"+TaskType);
//                            }
//                            //getSubTaskType(TaskClass);
//                        }
//                    });
//                } else {
//                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"), mContext);
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getDBDataLastUpdateTime();
//                    }
//                });
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                ToastUtil.showToastShort(LocaleUtils.getI18nValue("ErrorCauseByNoDataBase"), mContext);
//            }
//        });

    }

    @Override
    public void positiveListener(Integer id) {
        View view = dynamicControl.get(id);
        EditText editText = (EditText) view.findViewById(R.id.dynomicdata);
        String dateString = String.format("%d-%d-%d",
                mTimePickerDialog.getmYear(), mTimePickerDialog.getmMonth(), mTimePickerDialog.getmDay());
        editText.setText(dateString);
        JsonObject json = new JsonObject();
        json.addProperty("DataCode", dateString);
        dynamicSelectData2.put(id, json);
    }

    //时间选择器-------取消
    @Override
    public void negativeListener(Integer id) {

    }

    /**
     * create by jason
     *
     * @param sql
     */
    private void initDynamicCtrlDataByServe(final int key, final String sql) {

        HttpParams params = new HttpParams();
        String SQL = HttpUtils.toURLEncoded(sql);
        params.put("SQL", SQL);
        LogUtils.e("SQL--->sql---->" + sql);
        LogUtils.e("SQL--->" + SQL);
        HttpUtils.get(this, "DataDictionary/GetDataBySQL", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("null") || TextUtils.isEmpty(t)) {
                    LogUtils.e("查询出来数据为空");
                    return;
                }
                JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                DataElement jsonArrayElement = jsonObjectElement.get("PageData");
                ArrayList<ObjectElement> ao = new ArrayList<>();
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        ao.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }
                }
                dynamicBusinessData.put(key, ao);
                LogUtils.e("initDynamicCtrlDataByServe--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                LogUtils.e("initDynamicCtrlDataByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    private void initDynamicCtrlData(final Integer key, String rawQuery) { //
        LogUtils.e("rawQuery--->" + rawQuery);
        initDynamicCtrlDataByServe(key, rawQuery);
        //更改为服务端数据来源
//        ListenableFuture<DataElement> element = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, null);
//        Futures.addCallback(element, new FutureCallback<DataElement>() {
//            @Override
//            public void onSuccess(DataElement dataElement) {
//                LogUtils.e("initDynamicCtrlData---dataElement---->"+dataElement.asArrayElement().toString());
//                ArrayList<ObjectElement> ao = new ArrayList<>();
//                if (dataElement != null && dataElement.isArray()
//                        && dataElement.asArrayElement().size() > 0) {
//                    for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
//                        ao.add(dataElement.asArrayElement().get(i).asObjectElement());
//                    }
//                }
//                dynamicBusinessData.put(key, ao);
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                LogUtils.e("出错了--->"+throwable.getMessage());
//                CrashReport.postCatchedException(throwable);
//            }
//        });
    }

    /**
     * create by jason
     *
     * @param
     */
    private void layoutDynamicCtrlByServe() {

        HttpParams params = new HttpParams();
        params.put("dataType", "SetField");
        params.put("AppMode", getLoginInfo().getFromFactory());
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.get(this, "DataDictionary/DataDictionaryInfoList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t == null || t.equals("null")) {
                    LogUtils.e("t为null");
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    LogUtils.e("layoutDynamicCtrl--jsonArrayElement--->" + jsonArrayElement.asArrayElement().toString());
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        ObjectElement objectElement = jsonArrayElement.asArrayElement().get(i).asObjectElement();
                        DataElement dataValue2 = objectElement.get("DataValue2");
                        if (dataValue2 != null && !"".equals(dataValue2.valueAsString())) {
                            Integer id = objectElement.get("Data_ID").valueAsInt();
                            dynamicControlData.put(id, objectElement);
                            if ("DDL".equals(objectElement.get("DataValue1").valueAsString())) {

                                initDynamicCtrlData(id, objectElement.get("DataValue3").valueAsString());
                            }
                            createDynamicCtrl(id, dataValue2.valueAsString(), objectElement.get("DataValue1").valueAsString());
                        }
                    }
                } else {
                    LogUtils.e("没进去");
                }
                LogUtils.e("layoutDynamicCtrlByServe--测试成功--->" + t + "--数量--->" + jsonArrayElement.asArrayElement().size());
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                LogUtils.e("layoutDynamicCtrlByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    private void layoutDynamicCtrl() {
        LogUtils.e("layoutDynamicCtrl");
        layoutDynamicCtrlByServe();
        LogUtils.e("getLoginInfo()--getFromFactory--->" + getLoginInfo().getFromFactory());
        //更改为服务端数据来源
//        String rawQuery = "SELECT * FROM DataDictionary WHERE DataType = 'SetField' and Factory_ID='"
//                + getLoginInfo().getFromFactory() + "'  ORDER BY Sort DESC;";
//        ListenableFuture<DataElement> element = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, null);
//
//        try {
//            DataElement dataElement = Uninterruptibles.getUninterruptibly(element);
//            LogUtils.e("layoutDynamicCtrl--element--->"+dataElement.asArrayElement().toString());
//            if (dataElement != null && dataElement.isArray()
//                    && dataElement.asArrayElement().size() > 0) {
//                LogUtils.e("layoutDynamicCtrl--dataElement--->"+dataElement.asArrayElement().toString());
//                for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
//                    ObjectElement objectElement = dataElement.asArrayElement().get(i).asObjectElement();
//                    DataElement dataValue2 = objectElement.get("DataValue2");
//                    if (dataValue2 != null && !"".equals(dataValue2.valueAsString())) {
//                        Integer id = objectElement.get("Data_ID").valueAsInt();
//                        dynamicControlData.put(id, objectElement);
//                        if ("DDL".equals(objectElement.get("DataValue1").valueAsString())) {
//                            initDynamicCtrlData(id, objectElement.get("DataValue3").valueAsString());
//                        }
//                        createDynamicCtrl(id, dataValue2.valueAsString(), objectElement.get("DataValue1").valueAsString());
//                    }
//                }
//            }else{
//                LogUtils.e("没进去");
//            }
//
//        } catch (ExecutionException var3) {
//            LogUtils.e("出错了");
//            CrashReport.postCatchedException(new Throwable("layoutDynamicCtrl error"));
//            return;
//        }
    }

    //jason
    private void createDynamicCtrl(final Integer id, String controlName, String type) {//动态生成用户需要的控件
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = null;
        LogUtils.e("type--->" + type);
        LogUtils.e("TaskType--->" + TaskType);
        if (!"DDL".equals(type)) {
            view = inflater.inflate(R.layout.dynomic_input_ctl, null);
        } else {
            view = inflater.inflate(R.layout.dynomic, null);
            if (TaskType.equals("T08") || TaskType.equals("style_change_check_in") || TaskType.equals("style_change_")) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }

        }

        if ("DTP".equals(type)) {
            if (TaskType.equals("T08") || TaskType.equals("style_change_check_in") || TaskType.equals("style_change_")) {
                view.setLayoutParams(lp);
                ((TextView) view.findViewById(R.id.dynomicText)).setText(controlName + ":");
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout);
                LinearLayout deviceNameLayout = (LinearLayout) findViewById(R.id.equipment_num);
                mainLayout.addView(view, mainLayout.indexOfChild(deviceNameLayout) + 1);
            }
        } else {
            LogUtils.e("controlName--->" + controlName);

            view.setLayoutParams(lp);
            ((TextView) view.findViewById(R.id.dynomicText)).setText(controlName + ":");
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout);
            LinearLayout deviceNameLayout = (LinearLayout) findViewById(R.id.equipment_num);
            mainLayout.addView(view, mainLayout.indexOfChild(deviceNameLayout) + 1);
        }


        switch (type) {
            case "DTP": {
                LogUtils.e("进入DTP--->" + TaskType);
                if (TaskType.equals("T08") || TaskType.equals("style_change_check_in") || TaskType.equals("style_change_")) {
                    LogUtils.e("进入DTP创建控件");
                    EditText editText = (EditText) view.findViewById(R.id.dynomicdata);
                    editText.setHint(LocaleUtils.getI18nValue("select"));
                    editText.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mTimePickerDialog.showDateAndTimePickerDialog(id);
                                }
                            });
                }

                break;
            }
            case "DDL": {
                DropEditText det = (DropEditText) view.findViewById(R.id.dynomicDropEdit);
                det.setHint(LocaleUtils.getI18nValue("select"));
                initDropSearchView(group.getmEditText(), det.getmEditText(),
                        "",
                        id.toString(), -1,
                        LocaleUtils.getI18nValue("pleaseSelectGroup"),
                        det.getDropImage());
                break;
            }
            case "TXT": {
                ((EditText) view.findViewById(R.id.dynomicdata)).setHint(LocaleUtils.getI18nValue("select"));
                break;
            }
        }
        dynamicControl.put(id, view);
    }

    private void initView() {
        LogUtils.e("开始initView");
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.filter).setOnClickListener(this);
        ((ImageView) findViewById(R.id.filter)).setImageResource(R.mipmap.sync);
//kingzhang add 20211116
        findViewById(R.id.btn_WebLine).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_WebLine).setOnClickListener(this);
        ((ImageView) findViewById(R.id.btn_WebLine)).setImageResource(R.mipmap.star_selected);

        if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(this)) != RootUtil.ROOT_WARRANTY
                && OperatorInfo == null) {
            ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("ServerTeam"));
        } else {
            ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("belongGroup"));
        }
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("create_task"));
        findViewById(R.id.edit_resume).setOnClickListener(this);

        task_subtype_name_desc = (TextView) findViewById(R.id.task_subtype_name_id);
        task_type = (DropEditText) findViewById(R.id.task_type);
        module_type = (DropEditText) findViewById(R.id.module_type);
        simple_description = (DropEditText) findViewById(R.id.simple_description);
        task_description = (EditText) findViewById(R.id.task_description);
        task_subtype = (DropEditText) findViewById(R.id.task_subtype);
        group = (DropEditText) findViewById(R.id.group_id);
        device_name = (DropEditText) findViewById(R.id.device_name);
        targetOrganise = (DropEditText) findViewById(R.id.target_group);
        hasEquipment = (DropEditText) findViewById(R.id.hasEquipment);
        mdropEdit_sew_line = (DropEditText) findViewById(R.id.mdropEdit_sew_line);
        mdropEdit_order = (DropEditText) findViewById(R.id.mdropEdit_order);
        create_task = (EditText) findViewById(R.id.create_task);
        device_num = (EditText) findViewById(R.id.device_num);
        btn_sure = (Button) findViewById(R.id.sure);
        btn_sure.setOnClickListener(this);
        //kingzhang for srf 2022-0106 设备借还
        //begin
        equipmentOrganiseFrom = (DropEditText) findViewById(R.id.equipment_group_from_id);
        equipmentOrganiseFrom.setHint(LocaleUtils.getI18nValue("select"));
        equipmentOrganiseTo = (DropEditText) findViewById(R.id.equipment_group_to_id);
        equipmentOrganiseTo.setHint(LocaleUtils.getI18nValue("select"));
        equipmentOrganiseTo_cross = (DropEditText) findViewById(R.id.equipment_group_to_id_cross);
        equipmentOrganiseTo_cross.setHint(LocaleUtils.getI18nValue("select"));
        equipmentEmail = (DropEditText) findViewById(R.id.equipment_Email);
        equipmentEmail.setHint(LocaleUtils.getI18nValue("select"));
        equipmentApprove = (DropEditText) findViewById(R.id.equipment_Approve);
        equipmentApprove.setHint(LocaleUtils.getI18nValue("select"));
        ((EditText) findViewById(R.id.task_equipment_description)).setHint(LocaleUtils.getI18nValue("task_description_hint"));
        RadioGroup radgroup = (RadioGroup) findViewById(R.id.Cross_plant_Group);
        //第一种获得单选按钮值的方法
        //为radioGroup设置一个监听器:setOnCheckedChanged()
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                if (radbtn.getText().toString().equals("是")){
                    iSCross=true;
                    findViewById(R.id.equipment_LinearLayout).setVisibility(View.GONE);
                    findViewById(R.id.equipment_LinearLayout_cross).setVisibility(View.VISIBLE);
                }
                else{
                    iSCross=false;
                    findViewById(R.id.equipment_LinearLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.equipment_LinearLayout_cross).setVisibility(View.GONE);
                }

                GetPeopleInformationByServe_Email();
                GetPeopleInformationByServe_Approve();
                equipmentClearData();
            }
        });
        //end


        //facility Dropdown List
        list_facility_park = (DropEditText) findViewById(R.id.list_facility_park);
        list_facility_area = (DropEditText) findViewById(R.id.list_facility_area);
        list_facility_type = (DropEditText) findViewById(R.id.list_facility_type);
        list_facility_Name = (DropEditText) findViewById(R.id.list_facility_Name);

        ((TextView) findViewById(R.id.tvTaskType)).setText(LocaleUtils.getI18nValue("task_type"));
        ((TextView) findViewById(R.id.tvModuleType)).setText(LocaleUtils.getI18nValue("module_type"));
        task_subtype_name_desc.setText(LocaleUtils.getI18nValue("task_subtype_name"));
        ((TextView) findViewById(R.id.tvCreater)).setText(LocaleUtils.getI18nValue("creater"));
        ((TextView) findViewById(R.id.target_organise)).setText(LocaleUtils.getI18nValue("target_group"));
        ((TextView) findViewById(R.id.tvHasEquipment)).setText(LocaleUtils.getI18nValue("hasEquipment"));
        ((TextView) findViewById(R.id.tvEquipmentName)).setText(LocaleUtils.getI18nValue("equipment_name"));
        ((TextView) findViewById(R.id.tvEquipmentNum)).setText(LocaleUtils.getI18nValue("equipment_num"));
        ((TextView) findViewById(R.id.tvTaskCreateDescription)).setText(LocaleUtils.getI18nValue("task_create_description"));
        ((TextView) findViewById(R.id.tv_sew_line)).setText(LocaleUtils.getI18nValue("sewing_line"));
        ((TextView) findViewById(R.id.tv_order)).setText(LocaleUtils.getI18nValue("style_change_order"));
        ((TextView) findViewById(R.id.task_start_time_tag)).setText(LocaleUtils.getI18nValue("style_change_time"));

        //设施
        ((TextView) findViewById(R.id.tvfacilitypark)).setText(LocaleUtils.getI18nValue("task_facility_park_1"));
        ((TextView) findViewById(R.id.tvTaskFacilityArea)).setText(LocaleUtils.getI18nValue("task_facility_area_1"));
        ((TextView) findViewById(R.id.tvTaskFacilityType)).setText(LocaleUtils.getI18nValue("task_facility_type_1"));
        ((TextView) findViewById(R.id.tvTaskFacilityName)).setText(LocaleUtils.getI18nValue("task_facility_list_1"));
        ((TextView) findViewById(R.id.tvTaskdescription)).setText(LocaleUtils.getI18nValue("task_description_hint_2"));
        ((TextView) findViewById(R.id.textView19)).setText(LocaleUtils.getI18nValue("taskcreate_picture"));

        list_facility_park.setHint(LocaleUtils.getI18nValue("select"));
        list_facility_area.setHint(LocaleUtils.getI18nValue("select"));
        list_facility_type.setHint(LocaleUtils.getI18nValue("select"));
        list_facility_Name.setHint(LocaleUtils.getI18nValue("select"));
        task_type.setHint(LocaleUtils.getI18nValue("select"));
        task_subtype.setHint(LocaleUtils.getI18nValue("select"));
        create_task.setHint(LocaleUtils.getI18nValue("scan"));
        device_num.setHint(LocaleUtils.getI18nValue("scan"));
        group.setHint(LocaleUtils.getI18nValue("select"));
        hasEquipment.setHint(LocaleUtils.getI18nValue("select"));
        device_name.setHint(LocaleUtils.getI18nValue("select"));
        targetOrganise.setHint(LocaleUtils.getI18nValue("select"));
        simple_description.setHint(LocaleUtils.getI18nValue("select"));
        mdropEdit_sew_line.setHint(LocaleUtils.getI18nValue("select"));
        mdropEdit_order.setHint(LocaleUtils.getI18nValue("select"));
        task_description.setHint(LocaleUtils.getI18nValue("task_description_hint"));
        btn_sure.setText(LocaleUtils.getI18nValue("warning_message_confirm"));
        taskStartTime = (EditText) findViewById(R.id.task_start_time);
        taskStartTime.setHint(LocaleUtils.getI18nValue("please_select"));
        //kingzhang add 20210414
        //begin
        ((TextView) findViewById(R.id.tvReceiver)).setText(LocaleUtils.getI18nValue("Receiver"));
        receiver_num = (EditText) findViewById(R.id.receiver_num);
        receiver_num.setHint(LocaleUtils.getI18nValue("scan"));
        receiver_action = (ImageView) findViewById(R.id.receiver_action);
        receiver_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(CreateTaskActivity.this, com.king.zxing.CaptureActivity.class);
                it.setAction(Intents.Scan.ACTION);
                it.putExtra(Intents.Scan.CAMERA_ID, 0);
                it.putExtra(KEY_IS_CONTINUOUS, false);
                startActivityForResult(it, 2);
            }
        });

        //end
        //20210908 kingzhang add for srf 上传图片 begin

        imageUpload = (LinearLayout) findViewById(R.id.imageUpload);
        upload_img = (ImageView) findViewById(R.id.upload_img);
        upload_img.setTag(0);
        final ExpandGridView noScrollgridview = (ExpandGridView) findViewById(R.id.picture_containt);
        GridAdapter gridadapter = new GridAdapter(CreateTaskActivity.this);
        gridadapter.setItem_ID(upload_img.getTag().toString());
        gridadapter.setIndex(0);
        gridAdapterList.add(gridadapter);
        noScrollgridview.setAdapter(gridadapter);
        noScrollgridview.setTag(0);

        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Item_ID = upload_img.getTag().toString();

                currentIdx = (int) noScrollgridview.getTag();

                List<Map<String, Object>> imageList = new ArrayList();
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, Object> obj = dataList.get(i);
                    if (obj.get("Item_ID").equals(Item_ID)) {
                        imageList = (ArrayList) obj.get("list");
                        break;
                    }
                }

                if (arg2 == imageList.size()) {
//                    if (TaskStatus != 1 || TaskEquipmentStatus != 1 || isTaskHistory  ) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyDealingTaskCanAddPhoto"), mContext);
//                            }
//                        });
//                        return;
//                    }
                    if (imageList.size() >= 5) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("pictureNumLimit"), mContext);
                            }
                        });
                        return;
                    }


                    new CreateTaskActivity.PopupWindows(mContext);
                } else {
                    ImageView image = (ImageView) arg1.findViewById(R.id.item_grida_image);
                    imageClick(image);
                }
            }
        });
        noScrollgridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (isTaskHistory) {
                    return true;
                }
                currentIdx = (int) noScrollgridview.getTag();
                Item_ID = upload_img.getTag().toString();
                List<Map<String, Object>> imageList = new ArrayList();
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, Object> obj = dataList.get(i);
                    if (obj.get("Item_ID").equals(Item_ID)) {
                        imageList = (ArrayList) obj.get("list");
                        break;
                    }
                }

                final List<Map<String, Object>> result = imageList;

                //弹出确认删除图片对话框，点击确认后删除图片
                if (position != imageList.size()) {
                    new AlertDialog.Builder(mContext).setTitle(LocaleUtils.getI18nValue("makeSureDeletePicture"))
                            .setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePictureFromServer((String) result.get(position).get("TaskAttachment_ID"), result.get(position));
                                }
                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                return true;
            }
        });


        //20210908 kingzhang add for srf 上传图片 END

        initDatePickerDialog(taskStartTime, (ImageView) findViewById(R.id.task_start_time_image));

        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        findViewById(R.id.task_description_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_description.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
        layoutDynamicCtrl();
    }


    //20210908 kingzhang add for srf 上传图片 begin
    LinearLayout imageUpload;
    private boolean isTaskHistory = false;
    private int TaskStatus = -1;
    private String TaskEquipment;
    private int TaskEquipmentStatus;
    public ImageView upload_img;
    private List<GridAdapter> gridAdapterList = new ArrayList<>();
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
      String success="成功";
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private boolean shape;
        private int index;
        private String Item_ID;
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

            String itemID = getItem_ID();

            for (int i = 0; i < dataList.size(); i++) {

                Map<String, Object> map = dataList.get(i);
                if (map.get("Item_ID").equals(itemID)) {
                    List list = (ArrayList) map.get("list");
                    return list.size() + 1;
                }
            }
            return 1;
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public String getItem_ID() {
            return Item_ID;
        }

        public void setItem_ID(String item_ID) {
            Item_ID = item_ID;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
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
            CreateTaskActivity.GridAdapter.ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new CreateTaskActivity.GridAdapter.ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (CreateTaskActivity.GridAdapter.ViewHolder) convertView.getTag();
            }

            holder.image.setVisibility(View.VISIBLE);


            String itemID = getItem_ID();
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {

                Map<String, Object> map = dataList.get(i);
                if (map.get("Item_ID").equals(itemID)) {
                    list = (ArrayList) map.get("list");

                }
            }

            if (position == list.size()) {
//                holder.image.setImageBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.mipmap.icon_addpic_unfocused));

                // String addImageUrl =  "mipmap://" + R.mipmap.icon_addpic_unfocused;
                String imgUrl = "drawable://" + R.drawable.icon_addpic_unfocused;
                //addImageUrlToDataList(imgUrl);
                imageLoader.displayImage(imgUrl, holder.image, options,
                        animateFirstListener);
            } else {
                String imgUrl = (String) list.get(position).get("imageUrl");

                if (imgUrl.equals(holder.image.getTag())) {

                } else {
                    imageLoader.displayImage(imgUrl, holder.image, options,
                            animateFirstListener);
                }
                holder.image.setTag(imgUrl);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            LogUtils.e("弹出PopWindow-拍照####");

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(ll_popup, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            bt1.setText(LocaleUtils.getI18nValue("take_photo"));
            if ((TaskType.equals(Task.REPAIR_TASK)) && (moduleType.equals("Property"))){
                Button bt2 = (Button) view
                        .findViewById(R.id.item_popupwindows_Photo);

                ((LinearLayout) view.findViewById(R.id.Layout_popupwindows_Photo)).setVisibility(View.VISIBLE);
                bt2.setText("相册");
                bt2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        album();
                        dismiss();
                    }
                });
            }
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
    private static final int TAKE_photo = 1;
    private String path = "";
    private String Item_ID = "";
    // private Context mContext = this;

    public void photo() {

//        /storage/emulated/0/Android/data/com.emms.debug/files/btp
        //File dir = new File(mContext.getExternalFilesDir(null) + "/btp/");
        //kingzhang 29211129
        File dir = new File( "/storage/emulated/0/btp/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
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
    public void album() {

        //File dir = new File(mContext.getExternalFilesDir(null) + "/btp/");
        //kingzhang 29211129
        File dir = new File( "/storage/emulated/0/btp/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            // 创建Intent，用于打开手机本地图库选择图片
            Intent intent1 = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // 启动intent打开本地图库
            startActivityForResult(intent1,TAKE_photo);

        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

    }
    private List<Map<String, Object>> dataList = new ArrayList<>();

    private void addImageUrlToDataList(String path, String ID, String Item_ID) {

        boolean hasThisItemID = false;
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            if (map.get("Item_ID").equals(Item_ID)) {

                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("imageUrl", path);
                imageMap.put("TaskAttachment_ID", ID);

                ArrayList list = (ArrayList) map.get("list");
                list.add(list.size(), imageMap);

                map.put("list", list);
                hasThisItemID = true;

                break;
            }
        }
        if (!hasThisItemID) {
            List<Map<String, Object>> imageMapsList = new ArrayList<>();
            Map<String, Object> imageMap = new HashMap<>();
            imageMap.put("imageUrl", path);
            imageMap.put("TaskAttachment_ID", ID);
            imageMapsList.add(imageMapsList.size(), imageMap);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("Item_ID", Item_ID);
            dataMap.put("list", imageMapsList);
            dataList.add(dataList.size(), dataMap);
        }

//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("imageUrl", path);
//        dataMap.put("TaskAttachment_ID",ID);
//        dataList.add(dataList.size(), dataMap);
    }

    private String Task_ID;
    private String Task_List_ID;
    private String TempBillNo;

    private void submitPictureToServer(String path) {
        try {
            Bitmap bitmap = Bimp.revitionImageSize(path);
            String base64 = bitmapToBase64(bitmap);
            HttpParams params = new HttpParams();
            JsonObjectElement jsonObjectElement = new JsonObjectElement();
            jsonObjectElement.set(Task.TASK_ID, Task_ID);
            jsonObjectElement.set("Task_List_ID", Task_List_ID);
            jsonObjectElement.set("Item_ID", Item_ID);
            jsonObjectElement.set("TaskAttachment_ID", 0);
            jsonObjectElement.set("ImgBase64", base64);
            jsonObjectElement.set("AttachmentType", "jpg");
            jsonObjectElement.set("TempBillNo", TempBillNo);
            params.putJsonParams(jsonObjectElement.toJson());
            LogUtils.e("图片上传参数---->" + params.toString());
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

    private int currentIdx = -1;

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


                                for (int i = 0; i < dataList.size(); i++) {

                                    Map<String, Object> map = dataList.get(i);
                                    if (map.get("Item_ID").equals(Item_ID)) {
                                        ((ArrayList) map.get("list")).remove(data);
                                    }
                                }
                                // dataList.remove(data);
                                //adapter.notifyDataSetChanged();
                                if (currentIdx != -1) {
                                    CreateTaskActivity.GridAdapter gridAdapter = gridAdapterList.get(currentIdx);
                                    gridAdapter.notifyDataSetChanged();
                                    currentIdx = -1;

                                }
                                //Gridadapter.notifyDataSetChanged();
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

    private void getTaskAttachmentDataFromServerByTaskId() {
        if (null == Task_ID) {
            return;
        }
        HttpParams params = new HttpParams();
        params.put("task_id", Task_ID.toString());
        params.put("task_list_id", Task_List_ID);
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
                            String TaskAttachment_ID = DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskAttachment_ID"));
                            String Item_ID = DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Item_ID"));
                            addImageUrlToDataList(path, TaskAttachment_ID, Item_ID);
                        }
                        //在这里刷新图片列表
                        if (currentIdx != -1) {
                            GridAdapter gridAdapter = gridAdapterList.get(currentIdx);
                            gridAdapter.notifyDataSetChanged();
                            currentIdx = -1;

                        }
//                        if (null != Gridadapter) {
//                            Gridadapter.notifyDataSetChanged();
//                        }

//                        if (null != adapter) {
//                            adapter.notifyDataSetChanged();
//                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
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
        Intent showBigImageIntent = new Intent(CreateTaskActivity.this,
                ShowBigImageActivity.class);

        startActivity(showBigImageIntent);
    }
    //20210908 kingzhang add for srf 上传图片 end

    private void initDatePickerDialog(final EditText editText, ImageView imageView) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStyleDate();
                DateTimePickDialog dateTimePicKDialog = new DateTimePickDialog(CreateTaskActivity.this, initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(editText);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStyleDate();
                DateTimePickDialog dateTimePicKDialog = new DateTimePickDialog(CreateTaskActivity.this, initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(editText);
            }
        });
    }

    private void changeStyleDate() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
        String nowDateStr = DataUtil.utc2Local(DataUtil.getDate(sf.format(new Date())));
        Date nowDate = new Date();
        try {
            nowDate = sf.parse(nowDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initEndDateTime = sf1.format(nowDate);
    }

    /**
     * create by jason
     *
     * @param str
     */
    private void getSubTaskTypeByServe(String str, String pdataid) {
        try {
            HttpParams params = new HttpParams();
            params.put("DataType ", "TaskClass");
            params.put("PData_ID", pdataid);
            params.put("factory_id", getLoginInfo().getFactoryId());
            showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
            LogUtils.e("factoryId--->" + getLoginInfo().getFactoryId());
            HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'TaskSubClass'%20and%20PData_ID%20eq%20" + pdataid + "%20and%20factory_id%20eq%20'" + getLoginInfo().getFactoryId() + "'", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    dismissCustomDialog();
                    if (TextUtils.isEmpty(t) || t.equals("null")) {
                        return;
                    }
                    DataElement jsonArrayElement = new JsonArrayElement(t);
                    mSubType = new ArrayList<>();
                    LogUtils.e("getSubTaskTypeByServe---数量--->" + jsonArrayElement.asArrayElement().size());
                    LogUtils.e("jsonArrayElement--->" + jsonArrayElement.asArrayElement().toString());
                    if (jsonArrayElement != null && jsonArrayElement.isArray()
                            && jsonArrayElement.asArrayElement().size() > 0) {
                        mSubType.clear();
                        for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                            mSubType.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                            task_type_class.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("DataName")),
                                    DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("DataCode")));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                task_subtype.setVisibility(View.VISIBLE);
                                task_subtype_name_desc.setVisibility(View.VISIBLE);
                                findViewById(R.id.subTask).setVisibility(View.VISIBLE);
                                task_subtype.setDatas(mContext, mSubType, DataDictionary.DATA_NAME);

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                task_subtype.setVisibility(View.GONE);
                                task_subtype_name_desc.setVisibility(View.GONE);
                                findViewById(R.id.subTask).setVisibility(View.GONE);
                            }
                        });
                    }

                    LogUtils.e("getSubTaskTypeByServe--测试成功--->" + t);
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    dismissCustomDialog();
//                    Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                    LogUtils.e("getSubTaskTypeByServe--测试失败--->" + errorNo + "----" + strMsg);
                }
            });
        } catch (Exception e) {
            dismissCustomDialog();
            task_subtype.setVisibility(View.GONE);
            task_subtype_name_desc.setVisibility(View.GONE);
            findViewById(R.id.subTask).setVisibility(View.GONE);
            CrashReport.postCatchedException(e);
        }
    }


    private void getSubTaskType(String str) {
        try {
            int pos = 0;
            for (int i = 0; i < mTaskType.size(); i++) {
                if (DataUtil.isDataElementNull(mTaskType.get(i).get(DataDictionary.DATA_NAME)).equals(str)) {
                    pos = i;
                }
            }
            String pdataid = DataUtil.isDataElementNull(mTaskType.get(pos).get(DataDictionary.DATA_ID));
            getSubTaskTypeByServe(str, pdataid);
//            TaskType = DataUtil.isDataElementNull(mTaskType.get(pos).get(DataDictionary.DATA_CODE));
//更改为服务端数据来源
            LogUtils.e("pdataid--->" + pdataid);
//            String rawQuery = "select * from DataDictionary where " +
//                    "DataType = 'TaskClass' and PData_ID=" + pdataid;
//            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                    EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
//            Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//                @Override
//                public void onSuccess(DataElement element) {
//                    System.out.println(element);
//                    mSubType = new ArrayList<>();
//                    LogUtils.e("element--->"+element.asArrayElement().toString());
//                    if (element != null && element.isArray()
//                            && element.asArrayElement().size() > 0) {
//                        mSubType.clear();
//                        for (int i = 0; i < element.asArrayElement().size(); i++) {
//                            mSubType.add(element.asArrayElement().get(i).asObjectElement());
//                            task_type_class.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("DataName")),
//                                    DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("DataCode")));
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                task_subtype.setVisibility(View.VISIBLE);
//                                task_subtype_name_desc.setVisibility(View.VISIBLE);
//                                findViewById(R.id.subTask).setVisibility(View.VISIBLE);
//                                task_subtype.setDatas(mContext, mSubType, DataDictionary.DATA_NAME);
//
//                            }
//                        });
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                task_subtype.setVisibility(View.GONE);
//                                task_subtype_name_desc.setVisibility(View.GONE);
//                                findViewById(R.id.subTask).setVisibility(View.GONE);
//                            }
//                        });
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Throwable throwable) {
//                    System.out.println(throwable.getMessage());
//                }
//            });
        } catch (Exception e) {
            task_subtype.setVisibility(View.GONE);
            task_subtype_name_desc.setVisibility(View.GONE);
            findViewById(R.id.subTask).setVisibility(View.GONE);
            CrashReport.postCatchedException(e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_right_action) {
            finish();
        } else if (id == R.id.edit_resume) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);

        } else if (id == R.id.iv_search_clear) {
            searchBox.setText("");
            clearBtn.setVisibility(View.GONE);
        } else if (id == R.id.sure) {
            String taskType = task_type.getText();
            if (task_type_class.get(taskType) != null && task_type_class.get(taskType).equals("T08")) {
                LogUtils.e("上交任务-->createStyleChangeRequest");
                createStyleChangeRequest();
            } else {

                createRequest();
                LogUtils.e("上交任务-->createRequest");
            }
        } else if (id == R.id.filter) {
//            getDBDataLastUpdateTime();
            initData(true);
            initEvent();

        }else if (id == R.id.btn_WebLine) {
            //代码实现跳转
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            //UAT
            //Uri content_url = Uri.parse("http://getazdevnt009c.chinaeast.cloudapp.chinacloudapi.cn/emmsmobileweb/webs/login.html");
            //Prod
            Uri content_url = Uri.parse("http://getazdevnt009c.chinaeast.cloudapp.chinacloudapi.cn/emmsmobileweb/webs/login.html");
            switch (BuildConfig.appEnvironment) {
                case UAT: {
                    content_url = Uri.parse("http://getazdevnt009c.chinaeast.cloudapp.chinacloudapi.cn/emmsmobileweb/webs/login.html");
                }
                case PROD:{
                    content_url = Uri.parse("http://emmsin.esquel.cn/emmsmobileweb/webs/login.html");
                }default:{

                }
            }

            intent.setData(content_url);
            startActivity(intent);
        }
    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    private List<ObjectElement> getDynamicChildData(String childStringID) {
        Integer childID = Integer.valueOf(childStringID);
        ObjectElement childData = dynamicControlData.get(childID);
        int parentKey = childData.get("PData_ID").valueAsInt();
        if (parentKey == 0) {
            return dynamicBusinessData.get(childID);
        }

        ObjectElement parentData = dynamicSelectData.get(parentKey);
        if (parentData == null) {
            return dynamicBusinessData.get(childID);
        }
        List<ObjectElement> childDatas = new ArrayList<>();
        for (ObjectElement allChildData : dynamicBusinessData.get(childID)) {
            if (parentData.get("Data_ID").valueAsInt() == allChildData.get("PData_ID").valueAsInt())
                childDatas.add(allChildData);
        }
        return childDatas;
    }

    private void createStyleChangeRequest() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String taskType = task_type.getText();
                String teamName = group.getText();
                String createTask = create_task.getText().toString();
                String receiver = receiver_num.getText().toString();//kingzhang add 20210414

                String taskSubType = null;
                String deviceName = device_name.getText();
                String deviceNum = equipmentID;
                String sewingLine = mdropEdit_sew_line.getText();
                String orderno = mdropEdit_order.getText();
                if (View.VISIBLE == task_subtype.getVisibility()) {
                    taskSubType = task_subtype.getText();
                }

                if (taskType.equals("")) {
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_tasktype_post"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (createTask.equals("")) {
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_scan_operator_post"), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sewingLine.equals("")) {
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_sewling_post"), Toast.LENGTH_SHORT).show();
                    return;
                }
                //kingzhang add 20210414
//                if (receiver.equals("")) {
//                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_receiver_post"), Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (orderno.equals("")) {
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_order_post"), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (taskStartTime.getText().toString().equals("")) {
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_style_change_time_post"), Toast.LENGTH_SHORT).show();
                    return;
                }
                submitStyleChangeTask(taskType, taskSubType, sewingLine, orderno, taskStartTime.getText().toString(), receiver);
            }
        });
    }

    private void createRequest() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String taskType = task_type.getText();
                String teamName = group.getText();
                String createTask = create_task.getText().toString();

                String taskSubType = null;
                String deviceName = device_name.getText();
                String deviceNum = equipmentID;
                if (moduleType.equals("Property")) {
                    deviceNum = list_facility_Name_Value;
                    if (list_facility_park_Value.equals("") || list_facility_area_Value.equals("") || list_facility_type_Value.equals("") || list_facility_Name_Value.equals("")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_creatTask_facility"), Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }
                String simpledescription = simple_description.getText();
                if (View.VISIBLE == task_subtype.getVisibility()) {
                    taskSubType = task_subtype.getText();
                }

                if (taskType.equals("")) {
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_tasktype_post"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (createTask.equals("")) {
                    Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_scan_operator_post"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!task_type_class.get(taskType).equals(Task.MOVE_CAR_TASK)) {
                    if (teamName.equals("")) {
                        Toast.makeText(mContext, LocaleUtils.getI18nValue("tips_team_type_post"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.SIMPLE_DESCRIPTION_ACTION))) {
                    case "1": {
                        //Do nothing
                        break;
                    }
                    default: {
                        if ((simpledescription == null || simpledescription.equals(""))&&!(task_type_class.get(taskType).equals(Task.Lend_TASK) || task_type_class.get(taskType).equals(Task.Borrow_TASK))) {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("tips_task_desc_post"), mContext);
                            return;
                        }
                    }
                }


                if (simpledescription.equals(LocaleUtils.getI18nValue("other")) || "".equals(simpledescription)) {
                    simpledescription = "";
                    SimpleDescriptionCode = "Default";
                } else {
                    if (task_description.getText().toString().length() > 0) {
                        simpledescription += "\n";
                    }
                }
                simpledescription += task_description.getText().toString();
                if (task_type_class.get(taskType) != null && task_type_class.get(taskType).equals(Task.MOVE_CAR_TASK)) {
                    if (targetOrganise.getmEditText().getText().toString().equals("")) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseSelectTargetGroup"), mContext);
                        return;
                    }
                }
                //kingzhang for srf 2022-0106
                if (task_type_class.get(taskType).equals(Task.Lend_TASK) || task_type_class.get(taskType).equals(Task.Borrow_TASK)) {
                    if (TARGET_ORGANISE_Form_ID == null || TARGET_ORGANISE_Form_ID == "") {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("Please_MoveFrom"), mContext);
                        return;
                    }
                    if (((TARGET_ORGANISE_To_cross_ID == null || TARGET_ORGANISE_To_cross_ID == "") && (TARGET_ORGANISE_To_ID == null || TARGET_ORGANISE_To_ID == ""))) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("Please_MoveTo"), mContext);
                        return;
                    }
                    if (equipment_PeopleInformation_Email_ID == null || equipment_PeopleInformation_Email_ID == "") {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("Please_mailUser"), mContext);
                        return;
                    }
                    if (equipment_PeopleInformation_Approve_ID == null || equipment_PeopleInformation_Approve_ID == "") {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("Please_confirmerUser"), mContext);
                        return;
                    }
                }
                submitTask(taskType, taskSubType, "", deviceName, deviceNum, simpledescription);
            }
        });


    }

    //选择任务类型上面的搜索功能
    private void dropSearchViewOnClickListener(final boolean isImageView,
                                               final EditText condition, final String searchTitle,
                                               final String searchName, final int searTag, final String tips) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String copySearchName = searchName;
                searchDataLists.clear();
                switch (searTag) {
                    case TASK_TYPE:
                        searchDataLists.addAll(mTaskType);
                        break;
                    case MODULE_TYPE:
                        searchDataLists.addAll(mModuleType);
                        break;
                    case FACILITY_PARK:
                        searchDataLists.addAll(mList_facility_park);
                        break;
                    case FACILITY_AREA:
                        searchDataLists.addAll(mList_facility_area);
                        break;
                    case FACILITY_TYPE:
                        searchDataLists.addAll(mList_facility_type);
                        break;
                    case FACILITY_NAME:
                        searchDataLists.addAll(mList_facility_Name);
                        break;
                    case TASK_SUBTYPE:
                        searchDataLists.addAll(mSubType);
                        break;
                    case GROUP:
                        searchDataLists.addAll(mTeamNamelist);
                        break;
                    case DEVICE_NAME:
                        LogUtils.e("点击了DEVICE_NAME");
                        searchDataLists.addAll(mDeviceNamelist);
                        break;
                    case DEVICE_NUM:
                        if (isImageView) {
                            //作用:取消限制先选组别  Jason 2020/1/8 下午2:33
                           /* if (group.getmEditText().getText().toString().equals("")) {
                                ToastUtil.showToastLong(LocaleUtils.getI18nValue("pleaseSelectGroupFirst"), mContext);
                                return;
                            }*/
                            Intent it = new Intent(CreateTaskActivity.this, com.king.zxing.CaptureActivity.class);
                            it.setAction(Intents.Scan.ACTION);
                            it.putExtra(Intents.Scan.CAMERA_ID, 0);
                            it.putExtra(KEY_IS_CONTINUOUS, false);
                            startActivityForResult(it, 1);
//                            Intent it = new Intent(CreateTaskActivity.this, GoogleCaptureActivity.class);
//                            startActivityForResult(it, 1);
                            return;
                        } else {
                            searchDataLists.addAll(mDeviceNumlist);
                            break;
                        }
                    case SIMPLE_DESCRIPTION:
                        LogUtils.e("添加数据--->" + mSimpleDescriptionList.size());
                        searchDataLists.addAll(mSimpleDescriptionList);
                        break;
                    case HAS_EQUIPMENT:
                        searchDataLists.addAll(mHasEquipment);
                        break;
                    case TARGET_ORGANISE:
                        searchDataLists.addAll(mTargetGroup);
                        break;
                    case SEWING_LINE:
                        searchDataLists.addAll(mSewLine);
                        break;
                    case ORDER:
                        searchDataLists.addAll(mOrder);
                        break;
                    //kingzhang for srf 2022-0106 设备借还
                    case TARGET_ORGANISE_Form:
                        searchDataLists.addAll(mEquipmentOrganise);
                        break;
                    case TARGET_ORGANISE_To:
                        searchDataLists.addAll(mEquipmentOrganise);
                        break;
                    case TARGET_ORGANISE_To_cross:
                        searchDataLists.addAll(mEquipmentOrganise_cross);
                        break;
                    case equipment_PeopleInformation_Email:
                        searchDataLists.addAll(mEquipmentGroup_Email);
                        break;
                    case equipment_PeopleInformation_Approve:
                        searchDataLists.addAll(mEquipmentGroup_Approve);
                        break;
                    default: {
                        List<ObjectElement> ao = getDynamicChildData(searchName);
                        if (ao == null) {
                            ao = new ArrayList<ObjectElement>();
                        }
                        searchDataLists.addAll(ao);
                        copySearchName = "DataName";
                    }
                    break;
                }
                cacheSearchName = searchName;
                searchtag = searTag;
                LogUtils.e("进入数据更新--->" + condition + "----searchtag--->" + searTag + "---searchDataLists---->" + searchDataLists.size());

                if (condition != null) {
                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                        mResultAdapter.changeData(searchDataLists, copySearchName);
                        menuSearchTitle.setText(searchTitle);
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                    } else {
                        LogUtils.e("显示的tips--->" + tips);
                        ToastUtil.showToastShort(tips, mContext);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mResultAdapter.changeData(searchDataLists, copySearchName);
                        menuSearchTitle.setText(searchTitle);
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                    } else {
                        LogUtils.e("显示的tips--->" + tips);
                        ToastUtil.showToastShort(tips, mContext);
                    }
                }
            }
        });
    }

    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
//            String iccardID = "21288253";
            if (OperatorInfo != null || IntentTaskSubClass != null) {//若为从搬车/调车/巡检/保养任务中创建任务，则不可添加/修改设备
                return;
            }
            getDataByICcardID(iccardID, false);
        }

        // }
    }

    private void getDataByICcardID(String iccardID, boolean isQRCode) {
        if (iccardID == null) {
            return;
        } else if (iccardID.equals("")) {
            return;
        }
        if (HasEquipment_map.get(hasEquipment.getText()) == null || HasEquipment_map.get(hasEquipment.getText()) == 0) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseSelectHasEquipment"), mContext);
            return;
        }
        //作用:不再限制先选择部门  Jason 2020/1/8 下午2:20
        getEquipmentNumByICcardId(iccardID, isQRCode);
        /*if (!teamId.equals("")) {
            getEquipmentNumByICcardId(iccardID, isQRCode);
        } else {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseSelectGroupFirst"), mContext);
        }*/
    }

    //初始化搜索视图
    private void initDropSearchView(
            final EditText condition, EditText subEditText,
            final String searchTitle, final String searchName,
            final int searTag, final String tips, ImageView imageView) {
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dropSearchViewOnClickListener(false, condition,
                                        searchTitle, searchName, searTag, tips);
                            }
                        }
                );
        if (imageView != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropSearchViewOnClickListener(true, condition,
                            searchTitle, searchName, searTag, tips);
                }
            });
        }
    }

    private void submitStyleChangeTask(String TaskType, String TaskSubType, String sewingLine,
                                       String orderno, String TaskDescription, String receiver) {
        btn_sure.setEnabled(false);
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
//        if (StringUtils.isNotBlank(TaskSubType)) {
//            TaskType = TaskSubType;
//        }
//        //创建任务提交数据:创建人ID，任务类型"T01,T02"等，机台号（数组），任务描述，组别
//        JsonObjectElement task = new JsonObjectElement();
//        JsonObjectElement taskDetail = new JsonObjectElement();
//        //获取创建人ID
//        taskDetail.set("Applicant", creatorId);//任务发起人ID
//        taskDetail.set("Task_ID", 0);
////        taskDetail.set("TaskApplicantOrg",sewingLineID);
//        taskDetail.set("SetField3", orderno);
//        taskDetail.set("MoveTo_ID", sewingLineID);
//        taskDetail.set("StyleChangeDate", TaskDescription);
//        if (task_type_class.get(TaskType) != null) {//任务类型
//            taskDetail.set("TaskClass", task_type_class.get(TaskType));
//        }
//        taskDetail.set("Factory", getLoginInfo().getFactoryId());//任务发起人所属班组
//
//        if (FromTask_ID != null) {
//            taskDetail.set("FromTask_ID", FromTask_ID);
//        }
//
//        taskDetail.set("TaskDescrCode", "");
//
//        for (Map.Entry<Integer, ObjectElement> entry : dynamicControlData.entrySet()) {
//            ObjectElement it = entry.getValue();
//            if (it != null && it.get("DataCode") != null) {
//                if ("DDL".equals(it.get("DataValue1").valueAsString())) {
//                    ObjectElement selectData = dynamicSelectData.get(entry.getKey());
//                    if (selectData != null && selectData.get("DataCode") != null) {
//                        taskDetail.set(it.get("DataCode").valueAsString(),
//                                selectData.get("DataCode").valueAsString());
//                    }
//                } else if ("TXT".equals(it.get("DataValue1").valueAsString())) {
//                    View v = dynamicControl.get(entry.getKey());
//                    if (v != null) {
//                        String data = ((EditText) v.findViewById(R.id.dynomicdata)).getText().toString();
//                        if (data != null) {
//                            taskDetail.set(it.get("DataCode").valueAsString(),
//                                    data);
//                        }
//                    }
//                } else {
//                    JsonObject selectData = dynamicSelectData2.get(entry.getKey());
//                    if (selectData != null && selectData.get("DataCode") != null) {
//                        taskDetail.set(it.get("DataCode").valueAsString(),
//                                selectData.get("DataCode").getAsString());
//                    }
//                }
//            }
//        }
//
//        task.set("Task", taskDetail);
//
//        //包装数据
//        //填写创建人角色
//        if (OperatorInfo != null) {
//            task.set("UserRole_ID", RootUtil.ROOT_WARRANTY);
//        } else {
//            task.set("UserRole_ID", SharedPreferenceManager.getUserRoleID(this));
//        }

        params = submitStyleChangeData(TaskType, TaskSubType, sewingLine, orderno, TaskDescription, receiver);

//        params.putJsonParams(task.toJson());
        LogUtils.e("上传任务----->" + params);
        HttpUtils.getChangeFormServer(this, "emms/transferTask/save", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                btn_sure.setEnabled(true);
                LogUtils.e("上传任务成功返回数据----->" + t);
                if (t != null) {
                    LogUtils.e("上传任务成功返回数据----->" + t);
                    final JsonObjectElement data = new JsonObjectElement(t);
                    if (data.get(Data.SUCCESS).valueAsBoolean()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_sure.setEnabled(false);
                                ToastUtil.showToastLong(LocaleUtils.getI18nValue("SuccessCreateTask"), mContext);
                            }
                        });
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        }).start();


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(DataUtil.isDataElementNull(data.get("Msg")))) {
                                    TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(data.get("Msg")));
                                } else {
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCreateTask")
                                            + "," + DataUtil.isDataElementNull(data.get("Msg")), mContext);
                                }

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("上传转款任务失败---->" + errorNo + "---->" + strMsg);
                dismissCustomDialog();
                btn_sure.setEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCreateTaskCauseByTimeout"), mContext);
                    }
                });
            }
        });
    }

    /**
     * 上传任务的数据
     */
    private HttpParams submitStyleChangeData(String TaskType, String TaskSubType, String sewingLine,
                                             String orderno, String TaskDescription, String receiver) {

        HttpParams params = new HttpParams();
        if (StringUtils.isNotBlank(TaskSubType)) {
            TaskType = TaskSubType;
        }
        //创建任务提交数据:创建人ID，任务类型"T01,T02"等，机台号（数组），任务描述，组别
        JsonObjectElement task = new JsonObjectElement();
        JsonObjectElement taskDetail = new JsonObjectElement();
        //获取创建人ID
        task.set("createuserno", creatorId);//任务发起人ID
        taskDetail.set("orderno", orderno);
        try {
            String temp = DataUtil.dateToStamp(TaskDescription);
            LogUtils.e("时间戳---->" + temp);
            taskDetail.set("starttime", temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        taskDetail.set("TaskApplicantOrg",sewingLineID);
        taskDetail.set("sewingline", sewingLine);
        task.set("factory", getLoginInfo().getFactoryId());//任务发起人所属班组
        task.set("description", "");
        task.set("status", 1);
        task.set("ordertask", taskDetail);
        //kingzhang add 20210414
        task.set("receiver", receiver);

        //包装数据
        //填写创建人角色
        if (OperatorInfo != null) {
            task.set("createusername", getLoginInfo().getName());
        } else {
            task.set("createusername", getLoginInfo().getName());
        }

        params.putJsonParams(task.toJson());
        LogUtils.e("上传转款任务数据---->" + task.toString());
        return params;

    }

    private void submitTask(String TaskType, String TaskSubType, String standardWorkload, String equipmentName
            , String MachineCode, String TaskDescription) {
        btn_sure.setEnabled(false);
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        if (StringUtils.isNotBlank(TaskSubType)) {
            TaskType = TaskSubType;
        }
        //创建任务提交数据:创建人ID，任务类型"T01,T02"等，机台号（数组），任务描述，组别
        JsonObjectElement task = new JsonObjectElement();
        JsonObjectElement taskDetail = new JsonObjectElement();
        //获取创建人ID
        taskDetail.set("Applicant", creatorId);//任务发起人ID
        taskDetail.set("Task_ID", 0);
        taskDetail.set("ModuleType", moduleType);
        try {
            if (getIntent().getStringExtra(Constants.FLAG_FAULT_NOT_SOLVE) != null) {
                taskDetail.set("FromTaskType", "1");//任务发起人ID
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
        //create by jason 2019/04/10 增加手机端表示
        taskDetail.set("Source", "App");
        taskDetail.set("TaskDescr", TaskDescription);//任务描述
        if (task_type_class.get(TaskType) != null) {//任务类型
            taskDetail.set("TaskClass", task_type_class.get(TaskType));
        }
        taskDetail.set("TaskApplicantOrg", teamId);//任务发起人组别
        taskDetail.set("Factory", getLoginInfo().getFactoryId());//任务发起人所属班组
        taskDetail.set("IsExsitTaskEquipment", HasEquipment_map.get(hasEquipment.getText()));//是否为设备相关任务
        //TODO
        if (task_type_class.get(TaskType).equals(Task.MOVE_CAR_TASK)) {

            taskDetail.set("MoveTo_ID", targetOrganiseID);
        }
        //kingzhang for srf 2022-0106 提交任务判断输借出或者借入类型，进行数据获取
        if (task_type_class.get(TaskType).equals(Task.Lend_TASK) || task_type_class.get(TaskType).equals(Task.Borrow_TASK)) {
            taskDetail.set("move_from_id", TARGET_ORGANISE_Form_ID);
            taskDetail.set("MoveTo_ID",iSCross?TARGET_ORGANISE_To_cross_ID:TARGET_ORGANISE_To_ID);
            taskDetail.set("TaskApplicantOrg", TARGET_ORGANISE_Form_ID);
            taskDetail.set("isCross", iSCross);
            taskDetail.set("email_addressee", equipment_PeopleInformation_Email_ID);
            taskDetail.set("task_confirm_user", equipment_PeopleInformation_Approve_ID);
            taskDetail.set("TaskDescr", ((EditText) findViewById(R.id.task_equipment_description)).getText().toString());
        }

        if (IntentTaskSubClass != null) {
            taskDetail.set("TaskClass", Task.MAINTAIN_TASK);
            taskDetail.set("TaskSubClass", IntentTaskSubClass);
            if (IntentTaskItem != null) {
                ArrayList<ObjectElement> TaskItemList = new ArrayList<>();
                JsonObjectElement TaskItem = new JsonObjectElement();
                JsonObjectElement jsonObjectElement = new JsonObjectElement(IntentTaskItem);
                TaskItem.set("MaintainItem_ID", jsonObjectElement.get("MaintainItem_ID").valueAsInt());
                TaskItem.set("WorkTimeCode", DataUtil.isDataElementNull(jsonObjectElement.get("WorkTimeCode")));
                //create by jason 2019/3/21 新增关联字段
//                TaskItem.set("RelateTask_ID",jsonObjectElement.get("RelateTask_ID").valueAsString());
                TaskItemList.add(TaskItem);
                JsonArrayElement jsonArrayElement = new JsonArrayElement(TaskItemList.toString());
                task.set("TaskItem", jsonArrayElement);
            }
        }

        if (FromTask_ID != null) {
            taskDetail.set("FromTask_ID", FromTask_ID);
        }
        if (!SimpleDescriptionCode.equals("")) {
            taskDetail.set("TaskDescrCode", SimpleDescriptionCode);
        }

        for (Map.Entry<Integer, ObjectElement> entry : dynamicControlData.entrySet()) {
            ObjectElement it = entry.getValue();
            if (it != null && it.get("DataCode") != null) {
                if ("DDL".equals(it.get("DataValue1").valueAsString())) {
                    ObjectElement selectData = dynamicSelectData.get(entry.getKey());
                    if (selectData != null && selectData.get("DataCode") != null) {
                        taskDetail.set(it.get("DataCode").valueAsString(),
                                selectData.get("DataCode").valueAsString());
                    }
                } else if ("TXT".equals(it.get("DataValue1").valueAsString())) {
                    View v = dynamicControl.get(entry.getKey());
                    if (v != null) {
                        String data = ((EditText) v.findViewById(R.id.dynomicdata)).getText().toString();
                        if (data != null) {
                            taskDetail.set(it.get("DataCode").valueAsString(),
                                    data);
                        }
                    }
                } else {
                    JsonObject selectData = dynamicSelectData2.get(entry.getKey());
                    if (selectData != null && selectData.get("DataCode") != null) {
                        taskDetail.set(it.get("DataCode").valueAsString(),
                                selectData.get("DataCode").getAsString());
                    }
                }
            }
        }

        task.set("Task", taskDetail);

        if (moduleType.equals("Property")) {
            if (!MachineCode.equals("")) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement();
                jsonObjectElement.set("Equipment_ID", MachineCode);
                jsonObjectElement.set("TaskEquipment_ID", 0);
                ArrayList<ObjectElement> list = new ArrayList<>();
                list.add(jsonObjectElement);
                JsonArrayElement jsonArrayElement = new JsonArrayElement(list.toString());
                task.set("TaskEquipment", jsonArrayElement);
            }

        } else {
            if (!equipmentID.equals("")) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement();
                jsonObjectElement.set("Equipment_ID", MachineCode);
                jsonObjectElement.set("TaskEquipment_ID", 0);
                ArrayList<ObjectElement> list = new ArrayList<>();
                list.add(jsonObjectElement);
                JsonArrayElement jsonArrayElement = new JsonArrayElement(list.toString());
                task.set("TaskEquipment", jsonArrayElement);
            }
        }
        //包装数据
        //填写创建人角色
        if (OperatorInfo != null) {
            task.set("UserRole_ID", RootUtil.ROOT_WARRANTY);
        } else {
            task.set("UserRole_ID", SharedPreferenceManager.getUserRoleID(this));
        }

        task.set("ModuleType", moduleType);
        task.set("TempBillNo", TempBillNo);
        params.putJsonParams(task.toJson());
        LogUtils.e("请求的数据task.toJson()--->" + task.toJson());
        LogUtils.e("请求的数据--->" + params.getJsonParams());
        HttpUtils.postWithoutCookie(this, "TaskCollection", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                btn_sure.setEnabled(true);
                if (t != null) {
                    final JsonObjectElement data = new JsonObjectElement(t);
                    LogUtils.e("上传任务成功--->" + data.toString());
                    if (!TextUtils.isEmpty(data.get(Data.USER_DATA).valueAsString())) {
                        RelateTask_ID = data.get(Data.USER_DATA).valueAsString();
                        //数据是使用Intent返回
                        Intent intent = new Intent();
                        LogUtils.e("开始回传数据---->" + RelateTask_ID);
                        //把返回数据存入Intent
                        intent.putExtra("RelateTask_ID", RelateTask_ID);
                        setResult(1001, intent);
                    }
                    if (data.get(Data.SUCCESS).valueAsBoolean()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_sure.setEnabled(false);
                                ToastUtil.showToastLong(LocaleUtils.getI18nValue("SuccessCreateTask"), mContext);
                            }
                        });
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        }).start();


                    } else {
//                        if(!DataUtil.isDataElementNull(data.get("Msg")).equals("")){
//                            ToastUtil.showToastShort(DataUtil.isDataElementNull(data.get("Msg")),mContext);
//                        }else {
//                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCreateTask,mContext);
//                        }
                        LogUtils.e("上传任务失败--->" + DataUtil.isDataElementNull(data.get("Msg")));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(DataUtil.isDataElementNull(data.get("Msg")))) {
//                                    TipsUtil.ShowTips(mContext,LocaleUtils.getI18nValue("creat_task_fail_1"));
                                    TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(data.get("Msg")));
                                } else {
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCreateTask")
                                            + "," + DataUtil.isDataElementNull(data.get("Msg")), mContext);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                btn_sure.setEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCreateTaskCauseByTimeout"), mContext);
                    }
                });
            }
        });
    }

    /**
     * create by jason
     *
     * @param DataValue2
     * @param EquipmentClass
     */
    private void getSimpleDescriptionByServe(String DataValue2, String EquipmentClass) {

        HttpParams params = new HttpParams();

        params.put("DataType", "EquipmentClassTrouble");
        params.put("DataValue1", EquipmentClass);
        params.put("DataValue2", "01");
        LogUtils.e("EquipmentClass--->" + EquipmentClass);
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.get(this, "DataRelation/GetDataRelationSimple", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                ClearDescriptionList();
                if (jsonArrayElement != null && jsonArrayElement.isArray() && jsonArrayElement.asArrayElement().size() > 0) {
                    LogUtils.e("getSimpleDescription---数量---->" + jsonArrayElement.asArrayElement().size());
                    LogUtils.e("getSimpleDescription----element--->" + jsonArrayElement.asArrayElement().toString());
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        //翻译DATA_NAME
                        jsonArrayElement.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                        mSimpleDescriptionList.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }
                }
                LogUtils.e("getSimpleDescriptionByServe--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noSimpleDescriptionData"), mContext);
//                Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                LogUtils.e("getSimpleDescriptionByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    private void getSimpleDescription(String EquipmentClass) {
        String DataValue2;
        if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) != RootUtil.ROOT_WARRANTY) {
            DataValue2 = "'01','03'";
        } else {
            DataValue2 = "'01','02'";
        }
        getSimpleDescriptionByServe(DataValue2, EquipmentClass);
        //更改为服务端数据来源
//        DataUtil.getDataFromDataBase(mContext, "EquipmentClassTrouble", EquipmentClass, DataValue2, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//
//                ClearDescriptionList();
//                if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
//                    LogUtils.e("getSimpleDescription----element--->"+element.asArrayElement().toString());
//                    for (int i = 0; i < element.asArrayElement().size(); i++) {
//                        //翻译DATA_NAME
//                        element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
//                        mSimpleDescriptionList.add(element.asArrayElement().get(i).asObjectElement());
//                    }
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("noSimpleDescriptionData"), mContext);
//                    }
//                });
//            }
//        });
    }

    /**
     * create by jason
     *
     * @param equipmentID
     */
    private void getOrganiseNameAndEquipmentNameByEquipmentIDByServe(String equipmentID) {

        HttpParams params = new HttpParams();
//        JsonObjectElement checkJson =  new JsonObjectElement();
//        checkJson.set("id",equipmentID);
//        params.putJsonParams(checkJson.toJson());

        params.put("id", equipmentID);
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.get(this, "Equipment/" + equipmentID, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("null") || TextUtils.isEmpty(t)) {
                    LogUtils.e("获取数据库为空");
                    return;
                }
                dismissCustomDialog();
                LogUtils.e("getOrganiseNameAndEquipmentNameByEquipmentIDByServe--测试成功--->" + t);
                DataElement jsonArrayElement = new JsonObjectElement(t);
                if (jsonArrayElement != null) {
                    LogUtils.e("getOrganiseNameAndEquipmentNameByEquipmentID----element--->" + jsonArrayElement.asObjectElement().toString());

                    final ObjectElement data = jsonArrayElement.asObjectElement();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tag = true;
                            group.setText(DataUtil.isDataElementNull(data.get("UseOrganiseName")));
                            teamId = DataUtil.isDataElementNull(data.get("Organise_ID_Use"));
                            device_name.setText(DataUtil.isDataElementNull(data.get("EquipmentName")));
                            DeviceName = DataUtil.isDataElementNull(data.get("EquipmentClass"));
                            // getTeamIdByOrganiseID(DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("Organise_ID_Use")));
                            getSimpleDescription(DeviceName);
                            simple_description.getmEditText().setText("");
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("getOrganiseNameAndEquipmentNameByEquipmentIDByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    private void getOrganiseNameAndEquipmentNameByEquipmentID(String equipmentID) {
        LogUtils.e("equipmentID---->" + equipmentID);
        getOrganiseNameAndEquipmentNameByEquipmentIDByServe(equipmentID);
        //更改为服务端数据来源
//        String sql = "select OrganiseName,EquipmentName,EquipmentClass,Organise_ID_Use from Equipment e,BaseOrganise b" +
//                "    where Equipment_ID='" + equipmentID + "'" +
//                "    and e.Organise_ID_Use=b.Organise_ID";
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, new StoreCallback() {
//            @Override
//            public void success(final DataElement element, String resource) {
//                LogUtils.e("getOrganiseNameAndEquipmentNameByEquipmentID----element--->"+element.asArrayElement().toString());
//                if (element != null) {
//                    LogUtils.e("getOrganiseNameAndEquipmentNameByEquipmentID----element--->"+element.asArrayElement().toString());
//                    if (element.isArray()) {
//                        if (element.asArrayElement().size() > 0) {
//                            final ObjectElement data = element.asArrayElement().get(0).asObjectElement();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tag = true;
//                                    group.setText(DataUtil.isDataElementNull(data.get("OrganiseName")));
//                                    teamId = DataUtil.isDataElementNull(data.get("Organise_ID_Use"));
//                                    device_name.setText(DataUtil.isDataElementNull(data.get("EquipmentName")));
//                                    DeviceName = DataUtil.isDataElementNull(data.get("EquipmentClass"));
//                                    // getTeamIdByOrganiseID(DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("Organise_ID_Use")));
//                                    getSimpleDescription(DeviceName);
//                                    simple_description.getmEditText().setText("");
//                                }
//                            });
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                LogUtils.e("getOrganiseNameAndEquipmentNameByEquipmentID---ex--->"+ex.toString());
//            }
//        });
    }

    /**
     * create by jason
     *
     * @param iccardID
     */
    private void getEquipmentNumByICcardIdByEquipmentIDByServe(final String iccardID, boolean isQRCode) {
        HttpParams params = new HttpParams();
        if (isQRCode) {
//            params.put("IDType","1");
            params.put("CodeID", iccardID);
        } else {
//            params.put("IDType","2");
            params.put("CodeID", iccardID);
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.get(this, "Equipment/GetEquipmentByCardOrQRCode", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                LogUtils.e("getEquipmentNumByICcardIdByEquipmentIDByServe---数量--->");
                LogUtils.e("getEquipmentNumByICcardIdByEquipmentIDByServe--测试成功--->" + t);
                if (t == null && !"null".equals(t)) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    LogUtils.e("jsonArrayElement-ICCardID--->" + jsonArrayElement.asArrayElement().toString() + "--数量--->" + jsonArrayElement.asArrayElement().size());
                    final ObjectElement objectElement = jsonArrayElement.asArrayElement().get(0).asObjectElement();

                    //作用:如果teamId为空时 先查找teamId 然后自动显示部门  Jason 2020/1/8 下午2:17
                    if (TextUtils.isEmpty(teamId)) {
                        if (mTeamNamelist != null) {
                            for (int i = 0; i < mTeamNamelist.size(); i++) {
                                if (DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID"))
                                        .equals(DataUtil.isDataElementNull(objectElement.get(Equipment.ORGANISE_ID_USE)))) {
                                    group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(i).get("TeamName")));
                                    teamId = DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID"));
                                }
                            }
                            getDeviceName();
                        }
                    } else {
                        if (!DataUtil.isDataElementNull(objectElement.get(Equipment.ORGANISE_ID_USE)).equals(teamId)) {
                            if (mTeamNamelist != null) {
                                for (int i = 0; i < mTeamNamelist.size(); i++) {
                                    if (DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID"))
                                            .equals(DataUtil.isDataElementNull(objectElement.get(Equipment.ORGANISE_ID_USE)))) {
                                        group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(i).get("TeamName")));
                                        teamId = DataUtil.isDataElementNull(mTeamNamelist.get(i).get("Team_ID"));
                                    }
                                }
                                getDeviceName();
                            }
                        }
                    }
                    LogUtils.e("teamId--->" + teamId);
                    //if (Group_ID_List.contains(DataUtil.isDataElementNull(objectElement.get("Organise_ID_Use")))) {
                    if (DataUtil.isDataElementNull(objectElement.get(Equipment.ORGANISE_ID_USE)).equals(teamId)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.e("进入设备填值--->");
                                device_num.setText(DataUtil.isDataElementNull(objectElement.get(Equipment.ORACLE_ID)));
                                equipmentName = DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_NAME));
                                device_name.getmEditText().setText(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_NAME)));
                                DeviceName = DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_CLASS));
                                //getTeamIdByOrganiseID(DataUtil.isDataElementNull(objectElement.get("Organise_ID_Use")));
                                tag = true;
                                oracleId = DataUtil.isDataElementNull(objectElement.get(Equipment.ORACLE_ID));
                                resetDeviceName();
                                canDelete = false;

                            }
                        });
                        equipmentID = DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getEquipmentNumSuccess"), mContext);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailToScanEquipment, mContext);
                                showDataSyncDialog(LocaleUtils.getI18nValue("FailToScanEquipment"));
                            }
                        });
                    }
                } else {
                    LogUtils.e("当前数据不是数组---");
                    showDataSyncDialog(LocaleUtils.getI18nValue("NoCardDetailDoYouNeedToSyncData"));
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoCardDetail"), mContext);
                LogUtils.e("getEquipmentNumByICcardIdByEquipmentIDByServe--测试失败--->" + errorNo + "----" + strMsg);
//                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
//                dismissCustomDialog();
            }
        });

    }

    private void getEquipmentNumByICcardId(final String iccardID, boolean isQRCode) {
        String rawQuery;
        LogUtils.e("iccardID--->" + iccardID + "---->" + isQRCode);
        getEquipmentNumByICcardIdByEquipmentIDByServe(iccardID, isQRCode);
//        if (isQRCode) {
//            //更改为服务端数据来源
////            if(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID)==null){
//            rawQuery = "SELECT * FROM Equipment WHERE  Equipment_KyID ='" + iccardID + "'";
////            }else {
////                switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID))){
////                    case "1":{
////                        rawQuery = "SELECT * FROM Equipment WHERE  ICCardID ='" + iccardID + "'";
////                        break;
////                    }
////                    case "2":{
////                        rawQuery = "SELECT * FROM Equipment WHERE  AssetsID ='" + iccardID + "'";
////                        break;
////                    }
////                    default:{
////                        rawQuery = "SELECT * FROM Equipment WHERE  AssetsID ='" + iccardID + "'";
////                        break;
////                    }
////                }
////            }
//        } else {
//            //更改为服务端数据来源
//            rawQuery = "SELECT * FROM Equipment WHERE  ICCardID ='" + iccardID + "' ";
//        }
//        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
//        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//
//            @Override
//            public void onSuccess(DataElement dataElement) {
//                if (dataElement != null && dataElement.isArray()
//                        && dataElement.asArrayElement().size() > 0) {
//                    LogUtils.e("dataElement-ICCardID--->"+dataElement.asArrayElement().toString());
//                    final ObjectElement objectElement = dataElement.asArrayElement().get(0).asObjectElement();
//                    //if (Group_ID_List.contains(DataUtil.isDataElementNull(objectElement.get("Organise_ID_Use")))) {
//                    if (DataUtil.isDataElementNull(objectElement.get(Equipment.ORGANISE_ID_USE)).equals(teamId)) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                device_num.setText(DataUtil.isDataElementNull(objectElement.get(Equipment.ORACLE_ID)));
//                                equipmentName = DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_NAME));
//                                device_name.getmEditText().setText(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_NAME)));
//                                DeviceName = DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_CLASS));
//                                //getTeamIdByOrganiseID(DataUtil.isDataElementNull(objectElement.get("Organise_ID_Use")));
//                                tag = true;
//                                resetDeviceName();
//
//
//                            }
//                        });
//                        equipmentID = DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID));
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getEquipmentNumSuccess"), mContext);
//                            }
//                        });
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailToScanEquipment, mContext);
//                                showDataSyncDialog(LocaleUtils.getI18nValue("FailToScanEquipment"));
//                            }
//                        });
//                    }
//                } else {
//                    showDataSyncDialog(LocaleUtils.getI18nValue("NoCardDetailDoYouNeedToSyncData"));
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoCardDetail"), mContext);
//            }
//        });
    }

    private void resetCretor() {

        //Raymond
        //task_type.getmEditText().setText("");
        //TaskType = "";

        teamId = "";
        group.getmEditText().setText("");
        equipmentName = "";
        device_name.getmEditText().setText("");
        DeviceName = "";
        device_num.setText("");
        equipmentID = "";
        simple_description.getmEditText().setText("");
        ClearDescriptionList();
    }

    private void resetFacility() {
        task_type.getmEditText().setText("");
        list_facility_park.getmEditText().setText("");
        list_facility_area.getmEditText().setText("");
        list_facility_type.getmEditText().setText("");
        list_facility_Name.getmEditText().setText("");
        simple_description.getmEditText().setText("");

        TaskType = "";
        list_facility_park_Value = "";
        list_facility_area_Value = "";
        list_facility_type_Value = "";
        list_facility_Name_Value = "";

        ClearDescriptionList();
    }

    private void resetFacilityName(String FieldValue) {
        if (FieldValue.equals("Property_Name")) {
            list_facility_area.getmEditText().setText("");
            list_facility_area_Value = "";
            mList_facility_area.clear();
            list_facility_area_class.clear();

            list_facility_type.getmEditText().setText("");
            list_facility_type_Value = "";
            mList_facility_type.clear();
            list_facility_type_class.clear();

        } else if (FieldValue.equals("Property_Type")) {
            list_facility_type.getmEditText().setText("");
            list_facility_type_Value = "";
            mList_facility_type.clear();
            list_facility_type_class.clear();
        }
        list_facility_Name.getmEditText().setText("");
        list_facility_Name_Value = "";
        mList_facility_Name.clear();
        list_facility_Name_class.clear();
    }

    private void resetTeam() {
        equipmentName = "";
        device_name.getmEditText().setText("");
        DeviceName = "";
        device_num.setText("");
        equipmentID = "";
        simple_description.getmEditText().setText("");
        ClearDescriptionList();
        getDeviceName();
    }

    private void resetEquipment() {
        equipmentName = "";
        device_name.getmEditText().setText("");
        DeviceName = "";
        device_num.setText("");
        equipmentID = "";
        simple_description.getmEditText().setText("");
        ClearDescriptionList();
    }

    private void resetDeviceName() {
        if (!tag) {
            device_num.setText("");
            equipmentID = "";
        }
//        getSimpleDescription(DeviceName);
        ORACLE_ID = oracleId;
        getGroupArrangeSimpleDesListByServe();
        simple_description.getmEditText().setText("");
        tag = false;
    }

    private void initSimpleDescription() {
        JsonObjectElement jsonObjectElement = new JsonObjectElement();
        jsonObjectElement.set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue("other"));
        jsonObjectElement.set(DataDictionary.DATA_CODE, "00");
        mSimpleDescriptionList.add(0, jsonObjectElement);
    }

    private void initHasEquipmentData() {
        JsonObjectElement data1 = new JsonObjectElement();
        data1.set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue("haveEquipment"));
        JsonObjectElement data2 = new JsonObjectElement();
        data2.set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue("NothaveEquipment"));
        mHasEquipment.add(data1);
        mHasEquipment.add(data2);
    }

    private void initData(boolean isRefresh) {
        BaseData.setBaseData(mContext);
        {
            HasEquipment_map.put(LocaleUtils.getI18nValue("haveEquipment"), 1);
            HasEquipment_map.put(LocaleUtils.getI18nValue("NothaveEquipment"), 0);
        }
        getTargetGroupList();
        //kingzhang add for srf 2022-0106
        GetFactoryDepartmentsByServe();
        GetFactoryDepartmentsByServe_cross();
        GetPeopleInformationByServe_Email();
        GetPeopleInformationByServe_Approve();
        getDictionaryListByServe("ModuleType", isRefresh);
    }

    //kingzhang add for srf 2022-0106
    //begin
    //获取邮件接收人
    private void GetPeopleInformationByServe_Email() {
        mEquipmentGroup_Email.clear();
        if((FromFactory.isEmpty())&&iSCross) {
            return;
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        httpParams.put("IsCrossFactory",iSCross?"true":"false");
        httpParams.put("IsEmailCheck", "true");
        if(iSCross) {
            httpParams.put("FromFactory", FromFactory);
        }
            HttpUtils.get(this, "Operator/GetPeopleInformation", httpParams, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    dismissCustomDialog();
                    if (TextUtils.isEmpty(t) || t.equals("null")) {
                        return;
                    }
                    DataElement jsonArrayElement = new JsonArrayElement(t);
                    if (jsonArrayElement != null && jsonArrayElement.isArray()) {
                        LogUtils.e("GetPeopleInformation---数量--->" + jsonArrayElement.asArrayElement().size());
                        LogUtils.e("GetPeopleInformation--elment--->" + jsonArrayElement.asArrayElement().toString() + "--数量--->" + jsonArrayElement.asArrayElement().size());
                        for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                            mEquipmentGroup_Email.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mEquipmentGroup_Email.size() == 1) {
                                    equipmentEmail.setText(DataUtil.isDataElementNull(mEquipmentGroup_Email.get(0).get(BaseOrganise.PeopleInfor_equipment)));
                                    equipment_PeopleInformation_Email_ID = DataUtil.isDataElementNull(mEquipmentGroup_Email.get(0).get(BaseOrganise.PeopleInfor_Id_equipment));
                                }
                            }
                        });
                    }
                    LogUtils.e("GetPeopleInformationByServe-获取成功-->" + t);

                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    dismissCustomDialog();
                    HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                    LogUtils.e("GetPeopleInformationByServe--获取失败--->" + errorNo + "--strMsg-->" + strMsg);
                    //ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
                }
            });

//        HttpParams params = new HttpParams();
//
//        HttpUtils.get(this, "/api/BaseOrganise?$filter=OrganiseClass eq 0 and fromfactory eq "+"'"+getLoginInfo().getFactoryId()+"'" +"and OrgainiseType gt 1 and Status eq 1", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                LogUtils.e("getTargetGroupListByServe-获取成功-->"+t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+errorNo+"--strMsg-->"+strMsg);
//            }
//
//            @Override
//            public void onFailure(VolleyError error) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+error.getMessage());
//            }
//        });

    }
    //获取工单确认人
    private void GetPeopleInformationByServe_Approve() {
        mEquipmentGroup_Approve.clear();
        if((FromFactory.isEmpty())&&iSCross) {
            return;
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        httpParams.put("IsCrossFactory",iSCross?"true":"false");
        httpParams.put("IsEmailCheck", "false");
        if(iSCross) {
            httpParams.put("FromFactory", FromFactory);
        }
            HttpUtils.get(this, "Operator/GetPeopleInformation", httpParams, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    dismissCustomDialog();
                    if (TextUtils.isEmpty(t) || t.equals("null")) {
                        return;
                    }
                    DataElement jsonArrayElement = new JsonArrayElement(t);
                    if (jsonArrayElement != null && jsonArrayElement.isArray()) {
                        LogUtils.e("GetPeopleInformation---数量--->" + jsonArrayElement.asArrayElement().size());
                        LogUtils.e("GetPeopleInformation--elment--->" + jsonArrayElement.asArrayElement().toString() + "--数量--->" + jsonArrayElement.asArrayElement().size());
                        for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                            mEquipmentGroup_Approve.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mEquipmentGroup_Approve.size() == 1) {
                                    equipmentApprove.setText(DataUtil.isDataElementNull(mEquipmentGroup_Approve.get(0).get(BaseOrganise.PeopleInfor_equipment)));
                                    equipment_PeopleInformation_Approve_ID = DataUtil.isDataElementNull(mEquipmentGroup_Approve.get(0).get(BaseOrganise.PeopleInfor_Id_equipment));
                                }
                            }
                        });
                    }
                    LogUtils.e("GetPeopleInformationByServe-获取成功-->" + t);

                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    dismissCustomDialog();
                    HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                    LogUtils.e("GetPeopleInformationByServe--获取失败--->" + errorNo + "--strMsg-->" + strMsg);
                    //ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
                }
            });

//        HttpParams params = new HttpParams();
//
//        HttpUtils.get(this, "/api/BaseOrganise?$filter=OrganiseClass eq 0 and fromfactory eq "+"'"+getLoginInfo().getFactoryId()+"'" +"and OrgainiseType gt 1 and Status eq 1", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                LogUtils.e("getTargetGroupListByServe-获取成功-->"+t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+errorNo+"--strMsg-->"+strMsg);
//            }
//
//            @Override
//            public void onFailure(VolleyError error) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+error.getMessage());
//            }
//        });

    }
    //本厂部门信息
    private void GetFactoryDepartmentsByServe() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        httpParams.put("Factory", getLoginInfo().getFactoryId());
        mEquipmentOrganise.clear();
        equipment_targetOrganiseID=null;
        HttpUtils.get(this, "BaseOrganise/GetFactoryDepartments", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()) {
                    LogUtils.e("GetFactoryDepartments---数量--->" + jsonArrayElement.asArrayElement().size());
                    LogUtils.e("GetFactoryDepartments--elment--->" + jsonArrayElement.asArrayElement().toString() + "--数量--->" + jsonArrayElement.asArrayElement().size());
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        mEquipmentOrganise.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mEquipmentOrganise.size() == 1) {
                                ((DropEditText) findViewById(R.id.equipment_group_from_id)).setText(DataUtil.isDataElementNull(mEquipmentOrganise.get(0).get(BaseOrganise.ORGANISENAME_equipment)));
                                equipment_targetOrganiseID = DataUtil.isDataElementNull(mEquipmentOrganise.get(0).get(BaseOrganise.ORGANISE_ID_equipment));

                                ((DropEditText) findViewById(R.id.equipment_group_to_id)).setText(DataUtil.isDataElementNull(mEquipmentOrganise.get(0).get(BaseOrganise.ORGANISENAME_equipment)));
                                equipment_targetOrganiseID = DataUtil.isDataElementNull(mEquipmentOrganise.get(0).get(BaseOrganise.ORGANISE_ID_equipment));
                            }
                        }
                    });
                }
                LogUtils.e("GetPeopleInformationByServe-获取成功-->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                LogUtils.e("GetFactoryDepartmentsByServe--获取失败--->" + errorNo + "--strMsg-->" + strMsg);
                //ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
            }
        });

//        HttpParams params = new HttpParams();
//
//        HttpUtils.get(this, "/api/BaseOrganise?$filter=OrganiseClass eq 0 and fromfactory eq "+"'"+getLoginInfo().getFactoryId()+"'" +"and OrgainiseType gt 1 and Status eq 1", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                LogUtils.e("getTargetGroupListByServe-获取成功-->"+t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+errorNo+"--strMsg-->"+strMsg);
//            }
//
//            @Override
//            public void onFailure(VolleyError error) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+error.getMessage());
//            }
//        });

    }
    //跨厂部门信息
    private void GetFactoryDepartmentsByServe_cross() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        //httpParams.put("Factory", getLoginInfo().getFactoryId());
        httpParams.put("IsCrossFactory", "true");

        mEquipmentOrganise_cross.clear();
        equipment_targetOrganiseID=null;
        HttpUtils.get(this, "BaseOrganise/GetFactoryDepartments", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()) {
                    LogUtils.e("GetFactoryDepartmentsByServe_cross---数量--->" + jsonArrayElement.asArrayElement().size());
                    LogUtils.e("GetFactoryDepartmentsByServe_cross--elment--->" + jsonArrayElement.asArrayElement().toString() + "--数量--->" + jsonArrayElement.asArrayElement().size());
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        mEquipmentOrganise_cross.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mEquipmentOrganise_cross.size() == 1) {
                                ((DropEditText) findViewById(R.id.equipment_group_to_id_cross)).setText(DataUtil.isDataElementNull(mEquipmentOrganise_cross.get(0).get(BaseOrganise.ORGANISENAME_equipment_cross)));
                                equipment_targetOrganiseID = DataUtil.isDataElementNull(mEquipmentOrganise_cross.get(0).get(BaseOrganise.ORGANISE_ID_equipment));
                            }
                        }
                    });
                }
                LogUtils.e("GetFactoryDepartmentsByServe_cross-获取成功-->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                LogUtils.e("GetFactoryDepartmentsByServe_cross--获取失败--->" + errorNo + "--strMsg-->" + strMsg);
                //ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
            }
        });

//        HttpParams params = new HttpParams();
//
//        HttpUtils.get(this, "/api/BaseOrganise?$filter=OrganiseClass eq 0 and fromfactory eq "+"'"+getLoginInfo().getFactoryId()+"'" +"and OrgainiseType gt 1 and Status eq 1", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                LogUtils.e("getTargetGroupListByServe-获取成功-->"+t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+errorNo+"--strMsg-->"+strMsg);
//            }
//
//            @Override
//            public void onFailure(VolleyError error) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+error.getMessage());
//            }
//        });

    }
    //end
    /**
     * create by jason
     *
     * @param
     */
    private void getTargetGroupListByServe() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        HttpUtils.post(this, "BaseOrganise/APPGet?Parameter=filter%3DOrganiseClass%20%3C%202%20and%20fromfactory%20eq%20'" + getLoginInfo().getFactoryId() + "'%20and%20OrganiseType%20gt%201%20and%20Status%20eq%201", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()) {
                    LogUtils.e("getTargetGroupList---数量--->" + jsonArrayElement.asArrayElement().size());
                    LogUtils.e("getTargetGroupList--elment--->" + jsonArrayElement.asArrayElement().toString() + "--数量--->" + jsonArrayElement.asArrayElement().size());
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        mTargetGroup.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mTargetGroup.size() == 1) {
                                ((DropEditText) findViewById(R.id.target_group)).setText(DataUtil.isDataElementNull(mTargetGroup.get(0).get(BaseOrganise.ORGANISENAME)));
                                targetOrganiseID = DataUtil.isDataElementNull(mTargetGroup.get(0).get(BaseOrganise.ORGANISE_ID));
                            }
                        }
                    });
                }
                LogUtils.e("getTargetGroupListByServe-获取成功-->" + t);

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                LogUtils.e("getTargetGroupListByServe--获取失败--->" + errorNo + "--strMsg-->" + strMsg);
                //ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
            }
        });

//        HttpParams params = new HttpParams();
//
//        HttpUtils.get(this, "/api/BaseOrganise?$filter=OrganiseClass eq 0 and fromfactory eq "+"'"+getLoginInfo().getFactoryId()+"'" +"and OrgainiseType gt 1 and Status eq 1", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                LogUtils.e("getTargetGroupListByServe-获取成功-->"+t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+errorNo+"--strMsg-->"+strMsg);
//            }
//
//            @Override
//            public void onFailure(VolleyError error) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+error.getMessage());
//            }
//        });

    }

    //获取目标机台的列表信息
    private void getTargetGroupList() {
        String sql;
//        LogUtils.e("getLoginInfo().getFactoryId()---->"+getLoginInfo().getFactoryId());
        getTargetGroupListByServe();
        //更改为服务端数据来源
//        if(OperatorInfo!=null) {//OperatorInfo!=null即任务是从搬车或调车任务完成后，继续创建对应任务
//        sql = "select Organise_ID,OrganiseName from BaseOrganise where OrganiseClass=0 and FromFactory='" + getLoginInfo().getFactoryId() + "' and OrganiseType>1";
////        }else {
////            sql= "select Organise_ID,OrganiseName from BaseOrganise where Organise_ID in(" + getLoginInfo().getOrganiseID() + ") and OrganiseClass=0 and OrganiseType>1";
////        }
//        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                if (element != null && element.isArray()) {
//                    LogUtils.e("getTargetGroupList--elment--->"+element.asArrayElement().toString());
//                    for (int i = 0; i < element.asArrayElement().size(); i++) {
//                        mTargetGroup.add(element.asArrayElement().get(i).asObjectElement());
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mTargetGroup.size() == 1) {
//                                ((DropEditText) findViewById(R.id.target_group)).setText(DataUtil.isDataElementNull(mTargetGroup.get(0).get(BaseOrganise.ORGANISENAME)));
//                                targetOrganiseID = DataUtil.isDataElementNull(mTargetGroup.get(0).get(BaseOrganise.ORGANISE_ID));
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
//            }
//        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(mContext, LocaleUtils.getI18nValue("choose_sewing_sewing_line_no_exists"), Toast.LENGTH_SHORT).show();
                        mdropEdit_sew_line.getmEditText().setText("");
                        break;
                }
            }
        };
    }

    private void CreateFromMeasurePoint(String TaskType) {
        LogUtils.e("保养点任务--->" + TaskType);
        task_type.setText(TaskType);
        JsonObjectElement TaskEquipment = new JsonObjectElement(getIntent().getStringExtra("TaskEquipment"));
        SettingEquipmentDataAndInitSimpleDescription(TaskEquipment);
        DisableView();
        getOrganiseNameAndEquipmentNameByEquipmentID(equipmentID);
    }

    private void CreateShuntingTask(String TaskType) {
        task_type.setText(TaskType);
        SettingEquipmentDataAndInitSimpleDescription(EquipmentInfo);
        DisableView();
    }

    private void CreateCarMovingTask(String TaskType) {
        task_type.setText(TaskType);
        SettingEquipmentDataAndInitSimpleDescription(EquipmentInfo);
        DisableView();
        findViewById(R.id.target_group_layout).setVisibility(View.VISIBLE);
    }

    private void FALGNOTSOLVE(String TaskType) {
        task_type.setText(TaskType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        LogUtils.e("requestCode---->" + requestCode + "---->" + resultCode);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    LogUtils.e("获取到数据---->" + data.getStringExtra("result"));
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (result != null) {
                        //ToastUtil.showToastLong(result,mContext);
                        getDataByICcardID(result, true);
                    }
                }
                break;
            case 2://kingzhang add 20210414
                if (data != null) {
                    LogUtils.e("获取到数据---->" + data.getStringExtra("result"));
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (result != null) {
                        //ToastUtil.showToastLong(result,mContext);
                        //getDataByICcardID(result, true);
                        String[] all = result.split(";");
                        if (all.length > 1)
                            receiver_num.setText(all[0]);
                    }
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);

        if (moduleType.equals("Property")) {
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
                            //Bitmap bm = Bimp.revitionImageSize("/storage/emulated/0/1/1.jpg");
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

                            addImageUrlToDataList("file://" + SDPATH + fileName + ".JPEG", "0", Item_ID);
                            if (currentIdx != -1) {
                                GridAdapter gridAdapter = gridAdapterList.get(currentIdx);
                                gridAdapter.notifyDataSetChanged();

                            }
                        } catch (IOException e) {
                            CrashReport.postCatchedException(e);
                        }


                        //在此上传图片到服务器;
                        submitPictureToServer(path);
                    }
                    break;
                }
                case TAKE_photo: {
                    if (resultCode == -1) {
                        //Bimp.drr.add(path);
                        // 获取图库所选图片的uri
                        // 获取图库所选图片的uri
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String picturePath = cursor.getString(columnIndex);
                        LogUtils.e("测试相册路径---->" + picturePath);
                        //将图片地址增加到图片列表
                        Bimp.drr.add(picturePath);
                        String path = Bimp.drr.get(Bimp.max);
                        System.out.println(path);
                        try {
                            //Bitmap bm = Bimp.revitionImageSize("/storage/emulated/0/1/1.jpg");
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

                            addImageUrlToDataList("file://" + SDPATH + fileName + ".JPEG", "0", Item_ID);
                            if (currentIdx != -1) {
                                GridAdapter gridAdapter = gridAdapterList.get(currentIdx);
                                gridAdapter.notifyDataSetChanged();

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
                                //Equipment_ID = result;
                                //addTaskEquipment(result,true);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void showDataSyncDialog(final String message) {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(message);
                    builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            getDBDataLastUpdateTime();
                        }
                    }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    private void ClearDescriptionList() {
        mSimpleDescriptionList.clear();
        initSimpleDescription();
        if (!task_type.getText().equals("") && task_type_class.get(task_type.getText()) != null
                && task_type_class.get(task_type.getText()).equals(Task.GROUP_ARRANGEMENT)) {
            if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
                mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
            }
        }
    }

    private void DisableView() {
        task_type.getmEditText().setEnabled(false);
        task_type.getDropImage().setEnabled(false);
        group.getmEditText().setEnabled(false);
        group.getDropImage().setEnabled(false);
        device_num.setEnabled(false);
        findViewById(R.id.device_num_action).setEnabled(false);
        device_name.getmEditText().setEnabled(false);
        device_name.getDropImage().setEnabled(false);
        hasEquipment.getmEditText().setEnabled(false);
        hasEquipment.getDropImage().setEnabled(false);
        mdropEdit_sew_line.getmEditText().setEnabled(false);
        mdropEdit_sew_line.getDropImage().setEnabled(false);
        mdropEdit_order.getmEditText().setEnabled(false);
        mdropEdit_order.getDropImage().setEnabled(false);
    }

    /**
     * create by jason
     *
     * @param
     */
    private void getGroupArrangeSimpleDesListByServe() {

        HttpParams params = new HttpParams();
        String encodeOracleId = HttpUtils.toURLEncoded(ORACLE_ID);
        params.put("OracleID", encodeOracleId);
        params.put("TaskType", TaskType);

        HttpUtils.get(this, "DataRelation/GetDescription", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t == null || TextUtils.isEmpty(t) || t.equals("null")) {
                    LogUtils.e("t为空");
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement.asArrayElement().size() == 0) {
                    LogUtils.e("查询出来为null");
                    getDataIsNull();
                } else {
                    mGroupArrangeDescriptionList.clear();
                    mSimpleDescriptionList.clear();
                    for (DataElement e : jsonArrayElement.asArrayElement()) {

                        //翻译DATA_NAME
//                            LogUtils.e("执行有数据---->"+e.asObjectElement().toString());
                        e.asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(e.asObjectElement().get(DataDictionary.DATA_NAME))));
                        mGroupArrangeDescriptionList.add(e.asObjectElement());
                    }
                    LogUtils.e("一共获取数据---->" + mGroupArrangeDescriptionList.size());
                    mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
//                        LogUtils.e("有数据--->"+mSimpleDescriptionList.size());
//                        if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
//                            mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
//                        }
                    initDropSearchView(null, simple_description.getmEditText(), LocaleUtils.getI18nValue("simpleDescription"), "DataName", SIMPLE_DESCRIPTION, LocaleUtils.getI18nValue("NoEquipmentDescription"), simple_description.getDropImage());
                    try {
                        if (getIntent().getStringExtra(Constants.FLAG_FAULT_NOT_SOLVE) != null) {
                            for (int i = 0; i < mGroupArrangeDescriptionList.size(); i++) {
                                if (DataUtil.isDataElementNull(mGroupArrangeDescriptionList.get(i).get(DataDictionary.DATA_CODE)).equals(tasddesc_code)) {
                                    simple_description.getmEditText().setText(DataUtil.isDataElementNull(mGroupArrangeDescriptionList.get(i).get(DataDictionary.DATA_NAME)));
                                    SimpleDescriptionCode = DataUtil.isDataElementNull(mGroupArrangeDescriptionList.get(i).get(DataDictionary.DATA_CODE));
                                }
                            }
                        }
                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                    }
                }

                LogUtils.e("getGroupArrangeSimpleDesListByServe--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                LogUtils.e("getGroupArrangeSimpleDesListByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    //changed by jason 2019/3/28 更改查询条件
    private void getGroupArrangeSimpleDesList() {
        if (mGroupArrangeDescriptionList.size() >= 0) {
            mGroupArrangeDescriptionList.clear();
            mSimpleDescriptionList.clear();
        }

        LogUtils.e("开始执行简要描述语句");
        LogUtils.e("TaskType---->" + TaskType + "-ORACLE_ID--->" + ORACLE_ID);
        if (!TextUtils.isEmpty(TaskType) && !TextUtils.isEmpty(ORACLE_ID)) {
            getGroupArrangeSimpleDesListByServe();
            //更改为服务端数据来源
//                String sql = "select distinct DD.DataCode DataCode,DD.DataName DataName\n" +
//                        "from (select DataCode1,DataCode2 from DataRelation where RelationCode='01') DR\n" +
//                        "inner join (select * from DataDictionary where DataType='EquipmentClassTrouble'and Factory_ID="+"'"+getLoginInfo().getFactoryId()+"'"+")"+"DD on DR.DataCode1=DD.DataCode\n" +
//                        "inner join Equipment e on DR.DataCode2=e.EquipmentClass\n" +
//                        "where e.OracleID="+"'"+ORACLE_ID+"'" +
//                        "and ((dd.DataValue2=2 and "+"'"+TaskType+"'"+"<>'T04' and "+"'"+TaskType+"'"+"<>'T07') or (dd.DataValue2='3' and "+"'"+TaskType+"'"+"<>'T01') or dd.DataValue2='')";
//                LogUtils.e("查询语句--->"+sql);
//                ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(sql,
//                        EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, null);
//                EPassSqliteStoreOpenHelper helper = new EPassSqliteStoreOpenHelper(mContext);
//                helper.getDatabaseName();
//                Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//
//                    @Override
//                    public void onSuccess(DataElement dataElement) {
//                        LogUtils.e("执行有数据成功---->"+dataElement.asArrayElement().size());
//                        LogUtils.e("getGroupArrangeSimpleDesList--dataElement--->"+dataElement.asArrayElement().toString());
//                        if(dataElement.asArrayElement().size()==0){
//                            LogUtils.e("查询出来为null");
//                            getDataIsNull();
//                        }else{
//                            mGroupArrangeDescriptionList.clear();
//                            mSimpleDescriptionList.clear();
//                            for (DataElement e : dataElement.asArrayElement()) {
//
//                                //翻译DATA_NAME
//                                LogUtils.e("执行有数据---->"+e.asObjectElement().toString());
//                                e.asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(e.asObjectElement().get(DataDictionary.DATA_NAME))));
//                                mGroupArrangeDescriptionList.add(e.asObjectElement());
//                            }
//                            LogUtils.e("一共获取数据---->"+mGroupArrangeDescriptionList.size());
//                            mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
//                            LogUtils.e("有数据--->"+mSimpleDescriptionList.size());
////                        if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
////                            mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
////                        }
//                            initDropSearchView(null, simple_description.getmEditText(), LocaleUtils.getI18nValue("simpleDescription"), "DataName", SIMPLE_DESCRIPTION, LocaleUtils.getI18nValue("NoEquipmentDescription"), simple_description.getDropImage());
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        LogUtils.e("查询数据出错---->"+throwable.toString());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
//                            }
//                        });
//                    }
//                });
        } else {
            getDataIsNull();
//                  DataUtil.getDataFromDataBase(mContext, "EquipmentClassTrouble", "ZNAP000", "'01','02','03'", new StoreCallback() {
//                @Override
//
//                public void success(DataElement element, String resource) {
//                    for (DataElement e : element.asArrayElement()) {
//                        //翻译DATA_NAME
//                        LogUtils.e("原始执行有数据---->"+e.asObjectElement().toString());
//                        e.asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(e.asObjectElement().get(DataDictionary.DATA_NAME))));
//                        mGroupArrangeDescriptionList.add(e.asObjectElement());
//                    }
//
//                    if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
//                        mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
//                    }
//                }
//
//                @Override
//                public void failure(DatastoreException ex, String resource) {
//                    LogUtils.e("查询数据出错---->"+ex.toString());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
//                        }
//                    });
//                }
//            });
        }


    }

    /**
     * create by jason
     *
     * @param
     */
    private void getDataIsNullByServe() {

        HttpParams params = new HttpParams();
//        params.put("DataType","EquipmentClassTrouble");
//        params.put("factory_id",getLoginInfo().getFactoryId());
        String Parameter = HttpUtils.toURLEncoded("?$filter=DataType eq 'EquipmentClassTrouble' and factory_id eq " + "'" + getLoginInfo().getFactoryId() + "'");
        LogUtils.e("Parameter--->" + Parameter);
        params.put("Parameter", Parameter);
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'EquipmentClassTrouble'%20and%20factory_id%20eq%20'" + getLoginInfo().getFactoryId() + "'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElment = new JsonArrayElement(t);
                LogUtils.e("执行有数据成功---->" + jsonArrayElment.asArrayElement().size());
                mGroupArrangeDescriptionList.clear();
                mSimpleDescriptionList.clear();
                for (DataElement e : jsonArrayElment.asArrayElement()) {

                    //翻译DATA_NAME
//                        LogUtils.e("执行有数据---->"+e.asObjectElement().toString());
                    e.asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(e.asObjectElement().get(DataDictionary.DATA_NAME))));
                    mGroupArrangeDescriptionList.add(e.asObjectElement());
                }
                LogUtils.e("一共获取数据---->" + mGroupArrangeDescriptionList.size());
                mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
                LogUtils.e("有数据--->" + mSimpleDescriptionList.size());
//                        if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
//                            mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
//                        }
                initDropSearchView(null, simple_description.getmEditText(), LocaleUtils.getI18nValue("simpleDescription"), "DataName", SIMPLE_DESCRIPTION, LocaleUtils.getI18nValue("NoEquipmentDescription"), simple_description.getDropImage());

                LogUtils.e("getDataIsNullByServe--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
                LogUtils.e("getDataIsNullByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }


    /**
     * create by jason 2019/4/3 如果简要描述查出来为空，就按原来的方式查
     */
    private void getDataIsNull() {
        LogUtils.e("进入原来查询方法");
        getDataIsNullByServe();
        //更改为服务端数据来源
//        String sql = "select * from DataDictionary where DataType='EquipmentClassTrouble'";
//        LogUtils.e("查询语句--->"+sql);
//        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(sql,
//                EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, null);
//        EPassSqliteStoreOpenHelper helper = new EPassSqliteStoreOpenHelper(mContext);
//        helper.getDatabaseName();
//        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//
//            @Override
//            public void onSuccess(DataElement dataElement) {
//                LogUtils.e("执行有数据成功---->"+dataElement.asArrayElement().size());
//                    mGroupArrangeDescriptionList.clear();
//                    mSimpleDescriptionList.clear();
//                    for (DataElement e : dataElement.asArrayElement()) {
//
//                        //翻译DATA_NAME
////                        LogUtils.e("执行有数据---->"+e.asObjectElement().toString());
//                        e.asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(e.asObjectElement().get(DataDictionary.DATA_NAME))));
//                        mGroupArrangeDescriptionList.add(e.asObjectElement());
//                    }
//                    LogUtils.e("一共获取数据---->"+mGroupArrangeDescriptionList.size());
//                    mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
//                    LogUtils.e("有数据--->"+mSimpleDescriptionList.size());
////                        if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
////                            mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
////                        }
//                    initDropSearchView(null, simple_description.getmEditText(), LocaleUtils.getI18nValue("simpleDescription"), "DataName", SIMPLE_DESCRIPTION, LocaleUtils.getI18nValue("NoEquipmentDescription"), simple_description.getDropImage());
//
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                LogUtils.e("查询数据出错---->"+throwable.toString());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToastLong(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), mContext);
//                    }
//                });
//            }
//        });
    }

    private void SettingEquipmentDataAndInitSimpleDescription(JsonObjectElement data) {
        equipmentID = DataUtil.isDataElementNull(data.get(Equipment.EQUIPMENT_ID));
        equipmentName = DataUtil.isDataElementNull(data.get(Equipment.EQUIPMENT_NAME));
        device_name.setText(DataUtil.isDataElementNull(data.get(Equipment.EQUIPMENT_NAME)));
        device_num.setText(DataUtil.isDataElementNull(data.get(Equipment.ORACLE_ID)));
        DeviceName = DataUtil.isDataElementNull(data.get(Equipment.EQUIPMENT_CLASS));
        LogUtils.e("DeviceName--->" + DeviceName);
        getSimpleDescription(DeviceName);
        simple_description.getmEditText().setText("");
    }

    /**
     * create by jason
     *
     * @param
     */
    private void getTargetGroupListMoveCarByServe() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams = new HttpParams();
        HttpUtils.post(this, "BaseOrganise/APPGet?Parameter=filter%3DOrganiseClass%20eq%200%20and%20fromfactory%20eq%20'" + getLoginInfo().getFactoryId() + "'%20and%20OrganiseType%20gt%201%20and%20Status%20eq%201", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                mTeamNamelist.clear();
                LogUtils.e("getTeamIdByOrganiseIDByServe--获取成功--->" + t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        mTeamNamelist.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
//                        LogUtils.e("jsonArrayElement查询出来搬车任务部门--->" + jsonArrayElement.asArrayElement().size() + "---Name->" + jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }

                    LogUtils.e("jsonArrayElement查询出来的---->" + jsonArrayElement.asArrayElement().toString());
                    if (1 == mTeamNamelist.size()) {
                        teamId = DataUtil.isDataElementNull(mTeamNamelist.get(0).get(BaseOrganise.ORGANISE_ID));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                group.getmEditText().setText(DataUtil.isDataElementNull(mTargetGroup.get(0).get(BaseOrganise.ORGANISENAME)));
                                getDeviceName();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoOperatorGroup"), mContext);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("getTeamIdByOrganiseIDByServe--获取失败--->" + errorNo + "---->" + strMsg);
//                CrashReport.postCatchedException(throwable);
            }

        });
    }

    private void getRepairsFaclity(final String...RepairsType) {
        HttpParams params = new HttpParams();
        params.put("PropertyPark", list_facility_park_Value);
        params.put("PropertyArea", list_facility_area_Value);
        params.put("PropertyType", list_facility_type_Value);
        params.put("Field", FieldValue);

        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));

        //HttpUtils.post(mContext,"TaskAPI/TaskRecieve?task_id="+DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)), params, new HttpCallback() {

        String PropertyPark = "PropertyPark=" + list_facility_park_Value;
        String PropertyArea = "PropertyArea=" + list_facility_area_Value;
        String PropertyType = "PropertyType=" + list_facility_type_Value;
        String Field = "Field=" + FieldValue;

        String url = "PropertyFacility/GetRepairsFacility?" + PropertyPark + "&" + PropertyArea + "&" + PropertyType + "&" + Field;

        HttpUtils.post(this, url, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    return;
                }
                DataElement jsonArrayElement = new JsonArrayElement(t);
                LogUtils.e("getRepairsFaclity--->" + "----" + jsonArrayElement);
                for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                    if (FieldValue.equals("Property_Name")) {
                        mList_facility_area.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                        list_facility_area_class.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("DataName")),
                                DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("id")));
                    } else if (FieldValue.equals("Property_Type")) {
                        mList_facility_type.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                        list_facility_type_class.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("DataName")),
                                DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("id")));
                    } else if (FieldValue.equals("Facility_Name")) {
                        mList_facility_Name.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                        list_facility_Name_class.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("DataName")),
                                DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get("id")));
                    }
                }

                if (RepairsType.length > 0) {
                    if (RepairsType[0].equals("PropertyName")) {
                        //给予初始化默认值 By Leo
                        if (mList_facility_Name.size() > 0) {
                            list_facility_Name.getmEditText().setText(DataUtil.isDataElementNull(mList_facility_Name.get(0).get(DataDictionary.DATA_NAME)));
                            list_facility_Name_Value = list_facility_Name_class.get(DataUtil.isDataElementNull(mList_facility_Name.get(0).get(DataDictionary.DATA_NAME)));
                        }
                    }
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                LogUtils.e("getDictionaryListByServe--测试失败--->" + errorNo + "----" + strMsg);
            }

        });
    }

    //通用获取字典数据 By Leo 2021-08-05
    private void getDictionaryListByServe(final String DataType, final boolean isRefresh) {
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
                    DataElement jsonArrayElement = new JsonArrayElement(t);
                    ArrayList<ObjectElement> list = new ArrayList<>();
                    HashMap<String, String> classMap = new HashMap<>();
                    DropEditText dropEditText = null;
                    if (DataType.equals("ModuleType")) {
                        list = mModuleType;
                        classMap = module_type_class;
                        dropEditText = module_type;

                    } else if (DataType.equals("PropertyPark")) {
                        list = mList_facility_park;
                        classMap = list_facility_park_class;
                        dropEditText = list_facility_park;

                    } else if (DataType.equals("PropertyArea")) {
                        list = mList_facility_area;
                        classMap = list_facility_area_class;
                        dropEditText = list_facility_area;

                    } else if (DataType.equals("PropertyType")) {
                        list = mList_facility_type;
                        classMap = list_facility_type_class;
                        dropEditText = list_facility_type;
                    }

                    // By Leo 获取字典中包含VALUE1="default"的方法
                    int defaultIndex = 0;
                    if (jsonArrayElement != null && jsonArrayElement.isArray()
                            && jsonArrayElement.asArrayElement().size() > 0) {
                        list.clear();
                        for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {

                            list.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());

                            if (DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_VALUE1)).equals("default")) {
                                defaultIndex = i;
                            }

                            classMap.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
                                    DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
                        }
                    }

                    //list_facility_park, list_facility_area, list_facility_type, list_facility_Name
                    if (DataType.equals("PropertyPark")){
                        //园区
                        if(mList_facility_park.size()>0){
                            list_facility_park.getmEditText().setText(DataUtil.isDataElementNull(mList_facility_park.get(defaultIndex).get(DataDictionary.DATA_NAME)));
                            list_facility_park_Value = list_facility_park_class.get(DataUtil.isDataElementNull(mList_facility_park.get(defaultIndex).get(DataDictionary.DATA_NAME)));
                        }
                    }
                    if (DataType.equals("PropertyArea")){
                        //区域
                        if(mList_facility_area.size()>0){
                            list_facility_area.getmEditText().setText(DataUtil.isDataElementNull(mList_facility_area.get(defaultIndex).get(DataDictionary.DATA_NAME)));
                            list_facility_area_Value = list_facility_area_class.get(DataUtil.isDataElementNull(mList_facility_area.get(defaultIndex).get(DataDictionary.DATA_NAME)));
                        }
                    }
                    if (DataType.equals("PropertyType")){
                        //类型
                        if(mList_facility_type.size()>0){
                            list_facility_type.getmEditText().setText(DataUtil.isDataElementNull(mList_facility_type.get(defaultIndex).get(DataDictionary.DATA_NAME)));
                            list_facility_type_Value = list_facility_type_class.get(DataUtil.isDataElementNull(mList_facility_type.get(defaultIndex).get(DataDictionary.DATA_NAME)));
                        }
                    }
                    if (DataType.equals("PropertyName")){
                        //设施名称
                        FieldValue = "Facility_Name";
                        getRepairsFaclity("PropertyName");
                    }

                    if (DataType.equals("ModuleType")) {

                        if (!isRefresh) {
                            OldModuleType = moduleType = DataUtil.isDataElementNull(mModuleType.get(defaultIndex).get(DataDictionary.DATA_CODE));
                            module_type.getmEditText().setText(DataUtil.isDataElementNull(mModuleType.get(defaultIndex).get(DataDictionary.DATA_NAME)));
                        }

                        getTaskTypeByServe();

                        if (moduleType.equals("Property")) {
                            int sxViewIsGone = View.VISIBLE;
                            int sbViewIsGone = View.GONE;
                            resetCretor();
                            resetFacility();
                            getDictionaryListByServe("PropertyPark", false);
                            getDictionaryListByServe("PropertyArea", false);
                            getDictionaryListByServe("PropertyType", false);
                            getDictionaryListByServe("PropertyName", false);

                            ((TextView) findViewById(R.id.organise)).setText(LocaleUtils.getI18nValue("subordinate_departments"));
                            findViewById(R.id.linear_layout_transfer).setVisibility(View.GONE);

                            //让有无设备，设备名称，机器编码,任务描述及描述框那一行消失
                            findViewById(R.id.linear_layout_hasEquipment).setVisibility(sbViewIsGone);
                            findViewById(R.id.equipment_name).setVisibility(sbViewIsGone);
                            findViewById(R.id.equipment_num).setVisibility(sbViewIsGone);
                            findViewById(R.id.linear_layout_task_description).setVisibility(sbViewIsGone);

                            findViewById(R.id.group_id).setVisibility(sxViewIsGone);
                            findViewById(R.id.organise).setVisibility(sxViewIsGone);

                            findViewById(R.id.target_organise).setVisibility(sbViewIsGone);
                            findViewById(R.id.target_group).setVisibility(sbViewIsGone);
                            //让设施部分展现出来

                            findViewById(R.id.linear_layout_facility).setVisibility(sxViewIsGone);

                        }
                    }

                    //LogUtils.e("getDictionaryListByServe--mModuleType--->" + mModuleType);
                    //LogUtils.e("getDictionaryListByServe--测试成功--->" + t);
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

//        HttpParams params = new HttpParams();
//
//        HttpUtils.get(this, "/api/BaseOrganise?$filter=OrganiseClass eq 0 and fromfactory eq "+"'"+getLoginInfo().getFactoryId()+"'" +"and OrgainiseType gt 1 and Status eq 1", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                LogUtils.e("getTargetGroupListByServe-获取成功-->"+t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+errorNo+"--strMsg-->"+strMsg);
//            }
//
//            @Override
//            public void onFailure(VolleyError error) {
//                LogUtils.e("getTargetGroupListByServe--获取失败--->"+error.getMessage());
//            }
//        });

}



