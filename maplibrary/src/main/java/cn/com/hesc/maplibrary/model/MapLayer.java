package cn.com.hesc.maplibrary.model;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: MapLayer
 * Description: 图层类
 * Author: liujunlin
 * Date: 2016-04-11 16:19
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MapLayer {


    /**
     * 图层ID
     */
    private int id;

    /**
     * 图层名
     */
    private String name;

    /**
     * 图层别名
     */
    private String aliasName;

    /**
     * 显示状态
     */
    private boolean visible;

    /**
     * 构造函数
     */
    public MapLayer() {
    }

    /**
     * 构造函数
     * @param id 图层ID
     * @param name 图层名
     * @param visible 显示状态（true:显示;false:不显示）
     */
    public MapLayer(int id, String name, boolean visible) {
        this.id = id;
        this.name = name;
        this.visible = visible;
    }

    /**
     * 构造函数
     * @param id 图层ID
     * @param name 图层名
     * @param aliasName 图层别名
     * @param visible 显示状态（true:显示;false:不显示）
     */
    public MapLayer(int id, String name, String aliasName, boolean visible) {
        this.id = id;
        this.name = name;
        this.aliasName = aliasName;
        this.visible = visible;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }


}
