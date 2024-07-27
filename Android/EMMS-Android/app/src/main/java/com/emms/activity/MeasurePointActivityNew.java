package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.Build;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.adapter.StatusAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.MeasurePoint;
import com.emms.schema.Task;
import com.emms.ui.ChangeEquipmentDialog;
import com.emms.ui.ChangeEquipmentDialog_YMG;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.ui.EquipmentCompleteListener;
import com.emms.ui.ExpandGridView;
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
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.lang.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/11.
 */
public class MeasurePointActivityNew extends NfcActivity implements View.OnClickListener {
    private PullToRefreshListView Measure_Point_ListView;
    private ArrayList<ObjectElement> measure_point_list = new ArrayList<>();
    private TaskAdapter adapter;
    private Context context = this;
    //    private int PAGE_SIZE=10;
//    private int pageIndex=1;
//    private int RecCount=0;
    private Handler handler = new Handler();

    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private TextView menuSearchTitle;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> MeasureValueList = new ArrayList<>();
    //    private HashMap<Integer,DropEditText> map=new HashMap<>();
//    private HashMap<Integer,DropEditText> map2=new HashMap<>();
    private HashMap<String, String> MeasurePointType = new HashMap<>();
    private HashMap<String, String> MeasureValueMap = new HashMap<>();
    private HashMap<String, String> MeasureValueMap2 = new HashMap<>();
    private HashMap<String, String> TranslationMap = new HashMap<>();
    private ArrayList<ObjectElement> submitData = new ArrayList<>();
    private ArrayList<String> ObPointValueList = new ArrayList<>();
    private final int hour = 60 * 60 * 1000;
    private String TaskSubClass;
    private boolean isMain = false;
    private boolean isEquipmentComplete = false;
    private int TaskStatus = -1;
    private String Task_ID;
    private String TaskEquipment;
    private int TaskEquipmentStatus;
    private String Task_List_ID;
    private String moduleType = "";
    private final String IsNeedRefer = "IsNeedRefer";
    //create by jason 2019/3/25 返回的新单号
    private String RelateTask_ID = "";
    //create by jason 2019/3/26 获取Task_ID作弹窗判断
    private ArrayList<String> Task_ID_List = new ArrayList<>();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    //rivate ExpandGridView noScrollgridview;
    //private GridAdapter Gridadapter;
    private boolean isTaskHistory = false;
    private List<GridAdapter> gridAdapterList = new ArrayList<>();
    private int currentIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_point_new);
        TaskSubClass = getIntent().getStringExtra(Task.TASK_SUBCLASS);
        isMain = getIntent().getBooleanExtra("isMainPersonInTask", false);
        isEquipmentComplete = getIntent().getBooleanExtra("EquipmentStatus", false);
        Task_ID = getIntent().getStringExtra(Task.TASK_ID);
        TaskEquipment = getIntent().getStringExtra("TaskEquipment");
        TaskStatus = getIntent().getIntExtra("TaskStatus", -1);
        moduleType = getIntent().getStringExtra(Task.ModuleType);
        JsonObjectElement eleObj = new JsonObjectElement(TaskEquipment);
        if (eleObj != null) {
            Task_List_ID = eleObj.get("Equipment_ID").toString();
            //当前对象任务状态
            TaskEquipmentStatus = eleObj.get("Status").valueAsInt();
            LogUtils.e("Task_List_ID----->" + Task_List_ID);
        }

        LogUtils.e("TaskEquipment----->" + TaskEquipment);
        if (isEquipmentComplete) {
            findViewById(R.id.title_tips_layout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.title_tips)).setText(LocaleUtils.getI18nValue("EquipmentIsCompleteCanNotModifyMeasurePoint"));
        }
        initData();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(MeasurePointActivityNew.this));
//        initView();
//        //TestData();
//        initSearchView();
//        GetMeasurePointList();
    }

    private void initView() {
        if (getIntent().getStringExtra(Task.TASK_SUBCLASS).equals(Task.ROUTING_INSPECTION)) {
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("measure_point_list"));
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("upkeep_point_list"));
        }
        Measure_Point_ListView = (PullToRefreshListView) findViewById(R.id.measure_point_list);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        Button sureButton = (Button) findViewById(R.id.btn_sure_bg);
        sureButton.setText(LocaleUtils.getI18nValue("warning_message_confirm"));
        sureButton.setVisibility(View.VISIBLE);
        sureButton.setOnClickListener(this);

        adapter = new TaskAdapter(measure_point_list) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final TaskViewHolder holder;
