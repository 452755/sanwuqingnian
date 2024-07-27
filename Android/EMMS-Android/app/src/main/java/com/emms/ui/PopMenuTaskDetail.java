package com.emms.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.CreateTaskActivity;
import com.emms.activity.CusActivity;
import com.emms.activity.InvitorActivity;
import com.emms.activity.SparePartListViewActivity;
import com.emms.activity.SparePartRequestActivity;
import com.emms.activity.SparePartRequestListActivity;
import com.emms.activity.StyleChangeTaskDetailsActivity;
import com.emms.activity.SubTaskManageActivity;
import com.emms.activity.SummaryActivity;
import com.emms.activity.TaskCompleteActivity;
import com.emms.activity.WorkLoadActivity;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.RootUtil;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.zxing.android.CaptureActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class PopMenuTaskDetail {
    private JsonArrayElement itemList = new JsonArrayElement("[]");
    private Context context;
    private PopupWindow popupWindow;
    private PopMenuTaskDetail popMenuTaskDetail = this;
    private ListView listView;
    private PopAdapter popAdapter;
    private AlertDialog AddEquipmentDialog = null;
    private ObjectElement TaskDetail;
    private Long TaskId;
    private String Task_ID;//转款专用
    private String TaskClass;
	private String ModuleType = "Machine";//模块类型
	public static final String CREATE_NEW_TASK = "CREATE_NEW_TASK";
    public static final String SCAN_QR_CODE = "SCAN_QR_CODE";
    public static final String FAULT_SUMMARY = "FAULT_SUMMARY";
    public static final String WORKLOAD_INPUT = "WORKLOAD_INPUT";
    public static final String SUB_TASK_MANAGE = "SUB_TASK_MANAGE";
    public static final String INVITE_HELP = "INVITE_HELP";
    public static final String TRANSFER_ORDER = "TRANSFER_ORDER";
    public static final String TASK_COMPLETE = "TASK_COMPLETE";
    public static final String REFRESH = "REFRESH";
    public static final String STYLE_CHANGE_CANCEL_TASK = "STYLE_CHANGE_CANCEL_TASK";
    public static final String STYLE_CHANGE_CANCEL_ORDER = "STYLE_CHANGE_CANCEL_ORDER";
    public static final String STYLE_CHANGE_TASK_COMPLETE = "STYLE_CHANGE_TASK_COMPLETE";
    public static final String SPARE_PAER_REQUEST_CREATE = "SPARE_PAER_REQUEST_CREATE";
    public static final String SPARE_PART_VIEWHISTORICAL = "view_historical";
    public static final String SPARE_PART_VIEWSURPLUS = "view_Surplus";


    public void setHasNFC(boolean hasNFC) {
        isHasNFC = hasNFC;
    }

    private boolean isHasNFC = false;

    public NFCDialog getNfcDialog() {
        return nfcDialog;
    }

    public void setNfcDialog(NFCDialog nfcDialog) {
        this.nfcDialog = nfcDialog;
    }

    private NFCDialog nfcDialog;

    public void setIs_Main_person_in_charge_Operator_id(boolean is_Main_person_in_charge_Operator_id) {
        this.is_Main_person_in_charge_Operator_id = is_Main_person_in_charge_Operator_id;
    }

    private boolean taskComplete = false;
    private boolean is_Main_person_in_charge_Operator_id = false;

    public void setHasEquipment(boolean hasEquipment) {
        this.hasEquipment = hasEquipment;
    }

    private boolean hasEquipment = false;

    private String CheckOutData = "";

    public void setEquipmentNum(int equipmentNum) {
        EquipmentNum = equipmentNum;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public void setStyleChangeDetail(String detailInfo) {
        DetailInfo = detailInfo;
    }

    private String OrderNo = "";
    private String DetailInfo = "";
    private int EquipmentNum = 0;
    private HashMap<String, Integer> item_image_mapping = new HashMap<>();

    // private OnItemClickListener listener;
    public PopMenuTaskDetail(Context context, int width, String taskDetail, String taskClass) {
        // TODO Auto-generated constructor stub

        this.context = context;
        item_image_mapping.put(CREATE_NEW_TASK, R.mipmap.create);
        item_image_mapping.put(SCAN_QR_CODE, R.mipmap.more_scan);
        item_image_mapping.put(FAULT_SUMMARY, R.mipmap.failure_summary);
        item_image_mapping.put(WORKLOAD_INPUT, R.mipmap.more_input);
        item_image_mapping.put(SUB_TASK_MANAGE, R.mipmap.sub_task_management);
        item_image_mapping.put(INVITE_HELP, R.mipmap.more_invitation);
        item_image_mapping.put(TRANSFER_ORDER, R.mipmap.more_single_turn);
        item_image_mapping.put(TASK_COMPLETE, R.mipmap.more_finish);
        item_image_mapping.put(REFRESH, R.mipmap.refresh);
        item_image_mapping.put(STYLE_CHANGE_CANCEL_TASK, R.mipmap.more_finish);
        item_image_mapping.put(STYLE_CHANGE_CANCEL_ORDER, R.mipmap.more_cancel);
        item_image_mapping.put(STYLE_CHANGE_TASK_COMPLETE, R.mipmap.more_finish);
        item_image_mapping.put(SPARE_PAER_REQUEST_CREATE, R.mipmap.create);
        item_image_mapping.put(SPARE_PART_VIEWHISTORICAL,R.mipmap.cur_activity_task_history);
        item_image_mapping.put(SPARE_PART_VIEWSURPLUS,R.mipmap.cur_activity_change_style_check_in);

        this.TaskDetail = new JsonObjectElement(taskDetail);
        if (taskClass.equals("T08")) {
            Task_ID = TaskDetail.get(Task.TASK_ID).valueAsString();
        } else {
            TaskId = TaskDetail.get(Task.TASK_ID).valueAsLong();
        }
        ModuleType = TaskDetail.get(Task.ModuleType) == null ? ModuleType : TaskDetail.get(Task.ModuleType).valueAsString();
		TaskClass = taskClass;
        View view = LayoutInflater.from(context)
                .inflate(R.layout.popmenu, null);
        final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.popup_view_cont);
        layout.setBackgroundColor(Color.argb(80, 0, 0, 0));
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        popAdapter = new PopAdapter();
        // 设置 listview
        listView = (ListView) view.findViewById(R.id.listView);
        //listView.setPaddingRelative();
        listView.setBackgroundColor(Color.WHITE);
        listView.setAdapter(popAdapter);
        listView.setFocusableInTouchMode(true);
        listView.setFocusable(true);
        setOnItemClickListener();
        //  ListViewUtility.setListViewHeightBasedOnChildren(listView);
//		popupWindow = new PopupWindow(view, 254, LayoutParams.WRAP_CONTENT);

        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                onEventDismiss();
            }
        });
    }

    public abstract void onEventDismiss();

    public void refreshData() {
        popAdapter.notifyDataSetChanged();
    }

    // 设置菜单项点击监听器
    public void setOnItemClickListener() {
        // this.listener = listener;
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(CREATE_NEW_TASK)) {
                    CreateTask();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(SCAN_QR_CODE)) {
                    ScanQRCode();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(FAULT_SUMMARY)) {
                    FaultSummary();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(WORKLOAD_INPUT)) {
                    WorkloadInput();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(SUB_TASK_MANAGE)) {
                    SubTaskManage();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(INVITE_HELP)) {
                    InviteHelp();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(TRANSFER_ORDER)) {
                    ExChangeOrder();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(TASK_COMPLETE)) {
                    TaskComplete();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(REFRESH)) {
                    Refresh();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(STYLE_CHANGE_CANCEL_TASK)) {
                    CancelStyleChangeTask();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(STYLE_CHANGE_CANCEL_ORDER)) {
                    CancelStyleChangeTaskOrder();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(STYLE_CHANGE_TASK_COMPLETE)) {
                    StyleChangeTaskComplete();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(SPARE_PAER_REQUEST_CREATE)) {
                    SparePartRequestCreate();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(SPARE_PART_VIEWHISTORICAL)) {
                    SparePartRequestViewHistorical();
                } else if (itemList.get(position).asObjectElement().get("code").valueAsString().equals(SPARE_PART_VIEWSURPLUS)) {
                    SparePartRequestViewSurplus();
                }
                popMenuTaskDetail.dismiss();
            }
        });

    }

    // 批量添加菜单项
    public void addItems(JsonArrayElement items) {
        itemList = new JsonArrayElement("[]");
//		itemList.addAll(items);
        switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_COMPLETE_SHOW_WORKLOAD_ACTION))) {
            case "1": {
				/*if(itemList.equals(context.getString(R.string.menu_list_workload_input))){
					itemList.remove();
				}*/
                for (DataElement item : items) {
                    //ignore code is WORKLOAD_INPUT
                    if (!item.asObjectElement().get("code").valueAsString().equals(WORKLOAD_INPUT)) {
                        itemList.add(item);
                    }
                }
                break;
            }
            default: {
                itemList.addAll(items);
                break;
            }
        }
    }

	/*
	// 单个添加菜单项
	public void addItem(DataElement item) {
		itemList.add(item);
	}
	*/

    // 下拉式 弹出 pop菜单 parent 右下角
    public void showAsDropDown(View parent) {
        popupWindow.showAsDropDown(parent,
                10,
                // 保证尺寸是根据屏幕像素密度来的
                -1);

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状态
        popupWindow.update();
    }


    public boolean isShowing() {

        return popupWindow.isShowing();
    }

    // 隐藏菜单
    public void dismiss() {
        popupWindow.update();
        popupWindow.dismiss();
    }

    // 适配器
    private final class PopAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return itemList.get(position).asObjectElement();
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.popuwindow_task, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

                holder.groupItem = (TextView) convertView
                        .findViewById(R.id.textView);
                holder.imageView = (ImageView) convertView.findViewById(R.id.menu_detail);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.groupItem.setText(itemList.get(position).asObjectElement().get("name").valueAsString());
            try {
                Drawable img = context.getResources().getDrawable(R.mipmap.more_input);
                img = context.getResources().getDrawable(item_image_mapping.get(itemList.get(position).asObjectElement().get("code").valueAsString()));
                // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//			img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
//
//			holder.groupItem.setCompoundDrawables(img,null,null,null);
                holder.imageView.setImageDrawable(img);
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
            }
