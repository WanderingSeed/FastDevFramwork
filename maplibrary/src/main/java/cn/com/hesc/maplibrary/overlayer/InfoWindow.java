package cn.com.hesc.maplibrary.overlayer;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;

import cn.com.hesc.maplibrary.R;

/**
 * created by liujunlin on 2019/1/15 09:38
 */
public class InfoWindow implements Serializable {

    private Context context;


    public InfoWindow(Context context){
        this.context = context;
    }

    private PopupWindow initPopupWindow(@Nullable String title,@Nullable String content,@Nullable Object imgsrc) {
        View view = LayoutInflater.from(context).inflate(R.layout.popupwindow,null);
        TextView titletv = (TextView)view.findViewById(R.id.title);
        TextView contenttv = (TextView)view.findViewById(R.id.content);
        ImageView imageView = (ImageView)view.findViewById(R.id.titleicon);
        titletv.setText(title == null?"":title);
        contenttv.setText(content == null?"":content);
        if(imgsrc != null){
            Glide.with(context).load(imgsrc).placeholder(R.drawable.defaultpic).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }
        PopupWindow popupWindow = new PopupWindow(view, (int)LinearLayout.LayoutParams.WRAP_CONTENT,/*(int)hight*/LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(true);

        return popupWindow;
    }

    public void showInfoWindow(View parent, LatLng offsety, @Nullable String title,@Nullable String content,@Nullable Object imgsrc){

        PopupWindow popupWindow = initPopupWindow(title,content,imgsrc);

        popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int w = popupWindow.getContentView().getMeasuredWidth();
        int h = popupWindow.getContentView().getMeasuredHeight();
//        popupWindow.showAsDropDown(parent,/*(int)Math.abs(popupWindow.getWidth()/2 - offsety.getmLat())*/-800,/*(int)(parent.getHeight() - offsety.getmLng() + popupWindow.getHeight())*/1000,Gravity.START);
//        popupWindow.showAtLocation(parent,Gravity.NO_GRAVITY,(int)offsety.getmLat(),(int)offsety.getmLng());
        popupWindow.showAtLocation(parent,Gravity.NO_GRAVITY,(int)offsety.getmLat(),0);

//        showAsDropDown(popupWindow,parent,(int)offsety.getmLat(),(int)offsety.getmLng());
    }

    /**
     *
     * @param pw     popupWindow
     * @param anchor v
     * @param xoff   x轴偏移
     * @param yoff   y轴偏移
     */
    public static void showAsDropDown(final PopupWindow pw, final View anchor, final int xoff, final int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            pw.setHeight(height);
            pw.showAsDropDown(anchor, xoff, yoff);
        } else {
            pw.showAsDropDown(anchor, Gravity.TOP,xoff, yoff);
        }
    }
}
