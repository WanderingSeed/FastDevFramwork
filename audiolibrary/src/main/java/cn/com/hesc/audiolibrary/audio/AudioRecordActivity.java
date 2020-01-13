package cn.com.hesc.audiolibrary.audio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.com.hesc.audiolibrary.R;


/**
 * 音频录制
 */
public class AudioRecordActivity extends AppCompatActivity {

    private TextView audioflag;
    private ImageButton delrecord,donerecord;
    private RecordVoiceBtn recordmedia;
    private RecordAudioView audioflagview;
    private RelativeLayout flagline;
    private boolean isplaying = false;
    private File mAudioPath;
    private long audioTime;
    private ImageView playstopaudio;
    private MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        initView();
    }

    private void initView() {
        audioflag = (TextView)findViewById(R.id.audioflag);
        delrecord = (ImageButton)findViewById(R.id.delrecord);
        donerecord = (ImageButton)findViewById(R.id.donerecord);
        recordmedia = (RecordVoiceBtn)findViewById(R.id.recordmedia);
        recordmedia.setOnFinishedRecordListener(new RecordVoiceBtn.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(final File audioPath, final long recordtime) {
                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("type","1");
                bundle.putString("path",audioPath.getAbsolutePath());
                bundle.putLong("time",recordtime);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

            }
        });
        audioflagview = (RecordAudioView)findViewById(R.id.audioflagview);
        flagline = (RelativeLayout)findViewById(R.id.flagline);

        delrecord = (ImageButton)findViewById(R.id.delrecord);
        delrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAudioPath.exists() && mAudioPath.isFile()) {
                    mAudioPath.delete();
                }
                audioflag.setVisibility(View.VISIBLE);
                flagline.setVisibility(View.GONE);
                showToolsBtn(false);

            }
        });
        donerecord = (ImageButton)findViewById(R.id.donerecord);
        donerecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolsBtn(false);
                if(mAudioPath.exists()){
                    Intent it = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putLong("voicelength",audioTime);
                    bundle.putString("mediapath", TextUtils.isEmpty(mAudioPath.getAbsolutePath())?"":mAudioPath.getAbsolutePath());
                    it.putExtras(bundle);
                    AudioRecordActivity.this.setResult(RESULT_OK,it);
                    AudioRecordActivity.this.finish();
                }

            }
        });
        playstopaudio = (ImageView)findViewById(R.id.playstopaudio);
        playstopaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isplaying = !isplaying;
                if(isplaying){
                    checkMediaPlayer();
                    play();
                }else{
                    stop();
                }
            }
        });
    }

    private void checkMediaPlayer(){
        if(mediaplayer == null){
            mediaplayer = new MediaPlayer();
            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mediaplayer.setDataSource(mAudioPath.getAbsolutePath());
            mediaplayer.prepare();
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
            if(msg.getData().getString("type").equals("0")){
                int progress = msg.getData().getInt("progress");
                float angle = progress * 1.0f /audioTime * 360;
                Log.e("rotation",""+angle);
                audioflagview.setSweepAngle(angle);
            }else if(msg.getData().getString("type").equals("1")){
                mAudioPath = new File(msg.getData().getString("path"));
                audioTime = msg.getData().getLong("time");
                audioflag.setVisibility(View.GONE);
                flagline.setVisibility(View.VISIBLE);
                showToolsBtn(true);
                Toast.makeText(AudioRecordActivity.this,"存储路径为："+mAudioPath.getAbsolutePath()+",时长:"+audioTime,Toast.LENGTH_LONG).show();
            }

        }
    };

    private void play(){
        playstopaudio.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.audiorecordstop));
        try {
            mediaplayer.start();
            isplaying  = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isplaying){
                        Message msg = mHandler.obtainMessage();
                        Bundle bu = new Bundle();
                        bu.putInt("progress",mediaplayer.getCurrentPosition());
                        bu.putString("type","0");
                        msg.setData(bu);
                        mHandler.sendMessage(msg);
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop(){
        isplaying = false;
        mediaplayer.stop();
        mediaplayer.release();
        mediaplayer = null;
        audioflagview.setSweepAngle(0);
        playstopaudio.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.audiorecordplay));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()){
            if(mediaplayer != null){
                mediaplayer.stop();
                mediaplayer.release();
                mediaplayer = null;
            }
        }

    }

    private void showToolsBtn(boolean isShow){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int mWidth = wm.getDefaultDisplay().getWidth();
        int mHeight = wm.getDefaultDisplay().getHeight();
        if(isShow){
            recordmedia.setVisibility(View.GONE);
            AnimatorSet as = new AnimatorSet();
            ObjectAnimator ob1 = ObjectAnimator.ofFloat(delrecord,"alpha",0,1);
            ObjectAnimator ob11 = ObjectAnimator.ofFloat(delrecord,"translationX",0,-mWidth/4);
            ObjectAnimator ob2 = ObjectAnimator.ofFloat(donerecord,"alpha",0,1);
            ObjectAnimator ob21 = ObjectAnimator.ofFloat(donerecord,"translationX",0,mWidth/4);
            as.setDuration(500L);
            as.playTogether(ob1,ob2,ob11,ob21);
            as.setInterpolator(new BounceInterpolator());
            as.start();
            as.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });
        }else{

            AnimatorSet as = new AnimatorSet();
            ObjectAnimator ob1 = ObjectAnimator.ofFloat(delrecord,"alpha",1,0);
            ObjectAnimator ob11 = ObjectAnimator.ofFloat(delrecord,"translationX",mWidth/4,0);
            ObjectAnimator ob2 = ObjectAnimator.ofFloat(donerecord,"alpha",1,0);
            ObjectAnimator ob21 = ObjectAnimator.ofFloat(donerecord,"translationX",mWidth/4,0);
            as.setDuration(500L);
            as.playTogether(ob1,ob2,ob11,ob21);
            as.setInterpolator(new BounceInterpolator());
            as.start();
            as.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recordmedia.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
