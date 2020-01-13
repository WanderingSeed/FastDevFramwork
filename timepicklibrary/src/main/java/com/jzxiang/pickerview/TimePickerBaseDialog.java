/**
 * ProjectName: TimePickerBaseDialog-master
 * PackageName: com.jzxiang.pickerview.wheel
 * Author: yanbilian
 * Date: 2016-11-15 09:07
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
package com.jzxiang.pickerview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.jzxiang.pickerview.config.PickerConfig;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.data.WheelCalendar;
import com.jzxiang.pickerview.listener.OnDateSetBaseListener;

import java.util.Calendar;

import cn.com.hesc.timepicklibrary.R;

/**
 * ClassName: TimePickerBaseDialog
 * Description: TODO
 * Date: 2016-11-15 09:07
 */
public class TimePickerBaseDialog extends Dialog implements View.OnClickListener{
    PickerConfig mPickerConfig;
    private TimeWheel mTimeWheel;
    private long mCurrentMillSeconds;
    private Context mContext;

    public TimePickerBaseDialog(Context context) {
        this(context, R.style.Dialog_NoTitle);
    }

    public TimePickerBaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    private static TimePickerBaseDialog newIntance(Context context, PickerConfig pickerConfig) {
        TimePickerBaseDialog TimePickerBaseDialog = new TimePickerBaseDialog(context);
        TimePickerBaseDialog.initialize(pickerConfig);
        return TimePickerBaseDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Activity activity = (Activity)mContext;
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(initView());

        int height = mContext.getResources().getDimensionPixelSize(R.dimen.picker_height);

        Window window = getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);//Here!
        window.setGravity(Gravity.BOTTOM);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        int height = getResources().getDimensionPixelSize(R.dimen.picker_height);
//
//        Window window = getDialog().getWindow();
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);//Here!
//        window.setGravity(Gravity.BOTTOM);
//    }

    private void initialize(PickerConfig pickerConfig) {
        mPickerConfig = pickerConfig;
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_NoTitle);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setContentView(initView());
//        return dialog;
//    }

    View initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.timepicker_layout, null);
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(this);
        TextView sure = (TextView) view.findViewById(R.id.tv_sure);
        sure.setOnClickListener(this);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        View toolbar = view.findViewById(R.id.toolbar);

        title.setText(mPickerConfig.mTitleString);
        cancel.setText(mPickerConfig.mCancelString);
        sure.setText(mPickerConfig.mSureString);
        toolbar.setBackgroundColor(mPickerConfig.mThemeColor);

        mTimeWheel = new TimeWheel(view, mPickerConfig);
        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_cancel) {
            dismiss();
        } else if (i == R.id.tv_sure) {
            sureClicked();
        }
    }

    /*
    * @desc This method returns the current milliseconds. If current milliseconds is not set,
    *       this will return the system milliseconds.
    * @param none
    * @return long - the current milliseconds.
    */
    public long getCurrentMillSeconds() {
        if (mCurrentMillSeconds == 0)
            return System.currentTimeMillis();

        return mCurrentMillSeconds;
    }

    /*
    * @desc This method is called when onClick method is invoked by sure button. A Calendar instance is created and
    *       initialized.
    * @param none
    * @return none
    */
    void sureClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(Calendar.YEAR, mTimeWheel.getCurrentYear());
        calendar.set(Calendar.MONTH, mTimeWheel.getCurrentMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, mTimeWheel.getCurrentDay());
        calendar.set(Calendar.HOUR_OF_DAY, mTimeWheel.getCurrentHour());
        calendar.set(Calendar.MINUTE, mTimeWheel.getCurrentMinute());

        mCurrentMillSeconds = calendar.getTimeInMillis();
        if (mPickerConfig.mBaseCallBack != null) {
            mPickerConfig.mBaseCallBack.onDateSet(this, mCurrentMillSeconds);
        }
        dismiss();
    }

    public static class Builder {
        PickerConfig mPickerConfig;
        Context mContext;

        public Builder(Context context) {
            mPickerConfig = new PickerConfig();
            mContext = context;
        }

        public TimePickerBaseDialog.Builder setType(Type type) {
            mPickerConfig.mType = type;
            return this;
        }

        public TimePickerBaseDialog.Builder setThemeColor(int color) {
            mPickerConfig.mThemeColor = color;
            return this;
        }

        public TimePickerBaseDialog.Builder setCancelStringId(String left) {
            mPickerConfig.mCancelString = left;
            return this;
        }

        public TimePickerBaseDialog.Builder setSureStringId(String right) {
            mPickerConfig.mSureString = right;
            return this;
        }

        public TimePickerBaseDialog.Builder setTitleStringId(String title) {
            mPickerConfig.mTitleString = title;
            return this;
        }

        public TimePickerBaseDialog.Builder setToolBarTextColor(int color) {
            mPickerConfig.mToolBarTVColor = color;
            return this;
        }

        public TimePickerBaseDialog.Builder setWheelItemTextNormalColor(int color) {
            mPickerConfig.mWheelTVNormalColor = color;
            return this;
        }

        public TimePickerBaseDialog.Builder setWheelItemTextSelectorColor(int color) {
            mPickerConfig.mWheelTVSelectorColor = color;
            return this;
        }

        public TimePickerBaseDialog.Builder setWheelItemTextSize(int size) {
            mPickerConfig.mWheelTVSize = size;
            return this;
        }

        public TimePickerBaseDialog.Builder setCyclic(boolean cyclic) {
            mPickerConfig.cyclic = cyclic;
            return this;
        }

        public TimePickerBaseDialog.Builder setMinMillseconds(long millseconds) {
            mPickerConfig.mMinCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public TimePickerBaseDialog.Builder setMaxMillseconds(long millseconds) {
            mPickerConfig.mMaxCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public TimePickerBaseDialog.Builder setCurrentMillseconds(long millseconds) {
            mPickerConfig.mCurrentCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public TimePickerBaseDialog.Builder setYearText(String year){
            mPickerConfig.mYear = year;
            return this;
        }

        public TimePickerBaseDialog.Builder setMonthText(String month){
            mPickerConfig.mMonth = month;
            return this;
        }

        public TimePickerBaseDialog.Builder setDayText(String day){
            mPickerConfig.mDay = day;
            return this;
        }

        public TimePickerBaseDialog.Builder setHourText(String hour){
            mPickerConfig.mHour = hour;
            return this;
        }

        public TimePickerBaseDialog.Builder setMinuteText(String minute){
            mPickerConfig.mMinute = minute;
            return this;
        }

        public TimePickerBaseDialog.Builder setBaseCallBack(OnDateSetBaseListener listener) {
            mPickerConfig.mBaseCallBack = listener;
            return this;
        }

        public TimePickerBaseDialog.Builder setSelectedDate(String selectedDate){
            mPickerConfig.mSelectedDate = selectedDate;
            return this;
        }

        public TimePickerBaseDialog build() {
            return newIntance(mContext,mPickerConfig);
        }

    }
}
