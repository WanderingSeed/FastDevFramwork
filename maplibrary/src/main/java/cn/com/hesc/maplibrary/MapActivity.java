package cn.com.hesc.maplibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ZoomControls;

import java.util.List;

import cn.com.hesc.gpslibrary.model.GpsForUser;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.overlayer.Marker;
import cn.com.hesc.maplibrary.view.MapView;
import cn.com.hesc.maplibrary.view.iMapView;

public class MapActivity extends AppCompatActivity implements View.OnClickListener{

    private MapView mMapView;
    private ImageButton fullmapIbtn, layersIbtn, queryLayersIbtn, gpsIbtn, chooseIbtn,zoomin,zoomout,grid_btn;
    private double gpsx, gpsy, locationx, locationy;
    public ZoomControls zoomControls;
    public final int MY_PERMISSIONS_REQUEST_SDCARD = 100;
    private GpsForUser mGpsForUser;
    private String basicinfo,partinfo,annotation;
    private iMapView.MapType mMapType;
    private MapExtent mMapExtent;
    /**要绘制到地图上的一系列点*/
    private List<MapPoint> mMapPoints;
    /**绘制路径*/
    private List<List<MapPoint>> mPathMapPoints;
    /**绘制可点击弹出dialog的点位*/
    private List<Marker> markers;
    public static FrameLayout rootline;
    private MapPoint centerPoint;
    private int maxLevel = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        rootline = (FrameLayout)findViewById(R.id.rootline);

        mGpsForUser = GpsForUser.getInstance(this);
        //地图底图地址
        basicinfo = getIntent().getExtras().getString("basicinfo","");
        //地图部件地址
        partinfo = getIntent().getExtras().getString("partinfo","");
        //天地图标注地址
        annotation = getIntent().getExtras().getString("annotation","");
        //地图类型（天地图、arcgis、supermap，是枚举类型iMapView.MapType）
        mMapType = (iMapView.MapType) getIntent().getExtras().getSerializable("maptype");
        //地图初始化展示范围（天地图必须提供）
        mMapExtent = (MapExtent) getIntent().getExtras().getSerializable("mapextent");
        //已选位置（可为空）
        locationx = getIntent().getExtras().getDouble("strx",0);
        locationy = getIntent().getExtras().getDouble("stry",0);
        //要绘制的路径（可为空）
        mMapPoints = (List<MapPoint>) getIntent().getExtras().getSerializable("points");
        //要绘制的多条路径（可为空）
        mPathMapPoints = (List<List<MapPoint>>) getIntent().getExtras().getSerializable("paths");
        //要绘制的可点击的点位
        markers = (List<Marker>) getIntent().getExtras().getSerializable("marks");
        //设置地图最大比例尺
        maxLevel = getIntent().getExtras().getInt("maxlevel",0);

        double centerx = getIntent().getExtras().getDouble("centerx",0);
        double centery = getIntent().getExtras().getDouble("centery",0);
        if(centerx!=0 && centery!=0 ){
            centerPoint = new MapPoint(centerx,centery);
        }

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isGps = mGpsForUser.isGPSOpen();
        if(!isGps){
            new AlertDialog.Builder(this).setMessage("开启GPS，会使位置更精准").setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).setNegativeButton("下次", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        }
        if(mGpsForUser.getLocation()!=null){
            gpsx = mGpsForUser.getLocation().getLongitude();
            gpsy = mGpsForUser.getLocation().getLatitude();
        }

