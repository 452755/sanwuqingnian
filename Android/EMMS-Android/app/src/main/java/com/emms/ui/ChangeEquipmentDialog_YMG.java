package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.SparePartActivity;
import com.emms.activity.SparePartListViewActivity;
import com.emms.activity.dialogOnSubmitInterface;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class ChangeEquipmentDialog_YMG extends Dialog implements View.OnClickListener {
    private Context context;
    private String TaskId;
    private String EquipmentId;
    private String TaskEquipmentId;
    private KProgressHUD hud;

    public void setEquipemntStatus(int equipemntStatus) {
        EquipemntStatus = equipemntStatus;
    }

    public final static String DELETE = "delete";
    private int EquipemntStatus = 0;
    //private ArrayList<String> status=new ArrayList<String>();
    //private int tag=1;
    private Button change_equipment_operator_status, change_equipment_status;
    //   private Button equipment_resume,equipment_complete,equipment_wait_material,equipment_pause,equipment_start,quit,material_requisition;
    private ObjectElement TaskEquipmentData;
    private boolean is_Main_person_in_charge_operator_id = false;
    private boolean isNoEuqipment = false;

    private int Operator_Status = -1;

    //    public void setEquipment_Status(int equipment_Status) {
//        Equipment_Status = equipment_Status;
//    }
//
//    private int Equipment_Status=-1;
//    private ArrayWheelAdapter<String> adapter;
    private ArrayList<ObjectElement> Equipment_Status_List = new ArrayList<>();
    private ArrayList<ObjectElement> Equipment_Operator_Status_List = new ArrayList<>();
    private ArrayList<ObjectElement> showList = new ArrayList<>();
    private HashMap<String, Integer> Equipment_Operator_Status_Name_ID_map = new HashMap<>();
    private HashMap<String, Integer> Equipment_Status_Name_ID_map = new HashMap<>();
    private int ViewTag = 1;
    private boolean isMaintainTask = false;
    private int OperatorStatus = 0;
    private String moduleType = "";
    private String TaskClass;
    private String TaskSubClass;

    public ChangeEquipmentDialog_YMG(Context context, int layout, int style, boolean tag, boolean tag2, boolean tag3, boolean tag4, String Equipment_num, int OperatorStatu, int EquipmentS, String ModuleType,String TaskClass,String TaskSubClass) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        hud = KProgressHUD.create(context);
        is_Main_person_in_charge_operator_id = tag;
        isOneOperator = tag2;
        isNoEuqipment = tag3;
        isMaintainTask = tag4;
        OperatorStatus = OperatorStatu;
        EquipemntStatus = EquipmentS;
        moduleType = ModuleType;
        this.TaskClass = TaskClass;
        this.TaskSubClass = TaskSubClass;
        //if(Equipment_OperatorID_Status.get())
        //  Collections.addAll(status,context.getResources().getStringArray(R.array.equip_status));
        if (Equipment_num != null) {
            findViewById(R.id.Equipment_info_layout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.Equipment_info)).setText((moduleType.equals("Property") ? LocaleUtils.getI18nValue("facility_Num") : LocaleUtils.getI18nValue("device_num")) + Equipment_num);
        }
        initMap();
        initData();
        initview();
        setCanceledOnTouchOutside(false);

    }

    public void setOnSubmitInterface(dialogOnSubmitInterface onSubmitInterface) {
        this.onSubmitInterface = onSubmitInterface;
    }

    private dialogOnSubmitInterface onSubmitInterface = null;

    public EquipmentCompleteListener getEquipmentCompleteListener() {
        return equipmentCompleteListener;
    }

    public void setEquipmentCompleteListener(EquipmentCompleteListener equipmentCompleteListener) {
        this.equipmentCompleteListener = equipmentCompleteListener;
    }

    private EquipmentCompleteListener equipmentCompleteListener = null;

    public void initview() {
        try {
            LogUtils.e("进入ChangeEquipmentDialog--->" + EquipemntStatus);
            ((TextView) findViewById(R.id.cancle)).setText(LocaleUtils.getI18nValue("cancel"));
            findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            ListView listView = (ListView) findViewById(R.id.listView);
            TaskAdapter adapter = new TaskAdapter(showList) {
                @Override
                public View getCustomView(View convertView, int position, ViewGroup parent) {
                    TaskViewHolder holder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.dialog_item, parent, false);
                        holder = new TaskViewHolder();
                        holder.image = (ImageView) convertView.findViewById(R.id.image);
                        holder.tv_task_state = (TextView) convertView.findViewById(R.id.status);
                        convertView.setTag(holder);
                    } else {
                        holder = (TaskViewHolder) convertView.getTag();
                    }
                    LogUtils.e("改状态对话框---->" + DataUtil.isDataElementNull(showList.get(position).get("Type")));
                    // if(!showList.get(position).get("Type").valueAsString().equals("delete")){
                    if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentStatus")) {
                        holder.image.setImageResource(R.mipmap.equipment_status);
                    } else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentOperatorStatus")) {
                        holder.image.setImageResource(R.mipmap.equipment_operator_status_mipmap);
                    } else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("ChooseOrReplaceSparePart")){
                        holder.image.setImageResource(R.mipmap.equipment_operator_status_mipmap);
                    } else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("UsedSparePart")){
                        holder.image.setImageResource(R.mipmap.equipment_operator_status_mipmap);
                    }else {
                        holder.image.setImageResource(R.mipmap.delete_equipment);
                    }
                    holder.image.setVisibility(View.VISIBLE);
                    holder.tv_task_state.setVisibility(View.VISIBLE);
                    holder.tv_task_state.setText(DataUtil.isDataElementNull(showList.get(position).get("Status")));
                    return convertView;
                }
            };
            listView.setAdapter(adapter);
            adapter.setDatas(showList);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //修改设备状态
                    if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentStatus")) {
                        postTaskEquipment(Equipment_Status_Name_ID_map.get(DataUtil.isDataElementNull(showList.get(position).get("Status"))));
                    } else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("ChooseOrReplaceSparePart")){
                        //挑选或替换备件
                        dismiss();
                        Intent usedSpaerPart = new Intent(context, SparePartActivity.class);
                        usedSpaerPart.putExtra("TaskID",TaskId);
                        usedSpaerPart.putExtra("EquipmentId",EquipmentId);
                        usedSpaerPart.putExtra("TaskEquipmentId",TaskEquipmentId);
                        usedSpaerPart.putExtra(Task.TASK_CLASS,Task.SPAREPART_EQUIPMENT_CHOOSE);
                        context.startActivity(usedSpaerPart);
                    } else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("UsedSparePart")){
                        dismiss();
                        //查看已使用备件
                        Intent usedSpaerPart = new Intent(context, SparePartListViewActivity.class);
                        usedSpaerPart.putExtra("TaskID",TaskId);
                        usedSpaerPart.putExtra("EquipmentId",EquipmentId);
                        usedSpaerPart.putExtra("TaskEquipmentId",TaskEquipmentId);
                        usedSpaerPart.putExtra(Task.TASK_CLASS,Task.SPAREPART_EQUIPMENT_USED);
                        context.startActivity(usedSpaerPart);
                    }else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentOperatorStatus")) {
                        //修改设备参与人状态
                        if (isNoEuqipment) {
                            ChangeTaskOperatorStatus(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(showList.get(position).get("Status"))));
                        } else {
                            postTaskOperatorEquipment(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(showList.get(position).get("Status"))));
                        }
                    } else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals(DELETE)) {
//                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
//                    builder.setMessage(LocaleUtils.getI18nValue("sureDeleteEquipment"));
//                    builder.setPositiveButton(LocaleUtils.getI18nValue("sure"), new OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            deleteEquipment();
//                            dialog.dismiss();
//                        }
//                    });
//                    builder.show();
                        CancelTaskDialog cancleTaskDialog = new CancelTaskDialog(context);
                        cancleTaskDialog.setDialogTitle(LocaleUtils.getI18nValue("deleteEquipment"));
                        cancleTaskDialog.setHint(LocaleUtils.getI18nValue("pleaseInputDeleteEquipmentReason"));
                        cancleTaskDialog.setTaskCancelListener(new TaskCancelListener() {
                            @Override
                            public void submitCancel(String CancelReason) {
                                deleteEquipment(CancelReason);
                            }
                        });
                        cancleTaskDialog.show();
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.e("报错了---->" + e);
            CrashReport.postCatchedException(e);
        }
    }


    private void postTaskEquipment(final int status) {
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();

        JsonObjectElement taskEquepment = new JsonObjectElement();
//创建任务提交数据：任务创建人，任务类型“T01”那些，几台号（数组），
        taskEquepment.set(Task.TASK_ID, TaskId);
        //若任务未有设备，则输入为0，表示添加
        taskEquepment.set("TaskEquipment_ID", TaskEquipmentId);
        //若已有设备，申请状态变更
        taskEquepment.set("Equipment_ID", EquipmentId);
        //taskEquepment.set("Equipment_ID", equipmentID);
        taskEquepment.set("Status", status);

        params.putJsonParams(taskEquepment.toJson());

        HttpUtils.post(context, "TaskEquipmentAPI/ModifyTaskEquipmentStatus", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    if (jsonObjectElement.get("Success") != null && jsonObjectElement.get("Success").valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("SuccessChangeStatus"), context);
                        dismiss();
                        onSubmitInterface.onsubmit();
                        if (status == 2 && equipmentCompleteListener != null) {
                            equipmentCompleteListener.EquipmentComplete(true);//设备状态修改为完成的情况下回调
                        }
                    } else {
                        if (DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).equals("")) {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailChangeEquipmentStatusCauseByOperator"), context);
                        } else {
                            TipsUtil.ShowTips(context, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                        }
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailChangeEquipmentStatusCauseByTimeOut"), context);
                dismissCustomDialog();
            }
        });
    }

    public ObjectElement getTaskEquipmentData() {
        return TaskEquipmentData;
    }

    public void setTaskEquipmentData(ObjectElement taskEquipmentData) {
        TaskEquipmentData = taskEquipmentData;
    }

    public void setDatas(String taskId, String equipmentId, String taskEquipmentId) {
        this.TaskId = taskId;
        this.EquipmentId = equipmentId;
        this.TaskEquipmentId = taskEquipmentId;
    }

    public void setMainPersonInChargeOperatorId(boolean is_Main_person_in_charge_operator_id) {
        this.is_Main_person_in_charge_operator_id = is_Main_person_in_charge_operator_id;
    }

    /*
    private void initEquipmentTagView(){
        equipment_resume=(Button)findViewById(R.id.equipment_resume);
        equipment_complete=(Button)findViewById(R.id.equipment_complete);
        equipment_wait_material=(Button)findViewById(R.id.equipment_wait_material);
        equipment_pause=(Button)findViewById(R.id.equipment_pause);
        equipment_start=(Button)findViewById(R.id.equipment_start);
        quit=(Button)findViewById(R.id.quit);
        material_requisition=(Button)findViewById(R.id.material_requisition);
        equipment_resume.setOnClickListener(this);
        equipment_complete.setOnClickListener(this);
        equipment_wait_material.setOnClickListener(this);
        equipment_pause.setOnClickListener(this);
        equipment_start.setOnClickListener(this);
        quit.setOnClickListener(this);
        material_requisition.setOnClickListener(this);
    }
    private void TagView(int tag){
        if(tag==1){
            equipment_resume.setVisibility(View.VISIBLE);
            equipment_complete.setVisibility(View.VISIBLE);
            equipment_wait_material.setVisibility(View.VISIBLE);
            equipment_pause.setVisibility(View.VISIBLE);
            equipment_start.setVisibility(View.VISIBLE);
            quit.setVisibility(View.VISIBLE);
            material_requisition.setVisibility(View.VISIBLE);
        }
        else{
            equipment_resume.setVisibility(View.VISIBLE);
            equipment_complete.setVisibility(View.VISIBLE);
            equipment_wait_material.setVisibility(View.VISIBLE);
            equipment_pause.setVisibility(View.VISIBLE);
            equipment_start.setVisibility(View.VISIBLE);
            quit.setVisibility(View.GONE);
            material_requisition.setVisibility(View.GONE);
        }
    }
*/
    @Override
    public void onClick(View v) {
        // int id = v.getId();
    }

    private void initTagButton() {
        change_equipment_operator_status = (Button) findViewById(R.id.change_equipment_operator_status);
        // change_equipment_operator_status.setBackgroundColor(Color.RED);
        change_equipment_operator_status.setTextColor(Color.parseColor("#C4647C"));
        change_equipment_operator_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewTag = 1;

                // change_equipment_status.setBackgroundColor(Color.WHITE);
                //  change_equipment_operator_status.setBackgroundColor(Color.RED);
                change_equipment_status.setTextColor(Color.parseColor("#D2D2D2"));
                change_equipment_operator_status.setTextColor(Color.parseColor("#C4647C"));
            }
        });
        change_equipment_status = (Button) findViewById(R.id.change_equipment_status);
        change_equipment_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_Main_person_in_charge_operator_id) {
                    ViewTag = 2;
                    //change_equipment_operator_status.setBackgroundColor(Color.WHITE);
                    //  change_equipment_status.setBackgroundColor(Color.RED);
                    change_equipment_operator_status.setTextColor(Color.parseColor("#D2D2D2"));
                    change_equipment_status.setTextColor(Color.parseColor("#C4647C"));
                } else {
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("onlyTaskChargerCanChangeEquipmentStatus"), context);
                }
            }
        });
    }

    private void postTaskOperatorEquipment(final int status) {
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        // JsonObjectElement TaskOperatorDataToSubmit=new JsonObjectElement();
        //   TaskOperatorDataToSubmit.set("task_id",Integer.valueOf(TaskId));
        //   TaskOperatorDataToSubmit.set("equipment_id",Integer.valueOf(EquipmentId));
        //   TaskOperatorDataToSubmit.set("TaskEquipment_ID",Integer.valueOf(TaskEquipmentId));
        //   TaskOperatorDataToSubmit.set("status",status);
        //   params.putJsonParams(TaskOperatorDataToSubmit.toJson());
        HttpUtils.post(context, "TaskOperatorAPI/MotifyTaskOperatorStatus?task_id=" + TaskId + "&equipment_id=" + EquipmentId + "&status=" + status,
                params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        if (t != null) {
                            JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                            if (jsonObjectElement.get("Success").valueAsBoolean()) {
                                onSubmitInterface.onsubmit();
                                if (status == 1 && isOneOperator && equipmentCompleteListener != null) {//设备状态修改为完成的情况下回调
                                    equipmentCompleteListener.EquipmentComplete(true);
                                }
                                dismiss();
                            } else {
                                if (DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).equals("")) {
                                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("CanNotChangeStatus"), context);
                                } else {
                                    TipsUtil.ShowTips(context, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                }
                            }
                        }
                        dismissCustomDialog();
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("failToChangeStatus"), context);
                        dismissCustomDialog();
                    }
                });
    }

    private void StatusControl(int Status) {

    }

    public void initData() {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        String[] equipmentStatus = {LocaleUtils.getI18nValue("equipment_status_in_progress"), LocaleUtils.getI18nValue("equipment_status_suspend"), LocaleUtils.getI18nValue("equipment_status_complete"),LocaleUtils.getI18nValue("spare_parts_selection_or_replacement"),LocaleUtils.getI18nValue("used_spare_part")};
        String[] equipmentOperator = {LocaleUtils.getI18nValue("equipment_operator_status_pending"), LocaleUtils.getI18nValue("equipment_operator_status_suspend"), LocaleUtils.getI18nValue("equipment_operator_status_complete"),LocaleUtils.getI18nValue("spare_parts_selection_or_replacement"),LocaleUtils.getI18nValue("used_spare_part")};
        Collections.addAll(arrayList, equipmentStatus);
        Collections.addAll(list, equipmentOperator);

//        for(String ss:list){
//            JsonObjectElement json=new JsonObjectElement();
//            json.set("Status",ss);
//            json.set("Type","EquipmentOperatorStatus");
//            Equipment_Operator_Status_List.add(json);
//        }
        //人员状态开始-0，完成-1 ,暂停-2
        if (OperatorStatus == 2) {//当前人员状态为暂停
            //开始选项
            JsonObjectElement begin = new JsonObjectElement();
            begin.set("Status", list.get(0));
            begin.set("Type", "EquipmentOperatorStatus");
            Equipment_Operator_Status_List.add(begin);
        } else if (OperatorStatus == 1) {
            //do not show OperatorStatus
            //abrahamguo 2022-02-09 当当前操作员状态为完成时，并且当前COMPLETEMENUISSHOW为1时，添加使用物料和已使用物料选项
            if((TaskClass.equals(Task.REPAIR_TASK)||(TaskClass.equals(Task.MAINTAIN_TASK)&&TaskSubClass.equals(Task.UPKEEP)))
                    &&DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.COMPLETEMENUISSHOW)).equals("1")){
                //挑选/替换备件选项
                JsonObjectElement chooseOrReplace = new JsonObjectElement();
                chooseOrReplace.set("Status",list.get(3));
                chooseOrReplace.set("Type", "ChooseOrReplaceSparePart");
                Equipment_Operator_Status_List.add(chooseOrReplace);
                //查看已使用备件
                JsonObjectElement usedSparePart = new JsonObjectElement();
                usedSparePart.set("Status",list.get(4));
                usedSparePart.set("Type", "UsedSparePart");
                Equipment_Operator_Status_List.add(usedSparePart);
            }
        } else {//当前人员状态为开始
            if((TaskClass.equals(Task.REPAIR_TASK)||(TaskClass.equals(Task.MAINTAIN_TASK)&&TaskSubClass.equals(Task.UPKEEP)))
                    &&!DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.COMPLETEMENUISSHOW)).equals("1")){
                //挑选/替换备件选项
                JsonObjectElement chooseOrReplace = new JsonObjectElement();
                chooseOrReplace.set("Status",list.get(3));
                chooseOrReplace.set("Type", "ChooseOrReplaceSparePart");
                Equipment_Operator_Status_List.add(chooseOrReplace);
                //查看已使用备件
                JsonObjectElement usedSparePart = new JsonObjectElement();
                usedSparePart.set("Status",list.get(4));
                usedSparePart.set("Type", "UsedSparePart");
                Equipment_Operator_Status_List.add(usedSparePart);
            }
            //暂停选项
            JsonObjectElement pause = new JsonObjectElement();
            pause.set("Status", list.get(1));
            pause.set("Type", "EquipmentOperatorStatus");
            Equipment_Operator_Status_List.add(pause);
            //完成选项
            JsonObjectElement complete = new JsonObjectElement();
            complete.set("Status", list.get(2));
            complete.set("Type", "EquipmentOperatorStatus");
            Equipment_Operator_Status_List.add(complete);
        }
