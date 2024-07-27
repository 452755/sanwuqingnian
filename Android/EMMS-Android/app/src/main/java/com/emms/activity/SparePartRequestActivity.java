package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.ui.PopMenu;
import com.emms.ui.PopMenuTaskDetail;
import com.smartown.tableview.library.TableView;

import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.callback.CallBack;
import com.emms.fragment.LinkedOrdersFragment;
import com.emms.fragment.PendingOrdersFragment;
import com.emms.fragment.ProcessingFragment;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.ui.DropEditText;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.OnPageSelectListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.king.zxing.Intents;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.lang.StringUtils;
import org.restlet.representation.Representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AAbrahamguo on 2021-12-16 新建物料申请Activity.
 */
public class SparePartRequestActivity extends NfcActivity implements View.OnClickListener {
    private Context mContext;
    private String TaskClass;

    private EditText creator;
    private DropEditText requestTypeValue, subordinateDepartments;
    private LinearLayout sparePartListLayout;

    private ArrayList<ObjectElement> requestTypes = new ArrayList<>();
    private ArrayList<ObjectElement> operatorTeams = new ArrayList<>();

    private int operatorID;
    private int operatorTeamID;
    private String requestType;
    private ArrayList<ObjectElement> selectSparePart = new ArrayList<>();
    private JsonArrayElement selectSparePartNum;
    private Long TaskID = 0L;

    private ImageView clearBtn;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private EditText searchBox;
    private DrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();

