package cn.com.hesc.fastdevframwork.map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.maplibrary.MapActivity;
import cn.com.hesc.maplibrary.OpenLayerActivity;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.view.iMapView;
import cn.com.hesc.tdsdk.TDMapActivity;
import cn.com.hesc.tools.SdcardInfo;

public class MapTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maptest);
    }

    public void openarcgis(View v){
        Intent it = new Intent(this,MapActivity.class);
        Bundle bundle = new Bundle();
        /**开封*/
        bundle.putString("basicinfo","http://59.227.224.84:6080/arcgis/rest/services/kf2000dlg_luwang/MapServer");
//        bundle.putString("basicinfo","http://192.168.71.38:6080/arcgis/rest/services/kaifeng_img/MapServer");
//        bundle.putString("partinfo","http://222.188.95.198:6080/arcgis/rest/services/dongtai/dongtaibujian/MapServer");
        bundle.putSerializable("maptype", iMapView.MapType.ARCGIS);
        bundle.putDouble("strx",114.30);
        bundle.putDouble("stry",34.8);
        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void openMap(View v){
        Intent it = new Intent(this,MapActivity.class);
        Bundle bundle = new Bundle();
        /**兰溪天地图公网测试*/
//        bundle.putString("basicinfo","http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP@#@http://srv.jhmap.gov.cn/LXEMAP/service/WMTS?LAYER=LXEMAP");
//        bundle.putString("annotation","http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO@#@http://srv.jhmap.gov.cn/LXEMAPANNO/service/WMTS?Layer=LXEMAPANNO");
//        bundle.putString("partinfo","http://61.130.148.100:6080/arcgis/rest/services/lanxi/lanxibujian2000/MapServer");
//        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        /**如果是天地图的话，加个地图范围*/
//        bundle.putSerializable("mapextent",new MapExtent(119.417704358469,29.1918249442658,119.480102884241,29.2243062037684));

        /**东台arcgis测试*/
//        bundle.putString("basicinfo","http://222.188.95.198:6080/arcgis/rest/services/dongtai/dongtaiditu/MapServer");
//        bundle.putString("partinfo","http://222.188.95.198:6080/arcgis/rest/services/dongtai/dongtaibujian/MapServer");
//        bundle.putSerializable("maptype", iMapView.MapType.ARCGIS);


        /**杭州天地图测试*/
//        bundle.putString("basicinfo","http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP");
//        bundle.putString("annotation","http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO");
//        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
//        bundle.putSerializable("mapextent",new MapExtent(120.012,30.173,120.258,30.358));
//        bundle.putDouble("strx",120.212);
//        bundle.putDouble("stry",30.276);

        /**金华*/
        bundle.putString("basicinfo","http://t0.tianditu.com/vec_c/wmts?tk=8730e769e5f114d59325884bf3b7a2e9&@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=8730e769e5f114d59325884bf3b7a2e9@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=8730e769e5f114d59325884bf3b7a2e9");
        bundle.putString("annotation","http://t0.tianditu.com/cva_c/wmts?tk=8730e769e5f114d59325884bf3b7a2e9&@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=8730e769e5f114d59325884bf3b7a2e9@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=8730e769e5f114d59325884bf3b7a2e9");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        bundle.putSerializable("mapextent",new MapExtent(119.232,28.555,119.781,29.686));
        bundle.putDouble("strx",119.612);
        bundle.putDouble("stry",29.063);
        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void drawPathMap(View v){
        Intent it = new Intent(this,MapActivity.class);
        Bundle bundle = new Bundle();
        /**兰溪天地图测试*/
//        bundle.putString("basicinfo","http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP@#@http://srv.jhmap.gov.cn/LXEMAP/service/WMTS?LAYER=LXEMAP");
//        bundle.putString("annotation","http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO@#@http://srv.jhmap.gov.cn/LXEMAPANNO/service/WMTS?Layer=LXEMAPANNO");
//        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
//        /**如果是天地图的话，加个地图范围*/
//        bundle.putSerializable("mapextent",new MapExtent(119.417704358469,29.1918249442658,119.480102884241,29.2243062037684));
//        ArrayList<MapPoint> points = new ArrayList<>();
//        points.add(new MapPoint(119.452,29.212));
//        points.add(new MapPoint(119.455,29.213));
//        points.add(new MapPoint(119.455,29.214));
//        points.add(new MapPoint(119.452,29.212));
//        points.add(new MapPoint(119.456,29.213));
//        points.add(new MapPoint(119.457,29.214));
//        points.add(new MapPoint(119.452,29.212));
//        points.add(new MapPoint(119.453,29.214));
//        points.add(new MapPoint(119.454,29.215));
//        bundle.putSerializable("points",points);
//
//        ArrayList<MapPoint> paths = new ArrayList<>();
//        paths.add(new MapPoint(119.453,29.218));
//        paths.add(new MapPoint(119.453,29.219));
//        paths.add(new MapPoint(119.453,29.220));
//        paths.add(new MapPoint(119.453,29.221));
//        paths.add(new MapPoint(119.454,29.218));
//        paths.add(new MapPoint(119.454,29.219));
//        paths.add(new MapPoint(119.454,29.220));
//        paths.add(new MapPoint(119.454,29.221));
//        bundle.putSerializable("paths",paths);

        /**杭州天地图测试*/
        bundle.putString("basicinfo","http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP");
        bundle.putString("annotation","http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        bundle.putSerializable("mapextent",new MapExtent(120.012,30.173,120.258,30.358));

        //绘制描点
        ArrayList<MapPoint> points = new ArrayList<>();
        points.add(new MapPoint(120.116,30.281));
        points.add(new MapPoint(120.117,30.282));
        points.add(new MapPoint(120.117,30.285));
        points.add(new MapPoint(120.116,30.279));
        points.add(new MapPoint(120.116,30.278));
        points.add(new MapPoint(120.116,30.284));
        points.add(new MapPoint(120.118,30.281));
        points.add(new MapPoint(120.118,30.282));
        points.add(new MapPoint(120.118,30.283));
        bundle.putSerializable("points",points);

        //绘制路径
        ArrayList<MapPoint> paths = new ArrayList<>();
        paths.add(new MapPoint(120.116,30.281));
        paths.add(new MapPoint(120.116,30.282));
        paths.add(new MapPoint(120.116,30.283));
        paths.add(new MapPoint(120.117,30.283));
        paths.add(new MapPoint(120.117,30.282));
        paths.add(new MapPoint(120.117,30.281));
        paths.add(new MapPoint(120.117,30.280));
        paths.add(new MapPoint(120.118,30.280));

        //绘制路径2
        ArrayList<MapPoint> paths2 = new ArrayList<>();
        paths2.add(new MapPoint(120.119,30.281));
        paths2.add(new MapPoint(120.119,30.282));
        paths2.add(new MapPoint(120.119,30.283));
        paths2.add(new MapPoint(120.121,30.283));
        paths2.add(new MapPoint(120.121,30.282));
        paths2.add(new MapPoint(120.122,30.281));
        paths2.add(new MapPoint(120.122,30.280));
        paths2.add(new MapPoint(120.123,30.280));

        ArrayList<ArrayList<MapPoint>> pathths = new ArrayList<>();
        pathths.add(paths);
        pathths.add(paths2);
        bundle.putSerializable("paths",pathths);

        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void openTestMap(View v){
        Intent it = new Intent(this,MapTestActivity.class);
        Bundle bundle = new Bundle();
        /**兰溪天地图测试*/
//        bundle.putString("basicinfo","http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP@#@http://srv.jhmap.gov.cn/LXEMAP/service/WMTS?LAYER=LXEMAP");
//        bundle.putString("annotation","http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO@#@http://srv.jhmap.gov.cn/LXEMAPANNO/service/WMTS?Layer=LXEMAPANNO");
//        bundle.putString("partinfo","http://61.130.148.100:6080/arcgis/rest/services/lanxi/lanxibujian2000/MapServer");
//        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        /**如果是天地图的话，加个地图范围*/
//        bundle.putSerializable("mapextent",new MapExtent(119.417704358469,29.1918249442658,119.480102884241,29.2243062037684));

        /**东台arcgis测试*/
//        bundle.putString("basicinfo","http://222.188.95.198:6080/arcgis/rest/services/dongtai/dongtaiditu/MapServer");
//        bundle.putString("partinfo","http://222.188.95.198:6080/arcgis/rest/services/dongtai/dongtaibujian/MapServer");
//        bundle.putSerializable("maptype", iMapView.MapType.ARCGIS);


        /**杭州天地图测试*/
        bundle.putString("basicinfo","http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP");
        bundle.putString("annotation","http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
//        bundle.putSerializable("mapextent",new MapExtent(120.012,30.173,120.258,30.358));
//        bundle.putDouble("strx",120.117);
//        bundle.putDouble("stry",30.282);
        bundle.putSerializable("mapextent",new MapExtent(120.374,29.961,120.685,30.143));
        bundle.putDouble("strx",120.539);
        bundle.putDouble("stry",30.068);


        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    /**
     * 对接天地图SDK
     * @param v
     */
    public void openTd(View v){
        Intent it= new Intent(MapTestActivity.this,TDMapActivity.class);
        Bundle bundle = new Bundle();
        /**杭州天地图测试*/
        //模拟绘制点位
        ArrayList<MapPoint> points = new ArrayList<>();
        points.add(new MapPoint(120.116,30.281));
        points.add(new MapPoint(120.117,30.282));
        points.add(new MapPoint(120.117,30.285));
        points.add(new MapPoint(120.116,30.279));
        points.add(new MapPoint(120.116,30.278));
        points.add(new MapPoint(120.116,30.284));
        points.add(new MapPoint(120.118,30.281));
        points.add(new MapPoint(120.118,30.282));
        points.add(new MapPoint(120.118,30.283));
        bundle.putSerializable("points",points);

        //模拟选择了位置或者GPS定位等
        bundle.putDouble("strx",120.117);
        bundle.putDouble("stry",30.282);

        //模拟2条路径
        ArrayList<MapPoint> points1 = new ArrayList<>();
        points1.add(new MapPoint(120.122,30.281));
        points1.add(new MapPoint(120.122,30.282));
        points1.add(new MapPoint(120.122,30.285));
        points1.add(new MapPoint(120.123,30.279));

        ArrayList<MapPoint> points2 = new ArrayList<>();
        points2.add(new MapPoint(120.115,30.278));
        points2.add(new MapPoint(120.116,30.284));
        points2.add(new MapPoint(120.117,30.286));
        points2.add(new MapPoint(120.118,30.288));
        points2.add(new MapPoint(120.119,30.289));

        ArrayList<ArrayList<MapPoint>> paths = new ArrayList<>();
        paths.add(points1);
        paths.add(points2);
        bundle.putSerializable("paths",paths);

        //模拟2个网格图层
        ArrayList<MapPoint> points11 = new ArrayList<>();
        points11.add(new MapPoint(120.111,30.281));
        points11.add(new MapPoint(120.111,30.282));
        points11.add(new MapPoint(120.113,30.285));
        points11.add(new MapPoint(120.113,30.279));

        ArrayList<MapPoint> points21 = new ArrayList<>();
        points21.add(new MapPoint(120.125,30.278));
        points21.add(new MapPoint(120.125,30.284));
        points21.add(new MapPoint(120.126,30.286));
        points21.add(new MapPoint(120.126,30.288));
        points21.add(new MapPoint(120.127,30.289));

        ArrayList<ArrayList<MapPoint>> polygons = new ArrayList<>();
        polygons.add(points11);
        polygons.add(points21);
        bundle.putSerializable("polygons",polygons);

        it.putExtras(bundle);
        startActivity(it);
    }

    /**
     * openlayer地图操作
     * @param view
     */
    public void openlayer(View view){
        Intent intent = new Intent(this,OpenLayerActivity.class);
        intent.putExtra("mapextent",new MapExtent(116.59066468459,32.6125064403858,116.642700827516,32.6625399559395));
        intent.putExtra("basicurl","http://60.191.115.34:8080/geoserver/gwc/service/wmts?");
        startActivity(intent);
    }

    public void opencountry(View view){
        Intent it = new Intent(this,MapActivity.class);
        Bundle bundle = new Bundle();
        /**杭州天地图测试*/
        Random random = new Random();
        int index = random.nextInt(6);
        bundle.putString("basicinfo","https://t"+index+".tianditu.gov.cn/vec_w/wmts?tk=8730e769e5f114d59325884bf3b7a2e9");
        bundle.putString("annotation","https://t"+index+".tianditu.gov.cn/cva_w/wmts?tk=8730e769e5f114d59325884bf3b7a2e9");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        bundle.putInt("maxlevel",16);
        bundle.putSerializable("mapextent",new MapExtent(116.46,31.72,116.71,31.87));
        bundle.putDouble("strx",116.52);
        bundle.putDouble("stry",31.74);


        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(resultCode == RESULT_OK){
                    Log.e("position",data.getExtras().getString("strx")+"@"+data.getExtras().getString("stry")+"@"+data.getExtras().getString("partcode",""));
                }
                break;
        }
    }
}
