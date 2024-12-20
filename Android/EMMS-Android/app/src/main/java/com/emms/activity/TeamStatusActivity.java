package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.GroupAdapter;
import com.emms.adapter.MultiAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

public class TeamStatusActivity extends NfcActivity implements View.OnClickListener{

    private PullToRefreshListView mListView;
    private MultiAdapter adapter=null;
    private GroupAdapter groupAdapter;
    private ArrayList<ObjectElement> listItems=new ArrayList<>();
    private ArrayList<ObjectElement> listGroup=new ArrayList<>();
    private Context context=this;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    private ObjectElement groupData=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_status);
        initView();
    }

    /**
     * 初始化信息
     */
    private void getListItems() {
        int PAGE_SIZE = 10;
        LogUtils.e("pageindex----->"+pageIndex);

        if(RecCount!=0){
            if((pageIndex-1)* PAGE_SIZE >=RecCount){
                Toast toast=Toast.makeText(this, LocaleUtils.getI18nValue("noMoreData"),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }}
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        try{
            params.put("team_id", DataUtil.isDataElementNull(groupData.get("Organise_ID")));
        }catch (Exception e){
            LogUtils.e("数据异常--->"+groupData);
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("RestartActivity"),context);
            CrashReport.postCatchedException(e);
            finish();
        }

        params.put("pageSize", PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(this, "TaskOperatorAPI/GetTaskOperatorStatus", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到人员数据----->"+t);
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
                                for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                                    listItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                }
                            }else{
                                listItems.clear();
                                ToastUtil.showToastShort(LocaleUtils.getI18nValue("thisGroupHasNoPerson"),context);
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
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("team_status"));
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        adapter=new MultiAdapter(this,listItems,false);
        mListView = (PullToRefreshListView) findViewById(R.id.id_wait_list);
        mListView.setAdapter(adapter);
        ListView mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupAdapter.setSelection(listGroup.get(position));
                pageIndex=1;
                RecCount=0;
                groupData=listGroup.get(position);
                if (groupData!=null){
                    getListItems();
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
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getListItems();
                        mListView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
    }
    public void getGroupData() {
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        params.put(Task.TASK_ID,0);
        HttpUtils.get(this, "BaseDataAPI/GetBaseOrganise", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(t!=null){
                            try{
                                JsonObjectElement json=new JsonObjectElement(t);
                                if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                                    listGroup.clear();
                                    for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                                        listGroup.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                    }
                                    groupData=listGroup.get(0);
                                    getListItems();
                                    groupAdapter.setDatas(listGroup);
                                    groupAdapter.notifyDataSetChanged();
                                    groupAdapter.setSelection(listGroup.get(0));
                                }else {
                                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("GetGroupDataFail"),context);
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
                });
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastLong(LocaleUtils.getI18nValue("GetGroupDataFailCauseByNetWork"),context);
                    }
                });
                dismissCustomDialog();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int clikId =v.getId();
        switch (clikId) {
            case R.id.btn_right_action:{
                finish();
                break;
            }
        }

    }


    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