    private PopMenuTaskDetail popMenu;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SparePartActivity.SPAREPART_CHOOSE_CONFIRM_REQUESTCODE) {
            if (resultCode == SparePartActivity.SPAREPART_CHOOSE_CONFIRM_RESULTCODE) {
                LogUtils.e("----挑选备件完成----");
                String selectSparePartStr = data.getStringExtra("selectSparePart");
                LogUtils.e("挑选的备件" + selectSparePartStr);
                JsonArrayElement selectSparePartJson = new JsonArrayElement(selectSparePartStr);
                selectSparePart.clear();
                for (int i = 0; i < selectSparePartJson.size(); i++) {
                    selectSparePart.add(selectSparePartJson.get(i).asObjectElement());
                }
                refeshTable(selectSparePart);
                String selectSparePartNumStr = data.getStringExtra("selectSparePartNum");
                selectSparePartNum = new JsonArrayElement(selectSparePartNumStr);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.e("-----进入物料管理页面-----");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_part_request);
        mContext = this;
        BaseData.setBaseData(mContext);
        TaskClass = getIntent().getStringExtra(Task.TASK_CLASS);
        operatorID = (int) getLoginInfo().getId();
        LogUtils.e("TaskClass-->" + TaskClass + "-----operatorID-->" + operatorID);
        getTeamIdByOrganiseIDByServe(operatorID);
        initView();
        initSearchView();
        initPopMenu();
        getRequestTypeDataByShared();
    }

    private void initView() {
        try {
            LogUtils.e("进入物料申请页面");
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("spare_part_management"));
            ((TextView) findViewById(R.id.tvCreator)).setText(LocaleUtils.getI18nValue("applicanter"));
            ((TextView) findViewById(R.id.tv_subordinate_departments)).setText(LocaleUtils.getI18nValue("property_using_dept_1"));
            ((TextView) findViewById(R.id.tvRequestType)).setText(LocaleUtils.getI18nValue("reuqest_type"));
            ((TextView) findViewById(R.id.spare_part_choose)).setText(LocaleUtils.getI18nValue("spare_part_operation"));
            ((TextView) findViewById(R.id.tvPartsListing)).setText(LocaleUtils.getI18nValue("spare_part_list"));
            ((Button) findViewById(R.id.btn_choose)).setText(LocaleUtils.getI18nValue("spare_part_selection"));
            ((TextView) findViewById(R.id.spare_part_selectName)).setText(LocaleUtils.getI18nValue("spare_part_name"));
            ((TextView) findViewById(R.id.spare_part_selectType)).setText(LocaleUtils.getI18nValue("typeof_spare_part"));
            ((TextView) findViewById(R.id.spare_part_selectNum)).setText(LocaleUtils.getI18nValue("quantity"));
            ((Button) findViewById(R.id.sure)).setText(LocaleUtils.getI18nValue("warning_message_confirm"));
            findViewById(R.id.sure).setOnClickListener(this);

            this.TaskID = getIntent().getLongExtra(Task.TASK_ID, 0L);

            (findViewById(R.id.btn_choose)).setOnClickListener(this);
            requestTypeValue = (DropEditText) findViewById(R.id.request_type_value);
            requestTypeValue.setDatas(mContext, requestTypes, "DataName");
            requestTypeValue.setOnSelectChange(new DropEditText.OnSelectChange() {
                @Override
                public void OnSelectChange(int position) {
                    selectSparePartNum = null;
                    selectSparePart.clear();
                    refeshTable(null);
                }
            });

            creator = ((EditText) findViewById(R.id.creator));
            creator.setText(getLoginInfo().getName());

            subordinateDepartments = ((DropEditText) findViewById(R.id.subordinate_departments_value));
            subordinateDepartments.setOnClickListener(this);
            subordinateDepartments.getDropImage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.e("选择所属部门");
                    searchDataLists = operatorTeams;
                    mResultAdapter.changeData(searchDataLists, "OrganiseName");
                    mDrawer_layout.openDrawer(Gravity.RIGHT);
                }
            });

            subordinateDepartments.getmEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.e("选择所属部门");
                    searchDataLists = operatorTeams;
                    mResultAdapter.changeData(searchDataLists, "OrganiseName");
                    mDrawer_layout.openDrawer(Gravity.RIGHT);
                }
            });

            findViewById(R.id.btn_right_action).setOnClickListener(this);

            sparePartListLayout = (LinearLayout) findViewById(R.id.spare_part_list);

        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"), mContext);
            finish();
        }
    }

    private void initPopMenu() {
        LogUtils.e("----初始化右上角功能菜单----");
        findViewById(R.id.btn_bar_left).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_bar_left).setOnClickListener(this);
        popMenu = new PopMenuTaskDetail(mContext, 350, "{\"" + Task.TASK_ID + "\":" + TaskID + "}", TaskClass) {
            @Override
            public void onEventDismiss() {

            }
        };
        String mTitle = "[{ \"code\": \"" + PopMenuTaskDetail.SPARE_PART_VIEWHISTORICAL + "\", \"name\": \"" + LocaleUtils.getI18nValue("checkHistoryRecord") + "\"}," +
                "{ \"code\": \"" + PopMenuTaskDetail.SPARE_PART_VIEWSURPLUS + "\", \"name\": \"" + LocaleUtils.getI18nValue("spare_part_surplus_view") + "\"}]";
        LogUtils.e(mTitle);
        JsonArrayElement PopMenuArray = new JsonArrayElement(mTitle);
        popMenu.addItems(PopMenuArray);
    }

    @Override
    public void onClick(View v) {
        int click_id = v.getId();
        if (click_id == R.id.btn_right_action) {
            LogUtils.e("----返回页面----");
            finish();
        }
        switch (click_id) {
            case R.id.btn_bar_right:
            case R.id.btn_right_action: {
                LogUtils.e("----返回页面----");
                finish();
                break;
            }
            case R.id.btn_choose: {
                LogUtils.e("----跳转挑选备件页面----");
                if (requestTypes.size() > 0) {
                    requestType = requestTypes.get(requestTypeValue.getSelectPosition()).get("DataCode").valueAsString();
                    Intent intent = new Intent(mContext, SparePartActivity.class);
                    if (requestType.equals("Back")) {
                        intent.putExtra(Task.TASK_CLASS, Task.SPAREPART_RETURN);
                    } else if (requestType.equals("Receive")) {
                        intent.putExtra(Task.TASK_CLASS, Task.SPAREPART_CHOOSE);
                    } else {
                        intent.putExtra(Task.TASK_CLASS, Task.SPAREPART_CHOOSE);
                    }
                    if (selectSparePart.size() > 0) {
                        String selectSparePartStr = selectSparePart.toString();
                        intent.putExtra("selectSparePart", selectSparePartStr);
                    }
                    startActivityForResult(intent, SparePartActivity.SPAREPART_CHOOSE_CONFIRM_REQUESTCODE);
                } else {
                    //提示数据不完整，要求用户联系管理员 By Leo
                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("Error_PleaseForAdmin"), mContext);
                }
                break;
            }
            case R.id.btn_bar_left: {
                LogUtils.e("----展示菜单----");
                if (popMenu.isShowing()) {
                    popMenu.dismiss();
                } else {
                    popMenu.showAsDropDown(v);
                }
                break;
            }
            case R.id.sure: {
                SubmitData();
                break;
            }
        }
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    private void SubmitData() {
        if (selectSparePartNum == null || selectSparePartNum.size() == 0) {
            ToastUtil.showToastLong(LocaleUtils.getI18nValue("spare_part_list_not_empty"), mContext);
            return;
        }
        LogUtils.e("----提交备件申请单数据----");
        showCustomDialog(LocaleUtils.getI18nValue("waiting"));
        operatorID = (int) getLoginInfo().getId();
        requestType = requestTypes.get(requestTypeValue.getSelectPosition()).get("DataCode").valueAsString();
        String factory = SharedPreferenceManager.getFactory(mContext);
        HttpParams params = new HttpParams();
        JsonObjectElement json = new JsonObjectElement();
        json.set("Operator_ID", operatorID);
        json.set("Factory", factory);
        json.set("Bill_Type", requestType);
        json.set("Organise_ID", operatorTeamID);
        json.set("DetailList", selectSparePartNum);
        if (Long.getLong(getIntent().getStringExtra(Task.TASK_ID)) != null) {
            json.set("Task_Id", TaskID.intValue());
        } else {
            json.set("Task_Id", 0);
        }
        params.putJsonParams(json.toJson());
        LogUtils.e(params.getJsonParams());
        HttpUtils.post(mContext, "MaterialRequest/MaterialRequest_Create", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                LogUtils.e("MaterialRequest/MaterialRequest_Create--->提交的结果--->" + t);
                JsonObjectElement json = new JsonObjectElement(t);
                if (json.get("Success").valueAsBoolean()) {
                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("submitSuccess"), mContext);
                    finish();
                } else {
                    ToastUtil.showToastLong(DataUtil.isDataElementNull(json.get("Msg")), mContext);
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                //dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("MaterialRequest_Create--提交失败--->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastLong(LocaleUtils.getI18nValue("submit_Fail"), mContext);
                dismissCustomDialog();
            }
        });
    }

    private void getTeamIdByOrganiseIDByServe(final int operatorkeyno) {
        LogUtils.e("----获取用户所在的部门----");
        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    LogUtils.e("获取数据为空t--->" + t);
                    return;
                }
                LogUtils.e("GetOrganiseInfoByOperatorID--获取成功--->" + t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        operatorTeams.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }

                    operatorTeamID = operatorTeams.get(0).get("No").valueAsInt();
                    subordinateDepartments.setText(DataUtil.isDataElementNull(operatorTeams.get(0).get("OrganiseName")));

                    LogUtils.e("jsonArrayElement查询出来的---->" + jsonArrayElement.asArrayElement().toString());
                    LogUtils.e("创建人所属部门---->" + getLoginInfo().getOrganiseID());
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
                LogUtils.e("GetOrganiseInfoByOperatorID--获取失败--->" + errorNo + "---->" + strMsg);
            }
        };
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();

        params.put("Operator_ID", operatorkeyno);
        HttpUtils.get(this, "BaseOrganise/GetOrganiseInfoByOperatorID", params, callback);
    }

    //从共享数据中获取备件申请单类型数据
    private void getRequestTypeDataByShared() {
        Map<String, String> requestTypeMap = SharedPreferenceManager.getHashMapData(mContext, "SpareBillType");
        requestTypes.clear();
        for (Map.Entry<String, String> entry : requestTypeMap.entrySet()) {
            String DataCode = entry.getKey();
            String DataName = entry.getValue();
            requestTypes.add(new JsonObjectElement("{\"DataCode\":\"" + DataCode + "\",\"DataName\":\"" + DataName + "\"}"));
        }
        requestTypeValue.setDatas(mContext, requestTypes, "DataName");
        for (int i = 0; i < requestTypes.size(); i++) {
            if (TaskClass.equals(Task.SPAREPART_RETURN) &&
                    requestTypes.get(i).asObjectElement().get("DataCode").valueAsString().equals("Back")) {
                requestTypeValue.setText(requestTypes.get(i).get("DataName").valueAsString());
                requestTypeValue.setSelectPosition(i);
                requestType = requestTypes.get(i).get("DataCode").valueAsString();
            } else if (TaskClass.equals(Task.SPAREPART_REQUEST) &&
                    requestTypes.get(i).asObjectElement().get("DataCode").valueAsString().equals("Receive")) {
                requestTypeValue.setText(requestTypes.get(i).get("DataName").valueAsString());
                requestTypeValue.setSelectPosition(i);
                requestType = requestTypes.get(i).get("DataCode").valueAsString();
            }
        }
    }

    //2021-12-23 Abrahamguo 新增展示搜索视图方法
    private void initSearchView() {
        searchDataLists = operatorTeams;
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((TextView) findViewById(R.id.left_title)).setText(LocaleUtils.getI18nValue("title_search_group"));
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
        mResultAdapter.changeData(searchDataLists, "OrganiseName");
        mResultListView.setAdapter(mResultAdapter);

        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                ObjectElement searchResult = mResultAdapter.getItem(position);
                LogUtils.e("searchResult---->" + searchResult.toString());
                if (searchResult != null && !searchResult.get("OrganiseName").valueAsString().equals("")) {
                    operatorTeamID = searchResult.get("No").valueAsInt();
                    subordinateDepartments.setText(DataUtil.isDataElementNull(searchResult.get("OrganiseName")));
                    searchBox.setText("");
                    mDrawer_layout.closeDrawer(Gravity.RIGHT);
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"), mContext);
                }
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultAdapter.changeData(operatorTeams, "OrganiseName");
            }
        });
    }

    // 2021-12-01 Abrahamguo 新增在查询视图查询方法
    private ArrayList<ObjectElement> search(String keyword, String tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    private void refeshTable(ArrayList<ObjectElement> selectSparePart) {
        LogUtils.e("-----刷新选中的备件列表-----");
        sparePartListLayout.removeAllViews();
        if (selectSparePart != null && selectSparePart.size() > 0) {
            findViewById(R.id.table_fill).setVisibility(View.GONE);
            for (int i = 0; i < selectSparePart.size(); i++) {
                LinearLayout mLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spare_part_list, null);
                ObjectElement sparePart = selectSparePart.get(i);
                ((TextView) mLinearLayout.findViewById(R.id.name)).setText(DataUtil.isDataElementNull(sparePart.get("MaterialName")));
                ((TextView) mLinearLayout.findViewById(R.id.type)).setText(DataUtil.isDataElementNull(sparePart.get("MaterialType")));
                ((TextView) mLinearLayout.findViewById(R.id.quantity)).setText(DataUtil.isDataElementNull(sparePart.get("Quantity")));
                sparePartListLayout.addView(mLinearLayout);
            }
        } else {
            findViewById(R.id.table_fill).setVisibility(View.VISIBLE);
        }
    }
}
