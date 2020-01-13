package cn.com.hesc.ddpushlibrary.ddpush.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.TCPClientBase;
import org.ddpush.im.v1.client.appuser.UDPClientBase;

import java.io.Serializable;
import java.nio.ByteBuffer;

import cn.com.hesc.ddpushlibrary.R;
import cn.com.hesc.ddpushlibrary.ddpush.receiver.TickAlarmReceiver;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.os.Build.VERSION.SDK_INT;

//import android.app.NotificationManager;

public class OnlineService extends Service {
	private MediaPlayer bgmediaPlayer;
	protected PendingIntent tickPendIntent;
	protected TickAlarmReceiver tickAlarmReceiver = new TickAlarmReceiver();
	WakeLock wakeLock;
	/**
	 * MyTcpClient比MyUdpClient 可靠，但是更占资源
	 */
	//MyUdpClient myUdpClient;
	MyTcpClient myUdpClient;
	Notification notification;
	//在还没重置"RESET_LOGIN"时，会报错"推送的参数设置错误"
	private boolean isResetLogin = false;

	public OnlineService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.setTickAlarm();

		PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OnlineService");

		resetClient();
	}

	@Override
	public int onStartCommand(Intent param, int flags, int startId) {
		if(param == null){
			return START_STICKY;
		}
		String cmd = param.getStringExtra("CMD");
		if(cmd == null){
			cmd = "";
		}
		if(cmd.equals("TICK")){
			if(wakeLock != null && wakeLock.isHeld() == false){
				wakeLock.acquire();
			}
		}
		if(cmd.equals("RESET_LOGIN")){
			if(wakeLock != null && wakeLock.isHeld() == false){
				wakeLock.acquire();
			}
			if (param!=null){
				Serializable obj=param.getSerializableExtra(DDPushMagnager.key);
				if (obj!=null&&obj instanceof PushServerMsg){
					PushServerMsg serverMsg=(PushServerMsg)obj;
					SharedPreferences account = this.getSharedPreferences(Params.DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = account.edit();
					editor.putString(Params.SERVER_IP, ""+serverMsg.serverip);
					editor.putString(Params.SERVER_PORT, ""+serverMsg.serverport);
					editor.putString(Params.PUSH_PORT, ""+serverMsg.pushport);
					editor.putString(Params.USER_NAME, ""+serverMsg.username);
					editor.commit();
					isResetLogin = true;
				}
			}
			resetClient();
		}
		if(cmd.equals("RESET")){
			if(wakeLock != null && wakeLock.isHeld() == false){
				wakeLock.acquire();
			}
			resetClient();
		}
		if(cmd.equals("TOAST")){
			String text = param.getStringExtra("TEXT");
			if(text != null && text.trim().length() != 0){
				Toast.makeText(this, text, Toast.LENGTH_LONG).show();
			}
		}
		if(cmd.equals("TOAST_MSG")){
			SharedPreferences account = this.getSharedPreferences(Params.DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
			String serverIp = account.getString(Params.SERVER_IP, "");
			String serverPort = account.getString(Params.SERVER_PORT, "");
			String userName = account.getString(Params.USER_NAME, "");
			String pushPort = account.getString(Params.PUSH_PORT, "");
			Toast.makeText(this, serverIp+":"+serverPort+";"+pushPort+";"+userName, Toast.LENGTH_LONG).show();
		}
		if (cmd.equals("STOP")){
			stopSelf();
		}
		setPkgsInfo();

		int requestCode1 = (int) SystemClock.uptimeMillis();
		PendingIntent pendingIntent1 = PendingIntent.getActivity(this,requestCode1,param,FLAG_CANCEL_CURRENT);

		RemoteViews remoteView = new RemoteViews(getPackageName(),R.layout.notificationview);
		remoteView.setTextViewText(R.id.showtxt,"消息推送\n正在运行");
		remoteView.setImageViewResource(R.id.logo,R.drawable.ddpushmsg);
		remoteView.setImageViewResource(R.id.close,R.drawable.ads);
		remoteView.setOnClickPendingIntent(R.id.close,pendingIntent1);


		Intent notificationIntent = new Intent(getApplicationContext(), OnlineService.class);
		Bitmap largebit = BitmapFactory.decodeResource(getResources(),R.drawable.ddpushmsg);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder build = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ddpushmsg)
				.setLargeIcon(largebit)
				.setWhen(System.currentTimeMillis())
				.setTicker("消息推送")
				.setContentTitle("消息推送")
				.setContentText("正在运行")
				.setOngoing(true)
				.setContentIntent(pendingIntent);
		if(SDK_INT >= Build.VERSION_CODES.N)
			build.setCustomContentView(remoteView);
		notification = build.build();

		/*使用startForeground,如果id为0，那么notification将不会显示*/
		startForeground(200, notification);

		if(bgmediaPlayer == null){
			bgmediaPlayer = MediaPlayer.create(this, R.raw.nnvoice);
			bgmediaPlayer.setLooping(true);
			bgmediaPlayer.start();
		}

		return START_STICKY;
	}

	protected void setPkgsInfo(){
		//无实际意义
		if(this.myUdpClient == null){
			return;
		}
		long sent = myUdpClient.getSentPackets();
		long received = myUdpClient.getReceivedPackets();
		SharedPreferences account = this.getSharedPreferences(Params.DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = account.edit();
		editor.putString(Params.SENT_PKGS, ""+sent);
		editor.putString(Params.RECEIVE_PKGS, ""+received);
		editor.commit();
	}

	protected void resetClient(){
		SharedPreferences account = this.getSharedPreferences(Params.DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
		String serverIp = account.getString(Params.SERVER_IP, "");
		String serverPort = account.getString(Params.SERVER_PORT, "");
		String pushPort = account.getString(Params.PUSH_PORT, "");
		String userName = account.getString(Params.USER_NAME, "");
		int appid=1;
		try {
			appid=account.getInt(Params.APPID,1);
		}catch (Exception e){
			e.printStackTrace();
		}
		if (appid<=0||appid>255){
			Toast.makeText(this, "appid="+appid+";appid范围为1-255", Toast.LENGTH_LONG).show();
			return;
		}
		if (isResetLogin) {
			if (serverIp == null || serverIp.trim().length() == 0
					|| serverPort == null || serverPort.trim().length() == 0
					|| pushPort == null || pushPort.trim().length() == 0
					|| userName == null || userName.trim().length() == 0) {
				Toast.makeText(this, "推送的参数设置错误", Toast.LENGTH_LONG).show();
				return;
			}
		}
		if(this.myUdpClient != null){
			try{myUdpClient.stop();}catch(Exception e){}
		}
		try{
			//myUdpClient = new MyUdpClient(Util.md5Byte(userName), 1, serverIp, Integer.parseInt(serverPort));
			myUdpClient = new MyTcpClient(Util.md5Byte(userName), appid, serverIp, Integer.parseInt(serverPort));
			myUdpClient.setHeartbeatInterval(50);
			myUdpClient.start();
			//无实际意义
			SharedPreferences.Editor editor = account.edit();
			editor.putString(Params.SENT_PKGS, "0");
			editor.putString(Params.RECEIVE_PKGS, "0");
			editor.commit();
		}catch(Exception e){
			//Toast.makeText(this.getApplicationContext(), "操作失败："+e.getMessage(), Toast.LENGTH_LONG).show();
		}
		//Toast.makeText(this.getApplicationContext(), "ddpush：终端重置", Toast.LENGTH_LONG).show();
	}

	protected void tryReleaseWakeLock(){
		if(wakeLock != null && wakeLock.isHeld() == true){
			wakeLock.release();
		}
	}

	protected void setTickAlarm(){
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this,TickAlarmReceiver.class);
		int requestCode = 0;
		tickPendIntent = PendingIntent.getBroadcast(this,
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//小米2s的MIUI操作系统，目前最短广播间隔为5分钟，少于5分钟的alarm会等到5分钟再触发！2014-04-28
		long triggerAtTime = System.currentTimeMillis();
		int interval = 60 * 1000;
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval, tickPendIntent);
	}

	protected void cancelTickAlarm(){
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmMgr.cancel(tickPendIntent);
	}

	@Override
	public void onDestroy() {

		stopForeground(true);
		bgmediaPlayer.release();
		stopSelf();

		super.onDestroy();
		this.tryReleaseWakeLock();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class MyTcpClient extends TCPClientBase {

		public MyTcpClient(byte[] uuid, int appid, String serverAddr, int serverPort)
				throws Exception {
			super(uuid, appid, serverAddr, serverPort, 10);

		}

		@Override
		public boolean hasNetworkConnection() {
			return Util.hasNetwork(OnlineService.this);
		}


		@Override
		public void trySystemSleep() {
			tryReleaseWakeLock();
		}

		@Override
		public void onPushMessage(Message message) {
			if(message == null){
				return;
			}
			if(message.getData() == null || message.getData().length == 0){
				return;
			}
			//DDPush支持的三种推送类型
			if(message.getCmd() == 16){// 0x10 通用推送信息
				//notifyUser(16,"DDPush通用推送信息","时间："+DateTimeUtil.getCurDateTime(),"收到通用推送信息");
			}
			if(message.getCmd() == 17){// 0x11 分组推送信息
				long msg = ByteBuffer.wrap(message.getData(), 5, 8).getLong();
				//notifyUser(17,"DDPush分组推送信息",""+msg,"收到通用推送信息");
			}
			if(message.getCmd() == 32){// 0x20 自定义推送信息
				String str = null;
				try{
					str = new String(message.getData(),5,message.getContentLength(), "UTF-8");
				}catch(Exception e){
					str = Util.convert(message.getData(),5,message.getContentLength());
				}
				//接收到自定义的推送信息，通过broadcast广播转发给应用
				Intent intent=new Intent(PushConsts.PUSH_ACTION);
				Bundle bundle=new Bundle();
				bundle.putInt(PushConsts.CMD_ACTION, PushConsts.GET_MSG_DATA);
				bundle.putString(PushConsts.DATA, str);
				intent.putExtras(bundle);
				sendBroadcast(intent);
				//notifyUser(32,"DDPush自定义推送信息",""+str,"收到自定义推送信息");
			}
			setPkgsInfo();
		}

	}

	public class MyUdpClient extends UDPClientBase {

		public MyUdpClient(byte[] uuid, int appid, String serverAddr, int serverPort)
				throws Exception {
			super(uuid, appid, serverAddr, serverPort);

		}

		@Override
		public boolean hasNetworkConnection() {
			return Util.hasNetwork(OnlineService.this);
		}


		@Override
		public void trySystemSleep() {
			tryReleaseWakeLock();
		}

		@Override
		public void onPushMessage(Message message) {
			if(message == null){
				return;
			}
			if(message.getData() == null || message.getData().length == 0){
				return;
			}

//			bundle.putInt(PushConsts.CMD_ACTION, PushConsts.GET_CLIENTID);
//			//or
//			bundle.putInt(PushConsts.CMD_ACTION, PushConsts.THIRDPART_FEEDBACK);
//			//or
			if(message.getCmd() == 16){// 0x10 通用推送信息
				//notifyUser(16,"DDPush通用推送信息","时间："+DateTimeUtil.getCurDateTime(),"收到通用推送信息");
			}
			if(message.getCmd() == 17){// 0x11 分组推送信息
				long msg = ByteBuffer.wrap(message.getData(), 5, 8).getLong();
				//notifyUser(17,"DDPush分组推送信息",""+msg,"收到通用推送信息");
			}
			if(message.getCmd() == 32){// 0x20 自定义推送信息
				String str = null;
				try{
					str = new String(message.getData(),5,message.getContentLength(), "UTF-8");
				}catch(Exception e){
					str = Util.convert(message.getData(),5,message.getContentLength());
				}

				Intent intent=new Intent(PushConsts.PUSH_ACTION);
				Bundle bundle=new Bundle();
				Log.i("OnlineService", "msg:" + str);
				bundle.putInt(PushConsts.CMD_ACTION, PushConsts.GET_MSG_DATA);
				bundle.putString(PushConsts.DATA, str);
				intent.putExtras(bundle);
				sendBroadcast(intent);
				//notifyUser(32,"DDPush自定义推送信息",""+str,"收到自定义推送信息");
			}
			setPkgsInfo();
		}
	}
}
