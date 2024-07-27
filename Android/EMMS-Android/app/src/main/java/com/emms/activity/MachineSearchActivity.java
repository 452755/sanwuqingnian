package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.schema.Team;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.king.zxing.Intents;
import com.tencent.bugly.crashreport.CrashReport;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 说明：机器查找页面
* 添加时间：2019/9/26 上午10:56
* 作者：jason
*/
public class MachineSearchActivity extends NfcActivity implements View.OnClickListener {

    EditText iccard_id;
    EditText et_qr_code;
    ImageView  device_num_action;
    DropEditText et_oracle_id;
    EditText et_device_num;
    EditText et_factory_num;
    EditText et_equipment_name;
    EditText et_use_department;
    Button btn_search;
    Button btn_submit;
    Button btn_history;
    Button btn_clearData;
    ImageView btn_right_action;

    TextView tvICCardId;
    TextView tv_qr_code;
    TextView tv_oracleID;
    TextView tv_device_num;
    TextView tv_factory_num;
    TextView tv_equipment_name;
    TextView tv_using_department;
    TextView tv_title;
    TextView tv_equipment_status;
    DropEditText et_equipment_status;
    TextView tv_subordinate_departments;
    TextView tv_subordinate_departments_value;

    private TextView menuSearchTitle;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private EditText searchBox;


    public final static int GROUP = 4;
    public final static int STATUS = 5;

    Context context;
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    private ArrayList<ObjectElement> mTeamNamelist = new ArrayList<>();
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> typeList=new ArrayList<>();

    private DrawerLayout mDrawer_layout;

    private int  searchtag =0;

    private String keyword;

    String TeamId;
    String Equipment_ID;//主键 怎么来怎么回

    String iccard ;
    String qrcode ;
    String oracle_id ;
    String device_num ;
    String factory_num ;
    String equipment_name ;
    String use_equipment ;
    String belong_deparement;//所属部门
    String equipment_status;//设备状态
    String equipmentCode;

