package cn.com.hesc.gpslibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import cn.com.hesc.gpslibrary.R;
import cn.com.hesc.gpslibrary.model.GpsStateBean;

/**
 * ProjectName: HescLibrary_GPS_Master
 * ClassName: GpsStateView
 * Description: 绘制GPS的卫星情况，可嵌入任何view中
 * Author: liujunlin
 * Date: 2017-05-02 11:55 
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class GpsStateView extends View{

    private int width;//视图的宽度
    private int height;//视图的高度
    private Context mContext;
    private int zhutiwidth = 30;//默认柱体的宽度
    private Point centerPoint;//球形圆心位置
    private int radius;//球形的半径
    private ArrayList<GpsStateBean> starlist;//连接到的卫星列表
    private String latString;
    private String logString;
    private RectF gpsStateRect;//卫星柱体图
    private Point gpsTextPoint;//GPS展示信息，左上点
    private static int gpscount = 9;//卫星展示个数
    private String drawGpsData = "";

    public GpsStateView(Context context) {
        super(context);
        this.mContext = context;
    }

    public GpsStateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
		 /*设置背景为白色*/
        canvas.drawColor(Color.WHITE);
        drawCircle(canvas);
        drawGpsData(canvas);
    }

    /**
     * 绘制地球仪和卫星图
     * @author liujunlin
     * @param canvas
     * @time 2013-12-25
     */
    private void drawCircle(Canvas canvas){
        initView();
        drawCircleEarth(canvas);
        drawGpsStateList(canvas);
    }

    private void initView(){
        centerPoint = new Point();
        //横屏
        if(width > height){
            radius = Math.min(width/8,height/4);
            centerPoint.x = width/4;
            centerPoint.y = height/2;
            gpsStateRect = new RectF();
            gpsStateRect.left = width/2;
            gpsStateRect.top = centerPoint.y - radius;
            gpsStateRect.right = width*7/8;
            gpsStateRect.bottom = centerPoint.y + radius;
            gpsTextPoint = new Point();
            gpsTextPoint.x = centerPoint.x - radius;
            gpsTextPoint.y = centerPoint.y + radius + 30;
        }
        //纵屏
        else{
            radius = Math.min(height/8,width/4);
            centerPoint.x = width/2;
            centerPoint.y = height/4;
            gpsStateRect = new RectF();
            gpsStateRect.left = width/8;
            gpsStateRect.top = centerPoint.y + radius +radius;
            gpsStateRect.right = width*7/8;
            gpsStateRect.bottom = centerPoint.y + 4*radius;

            gpsTextPoint = new Point();
            gpsTextPoint.x = width/8;
            gpsTextPoint.y = Math.max(height - radius,50);
        }
    }

    /**
     * 绘制地图仪，同时绘制角度
     * @param canvas
     */
    private void drawCircleEarth(Canvas canvas){

        Paint paint=new Paint();
        /**去锯齿*/
        paint.setAntiAlias(true);
        /**设置paint的颜色*/
        paint.setColor(Color.RED);
        /**设置paint的 style 为STROKE：空心*/
        paint.setStyle(Paint.Style.STROKE);
        /**设置paint的外框宽度*/
        paint.setStrokeWidth(3);

        Point centerTopPoint = new Point(centerPoint.x,(centerPoint.y-radius));
        Point centerBottomPoint = new Point(centerPoint.x,(centerPoint.y+radius));
        Point centerLeftPoint = new Point(centerPoint.x-radius,centerPoint.y);
        Point centerRightPoint = new Point(centerPoint.x+radius,centerPoint.y);

        int x = (int)(centerPoint.x-radius*Math.cos(Math.PI*45/180));
        int y = (int)(centerPoint.y-radius*Math.sin(Math.PI*45/180));
        Point leftTopPoint = new Point(x,y);

        int xx = (int)(centerPoint.x+radius*Math.cos(Math.PI*45/180));
        int yy = (int)(centerPoint.y+radius*Math.sin(Math.PI*45/180));
        Point RightBottomPoint = new Point(xx,yy);

        int x2 = (int)(centerPoint.x+radius*Math.cos(Math.PI*45/180));
        int y2 = (int)(centerPoint.y-radius*Math.sin(Math.PI*45/180));
        Point RighTopPoint = new Point(x2,y2);

        int xx2 = (int)(centerPoint.x-radius*Math.cos(Math.PI*45/180));
        int yy2 = (int)(centerPoint.y+radius*Math.sin(Math.PI*45/180));
        Point LeftBottomPoint = new Point(xx2,yy2);


        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, paint);
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius/2, paint);

        canvas.drawLine(centerLeftPoint.x, centerLeftPoint.y, centerRightPoint.x, centerRightPoint.y, paint);
        drawText(canvas,"W\n270°",centerLeftPoint.x-5,centerLeftPoint.y,-90);
        drawText(canvas,"E\n90°",centerRightPoint.x+5,centerRightPoint.y,90);
        canvas.drawLine(leftTopPoint.x, leftTopPoint.y , RightBottomPoint.x, RightBottomPoint.y, paint);
        drawText(canvas,"315°",leftTopPoint.x-5,leftTopPoint.y-5,-45);
        drawText(canvas,"135°",RightBottomPoint.x+5,RightBottomPoint.y+5,135);
        canvas.drawLine(centerTopPoint.x, centerTopPoint.y, centerBottomPoint.x, centerBottomPoint.y, paint);
        drawText(canvas,"N\n0°",centerTopPoint.x,centerTopPoint.y-5,0);
        drawText(canvas,"S\n180°",centerBottomPoint.x,centerBottomPoint.y+5,180);
        canvas.drawLine(RighTopPoint.x, RighTopPoint.y, LeftBottomPoint.x, LeftBottomPoint.y, paint);
        drawText(canvas,"45°",RighTopPoint.x+5,RighTopPoint.y-5,45);
        drawText(canvas,"225°",LeftBottomPoint.x-5,LeftBottomPoint.y+5,225);
    }

    private void drawText(Canvas canvas ,String text , float x ,float y,float angle){

        Paint paint=new Paint();
        /*去锯齿*/
        paint.setAntiAlias(true);
        /*设置paint的颜色*/
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.circle_fontsize));


        if(angle != 0){
            canvas.rotate(angle, x, y);
        }
        canvas.drawText(text, x, y, paint);

        if(angle != 0){
            canvas.rotate(-angle, x, y);
        }
    }

    /**
     * 绘制卫星列表的地图和相关搜星过程
     * @param canvas
     */
    private void drawGpsStateList(Canvas canvas){
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        /*去锯齿*/
        paint.setAntiAlias(true);
        /*设置paint的颜色*/
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(gpsStateRect, 20, 15, paint);//绘制底图边框

        Paint rcpaint=new Paint();
        rcpaint.setStyle(Paint.Style.FILL);
        rcpaint.setARGB(255, 39, 64, 139);
        canvas.drawRoundRect(gpsStateRect, 21, 16, rcpaint);//填充底图

        Paint textp=new Paint();
        /*去锯齿*/
        textp.setAntiAlias(true);
        /*设置paint的颜色*/
        textp.setColor(Color.BLACK);
        textp.setTextAlign(Paint.Align.RIGHT);
        textp.setTextSize(mContext.getResources().getDimension(R.dimen.circle_fontsize));

        Paint linepP = new Paint();
        linepP.setARGB(255, 108, 166, 205);


        int ge = (int)gpsStateRect.height()/5;//设置分隔线

        RectF rect = gpsStateRect;

        canvas.drawLine(rect.left, rect.bottom - ge, rect.right,  rect.bottom - ge, linepP);
        canvas.drawText("25", rect.left-5, rect.bottom - ge, textp);
        canvas.drawLine(rect.left, rect.bottom - ge*2, rect.right,  rect.bottom - ge*2, linepP);
        canvas.drawText("50", rect.left-5, rect.bottom - ge*2, textp);
        canvas.drawLine(rect.left, rect.bottom - ge*3, rect.right,  rect.bottom - ge*3, linepP);
        canvas.drawText("75", rect.left-5, rect.bottom - ge*3, textp);
        canvas.drawLine(rect.left, rect.bottom - ge*4, rect.right,  rect.bottom - ge*4, linepP);
        canvas.drawText("100", rect.left-5, rect.bottom - ge*4, textp);

        if(starlist != null && starlist.size()>0)
            gpscount = starlist.size();

        int starnum = (int)rect.width()/gpscount;//定义卫星一共能收到10颗/*gpscount*/
        Paint starp=new Paint();
        /*去锯齿*/
        starp.setAntiAlias(true);
        starp.setStyle(Paint.Style.FILL);//充满

        Paint textp1=new Paint();
        /*去锯齿*/
        textp1.setAntiAlias(true);
        /*设置paint的颜色*/
        textp1.setColor(Color.BLACK);
        textp1.setTextAlign(Paint.Align.CENTER);

        Paint starposition=new Paint();
        /*去锯齿*/
        starposition.setAntiAlias(true);
        /*设置paint的颜色*/
        starposition.setColor(Color.BLUE);
        starposition.setStyle(Paint.Style.FILL);//充满

		/*绘制卫星高度*/
        if(starlist!=null && starlist.size()>0){
            for (int i = 0; i < gpscount; i++) {
                if(i<starlist.size()){
                    GpsStateBean sBean = starlist.get(i);
                    int stateLeft = (int)rect.left+starnum*(i)+10;
                    /*设置paint的颜色*/
                    if(sBean.getGs().getSnr() < 10)
                        starp.setColor(Color.RED);
                    else if(sBean.getGs().getSnr() >= 10 && sBean.getGs().getSnr() < 20)
                        starp.setColor(Color.argb(255, 255, 128, 0));
                    else if(sBean.getGs().getSnr() >= 20 && sBean.getGs().getSnr() < 30)
                        starp.setColor(Color.argb(255, 255, 255, 0));
                    else if(sBean.getGs().getSnr() >= 30 && sBean.getGs().getSnr() < 40)
                        starp.setColor(Color.argb(255, 217, 255, 0));
                    else
                        starp.setColor(Color.GREEN);
                    canvas.drawRect(stateLeft, (float) (rect.bottom-(sBean.getGs().getElevation()*ge/25.0)), stateLeft+zhutiwidth, rect.bottom, starp);
                    canvas.drawText(""+(i+1), stateLeft+zhutiwidth/2, rect.bottom + 20, textp1);

                    Point pt = getStarP(sBean);
                    canvas.drawCircle(pt.x, pt.y, 5,starposition);

                    if(sBean.getLatString()!=null&&sBean.getLogString()!=null){
                        latString = sBean.getLatString();
                        logString = sBean.getLogString();
                    }
                }

            }
        }

        drawZBinfo(canvas);

    }

    private void drawGpsData(Canvas canvas){
        Paint textp=new Paint();
        /*去锯齿*/
        textp.setAntiAlias(true);
        /*设置paint的颜色*/
        textp.setColor(Color.BLACK);
        textp.setTextAlign(Paint.Align.RIGHT);
        textp.setTextSize(mContext.getResources().getDimension(R.dimen.circle_fontsize));

        if(!TextUtils.isEmpty(drawGpsData)){
            canvas.drawText(drawGpsData,width*3/4,gpsTextPoint.y,textp);
        }

    }

    /*绘制卫星在地球上*/
    private Point getStarP(GpsStateBean sBean){
        //根据方向角和高度角计算出，卫星显示的位置
        Point point = new Point();
        int x = centerPoint.x; //左边地球圆形的圆心位置X坐标
        int y = centerPoint.y; //左边地球圆形的圆心位置Y坐标
        int r = radius;
        x+=(int)((r*sBean.getGs().getElevation()*Math.sin(Math.PI*sBean.getGs().getAzimuth()/180)/90));
        y-=(int)((r*sBean.getGs().getElevation()*Math.cos(Math.PI*sBean.getGs().getAzimuth()/180)/90));
        point.x = x;
        point.y = y;
        //point就是你需要绘画卫星图的起始坐标
        return point;
    }
    /*将经纬度绘制上去*/
    private void drawZBinfo(Canvas canvas){
        Paint textp=new Paint();
        /*去锯齿*/
        textp.setAntiAlias(true);
        /*设置paint的颜色*/
        textp.setColor(Color.BLACK);
        textp.setTextSize(28);

        if(latString != null && logString!=null){
            canvas.drawText("纬度:"+latString,30 , centerPoint.y+radius*3/2, textp);
            canvas.drawText("经度:"+logString,width/2+20 , centerPoint.y+radius*3/2, textp);
        }
    }

    public ArrayList<GpsStateBean> getStarlist() {
        return starlist;
    }

    public void setStarlist(ArrayList<GpsStateBean> starlist) {
        if(starlist!=null && starlist.size()>0){
            this.starlist = starlist;
            invalidate();
        }
    }

    public String getDrawGpsData() {
        return drawGpsData;
    }

    public void setDrawGpsData(String drawGpsData) {
        this.drawGpsData = drawGpsData;
    }
}
