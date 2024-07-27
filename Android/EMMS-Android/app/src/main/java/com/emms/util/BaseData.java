package com.emms.util;

import android.app.Activity;
import android.content.Context;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.text.TextUtils;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Factory;
import com.emms.schema.System_FunctionSetting;
import com.tencent.bugly.crashreport.CrashReport;


import java.io.PipedReader;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/24.
 * This Util is used to keep base data.
 */
public class BaseData {
    private BaseData() {
        //no instance
    }

    public static HashMap<String, String> TaskClass = new HashMap<>();

    public static HashMap<String, String> TaskStatus = new HashMap<>();

    public static HashMap<String, String> CheckStatus = new HashMap<>();

    public static HashMap<String, String> getCheckStatus() {
        return CheckStatus;
    }

    public static void setCheckStatusByServe(final Context context) {
        HttpParams params = new HttpParams();
//        JsonObjectElement checkJson =  new JsonObjectElement();
//        checkJson.set("DataType","TaskClass");
//        params.putJsonParams(checkJson.toJson());
//        params.put("Parameter","filter=DataType eq 'TaskClass' and factory_id eq 'GEW'");

        HttpUtils.post(context, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'TaskStatus'%20and%20factory_id%20eq%20'" + SharedPreferenceManager.getFactory(context) + "'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t) || t.equals("null")) {
                    LogUtils.e("setCheckStatusByServe--->t为空");
                    return;
                }
                LogUtils.e("setCheckStatusByServe--测试成功--->" + t);
                DataElement jsonArrayElment = new JsonArrayElement(t);
                for (int i = 0; i < jsonArrayElment.asArrayElement().size(); i++) {
                    //翻译DATA_NAME
                    jsonArrayElment.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                    CheckStatus.put(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                            DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                }
                if (baseDataListener != null) {
                    baseDataListener.GetBaseDataSuccess();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                HttpUtils.tips(context, errorNo + "strMsg-->" + strMsg);
                if (baseDataListener != null) {
                    baseDataListener.GetBaseDataFail();
                }
                LogUtils.e("setCheckStatusByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });
    }

    public static void setCheckStatus(Context context) {
        setCheckStatusByServe(context);
//        DataUtil.getDataFromDataBase(context, "CheckStatus", new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                for(int i=0;i<element.asArrayElement().size();i++) {
//                    //翻译DATA_NAME
//                    element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
//                    CheckStatus.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
//                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
//                }
//                if(baseDataListener!=null){
//                    baseDataListener.GetBaseDataSuccess();
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                if(baseDataListener!=null){
//                    baseDataListener.GetBaseDataFail();
//                }
//            }
//        });
    }


    public static HashMap<String, String> getTaskStatus() {
        return TaskStatus;
    }

    /**
     * create by jason
     *
     * @param context
     */
    public static void setTaskStatusByServe(final Context context) {
        HttpParams params = new HttpParams();
//        JsonObjectElement checkJson =  new JsonObjectElement();
//        checkJson.set("DataType","TaskClass");
//        params.putJsonParams(checkJson.toJson());
//        params.put("Parameter","filter=DataType eq 'TaskClass' and factory_id eq 'GEW'");
        LogUtils.e("正在请求数据--->");
        HttpUtils.post(context, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'TaskStatus'%20and%20factory_id%20eq%20'" + SharedPreferenceManager.getFactory(context) + "'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                try {
                    if (TextUtils.isEmpty(t) || t.equals("null")) {
                        LogUtils.e("setTaskStatusByServe--->t为空");
                        return;
                    }
                    LogUtils.e("setTaskStatusByServe--测试成功--->" + t);
                    DataElement jsonArrayElment = new JsonArrayElement(t);
                    for (int i = 0; i < jsonArrayElment.asArrayElement().size(); i++) {
                        //翻译DATA_NAME
                        jsonArrayElment.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                        TaskStatus.put(DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                                DataUtil.isDataElementNull(jsonArrayElment.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                    }
                    if (baseDataListener != null) {
                        baseDataListener.GetBaseDataSuccess();
                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp"), context);
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                HttpUtils.tips(context, errorNo + "strMsg-->" + strMsg);
                if (baseDataListener != null) {
                    baseDataListener.GetBaseDataFail();
                }
                LogUtils.e("setTaskStatusByServe--测试失败--->" + errorNo + "----" + strMsg);
            }
        });
    }

    public static void setTaskStatus(final Context context) {
        setTaskStatusByServe(context);
//        DataUtil.getDataFromDataBase(context, "TaskStatus", new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                for(int i=0;i<element.asArrayElement().size();i++) {
//                    //翻译DATA_NAME
//                    element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
//                    TaskStatus.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
//                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
//                }
//                if(baseDataListener!=null){
//                    baseDataListener.GetBaseDataSuccess();
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                if(baseDataListener!=null){
//                    baseDataListener.GetBaseDataFail();
//                }
////                ((Activity)context).runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp,context);
////                    }
////                });
//            }
//        });
    }

    public static HashMap<String, String> getTaskClass() {
        return TaskClass;
    }


    private static void setTaskClassByServe(final Context context) {

        try {


            HttpParams params = new HttpParams();

//            params.put("DataType","TaskClass,TaskSubClass,PropertyFacilityTaskClass");
            params.put("DataType", "TaskClass,TaskSubClass");

            HttpUtils.get(context, "DataDictionary/DataDictionaryList", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    if (TextUtils.isEmpty(t) || t.equals("null")) {
                        LogUtils.e("setTaskClass--t为空");
                        return;
                    }
                    LogUtils.e("setTaskClass--测试成功--->" + t);
                    try {
                        DataElement jsonArrayElement = new JsonArrayElement(t);
                        for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                            //翻译DATA_NAME
                            jsonArrayElement.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
                            TaskClass.put(DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                                    DataUtil.isDataElementNull(jsonArrayElement.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                        }
                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                    }


                    if (baseDataListener != null) {
                        baseDataListener.GetBaseDataSuccess();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    if (baseDataListener != null) {
                        baseDataListener.GetBaseDataFail();
                    }
                    LogUtils.e("setTaskClass--测试失败--->" + errorNo + "--->" + strMsg);
                }
            });
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }


    public static void setTaskClass(final Context context) {
        setTaskClassByServe(context);
//        DataUtil.getDataFromDataBase(context, "TaskClass", 0, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                for(int i=0;i<element.asArrayElement().size();i++){
//                    //翻译DATA_NAME
//                    element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME, LocaleUtils.getI18nValue(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME))));
//                    TaskClass.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
//                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
//                }
//                if(baseDataListener!=null){
//                    baseDataListener.GetBaseDataSuccess();
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                if(baseDataListener!=null){
//                    baseDataListener.GetBaseDataFail();
//                }
////                ((Activity)context).runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("FailGetDataPleaseRestartApp,context);
////                    }
////                });
//            }
//        });
    }

    public static boolean setBaseData(Context context) {
        if (SharedPreferenceManager.getFactory(context) != null) {
            setConfigData(context, SharedPreferenceManager.getFactory(context));
        }
        if (SharedPreferenceManager.getLanguageChange(context)) {
            BaseData.setTaskClass(context);
            BaseData.setTaskStatus(context);
            BaseData.setCheckStatus(context);
            SharedPreferenceManager.setLanguageChange(context, false);
            return false;
        }
        if (BaseData.getTaskClass().size() <= 0 || BaseData.getTaskStatus().size() <= 0 || BaseData.getCheckStatus().size() <= 0) {
            if (BaseData.getTaskClass().size() <= 0) {
                BaseData.setTaskClass(context);
            }
            if (BaseData.getTaskStatus().size() <= 0) {
                BaseData.setTaskStatus(context);
            }
            if (BaseData.getCheckStatus().size() <= 0) {
                BaseData.setCheckStatus(context);
            }
            return false;
        }
        return true;
    }

    private interface BaseDataListener {
        void GetBaseDataSuccess();

        void GetBaseDataFail();
    }

    public BaseDataListener getBaseDataListener() {
        return baseDataListener;
    }

    public void setBaseDataListener(BaseDataListener baseDataListener) {
        this.baseDataListener = baseDataListener;
    }

    private static BaseDataListener baseDataListener;

    public static JsonObjectElement getConfigData() {
        return ConfigData;
    }

    /**
     * create by jason
     *
     * @param context
     * @param FromFactory
     */
    public static void setConfigDataByServe(Context context, final String FromFactory) {

        try {


            HttpParams params = new HttpParams();
            params.put("pageSize", 100);
            params.put("pageIndex", 1);
            HttpUtils.get(context, "System_FunctionSetting/GetSystemFunctionSettingInfo", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    if (t.equals("null") || TextUtils.isEmpty(t)) {
                        LogUtils.e("返回参数为空");
                        return;
                    }
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    DataElement jsonArrayElement = jsonObjectElement.get("PageData");
                    LogUtils.e("setConfigDataByServe---测试获取数据---->" + jsonArrayElement.toString());
                    if (jsonArrayElement != null && jsonArrayElement.isArray() && jsonArrayElement.asArrayElement().size() > 0) {
                        for (DataElement dataElement : jsonArrayElement.asArrayElement()) {
                            ConfigData.set(DataUtil.isDataElementNull(dataElement.asObjectElement().get(System_FunctionSetting.FUNCTION_CODE)),
                                    DataUtil.isDataElementNull(dataElement.asObjectElement().get(System_FunctionSetting.FUNCTION_VALUE)));
                        }
                    } else {
                        setConfigDataDefault(FromFactory);
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    setConfigDataDefault(FromFactory);
                }
            });

        } catch (Exception e) {
            setConfigDataDefault(FromFactory);
            CrashReport.postCatchedException(e);
        }
    }

    public static void setConfigData(Context context, final String FromFactory) {

        setConfigDataByServe(context, FromFactory);

//        DataUtil.getConfigurationData(context, FromFactory, new StoreCallback() {
//            @Override
//            public void success(DataElement element, String resource) {
//                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
//                    for(DataElement dataElement:element.asArrayElement()){
//                        ConfigData.set(DataUtil.isDataElementNull(dataElement.asObjectElement().get(System_FunctionSetting.FUNCTION_CODE)),
//                                DataUtil.isDataElementNull(dataElement.asObjectElement().get(System_FunctionSetting.FUNCTION_VALUE)));
//                    }
//                }else {
//                    setConfigDataDefault(FromFactory);
//                }
//            }
//
//            @Override
//            public void failure(DatastoreException ex, String resource) {
//                setConfigDataDefault(FromFactory);
//            }
//        });
    }

    public static void setConfigDataDefault(String FromFactory) {
        if (DataUtil.isDataElementNull(ConfigData.get(SIMPLE_DESCRIPTION_ACTION)).isEmpty()) {
            switch (FromFactory) {
                case Factory.FACTORY_EGM: {
                    ConfigData.set(SIMPLE_DESCRIPTION_ACTION, 1);//1为创建任务不强制需要任务简要描述
                    ConfigData.set(RECEIVE_TASK_ACTION, 1);//1为接单成功时跳转进任务详情
                    ConfigData.set(TASK_DETAIL_SHOW_WORKLOAD_ACTION, 1);//1为任务详情处不显示工作量块
                    ConfigData.set(TASK_COMPLETE_ACTION, 1);//1为任务完成试需要验证和评价
                    ConfigData.set(TASK_COMPLETE_SHOW_WORKLOAD_ACTION, 1);//1为任务完成流程不需要展现工作量录入界面
                    ConfigData.set(TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID, 1);//扫描二维码根据ICCardID字段获取设备信息
                    break;
                }
                case Factory.FACTORY_GEW: {
                    ConfigData.set(SIMPLE_DESCRIPTION_ACTION, 2);//1为创建任务强制需要任务简要描述
                    ConfigData.set(RECEIVE_TASK_ACTION, 2);//2为接单成功时不跳转进任务详情
                    ConfigData.set(TASK_DETAIL_SHOW_WORKLOAD_ACTION, 2);//2为任务详情处显示工作量块
                    ConfigData.set(TASK_COMPLETE_ACTION, 2);//2为任务完成试不需要验证和评价
                    ConfigData.set(TASK_COMPLETE_SHOW_WORKLOAD_ACTION, 2);//1为任务完成流程需要展现工作量录入界面
                    ConfigData.set(TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID, 2);//扫描二维码根据AssertsID字段获取设备信息
                    break;
                }
                default: {
                    ConfigData.set(SIMPLE_DESCRIPTION_ACTION, 2);
                    ConfigData.set(RECEIVE_TASK_ACTION, 2);
                    ConfigData.set(TASK_DETAIL_SHOW_WORKLOAD_ACTION, 2);
                    ConfigData.set(TASK_COMPLETE_ACTION, 2);
                    ConfigData.set(TASK_COMPLETE_SHOW_WORKLOAD_ACTION, 2);
                    ConfigData.set(TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID, 2);
                    break;
                }
            }
        }
    }

    private static final JsonObjectElement ConfigData = new JsonObjectElement();
    public static final String SIMPLE_DESCRIPTION_ACTION = "SimpleDescriptionAction";
    public static final String RECEIVE_TASK_ACTION = "ReceiveTaskAction";
    public static final String TASK_DETAIL_SHOW_WORKLOAD_ACTION = "TaskDetailShowWorkloadAction";
    public static final String TASK_COMPLETE_ACTION = "TaskCompleteAction";
    public static final String TASK_COMPLETE_SHOW_WORKLOAD_ACTION = "TaskCompleteShowWorkloadAction";
    public static final String TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID = "TaskGetEquipmentDataFromICCardID";
    public static final String MAINTAIN_TASK_DELETEMACHINE = "MainTainTaskDeleteMachine";
    public static final String ADD_DEVICE_TIPS = "AddDeviceTips";
    public static final String SHOW_EQUIPMENT_TROUBLE_SORT = "ShowEquipmentTroubleSort";
    public static final String SHOW_TASK_SUMMARY = "ShowTaskSummary";
    public static final String SHOW_MAINTENANCECONDITION = "ShowMaintenanceCondition";
    public static final String SHOW_BREAKDOWN_DESC = "ShowBreakdownDesc";
    public static final String SHOW_PROCESS_MENU = "ShowProcessingMenu";
    public static final String SHOW_COMPLETE_MENU = "ShowCompleteMenu";
    //点击“完成任务”将会直接结束任务，不会走其它的页面跳转，不需要填写任务总结、任务评价等（设施专用） By Leo
    public static final String DIRECT_FINISH = "DirectFinish";
    //完成任务时，是否需要展示任务所使用备件列表
    public static final String TASKUSEMATERIALSACTION = "TaskUseMaterialsAction";
    //完成任务时，是否需要弹出"创建备件退回单"
    public static final String UPMATERIALSBILLALERT = "UpMaterialsBillAlert";
    //设备状态为完成时是否弹出使用物料和已使用物料选项
    public static final String COMPLETEMENUISSHOW = "CompleteMenuIsShow";
}
