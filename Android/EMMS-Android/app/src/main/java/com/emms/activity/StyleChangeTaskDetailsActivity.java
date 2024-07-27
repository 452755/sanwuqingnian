package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.emms.schema.Factory;
import com.emms.schema.Task;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.CaptureActivityHandler;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.DecodeHintManager;
import com.king.zxing.InactivityTimer;
import com.king.zxing.Intents;
import com.king.zxing.ViewfinderView;
import com.king.zxing.camera.CameraManager;
import com.smartown.tableview.library.TableView;
import com.zxing.android.CaptureActivity;
import com.zxing.android.MessageIDs;
import com.zxing.android.StyleChangeCaptureActivity;
//import com.zxing.android.view.GoogleCaptureActivity;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by @linkgoo on 2018/10/23.
 *
 */
public class StyleChangeTaskDetailsActivity extends BaseActivity implements View.OnClickListener,SurfaceHolder.Callback{
    private Context mContext ;
    private LinearLayout mContainer;
    private ArrayList<ObjectElement> styleChangedatas=new ArrayList<>();
    private ArrayList<ObjectElement> styleChangeNewDatas=new ArrayList<>();
    private ArrayList<String> modelIdList=new ArrayList<>();
    private HashMap<String,ArrayList> postData = new HashMap<>();
    private HashMap<String,ArrayList> styleChangeMap = new HashMap<>();
    private HashMap<String,ArrayList> styleChangeListMap = new HashMap<>();
    private ResultListAdapter mResultAdapter;
    private TextView menuSearchTitle;
    private ImageView clearBtn;
    private EditText searchBox;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private DrawerLayout mDrawer_layout;
    private String attachment = "";
    private String operationType = "";
    private HashMap<String,String> typeMap = new HashMap<>();
    private String qrCode = "";
    private String OrderNo = "";
    String sewline = "";
    private String DetailInfo = "";
    private String TaskDetail = null;//任务详细
    private String TaskClass=null;//任务类型
    private String taskStatus="";//任务类型
    private AlertDialog AddEquipmentDialog=null;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private PopMenuTaskDetail popMenuTaskDetail;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    CameraManager cameraManager;
    private CaptureActivityHandler handler;

    private InactivityTimer inactivityTimer;
    private ImageView receiver_action;//kingzhang 20210414
    private EditText receiver_num;//kingzhang 20210414
    String receiver = "";//kingzhang 20210414
    String taskId = "";//kingzhang 20210414