//			if (0 ==position) {
//				img = context.getResources().getDrawable(R.mipmap.create);
//			}else if (1 ==position){
//				img =context.getResources().getDrawable(R.mipmap.failure_summary);
//			}else if (2 == position){
//				img =context.getResources().getDrawable(R.mipmap.more_input);
//			}else if (3 == position){
//				img =context.getResources().getDrawable(R.mipmap.sub_task_management);
//			}else if (4 == position){
//				img =context.getResources().getDrawable(R.mipmap.more_invitation);
//			}else if (5 == position){
//				img =context.getResources().getDrawable(R.mipmap.more_single_turn);
//			}else if (6 == position){
//				img =context.getResources().getDrawable(R.mipmap.more_finish);
//			}

            return convertView;
        }

        private final class ViewHolder {
            TextView groupItem;
            ImageView imageView;
        }
    }

    private void WorkloadInput() {

        if (!is_Main_person_in_charge_Operator_id) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyMainPersonCanWriteWorkload"), context);
            return;
        }
        Intent intent = new Intent(context, WorkLoadActivity.class);
        //intent.putExtra(Task.TASK_ID,TaskId);
        intent.putExtra(Task.TASK_CLASS, TaskClass);
        intent.putExtra("TaskDetail", TaskDetail.toString());
        context.startActivity(intent);
    }

    //	private void Scan(){
