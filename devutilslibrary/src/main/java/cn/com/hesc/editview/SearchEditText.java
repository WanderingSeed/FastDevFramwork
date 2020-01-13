package cn.com.hesc.editview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.tools.DensityUtils;

/**
 * ProjectName: FastDev-master
 * ClassName: SearchEditText
 * Description: 搜索框组件,
 * Author: liujunlin
 * Date: 2016-09-10 10:33
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class SearchEditText extends EditText{

    private OnSearchListener mOnSearchListener;
    private Drawable searchdrawable;

    public interface OnSearchListener{
        void onSearch();
    }

    public SearchEditText(Context context) {
        super(context);
        initEditText();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText();
    }

    /**
     * 监听键盘的搜索键，进行回调
     * @param onSearchListener
     */
    public void setOnSearchListener(OnSearchListener onSearchListener) {
        mOnSearchListener = onSearchListener;
    }

    private void initEditText() {
        setSingleLine(true);
        searchdrawable = getResources().getDrawable(R.drawable.search_icon);
        searchdrawable.setBounds(0, 0, searchdrawable.getIntrinsicWidth(),
                searchdrawable.getIntrinsicHeight());
        setCompoundDrawables(searchdrawable,null,null,null);
        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ){
                    if(mOnSearchListener != null){
                        mOnSearchListener.onSearch();
                    }
                }

                return false;
            }
        });

        setBackgroundResource(R.drawable.search_edit_bg);
        setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.searchedit_text_size));
        setCompoundDrawablePadding(DensityUtils.dp2px(getContext(),getResources().getDimension(R.dimen.searchedit_drawable_padding)));

    }



}
