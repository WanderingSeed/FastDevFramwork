package cn.com.hesc.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 检测权限是否开启
 * created by liujunlin on 2018/11/1 10:50
 */
public class CheckPermissonUtils {

    private Activity mActivity;
    private String[] mPermissions;

    /**
     * 只能在activity里进行检测
     * @param activity
     * @param permissions
     */
    public CheckPermissonUtils(Activity activity,@NonNull String[] permissions){
        mActivity = activity;
        mPermissions = permissions;
    }

    public boolean checkPermission( String[] permissions){
        //只有6及以上才会检测权限
        boolean result = true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(null != permissions){
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (ContextCompat.checkSelfPermission(mActivity.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(mActivity, permissions, 0);
                        result = false;
                        break;
                    }
                }

                return result;
            }else
                return false;
        }

        return true;

    }
}
