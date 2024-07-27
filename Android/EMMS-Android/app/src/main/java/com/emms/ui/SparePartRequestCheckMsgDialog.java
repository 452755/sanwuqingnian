package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.emms.R;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;

/**
 * Created by Administrator on 2016/8/26.
 *
 */
public class SparePartRequestCheckMsgDialog extends Dialog{
    private Context context;
    private String Tips;
    private String SmallTips;
    private LinearLayout lackSparePartsList;
    public SparePartRequestCheckMsgDialog(Context context) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);
        this.context = context;
        setContentView(R.layout.dialog_spare_part_request_check_msg);
        initview();
        setCancelable(false);
    }
    public void setCheckMsg(JsonArrayElement checkMsg){
        refeshTable(checkMsg);
    }
    public void setTips(String tips,String smallTips){
        Tips=tips;
        this.SmallTips = smallTips;
        ((TextView) findViewById(R.id.DialogTitle)).setText(tips);
        ((TextView) findViewById(R.id.DialogSmallTitle)).setText(smallTips);
    }

    private void initview(){
        Tips = LocaleUtils.getI18nValue("the_warehouse_is_missing_a_material_list");
        SmallTips = LocaleUtils.getI18nValue("resubmit_after_modification");
        Button modifyDetail =  (Button)findViewById(R.id.modify_detail);
        modifyDetail.setText(LocaleUtils.getI18nValue("modify"));
        lackSparePartsList = (LinearLayout) findViewById(R.id.check_msg);
        ((TextView) findViewById(R.id.DialogTitle)).setText(Tips);
        ((TextView) findViewById(R.id.DialogSmallTitle)).setText(SmallTips);
        ((TextView) findViewById(R.id.spare_part_selectName)).setText(LocaleUtils.getI18nValue("spare_part_name"));
        ((TextView) findViewById(R.id.spare_part_selectType)).setText(LocaleUtils.getI18nValue("typeof_spare_part"));
        ((TextView) findViewById(R.id.spare_part_selectNum)).setText(LocaleUtils.getI18nValue("quantity"));
        modifyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawerListener.close();
            }
        });
        Button close =  (Button)findViewById(R.id.close);
        close.setText(LocaleUtils.getI18nValue("close"));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setCloseDrawerListener(CloseDrawerListener CloseDrawerListener) {
        this.CloseDrawerListener = CloseDrawerListener;
    }

    private CloseDrawerListener CloseDrawerListener;

    private void refeshTable(JsonArrayElement lackSparePart){
        lackSparePartsList.removeAllViews();
        for(int i = 0; i<lackSparePart.size();i++){
            LinearLayout mLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_spare_part_list, null);
            ObjectElement sparePart = lackSparePart.get(i).asObjectElement();
            ((TextView)mLinearLayout.findViewById(R.id.name)).setText(DataUtil.isDataElementNull(sparePart.get("name")));
            ((TextView)mLinearLayout.findViewById(R.id.type)).setVisibility(View.GONE);
            ((TextView)mLinearLayout.findViewById(R.id.quantity)).setText(DataUtil.isDataElementNull(sparePart.get("lack_number")));
            lackSparePartsList.addView(mLinearLayout);
        }
    }
}
