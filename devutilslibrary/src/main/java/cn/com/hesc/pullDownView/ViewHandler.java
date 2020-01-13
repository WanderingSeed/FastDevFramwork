package cn.com.hesc.pullDownView;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.com.hesc.devutilslibrary.R;


/**
 * ProjectName: FastDevDemo
 * ClassName: ViewHandler
 * Description: 创建万能的viewhandler来适应listview
 * Author: liujunlin
 * Date: 2016-09-11 16:33
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class ViewHandler {
    private SparseArray<View> mViewSparseArray;
    private View mConvertView;
    private int mPosition;
    private Context context;

    public ViewHandler(Context context, ViewGroup parent,int layoutid,int position){
        this.context = context;
        mPosition = position;
        mConvertView = LayoutInflater.from(context).inflate(layoutid,parent,false);
        mConvertView.setTag(this);
        mViewSparseArray = new SparseArray<>();

    }

    /**
     * 针对getview里的缓存写法
     * @param context
     * @param parent
     * @param convertView
     * @param layoutid
     * @param position
     * @return
     */
    public static ViewHandler getViewHandler(Context context,ViewGroup parent,View convertView,int layoutid,int position){
        if(convertView == null){
            return  new ViewHandler(context,parent,layoutid,position);
        }else{
            return (ViewHandler) convertView.getTag();
        }
    }

    public View getConvertView(int position){
        return mConvertView;
    }

    /**
     * 根据ID来或者对应item里的组件
     * @param viewid
     * @param <T>
     * @return
     */
    public <T extends View> T getItemView(int viewid){
        View view = mViewSparseArray.get(viewid);
        if(view == null){
            view = mConvertView.findViewById(viewid);
            mViewSparseArray.put(viewid,view);
        }
        return (T)view;
    }

    public ViewHandler setText(int viewid,String text){
        TextView textView = getItemView(viewid);
        textView.setText(TextUtils.isEmpty(text)?"":text);
        return this;
    }

    public ViewHandler setImageRes(int viewid,int imgresourceid){
        ImageView imageView = getItemView(viewid);
        imageView.setImageResource(imgresourceid);
        return this;
    }

    public ViewHandler setImageBtm(int viewid, Bitmap bitmap){
        ImageView imageView = getItemView(viewid);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    public ViewHandler setImageUrl(int viewid,String url){
        ImageView imageView = getItemView(viewid);
        Glide.with(context).load(url).into(imageView);
        return this;
    }

    public ViewHandler setButtonText(int viewid,String text){
        Button button = getItemView(viewid);
        button.setText(text);
        return this;
    }
}
