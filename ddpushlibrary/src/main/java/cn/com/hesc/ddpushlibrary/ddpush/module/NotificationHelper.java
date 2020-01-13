package cn.com.hesc.ddpushlibrary.ddpush.module;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

/**
 * Created by pubinfo on 2016/3/7.
 */
public class NotificationHelper {
    /**
     * 如果系统振动铃声设为0，则不会出现振动
     * @param context
     * @param intent          要跳转的Activity
     * @param icon            资源的id，通知靠左边顶端的图标
     * @param title           标题
     * @param content         内容
     * @param time            通知的时间, 小于0时不显示时间，大于0时使用参数time，等于0时使用系统当前时间
     * @param doVibrate      是否震动
     * @param noticeId       通知id   代码删除通知需要根据id进行删除
     */
    public static boolean notifyRunning(Context context, Intent intent, int icon, String title, String content, long time, boolean doVibrate, int noticeId){
        boolean flag=false;
        if(context!=null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		n = new Notification();
//		n.contentIntent = pi;
//		n.setLatestEventInfo(this, "DDPushDemoUDP", "正在运行", pi);
            int sdkInt = Build.VERSION.SDK_INT;
            Notification n = null;
            Notification.Builder builder = new Notification.Builder(context);
            long when = time>0?time: System.currentTimeMillis();
            if (intent!=null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (sdkInt>=19) {
                    n = builder.setContentIntent(pi).setSmallIcon(icon).setContentTitle(title).setContentText(content).setWhen(when).setShowWhen(time >= 0).build();
                }else{
                    n = builder.setContentIntent(pi).setSmallIcon(icon).setContentTitle(title).setContentText(content).setWhen(when).build();
                }
            }else{
                if (sdkInt>=19) {
                    n = builder.setSmallIcon(icon).setContentTitle(title).setContentText(content).setWhen(when).setShowWhen(time >= 0).build();
                }else{
                    n = builder.setSmallIcon(icon).setContentTitle(title).setContentText(content).setWhen(when).build();
                }
            }
            //n.defaults = Notification.DEFAULT_ALL;
            //n.flags |= Notification.FLAG_SHOW_LIGHTS;
            //n.flags |= Notification.FLAG_AUTO_CANCEL;
            n.flags |= Notification.FLAG_ONGOING_EVENT;
            n.flags |= Notification.FLAG_AUTO_CANCEL;
            if (doVibrate) {
                n.defaults |= Notification.DEFAULT_VIBRATE;
                long[] vibrate = {0, 100, 200, 300};
                n.vibrate = vibrate;
                //Define sound URI
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                n.sound = soundUri;
            }

            //n.iconLevel = 5;
            //n.icon = R.drawable.ic_launcher;
            //n.when = System.currentTimeMillis();
            //n.tickerText = "DDPushDemoUDP正在运行";
            notificationManager.notify(noticeId, n);
        }
        return flag;
    }
}
