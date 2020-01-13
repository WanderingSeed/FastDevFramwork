 package cn.com.hesc.ddpushlibrary.ddpush.module;

 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.Intent;
 import android.os.Bundle;


 import java.util.ArrayList;

 import cn.com.hesc.ddpushlibrary.ddpush.service.PushConsts;

 /**
  * 推送消息接收器demo，可以自定义，需要在AndroidManifest.xml中声明
  */
 public class PushDemoReceiver extends BroadcastReceiver {
	 static ArrayList<String> actions=new ArrayList<String>();
	 public final static String pushDataIntentStringKey="DATA";
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:
			try {
				if (actions!=null&&actions.size()>0) {
					for (int i = actions.size() - 1; i >= 0; i--) {
						String aciton = actions.get(i);
						Intent mIntent = new Intent(aciton);
						String data = bundle.getString(PushConsts.DATA);
						mIntent.putExtra(pushDataIntentStringKey, data != null ? data : "");
						context.sendBroadcast(mIntent);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
//
//			Intent mIntent = new Intent(
//					MainActivity.ACTION_INTENT_RECEIVER);
//			String data=bundle.getString(PushConsts.DATA);
//			mIntent.putExtra("DATA",data!=null?data:"PushConsts content null");
//			context.sendBroadcast(mIntent);
			break;
		default:
			break;
		}
	}

	 public synchronized static void addAction(String action){
		 if (!actions.contains(action)){
			 actions.add(action);
		 }
	 }

	 public synchronized static void removeAction(String action){
		 if (!actions.contains(action)){
			 actions.remove(action);
		 }
	 }
}
