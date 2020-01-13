package cn.com.hesc.tablayout;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;

/**
 * ProjectName: FastDev-master
 * ClassName: TablayoutWithViewPager
 * Description: tab页签和viewpager一起展示多个fragment，少量fragment采用FewOfFragmentAdapter，多个frag用MoreOfFragmentAdapter
 * 效果如网易新闻APP的上导航栏，可以滑动选择
 * Author: liujunlin
 * Date: 2017-02-09 09:13
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class TablayoutWithViewPager extends RelativeLayout{

    private Context mContext;
    protected TabLayout mTableLayout;
    protected ViewPager mViewPager;
    private boolean isTop = true;
    /**保存要显示的所有fragment*/
    protected List<Fragment> mFragments = new ArrayList<>();

    public TablayoutWithViewPager(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public TablayoutWithViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public TablayoutWithViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    protected void initView(){
        if(isTop){
            View view = LayoutInflater.from(mContext).inflate(R.layout.tablayout,null);
            mTableLayout = (TabLayout) view.findViewById(R.id.tablayout);
            mViewPager = (ViewPager)view.findViewById(R.id.viewpage);
            addView(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.tablayoutofbottom,null);
            mTableLayout = (TabLayout) view.findViewById(R.id.tablayout);
            mViewPager = (ViewPager)view.findViewById(R.id.viewpage);
            addView(view);
        }
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
        removeAllViews();
        initView();
    }

    /**
     * 将viewpager里的项和tab页签关联起来
     * @param pagerAdapter FewOfFragmentAdapter或者MoreOfFragmentAdapter的实例
     */
    public void resetTab(PagerAdapter pagerAdapter){

        mViewPager.setAdapter(pagerAdapter);
        mTableLayout.setupWithViewPager(mViewPager);

        int count = 0;

        if(pagerAdapter instanceof FewOfFragmentAdapter)
            count = ((FewOfFragmentAdapter) pagerAdapter).getFragments().size();
        else if(pagerAdapter instanceof MoreOfFragmentAdapter)
            count = pagerAdapter.getCount();
//        if(count > 3)
            mTableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        else
//            mTableLayout.setTabMode(TabLayout.MODE_FIXED);

        for (int i = 0,size = count; i < size; i++) {
            TabLayout.Tab tab = mTableLayout.getTabAt(i);
            if(pagerAdapter instanceof FewOfFragmentAdapter)
                tab.setCustomView(((FewOfFragmentAdapter)pagerAdapter).getTabView(i));
            else if(pagerAdapter instanceof MoreOfFragmentAdapter)
                tab.setCustomView(((MoreOfFragmentAdapter)pagerAdapter).getTabView(i));
        }
    }

    public TabLayout getTableLayout() {
        return mTableLayout;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

}
