package cn.com.hesc.backtask;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import cn.com.hesc.devutilslibrary.R;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.os.Build.VERSION.SDK_INT;

/**
 * 后台运行服务
 * created by liujunlin on 2018/10/26 15:29
 */
public abstract class BackGroundService extends Service{

    public Notification notification = null;
    private MediaPlayer bgmediaPlayer;
    public final static int notificationid = 68103;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("background","service start");

        if(null == notification){
            int requestCode1 = (int) SystemClock.uptimeMillis();
            PendingIntent pendingIntent1 = PendingIntent.getActivity(this,requestCode1,intent,FLAG_CANCEL_CURRENT);

            Bundle bundle = intent.getExtras();
            String title = null!=bundle?bundle.getString("notificationtitle","消息"):"消息";
            String content = null!=bundle?bundle.getString("notificationcontent","接收消息通知"):"接收消息通知";

            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.notificationview);
            remoteView.setTextViewText(R.id.showtxt,title+"\n"+content);
            remoteView.setImageViewResource(R.id.logo,R.drawable.msgicon);
            remoteView.setImageViewResource(R.id.close,R.drawable.ads);
//            remoteView.setOnClickPendingIntent(R.id.close,pendingIntent1);


//            Intent notificationIntent = new Intent(getApplicationContext(), BackGroundService.class);
            PendingIntent pendingIntent = null;
            try {
                if(intent.getComponent()!=null){
                    Class clazz2 = Class.forName(intent.getComponent().getClassName());

                    if(clazz2.newInstance() instanceof Activity){
                        pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    }else if(clazz2.newInstance() instanceof Service){
                        pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    }else if(clazz2.newInstance() instanceof BroadcastReceiver){
                        pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Notification.Builder build = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.msgicon)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("消息")
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOngoing(true);
            if(pendingIntent!=null){
                build.setContentIntent(pendingIntent);
            }

            if(SDK_INT >= Build.VERSION_CODES.N)
                build.setCustomContentView(remoteView);
            notification = build.build();
        }

        /*使用startForeground,如果id为0，那么notification将不会显示*/
        startForeground(notificationid, notification);

        if(bgmediaPlayer == null){
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bgmediaPlayer = MediaPlayer.create(this, R.raw.backmusic);
                bgmediaPlayer.setLooping(true);
                bgmediaPlayer.start();
            }
            else{
                bgmediaPlayer = new MediaPlayer();

                AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.backmusic);
                try {
                    bgmediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                            file.getLength());
                    bgmediaPlayer.prepare();
                    file.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        backTask();

        return START_STICKY;
    }

    /**
     * 自定义需要后台运行的业务代码
     */
    abstract public void backTask();

    @Override
    public void onDestroy() {
        stopForeground(true);
        if(bgmediaPlayer != null)
            bgmediaPlayer.release();
        stopSelf();
        super.onDestroy();
    }
}