/*
        if( (is_Main_person_in_charge_operator_id&&!isOneOperator)
                ||( is_Main_person_in_charge_operator_id&&isOneOperator&&OperatorStatus==1)   ){
            //设备状态开始-1，完成-2，暂停-3
            if(EquipemntStatus==3){//当前设备状态为暂停
                JsonObjectElement EquipmentStart=new JsonObjectElement();
                EquipmentStart.set("Status",arrayList.get(0));
                EquipmentStart.set("Type","EquipmentStatus");
                Equipment_Status_List.add(EquipmentStart);
            }else {//当前设备状态为开始
                JsonObjectElement Equipmentstop=new JsonObjectElement();
                Equipmentstop.set("Status",arrayList.get(1));
                Equipmentstop.set("Type","EquipmentStatus");
                Equipment_Status_List.add(Equipmentstop);
                JsonObjectElement EquipmentComplete=new JsonObjectElement();
                EquipmentComplete.set("Status",arrayList.get(2));
                EquipmentComplete.set("Type","EquipmentStatus");
                Equipment_Status_List.add(EquipmentComplete);
            }



        showList.addAll(Equipment_Status_List);
        }
*/
        showList.addAll(Equipment_Operator_Status_List);
        if (isOneOperator && !isNoEuqipment && !isMaintainTask) {//单人操作
            JsonObjectElement json = new JsonObjectElement();
            json.set("Status", moduleType.equals("Property") ? LocaleUtils.getI18nValue("deleteFacility") : LocaleUtils.getI18nValue("deleteEquipment"));
//            json.set("Status", LocaleUtils.getI18nValue("deleteEquipment"));
            json.set("Type", DELETE);
            showList.add(0, json);
        } else if (is_Main_person_in_charge_operator_id && !isOneOperator && !isNoEuqipment && !isMaintainTask) {//多人操作，主负责人控制
            JsonObjectElement json = new JsonObjectElement();
            json.set("Status", moduleType.equals("Property") ? LocaleUtils.getI18nValue("deleteFacility") : LocaleUtils.getI18nValue("deleteEquipment"));
//            json.set("Status", LocaleUtils.getI18nValue("deleteEquipment"));
            json.set("Type", DELETE);
            showList.add(0, json);
        } else {
            if (BaseData.getConfigData().get(BaseData.MAINTAIN_TASK_DELETEMACHINE) != null) {
//                String value = "1";
                String value = DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.MAINTAIN_TASK_DELETEMACHINE));
                LogUtils.e("value---->" + value);
                if (value.equals("1")) {
                    JsonObjectElement json = new JsonObjectElement();
                    json.set("Status", moduleType.equals("Property") ? LocaleUtils.getI18nValue("deleteFacility") : LocaleUtils.getI18nValue("deleteEquipment"));
                    json.set("Type", DELETE);
                    showList.add(0, json);
                }
            }
        }

        //abrahamguo 2022-02-09 当当前操作员状态为完成时，并且当前COMPLETEMENUISSHOW为1时，移除删除设备选项
        if (OperatorStatus == 1) {
            if((TaskClass.equals(Task.REPAIR_TASK)||(TaskClass.equals(Task.MAINTAIN_TASK)&&TaskSubClass.equals(Task.UPKEEP)))
                    &&DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.COMPLETEMENUISSHOW)).equals("1")){
                showList.remove(0);
            }
        }
        // adapter.notifyDataSetChanged();
    }

    private void initMap() {
        Equipment_Operator_Status_Name_ID_map.put(LocaleUtils.getI18nValue("equipment_operator_status_pending"), 0);
        Equipment_Operator_Status_Name_ID_map.put(LocaleUtils.getI18nValue("equipment_operator_status_suspend"), 2);
//            Equipment_Operator_Status_Name_ID_map.put( LocaleUtils.getI18nValue("quit"), 2);
//            Equipment_Operator_Status_Name_ID_map.put( LocaleUtils.getI18nValue("material_requisition"), 3);
//            Equipment_Operator_Status_Name_ID_map.put( LocaleUtils.getI18nValue("wait_material"), 4);
        Equipment_Operator_Status_Name_ID_map.put(LocaleUtils.getI18nValue("equipment_operator_status_complete"), 1);
/*
            Equipment_Status_Name_ID_map.put( LocaleUtils.getI18nValue("equipment_status_in_progress"), 1);
            Equipment_Status_Name_ID_map.put( LocaleUtils.getI18nValue("equipment_status_suspend"), 3);
//            Equipment_Status_Name_ID_map.put( LocaleUtils.getI18nValue("wait_material"), 3);
            Equipment_Status_Name_ID_map.put( LocaleUtils.getI18nValue("equipment_status_complete"), 2);
*/
    }

    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            Log.w("", e);
            CrashReport.postCatchedException(e);
        }

        return hasNavigationBar;

    }

    //获取NavigationBar的高度：
    private static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    public KProgressHUD initCustomDialog(String label) {
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setCancellable(true);
        return hud;
    }

    public void showCustomDialog(String label) {
        initCustomDialog(label);
        if (hud != null && !hud.isShowing()) {
            hud.show();
        }
    }

    public void dismissCustomDialog() {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
    }

    public void deleteEquipment(String CancelReason) {
        showCustomDialog(LocaleUtils.getI18nValue("deletingEquipment"));
        HttpParams params = new HttpParams();
        //IDList即TaskEquipmentId对应删除字段
        //params.put("id",TaskEquipmentId);
        JsonObjectElement jsonObjectElement = new JsonObjectElement();
        jsonObjectElement.set("IDList", TaskEquipmentId);
        jsonObjectElement.set("DeleteReason", CancelReason);
        params.putJsonParams(jsonObjectElement.toJson());
        HttpUtils.post(context, "TaskEquipmentAPI/TaskEquipmentDelete", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("submitFail"), context);
                dismissCustomDialog();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement json = new JsonObjectElement(t);
                    if (json.get(Data.SUCCESS).valueAsBoolean()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("deleteEquipmentSuccess"), context);
                        onSubmitInterface.onsubmit();
                        dismiss();
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("deleteEquipmentFail"), context);
                    }
                }
                dismissCustomDialog();
            }
        });
    }

    public void setOneOperator(boolean oneOperator) {
        isOneOperator = oneOperator;
    }

    private boolean isOneOperator = false;

    public void setTaskOperatorID(int taskOperatorID) {
        TaskOperatorID = taskOperatorID;
    }

    private int TaskOperatorID = 0;

    private void ChangeTaskOperatorStatus(int Status) {
        showCustomDialog(LocaleUtils.getI18nValue("submitData"));
        HttpParams params = new HttpParams();
        JsonObjectElement submitData = new JsonObjectElement();
        submitData.set("TaskOperator_ID", TaskOperatorID);
        submitData.set("Status", Status);
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(context, "TaskOperatorAPI/MotifyTaskOperatorStatusForSimple", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement data = new JsonObjectElement(t);
                    if (data.get(Data.SUCCESS).valueAsBoolean()) {
                        onSubmitInterface.onsubmit();
                        dismiss();
                    } else {
                        if (DataUtil.isDataElementNull(data.get("Msg")).equals("")) {
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("CanNotChangeStatus"), context);
                        } else {
                            TipsUtil.ShowTips(context, DataUtil.isDataElementNull(data.get("Msg")));
                        }
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(LocaleUtils.getI18nValue("failToChangeStatus"), context);
                dismissCustomDialog();
            }
        });
    }
}