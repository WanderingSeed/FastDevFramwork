package cn.com.hesc.picture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.tools.BitMapUtils;
import cn.com.hesc.tools.DisplayUtils;
import cn.com.hesc.tools.SdcardInfo;
import cn.com.hesc.tools.ToastUtils;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
import static android.hardware.Camera.Parameters.FLASH_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;

/**
 * 自定义拍照和录像功能的activity
 */
public class Camera_VideoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView close,flashflag,cameradirect;
    private ProgressBar vedioprogress;
    private SurfaceView camerasurfaceview,presurfaceview;
    private ImageButton recordmedia,delrecord,donerecord,modifyrecord;
    private final int PROGRESSSECOND = 10;//默认视频录制10秒
    private boolean isOpenFlash = false;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mMediaPlayer;
    private int cameraPosition;//默认是后置摄像头
    private boolean isCameraBack = true;
    private int cameraRotation;
    private DisplayUtils d;
    private SdcardInfo mSdcard;
    private String mediaPath = "";
    private boolean openFlash = false,isRecording = false,capturePic = false;
    private int captureispic = -1;//当前操作类型，-1 没有任何操作 0 拍照 1录像
    private int cursecond = 0;
    private int mOrientation = 90;
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private List<Camera.Size> prviewSizeList;
    private List<Camera.Size> videoSizeList;
    private long downtime = 0;
    private int maxSecond = PROGRESSSECOND;
    private ExifInterface exif;
    private boolean allowRecord = false;

    private boolean modify = false;
    private FrameLayout modifypiclayout;
    private ModifyImageView modifyImageView;
    private ImageView white,black,red,green,blue;
    private TextView cancel,ok;
    private File srcfile;
    private RelativeLayout titletoolline;

    private MediaRecorder.OnErrorListener onErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {

            Log.e("MediaRecorder onError","what:"+what+"extra:"+extra);

            try {
                if (mediaRecorder != null) {
                    mediaRecorder.setOnErrorListener(null);
                    mediaRecorder.setPreviewDisplay(null);
                    //停止录制
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    //释放资源
                    mediaRecorder.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        } else {
            Log.d("chengcj1", "Can't Detect Orientation");
        }


        setContentView(R.layout.activity_camera__video);

        if(getIntent().getExtras()!=null) {
            maxSecond = getIntent().getExtras().getInt("second", PROGRESSSECOND);
            allowRecord = getIntent().getExtras().getBoolean("allowrecord",false);
            modify =  getIntent().getExtras().getBoolean("modify",false);
        }

        initView();
        mSdcard = SdcardInfo.getInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()){
            mAlbumOrientationEventListener.disable();
            stopCamera();
            if(mMediaPlayer != null){
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if(mediaRecorder != null){
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }
    }



    private void initView() {
        close = (TextView)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera_VideoActivity.this.finish();
            }
        });
        flashflag = (TextView)findViewById(R.id.flashbtn);
        flashflag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFlash = !openFlash;
                flashflag.setBackgroundResource(openFlash?R.drawable.openflash:R.drawable.closeflash);
                resetCamera(cameraPosition);
            }
        });
        cameradirect = (TextView)findViewById(R.id.fbcamera);
        cameradirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCameraPosition();
            }
        });
        vedioprogress = (ProgressBar)findViewById(R.id.vedioprogress);
        vedioprogress.setMax(Math.max(maxSecond,PROGRESSSECOND));
        camerasurfaceview = (SurfaceView)findViewById(R.id.camerasurfaceview);
        presurfaceview = (SurfaceView)findViewById(R.id.previewsurfaceview);
        recordmedia = (ImageButton)findViewById(R.id.recordmedia);
        titletoolline = (RelativeLayout)findViewById(R.id.titletoolline);
        titletoolline.setVisibility(modify?View.GONE:View.VISIBLE);
        if(allowRecord){
            recordmedia.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    captureispic = 1;
                    vedioprogress.setVisibility(View.VISIBLE);
                    ToastUtils.showShort(Camera_VideoActivity.this,"开始录制");
                    mediaPath = SdcardInfo.File_Video + File.separator + System.currentTimeMillis()+".mp4";
                    startRecord();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (cursecond < maxSecond){
                                Message msg =  mHandler.obtainMessage();
                                Bundle bu = new Bundle();
                                bu.putInt("second",++cursecond);
                                msg.setData(bu);
                                mHandler.sendMessage(msg);
                                try {
                                    Thread.sleep(1000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            cursecond = 0;
                        }
                    }).start();

                    return false;
                }
            });
        }

        recordmedia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(captureispic == 1){
                        stopRecord();
                        captureispic = -1;
                    }else if(captureispic == 0){
                        capturePic();
                    }

                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    captureispic = 0;
                }
                return false;
            }
        });
        delrecord = (ImageButton)findViewById(R.id.delrecord);
        delrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPath.contains(".mp4")) {
                    cursecond = 0;
                    resetCamera(cameraPosition);
                }
                else
                    mCamera.startPreview();
                File file = new File(mediaPath);
                if(file.exists() && file.isFile()) {
                    file.delete();
                    mediaPath = "";
                }
                showToolsBtn(false);
                presurfaceview.setVisibility(View.INVISIBLE);
                camerasurfaceview.setVisibility(View.VISIBLE);

            }
        });
        donerecord = (ImageButton)findViewById(R.id.donerecord);
        donerecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //录制完成，将自定义的文件放入媒体库
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, mediaPath);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                File file = new File(mediaPath);
                if(file.exists() && file.isFile() && (file.getAbsolutePath().toLowerCase().contains("jpg")||file.getAbsolutePath().toLowerCase().contains("png")||file.getAbsolutePath().toLowerCase().contains("jpeg"))){
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                }

                showToolsBtn(false);
                Intent it = getIntent();
                it.putExtra("mediapath", TextUtils.isEmpty(mediaPath)?"":mediaPath);
                Camera_VideoActivity.this.setResult(RESULT_OK,it);
                Camera_VideoActivity.this.finish();
            }
        });
        modifyrecord = (ImageButton)findViewById(R.id.modifyrecord);
        modifyrecord.setVisibility(modify?View.VISIBLE:View.GONE);
        modifyrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifypiclayout.setVisibility(View.VISIBLE);
                String path = mediaPath;
                if("http".contains(path) || "https".contains(path)){
                    ToastUtils.showLong(Camera_VideoActivity.this,"暂不支持编辑网络图片");
                }else{
                    srcfile = new File(path);
                    if(srcfile.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        if(bitmap.getWidth() < bitmap.getHeight()){
                            modifyImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        modifyImageView.setImageBitmap(BitmapFactory.decodeFile(path));
                    }
                }
            }
        });

        d = new DisplayUtils(this);

        //配置SurfaceHodler
        mSurfaceHolder = camerasurfaceview.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
        mSurfaceHolder.setFixedSize(d.getWidth(), d.getHeight());
        // 设置该组件不会让屏幕自动关闭
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(mCallBack); //相机创建回调接口

        //初始化涂鸦部分
        modifypiclayout = (FrameLayout)findViewById(R.id.modifypiclayout);
        modifyImageView = (ModifyImageView)findViewById(R.id.modifyImageView);
        white = (ImageView)findViewById(R.id.white);
        white.setOnClickListener(this);
        black = (ImageView)findViewById(R.id.black);
        black.setOnClickListener(this);
        red = (ImageView)findViewById(R.id.red);
        red.setOnClickListener(this);
        green = (ImageView)findViewById(R.id.green);
        green.setOnClickListener(this);
        blue = (ImageView)findViewById(R.id.blue);
        blue.setOnClickListener(this);
        cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        ok = (TextView)findViewById(R.id.ok);
        ok.setOnClickListener(this);

    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            int progress = msg.getData().getInt("second");
            vedioprogress.setProgress(progress);
