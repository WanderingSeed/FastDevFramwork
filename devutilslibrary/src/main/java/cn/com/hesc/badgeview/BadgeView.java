package cn.com.hesc.badgeview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * ProjectName: FastDev-master
 * ClassName: BadgeView
 * Description: 做类似未读消息数量提醒，可以在依附组件的上下左右中显示
 * Author: liujunlin
 * Date: 2016-10-24 09:20
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class BadgeView extends TextView{

    /**设置挂载在父view的位置--左上*/
    public static final int POSITION_TOP_LEFT = 1;
    /**设置挂载在父view的位置--右上*/
    public static final int POSITION_TOP_RIGHT = 2;
    /**设置挂载在父view的位置--左下*/
    public static final int POSITION_BOTTOM_LEFT = 3;
    /**设置挂载在父view的位置--右下*/
    public static final int POSITION_BOTTOM_RIGHT = 4;
    /**设置挂载在父view的位置--居中*/
    public static final int POSITION_CENTER = 5;
    /**设置挂载在父view的位置--居左*/
    public static final int POSITION_LEFT = 6;
    /**设置挂载在父view的位置--居右*/
    public static final int POSITION_RIGHT = 7;

    /**设置布局间隔--距4周5dip*/
    private static final int DEFAULT_MARGIN_DIP = 5;
    /**设置布局间隔--左右缩进5dip*/
    private static final int DEFAULT_LR_PADDING_DIP = 5;
    /**设置圆角--半径8dip*/
    private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
    /**设置默认挂载位置---右上*/
    private static final int DEFAULT_POSITION = POSITION_TOP_RIGHT;
    /**设置默认底色偏红色*/
    private static final int DEFAULT_BADGE_COLOR = Color.parseColor("#CCFF0000"); //Color.RED;
    /**设置字体颜色白色*/
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private Context context;
    /**绑定的宿主组件*/
    private View target;

    private int badgePosition;
    private int badgeMarginH;
    private int badgeMarginV;
    private int badgeColor;
    private int badgeTextColor;

    /**是否显示*/
    private boolean isShown;

    private ShapeDrawable badgeBg;
    /**如果绑定的tab，tab的索引*/
    private int targetTabIndex;

    public BadgeView(Context context) {
        this(context, (AttributeSet) null, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    /**
     * Constructor -
     *
     * create a new BadgeView instance attached to a target {@link View}.
     *
     * @param context context for this view.
     * @param target the View to attach the badge to.
     */
    public BadgeView(Context context, View target) {
        this(context, null, android.R.attr.textViewStyle, target, 0);
    }

    /**
     * Constructor -
     *
     * create a new BadgeView instance attached to a target {@link TabWidget}
     * tab at a given index.
     *
     * @param context context for this view.
     * @param target the TabWidget to attach the badge to.
     * @param index the position of the tab within the target.
     */
    public BadgeView(Context context, TabWidget target, int index) {
        this(context, null, android.R.attr.textViewStyle, target, index);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex) {
        super(context, attrs, defStyle);
        init(context, target, tabIndex);
    }

    public int getBadgeMarginH() {
        return badgeMarginH;
    }

    /***
     * 横向间隔
     */
    public void setBadgeMarginH(int badgeMarginH) {
        this.badgeMarginH = badgeMarginH;
    }

    public int getBadgeMarginV() {
        return badgeMarginV;
    }


    /***
     * 纵向间隔
     */
    public void setBadgeMarginV(int badgeMarginV) {
        this.badgeMarginV = badgeMarginV;
    }

    /**
     * Returns the color value of the badge background.
     *
     */
    public int getBadgeBackgroundColor() {
        return badgeColor;
    }

    /**
     * Set the color value of the badge background.
     *
     * @param badgeColor the badge background color.
     */
    public void setBadgeBackgroundColor(int badgeColor) {
        this.badgeColor = badgeColor;
        badgeBg = getDefaultBackground();
    }

    public int getBadgeTextColor() {
        return badgeTextColor;
    }

    /**
     * 设置字体颜色
     * @param badgeTextColor
     */
    public void setBadgeTextColor(int badgeTextColor) {
        this.badgeTextColor = badgeTextColor;
    }

    public int getBadgePosition() {
        return badgePosition;
    }

    /**
     * badgeview挂靠的位置
     * @param badgePosition
     */
    public void setBadgePosition(int badgePosition) {
        this.badgePosition = badgePosition;
    }

    /**
     * 组件初始化
     * @param context 上下文
     * @param target 依附页签
     * @param tabIndex 页签索引
     */
    private void init(Context context, View target, int tabIndex) {

        this.context = context;
        this.target = target;
        this.targetTabIndex = tabIndex;

        // apply defaults
        badgePosition = DEFAULT_POSITION;
        badgeMarginH = dipToPixels(DEFAULT_MARGIN_DIP);
        badgeMarginV = badgeMarginH;
        badgeColor = DEFAULT_BADGE_COLOR;
        badgeTextColor = DEFAULT_TEXT_COLOR;

        setTypeface(Typeface.DEFAULT_BOLD);
        int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
        setPadding(paddingPixels, 0, paddingPixels, 0);
        isShown = false;
        if (this.target != null) {
            applyTo(this.target);
        } else {
            show();
        }
    }

    private void applyTo(View target) {
        ViewGroup.LayoutParams lp = target.getLayoutParams();
        ViewParent parent = target.getParent();
        FrameLayout container = new FrameLayout(context);
        if (target instanceof TabWidget) {
            // set target to the relevant tab child container
            target = ((TabWidget) target).getChildTabViewAt(targetTabIndex);
            this.target = target;
            ((ViewGroup) target).addView(container,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            this.setVisibility(View.GONE);
            container.addView(this);
        } else {
            ViewGroup group = (ViewGroup) parent;
            int index = group.indexOfChild(target);
            group.removeView(target);
            group.addView(container, index, lp);
            container.addView(target);
            this.setVisibility(View.GONE);
            container.addView(this);
            group.invalidate();
        }

    }

    /**
     * 显示badgeview
     */
    public void show() {
        setTextColor(badgeTextColor);
        if (getBackground() == null) {
            if (badgeBg == null) {
                badgeBg = getDefaultBackground();
            }
            setBackgroundDrawable(badgeBg);
        }
        applyLayoutParams();

        this.setVisibility(View.VISIBLE);
        isShown = true;
    }

    /**
     * 隐藏badgeview
     */
    public void hide() {
        this.setVisibility(View.GONE);
        isShown = false;
        setText("");
    }

    private ShapeDrawable getDefaultBackground() {
        int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
        float[] outerR = new float[] {r, r, r, r, r, r, r, r};
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(badgeColor);
        return drawable;
    }



    private void applyLayoutParams() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (badgePosition) {
            case POSITION_TOP_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.TOP;
//                lp.setMargins(badgeMarginH, badgeMarginV, 0, 0);
                lp.setMargins(0, 0, badgeMarginH, 0);
                break;
            case POSITION_TOP_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.TOP;
//                lp.setMargins(0, badgeMarginV, badgeMarginH, 0);
                lp.setMargins(badgeMarginH, 0, 0, 0);
                break;
            case POSITION_BOTTOM_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
//                lp.setMargins(badgeMarginH, 0, 0, badgeMarginV);
                lp.setMargins(0, 0, badgeMarginH, 0);
                break;
            case POSITION_BOTTOM_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//                lp.setMargins(0, 0, badgeMarginH, badgeMarginV);
                lp.setMargins(badgeMarginH, 0, 0, 0);
                break;
            case POSITION_CENTER:
                lp.gravity = Gravity.CENTER;
                lp.setMargins(0, 0, 0, 0);
                break;
            case POSITION_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.CENTER;
                lp.setMargins(badgeMarginH, 0, 0, 0);
                break;
            case POSITION_RIGHT:
                lp.gravity = Gravity.RIGHT  | Gravity.CENTER;
                lp.setMargins(0, 0, badgeMarginH, 0);
                break;
            default:
                break;
        }
        setLayoutParams(lp);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }
}
