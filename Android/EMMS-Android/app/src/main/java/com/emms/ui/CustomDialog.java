package com.emms.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.R;
import com.emms.activity.AppApplication;
import com.emms.activity.dialogOnSubmitInterface;
import com.emms.adapter.ResultListAdapter;
import com.emms.bean.WorkInfo;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class CustomDialog extends Dialog {
    private CustomDialog dialog = this;
    private Context context;
    private EditText  approved_working_hours;
    private TextView work_name, work_description;
    private TextView comfirm_button;
    private DropEditText work_num,sub_task_equipment_num;
    private Map<String, Object> dataMap = new HashMap<String, Object>();
    private ArrayList<ObjectElement> taskEquipment;
    private final String DATA_KEY_WORK_INFO = "workInfo";

    private static final int MSG_UPDATE_WORK_INFO = 10;
    private ObjectElement modifySubTask=null;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private String TaskId;
    private TextView menuSearchTitle;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private boolean isSearchview ;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> workNumList=new ArrayList<ObjectElement>();
    private String EquipmentId=null;

    public dialogOnSubmitInterface getDialogOnSubmit() {
        return dialogOnSubmit;
    }

    public void setDialogOnSubmit(dialogOnSubmitInterface dialogOnSubmit) {
        this.dialogOnSubmit = dialogOnSubmit;
    }

    public dialogOnSubmitInterface dialogOnSubmit=null;
  //  public interface RefreshDataInterface{
  //      void refreshData();
  //  }
    public CustomDialog(Context context, int layout, int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        initview();
        setCanceledOnTouchOutside(true);
        initSearchView();
    }
    public CustomDialog(Context context, int layout, int style,ObjectElement objectElement,ArrayList<ObjectElement> list) {
        super(context, style);
        this.context = context;
        this.modifySubTask=objectElement;
        this.taskEquipment=list;
        setContentView(layout);
        initview();
        setCanceledOnTouchOutside(true);
        initSearchView();
    }

    public void initview() {

      //  ((ViewGroup)findViewById(R.id.viewGroup)).setVisibility(View.GONE);
        findViewById(R.id.dismissView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        work_num = (DropEditText) findViewById(R.id.work_num);//添加情况下用户输入，修改情况下获取
        approved_working_hours = (EditText) findViewById(R.id.approved_working_hours);//根据work_num从数据库中查出
        work_name = (TextView) findViewById(R.id.work_name);//根据work_num从数据库中查出
        work_description = (TextView) findViewById(R.id.work_description);//根据work_num从数据库中查出
        sub_task_equipment_num = (DropEditText) findViewById(R.id.sub_task_equipment_num);//机台号，用列表中选择，列表数据从任务详细列表中传入
        comfirm_button = (TextView) findViewById(R.id.comfirm);//确定按钮，提交信息
        //若为修改状态，则有初始数据
        if(modifySubTask!=null){
            work_num.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkCode")));//待修改
           approved_working_hours.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkTime")));
            EquipmentId=DataUtil.isDataElementNull(modifySubTask.get("Equipment_ID"));
            sub_task_equipment_num.setText(DataUtil.isDataElementNull(modifySubTask.get("OracleID")));
            work_name.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkName")));
            work_description.setText(DataUtil.isDataElementNull(modifySubTask.get("DataDescr")));
        }
     /*   approved_working_hours.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    //？禁用标准工时输入框
                } else {
                    // 此处为失去焦点时的处理内容
                    InputMethodManager imm =
                            (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

                   imm.hideSoftInputFromWindow(approved_working_hours.getWindowToken(),0);
                }
            }
        });*/
        findViewById(R.id.dialog_view_to_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =
                        (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(approved_working_hours.getWindowToken(),0);
            }
        });
       // dialog_view_to_show
       //work_num.
   /*     work_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    //？禁用标准工时输入框
                } else {
                    // 此处为失去焦点时的处理内容
                    setWorkInfo(work_num.getText().toString());
                }


            }
        });*/
        work_num.getmEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setWorkInfo(work_num.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        comfirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comfirm_button_event();
            }
        });
        //修改子任务的情况下调用
        //setViewData();
    }

    public void comfirm_button_event() {
       if (work_num.getText()==null
                ||approved_working_hours.getText()==null||
                work_num.getText().toString().trim().equals("") ||
                approved_working_hours.getText().toString().trim().equals("")) {
            //判断数据为空，提示用户数据不能为空，拒绝提交
            Toast.makeText(context, "请输入数据", Toast.LENGTH_LONG).show();
            return;
        } else {
            submitSubTaskData();
        }
    }

    public void submitSubTaskData() {
        if(!DataUtil.isNum(approved_working_hours.getText().toString())){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("pleaseInputNum"),context);
            return;
        }
        HttpParams params = new HttpParams();
        JsonObjectElement jsonObjectElement = new JsonObjectElement();
        //如果是修改任务，传子任务ID,若添加子任务,传子任务ID=0
        if(modifySubTask==null){
        jsonObjectElement.set("TaskItem_ID",0);}
        else {
            jsonObjectElement.set("TaskItem_ID",DataUtil.isDataElementNull(modifySubTask.get("TaskItem_ID")));
        }
        jsonObjectElement.set(Task.TASK_ID,TaskId);

        jsonObjectElement.set("TaskItemName",work_name.getText().toString());
         jsonObjectElement.set("TaskItemDesc",work_description.getText().toString());
      //  jsonObjectElement.set("TaskItem_ID",0);
        if(EquipmentId!=null){
        jsonObjectElement.set("Equipment_ID",EquipmentId);}
       // jsonObjectElement.set("Equipment_ID",sub_task_equipment_num.getText().toString());
      //  jsonObjectElement.set("Equipment_ID","124124");
        jsonObjectElement.set("WorkTimeCode",work_num.getText().toString());
        jsonObjectElement.set("PlanManhour",approved_working_hours.getText().toString());
        params.putJsonParams(jsonObjectElement.toJson());
        HttpUtils.post(context, "TaskItem", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dismiss();
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS).valueAsBoolean()){
                        if(modifySubTask==null){
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("createSubTaskSuccess"),context);
                        }
                        else{
                          ToastUtil.showToastShort(LocaleUtils.getI18nValue("changeSubTaskSuccess"),context);
                        }
                        dialogOnSubmit.onsubmit();
                    }else{
                        if(modifySubTask==null){
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("createSubTaskFail"),context);
                        }
                        else{
                            ToastUtil.showToastShort(LocaleUtils.getI18nValue("changeSubTaskFail"),context);
                        }
                    }
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }

    public void setViewData() {
        if(modifySubTask!=null){
            work_num.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkCode")));
            approved_working_hours.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkTime")));
            work_name.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkName")));
            work_description.setText(DataUtil.isDataElementNull(modifySubTask.get("DataDescr")));
            sub_task_equipment_num.setText(DataUtil.isDataElementNull(modifySubTask.get("Equipment_ID")));
        }
    }

    private void setWorkInfo(String workCode) {

        ListenableFuture<DataElement> elemt = getWorkInfoByWorkCode(workCode);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(final DataElement element) {
                System.out.println(element);
                ArrayList<ObjectElement> workInfolist = new ArrayList<ObjectElement>();

                try {

                    if (element != null && element.isArray()
                            && element.asArrayElement().size() > 0) {
                        workInfolist.clear();
                        for (int i = 0; i < element.asArrayElement().size(); i++) {
                            workInfolist.add(element.asArrayElement().get(i)
                                    .asObjectElement());
                        }
                    } else {
                        //Toast.makeText(context, "程序数据库出错", Toast.LENGTH_SHORT).show();

                    }

                    WorkInfo workInfo = new WorkInfo();
                    if (null != workInfolist && !workInfolist.isEmpty()) {
                        ObjectElement dataElement = workInfolist.get(0);
                       final  JsonObjectElement jsonObjectElement = new JsonObjectElement(dataElement.toJson());

                        workInfo.setWorkCode(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_CODE)));
                        workInfo.setWorkName(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                        workInfo.setApprovedWorkingHours(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_VALUE1)));
                        workInfo.setWorkDescr(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_DESCR)));
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                approved_working_hours.setText(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_VALUE1)));
                                work_name.setText(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                                work_description.setText(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_DESCR)));
                            }
                        });
                    }

                    dataMap.put(DATA_KEY_WORK_INFO, workInfo);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_WORK_INFO, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }

    //主线程中的handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;

            switch (what) {
                case MSG_UPDATE_WORK_INFO: {
                    updateWorkInfoView();
                    break;
                }

            }
        }

    };

    public String nullToEmptyString(DataElement o) {
        if (null == o || o.isNull()) {
            return "";
        }
        return DataUtil.isDataElementNull(o);
    }

    private <T> T getDataFromDataMap(String key, Class<T> cls) {
        Object valueObj = dataMap.get(key);

        T retData = null;
        if (null != valueObj) {
            retData = (T) valueObj;
        }

        return retData;
    }

    private void updateWorkInfoView() {
        WorkInfo workInfo = getDataFromDataMap(DATA_KEY_WORK_INFO, WorkInfo.class);
        if (null == workInfo) {
            return;
        }

//        work_num.setText(workInfo.getWorkCode());
        approved_working_hours.setText(workInfo.getApprovedWorkingHours());
        work_name.setText(workInfo.getWorkName());
        work_description.setText(workInfo.getWorkDescr());
    }

    //根据工作编号取工作信息
    private ListenableFuture<DataElement> getWorkInfoByWorkCode(String workCode) {
        // SqliteStore sqliteStore =  ((AppApplication) getApplication()).getSqliteStore();

        SqliteStore sqliteStore = ((AppApplication) context.getApplicationContext()).getSqliteStore();

        String rawQuery = "select DataCode,DataName,DataDescr,DataValue1 from Datadictionary where DataCode =" + "'" + workCode + "'";
        ListenableFuture<DataElement> elemt = sqliteStore.performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        return elemt;
    }
    public void setData(ObjectElement objectElement){
        modifySubTask=objectElement;
    }
    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public ArrayList<ObjectElement> getTaskEquipment() {
        return taskEquipment;
    }

    public void setTaskEquipment(ArrayList<ObjectElement> taskEquipment) {
        this.taskEquipment = taskEquipment;
    }


    private void initSearchView() {
        initWorkNumListData();

        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.setHint(LocaleUtils.getI18nValue("hint_search_box"));
        mDrawer_layout = (CustomDrawerLayout) findViewById(R.id.search_page);
        mDrawer_layout.setCloseDrawerListener(new CloseDrawerListener() {
            @Override
            public void close() {
                searchBox.setText("");
            }
        });
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawer_layout.setBackgroundColor(Color.parseColor("#00000000"));

        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        ((TextView)emptyView.findViewById(R.id.tvNothingFound)).setText(LocaleUtils.getI18nValue("nothing_found"));
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                isSearchview = true ;
                final int inPosition = position;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    work_num.getmEditText().setText(searchResult);
                                    break;
                                case 2:
                                    sub_task_equipment_num.getmEditText().setText(searchResult);
                                    EquipmentId=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                                    break;
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    Toast.makeText(context, "出错了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initDropSearchView(null, work_num.getmEditText(), LocaleUtils.getI18nValue("work_num_dialog"), DataDictionary.DATA_CODE,
                1, LocaleUtils.getI18nValue("getDataFail"));
        initDropSearchView(null, sub_task_equipment_num.getmEditText(), LocaleUtils.getI18nValue("title_search_equipment_nun"), "OracleID",
                2, LocaleUtils.getI18nValue("task_not_machine"));
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
            }
        });
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

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final String tips){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchDataLists.clear();
                                        switch (searTag){
                                            case 1:
                                                searchDataLists.addAll(workNumList);
                                                break;
                                            case 2:
                                                searchDataLists.addAll(taskEquipment);
                                                break;

                                        }
                                        searchtag = searTag;
                                                if (condition != null) {
                                                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                                                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                                                        mResultAdapter.changeData(searchDataLists, searchName);
                                                        menuSearchTitle.setText(searchTitle);
                                                        menuSearchTitle.postInvalidate();
                                                        mDrawer_layout.postInvalidate();

                                                    } else {
                                                        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    if (searchDataLists.size() > 0) {
                                                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                                                        mResultAdapter.changeData(searchDataLists, searchName);
                                                        menuSearchTitle.setText(searchTitle);
                                                        menuSearchTitle.postInvalidate();
                                                        mDrawer_layout.postInvalidate();

                                                    } else {
                                                        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
                                                    }
                                                }



                                    }
                                });

                            }
                        }

                );
    }

    /**
     * creata by jason
     */
    private void initWorkNumListDataByServe(){
        try {
        HttpParams params = new HttpParams();
        HttpUtils.post(context, "DataDictionary/APPGet?Parameter=filter%3DDataType%20eq%20'WorkTime'", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (TextUtils.isEmpty(t)||t.equals("null")){
                    LogUtils.e("获取数据为空");
                    return;
                }
                LogUtils.e("initWorkNumListDataByServe---获取数据成功---->"+t);
                DataElement jsonArrayElement = new JsonArrayElement(t);
                workNumList.clear();
                if (jsonArrayElement != null && jsonArrayElement.isArray()
                        && jsonArrayElement.asArrayElement().size() > 0) {
                    for (int i = 0; i < jsonArrayElement.asArrayElement().size(); i++) {
                        workNumList.add(jsonArrayElement.asArrayElement().get(i).asObjectElement());
                    }
                } else {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "目前该设备没有工作编号", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                LogUtils.e("initWorkNumListDataByServe---获取数据失败---->"+errorNo+"----->"+strMsg);
            }
        });
        }catch (Exception e){
            e.printStackTrace();
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "目前该设备没有工作编号", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initWorkNumListData(){
        initWorkNumListDataByServe();
//        try {
//            String rawQuery = "select * from DataDictionary where DataType='WorkTime'  order by Data_ID asc";
//            ListenableFuture<DataElement> elemt = ((AppApplication) ((Activity)context).getApplication()).getSqliteStore().performRawQuery(rawQuery,
//                    EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, null);
//            Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//                @Override
//                public void onSuccess(DataElement dataElement) {
//                    workNumList.clear();
//                    LogUtils.e("initWorkNumListData---获取数据成功---->"+dataElement.asArrayElement().toString());
//                    if (dataElement != null && dataElement.isArray()
//                            && dataElement.asArrayElement().size() > 0) {
//                        for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
//                            workNumList.add(dataElement.asArrayElement().get(i).asObjectElement());
//                        }
//                    } else {
//                        ((Activity)context).runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(context, "目前该设备没有工作编号", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable throwable) {
//
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//            ((Activity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "目前该设备没有工作编号", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }
    //refreshData(){

    //}
}