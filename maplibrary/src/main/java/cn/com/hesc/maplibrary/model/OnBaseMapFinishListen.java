package cn.com.hesc.maplibrary.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * 地图加载回调,完成地图数据加载
 */
public interface OnBaseMapFinishListen {
    /**加载底图完成*/
    void OnSuccess(Bitmap bitmap, float topleftx, float toplefty);
    /**加载标注图层完成*/
    void OnAnntionSuccees(Bitmap bitmap, float topleftx, float toplefty);
    /**加载部件图层*/
    void OnPartLayersSuccess(Bitmap bitmap);
    /**查询到的部件图层属性信息*/
    void OnPartLayersAttribute(List<PartsObjectAttributes> partsObjectAttributes);
    /**加载失败*/
    void OnErrorMsg(String errormsg);
}
