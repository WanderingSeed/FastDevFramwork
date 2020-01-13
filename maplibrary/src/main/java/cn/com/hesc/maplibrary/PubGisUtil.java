package cn.com.hesc.maplibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.com.hesc.httputils.OkHttpUtils;
import cn.com.hesc.httputils.callback.BitmapCallback;
import cn.com.hesc.httputils.callback.Callback;
import cn.com.hesc.httputils.callback.StringCallback;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapPoint;
import okhttp3.Call;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: PubGisUtil
 * Description: 工具类
 * Author: liujunlin
 * Date: 2016-04-11 16:35
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class PubGisUtil {

    private static String logTag = "---PubGisUtil---";
    static final int BIGGER = 3;
    static final int SMALLER = 4;

    /**
     * 读取网络图片到本地_httpurlconnection版本
     * 需要在线程里执行操作
     * @param picUrlPath
     * @param dirName
     * @param fileName
     * @return
     */
    public static boolean storePic_httpurl(String picUrlPath, String dirName, String fileName) {
        boolean res = false;
        File file = null;
        InputStream inputStreamForBaseMap = null;
        HttpURLConnection httpURLConnectionForBaseMap = null;
        FileOutputStream fileOutputStreamForBaseMap = null;
        try {
            URL imageUrl = new URL(picUrlPath);
            Log.e("要下载的地址",picUrlPath);
			/* 取得连接 */
            if (imageUrl != null && URLUtil.isNetworkUrl(picUrlPath)) {
                httpURLConnectionForBaseMap = (HttpURLConnection) imageUrl
                        .openConnection();
                if (httpURLConnectionForBaseMap != null) {
                    httpURLConnectionForBaseMap.connect();
                    int connectCode = httpURLConnectionForBaseMap
                            .getResponseCode();
                    if (connectCode == HttpURLConnection.HTTP_OK) {
                        int lenghtOfFile = httpURLConnectionForBaseMap
                                .getContentLength();
						/* 文件夹不存在，则创建文件夹 */
                        File dirFile = new File(dirName);
                        if (dirFile != null && !dirFile.exists()) {
                            dirFile.mkdirs();
                        }
                        inputStreamForBaseMap = httpURLConnectionForBaseMap
                                .getInputStream();
                        int readLength = 0;

                        if (inputStreamForBaseMap != null) {
                            file = new File(dirName + "/" + fileName);
                            if (file != null) {
                                file.createNewFile();
								/* 取得返回的InputStream */
                                fileOutputStreamForBaseMap = new FileOutputStream(
                                        file);
                                byte[] bytes = new byte[1024 * 20];
                                int a = 0;
                                long readytime = System.currentTimeMillis();
                                while ((a = inputStreamForBaseMap.read(bytes)) > 0) {
                                    readLength += a;
                                    fileOutputStreamForBaseMap.write(bytes, 0,
                                            a);
                                }
                                /**默认是先读取文件大小，比对大小再保存，有时服务器返回的http头文件不对，导致获取不到文件大小，就通过下载后的大小来初略定下*/
                                if (lenghtOfFile == readLength) {
                                    res = true;
                                    Log.e("下载耗时:",(System.currentTimeMillis() - readytime)+"毫秒");
                                } else if(lenghtOfFile == -1){
                                    if(file.length() < 1024) {
                                        file.delete();
                                        Log.e("下载失败",picUrlPath);
                                    }else
                                        res = true;
                                }else{
                                    if(file.length() < 1024) {
                                        file.delete();
                                        Log.e("下载失败",picUrlPath);
                                    }
                                    else
                                        res = true;
                                }

                            }
                        }
                    }else if(connectCode == HttpURLConnection.HTTP_NOT_FOUND){
                        res = false;
                    }
                }
            }



        } catch (Exception e) {
           e.printStackTrace();
        } finally {

            try{
                if (fileOutputStreamForBaseMap != null) {
                    fileOutputStreamForBaseMap.flush();
                    fileOutputStreamForBaseMap.close();
                    fileOutputStreamForBaseMap = null;
                }
                if (inputStreamForBaseMap != null) {
                    inputStreamForBaseMap.close();
                    inputStreamForBaseMap = null;
                }
                if (httpURLConnectionForBaseMap != null) {
                    httpURLConnectionForBaseMap.disconnect();
                    httpURLConnectionForBaseMap = null;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return res;
    }

    public static byte[] getURLBitmap2Byte(String uri) {
        InputStream inputStreamForPartsMap = null;
        HttpURLConnection httpURLConnectionForPartsMap = null;
        ByteArrayOutputStream outStreamForPartsMap = null;
        byte[] data = null;
        try {
            URL imageUrl = new URL(uri);
            if (imageUrl != null) {
                httpURLConnectionForPartsMap = (HttpURLConnection) imageUrl
                        .openConnection();
                if (httpURLConnectionForPartsMap != null) {
                    httpURLConnectionForPartsMap.setDoInput(true);
                    httpURLConnectionForPartsMap.connect();

                    int connectCode = httpURLConnectionForPartsMap
                            .getResponseCode();
                    if (connectCode == HttpURLConnection.HTTP_OK) {
                        int lenghtOfFile = httpURLConnectionForPartsMap
                                .getContentLength();
                        inputStreamForPartsMap = httpURLConnectionForPartsMap
                                .getInputStream();
                        if (inputStreamForPartsMap != null) {
                            outStreamForPartsMap = new ByteArrayOutputStream();
                            byte[] buffer = new byte[16 * 1024];
                            int len = 0;
                            while ((len = inputStreamForPartsMap.read(buffer)) != -1) {
                                outStreamForPartsMap.write(buffer, 0, len);
                            }
                            if (outStreamForPartsMap != null) {
                                outStreamForPartsMap.close();
                            }
                            data = outStreamForPartsMap.toByteArray();

                        }
                    }
                }
            }
        } catch (Exception e) {
            String str1 = e.getMessage();
            String str2 = e.getLocalizedMessage();
            if (str2!=null&&str1!=null) {
                Log.i("--getURLBitmap2--", str2+"--"+str1);
            }
            else if(str1!=null){
                Log.i("--getURLBitmap2--", str1);
            }
            else if(str2!=null){
                Log.i("--getURLBitmap2--", str2);
            }else{
                e.printStackTrace();
            }
        } finally {
            try {
                if (outStreamForPartsMap != null) {
                    outStreamForPartsMap.close();
                    outStreamForPartsMap = null;
                }
                if (inputStreamForPartsMap != null) {
                    inputStreamForPartsMap.close();
                    inputStreamForPartsMap = null;
                }

                if (httpURLConnectionForPartsMap != null) {
                    httpURLConnectionForPartsMap.disconnect();
                    httpURLConnectionForPartsMap = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 读取网络图片到本地-okhttp版本
     * 在UI线程直接发起请求
     * @param picUrlPath
     * @param dirName
     * @param fileName
     * @return
     */
    public static boolean storePic_okhttp(String picUrlPath, String dirName, String fileName) {
        boolean res = true;
        try {
            /* 取得连接 */
            if (!TextUtils.isEmpty(picUrlPath) && URLUtil.isNetworkUrl(picUrlPath)) {

                /* 文件夹不存在，则创建文件夹 */
                File dirFile = new File(dirName);
                if (dirFile != null && !dirFile.exists()) {
                    dirFile.mkdirs();
                }
                final File file = new File(dirName + "/" + fileName);
                if (file != null) {
                    file.createNewFile();
                    OkHttpUtils.postFile().url(picUrlPath).file(file).build().execute(new BitmapCallback() {
                        @Override
                        //图片获取失败
                        public void onError(Call call, Exception e) {
                            e.printStackTrace();
                            file.delete();
                        }

                        @Override
                        public void onResponse(Bitmap bitmap) {
                            try {

                                if(bitmap!=null){
                                    BufferedOutputStream bos = new BufferedOutputStream(
                                            new FileOutputStream(file));

					            /* 采用压缩转档方法 */
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

					            /* 调用flush()方法，更新BufferStream */
                                    bos.flush();

					            /* 结束OutputStream */
                                    bos.close();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    /**
     * 获取图片信息
     *
     * @param filePath
     *            文件路径
     * @return
     */
    public static Bitmap getImageFromAssetFile(Context context, String filePath) {
        Bitmap image = null;
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(filePath);
            image = BitmapFactory.decodeStream(is);
            is.close();
            return image;
        } catch (Exception e) {
            Log.d(logTag, "--bitmap_error--" + e.getMessage());
        }
        return image;
    }

    class ResultCallback extends StringCallback {

        private String result = "";

        public String getResult() {
            return result;
        }

        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(String s) {
            result = s;
        }
    }



    /**
     * 使用get方法获取json数据
     * @param url
     * @return
     */
    public static void getJsonContent(String url, Callback callback){
        OkHttpUtils.get().url(url).build().execute(callback);
    }

    /**
     * 计算当前点距离距离原点的位置（图片编号返回）
     *
     * @param origin
     *            坐标原点
     * @param x
     *            当前点
     * @param tileDistance
     *           每张图片的宽度代表的地理距离
     * @return 图片编号，相当于所处的第几张图片的编号
     */
    public static long calculateCacheNumber(double origin, double x,
                                            double tileDistance) {
        long result = (long) Math.floor(Math.abs((origin - x) / tileDistance));
        return result;
    }

    /**
     * 根据屏幕横坐标获取地理横坐标
     * @param screenX 屏幕横坐标
     * @return 地理横坐标
     */
    public static double getGeoCoordinateX(float screenX, MapExtent currentMapExtent, double[] resolutions, int currentLevel, int screenWidth) {

        double geoCoordinateX = 0;
        try {
            geoCoordinateX = currentMapExtent.getCenterPoint().getX()
                    // this.screenWidth / 2 - screenX
                    // 表示在屏幕上的某一点距离屏幕中点位置x方向上的差值，又表示该点距离屏幕地图中点的x方向的差值
                    - (screenWidth / 2 - screenX)
                    // 上值乘以比例尺得到在实际范围中对应的某一点距离中点的x方向上的差值
                    * resolutions[currentLevel];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return geoCoordinateX;
    }

    /**
     * 根据屏幕纵坐标获取地理纵坐标
     * @param screenY 屏幕纵坐标
     * @return 地理纵坐标
     */
    public static double getGeoCoordinateY(float screenY, MapExtent currentMapExtent,double[] resolutions,int currentLevel,int screenHeight) {
        double geoCoordinateY = 0;
        try {
            geoCoordinateY = currentMapExtent.getCenterPoint().getY()
                    - (screenY - screenHeight / 2)
                    * resolutions[currentLevel];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return geoCoordinateY;
    }

    /**
     * 根据地理横坐标获取屏幕横坐标
     * @param geoX 地理横坐标
     * @return 屏幕横坐标
     */
    public static float getScreenX(double geoX, MapExtent currentMapExtent,double[] resolutions,int currentLevel) {
        float screenX = 0;
        try {
            screenX = (float) ((geoX - currentMapExtent.getMinX()) / resolutions[currentLevel]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenX;
    }

    /**
     * 根据地理纵坐标获取屏幕纵坐标
     * @param geoY 地理纵坐标
     * @return 屏幕横坐标
     */
    public static float getScreenY(double geoY, MapExtent currentMapExtent,double[] resolutions,int currentLevel) {
        float screenY = 0;
        try {
            //跟中心点去比较
            screenY = (float) ((currentMapExtent.getMaxY() - geoY) / resolutions[currentLevel]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenY;
    }

    public boolean isPointInPolygon(MapPoint tap, List<MapPoint> vertices) {
        int intersectCount = 0;
        for(int j=0; j<vertices.size()-1; j++) {
            if( LineIntersect(tap, vertices.get(j), vertices.get(j+1)) ) {
                intersectCount++;
            }
        }
        return (intersectCount%2) == 1; // odd = inside, even = outside;
    }
    private boolean LineIntersect(MapPoint tap, MapPoint vertA, MapPoint vertB) {
        double aY = vertA.getY();
        double bY = vertB.getY();
        double aX = vertA.getX();
        double bX = vertB.getX();
        double pY = tap.getY();
        double pX = tap.getX();
        if ( (aY>pY && bY>pY) || (aY<pY && bY<pY) || (aX<pX && bX<pX) ) {
            return false; }
        double m = (aY-bY) / (aX-bX);
        double bee = (-aX) * m + aY;                // y = mx + b
        double x = (pY - bee) / m;
        return x > pX;
    }

    public static String getResponseData(String url, String postData) {
        String data = null;
        try {
            URL dataUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) dataUrl
                    .openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);

            OutputStreamWriter out = null;
            out = new OutputStreamWriter(con.getOutputStream(), "gbk");
            out.write(postData);
            out.flush();
            out.close();

            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            reader.close();
            con.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
