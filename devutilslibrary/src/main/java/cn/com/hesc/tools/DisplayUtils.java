package cn.com.hesc.tools;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * ProjectName: FastDev-master
 * ClassName: DisplayUtils
 * Description: 获取屏幕相关属性
 * Author: liujunlin
 * Date: 2016-10-25 08:46
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class DisplayUtils {
    private int mWidth = 0;
    private int mHeight = 0;
    private Context mContext;

    public DisplayUtils(Context context){
        mContext = context;
        initData();
    }

    private void initData() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        mWidth = point.x;
        mHeight = point.y;
    }

    /**屏幕的高度*/
    public int getHeight() {
        return mHeight;
    }
    /**屏幕的宽度*/
    public int getWidth() {
        return mWidth;
    }
}
