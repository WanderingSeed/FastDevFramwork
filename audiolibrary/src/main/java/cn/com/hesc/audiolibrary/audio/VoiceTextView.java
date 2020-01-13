package cn.com.hesc.audiolibrary.audio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * ProjectName: FastDev-master
 * ClassName: VoiceTextView
 * Description: 用法和系统的textview一致，只是多加了2个属性，文件名和录制的毫秒数
 * Author: liujunlin
 * Date: 2017-02-13 14:55
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class VoiceTextView extends TextView{

    private String voicePath;
    private long length;

    public VoiceTextView(Context context) {
        super(context);
    }

    public VoiceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }
}
