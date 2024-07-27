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
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
import com.emms.fragment.LinkedOrdersFragment;
import com.emms.fragment.PendingOrdersFragment;
import com.emms.fragment.ProcessingFragment;
import com.emms.fragment.SparePartRequestListFragment;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.ui.DropEditText;
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
 * Created by jaffer.deng on 2016/6/20.
 *
 */
public class SparePartRequestListActivity extends NfcActivity implements OnTabSelectListener,View.OnClickListener{
    private Context mContext ;
    private String[] mTitles = { LocaleUtils.getI18nValue("waiting_for_delivery"), LocaleUtils.getI18nValue("waiting_for_write_off"),LocaleUtils.getI18nValue("task_state_details_finish")};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private SlidingTabLayout tabLayout_2;
    private ViewPager vp;
    private DropEditText subordinateDepartments;
    private EditText requestNoVal;


    private ImageView clearBtn;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private EditText searchBox;
    private DrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> operatorTeams = new ArrayList<>();

    private String requestNo = "";
    private int operatorTeamID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_part_request_list);
        mContext = this;
        initView();
        getTeamIdByOrganiseIDByServe((int) getLoginInfo().getId());
        initSearchView();
        SparePartRequestListFragment.CancelRequestCallback cancelRequestCallback = new SparePartRequestListFragment.CancelRequestCallback() {
            @Override
            public void invock() {
                ((SparePartRequestListFragment) mFragments.get(2)).doRefresh();
            }
        };
        for (int i =0;i< mTitles.length;i++) {
            LogUtils.e("Tab标签--->"+"i-->"+i+"---->"+mTitles[i]);
            if (i==0) {
                SparePartRequestListFragment pendingFragment = new SparePartRequestListFragment();
                pendingFragment.setCancelRequestCallback(cancelRequestCallback);
                Bundle bundle = new Bundle();
                bundle.putString("type",SparePartRequestListFragment.PENDING);
                bundle.putInt("operatorID",(int)getLoginInfo().getId());
                pendingFragment.setArguments(bundle);
                mFragments.add(pendingFragment);
            }else if (i ==1){
                SparePartRequestListFragment verifyFragment = new SparePartRequestListFragment();
                verifyFragment.setCancelRequestCallback(cancelRequestCallback);
                Bundle bundle = new Bundle();
                bundle.putString("type",SparePartRequestListFragment.NotVERIFY);
                bundle.putInt("operatorID",(int)getLoginInfo().getId());
                verifyFragment.setArguments(bundle);
                mFragments.add(verifyFragment);
            }else if (i ==2){
                SparePartRequestListFragment completeFragment = new SparePartRequestListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type",SparePartRequestListFragment.COMPLETE);
                bundle.putInt("operatorID",(int)getLoginInfo().getId());
                completeFragment.setArguments(bundle);
                mFragments.add(completeFragment);
            }
        }
        View decorView = getWindow().getDecorView();
        vp = ViewFindUtils.find(decorView, R.id.vp);
        vp.setOffscreenPageLimit(2);
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        try{
            tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
            tabLayout_2.setViewPager(vp);
            tabLayout_2.setOnTabSelectListener(this);
            tabLayout_2.setOnPageSelectListener(new OnPageSelectListener() {
                @Override
                public void onPageChange(int position) {
                    findViewById(R.id.search_filter).setVisibility(View.INVISIBLE);
                }
            });
        }catch (Throwable throwable){
            CrashReport.postCatchedException(throwable);
        }

    }

    private void initView() {
        try{
            LogUtils.e("查看备件申请历史页面");
            findViewById(R.id.btn_right_action).setOnClickListener(this);
            findViewById(R.id.filter).setOnClickListener(this);
            findViewById(R.id.filter).setVisibility(View.VISIBLE);
            findViewById(R.id.search_button).setOnClickListener(this);
            findViewById(R.id.reset_button).setOnClickListener(this);
            ((TextView) findViewById(R.id.tv_subordinate_departments)).setText(LocaleUtils.getI18nValue("property_using_dept_1"));
            ((TextView) findViewById(R.id.request_code)).setText(LocaleUtils.getI18nValue("request_no"));
            ((Button)findViewById(R.id.search_button)).setText(LocaleUtils.getI18nValue("sure"));
            ((Button)findViewById(R.id.reset_button)).setText(LocaleUtils.getI18nValue("Reset"));
            subordinateDepartments = (DropEditText) findViewById(R.id.subordinate_departments_value);
            subordinateDepartments.setHint(LocaleUtils.getI18nValue("select"));
            requestNoVal = (EditText) findViewById(R.id.request_code_value);
            requestNoVal.setHint(LocaleUtils.getI18nValue("pleaseInput"));
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("checkHistoryRecord"));

            subordinateDepartments.getDropImage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSearchView();
                }
            });

            subordinateDepartments.getmEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSearchView();
                }
            });
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
                    buttonAnim(true);
                }
                else {
                    buttonAnim(false);
                }
                break;
            }
            case R.id.search_button:{
                requestNo = requestNoVal.getText().toString();
                ((SparePartRequestListFragment) mFragments.get(vp.getCurrentItem())).setSearchCondition(operatorTeamID,requestNo);
                ((SparePartRequestListFragment)mFragments.get(vp.getCurrentItem())).doRefresh();
                buttonAnim(false);
                break;
            }
            case R.id.reset_button:{
                resetCondition();
                break;
            }
        }
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

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
        requestNoVal.setText("");
        subordinateDepartments.setText("");
        operatorTeamID = -1;
        requestNo = "";
        buttonAnim(false);
        ((SparePartRequestListFragment)mFragments.get(vp.getCurrentItem())).setSearchCondition(operatorTeamID,requestNo);
        ((SparePartRequestListFragment)mFragments.get(vp.getCurrentItem())).doRefresh();
    }

    private void showSearchView(){
        LogUtils.e("-----展示搜索部门-----");
        searchDataLists = operatorTeams;
        mResultAdapter.changeData(searchDataLists,"OrganiseName");
        mDrawer_layout.openDrawer(Gravity.RIGHT);
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
        mResultAdapter.changeData(searchDataLists,"OrganiseName");
        mResultListView.setAdapter(mResultAdapter);

        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id){
                String itemNam = mResultAdapter.getItemName();
                ObjectElement searchResult = mResultAdapter.getItem(position);
                LogUtils.e("searchResult---->" + searchResult.toString());
                if (searchResult!=null&&!searchResult.get("OrganiseName").valueAsString().equals("")) {
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

    private void getTeamIdByOrganiseIDByServe(final int operatorkeyno) {
        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                //dismissCustomDialog();
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    LogUtils.e("获取数据为空t--->" + t);
                    return;
                }
                LogUtils.e("GetOrganiseInfoByOperatorID--获取成功--->" + t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    operatorTeams.clear();
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        operatorTeams.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }

                    operatorTeamID = operatorTeams.get(0).get("No").valueAsInt();

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
                //dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("GetOrganiseInfoByOperatorID--获取失败--->" + errorNo + "---->" + strMsg);
            }
        };
        //showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("Operator_ID", operatorkeyno);
        HttpUtils.get(this, "BaseOrganise/GetOrganiseInfoByOperatorID", params, callback);
    }
}
