package cn.com.hesc.tdsdk;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapController;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MyLocationOverlay;
import com.tianditu.android.maps.TErrorCode;
import com.tianditu.android.maps.TGeoAddress;
import com.tianditu.android.maps.TGeoDecode;
import com.tianditu.android.maps.overlay.PolygonOverlay;
import com.tianditu.android.maps.overlay.PolylineOverlay;
import com.tianditu.android.maps.renderoption.LineOption;
import com.tianditu.android.maps.renderoption.PlaneOption;

import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.maplibrary.R;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.tools.ToastUtils;

/**
 * 整合天地图sdk
 */
public class TDMapActivity extends AppCompatActivity implements TGeoDecode.OnGeoResultListener {

    private MapView mMapView;
    /**传入的定位点*/
    private double locationx = 0, locationy = 0;
    private Context mContext;
    /**要绘制到地图上的一系列点*/
    private List<MapPoint> mMapPoints;
    /**绘制路径*/
    private List<List<MapPoint>> mPathMapPoints;
    /**绘制多边形，如网格图层等*/
    private List<List<MapPoint>> mPolygonPoints;
    /**逆地理编码*/
    private TGeoDecode mGeoDecode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdmap);

        mContext = this;

        // 地图视图
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setLogoPos(MapView.LOGO_LEFT_TOP);
        mMapView.setBuiltInZoomControls(true);

        if(getIntent().getExtras()!=null){
            locationx = getIntent().getExtras().getDouble("strx",0);
            locationy = getIntent().getExtras().getDouble("stry",0);
            if(locationx > 0 && locationy > 0){
                MapController mMapController = mMapView.getController();
                GeoPoint point = new GeoPoint((int) (locationy * 1E6), (int) (locationx * 1E6));
                //设置地图中心点
                mMapController.setCenter(point);
                //设置地图zoom级别
                mMapController.setZoom(12);


                // 开始搜索
                if (mGeoDecode == null)
                    mGeoDecode = new TGeoDecode(this);
                mGeoDecode.search(point);



            }

            mMapPoints = (List<MapPoint>) getIntent().getExtras().getSerializable("points");
            mPathMapPoints = (List<List<MapPoint>>) getIntent().getExtras().getSerializable("paths");
            mPolygonPoints = (List<List<MapPoint>>) getIntent().getExtras().getSerializable("polygons");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyLocationOverlay myLocation = new MyLocationOverlay(mContext,
                        mMapView);
                myLocation.enableCompass(); //显示指南针
                myLocation.enableMyLocation(); //显示我的位置
                mMapView.addOverlay(myLocation);

                //检测如果有要绘制的定位
                if(mMapPoints!=null && mMapPoints.size() > 0){
                    Drawable drawable = getResources().getDrawable(R.drawable.tn);
                    PointItemOverlayer pointItemOverlayer = new PointItemOverlayer(drawable,mContext,mMapPoints);
                    mMapView.addOverlay(pointItemOverlayer);
                }
                //绘制多组线条轨迹
                if(mPathMapPoints!=null && mPathMapPoints.size()>0){
                    for (int i = 0; i < mPathMapPoints.size(); i++) {
                        List<MapPoint> items = mPathMapPoints.get(i);

                        ArrayList<GeoPoint> points = new ArrayList<>();
                        if(items!=null && items.size()>0){
                            for (int j = 0; j < items.size(); j++) {
                                MapPoint mapPoint = items.get(j);
                                points.add(new GeoPoint((int) (mapPoint.getY() * 1E6), (int)(mapPoint.getX() * 1E6)));
                            }
                        }

                        LineOption lineOption = new LineOption();
                        lineOption.setStrokeWidth(5);
                        lineOption.setDottedLine(false);
                        lineOption.setStrokeColor(0xAA000000);
                        PolylineOverlay lineOverlay = new PolylineOverlay();
                        lineOverlay.setOption(lineOption);
                        lineOverlay.setPoints(points);
                        mMapView.addOverlay(lineOverlay);
                    }
                }

                //绘制多组多边形
                if(mPolygonPoints!=null && mPolygonPoints.size()>0){
                    for (int i = 0; i < mPolygonPoints.size(); i++) {
                        List<MapPoint> mapPoints = mPolygonPoints.get(i);
                        ArrayList<GeoPoint> points = new ArrayList<>();
                        if(mapPoints!=null && mapPoints.size()>0){
                            for (int j = 0; j < mapPoints.size(); j++) {
                                MapPoint mapPoint = mapPoints.get(j);
                                points.add(new GeoPoint((int) (mapPoint.getY() * 1E6), (int)(mapPoint.getX() * 1E6)));
                            }
                        }
                        PlaneOption polygonOption = new PlaneOption();
                        polygonOption.setStrokeColor(0xAA0000FF);
                        polygonOption.setStrokeWidth(5);
                        PolygonOverlay polygonOverlay = new PolygonOverlay();
                        polygonOverlay.setOption(polygonOption);
                        polygonOverlay.setPoints(points);
                        mMapView.addOverlay(polygonOverlay);
                    }
                }
            }
        },1000L);

    }

    @Override
    public void onGeoDecodeResult(TGeoAddress tGeoAddress, int errCode) {
        if (errCode != TErrorCode.OK) {
            ToastUtils.showShort(TDMapActivity.this,"获取地址失败,错误代码"+errCode);
            return;
        }
        if (tGeoAddress == null) {
            ToastUtils.showShort(TDMapActivity.this,"获取地址失败,地理编码为null");
            return;
        }
        // 提示
        String str = "";
        str += "最近的poi名称:" + tGeoAddress.getPoiName() + "\n";
        str += "最近poi的方位:" + tGeoAddress.getPoiDirection() + "\n";
        str += "最近poi的距离:" + tGeoAddress.getPoiDistance() + "\n";
        str += "城市名称:" + tGeoAddress.getCity() + "\n";
        str += "全称:" + tGeoAddress.getFullName() + "\n";
        str += "最近的地址:" + tGeoAddress.getAddress() + "\n";
        str += "最近地址的方位:" + tGeoAddress.getAddrDirection() + "\n";
        str += "最近地址的距离:" + tGeoAddress.getAddrDistance() + "\n";
        str += "最近的道路名称:" + tGeoAddress.getRoadName() + "\n";
        str += "最近道路的距离:" + tGeoAddress.getRoadDistance();
        Log.e("TDMapActivity",str);
    }
}
