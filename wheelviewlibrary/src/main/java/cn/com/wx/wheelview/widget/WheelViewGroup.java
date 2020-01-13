package cn.com.wx.wheelview.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import java.util.HashMap;
import java.util.List;

import cn.com.hesc.wheelviewlibrary.R;
import cn.com.wx.wheelview.adapter.ArrayWheelAdapter;


/**
 * ProjectName: Grid_Standard
 * ClassName: WheelViewGroup
 * Description: 花轮组合控件，现在就加到3级，项目不允许出现超过3级的菜单
 * Author: liujunlin
 * Date: 2016-11-02 11:11
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class WheelViewGroup extends LinearLayout {

    private Context mContext;
    private WheelView firstwheelview,secondwheelview,thirdwheelview;
    private int firstChoose = 0;
    private int secondChoose = 0;
    private int thirdChoose = 0;

    public WheelViewGroup(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public WheelViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public WheelView getFirstwheelview() {
        return firstwheelview;
    }

    public void setFirstwheelview(WheelView firstwheelview) {
        this.firstwheelview = firstwheelview;
    }

    public WheelView getSecondwheelview() {
        return secondwheelview;
    }

    public void setSecondwheelview(WheelView secondwheelview) {
        this.secondwheelview = secondwheelview;
    }

    public WheelView getThirdwheelview() {
        return thirdwheelview;
    }

    public void setThirdwheelview(WheelView thirdwheelview) {
        this.thirdwheelview = thirdwheelview;
    }

    public int getFirstChoose() {
        return firstChoose;
    }

    public int getSecondChoose() {
        return secondChoose;
    }

    public int getThirdChoose() {
        return thirdChoose;
    }

    private void init(){

        LayoutParams ll = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(ll);

        View parentview = LayoutInflater.from(mContext).inflate(R.layout.wheelviewgroup,null);

        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextColor = Color.parseColor("#0288ce");
        style.textColor = Color.GRAY;
        style.selectedTextSize = 20;

        firstwheelview = (WheelView) parentview.findViewById(R.id.firstwheelview);
        firstwheelview.setTag("first");
        firstwheelview.setSkin(WheelView.Skin.Holo);
        firstwheelview.setStyle(style);
        firstwheelview.setWheelAdapter(new ArrayWheelAdapter(mContext));
        firstwheelview.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                firstChoose = position;
            }
        });

        secondwheelview = (WheelView) parentview.findViewById(R.id.secondwheelview);
        secondwheelview.setTag("second");
        secondwheelview.setSkin(WheelView.Skin.Holo);
        secondwheelview.setStyle(style);
        secondwheelview.setWheelAdapter(new ArrayWheelAdapter(mContext));
        secondwheelview.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                secondChoose = position;
            }
        });

        thirdwheelview = (WheelView) parentview.findViewById(R.id.thirdwheelview);
        thirdwheelview.setTag("third");
        thirdwheelview.setSkin(WheelView.Skin.Holo);
        thirdwheelview.setStyle(style);
        thirdwheelview.setWheelAdapter(new ArrayWheelAdapter(mContext));
        thirdwheelview.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                thirdChoose = position;
            }
        });

        addView(parentview);
    }

    public String getList(){
        String str1 = (String) firstwheelview.getWheelData().get(firstChoose);
        if(secondwheelview.getVisibility() == VISIBLE && secondwheelview.getWheelData().size()>0)
            str1 += "@@@"+(String) secondwheelview.getWheelData().get(secondChoose);
        if(thirdwheelview.getVisibility() == VISIBLE && thirdwheelview.getWheelData().size()>0)
            str1 += "@@@"+(String) thirdwheelview.getWheelData().get(thirdChoose);

        return str1;
    }

    public void setWheelCount(int count){
        if(count <= 1)
            return;
        if(count == 2)
            secondwheelview.setVisibility(VISIBLE);
        if(count >= 3) {
            secondwheelview.setVisibility(VISIBLE);
            thirdwheelview.setVisibility(VISIBLE);
        }
    }

    /**
     * JOIN和Joindata一起使用，进行联动
     * @param parentView
     * @param childView
     */
    public void setJoin(WheelView parentView, WheelView childView){
        parentView.join(childView);
    }

    public void setJoinData(WheelView parentView, HashMap<String,List<String>> map){
        parentView.joinDatas(map);
    }

    public void showData(WheelView wheelView, List<String> datas){
        wheelView.setWheelData(datas);
    }

    public void setDataWithSelection(WheelView wheelView, List<String> datas, String selection){
        wheelView.setWheelData(datas);
        int index = datas.indexOf(selection);
        wheelView.setSelection(index);
    }
}
