package cn.com.hesc.tools;

import java.io.File;

/**
 * ProjectName: FastDev-master
 * ClassName: FiletypeUtils
 * Description: 检测文件格式
 * Author: liujunlin
 * Date: 2017-06-23 09:49
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class FiletypeUtils {
    /**
     * 根据文件判断文件类型
     * @param f 要判定的文件
     * @return 音频：audio；视频：video；图片：image；文本：txt
     */
    public static String getMIMEType(File f){
        String type = "";
        String fName = f.getName();
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();

        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav") || end.equals("amr")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("txt")) {
			/* android.permission.INSTALL_PACKAGES */
            type = "txt";
        } else {
            type = "*";
        }
        return type;
    }

    /**
     * 根据文件判断文件类型
     * @param file 要判定的文件
     * @return 音频：audio；视频：video；图片：image；文本：txt
     */
    public static String getMIMEType(String file){
        String type = "";
        String fName = file;
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();

        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav") || end.equals("amr")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("txt")) {
			/* android.permission.INSTALL_PACKAGES */
            type = "txt";
        } else {
            type = "*";
        }
        return type;
    }
}
