package cn.com.hesc.maplibrary.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.hesc.maplibrary.PubGisUtil;
import cn.com.hesc.maplibrary.view.MapView;
import cn.com.hesc.tools.SdcardInfo;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: PubGisProcess
 * Description: 地理信息处理主类
 * Author: liujunlin
 * Date: 2016-04-12 16:37
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public abstract class PubGisProcess {

    private static String Tag = "--PubGisProcess--";

    /**初始化范围（一般同全图时范围）*/
    protected MapExtent initMapExtent;
    /**当前范围*/
    protected MapExtent currentMapExtent;
    /**底图比例尺（分本地地图和天地图，数值不同）*/
    public double[] resolutions;
    /**地图比例尺级别数，对应resolutions的length*/
    private int mapScales;
    /**切片参数 初始化点，为地图左下点*/
    public double xorigin;
    public double yorigin;
    /**切片的长度*/
    public int tileCols;
    /**切片的宽度*/
    public int tileRows;
    /**当前比例下，全图的切片范围，最小行*/
    protected long minRow;
    /**当前比例下，全图的切片范围，最大行*/
    protected long maxRow;
    /**当前比例下，全图的切片范围，最小列*/
    protected long minClo;
    /**当前比例下，全图的切片范围，最大列*/
    protected long maxClo;
    /**当前地图比例尺(天地图比例尺是真实比例尺-1，是为了适应程序里的数组下标，arcgis本地地图和比例尺一致)*/
    protected int currentLevel;
    /**地图最大比例尺*/
    protected int maxLevel;
    /**地图最小比例尺*/
    protected int minLevel;
    /**底图切片文件，本地存放目录*/
    protected String cachePath;
    /**保存的切片修改后缀名*/
    public final String mapdata_suffix = ".tmp";
    /**瓦片列数*/
    private int columns;
    /**瓦片行数*/
    private int rows;
    /**需要展示的图层名称*/
    protected String dislayername;
    /**上下文关系*/
    protected Context context;
    /**记录已选择的位置点信息*/
    public MapPoint hadlocationPoint;
    /**GPS点信息*/
    protected MapPoint gpsPoint;
    /**操作地图的手势动作*/
    private MapView.Map_Gesture mMap_gesture;
    /**屏幕宽度*/
    protected int screenWidth;
    /**屏幕高度*/
    protected int screenHeight;
    /**是否是天地图*/
    protected boolean istiandi = false;
    /**部件信息*/
    protected MapLayer[] partMapArray;
    /**地图地图服务*/
    protected String basicInfo;
    /**地图部件服务*/
    protected String partInfo;
    /**标注服务*/
    protected String annotationInfo;
    /**针对天地图，设置地图范围*/
    private MapExtent mMapExtent;

    /**初始化底图信息*/
    protected abstract void initBaseInfo();
    /**初始化部件信息*/
    protected abstract void initPartInfo();
    /**加载部件图层*/
    protected abstract void getPartLayersBitmap(MapLayer[] partLayers);
    /**地图加载完成的回调*/
    protected OnBaseMapFinishListen mOnBaseMapFinishListen;
    /**
     * 查询容差
     */
    protected int tolerance = 20;
    private long index;

    /**
     * 标注图片url获取 只适应于天地图
     * @param row 切片行号
     * @param column 切片列号
     * @return
     */
    protected abstract String getAnnotaTionUrl(long row, long column);
    /**
     * 底图图片url获取
     * @param row 切片行号
     * @param column 切片列号
     * @return
     */
    protected abstract String getBaseMapUrl(long row, long column);

    /**
     * 发送http请求，查询对应的部件URL
     * @param locatPoint 点击的屏幕坐标
     * @return
     */
    protected abstract List<PartsObjectAttributes> queryPartAttributeInfo(MapPoint locatPoint);

    /**采用task拼接底图*/
    private AsyncBaseMap asyncBaseMap = null;

    /**采用task获取部件属性*/
    private AsyncPartAttributeInfo mAsyncPartAttributeInfo = null;

    /**获取切片保存的位置*/
    public String getCachePath() {
        return cachePath;
    }
    /**设置切片保存的位置*/
    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public void setGpsPoint(MapPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }

    public void setHadlocationPoint(MapPoint hadlocationPoint) {
        this.hadlocationPoint = hadlocationPoint;
    }

    /**设置地图操作的手势*/
    public void setMap_gesture(MapView.Map_Gesture map_gesture) {
        mMap_gesture = map_gesture;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setOnBaseMapFinishListen(OnBaseMapFinishListen onBaseMapFinishListen) {
        mOnBaseMapFinishListen = onBaseMapFinishListen;
    }

    public boolean istiandi() {
        return istiandi;
    }

    public void setIstiandi(boolean istiandi) {
        this.istiandi = istiandi;
    }

    public String getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

    public String getPartInfo() {
        return partInfo;
    }

    public void setPartInfo(String partInfo) {
        this.partInfo = partInfo;
    }

    public String getAnnotationInfo() {
        return annotationInfo;
    }

    public void setAnnotationInfo(String annotationInfo) {
        this.annotationInfo = annotationInfo;
    }

    public MapExtent getMapExtent() {
        return mMapExtent;
    }

    public void setMapExtent(MapExtent mapExtent) {
        mMapExtent = mapExtent;
        this.currentMapExtent = mapExtent;
    }

    public void setInitMapExtent(MapExtent mapExtent){
        MapExtent mapExtent1 = new MapExtent();
        mapExtent1.setMaxX(mapExtent.getMaxX());
        mapExtent1.setMaxY(mapExtent.getMaxY());
        mapExtent1.setMinX(mapExtent.getMinX());
        mapExtent1.setMinY(mapExtent.getMinY());
        this.initMapExtent = mapExtent1;
    }

    /**
     * 根据当前点调整地图展示范围
     * @param centerpoint 当前点设置为中心点
     */
    public void changeMapExtent(MapPoint centerpoint) {

        Log.e("changeMapExtent",System.currentTimeMillis()+"");

        cachePath = SdcardInfo.getInstance().getSdcardpath() + "/Layer_Cache/Layers/viewGroup";

        try {
            // 将中点坐标转换为地理坐标，即理解为将屏幕适配到地图范围情况下，屏幕的中点坐标
            double centerX = centerpoint.getX();
            double centerY = centerpoint.getY();
            // 将centerpoint移动到屏幕中间所截取的地图的范围，即是更新currentMapExtent
            this.currentMapExtent.setMaxX(centerX
                    + this.resolutions[this.currentLevel] * screenWidth / 2);
            this.currentMapExtent.setMinX(centerX
                    - this.resolutions[this.currentLevel] * screenWidth / 2);
            this.currentMapExtent.setMaxY(centerY
                    + this.resolutions[this.currentLevel] * screenHeight / 2);
            this.currentMapExtent.setMinY(centerY
                    - this.resolutions[this.currentLevel] * screenHeight / 2);

            /*移动后的地图中心点*/
            double afterChangCenterX = (currentMapExtent.getMaxX() + currentMapExtent
                    .getMinX()) / 2;
            /*移动后的地图中心点*/
            double afterChangCenterY = (currentMapExtent.getMaxY() + currentMapExtent
                    .getMinY()) / 2;
            MapPoint centerPoint = new MapPoint(afterChangCenterX,
                    afterChangCenterY);
            /*设置新的中心点*/
            this.currentMapExtent.setCenterPoint(centerPoint);

            // 保证地图切片在介于最小和最大范围之内
            this.minRow = PubGisUtil.calculateCacheNumber(this.yorigin,
                    this.initMapExtent.getMaxY(),
                    (this.tileCols * this.resolutions[this.currentLevel]));
            this.minClo = PubGisUtil.calculateCacheNumber(this.xorigin,
                    this.initMapExtent.getMinX(),
                    (this.tileRows * this.resolutions[this.currentLevel]));
            this.maxRow = PubGisUtil.calculateCacheNumber(this.yorigin,
                    this.initMapExtent.getMinY(),
                    (this.tileCols * this.resolutions[this.currentLevel]));
            this.maxClo = PubGisUtil.calculateCacheNumber(this.xorigin,
                    this.initMapExtent.getMaxX(),
                    (this.tileRows * this.resolutions[this.currentLevel]));

            loadBaseMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据地图范围拼接底图
     * 采用asynctask方式来获取地图信息
     */
    private void loadBaseMap() {
        try {
            synchronized (this) {
                if (asyncBaseMap != null && !asyncBaseMap.isCancelled()) {
                    asyncBaseMap.cancel(true);
                    asyncBaseMap = null;
                }
                asyncBaseMap = new AsyncBaseMap();
                asyncBaseMap.execute("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 采用asyntask任务来获取地图切片，请求的张数体量不是很大，就采用轻量级线程服务
     */
    public class AsyncBaseMap extends AsyncTask<String, String, Bitmap[]> {
        int errorCode[] = { -1 };
        ArrayList<PubBitmap> pubBitmapNewListTemp = null;//底图切片列表
        ArrayList<PubBitmap> pubBitmapNewMarkListTemp = null;//天地图标注切片列表

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap[] doInBackground(String... params) {

            Bitmap[] bitmaps = new Bitmap[2];

            try {
                pubBitmapNewListTemp = getBaseMap(false);
                if(pubBitmapNewListTemp!=null && pubBitmapNewListTemp.size()>0){
                    bitmaps[0] = combineBitmaps(columns,rows,pubBitmapNewListTemp);
                }
                if(istiandi()) {
                    pubBitmapNewMarkListTemp = getBaseMap(true);
                    if(pubBitmapNewMarkListTemp!=null&&pubBitmapNewMarkListTemp.size()>0){
                        bitmaps[1] = combineBitmaps(columns,rows,pubBitmapNewMarkListTemp);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return bitmaps;
        }

        @Override
        protected void onPostExecute(Bitmap[] result) {
            super.onPostExecute(result);
            // 执行完毕，更新UI
            if(null!=result){

                if(null != result[0]){
                    PubBitmap map = pubBitmapNewListTemp.get(0);
                    mOnBaseMapFinishListen.OnSuccess(result[0],map.getX(),map.getY());
                }
                if(null != result[1]){
                    PubBitmap map = pubBitmapNewMarkListTemp.get(0);
                    mOnBaseMapFinishListen.OnAnntionSuccees(result[1],map.getX(),map.getY());
                }

            }else{
                mOnBaseMapFinishListen.OnErrorMsg("地图加载失败");
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            asyncBaseMap = null;
        }
    };

    /**
     * 获取对应的切片
     * @istiandi true 获取天地图的标注图层切片，false获取普通的切片
     * @return
     */
    public ArrayList<PubBitmap> getBaseMap(boolean istiandi) {
        ArrayList<PubBitmap> baseMaps = new ArrayList<PubBitmap>();
        try {
            // PubBitmap用于记录图片信息的类
            PubBitmap pubBitmap = null;
            float leftTopX = 0;
            float leftTopY = 0;
            // 得到开始行
            long startRow = PubGisUtil.calculateCacheNumber(
                    // 参考点的y地理坐标
                    this.yorigin,
                    // 当前底图范围的最大y地理坐标
                    this.currentMapExtent.getMaxY(),
                    // tileCols(像素) 乘以 当前的缩放水平对应的底图比例尺Scale的值，得到一张图片表示的实际距离
                    (this.tileCols * this.resolutions[this.currentLevel]));

            //有可能地图数据问题导致原点超出地图范围
            if(Math.abs(yorigin)< Math.abs(this.currentMapExtent.getMaxY()))
                startRow = 0;

            long startCol = PubGisUtil.calculateCacheNumber(this.xorigin,
                    this.currentMapExtent.getMinX(),
                    (this.tileRows * this.resolutions[this.currentLevel]));

            long endRow = PubGisUtil.calculateCacheNumber(this.yorigin,
                    this.currentMapExtent.getMinY(),
                    (this.tileCols * this.resolutions[this.currentLevel]));
            long endCol = PubGisUtil.calculateCacheNumber(this.xorigin,
                    this.currentMapExtent.getMaxX(),
                    (this.tileRows * this.resolutions[this.currentLevel]));

            Log.e("切片序列","srow:"+startRow+"erow:"+endRow+"scol:"+startCol+"ecol:"+endCol);

            /**手机屏幕共加载的行数和列数*/
            this.rows = (int)(endRow - startRow + 1);
            this.columns = (int)(endCol - startCol + 1);

            // 开始图片原点的y轴地理坐标
            double topY = this.yorigin - startRow * this.tileRows
                    * this.resolutions[this.currentLevel];
            double leftX = this.xorigin + startCol * this.tileCols
                    * this.resolutions[this.currentLevel];

            // 换算出屏幕一半对应的范围（pixdx为一半屏幕的尺寸）
            float pixdx = (float) ((this.currentMapExtent.getCenterPoint()
                    .getX() - leftX) / this.resolutions[this.currentLevel]);
            float pixdy = (float) ((topY - this.currentMapExtent
                    .getCenterPoint().getY()) / this.resolutions[this.currentLevel]);

            // 当屏幕中点和地图中点重合时，它们之间的像素差值
            float xPan = pixdx - this.screenWidth / 2;
            float yPan = pixdy - this.screenHeight / 2;
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 8就代表容量变为以前容量的1/8
            options.inSampleSize = 1;

            //创建切片路径
            File fl3 = new File(cachePath);
            if (!fl3.exists())
                fl3.mkdirs();

//            ExecutorService executor = Executors.newFixedThreadPool(10);//添加线程池

            for (long i = startRow; i <= endRow; i++) {
                for (long j = startCol; j <= endCol; j++) {
                    leftTopX = this.tileRows * (j - startCol) - xPan;
                    leftTopY = this.tileCols * (i - startRow) - yPan;
                    String pathString = this.cachePath + "/"
                            + (this.currentLevel+1) + "/" + i + "/" + (istiandi?"mark/"+j:j) + mapdata_suffix;
                    File file = new File(pathString);
                    Log.e("要保存的地址",pathString);
                    if (file != null && file.exists()) {
                            Log.d("PugGisProcess","加载本地"+file.getAbsolutePath());
                    } else {

                        final String interPicUrl =  (istiandi?getAnnotaTionUrl(i,j):getBaseMapUrl(i, j));// 地图URL
                        final String dirName = !istiandi?this.cachePath + "/"
                                + (this.currentLevel+1) + "/" + i:this.cachePath + "/"
                                + (this.currentLevel+1) + "/" + i + "/mark";
                        index = j;
//                        executor.submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (interPicUrl != null
//                                        && interPicUrl.length() > 0
//                                        // 将切片下载到本地
//                                        && PubGisUtil.storePic_httpurl(interPicUrl, dirName, index
//                                        + mapdata_suffix)) {
//                                }
//                            }
//                        });

                        if (interPicUrl != null
                                && interPicUrl.length() > 0
                                // 将切片下载到本地
                                && PubGisUtil.storePic_httpurl(interPicUrl, dirName, j
                                + mapdata_suffix)) {
                        }
                    }
                    pubBitmap = new PubBitmap(pathString, leftTopX, leftTopY,
                            true);
                    baseMaps.add(pubBitmap);
                }
            }


//            String interPicUrl =  (getBaseMapUrl(0, 0));// 地图URL
//            String dirName = this.cachePath + "/"
//                    + (this.currentLevel+1) + "/";
//            if (interPicUrl != null
//                    && interPicUrl.length() > 0
//                    // 将切片下载到本地
//                    && PubGisUtil.storePic_httpurl(interPicUrl, dirName, "temp"
//                    + ".png")) {
//            }
//
//            pubBitmap = new PubBitmap(dirName+"/temp.png", 0, 0,
//                    true);
//            baseMaps.clear();
//            baseMaps.add(pubBitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseMaps;
    }

    /**
     * @param columns 底图中瓦片的列数
     * @param rows 底图中瓦片的行数
     * @param bitmaps 符合条件的切片集合，拼接为一张图
     * */
    private Bitmap combineBitmaps(int columns, int rows,
                                  ArrayList<PubBitmap> bitmaps) {
        if (columns <= 0 || bitmaps == null || bitmaps.size() == 0) {
           return null;
        }
        Bitmap newBitmap = null;
        try {
            int maxWidthPerImage = this.tileCols;
            int maxHeightPerImage = this.tileRows;
            // 创建图片组合Bitmap
            newBitmap = Bitmap.createBitmap(columns * maxWidthPerImage, rows
                    * maxHeightPerImage, Bitmap.Config.ARGB_4444);

            // 利用组合Bitmap创建图片画布
            Canvas cv = new Canvas(newBitmap);
            // 将newBitmap在画布上画出
            cv.drawBitmap(newBitmap, 0, 0, null);
            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < columns; y++) {
                    int index = x * columns + y;
                    if (index >= bitmaps.size()) {
                        break;
                    }

                    Bitmap bitmap = bitmaps.get(index).getBitmapFromFile();
                    if (bitmap != null && bitmap instanceof Bitmap
                            && !bitmap.isRecycled()) {
                        cv.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(),
                                bitmap.getHeight()), new Rect(y
                                * maxWidthPerImage, x * maxHeightPerImage, y
                                * maxWidthPerImage + maxHeightPerImage, x
                                * maxHeightPerImage + maxHeightPerImage), null);
                        bitmap.recycle();
                    }
                }
            }
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
        } catch (Exception e) {
           e.printStackTrace();
        }
//        finally {
//            if (bitmaps != null) {
//                bitmaps.clear();
//                bitmaps = null;
//            }
//        }

        return newBitmap;
    }

    /**
     * 根据地图点位获取部件属性
     */
    public void getPartAttributeInfo(MapPoint localPoint) {
        try {
            synchronized (this) {
                if (mAsyncPartAttributeInfo != null && !mAsyncPartAttributeInfo.isCancelled()) {
                    mAsyncPartAttributeInfo.cancel(true);
                    mAsyncPartAttributeInfo = null;
                }
                mAsyncPartAttributeInfo = new AsyncPartAttributeInfo();
                mAsyncPartAttributeInfo.execute(localPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 采用asyntask任务来获取地图切片，请求的张数体量不是很大，就采用轻量级线程服务
     */
    private class AsyncPartAttributeInfo extends AsyncTask<MapPoint, String, String> {

        List<PartsObjectAttributes> mPartsObjectAttributesList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(MapPoint... params) {
            try {
                mPartsObjectAttributesList = queryPartAttributeInfo(params[0]);
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
            if(result.equals("0")){
                mOnBaseMapFinishListen.OnPartLayersAttribute(mPartsObjectAttributesList);
            }else{
                mOnBaseMapFinishListen.OnErrorMsg("部件属性获取失败");
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncPartAttributeInfo = null;
        }
    };

}
