package com.emms.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.LogUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Abrahamguo on 2021/12/21.
 *
 */
public class SparePartAdapter extends BaseAdapter {

    //需要展示的备件集合
    private List<ObjectElement> listItems;

    private Context ctx;

    //选中备件码集合
    private List<String> selectItemCodes;

    private String TaskClass;

    private Map<String,String> SpareWarehouse;

    //选中备件集合
    private ArrayList<ObjectElement> selectItems;

    private Random random = new Random();

    //获取选中备件集合
    public ArrayList<ObjectElement> getSelectItems() {
        return selectItems;
    }

    //设置展示的备件集合
    public void setListItems(List<ObjectElement> listItems) {
        this.listItems = listItems;
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    //设置展示的备件集合
    public void setListItems(List<ObjectElement> listItems,ArrayList<ObjectElement> selectItems) {
        this.listItems = listItems;
        selectItemCodes.clear();
        this.selectItems = selectItems;
        for (ObjectElement selectItem:
                selectItems) {
            selectItemCodes.add(selectItem.get("Stock_item_id").valueAsString());
            if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)){
                selectItem.set("Quantity",selectItem.get("UseCount").valueAsInt());
            }
        }
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    //初始化
    public SparePartAdapter(Context context, List<ObjectElement> listItems,String taskClass) {
        this.ctx = context;
        this.listItems = listItems;
        selectItemCodes = new ArrayList<>();
        selectItems = new ArrayList<>();
        TaskClass = taskClass;
        SpareWarehouse = SharedPreferenceManager.getHashMapData(ctx,"SpareWarehouse");
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
        final ObjectElement sparePart = listItems.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_spare_part, null);

            holder.sparePartName = (TextView) convertView.findViewById(R.id.id_spare_part_name);
            holder.sparePartPositionTxt = (TextView) convertView.findViewById(R.id.spare_part_positionTxt);
            holder.sparePartPositionVal = (TextView) convertView.findViewById(R.id.spare_part_positionVal);
            holder.sparePartBrandTxt = (TextView) convertView.findViewById(R.id.spare_part_brandTxt);
            holder.sparePartBrandVal = (TextView) convertView.findViewById(R.id.spare_part_brandVal);
            holder.sparePartTypeTxt = (TextView) convertView.findViewById(R.id.spare_part_typeTxt);
            holder.sparePartTypeVal = (TextView) convertView.findViewById(R.id.spare_part_typeVal);
            holder.sparePartLastReplaceTimeTxt = (TextView) convertView.findViewById(R.id.spare_part_last_replace_timeTxt);
            holder.sparePartLastReplaceTimeVal = (TextView) convertView.findViewById(R.id.spare_part_last_replace_timeVal);
            holder.sparePartQuantityTxt = (TextView) convertView.findViewById(R.id.spare_part_quantityTxt);
            holder.sparePartQuantityVal = (TextView) convertView.findViewById(R.id.spare_part_quantityVal);

            holder.numAddReduce = (RelativeLayout) convertView.findViewById(R.id.num_add_reduce);
            holder.amount = (EditText) convertView.findViewById(R.id.amount);
            holder.btnAdd = (TextView) convertView.findViewById(R.id.btn_add);
            holder.btnReduce = (TextView) convertView.findViewById(R.id.btn_reduce);

            holder.sparePartSelect = (RelativeLayout) convertView.findViewById(R.id.select_layout);
            holder.select = (ImageView) convertView.findViewById(R.id.select);
            holder.selectNormal = (ImageView) convertView.findViewById(R.id.select_normal);

            holder.selectNum = (LinearLayout) convertView.findViewById(R.id.id_selectNum);
            holder.selectNumTxt = (TextView) convertView.findViewById(R.id.selectNumTxt);

