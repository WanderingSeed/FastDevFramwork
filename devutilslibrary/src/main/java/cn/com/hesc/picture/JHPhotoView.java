package cn.com.hesc.picture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * created by liujunlin on 2019/7/4 09:46
 */
public class JHPhotoView extends android.support.v7.widget.AppCompatImageView {

    private int border = 0,heighborder = 0;//矩形的边长
    private Point centerPointInRect = null;//矩形中心点
    private int maxborder = 0;//最大边长
    private int minborder = 0;//最小边长
    private Paint paint;
    private int downfingers = 0;
    private boolean isScale = false;
    //2指上次距离
    private float oldDist;
    private float beforex,beforey,afterX,afterY;
    private static int minDistance = 0;
    PointF mMidPt = new PointF();
    private int dpi = 480;

    public JHPhotoView(Context context) {
        super(context);
        setDrawingCacheEnabled(true);
        //获取系统认为的最小滑动距离
        minDistance = ViewConfiguration.get(context).getScaledTouchSlop();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        dpi = dm.densityDpi;
    }

    public JHPhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //获取imageview组件的高度和宽度
        int width = getWidth();
        int height = getHeight();

        maxborder = width*3/4;
        minborder = width/4;
        //居中绘制一个正方形，边长为宽的1/2
        if(border == 0) {
            border = maxborder;
        }
        if(width < height)
            heighborder = border / 2;
        else
            heighborder = border / 4;
        if(centerPointInRect == null){
            centerPointInRect = new Point();
            centerPointInRect.set(width/2,height/2);
        }

        //绘制矩形
        if(paint == null){
            paint = new Paint();
            paint.setColor(Color.rgb(255,0,0));
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10.0F);
        }
        Path path = new Path();
        path.addRect(new RectF(centerPointInRect.x-border/2,centerPointInRect.y-heighborder/2,centerPointInRect.x+border/2,centerPointInRect.y+heighborder/2),Path.Direction.CCW);
//        path.moveTo(centerPointInRect.x-border/2,centerPointInRect.y-border/2);
//        path.lineTo(centerPointInRect.x+border/2,centerPointInRect.y-border/2);
//        path.lineTo(centerPointInRect.x+border/2,centerPointInRect.y+border/2);
//        path.lineTo(centerPointInRect.x-border/2,centerPointInRect.y+border/2);
//        path.lineTo(centerPointInRect.x-border/2,centerPointInRect.y-border/2);
        Log.e("paint color start",paint.getColor()+"");
        canvas.drawPath(path,paint);
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveWithFinger(event);
        return true;
    }

    /**
     * 单点触控
     * @param event
     */
    private void moveWithFinger(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isScale = false;
                beforex = event.getX();
                beforey = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //双指在滑动
                if (event.getPointerCount() == 2) {
                    float curdistance = spacing(event);
                    if(Math.abs(curdistance - oldDist) > (20f*dpi/160)){
                        if((curdistance-oldDist)>0)
                            border = (border + 5*dpi/160);
                        else if((curdistance-oldDist)<0)
                            border = (border - 5*dpi/160);
                        if(border > maxborder)
                            border = maxborder;
                        else if(border < minborder)
                            border = minborder;
                    }

                }else if(event.getPointerCount() == 1 && !isScale){

                    afterX = event.getX();
                    afterY = event.getY();
                    int dx = (int) (afterX - beforex);
                    int dy = (int) (afterY - beforey);
                    centerPointInRect.set(centerPointInRect.x+dx,centerPointInRect.y+dy);
                    beforex = afterX;
                    beforey = afterY;
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                isScale = false;
                break;
            //多指中有一只离开屏幕触发
            case MotionEvent.ACTION_POINTER_UP:
                break;
            //至少第二指按压屏幕触发
            case MotionEvent.ACTION_POINTER_DOWN:
                if(event.getPointerCount() > 1) {
                    isScale = true;
                    oldDist = spacing(event);
                    if(oldDist > minDistance){//避免手指上有两个茧
                        mMidPt = mid(event);//计算两点之间中心点的位置
                    }
                }
                break;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两点之间中心点的位置
     * @param event
     * @return
     */
    private static PointF mid(MotionEvent event){
        float midx = event.getX(1) + event.getX(0);
        float midy = event.getY(1) + event.getY(0);

        return new PointF(midx/2, midy/2);
    }

    public void releaseSrc(){
        if(paint!=null)
            paint = null;
        if(centerPointInRect!=null)
            centerPointInRect = null;
    }
}
