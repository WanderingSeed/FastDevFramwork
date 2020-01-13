package cn.com.hesc.maplibrary.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.hesc.httputils.callback.StringCallback;
import cn.com.hesc.maplibrary.PubGisUtil;
import cn.com.hesc.maplibrary.tools.BitMapUtils;
import okhttp3.Call;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: TiandituGisProcess
 * Description: OpenLayerGis框架
 * Author: liujunlin
 * Date: 2016-04-13 11:18
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class OpenLayerGisProcess extends PubGisProcess{

    private static String Tag = "----OpenLayerGisProcess----";
    private static boolean isSuperMapPart = false;//是否为超图发布的部件服务
    /***************************超图相关************************************************/
    /**保留要查询的部件对象*/
    private JSONObject mainLayer;
    /**
     * 图层的JSON对象MAP
     */
    private Map<Integer, JSONObject> layersMap = new HashMap<Integer, JSONObject>();
    /**
     * 比例尺(每个地市不同，需要修改)
     */
    private double[] scales = {590995197.14166909755553014475 ,295497598.57083454877776507238 ,
            147748799.28541727438888253619 ,73874399.642708637194441268094 ,
            36937199.821354318597220634047 ,18468599.910677159298610317023 ,
            9234299.955338579649305158512 ,4617149.9776692898246525792559 ,
            2308574.9888346449123262896279 , 1154287.494417322456163144814,
            577143.74720866122808157240698 , 288571.87360433061404078620349 ,
            144285.93680216530702039310175 ,72142.968401082653510196550873 ,
            36071.484200541326755098275436 , 18035.742100270663377549137718 ,
            9017.871050135331688774568859 ,4508.9355250676658443872844296,
			2254.4677625338329221936422148, 1127.2338812669164610968211074 ,
			563.61694063345823054841055369};
    private MapLayer queryLayer;
    /***************************超图相关************************************************/

    /**
     * 采用task获取部件图层
     */
    private AsyncPartMap asyncBaseMap = null;
    /**
     * 保留显示的图层id
     */
    String partLayerstr = "";

    /**
     * 构造函数
     * @param layername
     * 			要展示的图层名
     */
    public OpenLayerGisProcess(Context context, String layername) {
        this.context = context;
        this.dislayername = layername;
    }

    /**设置是否是超图发布的部件服务*/
    public static void setSuperMapPart(boolean superMapPart) {
        isSuperMapPart = superMapPart;
    }

    @Override
    protected void initBaseInfo() {
        this.resolutions = new double[8];
        this.resolutions[0] = 0.0001609780989142;
        this.resolutions[1] = 0.0000804890494571;
        this.resolutions[2] = 0.0000402445247286;
        this.resolutions[3] = 0.0000201222623643;
        this.resolutions[4] = 0.0000100611311821;
        this.resolutions[5] = 0.0000050305655911;
        this.resolutions[6] = 0.0000025152827955;
        this.resolutions[7] = 0.0000012576413978;


        this.xorigin = -180;
        this.yorigin = 90;
        this.tileCols = 256;
        this.tileRows = 256;

//        this.currentMapExtent = new MapExtent(119.417704358469,29.1918249442658,119.480102884241,29.2243062037684);
//        this.initMapExtent = new MapExtent(118.417704358469,28.1918249442658,120.480102884241,30.2243062037684);

        this.currentLevel = 3;
        this.minLevel = 0;
        this.maxLevel = this.resolutions.length - 1;

        initPartInfo();

    }

    @Override
    protected void initPartInfo(){
        if(!TextUtils.isEmpty(partInfo)){
            if(isSuperMapPart)
                initPartInfo_SuperMap();
            else
                initPartInfo_Rest();
        }else{
            if (hadlocationPoint != null)
                currentMapExtent.setCenterPoint(hadlocationPoint);
            else if (gpsPoint != null)
                currentMapExtent.setCenterPoint(gpsPoint);
            changeMapExtent(currentMapExtent.getCenterPoint());
        }
    }

    @Override
    protected void getPartLayersBitmap(MapLayer[] partLayers) {
        // partMapArray该参数用于存放所有的查询图层对象，该参数在初始化中赋值
        if (partLayers != null && partLayers.length > 0) {

            partLayerstr = "";
            for (MapLayer layer : partLayers) {
                if (layer.isVisible()) {
                    partLayerstr += layer.getId() + ",";
                }
            }
            // 即表示选择了查询图层
            if (!partLayerstr.trim().equalsIgnoreCase("")) {
                // 去除字符串最后一个逗号
                partLayerstr = partLayerstr.substring(0,
                        partLayerstr.length() - 1);
                // 部件图片url获取,该地址中的内容是以png格式存放的图片,即导出地图操作；根据相应的id导出相应的图层
                // 部件图层是实时获取的，直接展示不用保存的存储卡上
            }
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
        String url = "";
        if(isSuperMapPart){
            String tempLayerSet_ID = "";
            try{
                buildTempLayerSetJson();
                String idUrl = basicInfo
                        + "/tempLayersSet.json?returnPostAction=true&getMethodForm=true";

                JSONArray mainArray = new JSONArray();
                mainArray.put(mainLayer);
                String result = PubGisUtil.getResponseData(idUrl,
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

            url = partInfo
                    + "/image.png?center={\"x\":" + this.currentMapExtent.getCenterPoint().getX()
                    + ",\"y\":"
                    + this.currentMapExtent.getCenterPoint().getY() + "}&scale="
                    + 1/this.scales[this.currentLevel+1] + "&layersID="
                    + tempLayerSet_ID + "&transparent=true&width="
                    + this.screenWidth + "&height=" + this.screenHeight
                    + "&cacheEnabled=false";
        }else{
            //======================arcgis发布的部件服务==============================
//            url = partInfo
//                    + "/export?bbox="
//                    + this.currentMapExtent.getMinX()
//                    + ","
//                    + this.currentMapExtent.getMinY()
//                    + ","
//                    + this.currentMapExtent.getMaxX()
//                    + ","
//                    + this.currentMapExtent.getMaxY()
//                    + "&layers=show:"
//                    + partLayerstr
//                    + "&size="
//                    + this.screenWidth
//                    + ","
//                    + this.screenHeight
//                    + "&bboxSR=&layerdefs=&imageSR=&format=png&transparent=true&dpi=96&f=image";

            //-----------------------------openlayers服务请求方式-------------------------------------------------
            try {
                url = partInfo
                        +"SERVICE=WMS&VERSION=1.1.0&REQUEST=GetMap&FORMAT=image/png&TRANSPARENT=true"
                        +"&LAYERS="+ URLEncoder.encode(partLayerstr,"UTF-8")
                        +"&SRS=EPSG:4490&STYLES=&WIDTH="+this.screenWidth+"&HEIGHT="+this.screenHeight
                        +"+&BBOX="
                        + this.currentMapExtent.getMinX()
                        + ","
                        + this.currentMapExtent.getMinY()
                        + ","
                        + this.currentMapExtent.getMaxX()
                        + ","
                        + this.currentMapExtent.getMaxY();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        return url;
    }

    @Override
    public String getBaseMapUrl(long row, long column) {

//        String[] basic = basicInfo.split("@#@");
//        if(basic == null || basic.length < 2){
//            return "";
//        }
//
//
        String interPicUrl = null;
//        String guostr="http://t0.tianditu.com/vec_c/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=c";
//        String shengstr = basic[0]+"&SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&FORMAT=image/png&TILEMATRIXSET=TileMatrixSet0";
//        String shistr = basic[1]+"&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrixSet=TileMatrixSet0";
//        if (row >= this.minRow && row <= this.maxRow && column >= this.minClo
//                && column <= this.maxClo) {
//                        if(this.currentLevel<14){
//            interPicUrl = guostr + "&TILEMATRIX="
//                    + (this.currentLevel + 1) + "&TILEROW=" + row + "&TILECOL=" + column + "&FORMAT=tiles";
//                        }else if(this.currentLevel<17){
//                            interPicUrl = shengstr + "&TILEMATRIX=" + (this.currentLevel + 1)
//                                    +"&STYLE=default" + "&TILEROW="+ row + "&TILECOL=" + column;
//                        }else {
//                            interPicUrl = shistr + "&TileMatrix=" + (this.currentLevel + 1)
//                                    +"&STYLE=default"+ "&TileRow="+ row + "&TileCol=" + column;
//                        }
//        }

        interPicUrl = basicInfo
                + "layer="+this.dislayername + ":" + this.dislayername
                + "&style=&tilematrixset="+dislayername
                + "&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/jpeg&TileMatrix=" + this.dislayername + ":"+this.currentLevel
                + "&TileCol=" + column
                + "&TileRow=" + row;

        return interPicUrl;
    }

    @Override
    public String getAnnotaTionUrl(long row, long column) {


//        String[] annotation = annotationInfo.split("@#@");
//        if(annotation == null || annotation.length < 2){
//            return "";
//        }
//
//
//        String guostr="http://t0.tianditu.com/cva_c/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=c";
//        String shengmarkstr = annotation[0]+"&SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&FORMAT=image/png&TILEMATRIXSET=TileMatrixSet0";
//        String shimarkstr = annotation[1]+"&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrixSet=TileMatrixSet0";
        String interPicUrl = null;
//        if (row >= this.minRow && row <= this.maxRow && column >= this.minClo
//                && column <= this.maxClo) {
//            if(this.currentLevel<14){
//                interPicUrl = guostr + "&TILEMATRIX="
//                        + (this.currentLevel + 1) + "&TILEROW=" + row + "&TILECOL=" + column + "&FORMAT=tiles";
//            }else if(this.currentLevel<17){
//                interPicUrl = shengmarkstr + "&TILEMATRIX=" + (this.currentLevel + 1)
//                        +"&STYLE=default" + "&TILEROW="+ row + "&TILECOL=" + column;
//            }else {
//                interPicUrl = shimarkstr + "&TileMatrix=" + (this.currentLevel + 1)
//                        +"&STYLE=default"+ "&TileRow="+ row + "&TileCol=" + column;
//            }
//        }
        return interPicUrl;

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
            throw e;
        }
    }

    @Override
    protected List<PartsObjectAttributes> queryPartAttributeInfo(MapPoint locatPoint) {
        if(isSuperMapPart){
            return queryPartAttributeInfo_SuperMap(locatPoint);
        }else{
            return queryPartAttributeInfo_Rest(locatPoint);
        }
    }

    /**
     * arcgis-rest部件服务
     */
    private void initPartInfo_Rest(){
        String url = partInfo + "?f=json";
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
     * supermap部件服务
     */
    private void initPartInfo_SuperMap(){
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
            if(!isSuperMapPart){
                try {
                    JSONObject jsonObject = new JSONObject(info);
                    JSONArray jsonArray = jsonObject.getJSONArray("layers");
                    if(jsonArray!=null){
                        this.partMapArray = new MapLayer[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject lb = jsonArray.getJSONObject(i);
                            boolean isShow = (!TextUtils.isEmpty(dislayername) && dislayername.equals(lb.getString("name")));
                            MapLayer m = new MapLayer(lb.getInt("id"), lb.getString("name"), isShow);
                            partMapArray[i] = m;
                        }
                        result = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
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


        }

        return result;
    }

    /**
     * 只查询部件图层里最后一个图层的属性
     * @param locatPoint 点击的屏幕坐标
     * @return
     */
    private List<PartsObjectAttributes> queryPartAttributeInfo_SuperMap(MapPoint locatPoint) {
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

    private List<PartsObjectAttributes> queryPartAttributeInfo_Rest(MapPoint locatPoint) {
        List<PartsObjectAttributes> partsObjectAttributesList = new ArrayList<>();
        try {
            String queryUrl = partInfo;
            queryUrl += "/identify";
            /* 查询部件几何类型 */
            queryUrl += "?geometryType=esriGeometryPoint";
            /* 客户端操作几何类型 */
            queryUrl += "&geometry={x:" + locatPoint.getX() + ",y:"
                    + locatPoint.getY() + "}";
			/* 查询部件ID */
            // int layerIdRest = Integer.parseInt(partLayers.split(",")[0]);
            queryUrl += "&layers=all:" + partLayerstr;
			/* 查询部件几何公差（例：点，相当于查询半径画圆） */
            queryUrl += "&tolerance=" + tolerance;
			/* 当前地图地理范围 */
            queryUrl += "&mapExtent=" + this.currentMapExtent.getMinX() + ","
                    + this.currentMapExtent.getMinY() + ","
                    + this.currentMapExtent.getMaxX() + ","
                    + this.currentMapExtent.getMaxY();
			/* 屏幕范围 */
            queryUrl += "&imageDisplay=" + Math.round(this.screenWidth) + ","
                    + Math.round(this.screenHeight) + ",96";
			/* 返回的数据类型 */
            queryUrl += "&f=json";

            //--------------------openlayers查询方式--------------------------------
            //queryUrl = "http://192.168.1.18:6350/geoserver/hescgis/ows?service=WFS&version=1.0.0&outputFormat=json&request=GetFeature&typeName=hescgis:"+ URLEncoder.encode("公共自行车","UTF-8")+"&SRS=EPSG:4490&maxfeatures=500&BBOX=120.05331761141758,29.374461695199273,121.37167698641728,29.74456362391021";




            URL url = new URL(queryUrl);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    con.getInputStream(), "UTF-8"));
            String payload = reader.readLine();
            reader.close();
            JSONObject jsonObject = new JSONObject(payload);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonAttObject = jsonArray.getJSONObject(i)
                            .getJSONObject("attributes");
                    Iterator<String> iterator = jsonAttObject.keys();

                    PartsObjectAttributes partsObjectAttributes = new PartsObjectAttributes();
                    Map<String, String> attributeMap = partsObjectAttributes
                            .getAttributeMap();
                    while (iterator.hasNext()) {
                        String keyName = iterator.next().toUpperCase();
                        try {
                            String value = jsonAttObject.getString(keyName);
                            attributeMap.put(keyName, value);

                        } catch (JSONException e) {
                            continue;
                        }

                    }
                    partsObjectAttributesList.add(partsObjectAttributes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partsObjectAttributesList;
    }
}
