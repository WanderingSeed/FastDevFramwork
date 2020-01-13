package cn.com.hesc.gpslibrary.model;

/**
 * ProjectName: HescLibrary_GPS_Master
 * ClassName: GeoData
 * Description: 可变参数部分，将经纬度转为本地坐标
 * Author: liujunlin
 * Date: 2017-05-02 16:53 
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class GeoData {
    double Pi = 3.14159265358979;
    double L0 = 0;
    double detX = 0;
    double detY = 0;
    double ti = 0;
    double k = 0;

    public double getL0() {
        return L0 * Pi / 180.0;
    }

    public void setL0(double l0) {
        L0 = l0;
    }

    public double getDetX() {
        return detX;
    }

    public void setDetX(double detX) {
        this.detX = detX;
    }

    public double getDetY() {
        return detY;
    }

    public void setDetY(double detY) {
        this.detY = detY;
    }

    public double getTi() {
        return ti;
    }

    public void setTi(double ti) {
        this.ti = ti;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }
}
