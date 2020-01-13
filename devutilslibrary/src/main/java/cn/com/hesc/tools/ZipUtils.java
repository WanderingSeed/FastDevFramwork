package cn.com.hesc.tools;

import android.util.Log;

import com.file.zip.ZipEntry;
import com.file.zip.ZipFile;
import com.file.zip.ZipOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;

import static android.content.ContentValues.TAG;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: ZipUtils
 * Description: 压缩、解压工具，只支持通用性好的zip格式，不解析别的格式
 * Author: liujunlin
 * Date: 2016-07-01 16:06
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class ZipUtils {

    public ZipUtils() {
    }

    private UnzipListener mUnzipListener;
    private zipListener mZipListener;

    public interface UnzipListener{
        void onSuccess(String result);
        void onError(String errorMsg);
    }

    public interface zipListener{
        void onSuccess(String result);
        void onError(String errorMsg);
    }

    /**
     * 添加解压缩回调
     * @param unzipListener
     */
    public void setUnzipListener(UnzipListener unzipListener) {
        mUnzipListener = unzipListener;
    }

    /**
     * 添加压缩回调
     * @param zipListener
     */
    public void setZipListener(zipListener zipListener) {
        mZipListener = zipListener;
    }

    /**
     * 解压文件，支持中文名称的文件
     * @param zipFile zip压缩包路径
     * @param folderPath 需要解压到目的地的文件夹
     */
    public void upZipFile(File zipFile, String folderPath){
        ZipFile zfile= null;
        try {
            zfile = new ZipFile(zipFile,"GBK");
        } catch (IOException e) {
            e.printStackTrace();
            if(mUnzipListener!=null){
                mUnzipListener.onError("压缩文件不存在");
            }
            return ;
        }
        try{
            Enumeration zList=zfile.getEntries();
            com.file.zip.ZipEntry ze=null;
            byte[] buf=new byte[1024];
            while(zList.hasMoreElements()){
                ze=(com.file.zip.ZipEntry)zList.nextElement();
                Log.e("zip",ze.getName());
                if(ze.isDirectory()){
                    String dirstr = folderPath +"/" +ze.getName();
                    File f=new File(dirstr);
                    f.mkdirs();
                    continue;
                }
                OutputStream os=new BufferedOutputStream(new FileOutputStream(new File(folderPath, ze.getName())));
                InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
                int readLen=0;
                while ((readLen=is.read(buf, 0, 1024))!=-1) {
                    os.write(buf, 0, readLen);
                }
                is.close();
                os.close();
            }
            zfile.close();
            if(mUnzipListener!=null){
                mUnzipListener.onSuccess("解压缩完成");
            }
        }catch (Exception e){
            e.printStackTrace();
            if(mUnzipListener!=null){
                mUnzipListener.onError(e.getMessage());
            }

        }
    }

    /**
     * [对指定路径下文件的压缩处理]
     * [功能详细描述]
     * @param srcFilePath 源文件(夹)路径
     * @param zipFilePath 指定到压缩文件夹的路径
     */
    public void zipFile(String srcFilePath, String zipFilePath){
        Log.e(TAG, "writeByApacheZipOutputStream");
        try {
            // ----压缩文件：
            ZipOutputStream f = new ZipOutputStream(new File(zipFilePath));
            // 使用指定校验和创建输出流
            CheckedOutputStream csum = new CheckedOutputStream(f, new CRC32());
            ZipOutputStream zos = new ZipOutputStream(csum);
            // 支持中文
            zos.setEncoding("GBK");
            BufferedOutputStream out = new BufferedOutputStream(zos);
            // 启用压缩
            zos.setMethod(ZipOutputStream.DEFLATED);
            // 压缩级别为最强压缩，但时间要花得多一点
            zos.setLevel(Deflater.BEST_COMPRESSION);
            //打开要输出的文件
            File file = new File(srcFilePath);
            //压缩
            zipFiles(file.getParent()+ File.separator, file.getName(),f);
            f.finish();
            f.close();
            // 注：校验和要在流关闭后才准备，一定要放在流被关闭后使用
            if(mZipListener!=null){
                mZipListener.onSuccess("压缩完成");
            }

        }catch (Exception e){
            e.printStackTrace();
            if(mZipListener!=null){
                mZipListener.onError(e.getMessage());
            }
        }

    }

    /**
     * 压缩文件
     * @param folderPath
     * @param filePath
     * @param zipOut
     * @throws Exception
     */
    private static void zipFiles(String folderPath, String filePath,
                                 ZipOutputStream zipOut)throws Exception{
        if(zipOut == null){
            return;
        }

        File file = new File(folderPath+filePath);

        //判断是不是文件
        if (file.isFile()) {
            ZipEntry zipEntry =  new ZipEntry(filePath);
            java.io.FileInputStream inputStream = new java.io.FileInputStream(file);
            zipOut.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];

            while((len=inputStream.read(buffer)) != -1) {
                zipOut.write(buffer, 0, len);
            }

            zipOut.closeEntry();
        } else {
            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                ZipEntry zipEntry =
                        new ZipEntry(filePath+ File.separator);
                zipOut.putNextEntry(zipEntry);
                zipOut.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                zipFiles(folderPath, filePath+ File.separator+fileList[i], zipOut);
            }

        }

    }
}
