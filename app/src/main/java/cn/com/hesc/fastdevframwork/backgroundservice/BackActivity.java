package cn.com.hesc.fastdevframwork.backgroundservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.materialdialogs.HescMaterialDialog;
import cn.com.hesc.tools.TimeUtils;

public class BackActivity extends AppCompatActivity {


    private TextView showtxt;
    private Intent backservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);

        showtxt = (TextView)findViewById(R.id.showmsg);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.hesc.text");
        registerReceiver(new MyBroadCase(),intentFilter);
    }

    class MyBroadCase extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            final String str = intent.getStringExtra("txt");
            if(!TextUtils.isEmpty(str)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showtxt.setText(str);
                        try{
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification.Builder builder = new Notification.Builder(getApplicationContext());
                            builder.setSmallIcon(R.drawable.msgicon);
                            builder.setWhen(System.currentTimeMillis());
                            builder.setContentTitle("更新通知");
                            builder.setContentText("更新内容"+ TimeUtils.longToString(System.currentTimeMillis()));
                            manager.notify(MybgService.notificationid,builder.build());
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });
            }
        }
    }

    public void startService(View view){
        if(backservice == null){
            backservice = new Intent(this,MybgService.class);
            Bundle bundle = new Bundle();
            bundle.putString("notificationtitle",getString(R.string.app_name));
            bundle.putString("notificationcontent","这是一个测试通知");
            backservice.putExtras(bundle);
            startService(backservice);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isNotificationEnabled()){
            HescMaterialDialog hescMaterialDialog = new HescMaterialDialog(this);
            hescMaterialDialog.showConfirmDialog("通知未打开", "打开通知可以更精准的获取消息", "去设置", "不闭了", new HescMaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(HescMaterialDialog dialog) {
                    super.onPositive(dialog);
                    gotoSet();
                }

                @Override
                public void onNegative(HescMaterialDialog dialog) {
                    super.onNegative(dialog);
                }
            });
        }else{
            if(backservice == null){
                backservice = new Intent(this,MybgService.class);
                Bundle bundle = new Bundle();
                bundle.putString("notificationtitle",getString(R.string.app_name));
                bundle.putString("notificationcontent","这是一个测试通知");
                backservice.putExtras(bundle);
                startService(backservice);
            }
        }
    }

    /**
     * 获取是否开启通知
     * @return
     */
    private boolean isNotificationEnabled() {

        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        boolean isEnabled = notification.areNotificationsEnabled();
        return isEnabled;
    }

    private void gotoSet() {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {

       stopService(backservice);

        super.onDestroy();
    }
}