//
//	}
    private void ExChangeOrder() {
        if (!is_Main_person_in_charge_Operator_id) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyMainPersonCanChangeOrder"), context);
            return;
        }
        if (taskComplete) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("TaskEquipmentIsCompleteCanNotChangeOrder"), context);
            return;
        }
        Intent intent = new Intent(context, InvitorActivity.class);
        intent.putExtra(Task.TASK_ID, String.valueOf(TaskId));
        intent.putExtra("isExChangeOrder", true);
        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_EXCHANGE_ORDER);
    }

    private void InviteHelp() {
        if (!is_Main_person_in_charge_Operator_id) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyMainPersonCanInvitePeople"), context);
            return;
        }
        if (taskComplete && !TaskClass.equals(Task.MOVE_CAR_TASK)) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("TaskEquipmentIsCompleteCanNotInvite"), context);
            return;
        }
        Intent intent = new Intent(context, InvitorActivity.class);
        intent.putExtra(Task.TASK_ID, String.valueOf(TaskId));
        intent.putExtra("isInviteHelp", true);
        context.startActivity(intent);

    }

    private void SubTaskManage() {
        Intent intent = new Intent(context, SubTaskManageActivity.class);
        //intent.putExtra(Task.TASK_ID,TaskId);
        intent.putExtra("TaskDetail", TaskDetail.toString());
        context.startActivity(intent);
    }

    //	private void FailureSummary(){
