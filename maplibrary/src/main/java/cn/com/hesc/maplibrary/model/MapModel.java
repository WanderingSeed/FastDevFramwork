package cn.com.hesc.maplibrary.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.rtp.RtpStream;
import android.support.annotation.Nullable;

import java.text.NumberFormat;
import java.util.List;

import cn.com.hesc.maplibrary.PubGisUtil;
import cn.com.hesc.maplibrary.presenter.OnMapFinishListener;
import cn.com.hesc.maplibrary.view.MapView;
import cn.com.hesc.maplibrary.view.ToastWithImg;
import cn.com.hesc.maplibrary.view.iMapView;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: MapModel
 * Description: 地图模型类，处理逻辑代码
 * Author: liujunlin
 * Date: 2016-04-12 16:22
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MapModel implements OnBaseMapFinishListen{

    private Context mContext;
    private PubGisProcess mPubGisProcess;
    /**要展示的部件图层*/
    private String partlayername = "";
    private OnMapFinishListener onMapFinishListener;
    /**GPS点位信息和已选位置信息*/
    private MapPoint gpsLocation,hadLocation;
    private int screenwidth,screenheight;
    private MapExtent mMapExtent;
    private int mapMaxLevel = 0;
    private int mapMinLevel = -1;
    private int mapCurLevel = -1;

    public MapExtent getMapExtent() {
        return mMapExtent;
    }

    public void setMapExtent(MapExtent mapExtent) {
        mMapExtent = mapExtent;
    }

    public String getPartlayername() {
        return partlayername;
    }

    public void setPartlayername(String partlayername) {
        this.partlayername = partlayername;
    }

    public MapModel(Context context){
        this.mContext = context;
    }

    /**
     * 地图初始化
     * @param onMapFinishListener
     */
    public void initMapinfo(String basicLayerUrl, String partLayerUrl, String annotationUrl, iMapView.MapType mapType, int screenwidth, int screenheight, @Nullable MapPoint centerPoint, OnMapFinishListener onMapFinishListener){

        this.screenwidth = screenwidth;
        this.screenheight = screenheight;

        if (mapType == iMapView.MapType.ARCGIS) {
            mPubGisProcess = new RestPubGisProcess(mContext, partlayername);
            mPubGisProcess.setIstiandi(false);
        } else if (mapType == iMapView.MapType.SUPERMAP) {
            mPubGisProcess = new SuperMapPubGisProcess(mContext, partlayername);
            mPubGisProcess.setIstiandi(false);
        } else if (mapType == iMapView.MapType.TIANDI) {
            mPubGisProcess = new TiandituGisProcess(mContext, partlayername);
            mPubGisProcess.setIstiandi(true);
            if(mMapExtent!=null){
                mPubGisProcess.setMapExtent(mMapExtent);
                mPubGisProcess.setInitMapExtent(mMapExtent);
            }
            if(centerPoint!=null){
                mPubGisProcess.getMapExtent().setCenterPoint(centerPoint);
            }
            /*地图初始化的时候指定下是否是超图发布的部件信息*/
            TiandituGisProcess.setSuperMapPart(false);
        }else if(mapType == iMapView.MapType.OPENLAYER){
            mPubGisProcess = new OpenLayerGisProcess(mContext, "maoji");
            mPubGisProcess.setIstiandi(false);
            if(mMapExtent!=null){
                mPubGisProcess.setMapExtent(mMapExtent);
                mPubGisProcess.setInitMapExtent(mMapExtent);
            }
            /*地图初始化的时候指定下是否是超图发布的部件信息*/
            TiandituGisProcess.setSuperMapPart(false);
        }

        if(mPubGisProcess!=null){
            /**针对天地图地图当前比例尺需要先进行设置*/

            if(mapMinLevel > 0)
                mPubGisProcess.setMinLevel(mapMinLevel);
            if(mapMaxLevel > 0)
                mPubGisProcess.setMaxLevel(mapMaxLevel);
            if(mapCurLevel > 0)
                mPubGisProcess.setCurrentLevel(mapCurLevel);
            else
                mPubGisProcess.setCurrentLevel(mPubGisProcess.istiandi()?13:mPubGisProcess.getMinLevel());
            mPubGisProcess.setHadlocationPoint(getHadLocation());
            mPubGisProcess.setGpsPoint(getGpsLocation());
            mPubGisProcess.setMap_gesture(MapView.Map_Gesture.TRANSLATION);
            mPubGisProcess.screenWidth = screenwidth;
            mPubGisProcess.screenHeight = screenheight;
            mPubGisProcess.setOnBaseMapFinishListen(this);
            this.onMapFinishListener = onMapFinishListener;
            mPubGisProcess.setBasicInfo(basicLayerUrl);
            mPubGisProcess.setPartInfo(partLayerUrl);
            mPubGisProcess.setAnnotationInfo(annotationUrl);
            mPubGisProcess.initBaseInfo();
        }
    }

    public int getMapMinLevel(){
        return mPubGisProcess.getMinLevel();
    }

    public void setMapMinLevel(int level){
        if(level < 0)
            return;
        this.mapMinLevel = level;
//        mPubGisProcess.setMinLevel(level);
    }

    public int getMapMaxLevel(){
        return mPubGisProcess.getMaxLevel();
    }

    public void setMapMaxLevel(int level){
        if(level > 0)
            this.mapMaxLevel = level;
//        mPubGisProcess.setMaxLevel(level);
    }



    public int getCurMapLevel(){
        return mPubGisProcess.getCurrentLevel();
    }

    public void setCurMapLevel(int level){
        if(level > 0)
            mapCurLevel = level;
//        mPubGisProcess.setCurrentLevel(level);
    }

    public MapLayer[] getPartLayers(){
        return mPubGisProcess.partMapArray;
    }

    public MapPoint getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(MapPoint gpsLocation) {
        this.gpsLocation = gpsLocation ;
        mPubGisProcess.changeMapExtent(gpsLocation);
    }

    public MapPoint getHadLocation() {
        return hadLocation;
    }

    public void setHadLocation(MapPoint hadLocation) {
        this.hadLocation = hadLocation ;
        mPubGisProcess.changeMapExtent(hadLocation);
    }

    /**
     * 将屏幕点转换为地图点
     * @param mapPoint 屏幕坐标
     * @return
     */
    public MapPoint getLocation(MapPoint mapPoint) {
        double dx = PubGisUtil.getGeoCoordinateX((float) mapPoint.getX(),mPubGisProcess.currentMapExtent,mPubGisProcess.resolutions,mPubGisProcess.getCurrentLevel(),screenwidth);
        double dy = PubGisUtil.getGeoCoordinateY((float) mapPoint.getY(),mPubGisProcess.currentMapExtent,mPubGisProcess.resolutions,mPubGisProcess.getCurrentLevel(),screenheight);
        return new MapPoint(dx,dy);
    }

    /**
     * 将坐标点转换为屏幕点
     * @param mapPoint 地图坐标点
     * @return 如果点位置是Integer.MIN_VALUE，Integer.MIN_VALUE表示点位超出地图范围
     */
    public MapPoint getDisplayLocation(MapPoint mapPoint){
        MapPoint mapPoint1 = new MapPoint();
        if(mapPoint.getX() < mPubGisProcess.initMapExtent.getMinX() || mapPoint.getX() > mPubGisProcess.initMapExtent.getMaxX() || mapPoint.getY() < mPubGisProcess.initMapExtent.getMinY() || mapPoint.getY() > mPubGisProcess.initMapExtent.getMaxY()){
            mapPoint1.setX(Integer.MIN_VALUE);
            mapPoint1.setY(Integer.MIN_VALUE);
        }
        else{
            mapPoint1.setX(PubGisUtil.getScreenX(mapPoint.getX(), mPubGisProcess.currentMapExtent, mPubGisProcess.resolutions, mPubGisProcess.getCurrentLevel()));
            mapPoint1.setY(PubGisUtil.getScreenY(mapPoint.getY(), mPubGisProcess.currentMapExtent, mPubGisProcess.resolutions, mPubGisProcess.getCurrentLevel()));
        }
        return mapPoint1;
    }

    /**
     * 是否超过了地图范围
     * @param mapPoint 地图点位信息
     * @return true在地图里 false不在地图里
     */
    public boolean isOverMapArea(MapPoint mapPoint){
        if (null != mPubGisProcess.initMapExtent){
            if(mapPoint.getX() < mPubGisProcess.initMapExtent.getMinX() || mapPoint.getX() > mPubGisProcess.initMapExtent.getMaxX() || mapPoint.getY() < mPubGisProcess.initMapExtent.getMinY() || mapPoint.getY() > mPubGisProcess.initMapExtent.getMaxY()){
                return false;
            }
            else{
                return true;
            }
        }else
            return true;
    }

    /**
     * 平移地图
     * @param dx
     * @param dy
     */
    public void translationMap(float dx,float dy){
        double x = mPubGisProcess.currentMapExtent.getCenterPoint().getX();
        double y = mPubGisProcess.currentMapExtent.getCenterPoint().getY();
        x -= mPubGisProcess.resolutions[mPubGisProcess.currentLevel] * dx;
        y += mPubGisProcess.resolutions[mPubGisProcess.currentLevel] * dy;
        mPubGisProcess.changeMapExtent(new MapPoint(x, y));
    }

    /**
     * 展示全图
     */
    public void fullMap(){
        double x = mPubGisProcess.initMapExtent.getCenterPoint().getX();
        double y = mPubGisProcess.initMapExtent.getCenterPoint().getY();
        mPubGisProcess.setCurrentLevel(mPubGisProcess.getMinLevel());
        mPubGisProcess.changeMapExtent(new MapPoint(x, y));
    }

    /**
     * 放大或缩小
     * @param isZoomIn true放大，false 缩小
     */
    public void zoomInOrOut(boolean isZoomIn){
        zoomInOrOut(isZoomIn,1);
//        double x = mPubGisProcess.currentMapExtent.getCenterPoint().getX();
//        double y = mPubGisProcess.currentMapExtent.getCenterPoint().getY();
//        if(isZoomIn)
//            mPubGisProcess.setCurrentLevel(mPubGisProcess.getCurrentLevel()+1);
//        else
//            mPubGisProcess.setCurrentLevel(mPubGisProcess.getCurrentLevel()-1);
//
//        mPubGisProcess.changeMapExtent(new MapPoint(x, y));
    }

    /**
     * 放大或缩小
     * @param isZoomIn true放大，false 缩小
     */
    public void zoomInOrOut(boolean isZoomIn,int level){
        double x = mPubGisProcess.currentMapExtent.getCenterPoint().getX();
        double y = mPubGisProcess.currentMapExtent.getCenterPoint().getY();
        if(isZoomIn) {

            if(mPubGisProcess.getCurrentLevel() >= mPubGisProcess.maxLevel){
                ToastWithImg toastWithImg = new ToastWithImg(mContext);
                toastWithImg.showToast("已是最大比例尺");
                return;
            }

            int slevel = mPubGisProcess.getCurrentLevel() + level;
            if(slevel >= mPubGisProcess.maxLevel) {
                slevel = mPubGisProcess.maxLevel;
            }
            mPubGisProcess.setCurrentLevel(slevel);
        }
        else {

            if(mPubGisProcess.getCurrentLevel() <= mPubGisProcess.minLevel){
                ToastWithImg toastWithImg = new ToastWithImg(mContext);
                toastWithImg.showToast("已是最小比例尺");
                return;
            }

            int slevel = mPubGisProcess.getCurrentLevel() - level;
            if(slevel <= mPubGisProcess.minLevel)
                slevel = mPubGisProcess.minLevel;
            mPubGisProcess.setCurrentLevel(slevel);
        }

        mPubGisProcess.changeMapExtent(new MapPoint(x, y));
    }

    public void getPartBitmap(MapLayer[] partLayers){
        mPubGisProcess.getPartLayersBitmap(partLayers);
    }

    public void getPartAttributeInfo(MapPoint mapPoint){
        mPubGisProcess.getPartAttributeInfo(mapPoint);
    }


    @Override
    public void OnSuccess(Bitmap bitmap, float topleftx, float toplefty) {
        onMapFinishListener.OnSuccess(bitmap,topleftx,toplefty);
    }

    @Override
    public void OnAnntionSuccees(Bitmap bitmap, float topleftx, float toplefty) {
        onMapFinishListener.OnAnntionSuccees(bitmap,topleftx,toplefty);
    }

    @Override
    public void OnPartLayersSuccess(Bitmap bitmap) {
        onMapFinishListener.OnPartLayersSuccess(bitmap);
    }

    @Override
    public void OnPartLayersAttribute(List<PartsObjectAttributes> partsObjectAttributes) {
        onMapFinishListener.OnPartLayersAttribute(partsObjectAttributes);
    }

    @Override
    public void OnErrorMsg(String errormsg) {
        onMapFinishListener.OnError(errormsg);
    }
}
