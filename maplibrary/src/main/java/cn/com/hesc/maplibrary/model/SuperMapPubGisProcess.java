package cn.com.hesc.maplibrary.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.hesc.httputils.callback.StringCallback;
import cn.com.hesc.maplibrary.PubGisUtil;
import cn.com.hesc.maplibrary.tools.BitMapUtils;
import okhttp3.Call;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: SuperMapPubGisProcess
 * Description: 超图
 * Author: liujunlin
 * Date: 2016-04-13 11:18
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class SuperMapPubGisProcess extends PubGisProcess{

    private static String Tag = "----SuperMapPubGisProcess----";
    /**
     * 图层的JSON对象MAP
     */
    private Map<Integer, JSONObject> layersMap = new HashMap<Integer, JSONObject>();

    /**
     * 比例尺(每个地市不同，需要修改)
     */
    private double[] scales = { 1 / 100000.0, 1 / 50000.0, 1 / 25000.0,
            1 / 12500.0, 1 / 6250.0, 1 / 3125.0 ,1/1562.0};

    /**保留要查询的部件对象*/
    private JSONObject mainLayer;
    /**
     * 采用task拼接底图
     */
    private AsyncPartMap asyncBaseMap = null;

    private MapLayer queryLayer;

    /**
     * 构造函数
     * @param layername
     * 			要展示的图层名
     */
    public SuperMapPubGisProcess(Context context, String layername) {
        this.context = context;
        this.dislayername = layername;
    }


    @Override
    protected void initBaseInfo() {
        this.tileCols = 512;
        this.tileRows = 512;
        this.resolutions = new double[this.scales.length];
        this.currentLevel = 0;
        this.minLevel = 0;
        this.maxLevel = resolutions.length - 1;
        String url = basicInfo + "/tileImage.json?x=0&y=0&width=" + tileCols + "&height="
                + tileRows + "&scale=" + this.scales[0];
        PubGisUtil.getJsonContent(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String s) {
                if (getBaseInfo(s))
                    initPartInfo();
            }
        });
    }

    private boolean getBaseInfo(String info) {
        boolean re = false;
        if (!TextUtils.isEmpty(info)) {
            try{
                JSONObject jsonObject = new JSONObject(info);

                 JSONObject boundsObject = null;
                boundsObject = jsonObject.getJSONObject("mapParam")
                        .getJSONObject("bounds");
                this.xorigin = boundsObject.getDouble("left");
                this.yorigin = boundsObject.getDouble("top");
                this.currentMapExtent = new MapExtent(
                        boundsObject.getDouble("left"),
                        boundsObject.getDouble("bottom"),
                        boundsObject.getDouble("right"),
                        boundsObject.getDouble("top"));
                this.initMapExtent = new MapExtent(boundsObject.getDouble("left"),
                        boundsObject.getDouble("bottom"),
                        boundsObject.getDouble("right"),
                        boundsObject.getDouble("top"));
                Double viewBoundsDis = jsonObject.getJSONObject("mapParam")
                        .getJSONObject("viewBounds").getDouble("right")
                        - jsonObject.getJSONObject("mapParam")
                        .getJSONObject("viewBounds").getDouble("left");
                for (int i = 0; i < this.resolutions.length; i++) {
                    this.resolutions[i] = this.scales[0]
                            * (viewBoundsDis / tileCols) / this.scales[i];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            re = true;
        }

        return re;
    }

    @Override
    protected void initPartInfo() {
        String url = partInfo + "/layers.json";
        PubGisUtil.getJsonContent(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String s) {
                if (getPartInfo(s)) {
                    if (hadlocationPoint != null)
                        currentMapExtent.setCenterPoint(hadlocationPoint);
                    else if (gpsPoint != null)
                        currentMapExtent.setCenterPoint(gpsPoint);
                    changeMapExtent(currentMapExtent.getCenterPoint());
                }

            }
        });
    }

    /**
     * 解析部件信息
     *
     * @param info
     * @return
     */
    private boolean getPartInfo(String info) {
        boolean result = false;
        if (!TextUtils.isEmpty(info)) {
            try{
                JSONArray mainArray = new JSONArray(info);
                mainLayer = mainArray.getJSONObject(0);
                JSONArray jsonArray = mainLayer.getJSONObject("subLayers")
                        .getJSONArray("layers");
                List<MapLayer> layerList = new ArrayList<MapLayer>();
                MapLayer mapLayer;
                String layerName = "";
                String aliasName = "";
                boolean layerVis = false;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject layerObject = jsonArray.getJSONObject(i);
                    layerName = layerObject.getString("caption");
                    aliasName = layerObject.getString("name");
                    layerVis = layerObject.getBoolean("visible");
                    if (layerName != null && (layerName.indexOf("专题图") < 0) && (!layerVis)) {
                        mapLayer = new MapLayer(i, layerName, aliasName, false);
                        layerList.add(mapLayer);
                        layersMap.put(i, layerObject);
                    }
                }
                this.partMapArray = new MapLayer[layerList.size()];
                layerList.toArray(partMapArray);
                mainLayer.remove("subLayers");

                result = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void getPartLayersBitmap(MapLayer[] partLayers) {
        // partMapArray该参数用于存放所有的查询图层对象，该参数在初始化中赋值
        if (partLayers != null && partLayers.length > 0) {
            partMapArray = partLayers;
            asyncBaseMap = new AsyncPartMap();
            asyncBaseMap.execute("0");
        }
    }

    private class AsyncPartMap extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = getPartUrl();
            Bitmap bitmapbytes = BitMapUtils.getURLBitmap2(url);
            return bitmapbytes;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mOnBaseMapFinishListen.OnPartLayersSuccess(result);
        }
    }

    private String getPartUrl() {
        String tempLayerSet_ID = "";
        try{
            buildTempLayerSetJson();
            String url = basicInfo
                    + "/tempLayersSet.json?returnPostAction=true&getMethodForm=true";

            JSONArray mainArray = new JSONArray();
            mainArray.put(mainLayer);
            String result = PubGisUtil.getResponseData(url,
                    mainArray.toString());
            JSONObject resultObject = new JSONObject(result);
            boolean isSucceed = resultObject.getBoolean("succeed");
            if (isSucceed) {
                tempLayerSet_ID = resultObject.getString("newResourceID");
            } else {
                tempLayerSet_ID = "";
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String url = partInfo
                + "/image.png?center={\"x\":"
                + this.currentMapExtent.getCenterPoint().getX() + ",\"y\":"
                + this.currentMapExtent.getCenterPoint().getY() + "}&scale="
                + this.scales[this.currentLevel] + "&layersID="
                + tempLayerSet_ID + "&transparent=true&width="
                + this.screenWidth + "&height=" + this.screenHeight
                + "&cacheEnabled=false";

        return url;
    }

    /**
     * 构建临时数据集
     * @throws Exception
     */
    private void buildTempLayerSetJson() throws Exception {
        try {
            JSONObject subLayersObject = new JSONObject();
            JSONArray layersArray = new JSONArray();
            if (partMapArray!=null) {
                for (int i = 0; i < partMapArray.length; i++) {
                    MapLayer mapLayer = partMapArray[i];
                    if (mapLayer != null && mapLayer.isVisible()) {
                        JSONObject layerObject = layersMap.get(mapLayer.getId());
                        layerObject.put("visible", true);
                        layersArray.put(layerObject);

                        this.queryLayer = mapLayer;
                    }
                }
                subLayersObject.put("layers", layersArray);
                mainLayer.put("subLayers", subLayersObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public String getAnnotaTionUrl(long row, long column) {
        return null;
    }

    @Override
    public String getBaseMapUrl(long row, long column) {
        String interPicUrl = null;
		/* 保证在切图范围内，否则不返回正确URL */
        if (row >= this.minRow && row <= this.maxRow && column >= this.minClo
                && column <= this.maxClo) {
            interPicUrl = basicInfo + "/tileImage.png?scale="
                    + this.scales[this.currentLevel] + "&x=" + column + "&y="
                    + row + "&width=" + this.tileCols + "&height="
                    + this.tileRows;
        }
        return interPicUrl;
    }

    /**
     * 只查询部件图层里最后一个图层的属性
     * @param locatPoint 点击的屏幕坐标
     * @return
     */
    @Override
    protected List<PartsObjectAttributes> queryPartAttributeInfo(MapPoint locatPoint) {
        List<PartsObjectAttributes> partsObjectAttributesList = new ArrayList<>();
        try {
            String queryUrl = partInfo;
            queryUrl += "/queryResults.json?returnContent=true";
            JSONObject queryParamObject = new JSONObject();
			/* 查询类型 */
            queryParamObject.put("queryMode", "SpatialQuery");
			/* 构建空间查询几何图形 */
            JSONObject geoObject = new JSONObject();
            geoObject.put("id", 0);
            geoObject.put("style", "null");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(5);
            geoObject.put("parts", jsonArray);

			/*添加查询矩形框(范围)*/
            {
                JSONArray pointsArray = new JSONArray();
				/*20像素代表的地理距离*/
                double dis = this.resolutions[this.currentLevel] * this.tolerance;
                double center_x = locatPoint.getX();
                double center_y = locatPoint.getY();

                JSONObject left_bottom = new JSONObject();
                left_bottom.put("x", center_x - dis);
                left_bottom.put("y", center_y - dis);

                JSONObject right_bottom = new JSONObject();
                right_bottom.put("x", center_x + dis);
                right_bottom.put("y", center_y - dis);

                JSONObject right_top = new JSONObject();
                right_top.put("x", center_x + dis);
                right_top.put("y", center_y + dis);

                JSONObject left_top = new JSONObject();
                left_top.put("x", center_x - dis);
                left_top.put("y", center_y + dis);

                pointsArray.put(left_bottom);
                pointsArray.put(right_bottom);
                pointsArray.put(right_top);
                pointsArray.put(left_top);
                pointsArray.put(left_bottom);

                geoObject.put("points", pointsArray);
            }
            geoObject.put("type", "REGION");
            queryParamObject.put("geometry", geoObject);
			/*添加查询参数*/
            JSONObject parametersObject = new JSONObject();
            {
                JSONObject paramsObject = new JSONObject();
                paramsObject.put("attributeFilter", "SMID>0");
                paramsObject.put("name", this.queryLayer.getAliasName());
                JSONArray paramsArray = new JSONArray();
                paramsArray.put(paramsObject);
                parametersObject.put("queryParams", paramsArray);
            }
            parametersObject.put("startRecord", 0);
            parametersObject.put("expectCount", 1000);
            parametersObject.put("queryOption", "ATTRIBUTEANDGEOMETRY");
            queryParamObject.put("queryParameters", parametersObject);

			/*
			 * 添加空间查询形式（相交，包含，被包含……）
			 */
            queryParamObject.put("spatialQueryMode", "CONTAIN");
            String result = PubGisUtil.getResponseData(queryUrl,
                    queryParamObject.toString());

            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultArray = jsonObject.getJSONArray("recordsets");
            if (resultArray != null && jsonObject != null
                    && jsonObject.getInt("currentCount") >= 1) {

                JSONArray featuresArray = resultArray.getJSONObject(0)
                        .getJSONArray("features");
                JSONArray fieldsArray = resultArray.getJSONObject(0)
                        .getJSONArray("fields");

                for (int i = 0; i < featuresArray.length(); i++) {
                    JSONArray valueObject = featuresArray.getJSONObject(i)
                            .getJSONArray("fieldValues");
                    PartsObjectAttributes partsObjectAttributes = new PartsObjectAttributes();
                    for (int index = 0; index < valueObject.length(); index++) {
                        Map<String, String> attributeMap = partsObjectAttributes
                                .getAttributeMap();
                        attributeMap.put(fieldsArray.getString(index),valueObject.getString(index));
                    }
                    partsObjectAttributesList.add(partsObjectAttributes);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return partsObjectAttributesList;
    }
}
