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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.hesc.httputils.callback.StringCallback;
import cn.com.hesc.maplibrary.PubGisUtil;
import cn.com.hesc.maplibrary.tools.BitMapUtils;
import okhttp3.Call;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: RestPubGisProcess
 * Description: 针对arcgis本地地图服务
 * Author: liujunlin
 * Date: 2016-04-12 17:03
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class RestPubGisProcess extends PubGisProcess {


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
     *
     * @param layerName 要展示的图层名
     */
    public RestPubGisProcess(Context context, String layerName) {
        this.context = context;
        this.dislayername = layerName;
    }

    /**
     * 初始化地图信息
     */
    @Override
    protected void initBaseInfo() {
        String url = basicInfo + "?f=json";
        PubGisUtil.getJsonContent(url, new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String s) {
                if (getBaseInfo(s)){
                    if(!TextUtils.isEmpty(partInfo))
                        initPartInfo();
                    else{
                        if (hadlocationPoint != null)
                            currentMapExtent.setCenterPoint(hadlocationPoint);
                        else if (gpsPoint != null)
                            currentMapExtent.setCenterPoint(gpsPoint);
                        changeMapExtent(currentMapExtent.getCenterPoint());
                    }
                }else{
                    mOnBaseMapFinishListen.OnErrorMsg("地图初始化失败");
                }
            }
        });
    }

    private boolean getBaseInfo(String info) {
        boolean re = false;
        int resolutionSize;
        JSONArray jsonArray;
        if (!TextUtils.isEmpty(info)) {
            try {
                JSONObject jsonObject = new JSONObject(info);
                jsonArray = jsonObject.getJSONObject("tileInfo").getJSONArray("lods");
                if(jsonArray!=null){
                    resolutionSize = jsonArray.length();
                    this.resolutions = new double[resolutionSize];
                    for (int i = 0; i < resolutions.length; i++) {
                        /**将地图比例尺录入对象*/
                        this.resolutions[i] = jsonObject.getJSONObject("tileInfo").getJSONArray("lods").getJSONObject(i).getDouble("resolution");
                    }
                    this.minLevel = 0;
                    this.maxLevel = resolutions.length - 1;
                    this.xorigin = jsonObject.getJSONObject("tileInfo").getJSONObject("origin").getInt("x");
                    this.yorigin = jsonObject.getJSONObject("tileInfo").getJSONObject("origin").getInt("y");
                    this.tileCols = jsonObject.getJSONObject("tileInfo").getInt("cols");
                    this.tileRows = jsonObject.getJSONObject("tileInfo").getInt("rows");
                    JSONObject fmap = jsonObject.getJSONObject("fullExtent");
                    this.currentMapExtent = new MapExtent(fmap.getDouble("xmin"), fmap.getDouble("ymin"), fmap.getDouble("xmax"), fmap.getDouble("ymax"));
                    this.initMapExtent = new MapExtent(fmap.getDouble("xmin"), fmap.getDouble("ymin"), fmap.getDouble("xmax"), fmap.getDouble("ymax"));
                    this.currentLevel = /*this.maxLevel/*/0;
                }
                re = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return re;
    }

    /**
     * 初始化部件图层
     */
    @Override
    protected void initPartInfo() {
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
                String url = getPartUrl(partLayerstr);
                asyncBaseMap = new AsyncPartMap();
                asyncBaseMap.execute("0", url);
            }
        }
    }

    private String getPartUrl(String partLayers) {
        String url = partInfo
                + "/export?bbox="
                + this.currentMapExtent.getMinX()
                + ","
                + this.currentMapExtent.getMinY()
                + ","
                + this.currentMapExtent.getMaxX()
                + ","
                + this.currentMapExtent.getMaxY()
                + "&layers=show:"
                + partLayers
                + "&size="
                + this.screenWidth
                + ","
                + this.screenHeight
                + "&bboxSR=&layerdefs=&imageSR=&format=png&transparent=true&dpi=96&f=image";

        //-----------------------------openlayers服务请求方式-------------------------------------------------
       /* String url = null;
        try {
            url = "http://192.168.1.18:6350/geoserver/hescgis/wms?SERVICE=WMS&VERSION=1.1.0&REQUEST=GetMap&FORMAT=image/png&TRANSPARENT=true&LAYERS="+ URLEncoder.encode("公共自行车","UTF-8")+"&SRS=EPSG:4490&STYLES=&WIDTH="+this.screenWidth+"&HEIGHT="+this.screenHeight+"&BBOX=120.05331761141758,29.374461695199273,121.37167698641728,29.74456362391021";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        return url;
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
        }

        return result;
    }

    /**
     * 只针对天地图的标注，所以这里返回空
     *
     * @param row    切片行号
     * @param column 切片列号
     * @return 天地图标注的url
     */
    @Override
    public String getAnnotaTionUrl(long row, long column) {
        return null;
    }

    /**
     * 根据所在的行列号获取对应的图片地址
     *
     * @param row    切片行号
     * @param column 切片列号
     * @return 切片的url
     */
    @Override
    public String getBaseMapUrl(long row, long column) {
        String interPicUrl = null;
//        if (row >= this.minRow && row <= this.maxRow && column >= this.minClo
//                && column <= this.maxClo) {
            interPicUrl = basicInfo + "/tile" + "/" + (this.currentLevel)
                    + "/" + row + "/" + column;
//        }
        return interPicUrl;
    }

    @Override
    protected List<PartsObjectAttributes> queryPartAttributeInfo(MapPoint locatPoint) {
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

    private class AsyncPartMap extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String type = params[0];
            String url = params[1];
            Bitmap bitmapbytes = BitMapUtils.getURLBitmap2(url);
            return bitmapbytes;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mOnBaseMapFinishListen.OnPartLayersSuccess(result);
        }
    }

}
