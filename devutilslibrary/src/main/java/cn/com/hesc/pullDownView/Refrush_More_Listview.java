package cn.com.hesc.pullDownView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.sql.Date;

import cn.com.hesc.devutilslibrary.R;

/**
 * ProjectName: FastDev-master
 * ClassName: Refrush_More_Listview
 * Description: 将下拉刷新和上拉加载更多放到viewgroup里去，提供接口方法供调用
 * Author: liujunlin
 * Date: 2016-11-03 17:00
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class Refrush_More_Listview extends LinearLayout implements PullDownView.onPullDownViewUpdateListener{

    private Context mContext;
    protected ListView mListView;
    protected PullDownView pullDownView;
    protected LinearLayout footerView;
    private boolean isscrollbottom = false;// 是否已滑到底部
    private int visibleLastIndex = -1;
    protected boolean isDataEnd = true;//是否已没有分页数据
    protected boolean isloading = false;//是否正在获取数据
    private RefrushListener mRefrushListener;
    private PULLDOWN_STATE mPulldownState = PULLDOWN_STATE.DEFAULT;

    /**
     * 定义下拉刷新状态
     * DEFAULT:默认状态，即没有进行手势操作时的状态
     * UPDATE:下拉状态，即用户进行了下拉手势操作
     * LOADMORE:加载更多状态，即用户进行了上拉手势操作
     */
    public enum PULLDOWN_STATE{
        DEFAULT,UPDATE,LOADMORE
    }

    public PULLDOWN_STATE getPulldownState() {
        return mPulldownState;
    }

    public void setPulldownState(PULLDOWN_STATE mPulldownState) {
        this.mPulldownState = mPulldownState;
    }

    public interface RefrushListener{
        void onRefrushListener();
        void onMoreListener();
    }

    public Refrush_More_Listview(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public Refrush_More_Listview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public void setRefrushListener(RefrushListener refrushListener) {
        mRefrushListener = refrushListener;
    }

    public ListView getListView() {
        return mListView;
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }

    private void initView(){

        LayoutParams llp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        setLayoutParams(llp);

        View parent = LayoutInflater.from(mContext).inflate(R.layout.activity_refresh_and_more,null);
        this.pullDownView = (PullDownView) parent.findViewById(R.id.consult_pullDownView);
        this.mListView = (ListView) parent.findViewById(R.id.listcontent);
        this.pullDownView.setUpdateHandle(this);// 设置回调
        this.pullDownView.setUpdateDate(new Date(System.currentTimeMillis()));
        LayoutInflater lInflater = LayoutInflater.from(mContext);
        View view = lInflater.inflate(R.layout.footview, null);
        footerView = (LinearLayout) view.findViewById(R.id.list_footview);
        mListView.addFooterView(view);
        footerView.setVisibility(View.INVISIBLE);
        mListView.setFooterDividersEnabled(false);
        mListView.setOnScrollListener(mOnScrollListener);
        addView(parent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(isloading){
                return true;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onPullDownViewUpdate() {
        isloading = true;
        if(mRefrushListener!=null) {
            mPulldownState = PULLDOWN_STATE.UPDATE;
            mRefrushListener.onRefrushListener();
        }
    }

    /**
     * 下拉完成界面刷新，重置下拉状态
     */
    public void resetPullDownViewState() {
        isloading = false;
        if (this.pullDownView != null) {
            mPulldownState = PULLDOWN_STATE.DEFAULT;
            Date date = new Date(System.currentTimeMillis());
            this.pullDownView.didActionFinished(date);
        }


    }

    public void showFootView(boolean isAttachFootView){
        footerView.setVisibility(isAttachFootView?View.VISIBLE:View.GONE);
    }

    /**
     * 当分页数据取尽时，去除掉listview的footview
     */
    public void deleteFootView(){
        mListView.removeFooterView(footerView);
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(isscrollbottom && scrollState == SCROLL_STATE_IDLE){
                if(!isDataEnd && !isloading){
                    footerView.setVisibility(View.VISIBLE);
                    isloading = true;
                    if(mRefrushListener!=null) {
                        mPulldownState = PULLDOWN_STATE.LOADMORE;
                        mRefrushListener.onMoreListener();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            if (visibleLastIndex == (totalItemCount - 1)
                    && totalItemCount > 0) {
                isscrollbottom = true;// 已滑到底部
            } else {
                isscrollbottom = false;
            }
        }
    };

    public boolean isDataEnd() {
        return isDataEnd;
    }

    public void setDataEnd(boolean dataEnd) {
        isDataEnd = dataEnd;
    }

    public boolean isloading() {
        return isloading;
    }

    public void setIsloading(boolean isloading) {
        this.isloading = isloading;
    }
}
