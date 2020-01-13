package cn.com.hesc.maplibrary.overlayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.Serializable;

import cn.com.hesc.maplibrary.R;
import cn.com.hesc.materialdialogs.HescMaterialDialog;
import cn.com.hesc.tools.DensityUtils;


/**
 * 定义路径点的数据结构，可以将数据结构保存导入到地图中
 * created by liujunlin on 2018/7/16 10:39
 */
public class Marker implements Serializable{
    private LatLng latLng;
    private Bitmap mBitmap;
    private String title;
    private String content;

    public Marker(LatLng latLng){
        this.latLng = latLng;
    }

    public boolean isClick(LatLng point, Context context){

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        //获取dpi
        int dpi = dm.densityDpi;

        double dist = 0.5F + 1 * dpi/160.0;
        if(Math.abs(point.getmLat() - latLng.getmLat())<=dist || Math.abs(point.getmLng() - latLng.getmLng())<=dist){
            //展示infowindow
//            infoWindow.showInfoWindow(view,point);

            //显示dialog
            View dialogcontent = LayoutInflater.from(context).inflate(R.layout.dialogmark,null);
            TextView titletv = (TextView)dialogcontent.findViewById(R.id.username);
            titletv.setText(title);
            TextView usercode = (TextView)dialogcontent.findViewById(R.id.usercode);
            usercode.setText(content);

            HescMaterialDialog hescMaterialDialog = new HescMaterialDialog(context);
            hescMaterialDialog.showCustomViewDialog("", dialogcontent, "确定", "", new HescMaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(HescMaterialDialog dialog) {
                    super.onPositive(dialog);
                }

                @Override
                public void onNegative(HescMaterialDialog dialog) {
                    super.onNegative(dialog);
                }
            });


//            AppCompatDialog dialog = new AppCompatDialog(context);
//            dialog.setTitle("信息显示");
//            dialog.setContentView(dialogcontent);
//            dialog.setCanceledOnTouchOutside(true);
//            dialog.show();
            return true;
        }
        return false;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
