package cn.com.hesc.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.hesc.devutilslibrary.R;


/**
 * ProjectName: FastDev-master
 * ClassName: TitleActivity
 * Description: 自定义带标题栏的activity
 * Author: liujunlin
 * Date: 2016-09-09 10:37
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class TitleActivity extends AppCompatActivity {
    protected TextView mTitleTextView;
    private Button mBackwardbButton;
    private Button mForwardButton;
    private FrameLayout mContentLayout;
    private TitleOnClickListener mTitleOnClick;
    private LinearLayout titlebarline;

    /**
     * 监听标题栏，左右键的点击事件
     */
    protected interface TitleOnClickListener{
        void onLeft();
        void onRight();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        super.setContentView(R.layout.activity_title);
        mTitleTextView = (TextView) findViewById(R.id.titletv);
        mContentLayout = (FrameLayout) findViewById(R.id.title_content);
        mBackwardbButton = (Button) findViewById(R.id.leftbtn);
        mBackwardbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTitleOnClick!=null)
                    mTitleOnClick.onLeft();
            }
        });
        mForwardButton = (Button) findViewById(R.id.rightbtn);
        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTitleOnClick!=null)
                    mTitleOnClick.onRight();
            }
        });
        titlebarline = (LinearLayout) findViewById(R.id.titlebarline);
    }

    /**
     * 是否显示返回按钮
     * @param backwardResid  文字
     * @param show  true则显示
     */
    protected void showBackwardView(int backwardResid, boolean show) {
        if (mBackwardbButton != null) {
            if (show) {
                mBackwardbButton.setText(backwardResid);
                mBackwardbButton.setVisibility(View.VISIBLE);
            } else {
                mBackwardbButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    /**
     * 是否显示返回按钮
     * @param backwardStr  文字
     * @param show  true则显示
     */
    protected void showBackwardView(CharSequence backwardStr, boolean show) {
        if (mBackwardbButton != null) {
            if (show) {
                mBackwardbButton.setText(backwardStr);
                mBackwardbButton.setVisibility(View.VISIBLE);
            } else {
                mBackwardbButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    /**
     * 提供是否显示提交按钮
     * @param forwardResId  文字
     * @param show  true则显示
     */
    protected void showForwardView(int forwardResId, boolean show) {
        if (mForwardButton != null) {
            if (show) {
                mForwardButton.setVisibility(View.VISIBLE);
                mForwardButton.setText(forwardResId);
            } else {
                mForwardButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    /**
     * 提供是否显示提交按钮
     * @param forwardStr  文字
     * @param show  true则显示
     */
    protected void showForwardView(CharSequence forwardStr, boolean show) {
        if (mForwardButton != null) {
            if (show) {
                mForwardButton.setVisibility(View.VISIBLE);
                mForwardButton.setText(forwardStr);
            } else {
                mForwardButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    //设置标题内容
    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(titleId);
    }

    //设置标题内容
    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    //设置标题文字颜色
    @Override
    public void setTitleColor(int textColor) {
        mTitleTextView.setTextColor(textColor);
    }

    public void setTitleBackGround(int color){
        titlebarline.setBackgroundColor(color);
    }

    //取出FrameLayout并调用父类removeAllViews()方法
    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View view = View.inflate(this, layoutResID, null);
        mContentLayout.addView(view);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
    }

    /**
     * 设置标题栏的左右按钮的监听
     * @param titleonclick
     */
    public void setTitleBtn(TitleOnClickListener titleonclick){
        mTitleOnClick = titleonclick;
    }

}
