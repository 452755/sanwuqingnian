package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.fragment.LinkedOrdersFragment;
import com.emms.fragment.LinkedOrdersFragmentNew;
import com.emms.fragment.PendingOrdersFragment;
import com.emms.fragment.PendingOrdersFragmentNew;
import com.emms.fragment.ProcessingFragmentNew;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.OnPageSelectListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.king.zxing.Intents;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2021/07/22.
 *
 */
public class TaskListActivityNew extends NfcActivity implements OnTabSelectListener,View.OnClickListener{
    private Context mContext ;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles = { LocaleUtils.getI18nValue("select_tab_status_processing"), LocaleUtils.getI18nValue("select_tab_status_orders"), LocaleUtils.getI18nValue("select_tab_status_closed_task")};
    private String TaskClass;
    private String TaskSubClass;
    private String logicType;
    private SlidingTabLayout tabLayout_2;
    private EditText editText_equipment_name,editText_equipment_num;
    private ViewPager vp;
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    private TextView tvICCardId;
    private TextView tv_qr_code;
    private EditText et_qr_code;
    private EditText iccard_id;
    private ImageView device_num_action;
    //private HashMap<String,Integer> TaskClass_Position_map=new HashMap<>()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_task_new);

        mContext = this;
        BaseData.setBaseData(mContext);
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        TaskSubClass=getIntent().getStringExtra(Task.TASK_SUBCLASS);
        logicType=getIntent().getStringExtra(Task.logicType);

        if (logicType == null) {
            logicType = "New";
        }

