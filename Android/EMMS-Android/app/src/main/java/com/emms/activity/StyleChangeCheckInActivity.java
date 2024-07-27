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
import android.widget.Toast;

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
import com.emms.schema.Factory;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.schema.Team;
import com.emms.ui.DropEditText;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.RootUtil;
import com.emms.callback.CallBack;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.AmbientLightManager;
import com.king.zxing.CaptureActivity;
import com.king.zxing.CaptureActivityHandler;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.DecodeHintManager;
import com.king.zxing.Intents;
import com.king.zxing.ViewfinderView;
import com.smartown.tableview.library.TableView;
import com.zxing.android.MessageIDs;
import com.zxing.android.StyleChangeCaptureActivity;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.InactivityTimer;
//import com.zxing.android.view.GoogleCaptureActivity;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.String;

import static com.emms.R.id.equipment_name;

/**
 * Created by @linkgoo on 2018/10/23.
 *
 */
public class StyleChangeCheckInActivity extends BaseActivity implements View.OnClickListener,SurfaceHolder.Callback {
    private Context mContext ;
    private LinearLayout mContainer;
    private ArrayList<ObjectElement> styleChangedatas=new ArrayList<>();
    private ArrayList<ObjectElement> styleChangeNewDatas=new ArrayList<>();
    private ArrayList<String> modelIdList=new ArrayList<>();
    private HashMap<String,ArrayList> postData = new HashMap<>();
    private HashMap<String,ArrayList> styleChangeMap = new HashMap<>();
    private HashMap<String,String> typeMap = new HashMap<>();
    private HashMap<String,ArrayList> styleChangeListMap = new HashMap<>();
    private DrawerLayout mDrawer_layout;
    private String attachment = "";
    private String operationType = "";
    private String messageStr = "";
    private String qrCode = "";
    private String OrderNo = "";
    private String DetailInfo = "";
    private String TaskClass="T08";//任务类型
    private AlertDialog AddEquipmentDialog=null;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private PopMenuTaskDetail popMenuTaskDetail;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    private boolean isNeedSewingLine = false;
    private String factory;
    private String sewingLine;
    CameraManager cameraManager;

    private InactivityTimer inactivityTimer;
    AmbientLightManager ambientLightManager;

    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType,?> decodeHints;
    private String characterSet;

    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    /**
     * 是否支持缩放（变焦），默认支持
     */
    private boolean isZoom = true;
    private float oldDistance;

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    // private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    /*
    * Abrahamguo 20211130 新增搜索视图所需字段
    * */
    private TextView menuSearchTitle;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private EditText searchBox;


    Button btn_itemtracking;
    Button open;

    public StyleChangeCheckInActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_change_task_details);
        String summitStr = "";
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        if(TaskClass.equals(Task.TRANSFER_CHECK_IN)){
            summitStr = LocaleUtils.getI18nValue("summit_check_in");
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("style_change_check_in"));
            messageStr = "AreYouSureToCheckIn";
        }else if(TaskClass.equals(Task.TRANSFER_REJECT)){
            summitStr = LocaleUtils.getI18nValue("summit_reject");
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("style_change_reject"));
            messageStr = "AreYouSureToReject";
        }
        ((Button)findViewById(R.id.summit)).setText(summitStr);
        //修改原来的扫描按钮为 辅件追踪
        btn_itemtracking = ((Button)findViewById(R.id.scan));
        btn_itemtracking.setText(LocaleUtils.getI18nValue("itemtracking"));
        btn_itemtracking.setVisibility(View.VISIBLE);
        open = (Button) findViewById(R.id.open);
        open.setVisibility(View.GONE);
        mContext = this;
        initView();
        inactivityTimer = new InactivityTimer(this);
        //新的扫描设置
