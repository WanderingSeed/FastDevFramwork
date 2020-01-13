package cn.com.hesc.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: BitMapUtils
 * Description: bitmap工具类，将bitmap保存为文件，
 * Author: liujunlin
 * Date: 2016-05-26 10:12
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class BitMapUtils {

    /**
     * 将bitmap保存到本地文件
     * @param bm  要保存的bitmap对象
     * @param fileName 文件存放路径--全路径
     * @param quality 压缩比例，0-100取值，值越小图越质量差，占用空间越小
     */
    public static boolean saveFile(Bitmap bm, String fileName,int quality){

        boolean result = false;
        try{
            File dirFile = new File(fileName);
            if(!dirFile.exists()){
                if(!dirFile.createNewFile())
                    return false;
            }else{
                dirFile.delete();
                if(!dirFile.createNewFile())
                    return false;
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dirFile));
            bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();

            if(!bm.isRecycled()){
                bm.recycle();
            }

            int rotation = readPictureDegree(dirFile.getAbsolutePath());
            if(rotation > 0){
                Bitmap bitmap = rotateBitmapByDegree(BitmapFactory.decodeFile(fileName),rotation);
                if(dirFile.exists())
                    dirFile.delete();
                dirFile.createNewFile();
                BufferedOutputStream bos1 = new BufferedOutputStream(new FileOutputStream(dirFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos1);
                bos1.flush();
                bos1.close();

                if(!bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }

            result = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取图片属性：旋转的角度(有些相机涉及到横屏。纵屏拍摄问题)
     *
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转，解决拍照倒置的问题
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    private static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 对图片压缩处理,使用时请注意横竖屏
     *
     * @param filename 原图片的路径
     * @param maxWidth 目标宽度
     * @param maxHeight 目标长度
     * @param addTime 是否加水印
     * @return bitmap or null
     */
    public static Bitmap scalePicture(String filename, int maxWidth,
                                      int maxHeight,boolean addTime) {
        Bitmap bitmap = null;
        try {
            File file = new File(filename);
            if(file.exists()){
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                int srcWidth = opts.outWidth;
                int srcHeight = opts.outHeight;
                /**针对三星竖屏拍照，导致图片旋转*/
                if(readPictureDegree(filename) == 90 || readPictureDegree(filename) == 270){
                    srcWidth = opts.outHeight;
                    srcHeight = opts.outWidth;
                }

                opts.inJustDecodeBounds = false;

                if(srcWidth < maxWidth || srcHeight < maxHeight){
                    return BitmapFactory.decodeFile(filename);
                }

                int desWidth;
                int desHeight;
                boolean isHoriz;//横屏拍照
                // 缩放比例
                double ratio;
                if (srcWidth > srcHeight) {
                    ratio = srcWidth / Math.max(maxWidth,maxHeight);
                    desWidth = Math.max(maxWidth,maxHeight);
                    desHeight = (int) (srcHeight / ratio);
                    isHoriz = true;
                } else {
                    ratio = srcHeight / Math.max(maxWidth,maxHeight);
                    desHeight = Math.max(maxWidth,maxHeight);
                    desWidth = (int) (srcWidth / ratio);
                    isHoriz = false;
                }

                // 设置输出宽度、高度
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inSampleSize = (int) (ratio) + 1;
                newOpts.inJustDecodeBounds = false;
                newOpts.outWidth = desWidth;
                newOpts.outHeight = desHeight;
                Bitmap scalebm = BitmapFactory.decodeFile(filename, newOpts);//先压缩

                scalebm = rotateBitmapByDegree(scalebm,readPictureDegree(filename));

                Bitmap newbm  = ThumbnailUtils.extractThumbnail(scalebm,desWidth, desHeight);//再进行缩放

                if(addTime){
                    /*图片加水印*/
                    String time = TimeUtils.getSystime("yyyy-MM-dd HH:mm:ss");
                    Paint p = new Paint();
                    p.setColor(Color.rgb(255,167,38));
                    p.setStyle(Paint.Style.STROKE);
                    bitmap = Bitmap.createBitmap(desWidth, desHeight, Bitmap.Config.ARGB_8888);
                    Canvas cv = new Canvas(bitmap);
                    cv.drawBitmap(newbm, 0, 0, null);

                    if(isHoriz) {
                        p.setTextSize(38);
                        cv.drawText(time, desWidth / 4, desHeight - 20, p);
                    }
                    else {
                        p.setTextSize(28);
                        cv.drawText(time, 10, desHeight-20, p);
                    }
                    cv.save(Canvas.ALL_SAVE_FLAG);
                    cv.restore();

                    if(scalebm!=null)
                        scalebm.recycle();

                }else{
                    bitmap = newbm;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 混合压缩，建议大于1M的图片再进行压缩
     * @param image
     * @param filePath
     */
    public static boolean mixCompress(Bitmap image, String filePath) {

        boolean success = false;

        try{
            image = rotateBitmapByDegree(image,readPictureDegree(filePath));

            // 最大图片大小 1000KB
            int maxSize = 1000;
            // 获取尺寸压缩倍数
            int ratio = getRatioSize(image.getWidth(), image.getHeight());
            // 压缩Bitmap到对应尺寸
            Bitmap result = Bitmap.createBitmap(image.getWidth() / ratio, image.getHeight() / ratio, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, image.getWidth() / ratio, image.getHeight() / ratio);
            canvas.drawBitmap(image, null, rect, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int quality = 100;
            result.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            // 循环判断如果压缩后图片是否大于最大值,大于继续压缩
            while (baos.toByteArray().length / 1024 > maxSize) {
                // 重置baos即清空baos
                baos.reset();
                // 每次都减少10
                quality -= 10;
                // 这里压缩options%，把压缩后的数据存放到baos中
                result.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }

            File myCaptureFile = new File(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bos.write(baos.toByteArray());
            bos.flush();
            bos.close();
            success = true;

            // 释放Bitmap
            if (result != null && !result.isRecycled()) {
                result.recycle();
                result = null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return success;
    }

    /**
     * 计算缩放比
     *
     * @param bitWidth  当前图片宽度
     * @param bitHeight 当前图片高度
     * @return
     * @Description:函数描述
     */
    private static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = 1920;
        int imageWidth = 1080;
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageHeight) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageHeight;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }
}
