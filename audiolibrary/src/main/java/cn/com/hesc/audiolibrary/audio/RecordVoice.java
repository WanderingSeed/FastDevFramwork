package cn.com.hesc.audiolibrary.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import com.pocketdigi.utils.FLameUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * ProjectName: Java_JS
 * ClassName: RecordVoice
 * Description: 提供录音功能
 * Author: liujunlin
 * Date: 2017-11-07 11:37
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class RecordVoice {

    public enum AUDIO_STYLE{
        AMR,
        RAW
    }

    private OnFinishedRecordListener finishedListener;

    private static final int MIN_INTERVAL_TIME = 2000;// 2s
    private long startTime;
    private Handler volumeHandler;

    private short[] mBuffer;
    //底层采样率等设置
    private AudioRecord mRecorder;
    //方便直接录音功能
    private MediaRecorder mMediaRecorder;

    private File tempFile;
    public boolean isRecording = false;
    public boolean isTimeing = true;
    private boolean timeenable = false;
    private String dir,fileName;

    private String mFileName = null;
    private AUDIO_STYLE mAUDIOStyle = AUDIO_STYLE.RAW;

    public AUDIO_STYLE getAUDIOStyle() {
        return mAUDIOStyle;
    }

    public void setAUDIOStyle(AUDIO_STYLE AUDIOStyle) {
        mAUDIOStyle = AUDIOStyle;
    }

    public OnFinishedRecordListener getFinishedListener() {
        return finishedListener;
    }

    public void setFinishedListener(OnFinishedRecordListener finishedListener) {
        this.finishedListener = finishedListener;
    }

    public boolean isTimeenable() {
        return timeenable;
    }

    public void setTimeenable(boolean timeenable) {
        this.timeenable = timeenable;
    }

    public  RecordVoice(String dir,String fileName){
        this.dir = dir;
        this.fileName = fileName;
        init();
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
    }

    private void cancelRecord() {
        stopRecording();
        tempFile.delete();
    }

    /**
     * 停止录音
     */
    public synchronized void stopRecording() {
        if(!isRecording)
            return;

        if(isTimeing)
            isTimeing = !isTimeing;

        if(mRecorder!=null){
            isRecording = false;
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }else if(mMediaRecorder!=null){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        long intervalTime = System.currentTimeMillis() - startTime;

        String filename = tempFile.getName();
        String voicefle = tempFile.getParent() + "/"
                + filename.substring(0, filename.length() - 4) + ".mp3";
        chang2mp3(tempFile.getAbsolutePath(), tempFile.getParent()
                + "/" + filename.substring(0, filename.length() - 4)
                + ".mp3");
        tempFile.delete();

        if (finishedListener != null)
            finishedListener.onFinishedRecord(voicefle,intervalTime);
    }

    /**
     * 转为mp3文件
     *
     * @author liujunlin
     * @param srcfile
     *            源文件
     * @param desfile
     *            目标文件
     * @time 2013-8-15
     */
    private void chang2mp3(String srcfile, String desfile) {
        try{
            FLameUtils lameUtils = new FLameUtils(1, 16000, 96);
            lameUtils.raw2mp3(srcfile, desfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     */
    public synchronized void startRecord(){
        if(isRecording)
            return;
        startTime = System.currentTimeMillis();
        initRecorder(mAUDIOStyle);

        if(timeenable){
            //创建监听录音时长
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isTimeing){
                        if(System.currentTimeMillis() - startTime > 20*1000){
                            isTimeing = false;
                            stopRecording();
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * 初始化录音
     * 采用amr的话,直接用mediarecord,
     * 采用raw转mp3的话用audiorecord,为了转MP3
     * @param AUDIOStyle  .amr/.raw
     */
    private void initRecorder(AUDIO_STYLE AUDIOStyle) {


        String fileStuffix = AUDIOStyle == AUDIO_STYLE.AMR ? ".amr" : ".raw";

        tempFile = new File(dir + File.separator + fileName + fileStuffix);
        if (!tempFile.exists())
            tempFile.mkdirs();

        if(fileStuffix.equals(".raw")){
            int bufferSize = AudioRecord.getMinBufferSize(16000,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            mBuffer = new short[bufferSize];
            mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            mRecorder.startRecording();
            recordVoice();
        }else if(fileStuffix.equals(".amr")){
            try {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 设置MediaRecorder的音频源为麦克风
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                // 设置MediaRecorder录制的音频格式
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                // 设置MediaRecorder录制音频的编码为amr.
                mMediaRecorder.setOutputFile(tempFile.getAbsolutePath());
                mMediaRecorder.prepare();// 准备录制
                mMediaRecorder.start();// 开始录制
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 录制音频文件
     *
     * @author liujunlin
     * @time 2013-8-7
     */
    private void recordVoice() {
        isRecording = true;
        try {
            startBufferedWrite(tempFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入到文件
     *
     * @param file
     */
    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(
                            new FileOutputStream(file)));
                    while (isRecording) {
                        int readSize = mRecorder.read(mBuffer, 0,
                                mBuffer.length);
                        int v = 0;
                        for (int i = 0; i < readSize; i++) {

                            // 这里没有做运算的优化，为了更加清晰的展示代码
                            v += mBuffer[i] * mBuffer[i];

                            output.writeShort(mBuffer[i]);

                        }
                        if (v != 0) {

                            int f = (int) (Math.abs((int)(v /(float)readSize)/10000) >> 1);

                            if (f < 50)
                                volumeHandler.sendEmptyMessage(0);
                            else if (f < 60)
                                volumeHandler.sendEmptyMessage(1);
                            else if (f < 70)
                                volumeHandler.sendEmptyMessage(2);
                            else
                                volumeHandler.sendEmptyMessage(3);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    static class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    public interface OnFinishedRecordListener {
        public void onFinishedRecord(String audioPath, long recordtime);
    }
}
