package cn.com.hesc.maplibrary.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.hesc.maplibrary.MapActivity;
import cn.com.hesc.maplibrary.R;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapLayer;
import cn.com.hesc.maplibrary.model.MapPaint;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.model.PartsObjectAttributes;
import cn.com.hesc.maplibrary.model.PubBitmap;
import cn.com.hesc.maplibrary.model.PubGisProcess;
import cn.com.hesc.maplibrary.overlayer.InfoWindow;
import cn.com.hesc.maplibrary.overlayer.LatLng;
import cn.com.hesc.maplibrary.overlayer.Marker;
import cn.com.hesc.maplibrary.presenter.MapPresenter;
import cn.com.hesc.maplibrary.tools.BitMapUtils;
import cn.com.hesc.tools.ToastUtils;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: MapView
 * Description: 地图绘制view主类
 * Author: liujunlin
 * Date: 2016-04-12 10:05
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

    public class MapView extends ImageView implements  iMapView{

    private Context mContext;
    public int screenHeight = 0;
    public int screenWidth = 0;
    /**组合图里底图的左上坐标点*/
    private float groupbasemapx = 0;
    private float groupbasemapy = 0;
    private Bitmap basebitmap;
    /**组合图里标注图层的左上坐标点，只针对天地图*/
    private float groupAnomapx = 0;
    private float groupAnomapy = 0;
    private Bitmap annbitmap;

    /**组合图里部件图层的左上坐标点*/
    private Bitmap partBitmap;

    private boolean isdonedraw = true;//默认画布已绘制完成
    // 创建画笔
    private Paint paint = new Paint();

    private float beforex,beforey,tempbx,tempby;
    private float afterX,afterY;

    private MapLayer[] mapLayers;//要展示的部件图层

    /**标识已选择的位置点图片*/
    public PubBitmap hadlocationBitmap;
    /**记录已选择的位置点信息*/
    public MapPoint hadlocationPoint;
    /**GPS点图片*/
    protected PubBitmap gpsBitmap;
    /**GPS点信息*/
    protected MapPoint gpsPoint;
    /**手动位置选择图片*/
    public PubBitmap locationBitmap;
    /**选中的位置点*/
    private MapPoint location;
    private AlertDialog.Builder partsLayerShowBuilder = null;
    public String returnPartCode = "";
    private boolean isPartChoose = false;
    private float basicScale = 1f,midScale = 1.75f,maxScale = 2.6f;
    private float cureScale = 1f;
    private boolean isScale = false;
    /**记录每次刷新地图后，地图的右、下边和屏幕的间隔距离*/
    private float rightspace = 0,bottomspace = 0,leftspce = 0,topspace = 0;
    /**要绘制到地图上的一系列点*/
    private List<MapPoint> mMapPoints;
    /**绘制路径,支持多个路径绘制*/
    private List<List<MapPoint>> mPathMapPoints;
    /**绘制可以点击的点位*/
    private List<Marker> markers;
    private int scale  = 1;//视图放大倍数
    //按压的手指数
    private int downfingers = 0;
    //2指上次距离
    private float oldDist;
    //保留之前的比例
    private float twofingerslastscale = 1;
    private String tempUrl = "";//符合金开区部件URL
    private ImapOnLoadFinishListener imapOnLoadFinishListener;
    private static int minDistance = 0;
    /**当设置了最大比例尺为18时，只加载国家级天地图，不加载省级*/
    private int maxMapLevel = 0;

    public interface ImapOnLoadFinishListener{
        void onLoadFinish();
    }

    public void setImapOnLoadFinishListener(ImapOnLoadFinishListener imapOnLoadFinishListener) {
        this.imapOnLoadFinishListener = imapOnLoadFinishListener;
    }

    /**
     * 定义地图操作的手势类型
     */
    public enum Map_Gesture{
        TRANSLATION,//平移
        ZOOMIN,//放大
        ZOOMOUT,//缩小
        GPS,//GPS定位
        PARTQUERY,//部件查询
        CHOOSELOCATION, //位置选择,
        REFRUSHMAP  //刷新地图指令
    }

    private MapPresenter mMapPresenter;
    private WaitProgressDialog mProgressBar;
    private Map_Gesture curgesture;
    private MapExtent mMapExtent;
    PointF  mMidPt = new PointF();
    private boolean isscalemode = false;
    private Matrix scalematrix = new Matrix();

    public MapView(Context context) {
        super(context);
        this.mContext = context;
        //获取系统认为的最小滑动距离
        minDistance = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Map_Gesture getCurgesture() {
        return curgesture;
    }

    public void setCurgesture(Map_Gesture curgesture) {
        this.curgesture = curgesture;
    }

    public MapPoint getLocation() {
        return location;
    }

    public String getReturnPartCode() {
        return returnPartCode;
    }

    public List<MapPoint> getMapPoints() {
        return mMapPoints;
    }

    public void setMapPoints(List<MapPoint> mapPoints) {
        mMapPoints = mapPoints;
    }

    public List<List<MapPoint>> getPathMapPoints() {
        return mPathMapPoints;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    public void setPathMapPoints(List<List<MapPoint>> pathMapPoints) {
        mPathMapPoints = pathMapPoints;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            int w = getWidth();
            int h = getHeight();
            Rect srcrect = new Rect();
            srcrect.left = (int) Math.abs(0-groupbasemapx);
            srcrect.top = (int) Math.abs(0-groupbasemapy);
            srcrect.right = srcrect.left + w;
            srcrect.bottom = srcrect.top + h;
            /**绘制地图*/
            if(basebitmap!=null && basebitmap instanceof Bitmap) {
                if(isscalemode){
                    canvas.drawBitmap(basebitmap, scalematrix, paint);
                }else{
                    //绘制
                    canvas.drawBitmap(basebitmap,groupbasemapx,groupbasemapy,paint);
                }


//                if(isScale) {
//                    scalematrix = new Matrix();
//                    scalematrix.setScale(cureScale, cureScale, getWidth() / 2, getHeight() / 2);
//                    canvas.drawBitmap(basebitmap, scalematrix, paint);
//                }else{
//                    translationmatrix = new Matrix();
//                    if(scalematrix!=null)
//                        translationmatrix.postConcat(scalematrix);
//                    translationmatrix.postTranslate(groupbasemapx,groupbasemapy);
//                    canvas.drawBitmap(basebitmap, translationmatrix, paint);
//                }


            }
            /**绘制天地图的标注图层*/
            if(annbitmap!=null && annbitmap instanceof Bitmap) {
                if(isscalemode){
                    canvas.drawBitmap(annbitmap, scalematrix, paint);
                }else
                    canvas.drawBitmap(annbitmap,groupAnomapx,groupAnomapy,paint);

//                if(isScale) {
//                    scalematrix = new Matrix();
//                    scalematrix.setScale(cureScale, cureScale, getWidth() / 2, getHeight() / 2);
//                    canvas.drawBitmap(annbitmap, scalematrix, paint);
//                }else{
//                    translationmatrix = new Matrix();
//                    if(scalematrix!=null)
//                        translationmatrix.postConcat(scalematrix);
//                    translationmatrix.postTranslate(groupAnomapx,groupAnomapy);
//                    canvas.drawBitmap(annbitmap, translationmatrix, paint);
//                }

            }
            /**绘制GPS点位*/
            if(gpsBitmap!=null && gpsBitmap instanceof PubBitmap) {
//                canvas.drawBitmap(gpsBitmap.getBitmapFromResource(mContext), gpsBitmap.getX(), gpsBitmap.getY(), paint);


                Bitmap bitmap = /*BitmapFactory.decodeResource(getResources(),R.drawable.gps2)*/ gpsBitmap.getBitmapFromResource(getContext());
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                canvas.drawBitmap(bitmap, gpsBitmap.getX()-width/2, gpsBitmap.getY()-height/2, paint);
                Matrix matrix = new Matrix();
                matrix.postScale(1.0f/scale,1.0f/scale);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                canvas.drawBitmap(newBitmap,null,new Rect((int)(gpsBitmap.getX()-newBitmap.getWidth()/2),(int)(gpsBitmap.getY()-newBitmap.getHeight()/2),(int)(gpsBitmap.getX()+newBitmap.getWidth()/2),(int)(gpsBitmap.getY()+newBitmap.getHeight()/2)),paint);



//                int a = gpsBitmap.getBitmapFromResource(mContext).getWidth();
//                int b = gpsBitmap.getBitmapFromResource(mContext).getHeight();
//
//                Bitmap afterBitmap = Bitmap.createBitmap(
//                (int) (gpsBitmap.getBitmapFromResource(mContext).getWidth() ),
//                 (int) (gpsBitmap.getBitmapFromResource(mContext).getHeight() ), Bitmap.Config.ARGB_4444);
//                Canvas canvas1 = new Canvas(afterBitmap);
//                // 初始化Matrix对象
//                Matrix matrix = new Matrix();
//                // 根据传入的参数设置缩放比例
//                matrix.setScale(1.0f/scale, 1.0f/scale);
//                // 根据缩放比例，把图片draw到Canvas上
//                canvas1.drawBitmap(gpsBitmap.getBitmapFromResource(mContext), matrix,paint);
//
//                //新图缩放后的宽和高为
//                float nw = a * 1.0F / scale;
//                float nh = b * 1.0F / scale;
//
//                //新图平移，达到定位目的
//                float fw = a/2 - nw/2;
//                float fh = b - nh;
//
//                canvas.drawBitmap(afterBitmap, gpsBitmap.getX()+fw, gpsBitmap.getY()+fh, paint);
            }
            /**绘制已选点位*/
            if(hadlocationBitmap!=null && hadlocationBitmap instanceof PubBitmap) {
//                canvas.drawBitmap(hadlocationBitmap.getBitmapFromResource(mContext), hadlocationBitmap.getX(), hadlocationBitmap.getY(), paint);

                Bitmap bitmap = /*BitmapFactory.decodeResource(getResources(),R.drawable.hadlocation2)*/hadlocationBitmap.getBitmapFromResource(getContext());
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(1.0f/scale,1.0f/scale);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                canvas.drawBitmap(newBitmap,null,new Rect((int)(hadlocationBitmap.getX()-newBitmap.getWidth()/2),(int)(hadlocationBitmap.getY()-newBitmap.getHeight()/2),(int)(hadlocationBitmap.getX()+newBitmap.getWidth()/2),(int)(hadlocationBitmap.getY()+newBitmap.getHeight()/2)),paint);






//                int a = hadlocationBitmap.getBitmapFromResource(mContext).getWidth();
//                int b = hadlocationBitmap.getBitmapFromResource(mContext).getHeight();
//
//                Bitmap afterBitmap = Bitmap.createBitmap(
//                        (int) (hadlocationBitmap.getBitmapFromResource(mContext).getWidth() ),
//                        (int) (hadlocationBitmap.getBitmapFromResource(mContext).getHeight() ), Bitmap.Config.ARGB_4444);
//                Canvas canvas1 = new Canvas(afterBitmap);
//                // 初始化Matrix对象
//                Matrix matrix = new Matrix();
//                // 根据传入的参数设置缩放比例
//                matrix.setScale(1.0f/scale, 1.0f/scale);
//                // 根据缩放比例，把图片draw到Canvas上
//                canvas1.drawBitmap(hadlocationBitmap.getBitmapFromResource(mContext), matrix,paint);
//
//                //新图缩放后的宽和高为
//                float nw = a * 1.0F / scale;
//                float nh = b * 1.0F / scale;
//
//                //新图平移，达到定位目的
//                float fw = a/2 - nw/2;
//                float fh = b - nh;
//
//                canvas.drawBitmap(afterBitmap, hadlocationBitmap.getX()-nw/2, hadlocationBitmap.getY()-nh/2, paint);
            }
            /**绘制当前选点位*/
            if(locationBitmap!=null && locationBitmap instanceof PubBitmap) {
//                canvas.drawBitmap(locationBitmap.getBitmapFromResource(mContext), locationBitmap.getX(), locationBitmap.getY(), paint);

                Bitmap bitmap = /*BitmapFactory.decodeResource(getResources(),R.drawable.flaglocation2)*/locationBitmap.getBitmapFromResource(getContext());
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(1.0f/scale,1.0f/scale);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                Rect rect = new Rect((int)(locationBitmap.getX()-newBitmap.getWidth()/2),(int)(locationBitmap.getY()-newBitmap.getHeight()/2),(int)(locationBitmap.getX()+newBitmap.getWidth()/2),(int)(locationBitmap.getY()+newBitmap.getHeight()/2));
                canvas.drawBitmap(newBitmap,null,rect,paint);






//                int a = locationBitmap.getBitmapFromResource(mContext).getWidth();
//                int b = locationBitmap.getBitmapFromResource(mContext).getHeight();
//
//                Bitmap afterBitmap = Bitmap.createBitmap(
//                        (int) (locationBitmap.getBitmapFromResource(mContext).getWidth() ),
//                        (int) (locationBitmap.getBitmapFromResource(mContext).getHeight() ), Bitmap.Config.ARGB_4444);
//                Canvas canvas1 = new Canvas(afterBitmap);
//                // 初始化Matrix对象
//                Matrix matrix = new Matrix();
//                // 根据传入的参数设置缩放比例
//                matrix.setScale(1.0f/scale, 1.0f/scale);
//                // 根据缩放比例，把图片draw到Canvas上
//                canvas1.drawBitmap(locationBitmap.getBitmapFromResource(mContext), matrix,paint);
//
//                //新图缩放后的宽和高为
//                float nw = a * 1.0F / scale;
//                float nh = b * 1.0F / scale;
//
//                //新图平移，达到定位目的
//                float fw = a/4 - nw/2;
//                float fh = b - nh;
//
//                canvas.drawBitmap(afterBitmap, locationBitmap.getX()+fw, locationBitmap.getY()+fh, paint);
            }
            /**绘制部件图层*/
            if(partBitmap!=null && !partBitmap.isRecycled() && partBitmap instanceof Bitmap) {
                canvas.drawBitmap(partBitmap, 0, 0, paint);
            }

            /**绘制地图点*/
            if(mMapPoints!=null){
                MapPaint paint = null;
                for (int i = 0; i < mMapPoints.size(); i++) {
                    MapPoint m = mMapPoints.get(i);
                    paint = m.getPaint();
                    if(paint == null){
                        paint = new MapPaint();
                        paint.setColor(Color.RED);
                        m.setPaint(paint);
                    }
                    MapPoint dispoint = mMapPresenter.getDisplayLocation(m);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_home_tab_near_selected);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale(1.0f/scale,1.0f/scale);
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    canvas.drawBitmap(newBitmap,null,new Rect((int)(dispoint.getX()-newBitmap.getWidth()/2),(int)(dispoint.getY()-newBitmap.getHeight()),(int)(dispoint.getX()+newBitmap.getWidth()/2),(int)(dispoint.getY())),paint);
                }
            }

            /**绘制可点击的地图点*/
            if(markers!=null){
                for (int i = 0; i < markers.size(); i++) {
                    Marker m = markers.get(i);
                    MapPoint dispoint = mMapPresenter.getDisplayLocation(new MapPoint(m.getLatLng().getmLng(),m.getLatLng().getmLat()));
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_home_tab_near_selected);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale(1.0f/scale,1.0f/scale);
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    canvas.drawBitmap(newBitmap,null,new Rect((int)(dispoint.getX()-newBitmap.getWidth()/2),(int)(dispoint.getY()-newBitmap.getHeight()),(int)(dispoint.getX()+newBitmap.getWidth()/2),(int)(dispoint.getY())),paint);
                }
            }

            /**绘制路径*/
            if(mPathMapPoints!=null){
                if(mPathMapPoints!=null){
                    for (int i = 0; i < mPathMapPoints.size(); i++) {
                        List<MapPoint> items = mPathMapPoints.get(i);
                        if(items!=null){
                            Paint paint = null;
                            Path pa = new Path();
                            for (int ii = 0; ii < items.size(); ii++) {
                                MapPoint m = items.get(ii);
                                if(m.getPaint()!=null)
                                    paint = m.getPaint();
                                MapPoint dispoint = mMapPresenter.getDisplayLocation(m);

                                if(ii == 0)
                                    pa.moveTo((float) dispoint.getX(),(float) dispoint.getY());
                                else
                                    pa.lineTo((float) dispoint.getX(),(float) dispoint.getY());
                            }
                            if(paint == null){
                                paint = new Paint();
                                paint.setColor(Color.RED);
                                paint.setAntiAlias(true);
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(10.0F/scale);
                            }
                            canvas.drawPath(pa,paint);
                        }
                    }
                }
            }
            canvas.scale(2,2);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            isdonedraw = true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        isScale = false;

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
                beforex = event.getX();
                beforey = event.getY();
                tempbx = event.getX();
                tempby = event.getY();
                if(curgesture == Map_Gesture.PARTQUERY){
                    mMapPresenter.getLayersAttr(mMapPresenter.getLocation(new MapPoint(event.getX(),event.getY())));
                    curgesture = Map_Gesture.TRANSLATION;//操作地图完成恢复动作指令
                }else if(curgesture == Map_Gesture.CHOOSELOCATION){
                    if(isPartChoose){
                        mMapPresenter.getLayersAttr(mMapPresenter.getLocation(new MapPoint(event.getX(),event.getY())));
                    }
//                    locationBitmap = new PubBitmap(R.drawable.flaglocation2);
                    Log.e("选图点击",event.getX()+"@@@@@@"+event.getY());
                    location = mMapPresenter.getLocation(new MapPoint(event.getX(),event.getY()));
//                    locationBitmap.setX(event.getX());
//                    locationBitmap.setY(event.getY()-locationBitmap.getBitmapFromResource(mContext).getHeight());
//                    locationBitmap.setY(event.getY());
                    curgesture = Map_Gesture.TRANSLATION;//操作地图完成恢复动作指令
//                    MapPoint ll = mMapPresenter.getLocation(new MapPoint(event.getX(),event.getY()));
//                    Log.e("选择的位置",ll.getX()+"@@@"+ll.getY());
                    postInvalidate();
                }

                LatLng latLng = new LatLng(event.getX(),event.getY());
                if(markers!=null){
                    for (int i = 0; i < markers.size(); i++) {
                        Marker temp = markers.get(i);
                        MapPoint dispoint = mMapPresenter.getDisplayLocation(new MapPoint(temp.getLatLng().getmLng(),temp.getLatLng().getmLat()));
                        Marker marker = new Marker(new LatLng(dispoint.getX(),dispoint.getY()));
                        marker.setTitle(temp.getTitle());
                        marker.setContent(temp.getContent());
                        marker.isClick(latLng,getContext());
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                //双指在滑动
                if (downfingers == 1) {
                    float curdistance = spacing(event);
                    if(Math.abs(curdistance - oldDist) > 20f){
                        isscalemode = true;
                        float scale = curdistance / oldDist;//放大倍数
                        Log.e("scale=", String.valueOf(scale));
                        zoom(scale);
                    }

                }else if(downfingers == 0){
                    afterX = event.getX();
                    afterY = event.getY();
                    curgesture = Map_Gesture.TRANSLATION;

                    float dx = (afterX - tempbx);
                    float dy = (afterY - tempby);
                    if(isScale){
                        dx *= cureScale;
                        dy *= cureScale;
                    }
                    // 移动底图
                    groupbasemapx = groupbasemapx + dx;
                    groupbasemapy = groupbasemapy + dy;
                    // 移动标注
                    groupAnomapx = groupAnomapx + dx;
                    groupAnomapy = groupAnomapy + dy;
                    //gps位图
                    if(gpsBitmap!=null){
                        gpsBitmap.setX(gpsBitmap.getX()+dx);
                        gpsBitmap.setY(gpsBitmap.getY()+dy);
                    }
                    //历史记录点
                    if(hadlocationBitmap!=null){
                        hadlocationBitmap.setX(hadlocationBitmap.getX()+dx);
                        hadlocationBitmap.setY(hadlocationBitmap.getY()+dy);
                    }
                    //当前记录点
                    if(locationBitmap!=null){
                        locationBitmap.setX(locationBitmap.getX()+dx);
                        locationBitmap.setY(locationBitmap.getY()+dy);
                        Log.e("move","x="+locationBitmap.getX()+":y="+locationBitmap.getY());
                    }
                    tempbx = afterX;
                    tempby = afterY;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isscalemode){

                }else{
                    if(curgesture == Map_Gesture.TRANSLATION){
                        translation(beforex,beforey,event.getX(),event.getY());
                    }
                }
                downfingers = 0;
                twofingerslastscale = 0;
                isscalemode  = false;
                curgesture = Map_Gesture.TRANSLATION;
                break;
                //多指中有一只离开屏幕触发
            case MotionEvent.ACTION_POINTER_UP:
                --downfingers;
                if(downfingers == 0) {
                    isscalemode = false;
                    curgesture = Map_Gesture.ZOOMIN;
                    //放大地图
                    if(twofingerslastscale > 1){
                        if(mMapPresenter.getCurMapLevel()+(int)twofingerslastscale >= mMapPresenter.getMapMaxLevel()) {
                            return;
                        }
                        mMapPresenter.zoomInBylevel((int) twofingerslastscale);
                    }else{
                        if(mMapPresenter.getCurMapLevel()-(int)(1/twofingerslastscale) <= mMapPresenter.getMapMinLevel()) {
                            return;
                        }
                        curgesture = Map_Gesture.ZOOMOUT;
                        mMapPresenter.zoomOutBylevel((int) (1/twofingerslastscale));
                    }
                }
                break;
                //至少第二指按压屏幕触发
            case MotionEvent.ACTION_POINTER_DOWN:
                ++downfingers;
                isscalemode = true;
                if(downfingers == 1) {
                    oldDist = spacing(event);
                    if(oldDist > minDistance){//避免手指上有两个茧
                        mMidPt = mid(event);//计算两点之间中心点的位置
                    }
                }
                break;
        }
    }

    private void zoom(float f) {
        twofingerslastscale = f;
        if(f > 1){
            if(mMapPresenter.getCurMapLevel()+(int)f >= mMapPresenter.getMapMaxLevel())
                return;
        }else if(f < 1){
            if(mMapPresenter.getCurMapLevel()-(int)(1/f) <= mMapPresenter.getMapMinLevel())
                return;
        }

        setScaleType(ImageView.ScaleType.MATRIX);
        scalematrix.set(getImageMatrix());
        f = 0.5f + f;
        scalematrix.postScale(f,f, screenWidth/2, screenHeight/2);
        setImageMatrix(scalematrix);

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



    /**
     * 调用imageview必须先进行初始化
     * @param context
     */
    @Override
    public void initMap(Context context,String basicLayerUrl,String partLayerUrl,String annotationUrl,MapType mapType) {
        mContext = context;

        mProgressBar = WaitProgressDialog.createDialog(context);
        mProgressBar.setMessage(null);
        mProgressBar.setCanceledOnTouchOutside(false);

        mMapPresenter = new MapPresenter(this,context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        //获取dpi
        int dpi = dm.densityDpi;

        setScaleType(ScaleType.MATRIX);
        scale = dpi / 160;
        if(scale < 1)
            scale = 1;
        setScaleX(scale);
        setScaleY(scale);

        //直接将像素作为显示
        this.screenHeight = dm.heightPixels;
        this.screenWidth = dm.widthPixels;


        if(mapType == MapType.TIANDI || mapType == MapType.OPENLAYER)
            mMapPresenter.setMapExtent(mMapExtent);
        /**天地图有省、市级的区分，请使用省市和并字符串的方式来传输，分隔用@#@,地图和标注*/
        mMapPresenter.initMapInfo(basicLayerUrl,partLayerUrl,annotationUrl,mapType);

    }

    /**
     * 调用imageview必须先进行初始化
     * @param context
     */
    @Override
    public void initMap(Context context,String basicLayerUrl,String partLayerUrl,String annotationUrl,MapType mapType,MapPoint centerpoint) {
        mContext = context;

        mProgressBar = WaitProgressDialog.createDialog(context);
        mProgressBar.setMessage(null);
        mProgressBar.setCanceledOnTouchOutside(false);

        mMapPresenter = new MapPresenter(this,context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        //获取dpi
        int dpi = dm.densityDpi;

        setScaleType(ScaleType.MATRIX);
        scale = dpi / 160;
        if(scale < 1)
            scale = 1;
        setScaleX(scale);
        setScaleY(scale);

        //直接将像素作为显示
        this.screenHeight = dm.heightPixels;
        this.screenWidth = dm.widthPixels;


        if(mapType == MapType.TIANDI || mapType == MapType.OPENLAYER)
            mMapPresenter.setMapExtent(mMapExtent);
        /**天地图有省、市级的区分，请使用省市和并字符串的方式来传输，分隔用@#@,地图和标注*/
        if(centerpoint != null)
            mMapPresenter.initMapInfo(basicLayerUrl,partLayerUrl,annotationUrl,mapType,centerpoint);
        else
            mMapPresenter.initMapInfo(basicLayerUrl,partLayerUrl,annotationUrl,mapType);

    }

    @Override
    public void initMap(Context context, String basicLayerUrl, String partLayerUrl, String annotationUrl, MapType mapType, MapPoint centerpoint, int maxLevel) {
        mContext = context;

        mProgressBar = WaitProgressDialog.createDialog(context);
        mProgressBar.setMessage(null);
        mProgressBar.setCanceledOnTouchOutside(false);

        mMapPresenter = new MapPresenter(this,context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        //获取dpi
        int dpi = dm.densityDpi;

        setScaleType(ScaleType.MATRIX);
        scale = dpi / 160;
        if(scale < 1)
            scale = 1;
        setScaleX(scale);
        setScaleY(scale);

        //直接将像素作为显示
        this.screenHeight = dm.heightPixels;
        this.screenWidth = dm.widthPixels;


        if(mapType == MapType.TIANDI || mapType == MapType.OPENLAYER) {
            mMapPresenter.setMapExtent(mMapExtent);
            mMapPresenter.setMapMaxLevel(maxLevel);
            mMapPresenter.setMapMinLevel(14);
            mMapPresenter.setCurMapLevel(15);
        }
        /**天地图有省、市级的区分，请使用省市和并字符串的方式来传输，分隔用@#@,地图和标注*/
        if(centerpoint != null)
            mMapPresenter.initMapInfo(basicLayerUrl,partLayerUrl,annotationUrl,mapType,centerpoint);
        else
            mMapPresenter.initMapInfo(basicLayerUrl,partLayerUrl,annotationUrl,mapType);
    }

    /**
     * 设置地图的最小比例尺
     * @param mapMinLevel
     */
    public void setMapMinLevel(int mapMinLevel){
        if(mapMinLevel < 0)
            return;
        /**设置地图显示的最小比例尺，默认设置为16*/
        mMapPresenter.setMapMinLevel(mapMinLevel);
    }

    /**
     * 设置当前比例尺
     * @param mapLevel
     */
    public void setCurMapLevel(int mapLevel){
        if(mapLevel > mMapPresenter.getMapMaxLevel())
            return;

        mMapPresenter.setCurMapLevel(mapLevel);
    }

    /**
     * 设置最大比例尺,设为16时就采用国家级
     */
    public void setMapMaxLevel(int mapMaxLevel){
        if(mapMaxLevel < 19)
            return;
        mMapPresenter.setMapMaxLevel(mapMaxLevel);
    }

    @Override
    public void zoomIn() {
        if (mMapPresenter.getCurMapLevel() < mMapPresenter.getMapMaxLevel()){
            curgesture = Map_Gesture.ZOOMIN;
            if(cureScale < midScale)
                cureScale = midScale;
            else if(cureScale > maxScale)
                cureScale = maxScale;
            isScale = true;
            mMapPresenter.zoomIn();
        }else{
            ToastWithImg toastWithImg = new ToastWithImg(mContext);
            toastWithImg.showToast("已是最大比例尺");
        }
    }

    @Override
    public void zoomOut() {
        if (mMapPresenter.getCurMapLevel() > mMapPresenter.getMapMinLevel()){
            curgesture = Map_Gesture.ZOOMOUT;
            mMapPresenter.zoomOut();
            if(cureScale == maxScale)
                cureScale = midScale;
            else if(cureScale == midScale)
                cureScale = basicScale;
            isScale = true;
        }else{
            ToastWithImg toastWithImg = new ToastWithImg(mContext);
            toastWithImg.showToast("已是最小比例尺");
        }
    }

    @Override
    public void translation(float beforeX,float beforeY,float afterX,float afterY) {
        float dx = afterX - beforeX;
        float dy = afterY - beforeY;
        mMapPresenter.translation(dx,dy);
    }

    @Override
    public void fullMap() {
        mMapPresenter.fullMap();
    }

    /**展示部件图层选择框*/
    @Override
    public void showLayers() {
        mapLayers = getPartLayers();
        if(mapLayers!=null && mapLayers.length > 0){
            showLayerControlDialog(mapLayers);
        }
    }

    /**
     * 获取选中的图件图层中的部件属性
     * @return
     */
    @Override
    public ArrayList<PartsObjectAttributes> getLayersAttr() {
        return null;
    }

    /**
     * 定位网格，提供给金华金开区使用
     * @param gridpoint
     */
    public void showGridArea(MapPoint gridpoint){
        if(gridpoint != null){

            MapPoint dispoint = mMapPresenter.getDisplayLocation(gridpoint);

            translation((float) dispoint.getX(),(float) dispoint.getY(),getWidth()/2,getHeight()/2);
        }
    }

    /**
     * 根据经纬度返回绘制的图标
     * @param mapPoint
     * @return
     */
    public PubBitmap getPubBitMap(MapPoint mapPoint,MapViewTest.PointType pointType){
        PubBitmap pubBitmap = null;
        MapPoint localpoint = mMapPresenter.getDisplayLocation(mapPoint);
        Log.e("屏幕坐标",localpoint.getX()+"@@@@@@"+localpoint.getY());
        if(pointType == MapViewTest.PointType.GPS){
            pubBitmap = new PubBitmap(R.drawable.gps6);
        }else if(pointType == MapViewTest.PointType.CHOOSEPOSITION){
            pubBitmap = new PubBitmap(R.drawable.flaglocation2);
        }else if(pointType == MapViewTest.PointType.HADPOSITION){
            pubBitmap = new PubBitmap(R.drawable.hadlocation3);
        }


        //屏幕
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        //应用区域
        Rect outRect1 = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        int statusBar = dm.heightPixels - outRect1.height();


//        Bitmap bitmap = pubBitmap.getBitmapFromResource(getContext());
//        pubBitmap.setX((float) (localpoint.getX()-bitmap.getWidth()/2.0));
//        pubBitmap.setY((float) (localpoint.getY()-statusBar/scale-bitmap.getHeight()/2.0));
        pubBitmap.setX((float) (localpoint.getX()));
        pubBitmap.setY((float) (localpoint.getY()));

        return pubBitmap;
    }



    /**
     * 加载GPS图标资源
     * @param gpspoint gps位置点
     */
    @Override
    public void showGpsLocation(MapPoint gpspoint) {

        if(isInMap(gpspoint)){
            curgesture = Map_Gesture.GPS;
            mMapPresenter.showGpsLocation(gpspoint);
            this.gpsPoint = gpspoint;
//            gpsBitmap = new PubBitmap(R.drawable.gps);
//            MapPoint dispoint = mMapPresenter.getDisplayLocation(gpspoint);
//            gpsBitmap.setX((float) dispoint.getX()-gpsBitmap.getBitmapFromResource(mContext).getWidth()/2);
//            gpsBitmap.setY((float) dispoint.getY()-gpsBitmap.getBitmapFromResource(mContext).getHeight());
//
//            if(hadlocationPoint!=null){
//                hadlocationBitmap = new PubBitmap(R.drawable.hadlocation2);
//                MapPoint dispointlocation = mMapPresenter.getDisplayLocation(hadlocationPoint);
//                hadlocationBitmap.setX((float) dispointlocation.getX());
//                hadlocationBitmap.setY((float) dispointlocation.getY()-hadlocationBitmap.getBitmapFromResource(mContext).getHeight());
//            }

        }else{
            gpsBitmap = null;
            ToastUtils.showShort(mContext,"当前坐标不在本市区范围内");
        }

//        postInvalidate();

    }

    private MapLayer[] getPartLayers(){
        return mMapPresenter.getPartLayers();
    }

    /**
     * 已选位置资源信息
     * @param hadLocation 已选位置点
     */
    @Override
    public void showHadLocation(MapPoint hadLocation) {

        if(isInMap(hadLocation)){
            mMapPresenter.showHadLocation(hadLocation);

            this.hadlocationPoint = hadLocation;
//            hadlocationBitmap = new PubBitmap(R.drawable.hadlocation2);
//            MapPoint dispoint = mMapPresenter.getDisplayLocation(hadLocation);
//            hadlocationBitmap.setX((float) dispoint.getX());
//            hadlocationBitmap.setY((float) dispoint.getY()-hadlocationBitmap.getBitmapFromResource(mContext).getHeight());
//
//            if(gpsPoint!=null){
//                gpsBitmap = new PubBitmap(R.drawable.gps);
//                MapPoint dispointgps = mMapPresenter.getDisplayLocation(gpsPoint);
//                gpsBitmap.setX((float) dispointgps.getX()-gpsBitmap.getBitmapFromResource(mContext).getWidth()/2);
//                gpsBitmap.setY((float) dispointgps.getY()-gpsBitmap.getBitmapFromResource(mContext).getHeight());
//            }

        }else{
            hadlocationBitmap = null;
        }

        postInvalidate();
    }

    /**判断点位是否在中国，超出范围就弃用*/
    private boolean isInMap(MapPoint mapPoint){
        return  mMapPresenter.isOverMapArea(mapPoint);
    }

    @Override
    public int getCurMapLevel() {
        return mMapPresenter.getCurMapLevel();
    }

    @Override
    public int getMaxMapLevel() {
        return mMapPresenter.getMapMaxLevel();
    }

    @Override
    public int getMinMapLevel() {
        return mMapPresenter.getMapMinLevel();
    }

    @Override
    public void showProgressbar() {
        if(mProgressBar!=null && !mProgressBar.isShowing())
            mProgressBar.show();
    }

    @Override
    public void hideProgressbar() {
        if(mProgressBar!=null && mProgressBar.isShowing())
            mProgressBar.dismiss();
    }

    @Override
    public void showMsg(String msg) {
        ToastWithImg toastWithImg = new ToastWithImg(mContext);
        toastWithImg.showToast(null,msg);
    }

    @Override
    public int getScreenWidth() {
        return this.screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return this.screenHeight;
    }

    /**
     * 回调刷新底图
     * @param bitmap 底图bitmap
     * @param leftTopX 屏幕left坐标
     * @param leftTopY 屏幕top坐标
     */
    @Override
    public void refreshView(Bitmap bitmap, float leftTopX, float leftTopY) {
        basebitmap = bitmap;
        groupbasemapx = leftTopX;
        groupbasemapy = leftTopY;
        rightspace = leftTopX+basebitmap.getWidth()-screenWidth;
        bottomspace = leftTopY+basebitmap.getHeight()-screenHeight;
        leftspce = Math.abs(groupbasemapx);
        topspace = Math.abs(groupbasemapy);
        Log.e("底图范围","右间距:"+(rightspace)+":下间距"+(bottomspace));

        /**避免平移时刷地图*/
        if(hadlocationPoint!=null){
            hadlocationBitmap = getPubBitMap(hadlocationPoint,MapViewTest.PointType.HADPOSITION);
//            MapPoint dispoint = mMapPresenter.getDisplayLocation(hadlocationPoint);
//            hadlocationBitmap.setX((float) dispoint.getX());
//            hadlocationBitmap.setY((float) dispoint.getY()-hadlocationBitmap.getBitmapFromResource(mContext).getHeight());
        }

        if(location!=null){
            locationBitmap = getPubBitMap(location,MapViewTest.PointType.CHOOSEPOSITION);
//            MapPoint dispoint = mMapPresenter.getDisplayLocation(location);
//            locationBitmap.setX((float) dispoint.getX());
////            locationBitmap.setY((float) dispoint.getY()-locationBitmap.getBitmapFromResource(mContext).getHeight());
//            locationBitmap.setY((float) dispoint.getY());
//            Log.e(curgesture+"","x="+locationBitmap.getX()+":y="+locationBitmap.getY());
        }

        if(gpsPoint!=null){
            gpsBitmap = getPubBitMap(gpsPoint,MapViewTest.PointType.GPS);
//            MapPoint dispointgps = mMapPresenter.getDisplayLocation(gpsPoint);
//            gpsBitmap.setX((float) dispointgps.getX()-gpsBitmap.getBitmapFromResource(mContext).getWidth()/2);
//            gpsBitmap.setY((float) dispointgps.getY()-gpsBitmap.getBitmapFromResource(mContext).getHeight());
        }
        postInvalidate();

        curgesture = Map_Gesture.TRANSLATION;

        if(mapLayers!=null && mapLayers.length>0)
            showPartLayers(mapLayers);

        if(imapOnLoadFinishListener != null) {
            imapOnLoadFinishListener.onLoadFinish();
            imapOnLoadFinishListener = null;
        }
    }

    /**
     * 回调刷新天地图的标注图层
     * @param bitmap
     * @param leftTopX
     * @param leftTopY
     */
    @Override
    public void refreshAnnView(Bitmap bitmap, float leftTopX, float leftTopY) {
        annbitmap = bitmap;
        groupAnomapx = leftTopX;
        groupAnomapy = leftTopY;
        postInvalidate();


        drawPartView(tempUrl);

    }

    /**
     * 金开区绘制部件图层需要
     * @param url 部件图层地址
     */
    public synchronized void drawPartView(String url){

        if(TextUtils.isEmpty(url.trim())){
            return;
        }

        MapPoint mapPointlb = mMapPresenter.getLocation(new MapPoint(0,getScreenHeight()));
        MapPoint mapPointrt = mMapPresenter.getLocation(new MapPoint(getScreenWidth(),0));
        url += "&WIDTH="+getScreenWidth()+"&HEIGHT="+getScreenHeight()+"&BBOX="+mapPointlb.getX()+","+mapPointlb.getY()+","+mapPointrt.getX()+","+mapPointrt.getY();


        AsyncBaseMap asyncBaseMap = new AsyncBaseMap();
        asyncBaseMap.execute(url);
    }

    public String getTempUrl() {
        return tempUrl;
    }

    public void setTempUrl(String tempUrl) {
        this.tempUrl = tempUrl;

        drawPartView(tempUrl);
    }

    /**
     * 采用asyntask任务来获取地图切片，请求的张数体量不是很大，就采用轻量级线程服务
     */
    public class AsyncBaseMap extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = params[0];
                partBitmap = BitMapUtils.getURLBitmap2(url);
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 执行完毕，更新UI
            if (result.equals("0")) {
                postInvalidate();
            }

        }
    }

    /**
     * 绘制部件图层
     * @param bitmap 部件图层以屏幕左上0,0点开始绘制
     */
    @Override
    public void refreshPartView(Bitmap bitmap) {
        if(bitmap !=null && !bitmap.isRecycled() && bitmap instanceof Bitmap)
            partBitmap = bitmap;
        postInvalidate();
    }

    @Override
    public void getPartLayersInfo(List<PartsObjectAttributes> partsObjectAttributes) {
        showPartsInfo(partsObjectAttributes);
    }

    @Override
    public void setMapArea(MapExtent mapArea) {
        mMapExtent = mapArea;
    }

    /**
     * 弹出部件图层对话框
     * @param partMapArray 部件图层数组
     */
    private void showLayerControlDialog(final MapLayer[] partMapArray) {
        synchronized (this) {
            if (partsLayerShowBuilder != null) {
                return;
            }

            isPartChoose = false;
            try {
                if (partMapArray != null && partMapArray.length > 0) {
                    final String[] names = new String[partMapArray.length];
                    final boolean[] visibles = new boolean[partMapArray.length];
                    for (int i = 0; i < partMapArray.length; i++) {
                        names[i] = partMapArray[i].getName();
                        visibles[i] = partMapArray[i].isVisible();
                    }
                    partsLayerShowBuilder = new AlertDialog.Builder(mContext);
                    partsLayerShowBuilder
                            .setPositiveButton("-确定-",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                            for (MapLayer layer : partMapArray) {
                                                if (layer.isVisible()) {
                                                   isPartChoose = true;
                                                    break;
                                                }
                                            }
                                            /**展示选中的部件图层，部件图层是实时从服务器端获取，不做缓冲处理*/
                                            showPartLayers(partMapArray);
                                        }
                                    })
                            .setMultiChoiceItems(
                                    names,
                                    visibles,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which, boolean isChecked) {
                                            /**选中的图层将属性置为可见*/
                                            partMapArray[which]
                                                        .setVisible(isChecked);

                                        }
                                    }).create();
                    partsLayerShowBuilder
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                        dialog.dismiss();

                                }
                            });
                    partsLayerShowBuilder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                partsLayerShowBuilder = null;
            }
        }
    }

    /**
     * 获取对应的图件图层图片信息
     */
    private void showPartLayers(MapLayer[] partMapArray) {
        mMapPresenter.getPartBitmap(partMapArray);
    }

    /**
     * 展示要查询的部件图层属性
     */
    private void showPartsInfo(final List<PartsObjectAttributes> partsObjectAttributes){
        synchronized (this) {
            if (partsLayerShowBuilder != null) {
                return;
            }
            try {
                if (partsObjectAttributes != null && partsObjectAttributes.size() > 0) {
                    String[] infos = new String[partsObjectAttributes.size()];
                    for (int i = 0; i < partsObjectAttributes.size(); i++) {
                        PartsObjectAttributes partsObjectAttribute = partsObjectAttributes.get(i);
                        Map<String, String> attributeMap = partsObjectAttribute
                                .getAttributeMap();
                        String name = PartsObjectAttributes.mapKey[1] != null?PartsObjectAttributes.mapKey[1].toUpperCase():"";
                        String ObjName = attributeMap
                                .get(name);

                        String code = PartsObjectAttributes.mapKey[0]!=null?PartsObjectAttributes.mapKey[0].toUpperCase():"";
                        String ObjCode = attributeMap
                                .get(code);

                        String state = PartsObjectAttributes.mapKey[2]!=null?PartsObjectAttributes.mapKey[2].toUpperCase():"";
                        String ObjState = attributeMap
                                .get(state);

                        String depName = PartsObjectAttributes.mapKey[3]!=null?PartsObjectAttributes.mapKey[3].toUpperCase():"";
                        String CDepName = attributeMap.get(depName);
                        if(CDepName == null){
                            depName = PartsObjectAttributes.mapKey[4]!=null?PartsObjectAttributes.mapKey[4].toUpperCase():"";
                            CDepName = attributeMap.get(depName);
                        }

                        String resultInfo = "";
                        resultInfo = "名称" + "：" + (ObjName==null?"":ObjName)  + "\n";
                        resultInfo += "编号" + "：" + (ObjCode==null?"":ObjCode)  + "\n";
                        resultInfo += "状态" + "：" + (ObjState==null?"":ObjState)  + "\n";
                        resultInfo += "主管" + "：" + (CDepName==null?"":CDepName)  + "\n";

                        infos[i] = resultInfo;
                    }
                    partsLayerShowBuilder = new AlertDialog.Builder(mContext);
                    partsLayerShowBuilder
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();

                                        }
                                    })
                            .setSingleChoiceItems(infos, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Map<String, String> attributeMap = partsObjectAttributes.get(which)
                                            .getAttributeMap();
                                    String code = PartsObjectAttributes.mapKey[0]!=null?PartsObjectAttributes.mapKey[0].toUpperCase():"";
                                    returnPartCode = attributeMap.get(code);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
                    partsLayerShowBuilder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                partsLayerShowBuilder = null;
            }
        }
    }
}
