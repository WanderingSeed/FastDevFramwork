package cn.com.hesc.audiolibrary.audio;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.pocketdigi.utils.FLameUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.hesc.audiolibrary.R;


/**
 * ProjectName: FastDev-master
 * ClassName: RecordVoiceBtn
 * Description: 模仿微信里的按住说明按钮，可以自定义style
 * Author: liujunlin
 * Date: 2017-02-13 10:36
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class RecordVoiceBtn extends AppCompatButton {



    private OnFinishedRecordListener finishedListener;

    private static final int MIN_INTERVAL_TIME = 2000;// 2s
    private long startTime;

    private Dialog recordIndicator;

    private static int[] res = { R.drawable.mic_2, R.drawable.mic_3,
            R.drawable.mic_4, R.drawable.mic_5 };

    private static ImageView view;


    private Handler volumeHandler;

    private short[] mBuffer;
    //底层采样率等设置
    private AudioRecord mRecorder;
    //方便直接录音功能
    private MediaRecorder mMediaRecorder;

    private File tempFile;
    private String File_Voice = "";
    boolean isRecording = false;

    private String mFileName = null;
    private AUDIO_STYLE mAUDIOStyle = AUDIO_STYLE.RAW;
    private long intervalTime;
    private String audiopath;

    public enum AUDIO_STYLE{
        AMR,
        RAW
    }

    public AUDIO_STYLE getAUDIOStyle() {
        return mAUDIOStyle;
    }

    public void setAUDIOStyle(AUDIO_STYLE AUDIOStyle) {
        mAUDIOStyle = AUDIOStyle;
    }

    public RecordVoiceBtn(Context context) {
        super(context);
        init();
    }

    public RecordVoiceBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordVoiceBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSavePath(String path) {
        mFileName = path;
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
    }


    private void cancelRecord() {
        stopRecording();
        recordIndicator.dismiss();

        Toast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT).show();
        tempFile.delete();
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

    private void stopRecording() {
        isRecording = false;

        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initDialogAndStartRecord();
                break;
            case MotionEvent.ACTION_UP:
                finishRecord();
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                cancelRecord();
                break;
        }

        return true;
    }

    private void initDialogAndStartRecord() {

        startTime = System.currentTimeMillis();
        recordIndicator = new Dialog(getContext(),
               R.style.like_toast_dialog_style);
        view = new ImageView(getContext());
        view.setImageResource(R.drawable.mic_2);
        recordIndicator.setContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recordIndicator.setOnDismissListener(onDismiss);
        WindowManager.LayoutParams lp = recordIndicator.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;

        initRecorder(mAUDIOStyle);

        recordIndicator.show();
    }

    private String randomFilename(){
        String systime;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = c.getTime();
        systime = df.format(date);
        return systime;
    }

    /**
     * 初始化录音
     * 采用amr的话,直接用mediarecord,
     * 采用raw转mp3的话用audiorecord,为了转MP3
     * @param AUDIOStyle  .amr/.raw
     */
    private void initRecorder(AUDIO_STYLE AUDIOStyle) {

        try {

            String filename = randomFilename();
            File_Voice = Environment.getExternalStorageDirectory().getPath()  + "/zhdt/voice";
            File file = new File(File_Voice);
            if(!file.exists())
                file.mkdirs();

            String fileStuffix = AUDIOStyle == AUDIO_STYLE.AMR ? ".amr" : ".raw";

            tempFile = new File(File_Voice + File.separator + filename + fileStuffix);
            File fl = new File(File_Voice);
            if (!fl.exists())
                fl.createNewFile();

            //暂时将采样率改为8000   44100
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

                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    // 设置MediaRecorder的音频源为麦克风
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    // 设置MediaRecorder录制的音频格式
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    // 设置MediaRecorder录制音频的编码为amr.
                    mMediaRecorder.setOutputFile(tempFile.getAbsolutePath());
                    mMediaRecorder.setAudioSamplingRate(8000);
                    mMediaRecorder.setAudioEncodingBitRate(16);
                    mMediaRecorder.prepare();// 准备录制
                    mMediaRecorder.start();// 开始录制
            }
        }catch (Exception e){
            e.printStackTrace();
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

    private void finishRecord() {
        if(mRecorder!=null){
            stopRecording();
        }else if(mMediaRecorder!=null){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        recordIndicator.dismiss();

        intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(getContext(), "时间太短！", Toast.LENGTH_SHORT).show();
            tempFile.delete();
            return;
        }

        if (tempFile.exists()) {
            audiopath = tempFile.getAbsolutePath();
            if(mAUDIOStyle == AUDIO_STYLE.RAW){
                final String filename = tempFile.getName();
                audiopath = tempFile.getParent() + "/"
                        + filename.substring(0, filename.length() - 4) + ".mp3";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        chang2mp3(tempFile.getAbsolutePath(), audiopath);
                        tempFile.delete();
                        if (finishedListener != null)
                            finishedListener.onFinishedRecord(new File(audiopath),intervalTime);
                    }
                }).start();

            }else{
                if (finishedListener != null)
                    finishedListener.onFinishedRecord(new File(audiopath),intervalTime);
            }

        }

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
            FLameUtils lameUtils = new FLameUtils(1, 44100, 96);
            lameUtils.raw2mp3(srcfile, desfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    static class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            view.setImageResource(res[msg.what]);
        }
    }

    public interface OnFinishedRecordListener {
        public void onFinishedRecord(File audioPath, long recordtime);
    }


}
