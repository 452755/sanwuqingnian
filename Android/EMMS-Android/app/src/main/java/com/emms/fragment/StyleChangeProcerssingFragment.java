package com.emms.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.StyleChangeTaskDetailsActivity;
import com.emms.activity.TaskDetailsActivity;
import com.emms.activity.TaskNumInteface;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.ToastUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/20.
 *
 */
public class StyleChangeProcerssingFragment extends BaseFragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    public ArrayList<ObjectElement> getDatas() {
        return styleChangedatas;
    }

    private ArrayList<ObjectElement> datas=new ArrayList<>();
    private ArrayList<ObjectElement> styleChangedatas=new ArrayList<>();
    private Context mContext;
    private  String TaskClass;
    private Handler handler=new Handler();
    private int RecCount=0;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        TaskClass=this.getArguments().getString(Task.TASK_CLASS);
//        TaskSubClass=this.getArguments().getString(Task.TASK_SUBCLASS);
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
       // listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getStyleChangeProcessingDataFromServer();
                        listView.onRefreshComplete();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getStyleChangeProcessingDataFromServer();
                        listView.onRefreshComplete();
                    }
                },0);
            }
        });
        taskAdapter = new TaskAdapter(styleChangedatas) {
            private  int   dynamicLayout(TaskViewHolder holder,int indix , DataElement de1){
                if(de1==null||de1.valueAsString()==null||"".equals(de1.valueAsString())){//后台空对象为""
                    return  indix;
                }
                JsonObject jsonData = new JsonParser().parse(de1.valueAsString()).getAsJsonObject();

                return indix;
            }

//            private  void    dynamicLayout2(TaskViewHolder holder,int indix ){
//                switch (indix){
//                    case 1:
//                        holder.tv_dynomic1_tag.setVisibility(View.GONE);
//                        holder.tv_dynomic1_description.setVisibility(View.GONE);
//                        holder.tv_dynomic2_tag.setVisibility(View.GONE);
//                        holder.tv_dynomic2_description.setVisibility(View.GONE);
//                        holder.tv_dynomic3_tag.setVisibility(View.GONE);
//                        holder.tv_dynomic3_description.setVisibility(View.GONE);
//                        holder.tv_dynomic4_tag.setVisibility(View.GONE);
//                        holder.tv_dynomic4_description.setVisibility(View.GONE);
//                        holder.tv_dynomic5_tag.setVisibility(View.GONE);
//                        holder.tv_dynomic5_description.setVisibility(View.GONE);
//                        break;
//                }
//            }

//            private void createDynamicCtrl( ObjectElement taskDetail,TaskViewHolder holder) {//动态生成用户需要的控件
//                int indix=1;
//                DataElement de1=taskDetail.get("SetField1");
//                indix=dynamicLayout(holder,indix,de1);
//                DataElement de2=taskDetail.get("SetField2");
//                indix=dynamicLayout(holder,indix,de2);
//                DataElement de3=taskDetail.get("SetField3");
//                indix=dynamicLayout(holder,indix,de3);
//                DataElement de4=taskDetail.get("SetField4");
//                indix=dynamicLayout(holder,indix,de4);
//                DataElement de5=taskDetail.get("SetField5");
//                indix=dynamicLayout(holder,indix,de5);
//                dynamicLayout2(holder,indix);
//            }
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_style_change_process, parent, false);
                    ((TextView)convertView.findViewById(R.id.id_operation)).setText(LocaleUtils.getI18nValue("operation"));
                    holder = new TaskViewHolder();

                    holder.tv_opreation = (TextView) convertView.findViewById(R.id.id_operation);
                    String languageStr = LocaleUtils.getLanguage(mContext);

                    holder.tv_opreation.setText(DataUtil.isDataElementNull(styleChangedatas.get(position).get("opreation")));

                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }

                return convertView;
            }
        };
       listView.setAdapter(taskAdapter);
        getStyleChangeProcessingDataFromServer();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getStyleChangeProcessingDataFromServer(){
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params=new HttpParams();
        String sewline = this.getArguments().getString("SewingLine");
        String orderno = this.getArguments().getString("OrderNo");
        String paraStr = "%5b%7b%22_and%22%3a%5b%7b%22orderno%22%3a%7b%22eq%22%3a%22"+orderno+"%22%7d%7d%5d%7d%5d";

        HttpUtils.getChangeStyle(mContext, "1.1/classes/order?q=&offset=1&oeder=-id&where="+paraStr, params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                if(!Strings.isNullOrEmpty(t)){
                    styleChangedatas.clear();
                    JsonObjectElement obj=new JsonObjectElement(t);
                    ObjectElement objEle =obj.getAsObjectElement("data");
                    if (objEle != null && objEle.asObjectElement() != null && objEle.get("items").asArrayElement() != null) {
                        ArrayElement items = objEle.get("items").asArrayElement();
                        if(items.size() == 1){
                            ObjectElement assignmentObj = items.get(0).asObjectElement();
                            ArrayElement assignmentItems = assignmentObj.get("assignments").asArrayElement();
                            for(int i = 0;i<assignmentItems.size();i++){
                                styleChangedatas.add(assignmentItems.get(i).asObjectElement());
                            }
                        }
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        taskAdapter.setDatas(styleChangedatas);
                        taskAdapter.notifyDataSetChanged();
                    }
                });
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetStyleChangeTaskDetailCauseByTimeOut+errorNo"),mContext);
                dismissCustomDialog();
            }
        });
    }

    public static StyleChangeProcerssingFragment newInstance(String TaskClass,String sewingLine,String orderno){
        StyleChangeProcerssingFragment fragment = new StyleChangeProcerssingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        bundle.putString("SewingLine",sewingLine);
        bundle.putString("OrderNo",orderno);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void doRefresh(){
        getStyleChangeProcessingDataFromServer();
    }

    public void setTaskNumInteface(TaskNumInteface taskNumInteface) {
        this.taskNumInteface = taskNumInteface;
    }

    private TaskNumInteface taskNumInteface;

}
