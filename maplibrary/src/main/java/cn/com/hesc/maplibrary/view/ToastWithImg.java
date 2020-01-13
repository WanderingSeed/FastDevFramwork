package cn.com.hesc.maplibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.hesc.maplibrary.R;


/**
 * Created by liujunlin on 2015/6/30 11:20.
 * 自定义一个有图标的toast
 */
public class ToastWithImg {

    private Toast mToast;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ToastWithImg(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    /**
     * 展示带图标的toast
     * @param bitmap 要展示的图片,null隐藏图标
     * @param toastContent 要展示的内容
     */
    public void showToast(Bitmap bitmap, String toastContent){
        View view = mLayoutInflater.inflate(R.layout.toastwithimg,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.toastimg);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setVisibility(View.GONE);
        }

        TextView textView = (TextView)view.findViewById(R.id.toastcontent);
        if(toastContent!=null)
            textView.setText(toastContent);
        else
            textView.setText("");

        mToast = new Toast(mContext);
        mToast.setGravity(Gravity.CENTER, 0, 300);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(view);
        mToast.show();
    }

    /**
     * 带默认图标的toast
     * @param toastContent toast要显示的值
     */
    public void showToast(String toastContent){
        View view = mLayoutInflater.inflate(R.layout.toastwithimg,null);
        TextView textView = (TextView)view.findViewById(R.id.toastcontent);
        if(toastContent!=null)
            textView.setText(toastContent);
        else
            textView.setText("");

        mToast = new Toast(mContext);
        mToast.setGravity(Gravity.CENTER, 0, 300);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(view);
        mToast.show();
    }
}