//        ambientLightManager = new AmbientLightManager(this);
        //获取当前所在工厂
        this.factory = SharedPreferenceManager.getFactory(mContext);
        //获取骑缝线
        getmSewingDataFromAPI("%5B%7B%22_and%22%3A%5B%7B%22state%22%3A%7B%22eq%22%3A2%7D%7D%2C%7B%22factory%22%3A%7B%22eq%22%3A%22" + this.factory + "%22%7D%7D%5D%7D%5D");
    }

    /**
     * 新扫描的东西
     */
    private void resetStatusView() {
//        resultView.setVisibility(View.GONE);
//        statusView.setText(R.string.msg_default_status);
//        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
//        lastResult = null;
    }

    @Override
    public void onResume() {
//        super.onResume();
        super.onResume();
        cameraManager = new CameraManager(getApplication());
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
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    private  void refreshTableView(){
        mContainer.removeAllViews();
        for(String key:styleChangeListMap.keySet()){
            ArrayList<StyleChangeInfo> lists = styleChangeListMap.get(key);
            dynamicRenderTable(lists,key);
        }
    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.summit).setOnClickListener(this);
        findViewById(R.id.scan).setOnClickListener(this);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);
        hasSurface = false;
        mContainer = (LinearLayout) findViewById(R.id.styleChangeTaskDetailContainer);
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
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
            LogUtils.e("开启相机");
            // CameraManager.get().openDriver(surfaceHolder);
            cameraManager.openDriver(surfaceHolder);
