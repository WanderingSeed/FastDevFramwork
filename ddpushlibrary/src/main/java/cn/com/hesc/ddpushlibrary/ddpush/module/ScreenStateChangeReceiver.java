package cn.com.hesc.ddpushlibrary.ddpush.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 屏幕黑屏点亮通知监听器
 */
public class ScreenStateChangeReceiver extends BroadcastReceiver {
    public final static String ACTION_SCREEN_CHANGE="SCREEN_CHANGE";
    public final static String INTENT_ACTION_SCREEN_STATE="STATE";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
			if (action.equals(Intent.ACTION_SCREEN_OFF)){
				Intent mIntent = new Intent(ACTION_SCREEN_CHANGE);
				mIntent.putExtra(INTENT_ACTION_SCREEN_STATE, Intent.ACTION_SCREEN_OFF);
				context.sendBroadcast(mIntent);
			}else if (action.equals(Intent.ACTION_SCREEN_ON)){
				Intent mIntent = new Intent(ACTION_SCREEN_CHANGE);
				mIntent.putExtra(INTENT_ACTION_SCREEN_STATE, Intent.ACTION_SCREEN_ON);
				context.sendBroadcast(mIntent);
			}
        }
    }

    static ScreenStateChangeReceiver receiver=null;
    public synchronized static void registerBroadcast(Context context) {
        try {
            if (context != null) {
                if (receiver==null) {
                    receiver=new ScreenStateChangeReceiver();
                    final IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    filter.addAction(Intent.ACTION_SCREEN_ON);
                    context.registerReceiver(receiver, filter);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized static void unregisterBroadcast(Context context) {
        try {
            if (context != null&&receiver!=null) {
                context.unregisterReceiver(receiver);
                receiver=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
