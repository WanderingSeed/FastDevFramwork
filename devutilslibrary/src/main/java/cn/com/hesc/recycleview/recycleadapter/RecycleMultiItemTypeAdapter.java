package cn.com.hesc.recycleview.recycleadapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.recycleview.recycleadapter.itemview.ItemViewDelegate;
import cn.com.hesc.recycleview.recycleadapter.itemview.ItemViewDelegateManager;
import cn.com.hesc.recycleview.recycleadapter.itemview.ViewHolder;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * ProjectName: FastDev-master
 * ClassName: RecycleMultiItemTypeAdapter
 * Description: TODO
 * Author: liujunlin
 * Date: 2017-03-01 10:21
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class RecycleMultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;

    //设置加载更多
    private boolean isMoreData = false;
    private final int colunmMore = Integer.MAX_VALUE;//标示为有更多项
    private final int colunmNormal = -1;

    protected ItemViewDelegateManager mItemViewDelegateManager;
    protected OnItemClickListener mOnItemClickListener;
    protected OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mRecyclerView;
    private int lastVisibleItemIndex;
    private boolean isLoadAccessed = false;
    private int firstPositionVisiable = -1,lastPositionVisiable = -1;


    /**
     *
     * @param context 上下文
     * @param datas 数据集
     * @param recyclerView //需要绑定数据的recyclerView
     * @param isPaging // 是否为分页，默认是分页
     */
    public RecycleMultiItemTypeAdapter(Context context, List<T> datas,RecyclerView recyclerView,boolean isPaging) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
        isMoreData = isPaging;
        mRecyclerView = recyclerView;

        if(isMoreData){
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                    if(newState == SCROLL_STATE_IDLE){
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)mRecyclerView.getLayoutManager();
                        lastVisibleItemIndex = linearLayoutManager.findLastVisibleItemPosition();
                        if(isMoreData && lastVisibleItemIndex == getItemCount()-1 ){
                            if(mOnLoadMoreListener != null)
                                mOnLoadMoreListener.onLoadMoreListener(lastVisibleItemIndex);
                        }
                    }
                    isLoadAccessed = (newState == SCROLL_STATE_IDLE);

                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    //判断是当前layoutManager是否为LinearLayoutManager
                    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见view的位置
                        lastPositionVisiable = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见view的位置
                        firstPositionVisiable = linearManager.findFirstVisibleItemPosition();
                    }
                }

            });
        }
    }

    /**
     * 是否要展示列表底部的加载更多视图
     * @param isShow
     */
    public void showFootView(boolean isShow){
        if(isShow){
            isMoreData = true;
        }else{
            isMoreData = false;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(isMoreData){
            if(position == getItemCount() - 1){
                return colunmMore;
            }else{
                if (!useItemViewDelegateManager())
                    return super.getItemViewType(position);
                return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
            }
        }else{
            if (!useItemViewDelegateManager())
                return super.getItemViewType(position);
            return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
        }

//        if (!useItemViewDelegateManager())
//            return super.getItemViewType(position);
//        return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(isMoreData){
            if(viewType == colunmMore){
                ViewHolder footView = ViewHolder.createViewHolder(mContext,parent, R.layout.list_footview);
                return footView;
            }else{
                ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
                int layoutId = itemViewDelegate.getItemViewLayoutId();
                ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
                onViewHolderCreated(holder,holder.getConvertView());
                setListener(parent, holder, viewType);
                return holder;
            }
        }else{
            ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
            int layoutId = itemViewDelegate.getItemViewLayoutId();
            ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
            onViewHolderCreated(holder,holder.getConvertView());
            setListener(parent, holder, viewType);
            return holder;
        }

//        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
//        int layoutId = itemViewDelegate.getItemViewLayoutId();
//        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
//        onViewHolderCreated(holder,holder.getConvertView());
//        setListener(parent, holder, viewType);
//        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder,View itemView){

    }

    private boolean vetifyPosition(int position){
        return  (position < firstPositionVisiable || position > lastPositionVisiable);
    }

    public void convert(ViewHolder holder, T t) {

        /*
        if (isLoadAccessed && !vetifyPosition(holder.getAdapterPosition()))*/
            mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder , position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        if(holder instanceof ViewHolder){
            if(position < mDatas.size()) {
                convert(holder, mDatas.get(position));
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null && position < getItemCount())
                            mOnItemClickListener.onItemClick(v,holder, position);
                    }
                });
                holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemClickListener != null && position < getItemCount()) {
                            mOnItemClickListener.onItemLongClick(v, holder,position);
                        }
                        return true;
                    }
                });
            }
        }
//        convert(holder, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = mDatas.size();
        return /*itemCount*/isMoreData?mDatas.size()+1:mDatas.size();
    }


    public List<T> getDatas() {
        return mDatas;
    }

    public RecycleMultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public RecycleMultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnLoadMoreListener{
        void onLoadMoreListener(final int position);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void addItemData(int index,List<T> datas){
        if(datas!=null && datas.size()>0){
            mDatas.addAll(index, (Collection<? extends T>) datas);
            notifyItemRangeInserted(index,datas.size());
        }
    }

    public void deleteItemData(int index,T data){
        if(index != -1 && data !=null){
            mDatas.remove(data);
            notifyItemRemoved(index);
        }
    }
}
