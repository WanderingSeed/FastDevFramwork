package cn.com.hesc.maplibrary.overlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 点位
 * created by liujunlin on 2018/7/16 15:30
 */
public class PointLayer {
    List<Marker> markers;

    public PointLayer(){
        markers = new ArrayList<>();
    }

    public List<Marker> getMarker() {
        return markers;
    }

    public void addMark(Marker marker) {
        this.markers.add(marker);
    }

    public int getClickIndex(LatLng latLng){
        int index = -1;
        for (int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);
            if(marker.isClick(latLng,null)){
                index = i;
                break;
            }
        }
        return index;
    }
}