//                if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_measure_point_list_new, parent, false);
                ((TextView) convertView.findViewById(R.id.measure_point_name_tag)).setText(LocaleUtils.getI18nValue("measure_point_name"));
                ((TextView) convertView.findViewById(R.id.measure_point_content_tag)).setText(LocaleUtils.getI18nValue("measure_point_content"));
                ((TextView) convertView.findViewById(R.id.measure_point_standard_tag)).setText(LocaleUtils.getI18nValue("measure_point_standard"));
                ((TextView) convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(LocaleUtils.getI18nValue("StandardValue"));
                ((TextView) convertView.findViewById(R.id.MeasureValueSelect_tag)).setText(LocaleUtils.getI18nValue("MeasureValue"));
                ((TextView) convertView.findViewById(R.id.tvStateValue)).setText(LocaleUtils.getI18nValue("StateValue"));
                if (moduleType.equals("Property")) {
//                    LocaleUtils.getI18nValue("Maintain_Result") 检查结果
                    ((TextView) convertView.findViewById(R.id.measure_point_standard_tag)).setText(LocaleUtils.getI18nValue("Maintain_Standard"));
                    ((TextView) convertView.findViewById(R.id.tvStateValue)).setText(LocaleUtils.getI18nValue("task_note"));
                    //隐藏点击状态标记
                    convertView.findViewById(R.id.image).setVisibility(View.INVISIBLE);
                    //隐藏“已检测”标记
                    convertView.findViewById(R.id.measure_point_status).setVisibility(View.INVISIBLE);
                }

                ((TextView) convertView.findViewById(R.id.textView19)).setText(LocaleUtils.getI18nValue("task_picture"));
//                    LogUtils.e("StandardValue--->"+LocaleUtils.getI18nValue("StandardValue"));
//                    LogUtils.e("MeasureValue--->"+LocaleUtils.getI18nValue("MeasureValue"));
//                    LogUtils.e("StateValue--->"+LocaleUtils.getI18nValue("StateValue"));
                holder = new TaskViewHolder();
                holder.tv_group = (TextView) convertView.findViewById(R.id.measure_point_name);
                holder.tv_task_describe = (TextView) convertView.findViewById(R.id.measure_point_content);
                holder.tv_repair_time = (TextView) convertView.findViewById(R.id.measure_point_standard_tag);
                holder.tv_create_time = (TextView) convertView.findViewById(R.id.measure_point_standard);

                //已检测
                holder.tv_device_name = (TextView) convertView.findViewById(R.id.measure_point_status);
                holder.tv_task_state = (TextView) convertView.findViewById(R.id.sequence_number);
                holder.editText = (EditText) convertView.findViewById(R.id.StateValueInput);
                holder.editText.setHint(LocaleUtils.getI18nValue("pleaseInput"));
                holder.dropEditText = (DropEditText) convertView.findViewById(R.id.MeasureValueSelect);
                holder.dropEditText.setHint(LocaleUtils.getI18nValue("pleaseInput"));
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.upload_img = (ImageView) convertView.findViewById(R.id.upload_img);
                //  holder.tv_repair_time=(TextView)convertView.findViewById(R.id.result_text);
                //  holder.tv_end_time=(TextView)convertView.findViewById(R.id.state_text);

                holder.dropEditText2 = (DropEditText) convertView.findViewById(R.id.MeasureValueStandard);
                holder.dropEditText2.setHint(LocaleUtils.getI18nValue("pleaseInput"));
                holder.gridView = (GridView) convertView.findViewById(R.id.ObPointValueList);
                holder.editText2 = (EditText) convertView.findViewById(R.id.CollectionPoint);
                holder.editText2.setHint(LocaleUtils.getI18nValue("pleaseInput"));
                holder.tv_end_time = (TextView) convertView.findViewById(R.id.ValueUnit);
                holder.warranty_person = (TextView) convertView.findViewById(R.id.MeasureStandard_value);

                holder.tv_device_num = (TextView) convertView.findViewById(R.id.measure_point_standard_unit);
                // holder.tv_start_time=(TextView)convertView.findViewById(R.id.MeasureValueStandard_text);
//                    convertView.setTag(holder);
//                }else {
//                    holder = (TaskViewHolder) convertView.getTag();
//                }
                {
                    if (isEquipmentComplete) {
                        holder.editText.setEnabled(false);
                        holder.dropEditText.getmEditText().setEnabled(false);
                        holder.dropEditText.getDropImage().setEnabled(false);
                        holder.image.setEnabled(false);
                        convertView.findViewById(R.id.imageLayout).setEnabled(false);
                        holder.dropEditText2.getmEditText().setEnabled(false);
                        holder.dropEditText.getmEditText().setEnabled(false);
                        holder.gridView.setEnabled(false);
                        holder.editText2.setEnabled(false);
                    }
                }
//                LogUtils.e("measure_point_list---->"+measure_point_list.get(position).get("tag"));
                if (measure_point_list.get(position).get("tag") != null) {
                    if (measure_point_list.get(position).get("tag").valueAsBoolean()) {
                        holder.image.setImageResource(R.mipmap.select_pressed);
                    } else {
                        holder.image.setImageResource(R.mipmap.select_normal);
                    }
                }
                convertView.findViewById(R.id.upload_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item_ID = v.getTag().toString();
                        new PopupWindows(mContext);
                    }

                });
                convertView.findViewById(R.id.imageLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.e("点击任务打勾事件---->" + measure_point_list.get(position).get("tag").valueAsBoolean());
                        if (measure_point_list.get(position).get("tag").valueAsBoolean()) {
                            measure_point_list.get(position).set("tag", false);
                        } else {
                            if (checkResultValue(measure_point_list.get(position), holder.editText2)) {
                                return;
                            }
                            measure_point_list.get(position).set("tag", true);
                        }
                        //new PopupWindows(mContext);
                        notifyDataSetChanged();
                        if (submitData.contains(measure_point_list.get(position))) {
                            submitData.remove(measure_point_list.get(position));
                        } else {
                            submitData.add(measure_point_list.get(position));
                        }
                    }
                });
                //map.put(position,holder.dropEditText);
                //map2.put(position,holder.dropEditText2);
                if (measure_point_list.get(position).get("IsResultSubmit").valueAsBoolean()) {
                    holder.tv_device_name.setText(LocaleUtils.getI18nValue("IsCheck"));
                    holder.tv_device_name.setTextColor(getResources().getColor(R.color.order_color));
                } else {
                    holder.tv_device_name.setText(LocaleUtils.getI18nValue("IsNotCheck"));
                    holder.tv_device_name.setTextColor(getResources().getColor(R.color.esquel_red));
                }
                holder.editText2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                holder.dropEditText.getmEditText().setInputType(EditorInfo.TYPE_CLASS_PHONE);
                holder.dropEditText2.getmEditText().setInputType(EditorInfo.TYPE_CLASS_PHONE);
                holder.dropEditText.getmEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
                holder.dropEditText2.getmEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
                // holder.editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                initDropSearchView(null, holder.dropEditText.getmEditText(), LocaleUtils.getI18nValue("MeasureValueInput"), DataDictionary.DATA_NAME, position, LocaleUtils.getI18nValue("nothing_found"), holder.dropEditText.getDropImage(), measure_point_list.get(position));
                initDropSearchView(null, holder.dropEditText2.getmEditText(), LocaleUtils.getI18nValue("StandardValueInput"), DataDictionary.DATA_NAME, position, LocaleUtils.getI18nValue("nothing_found"), holder.dropEditText2.getDropImage(), measure_point_list.get(position));
                SetTextChangeListener(holder.editText, measure_point_list.get(position), "Remarks");
                SetTextChangeListener(holder.editText2, measure_point_list.get(position), "ResultValue");
                SetTextChangeListener(holder.dropEditText.getmEditText(), measure_point_list.get(position), "ResultValue");
                SetTextChangeListener(holder.dropEditText2.getmEditText(), measure_point_list.get(position), "ReferenceValue");
                //等于-1为计数器测点，等于T0203为采集器测点
                if (DataUtil.isDataElementNull(measure_point_list.get(position).get("MaintainItem_ID")).equals("-1")
                        || (DataUtil.isDataElementNull(measure_point_list.get(position).get("TaskSubClass")).equals("T0203"))) {
                    holder.dropEditText.setVisibility(View.GONE);
                    holder.dropEditText2.setVisibility(View.GONE);
                    holder.gridView.setVisibility(View.GONE);
//                    holder.tv_create_time.setVisibility(View.GONE);
//                    holder.tv_repair_time.setVisibility(View.GONE);
                    holder.warranty_person.setVisibility(View.VISIBLE);
                    holder.tv_device_num.setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(LocaleUtils.getI18nValue("LastMeasure"));
                    convertView.findViewById(R.id.CollectionPoint_layout).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.VISIBLE);
                } else {
                    holder.tv_device_num.setVisibility(View.GONE);
                    String pointType = DataUtil.isDataElementNull(measure_point_list.get(position).get("PointType"));
                    switch (pointType) {
                        case MeasurePoint.OBVERSE_MEASURE_POINT: {
//                            holder.tv_create_time.setVisibility(View.GONE);
//                            holder.tv_repair_time.setVisibility(View.GONE);
                            convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.GONE);
                            holder.dropEditText.setVisibility(View.GONE);
                            ((TextView) convertView.findViewById(R.id.MeasureValueSelect_tag)).setText(LocaleUtils.getI18nValue("checkResult"));
                            ((TextView) convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(LocaleUtils.getI18nValue("StandardValue"));
                            holder.gridView.setVisibility(View.VISIBLE);
                            if (holder.gridView.getAdapter() != null) {
                                ((StatusAdapter) holder.gridView.getAdapter()).notifyDataSetChanged();
                            } else {
                                holder.gridView.setAdapter(new StatusAdapter(ObPointValueList) {
                                    @Override
                                    public View getCustomView(View convertView1, int position1, ViewGroup parent1) {
                                        ViewHolder viewHolder;
                                        if (convertView1 == null) {
                                            convertView1 = LayoutInflater.from(context).inflate(R.layout.item_ob_point, parent1, false);
                                            viewHolder = new ViewHolder();
                                            viewHolder.statu = (TextView) convertView1.findViewById(R.id.obValue);
                                            convertView1.setTag(viewHolder);
                                        } else {
                                            viewHolder = (ViewHolder) convertView1.getTag();
                                        }
                                        viewHolder.statu.setText(ObPointValueList.get(position1));
//                                        LogUtils.e("statu--->"+ObPointValueList.get(position1)+"measure_point_list--->"+measure_point_list.get(position).get("ResultValue").toString());
                                        if (viewHolder.statu.getText().toString().equals(
                                                DataUtil.isDataElementNull(measure_point_list.get(position).get("ResultValue")))) {
                                            viewHolder.statu.setTextColor(Color.WHITE);
                                            viewHolder.statu.setBackgroundResource(R.drawable.bg_edit_select);
                                        } else {
                                            viewHolder.statu.setTextColor(Color.BLACK);
                                            viewHolder.statu.setBackgroundResource(R.drawable.bg_edit_normal);
                                        }
                                        return convertView1;
                                    }
                                });
                            }
                          /*  if (LocaleUtils.getLanguage(context) != null && LocaleUtils.getLanguage(context) == LocaleUtils.SupportedLanguage.ENGLISH) {
                                holder.gridView.setNumColumns(2);
                                ListViewUtility.setGridViewHeightBasedOnChildren(holder.gridView, 2);
                            } else {
                                holder.gridView.setNumColumns(3);
                            }*/
                            if (LocaleUtils.getLanguage(context).substring(0, 2).equals("zh")) {
                                holder.gridView.setNumColumns(3);
                            } else {
                                holder.gridView.setNumColumns(2);
                                ListViewUtility.setGridViewHeightBasedOnChildren(holder.gridView, 2);
                            }
                            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent2, View view2, int position2, long id) {
                                    measure_point_list.get(position).set("ResultValue", ObPointValueList.get(position2));
                                    LogUtils.e("点击修改打勾状态--->" + position2 + "---->" + position);
                                    measure_point_list.get(position).set("tag", true);
                                    if (!submitData.contains(measure_point_list.get(position))) {
                                        submitData.add(measure_point_list.get(position));
                                    }
                                    ((StatusAdapter) holder.gridView.getAdapter()).notifyDataSetChanged();
                                    //notifyDataSetChanged();
                                }
                            });
                            break;
                        }
                        case MeasurePoint.PROCESS_MEASURE_POINT: {
                            holder.tv_create_time.setVisibility(View.VISIBLE);
                            holder.tv_repair_time.setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.VISIBLE);
                            holder.gridView.setVisibility(View.GONE);
                            holder.dropEditText.setVisibility(View.VISIBLE);
                            if (measure_point_list.get(position).get(IsNeedRefer) != null && !measure_point_list.get(position).get(IsNeedRefer).valueAsBoolean()) {
                                convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.GONE);
                            }
                            ((TextView) convertView.findViewById(R.id.MeasureValueSelect_tag)).setText(LocaleUtils.getI18nValue("MeasureValue"));
                            ((TextView) convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(LocaleUtils.getI18nValue("StandardValue"));
                            break;
                        }
                        default: {
                            holder.tv_create_time.setVisibility(View.VISIBLE);
                            holder.tv_repair_time.setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.VISIBLE);
                            holder.gridView.setVisibility(View.GONE);
                            holder.dropEditText.setVisibility(View.VISIBLE);
                            if (measure_point_list.get(position).get(IsNeedRefer) != null && !measure_point_list.get(position).get(IsNeedRefer).valueAsBoolean()) {
                                convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.GONE);
                            }
                            ((TextView) convertView.findViewById(R.id.MeasureValueSelect_tag)).setText(LocaleUtils.getI18nValue("MeasureValue"));
                            ((TextView) convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(LocaleUtils.getI18nValue("SettingMeasureResult"));
                            break;
                        }
                    }
                }
                holder.editText2.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ResultValue")));
                //TODO 计数器测量值单位
                holder.tv_end_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("Unit")));
                holder.tv_device_num.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("Unit")));
                holder.warranty_person.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ReferenceValue")));
                holder.tv_task_state.setText(String.valueOf(position + 1));
                holder.editText.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("Remarks")));
                holder.dropEditText.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ResultValue")));
                //holder.tv_repair_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ResultValue")));
                //holder.tv_end_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("Remarks")));
                holder.tv_group.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("TaskItemName")));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("PointContent")));
                holder.tv_create_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("MaintainStandard")));
                holder.dropEditText2.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ReferenceValue")));
                //holder.tv_start_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ReferenceValue")));
                holder.upload_img.setTag(DataUtil.isDataElementNull(measure_point_list.get(position).get("TaskItem_ID")));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MeasurePointContentActivity.class);
                        intent.putExtra("measure_point_detail", measure_point_list.get(position).toString());
                        intent.putExtra("ModuleType", moduleType);
                        startActivity(intent);
                    }
                });

                final ExpandGridView noScrollgridview = (ExpandGridView) convertView.findViewById(R.id.picture_containt);
                GridAdapter gridadapter = new GridAdapter(MeasurePointActivityNew.this);
                gridadapter.setItem_ID(holder.upload_img.getTag().toString());
                gridadapter.setIndex(position);
                gridAdapterList.add(gridadapter);
                noScrollgridview.setAdapter(gridadapter);
                noScrollgridview.setTag(position);

                noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        Item_ID = holder.upload_img.getTag().toString();

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
                            if (TaskStatus != 1 || TaskEquipmentStatus != 1 || isTaskHistory  ) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyDealingTaskCanAddPhoto"), mContext);
                                    }
                                });
                                return;
                            }
                            if (imageList.size() >= 5) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("pictureNumLimit"), mContext);
                                    }
                                });
                                return;
                            }


                            new PopupWindows(mContext);
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
                        Item_ID = holder.upload_img.getTag().toString();
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


                return convertView;
            }
        };
        Measure_Point_ListView.setAdapter(adapter);
        Measure_Point_ListView.setRefreshing(false);
        Measure_Point_ListView.setPullToRefreshOverScrollEnabled(false);
        Measure_Point_ListView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
        Measure_Point_ListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // pageIndex=1;
                        GetMeasurePointList();
                        Measure_Point_ListView.onRefreshComplete();
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetMeasurePointList();
                        Measure_Point_ListView.onRefreshComplete();
                    }
                }, 0);
            }
        });
