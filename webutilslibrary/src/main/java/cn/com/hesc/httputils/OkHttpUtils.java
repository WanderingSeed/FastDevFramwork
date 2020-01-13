package cn.com.hesc.httputils;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import cn.com.hesc.httputils.builder.GetBuilder;
import cn.com.hesc.httputils.builder.PostStringBuilder;
import cn.com.hesc.httputils.callback.Callback;
import cn.com.hesc.httputils.cookie.store.CookieStore;
import cn.com.hesc.httputils.cookie.store.HasCookieStore;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: OkHttpUtils
 * Description: 根据开源的okhttp进行二次封装，为了迎合android6.0以上废除了httpclient
 * Author: liujunlin
 * Date: 2016-04-08 09:31
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class OkHttpUtils {

    public static final long DEFAULT_MILLISECONDS = 30000;
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;//指定使用okhttpclient
    private Handler mDelivery;

    public OkHttpUtils(OkHttpClient okHttpClient)
    {
        if (okHttpClient == null)
        {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            //cookie enabled
            okHttpClientBuilder.cookieJar(new cn.com.hesc.httputils.cookie.CookieJarImpl(new cn.com.hesc.httputils.cookie.store.MemoryCookieStore()));
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            });

            mOkHttpClient = okHttpClientBuilder.build();
        } else
        {
            mOkHttpClient = okHttpClient;
        }

        init();
    }

    private void init()
    {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtils getInstance(OkHttpClient okHttpClient)
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils(null);
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery()
    {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }

    /**
     * 获取get方法的builder
     * @return
     */
    public static GetBuilder get()
    {
        return new GetBuilder();
    }

    /**
     * 获取psot方法传string的builder
     * @return
     */
    public static PostStringBuilder postString()
    {
        return new PostStringBuilder();
    }

    /**
     * 获取post方法传文件的builder
     * @return
     */
    public static cn.com.hesc.httputils.builder.PostFileBuilder postFile()
    {
        return new cn.com.hesc.httputils.builder.PostFileBuilder();
    }

    /**
     * 获取post方法传表单的builder
     * @return
     */
    public static cn.com.hesc.httputils.builder.PostFormBuilder post()
    {
        return new cn.com.hesc.httputils.builder.PostFormBuilder();
    }

    /**
     * 获取put方法的builder
     * @return
     */
    public static cn.com.hesc.httputils.builder.OtherRequestBuilder put()
    {
        return new cn.com.hesc.httputils.builder.OtherRequestBuilder(METHOD.PUT);
    }

    /**
     * 获取head方法的builder
     * @return
     */
    public static cn.com.hesc.httputils.builder.HeadBuilder head()
    {
        return new cn.com.hesc.httputils.builder.HeadBuilder();
    }

    /**
     * 获取delete方法的builder
     * @return
     */
    public static cn.com.hesc.httputils.builder.OtherRequestBuilder delete()
    {
        return new cn.com.hesc.httputils.builder.OtherRequestBuilder(METHOD.DELETE);
    }

    /**
     * 获取patch方法的builder
     * @return
     */
    public static cn.com.hesc.httputils.builder.OtherRequestBuilder patch()
    {
        return new cn.com.hesc.httputils.builder.OtherRequestBuilder(METHOD.PATCH);
    }

    /**
     * 异步执行request
     * @param requestCall request请求
     * @param callback 服务器返回回调，可以自己实现回调方式
     */
    public void execute(final cn.com.hesc.httputils.request.RequestCall requestCall, Callback callback)
    {
        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new okhttp3.Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(final Call call, final Response response)
            {
                if (response.code() >= 400 && response.code() <= 599)
                {
                    try
                    {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    return;
                }

                try
                {
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (Exception e)
                {
                    sendFailResultCallback(call, e, finalCallback);
                }

            }
        });
    }

    public CookieStore getCookieStore()
    {
        final CookieJar cookieJar = mOkHttpClient.cookieJar();
        if (cookieJar == null)
        {
            cn.com.hesc.httputils.utils.Exceptions.illegalArgument("you should invoked okHttpClientBuilder.cookieJar() to set a cookieJar.");
        }
        if (cookieJar instanceof HasCookieStore)
        {
            return ((HasCookieStore) cookieJar).getCookieStore();
        } else
        {
            return null;
        }
    }


    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback)
    {
        if (callback == null) return;

        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onError(call, e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback)
    {
        if (callback == null) return;
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag)
    {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
    }


    /**
     * for https-way authentication
     *
     * @param certificates
     */
    public void setCertificates(InputStream... certificates)
    {
        SSLSocketFactory sslSocketFactory = cn.com.hesc.httputils.https.HttpsUtils.getSslSocketFactory(certificates, null, null);

        OkHttpClient.Builder builder = getOkHttpClient().newBuilder();
        builder = builder.sslSocketFactory(sslSocketFactory);
        mOkHttpClient = builder.build();


    }

    /**
     * for https mutual authentication
     *
     * @param certificates
     * @param bksFile
     * @param password
     */
    public void setCertificates(InputStream[] certificates, InputStream bksFile, String password)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(cn.com.hesc.httputils.https.HttpsUtils.getSslSocketFactory(certificates, bksFile, password))
                .build();
    }

    public void setHostNameVerifier(HostnameVerifier hostNameVerifier)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .hostnameVerifier(hostNameVerifier)
                .build();
    }

    public void setConnectTimeout(int timeout, TimeUnit units)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(timeout, units)
                .build();
    }

    public void setReadTimeout(int timeout, TimeUnit units)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .readTimeout(timeout, units)
                .build();
    }

    public void setWriteTimeout(int timeout, TimeUnit units)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .writeTimeout(timeout, units)
                .build();
    }


    public static class METHOD
    {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}
