package cn.com.hesc.ddpushlibrary.ddpush.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager.WakeLock;


import cn.com.hesc.ddpushlibrary.ddpush.service.OnlineService;
import cn.com.hesc.ddpushlibrary.ddpush.service.Util;


public class TickAlarmReceiver extends BroadcastReceiver {

	WakeLock wakeLock;
	
	public TickAlarmReceiver() {
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		//DDPushMagnager.checkService(context);
		if(Util.hasNetwork(context) == false){
			return;
		}
		Intent startSrv = new Intent(context, OnlineService.class);
		startSrv.putExtra("CMD", "TICK");
		context.startService(startSrv);
	}

}
