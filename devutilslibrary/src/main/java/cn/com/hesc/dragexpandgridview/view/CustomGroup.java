package cn.com.hesc.dragexpandgridview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.dragexpandgridview.Model.DargChildInfo;
import cn.com.hesc.dragexpandgridview.Model.DragIconInfo;

/**
 * ProjectName: FastDev-master
 * ClassName: CustomGroup
 * Description: TODO
 * Author: liujunlin
 * Date: 2016-10-28 08:55
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class CustomGroup extends ViewGroup{



    private CustomAboveView mCustomAboveView;
    public static final int COLUMNUM = 3;
    private Context mContext;
    //所有以的list
    private List<DragIconInfo> allInfoList = new ArrayList<DragIconInfo>();
    /**显示的带more的list*/
    private ArrayList<DragIconInfo> homePageInfoList = new ArrayList<DragIconInfo>();
    /**可展开的list*/
    private ArrayList<DragIconInfo> expandInfoList = new ArrayList<DragIconInfo>();

    /**不可展开的list*/
    private ArrayList<DragIconInfo> onlyInfoList = new ArrayList<DragIconInfo>();

    private InfoEditModelListener editModelListener;

    public interface InfoEditModelListener {
        public void onModleChanged(boolean isEditModel);
    }

    public CustomGroup(Context context){
        super(context);
    }

    public CustomAboveView getmCustomAboveView() {
        return mCustomAboveView;
    }

    /**
     *
     * 标题: 构造器 <p>
     * 描述: TODO <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午5:29:30 <p>
     * @param context
     * @param attrs
     */
    public CustomGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutParams upParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        mCustomAboveView = new CustomAboveView(context, this);
        addView(mCustomAboveView, upParams);
    }

    public InfoEditModelListener getEditModelListener() {
        return editModelListener;
    }

    public void setEditModelListener(InfoEditModelListener editModelListener) {
        this.editModelListener = editModelListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasure = 0;
        int heightMeasure = 0;
        mCustomAboveView.measure(widthMeasureSpec, heightMeasureSpec);
        widthMeasure = mCustomAboveView.getMeasuredWidth();
        heightMeasure = mCustomAboveView.getMeasuredHeight();
        setMeasuredDimension(widthMeasure, heightMeasure);

    }

    /**
     * 方法: onLayout <p>
     * 描述: TODO<p>
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b <p>
     * @see ViewGroup#onLayout(boolean, int, int, int, int) <p>
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int aboveHeight = mCustomAboveView.getMeasuredHeight();
        mCustomAboveView.layout(l, 0, r, aboveHeight + t);
    }

    /**
     *
     * 方法: initIconInfo <p>
     * 描述: 初始化数据 <p>
     * 参数:  <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午5:33:14
     */
    public void initIconInfo(List<DragIconInfo> dragIconInfos) {
        if(dragIconInfos!=null && dragIconInfos.size()>0){
            allInfoList.clear();
            allInfoList.addAll(dragIconInfos);
            getPageInfoList();
            refreshIconInfo();
        }

//        setCustomViewClickListener(new CustomAboveView.CustomAboveViewClickListener() {
//
//            @Override
//            public void onSingleClicked(DragIconInfo iconInfo) {
//                // TODO Auto-generated method stub
//                dispatchSingle(iconInfo);
//            }
//
//            @Override
//            public void onChildClicked(DargChildInfo childInfo) {
//                // TODO Auto-generated method stub
//                dispatchChild((childInfo));
//            }
//        });
    }

    /**
     * 过滤主页面展示的信息
     */
    private void getPageInfoList() {
        homePageInfoList.clear();
        for (DragIconInfo info : allInfoList) {
            if (info.isVisit() && info.isShow()) {
                homePageInfoList.add(info);
            }
        }
    }

    /**
     *
     * 方法: refreshIconInfo <p>
     * 描述: 刷新信息 <p>
     * 参数:  <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午5:38:11
     */
    private void refreshIconInfo() {
        judeHomeInfoValid();

//        ArrayList<DragIconInfo> moreInfo = getMoreInfoList(allInfoList, homePageInfoList);
//        expandInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_EXPAND);
//        onlyInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_ONLY);
        expandInfoList = getInfoByType(homePageInfoList, DragIconInfo.CATEGORY_EXPAND);
        onlyInfoList = getInfoByType(homePageInfoList, DragIconInfo.CATEGORY_ONLY);
        setIconInfoList(homePageInfoList);
    }



    /**
     *
     * 方法: judeHomeInfoValid <p>
     * 描述: 判断下显示里面是否包含更多 或者看下是否是最后一个 固定更多的位置 <p>
     * 参数:  <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午5:38:37
     */
    private void judeHomeInfoValid() {
        boolean hasMoreInfo = false;
        int posit = 0;
        for(int index = 0;index<homePageInfoList.size();index++){
            DragIconInfo tempInfo = homePageInfoList.get(index);
            if(tempInfo.getId()==CustomAboveView.MORE){
                hasMoreInfo = true;
                posit = index;
                break;
            }
        }
        if(!hasMoreInfo){
            //没有更多 增加
            homePageInfoList.add(new DragIconInfo(CustomAboveView.MORE, "更多", R.drawable.icon_home_more, 0, new ArrayList<DargChildInfo>()));
        }else{
            if(posit!=homePageInfoList.size()-1){
                //排序，把更多放最后
                DragIconInfo moreInfo = homePageInfoList.remove(posit);
                homePageInfoList.add(moreInfo);
            }
        }
    }


    /**
     *
     * 方法: getInfoByType <p>
     * 描述: TODO <p>
     * 参数: @param moreInfo
     * 参数: @param categorySpt
     * 参数: @return <p>
     * 返回: ArrayList<DragIconInfo> <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午6:50:25
     */
    private ArrayList<DragIconInfo> getInfoByType(ArrayList<DragIconInfo> moreInfo, int categorySpt) {
        ArrayList<DragIconInfo> typeList = new ArrayList<DragIconInfo>();
        for (DragIconInfo info : moreInfo) {
            if (info.getCategory() == categorySpt) {
                typeList.add(info);
            }
        }
        return typeList;
    }


    public void setCustomViewClickListener(CustomAboveView.CustomAboveViewClickListener gridViewClickListener) {
        mCustomAboveView.setGridViewClickListener(gridViewClickListener);
    }

    /**
     *
     * 方法: setIconInfoList <p>
     * 描述: 设置信息 <p>
     * 参数: @param iconInfoList <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午6:45:55
     */
    public void setIconInfoList(ArrayList<DragIconInfo> iconInfoList) {
        mCustomAboveView.refreshIconInfoList(iconInfoList);
    }

    /**
     *
     * 方法: getMoreInfoList <p>
     * 描述: TODO <p>
     * 参数: @param allInfoList
     * 参数: @param homePageInfoList
     * 参数: @return <p>
     * 返回: ArrayList<DragIconInfo> <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午6:57:06
     */
    private ArrayList<DragIconInfo> getMoreInfoList(List<DragIconInfo> allInfoList, List<DragIconInfo> homePageInfoList) {
        ArrayList<DragIconInfo> moreInfoList = new ArrayList<DragIconInfo>();
        moreInfoList.addAll(allInfoList);
        moreInfoList.removeAll(homePageInfoList);
        return moreInfoList;
    }



    /**
     *
     * 方法: deletHomePageInfo <p>
     * 描述: TODO <p>
     * 参数: @param dragIconInfo <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午6:56:19
     */
    public void deletHomePageInfo(DragIconInfo dragIconInfo) {
        homePageInfoList.remove(dragIconInfo);
        mCustomAboveView.refreshIconInfoList(homePageInfoList);
        int category = dragIconInfo.getCategory();
        switch (category) {
            case DragIconInfo.CATEGORY_ONLY:
                onlyInfoList.add(dragIconInfo);
                break;
            case DragIconInfo.CATEGORY_EXPAND:
                expandInfoList.add(dragIconInfo);
                break;
            default:
                break;
        }
        allInfoList.remove(dragIconInfo);
        allInfoList.add(dragIconInfo);
    }




    /**
     *
     * 方法: dispatchChild <p>
     * 描述: 点击child <p>
     * 参数: @param childInfo <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午5:30:58
     */
    protected void dispatchChild(DargChildInfo childInfo) {
        if (childInfo == null) {
            return;
        }
        Toast.makeText(mContext, "点击了item"+childInfo.getName(), Toast.LENGTH_SHORT).show();

    }


    /**
     *
     * 方法: dispatchSingle <p>
     * 描述: 没child的点击 <p>
     * 参数: @param dragInfo <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午5:30:40
     */
    public void dispatchSingle(DragIconInfo dragInfo) {
        if (dragInfo == null) {
            return;
        }
        Toast.makeText(mContext, "点击了icon"+dragInfo.getName(), Toast.LENGTH_SHORT).show();


    }
}
