package cn.com.hesc.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.hesc.request.HttpRequest;

/**
 * ProjectName: unifylogin
 * ClassName: HttpWebUtils
 * Description: 封装常用的http请求
 * Author: liujunlin
 * Date: 2018-03-21 15:55
 */
public class HttpWebUtils {

    /**
     * 附件上报
     * url  附件服务地址
     * @param medias 多媒体文件列表
     * @param onResponseLister http调用完成的回调
     */
    public void sendMediaFiles(@NonNull final String url, @NonNull List<String> medias, @NonNull final HttpRequest.OnResponseLister onResponseLister){

        String namespace = "file";

        List<File> files = new ArrayList<>();
        if(medias!=null && medias.size()>0){
            for (String str:medias) {
                File file = new File(str);
                if(file.exists() && file.isFile())
                    files.add(file);
            }
        }

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.uploadFiles_Form(url,files,namespace);
        httpRequest.setOnResponseLister(onResponseLister);
    }

    /**
     * post请求 contenttype-  x-www-form-urlencoded
     * @param url
     * @param params
     * @param onResponseLister
     */
    public void post_url(@NonNull String url, @NonNull Map<String,String> params, @NonNull final HttpRequest.OnResponseLister onResponseLister){
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
        String[] keyArray = keys.toArray(new String[keys.size()]);
        String[] valueArray = values.toArray(new String[keys.size()]);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestWeb(url,"",keyArray,valueArray);
        httpRequest.setOnResponseLister(onResponseLister);
    }

    /**
     * post请求 contenttype-application-json
     * @param url 地址
     * @param object 参数值
     * @param headers httpheader
     * @param onResponseLister 回调
     */
    public void post_url(@NonNull String url,@NonNull Object object,@Nullable Map<String,String> headers,@NonNull final HttpRequest.OnResponseLister onResponseLister){
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestWebOfJson_UTF(url,object,headers);
        httpRequest.setOnResponseLister(onResponseLister);
    }

    /**
     * post-form请求
     * @param url 请求方法
     * @param params map参数
     * @param headers 可为空的http头信息
     */
    public void post_form(@NonNull String url, @NonNull Map<String,String> params, @Nullable Map<String,String> headers,@NonNull final HttpRequest.OnResponseLister onResponseLister){
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
        String[] keyArray = keys.toArray(new String[keys.size()]);
        String[] valueArray = values.toArray(new String[keys.size()]);

        HttpRequest httpRequest = new HttpRequest();
        if(headers == null){
            httpRequest.requestWeb_PostForm(url,keyArray,valueArray);
        }else{
            httpRequest.requestWeb_PostForm(url,keyArray,valueArray,headers);
        }
        httpRequest.setOnResponseLister(onResponseLister);
    }

    /**
     * get请求
     * @param url 请求地址
     * @param headers 可为空的头文件信息
     * @param onResponseLister 服务回调
     */
    public void get_url(@NonNull String url, @Nullable Map<String,String> headers, @NonNull final HttpRequest.OnResponseLister onResponseLister){
        HttpRequest httpRequest = new HttpRequest();
        if(headers == null)
            httpRequest.requestWeb_Get(url);
        else
            httpRequest.requestWeb_Get(url,headers);
        httpRequest.setOnResponseLister(onResponseLister);
    }
}
