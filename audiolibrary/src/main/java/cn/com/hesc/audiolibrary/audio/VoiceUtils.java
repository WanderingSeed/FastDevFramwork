package cn.com.hesc.audiolibrary.audio;

import android.media.MediaPlayer;
import android.text.TextUtils;

/**
 * ProjectName: Java_JS
 * ClassName: VoiceUtils
 * Description: 播放语音工具类
 * Author: liujunlin
 * Date: 2017-11-07 15:51
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class VoiceUtils {
    private MediaPlayer mPlayer;
    private boolean isPlayVoice;
    private String curVoicePath;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    public VoiceUtils(){
        mPlayer = new MediaPlayer();
    }

    public MediaPlayer getPlayer() {
        if(mPlayer == null)
            mPlayer = new MediaPlayer();
        return mPlayer;
    }

    public void setPlayer(MediaPlayer player) {
        mPlayer = player;
    }

    public MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return mOnCompletionListener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    public void startPlay(String path){
        if(isPlayVoice && mPlayer!=null && !TextUtils.isEmpty(curVoicePath)){
            return;
        }

        try {

            curVoicePath = path;
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();

            isPlayVoice = true;
//            if (mPlayer.isPlaying()) {
//
//                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        stopPlayVoice();
//                    }
//                });
//            }

            mPlayer.setOnCompletionListener(mOnCompletionListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlayVoice(){
        if(mPlayer == null)
            return;
        isPlayVoice = false;
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        curVoicePath = "";
    }
}
