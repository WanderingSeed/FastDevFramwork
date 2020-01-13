package cn.com.hesc.tools;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * ProjectName: FastDev-master
 * ClassName: OpenActivityUtils
 * Description: 打开其他APP的各种方法
 * Author: liujunlin
 * Date: 2016-09-13 08:40
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class OpenActivityUtils {

    private Context mContext;
    public OpenActivityUtils(Context context){
        mContext = context;
    }

    /**
     * 打开已知package的APP，可以不用进行安装，APP一定要放在存储卡上，针对是自己开发的APP，可以控制的
     * 此方法会让新activity以现有APP的风格打开，用法：如A--B.
     * B里需要自定义一个方法：
     * private Activity mActivity;
     * public void setActivity(Activity activity){
     *     mActivity = activity;//这样就把A的格式传入了B，使得B也在A的风格下运行
     * }
     * @param bundle 要传入对应activity里的savedInstanceState的bundle
     * @param dexpath APP在存储卡上的路径
     * @param dexoutputpath 生成dex文件的路径，默认可以直接放在存储卡根路径下
     */
    public void loadOtherApk(Bundle bundle, String dexpath, String dexoutputpath){
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(dexpath,dexoutputpath,null,classLoader);
        try{
            PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(dexpath, PackageManager.GET_ACTIVITIES);
            if(packageInfo.activities!=null && packageInfo.activities.length>0){
                String activitystr = packageInfo.activities[0].name;
                Class activityclass = classLoader.loadClass(activitystr);
                Constructor localConstructor = activityclass.getConstructor(new Class[]{});
                Object object = localConstructor.newInstance(new Object[]{});
                Method localMethod = activityclass.getDeclaredMethod("setActivity",new Class[]{Activity.class});
                localMethod.setAccessible(true);
                localMethod.invoke(object,new Object[]{});
                Method methodOnCreate = activityclass.getDeclaredMethod("onCreate",new Class[]{Bundle.class});
                methodOnCreate.setAccessible(true);
                methodOnCreate.invoke(object,new Object[]{bundle});
            }
            return;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 直接打开第三方的APP，不做任何改动
     * @param bundle 要传给第三方APP的内容
     * @param packagename 第三方app的包名
     * @param openactivityname 第三方app要打开的activity
     */
    public void openOtherApk(Bundle bundle,String packagename,String openactivityname){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName com = new ComponentName(packagename,
                openactivityname);
        intent.setComponent(com);

        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            intent.putExtra("bundle", bundle);
            mContext.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
