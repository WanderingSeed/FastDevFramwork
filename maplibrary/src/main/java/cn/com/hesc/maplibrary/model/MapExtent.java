package cn.com.hesc.maplibrary.model;

import java.io.Serializable;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: MapExtent
 * Description: 地图范围
 * Author: liujunlin
 * Date: 2016-04-12 16:38
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MapExtent implements Serializable{


    /* 地图坐标 */
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    /* 中点坐标 */
    private MapPoint centerPoint;

    /* 构造方法 */
    public MapExtent() {
    }

    /* 构造方法重载 */
    public MapExtent(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.centerPoint = new MapPoint((this.minX + this.maxX) / 2,
                (this.minY + this.maxY) / 2);
    }

    @Override
    public String toString() {
        return "minx:" + this.minX + ";miny:" + this.minY + ";maxx:"
                + this.maxX + ";maxy:" + this.maxY;
    }

    /* getter以及setter方法 */
    public MapPoint getCenterPoint() {

        return this.centerPoint;
    }

    public void setCenterPoint(MapPoint centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinY() {
        return minY;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxY() {
        return maxY;
    }


}
