package cn.com.hesc.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by liujunlin on 2015/6/30 10:26.
 * 用来检测网络状态和网络类别
 */
public class NetWorkState {
    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private String nettype = "";

    public NetWorkState(Context context){
        this.mContext = context;
        mConnectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 获取网络类型
     * @return “wifi”、“mobile”，“”代表无网络或是别的网络类型
     */
    public String getNettype() {
        return nettype;
    }

    /**
     * 判断网络是否连接
     * @return
     */
    public boolean isHadNetWork(){
        boolean isexits = false;
        NetworkInfo nk = mConnectivityManager.getActiveNetworkInfo();
        if(nk != null) {
            isexits = nk.isConnected();
            int nettypeint = nk.getType();
            if(nettypeint == ConnectivityManager.TYPE_WIFI)
                nettype = "wifi";
            else if(nettypeint == ConnectivityManager.TYPE_MOBILE)
                nettype = "mobile";
        }

        return  isexits;

    }


}
