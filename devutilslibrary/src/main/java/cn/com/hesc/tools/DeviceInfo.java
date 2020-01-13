package cn.com.hesc.tools;

import android.content.Context;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * ProjectName: FastDev-master
 * ClassName: DeviceInfo
 * Description: TODO
 * Author: liujunlin
 * Date: 2016-11-21 09:00
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class DeviceInfo {
    private Context mContext;
    TelephonyManager tm = null;

    public DeviceInfo(Context context){
        mContext = context;
        tm = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
    }
    /**
   * 电话状态：
   * 1.tm.CALL_STATE_IDLE=0          无活动
   * 2.tm.CALL_STATE_RINGING=1  响铃
   * 3.tm.CALL_STATE_OFFHOOK=2  摘机
   */
    public int getPhoneState(){
        return tm.getCallState();
    }

    /**
   * 唯一的设备ID：
   * GSM手机的 IMEI 和 CDMA手机的 MEID.
   * Return null if device ID is not available.
   */
    public String getDeviceCode(){
        return tm.getDeviceId();
    }

    /**
  * 设备的软件版本号：
  * 例如：the IMEI/SV(software version) for GSM phones.
  * Return null if the software version is not available.
  */
    public String getDeviceSoftVersion(){
        return tm.getDeviceSoftwareVersion();//String
    }

    /**
   * 手机类型：
   * 例如： PHONE_TYPE_NONE  无信号
     PHONE_TYPE_GSM   GSM信号
     PHONE_TYPE_CDMA  CDMA信号
   */
    public int getPhoneType(){
        return tm.getPhoneType();//int
    }

    /**
  * 服务商名称：
  * 例如：中国移动、联通
  * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
  */
    public String getPhoneSim(){
        if(tm.getSimState() == TelephonyManager.SIM_STATE_READY)
            return tm.getSimOperatorName();
        return  "";
    }
}