    private boolean issave=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_search);
        context = this;
        initData();
        //作用:初始化控件  Jason 2019/9/26 下午1:49
        initView();
    }

    private void initView(){
        iccard_id = (EditText) findViewById(R.id.iccard_id);
        et_qr_code = (EditText) findViewById(R.id.et_qr_code);
        device_num_action = (ImageView) findViewById(R.id.device_num_action);
        et_oracle_id = (DropEditText) findViewById(R.id.et_oracle_id);
        et_device_num = (EditText) findViewById(R.id.et_device_num);
        et_factory_num = (EditText) findViewById(R.id.et_factory_num);
        et_equipment_name = (EditText) findViewById(R.id.et_equipment_name);
        et_use_department = (EditText) findViewById(R.id.et_use_department);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_history = (Button) findViewById(R.id.btn_history);
        btn_clearData = (Button) findViewById(R.id.btn_clearData);
        btn_right_action = (ImageView) findViewById(R.id.btn_right_action);
        tv_equipment_name = (TextView) findViewById(R.id.tv_equipment_name);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_device_num = (TextView) findViewById(R.id.tv_device_num);
        tv_factory_num = (TextView) findViewById(R.id.tv_factory_num);
        tv_qr_code = (TextView) findViewById(R.id.tv_qr_code);
        tv_using_department = (TextView) findViewById(R.id.tv_using_department);
        tv_oracleID = (TextView) findViewById(R.id.tv_oracleID);
        tvICCardId = (TextView) findViewById(R.id.tvICCardId);
        tv_equipment_status = (TextView) findViewById(R.id.tv_equipment_status);
        tv_subordinate_departments = (TextView) findViewById(R.id.tv_subordinate_departments);
        tv_subordinate_departments_value = (TextView) findViewById(R.id.tv_subordinate_departments_value);
        et_equipment_status = (DropEditText) findViewById(R.id.et_equipment_status);

        //作用: 输入默认值 Jason 2019/10/8 下午2:35
        Map<String,String> map = SharedPreferenceManager.getHashMapData(context,"machine_map");
        iccard_id.setText(map.get("iccard"));
        et_qr_code.setText(map.get("qrcode"));
        et_oracle_id.setText(map.get("oracle_id"));
        et_device_num.setText(map.get("device_num"));
        et_factory_num.setText(map.get("factory_num"));
        et_equipment_name.setText(map.get("equipment"));
        et_use_department.setText(map.get("use_equipment"));
        et_equipment_status.setText(map.get("equipment_status"));
        tv_subordinate_departments_value.setText(map.get("subordinate_departments"));
        TeamId = map.get("TeamId");
        equipmentCode = map.get("equipmentCode");
        belong_deparement = map.get("subordinate_departments");
        Equipment_ID = map.get("Equipment_ID");
        LogUtils.e("获取储存的map--->"+map.get(equipment_name));

        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        device_num_action.setOnClickListener(this);
        btn_right_action.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_history.setOnClickListener(this);
        btn_clearData.setOnClickListener(this);

        //作用:初始化控件基本信息  Jason 2019/9/26 下午1:51
        iccard_id.setHint(LocaleUtils.getI18nValue("scan"));
        et_qr_code.setHint(LocaleUtils.getI18nValue("scan"));
        et_oracle_id.setHint(LocaleUtils.getI18nValue("please_select"));
        et_equipment_status.setHint(LocaleUtils.getI18nValue("please_select"));
        et_equipment_name.setHint(LocaleUtils.getI18nValue("machine_input"));
        et_use_department.setHint(LocaleUtils.getI18nValue("machine_input"));
        et_factory_num.setHint(LocaleUtils.getI18nValue("machine_input"));
        et_device_num.setHint(LocaleUtils.getI18nValue("machine_input"));

        tv_equipment_name.setText(LocaleUtils.getI18nValue("equipment_name"));
        tv_device_num.setText(LocaleUtils.getI18nValue("equipment_num"));
        tv_title.setText(LocaleUtils.getI18nValue("machine_search"));
        tvICCardId.setText(LocaleUtils.getI18nValue("machine_rfid"));
        tv_oracleID.setText(LocaleUtils.getI18nValue("machine_use_department"));
        tv_qr_code.setText(LocaleUtils.getI18nValue("machine_qrcode"));
        tv_device_num.setText(LocaleUtils.getI18nValue("machine_assetnum"));
        tv_factory_num.setText(LocaleUtils.getI18nValue("machine_serialnum"));
        btn_history.setText(LocaleUtils.getI18nValue("machine_history_btn"));
        btn_search.setText(LocaleUtils.getI18nValue("machine_search_btn"));
        btn_submit.setText(LocaleUtils.getI18nValue("machine_submit_btn"));
        btn_clearData.setText(LocaleUtils.getI18nValue("machine_clear_data"));
        tv_subordinate_departments.setText(LocaleUtils.getI18nValue("subordinate_departments"));
        tv_equipment_status.setText(LocaleUtils.getI18nValue("device_status"));

        initSearchView();

        initDropSearchView(null, et_oracle_id.getmEditText(),
                LocaleUtils.getI18nValue("title_search_group"), Team.TEAMNAME, GROUP, LocaleUtils.getI18nValue("pleaseScanICcard"), et_oracle_id.getDropImage());

        initDropSearchView(null,et_equipment_status.getmEditText(),LocaleUtils.getI18nValue("device_status"),"DataName",STATUS,LocaleUtils.getI18nValue("please_select"),et_equipment_status.getDropImage());


    }
    /**
    * 说明：加载部门
    * 添加时间：2019/9/27 下午1:35
    * 作者：Jason
    */
    private void initData(){
        getTeamIdByOrganiseIDByServe();
        getStatus();
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

                    case GROUP:
                        searchDataLists.addAll(mTeamNamelist);
                        break;
                    case STATUS:
                        searchDataLists.addAll(typeList);
                        break;

                    default: {

                    }
                    break;
                }
                LogUtils.e("进入数据更新--->"+condition+"----searchtag--->"+searTag+"---searchDataLists---->"+searchDataLists.size());
                searchtag = searTag;
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
                LogUtils.e("itemName---->"+itemName);
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
                String itemName = mResultAdapter.getItemName();
                final String searchResult = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemName));

                LogUtils.e("searchResult---->"+searchResult+"---searchResult-->"+searchtag);
                if (!searchResult.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {

                                case GROUP: {
                                    TeamId = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("Team_ID"));
                                    et_oracle_id.getmEditText().setText(searchResult);
                                    break;
                                }
                                case STATUS:
                                    equipmentCode = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("DataCode"));
                                    et_equipment_status.getmEditText().setText(searchResult);
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
        clearBtn.setOnClickListener(this);
        searchDataLists = new ArrayList<>();
    }

    private ArrayList<ObjectElement> search(String keyword, String tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        LogUtils.e("进入查询---->"+keyword+"----->"+tagString);
        for (int i = 0; i < mTeamNamelist.size(); i++) {
//            LogUtils.e("进入查询---->"+keyword+"----->"+tagString);
            if (DataUtil.isDataElementNull(mTeamNamelist.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                LogUtils.e("加入查询列表---->");
                reDatas.add(mTeamNamelist.get(i));
            }
        }
        return reDatas;
    }

    private ArrayList<ObjectElement> searchStatus(String keyword, String tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        LogUtils.e("进入查询---->"+keyword+"----->"+tagString);
        for (int i = 0; i < typeList.size(); i++) {
//            LogUtils.e("进入查询---->"+keyword+"----->"+tagString);
            if (DataUtil.isDataElementNull(typeList.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                LogUtils.e("加入查询列表---->");
                reDatas.add(typeList.get(i));
            }
        }
        return reDatas;
    }


    @Override
    public void resolveNfcMessage(Intent intent) {
        try{


        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
//            iccard_id.setText("36113374229071876");
//            searchByServer("36113374229071876");
            if (iccard_id!=null){
                iccard_id.setText(iccardID);
            }else{
                iccard_id = (EditText) findViewById(R.id.iccard_id);
                iccard_id.setText(iccardID);
            }

            searchByServer(true,false);
//            MessageUtils.showToast(iccardID,this);
        }
        }catch (Exception e){
            CrashReport.postCatchedException(e);
        }
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.device_num_action:
                Intent it = new Intent(context, com.king.zxing.CaptureActivity.class);
                it.setAction(Intents.Scan.ACTION);
                it.putExtra(Intents.Scan.CAMERA_ID,0);
                it.putExtra(KEY_IS_CONTINUOUS,false);
                startActivityForResult(it, 1);
                break;
            case R.id.btn_submit:
                submit();
                break;
            case R.id.btn_history:
                Intent intent = new Intent(context,MaintainHistoryActivity.class);
                if (Equipment_ID==null||Equipment_ID.isEmpty()){
                    LogUtils.e("EquipmentID为空");
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("machine_assetnum_isnull"),context);
                    return;
                }
                intent.putExtra("Equipment_ID",Equipment_ID);
                startActivity(intent);
                break;
            case R.id.btn_search:
                if (!TextUtils.isEmpty(et_qr_code.getText().toString().trim())){
                    keyword = et_qr_code.getText().toString();
                }

                if (!TextUtils.isEmpty(iccard_id.getText().toString().trim())){
                    keyword = iccard_id.getText().toString();
                }
                if (!TextUtils.isEmpty(et_use_department.getText().toString().trim())){
                    keyword = et_use_department.getText().toString().trim();
                }
                if (!TextUtils.isEmpty(et_device_num.getText().toString().trim())){
                    keyword = et_device_num.getText().toString().trim();
                }
                if (!TextUtils.isEmpty(et_equipment_name.getText().toString().trim())){
                    keyword = et_equipment_name.getText().toString().trim();
                }
                if (!TextUtils.isEmpty(et_factory_num.getText().toString().trim())){
                    keyword = et_factory_num.getText().toString().trim();
                }
                if (!TextUtils.isEmpty(et_use_department.getText().toString().trim())){
                    keyword = et_use_department.getText().toString().trim();
                }
                if (!TextUtils.isEmpty(keyword)){
                    searchByServer(false,false);
                }else{
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseScanICcardNum"),this);
                }

                break;
            case R.id.btn_right_action:

                finish();
                break;
            case R.id.btn_clearData:
                et_factory_num.setText("");
                et_oracle_id.setText("");
                et_equipment_status.setText("");
                et_equipment_name.setText("");
                et_use_department.setText("");
                et_device_num.setText("");
                iccard_id.setText("");
                et_qr_code.setText("");
                et_equipment_status.setText("");
                tv_subordinate_departments_value.setText("");
                Equipment_ID = "";
                TeamId = "";
                equipmentCode = "";
                clearMap();
                issave = false;
                break;
        }
    }

    /**
     * create by jason
     * @param
     */
    private void getTeamIdByOrganiseIDByServe(){

        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    LogUtils.e("获取数据为空t--->"+t);
                    return;
                }
                mTeamNamelist.clear();
                LogUtils.e("getTeamIdByOrganiseIDByServe--获取成功--->"+t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        mTeamNamelist.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
//                        LogUtils.e("jsonArrayElement查询出来搬车任务部门--->"+jsonArrayElement.asArrayElement().size()+"---Name->"+jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }

                    LogUtils.e("jsonArrayElement查询出来的---->"+jsonArrayElement.asArrayElement().toString());
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("NoOperatorGroup"), context);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                HttpUtils.tips(context,errorNo+"strMsg-->"+strMsg);
                LogUtils.e("getTeamIdByOrganiseIDByServe--获取失败--->"+errorNo+"---->"+strMsg);
