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
 * ClassName: EmailEditText
 * Description: 检测邮箱地址
 * Author: liujunlin
 * Date: 2017-02-07 11:28
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class EmailEditText extends MMClearEditText{
    public EmailEditText(Context context) {
        super(context);
    }

    public EmailEditText(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setAttrs(attributeset);
        initEditText();
        initView();
    }

    public EmailEditText(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        setAttrs(attributeset);
        initEditText();
        initView();
    }

    private void initView() {
        setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
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
    public static boolean isEmail(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?"); // 验证邮箱号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
}
