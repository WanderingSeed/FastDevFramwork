package cn.com.hesc.tdsdk;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.ItemizedOverlay;
import com.tianditu.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.maplibrary.model.MapPoint;

/**
 * 绘制标记点图层
 * created by liujunlin on 2018/7/30 11:04
 */
public class PointItemOverlayer extends ItemizedOverlay<OverlayItem> {

    private Context mContext;
    private List<OverlayItem> geoList = new ArrayList<>();

    public PointItemOverlayer(Drawable marker,Context context,List<MapPoint> items){
        super(boundCenterBottom(marker));
        mContext = context;
        geoList.clear();
        for (int i = 0; i < items.size(); i++) {
            MapPoint mp = items.get(i);
            OverlayItem item = new OverlayItem(
                    new GeoPoint((int) (mp.getY() * 1E6),
                            (int) (mp.getX() * 1E6)), "P" + i, "point" + i);
            item.setMarker(marker);
            geoList.add(item);
        }
        //一旦有了数据，在调用其他方法前，必须首先调用这个方法
        populate();

    }

    @Override
    public int size() {
        return geoList.size();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return geoList.get(i);
    }

    /*
     * 在某个条目被点击时调用   */
    @Override
    protected boolean onTap(int i) {
        return true;
    }
}
