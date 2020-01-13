package cn.com.hesc.request;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.hesc.httputils.OkHttpUtils;
import cn.com.hesc.httputils.builder.GetBuilder;
import cn.com.hesc.httputils.builder.PostFormBuilder;
import cn.com.hesc.httputils.builder.PostStringBuilder;
import cn.com.hesc.httputils.callback.BitmapCallback;
import cn.com.hesc.httputils.callback.FileCallBack;
import cn.com.hesc.httputils.callback.StringCallback;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * ProjectName: FastDev-master
 * ClassName: HttpRequest
 * Description: 将http请求封装到这里
 * Author: liujunlin
 * Date: 2016-10-18 17:32
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class HttpRequest {
    private OnResponseLister mOnResponseLister;

    /**
     * 调用服务后的回调接口
     */
    public interface OnResponseLister<T>{
        /**调用成功*/
        void onResponse(T response);
        /**调用失败*/
        void onError(Object errormsg);
        /**文件下载 progress进度下载的百分比 total总量*/
        void onDownLoad(float progress, long total);
    }

    /**
     * web返回的监听，必须设置才能获取返回值
     * @param onResponseLister
     */
    public void setOnResponseLister(OnResponseLister onResponseLister) {
        mOnResponseLister = onResponseLister;
    }

    /**
     * post基本用法
     * @param reqUrl 请求地址
     * @param method 请求方法
     * @param params 参数名
     * @param values 参数值
     */
    public void requestWeb(String reqUrl,String method,String[] params,String[] values){
        Map<String,String> mapparams = new HashMap<>();
        for (int i=0;i<params.length;i++) {
            mapparams.put(params[i],values[i]);
        }
        OkHttpUtils.post().url(reqUrl+method).params(mapparams)
                .build().execute(new StringCallback() {


            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });

    }

    /**
     * POST请求
     * @param reqUrl 请求地址
     * @param method 请求方法
     * @param params 请求参数
     * @param values 请求值
     * @param headers http头文件
     */
    public void requestWeb(String reqUrl,String method,String[] params,String[] values,Map<String,String> headers){
        Map<String,String> mapparams = new HashMap<>();
        for (int i=0;i<params.length;i++) {
            mapparams.put(params[i],values[i]);
        }
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        if(headers!=null)
            postFormBuilder.headers(headers);
        postFormBuilder.url(reqUrl+method).params(mapparams)
                .build().execute(new StringCallback() {


            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });

    }

    /**
     * 以表单方式提交数据信息
     * @param reqUrl 请求的地址
     * @param params 表单参数名
     * @param values 参数值
     */
    public void requestWeb_PostForm(String reqUrl,String[] params,String[] values){
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        Map<String,String> mapparams = new HashMap<>();
        for (int i=0;i<params.length;i++) {
            mapparams.put(params[i],values[i]);
        }
        postFormBuilder.params(mapparams).url(reqUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * 以表单方式提交数据信息
     * @param reqUrl 请求的地址
     * @param params 表单参数名
     * @param values 参数值
     * @param headers http头文件
     */
    public void requestWeb_PostForm(String reqUrl,String[] params,String[] values,Map<String,String> headers){
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        Map<String,String> mapparams = new HashMap<>();
        for (int i=0;i<params.length;i++) {
            mapparams.put(params[i],values[i]);
        }
        if(headers!=null)
            postFormBuilder.headers(headers);
        postFormBuilder.params(mapparams).url(reqUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * http-get方法
     * @param reqUrl 请求的url
     */
    public void requestWeb_Get(String reqUrl){
        OkHttpUtils.get().url(reqUrl).build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * http-get方法
     * @param reqUrl 请求的url
     * @param headers HTTP头文件
     */
    public void requestWeb_Get(String reqUrl,Map<String,String> headers){
        GetBuilder builder =  OkHttpUtils.get();
        builder.url(reqUrl);
        if(headers!=null)
            builder.headers(headers);
        builder.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * post请求
     * @param reqUrl 请求地址
     * @param jsonobject utf-8格式的json对象传值
     */
    public void requestWebOfJson_UTF(String reqUrl,Object jsonobject){
        OkHttpUtils.postString().url(reqUrl)
                .mediaType(MediaType.parse("application/json; charset=UTF-8"))
                .content(new Gson().toJson(jsonobject))
                .build().execute(new StringCallback() {

            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });

    }

    /**
     * post请求
     * @param reqUrl 请求地址
     * @param jsonobject utf-8格式的json对象传值
     */
    public void requestWebOfJson_UTF(String reqUrl,Object jsonobject,Map<String,String> headers){
        PostStringBuilder builder = OkHttpUtils.postString();
        if(headers!=null)
            builder.headers(headers);

        builder.url(reqUrl)
                .mediaType(MediaType.parse("application/json; charset=UTF-8"))
                .content(new Gson().toJson(jsonobject))
                .build().execute(new StringCallback() {

            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });

    }

    /**
     * post请求
     * @param reqUrl 请求地址
     * @param method 请求方法
     * @param jsonobject GBK格式的json对象传值
     */
    public void requestWebOfJson_GBK(String reqUrl,String method,Object jsonobject){
        OkHttpUtils.postString().url(reqUrl+method)
                .mediaType(MediaType.parse("application/json; charset=GBK"))
                .content(new Gson().toJson(jsonobject))
                .build().execute(new StringCallback() {

            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * post请求
     * @param reqUrl 请求地址
     * @param method 请求方法
     * @param jsonobject GBK格式的json对象传值
     */
    public void requestWebOfJson_GBK(String reqUrl,String method,Object jsonobject,Map<String,String> headers){
        PostStringBuilder builder = OkHttpUtils.postString();
        if(headers!=null)
            builder.headers(headers);
        builder.url(reqUrl+method)
                .mediaType(MediaType.parse("application/json; charset=GBK"))
                .content(new Gson().toJson(jsonobject))
                .build().execute(new StringCallback() {

            @Override
            public void onError(okhttp3.Call call, Exception e) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * 获取网络图片
     * @param url 请求地址
     */
    public void requestImg(String url){
        OkHttpUtils.get().url(url).build().connTimeOut(20000L).readTimeOut(20000L).writeTimeOut(20000L)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        if(mOnResponseLister!=null)
                            mOnResponseLister.onError(e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        if(mOnResponseLister!=null)
                            mOnResponseLister.onResponse(response);
                    }
                });
    }

    /**
     * 表单方式提交文件
     * @param reqUrl  请求http
     * @param files   上传的文件
     * @param namespace 上传的域，具体由http提供服务方给定
     */
    public void uploadFiles_Form(String reqUrl, List<File> files,String namespace){
        PostFormBuilder pb = OkHttpUtils.post();
        pb.url(reqUrl);
        for(int i=0;i<files.size();i++){
            File file = files.get(i);
            if(file.exists() && file.isFile()) {
                pb.addFile(namespace, file.getName(), file);
            }
        }
        pb.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                e.printStackTrace();
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * 将文字和图片等一起作为表单提交
     * @param reqUrl 请求的url
     * @param files  图片列表
     * @param paramers 表单数据
     * @param headers 表头信息
     */
    public void upload_FormData(String reqUrl, List<File> files, Map<String,String> paramers,ArrayMap<String,String> headers,String namespace){
        PostFormBuilder pb = OkHttpUtils.post();
        pb.url(reqUrl);
        if(paramers!=null)
            pb.params(paramers);
        if(headers!=null)
            pb.headers(headers);
        for(int i=0;i<files.size();i++){
            File file = files.get(i);
            if(file.exists() && file.isFile()) {
                pb.addFile(namespace, file.getName(), file);
            }
        }
        pb.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                e.printStackTrace();
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * 将文字和图片（处理前、处理后）等一起作为表单提交
     * @param reqUrl 请求的url
     * @param files  处理前图片列表
     * @param haddealfiles  处理后图片列表
     * @param paramers 表单数据
     * @param headers 表头信息
     * @param namespace 处理前图片域
     * @param haddealnamespace 处理后图片域
     */
    public void upload_FastFormData(String reqUrl, List<File> files,List<File> haddealfiles, Map<String,String> paramers,ArrayMap<String,String> headers,String namespace,String haddealnamespace){
        PostFormBuilder pb = OkHttpUtils.post();
        pb.url(reqUrl);
        if(paramers!=null)
            pb.params(paramers);
        if(headers!=null)
            pb.headers(headers);
        for(int i=0;i<files.size();i++){
            File file = files.get(i);
            if(file.exists() && file.isFile()) {
                pb.addFile(namespace, file.getName(), file);
            }
        }
        for(int i=0;i<haddealfiles.size();i++){
            File file = haddealfiles.get(i);
            if(file.exists() && file.isFile()) {
                pb.addFile(haddealnamespace, file.getName(), file);
            }
        }
        pb.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                e.printStackTrace();
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

    /**
     * 通过http下载文件
     * @param downUrl 下载路径
     * @param localPath 本地路径
     * @param filename  保存的文件名
     */
    public void downLoadFile(String downUrl,final String localPath,final String filename){

        OkHttpUtils.get().url(downUrl).build().execute(new FileCallBack(localPath,filename) {
            /**
             *
             * @param progress 下载的百分比
             * @param total 文件size
             */
            @Override
            public void inProgress(float progress, long total) {
                //下载进度
                if(mOnResponseLister!=null)
                    mOnResponseLister.onDownLoad(progress,total);
            }

            @Override
            public void onError(okhttp3.Call call, Exception e) {
                e.printStackTrace();
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(e.getMessage());
            }

            @Override
            public void onResponse(File response) {
                //下载完成
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(response);
            }
        });
    }

}
