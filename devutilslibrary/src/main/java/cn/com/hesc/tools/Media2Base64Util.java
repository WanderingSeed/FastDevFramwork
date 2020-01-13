package cn.com.hesc.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ProjectName: FastDev-master
 * ClassName: Media2Base64Util
 * Description: 将文件进行base64位编码
 * Author: liujunlin
 * Date: 2017-08-29 09:44
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class Media2Base64Util {
    /**
     * 将文件转换成Base64编码
     * @param imgFile 待处理图片
     * @return
     */
    public static String toBase64(String imgFile) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Base64.encode(data,Base64.DEFAULT));
    }

    /**
     * 对字节数组字符串进行Base64解码并生成文件
     *
     * @param imgStr      base64字符串形式
     * @param imgFilePath 要保存的图片全路径地址
     * @return
     */
    public static boolean fromBase64(String imgStr, String imgFilePath) {
        //
        if (imgStr == null) //图像数据为空
            return false;
        try {
            //Base64解码
            byte[] b = Base64.decode(imgStr,Base64.DEFAULT);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * base64转为bitmap
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
