package cn.com.hesc.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by liujunlin on 2015/5/26 17:28.
 * 获取软件版本号
 */
public class SoftVersion {

    private Context m_context;//上下文关系
    private String versionName = "";//软件版本号
    private int versioncode;//版本编码

    public SoftVersion(Context context){
        this.m_context = context;
        initGetVersion();
    }

    private void initGetVersion(){
        try {
            PackageManager pManager = m_context.getPackageManager();
            PackageInfo info = pManager.getPackageInfo(m_context.getPackageName(), 0);
            versionName = info.versionName;
            versioncode = info.versionCode;

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     * 获取版本号
     * @return
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * @return 版本号编码
     */
    public int getVersioncode() {
        return versioncode;
    }
}
