package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.bean.StyleChangeInfo;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.schema.Team;
import com.emms.ui.DropEditText;
import com.emms.ui.KProgressHUD;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.king.zxing.Intents;
import com.smartown.tableview.library.TableView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.emms.R.id.equipment_name;

/**
 * 新增辅件跟踪页面
 * 2019/09/10
 */
public class ItemTrackingActivity extends BaseActivity implements View.OnClickListener {

    private DropEditText Det_sewLine;
    public final static int SEWING_LINE=11;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> mSewLine = new ArrayList<>();
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private EditText searchBox;
    Button btn_back;
    private int  searchtag =0;
    private Context context;
    private DrawerLayout mDrawer_layout;
    private String  cacheSearchName ="";
    private TextView menuSearchTitle;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private LinearLayout mContainer;
    private ArrayList<ObjectElement> styleChangeNewDatas=new ArrayList<>();
    private HashMap<String,ArrayList> styleChangeMap = new HashMap<>();
    private HashMap<String,ArrayList> styleChangeListMap = new HashMap<>();
    private ArrayList<String> keyList = new ArrayList<>();
    private ArrayList<ObjectElement> styleChangedatas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_itemtracking);
        context = this;
        initView();

    }

    private void initView(){
        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("itemtracking"));
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        Det_sewLine = (DropEditText) findViewById(R.id.Det_sewLine);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        mContainer = (LinearLayout) findViewById(R.id.styleChangeTaskDetailContainer);
        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.setHint(LocaleUtils.getI18nValue("hint_search_box"));
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        ((TextView) emptyView.findViewById(R.id.tvNothingFound)).setText(LocaleUtils.getI18nValue("nothing_found"));
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        clearBtn.setVisibility(View.INVISIBLE);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mResultAdapter = new ResultListAdapter(this);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                final String searchResult = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                LogUtils.e("searchResult---->"+searchResult+"---searchResult-->"+searchtag);
                if (!searchResult.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case SEWING_LINE:
                                    Det_sewLine.getmEditText().setText(searchResult);
                                    LogUtils.e("searchResult--->"+searchResult);
                                    getStyleChangeProcessingDataFromServer(searchResult);
                                    break;
                                default: {
                                }
                            }
                            searchBox.setText("");
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"), context);
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
                String keyword = s.toString();
                if (TextUtils.isEmpty(s)){
                    clearBtn.setVisibility(View.GONE);
                }else{
                    clearBtn.setVisibility(View.VISIBLE);
                }
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
        clearBtn.setOnClickListener(this);
        searchDataLists = new ArrayList<>();
        initData();
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

    private void initData(){
        getSewLineData();
        initDropSearchView(null, Det_sewLine.getmEditText(),
                LocaleUtils.getI18nValue("title_search_sew_line"), "sewingline", SEWING_LINE, LocaleUtils.getI18nValue("pleaseSelectSewingLine"), Det_sewLine.getDropImage());
    }

    @Override
    public void onClick(View view) {
        int click_id = view.getId();
        switch (click_id){
            case R.id.btn_right_action:
                searchBox.setText("");
                mDrawer_layout.closeDrawer(Gravity.RIGHT);
                finish();
                break;
            case R.id.btn_back:
                searchBox.setText("");
                mDrawer_layout.closeDrawer(Gravity.RIGHT);
                finish();
                break;
            case R.id.iv_search_clear:
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
                break;
        }
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
                    case SEWING_LINE:
                        searchDataLists.addAll(mSewLine);
                        break;
                    default: {

                    }
                    break;
                }
                cacheSearchName = searchName;
                searchtag = searTag;
                LogUtils.e("进入数据更新--->"+condition+"----searchtag--->"+searTag+"---searchDataLists---->"+searchDataLists.size());

                if (condition != null) {
                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                        mResultAdapter.changeData(searchDataLists, copySearchName);
                        menuSearchTitle.setText(searchTitle);
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                    } else {
                        LogUtils.e("显示的tips--->"+tips);
                        ToastUtil.showToastShort(tips, context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mResultAdapter.changeData(searchDataLists, copySearchName);
                        menuSearchTitle.setText(searchTitle);
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                    } else {
                        LogUtils.e("显示的tips--->"+tips);
                        ToastUtil.showToastShort(tips, context);
                    }
                }
            }
        });
    }

    private void getSewLineData(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        HttpUtils.getChangeStyle(context, "1.1/emms/getSewingline/" + getLoginInfo().getFactoryId(), params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if (t==null&&TextUtils.isEmpty(t)){
                    LogUtils.e("获取sewline为空");
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                    return;
                }
                LogUtils.e("getmSewingDataFromAPI--获取数据成功--->"+t);
                JsonObjectElement obj=new JsonObjectElement(t);
                ObjectElement objEle =obj.getAsObjectElement("data");
                if (objEle != null && objEle.asObjectElement() != null && objEle.get("sewinglines").asArrayElement() != null&&objEle.get("sewinglines").asArrayElement().size()>0) {
                    ArrayElement items = objEle.get("sewinglines").asArrayElement();
                    String sewline = items.get(0).valueAsString();
                    Det_sewLine.setText(sewline);
                    getStyleChangeProcessingDataFromServer(sewline);
                    for (int i = 0; i < items.size(); i++) {
                        JsonObjectElement jsonObj = new JsonObjectElement();
                        jsonObj.set("sewingline",items.get(i).toString().replace("\"",""));
                        mSewLine.add(jsonObj.asObjectElement());
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail")+errorNo,context);
                dismissCustomDialog();
            }
        });
    }

    //更新tableview
    private void dynamicRenderTable(ArrayList<StyleChangeInfo> lists) {
        LinearLayout mLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_style_change_task_details, null);
        TableView tableView = (TableView)mLinearLayout.findViewById(R.id.table);
        String operationValue = lists.get(0).getOperationName();
        TextView tv_orderno = (TextView) mLinearLayout.findViewById(R.id.tv_orderno);
        TextView tv_orderno_value = (TextView) mLinearLayout.findViewById(R.id.tv_orderno_value);
        TextView tv_jo_closedate = (TextView) mLinearLayout.findViewById(R.id.tv_jo_closedate);
        TextView tv_closedate = (TextView) mLinearLayout.findViewById(R.id.tv_closedate);
        RelativeLayout rl_attcement_name = (RelativeLayout) mLinearLayout.findViewById(R.id.rl_attcement_name);
        rl_attcement_name.setVisibility(View.GONE);
        tv_orderno.setText(LocaleUtils.getI18nValue("task_number"));
        tv_orderno_value.setText(lists.get(0).getOrderNo());
        tv_jo_closedate.setText(LocaleUtils.getI18nValue("end_date"));
        tv_closedate.setText(lists.get(0).getJoCloseDate());
        ((TextView) mLinearLayout.findViewById(R.id.tv_attcement_label)).setText(LocaleUtils.getI18nValue("attachment_name"));
        ((TextView) mLinearLayout.findViewById(R.id.tv_attcement_name)).setText(lists.get(0).getDes());
        ((TextView) mLinearLayout.findViewById(R.id.tv_label)).setText(LocaleUtils.getI18nValue("operation")+"：");
        ((TextView) mLinearLayout.findViewById(R.id.tv_value)).setText(LocaleUtils.getI18nValue(operationValue));
        ((TextView) mLinearLayout.findViewById(R.id.tv_type_label)).setText(LocaleUtils.getI18nValue("operation_type")+"：");
        ((TextView) mLinearLayout.findViewById(R.id.tv_type_value)).setText(LocaleUtils.getI18nValue(lists.get(0).getOperationType()));
        tableView.clearTableContents()
                .setHeader(LocaleUtils.getI18nValue("barCode"),LocaleUtils.getI18nValue("attachment_name"),
                        LocaleUtils.getI18nValue("return_back_qty"));
        for(int i =0;i<lists.size();i++){
            StyleChangeInfo styleChangeInfo = lists.get(i);
            tableView.addContent(styleChangeInfo.getAttachmentId(), lists.get(0).getDes(),styleChangeInfo.getReturnQty());
        }
        tableView.refreshTable();
        mContainer.addView(mLinearLayout);
    }

    private  void refreshTableView(){
        mContainer.removeAllViews();

        for(String key:keyList){
            LogUtils.e("key---->"+key);
            ArrayList<StyleChangeInfo> lists = styleChangeListMap.get(key);
            dynamicRenderTable(lists);
        }
    }

    private void getStyleChangeProcessingDataFromServer(String sewline){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        JSONObject data=new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject condition1 = new JSONObject();
        JSONObject condition2 = new JSONObject();
        try {
            condition1.put("fieldName","factory");
            condition1.put("conditionType","eq");
            condition1.put("value",getLoginInfo().getFactoryId());

            condition2.put("fieldName","sewingline");
            condition2.put("conditionType","eq");
            condition2.put("value",sewline);

            jsonArray.put(condition1);
            jsonArray.put(condition2);
            data.put("conditions",jsonArray);
//            data.put("total","");
            params.putJsonParams(data.toString());
            LogUtils.e("上传字段---->"+data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.postChangeStyle(context, "1.1/emms/findBeReceipt", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                super.onSuccess(t);
                if (t==null&&TextUtils.isEmpty(t)){
                    LogUtils.e("获取到的辅件信息为空");
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                    return;
                }
                LogUtils.e("获取辅件信息成功---->"+t);
                JsonObjectElement obj=new JsonObjectElement(t);
                ArrayElement objEle =obj.getAsArrayElement("data");
                if (objEle!=null&&objEle.size()>0){
                    styleChangeNewDatas.clear();
                    keyList.clear();
                    styleChangeListMap = new HashMap<String, ArrayList>();
                    styleChangeMap = new HashMap<String, ArrayList>();
                    for (int n=0;n<objEle.size();n++) {
                        ObjectElement assignmentObj = objEle.get(n).asObjectElement();
                        String orderNo = "";
                        if (!assignmentObj.get("orderNo").isNull()) {
                            orderNo = assignmentObj.get("orderNo").valueAsString();
                        }
                        DataElement date = assignmentObj.get("joCloseDate");
                        String joCloseDate = "";
                        if (!date.isNull()) {
                            joCloseDate = date.valueAsString();
                        }
                        ArrayElement assignmentItems = assignmentObj.get("assignments").asArrayElement();
                        LogUtils.e("assignmentItemt---->"+assignmentItems.size()+"---->"+LocaleUtils.getI18nValue("noData"));
                        for (int i = 0; i < assignmentItems.size(); i++) {
                            ObjectElement assigmentObj = assignmentItems.get(i).asObjectElement();
                            LogUtils.e("assigmentObj--->"+i+"---->"+assigmentObj.toString());
                            ObjectElement operationObj = assigmentObj.get("operation").asObjectElement();
                            String operationType = operationObj.get("type").toString().replace("\"", "");
                            LogUtils.e("operationType--->"+operationType);
                            ObjectElement operationNameObj = operationObj.get("name").asObjectElement();
                            String languageStr = LocaleUtils.getLanguage(context).toUpperCase().equals("ZH-CN") ? "zh_cn" : "en_us";
                            String operationName = operationNameObj.get(languageStr).toString().replace("\"", "");
                            ArrayList<StyleChangeInfo> styleChangeInfos = styleChangeListMap.get(operationType.toUpperCase());

                            ArrayElement productsArray = assigmentObj.get("products").asArrayElement();
                            StringBuilder AttchementNames = new StringBuilder();
                            String attchementName = "";
                            for (int j = 0; j < productsArray.size(); j++) {
                                ObjectElement productObj = productsArray.get(j).asObjectElement();
                                String attachmentId = productObj.get("ucc").toString().replace("\"", "");
                                String returnAqt = productObj.get("unReturnQty").valueAsString();

                                DataElement name = productObj.get("name");
                                String des = "";
                                if (!name.isNull()) {
                                    ObjectElement descs = productObj.get("name").asObjectElement();
                                    String language = LocaleUtils.getLanguage(context).toUpperCase().equals("ZH-CN") ? "zh_cn" : "en_us";
                                    des = descs.get(language).toString().replace("\"", "");
                                }
                                if (!attchementName.equals(des)) {
                                    attchementName = des;
                                    AttchementNames.append(attchementName);
                                    if (j != productsArray.size() - 1) {
                                        AttchementNames.append("/");
                                    }
                                }

                                LogUtils.e("获取到新增的数据---->" + des);
                                //将attachmentid 和operation匹配起来
                                ArrayList<StyleChangeInfo> typeList = styleChangeMap.get(attachmentId.toUpperCase());
                                StyleChangeInfo info = new StyleChangeInfo();
                                info.setOperationType(operationType);
                                info.setOperationName(operationName);
                                info.setOrderNo(orderNo);
                                info.setJoCloseDate(joCloseDate);
                                if (typeList == null) {
                                    typeList = new ArrayList<StyleChangeInfo>();

                                    typeList.add(info);
                                    styleChangeMap.put(attachmentId.toUpperCase(), typeList);
                                } else {
                                    typeList.add(info);
                                }

                                //将数据存入map中。
                                StyleChangeInfo styleChangeInfo = new StyleChangeInfo();
                                LogUtils.e("attchmentId66---->"+attachmentId);
                                styleChangeInfo.setAttachmentId(attachmentId);
                                styleChangeInfo.setReturnQty(returnAqt);
                                styleChangeInfo.setDes(des);//新增一个描述
                                String AttachmentType = productObj.get("attachmenttype") == null ? "" : productObj.get("attachmenttype").toString().replace("\"", "");
                                styleChangeInfo.setAttachmentType(AttachmentType);
                                String Requestqty = productObj.get("requestqty") == null ? "" : productObj.get("requestqty").toString().replace("\"", "");
                                styleChangeInfo.setRequestqty(Requestqty);
                                String Status = productObj.get("status") == null ? "" : productObj.get("status").toString().replace("\"", "");
                                styleChangeInfo.setStatus(Status);
                                String Version = productObj.get("version") == null ? "0" : productObj.get("version").toString().replace("\"", "");
                                styleChangeInfo.setVersion(Version);
                                String issueQty = productObj.get("issueqty") == null ? "0" : productObj.get("issueqty").toString().replace("\"", "");
                                String receiptQty = productObj.get("receiptqty") == null ? "0" : productObj.get("receiptqty").toString().replace("\"", "");
                                String location = productObj.get("location") == null ? "" : productObj.get("location").toString().replace("\"", "");
                                styleChangeInfo.setLocation(location);
                                styleChangeInfo.setIssueQty(issueQty);
                                styleChangeInfo.setReceiptQty(receiptQty);
                                styleChangeInfo.setOperationName(operationName);
                                styleChangeInfo.setOperationType(operationType);
                                styleChangeInfo.setJoCloseDate(joCloseDate);
                                styleChangeInfo.setOrderNo(orderNo);
                                styleChangeInfo.setScanQty("0");
                                if (styleChangeInfos == null) {
                                    styleChangeInfos = new ArrayList<StyleChangeInfo>();
                                    styleChangeInfos.add(styleChangeInfo);
                                    LogUtils.e("operationType666--->"+operationType);
                                    keyList.add(operationType+orderNo.toUpperCase());
                                    styleChangeListMap.put(operationType+orderNo.toUpperCase(), styleChangeInfos);
                                } else {
                                    styleChangeInfos.add(styleChangeInfo);
                                }
                            }
                            styleChangedatas.add(assignmentItems.get(i).asObjectElement());
                        }
                    }
                }else{
                    styleChangeNewDatas.clear();
                    styleChangeListMap.clear();
                    keyList.clear();
                    styleChangeMap.clear();
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("GetStyleChangeTaskDetailDataIsEmpty"),context);
                }
                refreshTableView();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail")+errorNo,context);
                dismissCustomDialog();
                LogUtils.e("获取辅件信息失败---->"+errorNo+"---strMsg--->"+strMsg);
            }
        });
    }
}
