package cn.com.hesc.tablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.util.List;

/**
 * ProjectName: FastDev-master
 * ClassName: FewOfFragmentAdapter
 * Description: 适合比较少的几个fragment，一起加载进内存，加载速度较快;
 * 使用中只要再实现getCount()和getItem(int position)即可，暂不支持动态添加和删除fragment
 * Author: liujunlin
 * Date: 2017-02-09 09:31
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public abstract class FewOfFragmentAdapter<T> extends FragmentPagerAdapter{

    /**要加载的fragment通过构造函数传入*/
    private List<Fragment> mFragments;
    /**要加载的对象，支持泛型*/
    private List<T> mItemObjects;
    private FragmentManager mFragmentManager;

    public FewOfFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public FewOfFragmentAdapter(FragmentManager fm, List<Fragment> fragments,List<T> itemObjects){
        super(fm);
        mFragments = fragments;
        mItemObjects = itemObjects;
        mFragmentManager = fm;
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    public void setFragments(List<Fragment> fragments) {
        if(this.mFragments != null){
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            for(Fragment f:this.mFragments){
                fragmentTransaction.remove(f);
            }
            fragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();
        }
        this.mFragments = fragments;
        notifyDataSetChanged();
    }

    public List<T> getItemObjects() {
        return mItemObjects;
    }

    public void setItemObjects(List<T> itemObjects) {
        mItemObjects = itemObjects;
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

}
