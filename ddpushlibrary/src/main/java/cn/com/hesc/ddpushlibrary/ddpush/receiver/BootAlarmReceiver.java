package cn.com.hesc.ddpushlibrary.ddpush.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import cn.com.hesc.ddpushlibrary.ddpush.service.OnlineService;

public class BootAlarmReceiver extends BroadcastReceiver {

	public BootAlarmReceiver() {

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startSrv = new Intent(context, OnlineService.class);
		startSrv.putExtra("CMD", "TICK");
		context.startService(startSrv);
	}
}
