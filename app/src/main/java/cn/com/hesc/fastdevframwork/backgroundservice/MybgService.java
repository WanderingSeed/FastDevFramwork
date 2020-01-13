package cn.com.hesc.fastdevframwork.backgroundservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.DateFormat;

import cn.com.hesc.backtask.BackGroundService;
import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.tools.TimeUtils;

public class MybgService extends BackGroundService {

    private boolean isTheadRun = true;

    public MybgService() {
    }

    @Override
    public void backTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTheadRun){

                    //发送广播信息
                    Intent intent = new Intent();
                    intent.setAction("com.hesc.text");
                    intent.putExtra("txt", TimeUtils.longToString(System.currentTimeMillis()));
                    sendBroadcast(intent);

                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        isTheadRun = false;
        super.onDestroy();
    }
}
