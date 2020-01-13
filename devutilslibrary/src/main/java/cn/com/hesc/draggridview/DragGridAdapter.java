package cn.com.hesc.draggridview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * ProjectName: FastDev-master
 * ClassName: DragGridAdapter
 * Description: TODO
 * Author: liujunlin
 * Date: 2016-10-24 14:44
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class DragGridAdapter<T> extends BaseAdapter implements DragGridBaseAdapter{

    private List<T> itemlists;
    private LayoutInflater mInflater;
    private int mHidePosition = -1;
    private Context mContext;

    public DragGridAdapter(Context context, List<T> list){
        this.itemlists = list;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return itemlists.size();
    }

    @Override
    public Object getItem(int position) {
        return itemlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 由于复用convertView导致某些item消失了，所以这里不复用item，
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {

    }

    @Override
    public void setHideItem(int hidePosition) {

    }

    @Override
    public void finishDrag() {

    }
}