//            cameraManager.startPreview();
        } catch (IOException ioe) {
            LogUtils.e("开启相机出错了---->"+ioe.getMessage().toString());
            return;
        } catch (RuntimeException e) {
            LogUtils.e("开启相机出错了---->"+e.toString());
            return;
        }
        if (handler == null) {
            LogUtils.e("进入handler");
            handler = new CaptureActivityHandler(StyleChangeCheckInActivity.this, decodeFormats, decodeHints,characterSet,cameraManager);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.e("surfaceChanged---->"+hasSurface);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.e("surfaceCreated---->"+hasSurface);
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.e("surfaceDestroyed---->"+hasSurface);
        hasSurface = false;

    }

    //更新tableview
    private void dynamicRenderTable(ArrayList<StyleChangeInfo> lists,String operation_Type) {
        LinearLayout mLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_style_change_task_details, null);
        TableView tableView = (TableView)mLinearLayout.findViewById(R.id.table);
        ((TextView) mLinearLayout.findViewById(R.id.tv_value)).setText(operation_Type);
        ((TextView) mLinearLayout.findViewById(R.id.tv_type_label)).setVisibility(View.GONE);
        ((TextView) mLinearLayout.findViewById(R.id.tv_type_value)).setVisibility(View.GONE);

        mLinearLayout.findViewById(R.id.tv_orderno).setVisibility(View.GONE);
        mLinearLayout.findViewById(R.id.tv_orderno_value).setVisibility(View.GONE);
        mLinearLayout.findViewById(R.id.tv_jo_closedate).setVisibility(View.GONE);
        mLinearLayout.findViewById(R.id.tv_closedate).setVisibility(View.GONE);
        mLinearLayout.findViewById(R.id.tv_attcement_label).setVisibility(View.GONE);
        mLinearLayout.findViewById(R.id.tv_attcement_name).setVisibility(View.GONE);

        String nameTitle = "";
        String qtyTitle = "";
        if(TaskClass.equals(Task.TRANSFER_CHECK_IN)){
            ((TextView) mLinearLayout.findViewById(R.id.tv_label)).setText(LocaleUtils.getI18nValue("check_in_type_name"));
            nameTitle = LocaleUtils.getI18nValue("check_in_name");
            qtyTitle = LocaleUtils.getI18nValue("check_in_qty");
        }else if(TaskClass.equals(Task.TRANSFER_REJECT)){
            ((TextView) mLinearLayout.findViewById(R.id.tv_label)).setText(LocaleUtils.getI18nValue("reject_type_name"));
            nameTitle = LocaleUtils.getI18nValue("reject_name");
            qtyTitle = LocaleUtils.getI18nValue("reject_qty");
        }

        tableView.clearTableContents()
                .setHeader(LocaleUtils.getI18nValue("attachment_id"),nameTitle,qtyTitle);
        for(int i =0;i<lists.size();i++){
            StyleChangeInfo styleChangeInfo = lists.get(i);
            String name = styleChangeInfo.getOperationName();
            tableView.addContent(styleChangeInfo.getAttachmentId(), name,styleChangeInfo.getScanQty());
        }
        tableView.refreshTable();
        mContainer.addView(mLinearLayout);
    }

    private void postStyleChangeData(){
        if(modelIdList.size() == 0){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("CheckInDataIsEmpty"),mContext);
            return;
        }
        //
        final String DialogMessage=LocaleUtils.getI18nValue(messageStr);

        //2021-12-01 Abrahamguo 定义runnable对象，执行原确认提交逻辑
        final Runnable runnable = new Runnable() {
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
                                    if(TaskClass.equals(Task.TRANSFER_CHECK_IN)){
                                        postStyleChangeOrderStatus();
                                    }else if(TaskClass.equals(Task.TRANSFER_REJECT)){
                                        postStyleChangeReject();
                                    }
                                    //是否需要骑缝线为否
                                    isNeedSewingLine = false;
                                }
                            });
                        }
                    }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //是否需要骑缝线为否
                            isNeedSewingLine = false;
                        }
                    });
                    AddEquipmentDialog=builder.create();
                    AddEquipmentDialog.show();
                }
            }
        };

        LogUtils.e(SharedPreferenceManager.getFactory(mContext));
        LogUtils.e(getLoginInfo().getFromFactory());
        LogUtils.e(modelIdList.toString());

        //遍历qrcode集合
        for (String qrCode: modelIdList) {
            //判断是否存在cf开头的qrcode(是否存在压脚)
            if(qrCode.startsWith("CF")){
                //需要骑缝线为真
                this.isNeedSewingLine = true;
                //跳出循环
                break;
            }
        }
        //判断是否需要骑缝线
        if(isNeedSewingLine){
            showSearchView(new CallBack() {
                @Override
                public void Invoke(Object... objects) {
                    if(objects.length>0){
                        sewingLine = (String)objects[0];
                    }
                    runOnUiThread(runnable);
                }
            });
        }
        else{
            runOnUiThread(runnable);
        }
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


    //扫描时，scan数量增加1
    private void refreshScanQty(String scanValue,String operationTypeStr,String type){
        ArrayList<StyleChangeInfo> styleChangeInfos = styleChangeListMap.get(operationTypeStr);
        ArrayList<StyleChangeInfo> newList = new ArrayList<>();
        newList.addAll(styleChangeInfos);
        if(styleChangeInfos == null){
            return;
        }
        for(int i=0;i<styleChangeInfos.size();i++){
            StyleChangeInfo styleChangeInfo = styleChangeInfos.get(i);
            if(scanValue.equals(styleChangeInfo.getAttachmentId())){
                Integer scanQty = Integer.valueOf(styleChangeInfo.getScanQty());
                List<String> qrCodeList = styleChangeInfo.getQrCodeList();
                if(type.equals("+")){
                    scanQty+=1;
                    qrCodeList.add(qrCode);
                }else if(type.equals("-")){
                    scanQty-=1;
                    qrCodeList.remove(qrCode);
                }
                if (!scanQty.equals(0)){
                    styleChangeInfo.setQrCodeList(qrCodeList);
                    styleChangeInfo.setScanQty(scanQty.toString());
                }else{
                    newList.remove(styleChangeInfo);
                }
                break;
            }
        }
        if(newList.size() == 0){
            styleChangeListMap.remove(operationTypeStr);
        }else{
            styleChangeInfos.clear();
            styleChangeInfos.addAll(newList);
        }
        refreshTableView();
    }

    //扫描二维码调用接口获取数据
    private void getStyleChangeProcessingDataFromServer(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        LogUtils.e("获取到attachment--->"+attachment);
        HttpUtils.getChangeStyle(mContext, "1.0/classes/product?offset=1&order=itemid&where=%5B%7B%22_and%22%3A%5B%7B%22itemid%22%3A%7B%22eq%22%3A%22"+attachment+"%22%7D%7D%5D%7D%5D", params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                LogUtils.e("获取数据成功--->"+t);
                if(!Strings.isNullOrEmpty(t)){
                    JsonObjectElement obj=new JsonObjectElement(t);
                    ObjectElement objEle =obj.getAsObjectElement("data");
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("items").asArrayElement() != null) {
                        ArrayElement items = objEle.get("items").asArrayElement();
                        if(items.size() == 1){
                            ObjectElement assignmentObj = items.get(0).asObjectElement();
                            operationType = assignmentObj.get("type").toString().replace("\"","");
                            ArrayList<StyleChangeInfo> styleChangeInfos = styleChangeListMap.get(operationType);
                            StyleChangeInfo info = new StyleChangeInfo();
                            info.setAttachmentId(assignmentObj.get("itemid").toString().replace("\"",""));
                            info.setOperationType(operationType);
                            ObjectElement operationNameObj = assignmentObj.get("name").asObjectElement();
                            String languageStr = LocaleUtils.getLanguage(mContext).toUpperCase().equals("ZH-CN")?"zh_cn":"en_us";
                            String operationName = operationNameObj.get(languageStr).toString().replace("\"","");
                            List<String> qrCodeList = new ArrayList<String>();
                            info.setOperationName(operationName);
                            if(styleChangeInfos == null){
                                styleChangeInfos = new ArrayList<>();
                                styleChangeInfos.add(info);
                                info.setScanQty("1");
                                qrCodeList.add(qrCode);
                                info.setQrCodeList(qrCodeList);
                                styleChangeListMap.put(operationType,styleChangeInfos);
                            }else{
                                boolean isSave = false;
                                for(int k = 0;k<styleChangeInfos.size();k++){
                                    StyleChangeInfo info2 = styleChangeInfos.get(k);
                                    qrCodeList = info2.getQrCodeList();
                                    if(info2.getAttachmentId().equals(info.getAttachmentId())){
                                        qrCodeList.add(qrCode);
                                        info2.setQrCodeList(qrCodeList);
                                        Integer num = qrCodeList.size();
                                        info2.setScanQty(num.toString());
                                        isSave = true;
                                        break;
                                    }
                                }
                                if (!isSave){
                                    info.setScanQty("1");
                                    styleChangeInfos.add(info);
                                }
                            }
                            modelIdList.add(qrCode);
                            typeMap.put(qrCode,operationType);
                            //这一句是重新扫描
                            handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
                        }else{
                            //这一句是重新扫描
                            handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("GetStyleChangeQrCodeDataIsEmpty"),mContext);
                        }
                    }else{
                        handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
                    }
                }
                refreshTableView();
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("getStyleChangeProcessingDataFromServer-获取数据失败--->"+errorNo+"---->"+strMsg);
                //这一句是重新扫描
