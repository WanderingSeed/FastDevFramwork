package cn.com.hesc.gpslibrary.model;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: GpsData2GeoData
 * Description: 将gps数据转为本地坐标的计算方法
 * Author: liujunlin
 * Date: 2016-04-28 15:02
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class GpsData2GeoData {

     double Pi = 3.14159265358979;
     double E2 = 0;
     double A = 0;
     double X0 = 0.0;
     double Y0 = 500000.0;
    private RefData m_robj;
    private String m_lat;
    private String m_log;
    private Coordinate mCoordinate = Coordinate.COORDINATE_80;
    private GeoData mGeoData;

    /**
     * 本地坐标系分为54和80坐标系，转换需要找gis问清楚坐标系
     */
    public enum Coordinate{
        COORDINATE_54,
        COORDINATE_80
    }



    /**
     * 输入GPS数据
     * @param lat 纬度
     * @param log 经度
     * @param coordinate 54或80坐标系
     * @param geoData 转为本地坐标时的参数
     */
    public GpsData2GeoData(double lat, double log,Coordinate coordinate,GeoData geoData) {
        m_lat = String.valueOf(lat);
        m_log = String.valueOf(log);

        this.mCoordinate = coordinate;
        if(mCoordinate == Coordinate.COORDINATE_80){
            E2 = 0.006694385048818807;
            A = 6378140.0;
        }else if(mCoordinate == Coordinate.COORDINATE_54){
            E2 = 0.006693421622965949;
            A = 6378245.0;
        }
        mGeoData = geoData;
        m_robj = getData();
    }

    /**
     * 转换后的纵坐标
     * @return
     */
    public String getGeoLatitude() {
        return m_robj.getLGpsX();
    }

    /**
     * 转换后的横坐标
     * @return
     */
    public String getGeoLogtitude() {
        return m_robj.getLGpsY();
    }

    /**
     * 将经纬度转换为弧度 coordinate 度为单位
     */
    private // 数据度转化成弧度
    double Dms2rad2(String coordinate) {
        double dd = Double.parseDouble(coordinate);
        dd = dd * Pi / 180.0;
        return dd;
    }

    private RefData getData() {
        RefData robj3 = gaussxy(Dms2rad2(m_lat), Dms2rad2(m_log));
        return conventToLocal(robj3);
    }

    /**
     * 四参数变换
     *
     * @param robj
     *            RefData
     * @return RefData
     */
    private  RefData conventToLocal(RefData robj) {
        double gx = 0;
        double gy = 0;
        gx = Double.parseDouble(robj.getLGpsX());
        gy = Double.parseDouble(robj.getLGpsY());
        // 平移+旋转
        double gx0 = mGeoData.getDetX() + mGeoData.getK() * (gx * Math.cos(mGeoData.getTi()) + Math.sin(mGeoData.getTi()) * gy);
        double gy0 = mGeoData.getDetY() + mGeoData.getK() * (gy * Math.cos(mGeoData.getTi()) - Math.sin(mGeoData.getTi()) * gx);

        robj.setLGpsX(String.valueOf(gx0));
        robj.setLGpsY(String.valueOf(gy0));

        return robj;
    }

    /**
     * 四参数变换
     *
     * @param robj
     *            RefData
     * @return RefData
     */
    private  RefData conventToLocal_lanzhou(RefData robj) {
        double gx = 0;
        double gy = 0;
        gx = Double.parseDouble(robj.getLGpsX());
        gy = Double.parseDouble(robj.getLGpsY());
        // 平移+旋转
        double gx0 = mGeoData.getDetX() + mGeoData.getK() * (gx * Math.cos(mGeoData.getTi()) + Math.sin(mGeoData.getTi()) * gy);
        double gy0 = mGeoData.getDetY() + mGeoData.getK() * (gy * Math.cos(mGeoData.getTi()) - Math.sin(mGeoData.getTi()) * gx);

        robj.setLGpsX(String.valueOf(gx0));
        robj.setLGpsY(String.valueOf(gy0));

        return robj;
    }

    /**
     * 高斯投影
     *
     * @param b
     *            double 纬度
     * @param l
     *            double 经度
     * @return RefData 投影之后54平面坐标
     */
    private  RefData gaussxy(double b, double l) {
        RefData robj = new RefData();
        double gx = 0;
        double gy = 0;
        double x2, x4, x6, x8, x10, x12;
        x2 = E2;
        x4 = x2 * x2;
        x6 = x2 * x4;
        x8 = x4 * x4;
        x10 = x2 * x8;
        x12 = x6 * x6;
        double aa, bb, cc, dd, ee, ff;
        aa = 1.0 + 3.0 * x2 / 4.0 + 45.0 * x4 / 64.0 + 175.0 * x6 / 256.0;
        aa = aa + 11025.0 * x8 / 16384.0 + 43659.0 * x10 / 65536.0;
        bb = 3.0 * x2 / 4.0 + 15.0 * x4 / 16.0 + 525.0 * x6 / 512.0;
        bb = bb + 2205.0 * x8 / 2048.0 + 72765.0 * x10 / 65536.0;
        cc = 15.0 * x4 / 64.0 + 105.0 * x6 / 256.0;
        cc = cc + 2205.0 * x8 / 4096.0 + 10395.0 * x10 / 16384.0;
        dd = 35.0 * x6 / 512.0 + 315.0 * x8 / 2048.0 + 31185.0 * x10 / 13072.0;
        ee = 315.0 * x8 / 16384.0 + 3465.0 * x10 / 65536.0;
        ff = 693.0 * x10 / 131072.0;
        double a1, a2, a3, a4, a5, a6;
        a1 = aa * A * (1.0 - E2);
        a2 = -bb * A * (1.0 - E2) / 2.0;
        a3 = cc * A * (1.0 - E2) / 4.0;
        a4 = -dd * A * (1.0 - E2) / 6.0;
        a5 = ee * A * (1.0 - E2) / 8.0;
        a6 = -ff * A * (1.0 - E2) / 10.0;

        double r0, r1, r2, r3;
        r0 = a1;
        r1 = 2.0 * a2 + 4.0 * a3 + 6.0 * a4;
        r2 = -8.0 * a3 - 32.0 * a4;
        r3 = 32.0 * a4;

        double Tb, Y2, N;
        Tb = Math.tan(b);
        Y2 = x2 / (1.0 - x2) * Math.cos(b) * Math.cos(b);
        N = A / Math.sqrt(1 - E2 * Math.sin(b) * Math.sin(b));
        double x0, M0, M2;
        // 子午线弧长
        x0 = r0
                * b
                + Math.cos(b)
                * Math.sin(b)
                * (r1 + Math.sin(b) * Math.sin(b)
                * (r2 + Math.sin(b) * Math.sin(b) * r3));

        double ll = l - mGeoData.getL0();
        M0 = Math.cos(b) * ll;
        M2 = M0 * M0;
        gx = x0
                + N
                * Tb
                * Math.pow(M0, 2)
                * (0.5
                + 1.0
                / 24.0
                * Math.pow(M0, 2)
                * (5.0 - Math.pow(Tb, 2) + 9.0 * Y2 + 4.0 * Math.pow(
                Y2, 2)) + 1.0 / 720.0 * Math.pow(M0, 4)
                * (61.0 - 58.0 * Math.pow(Tb, 2) + Math.pow(Tb, 4)));

        gy = N
                * M0
                * (1.0 + 1.0 / 6.0 * Math.pow(Math.cos(b), 2)
                * (1.0 - Math.pow(Tb, 2) + Y2) * Math.pow(ll, 2) + 1.0
                / 120.0
                * Math.pow(Math.cos(b), 4)
                * (5.0 - 18.0 * Math.pow(Tb, 2) + Math.pow(Tb, 4)
                + 14.0 * Y2 - 58.0 * Math.pow(Tb, 2) * Y2)
                * Math.pow(ll, 4));
        // 投影后加上常量
        gy = (gy + Y0);
        gx = (gx + X0);
        robj.setLGpsX(String.valueOf(gx));
        robj.setLGpsY(String.valueOf(gy));
        return robj;
    }

}