    //新扫描的方法
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType,?> decodeHints;
    private String characterSet;
    //新的扫描
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    // private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private Button open;
    private boolean showCompelete = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_change_task_details);
        ((Button)findViewById(R.id.summit)).setText(LocaleUtils.getI18nValue("summit"));
        ((Button)findViewById(R.id.scan)).setText(LocaleUtils.getI18nValue("scan"));
        mContext = this;
        initView();
        initSearchView();

        getStyleChangeProcessingDataFromServer();

        inactivityTimer = new InactivityTimer(this);
    }

    private  void refreshTableView(){
        mContainer.removeAllViews();
        for(String key:styleChangeListMap.keySet()){
            LogUtils.e("key---->"+key);
            ArrayList<StyleChangeInfo> lists = styleChangeListMap.get(key);
            LogUtils.e("lists--->"+lists.size());
            dynamicRenderTable(lists);
        }
    }

    private void initView() {
         //kingzhang add 20210414
        //begin
        ((TextView) findViewById(R.id.tvReceiver)).setText(LocaleUtils.getI18nValue("Receiver"));
        receiver_num = (EditText) findViewById(R.id.receiver_num);
        receiver_num.setHint(LocaleUtils.getI18nValue("scan"));
        receiver_action=(ImageView) findViewById(R.id.receiver_action);
        receiver_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(StyleChangeTaskDetailsActivity.this, com.king.zxing.CaptureActivity.class);
                it.setAction(Intents.Scan.ACTION);
                it.putExtra(Intents.Scan.CAMERA_ID,0);
                it.putExtra(KEY_IS_CONTINUOUS,false);
                startActivityForResult(it, 2);

            }
        });
        //end

        ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("task_details"));
        ImageView menuImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
        menuImageView.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_bar_left).setVisibility(View.VISIBLE);
        menuImageView.setOnClickListener(this);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.summit).setOnClickListener(this);
        findViewById(R.id.scan).setOnClickListener(this);
        mContainer = (LinearLayout) findViewById(R.id.styleChangeTaskDetailContainer);
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        TaskDetail = getIntent().getStringExtra("TaskDetail");
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        taskStatus = getIntent().getStringExtra("FromProcessingFragment").toString();
//        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        LogUtils.e("TaskDetail---->"+TaskDetail+"---->"+TaskClass);
        popMenuTaskDetail = new PopMenuTaskDetail(this, 310, TaskDetail,TaskClass) {
            @Override
            public void onEventDismiss() {

            }
        };
        String mTitle= "[{ \"code\": \""+ PopMenuTaskDetail.STYLE_CHANGE_CANCEL_TASK + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_cancel_task") + "\"},"+
                "{ \"code\": \""+ PopMenuTaskDetail.STYLE_CHANGE_CANCEL_ORDER + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_cancel_the_receipt") + "\"}," +
                "{ \"code\": \""+ PopMenuTaskDetail.STYLE_CHANGE_TASK_COMPLETE + "\", \"name\": \"" + LocaleUtils.getI18nValue("menu_list_group_arrange_complete_task") + "\"}]";
        JsonArrayElement PopMenuTaskDetailArray = new JsonArrayElement(mTitle);
        popMenuTaskDetail.addItems(PopMenuTaskDetailArray);
        popMenuTaskDetail.setOnTaskDetailRefreshListener(new PopMenuTaskDetail.OnTaskDetailRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });


        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);
        if(taskStatus.equals("0")){
            findViewById(R.id.btn_bar_left).setVisibility(View.GONE);
            findViewById(R.id.scan).setVisibility(View.GONE);
            findViewById(R.id.summit).setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
            viewfinderView.setVisibility(View.GONE);
        }
        cameraManager = new CameraManager(getApplication());
        viewfinderView.setCameraManager(cameraManager);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mContainer = (LinearLayout) findViewById(R.id.styleChangeTaskDetailContainer);
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        open = (Button) findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showCompelete){
                    showCompelete = false;
                    open.setText(LocaleUtils.getI18nValue("isScan"));
                    refreshTableView();
                }else{
                    showCompelete = true;
                    open.setText(LocaleUtils.getI18nValue("notScan"));
                    refreshTableView();
                }
            }
        });
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            // CameraManager.get().openDriver(surfaceHolder);
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(StyleChangeTaskDetailsActivity.this,decodeFormats,decodeHints,characterSet,cameraManager);

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    @Override
    protected void onPause() {

        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new com.king.zxing.camera.CameraManager(getApplication());
        cameraManager.setHalfSize(true);
        viewfinderView.setCameraManager(cameraManager);
//        ambientLightManager.start
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
//        inactivityTimer.onResume();


        //这是新扫描的代码
        Intent intent = new Intent();
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.CAMERA_ID, 0);
        intent.putExtra(KEY_IS_CONTINUOUS, false);
        decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
        decodeHints = DecodeHintManager.parseDecodeHints(intent);
        cameraManager.setManualCameraId(0);

        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result obj, Bitmap barcode,float scaleFactor) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        LogUtils.e("获取到扫描数据----->"+obj.toString());
        showResult(obj,barcode);
    }

    protected void showResult(final Result rawResult, Bitmap barcode) {
        inactivityTimer.onActivity();
        if(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID)==null) {
            switch (getLoginInfo().getFromFactory()) {
                case Factory.FACTORY_GEW: {
                    handlerResult(DataUtil.replaceBlank(rawResult.getText().trim()));
                    break;
                }
                case Factory.FACTORY_EGM: {
                    try {
                        LogUtils.e("扫描走这里");
                        //rawResult.getText().toCharArray();
                        handlerResult(new BigInteger(DataUtil.replaceBlank(rawResult.getText().trim()),16).toString().trim());
//						it.putExtra("result", NumberFormatUtil.HexToLongString(rawResult.getText()));
                    } catch (Exception e) {
                        handlerResult(DataUtil.replaceBlank(rawResult.getText().trim()));
                    }
                    break;
                }
                default: {
                    handlerResult(DataUtil.replaceBlank(rawResult.getText().trim()));
                    break;
                }
            }
        }else {
            switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID))){
                case "1":{
                    try {
                        LogUtils.e("扫描走这里");
                        handlerResult(new BigInteger(DataUtil.replaceBlank(rawResult.getText().trim()),16).toString().trim());
                    } catch (Exception e) {
                        handlerResult(DataUtil.replaceBlank(rawResult.getText().trim()));
                    }
                    break;
                }
                case "2":{
                    handlerResult(DataUtil.replaceBlank(rawResult.getText().trim()));
                    break;
                }
                default:{
                    handlerResult(DataUtil.replaceBlank(rawResult.getText().trim()));
                    break;
                }
            }
        }
    }

    //更新tableview
    private void dynamicRenderTable(ArrayList<StyleChangeInfo> lists) {
        LinearLayout mLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_style_change_task_details, null);
        TableView tableView = (TableView)mLinearLayout.findViewById(R.id.table);
        String operationValue = lists.get(0).getOperationName();
        LogUtils.e("operationValue---->"+operationValue);
        ((TextView) mLinearLayout.findViewById(R.id.tv_orderno)).setVisibility(View.GONE);
        ((TextView) mLinearLayout.findViewById(R.id.tv_orderno_value)).setVisibility(View.GONE);
        ((TextView) mLinearLayout.findViewById(R.id.tv_jo_closedate)).setVisibility(View.GONE);
        ((TextView) mLinearLayout.findViewById(R.id.tv_closedate)).setVisibility(View.GONE);
        ((TextView) mLinearLayout.findViewById(R.id.tv_attcement_label)).setText(LocaleUtils.getI18nValue("attachment_name"));
        ((TextView) mLinearLayout.findViewById(R.id.tv_attcement_name)).setText(lists.get(0).getDes());
        ((TextView) mLinearLayout.findViewById(R.id.tv_label)).setText(LocaleUtils.getI18nValue("operation"));
        ((TextView) mLinearLayout.findViewById(R.id.tv_value)).setText(LocaleUtils.getI18nValue(operationValue));
        ((TextView) mLinearLayout.findViewById(R.id.tv_type_label)).setText(LocaleUtils.getI18nValue("operation_type"));
        ((TextView) mLinearLayout.findViewById(R.id.tv_type_value)).setText(LocaleUtils.getI18nValue(lists.get(0).getOperationType()));
        tableView.clearTableContents()
                .setHeader(LocaleUtils.getI18nValue("attachment_id"),LocaleUtils.getI18nValue("location"),
                        LocaleUtils.getI18nValue("plan_take_qty"),LocaleUtils.getI18nValue("sacn_qty"));
        for(int i =0;i<lists.size();i++){
            StyleChangeInfo styleChangeInfo = lists.get(i);
            LogUtils.e("styleChangeInfo.getRequestqty()----->"+styleChangeInfo.getRequestqty()
                    +"---stylechangeScan--->"+styleChangeInfo.getScanQty()
                    +"id---->"+styleChangeInfo.getAttachmentId());
                if (styleChangeInfo.getRequestqty()==styleChangeInfo.getScanQty()){
                    if (showCompelete){
                        tableView.addContent(styleChangeInfo.getAttachmentId(), styleChangeInfo.getLocation(),styleChangeInfo.getRequestqty()+"("+styleChangeInfo.getIssueQty()+ "/"
                                + styleChangeInfo.getReceiptQty() +")",styleChangeInfo.getScanQty());
                    }
                }else{
                    tableView.addContent(styleChangeInfo.getAttachmentId(), styleChangeInfo.getLocation(),styleChangeInfo.getRequestqty()+"("+styleChangeInfo.getIssueQty()+ "/"
                            + styleChangeInfo.getReceiptQty() +")",styleChangeInfo.getScanQty());
                }
        }
        tableView.refreshTable();

        //begin 2021-12-03 Abrahamguo 重新定义是否展示表格
        //定义是否已扫描完成集合
        ArrayList<Boolean> isScanCompletes = new ArrayList<Boolean>();
        //遍历StyleChangeInfo集合
        for(int i =0;i<lists.size();i++){
            //定义StyleChangeInfo对象为集合对应索引的值
            StyleChangeInfo styleChangeInfo = lists.get(i);
            //判断StyleChangeInfo已经扫描完成
            if (Integer.parseInt(styleChangeInfo.getRequestqty())==Integer.parseInt(styleChangeInfo.getScanQty())) {
                //扫描完成集合内添加true
                isScanCompletes.add(true);
            }
            //判断StyleChangeInfo未扫描完成
            else if(Integer.parseInt(styleChangeInfo.getRequestqty())!=Integer.parseInt(styleChangeInfo.getScanQty())){
                //扫描完成集合添加false
                isScanCompletes.add(false);
            }
        }
        //判断展示未扫描完成视图
        if(!showCompelete){
            //如果扫描完成集合中不存在未扫描完成
            if(!isScanCompletes.contains(false)){
                //表格不展示
                mLinearLayout.setVisibility(View.GONE);
            }
        }
        //如果展示已扫描完成视图
        else{
            //如果扫描完成集合中存在未扫描完成
            if(isScanCompletes.contains(false)){
                //表格不展示
                mLinearLayout.setVisibility(View.GONE);
            }
        }
        //end
        //注释原逻辑
