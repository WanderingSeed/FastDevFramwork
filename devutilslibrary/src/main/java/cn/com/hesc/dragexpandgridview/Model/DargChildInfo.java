package cn.com.hesc.dragexpandgridview.Model;

/**
 * ProjectName: FastDev-master
 * ClassName: DargChildInfo
 * Description: 表格布局的二级菜单
 * Author: liujunlin
 * Date: 2016-10-28 08:57
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class DargChildInfo {
    /**布局id，采用索引方式，方便排序*/
    protected int id;
    /**布局名称*/
    protected String name;
    /**布局自带的图标名，可为空*/
    protected String srcimgname;


    public DargChildInfo() {
    }


    public DargChildInfo(int id, String name) {
        super();
        this.id = id;
        this.name = name;
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


    public String getSrcimgname() {
        return srcimgname;
    }

    public void setSrcimgname(String srcimgname) {
        this.srcimgname = srcimgname;
    }
}
