package cn.com.hesc.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * ProjectName: FastDev-master
 * ClassName: ToastUtils
 * Description: Toast简写方式
 * Author: liujunlin
 * Date: 2017-08-31 15:44
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class ToastUtils {

    public static void showShort(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
