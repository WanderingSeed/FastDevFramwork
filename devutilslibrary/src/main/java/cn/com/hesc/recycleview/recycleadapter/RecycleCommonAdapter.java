package cn.com.hesc.recycleview.recycleadapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import java.util.Collection;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.recycleview.recycleadapter.itemview.ViewHolder;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * ProjectName: FastDev-master
 * ClassName: RecycleCommonAdapter
 * Description: 针对recycle加入的万能适配，实现兼容各种view，加入刷新更多数据，加入itemview的click和longclick
 * Author: liujunlin
 * Date: 2016-09-18 16:03
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public abstract class RecycleCommonAdapter<T> extends RecyclerView.Adapter<ViewHolder>{

    protected Context mContext;
    protected List<T> mDatas;
    private boolean isMoreData = false;
    private final int colunmMore = 2;//标示为有更多项
    private final int colunmNormal = 0;
    protected int mLayoutId;

    protected OnItemClickListener mOnItemClickListener;
    protected OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mRecyclerView;
    private int lastVisibleItemIndex;
    private int firstVisibleItemIndex;

    /**
     * 万能适配器的构造函数
     * @param context 上下文
     * @param recyclerView 对应绑定的RecyclerView
     * @param datas 支持泛型的列表
     * @param isPaging  是否为分页，默认分页
     * @param layoutId 列表项的子布局
     */
    public RecycleCommonAdapter(Context context,RecyclerView recyclerView,List<T> datas,boolean isPaging,final int layoutId){
        this.mContext = context;
        this.mDatas = datas;
        isMoreData = isPaging;
        mLayoutId = layoutId;
        mRecyclerView = recyclerView;

        if(isMoreData){
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                    if(newState == SCROLL_STATE_IDLE){
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)mRecyclerView.getLayoutManager();
                        lastVisibleItemIndex = linearLayoutManager.findLastVisibleItemPosition();
                        firstVisibleItemIndex = linearLayoutManager.findFirstVisibleItemPosition();
                        if(isMoreData && lastVisibleItemIndex == getItemCount()-1 ){
                            if(mOnLoadMoreListener != null)
                                mOnLoadMoreListener.onLoadMoreListener(lastVisibleItemIndex);
                        }
                    }
                }

            });
        }
    }

    public void showFootView(boolean isShow){
        if(isShow){
            isMoreData = true;
        }else{
            isMoreData = false;
        }
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        if(isMoreData){
            if(position == getItemCount() - 1){
                return colunmMore;
            }else{
                return colunmNormal;
            }
        }else{
            return colunmNormal;
        }
    }

    //继承通用适配器时，实现数据展示
    public abstract void conver(ViewHolder viewHandler, T item, int position);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(isMoreData){
            if(viewType == colunmMore){
                ViewHolder footView = ViewHolder.createViewHolder(mContext,parent, R.layout.list_footview);
                return footView;
            }else{
                ViewHolder itemView = ViewHolder.createViewHolder(mContext,parent, mLayoutId);
                return itemView;
            }
        }else{
            ViewHolder itemView = ViewHolder.createViewHolder(mContext,parent, mLayoutId);
            return itemView;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(holder instanceof ViewHolder){
            //只刷新可见区域
            if(position < mDatas.size()  ) {
                conver(holder, mDatas.get(position), position);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null && position < getItemCount())
                            mOnItemClickListener.onItemClick(v, position);
                    }
                });
                holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemClickListener != null && position < getItemCount()) {
                            mOnItemClickListener.onItemLongClick(v, position);
                        }
                        return true;
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {

        Log.e("itemcount",String.valueOf(isMoreData?mDatas.size()+1:mDatas.size()+""));

        return isMoreData?mDatas.size()+1:mDatas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
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
        notifyDataSetChanged();
    }

    public void deleteItemData(int index,T data){
        if(index != -1 && data !=null){
            mDatas.remove(data);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index,mDatas.size());
        }
    }

}