//        Measure_Point_ListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent(context,MeasurePointContentActivity.class);
//                intent.putExtra("measure_point_detail",measure_point_list.get(position).toString());
//                startActivity(intent);
//            }
//        });

//        noScrollgridview = (ExpandGridView) findViewById(R.id.picture_containt);
//        Gridadapter = new GridAdapter(this);
//        noScrollgridview.setAdapter(Gridadapter);

    }


    //kingzhang add for srf 上传图片 begin
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类

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

            LogUtils.e("弹出PopWindow");

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
    private String Item_ID = "";
    private Context mContext = this;

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
                                    GridAdapter gridAdapter = gridAdapterList.get(currentIdx);
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


    //kingzhang add for srf 上传图片 end

    /**
     * create by jason
     */
    private void initDataByServe() {
        HttpParams params = new HttpParams();
        HttpUtils.post(this, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'MaintainPointResult'%20and%20factory_id%20eq%20'" + getLoginInfo().getFactoryId() + "'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    LogUtils.e("数据为空");
                    return;
                }
                DataElement jsonArrayElment = new JsonArrayElement(t);
                if (jsonArrayElment.asArrayElement().size() <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), context);
                        }
                    });
                } else {
                    for (int i = 0; i < jsonArrayElment.asArrayElement().size(); i++) {
                        ObjectElement jsonObjectElement = jsonArrayElment.asArrayElement().get(i).asObjectElement();
                        //翻译DATA_NAME
                        jsonObjectElement.set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME))));
                        //  MeasurePointType.put(DataUtil.isDataElementNull(jsonObjectElement.get("DataCode")),
                        //         DataUtil.isDataElementNull(jsonObjectElement.get("DataName")));
                        MeasureValueList.add(jsonObjectElement);
                        MeasureValueMap.put(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)),
                                DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_CODE)));
                        MeasureValueMap2.put(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_CODE)),
                                DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                        ObPointValueList.add(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                        if (jsonObjectElement.get("Translation_Code") != null) {
                            TranslationMap.put(DataUtil.isDataElementNull(jsonObjectElement.get("Translation_Code")),
                                    DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                            //TestData();
                            initSearchView();
                            GetMeasurePointList();
                        }
                    });
                }
                LogUtils.e("执行有数据成功---->" + jsonArrayElment.asArrayElement().size());

                LogUtils.e("initDataByServe--测试成功--->" + t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                HttpUtils.tips(context, errorNo + "strMsg-->" + strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), context);
                    }
                });
                LogUtils.e("initDataByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });

    }

    private void initData() {
//        for(int i=0;i<3;i++){
//            JsonObjectElement jsonObjectElement=new JsonObjectElement();
//            if(i==0) {
//                jsonObjectElement.set(DataDictionary.DATA_NAME,"正常");
//            }
//            if(i==1) {
//                jsonObjectElement.set(DataDictionary.DATA_NAME, "报警");
//            }
//            if(i==2) {
//                jsonObjectElement.set(DataDictionary.DATA_NAME,"危险" );
//            }
//            MeasureValueList.add(jsonObjectElement);
//        }

        initDataByServe();
        getTaskAttachmentDataFromServerByTaskId();
//        DataUtil.getDataFromDataBase(context, "MaintainPointResult", new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                if(element.asArrayElement().size()<=0){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"),context);
//                        }
//                    });
//                }else {
//                    LogUtils.e("initData--测试成功--->"+element.asArrayElement().toString());
//                    for (int i = 0; i < element.asArrayElement().size(); i++) {
//                        ObjectElement jsonObjectElement = element.asArrayElement().get(i).asObjectElement();
//                        //翻译DATA_NAME
//                        jsonObjectElement.set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME))));
//                        //  MeasurePointType.put(DataUtil.isDataElementNull(jsonObjectElement.get("DataCode")),
//                        //         DataUtil.isDataElementNull(jsonObjectElement.get("DataName")));
//                        MeasureValueList.add(jsonObjectElement);
//                        MeasureValueMap.put(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)),
//                                DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_CODE)));
//                        MeasureValueMap2.put(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_CODE)),
//                                DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
//                        ObPointValueList.add(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
//                        if(jsonObjectElement.get("Translation_Code")!=null) {
//                            TranslationMap.put(DataUtil.isDataElementNull(jsonObjectElement.get("Translation_Code")),
//                                    DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
//                        }
//                        }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            initView();
//                            //TestData();
//                            initSearchView();
//                            GetMeasurePointList();
//                        }
//                    });
//                }
//            }
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"),context);
//                    }
//                });
//            }
//        });

        MeasurePointType.put(MeasurePoint.UPKEEP_POINT, LocaleUtils.getI18nValue("MPT01"));
        MeasurePointType.put(MeasurePoint.PROCESS_MEASURE_POINT, LocaleUtils.getI18nValue("MPT02"));
        MeasurePointType.put(MeasurePoint.OBVERSE_MEASURE_POINT, LocaleUtils.getI18nValue("MPT03"));
        MeasurePointType.put(MeasurePoint.CHECK_POINT, LocaleUtils.getI18nValue("MPT04"));
    }

    @Override
    public void resolveNfcMessage(Intent intent) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_right_action: {
                finish();
                break;
            }
            case R.id.btn_sure_bg: {
                if (isMain) {
                    if (!isEquipmentComplete) {
                        SubmitDataToServer();
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("CannotSubmitMeasurePointValue"), context);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyMainPersonCanSubmit"), context);
                        }
                    });
                }
                break;
            }
        }
    }

    private void initSearchView() {
        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.setHint(LocaleUtils.getI18nValue("hint_search_box"));
        mDrawer_layout = (CustomDrawerLayout) findViewById(R.id.search_page);
        if (mDrawer_layout != null) {
            mDrawer_layout.setCloseDrawerListener(new CloseDrawerListener() {
                @Override
                public void close() {
                    searchBox.setText("");
                }
            });
        }
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawer_layout.setBackgroundColor(Color.parseColor("#00000000"));
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        ((TextView) emptyView.findViewById(R.id.tvNothingFound)).setText(LocaleUtils.getI18nValue("nothing_found"));
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                //SelectItem=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                final String searchResult = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            switch (searchtag) {
//                                case 1:
//                                    //equipment_id.getmEditText().setText(searchResult);
//                                    break;
//                            }
                            //map.get(searchtag).getmEditText().setText(searchResult);
                            //map2.get(searchtag).getmEditText().setText(searchResult);
                            if (mDrawer_layout != null) {
                                mDrawer_layout.closeDrawer(Gravity.RIGHT);
                            }

                        }
                    });
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"), context);
                }
            }
        });
