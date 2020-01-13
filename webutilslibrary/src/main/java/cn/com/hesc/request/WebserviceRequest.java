package cn.com.hesc.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cn.com.hesc.webserviceutils.MyAndroidHttpTransport;

/**
 * ProjectName: FastDev-master
 * ClassName: WebserviceRequest
 * Description: 封装了webservice的调用，通过回调函数返回值
 * Author: liujunlin
 * Date: 2016-10-18 17:14
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class WebserviceRequest {
    enum Msg_enum {
        XML_ERROR, NET_ERROR, METHOD_ERROR, URLISNULL_ERROR, PARA_ERROR, IO_ERROR, SUCCEED
    }

    private Msg_enum mMsg_enum;
    private String xmlString = "";//调用成功返回的字符串
    private String serverip = "";
    private OnResponseLister mOnResponseLister;
    private String mreqUrl;
    private String mnameSpace;
    private int mTimeOut = 15000;

    public WebserviceRequest(String reqUrl,String namespace){
        mreqUrl = TextUtils.isEmpty(reqUrl)?"":reqUrl;
        mnameSpace = TextUtils.isEmpty(namespace)?"":namespace;
    }

    public interface OnResponseLister<T>{
        void onResponse(T response);
        void onError(String errormsg, Exception e);
    }

    public void setOnResponseLister(OnResponseLister onResponseLister) {
        mOnResponseLister = onResponseLister;
    }

    public int getTimeOut() {
        return mTimeOut;
    }

    public void setTimeOut(int timeOut) {
        mTimeOut = timeOut;
    }

    /**
     * @return  接口调用失败返回错误信息，方便调试时使用
     */
    public String toString() {
        String result = "";
        switch (mMsg_enum) {
            case XML_ERROR:
                result = "XML解析错误";
                break;
            case NET_ERROR:
                result = "网络错误";
                break;
            case METHOD_ERROR:
                result = "接口方法不存在";
                break;
            case URLISNULL_ERROR:
                result = "接口地址不存在";
                break;
            case PARA_ERROR:
                result = "接口参数不准确";
                break;
            case IO_ERROR:
                result = "请检查服务是否正常";
                break;
            case SUCCEED:
                result = "准确";
                break;
        }
        return result;
    }

    /**
     * @author liujunlin
     * @param method
     *            接口方法名
     * @param para
     *            接口对应的参数名
     * @param paravalues
     *            接口对应的参数值
     */
    public void requestWebService(@NonNull final String method,@NonNull final String[] para,@NonNull final String[] paravalues) {

        String webserviceUrl = "";
        if(TextUtils.isEmpty(mreqUrl)||TextUtils.isEmpty(mnameSpace)){
            mMsg_enum = Msg_enum.URLISNULL_ERROR;
            Message msg1 = mHandler.obtainMessage();
            Bundle bundle1 = msg1.getData();
            bundle1.putInt("type",1);
            bundle1.putString("result","URL为空");
            bundle1.putSerializable("exception",null);
            msg1.setData(bundle1);
            mHandler.sendMessage(msg1);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SoapObject request = new SoapObject(mnameSpace, method);
                for (int i = 0; i < para.length; i++) {
                    request.addProperty(para[i], paravalues[i]);
                }
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.bodyOut = request;
                MyAndroidHttpTransport androidHttpTransport = new MyAndroidHttpTransport(
                        mreqUrl + "?wsdl", mTimeOut);
                androidHttpTransport.debug = true;

                try {
                    androidHttpTransport.call(null, envelope);
                } catch (IOException e) {
                    mMsg_enum = Msg_enum.IO_ERROR;
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putSerializable("exception",e);
                    bundle.putString("result","请检查服务是否正常");
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    return;

                } catch (XmlPullParserException e) {

                    mMsg_enum = Msg_enum.XML_ERROR;
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putSerializable("exception",e);
                    bundle.putString("result","XML解析错误");
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putSerializable("exception",e);
                    bundle.putString("result","服务调用失败");
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    return;
                }
                SoapObject o = null;
                try {
                    o = (SoapObject) envelope.bodyIn;
                    if(o!=null &&o.getProperty(0)!=null ){
                        xmlString = o.getProperty(0).toString();
                        mMsg_enum = Msg_enum.SUCCEED;
                        Message msg = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putInt("type",0);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }else{
                        Message msg = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putInt("type",1);
                        bundle.putSerializable("exception",null);
                        bundle.putString("result","接口数据解析失败");
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        return;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    mMsg_enum = Msg_enum.METHOD_ERROR;
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putSerializable("exception",e);
                    bundle.putString("result","接口调用方法失败");
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    return;
                }
            }
        }).start();

    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.getData().getInt("type") == 0){
                if(mOnResponseLister!=null)
                    mOnResponseLister.onResponse(xmlString);
            }else{
                String result = msg.getData().getString("result","");
                Exception e = (Exception) msg.getData().get("exception");
                if(mOnResponseLister!=null)
                    mOnResponseLister.onError(result,e);
            }
        }
    };
}
