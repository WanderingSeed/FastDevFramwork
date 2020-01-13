package cn.com.hesc.maplibrary.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import cn.com.hesc.maplibrary.PubGisUtil;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: PubBitmap
 * Description: 用于记录图片信息的类
 * Author: liujunlin
 * Date: 2016-04-11 16:23
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class PubBitmap {


    /**
     * 图片左上角顶点位置信息
     */
    private float x = 0;
    private float y = 0;

    /**
     * 图片数据信息
     */
    public String filePath = "";
    /**
     * 标注（true：需要画;false 不需要画）
     */
    private boolean show = false;
    private int resourceid = 0;

    public PubBitmap(int resourceid){
        this.resourceid = resourceid;
    }

    /**
     * 构造函数
     */
    public PubBitmap(String assertFileName) {
        this.filePath = assertFileName;
    }

    /**
     * 构造函数
     */
    public PubBitmap(String path, float x, float y, boolean show) {
        this.filePath = path;
        this.x = x;
        this.y = y;
        this.show = show;
    }




    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * 从文件里读取
     * @return
     * @throws Exception
     */
    public Bitmap getBitmapFromFile() throws Exception {
        Bitmap bitmap = null;
        try {
                File file = new File(filePath);
                if (file != null && file.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // 8就代表容量变为以前容量的1/8
                    options.inSampleSize = 1;
                    bitmap = BitmapFactory.decodeFile(filePath, options);
                } else {
                    bitmap = null;
                }
        } catch (Exception e) {
            throw e;
        }

        return bitmap;
    }

    /**
     * 从Assert文件里获取地图用到的图标
     * @param context
     * @return
     * @throws Exception
     */
    public Bitmap getBitmapFromAssertDirectory(Context context) {
        Bitmap bitmap = null;
        bitmap = PubGisUtil.getImageFromAssetFile(context, filePath);

        return bitmap;
    }

    /**
     * 从drawable里获取地图用到的图标
     * @param context
     * @return
     * @throws Exception
     */
    public Bitmap getBitmapFromResource(Context context) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(context.getResources(),resourceid);
        return bitmap;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }


}