//
//	}
//	private void setTaskIdFromActivity(Long taskId){
//		this.TaskId=taskId;
//	}
    private void FaultSummary() {
        if (!is_Main_person_in_charge_Operator_id) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyMainPersonCanWriteFaultSummary"), context);
            return;
        }
        if (RootUtil.rootTaskClass(TaskClass, Task.REPAIR_TASK)
                || RootUtil.rootTaskClass(TaskClass, Task.GROUP_ARRANGEMENT)) {
            Intent intent = new Intent(context, SummaryActivity.class);
            //intent.putExtra(Task.TASK_ID,TaskId);
            intent.putExtra("TaskDetail", TaskDetail.toString());
            intent.putExtra(Task.TASK_CLASS, TaskClass);
            context.startActivity(intent);
        } else {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("judgeTaskClass"), context);
        }
    }

    private void TaskComplete() {
        LogUtils.e("进入任务完成页面--->" + TaskClass);
        LogUtils.e("hasEquipment--->" + hasEquipment + "---->" + LocaleUtils.getI18nValue("TaskHasNoEquipment"));
        //kingzhang for srf 2022-0106
        if(Task.Lend_TASK.contains(TaskClass)){
            taskComplete=true;
        }
        if (!is_Main_person_in_charge_Operator_id) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("OnlyMainPersonCanSubmitTaskComplete"), context);
            return;
        }
        if (hasEquipment && EquipmentNum <= 0) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("TaskHasNoEquipment"), context);
            return;
        }
        LogUtils.e("hasEquipment--->" + hasEquipment + "---->" + LocaleUtils.getI18nValue("TaskEquipmentNotComplete"));
        if (!taskComplete) {
            if (hasEquipment) {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("TaskEquipmentNotComplete"), context);
            } else {
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("CanNotCompleteTask"), context);
            }
            return;
        }
        if (TaskClass != null && TaskClass.equals(Task.MOVE_CAR_TASK)) {
            if (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_COMPLETE_ACTION)).equals("1")) {
                Intent intent = new Intent(context, TaskCompleteActivity.class);
                intent.putExtra("TaskDetail", TaskDetail.toString());
                intent.putExtra("TaskComplete", true);
                intent.putExtra(Task.TASK_CLASS, TaskClass);
                context.startActivity(intent);
            } else {
                //如不需要评价 直接结束单
                onTaskCompleteListener.onTaskComplete();
            }
		} else if (TaskClass != null && TaskClass.equals(Task.TRANSFER_MODEL_TASK)) {
			Intent intent = new Intent(context, SummaryActivity.class);
			//intent.putExtra(Task.TASK_ID,TaskId);
			intent.putExtra("TaskDetail", TaskDetail.toString());
			intent.putExtra("TaskComplete", true);
			intent.putExtra(Task.TASK_CLASS, TaskClass);
			context.startActivity(intent);
        } else if (TaskClass != null && ModuleType.equals("Property") && DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.DIRECT_FINISH)).equals("1")) {
            //设施的报修完成直接结束,不需要评价 By Leo
            onTaskCompleteListener.onTaskComplete();
        }
        else  if(Task.Lend_TASK.contains(TaskClass)){
            //kingzhang for srf 2022-0106 设备借还直接结束 不需要评价
            onTaskCompleteListener.onTaskComplete();
        }
        else {
			Intent intent = new Intent(context, SubTaskManageActivity.class);
			//intent.putExtra(Task.TASK_ID,TaskId);
			intent.putExtra("TaskDetail", TaskDetail.toString());
			intent.putExtra("TaskComplete", true);
			intent.putExtra(Task.TASK_CLASS, TaskClass);
			context.startActivity(intent);
		}
    }


    private void StyleChangeTaskComplete() {
        LogUtils.e("进入StyleTaskComplete--->" + TaskClass);
        if (TaskClass != null && TaskClass.equals(Task.TRANSFER_TASK)) {
//			Intent intent=new Intent(context, TaskCompleteActivity.class);
//			intent.putExtra("TaskDetail", TaskDetail.toString());
//			intent.putExtra("TaskComplete", true);
//			intent.putExtra(Task.TASK_CLASS, TaskClass);
//			intent.putExtra("orderNo",OrderNo);
//			context.startActivity(intent);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(LocaleUtils.getI18nValue("IsCompleted"));
            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StylChangeOrderTaskCompelete();
                    dialog.dismiss();
                }
            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AddEquipmentDialog = builder.create();
            AddEquipmentDialog.show();
        }
    }

    /**
     * 转款专用完成任务的接口
     */
    private void StylChangeOrderTaskCompelete() {
        LogUtils.e("转款专门的取消任务接口---->" + DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set("taskId", DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        submitData.set("status", 99);
        submitData.set("quitReason", "");
        params.putJsonParams(submitData.toJson());
        LogUtils.e("转款取消单上传参数---->" + submitData.toString());
        HttpUtils.getChangeFormServer(context, "emms/transferTask/updateTransferTaskStatus", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("转款-完成单数据返回失败---->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"), context);
            }

            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                LogUtils.e("转款完成单的返回数据--->" + t);
                if (t != null) {
                    final JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    if (jsonObjectElement.get("Success") != null &&
                            jsonObjectElement.get("Success").valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("taskComplete"), context);
                        patchStyleChangeOrderStatus("11");
                        context.startActivity(new Intent(context, CusActivity.class));
                    } else {

                        if (DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()) {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("canNotSubmitTaskComplete"), context);
                        } else {
                            TipsUtil.ShowTips(context, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                        }
                    }
                }
            }
        });
    }

    private void patchStyleChangeOrderStatus(String status) {
        if (OrderNo.equals("")) {
            return;
        }
        HttpParams params = new HttpParams();
        HttpUtils.patchChangeStyle(context, "1.1/emms/order/" + OrderNo + "/" + status, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailPatchOrderStatus+errorNo"), context);
            }
        });
    }


    private void CreateTask() {
        Intent intent = new Intent(context, CreateTaskActivity.class);
        intent.putExtra("FromTask_ID", String.valueOf(TaskId));
        context.startActivity(intent);
    }

    private void ScanQRCode() {
        if (!hasEquipment) {
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("error_add_equipment"), context);
            return;
        }
        ((Activity) context).startActivityForResult(new Intent(context, CaptureActivity.class), Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY);
