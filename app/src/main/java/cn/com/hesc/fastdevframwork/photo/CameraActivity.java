package cn.com.hesc.fastdevframwork.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.PrimitiveIterator;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.gpslibrary.model.GpsForUser;
import cn.com.hesc.picture.Camera_VideoActivity;
import cn.com.hesc.picture.JHPhotoActivity;
import cn.com.hesc.picture.ModifyPicActivity;
import cn.com.hesc.picture.MultiePreViewActivity;
import cn.com.hesc.picture.multiplepic.MultiplePicActivity;
import cn.com.hesc.tools.BitMapUtils;
import cn.com.hesc.tools.DensityUtils;
import cn.com.hesc.tools.TimeUtils;
import cn.com.hesc.tools.ToastUtils;


public class CameraActivity extends AppCompatActivity {

    private ArrayList<String> mediapaths = new ArrayList<>();
    private Context mContext;
    private GpsForUser mGpsForUser;
    private String editpath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mContext = this;


        mGpsForUser = GpsForUser.getInstance(this);
        mGpsForUser.isGPSOpen();

    }

    public void takePhoto(View view){
        Intent it = new Intent(CameraActivity.this, Camera_VideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("second",15);
        bundle.putBoolean("modify",true);
        it.putExtras(bundle);
        startActivityForResult(it,0);
    }

    public void prePhoto(View view){
        Intent it = new Intent(CameraActivity.this, MultiePreViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("pics",mediapaths);
        bundle.putBoolean("isedit",true);
        bundle.putBoolean("modify",true);
        it.putExtras(bundle);
        startActivityForResult(it,1);
    }

    public void picfactory(View view){
        Intent it = new Intent(CameraActivity.this, MultiplePicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("piccount",9);
        it.putExtras(bundle);
        startActivityForResult(it,2);
    }

    public void editPhoto(View view){

        if(TextUtils.isEmpty(editpath)){
            ToastUtils.showShort(this,"先拍照或者选图");
            return;
        }

        Intent it = new Intent(CameraActivity.this, ModifyPicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path",editpath);
        it.putExtras(bundle);
        startActivityForResult(it,2);
    }

    public void jhPhoto(View view){
        Intent it = new Intent(CameraActivity.this, JHPhotoActivity.class);
        startActivityForResult(it,3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 0:
                if(resultCode != RESULT_OK)
                    return;
                String path = data.getExtras().getString("mediapath");
                try{
                    //先压缩
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
//                BitMapUtils.mixCompress(bitmap,path);
                    //加水印
                    int picw = bitmap.getWidth();
                    int pich = bitmap.getHeight();
                    TextPaint p = new TextPaint ();

                    Bitmap canvasbitmap = Bitmap.createBitmap(picw, pich, Bitmap.Config.ARGB_8888);
                    Canvas cv = new Canvas(canvasbitmap);
                    cv.drawBitmap(bitmap, 0, 0, null);

                    int clock = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    if(clock < 18)
                        p.setColor(Color.rgb(255,167,38));
                    else
                        p.setColor(Color.rgb(255,255,255));
                    p.setStyle(Paint.Style.FILL);
                    p.setAntiAlias(true);
                    p.setTextSize(Math.min(picw,pich)/20);
                    String str = "经度:120.25685122\n纬度:30.1245562214\n地址:金华路99号\n时间:2018-09-29 15:55:55";
                    StaticLayout layout = null;
                    cv.save(Canvas.ALL_SAVE_FLAG);

                    TextPaint textPaint = new TextPaint(p);
                    textPaint.density = 2.0f;

                    if(picw > pich){
                        layout = new StaticLayout(str, textPaint, picw * 4 / 5, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                        cv.translate(picw / 10, pich * 2 /3);
                    }else{
                        layout = new StaticLayout(str, textPaint, picw * 4 / 5, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                        cv.translate(picw / 10, pich * 2 / 3);
                    }
                    layout.draw(cv);
                    cv.restore();

                    File myCaptureFile = new File(path);
                    if(myCaptureFile.exists()){
                        myCaptureFile.delete();
                        myCaptureFile.createNewFile();
                    }
                    BitMapUtils.saveFile(canvasbitmap,path,100);

                    if(bitmap != null)
                        bitmap.recycle();

                    if(canvasbitmap != null)
                        canvasbitmap.recycle();

                    //设置图片头部地理位置信息和读取信息
                    ExifInterface exif = new ExifInterface(path);

                    exif.setAttribute(ExifInterface.TAG_DATETIME,System.currentTimeMillis()+"");
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,"30/1,16/1,28/1");
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,"120/1,48/1,29/1");
                    exif.setAttribute(ExifInterface.TAG_COPYRIGHT,"30.1645879@120.25685122");

                    String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
                    Log.e("picture","TAG_DATETIME:" + dateTime);

                    float[] latlong = new float[2];
                    exif.getLatLong(latlong);
                    Log.e("latlong",latlong[0]+"@@@"+latlong[1]);


                    String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    Log.e("latitude","TAG_GPS_LATITUDE:" + lat);
                    String latef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                    Log.e("latitude","TAG_GPS_LATITUDE_REF:" + latef);
                    String lon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    Log.e("latitude","TAG_GPS_LONGITUDE:" + lon);
                    String lonef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                    Log.e("latitude","TAG_GPS_LONGITUDE_REF:" + lonef);
                    String copy = exif.getAttribute(ExifInterface.TAG_COPYRIGHT);
                    Log.e("TAG_COPYRIGHT",copy);
                }catch (Exception e){
                    e.printStackTrace();
                }

                mediapaths.add(path);
                editpath = path;
                break;
            case 2:
                if(resultCode != RESULT_OK)
                    return;
                ArrayList<String> pics = data.getExtras().getStringArrayList("pics");
                if(pics != null) {
                    mediapaths.addAll(pics);
                    editpath = pics.get(0);
                    String pic = pics.get(0);
                    try {
                        ExifInterface exif = new ExifInterface(pic);
                        float[] fts = new float[2];
                        exif.getLatLong(fts);
                        String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
                        Log.e("picture","TAG_DATETIME:" + dateTime);
                        String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                        Log.e("latitude","TAG_GPS_LATITUDE:" + lat);
                        String latef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                        Log.e("latitude","TAG_GPS_LATITUDE_REF:" + latef);
                        String lon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        Log.e("latitude","TAG_GPS_LATITUDE:" + lon);
                        String lonef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                        Log.e("latitude","TAG_GPS_LONGITUDE_REF:" + lonef);
                        float[] latlong = new float[2];
                        exif.getLatLong(latlong);
                        Log.e("latlong",latlong[0]+"@@@"+latlong[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                mediapaths.clear();
                mediapaths = data.getBundleExtra("bundle").getStringArrayList("piclist");
                break;
            case 3:
                if(resultCode == RESULT_OK){
                    ToastUtils.showShort(CameraActivity.this,data.getExtras().getString("mediapath"));
                }
                break;
        }
    }

    //引入百度逆地理编码
    public void baiduAddress(){

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
