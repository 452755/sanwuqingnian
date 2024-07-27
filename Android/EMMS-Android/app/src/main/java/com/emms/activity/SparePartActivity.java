package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.adapter.SparePartAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.king.zxing.Intents;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Abrahamguo 20201/12/21. 创建备件挑选页面
 *
 */
public class SparePartActivity extends NfcActivity implements View.OnClickListener {
    public static final int SPAREPART_CHOOSE_CONFIRM_RESULTCODE = 2;//备件挑选确认结果
    public static final int SPAREPART_CHOOSE_RESET_RESULTCODE = 1;//备件重新挑选结果
    public static final int SPAREPART_CHOOSE_CONFIRM_REQUESTCODE = 2;//备件挑选确认请求
    public static final int SPAREPART_CODE_SCAN_REQUESTCODE = 1;//备件码扫描请求
    public static final int SPAREPART_CHOOSE_RESET_REQUSETCODE = 3;//请求单重新挑选备件请求
    private String TaskClass;//任务类型
    private PullToRefreshListView mListView;//备件展示控件
    private ListView mGroupListView;//备件分组展示控件
    private SparePartAdapter adapter=null;//备件展示适配器
    private GroupAdapter groupAdapter;//备件分组适配器
    private ArrayList<ObjectElement> listItems=new ArrayList<>();//备件展示集合
    private ArrayList<ObjectElement> listGroup=new ArrayList<>();//备件分组集合
    private ArrayList<ObjectElement> selectedSparePartsNum = new ArrayList<>();//选中备件集合
    private Context context=this;
    private int pageIndex=1;//分页索引
    private int RecCount=0;//总数量
    private Handler handler=new Handler();
    private ObjectElement groupData=null;//选中分组数据
    private String selectedGroupDataCode;//选中分组数据编号
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";
    private EditText sparePartName,sparePartBrand,etSparePartCode,sparePartQuantity,sparePartType;
    private DropEditText sparePartWareHouse;
    private ArrayList<ObjectElement> selectItems = new ArrayList<>(); //设备选中备件集合
    private String TaskID;
    private String EquipmentId;
    private int isFilter = 0; //0默认不筛选,1第一次筛选,2之后筛选
    private String requestNo;
    private String WareHouseCode = "";
    private boolean isEquipmentHaveUsed = false;

