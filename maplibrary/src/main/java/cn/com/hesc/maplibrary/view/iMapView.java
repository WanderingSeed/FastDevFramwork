package cn.com.hesc.maplibrary.view;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.model.PartsObjectAttributes;

/**
 * 定义接口，规范地图操作的mapview，必须实现接口
 */
public interface iMapView {

    enum MapType{
        ARCGIS,
        SUPERMAP,
        TIANDI,
        OPENLAYER
    }

    /** 初始化信息 地图、部件、标注服务*/
    void initMap(Context context,String basicLayerUrl,String partLayerUrl,String annotationUrl,MapType mapType);
    /** 初始化信息 地图、部件、标注服务*/
    void initMap(Context context,String basicLayerUrl,String partLayerUrl,String annotationUrl,MapType mapType,MapPoint centerpoint);
    /** 初始化信息 地图、部件、标注服务*/
    void initMap(Context context,String basicLayerUrl,String partLayerUrl,String annotationUrl,MapType mapType,MapPoint centerpoint,int maxLevel);
    /** 放大*/
    void zoomIn();
    /**缩小*/
    void zoomOut();
    /**平移*/
    void translation(float beforeX, float beforeY, float afterX, float afterY);
    /**全图*/
    void fullMap();
    /**展示图层*/
    void showLayers();
    /**获取当前展示的多个图层属性*/
    ArrayList<PartsObjectAttributes> getLayersAttr();
    /**gps定位*/
    void showGpsLocation(MapPoint gpspoint);
    /**展示已有位置*/
    void showHadLocation(MapPoint gpspoint);
    /**获取当前地图比例尺*/
    int getCurMapLevel();
    /**获取地图最大比例尺*/
    int getMaxMapLevel();
    /**获取地图最小比例尺*/
    int getMinMapLevel();
    /**展示等待框*/
    void showProgressbar();
    /**隐藏等待框*/
    void hideProgressbar();
    /**展示消息*/
    void showMsg(String msg);
    /**获取屏幕宽度*/
    int getScreenWidth();
    /**获取屏幕高度*/
    int getScreenHeight();
    /**绘制地图*/
    void refreshView(Bitmap bitmap, float leftTopX, float leftTopY);
    /**绘制标注*/
    void refreshAnnView(Bitmap bitmap, float leftTopX, float leftTopY);
    /**绘制部件图层*/
    void refreshPartView(Bitmap bitmap);
    /**展示查询到的部件属性*/
    void getPartLayersInfo(List<PartsObjectAttributes> partsObjectAttributes);
    /**设置地图的范围*/
    void setMapArea(MapExtent mapArea);
}