//        if (lists.size()==1){
//            StyleChangeInfo styleChangeInfo = lists.get(0);
//            if (!showCompelete){
//                if (Integer.parseInt(styleChangeInfo.getRequestqty())==Integer.parseInt(styleChangeInfo.getScanQty())) {
//                    LogUtils.e("扫描数等于总数");
//                    mLinearLayout.setVisibility(View.GONE);
//                }
//            }else{
//                if (Integer.parseInt(styleChangeInfo.getRequestqty())!=Integer.parseInt(styleChangeInfo.getScanQty())) {
//                    LogUtils.e("扫描数等于总数");
//                    mLinearLayout.setVisibility(View.GONE);
//                }
//            }
//        }
        mContainer.addView(mLinearLayout);
    }

    private void postStyleChangeData(){
        //10表示，这个订单的开始
        if(modelIdList.size() == 0){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("summitDataIsEmpty"),mContext);
            return;
        }
        patchStyleChangeOrderStatus("10");
    }

    //扫描时，scan数量增加1
    private void refreshScanQty(String scanValue,String operationTypeStr,String type){
        ArrayList<StyleChangeInfo> styleChangeInfos = styleChangeListMap.get(operationTypeStr);
        if(styleChangeInfos == null){
            return;
        }

        //mark update 20210421
        String receiver = receiver_num.getText().toString();
//        if(StringUtils.isBlank(receiver)) {
//            ToastUtil.showToastShort(LocaleUtils.getI18nValue("receiverCanNotBeEmpty"),mContext);
//            return;
//        }
        for(int i=0;i<styleChangeInfos.size();i++){
            StyleChangeInfo styleChangeInfo = styleChangeInfos.get(i);
            if(scanValue.equals(styleChangeInfo.getAttachmentId())){
                int scanQty = Integer.valueOf(styleChangeInfo.getScanQty());
                //作用:针对CF开头的操作，可以扫描增加  Jason 2020/3/13 下午2:24
                int Qty = Integer.parseInt(styleChangeInfo.getRequestqty());
                int issueQty = Integer.parseInt(styleChangeInfo.getIssueQty());
                int count = scanQty+issueQty;
                if (qrCode.startsWith("CF")){
                    if (Qty==count){
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("QuantityGreater"),mContext);
                        return;
                    }
                }

                List<String> qrCodeList = styleChangeInfo.getQrCodeList();
                if(type.equals("+")){
                    scanQty+=1;
                    qrCodeList.add(qrCode);
                }else if(type.equals("-")){
                    scanQty-=1;
                    qrCodeList.remove(qrCode);
                }
                styleChangeInfo.setQrCodeList(qrCodeList);
                styleChangeInfo.setScanQty(String.valueOf(scanQty));

                //mark update 20210421
                styleChangeInfo.setReceiver(receiver);
                break;
            }
        }
    }

    //扫描到二维码，返回页面时的处理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2://kingzhang add 20210414
                if (data != null) {
                    LogUtils.e("获取到数据---->"+data.getStringExtra("result"));
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (result != null) {
                        //ToastUtil.showToastLong(result,mContext);
                        //getDataByICcardID(result, true);
                        String[] all=result.split(";");
                        if(all.length>1)
                          receiver_num.setText(all[0]);
                    }
                }
                break;
            case Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY:{
                if(resultCode==Constants.RESULT_CODE_CAPTURE_ACTIVITY_TO_TASK_DETAIL){
                    if (data != null)
                    {
                        final String result = data.getStringExtra("result");
                        if (result != null){
                            ArrayList<StyleChangeInfo> typeList = new ArrayList<>();
                            for (String key : styleChangeMap.keySet()){
                                if(result.toUpperCase().indexOf(key.toUpperCase()) != -1){
                                    typeList = styleChangeMap.get(key.toUpperCase());
                                    attachment = key;
                                    break;
                                }
                            }
                            qrCode = result;
                            if(modelIdList.contains(qrCode)){

                                final String DialogMessage=LocaleUtils.getI18nValue("AreYouSureToCancelQRCode")
                                        +"\n"+LocaleUtils.getI18nValue("qr_code")+qrCode;
                                LogUtils.e("显示对话框--->"+DialogMessage);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            builder.setMessage(DialogMessage);
                                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            refreshScanQty(attachment,operationType,"-");
                                                            modelIdList.remove(qrCode);
                                                            typeMap.remove(qrCode);
                                                            refreshTableView();
                                                        }
                                                    });
                                                }
                                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            AddEquipmentDialog=builder.create();
                                            AddEquipmentDialog.show();
                                        }
                                    }
                                });
                                return;
                            }

                            if (typeList.size() == 0){
                                //扫描的物件不存在于待申领页面中
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("scan_no_exists_attachment"),mContext);
                            }else if(typeList.size() ==1){
                                operationType = typeList.get(0).getOperationType();
                                refreshScanQty(attachment,operationType,"+");
                                modelIdList.add(result);
                                typeMap.put(qrCode,operationType);
                                refreshTableView();
                            }else{
                                searchDataLists.clear();
                                for(int i = 0;i<typeList.size() ;i++){
                                    JsonObjectElement obj = new JsonObjectElement();
                                    obj.set("operation",typeList.get(i).getOperationName().replace("\"",""));
                                    obj.set("type",typeList.get(i).getOperationType().replace("\"",""));
                                    searchDataLists.add(obj.asObjectElement());
                                }
                                dropSearchViewOnClickListener(searchDataLists,LocaleUtils.getI18nValue("title_search_operation"));
                                mResultAdapter.changeData(searchDataLists,"type");
                                mDrawer_layout.openDrawer(Gravity.RIGHT);
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    private void patchStyleChangeOrderStatus(String status){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        //这里是调用，urlStr是对应的接口，请求过去后直接报了-1错误
        //kingzhang update 20210415
        String receiver = receiver_num.getText().toString();
        String url="1.1/emms/order/"+OrderNo+"/"+sewline+"/"+status;
        if(!(receiver.equals("")))
        {
            url="1.1/emms/order/"+OrderNo+"/"+sewline+"/"+status+"/"+taskId+"/"+receiver;
        }
        HttpUtils.patchChangeStyle(mContext, url, params, new HttpCallback() {
        //HttpUtils.patchChangeStyle(mContext, "1.1/emms/order/"+OrderNo+"/"+sewline+"/"+status, params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                dismissCustomDialog();
                LogUtils.e("请求辅件出仓成功---->"+t);
                //确认可以开始这个当的出仓，弹出提示框，确认是否需要出仓
                final String DialogMessage=LocaleUtils.getI18nValue("AreYouSureToSummit");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(DialogMessage);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            postStyleChangeOrderStatus();
                                        }
                                    });
                                }
                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AddEquipmentDialog=builder.create();
                            AddEquipmentDialog.show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("请求辅件出仓失败---->"+errorNo+"---->"+strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailPatchOrderStatus+errorNo"),mContext);
                dismissCustomDialog();
            }
        });
    }

    private void postStyleChangeOrderStatus(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        List<JSONObject> objList = new ArrayList<>();
        for (String key:styleChangeListMap.keySet()){
            List<StyleChangeInfo> infoList = styleChangeListMap.get(key);
            for (StyleChangeInfo item:infoList){
                if (item.getQrCodeList().size() == 0){
                    continue;
                }
                JSONObject obj = new JSONObject();
                try {
                    obj.put("operationid",key);
                    obj.put("attachmentid",item.getAttachmentId());
                    obj.put("attachmentType",item.getAttachmentType());
                    obj.put("ucc",new JSONArray(item.getQrCodeList().toString()));

                    //mark update 20210421
                    obj.put("receiver", item.getReceiver());
                    objList.add(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        final int updateSize = objList.size();
        JsonArrayElement jsonArrayElement=new JsonArrayElement(objList.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        LogUtils.e("出仓上传的数据---->"+jsonArrayElement.toJson());
        //接口确认直接使用严格模式出仓数据
        HttpUtils.postChangeStyle(mContext, "1.1/emms/vms/issue/"+OrderNo+"/"+sewline+"/"+"/1", params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                if(!Strings.isNullOrEmpty(t)){
                    LogUtils.e("调用出仓接口成功---->"+t);

                    JsonObjectElement obj=new JsonObjectElement(t);
                    ObjectElement objEle =obj.getAsObjectElement("data");
                    if (obj.get("code") !=null && obj.get("code").toString().replace("\"","").equals("200")) {
                        if(objEle != null){
                            //obj.get("data").asObjectElement().get("success").toString().replace("\"","").equals("1")
                            int success = obj.get("data").asObjectElement().get("success").valueAsInt();
                            if(updateSize == success){
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("issueSuccess"),mContext);
                                modelIdList.clear();
                                getStyleChangeProcessingDataFromServer();
                            }else{
                                final String DialogMessage=  obj.get("data").asObjectElement().get("fail").toString();
                                try {
                                    final ArrayElement failStr = obj.get("data").asObjectElement().get("issueList").asArrayElement();


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                builder.setMessage(DialogMessage);
                                                builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                deleteQrCode(failStr);
                                                            }
                                                        });
                                                    }
                                                });
                                                AddEquipmentDialog=builder.create();
                                                AddEquipmentDialog.show();
                                            }
                                        }
                                    });
                                }catch (Exception e){
                                    ToastUtil.showToastLong( LocaleUtils.getI18nValue("FailPostOrderStatus+errorNo"),mContext);
                                }
                            }
                        }
                    }else{
                        final String DialogMessage= LocaleUtils.getI18nValue("FailPostOrderStatus+errorNo");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setMessage(DialogMessage);
                                    builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    modelIdList.clear();
                                                    getStyleChangeProcessingDataFromServer();
                                                }
                                            });
                                        }
                                    });
                                    AddEquipmentDialog=builder.create();
                                    AddEquipmentDialog.show();
                                }
                            }
                        });
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("调用出仓接口失败--0-->"+errorNo+"--->"+strMsg);
                final String DialogMessage= LocaleUtils.getI18nValue("FailPostOrderStatus+errorNo");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(DialogMessage);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    });
                                }
                            });
                            AddEquipmentDialog=builder.create();
                            AddEquipmentDialog.show();
                        }
                    }
                });
                dismissCustomDialog();
            }
        });
    }

    private void deleteQrCode(ArrayElement failList){
        for (int i= 0;i<failList.size();i++){
            ObjectElement data = failList.get(i).asObjectElement();
            ArrayElement uccList =  data.get("ucc").asArrayElement();
            for(int j = 0;j<uccList.size();j++){
                String qrCodeStr = uccList.get(j).toString().replace("\"","");
                String scanValue;
                if (qrCodeStr.startsWith("CF")){
                    LogUtils.e("二维码开头是CF的---->"+qrCodeStr);
                    scanValue = qrCodeStr;
                }else{
                    scanValue = qrCodeStr.substring(0,qrCodeStr.length()-6);
                }
                String type = typeMap.get(qrCodeStr);
                refreshScanQty(scanValue,type,"-");
                typeMap.remove(qrCodeStr);
                modelIdList.remove(qrCodeStr);
            }
        }
        refreshTableView();
    }

    //选择任务类型上面的搜索功能
    private void dropSearchViewOnClickListener(final ArrayList<ObjectElement> searchDataLists, final String searchTitle){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (searchDataLists.size() > 0) {
                    mResultAdapter.changeData(searchDataLists, "type");
                    menuSearchTitle.setText(searchTitle);
                    modelIdList.add(qrCode);
                    mDrawer_layout.openDrawer(Gravity.RIGHT);
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
        ((TextView)emptyView.findViewById(R.id.tvNothingFound)).setText(LocaleUtils.getI18nValue("nothing_found"));
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
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                final String searchResult = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));

                if (!searchResult.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchBox.setText("");
                            for(int i = 0;i<searchDataLists.size();i++){
                                ObjectElement obj = searchDataLists.get(i);
                                if(searchResult.equals(obj.get("type").toString().replace("\"",""))){
                                    operationType = obj.get("type").toString().replace("\"","");
                                }
                            }
                            refreshScanQty(attachment,operationType,"+");
                            typeMap.put(qrCode,operationType);
                            refreshTableView();
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"),mContext);
                }
            }
        });
        clearBtn.setOnClickListener(this);
        searchDataLists = new ArrayList<>();
    }

    private void ScanQRCode(){
        ((Activity)mContext).startActivityForResult(new Intent(mContext, StyleChangeCaptureActivity.class), Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY);
//        ((Activity)mContext).startActivityForResult(new Intent(mContext, GoogleCaptureActivity.class),Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY);
    }

    private ArrayList<ObjectElement> search(String keyword,String  tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    //进入页面，获取的数据来源
    private void getStyleChangeProcessingDataFromServer(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        sewline =  getIntent().getStringExtra("SewingLine");
        OrderNo = getIntent().getStringExtra("OrderNo").split("/")[0];
        //kingzhang add 20210415
        //receiver =  getIntent().getStringExtra("Receiver");
        //receiver_num.setText(receiver);
        taskId =  getIntent().getStringExtra("Task_ID");
        String status = "1";//调用接口时是2，现阶段只是为了测试数据
//        String paraStr = "%5B%7B%22_and%22%3A%5B%7B%22state%22%3A%7B%22gte%22%3A"+status+"%7D%7D%2C%7B%22orderno%22%3A%7B%22eq%22%3A%22"+OrderNo+"%22%7D%7D%2C%7B%22sewingline%22%3A%7B%22eq%22%3A%22"+sewline+"%22%7D%7D%5D%7D%5D";
//        String paraStr = "%5b%7b%22_and%22%3a%5b%7b%22orderno%22%3a%7b%22eq%22%3a%22"+OrderNo+"%22%7d%7d%5d%7d%5d";
        String paraStr = "%5B%7B%22_and%22%3A%20%5B%7B%22orderno%22%3A%20%7B%22eq%22%3A%20%22"+OrderNo+"%22%7D%7D%2C%20%7B%22sewingline%22%3A%20%7B%22eq%22%3A%20%22"+sewline+"%22%7D%7D%5D%7D%5D";
//kingzhang update 20210416 order>task
        LogUtils.e("getStyleChangeProcessingDataFromServer--获取数据后台--->"+"1.1/classes/task/"+taskId+"?q=&offset=1&order=-id&where="+paraStr);
        HttpUtils.getChangeStyle(mContext, "1.1/classes/task/"+taskId+"?q=&offset=1&order=-id&where="+paraStr, params, new HttpCallback() {
            //HttpUtils.getChangeStyle(mContext, "1.1/classes/order?q=&offset=1&order=-id&where="+paraStr, params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                if(!Strings.isNullOrEmpty(t)){
                    LogUtils.e("getStyleChangeProcessingDataFromServer--获取数据后台--->"+t);
                    JsonObjectElement obj=new JsonObjectElement(t);
                    ObjectElement objEle =obj.getAsObjectElement("data");
                    receiver_num.setText(objEle.get("receiver").toString().replace("\"",""));
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("items").asArrayElement() != null) {
                        ArrayElement items = objEle.get("items").asArrayElement();
                        if(items.size() == 1){
                            styleChangeNewDatas.clear();
                            styleChangeListMap = new HashMap<String, ArrayList>();
                            styleChangeMap = new HashMap<String, ArrayList>();
                            ObjectElement assignmentObj = items.get(0).asObjectElement();
                            String statusStr = assignmentObj.get("state").toString();

                            ArrayElement assignmentItems = assignmentObj.get("assignments").asArrayElement();
                            if(assignmentItems==null){
                                dismissCustomDialog();
                                return;
                            }
                            for(int i = 0;i<assignmentItems.size();i++){
                                ObjectElement assigmentObj = assignmentItems.get(i).asObjectElement();
                                ObjectElement operationObj = assigmentObj.get("operation").asObjectElement();
                                String operationType = operationObj.get("type").toString().replace("\"","");
                                ObjectElement operationNameObj = operationObj.get("name").asObjectElement();
                                String languageStr = LocaleUtils.getLanguage(mContext).toUpperCase().equals("ZH-CN")?"zh_cn":"en_us";
                                String operationName = operationNameObj.get(languageStr).toString().replace("\"","");
                                ArrayList<StyleChangeInfo> styleChangeInfos = styleChangeListMap.get(operationType.toUpperCase());

                                ArrayElement productsArray = assigmentObj.get("products").asArrayElement();
                                StringBuilder AttchementNames = new StringBuilder();
                                String attchementName = "";
                                for(int j = 0;j<productsArray.size();j++){
                                    ObjectElement productObj = productsArray.get(j).asObjectElement();
                                    String attachmentId = productObj.get("attachmentid").toString().replace("\"","");
                                    ObjectElement descs = productObj.get("name").asObjectElement();
                                    String language = LocaleUtils.getLanguage(mContext).toUpperCase().equals("ZH-CN")?"zh_cn":"en_us";
                                    String des = descs.get(language).toString().replace("\"","");
                                    if (!attchementName.equals(des)){
                                        attchementName = des;
                                        AttchementNames.append(attchementName);
                                        if (j!=productsArray.size()-1){
                                            AttchementNames.append("/");
                                        }
                                    }

                                    LogUtils.e("获取到新增的数据---->"+des);
                                    //将attachmentid 和operation匹配起来
                                    ArrayList<StyleChangeInfo> typeList = styleChangeMap.get(attachmentId.toUpperCase());
                                    StyleChangeInfo info = new StyleChangeInfo();
                                    info.setOperationType(operationType);
                                    info.setOperationName(operationName);
                                    if(typeList==null){
                                        typeList = new ArrayList<StyleChangeInfo>();

                                        typeList.add(info);
                                        styleChangeMap.put(attachmentId.toUpperCase(),typeList);
                                    }else{
                                        typeList.add(info);
                                    }

                                    //将数据存入map中。
                                    StyleChangeInfo styleChangeInfo = new StyleChangeInfo();
                                    styleChangeInfo.setAttachmentId(attachmentId);
                                    styleChangeInfo.setDes(AttchementNames.toString());//新增一个描述
                                    String AttachmentType = productObj.get("attachmenttype")==null?"":productObj.get("attachmenttype").toString().replace("\"","");
                                    styleChangeInfo.setAttachmentType(AttachmentType);
                                    String Requestqty = productObj.get("requestqty")==null?"":productObj.get("requestqty").toString().replace("\"","");
                                    styleChangeInfo.setRequestqty(Requestqty);
                                    String Status = productObj.get("status")==null?"":productObj.get("status").toString().replace("\"","");
                                    styleChangeInfo.setStatus(Status);
                                    String Version = productObj.get("version")==null?"0":productObj.get("version").toString().replace("\"","");
                                    styleChangeInfo.setVersion(Version);
                                    String issueQty = productObj.get("issueqty")==null?"0":productObj.get("issueqty").toString().replace("\"","");
                                    String receiptQty = productObj.get("receiptqty")==null?"0":productObj.get("receiptqty").toString().replace("\"","");
                                    String location = productObj.get("location")==null?"":productObj.get("location").toString().replace("\"","");
                                    styleChangeInfo.setLocation(location);
                                    styleChangeInfo.setIssueQty(issueQty);
                                    styleChangeInfo.setReceiptQty(receiptQty);
                                    styleChangeInfo.setOperationName(operationName);
                                    styleChangeInfo.setOperationType(operationType);
                                    styleChangeInfo.setScanQty("0");
                                    if(styleChangeInfos == null){
                                        styleChangeInfos = new ArrayList<StyleChangeInfo>();
                                        styleChangeInfos.add(styleChangeInfo);
                                        styleChangeListMap.put(operationType.toUpperCase(),styleChangeInfos);
                                    }else{
                                        styleChangeInfos.add(styleChangeInfo);
                                    }
                                }
                                styleChangedatas.add(assignmentItems.get(i).asObjectElement());
                            }
                        }else{
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("GetStyleChangeTaskDetailDataIsEmpty"),mContext);
                        }
                    }
                }
                refreshTableView();
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("getStyleChangeProcessingDataFromServer--获取后台数据失败--->"+errorNo+"---->"+strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetStyleChangeTaskDetailCauseByTimeOut+errorNo"),mContext);
                dismissCustomDialog();
            }
        });
    }

    /**
     * 声音与振动
     */
    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            try {
                AssetFileDescriptor fileDescriptor = getAssets().openFd("qrbeep.ogg");
                this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException e) {
                this.mediaPlayer = null;
            }
        }
    }

    private ArrayList<String> changeToArrayList(JsonArray jsonObject){
        ArrayList<String> listdata = new ArrayList<String>();
        if (jsonObject != null) {
            for (int i=0;i<jsonObject.size();i++){
                listdata.add(jsonObject.get(i).toString());
            }
        }
        return listdata;
    }

    @Override
    public void onClick(View v) {
        int click_id = v.getId();
        switch (click_id){
            case R.id.btn_right_action:
                finish();
                break;
            case R.id.summit:
                postStyleChangeData();
                break;
            case R.id.scan:
                ScanQRCode();
                break;
            case R.id.btn_bar_left_action:
                popMenuTaskDetail.setOrderNo(OrderNo);
                popMenuTaskDetail.showAsDropDown(v);
                break;
        }
    }

    String RepeatCode="";
    boolean isRepeat = false;

    /**
     * 处理结果的方法
     */
    private void handlerResult(String result) {
        LogUtils.e("扫描结果---->"+result);
        if (result != null&&!TextUtils.isEmpty(result.trim())){

            if (RepeatCode.equals(result.trim())){
                LogUtils.e("扫描重复了");
                isRepeat = true;
//                handler.sendEmptyMessageDelayed(MessageIDs.restart_preview,3000);
            }else{
                isRepeat = false;
            }

            RepeatCode = result.trim();
            String scanValue;
            if (result.startsWith("CF")){
                LogUtils.e("二维码开头是CF的---->"+result);
                scanValue = result;
            }else{
                scanValue = result.substring(0,result.length()-6);
            }

            ArrayList<StyleChangeInfo> typeList = new ArrayList<>();
            for (String key : styleChangeMap.keySet()){
                LogUtils.e("循环-->"+key+"--->"+scanValue.toUpperCase()+"--->"+scanValue.toUpperCase().indexOf(key.toUpperCase()));
                LogUtils.e("循环-->"+key+"--->"+result.toUpperCase()+"--->"+result.toUpperCase().indexOf(key.toUpperCase()));
                if(scanValue.toUpperCase().indexOf(key.toUpperCase()) != -1){
                    LogUtils.e("判断--->"+key.toUpperCase());
                    typeList = styleChangeMap.get(key.toUpperCase());
                    attachment = key;
                    //这一句是重新扫描
                    break;
                }
            }
            qrCode = result;
            if (!qrCode.startsWith("CF")){
            if(modelIdList.contains(qrCode)){
                final String DialogMessage=LocaleUtils.getI18nValue("AreYouSureToCancelQRCode")
                        +"\n"+LocaleUtils.getI18nValue("qr_code")+qrCode;
                LogUtils.e("显示对话框---->"+DialogMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(DialogMessage);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshScanQty(attachment,operationType,"-");
                                            modelIdList.remove(qrCode);
                                            typeMap.remove(qrCode);
                                            refreshTableView();
                                            //这一句是重新扫描
                                            if (isRepeat){
                                                handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
                                            }else{
                                                handler.sendEmptyMessage(R.id.restart_preview);
                                            }
                                        }
                                    });
                                }
                            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //这一句是重新扫描
                                    if (isRepeat){
                                        handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
                                    }else{
                                        handler.sendEmptyMessage(R.id.restart_preview);
                                    }
                                }
                            });
                            AddEquipmentDialog=builder.create();
                            AddEquipmentDialog.show();
                        }
                    }
                });
                return;
            } }
            LogUtils.e("typeList---->"+typeList.size());
            if (typeList.size() == 0){
                //扫描的物件不存在于待申领页面中
                LogUtils.e("进入这里---->"+LocaleUtils.getI18nValue("scan_no_exists_attachment"));
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("scan_no_exists_attachment"),mContext);
                //这一句是重新扫描
                handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);

            }else if(typeList.size() ==1){
                operationType = typeList.get(0).getOperationType();
                refreshScanQty(attachment,operationType,"+");
                modelIdList.add(result);
                typeMap.put(qrCode,operationType);
                refreshTableView();
                //这一句是重新扫描
                handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
            }else{
                searchDataLists.clear();
                for(int i = 0;i<typeList.size() ;i++){
                    JsonObjectElement obj = new JsonObjectElement();
                    obj.set("operation",typeList.get(i).getOperationName().replace("\"",""));
                    obj.set("type",typeList.get(i).getOperationType().replace("\"",""));
                    searchDataLists.add(obj.asObjectElement());
                }
                dropSearchViewOnClickListener(searchDataLists,LocaleUtils.getI18nValue("title_search_operation"));
                mResultAdapter.changeData(searchDataLists,"type");
                mDrawer_layout.openDrawer(Gravity.RIGHT);
                //这一句是重新扫描
                handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
            }
        }else{
            //这一句是重新扫描
            if (isRepeat){
                handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
            }else{
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        }

    }

}
