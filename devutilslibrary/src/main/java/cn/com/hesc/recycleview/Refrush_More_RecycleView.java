package cn.com.hesc.recycleview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.recycleview.recycleadapter.RecycleCommonAdapter;
import cn.com.hesc.recycleview.recycleadapter.RecycleMultiItemTypeAdapter;
import cn.com.hesc.recycleview.recycleadapter.itemview.RecycleViewDivider;

/**
 * ProjectName: FastDev-master
 * ClassName: Refrush_More_RecycleView
 * Description: 支持下拉、上拉的RecycleView，目前只支持线性布局列表
 * Author: liujunlin
 * Date: 2017-01-06 09:42
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class Refrush_More_RecycleView extends LinearLayout{

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecycleCommonAdapter mRecycleCommonAdapter;
    private RecycleMultiItemTypeAdapter mRecycleMultiItemTypeAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EventListener mRefrushListener;
    private boolean isLoading = false;
    private ListState mListState = ListState.DEFAULTE;
    private boolean isBotton = false;
    private boolean isTop = false;
    private ValueAnimator valueAnimator;

    public enum ListState{
        DEFAULTE,
        REFRUSH,
        MORE
    }

    public ListState getListState() {
        return mListState;
    }

    public void setListState(ListState listState) {
        mListState = listState;
    }

    /**监听回调，下拉刷新、上拉加载更多、点击列表项、长按列表项*/
    public interface EventListener{
        void onRefrushListener();
        void onMoreListener(int lastPosition);
        void onItemClick(View v, int position);
        void onItemLongClick(View v, int position);
    }


    public Refrush_More_RecycleView(Context context) {
        super(context);
        mContext = context;
    }

    public Refrush_More_RecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**获取RecyclerView，系统控件，可以方便进行属性定义*/
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public RecycleCommonAdapter getRecycleCommonAdapter() {
        return mRecycleCommonAdapter;
    }

    /**万能RecyclerView的数据适配器*/
    public void setRecycleCommonAdapter(RecycleCommonAdapter recycleCommonAdapter) {
        mRecycleCommonAdapter = recycleCommonAdapter;
        mRecyclerView.setAdapter(mRecycleCommonAdapter);
    }

    public RecycleMultiItemTypeAdapter getRecycleMultiItemTypeAdapter() {
        return mRecycleMultiItemTypeAdapter;
    }

    public void setRecycleMultiItemTypeAdapter(RecycleMultiItemTypeAdapter recycleMultiItemTypeAdapter) {
        mRecycleMultiItemTypeAdapter = recycleMultiItemTypeAdapter;
        mRecyclerView.setAdapter(mRecycleMultiItemTypeAdapter);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    /**系统提供的下拉刷新组件*/
    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    public EventListener getRefrushListener() {
        return mRefrushListener;
    }

    public void setEventListener(EventListener refrushListener) {
        mRefrushListener = refrushListener;
        if(mRecycleCommonAdapter!=null){
            mRecycleCommonAdapter.setOnLoadMoreListener(new RecycleCommonAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMoreListener(final int position) {
                    if(mRefrushListener!=null) {
                        mListState = ListState.MORE;
                        mRefrushListener.onMoreListener(position);
                    }
                }

            });
            mRecycleCommonAdapter.setOnItemClickListener(new RecycleCommonAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(mRefrushListener!=null){
                        mRefrushListener.onItemClick(view, position);
                    }
                }

                @Override
                public boolean onItemLongClick(View view, int position) {
                    if(mRefrushListener!=null){
                        mRefrushListener.onItemLongClick(view,position);
                    }
                    return false;
                }
            });
        }

        if(mRecycleMultiItemTypeAdapter!=null){
            mRecycleMultiItemTypeAdapter.setOnLoadMoreListener(new RecycleMultiItemTypeAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMoreListener(final int position) {
                    if(mRefrushListener!=null) {
                        mListState = ListState.MORE;
                        mRefrushListener.onMoreListener(position);
                    }
                }

            });
            mRecycleMultiItemTypeAdapter.setOnItemClickListener(new RecycleMultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    if(mRefrushListener!=null){
                        mRefrushListener.onItemClick(view,position);
                    }
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    if(mRefrushListener!=null){
                        mRefrushListener.onItemLongClick(view,position);
                    }
                    return false;
                }
            });
        }

        if(mSwipeRefreshLayout!=null){
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if(mRefrushListener!=null){
                        mListState = ListState.REFRUSH;
                        mRefrushListener.onRefrushListener();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    /***
     *
     * @param orientation 0 横向|1 纵向
     * @param divider_height  分隔线的高度或者宽度，可以是px或者从dimen里读取
     * @param  dividerColor 分隔线颜色值，可以从color里读或者drawable里读取文件
     */
    public void initView(int orientation,int divider_height,int dividerColor) {
        View parent = LayoutInflater.from(mContext).inflate(R.layout.recycle_view,null);
        mSwipeRefreshLayout = (SwipeRefreshLayout)parent.findViewById(R.id.swipefresh);
        mRecyclerView = (RecyclerView)parent.findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(orientation);
        //new GridLayoutManager(mContext,4) 可设置为网格布局
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, orientation == OrientationHelper.VERTICAL?LinearLayoutManager.VERTICAL:LinearLayoutManager.HORIZONTAL,
                divider_height,dividerColor));

        addView(parent);

    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if(!isLoading)
            mListState = ListState.DEFAULTE;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(isLoading){
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 滚动到指定位置，将该pos置顶
     * @param pos
     */
    public void scrollToPosition(int pos){
        linearLayoutManager.scrollToPositionWithOffset(pos,0);
        linearLayoutManager.setStackFromEnd(true);
    }

    /**
     * 判断是否已滑到底部
     * @param recyclerView
     */
   public boolean judgeIsBottom(RecyclerView recyclerView){
       View lastChild = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount()-1);
       int lastChildBottom = lastChild.getBottom();
       int recyclerViewBottom = recyclerView.getBottom()-recyclerView.getPaddingBottom();
       int lastChildIndex = recyclerView.getLayoutManager().getPosition(lastChild);
       if(lastChildBottom == recyclerViewBottom && lastChildIndex == recyclerView.getLayoutManager().getChildCount()-1){
            isBotton = true;
       }else{
           isBotton = false;
       }

       return isBotton;
    }

    /**
     * 判断是否已滑到顶部
     * @param recyclerView
     */
    public boolean judgeIsHead(RecyclerView recyclerView){
        View firstChild = recyclerView.getLayoutManager().getChildAt(0);
        int firstChildTop = firstChild.getTop();
        int recyclerViewTop = recyclerView.getTop()-recyclerView.getPaddingTop();
        int firstIndex = recyclerView.getLayoutManager().getPosition(firstChild);
        if(firstChildTop == Math.abs(recyclerViewTop) && firstIndex == 0){
            isTop = true;
        }else{
            isTop = false;
        }

        return isTop;
    }
}