        initView();
        //getRepairTaskFromServer();
        for (int i =0;i< mTitles.length;i++) {
            LogUtils.e("Tab标签--->"+"i-->"+i+"---->"+mTitles[i]);
            if (i==0) {
                //处理中页面
                ProcessingFragmentNew processingFragment=ProcessingFragmentNew.newInstance(TaskClass,TaskSubClass,logicType);
                processingFragment.setFactoryId(getLoginInfo().getFactoryId());
                processingFragment.setOperatorID(getLoginInfo().getOperator_no());
                processingFragment.setTaskNumInteface(new TaskNumInteface() {
                    @Override
                    public void ChangeTaskNumListener(int tag, int num) {
                       ChangeTaskNum(tag,num);
                    }
                    @Override
                    public void refreshProcessingFragment() {
                    }
                });
                mFragments.add(processingFragment);
            }else if (i ==1){
                //待接单页面
                PendingOrdersFragmentNew pendingOrdersFragment= PendingOrdersFragmentNew.newInstance(TaskClass,TaskSubClass,logicType);
                pendingOrdersFragment.setFactory(getLoginInfo().getFromFactory());
                pendingOrdersFragment.setFactoryId(getLoginInfo().getFactoryId());
                pendingOrdersFragment.setUserId(getLoginInfo().getOperator_no());
                LogUtils.e("getFromFactory--->"+getLoginInfo().getFromFactory());
                pendingOrdersFragment.setOperatorID(String.valueOf(getLoginInfo().getId()));
                pendingOrdersFragment.setTaskNumInteface(new TaskNumInteface() {
                    @Override
                    public void ChangeTaskNumListener(int tag, int num) {
                        ChangeTaskNum(tag,num);
                    }

                    @Override
                    public void refreshProcessingFragment() {
                        ((ProcessingFragmentNew)mFragments.get(0)).doRefresh();
                    }
                });
                mFragments.add(pendingOrdersFragment);
            }else if (i ==2){
                //已接单任务
                LinkedOrdersFragmentNew linkedOrdersFragment = (LinkedOrdersFragmentNew) LinkedOrdersFragmentNew.newInstance(TaskClass,TaskSubClass,logicType);
                linkedOrdersFragment.setFactoryId(getLoginInfo().getFactoryId());
                mFragments.add(linkedOrdersFragment);
            }
        }
        View decorView = getWindow().getDecorView();
        vp = ViewFindUtils.find(decorView, R.id.vp);
        vp.setOffscreenPageLimit(2);
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
       try{
           String[] taskNum=getIntent().getStringExtra("TaskNum").split("/");
           LogUtils.e("订单数量taskNum---->"+getIntent().getStringExtra("TaskNum"));
           tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
           tabLayout_2.setViewPager(vp);
           tabLayout_2.setOnTabSelectListener(this);
           tabLayout_2.setOnPageSelectListener(new OnPageSelectListener() {
               @Override
               public void onPageChange(int position) {
                       findViewById(R.id.search_filter).setVisibility(View.INVISIBLE);
               }
           });

           //   tabLayout_2.showMsg(2, 9);         //消息数量和位置
           //   tabLayout_2.setMsgMargin(2, 12, 10);

           tabLayout_2.showMsg(1, Integer.valueOf(taskNum[1]));
           tabLayout_2.setMsgMargin(1, 12, 10);

           tabLayout_2.showMsg(0, Integer.valueOf(taskNum[0]));
           tabLayout_2.setMsgMargin(0, 12, 10);
           //getSupportFragmentManager().
       }catch (Throwable throwable){
           CrashReport.postCatchedException(throwable);
       }
    }

    private void initView() {
        try{
        LogUtils.e("进入保养页面");
        LogUtils.e("进入点巡检页面");
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.filter).setOnClickListener(this);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.search_button).setOnClickListener(this);
        findViewById(R.id.reset_button).setOnClickListener(this);
        ((TextView)findViewById(R.id.tvEquipmentName)).setText(LocaleUtils.getI18nValue("equipment_name"));
        ((TextView)findViewById(R.id.tvEquipmentNum)).setText(LocaleUtils.getI18nValue("equipment_num"));
        ((Button)findViewById(R.id.search_button)).setText(LocaleUtils.getI18nValue("sure"));
        ((Button)findViewById(R.id.reset_button)).setText(LocaleUtils.getI18nValue("Reset"));
        editText_equipment_name=(EditText) findViewById(R.id.equipment_name);
        editText_equipment_name.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        editText_equipment_num=(EditText)findViewById(R.id.equipment_num);
        editText_equipment_num.setHint(LocaleUtils.getI18nValue("pleaseInput"));
        device_num_action = (ImageView) findViewById(R.id.device_num_action);
        tvICCardId = (TextView) findViewById(R.id.tvICCardId);
        tvICCardId.setText(LocaleUtils.getI18nValue("machine_rfid"));
        tv_qr_code = (TextView) findViewById(R.id.tv_qr_code);
        tv_qr_code.setText(LocaleUtils.getI18nValue("machine_qrcode"));
        et_qr_code = (EditText) findViewById(R.id.et_qr_code);
        et_qr_code.setHint(LocaleUtils.getI18nValue("scan"));
        iccard_id = (EditText) findViewById(R.id.iccard_id);
        iccard_id.setHint(LocaleUtils.getI18nValue("scan"));
        device_num_action.setOnClickListener(this);
        switch (TaskClass){
           case Task.REPAIR_TASK:{
               LogUtils.e("text--->"+LocaleUtils.getI18nValue("repair_task"));
               ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("repair_task"));
               break;
           }
           case Task.MAINTAIN_TASK:{
               if(TaskSubClass==null){
                   LogUtils.e("text--->" + LocaleUtils.getI18nValue("maintain_task"));
                   ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("maintain_task"));
               }
               else if(TaskSubClass.equals(Task.ROUTING_INSPECTION)){
                   LogUtils.e("text--->"+LocaleUtils.getI18nValue("routingInspectionTask"));
                   ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("routingInspectionTask"));

               }else if(logicType.equals("New")){
                   LogUtils.e("text--->"+LocaleUtils.getI18nValue("new_upkeepTask"));
                   ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("new_upkeepTask"));
               }else {
                   LogUtils.e("text--->"+LocaleUtils.getI18nValue("upkeepTask"));
                   ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("upkeepTask"));
               }
               break;
           }
           case Task.MOVE_CAR_TASK:{
               LogUtils.e("text--->"+LocaleUtils.getI18nValue("move_car_task"));
               ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("move_car_task"));
               break;
           }
           case Task.OTHER_TASK:{
               LogUtils.e("text--->"+LocaleUtils.getI18nValue("other_task"));
               ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("other_task"));
               break;
           }
           case Task.TRANSFER_MODEL_TASK:{
               LogUtils.e("text--->"+LocaleUtils.getI18nValue("transfer_model_task"));
               ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("transfer_model_task"));
               break;
           }
           case Task.GROUP_ARRANGEMENT:{
               LogUtils.e("text--->"+LocaleUtils.getI18nValue("GroupArrangementTask"));
               ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("GroupArrangementTask"));
               break;
           }
            case Task.TRANSFER_TASK:{
                LogUtils.e("text--->"+LocaleUtils.getI18nValue("style_change"));
                ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("style_change"));
                break;
            }
       }
        }catch (Exception e){
            CrashReport.postCatchedException(e);
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"),mContext);
            finish();
        }
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    public void onClick(View v) {
        int click_id = v.getId();
        if (click_id ==R.id.btn_right_action){
            finish();
        }
        switch (click_id){
            case R.id.btn_right_action:{
                finish();
                break;
            }
        case R.id.filter:{
            if(findViewById(R.id.search_filter).getVisibility()==View.GONE||
                    findViewById(R.id.search_filter).getVisibility()==View.INVISIBLE){
                //findViewById(R.id.search_filter).setVisibility(View.VISIBLE);
                buttonAnim(true);
            }
            else {
                //findViewById(R.id.search_filter).setVisibility(View.GONE);
                buttonAnim(false);
            }
            break;
        }
        case R.id.search_filter:{
            break;
        }
        case R.id.search_button:{
            switch (vp.getCurrentItem()){
                case 1:{
                    ((PendingOrdersFragmentNew)mFragments.get(1)).setSearchCondition(editText_equipment_num.getText().toString(),editText_equipment_name.getText().toString(),et_qr_code.getText().toString(),iccard_id.getText().toString().trim());
                    ((PendingOrdersFragmentNew)mFragments.get(1)).doRefresh();
                    buttonAnim(false);
//                    vp.setCurrentItem(1);
                    break;
                }
                case 0:{
                    ((ProcessingFragmentNew)mFragments.get(0)).setSearchCondition(editText_equipment_num.getText().toString(),editText_equipment_name.getText().toString(),et_qr_code.getText().toString().trim(),iccard_id.getText().toString().trim());
                    ((ProcessingFragmentNew)mFragments.get(0)).doRefresh();
                    buttonAnim(false);
                    break;
                }
                case 2:{
                    ((LinkedOrdersFragmentNew)mFragments.get(2)).setSearchCondition(editText_equipment_num.getText().toString(),editText_equipment_name.getText().toString(),et_qr_code.getText().toString().trim(),iccard_id.getText().toString().trim());
                    ((LinkedOrdersFragmentNew)mFragments.get(2)).doRefresh();
                    buttonAnim(false);
                    break;
                }
            }

            break;
        }
        case R.id.reset_button:{
            resetCondition();
            break;
        }
        case R.id.device_num_action:
            Intent it = new Intent(mContext, com.king.zxing.CaptureActivity.class);
            it.setAction(Intents.Scan.ACTION);
            it.putExtra(Intents.Scan.CAMERA_ID,0);
            it.putExtra(KEY_IS_CONTINUOUS,false);
            startActivityForResult(it, 1);
            break;
        }
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

//            MessageUtils.showToast(iccardID,this);
            }
        }catch (Exception e){
            CrashReport.postCatchedException(e);
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL:{
                if(resultCode==2){
                ((ProcessingFragmentNew)mFragments.get(0)).doRefresh();
                ((PendingOrdersFragment)mFragments.get(1)).doRefresh();}
                break;
            }
            case 1:
                if (data != null) {
                    LogUtils.e("获取到数据---->"+data.getStringExtra("result"));
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (result != null) {
                        //作用:获取到二维码后进行网络请求  Jason 2019/9/26 下午2:06
                        et_qr_code.setText(result);
                    }
                }
                break;
        }
    }
    private void ChangeTaskNum(int tag,int num){
        tabLayout_2.showMsg(tag,num);
    }
    private void getTaskCountFromServer(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        HttpUtils.get(this, "TaskNum", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement json = new JsonObjectElement(t);
                    //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                    if (json.get("PageData") != null && json.get("PageData").asArrayElement() != null&&json.get("PageData").asArrayElement().size()>0) {
                        for (int i = 0; i < json.get("PageData").asArrayElement().size(); i++) {
                            //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                            //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectEl
                            if(DataUtil.isDataElementNull(json.get("PageData").asArrayElement().get(i).asObjectElement().get("DataCode")).equals(TaskClass)){
                                tabLayout_2.showMsg(1, json.get("PageData").asArrayElement().get(i).asObjectElement().get("S0").valueAsInt());
                                tabLayout_2.showMsg(0, json.get("PageData").asArrayElement().get(i).asObjectElement().get("S1").valueAsInt());
                            }
                        }
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("loadingFail"),mContext);
                dismissCustomDialog();
            }
        });
    }
    private void getTaskByICcardID(String ICcardID){

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
    private void  resetCondition(){
        editText_equipment_name.setText("");
        editText_equipment_num.setText("");
        et_qr_code.setText("");
        iccard_id.setText("");
    }
}