//		((Activity)context).startActivityForResult(new Intent(context, GoogleCaptureActivity.class),Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY);
    }

    private void Refresh() {
        if (onTaskDetailRefreshListener != null) {
            onTaskDetailRefreshListener.onRefresh();
        }
    }

    private void CancelStyleChangeTask() {
        cancelTaskDiglog();
    }

    private void CancelStyleChangeTaskOrder() {
        confirmToCancelOrderTask();
    }

    private void confirmToCancelOrderTask() {
        if (AddEquipmentDialog == null || !AddEquipmentDialog.isShowing()) {
            final String DialogMessage = LocaleUtils.getI18nValue("AreYouSureToCancelTaskOrder");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(DialogMessage);
            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TaskClass.equals("T08")) {
                                StylChangeOrderTask();
                            } else {
                                cancelOrderTask();
                            }

                        }
                    });

                }
            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AddEquipmentDialog = builder.create();
            AddEquipmentDialog.show();
        }
    }

    /**
     * 转款专用取消单的接口
     */
    private void StylChangeOrderTask() {
        LogUtils.e("转款专门的取消任务接口---->" + Task_ID);
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set("taskId", Task_ID);
        submitData.set("status", 1);
        submitData.set("quitReason", "");
        params.putJsonParams(submitData.toJson());
        LogUtils.e("转款取消单上传参数---->" + submitData.toString());
        HttpUtils.getChangeFormServer(context, "emms/transferTask/updateTransferTaskStatus", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("转款-取消单数据返回失败---->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailDeReceiveTaskByNetWork"), context);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtils.e("转款取消单的返回数据--->" + t);
                if (t != null) {
                    JsonObjectElement returnData = new JsonObjectElement(t);
                    if (returnData.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessDeReceiveTask"), context);
                        context.startActivity(new Intent(context, CusActivity.class));
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailDeReceiveTask"), context);
                    }
                }
            }
        });
    }

    private void cancelOrderTask() {
        LogUtils.e("进入转款取消单状态--->" + TaskId);
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set(Task.TASK_ID, TaskId.toString());
        params.putJsonParams(submitData.toJson());
        LogUtils.e("转款取消单的上传数据--->" + submitData.toString());
        HttpUtils.post(context, "TaskAPI/DeReceiveTask?task_id=" + TaskId.toString(), params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailDeReceiveTaskByNetWork"), context);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtils.e("转款取消单的返回数据--->" + t);
                if (t != null) {
                    JsonObjectElement returnData = new JsonObjectElement(t);
                    if (returnData.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessDeReceiveTask"), context);
                        context.startActivity(new Intent(context, CusActivity.class));
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailDeReceiveTask"), context);
                    }
                }
            }
        });
    }

    public void setTaskComplete(boolean taskComplete) {
        this.taskComplete = taskComplete;
    }

    public interface OnTaskDetailRefreshListener {
        void onRefresh();
    }

    public OnTaskDetailRefreshListener getOnTaskDetailRefreshListener() {
        return onTaskDetailRefreshListener;
    }

    public void setOnTaskDetailRefreshListener(OnTaskDetailRefreshListener onTaskDetailRefreshListener) {
        this.onTaskDetailRefreshListener = onTaskDetailRefreshListener;
    }

    private void cancelTaskDiglog() {
        CancelTaskDialog cancleTaskDialog = new CancelTaskDialog(this.context);
        cancleTaskDialog.setDialogTitle(LocaleUtils.getI18nValue("cancleTask"));
        cancleTaskDialog.setTaskCancelListener(new TaskCancelListener() {
            @Override
            public void submitCancel(String CancelReason) {
                if (TaskClass.equals("T08")) {
                    StyleChangeCancelTask(Task_ID, CancelReason);
                } else {
                    CancelTask(TaskId.toString(), CancelReason);
                }

            }
        });
        cancleTaskDialog.show();
    }

    /**
     * 转款专门的取消任务接口
     *
     * @param task_ID
     * @param reason
     */
    private void StyleChangeCancelTask(String task_ID, final String reason) {

        LogUtils.e("转款专门的取消任务接口---->" + task_ID);
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set("taskId", task_ID);
        submitData.set("status", 0);
        submitData.set("quitReason", reason);
        params.putJsonParams(submitData.toJson());
        LogUtils.e("转款取消任务上传参数---->" + submitData.toString());
        HttpUtils.getChangeFormServer(context, "emms/transferTask/updateTransferTaskStatus", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                LogUtils.e("转款-取消任务的返回失败参数--->" + errorNo + "---->" + strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTaskCauseByNetWork"), context);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtils.e("取消任务的返回参数--->" + t);
                if (t != null) {
                    JsonObjectElement returnData = new JsonObjectElement(t);
                    if (returnData.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessCancelTask"), context);
                        context.startActivity(new Intent(context, CusActivity.class));
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTask"), context);
                    }
                }
            }
        });
    }

    private void CancelTask(String taskId, final String reason) {
        LogUtils.e("取消任务--->" + taskId);
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set(Task.TASK_ID, taskId);
        submitData.set("QuitReason", reason);
        params.putJsonParams(submitData.toJson());
        LogUtils.e("取消任务的上传参数--->" + submitData.toString());
        HttpUtils.post(context, "TaskAPI/TaskQuit", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTaskCauseByNetWork"), context);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtils.e("取消任务的返回参数--->" + t);
                if (t != null) {
                    JsonObjectElement returnData = new JsonObjectElement(t);
                    if (returnData.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessCancelTask"), context);
                        context.startActivity(new Intent(context, CusActivity.class));
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailCancelTask"), context);
                    }
                }
            }
        });
    }

    //创建备件申请单
    private void SparePartRequestCreate() {
        if (AddEquipmentDialog == null || !AddEquipmentDialog.isShowing()) {
            final String DialogMessage = LocaleUtils.getI18nValue("continue_to_create_spare_parts_requisition");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(DialogMessage);
            builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context,SparePartRequestActivity.class);
                    intent.putExtra(Task.TASK_CLASS,Task.SPAREPART_REQUEST);
                    intent.putExtra(Task.TASK_ID, TaskId);
                    context.startActivity(intent);
                }
            }).setNegativeButton(LocaleUtils.getI18nValue("cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AddEquipmentDialog = builder.create();
            AddEquipmentDialog.show();
        }
    }

    //查看历史申请单
    private void SparePartRequestViewHistorical(){
        Intent intent = new Intent(context,SparePartRequestListActivity.class);
        context.startActivity(intent);
    }

    //查看剩余备件
    private void SparePartRequestViewSurplus(){
        Intent intent = new Intent(context, SparePartListViewActivity.class);
        intent.putExtra(Task.TASK_CLASS,Task.SPAREPART_SURPLUS);
        context.startActivity(intent);
    }

    private OnTaskDetailRefreshListener onTaskDetailRefreshListener;

    public interface OnTaskCompleteListener {
        void onTaskComplete();
    }

    public OnTaskCompleteListener getOnTaskCompleteListener() {
        return onTaskCompleteListener;
    }

    public void setOnTaskCompleteListener(OnTaskCompleteListener onTaskCompleteListener) {
        this.onTaskCompleteListener = onTaskCompleteListener;
    }

    private OnTaskCompleteListener onTaskCompleteListener;
}
