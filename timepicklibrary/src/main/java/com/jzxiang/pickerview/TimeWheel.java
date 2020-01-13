package com.jzxiang.pickerview;

import android.content.Context;
import android.view.View;

import com.jzxiang.pickerview.adapters.NumericWheelAdapter;
import com.jzxiang.pickerview.config.PickerConfig;
import com.jzxiang.pickerview.data.source.TimeRepository;
import com.jzxiang.pickerview.utils.PickerContants;
import com.jzxiang.pickerview.utils.Utils;
import com.jzxiang.pickerview.wheel.OnWheelChangedListener;
import com.jzxiang.pickerview.wheel.WheelView;

import java.util.Calendar;

import cn.com.hesc.timepicklibrary.R;

/**
 * Created by jzxiang on 16/4/20.
 */
public class TimeWheel {
    Context mContext;

    WheelView year, month, day, hour, minute;
    NumericWheelAdapter mYearAdapter, mMonthAdapter, mDayAdapter, mHourAdapter, mMinuteAdapter;

    PickerConfig mPickerConfig;
    TimeRepository mRepository;

    private String mDefaultDate;
    private int mDefaultYear;
    private int mDefaultMonth;
    private int mDefaultDay;
    private int mDefaultHour;
    private int mDefaultMinute;

    OnWheelChangedListener yearListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateMonths();
        }
    };
    OnWheelChangedListener monthListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateDays();
        }
    };
    OnWheelChangedListener dayListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateHours();
        }
    };
    OnWheelChangedListener minuteListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateMinutes();
        }
    };

    private void string2IntArrayYMD(String s) {
        if (s != null && !"".equals(s)) {
            String as[] = s.split(" ")[0].split("-");
            mDefaultYear = Integer.valueOf(as[0]).intValue();
            mDefaultMonth = Integer.valueOf(as[1]).intValue();
            mDefaultDay = Integer.valueOf(as[2]).intValue();
        }
    }

    private void string2IntArrayYMDHM(String s) {
        if (s != null && !"".equals(s)) {
            String as[];
            String as1[];
            as = s.split(" ");
            as1 = as[0].split("-");
            mDefaultYear = Integer.valueOf(as1[0]).intValue();
            mDefaultMonth = Integer.valueOf(as1[1]).intValue();
            mDefaultDay = Integer.valueOf(as1[2]).intValue();
            String as2[];
            as2 = as[1].split(":");
            mDefaultHour = Integer.valueOf(as2[0]).intValue();
            mDefaultMinute = Integer.valueOf(as2[1]).intValue();
        }
    }

    private void string2IntArray(String s){
        if (s != null && !"".equals(s)) {
            String as[];
            String as1[];
            as = s.split(" ");
            as1 = as[0].split("-");
            mDefaultYear = Integer.valueOf(as1[0]).intValue();
            mDefaultMonth = Integer.valueOf(as1[1]).intValue();
            mDefaultDay = Integer.valueOf(as1[2]).intValue();
            String as2[];
            as2 = as[1].split(":");
            mDefaultHour = Integer.valueOf(as2[0]).intValue();
            mDefaultMinute = Integer.valueOf(as2[1]).intValue();
        }
    }

    public TimeWheel(View view, PickerConfig pickerConfig) {
        mPickerConfig = pickerConfig;

        mDefaultDate = mPickerConfig.mSelectedDate;
//        if (mPickerConfig.mType.equals(Type.ALL)) {
//            string2IntArrayYMDHM(mDefaultDate);
//        } else if (mPickerConfig.mType.equals(Type.YEAR_MONTH_DAY)) {
//            string2IntArrayYMD(mDefaultDate);
//        }else{
//            string2IntArray(mDefaultDate);
//        }

        string2IntArray(mDefaultDate);
        mRepository = new TimeRepository(pickerConfig);
        mContext = view.getContext();
        initialize(view);
    }

    public void initialize(View view) {
        initView(view);
        initYear();
        initMonth();
        initDay();
        initHour();
        initMinute();
    }


    void initView(View view) {
        year = (WheelView) view.findViewById(R.id.year);
        month = (WheelView) view.findViewById(R.id.month);
        day = (WheelView) view.findViewById(R.id.day);
        hour = (WheelView) view.findViewById(R.id.hour);
        minute = (WheelView) view.findViewById(R.id.minute);

        switch (mPickerConfig.mType) {
            case ALL:

                break;
            case YEAR_MONTH_DAY:
                Utils.hideViews(hour, minute);
                break;
            case YEAR_MONTH:
                Utils.hideViews(day, hour, minute);
                break;
            case MONTH_DAY_HOUR_MIN:
                Utils.hideViews(year);
                break;
            case HOURS_MINS:
                Utils.hideViews(year, month, day);
                break;
            case YEAR:
                Utils.hideViews(month, day, hour, minute);
                break;
        }

        year.addChangingListener(yearListener);
        year.addChangingListener(monthListener);
        year.addChangingListener(dayListener);
        year.addChangingListener(minuteListener);
        month.addChangingListener(monthListener);
        month.addChangingListener(dayListener);
        month.addChangingListener(minuteListener);
        day.addChangingListener(dayListener);
        day.addChangingListener(minuteListener);
        hour.addChangingListener(minuteListener);
    }

    void initYear() {
        int minYear = mRepository.getMinYear();
        int maxYear = mRepository.getMaxYear();

        mYearAdapter = new NumericWheelAdapter(mContext, minYear, maxYear, PickerContants.FORMAT, mPickerConfig.mYear);
        mYearAdapter.setConfig(mPickerConfig);
        year.setViewAdapter(mYearAdapter);
        int currentYear = -1;
        if (mDefaultDate != null && !"".equals(mDefaultDate)) {
            currentYear = mDefaultYear;
        } else {
            currentYear = mRepository.getDefaultCalendar().year;
        }
        year.setCurrentItem(currentYear - minYear);
    }

    void initMonth() {
        updateMonths();
        int curYear = getCurrentYear();
        int minMonth = mRepository.getMinMonth(curYear);
        int currentMonth = -1;
        if (mDefaultDate != null && !"".equals(mDefaultDate)) {
            currentMonth = mDefaultMonth;
        } else {
            currentMonth = mRepository.getDefaultCalendar().month;
        }
        month.setCurrentItem(currentMonth - minMonth);
        month.setCyclic(mPickerConfig.cyclic);
    }

    void initDay() {
        updateDays();
        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();

        int minDay = mRepository.getMinDay(curYear, curMonth);
        int currentDay = 1;
        if (mDefaultDate != null && !"".equals(mDefaultDate)) {
            currentDay = mDefaultDay;
        } else {
            currentDay = mRepository.getDefaultCalendar().day;
        }
        day.setCurrentItem(currentDay - minDay);
        day.setCyclic(mPickerConfig.cyclic);
    }

    void initHour() {
        updateHours();
        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();
        int curDay = getCurrentDay();

        int minHour = mRepository.getMinHour(curYear, curMonth, curDay);
        int currentHour = -1;
        if (mDefaultDate != null && !"".equals(mDefaultDate)) {
            currentHour = mDefaultHour;
        } else {
            currentHour = mRepository.getDefaultCalendar().hour;
        }
        hour.setCurrentItem( currentHour - minHour);
        hour.setCyclic(mPickerConfig.cyclic);
    }

    void initMinute() {
        updateMinutes();
        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();
        int curDay = getCurrentDay();
        int curHour = getCurrentHour();
        int minMinute = mRepository.getMinMinute(curYear, curMonth, curDay, curHour);

        int currentMinute = -1;
        if (mDefaultDate != null && !"".equals(mDefaultDate)) {
            currentMinute = mDefaultMinute;
        } else {
            currentMinute = mRepository.getDefaultCalendar().minute;
        }
        minute.setCurrentItem(currentMinute - minMinute);
        minute.setCyclic(mPickerConfig.cyclic);

    }

    void updateMonths() {
        if (month.getVisibility() == View.GONE)
            return;

        int curYear = getCurrentYear();
        int minMonth = mRepository.getMinMonth(curYear);
        int maxMonth = mRepository.getMaxMonth(curYear);
        mMonthAdapter = new NumericWheelAdapter(mContext, minMonth, maxMonth, PickerContants.FORMAT, mPickerConfig.mMonth);
        mMonthAdapter.setConfig(mPickerConfig);
        month.setViewAdapter(mMonthAdapter);

        if (mRepository.isMinYear(curYear)) {
            month.setCurrentItem(0, false);
        }
    }

    void updateDays() {
        if (day.getVisibility() == View.GONE)
            return;

        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, curMonth);

        int maxDay = mRepository.getMaxDay(curYear, curMonth);
        int minDay = mRepository.getMinDay(curYear, curMonth);
        mDayAdapter = new NumericWheelAdapter(mContext, minDay, maxDay, PickerContants.FORMAT, mPickerConfig.mDay);
        mDayAdapter.setConfig(mPickerConfig);
        day.setViewAdapter(mDayAdapter);

        if (mRepository.isMinMonth(curYear, curMonth)) {
            day.setCurrentItem(0, true);
        }

        int dayCount = mDayAdapter.getItemsCount();
        if (day.getCurrentItem() >= dayCount) {
            day.setCurrentItem(dayCount - 1, true);
        }
    }

    void updateHours() {
        if (hour.getVisibility() == View.GONE)
            return;

        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();
        int curDay = getCurrentDay();

        int minHour = mRepository.getMinHour(curYear, curMonth, curDay);
        int maxHour = mRepository.getMaxHour(curYear, curMonth, curDay);

        mHourAdapter = new NumericWheelAdapter(mContext, minHour, maxHour, PickerContants.FORMAT, mPickerConfig.mHour);
        mHourAdapter.setConfig(mPickerConfig);
        hour.setViewAdapter(mHourAdapter);

        if (mRepository.isMinDay(curYear, curMonth, curDay))
            hour.setCurrentItem(0, false);
    }

    void updateMinutes() {
        if (minute.getVisibility() == View.GONE)
            return;

        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();
        int curDay = getCurrentDay();
        int curHour = getCurrentHour();

        int minMinute = mRepository.getMinMinute(curYear, curMonth, curDay, curHour);
        int maxMinute = mRepository.getMaxMinute(curYear, curMonth, curDay, curHour);

        mMinuteAdapter = new NumericWheelAdapter(mContext, minMinute, maxMinute, PickerContants.FORMAT, mPickerConfig.mMinute);
        mMinuteAdapter.setConfig(mPickerConfig);
        minute.setViewAdapter(mMinuteAdapter);

        if (mRepository.isMinHour(curYear, curMonth, curDay, curHour))
            minute.setCurrentItem(0, false);
    }

    public int getCurrentYear() {
        return year.getVisibility()==View.GONE?mDefaultYear:year.getCurrentItem() + mRepository.getMinYear();
    }

    public int getCurrentMonth() {
        int curYear = getCurrentYear();
        return month.getVisibility()==View.GONE?mDefaultMonth:month.getCurrentItem() + +mRepository.getMinMonth(curYear);
    }

    public int getCurrentDay() {
        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();
        return day.getVisibility()==View.GONE?mDefaultDay:day.getCurrentItem() + mRepository.getMinDay(curYear, curMonth);
    }

    public int getCurrentHour() {
        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();
        int curDay = getCurrentDay();
        return hour.getVisibility()==View.GONE?mDefaultHour:hour.getCurrentItem() + mRepository.getMinHour(curYear, curMonth, curDay);
    }

    public int getCurrentMinute() {
        int curYear = getCurrentYear();
        int curMonth = getCurrentMonth();
        int curDay = getCurrentDay();
        int curHour = getCurrentHour();

        return minute.getVisibility()==View.GONE?mDefaultMinute:minute.getCurrentItem() + mRepository.getMinMinute(curYear, curMonth, curDay, curHour);
    }


}
