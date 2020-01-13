package cn.com.hesc.fastdevframwork;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import cn.com.hesc.audiolibrary.audio.AudioRecordActivity;
import cn.com.hesc.fastdevframwork.backgroundservice.BackActivity;
import cn.com.hesc.fastdevframwork.gps.GpsTestActivity;
import cn.com.hesc.fastdevframwork.map.MapTestActivity;
import cn.com.hesc.fastdevframwork.metrialdialog.DialogActivity;
import cn.com.hesc.fastdevframwork.photo.CameraActivity;
import cn.com.hesc.fastdevframwork.recycleview.RecycleViewActivity;
import cn.com.hesc.fastdevframwork.settting.OpenSettingActivity;
import cn.com.hesc.fastdevframwork.timepickdemo.TimePickActivity;
import cn.com.hesc.fastdevframwork.webdemo.WebUtilsActivity;
import cn.com.hesc.fastdevframwork.wheelview.WheelViewActivity;
import cn.com.hesc.fastdevframwork.zxing.ZxingActivity;
import cn.com.hesc.tools.CheckPermissonUtils;

public class MainActivity extends AppCompatActivity {

    private MainActivity mainActivity;
    String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckPermissonUtils checkPermissonUtils = new CheckPermissonUtils(mainActivity,permissions);
        checkPermissonUtils.checkPermission(permissions);
    }

    public void dialog(View view){
        Intent intent = new Intent(MainActivity.this, DialogActivity.class);
        startActivity(intent);
    }

    public void timepick(View view){
        Intent intent = new Intent(MainActivity.this, TimePickActivity.class);
        startActivity(intent);
    }

    public void webDemo(View view){
        Intent intent = new Intent(MainActivity.this, WebUtilsActivity.class);
        startActivity(intent);
    }

    public void wheelview(View view){
        Intent intent = new Intent(MainActivity.this, WheelViewActivity.class);
        startActivity(intent);
    }

    public void takephoto(View view){
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    public void recycle(View view){
        Intent intent = new Intent(MainActivity.this, RecycleViewActivity.class);
        startActivity(intent);
    }

    public void zxing(View view){
        Intent intent = new Intent(MainActivity.this, ZxingActivity.class);
        startActivity(intent);
    }

    public void maptest(View view){
        Intent intent = new Intent(MainActivity.this, MapTestActivity.class);
        startActivity(intent);
    }

    public void gpstest(View view){
        Intent intent = new Intent(MainActivity.this, GpsTestActivity.class);
        startActivity(intent);
    }

    public void audio(View view){
        Intent intent = new Intent(MainActivity.this, AudioRecordActivity.class);
        startActivity(intent);
    }

    public void backservice(View view){
        Intent intent = new Intent(MainActivity.this, BackActivity.class);
        startActivity(intent);
    }

    public void syssetting(View view){
        Intent intent = new Intent(MainActivity.this, OpenSettingActivity.class);
        startActivity(intent);
    }
}
