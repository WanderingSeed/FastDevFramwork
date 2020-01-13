package cn.com.hesc.dragexpandgridview.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName: FastDev-master
 * ClassName: DragIconInfo
 * Description: 表格布局的一级菜单
 * Author: liujunlin
 * Date: 2016-10-28 08:56
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class DragIconInfo {


    /**
     * 可展开的
     **/
    public static final int CATEGORY_EXPAND = 100;

    /**
     * 不可展开的
     **/
    public static final int CATEGORY_ONLY = 300;
    /**布局id，采用索引方式，方便排序*/
    protected int id;
    /**布局名称*/
    protected String name;
    /**布局要展示的图片resourceid*/
    protected int resIconId;
    /**
     * 类型
     **/
    protected int category;
    /**
     * 是否可显实，默认都不显示
     */
    protected boolean isShow = false;
    /***
     * 是否可用，默认可以用
     */
    protected boolean isVisit = true;
    protected String picFileName;
    protected Context mContext;

    /**
     * 展开的child
     */
    private List<DargChildInfo> childList = new ArrayList<DargChildInfo>();

    public DragIconInfo() {
    }

    public DragIconInfo(Context context){
        mContext = context;
    }

    public String getPicFileName() {
        return picFileName;
    }

    public void setPicFileName(String picFileName) {
        this.picFileName = picFileName;

        setResIconId(mContext.getResources().getIdentifier(picFileName,
                "drawable", mContext.getPackageName()));
    }

    public DragIconInfo(int id, String name, int resIconId, int category,
                        ArrayList<DargChildInfo> childList) {
        super();
        this.id = id;
        this.name = name;
        this.resIconId = resIconId;
        this.category = category;
        this.childList = childList;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getResIconId() {
        return resIconId;
    }


    public void setResIconId(int resIconId) {
        this.resIconId = resIconId;
    }


    public int getCategory() {
        return category;
    }


    public void setCategory(int category) {
        this.category = category;
    }


    public List<DargChildInfo> getChildList() {
        return childList;
    }


    public void setChildList(List<DargChildInfo> childList) {
        this.childList = childList;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isVisit() {
        return isVisit;
    }

    public void setVisit(boolean visit) {
        isVisit = visit;
    }
}
