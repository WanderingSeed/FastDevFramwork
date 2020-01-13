package cn.com.hesc.maptest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Random;

import cn.com.hesc.maplibrary.MapActivity;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.model.MapPoint;
import cn.com.hesc.maplibrary.overlayer.InfoWindow;
import cn.com.hesc.maplibrary.overlayer.LatLng;
import cn.com.hesc.maplibrary.overlayer.Marker;
import cn.com.hesc.maplibrary.view.iMapView;
import cn.com.hesc.picture.MultiePreViewActivity;

public class MainActivity extends AppCompatActivity {

    private static int downfingers = 0;
    private static int minDistance = 0;
    private static int dpi = 1;
    private final static int mapmaxscale = 10;
    private final static int mapminscale = 1;
    private static float oldtwodistance = 0;
    String strx = "0";
    String stry = "0";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取系统认为的最小滑动距离
        minDistance = ViewConfiguration.get(this).getScaledTouchSlop();
        Log.e("minDistance", minDistance + "");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        //获取dpi
        dpi = dm.densityDpi;

    }

    public void openarcgis(View v){
        Intent it = new Intent(this,MapActivity.class);
        Bundle bundle = new Bundle();
        /**开封  http://59.227.224.84:6080/arcgis/rest/services/kf2000_DLGdongtai/MapServer  Bundle[{strx=114.37279501722071, stry=33.78968345655531, partcode=}]*/
        bundle.putString("basicinfo","http://59.227.251.211:6080/arcgis/rest/services/kf2000_DLGdongtai_201812311445/MapServer");
        bundle.putSerializable("maptype", iMapView.MapType.ARCGIS);
        bundle.putDouble("strx",114.3774900192357);
        bundle.putDouble("stry",34.77867243156861);
        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void openmap(View view){
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
        bundle.putString("basicinfo","http://t0.tianditu.com/vec_c/wmts?tk=58031762044c8b72881de0f59e93d167&@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=9bdabcaae1e933dc4dff9d378630182b@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=9bdabcaae1e933dc4dff9d378630182b");
        bundle.putString("annotation","http://t0.tianditu.com/cva_c/wmts?tk=58031762044c8b72881de0f59e93d167&@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=9bdabcaae1e933dc4dff9d378630182b@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=9bdabcaae1e933dc4dff9d378630182b");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        bundle.putSerializable("mapextent",new MapExtent(119.126652, 28.516305, 120.777572, 29.720231));
        bundle.putDouble("centerx",119.642722);
        bundle.putDouble("centery", 29.082087);

        bundle.putDouble("strx",120.612);
        bundle.putDouble("stry",29.896);


        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void showmap(View view){
        Intent it = new Intent(this,MapActivity.class);
        Bundle bundle = new Bundle();

        /**杭州天地图测试*/
        bundle.putString("basicinfo","http://t0.tianditu.com/vec_c/wmts?tk=58031762044c8b72881de0f59e93d167&@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=9bdabcaae1e933dc4dff9d378630182b@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=9bdabcaae1e933dc4dff9d378630182b");
        bundle.putString("annotation","http://t0.tianditu.com/cva_c/wmts?tk=58031762044c8b72881de0f59e93d167&@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=9bdabcaae1e933dc4dff9d378630182b@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=9bdabcaae1e933dc4dff9d378630182b");
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
//        bundle.putSerializable("points",points);

        //绘制可以点击显示信息的点
        ArrayList<Marker> markers = new ArrayList<>();
        Marker marker = new Marker(new LatLng(30.283,120.118));
        marker.setTitle("巡查员001");
        marker.setContent("姓名：张成国");
        markers.add(marker);
        bundle.putSerializable("marks",markers);

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

        bundle.putDouble("strx",Double.parseDouble(strx));
        bundle.putDouble("stry",Double.parseDouble(stry));


        bundle.putSerializable("paths",pathths);

        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void copymap(View view){
        Intent it = new Intent(this,MapTestActivity.class);
        Bundle bundle = new Bundle();

        /**杭州天地图测试*/
        bundle.putString("basicinfo","http://t0.tianditu.com/vec_c/wmts?&tk=5ae9ec312d4a599bd8c405c40027f493@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=c03a17565ad49be5043309101f51e9c1@#@http://srv.zjditu.cn/ZJEMAP_2D/wmts?layer=TDT_ZJEMAP&tk=c03a17565ad49be5043309101f51e9c1");
        bundle.putString("annotation","http://t0.tianditu.com/cva_c/wmts?&tk=5ae9ec312d4a599bd8c405c40027f493@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=c03a17565ad49be5043309101f51e9c1@#@http://srv.zjditu.cn/ZJEMAPANNO_2D/wmts?LAYER=TDT_ZJEMAPANNO&tk=c03a17565ad49be5043309101f51e9c1");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        bundle.putSerializable("mapextent",new MapExtent(120.012,30.173,120.258,30.358));
        bundle.putDouble("strx",Double.parseDouble(strx));
        bundle.putDouble("stry",Double.parseDouble(stry));
        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void opencountry(View view){
        Intent it = new Intent(this,MapActivity.class);
        Bundle bundle = new Bundle();
        /**杭州天地图测试*/
        Random random = new Random();
        int index = random.nextInt(6);
        bundle.putString("basicinfo","https://t"+index+".tianditu.com/vec_w/wmts?tk=58031762044c8b72881de0f59e93d167");
        bundle.putString("annotation","https://t"+index+".tianditu.com/cva_w/wmts?tk=58031762044c8b72881de0f59e93d167");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        bundle.putInt("maxlevel",16);
        bundle.putSerializable("mapextent",new MapExtent(120.374,29.961,120.685,30.143));
        bundle.putDouble("strx",120.539);
        bundle.putDouble("stry",30.068);

        Intent intent = new Intent(MainActivity.this,MultiePreViewActivity.class);


        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(resultCode == RESULT_OK){
                    strx = data.getExtras().getString("strx");
                    stry = data.getExtras().getString("stry");
                }
                break;
        }
    }
}
