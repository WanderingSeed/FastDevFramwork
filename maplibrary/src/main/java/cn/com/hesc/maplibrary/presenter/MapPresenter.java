package cn.com.hesc.maplibrary.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapLayer;
import cn.com.hesc.maplibrary.model.MapModel;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.model.PartsObjectAttributes;
import cn.com.hesc.maplibrary.view.iMapView;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: MapPresenter
 * Description: 地图接口类
 * Author: liujunlin
 * Date: 2016-04-12 16:02
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MapPresenter implements iMapPresenter{
    private iMapView mIMapView;
    private MapModel mMapModel;
    private MapExtent mMapExtent;
    private int curMapLevel = 0;
    private int minMapLevel = 0;
    private int MaxLevel = 0;

    public MapPresenter(iMapView imapView, Context context){
        this.mIMapView = imapView;
        mMapModel = new MapModel(context);
    }

    public MapExtent getMapExtent() {
        return mMapExtent;
    }

    public void setMapExtent(MapExtent mapExtent) {
        mMapExtent = mapExtent;
    }

    /**
     * 初始地图信息
     */
    @Override
    public void initMapInfo(String basicLayerUrl,String partLayerUrl,String annotationUrl,iMapView.MapType mapType) {
        mIMapView.showProgressbar();
        if(mapType == iMapView.MapType.TIANDI || mapType == iMapView.MapType.OPENLAYER) {
            mMapModel.setMapExtent(mMapExtent);
            if(MaxLevel != 0)
                mMapModel.setMapMaxLevel(MaxLevel);
            if(minMapLevel != 0)
                mMapModel.setMapMinLevel(minMapLevel);
            if(curMapLevel != 0)
                mMapModel.setCurMapLevel(curMapLevel);
        }
        initMapInfo(basicLayerUrl,partLayerUrl,annotationUrl,mapType,null);
    }

    /**
     * 初始地图信息
     */
    @Override
    public void initMapInfo(String basicLayerUrl,String partLayerUrl,String annotationUrl,iMapView.MapType mapType,MapPoint mapPoint) {
        mIMapView.showProgressbar();
        if(mapType == iMapView.MapType.TIANDI || mapType == iMapView.MapType.OPENLAYER) {
            mMapModel.setMapExtent(mMapExtent);
            if(MaxLevel != 0)
                mMapModel.setMapMaxLevel(MaxLevel);
            if(minMapLevel != 0)
                mMapModel.setMapMinLevel(minMapLevel);
            if(curMapLevel != 0)
                mMapModel.setCurMapLevel(curMapLevel);
        }
        mMapModel.initMapinfo(basicLayerUrl,partLayerUrl,annotationUrl,mapType,mIMapView.getScreenWidth(),mIMapView.getScreenHeight(),mapPoint,new OnMapFinishListener() {

            @Override
            public void OnSuccess(Bitmap bitmap, float topleftx, float toplefty) {
                mIMapView.hideProgressbar();
                mIMapView.refreshView(bitmap,topleftx,toplefty);
            }

            @Override
            public void OnAnntionSuccees(Bitmap bitmap, float topleftx, float toplefty) {
                mIMapView.hideProgressbar();
                mIMapView.refreshAnnView(bitmap,topleftx,toplefty);
            }

            @Override
            public void OnPartLayersSuccess(Bitmap bitmap) {
                mIMapView.hideProgressbar();
                mIMapView.refreshPartView(bitmap);
            }

            @Override
            public void OnPartLayersAttribute(List<PartsObjectAttributes> partsObjectAttributes) {
                mIMapView.getPartLayersInfo(partsObjectAttributes);
            }

            @Override
            public void OnError(String errormsg) {
                mIMapView.hideProgressbar();
                mIMapView.showMsg(errormsg);
            }
        });
    }

    @Override
    public void zoomIn() {
        mIMapView.showProgressbar();
        mMapModel.zoomInOrOut(true);
    }

    public void zoomInBylevel(int level){
        mIMapView.showProgressbar();
        mMapModel.zoomInOrOut(true,level);
    }

    @Override
    public void zoomOut() {
        mIMapView.showProgressbar();
        mMapModel.zoomInOrOut(false);
    }

    public void zoomOutBylevel(int level){
        mIMapView.showProgressbar();
        mMapModel.zoomInOrOut(false,level);
    }

    /**
     * 平移的位移差
     * @param dx 横坐标移动的位置
     * @param dy 纵坐标移动的位置
     */
    @Override
    public void translation(float dx,float dy) {
//        mIMapView.showProgressbar();
        mMapModel.translationMap(dx,dy);
    }

    @Override
    public void fullMap() {
        mIMapView.showProgressbar();
        mMapModel.fullMap();
    }

    /**
     * 是否超过了地图范围
     * @param mapPoint 地图点位信息
     * @return true在地图里 false不在地图里
     */
    public boolean isOverMapArea(MapPoint mapPoint){
        return mMapModel.isOverMapArea(mapPoint);
    }

    /**
     * 设置地图的GPS点
     * @param gpspoint
     */
    @Override
    public void showGpsLocation(MapPoint gpspoint) {
        mMapModel.setGpsLocation(gpspoint);
    }

    /**
     * 设置已选位置信息
     * @param hadlocation
     */
    @Override
    public void showHadLocation(MapPoint hadlocation) {
        mMapModel.setHadLocation(hadlocation);
    }

    @Override
    public ArrayList<PartsObjectAttributes> getLayersAttr(MapPoint localPoint) {
        mIMapView.showProgressbar();
        mMapModel.getPartAttributeInfo(localPoint);
        return null;
    }

    /**
     * 将屏幕坐标转换为地图坐标
     * @param mapPoint 屏幕坐标点
     * @return
     */
    @Override
    public MapPoint getLocation(MapPoint mapPoint) {
        return mMapModel.getLocation(mapPoint);
    }

    /**
     * 将地图坐标转换为屏幕坐标
     * @param mapPoint
     * @return
     */
    @Override
    public MapPoint getDisplayLocation(MapPoint mapPoint) {
        return mMapModel.getDisplayLocation(mapPoint);
    }

    @Override
    public int getMapMinLevel() {
        return mMapModel.getMapMinLevel();
    }

    @Override
    public void setMapMinLevel(int mapMinLevel) {
        this.minMapLevel = mapMinLevel;
        mMapModel.setMapMinLevel(mapMinLevel);
    }

    @Override
    public int getMapMaxLevel() {
        return mMapModel.getMapMaxLevel();
    }

    @Override
    public void setMapMaxLevel(int mapMaxLevel) {
        this.MaxLevel = mapMaxLevel;
        mMapModel.setMapMaxLevel(mapMaxLevel);
    }

    @Override
    public void setCurMapLevel(int mapLevel) {
        this.curMapLevel = mapLevel;
        mMapModel.setCurMapLevel(mapLevel);
    }

    @Override
    public int getCurMapLevel() {
        return mMapModel.getCurMapLevel();
    }

    @Override
    public MapLayer[] getPartLayers() {
        return mMapModel.getPartLayers();
    }

    @Override
    public void getPartBitmap(MapLayer[] partLayers) {
        mIMapView.showProgressbar();
        mMapModel.getPartBitmap(partLayers);
    }
}
