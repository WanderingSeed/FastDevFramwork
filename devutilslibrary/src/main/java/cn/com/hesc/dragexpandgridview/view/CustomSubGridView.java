package cn.com.hesc.dragexpandgridview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.dragexpandgridview.Model.DargChildInfo;
import cn.com.hesc.tools.DensityUtils;

/**
 * ProjectName: FastDev-master
 * ClassName: CustomSubGridView
 * Description: 绘制二级菜单，通过一个个单元格来完成表格的绘制
 * Author: liujunlin
 * Date: 2016-10-28 09:20
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class CustomSubGridView extends LinearLayout {


    private Context mContext;
    private ArrayList<DargChildInfo> mPlayList = new ArrayList<DargChildInfo>();
    private int viewHeight;
    private int viewWidth;
    private LinearLayout mParentView;
    private int rowNum;
    private int verticalViewWidth;
    private CustomChildClickListener childClickListener;
    private List<View> subViews = new ArrayList<>();//保留二级菜单项的view

    /**
     * 点击事件接口
     */
    public interface CustomChildClickListener {
        public void onChildClicked(DargChildInfo chilidInfo);
    }

    public CustomSubGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomChildClickListener getChildClickListener() {
        return childClickListener;
    }

    public void setChildClickListener(CustomChildClickListener childClickListener) {
        this.childClickListener = childClickListener;
    }

    /**
     * 初始化子单元网格布局，这里设置布局文件和绑定
     * @param context
     */
    private void initView(Context context) {
        this.mContext = context;
        verticalViewWidth = DensityUtils.dp2px(mContext, 1.0f);
        View root = View.inflate(mContext, R.layout.gridview_child_layoutview, null);
        TextView textView = (TextView) root.findViewById(R.id.gridview_child_name_tv);
        int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(widthSpec, heightSpec);
        viewHeight = textView.getMeasuredHeight();
        viewWidth = (mContext.getResources().getDisplayMetrics().widthPixels - DensityUtils.dp2px(mContext, 2)) / CustomGroup.COLUMNUM;
    }

    public CustomSubGridView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 方法: refreshDataSet <p>
     * 描述: 刷新页面<p>
     * 参数: @param playList<p>
     * 返回: void<p>
     * 异常 <p>
     * 作者: 梅雄新 meixx@500wan.com<p>
     * 时间: 2014年11月15日 上午10:46:06<p>
     */
    public void refreshDataSet(List<DargChildInfo> playList) {
        mPlayList.clear();
        mPlayList.addAll(playList);
        notifyDataSetChange(false);
    }

    /**
     * 方法: notifyDataSetChange <p>
     * 描述: 刷新UI<p>
     * 参数: @param needAnim<p>
     * 返回: void<p>
     * 异常 <p>
     * 作者: 梅雄新 meixx@500wan.com<p>
     * 时间: 2014年11月15日 上午10:46:19<p>
     */
    public void notifyDataSetChange(boolean needAnim) {
        subViews.clear();
        removeAllViews();
        rowNum = mPlayList.size() / CustomGroup.COLUMNUM + (mPlayList.size() % CustomGroup.COLUMNUM > 0 ? 1 : 0);
        LayoutParams rowParam = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams verticalParams = new LayoutParams(verticalViewWidth, LayoutParams.FILL_PARENT);
        LayoutParams horizontalParams = new LayoutParams(LayoutParams.FILL_PARENT, verticalViewWidth);
        for (int rowIndex = 0; rowIndex < rowNum; rowIndex++) {
            LinearLayout llContainer = new LinearLayout(mContext);
            llContainer.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams itemParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            itemParam.width = viewWidth;
            for (int columnIndex = 0; columnIndex < CustomGroup.COLUMNUM; columnIndex++) {
                int itemInfoIndex = rowIndex * CustomGroup.COLUMNUM + columnIndex;
                boolean isValidateView = true;
                if (itemInfoIndex >= mPlayList.size()) {
                    isValidateView = false;
                }
                View root = View.inflate(mContext, R.layout.gridview_child_layoutview, null);
                TextView textView = (TextView) root.findViewById(R.id.gridview_child_name_tv);
                if (isValidateView) {
                    final DargChildInfo tempChilid = mPlayList.get(itemInfoIndex);
                    textView.setText(tempChilid.getName());
                    textView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (childClickListener != null) {
                                childClickListener.onChildClicked(tempChilid);
                            }
                        }
                    });
                    subViews.add(root);
                }
                llContainer.addView(root, itemParam);
                if (columnIndex != CustomGroup.COLUMNUM - 1) {
                    View view = new View(mContext);
                    view.setBackgroundResource(/*R.drawable.ver_line*/R.color.subItem_divider);
                    llContainer.addView(view, verticalParams);
                }
            }
            addView(llContainer, rowParam);
            View view = new View(mContext);
            view.setBackgroundResource(/*R.drawable.hor_line*/R.color.subItem_divider);
            addView(view, horizontalParams);
            Log.e("animator", "" + getHeight() + "--" + rowNum * viewHeight);
            if (needAnim) {
                createHeightAnimator(mParentView, CustomSubGridView.this.getHeight(), rowNum * viewHeight);
            }
        }
    }

    /**
     * 方法: createHeightAnimator <p>
     * 描述: TODO<p>
     * 参数: @param view
     * 参数: @param start
     * 参数: @param end<p>
     * 返回: void<p>
     * 异常 <p>
     * 作者: 梅雄新 meixx@500wan.com<p>
     * 时间: 2014年11月15日 上午10:46:35<p>
     */
    public void createHeightAnimator(final View view, int start, int end) {
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
        animator.setDuration(200);
        animator.start();
    }

    /**
     * 方法: setParentView <p>
     * 描述: TODO<p>
     * 参数: @param llBtm<p>
     * 返回: void<p>
     * 异常 <p>
     * 作者: 梅雄新 meixx@500wan.com<p>
     * 时间: 2014年11月15日 上午10:46:40<p>
     */
    public void setParentView(LinearLayout llBtm) {
        this.mParentView = llBtm;
    }

}
