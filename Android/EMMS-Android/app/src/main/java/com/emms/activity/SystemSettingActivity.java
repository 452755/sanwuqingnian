package com.emms.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.adapter.MainActivityAdapter;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/31.
 *
 */
public class SystemSettingActivity extends NfcActivity implements View.OnClickListener{
    private Context context=this;
    private ArrayList<ObjectElement> moduleList=new ArrayList<>();
    private BroadcastReceiver receiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        initTitle();
        initView();
        initData();
        initLanguageBroadcastReceiver();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if(SharedPreferenceManager.getLanguageChange(this)){
            initView();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initTitle(){
        ((TextView)findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("systemSetting"));
    }
    private void initView(){
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.filter).setOnClickListener(this);
        ((ImageView)findViewById(R.id.filter)).setImageResource(R.mipmap.sync);
        final GridView module_list = (GridView) findViewById(R.id.module_list);
        MainActivityAdapter adapter = new MainActivityAdapter(moduleList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_cur, parent, false);
                    holder = new TaskViewHolder();
                    holder.image = (ImageView) convertView.findViewById(R.id.module_image);
                    holder.moduleName = (TextView) convertView.findViewById(R.id.module_name);
                    holder.moduleName.setText(LocaleUtils.getI18nValue("repair_tag"));
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                if (moduleList.get(position).get("module_image") != null) {
                    holder.image.setImageResource(moduleList.get(position).get("module_image").valueAsInt());
                }
                if (moduleList.get(position).get("module_name") != null) {
                    holder.moduleName.setText(moduleList.get(position).get("module_name").valueAsString());
                }
                return convertView;
            }
        };
        module_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(moduleList.get(position).get("Class")!=null){
                    try {
                        LogUtils.e("跳转到--->"+moduleList.get(position).get("Class"));
                        Class c=Class.forName(DataUtil.isDataElementNull(moduleList.get(position).get("Class")));
                        Intent intent=new Intent(context,c);
                        startActivity(intent);
                    }catch (Exception e){
                        Log.e("e",e.toString());
                    }
                }
            }
        });
        module_list.setAdapter(adapter);
    }
    private void initData(){
        moduleList.clear();
        for(int i=0;i<3;i++ ){
            if(i!=1) {//屏蔽绑定设备
                JsonObjectElement jsonObjectElement = new JsonObjectElement();
                jsonObjectElement.set("module_ID", i + 1);
                jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                moduleList.add(jsonObjectElement);
            }
        }
    }
    private void initLanguageBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initTitle();
                    initData();
                }
            });
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocaleUtils.LANGUAGE_SETTING_ACTION);
        registerReceiver(receiver, filter);
    }
    private JsonObjectElement moduleMatchingRule(JsonObjectElement obj){
        int module_id=obj.get("module_ID").valueAsInt();
        String packageName="com.emms.activity.";
        switch (module_id) {
            case 1: {//FactorySetting
                obj.set("module_image", R.mipmap.system_setting_activity_setting);
                obj.set("module_name", LocaleUtils.getI18nValue("FactorySetting"));
                obj.set("Class", packageName + "SettingActivity");
                break;
            }
            case 2: {//EquipmentBinding
                obj.set("module_image", R.mipmap.system_setting_activity_binding);
                obj.set("module_name", LocaleUtils.getI18nValue("EquipmentBinding"));
                obj.set("Class", packageName + "EnteringEquipmentICCardIDActivity");
                break;
            }
            case 3: {//version_info
                obj.set("module_image", R.mipmap.module_version_info);
                obj.set("module_name", LocaleUtils.getI18nValue("version_info"));
                obj.set("Class", packageName + "VersionInfoActivity");
                break;
            }
            default:{
                obj.set("module_image", R.mipmap.module_version_info);
                obj.set("module_name", LocaleUtils.getI18nValue("version_info"));
                obj.set("Class", packageName + "VersionInfoActivity");
                break;
            }
        }
        return obj;
    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_right_action:{
                finish();
                break;
            }
            case R.id.filter:{
//                getDBDataLastUpdateTime();
                break;
            }
        }
    }


}
