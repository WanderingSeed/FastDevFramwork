package cn.com.hesc.maplibrary.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.hesc.maplibrary.R;


/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: WaitProgressDialog
 * Description: 加等待光标的对话框
 * Author: liujunlin
 * Date: 2016-06-30 14:31
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class WaitProgressDialog extends Dialog {

    private static Context context = null;
    private static WaitProgressDialog customProgressDialog = null;

    public WaitProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public WaitProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static WaitProgressDialog createDialog(Context context) {
        customProgressDialog = new WaitProgressDialog(context,
                R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.waitdialogbg);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        customProgressDialog.context = context;

        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        if (customProgressDialog == null) {
            return;
        }

        ImageView imageView = (ImageView) customProgressDialog
                .findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView
                .getBackground();
        animationDrawable.start();
    }

    /**
     *
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public WaitProgressDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null){
            if(!TextUtils.isEmpty(strMessage))
                tvMsg.setText(strMessage);
            else
                tvMsg.setVisibility(View.GONE);
        }

        return customProgressDialog;
    }
}
