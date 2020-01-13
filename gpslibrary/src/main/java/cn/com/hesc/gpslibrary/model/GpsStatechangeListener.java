package cn.com.hesc.gpslibrary.model;

import java.util.ArrayList;

/**
 * 卫星发生变化的回调
 */
public interface GpsStatechangeListener {
    public void OnGpsChanged(ArrayList<GpsStateBean> satelist);
    public void OnGpsClosed();
    public void OnGpsOpened();
}
