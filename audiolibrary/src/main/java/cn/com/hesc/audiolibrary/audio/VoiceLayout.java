package cn.com.hesc.audiolibrary.audio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * ProjectName: FastDev-master
 * ClassName: VoiceLayout
 * Description: TODO
 * Author: liujunlin
 * Date: 2017-02-13 14:39
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class VoiceLayout extends LinearLayout{

    private VoiceTextView myTextView;
    private ImageView animimgView;
    private ImageView delimgviewImageView;
    private boolean deleting = false;

    public VoiceLayout(Context context) {
        super(context);
    }

    public VoiceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView getAnimimgView() {
        return animimgView;
    }

    public void setAnimimgView(ImageView animimgView) {
        this.animimgView = animimgView;
    }

    public boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public ImageView getDelimgviewImageView() {
        return delimgviewImageView;
    }

    public void setDelimgviewImageView(ImageView delimgviewImageView) {
        this.delimgviewImageView = delimgviewImageView;
    }

    public VoiceTextView getMyTextView() {
        return myTextView;
    }

    public void setMyTextView(VoiceTextView myTextView) {
        this.myTextView = myTextView;
    }
}