    private ImageView clearBtn;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private EditText searchBox;
    private DrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> WareHouses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_part);
        initView();
        initWareHouse();
        initSearchView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SPAREPART_CODE_SCAN_REQUESTCODE: {
                if (data != null) {
                    LogUtils.e("获取到数据---->" + data.getStringExtra("result"));
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (result != null) {
                        etSparePartCode.setText(result);
                    }
                }
                break;
            }
            case SPAREPART_CHOOSE_CONFIRM_REQUESTCODE:{
                switch (resultCode){
                    case SPAREPART_CHOOSE_RESET_RESULTCODE:{
                        break;
                    }
                    case SPAREPART_CHOOSE_CONFIRM_RESULTCODE:{
                        setSparepartSelected();
                        Intent intent = new Intent();
                        intent.putExtra("selectSparePart",adapter.getSelectItems().toString());
                        intent.putExtra("selectSparePartNum",selectedSparePartsNum.toString());
                        setResult(SPAREPART_CHOOSE_CONFIRM_RESULTCODE,intent);
                        finish();
                        break;
                    }
                }
                break;
            }
            case SPAREPART_CHOOSE_RESET_REQUSETCODE:{
                switch (resultCode){
                    case SPAREPART_CHOOSE_RESET_RESULTCODE:{
                        break;
                    }
                    case SPAREPART_CHOOSE_CONFIRM_RESULTCODE:{
                        setSparepartSelected();
                        Intent intent = new Intent();
                        intent.putExtra("selectSparePart",adapter.getSelectItems().toString());
                        intent.putExtra("selectSparePartNum",selectedSparePartsNum.toString());
                        setResult(SPAREPART_CHOOSE_CONFIRM_RESULTCODE,intent);
                        finish();
                        break;
                    }
                }
                break;
            }
        }
    }

    private void setSparepartSelected(){
        ArrayList<ObjectElement> selectItems1 = adapter.getSelectItems();
        selectedSparePartsNum.clear();
        for(int i = 0;i<selectItems1.size();i++){
            ObjectElement item = new JsonObjectElement();
            if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)){
                item.set("UseCount",selectItems1.get(i).get("Quantity"));
                item.set("StockItemId",selectItems1.get(i).get("Stock_item_id"));
            }else{
                item.set("Quantity",selectItems1.get(i).get("Quantity"));
                item.set("Stock_Item_Id",selectItems1.get(i).get("Stock_item_id"));
            }
            if(TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
                item.set("Spare_BillNo",requestNo);
                if(selectItems1.get(i).get("Id")!=null&&!selectItems1.get(i).get("Id").isNull()){
                    item.set("Id",selectItems1.get(i).get("Id"));
                }
            }
            selectedSparePartsNum.add(item);
        }
    }

    /**
     * 初始化信息
     */
    private void getListItems() {
        int PAGE_SIZE = 10;
        LogUtils.e("pageindex----->"+pageIndex);

        if(RecCount!=0){
            if((pageIndex-1)* PAGE_SIZE >=RecCount){
                Toast toast=Toast.makeText(this, LocaleUtils.getI18nValue("noMoreData"),Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        params.put("pageSize", PAGE_SIZE);
        params.put("pageNumber",pageIndex);
        if(isFilter==0){
            params.put("CategoryId",selectedGroupDataCode);
        }else if(isFilter==1){
            if(!DataUtil.isNullOrEmpty(sparePartName.getText().toString())){
                params.put("Name",sparePartName.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(sparePartBrand.getText().toString())){
                params.put("Brand",sparePartBrand.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(etSparePartCode.getText().toString())){
                params.put("QRCode",etSparePartCode.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(WareHouseCode)){
                params.put("Warehouse",WareHouseCode);
            }
            if(!DataUtil.isNullOrEmpty(sparePartQuantity.getText().toString())){
                params.put("Inventory",Integer.parseInt(sparePartQuantity.getText().toString()));
            }
            if(!DataUtil.isNullOrEmpty(sparePartType.getText().toString())){
                params.put("Type",Integer.parseInt(sparePartType.getText().toString()));
            }
        }else if(isFilter==2){
            params.put("CategoryId",selectedGroupDataCode);
            if(!DataUtil.isNullOrEmpty(sparePartName.getText().toString())){
                params.put("Name",sparePartName.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(sparePartBrand.getText().toString())){
                params.put("Brand",sparePartBrand.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(etSparePartCode.getText().toString())){
                params.put("QRCode",etSparePartCode.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(WareHouseCode)){
                params.put("Warehouse",WareHouseCode);
            }
            if(!DataUtil.isNullOrEmpty(sparePartQuantity.getText().toString())){
                params.put("Inventory",Integer.parseInt(sparePartQuantity.getText().toString()));
            }
            if(!DataUtil.isNullOrEmpty(sparePartType.getText().toString())){
                params.put("Type",Integer.parseInt(sparePartType.getText().toString()));
            }
        }

        LogUtils.e(params.getUrlParams().toString());

        HttpUtils.get(this, "Material/GetMaterialListByParam", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到备件数据----->"+t);
                        JsonObjectElement json=new JsonObjectElement(t);
                        if(json.get("Success").valueAsBoolean()){
                            RecCount=json.get("RecCount").valueAsInt();
                            LogUtils.e("RecCount---->"+RecCount);
                            if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                                if(pageIndex==1){
                                    listItems.clear();
                                }
                                LogUtils.e("pageIndexeee---->"+pageIndex);
                                pageIndex++;
                                if(isFilter==1){
                                    isFilter = 2;
                                    pageIndex = 1;
                                    String MaterialCategory = json.get("PageData").asArrayElement().get(0).asObjectElement().get("MaterialCategory").valueAsString();
                                    for(int i=0;i<listGroup.size();i++){
                                        if(listGroup.get(i).get("DataCode").valueAsString().equals(MaterialCategory)){
                                            groupData=listGroup.get(i);
                                            groupAdapter.setSelection(listGroup.get(i));
                                            groupAdapter.notifyDataSetChanged();
                                            selectedGroupDataCode=listGroup.get(i).get("DataCode").valueAsString();
                                            mGroupListView.smoothScrollToPositionFromTop(i, 300, 500);
                                        }
                                    }
                                    getListItems();
                                    return;
                                }else {
                                    for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                                        listItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                    }
                                }
                            }else{
                                listItems.clear();
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                            }
                            adapter.setListItems(listItems);
                        }
                        else{
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),context);}
                    }finally {
                        dismissCustomDialog();
                    }

                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                dismissCustomDialog();
            }
        });
    }

    private void getEquipmentSelectItems(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        params.put("pageSize", Integer.MAX_VALUE);
        params.put("pageNumber",1);
        params.put("TaskId",TaskID);
        params.put("EquipmentId",EquipmentId);
        params.put("OperatorId",(int)getLoginInfo().getId());

        LogUtils.e(params.getUrlParams().toString());

        HttpUtils.get(this, "MaterialRequest/GetMaterialUsageList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到备件数据----->"+t);
                        JsonObjectElement json=new JsonObjectElement(t);
                        if(json.get("Success").valueAsBoolean()){
                            selectItems.clear();
                            if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                                for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                                    selectItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                }
                                isEquipmentHaveUsed = true;
                            }
                            adapter.setListItems(listItems,selectItems);
                        }
                        else{
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),context);}
                    }finally {
                        dismissCustomDialog();
                    }

                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                dismissCustomDialog();
            }
        });
    }

    private void getRequestSelectItems(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        params.put("pageSize", Integer.MAX_VALUE);
        params.put("pageNumber",1);
        params.put("SpareBillNo",requestNo);

        HttpUtils.get(this, "MaterialRequest/GetRequestBillByNo_Detail", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到备件数据----->"+t);
                        JsonObjectElement json=new JsonObjectElement(t);
                        if(json.get("Success").valueAsBoolean()){
                            selectItems.clear();
                            if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                                for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                                    selectItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                }
                            }else{
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                            }
                            adapter.setListItems(listItems,selectItems);
                        }
                        else{
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),context);}
                    }finally {
                        dismissCustomDialog();
                    }

                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                dismissCustomDialog();
            }
        });
    }

    private void getListItemsByEquipment(){
        int PAGE_SIZE = 10;
        LogUtils.e("pageindex----->"+pageIndex);

        if(RecCount!=0){
            if((pageIndex-1)* PAGE_SIZE >=RecCount){
                Toast toast=Toast.makeText(this, LocaleUtils.getI18nValue("noMoreData"),Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        params.put("pageSize", PAGE_SIZE);
        params.put("pageNumber",pageIndex);
        params.put("operatorId",(int)getLoginInfo().getId());
        if(isFilter==0){
            params.put("CategoryId",selectedGroupDataCode);
        }else if(isFilter==1){
            if(!DataUtil.isNullOrEmpty(sparePartName.getText().toString())){
                params.put("Name",sparePartName.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(sparePartBrand.getText().toString())){
                params.put("Brand",sparePartBrand.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(etSparePartCode.getText().toString())){
                params.put("QRCode",etSparePartCode.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(WareHouseCode)){
                params.put("Warehouse",WareHouseCode);
            }
            if(!DataUtil.isNullOrEmpty(sparePartQuantity.getText().toString())){
                params.put("Inventory",Integer.parseInt(sparePartQuantity.getText().toString()));
            }
            if(!DataUtil.isNullOrEmpty(sparePartType.getText().toString())){
                params.put("Type",Integer.parseInt(sparePartType.getText().toString()));
            }
        }else if(isFilter==2){
            params.put("CategoryId",selectedGroupDataCode);
            if(!DataUtil.isNullOrEmpty(sparePartName.getText().toString())){
                params.put("Name",sparePartName.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(sparePartBrand.getText().toString())){
                params.put("Brand",sparePartBrand.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(etSparePartCode.getText().toString())){
                params.put("QRCode",etSparePartCode.getText().toString());
            }
            if(!DataUtil.isNullOrEmpty(WareHouseCode)){
                params.put("Warehouse",WareHouseCode);
            }
            if(!DataUtil.isNullOrEmpty(sparePartQuantity.getText().toString())){
                params.put("Inventory",Integer.parseInt(sparePartQuantity.getText().toString()));
            }
            if(!DataUtil.isNullOrEmpty(sparePartType.getText().toString())){
                params.put("Type",Integer.parseInt(sparePartType.getText().toString()));
            }
        }

        if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)){
            params.put("isBill",Boolean.FALSE.toString());
            params.put("Task_id",TaskID);
            params.put("equipment_id",EquipmentId);
        }

        LogUtils.e("GetMachineRepairStockListByApp 请求的参数----->"+params.getUrlParams());

        HttpUtils.get(this, "MaterialRequest/GetMachineRepairStockListByApp", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到备件数据----->"+t);
                        JsonObjectElement json=new JsonObjectElement(t);
                        if(json.get("Success").valueAsBoolean()){
                            RecCount=json.get("RecCount").valueAsInt();
                            LogUtils.e("RecCount---->"+RecCount);
                            if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                                if(pageIndex==1){
                                    listItems.clear();
                                }
                                LogUtils.e("pageIndexeee---->"+pageIndex);
                                pageIndex++;
                                if(isFilter==1){
                                    pageIndex = 1;
                                    isFilter = 2;
                                    String MaterialCategory = json.get("PageData").asArrayElement().get(0).asObjectElement().get("MaterialCategory").valueAsString();
                                    for(int i=0;i<listGroup.size();i++){
                                        if(listGroup.get(i).get("DataCode").valueAsString().equals(MaterialCategory)){
                                            groupData=listGroup.get(i);
                                            groupAdapter.setSelection(listGroup.get(i));
                                            groupAdapter.notifyDataSetChanged();
                                            selectedGroupDataCode=listGroup.get(i).get("DataCode").valueAsString();
                                            mGroupListView.smoothScrollToPositionFromTop(i, 300, 500);
                                        }
                                    }
                                    getListItemsByEquipment();
                                    return;
                                }else {
                                    for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                                        listItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                    }
                                }
                            }else{
                                listItems.clear();
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);
                            }
                            adapter.setListItems(listItems);
                        }
                        else{
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),context);}
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),context);
                dismissCustomDialog();
            }
        });
    }

    //初始化视图
    private void initView(){
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        switch (TaskClass){
            case Task.SPAREPART_CHOOSE:{
                ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("spare_part_selection"));
                break;
            }
            case Task.SPAREPART_RETURN:{
                ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("machine_repair_inventory_list"));
                break;
            }
            case Task.SPAREPART_EQUIPMENT_CHOOSE:{
                ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("machine_repair_inventory_list"));
                TaskID = getIntent().getStringExtra("TaskID");
                EquipmentId = getIntent().getStringExtra("EquipmentId");
                getEquipmentSelectItems();
                break;
            }
            case Task.SPAREPART_CHECK_MESSAGE:{
                ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("spare_part_selection"));
                requestNo = getIntent().getStringExtra("requestNo");
                getRequestSelectItems();
                break;
            }
        }

        adapter=new SparePartAdapter(this,listItems,TaskClass);

        if(getIntent().getStringExtra("selectSparePart")!=null){
            String selectSparePartStr = getIntent().getStringExtra("selectSparePart");
            JsonArrayElement selectSparePartJson = new JsonArrayElement(selectSparePartStr);
            ArrayList<ObjectElement> selectSparePartEles = new ArrayList<>();
            for(int i = 0;i<selectSparePartJson.size();i++){
                selectSparePartEles.add(selectSparePartJson.get(i).asObjectElement());
            }
            adapter.setListItems(listItems,selectSparePartEles);
        }

        findViewById(R.id.summit).setOnClickListener(this);
        ((Button) findViewById(R.id.summit)).setText(LocaleUtils.getI18nValue("submit"));

        findViewById(R.id.filter).setOnClickListener(this);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvSparePartName)).setText(LocaleUtils.getI18nValue("spare_part_name"));
        ((TextView) findViewById(R.id.tvSparePartBrand)).setText(LocaleUtils.getI18nValue("spare_part_brand"));
        ((TextView) findViewById(R.id.tv_SparePart_code)).setText(LocaleUtils.getI18nValue("spare_part_code"));
        ((TextView) findViewById(R.id.tvSparePartWareHouse)).setText(LocaleUtils.getI18nValue("inventory_location"));
        ((TextView) findViewById(R.id.tvSparePartQuantity)).setText(LocaleUtils.getI18nValue("inventory_quantity"));
        ((TextView) findViewById(R.id.tvSparePartType)).setText(LocaleUtils.getI18nValue("typeof_spare_part"));

        findViewById(R.id.reset_button).setOnClickListener(this);
        findViewById(R.id.search_button).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_button)).setText(LocaleUtils.getI18nValue("Reset"));
        ((Button) findViewById(R.id.search_button)).setText(LocaleUtils.getI18nValue("sure"));
        sparePartName = (EditText) findViewById(R.id.spare_part_name);
        sparePartBrand = (EditText) findViewById(R.id.spare_part_brand);
        etSparePartCode = (EditText) findViewById(R.id.et_SparePart_code);
        sparePartWareHouse = (DropEditText) findViewById(R.id.WareHouse);
        sparePartQuantity = (EditText) findViewById(R.id.spare_part_quantity);
        sparePartType = (EditText) findViewById(R.id.spare_part_type);

        sparePartName.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        sparePartBrand.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        etSparePartCode.setHint(LocaleUtils.getI18nValue("scan"));
        sparePartWareHouse.setHint(LocaleUtils.getI18nValue("select"));
        sparePartQuantity.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        sparePartType.setHint(LocaleUtils.getI18nValue("pleaseInput"));

        findViewById(R.id.spare_part_code_action).setOnClickListener(this);

        sparePartWareHouse.getmEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView();
            }
        });
        sparePartWareHouse.getDropImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView();
            }
        });

        findViewById(R.id.btn_right_action).setOnClickListener(this);
        mListView = (PullToRefreshListView) findViewById(R.id.id_wait_list);
        mListView.setAdapter(adapter);
        mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupAdapter.setSelection(listGroup.get(position));
                pageIndex=1;
                RecCount=0;
                groupData=listGroup.get(position);
                if (groupData!=null){
                    selectedGroupDataCode = listGroup.get(position).get("DataCode").valueAsString();
                    isFilter=isFilter==1?2:isFilter;
                    if (TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
                        getListItems();
                    }else if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
                        getListItemsByEquipment();
                    }
                }

            }
        });
        groupAdapter=new GroupAdapter(this,listGroup);
        mGroupListView.setAdapter(groupAdapter);
        getGroupData(); //设置组别
        mListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
                            getListItems();
                        }else if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
                            getListItemsByEquipment();
                        }
                        mListView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
    }

    public void getGroupData() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                LogUtils.e(t);
                if(t!=null){
                    try{
                        ArrayElement json = new JsonArrayElement("[]");
                        if (TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
                            json = new JsonObjectElement(t).get("PageData").asArrayElement();
                        }else if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
                            json = new JsonArrayElement(t);
                        }
                        if(json.size()>0){
                            listGroup.clear();
                            for(int i=0;i<json.size();i++){
                                listGroup.add(json.get(i).asObjectElement());
                            }
                            groupData=listGroup.get(0);
                            groupAdapter.setDatas(listGroup);
                            groupAdapter.setSelection(listGroup.get(0));
                            groupAdapter.notifyDataSetChanged();
                            selectedGroupDataCode=listGroup.get(0).get("DataCode").valueAsString();
                            if (TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
                                getListItems();
                            }else if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
                                getListItemsByEquipment();
                            }
                            dismissCustomDialog();
                        }else {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoMaterialStock"),context);
                            dismissCustomDialog();
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null) {
                            ToastUtil.showToastShort(e.getCause().toString(), context);
                        }
                        dismissCustomDialog();
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoMaterialStock"),context);
                    }
                });
                dismissCustomDialog();
            }
        };
        HttpParams params = new HttpParams();
        if (TaskClass.equals(Task.SPAREPART_CHOOSE) || TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)) {
            if (isFilter == 1) {
                if (!DataUtil.isNullOrEmpty(sparePartName.getText().toString())) {
                    params.put("Name", sparePartName.getText().toString());
                }
                if (!DataUtil.isNullOrEmpty(sparePartBrand.getText().toString())) {
                    params.put("Brand", sparePartBrand.getText().toString());
                }
                if (!DataUtil.isNullOrEmpty(etSparePartCode.getText().toString())) {
                    params.put("QRCode", etSparePartCode.getText().toString());
                }
                if (!DataUtil.isNullOrEmpty(WareHouseCode)) {
                    params.put("Warehouse", WareHouseCode);
                }
                if (!DataUtil.isNullOrEmpty(sparePartQuantity.getText().toString())) {
                    params.put("Inventory", Integer.parseInt(sparePartQuantity.getText().toString()));
                }
                if (!DataUtil.isNullOrEmpty(sparePartType.getText().toString())) {
                    params.put("Type", sparePartType.getText().toString());
                }
                isFilter = 2;
            }
            HttpUtils.get(this, "Material/GetMaterialCategoryList", params, callback);
        } else if (TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE) || TaskClass.equals(Task.SPAREPART_RETURN)) {
            params.put("operatorId", (int) getLoginInfo().getId());
            HttpUtils.get(this, "MaterialRequest/GetMachineRepairCategoryList", params, callback);
        }
    }

    @Override
    public void onClick(View v) {
        int clikId =v.getId();
        switch (clikId) {
            case R.id.btn_right_action:{
                finish();
                break;
            }
            case R.id.summit:{
                summitDate();
                break;
            }
            case R.id.search_button:{
                SearchData();
                break;
            }
            case R.id.reset_button:{
                ResetData();
                break;
            }
            case R.id.filter:{
                if(findViewById(R.id.search_filter).getVisibility()==View.GONE||
                        findViewById(R.id.search_filter).getVisibility()==View.INVISIBLE){
                    buttonAnim(true);
                }
                else {
                    buttonAnim(false);
                }
                break;
            }
            case R.id.spare_part_code_action:{
                Intent it = new Intent(context, com.king.zxing.CaptureActivity.class);
                it.setAction(Intents.Scan.ACTION);
                it.putExtra(Intents.Scan.CAMERA_ID,0);
                it.putExtra(KEY_IS_CONTINUOUS,false);
                startActivityForResult(it, SPAREPART_CODE_SCAN_REQUESTCODE);
                break;
            }
        }

    }

    private void summitDate(){
        switch (TaskClass){
            case Task.SPAREPART_EQUIPMENT_CHOOSE:{
                summitEquipmentSparePart();
                break;
            }
            case Task.SPAREPART_RETURN:{
                if(adapter.getSelectItems().size()==0){
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("please_select_spare_parts_first"),context);
                    return;
                }
                //查看所有已挑选备件
                Intent usedSpaerPart = new Intent(context, SparePartListViewActivity.class);
                usedSpaerPart.putExtra(Task.TASK_CLASS,Task.SPAREPART_CONFIRM);
                String selectSparePart = adapter.getSelectItems().toString();
                usedSpaerPart.putExtra("selectSparePart",selectSparePart);
                startActivityForResult(usedSpaerPart, SPAREPART_CHOOSE_CONFIRM_REQUESTCODE);
                break;
            }
            case Task.SPAREPART_CHOOSE:{
                if(adapter.getSelectItems().size()==0){
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("please_select_spare_parts_first"),context);
                    return;
                }
                //查看所有已挑选备件
                Intent usedSpaerPart = new Intent(context, SparePartListViewActivity.class);
                usedSpaerPart.putExtra(Task.TASK_CLASS,Task.SPAREPART_CONFIRM);
                String selectSparePart = adapter.getSelectItems().toString();
                usedSpaerPart.putExtra("selectSparePart",selectSparePart);
                startActivityForResult(usedSpaerPart, SPAREPART_CHOOSE_CONFIRM_REQUESTCODE);
                break;
            }
            case Task.SPAREPART_CHECK_MESSAGE:{
                if(adapter.getSelectItems().size()==0){
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("please_select_spare_parts_first"),context);
                    return;
                }
                //查看所有已挑选备件
                Intent usedSpaerPart = new Intent(context, SparePartListViewActivity.class);
                usedSpaerPart.putExtra(Task.TASK_CLASS,Task.SPAREPART_CONFIRM);
                String selectSparePart = adapter.getSelectItems().toString();
                usedSpaerPart.putExtra("selectSparePart",selectSparePart);
                startActivityForResult(usedSpaerPart, SPAREPART_CHOOSE_RESET_REQUSETCODE);
                break;
            }
        }
    }

    private void summitEquipmentSparePart(){
        if((adapter.getSelectItems() == null||adapter.getSelectItems().size()==0) && isEquipmentHaveUsed == false){
            ToastUtil.showToastShort("请选择备件",context);
            return;
        }
        setSparepartSelected();
        JsonArrayElement selectSparePartNum = new JsonArrayElement("[]");
        for (ObjectElement item:selectedSparePartsNum) {
            selectSparePartNum.add(item);
        }
        LogUtils.e("----提交备件申请单数据----");
        showCustomDialog(LocaleUtils.getI18nValue("waiting"));
        String factory = SharedPreferenceManager.getFactory(context);
        HttpParams params = new HttpParams();
        JsonObjectElement json = new JsonObjectElement();
        json.set("OperatorId",(int) getLoginInfo().getId());
        json.set("Factory",factory);
        json.set("EquipmentId",Integer.parseInt(EquipmentId));
        json.set("TaskId",Integer.parseInt(TaskID));
        json.set("Detail",selectSparePartNum);
        params.putJsonParams(json.toJson());
        LogUtils.e(params.getJsonParams());
        HttpUtils.post(context, "MaterialRequest/MaterialUsageModify", params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                LogUtils.e("MaterialRequest/MaterialUsageModify--->提交的结果--->"+t);
                JsonObjectElement json = new JsonObjectElement(t);
                if(json.get("Success").valueAsBoolean()){
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitSuccess"),context);
                    finish();
                }
                else {
                    ToastUtil.showToastLong(DataUtil.isDataElementNull(json.get("Msg")),context);
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg){
                //dismissCustomDialog();
                HttpUtils.tips(context, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("MaterialRequest_Create--提交失败--->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastLong(LocaleUtils.getI18nValue("submit_Fail"),context);
                dismissCustomDialog();
            }
        });
    }

    private void ResetData(){
        sparePartName.setText("");
        sparePartBrand.setText("");
        etSparePartCode.setText("");
        sparePartWareHouse.setText("");
        sparePartQuantity.setText("");
        WareHouseCode = "";
        isFilter = 0;
        pageIndex=1;
        RecCount=0;
        if (TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
            getGroupData();
        }else if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
            getListItemsByEquipment();
        }
        buttonAnim(false);
    }

    private void SearchData(){
        isFilter = 1;
        pageIndex=1;
        RecCount=0;
        buttonAnim(false);
        if(sparePartName.getText().toString().equals("")
                &&sparePartBrand.getText().toString().equals("")
                &&etSparePartCode.getText().toString().equals("")
                &&WareHouseCode.equals("")
                &&sparePartQuantity.getText().toString().equals("")
                &&sparePartType.getText().toString().equals("")){
            isFilter = 0;
        }
        if (TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
            getGroupData();
        }else if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
            getListItemsByEquipment();
        }
    }

    private void buttonAnim(final boolean showChannelFilterView){
        if(showChannelFilterView){
            Animation operatingAnim2 = AnimationUtils.loadAnimation(this, R.anim.expand);
            operatingAnim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.search_filter).setVisibility(View.VISIBLE);
                }
            });
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim2.setInterpolator(lin);
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);
        }else{
            Animation operatingAnim2 = AnimationUtils.loadAnimation(this, R.anim.collapse);
            operatingAnim2.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.search_filter).setVisibility(View.GONE);
                }
            });
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim2.setInterpolator(lin);
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);
        }
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    public class GroupAdapter extends BaseAdapter {

        private Context context;
        public ArrayList<ObjectElement> getDatas() {
            return datas;
        }

        public void setDatas(ArrayList<ObjectElement> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        private ArrayList<ObjectElement> datas;
        public GroupAdapter(Context context,ArrayList<ObjectElement> datas) {
            this.context =context;
            this.datas =datas;
            notifyDataSetChanged();
        }
        private ObjectElement selection;
        public void setSelection(ObjectElement selection){
            this.selection=selection;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_group_name, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

                holder.groupItem = (TextView) convertView
                        .findViewById(R.id.group_name);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(selection!=null){
                if(datas.get(position).equals(selection)){
                    holder.groupItem.setTextColor(Color.parseColor("#AB2D42"));
                }
                else {
                    holder.groupItem.setTextColor(Color.BLACK);
                }
            }
            holder.groupItem.setText(DataUtil.isDataElementNull(datas.get(position).get("DataName")));

            return convertView;
        }
    }

    //视图支持
    private final class ViewHolder {
        TextView groupItem;
    }

    private void initWareHouse(){
        Map<String,String> wareHouses = SharedPreferenceManager.getHashMapData(context,"SpareWarehouse");
        WareHouses.clear();
        for (Map.Entry<String, String> entry : wareHouses.entrySet()) {
            String DataCode = entry.getKey();
            String DataName = entry.getValue();
            WareHouses.add(new JsonObjectElement("{\"DataCode\":\""+DataCode+"\",\"DataName\":\""+DataName+"\"}"));
        }
    }

    private void showSearchView() {
        searchDataLists = WareHouses;
        mResultAdapter.changeData(searchDataLists,"DataName");
        mDrawer_layout.openDrawer(Gravity.RIGHT);
    }

    //2021-12-23 Abrahamguo 新增展示搜索视图方法
    private void initSearchView() {
        searchDataLists = WareHouses;
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((TextView) findViewById(R.id.left_title)).setText(LocaleUtils.getI18nValue("物料仓库"));
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
        mResultAdapter.changeData(searchDataLists,"OrganiseName");
        mResultListView.setAdapter(mResultAdapter);

        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id){
                ObjectElement searchResult = mResultAdapter.getItem(position);
                LogUtils.e("searchResult---->" + searchResult.toString());
                if (searchResult!=null&&!searchResult.get("DataName").valueAsString().equals("")) {
                    WareHouseCode = searchResult.get("DataCode").valueAsString();
                    sparePartWareHouse.setText(DataUtil.isDataElementNull(searchResult.get("DataName")));
                    searchBox.setText("");
                    mDrawer_layout.closeDrawer(Gravity.RIGHT);
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"), context);
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
}