            holder.multi_item = (LinearLayout) convertView.findViewById(R.id.multi_item);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.sparePartName.setText(DataUtil.isDataElementNull(sparePart.get("MaterialName")));
        holder.sparePartPositionTxt.setText(LocaleUtils.getI18nValue("inventory_location"));
        holder.sparePartTypeTxt.setText(LocaleUtils.getI18nValue("typeof_spare_part"));
        holder.sparePartBrandTxt.setText(LocaleUtils.getI18nValue("spare_part_brand"));
        holder.sparePartQuantityTxt.setText(LocaleUtils.getI18nValue("inventory_quantity"));
        holder.sparePartLastReplaceTimeTxt.setText(LocaleUtils.getI18nValue("replacement_time"));
        String WareHouse = DataUtil.isDataElementNull(sparePart.get("WareHouse"));
        holder.sparePartPositionVal.setText(SpareWarehouse.get(WareHouse));
        holder.sparePartTypeVal.setText(DataUtil.isDataElementNull(sparePart.get("MaterialType")));
        holder.sparePartBrandVal.setText(DataUtil.isDataElementNull(sparePart.get("MaterialBrand")));

        if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE) || TaskClass.equals(Task.SPAREPART_RETURN)){
            holder.sparePartQuantityTxt.setVisibility(View.VISIBLE);
            holder.sparePartQuantityVal.setVisibility(View.VISIBLE);
            holder.selectNum.setVisibility(View.GONE);
            holder.sparePartSelect.setVisibility(View.VISIBLE);
            holder.numAddReduce.setVisibility(View.VISIBLE);
            holder.sparePartQuantityVal.setText(DataUtil.isDataElementNull(sparePart.get("RealCount")));
        }else if(TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
            holder.sparePartQuantityTxt.setVisibility(View.VISIBLE);
            holder.sparePartQuantityVal.setVisibility(View.VISIBLE);
            holder.selectNum.setVisibility(View.GONE);
            holder.numAddReduce.setVisibility(View.VISIBLE);
            holder.sparePartSelect.setVisibility(View.VISIBLE);
            holder.sparePartQuantityVal.setText(DataUtil.isDataElementNull(sparePart.get("Inventory")));
        }else if(TaskClass.equals(Task.SPAREPART_CONFIRM)||TaskClass.equals(Task.SPAREPART_DETAILS)){
            onlyShow(holder,sparePart,"Quantity");
            return convertView;
        }else if(TaskClass.equals(Task.SPAREPART_SURPLUS)){
            onlyShow(holder,sparePart,"RealCount");
            return convertView;
        }else if(TaskClass.equals(Task.SPAREPART_TASK_USED)){
            onlyShow(holder,sparePart,"UseCount");
            return convertView;
        }else if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_USED)){
            onlyShow(holder,sparePart,"UseCount");
            holder.sparePartLastReplaceTimeTxt.setVisibility(View.VISIBLE);
            holder.sparePartLastReplaceTimeVal.setVisibility(View.VISIBLE);
            holder.sparePartLastReplaceTimeVal.setText(DataUtil.isDataElementNull(sparePart.get("Use_Date")));
            return convertView;
        }

        //判断是否有被选中
        String MaterialCode = "";
        if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
            MaterialCode = sparePart.get("Stock_item_id").valueAsString();
        }else if(TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
            MaterialCode = sparePart.get("Id").valueAsString();
        }

        if(selectItemCodes.contains(MaterialCode)){
            for (ObjectElement SparePartItem:selectItems) {
                if (SparePartItem.get("Stock_item_id").valueAsString().equals(MaterialCode)){
                    holder.amount.setText(SparePartItem.get("Quantity").valueAsString());
                }
            }
            holder.select.setVisibility(View.VISIBLE);
            holder.selectNormal.setVisibility(View.GONE);
        } else{
            holder.select.setVisibility(View.GONE);
            holder.selectNormal.setVisibility(View.VISIBLE);
            holder.amount.setText("0");
        }

        holder.btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reduce(position,holder);
            }
        });
        //增加按钮点击事件
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
                    AddByLocal(position,holder);
                }else if(TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
                    AddByCloud(position,holder);
                }
            }
        });

        holder.amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {


            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE) || TaskClass.equals(Task.SPAREPART_RETURN)) {
                    AddByLocal(position, holder, Integer.parseInt(holder.amount.getText().toString()));
                } else if (TaskClass.equals(Task.SPAREPART_CHOOSE) || TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)) {
                    AddByCloud(position, holder, Integer.parseInt(holder.amount.getText().toString()));
                }
                return true;
            }
        });

        return convertView;
    }

    private void onlyShow(ViewHolder holder,ObjectElement sparePart,String showNumName){
        holder.sparePartQuantityTxt.setVisibility(View.GONE);
        holder.sparePartQuantityVal.setVisibility(View.GONE);
        holder.selectNum.setVisibility(View.VISIBLE);
        holder.sparePartSelect.setVisibility(View.GONE);
        holder.numAddReduce.setVisibility(View.GONE);
        holder.selectNumTxt.setText(DataUtil.isDataElementNull(sparePart.get(showNumName)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(holder.multi_item.getLayoutParams());
        lp.setMargins(20,20,20,0);
        holder.multi_item.setLayoutParams(lp);
        holder.multi_item.setBackgroundColor(ctx.getResources().getColor(R.color.click_item));
    }

    //从云端库存挑选备件时添加数量方法
    private void AddByCloud(int position,ViewHolder holder,int... inputAmount){
        //获取当前点击项的备件
        ObjectElement currentSparePart = listItems.get(position);
        if(currentSparePart.get("Inventory").valueAsInt() == 0){
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("greater_than_inventory"),ctx);
            return;
        }
        //判断当前备件是否已被选中
        if (!selectItemCodes.contains(currentSparePart.get("Id").valueAsString())) {
            //没有被选中
            //设置当前备件的选中数量为1
            ObjectElement selectSparePart = new JsonObjectElement();
            selectSparePart.set("Quantity", inputAmount.length > 0 ? inputAmount[0] : 1);
            selectSparePart.set("Stock_item_id", currentSparePart.get("Id"));
            selectSparePart.set("MaterialName", currentSparePart.get("MaterialName"));
            selectSparePart.set("MaterialType", currentSparePart.get("MaterialType"));
            selectSparePart.set("WareHouse", currentSparePart.get("WareHouse"));
            selectSparePart.set("MaterialBrand", currentSparePart.get("MaterialBrand"));
            selectSparePart.set("CountNum", currentSparePart.get("CountNum"));
            //将当前选中备件的备件码存入已选中备件码集合
            selectItemCodes.add(currentSparePart.get("Id").valueAsString());
            //将当前选中备件添加进选中备件集合
            selectItems.add(selectSparePart);
            //展示当前备件的选中数量
            if (inputAmount.length > 0) {
                if (selectSparePart.get("Quantity").valueAsInt() >= currentSparePart.get("Inventory").valueAsInt()) {
                    //提示所选数量大于剩余库存
                    ToastUtil.showToastShort(LocaleUtils.getI18nValue("greater_than_inventory"), ctx);
                    //如果超过最大库存，则默认只赋值最大库存量进行操作 By Leo
                    selectSparePart.set("Quantity", currentSparePart.get("Inventory").valueAsInt());
                }
            }
            holder.amount.setText(selectSparePart.get("Quantity").valueAsString());
            //展示当前备件为选中状态
            holder.select.setVisibility(View.VISIBLE);
            holder.selectNormal.setVisibility(View.GONE);
        } else {
            //被选中
            //循环已选中备件集合
            for (ObjectElement SparePartItem : selectItems) {
                //判断是否等于当前选中备件
                if (SparePartItem.get("Stock_item_id").valueAsString().equals(currentSparePart.get("Id").valueAsString())) {
                    //如果直接输入,则直接赋值然后在进行判断 By Leo
                    if (inputAmount.length > 0) {
                        SparePartItem.set("Quantity", inputAmount[0]);
                    }
                    //判断当前选中备件所选数量是否等于当前备件的剩余数量
                    if (SparePartItem.get("Quantity").valueAsInt() >= currentSparePart.get("Inventory").valueAsInt()) {
                        //提示所选数量大于剩余库存
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("greater_than_inventory"), ctx);
                        //如果超过最大库存，则默认只赋值最大库存量进行操作 By Leo
                        SparePartItem.set("Quantity", currentSparePart.get("Inventory").valueAsInt());
                        holder.amount.setText(SparePartItem.get("Quantity").valueAsString());
                    } else {
                        if (inputAmount.length == 0) {
                            //设置当前选中备件的选中数量+1
                            SparePartItem.set("Quantity", (SparePartItem.get("Quantity").valueAsInt() + 1));
                            //展示当前备件的选中数量
                            holder.amount.setText(SparePartItem.get("Quantity").valueAsString());
                        }
                    }
                }
            }
        }
    }
    //从本地库存挑选备件时添加数量方法
    private void AddByLocal(int position,ViewHolder holder,int... inputAmount){
        //获取当前点击项的备件
        final ObjectElement currentSparePart = listItems.get(position);
        //判断当前选中备件的库存数量是否等于0
        if(currentSparePart.get("RealCount").valueAsInt() == 0){
            //提示所选数量大于剩余库存
            ToastUtil.showToastShort(LocaleUtils.getI18nValue("greater_than_inventory"),ctx);
        }
        //判断当前备件是否已被选中
        else if(!selectItemCodes.contains(currentSparePart.get("Stock_item_id").valueAsString())){
            //没有被选中
            //设置当前备件的选中数量为1
            ObjectElement selectSparePart = new JsonObjectElement();
            selectSparePart.set("Quantity", inputAmount.length > 0 ? inputAmount[0] : 1);
            selectSparePart.set("Stock_item_id",currentSparePart.get("Stock_item_id"));
            selectSparePart.set("MaterialName",currentSparePart.get("MaterialName"));
            selectSparePart.set("MaterialType",currentSparePart.get("MaterialType"));
            selectSparePart.set("WareHouse",currentSparePart.get("WareHouse"));
            selectSparePart.set("MaterialBrand",currentSparePart.get("MaterialBrand"));
            selectSparePart.set("RealCount",currentSparePart.get("RealCount"));
            //将当前选中备件的备件码存入已选中备件码集合
            selectItemCodes.add(selectSparePart.get("Stock_item_id").valueAsString());
            //将当前选中备件添加进选中备件集合
            selectItems.add(selectSparePart);
            //展示当前备件的选中数量

            //判断是否是设备使用物料
            if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)){
                if (inputAmount.length > 0){
                    if (inputAmount[0] > currentSparePart.get("RealCount").valueAsInt()) {
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("greater_than_inventory"), ctx);
                        selectSparePart.set("Quantity", 0);
                        holder.amount.setText("0");
                    }
                }else{
                    holder.amount.setText(selectSparePart.get("Quantity").valueAsString());
                    //将当前备件的剩余数量减1
                    currentSparePart.set("RealCount",currentSparePart.get("RealCount").valueAsInt()-1);
                    holder.sparePartQuantityVal.setText(currentSparePart.get("RealCount").valueAsString());
                }
            }
            //展示当前备件为选中状态
            holder.select.setVisibility(View.VISIBLE);
            holder.selectNormal.setVisibility(View.INVISIBLE);
            holder.select.setImageResource(R.mipmap.select_pressed);
        }
        else{
            //被选中
            //循环已选中备件集合
            for (ObjectElement SparePartItem:selectItems) {
                //判断是否等于当前选中备件
                if (SparePartItem.get("Stock_item_id").valueAsString().equals(currentSparePart.get("Stock_item_id").valueAsString())){
                    if(inputAmount.length > 0){
                        SparePartItem.set("Quantity",inputAmount[0]);
                    }
                    if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)){
                        if (inputAmount.length == 0) {
                            //设置当前选中备件的选中数量+1
                            SparePartItem.set("Quantity", (SparePartItem.get("Quantity").valueAsInt() + 1));
                            //展示当前备件的选中数量
                            holder.amount.setText(SparePartItem.get("Quantity").valueAsString());
                            //将当前备件的剩余数量减1
                            currentSparePart.set("RealCount", currentSparePart.get("RealCount").valueAsInt() - 1);
                            holder.sparePartQuantityVal.setText(currentSparePart.get("RealCount").valueAsString());
                            return;
                        }
                    }
                    //判断当前选中备件所选数量是否等于当前备件的剩余数量
                    if (SparePartItem.get("Quantity").valueAsInt() >= currentSparePart.get("RealCount").valueAsInt()) {
                        //提示所选数量大于剩余库存
                        ToastUtil.showToastShort(LocaleUtils.getI18nValue("greater_than_inventory"),ctx);
                        //如果超过最大库存，则默认只赋值最大库存量进行操作 By Leo
                        SparePartItem.set("Quantity", currentSparePart.get("Inventory").valueAsInt());
                        holder.amount.setText(SparePartItem.get("Quantity").valueAsString());
                    } else {
                        if (inputAmount.length == 0) {
                            //设置当前选中备件的选中数量+1
                            SparePartItem.set("Quantity", (SparePartItem.get("Quantity").valueAsInt() + 1));
                            //展示当前备件的选中数量
                            holder.amount.setText(SparePartItem.get("Quantity").valueAsString());
                        }
                    }
                }
            }
        }
    }
    //减少选中备件方法
    private void Reduce(int position,ViewHolder holder){
        //获取当前点击项的备件
        final ObjectElement currentSparePart = listItems.get(position);
        String currentSpareItemID = "";
        if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)||TaskClass.equals(Task.SPAREPART_RETURN)){
            currentSpareItemID = currentSparePart.get("Stock_item_id").valueAsString();
        }else if(TaskClass.equals(Task.SPAREPART_CHOOSE)||TaskClass.equals(Task.SPAREPART_CHECK_MESSAGE)){
            currentSpareItemID = currentSparePart.get("Id").valueAsString();
        }

        //判断当前备件是否被选中
        if(selectItemCodes.contains(currentSpareItemID)){
            //被选中
            //定义选中备件对象
            ObjectElement selectSparePartItem = null;
            //循环已选中备件集合
            for (ObjectElement SparePartItem:selectItems) {
                //判断是否等于当前选中备件
                if (SparePartItem.get("Stock_item_id").valueAsString().equals(currentSpareItemID)){
                    selectSparePartItem = SparePartItem;
                }
            }
            //判断选中备件的选中数量是否等于1
            if(selectSparePartItem.get("Quantity").valueAsInt() == 1){
                //从选中备件码集合中移除当前选中备件的备件码
                selectItemCodes.remove(selectSparePartItem.get("Stock_item_id").valueAsString());
                //从选中备件集合中移除当前选中备件
                selectItems.remove(selectSparePartItem);
                //展示当前备件为未选中状态
                holder.select.setVisibility(View.GONE);
                holder.selectNormal.setVisibility(View.VISIBLE);
                //展示当前备件的选中数量
                holder.amount.setText("0");
            }else{
                //修改当前选中备件的选中数量
                selectSparePartItem.set("Quantity",(selectSparePartItem.get("Quantity").valueAsInt()-1));
                //展示当前备件的选中数量
                holder.amount.setText(selectSparePartItem.get("Quantity").valueAsString());
            }
            if(TaskClass.equals(Task.SPAREPART_EQUIPMENT_CHOOSE)){
                currentSparePart.set("RealCount",currentSparePart.get("RealCount").valueAsInt()+1);
                holder.sparePartQuantityVal.setText(currentSparePart.get("RealCount").valueAsString());
            }
        }
    }

    public static class ViewHolder {
        TextView sparePartName;
        TextView sparePartPositionTxt;
        TextView sparePartPositionVal;
        TextView sparePartBrandTxt;
        TextView sparePartBrandVal;
        TextView sparePartTypeTxt;
        TextView sparePartTypeVal;
        TextView sparePartLastReplaceTimeTxt;
        TextView sparePartLastReplaceTimeVal;
        TextView sparePartQuantityTxt;
        TextView sparePartQuantityVal;

        RelativeLayout numAddReduce;
        EditText amount;
        TextView btnReduce;
        TextView btnAdd;

        RelativeLayout sparePartSelect;
        ImageView select;
        ImageView selectNormal;

        LinearLayout selectNum;
        TextView selectNumTxt;

        LinearLayout multi_item;
    }
}
