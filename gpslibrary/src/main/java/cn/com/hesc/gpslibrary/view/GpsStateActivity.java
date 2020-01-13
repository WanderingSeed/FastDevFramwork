package cn.com.hesc.gpslibrary.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.gpslibrary.R;
import cn.com.hesc.gpslibrary.model.GpsForUser;
import cn.com.hesc.gpslibrary.model.GpsStateBean;
import cn.com.hesc.gpslibrary.model.GpsStatechangeListener;

/**
 * 展示当前搜星情况，红色代表未连接成功，绿色代表已连接，中间颜色代表正在连接
 */
public class GpsStateActivity extends AppCompatActivity implements GpsStatechangeListener{

    private GpsStateView mGpsView;
    private GpsForUser mGpsForUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gps_state);

        mGpsView = new GpsStateView(this);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        this.addContentView(mGpsView,rl);

        mGpsForUser = GpsForUser.getInstance(this);
        boolean isGps = mGpsForUser.isGPSOpen();
        if(!isGps){
            new AlertDialog.Builder(this).setMessage("开启GPS，会使位置更精准").setPositiveButton("开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        }

        mGpsForUser.setGpsStatechangeListener(this);
    }

    @Override
    public void OnGpsChanged(ArrayList<GpsStateBean> satelist) {
        if(mGpsForUser.getLocation()!=null){
            Location location = mGpsForUser.getLocation();
            mGpsView.setDrawGpsData("点位信息:\n"+location.getLongitude()+"###"+location.getLatitude());
        }
        mGpsView.setStarlist(satelist);
        Log.e("search gps count",satelist.size()+"####");
        if(mGpsForUser.getLocation()!=null){
            Geocoder gc = new Geocoder(GpsStateActivity.this);
            try {
                List<Address> addresses = gc.getFromLocation(mGpsForUser.getLocation().getLatitude(),mGpsForUser.getLocation().getLongitude(),10);
                if(addresses.size()>0){
                    Address address = addresses.get(0);
                    String line = address.getAddressLine(0);
                    Log.e("line",line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnGpsClosed() {
        Toast.makeText(GpsStateActivity.this,"GPS已关闭",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnGpsOpened() {
        Toast.makeText(GpsStateActivity.this,"GPS已打开",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        mGpsForUser.stopLocationService();
        super.onDestroy();
    }
}
