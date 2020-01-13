package cn.com.hesc.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: SdcardInfo
 * Description: 配置存储卡信息
 * Author: liujunlin
 * Date: 2016-05-23 09:36
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class SdcardInfo {

    private static SdcardInfo instance; // 单例模式

    /**
     * 返回存储卡路径
     */
    private static String currentSDCardPath = "";
    /**
     * 根据自己的APP来定义这个文件夹目录
     */
    private final static String APPPath = "/hesc";
    public static String File_Pic = Environment.getExternalStorageDirectory()
            .getPath() + APPPath + "/pic";// 照片全部放在这个目录下
    public static String File_Video = Environment.getExternalStorageDirectory()
            .getPath() + APPPath + "/video";// 视频全部放在这个目录下
    public static String File_Voice = Environment.getExternalStorageDirectory()
            .getPath() + APPPath + "/voice";// 录音全部放在这个目录下
    public static String File_Download = Environment.getExternalStorageDirectory()
            .getPath() + APPPath + "/download";// 下载全部放在这个目录下
    public static String File_Html = Environment.getExternalStorageDirectory()
            .getPath() + APPPath + "/html";// word转换来的html全部放在这个目录下

    /**
     * 获取当前系统的android版本号
     */
    static int currentApiVersion = Build.VERSION.SDK_INT;

    private SdcardInfo() {
        initSDCard();
    }

    public static SdcardInfo getInstance() {
        if (instance == null) {
            synchronized (SdcardInfo.class) {
                if (instance == null) {
                    instance = new SdcardInfo();
                }
            }
        }
        return instance;
    }

    private void initSDCard() {
        checkSDCard();
    }

    /**
     * 判断手机有几个存储卡，哪个存储卡空间大就用哪个存储卡来放置照片等多媒体
     */
    private static void checkSDCard() {
        String sdcardState = Environment.getExternalStorageState();
        if (sdcardState.equals(Environment.MEDIA_MOUNTED)) {
            /**内置存储卡*/
            File internalSDCard = Environment.getExternalStorageDirectory();
            double internalVolume = 0.0;
            String internalPath = "";
            double externalVolume = 0.0;
            String externalPath = "";
            if (internalSDCard.exists()) {
                internalVolume = spareCard(internalSDCard.getAbsolutePath());
                internalPath = internalSDCard.getAbsolutePath();
            }
            /**外置存储卡
             * 针对android4.4及以上版本的限制，暂时放弃外置存储卡的读取和写入
             * 目前测试，华为机可以读取，三星失效
             * */
//            ArrayList<String> paths = getExternalSDcardPaths();
//            if (paths != null && paths.size() > 0) {
//                double tempVolume = 0.0;
//                String tempPath = "";
//                for (String path : paths) {
//                    File fl = new File(path);
//                    if (fl.isDirectory() && fl.exists()) {
//                        double volume = spareCard(fl.getAbsolutePath());
//                        if (volume > 0) {
//                            if (tempVolume == 0.0) {
//                                tempVolume = volume;
//                                if (!TextUtils.isEmpty(fl.getAbsolutePath()))
//                                    tempPath = fl.getAbsolutePath();
//                            } else {
//                                if (tempVolume < volume) {
//                                    tempVolume = volume;
//                                    tempPath = fl.getAbsolutePath();
//                                }
//                            }
//                        }
//
//                    }
//                }
//                externalVolume = tempVolume;
//                externalPath = tempPath;
//            }

            if (internalVolume > externalVolume) {
                currentSDCardPath = internalPath;
                createDir(currentSDCardPath);
            } else {
                creatExDir();
            }
        }
    }

    @SuppressLint("NewApi")
    private static void createDir(String sdcardpath) {

        if (!TextUtils.isEmpty(sdcardpath)) {
            File_Pic = sdcardpath + APPPath + "/pic";
            File_Video = sdcardpath + APPPath + "/video";
            File_Voice = sdcardpath + APPPath + "/voice";
            File_Download = sdcardpath + APPPath + "/download";
            File fl = new File(File_Pic);
            if (!fl.exists())
                fl.mkdirs();
            File fl1 = new File(File_Video);
            if (!fl1.exists())
                fl1.mkdirs();
            File fl2 = new File(File_Voice);
            if (!fl2.exists())
                fl2.mkdirs();
            File fl3 = new File(File_Download);
            if (!fl3.exists())
                fl3.mkdirs();
            File fl4 = new File(File_Html);
            if (!fl4.exists())
                fl4.mkdirs();
        }
    }

    /**
     * 针对4.4及以上系统，对外插存储卡的修改
     */
    private static void creatExDir(){
        File_Pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File_Video = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File_Voice = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        File_Download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public boolean deleteFolder(String sPath) {
        boolean flag = true;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return false;
        } else {
            // 为文件时调用删除文件方法
            if (file.isFile()) {
                return file.delete();
            }
            // 为目录时调用删除目录方法
            else {
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private boolean deleteDirectory(String sPath) {
        boolean result = false;
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                if (!files[i].delete())
                    break;
            } //删除子目录
            else {
                boolean flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }

            result = true;
        }
        if (!result)
            return false;
        //删除当前目录
        return dirFile.delete();
    }

    /**
     * 获取SD卡的根路径
     * @return 返回路径
     */
    public String getSdcardpath() {
        return currentSDCardPath;
    }

    /**
     * SDCard是否已满，默认小于20M的空间就认为存储卡已满
     *
     * @return true已满   false未满
     */
    public boolean SdcardIsFull() {
        return !(spareCard(currentSDCardPath) > 20.0);
    }

    /**
     * 获取存储卡剩余空间
     * @return 返回剩余空间，以M为单位
     */
    public double getSdCardRemainSpace(){
        return spareCard(currentSDCardPath);
    }

    /**
     * SDCard是否存在
     *
     * @return true 存在存储卡  false不存在
     */
    public boolean isExistSDcard() {
        return !TextUtils.isEmpty(currentSDCardPath);
    }

    /**
     * @param path 存储卡路径
     * @return 剩余空间   以MB为单位
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static double spareCard(String path) {
        double spare;
        StatFs statfs = new StatFs(path);
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        long allStore;

        if (currentApiVersion < Build.VERSION_CODES.KITKAT) {
            //获取单个数据块的大小(Byte)
            blockSize = statfs.getBlockSize();
            /** 总 Block 数量 */
            totalBlocks = statfs.getBlockCount();
            /** 剩余的 Block 数量 */
            availableBlocks = statfs.getAvailableBlocks();
            /*存储卡总容量*/
            allStore = blockSize * totalBlocks;
        } else {
            //获取单个数据块的大小(Byte)
            blockSize = statfs.getBlockSizeLong();
            /** 总 Block 数量 */
            totalBlocks = statfs.getBlockCountLong();
            /** 剩余的 Block 数量 */
            availableBlocks = statfs.getAvailableBlocksLong();
			/*存储卡总容量*/
            allStore = blockSize * totalBlocks;
        }

        spare = (double) availableBlocks * blockSize / (double) (1024 * 1024);
        return spare;
    }


    /**
     * 获取外置存储卡
     *
     * @return
     */
    private ArrayList<String> getExternalSDcardPaths() {
        ArrayList<String> files = new ArrayList<>();
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(Environment.getExternalStorageDirectory()
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                files.add(mountPath);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return files;
    }
}
