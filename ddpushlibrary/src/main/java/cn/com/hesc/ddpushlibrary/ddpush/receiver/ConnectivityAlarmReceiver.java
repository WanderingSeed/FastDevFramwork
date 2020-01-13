package cn.com.hesc.ddpushlibrary.ddpush.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import cn.com.hesc.ddpushlibrary.ddpush.service.OnlineService;
import cn.com.hesc.ddpushlibrary.ddpush.service.Util;

public class ConnectivityAlarmReceiver extends BroadcastReceiver {

	public ConnectivityAlarmReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if(Util.hasNetwork(context) == false){
			return;
		}
		Intent startSrv = new Intent(context, OnlineService.class);
		startSrv.putExtra("CMD", "RESET");
		context.startService(startSrv);
	}

}
