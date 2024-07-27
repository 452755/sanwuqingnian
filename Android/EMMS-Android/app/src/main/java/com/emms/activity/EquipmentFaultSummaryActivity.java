package com.emms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;

/**
 * Created by Administrator on 2016/8/24.
 *
 */
public class EquipmentFaultSummaryActivity extends NfcActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault_summary);
        initView();
        String faultDetail=getIntent().getStringExtra("FaultDetail");
        setData(faultDetail);
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("FaultSummaryDetail"));
        ((TextView)findViewById(R.id.task_id_tag)).setText(LocaleUtils.getI18nValue("task_id"));
        ((TextView)findViewById(R.id.summary_person_tag)).setText(LocaleUtils.getI18nValue("summary_person"));
        ((TextView)findViewById(R.id.equipment_name_tag)).setText(LocaleUtils.getI18nValue("equipment_name"));
        ((TextView)findViewById(R.id.fault_type_tag)).setText(LocaleUtils.getI18nValue("fault_type"));
        ((TextView)findViewById(R.id.fault_description_tag)).setText(LocaleUtils.getI18nValue("fault_description"));
        ((TextView)findViewById(R.id.repair_status_tag)).setText(LocaleUtils.getI18nValue("repair_status"));
        findViewById(R.id.btn_right_action).setOnClickListener(this);
    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void setData(String faultDetail){
        JsonObjectElement Detail=new JsonObjectElement(faultDetail);
        ((TextView)findViewById(R.id.task_id)).setText(DataUtil.isDataElementNull(Detail.get(Task.TASK_ID)));
        ((TextView)findViewById(R.id.summary_person)).setText(DataUtil.isDataElementNull(Detail.get("Name")));
        ((TextView)findViewById(R.id.fault_type)).setText(DataUtil.isDataElementNull(Detail.get("TroubleType")));
        ((TextView)findViewById(R.id.fault_description)).setText(DataUtil.isDataElementNull(Detail.get("TroubleDescribe")));
        ((TextView)findViewById(R.id.repair_status)).setText(DataUtil.isDataElementNull(Detail.get("MaintainDescribe")));
        ((TextView)findViewById(R.id.equipment_name)).setText(DataUtil.isDataElementNull(Detail.get("TaskEquipmentList")));
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id) {
            case R.id.btn_right_action: {
                finish();
                break;
            }
        }
    }
}