//            if("success".equals(msg.getData().getString("record",""))){
//                stopRecord();
//            }
        }
    };

    /**
     * 开始录制视频
     */
    public void startRecord() {
        try {
            File file = new File(mediaPath);
            if(!file.exists())
                file.createNewFile();
//            initCamera();
            mCamera.unlock();
            setConfigRecord();
            //开始录制
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            if (mCamera != null) {
                resetCamera(cameraPosition);
            }
            if(mediaRecorder != null){
                mediaRecorder.release();
                mediaRecorder = null;
            }

        }
        isRecording = true;
    }

    /**
     * 配置MediaRecorder()
     */
    private void setConfigRecord() throws Exception{
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOnErrorListener(onErrorListener);

        //使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        //1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置立体声
        //        mediaRecorder.setAudioChannels(2);
        //设置最大录像时间 单位：毫秒
        //        mediaRecorder.setMaxDuration(60 * 1000);
        //设置最大录制的大小 单位，字节
        //        mediaRecorder.setMaxFileSize(1024 * 1024);
        //音频一秒钟包含多少数据位

        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile.videoBitRate > 5 * 1024 * 1024)
            mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        else
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);

        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        if((mOrientation > 30 && mOrientation < 150)){
            mediaRecorder.setOrientationHint(cameraRotation+90);
        }else if(mOrientation > 210 && mOrientation < 330){
            mediaRecorder.setOrientationHint(0);
        }
        else{
            if(cameraPosition == CAMERA_FACING_FRONT)
                mediaRecorder.setOrientationHint(cameraRotation+180);
            else
                mediaRecorder.setOrientationHint(cameraRotation);
        }
