package cn.com.hesc.dragexpandgridview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.dragexpandgridview.Model.DargChildInfo;
import cn.com.hesc.dragexpandgridview.Model.DragIconInfo;

/**
 * ProjectName: FastDev-master
 * ClassName: CustomAboveView
 * Description: 自绘表格，模拟gridview，生成一级菜单
 * Author: liujunlin
 * Date: 2016-10-28 09:02
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class CustomAboveView extends LinearLayout{


    private ArrayList<DragIconInfo> mIconInfoList = new ArrayList<DragIconInfo>();
    private Context mContext;
    private CustomGroup mCustomGroup;
    private ItemViewClickListener mItemViewClickListener;
    private final int verticalViewWidth = 1;
    private CustomAboveViewClickListener gridViewClickListener;
    private MotionEvent firstEvent;
    public static final int  MORE = 99999;
    private List<View> mainViews = new ArrayList<>();//保留一级菜单项的view
    private List<View> subViews = new ArrayList<>();//保留二级菜单项的view

    public interface CustomAboveViewClickListener {
        /**
         * 点击一级菜单
         * @param iconInfo 点击的一级数据
         */
        public void onSingleClicked(DragIconInfo iconInfo);

        /**
         * 点击二级菜单
         * @param childInfo 点击的二级数据
         */
        public void onChildClicked(DargChildInfo childInfo);
    }

    public CustomAboveView(Context context, CustomGroup customGoup) {
        super(context, null);
        this.mContext = context;
        this.mCustomGroup = customGoup;
        setOrientation(LinearLayout.VERTICAL);
        initData();
    }

    public CustomAboveViewClickListener getGridViewClickListener() {
        return gridViewClickListener;
    }

    public void setGridViewClickListener(CustomAboveViewClickListener gridViewClickListener) {
        this.gridViewClickListener = gridViewClickListener;
    }

    /**
     * 设置二级菜单的点击监听
     */
    private void initData() {
        mChildClickListener = new CustomSubGridView.CustomChildClickListener() {

            @Override
            public void onChildClicked(DargChildInfo chilidInfo) {
                if (gridViewClickListener != null) {
                    gridViewClickListener.onChildClicked(chilidInfo);
                }
            }
        };
    }

    /**
     * 刷新一级菜单界面
     * @param iconInfoList
     */
    public void refreshIconInfoList(ArrayList<DragIconInfo> iconInfoList) {
        mIconInfoList.clear();
        mIconInfoList.addAll(iconInfoList);
        refreshViewUI();
    }

    /**
     *
     * 方法: getIconInfoList <p>
     * 描述: 获取一级菜单
     * 参数: @return <p>
     * 返回: ArrayList<DragIconInfo> <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午6:54:43
     */
    public ArrayList<DragIconInfo> getIconInfoList() {
        return mIconInfoList;
    }

    public void setIconInfoList(ArrayList<DragIconInfo> mIconInfoList) {
        this.mIconInfoList = mIconInfoList;
    }

    /**
     *
     * 方法: refreshViewUI <p>
     * 描述:  刷新UI，完成整体一级菜单网格的绘制
     * 参数:  <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午7:02:17
     */
    private void refreshViewUI() {
        mainViews.clear();
        subViews.clear();
        removeAllViews();
        //计算一级菜单行数
        int rowNum = mIconInfoList.size() / CustomGroup.COLUMNUM + (mIconInfoList.size() % CustomGroup.COLUMNUM > 0 ? 1 : 0);
        //设置一行的宽高
        LayoutParams rowParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //设置竖直分隔线
        LayoutParams verticalParams = new LayoutParams(verticalViewWidth, LayoutParams.FILL_PARENT);
        //设置水平分隔线
        LayoutParams horizontalParams = new LayoutParams(LayoutParams.FILL_PARENT, verticalViewWidth);
        //先画行
        for (int rowIndex = 0; rowIndex < rowNum; rowIndex++) {
            final View rowView = View.inflate(mContext, R.layout.gridview_above_rowview, null);

            LinearLayout llRowContainer = (LinearLayout) rowView.findViewById(R.id.gridview_rowcontainer_ll);
            final ImageView ivOpenFlag = (ImageView) rowView.findViewById(R.id.gridview_rowopenflag_iv);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
            ivOpenFlag.setMinimumWidth(width/CustomGroup.COLUMNUM);
            ivOpenFlag.setMaxWidth(width/CustomGroup.COLUMNUM);

            LinearLayout llBtm = (LinearLayout) rowView.findViewById(R.id.gridview_rowbtm_ll);
            final CustomSubGridView gridViewNoScroll = (CustomSubGridView) rowView.findViewById(R.id.gridview_child_gridview);
            if(mChildClickListener!=null){
                gridViewNoScroll.setChildClickListener(mChildClickListener);
            }
            gridViewNoScroll.setParentView(llBtm);
            LayoutParams itemParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            itemParam.weight = 1.0f;
            ItemViewClickListener itemClickLitener = new ItemViewClickListener(llBtm, ivOpenFlag, new ItemViewClickInterface() {

                @Override
                public boolean shoudInteruptViewAnimtion(ItemViewClickListener listener, int position) {
                    boolean isInterupt = false;
                    if (mItemViewClickListener != null && !mItemViewClickListener.equals(listener)) {
                        mItemViewClickListener.closeExpandView();
                    }
                    mItemViewClickListener = listener;
                    DragIconInfo iconInfo = mIconInfoList.get(position);
                    List<DargChildInfo> childList = iconInfo.getChildList();
                    if (childList.size() > 0) {
                        gridViewNoScroll.refreshDataSet(childList);
                    } else {
                        setViewCollaps();
                        isInterupt = true;
                        if (gridViewClickListener != null) {
                            gridViewClickListener.onSingleClicked(iconInfo);
                        }
                    }
                    return isInterupt;
                }

                @Override
                public void viewUpdateData(int position) {
                    gridViewNoScroll.notifyDataSetChange(true);
                }
            });
            for (int columnIndex = 0; columnIndex < CustomGroup.COLUMNUM; columnIndex++) {
                View itemView = View.inflate(mContext, R.layout.gridview_above_itemview, null);
                ImageView ivIcon = (ImageView) itemView.findViewById(R.id.icon_iv);
                TextView tvName = (TextView) itemView.findViewById(R.id.name_tv);
                int itemInfoIndex = rowIndex * CustomGroup.COLUMNUM + columnIndex;
                if (itemInfoIndex > mIconInfoList.size()-1) {
                    itemView.setVisibility(View.INVISIBLE);
                }else{
                    final DragIconInfo iconInfo = mIconInfoList.get(itemInfoIndex);
                    ivIcon.setImageResource(iconInfo.getResIconId());
                    tvName.setText(iconInfo.getName());
                    itemView.setId(itemInfoIndex);
                    itemView.setTag(itemInfoIndex);

                    mainViews.add(itemView);

                    itemView.setOnClickListener(itemClickLitener);
                    itemView.setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            return true;
                        }
                    });
                }

                llRowContainer.addView(itemView, itemParam);
                View view = new View(mContext);
                view.setBackgroundResource(R.color.gray_1);
                llRowContainer.addView(view, verticalParams);
            }
            View view = new View(mContext);
            view.setBackgroundResource(R.color.gray_1);
            addView(view, horizontalParams);
            addView(rowView, rowParam);
            if (rowIndex == rowNum - 1) {
                View btmView = new View(mContext);
                btmView.setBackgroundResource(R.color.gray_1);
                addView(btmView, horizontalParams);
            }

        }
    }

    public List<View> getMainViews() {
        return mainViews;
    }

    /**
     *
     * 方法: setViewCollaps <p>
     * 描述: TODO <p>
     * 参数:  <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: wedcel wedcel@gmail.com <p>
     * 时间: 2015年8月25日 下午7:03:23
     */
    public void setViewCollaps() {
        if (mItemViewClickListener != null) {
            mItemViewClickListener.closeExpandView();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.firstEvent = ev;
        getParent().requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
    }

    public MotionEvent getFirstEvent() {
        return firstEvent;
    }

    public void setFirstEvent(MotionEvent firstEvent) {
        this.firstEvent = firstEvent;
    }

    private CustomSubGridView.CustomChildClickListener mChildClickListener;

    public interface ItemViewClickInterface {
        public boolean shoudInteruptViewAnimtion(ItemViewClickListener animUtil, int position);

        public void viewUpdateData(int position);
    }

    public class ItemViewClickListener implements OnClickListener {

        private View mContentParent;
        private ItemViewClickInterface animationListener;
        private final int INVALID_ID = -1000;
        private int mLastViewID = INVALID_ID;
        private View mViewFlag;

        private int startX;
        private int viewFlagWidth;
        private int itemViewWidth;

        public ItemViewClickListener(View contentParent, View viewFlag, ItemViewClickInterface animationListener) {
            this.mContentParent = contentParent;
            this.animationListener = animationListener;
            this.mViewFlag = viewFlag;
        }

        public View getContentView() {
            return mContentParent;
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            boolean isTheSameView = false;
            if (animationListener != null) {
                if (animationListener.shoudInteruptViewAnimtion(this, viewId)) {
                    return;
                }
            }
            if (mLastViewID == viewId) {
                isTheSameView = true;
            } else {
                mViewFlag.setVisibility(View.VISIBLE);
                viewFlagWidth = getViewFlagWidth();
                itemViewWidth = view.getWidth();
                int endX = view.getLeft() + itemViewWidth / 2 - viewFlagWidth / 2;
                if (mLastViewID == INVALID_ID) {
                    startX = endX;
                    xAxismoveAnim(mViewFlag, startX, endX);
                } else {
                    xAxismoveAnim(mViewFlag, startX, endX);
                }
                startX = endX;
            }
            boolean isVisible = mContentParent.getVisibility() == View.VISIBLE;
            if (isVisible) {
                if (isTheSameView) {
                    animateCollapsing(mContentParent);
                } else {
                    if (animationListener != null) {
                        animationListener.viewUpdateData(viewId);//同一行需要更新数据

                    }
                }
            } else {
                if (isTheSameView) {
                    mViewFlag.setVisibility(View.VISIBLE);
                    xAxismoveAnim(mViewFlag, startX, startX);
                }
                animateExpanding(mContentParent);
            }
            mLastViewID = viewId;
        }

        private int getViewFlagWidth() {
            int viewWidth = mViewFlag.getWidth();
            if (viewWidth == 0) {
//                int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                mViewFlag.measure(widthSpec, heightSpec);
//                viewWidth = mViewFlag.getMeasuredWidth();
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
                viewWidth = width/CustomGroup.COLUMNUM;
            }
            return viewWidth;
        }



        /**
         *
         * 方法: xAxismoveAnim <p>
         * 描述: x轴移动 <p>
         * 参数: @param v
         * 参数: @param startX
         * 参数: @param toX <p>
         * 返回: void <p>
         * 异常  <p>
         * 作者: wedcel wedcel@gmail.com <p>
         * 时间: 2015年8月25日 下午7:03:35
         */
        public void xAxismoveAnim(View v, int startX, int toX) {
            moveAnim(v, startX, toX, 0, 0, 200);
        }

        /**
         *
         * 方法: moveAnim <p>
         * 描述: 移动动画 <p>
         * 参数: @param v
         * 参数: @param startX
         * 参数: @param toX
         * 参数: @param startY
         * 参数: @param toY
         * 参数: @param during <p>
         * 返回: void <p>
         * 异常  <p>
         * 作者: wedcel wedcel@gmail.com <p>
         * 时间: 2015年8月25日 下午7:03:40
         */
        private void moveAnim(View v, int startX, int toX, int startY, int toY, long during) {
            TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
            anim.setDuration(during);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }

        /**
         *
         * 方法: closeExpandView <p>
         * 描述: 收缩 <p>
         * 参数:  <p>
         * 返回: void <p>
         * 异常  <p>
         * 作者: wedcel wedcel@gmail.com <p>
         * 时间: 2015年8月25日 下午7:03:49
         */
        public void closeExpandView() {
            boolean isVisible = mContentParent.getVisibility() == View.VISIBLE;
            if (isVisible) {
                animateCollapsing(mContentParent);
            }
        }

        /**
         *
         * 方法: animateCollapsing <p>
         * 描述: 收缩动画 <p>
         * 参数: @param view <p>
         * 返回: void <p>
         * 异常  <p>
         * 作者: wedcel wedcel@gmail.com <p>
         * 时间: 2015年8月25日 下午7:04:01
         */
        public void animateCollapsing(final View view) {
            int origHeight = view.getHeight();

            ValueAnimator animator = createHeightAnimator(view, origHeight, 0);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setVisibility(View.GONE);
                    mViewFlag.clearAnimation();
                    mViewFlag.setVisibility(View.GONE);
                }
            });
            animator.start();
        }

        /**
         *
         * 方法: animateExpanding <p>
         * 描述: 动画展开 <p>
         * 参数: @param view <p>
         * 返回: void <p>
         * 异常  <p>
         * 作者: wedcel wedcel@gmail.com <p>
         * 时间: 2015年8月25日 下午7:04:22
         */
        public void animateExpanding(final View view) {
            view.setVisibility(View.VISIBLE);
            final int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            final int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            view.measure(widthSpec, heightSpec);
            ValueAnimator animator = createHeightAnimator(view, 0, view.getMeasuredHeight());
            animator.start();
        }

        /**
         *
         * 方法: createHeightAnimator <p>
         * 描述: TODO <p>
         * 参数: @param view
         * 参数: @param start
         * 参数: @param end
         * 参数: @return <p>
         * 返回: ValueAnimator <p>
         * 异常  <p>
         * 作者: wedcel wedcel@gmail.com <p>
         * 时间: 2015年8月25日 下午7:04:29
         */
        public ValueAnimator createHeightAnimator(final View view, int start, int end) {
            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (Integer) valueAnimator.getAnimatedValue();

                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = value;
                    view.setLayoutParams(layoutParams);
                }
            });
            return animator;
        }

    }

}