        loadMap();
    }

    private void initView() {
        fullmapIbtn = (ImageButton) findViewById(R.id.fullmap_btn);
        fullmapIbtn.setOnClickListener(this);
        layersIbtn = (ImageButton) findViewById(R.id.maplayer_btn);
        layersIbtn.setOnClickListener(this);
        queryLayersIbtn = (ImageButton) findViewById(R.id.qlayerinfo_btn);
        queryLayersIbtn.setOnClickListener(this);
        gpsIbtn = (ImageButton) findViewById(R.id.gps_btn);
        gpsIbtn.setOnClickListener(this);
        chooseIbtn = (ImageButton) findViewById(R.id.seclocation_btn);
        chooseIbtn.setOnClickListener(this);
        zoomout = (ImageButton)findViewById(R.id.zoomout);
        zoomout.setOnClickListener(this);
        zoomin = (ImageButton)findViewById(R.id.zoomin);
        zoomin.setOnClickListener(this);
        grid_btn = (ImageButton)findViewById(R.id.grid_btn);
        grid_btn.setOnClickListener(this);
        zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.zoomIn();
            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.zoomOut();
            }
        });

        if(TextUtils.isEmpty(partinfo)){
            layersIbtn.setVisibility(View.GONE);
            queryLayersIbtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == fullmapIbtn) {
            mMapView.fullMap();
            if(locationx>0 && locationy>0){
                mMapView.showHadLocation(new MapPoint(locationx,locationy));
            }
        } else if (v == layersIbtn) {
            mMapView.showLayers();
        } else if (v == queryLayersIbtn) {
            mMapView.setCurgesture(MapView.Map_Gesture.PARTQUERY);
        } else if (v == gpsIbtn) {
            Location location = mGpsForUser.getLocation();
            if (location != null) {
                MapPoint mapPoint = new MapPoint(location.getLongitude(), location.getLatitude());//读取到GPS数据
                mMapView.showGpsLocation(mapPoint);
            }
        } else if (v == chooseIbtn) {
            mMapView.setCurgesture(MapView.Map_Gesture.CHOOSELOCATION);
        }else if(v == zoomin){
            mMapView.zoomIn();
        }else if(v == zoomout){
            mMapView.zoomOut();
        }else if(v == grid_btn){
            MapPoint mapPoint = new MapPoint(120.116, 30.245);//读取到GPS数据
            mMapView.showGridArea(mapPoint);
        }
    }

    private void loadMap(){
        mMapView = (MapView) findViewById(R.id.mapview);
        if(mMapExtent != null)
            mMapView.setMapArea(mMapExtent);
        if(centerPoint==null && maxLevel == 0)
            mMapView.initMap(this,basicinfo,partinfo,annotation, mMapType);
        else if(centerPoint!=null && maxLevel == 0)
            mMapView.initMap(this,basicinfo,partinfo,annotation, mMapType,centerPoint);
        else
            mMapView.initMap(this,basicinfo,partinfo,annotation, mMapType,centerPoint,maxLevel);

        mMapView.setImapOnLoadFinishListener(new MapView.ImapOnLoadFinishListener() {
            @Override
            public void onLoadFinish() {

//                mMapView.setCurMapLevel(18);

                /**优先展示选择位置进行定位*/
                if (locationx > 0 && locationy > 0) {
                    MapPoint mapPoint = new MapPoint(locationx, locationy);
                    mMapView.showHadLocation(mapPoint);
                }
                else if (gpsx > 0 && gpsy > 0) {
                    MapPoint mapPoint = new MapPoint(gpsx, gpsy);
                    mMapView.showGpsLocation(mapPoint);
                }
                if(mMapPoints != null)
                    mMapView.setMapPoints(mMapPoints);
                if(mPathMapPoints   != null)
                    mMapView.setPathMapPoints(mPathMapPoints);
                if(markers != null)
                    mMapView.setMarkers(markers);
            }
        });

    }

    @Override
    public void onBackPressed() {
        exitMap();
    }

    public void positionok(View view){
        exitMap();
    }

    private void exitMap(){
        Intent it = getIntent();
        MapPoint point = mMapView.getLocation();
        if(point!=null){
            Bundle bundle = new Bundle();
            bundle.putString("strx",point.getX()+"");
            bundle.putString("stry",point.getY()+"");
            bundle.putString("partcode",mMapView.getReturnPartCode());

            Log.e("position:",point.getX()+"!"+point.getY());

            it.putExtras(bundle);
            MapActivity.this.setResult(RESULT_OK,it);
            MapActivity.this.finish();
        }else{
            MapActivity.this.setResult(RESULT_CANCELED,it);
            MapActivity.this.finish();
        }
    }
}