//        mediaRecorder.setOrientationHint(90);

        //设置录像的分辨率
        int index=bestVideoSize(prviewSizeList.get(0).width);
        mediaRecorder.setVideoSize(videoSizeList.get(index).width,videoSizeList.get(index).height);


//        mediaRecorder.setVideoSize(176, 144);



        mediaRecorder.setOutputFile(mediaPath);

    }

    //查找出最接近的视频录制分辨率
    public int bestVideoSize(int _w){
        //降序排列
        Collections.sort(videoSizeList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width > rhs.width) {
                    return -1;
                } else if (lhs.width == rhs.width) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for(int i=0;i<videoSizeList.size();i++){
            if(videoSizeList.get(i).width <= _w){
                return i;
            }
        }
        return 0;
    }

    /**
     * 停止录制视频
     */
    public void stopRecord() {
        Log.e("stopRecord",System.currentTimeMillis()+"");
//        if(mCamera != null)
//            mCamera.unlock();

        try {

        if (isRecording && mediaRecorder != null) {
            isRecording = false;
            // 设置后不会崩
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setPreviewDisplay(null);
            //停止录制
            mediaRecorder.stop();
            mediaRecorder.reset();
            //释放资源
            mediaRecorder.release();
            mediaRecorder = null;
        }
        showToolsBtn(true);

        vedioprogress.setVisibility(View.INVISIBLE);
        camerasurfaceview.setVisibility(View.INVISIBLE);
        presurfaceview.setVisibility(View.VISIBLE);


            if (mMediaPlayer == null)
            {
                mMediaPlayer = new MediaPlayer();
            }

            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                mMediaPlayer = new MediaPlayer();
            }
            // 设置分辨率
            SurfaceHolder surfaceHolder = presurfaceview.getHolder();

            // 设置该组件不会让屏幕自动关闭
            surfaceHolder.setKeepScreenOn(true);
            mMediaPlayer.setDataSource(mediaPath);
            if(mMediaPlayer.getVideoWidth() > mMediaPlayer.getVideoHeight()){
                surfaceHolder.setFixedSize(d.getWidth(),d.getHeight()/3);
            }else{
                surfaceHolder.setFixedSize(d.getWidth(), d.getHeight());
            }
            mMediaPlayer.setDisplay(surfaceHolder);

            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setLooping(true);

        } catch (Exception e) {
            e.printStackTrace();
            if(mCamera != null){
                resetCamera(cameraPosition);
            }
            ToastUtils.showShort(Camera_VideoActivity.this,"录制失败，请重试");
        }
    }

    private void capturePic(){

        if(capturePic)
            return;
        capturePic = true;
        mediaPath = SdcardInfo.File_Pic + File.separator + System.currentTimeMillis()+".jpg";
        try {
        if(mCamera == null){
            initCamera();
        }

//            File file = new File(mediaPath);
//            if(!file.exists())
//                file.createNewFile();

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                Camera.CameraInfo info =
                        new Camera.CameraInfo();
                Camera.getCameraInfo(cameraPosition, info);
                mCamera.stopPreview();

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Matrix matrix = new Matrix();
                            //认为横屏
                            if((mOrientation > 30 && mOrientation < 150)/*||(mOrientation > 210 && mOrientation < 330)*/){
                                matrix.postRotate(cameraRotation+90);
                            }else if(mOrientation > 210 && mOrientation < 330){
                                matrix.postRotate(cameraRotation-90);
                            }
                            else{
                                if(cameraPosition == CAMERA_FACING_FRONT)
                                    matrix.postRotate(cameraRotation+180);
                                else
                                    matrix.postRotate(cameraRotation);
                            }

                            // 创建新的图片
                            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            //加入翻转 把相机拍照返回照片转正
                            BitMapUtils.saveFile(resizedBitmap,mediaPath,80);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToolsBtn(true);
                                    captureispic = -1;
                                    capturePic = false;
                                }
                            });