//                handler.sendEmptyMessageDelayed(MessageIDs.restart_preview,3000);
                handler.sendEmptyMessageDelayed(R.id.restart_preview,3000);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetStyleChangeQrCodeCauseByTimeOut+errorNo"),mContext);
                dismissCustomDialog();
            }
        });
    }

    //扫描到二维码，返回页面时的处理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY:{
                if(resultCode==Constants.RESULT_CODE_CAPTURE_ACTIVITY_TO_TASK_DETAIL){
                    if (data != null)
                    {
                        final String result = data.getStringExtra("result");
                        if(!Strings.isNullOrEmpty(result)){
                            qrCode=result;
                            if (qrCode.startsWith("CF")){
                                attachment = qrCode;
                            }else{
                                attachment = qrCode.substring(0,qrCode.length()-6);
                            }
                            String str = attachment.substring(attachment.length()-1);
                            if (str.equals("-")){
                                attachment = attachment.substring(0,attachment.length()-1);
                            }
                            LogUtils.e("最后的attachment--->"+attachment);
                            if (qrCode.startsWith("CF")){
                                getStyleChangeProcessingDataFromServer();
                                return;
                            }
                            if(modelIdList.contains(qrCode)){
                                final String DialogMessage=LocaleUtils.getI18nValue("AreYouSureToCancelQRCode")
                                        +"\n"+LocaleUtils.getI18nValue("qr_code")+qrCode;
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
                                                            String type = typeMap.get(qrCode);
                                                            refreshScanQty(attachment,type,"-");
                                                            modelIdList.remove(qrCode);
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
                            }else{
                                getStyleChangeProcessingDataFromServer();
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    //入仓接口
    private void postStyleChangeOrderStatus(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        JsonObjectElement obj = new JsonObjectElement();
        JsonArrayElement jsonArrayElement = new JsonArrayElement(modelIdList.toString());
        obj.set("ucc",jsonArrayElement);
        params.putJsonParams(obj.toJson());

        String url;
        if(isNeedSewingLine){
            url = "1.1/emms/vms/receipt/" + this.factory + "/" + this.sewingLine;
        }
        else{
            url = "1.1/emms/vms/receipt/1";
        }

        LogUtils.e(url);

        //接口确认直接使用严格模式出仓数据
        HttpUtils.postChangeStyle(mContext, url, params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                LogUtils.e("postStyleChangeOrderStatus-提交入仓数据成功-->"+t);
                List<String> returnMsg = new ArrayList<String>();
                if(!Strings.isNullOrEmpty(t)){
                    JsonObjectElement obj=new JsonObjectElement(t);
                    DataElement codeEle = obj.get("code");
                    ObjectElement objEle =obj.getAsObjectElement("data");
                    //ObjectElement msgEle = obj.getAsObjectElement("msg");
					DataElement msgEle = obj.get("msg");
                    StringBuffer DialogMessageBuffer = new StringBuffer();
                    //判断请求结果代码是否为424
                    if(codeEle != null && codeEle.valueAsInt() == 424){
                        DialogMessageBuffer.append("请联系IT部门进行处理。");
                        LogUtils.e(msgEle.toString());
                        returnMsg.clear();
                        returnMsg.addAll(modelIdList);
                    }
                    //判断是否存在辅件已在库存中,不可重复入仓
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("ucc").asArrayElement() != null && objEle.get("ucc").asArrayElement().size() > 0) {
                        ArrayElement items = objEle.get("ucc").asArrayElement();
                        List<String> uccMsg = new ArrayList<String>();
                        for(int i=0;i< items.size();i++){
                            String uccCode = items.get(i).toString();
                            uccCode = uccCode.replace("\"","");
                            returnMsg.add(uccCode);
                            uccMsg.add(uccCode);
                        }
                        DialogMessageBuffer.append(LocaleUtils.getI18nValue("FailReceiptUcc+errorNo:")+"\n"+uccMsg.toString()+"\n");
                    }
                    //判断是否存在压脚归还数量超过借出数量
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("footUcc").asArrayElement() != null && objEle.get("footUcc").asArrayElement().size() > 0) {
                        ArrayElement items = objEle.get("footUcc").asArrayElement();
                        List<String> footUccMsg = new ArrayList<String>();
                        for(int i=0;i< items.size();i++){
                            String footUccCode = items.get(i).toString();
                            footUccCode = footUccCode.replace("\"","");
                            returnMsg.add(footUccCode);
                            footUccMsg.add(footUccCode);
                        }
                        DialogMessageBuffer.append("以下压脚入仓数量大于出仓数量："+"\n"+footUccMsg.toString()+"\n");
                    }
                    //判断是否存在数据异常, 部分压脚已在库存中,不可重复入仓
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("inconsistentFootUcc").asArrayElement() != null && objEle.get("inconsistentFootUcc").asArrayElement().size() > 0) {
                        ArrayElement items = objEle.get("inconsistentFootUcc").asArrayElement();
                        List<String> inconsistentFootUccMsg = new ArrayList<String>();
                        for(int i=0;i< items.size();i++){
                            String inconsistentFootUccCode = items.get(i).toString();
                            inconsistentFootUccCode = inconsistentFootUccCode.replace("\"","");
                            returnMsg.add(inconsistentFootUccCode);
                            inconsistentFootUccMsg.add(inconsistentFootUccCode);
                        }
                        DialogMessageBuffer.append("以下压脚已有部分入仓,不可重复入仓："+"\n"+inconsistentFootUccMsg.toString()+"\n");
                    }
                    if(returnMsg.size()>0){
                        DialogMessageBuffer.append("0个辅件入仓成功");
                        final String DialogMessage=DialogMessageBuffer.toString();
                        final List<String> returnStr = returnMsg;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("提交失败：");
                                    builder.setMessage(DialogMessage);
                                    builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    deleteQrCode(returnStr);
                                                }
                                            });
                                        }
                                    });
                                    AddEquipmentDialog=builder.create();
                                    AddEquipmentDialog.show();
                                }
                            }
                        });
                    }else{
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("ReceiptSuccess"),mContext);
                        styleChangeListMap.clear();
                        modelIdList.clear();
                        mContainer.removeAllViews();
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("postStyleChangeOrderStatus-提交入仓数据失败-->"+errorNo+"---->"+strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailPostReceipt+errorNo"),mContext);
                dismissCustomDialog();
            }
        });
    }

    private void deleteQrCode(List<String> qrCodeList){
        for(String qrCodeStr : qrCodeList){
            String scanValue;
            if (qrCodeStr.startsWith("CF")){
                scanValue = qrCodeStr;
            }else{
                scanValue = qrCodeStr.substring(0,qrCodeStr.length()-6);
            }
            String type = typeMap.get(qrCodeStr);
            refreshScanQty(scanValue,type,"-");
            modelIdList.remove(qrCodeStr);
        }
        refreshTableView();
    }

    //报废接口
    private void postStyleChangeReject(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        JsonObjectElement obj = new JsonObjectElement();
        JsonArrayElement jsonArrayElement = new JsonArrayElement(modelIdList.toString());
        obj.set("ucc",jsonArrayElement);
        params.putJsonParams(obj.toJson());
        //接口确认直接使用严格模式出仓数据
        HttpUtils.postChangeStyle(mContext, "1.1/emms/vms/scrap/1", params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                List<String> returnMsg = new ArrayList<String>();
                LogUtils.e("获取到加载的数据---->"+t);
                if(!Strings.isNullOrEmpty(t)){
                    LogUtils.e("获取到加载的数据---->"+t);
                    JsonObjectElement obj=new JsonObjectElement(t);
                    ObjectElement objEle =obj.getAsObjectElement("data");
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("ucc").asArrayElement() != null && objEle.get("ucc").asArrayElement().size() > 0) {
                        ArrayElement items = objEle.get("ucc").asArrayElement();
                        for(int i=0;i< items.size();i++){
                            String uccCode = items.get(i).toString();
                            uccCode = uccCode.replace("\"","");
                            returnMsg.add(uccCode);
                        }
                    }
                    if(returnMsg.size()>0){
                        final String DialogMessage=LocaleUtils.getI18nValue("FailRejectUcc+errorNo")+returnMsg.toString();
                        final List<String> returnStr = returnMsg;
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
                                                    deleteQrCode(returnStr);
                                                }
                                            });
                                        }
                                    });
                                    AddEquipmentDialog=builder.create();
                                    AddEquipmentDialog.show();
                                }
                            }
                        });
                    }else{
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("ScrapSuccess"),mContext);
                        styleChangeListMap.clear();
                        modelIdList.clear();
                        mContainer.removeAllViews();
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("获取到加载的数据失败---->"+errorNo+"--->"+strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailPostReject+errorNo"),mContext);
                dismissCustomDialog();
            }
        });
    }

    private void ScanQRCode(){
        ((Activity)mContext).startActivityForResult(new Intent(mContext, StyleChangeCaptureActivity.class), Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY);
//        ((Activity)mContext).startActivityForResult(new Intent(mContext, GoogleCaptureActivity.class),Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY);
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
//                ScanQRCode();
                //从原来的扫描修改为跳转到辅件跟踪
                jumpToitemtracking();
                break;
        }
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

    String RepeatCode="";
    boolean isRepeat = false;

    /**
     * 处理结果的方法
     */
    private void handlerResult(String result) {
        if (!Strings.isNullOrEmpty(result)) {

            if (RepeatCode.equals(result)){
                LogUtils.e("扫描重复了");
                isRepeat = true;
//                handler.sendEmptyMessageDelayed(MessageIDs.restart_preview,3000);
            }else{
                isRepeat = false;
            }

            qrCode = result;
            if (qrCode.startsWith("CF")){
                attachment = qrCode;
            }else{
                attachment = qrCode.substring(0, qrCode.length() - 6);
            }

            RepeatCode = qrCode;

            String str = attachment.substring(attachment.length()-1);
            if (str.equals("-")){
                attachment = attachment.substring(0,attachment.length()-1);
            }
            LogUtils.e("最后的attachment--->"+attachment);
            if (qrCode.startsWith("CF")){
                getStyleChangeProcessingDataFromServer();
                return;
            }

            if (modelIdList.contains(qrCode)) {
                final String DialogMessage = LocaleUtils.getI18nValue("AreYouSureToCancelQRCode")
                        + "\n" + LocaleUtils.getI18nValue("qr_code") + qrCode;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (AddEquipmentDialog == null || !AddEquipmentDialog.isShowing()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(DialogMessage);
                            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String type = typeMap.get(qrCode);
                                            refreshScanQty(attachment, type, "-");
                                            modelIdList.remove(qrCode);
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
                            AddEquipmentDialog = builder.create();
                            AddEquipmentDialog.show();
                        }
                    }
                });
            }else{
                getStyleChangeProcessingDataFromServer();

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

    private void jumpToitemtracking(){
        Intent intent = new Intent(mContext,ItemTrackingActivity.class);
        startActivity(intent);
    }

    //2021-12-01 Abrahamguo 新增展示搜索视图方法（车缝线）
    private void showSearchView(final CallBack callBack) {
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        menuSearchTitle.setText("请选择车缝线");
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
        mResultAdapter.changeData(searchDataLists,"sewingline");
        mResultListView.setAdapter(mResultAdapter);

        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id){
                String itemNam = mResultAdapter.getItemName();
                final String searchResult = DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                LogUtils.e("searchResult---->" + searchResult);
                if (!searchResult.equals("")) {
                    searchBox.setText("");
                    mDrawer_layout.closeDrawer(Gravity.RIGHT);
                    callBack.Invoke(searchResult);
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_occur"), mContext);
                }
            }
        });
        clearBtn.setOnClickListener(this);
        mDrawer_layout.openDrawer(Gravity.RIGHT);
    }

    // 2021-12-01 Abrahamguo 新增在查询视图查询车缝线方法
    private ArrayList<ObjectElement> search(String keyword, String tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    // 2021-12-01 Abrahamguo 新增获取车缝线方法
    private void getmSewingDataFromAPI(String whereJson) {
        HttpParams params = new HttpParams();
        LogUtils.e("getmSewingDataFromAPI--获取数据成功--->" + whereJson + params);
        HttpUtils.getChangeStyle(mContext, "1.1/classes/order/sewingline?order=-sewingline&where=" + whereJson, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                dismissCustomDialog();
                searchDataLists.clear();
                if (!Strings.isNullOrEmpty(t)) {
                    LogUtils.e("getmSewingDataFromAPI--获取数据成功--->" + t);
                    JsonObjectElement obj = new JsonObjectElement(t);
                    ObjectElement objEle = obj.getAsObjectElement("data");
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("items").asArrayElement() != null) {
                        ArrayElement items = objEle.get("items").asArrayElement();
                        for (int i = 0; i < items.size(); i++) {
                            JsonObjectElement jsonObj = new JsonObjectElement();
                            jsonObj.set("sewingline", items.get(i).toString().replace("\"", ""));
                            searchDataLists.add(jsonObj.asObjectElement());
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
}
