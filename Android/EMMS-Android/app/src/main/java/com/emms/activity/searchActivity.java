package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.util.LocaleUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaffer.deng on 2016/6/14.
 *
 */
public class searchActivity extends NfcActivity implements View.OnClickListener {

    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private int index;
    private ArrayList<String> dataLists;
    private boolean tag = false;
    private int type;
    public final static int RESULT_CODE =10001;
    public final static String BACK_CONTENT ="back_content";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {

    }


    private void initData() {
        dataLists =getIntent().getStringArrayListExtra(CreateTaskActivity.FORM_CONTENT);
        type = getIntent().getIntExtra(CreateTaskActivity.FORM_TYPE,0);

        mResultAdapter = new ResultListAdapter(this);
        if (dataLists!=null) {
            if (dataLists.size() > 0) {
//                mResultAdapter.changeData(dataLists);
            }
        }else {
            Toast.makeText(this,"获取数据失败",Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        if (type == CreateTaskActivity.TASK_TYPE) {
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("title_search_task_type"));
        }else if (type ==CreateTaskActivity.TASK_SUBTYPE){
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("title_search_task_subtype"));
        }else if (type ==CreateTaskActivity.GROUP){
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("title_search_group"));
        }else if (type ==CreateTaskActivity.DEVICE_NAME){
            ((TextView) findViewById(R.id.tv_title)).setText(LocaleUtils.getI18nValue("title_search_equipment_name"));
        }


    }

    private ArrayList<String> search(String keyword) {
        ArrayList<String> reDatas = new ArrayList<>();
        for (int i = 0; i < dataLists.size(); i++) {
            if (dataLists.get(i).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(dataLists.get(i));
            }
        }
        return reDatas;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_clear:
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
                break;
            case R.id.btn_right_action:
                finish();
                break;

        }
    }
        @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
