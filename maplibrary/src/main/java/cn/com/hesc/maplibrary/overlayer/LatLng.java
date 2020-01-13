package cn.com.hesc.maplibrary.overlayer;

import java.io.Serializable;

/**
 * 保存坐标点位
 * created by liujunlin on 2018/7/16 11:28
 */
public class LatLng implements Serializable{

    private double mLat,mLng;

    /**
     * 构造函数
     * @param lat 纬度
     * @param lng 经度
     */
    public LatLng(double lat,double lng){
        this.mLat = lat;
        this.mLng = lng;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLng() {
        return mLng;
    }

    public void setmLng(double mLng) {
        this.mLng = mLng;
    }
}
