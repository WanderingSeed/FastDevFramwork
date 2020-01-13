package cn.com.hesc.tools;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: TimeUtils
 * Description: long型时间和标准时间的转换和字符串的格式化
 * Author: liujunlin
 * Date: 2016-04-25 16:45
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class TimeUtils {
    /**
     * 获取格式化后的时间值
     * @param formatstr 格式化字符("yyyy-MM-dd HH:mm:ss","yyyy-MM-dd","yyyyMMddHHmmss")等
     * @return
     */
    public static String getSystime(String formatstr)
    {
        String systime;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(formatstr);
        Date date = c.getTime();
        systime = df.format(date);
        return systime;
    }

    /**
     * 转换为系统标准时间
     * @param longtime  long型时间值
     * @return  YYYY-MM-DD HH:MM:SS
     */
    public static String longToString(long longtime){
        return longToString(longtime,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将标准时间值转为long型毫秒数据
     * @param time 标准时间格式（如："2016-05-01 12:00:00"）,无时间请标注为（2016-05-01 00:00:00）
     * @return long型时间
     */
    public static long stringtoLong(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date;
        long millionSeconds = 0;
        try {
            date = sdf.parse(time);

            millionSeconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }// 毫秒

        return millionSeconds;
    }

    /**
     * 将long型时间转换为标准时间
     * @param longtime long型数值
     * @param format "yyyy-MM-dd" 等形式，null为标准的年月日、时分秒
     * @return
     */
    public static String longToString(long longtime,String format){
        Date date = new Date(longtime);
        SimpleDateFormat formatter = new SimpleDateFormat(TextUtils.isEmpty(format)?"yyyy-MM-dd HH:mm:ss":format);
        String dateString = formatter.format(date);
        return dateString;
    }
}
