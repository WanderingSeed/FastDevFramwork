package cn.com.hesc.maplibrary.presenter;

import java.util.ArrayList;

import cn.com.hesc.maplibrary.model.MapLayer;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.model.PartsObjectAttributes;
import cn.com.hesc.maplibrary.view.iMapView;

public interface iMapPresenter {
    /**初始化地图信息*/
    void initMapInfo(String basicLayerUrl,String partLayerUrl,String annotationUrl,iMapView.MapType mapType);
    /**初始化地图信息*/
    void initMapInfo(String basicLayerUrl,String partLayerUrl,String annotationUrl,iMapView.MapType mapType,MapPoint mapPoint);
    /**放大*/
    void zoomIn();
    /**缩小*/
    void zoomOut();
    /**平移*/
    void translation(float dx, float dy);
    /**全图*/
    void fullMap();
    /**gps定位*/
    void showGpsLocation(MapPoint gpsPoint);
    /**已选位置定位*/
    void showHadLocation(MapPoint gpsPoint);
    /**获取选中点的所有图层属性*/
    ArrayList<PartsObjectAttributes> getLayersAttr(MapPoint localPoint);
    /**将屏幕点位转换为地图坐标点*/
    MapPoint getLocation(MapPoint mapPoint);
    /**将地图坐标点转换为屏幕点位*/
    MapPoint getDisplayLocation(MapPoint mapPoint);
    /**获取地图最小比例尺*/
    int getMapMinLevel();
    /**设置最小比例尺*/
    void setMapMinLevel(int mapMinLevel);
    /**获取地图最大比例尺*/
    int getMapMaxLevel();
    /*设置最大比例尺*/
    void setMapMaxLevel(int mapMaxLevel);
    /**设置最小比例尺*/
    void setCurMapLevel(int mapMinLevel);
    /**获取地图当前比例尺*/
    int getCurMapLevel();
    /**获取部件图层名称*/
    MapLayer[] getPartLayers();
    /**请求部件图层的bitmap*/
    void getPartBitmap(MapLayer[] partLayers);
}