//        initDropSearchView(null, equipment_id.getmEditText(), context.getResources().
//                        getString(LocaleUtils.getI18nValue("work_num_dialog), Equipment.ASSETSID,
//                1, LocaleUtils.getI18nValue("getDataFail,equipment_id.getDropImage());
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer_layout != null) {
                    mDrawer_layout.closeDrawer(Gravity.RIGHT);
                }

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
                // initData(s.toString());
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

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
            }
        });
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

    private void initDropSearchView(
            final EditText condition, final EditText subEditText,
            final String searchTitle, final String searchName, final int searTag, final String tips, ImageView imageView, final ObjectElement objectElement) {
//优化adapter
//        if(subEditText.getTag(tips)!=null){
//            return;
//        }
//        subEditText.setTag(tips,123);
        subEditText.setFocusable(true);
        subEditText.setFocusableInTouchMode(true);
        subEditText.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        subEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        DropSearch(condition,
//                                searchTitle,searchName,searTag ,tips);
                        subEditText.requestFocus();
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtil.isDataElementNull(measure_point_list.get(searTag).get("MaintainItem_ID")).equals("-1")) {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseInputMeasureValue"), context);
                } else {
                    subEditText.setText(MeasureValueMap2.get("MPR03"));
                    objectElement.set("tag", true);
                    if (!submitData.contains(objectElement)) {
                        submitData.add(objectElement);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void DropSearch(final EditText condition,
                            final String searchTitle, final String searchName, final int searTag, final String tips) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
//                switch (searTag) {
//                    case 1:{
//                if(DataUtil.isDataElementNull(measure_point_list.get(searTag).get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)){
                searchDataLists.addAll(MeasureValueList);
//                }else {
//                searchDataLists.add(MeasureValueList.get(MeasureValueList.size()-1));
//                }
//                        break;
//                    }}
                if (condition != null) {
                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {

                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips, context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips, context);
                    }
                }
            }
        });
    }

    private void GetMeasurePointList() {
//        if(RecCount!=0){
//            if((pageIndex-1)*PAGE_SIZE>=RecCount){
//                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noMoreData,context);
//                return;
//            }}
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
//        params.put("task_id",getIntent().getStringExtra(Task.TASK_ID));
//        params.put("taskEquipment_id",DataUtil.isDataElementNull(new JsonObjectElement(getIntent().getStringExtra("TaskEquipment")).get("TaskEquipment_ID")));
//        params.put("pageSize",PAGE_SIZE);
//        params.put("pageIndex",pageIndex);
        HttpUtils.post(this, "MaintainAPI/MaintainPointList?task_id=" + Task_ID
                + "&taskEquipment_id=" + DataUtil.isDataElementNull(new JsonObjectElement(TaskEquipment).get("TaskEquipment_ID"))
                + "&pageSize=1000&pageIndex=1", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (!DataUtil.isNullOrEmpty(t)) {
                    try {
                        LogUtils.e("获取到数据---->" + t);
                        submitData.clear();
                        measure_point_list.clear();
                        JsonArrayElement jsonArrayElement = new JsonArrayElement(t);
                        if (jsonArrayElement.size() > 0) {
                            //if (pageIndex == 1) {
//                            }
//                            pageIndex++;
                            LogUtils.e("获取列表数据--->" + jsonArrayElement.toString());
                            for (int i = 0; i < jsonArrayElement.size(); i++) {
                                ObjectElement json = jsonArrayElement.get(i).asObjectElement();
                                json.set("tag", false);
//                                    LogUtils.e("isResultcommit--->"+json.get("IsResultSubmit"));
                                if (DataUtil.isDataElementNull(json.get("PointType")).equals(MeasurePoint.PROCESS_MEASURE_POINT)
                                        || DataUtil.isDataElementNull(json.get("PointType")).equals(MeasurePoint.CHECK_POINT)
                                        || DataUtil.isDataElementNull(json.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
                                    //create by jason 2019/3/25 注释这些代码看看 到727行
                                    if (DataUtil.isDataElementNull(json.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
//                                            LogUtils.e("PointType--->"+json.get("PointType"));
//                                            json.set("ResultValue","正常");
                                        json.set("IsResultSubmit", true);
                                        json.set("tag", true);
                                    }

                                        /*if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH) {
                                            //英文环境的情况下，实际测值为无法检测情况下，翻译为英文
                                            if(TranslationMap.get(DataUtil.isDataElementNull(json.get("ResultValue")))!=null) {
                                                if (MeasureValueMap.get(TranslationMap.get(DataUtil.isDataElementNull(json.get("ResultValue")))) != null) {
                                                    json.set("ResultValue", TranslationMap.get(DataUtil.isDataElementNull(json.get("ResultValue"))));
                                                    json.set("ReferenceValue",DataUtil.isDataElementNull(json.get("ResultValue")));
                                                }
                                            }
                                        }else {
                                            //中文环境下，过程测点和校验测点的实际测值为无法检测的情况下，设定标准测值也为无法检测
                                            if(MeasureValueMap.get(DataUtil.isDataElementNull(json.get("ResultValue")))!=null){
                                                json.set("ReferenceValue",DataUtil.isDataElementNull(json.get("ResultValue")));
                                            }
                                        }*/
                                    if (MeasureValueMap.get(LocaleUtils.getI18nValue(DataUtil.isDataElementNull(json.get("ResultValue")))) != null) {
                                        json.set("ResultValue", LocaleUtils.getI18nValue(DataUtil.isDataElementNull(json.get("ResultValue"))));
                                        json.set("ReferenceValue", DataUtil.isDataElementNull(json.get("ResultValue")));
                                    }
//                                        if(ObPointValueList.contains(DataUtil.isDataElementNull(json.get("ResultValue")))){
//                                            json.set("ReferenceValue",DataUtil.isDataElementNull(json.get("ResultValue")));
//                                        }
                                }
                                json.set("num", i + 1);
                                if (Boolean.parseBoolean(json.get("IsResultSubmit").valueAsString())) {
                                    json.set("tag", true);
                                }
                                if (DataUtil.isDataElementNull(json.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
                                    submitData.add(json);
                                }
                                //create by jason 2019/3/21
//                                    LogUtils.e("measure_point_list数据--->"+json.toString());
                                measure_point_list.add(json);
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setDatas(measure_point_list);
                                adapter.notifyDataSetChanged();
                            }
                        });
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
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetMeasurePointListCauseByTimeOut"), context);
                dismissCustomDialog();
            }
        });
    }

    private void SubmitDataToServer() {
        if (submitData.size() <= 0) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseSelectSubmitData"), context);
            return;
        } else if (TaskEquipmentStatus != 1) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("BanClickSub"), context);
            return;
        }
        for (int i = 0; i < submitData.size(); i++) {
            //判断测量值是否为空，没值返回并提示
            if (submitData.get(i).get("ResultValue") == null || DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")).isEmpty()) {
                String s = String.format(LocaleUtils.getI18nValue("id"), DataUtil.isDataElementNull(submitData.get(i).get("num")))
                        + LocaleUtils.getI18nValue("pleaseInputMeasureData");
                ToastUtil.showToastShort(s, context);
                return;
            }
            //TODO
            if (!DataUtil.isDataElementNull(submitData.get(i).get("MaintainItem_ID")).equals("-1")
                    && !(DataUtil.isDataElementNull(submitData.get(i).get("TaskSubClass")).equals("T0203"))) {
                if (judgeNonCounterOrCollectorPoint(i)) {
                    return;
                }
            } else {
                if (checkResultValue(submitData.get(i), null)) {
                    return;
                }
            }
            if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))) == null) {
                if (!DataUtil.isFloat(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")).trim())) {
                    String s = String.format(LocaleUtils.getI18nValue("id"), DataUtil.isDataElementNull(submitData.get(i).get("num")))
                            + LocaleUtils.getI18nValue("pleaseInputNum");
                    ToastUtil.showToastShort(s, context);
                    return;
                }
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        ArrayList<ObjectElement> list = new ArrayList<>();
        for (int i = 0; i < submitData.size(); i++) {
            JsonObjectElement jsonObjectElement = new JsonObjectElement();
            jsonObjectElement.set("TaskItem_ID", DataUtil.isDataElementNull(submitData.get(i).get("TaskItem_ID")));
            if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))) != null) {
//                LogUtils.e("开始执行Result-->"+MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))))
                //create by jason 2019/04/11
                jsonObjectElement.set("ResultCode", MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))));