//                    if(file.length() > 100*1024){
//                        Bitmap bitmap1 = BitMapUtils.scalePicture(picPath,640,480,false);
//                        BitMapUtils.saveFile(bitmap1,picPath,100);
//                    }

                    }
                }).start();
            }
        });

        } catch (Exception e) {
            e.printStackTrace();
            capturePic = false;
        }
    }

    private void showToolsBtn(boolean isShow){
//        modifyrecord.setVisibility(isShow?View.VISIBLE:View.GONE);
        if(isShow){
            recordmedia.setVisibility(View.GONE);
            AnimatorSet as = new AnimatorSet();
            ObjectAnimator ob1 = ObjectAnimator.ofFloat(delrecord,"alpha",0,1);
            ObjectAnimator ob11 = ObjectAnimator.ofFloat(delrecord,"translationX",0,-d.getWidth()/4);
            ObjectAnimator ob2 = ObjectAnimator.ofFloat(donerecord,"alpha",0,1);
            ObjectAnimator ob21 = ObjectAnimator.ofFloat(donerecord,"translationX",0,d.getWidth()/4);
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
            ObjectAnimator ob11 = ObjectAnimator.ofFloat(delrecord,"translationX",d.getWidth()/4,0);
            ObjectAnimator ob2 = ObjectAnimator.ofFloat(donerecord,"alpha",1,0);
            ObjectAnimator ob21 = ObjectAnimator.ofFloat(donerecord,"translationX",d.getWidth()/4,0);
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

    private SurfaceHolder.Callback mCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            stopCamera();
        }
    };

    /**
     * 初始化摄像头
     *
     */
    private void initCamera() {
        if (mCamera != null) {
            stopCamera();
        }
        //默认启动后置摄像头
        if(isCameraBack){
            mCamera = Camera.open(CAMERA_FACING_BACK);//打开后置摄像头
            cameraPosition = CAMERA_FACING_BACK;
        }else{
            mCamera = Camera.open(CAMERA_FACING_FRONT);//打开前置摄像头
            cameraPosition = CAMERA_FACING_FRONT;
        }
        if (mCamera == null) {
            Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //配置CameraParams
            setCameraParams();
        } catch (IOException e) {
            Log.d("camera is not init", "Error starting camera preview: " + e.getMessage());
        }
        //启动相机预览
        mCamera.startPreview();

    }


    /**
     * 设置摄像头为竖屏
     *
     * @author liuzhongjun
     * @date 2016-3-16
     */
    private void setCameraParams() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            //设置相机的横竖屏幕
