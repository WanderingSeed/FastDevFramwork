package cn.com.hesc.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

import cn.com.hesc.request.HttpRequest;

/**
 * ProjectName: FastDev-master
 * ClassName: DownLoadUtils
 * Description: 下载文件管理工具
 * Author: liujunlin
 * Date: 2017-08-03 09:04
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class DownLoadUtils {

    /**下载的是图片，直接保留bitmap*/
    private Bitmap picBitmap;
    /**返回下载成功的文件路径*/
    private String picPath;

    /**
     * 下载文件到指定路径
     * @param fileUrl 文件url
     * @param filedir 本地路径
     * @param filename 文件名
     * @param responseLister url回调
     * @return 如果本地已下载保存过该文件，返回文件路径
     */
    public String downLoadFile(String fileUrl,String filedir,String filename,@Nullable HttpRequest.OnResponseLister<File> responseLister){
        if(TextUtils.isEmpty(fileUrl))
            return fileUrl;

        if(!fileUrl.contains("http") && !fileUrl.contains("https"))
            return fileUrl;

        File tempdir = new File(filedir);
        if(!tempdir.exists())
            tempdir.mkdirs();
        String temp = filedir+"/"+filename;
        File myCaptureFile = new File(temp);
        if(myCaptureFile.exists()){
            if(myCaptureFile.length() < 1024)
                myCaptureFile.delete();
            else
                return myCaptureFile.getAbsolutePath();
        }
        HttpRequest httpRequest = new HttpRequest();
        if(null != responseLister)
            httpRequest.setOnResponseLister(responseLister);
        httpRequest.downLoadFile(fileUrl,filedir,filename);

        return myCaptureFile.getAbsolutePath();
    }

    /**
     *
     * @param fileUrl
     * @param onResponseLister
     */
    public void downLoadFile(String fileUrl, HttpRequest.OnResponseLister<File> onResponseLister){
        if(TextUtils.isEmpty(fileUrl))
            return;
        if(!fileUrl.contains("http") && !fileUrl.contains("https"))
            return;
        String pic = fileUrl;
        String filename = pic.substring(pic.lastIndexOf("/")+1,pic.length());
        String dir = Environment.getExternalStorageDirectory().getPath() + "/hesc/download";
        File tempdir = new File(fileDirs(dir,filename));
        if(!tempdir.exists())
            tempdir.mkdirs();
        String temp = dir+"/"+filename;
        File myCaptureFile = new File(temp);
        if(myCaptureFile.exists()){
            if(myCaptureFile.length() < 1024)
                myCaptureFile.delete();
            else
                return;
        }

        downLoadFile(fileUrl,dir,filename,onResponseLister);
    }

    /**
     *
     * @param fileUrl
     * @param onResponseLister
     */
    public void downLoadFile(String fileUrl, String fileName,HttpRequest.OnResponseLister<File> onResponseLister){
        if(TextUtils.isEmpty(fileUrl))
            return;
        if(!fileUrl.contains("http") && !fileUrl.contains("https"))
            return;
        String filename = fileName;
        String dir = Environment.getExternalStorageDirectory().getPath() + "/hesc/download";
        File tempdir = new File(fileDirs(dir,filename));
        if(!tempdir.exists())
            tempdir.mkdirs();
        String temp = dir+"/"+filename;
        File myCaptureFile = new File(temp);
        if(myCaptureFile.exists()){
            if(myCaptureFile.length() < 1024)
                myCaptureFile.delete();
            else
                return;
        }

        downLoadFile(fileUrl,dir,filename,onResponseLister);
    }

    /**
     * 检测本地文件是否已进行了本地缓存处理
     * @param headPath 本地文件地址
     * @return
     */
    public boolean isExistFile(String headPath){
        if(TextUtils.isEmpty(headPath))
            return false;
        String picname = headPath.substring(headPath.lastIndexOf("/")+1,headPath.length());
        String dir = headPath.substring(0,headPath.lastIndexOf("/"));

        headPath = fileDirs(dir,picname) + "/" +picname;
        File tempdir = new File(headPath);
        if(!tempdir.exists())
            return false;
        if(tempdir.length() > 1024){
            if(picname.toLowerCase().contains(".jpg")||picname.toLowerCase().contains(".png")||picname.toLowerCase().contains(".jpeg"))
                picBitmap = BitmapFactory.decodeFile(tempdir.getAbsolutePath());
            picPath = tempdir.getAbsolutePath();
            return true;
        }

        return false;
    }

    private String fileDirs(String dir,String picname){
        if (TextUtils.isEmpty(picname))
            return "";
        String dirs = "";
        if(picname.toLowerCase().contains(".jpg")||picname.toLowerCase().contains(".jpeg")||picname.toLowerCase().contains(".png"))
            dirs = dir + "/pic/temp";
        else if(picname.toLowerCase().contains(".mp3")||picname.toLowerCase().contains(".wav")||picname.toLowerCase().contains(".amr"))
            dirs = dir + "/audio/temp";
        else if(picname.toLowerCase().contains(".mp4")||picname.toLowerCase().contains(".ogg"))
            dirs = dir + "/video/temp";
        else if(picname.toLowerCase().contains(".html")||picname.toLowerCase().contains(".xml")||picname.toLowerCase().contains(".xhtml"))
            dirs = dir + "/html/temp";
        else
            dirs = dir + "/temp";

        return dirs;
    }

    public Bitmap getPicBitmap() {
        return picBitmap;
    }

    public String getPicPath() {
        return picPath;
    }

    /**
     * 通过web读取图片，bitmap对象进行返回
     * @param url
     * @param onResponseLister 回调的对象即为bitmap
     */
    public void bitmapFromWeb(String url, HttpRequest.OnResponseLister onResponseLister){
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestImg(url);
        httpRequest.setOnResponseLister(onResponseLister);
    }
}
