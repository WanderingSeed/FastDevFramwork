package cn.com.hesc.fastdevframwork.timepickdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.hesc.fastdevframwork.R;

public class TimePickActivity extends AppCompatActivity implements OnDateSetListener {

    TimePickerDialog mDialogAll;//年月日时分
    TimePickerDialog mDialogYearMonth;//年月
    TimePickerDialog mDialogYearMonthDay;//年月日
    TimePickerDialog mDialogMonthDayHourMinute;//月日时分
    TimePickerDialog mDialogHourMinute;//时分
    TimePickerDialog mDialogYear;//年份

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pick);
    }

    public void ymdhm(View view){
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("时间选择")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setSelectedDate(longToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm"))
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
//                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();
        mDialogAll.show(getSupportFragmentManager(),"all");
    }

    public void ym(View view){
        mDialogYearMonth = new TimePickerDialog.Builder()
                .setType(Type.YEAR_MONTH)
                .setTitleStringId("时间选择")
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setCurrentMillseconds(System.currentTimeMillis())
                .setSelectedDate(longToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm"))
                .setCallBack(this)
                .build();
        mDialogYearMonth.show(getSupportFragmentManager(),"YEAR_MONTH");
    }

    public void ymd(View view){
        mDialogYearMonthDay = new TimePickerDialog.Builder()
                .setType(Type.YEAR_MONTH_DAY)
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setCallBack(this)
                .setTitleStringId("时间选择")
                .setSelectedDate(longToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm"))
                .build();
        mDialogYearMonthDay.show(getSupportFragmentManager(),"YEAR_MONTH_DAY");
    }

    public void mdhm(View view){
        mDialogMonthDayHourMinute = new TimePickerDialog.Builder()
                .setType(Type.MONTH_DAY_HOUR_MIN)
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setCallBack(this)
                .setTitleStringId("时间选择")
                .setSelectedDate(longToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm"))
                .build();
        mDialogMonthDayHourMinute.show(getSupportFragmentManager(),"MONTH_DAY_HOUR_MIN");

    }

    public void hm(View view){
        mDialogHourMinute = new TimePickerDialog.Builder()
                .setType(Type.HOURS_MINS)
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setTitleStringId("时间选择")
                .setCallBack(this)
                .setSelectedDate(longToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm"))
                .build();
        mDialogHourMinute.show(getSupportFragmentManager(),"HOURS_MINS");
    }

    public void year(View view){
        mDialogYear = new TimePickerDialog.Builder()
                .setType(Type.YEAR)
                .setTitleStringId("时间选择")
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setCallBack(this)
                .setSelectedDate(longToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm"))
                .build();
        mDialogYear.show(getSupportFragmentManager(),"YEAR");
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

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        Toast.makeText(TimePickActivity.this,"选择的时间为:"+longToString(millseconds,null),Toast.LENGTH_SHORT).show();
    }
}
