package cn.com.hesc.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

import cn.com.hesc.maplibrary.MapActivity;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.view.iMapView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openmap(View view){
        Intent it = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();
        /**杭州天地图测试*/
        Random random = new Random();
        int index = random.nextInt(6);
        bundle.putString("basicinfo","http://t"+index+".tianditu.com/vec_c/wmts?tk=58031762044c8b72881de0f59e93d167");
        bundle.putString("annotation","http://t"+index+".tianditu.com/cva_c/wmts?tk=58031762044c8b72881de0f59e93d167");
        bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
        bundle.putInt("maxlevel",16);
        bundle.putSerializable("mapextent",new MapExtent(116.42,31.71,116.57,31.80));
        bundle.putDouble("strx",116.51);
        bundle.putDouble("stry",31.74);

        it.putExtras(bundle);
        startActivityForResult(it,0);
    }
}
