package com.emms.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.emms.R;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaffer.deng on 2016/7/16.
 *
 */
public class MultiAdapter extends BaseAdapter {
    public List<Integer> getListItemID() {
        return listItemID;
    }
    public List<ObjectElement> getListItems() {
        return listItems;
    }

    public void setListItems(List<ObjectElement> listItems) {
        this.listItems = listItems;
        mChecked.clear();
        if(mChecked!=null){
            for (; mChecked.size()<listItems.size();) {// 遍历且设置CheckBox默认状态为未选中
                mChecked.add(false);
            }}
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private List<ObjectElement> listItems;

    private Context ctx;


    /** 标记CheckBox是否被选中 **/
    ArrayList<Boolean> mChecked= new ArrayList<>();;

    List<Integer> listItemID;
    //HashSet<Integer> OperatorSet=new HashSet<Integer>();
    private boolean tag=false;
    @SuppressLint("UseSparseArrays")
    //private Map<Integer, View> viewMap = new HashMap<>();
    public MultiAdapter(Context context, List<ObjectElement> listItems,boolean tag) {
        // TODO Auto-generated constructor stub
        this.ctx = context;
        this.tag=tag;
        this.listItems = listItems;
        for (int i = 0; i < listItems.size(); i++) {// 遍历且设置CheckBox默认状态为未选中
            mChecked.add(false);
        }

        listItemID = new ArrayList<>();

    }



    public void ClickResult()
    {
        listItemID.clear();// 清空listItemID

        for (int i = 0; i < mChecked.size(); i++) {
            if (mChecked.get(i)) {

                listItemID.add(i);
            }
        }

//		if (listItemID.size() == 0) {
//			AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
//			builder1.setMessage("没有选中任何记录");
//			builder1.show();
//		} else {// 如果列表不为空,在对话框上显示选中项的ID
//			StringBuilder sb = new StringBuilder();
//
//			for (int i = 0; i < listItemID.size(); i++) {// 遍历listItemID列表取得存放的每一项
//				sb.append("ItemID=" + listItemID.get(i) + " . "+listItems.get(i).toString()+".");
//
//			}
//			AlertDialog.Builder builder2 = new AlertDialog.Builder(
//					ctx);
//			builder2.setMessage(sb.toString());
//
//			builder2.show();// 显示对话框
//		}
    }

    public List<Integer> getlistItemID (){
        return listItemID;
    }
    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //  View rowView = this.viewMap.get(position);
        //  AwaitRepair awaitRepair =  listItems.get(position);
        final ObjectElement Operator = listItems.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder=new ViewHolder();
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_invitor, null);
            //((TextView)convertView.findViewById(R.id.textView22)).setText(LocaleUtils.getI18nValue("workname"));
            //((TextView)convertView.findViewById(R.id.textView23)).setText(LocaleUtils.getI18nValue("tech"));
            holder.workname = (TextView) convertView.findViewById(R.id.id_worknum);
            //holder.tech = (TextView) convertView.findViewById(R.id.id_tech);
            holder.statusText = (TextView) convertView.findViewById(R.id.workstatus);
            holder.timeUsed = (TextView) convertView.findViewById(R.id.work_timeusedTxt);
            holder.taskFinished = (TextView) convertView.findViewById(R.id.work_taskcompletedTxt);
            holder.organizeName = (TextView) convertView.findViewById(R.id.work_positionTxt);
            holder.equipmentName = (TextView) convertView.findViewById(R.id.work_equipmentTxt);
            holder.workWip = (TextView) convertView.findViewById(R.id.work_wipTxt);

            holder.select = (ImageView) convertView.findViewById(R.id.select);
            holder.selectNormal = (ImageView) convertView.findViewById(R.id.select_normal);
            holder.multi_item = (LinearLayout) convertView.findViewById(R.id.multi_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        if(!tag){
           convertView.findViewById(R.id.layout).setVisibility(View.GONE);
        }
        holder.workname.setText(DataUtil.isDataElementNull(Operator.get("Name")));
        holder.timeUsed.setText(DataUtil.isDataElementNull(Operator.get("TimeUsed")));
        holder.taskFinished.setText(DataUtil.isDataElementNull(Operator.get("TaskFinished")));
        holder.organizeName.setText(DataUtil.isDataElementNull(Operator.get("OrganiseName")));
        holder.equipmentName.setText(DataUtil.isDataElementNull(Operator.get("EquipmentName")));
        holder.workWip.setText(DataUtil.isDataElementNull(Operator.get("Processing")));
        //holder.tech.setText(DataUtil.isDataElementNull(Operator.get("Skill")));
            if (DataUtil.isDataElementNull(Operator.get("Status")).equals("1")) {
                holder.statusText.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.bg_busy));
                holder.statusText.setText(LocaleUtils.getI18nValue("busy"));
            } else {
               // holder.statusText.setImageResource(R.mipmap.idle);
                holder.statusText.setText(LocaleUtils.getI18nValue("free"));
                holder.statusText.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.bg_free));
            }
           if(mChecked.get(position)){
               holder.select.setVisibility(View.VISIBLE);
               holder.selectNormal.setVisibility(View.INVISIBLE);
           }
            else{
               holder.select.setVisibility(View.INVISIBLE);
               holder.selectNormal.setVisibility(View.VISIBLE);
           }

            holder.multi_item.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!DataUtil.isDataElementNull(Operator.get("Status")).equals("1")||isFromTaskInfoEnteringActivity) {
                        if (mChecked.get(position))//当前已选中，点击后取消选中
                        {
                            holder.select.setVisibility(View.INVISIBLE);
                            holder.selectNormal.setVisibility(View.VISIBLE);
                            mChecked.set(position, false);
                        } else {
                            holder.select.setVisibility(View.VISIBLE);
                            holder.selectNormal.setVisibility(View.INVISIBLE);
                            holder.select.setImageResource(R.mipmap.select_pressed);
                            mChecked.set(position, true);
                        }
                        ClickResult();
                    } else {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("thisWorkerIsBusy"), ctx);
                    }
                }
            });
        LinearLayout workSelect = (LinearLayout)convertView.findViewById(R.id.id_workselect);
        if(!tag) {
            holder.multi_item.setEnabled(false);
            workSelect.setVisibility(View.GONE);
        }else workSelect.setVisibility(View.VISIBLE);
           // viewMap.put(position, convertView);



            return convertView;



    }
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return true;
    }
    public static class ViewHolder {
        TextView workname;
        TextView timeUsed;
        TextView taskFinished;
        TextView organizeName;
        TextView equipmentName;
        TextView workWip;
        //TextView tech;
        TextView statusText;
        //ImageView status;
        ImageView select;
        ImageView selectNormal;
        LinearLayout multi_item;
    }

    public void setFromTaskInfoEnteringActivity(boolean fromTaskInfoEnteringActivity) {
        isFromTaskInfoEnteringActivity = fromTaskInfoEnteringActivity;
    }

    private boolean isFromTaskInfoEnteringActivity=false;
}
