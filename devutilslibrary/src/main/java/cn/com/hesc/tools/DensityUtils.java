/**
 * ProjectName: HescAndroidLibrary
 * PackageName: com.hesc.android.library.utils
 * Author: yanbilian
 * Date: 2015-12-23 16:01
 * Copyright: (C)HESC Co.,Ltd. 2015. All rights reserved.
 */
package cn.com.hesc.tools;

import android.content.Context;

/**
 * ClassName: DensityUtils
 * Description: 常用分辨率转换工具类
 */
public class DensityUtils {

    public static final int DENSITY_MEDIUM = 160;

    /**
     * dp转px
     * @param paramContext
     * @param paramFloat
     * @return
     */
    public static final int dp2px(Context paramContext, float paramFloat) {
        return (int) (0.5F + paramFloat * (paramContext.getResources().getDisplayMetrics().density));
    }

    /**
     * px转dp
     * @param paramContext
     * @param paramFloat
     * @return
     */
    public static final int px2dp(Context paramContext, float paramFloat) {
        return (int) (0.5F + paramFloat
            / paramContext.getResources().getDisplayMetrics().density);
    }

    /**
     * sp转PX
     * @param paramContext
     * @param paramFloat
     * @return
     */
    public static int sp2px(Context paramContext, float paramFloat) {
        int result = (int) (0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().scaledDensity);
        return result;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}  

