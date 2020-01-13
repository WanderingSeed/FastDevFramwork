package cn.com.hesc.maplibrary.model;

import android.graphics.Paint;

import java.io.Serializable;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: MapPoint
 * Description: 地图点
 * Author: liujunlin
 * Date: 2016-04-11 16:20
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MapPoint implements Serializable{


    /* 点坐标参数 */
    private double x;
    private double y;
    private MapPaint paint;

    /* 构造方法 */
    public MapPoint() {
    }

    /* 构造方法重载 */
    public MapPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MapPoint(double x,double y,MapPaint paint){
        this.x = x;
        this.y = y;
        this.paint = paint;
    }

    /* getter以及setter方法 */
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public MapPaint getPaint() {
        return paint;
    }

    public void setPaint(MapPaint paint) {
        this.paint = paint;
    }
}
