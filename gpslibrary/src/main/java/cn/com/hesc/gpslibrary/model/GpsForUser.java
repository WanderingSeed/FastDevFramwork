package cn.com.hesc.gpslibrary.model;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: GpsForUser
 * Description: GPS对外接口程序
 * Author: liujunlin
 * Date: 2016-04-22 11:05
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class GpsForUser {
    private final static String TAG = "GpsForUser";
    private LocationManager locationManager = null;
    private Location mLocation, gpsLocation, networkLocation,othergpsLocation;
    private Context mContext;
    private boolean isGps_enabled = false;
    private boolean isNetwork_enabled = true;
    private boolean isOtherGps_enabled = false;
    private GpsStatechangeListener mGpsStatechangeListener;
    MyGpsStatusLissener gpsStatusLisener = null;
    private ArrayList<GpsStateBean> numSatelliteList = new ArrayList<>();
    private static volatile GpsForUser instance;
    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    /**是否允许获取GPS的缓存位置，默认为允许*/
    private boolean allowCacheGps = true;

    public static GpsForUser getInstance(Context context) {
        if (instance == null) {
            synchronized (GpsForUser.class) {
                if (instance == null) {
                    instance = new GpsForUser(context.getApplicationContext());
                }
            }
        }

        return instance;
    }

    public GpsForUser(Context context) {
        mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        isWifiOrCell();
    }

    /**
     * 监听卫星变化
     * @param gpsStatechangeListener
     */
    public void setGpsStatechangeListener(GpsStatechangeListener gpsStatechangeListener) {
        mGpsStatechangeListener = gpsStatechangeListener;
    }

    public boolean isAllowCacheGps() {
        return allowCacheGps;
    }

    /**
     * 是否允许获取GPS的缓存位置
     * @param allowCacheGps
     */
    public void setAllowCacheGps(boolean allowCacheGps) {
        this.allowCacheGps = allowCacheGps;
    }

    /**
     * 是否打开GPS
     * @return true 打开了GPS定位
     */
    public boolean isGPSOpen() {
        try {
            isGps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            setGpsInfo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GPS provider", "is null");
        }

        return isGps_enabled;
    }

    /**
     * 是否打开了基站或者wifi
     * @return true 网络定位服务开启
     */
    public boolean isWifiOrCell() {
        try {
            isNetwork_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            setNetWorkInfo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("network provider", "is null");
        }

        return isNetwork_enabled;
    }

    private void setGpsInfo() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 高精度
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
            String best = locationManager.getBestProvider(criteria, true);
            if(best!=null){
                if (best.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
//                    best = LocationManager.GPS_PROVIDER;
                    locationManager.requestLocationUpdates(best, 1000L, 0.0F, gpsLocationListener);
                }else if(best.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)){
                    isWifiOrCell();
                }else if(best.equalsIgnoreCase(LocationManager.PASSIVE_PROVIDER)){
                    locationManager.requestLocationUpdates(best, 10000L, 10.0F, otherGpsLocationListener);
                }
            }
        } else {
            Toast.makeText(mContext, "GPS权限已被禁止,请先设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * gps数据
     */
    private final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpsLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("gg",provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            if(LocationManager.GPS_PROVIDER.equals(provider))
                isGps_enabled = true;
//            Log.e("gg",provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(LocationManager.GPS_PROVIDER.equals(provider))
                isGps_enabled = false;
//            Log.e("gg bad",provider);
        }
    };

    /**
     * 检测卫星情况
     */
    class MyGpsStatusLissener implements GpsStatus.Listener {

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        GpsStatus gpsStatus1 = locationManager.getGpsStatus(null);
                    }

                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    numSatelliteList.clear();
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    if(gpsStatus != null){
                        Iterable<GpsSatellite> gpsSatellites = gpsStatus.getSatellites();
                        if(null == gpsSatellites)
                            return;
                        int maxSatellites = gpsStatus.getMaxSatellites();
                        Iterator<GpsSatellite> iters = gpsSatellites
                                .iterator();
                        int count = 0;
                        while (iters.hasNext() && count <= maxSatellites) {
                            GpsSatellite s = iters.next();
                            GpsStateBean gBean = new GpsStateBean();
                            gBean.setGs(s);
                            numSatelliteList.add(gBean);

                        }
                    }
                    if (mGpsStatechangeListener != null)
                        mGpsStatechangeListener.OnGpsChanged(numSatelliteList);
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "start");
                    if (mGpsStatechangeListener != null)
                        mGpsStatechangeListener.OnGpsOpened();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "closed");
                    if (mGpsStatechangeListener != null)
                        mGpsStatechangeListener.OnGpsClosed();
                    break;
            }
        }
    }


    private void setNetWorkInfo() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            try{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 100, networkLocationListener);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    /**
     * 网络监听
     */
    private LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            networkLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(provider,""+status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            if(LocationManager.NETWORK_PROVIDER.equals(provider))
                isNetwork_enabled = true;
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(LocationManager.NETWORK_PROVIDER.equals(provider))
                isNetwork_enabled = false;
        }
    };

    /**
     * 其他GPS源
     */
    private LocationListener otherGpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            othergpsLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(provider,""+status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            if(LocationManager.PASSIVE_PROVIDER.equals(provider))
                isOtherGps_enabled = true;
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(LocationManager.PASSIVE_PROVIDER.equals(provider))
                isOtherGps_enabled = false;
        }
    };

    /**
     * 获取位置信息
     * @return 获取到的位置数据结构,无位置信息返回null
     */
    public Location getLocation() {

        if (isGps_enabled && gpsLocation != null) {
            mLocation = gpsLocation;
        }
        else if (isNetwork_enabled && networkLocation != null) {
            mLocation = networkLocation;
        }else if(isOtherGps_enabled && othergpsLocation != null){
            mLocation = othergpsLocation;
        }
        //缓存位置放弃
        else {
            if(allowCacheGps){
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
        return mLocation;
    }

    /**
     * 程序退出时注销所有的GPS服务
     */
    public void stopLocationService() {
        if (locationManager != null) {
            if (gpsLocationListener != null)
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(gpsLocationListener);
                }
            if(gpsStatusLisener!=null)
                locationManager.removeGpsStatusListener(gpsStatusLisener);
            if(networkLocationListener!=null)
                locationManager.removeUpdates(networkLocationListener);
        }
    }

    /**
     * 将GPS经纬度转为本地坐标
     * @param coordinate 本地坐标系
     * @param geoData 可变的地图参数
     * @return
     */
    public GeoLocation getGeoLocation(GpsData2GeoData.Coordinate coordinate, GeoData geoData){
        GeoLocation geoLocation = new GeoLocation();
        GpsData2GeoData gpsData2GeoData = new GpsData2GeoData(mLocation.getLatitude(), mLocation.getLongitude(),coordinate,geoData);
        geoLocation.x = Double.parseDouble(TextUtils.isEmpty(gpsData2GeoData.getGeoLogtitude())?"0":gpsData2GeoData.getGeoLogtitude());
        geoLocation.y = Double.parseDouble(TextUtils.isEmpty(gpsData2GeoData.getGeoLatitude())?"0":gpsData2GeoData.getGeoLatitude());

        return geoLocation;
    }
}
