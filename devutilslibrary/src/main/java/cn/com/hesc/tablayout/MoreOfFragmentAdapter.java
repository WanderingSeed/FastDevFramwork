package cn.com.hesc.tablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import java.util.List;

/**
 * ProjectName: FastDev-master
 * ClassName: MoreOfFragmentAdapter
 * Description: 支持较多的多个fragment，内存清除时会记录fragment的状态，只要在fragment的onCreate里判断savedInstanceState即可
 * onSaveInstanceState里会对要销毁的对象状态的保存,可以方便的添加和删除fragment，代价是所有fragment都要重置下
 * Author: liujunlin
 * Date: 2017-02-09 09:45
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public abstract class MoreOfFragmentAdapter<T> extends FragmentStatePagerAdapter{

    private List<Fragment> mFragments;
    private List<T> mTList;

    public MoreOfFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public MoreOfFragmentAdapter(FragmentManager fm, List<Fragment> fragments,List<T> itemObject){
        super(fm);
        mFragments = fragments;
        mTList = itemObject;
    }

    /**
     * 实现自定义tabview的方法，将自己的view加载并返回
     * @param position tab所在的索引
     * @return 返回自定义的view
     * 以下为简单实例
     * View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tabitem,null);
    ImageView im = (ImageView)v.findViewById(R.id.srcicon);
    im.setImageResource(R.drawable.icon_home_more);
    TextView t = (TextView)v.findViewById(R.id.note);
    t.setText(titles.get(position));
    return v;
     */
    public abstract View getTabView(int position);

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 添加fragment
     * @param fragment 碎片
     * @param object 页签对象
     */
    public void addFragment(Fragment fragment,T object){
        mFragments.add(fragment);
        mTList.add(object);
        notifyDataSetChanged();
    }

    /**
     * 添加fragment
     * @param index 添加到对应位置
     * @param fragment 碎片
     * @param object 页签对象
     */
    public void addFragment(int index,Fragment fragment,T object){
        mFragments.add(index,fragment);
        mTList.add(index,object);
        notifyDataSetChanged();
    }

    /**
     * 移除fragment
     * @param index 要移除的位置
     */
    public void removeFragment(int index){
        mFragments.remove(index);
        mTList.remove(index);
        notifyDataSetChanged();
    }
}