//            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//                params.set("Orientation", "portrait");
//                mCamera.setDisplayOrientation(90);
//            } else {
//                params.set("Orientation", "landscape");
//                mCamera.setDisplayOrientation(0);
//            }
            setCameraDisplayOrientation(this,cameraPosition,mCamera);
            //设置聚焦模式
            if(cameraPosition == CAMERA_FACING_BACK) {
                params.setFocusMode(FOCUS_MODE_CONTINUOUS_VIDEO);
                //设置闪光灯模式,后置摄像头才设置这个参数
                params.setFlashMode(FLASH_MODE_AUTO);
            }

            //缩短Recording启动时间
            params.setRecordingHint(true);
            //是否支持影像稳定能力，支持则开启
            if (params.isVideoStabilizationSupported())
                params.setVideoStabilization(true);
            //获取手机支持的预览和视频大小
            prviewSizeList = params.getSupportedPreviewSizes();
            videoSizeList = params.getSupportedVideoSizes();


            List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
            List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();

            for (int i=0; i<pictureSizes.size(); i++) {
                Camera.Size pSize = pictureSizes.get(i);
                Log.i("-------initCamera", "---------------------PictureSize.width = "+pSize.width+"-----------------PictureSize.height = "+pSize.height);
            }

            for (int i=0; i<previewSizes.size(); i++) {
                Camera.Size pSize = previewSizes.get(i);
                Log.i("--------initCamera", "--------------------previewSize.width = "+pSize.width+"-----------------previewSize.height = "+pSize.height);
            }


            CameraSize cs = getMaxSize(prviewSizeList,pictureSizes);
            int width = cs.width,height = cs.height;
            Log.e("size","pic:width-"+width+",height-"+height);
            if(width!=0 && height!=0) {
                params.setPreviewSize(width,height);
                params.setPictureSize(width,height);
            }

            try{
                mCamera.setParameters(params);
            }catch (Exception e){
                e.printStackTrace();
                Camera.Parameters parameters = mCamera.getParameters();// 得到摄像头的参数
                mCamera.setParameters(parameters);
            }

        }
    }

    @Override
    public void onClick(View v) {
        Paint paint =  modifyImageView.getPaint();
       if(v == white){
            paint.setColor(Color.rgb(255,255,255));
            modifyImageView.setPaint(paint);
        }else if(v == black){
            paint.setColor(Color.rgb(0,0,0));
            modifyImageView.setPaint(paint);
        }else if(v == blue){
            paint.setColor(Color.rgb(0,191,255));
            modifyImageView.setPaint(paint);
        }else if(v == green){
            paint.setColor(Color.rgb(100,221,23));
            modifyImageView.setPaint(paint);
        }else if(v == red){
            paint.setColor(Color.rgb(240,128,128));
            modifyImageView.setPaint(paint);
        }else if(v == cancel){
           modifyImageView.releaseSrc();
            modifypiclayout.setVisibility(View.GONE);
        }else if(v == ok){
            //将修改好的图片进行保存为文件
            if(srcfile != null && srcfile.exists()){
                String name = srcfile.getName();
                String filename = name.substring(0,name.lastIndexOf("."));
                String stufix = name.substring(name.lastIndexOf("."));
                try {
                    modifyImageView.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(modifyImageView.getDrawingCache());
                    modifyImageView.setDrawingCacheEnabled(false);
                    String descfile = srcfile.getParentFile().getPath()+"/"+filename+"-temp"+stufix;
                    BitMapUtils.saveFile(bitmap,descfile,100);

                    Intent it = getIntent();
                    it.putExtra("mediapath", TextUtils.isEmpty(descfile)?"":descfile);
                    Camera_VideoActivity.this.setResult(RESULT_OK,it);
                    Camera_VideoActivity.this.finish();
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.showLong(Camera_VideoActivity.this,"图片合成失败，请重试");
                }finally {
                    modifyImageView.releaseSrc();
                    modifypiclayout.setVisibility(View.GONE);
                }
            }else{
                modifyImageView.releaseSrc();
                modifypiclayout.setVisibility(View.GONE);
            }
        }
    }

    class CameraSize{
        public int width = 0;
        public int height = 0;

        public CameraSize(int wid,int hei){
            this.width = wid;
            this.height = hei;
        }
    }

    /**
     * 取分辨率最大的作为预览尺寸
     * @param sizes
     * @return
     */
    private CameraSize getMaxSize(List<Camera.Size> sizes,List<Camera.Size> pictures){
        int Maxw = 0,Maxh = 0;
        try{
            for (int i = 0; i < sizes.size(); i++) {
                if(Maxh < sizes.get(i).height) {
//                    Maxw = sizes.get(i).width;
                    Maxh = sizes.get(i).height;
                }
            }

            for (int i = 0; i < pictures.size(); i++) {
                if(Maxh == pictures.get(i).height){
                    if(Maxw < pictures.get(i).width)
                        Maxw = pictures.get(i).width;
                }
            }

        }catch (Exception e){
            e.printStackTrace();

        }

        return new CameraSize(Maxw,Maxh);
    }

    public void setCameraDisplayOrientation(Activity activity,
              int cameraId, Camera camera) {
          Camera.CameraInfo info =
                  new Camera.CameraInfo();
          Camera.getCameraInfo(cameraId, info);
          int rotation = activity.getWindowManager().getDefaultDisplay()
                             .getRotation();
          int degrees = 0;
          switch (rotation) {
              case Surface.ROTATION_0: degrees = 0; break;
              case Surface.ROTATION_90: degrees = 90; break;
              case Surface.ROTATION_180: degrees = 180; break;
              case Surface.ROTATION_270: degrees = 270; break;
          }

          int result;
          if (info.facing == CAMERA_FACING_FRONT) {
              result = (info.orientation + degrees) % 360;
              result = (360 - result) % 360;  // compensate the mirror
          } else {  // back-facing
              result = (info.orientation - degrees + 360) % 360;
          }
          camera.setDisplayOrientation(result);
        cameraRotation = result;
    }


    /**
     * 释放摄像头资源
     *
     * @date 2016-2-5
     */
    private void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /*    *
     * 是否开启了闪光灯
     * @return
     */
    public void isFlashlightOn() {
//        try {
//            mCamera.stopPreview();//停掉原来摄像头的预览
//            mCamera.release();//释放资源
//            mCamera = null;//取消原来摄像头
//            mCamera = Camera.open(cameraPosition);//打开当前选中的摄像头
//            Camera.Parameters parameters = mCamera.getParameters();
//            String flashMode = parameters.getFlashMode();
//            if (flashMode.equals(FLASH_MODE_OFF)) {
//                parameters.setFlashMode(FLASH_MODE_TORCH);
//            } else {
//                parameters.setFlashMode(FLASH_MODE_OFF);
//            }
//            mCamera.setParameters(parameters);
//            deal(mCamera);
//            mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
//            mCamera.startPreview();//开始预览
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void resetCamera(int openId){
        try{
            if(mCamera != null){
                mCamera.stopPreview();//停掉原来摄像头的预览
                mCamera.release();//释放资源
                mCamera = null;//取消原来摄像头
                mCamera = Camera.open(openId);//打开当前选中的摄像头
                setCameraParams();
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();//开始预览
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void changeCameraPosition(){

        isCameraBack = !isCameraBack;

        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for(int i = 0; i < cameraCount; i++) {

            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if(cameraPosition == CAMERA_FACING_BACK) {
                //现在是后置，变更为前置
                if(cameraInfo.facing  == CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    cameraPosition = CAMERA_FACING_FRONT;
                    resetCamera(i);
//                    mCamera.stopPreview();//停掉原来摄像头的预览
//                    mCamera.release();//释放资源
//                    mCamera = null;//取消原来摄像头
//                    mCamera = Camera.open(i);//打开当前选中的摄像头
//                    try {
////                        deal(mCamera,true);
//                        setCameraParams();
//                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mCamera.startPreview();//开始预览
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if(cameraInfo.facing  == CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    cameraPosition = CAMERA_FACING_BACK;
                    resetCamera(i);
//                    mCamera.stopPreview();//停掉原来摄像头的预览
//                    mCamera.release();//释放资源
//                    mCamera = null;//取消原来摄像头
//                    mCamera = Camera.open(i);//打开当前选中的摄像头
//                    try {
////                        deal(mCamera,false);
//                        setCameraParams();
//                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mCamera.startPreview();//开始预览

                    break;
                }
            }
        }
    }

    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            mOrientation = orientation;

            Log.w("@@",orientation+"#");

            //保证只返回四个方向
//            int newOrientation = ((orientation + 45) / 90 * 90) % 360;
//            if (newOrientation != mOrientation) {
//                mOrientation = newOrientation;
//
//                //返回的mOrientation就是手机方向，为0°、90°、180°和270°中的一个
//            }
        }
    }

}
