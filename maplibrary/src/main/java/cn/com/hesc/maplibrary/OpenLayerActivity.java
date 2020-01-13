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
import android.widget.ImageButton;

import cn.com.hesc.gpslibrary.model.GpsForUser;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.view.MapView;
import cn.com.hesc.maplibrary.view.OpenLayerMapview;
import cn.com.hesc.maplibrary.view.iMapView;

public class OpenLayerActivity extends AppCompatActivity implements View.OnClickListener{

    private OpenLayerMapview mMapView;
    private ImageButton fullmapIbtn, layersIbtn, queryLayersIbtn, gpsIbtn, chooseIbtn,zoomin,zoomout,grid_btn;
    private double gpsx, gpsy, locationx, locationy;
    private String partinfo;
    private GpsForUser mGpsForUser;
    private Intent intent;
    private String basicinfo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_layer);

        intent = getIntent();

        mGpsForUser = GpsForUser.getInstance(this);

        initView();
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
                location.setLongitude(116.62066468459);
                location.setLatitude(32.6525064403858);
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
            MapPoint mapPoint = new MapPoint(116.59066468459, 32.6125064403858);//定位网格
            mMapView.showGridArea(mapPoint);
        }
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

    private void loadMap(){
        basicinfo = intent.getStringExtra("basicurl");
        mMapView = (OpenLayerMapview)findViewById(R.id.mapview);
        mMapView.setMapArea((MapExtent) intent.getSerializableExtra("mapextent"));
        mMapView.initMap(this,basicinfo,"","", iMapView.MapType.OPENLAYER);
        /**优先展示选择位置进行定位*/
        if (locationx > 0 && locationy > 0) {
            MapPoint mapPoint = new MapPoint(locationx, locationy);
            mMapView.showHadLocation(mapPoint);
        }
        else if (gpsx > 0 && gpsy > 0) {
            MapPoint mapPoint = new MapPoint(gpsx, gpsy);
            mMapView.showGpsLocation(mapPoint);
        }
    }

    @Override
    public void onBackPressed() {
        Intent it = getIntent();
        MapPoint point = mMapView.getLocation();
        if(point!=null){
            Bundle bundle = new Bundle();
            bundle.putString("strx",point.getX()+"");
            bundle.putString("stry",point.getY()+"");
            bundle.putString("partcode",mMapView.getReturnPartCode());
            it.putExtras(bundle);
            OpenLayerActivity.this.setResult(RESULT_OK,it);
            OpenLayerActivity.this.finish();
        }else{
            OpenLayerActivity.this.setResult(RESULT_CANCELED,it);
            OpenLayerActivity.this.finish();
        }

    }
}
