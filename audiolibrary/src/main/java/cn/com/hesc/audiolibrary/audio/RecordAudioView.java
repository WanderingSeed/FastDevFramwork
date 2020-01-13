package cn.com.hesc.audiolibrary.audio;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.com.hesc.audiolibrary.R;


/**
 * ProjectName: union
 * ClassName: RecordAudioView
 * Description: 绘制录制语音界面，集成播放效果
 * Author: liujunlin
 * Date: 2018-03-01 11:46
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class RecordAudioView extends ImageView{

    private Context mContext;
    //内半径
    private Paint innerCircle;
    //外半径
    private Paint outnerCircle;
    //弧线
    private Paint arcPaint;
    private float sweepAngle = 0;

    public RecordAudioView(Context context) {
        super(context);
        mContext = context;
    }

    public RecordAudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setBackground(Drawable drawable){
        setBackground(drawable);
        invalidate();
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int square = Math.min(width,height);


        outnerCircle = new Paint();
        outnerCircle.setColor(Color.rgb(72,71,76));
        canvas.drawCircle(width/2,height/2,square/2,outnerCircle);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.audiopadding);
        innerCircle = new Paint();
        innerCircle.setColor(Color.WHITE);
        canvas.drawCircle(width/2,height/2,square/2-padding,innerCircle);
        arcPaint = new Paint();
        arcPaint.setColor(Color.rgb(120,160,195));
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth((float) 8.0);
        //起始角度,要旋转多少度
        canvas.drawArc(new RectF(padding/2,padding/2,square-padding/2,square-padding/2),-90,sweepAngle,false,arcPaint);
        //画个微型圆
        if(sweepAngle > 0){
            float miniCircleAngle = sweepAngle;
            int centerminiX = 0;
            int centerminiY = 0;
            centerminiX = (int)((1+Math.sin(Math.toRadians(miniCircleAngle)))*square/2 - padding/2*Math.sin(Math.toRadians(miniCircleAngle)));
            centerminiY = (int)((1-Math.cos(Math.toRadians(miniCircleAngle)))*square/2 + padding/2*Math.cos(Math.toRadians(miniCircleAngle)));
            Paint miniPaint = new Paint();
            miniPaint.setColor(Color.rgb(120,160,195));
            canvas.drawCircle(centerminiX,centerminiY,10f,miniPaint);
        }

        canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.audioflag),null,new Rect(square/4,square/4,3*square/4,3*square/4),null);

    }

}
