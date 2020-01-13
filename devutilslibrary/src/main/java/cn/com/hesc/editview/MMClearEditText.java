package cn.com.hesc.editview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.com.hesc.devutilslibrary.R;


/**
 * ProjectName: FastDevDemo
 * ClassName: MMClearEditText
 * Description: 带删除功能的输入框
 * Author: liujunlin
 * Date: 2016-09-08 15:43
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MMClearEditText extends AppCompatEditText {
    public enum DELETEBUTTONSTYLE {
        DELETEBUTTONSTYLE_GRAY,DELETEBUTTONSTYLE_WHITE;
    }

    private Drawable clearIconDrawable = null;
    private Drawable searchIconDrawable = null;
    protected int limitLength = -1;
    protected DELETEBUTTONSTYLE deleteButtonStyle = DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_GRAY;
    private EditTextLimitLenghtListener onEditTextLimitLenghtListener = null;
    private EditTextClearListener onEditTextClearListener = null;
    private EditTextChangeListener onEditTextChangeListener = null;

    public MMClearEditText(Context context) {
        super(context);
        initEditText();
    }

    public MMClearEditText(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setAttrs(attributeset);
        initEditText();
    }

    public MMClearEditText(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        setAttrs(attributeset);
        initEditText();
    }

    static void clearClearIconDrawable(MMClearEditText mmclearedittext) {
        mmclearedittext.clearClearIconDrawable();
    }

    public static DELETEBUTTONSTYLE intToDeleteButtonStyle(int style){
        if (style == DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_GRAY.ordinal()) {
            return DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_GRAY;
        } else if (style == DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_WHITE.ordinal()) {
            return DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_WHITE;
        }
        return DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_GRAY;
    }

    public static int DeleteButtonStyleToInt(DELETEBUTTONSTYLE style){
        return style.ordinal();
    }

    @SuppressWarnings("deprecation")
    protected void initEditText() {
        initDeleteButtonStyle();
        searchIconDrawable = getResources().getDrawable(R.drawable.search_icon);
        setViewLisener();
    }

    @SuppressWarnings("deprecation")
    private void initDeleteButtonStyle(){
        if (deleteButtonStyle == DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_WHITE) {
            clearIconDrawable = getResources().getDrawable(R.drawable.clear_btn_white);
        } else if (deleteButtonStyle == DELETEBUTTONSTYLE.DELETEBUTTONSTYLE_GRAY) {
            clearIconDrawable = getResources().getDrawable(R.drawable.clear_btn);
        }
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

    private void setClearIconDrawable() {
        clearIconDrawable.setBounds(0, 0, clearIconDrawable.getIntrinsicWidth(),
                clearIconDrawable.getIntrinsicHeight());
        if (this.getText().toString().equals("") || !isFocused())
            clearClearIconDrawable();
        else
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                    clearIconDrawable, getCompoundDrawables()[3]);
    }

    private void clearClearIconDrawable() {
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null,
                getCompoundDrawables()[3]);
    }

    public static void setClearIconDrawable(MMClearEditText mmclearedittext) {
        mmclearedittext.setClearIconDrawable();
    }

    public void setSearchIcon() {
        setCompoundDrawables(searchIconDrawable, getCompoundDrawables()[1],
                getCompoundDrawables()[2], getCompoundDrawables()[3]);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViewLisener() {
        searchIconDrawable.setBounds(0, 0, searchIconDrawable.getIntrinsicWidth(),
                searchIconDrawable.getIntrinsicHeight());
        setClearIconDrawable();
        // setMinHeight(clearIconDrawable.getIntrinsicHeight() + 5
        // * getResources().getDimensionPixelSize(Res.dimen.OneDPPadding));
        setOnTouchListener(onTouchListener);
        addTextChangedListener(textWatcher);
        setOnFocusChangeListener(onFocusChangeListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getCompoundDrawables()[2] != null
                    && event.getAction() == 1
                    && event.getX() > (float) (getWidth() - getPaddingRight() - clearIconDrawable
                    .getIntrinsicWidth())) {
                if(onEditTextClearListener!=null)
                    onEditTextClearListener.onEditTextClearListener();
                setText("");
                clearClearIconDrawable(MMClearEditText.this);
            }

            return false;
        }
    };

    /**
     * @return the onEditTextLimitLenghtListener
     */
    public EditTextLimitLenghtListener getOnEditTextLimitLenghtListener() {
        return onEditTextLimitLenghtListener;
    }

    public EditTextChangeListener getOnEditTextChangeListener() {
        return onEditTextChangeListener;
    }

    public void setOnEditTextChangeListener(EditTextChangeListener onEditTextChangeListener) {
        this.onEditTextChangeListener = onEditTextChangeListener;
    }

    /**
     * 设置输入字符的限制监听
     * @param onEditTextLimitLenghtListener
     *            the onEditTextLimitLenghtListener to set
     */
    public void setOnEditTextLimitLenghtListener(
            EditTextLimitLenghtListener onEditTextLimitLenghtListener) {
        this.onEditTextLimitLenghtListener = onEditTextLimitLenghtListener;
    }

    /**
     * 设置清空字符的监听
     * @param onEditTextClearListener
     */
    public void setOnEditTextClearListener(EditTextClearListener onEditTextClearListener) {
        this.onEditTextClearListener = onEditTextClearListener;
    }

    /**
     * 返回所限字符的长度
     * @return the limitLength
     */
    public int getLimitLength() {
        return limitLength;
    }

    /**
     * 设置所限长度
     * @param limitLength the limitLength to set
     */
    public void setLimitLength(int limitLength) {
        this.limitLength = limitLength == -1?Integer.MAX_VALUE:limitLength;
    }

    /**
     * @return the deleteButtonStyle
     */
    public DELETEBUTTONSTYLE getDeleteButtonStyle() {
        return deleteButtonStyle;
    }

    /**
     * @param deleteButtonStyle the deleteButtonStyle to set
     */
    public void setDeleteButtonStyle(DELETEBUTTONSTYLE deleteButtonStyle) {
        this.deleteButtonStyle = deleteButtonStyle;
        initDeleteButtonStyle();
        setClearIconDrawable();
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            setClearIconDrawable(MMClearEditText.this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setClearIconDrawable(MMClearEditText.this);
            mAfterTextChanged(s);
            if(onEditTextChangeListener!=null)
                onEditTextChangeListener.onEditTextChangeListener();
        }

    };

    private int selectionStart;
    private int selectionEnd;

    private void mAfterTextChanged(Editable s) {
        selectionStart = this.getSelectionStart();
        selectionEnd = this.getSelectionEnd();
        boolean isOverLimit = false;
        if (getRealLength(s.toString()) > limitLength && limitLength != -1) {
            s.delete(selectionStart - 1, selectionEnd);
            int tempSelection = selectionStart;
            this.setText(s);
            this.setSelection(tempSelection);
            isOverLimit = true;
        }
        if (this.onEditTextLimitLenghtListener != null)
            this.onEditTextLimitLenghtListener.onEditTextLimitLenghtListener(limitLength,
                    isOverLimit);
    }

    private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            setClearIconDrawable(MMClearEditText.this);
        }
    };

    public interface EditTextLimitLenghtListener {
        public void onEditTextLimitLenghtListener(int limitLength, boolean isOverLimit);
    }

    public interface EditTextClearListener {
        public void onEditTextClearListener();
    }

    public interface EditTextChangeListener {
        public void onEditTextChangeListener();
    }

    // 汉字占2个长度，其他1
    private static int getRealLength(String str) {
        int len = str.length();
        int reLen = 0;
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) < 27 || str.charAt(i) > 126) {
                // 全角
                reLen += 2;
            } else {
                reLen++;
            }
        }
        return reLen;
    }
}
