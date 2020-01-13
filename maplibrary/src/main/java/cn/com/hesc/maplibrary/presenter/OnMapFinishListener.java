package cn.com.hesc.maplibrary.presenter;

import android.graphics.Bitmap;

import java.util.List;

import cn.com.hesc.maplibrary.model.PartsObjectAttributes;

/**
 * 回调mapview，绘制地图
 */
public interface OnMapFinishListener {
    /**加载底图完成*/
    void OnSuccess(Bitmap bitmap, float topleftx, float toplefty);
    /**加载标注图层完成*/
    void OnAnntionSuccees(Bitmap bitmap, float topleftx, float toplefty);
    /**加载部件图层*/
    void OnPartLayersSuccess(Bitmap bitmap);
    /**查询到的部件图层属性信息*/
    void OnPartLayersAttribute(List<PartsObjectAttributes> partsObjectAttributes);
    /**加载失败*/
    void OnError(String errormsg);
}
