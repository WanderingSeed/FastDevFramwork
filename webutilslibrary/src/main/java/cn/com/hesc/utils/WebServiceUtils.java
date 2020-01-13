package cn.com.hesc.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.com.hesc.request.WebserviceRequest;

/**
 * ProjectName: unifylogin
 * ClassName: WebUtils
 * Description: webservice工具类
 * Author: liujunlin
 * Date: 2018-03-21 15:58
 */
public class WebServiceUtils {
    private Context mContext;

    public WebServiceUtils(Context context){
        mContext = context;
    }

    /**
     * 对外提供接口服务的统一方法,回调里的对象为Msg
     * @param url 接口地址
     * @param namespace 接口服务的namespace
     * @param version 客户端版本号
     * @param mac 客户端机器码
     * @param operate 客户端调用的方法类型-webservice提供的方法
     * @param content 客户端传过来的值，json格式的数据
     * @param type 客户端类型
     */
    public void requestWebService(@NonNull String url,@NonNull String namespace, String version, String mac,@NonNull String operate,@NonNull String content, String type, final WebserviceRequest.OnResponseLister onResponseLister){

        final WebserviceRequest webserviceRequest = new WebserviceRequest(url,namespace);

        if(TextUtils.isEmpty(version))
            version = "1.0";

        if(TextUtils.isEmpty(mac))
            mac = "";

        if(TextUtils.isEmpty(type))
            type = "mobile";

        webserviceRequest.requestWebService("getService",new String[]{"arg0","arg1","arg2","arg3","arg4"},new String[]{version
                ,mac,operate,content,type});

        webserviceRequest.setOnResponseLister(onResponseLister);
    }

}