//                CrashReport.postCatchedException(throwable);
            }
        };
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        LogUtils.e("执行这里--Factory-->"+getLoginInfo().getFactoryId());
        params.put("Factory",getLoginInfo().getFactoryId());
        HttpUtils.get(this,"BaseOrganise/GetFactoryDepartments",params,callback);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        LogUtils.e("requestCode---->"+requestCode+"---->"+resultCode);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    LogUtils.e("获取到数据---->"+data.getStringExtra("result"));
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (result != null) {
                        //作用:获取到二维码后进行网络请求  Jason 2019/9/26 下午2:06
                        et_qr_code.setText(result);
                        searchByServer(false,true);
                    }
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 通过网络查询网络数据 加载
     * @param
     * @return
     */
    private void searchByServer(boolean isICCard,boolean isQrcode) {
        issave = true;
        //使用网络去查找数据
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        JsonObjectElement task = new JsonObjectElement();
        String ICcardId = iccard_id.getText().toString().trim();
        String oracleId = et_use_department.getText().toString().trim();
        String qrCode = et_qr_code.getText().toString().trim();
        String DeviceNum = et_device_num.getText().toString().trim();
        String equipmentName = et_equipment_name.getText().toString().trim();
        String facotyNum = et_factory_num.getText().toString().trim();
//        if (TextUtils.isEmpty(ICcardId)&&TextUtils.isEmpty(oracleId)&&TextUtils.isEmpty(qrCode)){
//            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseScanICcardNum"),this);
//            dismissCustomDialog();
//            return;
//        }
        if (TextUtils.isEmpty(ICcardId)){
            ICcardId = "";
        }
        if (TextUtils.isEmpty(oracleId)){
            oracleId = "";
        }

        if (TextUtils.isEmpty(qrCode)){
            qrCode = "";
        }

        if (TextUtils.isEmpty(DeviceNum)){
            DeviceNum = "";
        }

        if (isICCard){
            qrCode = "";
            oracleId = "";
            DeviceNum = "";
            equipmentName = "";
            facotyNum = "";
            TeamId = "0";
            equipmentCode = "0";
        }

        if (isQrcode){
            ICcardId = "";
            oracleId = "";
            DeviceNum = "";
            equipmentName = "";
            facotyNum = "";
            TeamId = "0";
            equipmentCode = "0";
        }
        if (TextUtils.isEmpty(TeamId)){
            TeamId = "0";
        }
        task.set("ICCardID", ICcardId);
        task.set("OracleID",oracleId);
        task.set("Equipment_KyID",qrCode);
        task.set("EquipmentName",equipmentName);
        task.set("ProductCode",facotyNum);
        task.set("Organise_ID_Use",Integer.parseInt(TeamId));
        task.set("AssetsID",DeviceNum);

//        params.put("ICCardID", ICcardId);
//        params.put("OracleID",oracleId);
//        params.put("QRCode",qrCode);
        params.putJsonParams(task.toString());
        LogUtils.e("请求的参数---->"+task.toString());
        HttpUtils.post(context, "Equipment/SearchEquipment", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                keyword = "";
                super.onSuccess(t);
                if (TextUtils.isEmpty(t)) {
                    LogUtils.e("请求的数据为空");
                    return;
                }
                LogUtils.e("查询数据获取成功--->" + t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null) {
                    if (jsonArrayElement.asArrayElement().size() > 0) {
                        LogUtils.e("search--jsonArrayElement->" + jsonArrayElement.asArrayElement().size());
                            if (jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.ORACLE_ID) != null &&
                                    !DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.ORACLE_ID)).equals("")) {
                                ObjectElement jsonObjectElement = jsonArrayElement.asArrayElement().get(0).asObjectElement();
                                if (jsonObjectElement.get("Equipment_KyID")!=null&&!jsonObjectElement.get("Equipment_KyID").isNull()){
                                    et_qr_code.setText(jsonObjectElement.get("Equipment_KyID").valueAsString());
                                }
                                if (jsonObjectElement.get("Equipment_ID")!=null&&!jsonObjectElement.get("Equipment_ID").isNull()){
                                    Equipment_ID = jsonObjectElement.get("Equipment_ID").valueAsString();
                                }

                                if (jsonObjectElement.get("AssetsID")!=null&&!jsonObjectElement.get("AssetsID").isNull()){
                                    et_device_num.setText(jsonObjectElement.get("AssetsID").valueAsString());
                                }
                                if (jsonObjectElement.get("OracleID")!=null&&!jsonObjectElement.get("OracleID").isNull()){
                                    et_use_department.setText(jsonObjectElement.get("OracleID").valueAsString());
                                }
                                try {
                                    iccard_id.setText(jsonObjectElement.get("ICCardID").valueAsString());
                                }catch (Exception e){
                                    iccard_id.setText("");
                                }
                                if (jsonObjectElement.get("EquipmentName")!=null&&!jsonObjectElement.get("EquipmentName").isNull()){
                                    et_equipment_name.setText(jsonObjectElement.get("EquipmentName").valueAsString());//设备名称
                                }
                                if (jsonObjectElement.get("Organise_ID_Use")!=null&&!jsonObjectElement.get("Organise_ID_Use").isNull()){
                                    LogUtils.e("Organise_ID_Use--->"+jsonObjectElement.get("Organise_ID_Use").isNull());
                                    try {
                                        String itemkey = jsonObjectElement.get("Organise_ID_Use").valueAsString();
                                        ArrayList<ObjectElement> result = search(itemkey,"Team_ID");
                                        if (result!=null&&result.size()>0){
                                            ObjectElement objectElement = result.get(0);
                                            et_oracle_id.setText(objectElement.get("TeamName").valueAsString());
                                            TeamId = objectElement.get("Team_ID").valueAsString();
                                        }
                                    }catch (Exception e){
                                        et_oracle_id.setText("");
                                    }

                                }

                                if (jsonObjectElement.get("EquipmentState")!=null&&!jsonObjectElement.get("EquipmentState").isNull()){
                                    LogUtils.e("EquipmentState--->"+jsonObjectElement.get("EquipmentState").isNull());
                                    try {
                                        String itemkey = jsonObjectElement.get("EquipmentState").valueAsString();
                                        ArrayList<ObjectElement> result = searchStatus(itemkey,"DataCode");
                                        if (result!=null&&result.size()>0){
                                            ObjectElement objectElement = result.get(0);
                                            et_equipment_status.setText(objectElement.get("DataName").valueAsString());
                                            equipmentCode = objectElement.get("DataCode").valueAsString();
                                            equipment_status = objectElement.get("DataName").valueAsString();
                                        }
                                    }catch (Exception e){
                                        et_equipment_status.setText("");
                                    }

                                }

                                if (jsonObjectElement.get("Organise_ID_Belong")!=null&&!jsonObjectElement.get("Organise_ID_Belong").isNull()){
                                    LogUtils.e("Organise_ID_Belong--->"+jsonObjectElement.get("Organise_ID_Belong").isNull());
                                    try {
                                        String itemkey = jsonObjectElement.get("Organise_ID_Belong").valueAsString();
                                        ArrayList<ObjectElement> result = search(itemkey,"Team_ID");
                                        if (result!=null&&result.size()>0){
                                            ObjectElement objectElement = result.get(0);
                                            tv_subordinate_departments_value.setText(objectElement.get("TeamName").valueAsString());
                                            belong_deparement = objectElement.get("TeamName").valueAsString();
                                        }
                                    }catch (Exception e){
                                        tv_subordinate_departments_value.setText("");
                                    }


                                }
                                if (jsonObjectElement.get("ProductCode")!=null&&!jsonObjectElement.get("ProductCode").isNull()){
                                    et_factory_num.setText(jsonObjectElement.get("ProductCode").valueAsString());//出厂编号
                                }

                                if (!DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.IC_CARD_ID)).isEmpty()
                                        && !DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_KY_ID)).isEmpty()) {
                                    jsonArrayElement.asArrayElement().get(0).asObjectElement().set(Equipment.ORACLE_ID,
                                            DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.ORACLE_ID))
                                                    + LocaleUtils.getI18nValue("IsBinding"));


                                }
                                jsonArrayElement.asArrayElement().get(0).asObjectElement().set(Equipment.EQUIPMENT_ID,
                                        DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_ID)));

                            }

                    }else{
                        et_factory_num.setText("");
                        et_oracle_id.setText("");
                        et_equipment_status.setText("");
                        et_equipment_name.setText("");
                        et_use_department.setText("");
                        et_device_num.setText("");
                        tv_subordinate_departments_value.setText("");
                        iccard_id.setText("");
                        et_qr_code.setText("");
                        Equipment_ID = "";
                        TeamId = "";
                        equipmentCode = "";
                        clearMap();
                        issave = false;
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);

                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                super.onFailure(errorNo, strMsg);
                keyword = "";
                LogUtils.e("获取数据失败---->" + errorNo + "--->" + strMsg);
                issave = false;
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("noData"),context);

            }
        });

    }

    /**
    * 说明：提交事件
    * 添加时间：2019/9/27 下午3:54
    * 作者：Jason
    */
    private void submit(){

        HttpParams params = new HttpParams();
        JsonObjectElement task = new JsonObjectElement();
        if (TextUtils.isEmpty(et_device_num.getText().toString().trim())){
            LogUtils.e("设备编号为空");
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("machine_assetnum_isnull"),context);
            return;
        }
        if (Equipment_ID==null||TextUtils.isEmpty(Equipment_ID)){
            LogUtils.e("主键id为空");
            return;
        }
        task.set("Equipment_ID",Equipment_ID);
        task.set("AssetsID",et_device_num.getText().toString().trim());
        task.set("Equipment_KyID",et_qr_code.getText().toString().trim());
        task.set("OracleID",et_use_department.getText().toString().trim());
        task.set("ICCardID",iccard_id.getText().toString().trim());
        task.set("ProductCode",et_factory_num.getText().toString().trim());

        if (TextUtils.isEmpty(et_equipment_name.getText().toString().trim())){
            LogUtils.e("设备名称为空");
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("machine_equipmentname_isnull"),context);
            return;
        }
        task.set("EquipmentName",et_equipment_name.getText().toString().trim());
        if (TeamId==null||TextUtils.isEmpty(TeamId)){
            LogUtils.e("TeamId使用部门为空");
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("machine_usedepartment_isnull"),context);
            return;
        }

        if (equipmentCode==null||TextUtils.isEmpty(equipmentCode)){
            LogUtils.e("equipmentCode使用部门为空");
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("machine_usedepartment_isnull"),context);
            return;
        }
        task.set("Organise_ID_Use",TeamId);
        task.set("EquipmentState",equipmentCode);
        params.putJsonParams(task.toString());
        LogUtils.e("提交数据---->"+task.toString());
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpUtils.post(context, "Equipment/SimpleUpdate", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                if (t==null||TextUtils.isEmpty(t)){
                    LogUtils.e("获取数据为空");
                    return;
                }
                JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                if(jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()){
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitSuccess"),context);
                    issave = false;
                    keyword = "";
                    et_factory_num.setText("");
                    et_oracle_id.setText("");
                    et_equipment_status.setText("");
                    et_equipment_name.setText("");
                    et_use_department.setText("");
                    et_device_num.setText("");
                    et_qr_code.setText("");
                    iccard_id.setText("");
                    tv_subordinate_departments_value.setText("");
                    Equipment_ID = "";
                    TeamId = "";
                    equipmentCode = "";
                }else {
                    if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()){
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("submit_Fail"), context);
                    }else {
                        TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                    }
                    issave = false;

                }
                LogUtils.e("提交数据成功---->"+t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                dismissCustomDialog();
                LogUtils.e("提交数据失败---->"+errorNo+"----->"+strMsg);
            }
        });
    }

    /**
    * 说明：保存Map
    * 添加时间：2019/10/8 下午2:11
    * 作者：Jason
    */
    private void saveMap(){
        Map<String,String> map = new HashMap<>();
        map.put("iccard",iccard);
        map.put("qrcode",qrcode);
        map.put("oracle_id",oracle_id);
        map.put("device_num",device_num);
        map.put("factory_num",factory_num);
        map.put("equipment",equipment_name);
        map.put("use_equipment",use_equipment);
        map.put("Equipment_ID",Equipment_ID);
        map.put("TeamId",TeamId);
        map.put("equipmentCode",equipmentCode);
        map.put("subordinate_departments",belong_deparement);
        map.put("equipment_status",equipment_status);
        LogUtils.e("开始保存---->"+equipment_name);



        SharedPreferenceManager.putHashMapData(context,"machine_map",map);
    }


    /**
     * 说明：清除Map
     * 添加时间：2019/10/8 下午2:11
     * 作者：Jason
     */
    private void clearMap(){
        Map<String,String> map = new HashMap<>();
        map.put("iccard","");
        map.put("qrcode","");
        map.put("oracle_id","");
        map.put("device_num","");
        map.put("factory_num","");
        map.put("equipment","");
        map.put("use_equipment","");
        map.put("Equipment_ID","");
        map.put("TeamId","");
        map.put("equipmentCode","");
        LogUtils.e("开始保存---->"+equipment_name);
        SharedPreferenceManager.putHashMapData(context,"machine_map",map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iccard = iccard_id.getText().toString().trim();
        qrcode = et_qr_code.getText().toString().trim();
        if (!TextUtils.isEmpty(iccard)||!TextUtils.isEmpty(qrcode)){
            issave = true;
        }
        if (issave) {
            iccard = iccard_id.getText().toString().trim();
            qrcode = et_qr_code.getText().toString().trim();
            oracle_id = et_oracle_id.getText().toString().trim();
            equipment_status = et_equipment_status.getText().toString().trim();
            device_num = et_device_num.getText().toString().trim();
            factory_num = et_factory_num.getText().toString().trim();
            equipment_name = et_equipment_name.getText().toString().trim();
            use_equipment = et_use_department.getText().toString().trim();
            saveMap();
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("machine_dataNotSubmit"), context);
        }else{
            clearMap();
        }
    }

    /**
    * 说明：获取设备状态
    * 添加时间：2019/11/27 下午2:02
    * 作者：Jason
    */
    public void getStatus(){
        LogUtils.e("<------getStatus----->");
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams httpParams=new HttpParams();
        httpParams.put("dataType", "EquipmentState");
        HttpUtils.get(this, "DataDictionary/DataDictionaryList", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{

                        DataElement jsonObjectElement=new JsonArrayElement(t);
                        LogUtils.e("<----获取数据成功---->"+jsonObjectElement.toString());
                        if(jsonObjectElement.isArray() && jsonObjectElement.asArrayElement().size()>0){
                            typeList.clear();
                            final ObjectElement faultData=jsonObjectElement.asArrayElement().get(0).asObjectElement();
                            for(int i=0;i<jsonObjectElement.asArrayElement().size();i++){
                                //翻译DATA_NAME
                                jsonObjectElement.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonObjectElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                                typeList.add(jsonObjectElement.asArrayElement().get(i).asObjectElement());
                            }
                        }
                    }catch (Throwable throwable){
                        CrashReport.postCatchedException(throwable);
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),context);
                dismissCustomDialog();
            }
        });
    }
}