//                if (!Task_ID_List.contains(jsonObjectElement.get("TaskItem_ID").valueAsString())){
//                    jsonObjectElement.set("ResultCode",MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))));
//                }else{
//                    jsonObjectElement.set("ResultCode","MPR02");
//                }

                //create by jason 2019/3/25 新增字段
                if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))).equals("MPR01")) {


                    LogUtils.e("是否有RelateTask_ID--->" + jsonObjectElement.get("RelateTask_ID"));
                    jsonObjectElement.set("RelateTask_ID", submitData.get(i).get("RelateTask_ID"));
                    LogUtils.e("submitData-->" + submitData.get(i).get("RelateTask_ID"));
                    if (submitData.get(i).get("RelateTask_ID") != null) {

                        if (submitData.get(i).get("RelateTask_ID").valueAsInt() == 0) {
                            if (!Task_ID_List.contains(jsonObjectElement.get("TaskItem_ID").valueAsString())) {
                                if (!TextUtils.isEmpty(RelateTask_ID)) {
                                    LogUtils.e("RelateTask_ID不为空-->" + RelateTask_ID);
                                    jsonObjectElement.set("RelateTask_ID", RelateTask_ID);
                                    Task_ID_List.add(jsonObjectElement.get("TaskItem_ID").valueAsString());
                                    RelateTask_ID = "";
                                }
                            } else {
                                LogUtils.e("Task_ID_List已经有了这个-->" + jsonObjectElement.get("TaskItem_ID").valueAsString());
                            }
                        }
                    }

//                        if (Task_ID_List.size()>0) {
//                            for (int j=0;j<Task_ID_List.size();j++) {
//                                String str = Task_ID_List.get(j);
//                                LogUtils.e("str--->"+RelateTask_ID+"---->"+Task_ID_List.get(j)+"--->"+Task_ID_List.size());
//                                if (!TextUtils.isEmpty(str)){
//                                    if (str.equals(RelateTask_ID)) {
//                                        LogUtils.e("不用创建RelateTask_ID");
//                                    }else{
//                                        LogUtils.e("创建RelateTask_ID--->"+RelateTask_ID);
//                                        jsonObjectElement.set("RelateTask_ID",RelateTask_ID);
//                                        submitData.set(i,jsonObjectElement);
//                                        Task_ID_List.add(RelateTask_ID);
//                                    }
//                                }
//
//                            }
//                        }else{
//                            LogUtils.e("创建RelateTask_ID--->");
//                            jsonObjectElement.set("RelateTask_ID",RelateTask_ID);
//                            submitData.set(i,jsonObjectElement);
//                            Task_ID_List.add(RelateTask_ID);
//                        }

                }
            } else {
                jsonObjectElement.set("ResultValue", DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")));
            }


            if (!DataUtil.isDataElementNull(submitData.get(i).get("MaintainItem_ID")).equals("-1")
                    && !(DataUtil.isDataElementNull(submitData.get(i).get("TaskSubClass")).equals("T0203"))) {
                if (!DataUtil.isDataElementNull(submitData.get(i).get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
                    if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue"))) != null) {
                        //jsonObjectElement.set("ResultCode",MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue"))));
                    } else {
                        if (submitData.get(i).get(IsNeedRefer) == null || submitData.get(i).get(IsNeedRefer).valueAsBoolean()) {
                            jsonObjectElement.set("ReferenceValue", DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")));
                        }
                    }
                }
            }
            jsonObjectElement.set("Remarks", DataUtil.isDataElementNull(submitData.get(i).get("Remarks")));
            list.add(jsonObjectElement);
        }
        final JsonArrayElement jsonArrayElement = new JsonArrayElement(list.toString());
        String path = "MaintainAPI/JudeResultValue";
        if (moduleType.equals("Property")) {
            path = "MaintainAPI/NJudeResultValue";
            JsonObjectElement jsonObjectElement = new JsonObjectElement();
            jsonObjectElement.set("moduleType", moduleType);
            jsonObjectElement.set("task_id", Task_ID);
            jsonObjectElement.set("equipment_id", Task_List_ID);
            jsonObjectElement.set("status", 1);
            jsonObjectElement.set("taskItemList", jsonArrayElement);
            params.putJsonParams(jsonObjectElement.toJson());
        } else {
            params.putJsonParams(jsonArrayElement.toJson());
        }
        LogUtils.e("上传数据到后台--->" + params.getJsonParams());
        HttpUtils.post(context, path, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if (t != null) {
//                    JsonObjectElement json=new JsonObjectElement(t);
//                    if(json.get(Data.SUCCESS).valueAsBoolean()){
//                           //pageIndex=1;
//                          GetMeasurePointList();
//                    }else {
//                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("submit_Fail,context);
//                    }
//                }
                    submitData.clear();
                    GetMeasurePointList();
                    boolean isFinish = true;
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitSuccess"), context);
                    final JsonArrayElement data = new JsonArrayElement(t);
                    LogUtils.e("请求网络数据成功--->" + data.toString());
                    boolean isShow = true;
                    for (int i = 0; i < data.size(); i++) {
                        final ObjectElement objectElement = data.get(i).asObjectElement();
                        if (DataUtil.isDataElementNull(data.get(i).asObjectElement().get("EventType")).equals("ET01")) {
                            //如果提交过任务了，就不再显示对话框
                            if (!Task_ID_List.contains(data.get(i).asObjectElement().get("TaskItem_ID").valueAsString())) {
                                LogUtils.e("Task_ID_List--->" + data.get(i).asObjectElement().get("TaskItem_ID").valueAsString());
                                if (isShow) {
                                    LogUtils.e("显示对话框");
                                    //create by jason 2019/3/26 增加判断 已弹过窗后 不再弹窗
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            context);
                                    builder.setMessage("[" + DataUtil.isDataElementNull(data.get(i).asObjectElement().get("MaintainWorkItemName"))
                                            + "]" + LocaleUtils.getI18nValue("CreateNewTaskTips"));
                                    builder.setPositiveButton(LocaleUtils.getI18nValue("CreateNewTask"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            LogUtils.e("确认创建新任务");
                                            CreateNewTask(objectElement);
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                    isShow = false;
                                    isFinish = false;
                                }
                            }

                        }
                        if (DataUtil.isDataElementNull(data.get(i).asObjectElement().get("EventType")).equals("ET02")) {
                            LogUtils.e("进入T02类任务---->");
                            Toast.makeText(context, "[" + DataUtil.isDataElementNull(data.get(i).asObjectElement().get("MaintainWorkItemName"))
                                    + "]" + LocaleUtils.getI18nValue("MeasurePointValueTips"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    //create by jason 2019/3/21 提交成功后 自动关闭
                    if (isFinish) {
                        dismissCustomDialog();
                        finish();
                    }

                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("submit_Fail"), context);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"), context);
                dismissCustomDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            LogUtils.e("获取到上个页面返回数据--->" + data.toString());
        }
        switch (requestCode) {
            case 1001:
                if (data != null) {
                    RelateTask_ID = data.getExtras().getString("RelateTask_ID");//得到新Activity 关闭后返回的数据
                    SubmitDataToServer();
                }
                break;
        }


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

                        addImageUrlToDataList("file://" + SDPATH + fileName + ".JPEG", "0", Item_ID);
                        if (currentIdx != -1) {
                            GridAdapter gridAdapter = gridAdapterList.get(currentIdx);
                            gridAdapter.notifyDataSetChanged();
                        }

                        if (null != adapter) {
//                            adapter.setData(dataList);
//                            adapter.notifyDataSetChanged();
                            //Gridadapter.notifyDataSetChanged();
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

    private void CreateNewTask(ObjectElement objectElement) {
        Intent intent = new Intent(MeasurePointActivityNew.this, CreateTaskActivity.class);
        intent.putExtra("TaskEquipment", TaskEquipment);
        intent.putExtra("FromTask_ID", Task_ID);
        intent.putExtra("FromMeasurePointActivity", "FromMeasurePointActivity");
        intent.putExtra("TaskSubClass", TaskSubClass);
        //create by jason 2019/3/20
        objectElement.set("RelateTask_ID", Task_ID);
        intent.putExtra("TaskItem", objectElement.toJson());
        LogUtils.e("TaskTtem--->" + objectElement.toString());
        //create by jason 2019/3/25
        startActivityForResult(intent, 1001);
    }

    private void SetTextChangeListener(final EditText editText, final ObjectElement objectElement, final String key) {
        /*优化adapter*/
//        if(editText.getTag(R.id.aaaa)!=null){
//            return;
//        }
//        editText.setTag(R.id.aaaa,123);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if(!s.toString().equals(DataUtil.isDataElementNull(objectElement.get(key)))){

//                }
                objectElement.set(key, s.toString());
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (checkResultValue(objectElement, editText)) {
                        return true;
                    }
                    objectElement.set(key, editText.getText().toString());
                    objectElement.set("tag", true);
                    if (!submitData.contains(objectElement)) {
                        submitData.add(objectElement);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    objectElement.set("tag",true);
//                    if(!submitData.contains(objectElement)){
//                        submitData.add(objectElement);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
    }

    private boolean checkResultValue(ObjectElement objectElement, final EditText editText) {
        if (DataUtil.isDataElementNull(objectElement.get("MaintainItem_ID")).equals("-1")
                || (DataUtil.isDataElementNull(objectElement.get("TaskSubClass")).equals("T0203"))) {
            //TODO  检查用户输入
            if (!DataUtil.isDataElementNull(objectElement.get("ReferenceValue")).equals("")
                    && !DataUtil.isDataElementNull(objectElement.get("ResultValue")).equals("")
                    && DataUtil.isFloat(DataUtil.isDataElementNull(objectElement.get("ResultValue")))
                    && DataUtil.isFloat(DataUtil.isDataElementNull(objectElement.get("ReferenceValue")))) {
                if (!DataUtil.isDataElementNull(objectElement.get("UnitCode")).equals("")
                        && DataUtil.isDataElementNull(objectElement.get("UnitCode")).equals("MSNU02")
                        && DataUtil.isDataElementNull(objectElement.get("MaintainItem_ID")).equals("-1")
                        && !DataUtil.isDataElementNull(objectElement.get("UpdateTime")).equals("")) {
                    //TODO 根据ReferenceValue以及时间进行限制
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(LocaleUtils.getI18nValue("UpdateTime"));
                        long updateTime = sdf.parse(DataUtil.utc2Local(DataUtil.isDataElementNull(objectElement.get("UpdateTime")))).getTime();
                        long currentTime = new Date().getTime();
                        int Time = (int) ((currentTime - updateTime) / hour);
                        if (objectElement.get("ResultValue").valueAsFloat() < objectElement.get("ReferenceValue").valueAsFloat()
                                || objectElement.get("ResultValue").valueAsFloat() > (objectElement.get("ReferenceValue").valueAsFloat() + Time)) {
                            String s = LocaleUtils.getI18nValue("pleaseInputRightValue")
                                    + DataUtil.isDataElementNull(objectElement.get("ReferenceValue"))
                                    + "-" + String.valueOf(objectElement.get("ReferenceValue").valueAsFloat() + Time);
                            showdialog(s, editText);
                            return true;
                        }
                    } catch (Exception e) {
                        if (objectElement.get("ResultValue").valueAsFloat() < objectElement.get("ReferenceValue").valueAsFloat()
                        ) {
                            String s = LocaleUtils.getI18nValue("pleaseInputRightValue") + LocaleUtils.getI18nValue("equelToOrLargeThan") +
                                    DataUtil.isDataElementNull(objectElement.get("ReferenceValue"));
                            showdialog(s, editText);
                            return true;
                        }
                    }
                } else {
                    //TODO 根据ReferenceValue进行限制
                    if (objectElement.get("ResultValue").valueAsFloat() < objectElement.get("ReferenceValue").valueAsFloat()) {
                        String s = LocaleUtils.getI18nValue("pleaseInputRightValue") + LocaleUtils.getI18nValue("equelToOrLargeThan") +
                                DataUtil.isDataElementNull(objectElement.get("ReferenceValue"));
                        showdialog(s, editText);
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private void showdialog(String message, final EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setMessage(message);
        builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText != null) {
                    editText.setText("");
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private boolean judgeNonObverseMeasurePoint(int i) {
        if (submitData.get(i).get("ReferenceValue") == null || DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")).equals("")) {
            String s = String.format(LocaleUtils.getI18nValue("id"), DataUtil.isDataElementNull(submitData.get(i).get("num")))
                    + LocaleUtils.getI18nValue("pleaseInputMeasureDataStandard");
            ToastUtil.showToastShort(s, context);
            return true;
        }
        if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue"))) == null) {
            if (!DataUtil.isFloat(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")).trim())) {
                String s = String.format(LocaleUtils.getI18nValue("id"), DataUtil.isDataElementNull(submitData.get(i).get("num")))
                        + LocaleUtils.getI18nValue("pleaseInputNum");
                ToastUtil.showToastShort(s, context);
                return true;
            }
        } else {
            if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))) == null) {
                String s = String.format(LocaleUtils.getI18nValue("id"), DataUtil.isDataElementNull(submitData.get(i).get("num")))
                        + LocaleUtils.getI18nValue("id");
                ToastUtil.showToastShort(s, context);
                return true;
            }
        }
        return false;
    }

    private boolean judgeNonCounterOrCollectorPoint(int i) {
        if (!DataUtil.isDataElementNull(submitData.get(i).get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
            //非观察测点判断
            if (submitData.get(i).get(IsNeedRefer) == null || submitData.get(i).get(IsNeedRefer).valueAsBoolean()) {
                //当需要输入标准测值的之后进行检查
                if (judgeNonObverseMeasurePoint(i)) {
                    //返回true，表示输入不符合要求
                    return true;
                }
            }
        } else {
            //观察测点判断
            if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))) == null) {
                String s = String.format(LocaleUtils.getI18nValue("id"), DataUtil.isDataElementNull(submitData.get(i).get("num")))
                        + LocaleUtils.getI18nValue("pleaseSelectMeaValue");
                ToastUtil.showToastShort(s, context);
                return true;
            }
        }
        return false;
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
        Intent showBigImageIntent = new Intent(MeasurePointActivityNew.this,
                ShowBigImageActivity.class);

        startActivity(showBigImageIntent);
    }
}
