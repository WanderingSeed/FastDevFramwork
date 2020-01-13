package cn.com.hesc.fastdevframwork.gps;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.gpslibrary.model.GpsForUser;
import cn.com.hesc.gpslibrary.view.GpsStateActivity;
import cn.com.hesc.tools.TimeUtils;
import cn.com.hesc.tools.ToastUtils;

public class GpsTestActivity extends AppCompatActivity {

    private TextView showgps;
    private Handler mHandler;
    private Thread mThread;
    private boolean isStop = false;
    private GpsForUser mGpsForUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);

        mHandler = new Handler(this.getMainLooper());
        showgps = (TextView)findViewById(R.id.showgps);
        mGpsForUser = GpsForUser.getInstance(this);
        mGpsForUser.setAllowCacheGps(false);//不允许缓存
        if(!mGpsForUser.isGPSOpen()){
            ToastUtils.showLong(GpsTestActivity.this,"开启GPS才会使得位置更准确");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mThread != null){
            mThread = null;
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Location location = mGpsForUser.getLocation();
                            if(location != null){
                                String str = "";
                                str += "数据来源:"+location.getProvider()+"\n";
                                str += "纬度:"+location.getLatitude()+"\n";
                                str += "经度:"+location.getLongitude()+"\n";
                                str += "时间:"+ TimeUtils.longToString(location.getTime())+"\n";
                                showgps.setText(str);
                            }

                        }
                    });

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
        mThread = null;
        mGpsForUser.stopLocationService();
    }

    public void toview(View v){
        Intent intent = new Intent(GpsTestActivity.this, GpsStateActivity.class);
        startActivity(intent);
    }
}
