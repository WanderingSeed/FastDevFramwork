package cn.com.hesc.maplibrary.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * created by liujunlin on 2018/9/10 14:36
 */
public class BitMapUtils {

    public static Bitmap getURLBitmap2(String uri) {
        Bitmap bitmap = null;
        InputStream inputStreamForPartsMap = null;
        HttpURLConnection httpURLConnectionForPartsMap = null;
        try {

//            uri = URLEncoder.encode(uri,"UTF-8");
            URL imageUrl = new URL(uri);
            httpURLConnectionForPartsMap = (HttpURLConnection) imageUrl
                    .openConnection();
            if (httpURLConnectionForPartsMap != null) {
                httpURLConnectionForPartsMap.setDoInput(true);
                httpURLConnectionForPartsMap.connect();

                int connectCode = httpURLConnectionForPartsMap
                        .getResponseCode();
                if (connectCode == HttpURLConnection.HTTP_OK) {
                    inputStreamForPartsMap = httpURLConnectionForPartsMap
                            .getInputStream();

                    //---------------------OpenLayers 服务--------------------------------------------
//                        if(inputStreamForPartsMap!=null){
//
//                            String log = SdcardInfo.getInstance().getSdcardpath()+java.io.File.separator+"temp.png";
//                            File file = new File(log);
//                            if(!file.exists())
//                                file.createNewFile();
//                            FileOutputStream fileOut = new FileOutputStream(file);
//
//                            int len = 0;
//                            byte[] buffer = new byte[1024];
//                            while ((len = inputStreamForPartsMap.read(buffer)) != -1) {
//                                fileOut.write(buffer, 0, len);
//                            }

//                              fileOut.close();
//
//                            if(file.exists()){
//                                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                                  file.delete();
//                            }
//                        }
                    //---------------------OpenLayers 服务--------------------------------------------

                    //--------------------正常的arcgis服务--------------------------------------------
                    if (inputStreamForPartsMap != null) {
                        bitmap = BitmapFactory.decodeStream(inputStreamForPartsMap);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamForPartsMap != null) {
                    inputStreamForPartsMap.close();
                }

                if (httpURLConnectionForPartsMap != null) {
                    httpURLConnectionForPartsMap.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
