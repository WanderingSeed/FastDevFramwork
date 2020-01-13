package cn.com.hesc.fastdevframwork.webdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.request.HttpRequest;
import cn.com.hesc.request.WebserviceRequest;
import cn.com.hesc.utils.HttpWebUtils;
import cn.com.hesc.utils.WebServiceUtils;


public class WebUtilsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_utils);
    }

    public void httpGet(View view){
        String url = "http://192.168.0.105:8080/config/te/hello";
        Map<String,String> map = new HashMap<>();
        HttpWebUtils httpWebUtils = new HttpWebUtils();
        httpWebUtils.get_url(url, map, new HttpRequest.OnResponseLister() {
            @Override
            public void onResponse(Object response) {
                Log.e("成功",String.valueOf(response));
            }

            @Override
            public void onError(Object errormsg) {
                Log.e("失败",String.valueOf(errormsg));
            }

            @Override
            public void onDownLoad(float progress, long total) {

            }
        });
    }

    public void httpPost(View view){
        String url = "http://192.168.0.105:8080/config/te/add";
        Map<String,String> map = new HashMap<>();
        String id = System.currentTimeMillis()+"";
        String str_count = "test";
        map.put("id",id);
        map.put("desc",str_count);
        HttpWebUtils httpWebUtils = new HttpWebUtils();
        httpWebUtils.post_url(url, map, null,new HttpRequest.OnResponseLister() {
            @Override
            public void onResponse(Object response) {
                Log.e("成功",String.valueOf(response));
            }

            @Override
            public void onError(Object errormsg) {
                Log.e("失败",String.valueOf(errormsg));
            }

            @Override
            public void onDownLoad(float progress, long total) {

            }
        });
    }

    public void webservice(View view){

        UserMsg user = new UserMsg();
        user.setId("");
        user.setPageNum(1);


        String url = "http://115.236.1.244:81/webservice/webservice/WebService";
        String namespace = "http://webservice.trundle.hesc.com/";
        WebServiceUtils webServiceUtils = new WebServiceUtils(this);
        webServiceUtils.requestWebService(url,namespace,null,null,"getNotice", new Gson().toJson(user),null,new WebserviceRequest.OnResponseLister(){
            @Override
            public void onResponse(Object response) {
                Log.e("response",String.valueOf(response));
            }

            @Override
            public void onError(String errormsg, Exception e) {
                e.printStackTrace();
                Log.e("response",String.valueOf(errormsg));
            }
        });
    }

    public void robredpack(View view){

        final String[] useid = {"a","b","c","d","e","f","g","h","i","j"};
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "http://192.168.0.106/redpack/robredpack/26b3a9146330475396b1cceea0ae58d7/"+useid[new Random().nextInt(10)];
                    Map<String,String> map = new HashMap<>();
                    HttpWebUtils httpWebUtils = new HttpWebUtils();
                    httpWebUtils.post_url(url, map,new HttpRequest.OnResponseLister() {
                        @Override
                        public void onResponse(Object response) {
                            Log.e("成功",String.valueOf(response));
                        }

                        @Override
                        public void onError(Object errormsg) {
                            Log.e("失败",String.valueOf(errormsg));
                        }

                        @Override
                        public void onDownLoad(float progress, long total) {

                        }
                    });
                }
            }).start();
        }


    }

    class UserMsg{
        String id;
        int pageNum;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }
    }
}
