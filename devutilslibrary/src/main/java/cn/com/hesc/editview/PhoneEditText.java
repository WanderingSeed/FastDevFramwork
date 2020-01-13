package cn.com.hesc.editview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.hesc.devutilslibrary.R;

/**
 * ProjectName: FastDev-master
 * ClassName: PhoneTextView
 * Description: 编写电话号码的输入框
 * Author: liujunlin
 * Date: 2017-02-07 08:53
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class PhoneEditText extends MMClearEditText{

    public PhoneEditText(Context context) {
        super(context);
        initView();
    }

    public PhoneEditText(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setAttrs(attributeset);
        initEditText();
        initView();
    }

    public PhoneEditText(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        setAttrs(attributeset);
        initEditText();
        initView();
    }

    private void initView(){
        setInputType(InputType.TYPE_CLASS_PHONE);
    }

    private void setAttrs(AttributeSet paramAttributeSet) {
        TypedArray localTypedArray = getContext().obtainStyledAttributes(paramAttributeSet,
                R.styleable.MMClearEditText);
        if (localTypedArray != null) {
            int style = localTypedArray.getInt(R.styleable.MMClearEditText_deleteButtonStyle,
                    DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_GRAY.ordinal());
            this.deleteButtonStyle = intToDeleteButtonStyle(style);
            this.limitLength = localTypedArray.getInt(R.styleable.MMClearEditText_limitLength,
                    Integer.MAX_VALUE);
            localTypedArray.recycle();
        }
    }

    /**
     * 手机号验证
     * 通过正则验证格式，不验证号码真伪
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
    /**
     * 电话号码验证
     * 电话号码格式：1.加区号 xxxx-xxxxx  2.不加区号 xxxxxxxx
     * 不考虑分机的情况，分机由总机接入
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null,p2 = null,p3,p4,p5;
        Matcher m = null,m1;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}[0-9]{6,10}$");  // 验证带区号
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if(str.length() <= 8)
        {   m = p2.matcher(str);
            b = m.matches();
        }else{
            m = p1.matcher(str);
            b = m.matches();
        }
        return b;
    }


}
