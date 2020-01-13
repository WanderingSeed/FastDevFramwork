package cn.com.hesc.pullDownView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * ProjectName: FastDevDemo
 * ClassName: CommonAdapter
 * Description: 构建万能的适配器
 * Author: liujunlin
 * Date: 2016-09-11 16:58
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public abstract class CommonAdapter<T> extends BaseAdapter{

    private List<T> beans;
    private Context mContext;
    private int mLayoutid;

    public CommonAdapter(Context context,int layoutid,List<T> datas){
        mContext = context;
        beans = datas;
        mLayoutid = layoutid;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHandler viewHandler = ViewHandler.getViewHandler(mContext,parent,convertView,mLayoutid,position);
        conver(viewHandler,beans.get(position),position);
        return viewHandler.getConvertView(position);
    }

    public abstract void conver(ViewHandler viewHandler,T item, int position);

    public void safeRrefush(List<T> datas,boolean isRefresh){
        if(beans != null && datas != null){
            if(isRefresh)
                beans.addAll(0,datas);
            else
                beans.addAll(datas);
        }
        notifyDataSetChanged();
    }
}
