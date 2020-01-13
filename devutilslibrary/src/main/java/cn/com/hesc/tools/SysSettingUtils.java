package cn.com.hesc.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * 打开本程序相关的所有系统设置
 * created by liujunlin on 2018/10/30 09:59
 */
public class SysSettingUtils {

    private Context mContext;

    public SysSettingUtils(Context context){
        mContext = context;
    }

    /**
     * 打开GPS设置
     */
    public void openGPSSetting(){
        // 转到手机设置界面，用户设置GPS
        Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mContext.startActivity(intent);
    }

    /**
     * 打开通知设置界面
     */
    public void openNotificationSetting(){
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", mContext.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", mContext.getPackageName());
            intent.putExtra("app_uid", mContext.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 打开应用管理设置界面
     */
    public void openApplicationSetting(){
        Intent intent = new Intent();
        Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(packageURI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

    }
}
