package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.SparePartListViewActivity;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.ui.CancelTaskDialog;
import com.emms.ui.TaskCancelListener;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jaffer.deng on 2016/6/20.
 * 处理中的Fragment
 *
 */
public class SparePartRequestListFragment extends BaseFragment {
    public static final String PENDING = "Pending";//待处理
    public static final String NotVERIFY = "NotVerify";//待核销
    public static final String COMPLETE = "Complete";//已完成

    private String type;

    private PullToRefreshListView listView;
    private SparePartRequestAdapter adapter;
    public ArrayList<ObjectElement> getDatas() {
        return datas;
    }

    private ArrayList<ObjectElement> datas=new ArrayList<>();
    private Context mContext;
    private Handler handler=new Handler();
    private int pageIndex=1;
    private int RecCount=0;
    private int organiseID = -1;
    private String requestNo = "";
    private int operatorID;

    private CancelRequestCallback cancelRequestCallback;

    public void setCancelRequestCallback(CancelRequestCallback cancelRequestCallback){
        this.cancelRequestCallback = cancelRequestCallback;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        type = getArguments().getString("type");
        operatorID = getArguments().getInt("operatorID",0);
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex=1;

                        initListData(true);
                        listView.onRefreshComplete();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        initListData(true);
                        listView.onRefreshComplete();
                    }
                },0);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, SparePartListViewActivity.class);
                intent.putExtra(Task.TASK_CLASS,Task.SPAREPART_DETAILS);
                intent.putExtra("requestNo",DataUtil.isDataElementNull(datas.get(position-1).get("SpareBillNo")));
                startActivity(intent);
            }
        });
        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                boolean isCancel = false;
                if(datas.get(position-1).get("BillStatus").valueAsString().equals("Pending")){
                    isCancel = true;
                }
                if(isCancel){
                    CancelTaskDialog cancleTaskDialog = new CancelTaskDialog(mContext);
                    cancleTaskDialog.setDialogTitle(LocaleUtils.getI18nValue("cancel_request"));
                    cancleTaskDialog.setTips(LocaleUtils.getI18nValue("NoRequestCancelReason"));
                    cancleTaskDialog.setTaskCancelListener(new TaskCancelListener() {
                        @Override
                        public void submitCancel(String CancelReason) {
                            CancelRequest(DataUtil.isDataElementNull(datas.get(position-1).get("SpareBillNo")),
                                    DataUtil.isDataElementNull(datas.get(position-1).get("BillType")),
                                    CancelReason);
                        }
                    });
                    cancleTaskDialog.show();
                    return true;
                } else {
                    return false;
                }
            }
        });
        adapter = new SparePartRequestAdapter();
        listView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListData(false);
    }

    public void doRefresh(){
        pageIndex=1;
        initListData(true);
    }

    public void setSearchCondition(int organiseID,String requestNo){
        this.organiseID = organiseID;
        this.requestNo = requestNo;

    }

    private void initListData(final boolean isShowNoMoreData){
        LogUtils.e("pageindex----->"+pageIndex);

        int PAGE_SIZE=10;

        if(RecCount!=0){
            if((pageIndex-1)* PAGE_SIZE >=RecCount){
                if(isShowNoMoreData){
                    Toast toast=Toast.makeText(mContext, LocaleUtils.getI18nValue("noMoreData"),Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                return;
            }
        }
        showCustomDialog(LocaleUtils.getI18nValue("loadingData"));
        HttpParams params = new HttpParams();
        params.put("pageSize", PAGE_SIZE);
        params.put("pageNumber",pageIndex);
        if(!requestNo.equals("")){
            params.put("SpareBillNo",requestNo);
        }
        if(organiseID != -1){
            params.put("Organise_ID",organiseID);
        }
        if(type.equals(PENDING)){
            params.put("billType","Receive");
            params.put("BillStatus","Pending");
        }else if(type.equals(NotVERIFY)){
            params.put("billType","Back");
            params.put("BillStatus","Pending");
        }else if(type.equals(COMPLETE)){
            params.put("BillStatus","Other");
        }
        HttpUtils.get(mContext, "MaterialRequest/GetRequestBillByList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        LogUtils.e("获取到备件请求单数据----->"+t);
                        JsonObjectElement json=new JsonObjectElement(t);
                        if(json.get("Success").valueAsBoolean()){
                            RecCount=json.get("RecCount").valueAsInt();
                            LogUtils.e("RecCount---->"+RecCount);
                            if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                                if(pageIndex==1){
                                    datas.clear();
                                }
                                LogUtils.e("pageIndexeee---->"+pageIndex);
                                pageIndex++;
                                for (int i = 0;i<json.get("PageData").asArrayElement().size();i++){
                                    datas.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                                }
                            }else if(json.get("PageData")!=null&&json.get("PageData").isNull()){
                                if(isShowNoMoreData){
                                    Toast toast=Toast.makeText(mContext, LocaleUtils.getI18nValue("noMoreData"),Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                }
                                datas.clear();
                            }
                            adapter.setRequestDetails(datas);
                        } else {
                            if(isShowNoMoreData)
                            {
                                Toast toast=Toast.makeText(mContext, LocaleUtils.getI18nValue("noMoreData"),Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                            datas.clear();
                            adapter.setRequestDetails(datas);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                            ToastUtil.showToastShort(e.getCause().toString(),mContext);}
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("getDataFail"),mContext);
                dismissCustomDialog();
            }
        });
    }

    private void CancelRequest(String requestNo,String billType, String CancelReason){
        showCustomDialog(LocaleUtils.getI18nValue("waiting"));
        HttpParams params = new HttpParams();
        JsonObjectElement json = new JsonObjectElement();
        json.set("Spare_BillNo",requestNo);
        json.set("Bill_Status","Cancel");
        json.set("Remark",CancelReason);
        json.set("Bill_Type",billType);
        json.set("Operator_ID", operatorID);
        json.set("spare_Details",new JsonArrayElement("[]"));
        params.putJsonParams(json.toJson());
        LogUtils.e(params.getJsonParams());
        HttpUtils.post(mContext, "MaterialRequest/ModifyMaterialRequest", params, new HttpCallback() {
            @Override
            public void onSuccess(String t){
                LogUtils.e("MaterialRequest/MaterialRequest_Create--->提交的结果--->"+t);
                JsonObjectElement json = new JsonObjectElement(t);
                if(json.get("Success").valueAsBoolean()){
                    ToastUtil.showToastLong(LocaleUtils.getI18nValue("submitSuccess"),mContext);
                    doRefresh();
                    cancelRequestCallback.invock();
                }
                else {
                    ToastUtil.showToastLong(DataUtil.isDataElementNull(json.get("Msg")),mContext);
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg){
                //dismissCustomDialog();
                HttpUtils.tips(mContext, errorNo + "strMsg-->" + strMsg);
                LogUtils.e("getTeamIdByOrganiseIDByServe--获取失败--->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastLong(LocaleUtils.getI18nValue("submit_Fail"),mContext);
                dismissCustomDialog();
            }
        });
    }

    private class SparePartRequestAdapter extends BaseAdapter{

        private ArrayList<ObjectElement> requestDetails = new ArrayList<>();

        private Map<String,String> SpareBillType;

        Map<String,String> BillStatuses = SharedPreferenceManager.getHashMapData(mContext,"BillStatus");

        public void setRequestDetails(ArrayList<ObjectElement> requestDetails){
            this.requestDetails = requestDetails;
            notifyDataSetChanged();
        }

        private SparePartRequestAdapter(){
            SpareBillType = SharedPreferenceManager.getHashMapData(mContext,"SpareBillType");
        }

        @Override
        public int getCount() {
            return requestDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return requestDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return requestDetails.get(position).get("Id").valueAsLong();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            ObjectElement requestDetail = requestDetails.get(position);
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.deatils_spare_part_request, null);
                holder = new ViewHolder();
                holder.spare_part_request_status = ((LinearLayout) convertView.findViewById(R.id.spare_part_request_status));
                holder.request_typeVal = ((TextView) convertView.findViewById(R.id.request_typeVal));
                holder.request_typeTxt = ((TextView) convertView.findViewById(R.id.request_typeTxt));
                holder.request_departmentsVal = ((TextView) convertView.findViewById(R.id.request_departmentsVal));
                holder.request_departmentsTxt = ((TextView) convertView.findViewById(R.id.request_departmentsTxt));
                holder.request_creatorVal = ((TextView) convertView.findViewById(R.id.request_creatorVal));
                holder.request_creatorTxt = ((TextView) convertView.findViewById(R.id.request_creatorTxt));
                holder.request_codeTxt = ((TextView) convertView.findViewById(R.id.request_codeTxt));
                holder.request_codeVal = ((TextView) convertView.findViewById(R.id.request_codeVal));
                holder.request_create_dateTxt = ((TextView) convertView.findViewById(R.id.request_create_dateTxt));
                holder.request_create_dateVal = ((TextView) convertView.findViewById(R.id.request_create_dateVal));
                holder.request_status = ((TextView) convertView.findViewById(R.id.request_status));
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.request_codeTxt.setText(LocaleUtils.getI18nValue("request_no"));
            holder.request_departmentsTxt.setText(LocaleUtils.getI18nValue("property_using_dept_1"));
            holder.request_creatorTxt.setText(LocaleUtils.getI18nValue("applicanter"));
            holder.request_typeTxt.setText(LocaleUtils.getI18nValue("reuqest_type"));
            holder.request_create_dateTxt.setText(LocaleUtils.getI18nValue("create_time"));

            holder.request_codeVal.setText(DataUtil.isDataElementNull(requestDetail.get("SpareBillNo")));
            holder.request_departmentsVal.setText(DataUtil.isDataElementNull(requestDetail.get("OrganiseName")));
            holder.request_creatorVal.setText(DataUtil.isDataElementNull(requestDetail.get("OperatorName")));
            holder.request_create_dateVal.setText(DataUtil.isDataElementNull(requestDetail.get("Create_Date")));
            String BillType = DataUtil.isDataElementNull(requestDetail.get("BillType"));
            holder.request_typeVal.setText(SpareBillType.get(BillType));
            holder.request_status.setTextColor(android.graphics.Color.RED);
            String RequestStatus = DataUtil.isDataElementNull(requestDetail.get("BillStatus"));
            holder.request_status.setText(BillStatuses.get(RequestStatus));

            return convertView;
        }
    }

    //视图支持
    private final class ViewHolder {
        TextView request_codeTxt;
        TextView request_codeVal;
        TextView request_creatorTxt;
        TextView request_creatorVal;
        TextView request_departmentsTxt;
        TextView request_departmentsVal;
        TextView request_typeTxt;
        TextView request_typeVal;
        TextView request_create_dateTxt;
        TextView request_create_dateVal;
        LinearLayout spare_part_request_status;
        TextView request_status;
    }

    public interface CancelRequestCallback{
        void invock();
    }
}
