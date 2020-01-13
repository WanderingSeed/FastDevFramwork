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
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.tools.BitMapUtils;
import cn.com.hesc.tools.DisplayUtils;
import cn.com.hesc.tools.SdcardInfo;
import cn.com.hesc.tools.ToastUtils;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
import static android.hardware.Camera.Parameters.FLASH_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;

/**
 * 金华牛皮鲜定制相机
 */
public class JHPhotoActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView close,flashflag,cameradirect;
    private SurfaceView camerasurfaceview,presurfaceview;
    private ImageButton recordmedia,delrecord,donerecord,modifyrecord;
    private boolean isOpenFlash = false;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int cameraPosition;//默认是后置摄像头
    private boolean isCameraBack = true;
    private int cameraRotation;
    private DisplayUtils d;
    private String mediaPath = "";
    private boolean openFlash = false,isRecording = false,capturePic = false;
    private int captureispic = -1;//当前操作类型，-1 没有任何操作 0 拍照 1录像
    private List<Camera.Size> prviewSizeList,picSizeList;
    private SdcardInfo mSdcard;
    private JHPhotoView mashimgview;
    private final static String TAG = JHPhotoActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_jhphoto);
        mSdcard = SdcardInfo.getInstance();
        d = new DisplayUtils(this);
        initView();
    }

    private void initView() {
        flashflag = (TextView)findViewById(R.id.flashbtn);
        flashflag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(cameraPosition == CAMERA_FACING_BACK) {
                openFlash = !openFlash;
                flashflag.setBackgroundResource(openFlash?R.drawable.openflash:R.drawable.closeflash);
                resetCamera(cameraPosition);
            }
            }
        });
        cameradirect = (TextView)findViewById(R.id.fbcamera);
        cameradirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCameraPosition();
            }
        });
        close = (TextView)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JHPhotoActivity.this.finish();
            }
        });
        camerasurfaceview = (SurfaceView)findViewById(R.id.camerasurfaceview);
        presurfaceview = (SurfaceView)findViewById(R.id.previewsurfaceview);
//        camerasurfaceview.setLayoutParams(new FrameLayout.LayoutParams(d.getWidth(),d.getWidth()*4/3));
//        presurfaceview.setLayoutParams(new FrameLayout.LayoutParams(d.getWidth(),d.getWidth()*4/3));
        recordmedia = (ImageButton)findViewById(R.id.recordmedia);
        recordmedia.setOnClickListener(this);
        delrecord = (ImageButton)findViewById(R.id.delrecord);
        delrecord.setOnClickListener(this);
        donerecord = (ImageButton)findViewById(R.id.donerecord);
        donerecord.setOnClickListener(this);
        mashimgview = (JHPhotoView)findViewById(R.id.mashimgview);
        //配置SurfaceHodler
        mSurfaceHolder = camerasurfaceview.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
//        mSurfaceHolder.setFixedSize(d.getWidth(),d.getWidth()*4/3);
        mSurfaceHolder.setSizeFromLayout();
        // 设置该组件不会让屏幕自动关闭
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(mCallBack); //相机创建回调接口
    }

    @Override
    public void onClick(View v) {
        if(v == recordmedia){
            takeCapturePic();
        }else if(v == delrecord){
            mCamera.startPreview();
            File file = new File(mediaPath);
            if(file.exists() && file.isFile()) {
                file.delete();
                mediaPath = "";
            }
            showToolsBtn(false);
            presurfaceview.setVisibility(View.INVISIBLE);
            camerasurfaceview.setVisibility(View.VISIBLE);
            mashimgview.setVisibility(View.GONE);
        }else if(v == donerecord){
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
            JHPhotoActivity.this.setResult(RESULT_OK,it);
            JHPhotoActivity.this.finish();
        }
    }

    private void takeCapturePic(){

        if(capturePic)
            return;
        capturePic = true;
        mediaPath = SdcardInfo.File_Pic + File.separator + System.currentTimeMillis()+".jpg";
        try {
            if(mCamera == null){
                initCamera();
            }
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
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Matrix matrix = new Matrix();
                            //认为横屏
//                            if((mOrientation > 30 && mOrientation < 150)/*||(mOrientation > 210 && mOrientation < 330)*/){
//                                matrix.postRotate(cameraRotation+90);
//                            }else if(mOrientation > 210 && mOrientation < 330){
//                                matrix.postRotate(cameraRotation-90);
//                            }
//                            else{
                                if(cameraPosition == CAMERA_FACING_FRONT)
                                    matrix.postRotate(cameraRotation+180);
                                else
                                    matrix.postRotate(cameraRotation);
//                            }

                            // 创建新的图片
                            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            //加入翻转 把相机拍照返回照片转正
                            BitMapUtils.saveFile(resizedBitmap,mediaPath,80);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mashimgview.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                                    //合成图片
                                    mergePic();
                                    showToolsBtn(true);
                                    captureispic = -1;
                                    capturePic = false;
                                }
                            });
                        }
                    }).start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            capturePic = false;
        }
    }

    private void mergePic(){
        File srcfile = new File(mediaPath);
        if(srcfile != null && srcfile.exists()) {
            String name = srcfile.getName();
            try {
                mashimgview.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(mashimgview.getDrawingCache());
                mashimgview.setDrawingCacheEnabled(false);
                BitMapUtils.saveFile(bitmap, srcfile.getAbsolutePath(), 100);

            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(JHPhotoActivity.this, "图片合成失败，请重试");
            } finally {
                mashimgview.releaseSrc();
//                mashimgview.setVisibility(View.GONE);
            }
        }
    }

    private void showToolsBtn(boolean isShow){
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
            //配置CameraParams
            setCameraParams();
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            // 设置分辨率
//            mSurfaceHolder.setFixedSize(size.width, size.height);
            mCamera.setPreviewDisplay(mSurfaceHolder);

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
                params.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                //设置闪光灯模式,后置摄像头才设置这个参数
                params.setFlashMode(FLASH_MODE_AUTO);
            }

            //缩短Recording启动时间
            params.setRecordingHint(true);
            //是否支持影像稳定能力，支持则开启
            if (params.isVideoStabilizationSupported())
                params.setVideoStabilization(true);
            Log.e("display",d.getWidth()+d.getHeight()+"");
            //获取手机支持的预览和视频大小
            prviewSizeList = params.getSupportedPreviewSizes();
            picSizeList = params.getSupportedPictureSizes();
            CameraSize cs = getMaxSize(prviewSizeList/*,pictureSizes*/);
            int width = cs.width,height = cs.height;
            Log.e("size","pic:width-"+width+",height-"+height);

            params.setPreviewSize(width,height);
            params.setPictureSize(width,height);

            try{
                mCamera.setParameters(params);
            }catch (Exception e){
                e.printStackTrace();
                Camera.Parameters parameters = mCamera.getParameters();// 得到摄像头的参数
                mCamera.setParameters(parameters);
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
    private CameraSize getMaxSize(List<Camera.Size> sizes/*, List<Camera.Size> pictures*/){
        int Maxw = 0,Maxh = 0;
        try{
            for (int i = 0; i < sizes.size(); i++) {
                if(Maxw < sizes.get(i).width) {
                    Maxw = sizes.get(i).width;
                    Maxh = sizes.get(i).height;
                }
            }

//            for (int i = 0; i < sizes.size(); i++) {
//                if(Maxh < sizes.get(i).height) {
//                    Maxh = sizes.get(i).height;
//                    Maxw = sizes.get(i).width;
//                }
//            }

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
//        switch (result) {
//            case 0:
//            case 180:
//                setCameraSize(camera.getParameters(),d.getWidth(), d.getHeight());
//                break;
//            case 90:
//            case 270:
//                setCameraSize(camera.getParameters(), d.getWidth(),d.getHeight());
//                break;
//        }
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
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if(cameraInfo.facing  == CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    cameraPosition = CAMERA_FACING_BACK;
                    resetCamera(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()){
            stopCamera();
        }
    }


    public void setCameraSize(Camera.Parameters parameters, int width, int height) {
        Map<String, List<Camera.Size>> allSizes = new HashMap<>();
        String typePreview = "typePreview";
        String typePicture = "typePicture";
        allSizes.put(typePreview, parameters.getSupportedPreviewSizes());
        allSizes.put(typePicture, parameters.getSupportedPictureSizes());
        Iterator iterator = allSizes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Camera.Size>> entry = (Map.Entry<String, List<Camera.Size>>) iterator.next();
            List<Camera.Size> sizes = entry.getValue();
            if (sizes == null || sizes.isEmpty()) continue;
            ArrayList<WrapCameraSize> wrapCameraSizes = new ArrayList<>(sizes.size());
            for (Camera.Size size : sizes) {
                WrapCameraSize wrapCameraSize = new WrapCameraSize();
                wrapCameraSize.setWidth(size.width);
                wrapCameraSize.setHeight(size.height);
                wrapCameraSize.setD(Math.abs((size.width - width)) + Math.abs((size.height - height)));
                if (size.width == width && size.height == height) {
                    if (typePreview.equals(entry.getKey())) {
                        parameters.setPreviewSize(size.width, size.height);
                    } else if (typePicture.equals(entry.getKey())) {
                        parameters.setPictureSize(size.width, size.height);
                    }
                    Log.d(TAG, "best size: width=" + size.width + ";height=" + size.height);
                    break;
                }
                wrapCameraSizes.add(wrapCameraSize);
            }
            Log.d(TAG, "wrapCameraSizes.size()=" + wrapCameraSizes.size());
            Camera.Size resultSize = null;
            if (typePreview.equals(entry.getKey())) {
                resultSize = parameters.getPreviewSize();
            } else if (typePicture.equals(entry.getKey())) {
                resultSize = parameters.getPictureSize();
            }
            if (resultSize == null || (resultSize.width != width && resultSize.height != height)) {
                //找到相机Preview Size 和 Picture Size中最适合的大小
                if(wrapCameraSizes.isEmpty()) continue;
                WrapCameraSize minCameraSize = Collections.min(wrapCameraSizes);
                while (!(minCameraSize.getWidth() >= width && minCameraSize.getHeight() >= height)) {
                    wrapCameraSizes.remove(minCameraSize);
                    if(wrapCameraSizes.isEmpty()) break;
                    minCameraSize = null;
                    minCameraSize = Collections.min(wrapCameraSizes);
                }
                Log.d(TAG, "best min size: width=" + minCameraSize.getWidth() + ";height=" + minCameraSize.getHeight());
                if (typePreview.equals(entry.getKey())) {
                    parameters.setPreviewSize(minCameraSize.getWidth(), minCameraSize.getHeight());
                } else if (typePicture.equals(entry.getKey())) {
                    parameters.setPictureSize(minCameraSize.getWidth(), minCameraSize.getHeight());
                }
                mSurfaceHolder.setFixedSize(minCameraSize.getWidth(),minCameraSize.getHeight());
            }
            iterator.remove();
        }
    }

    class WrapCameraSize implements Comparable<WrapCameraSize>{

        private int width;
        private int height;
        private int d;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }


        @Override
        public int compareTo(@NonNull WrapCameraSize o) {
            if(o.getD() > this.d)
                return -1;
            else if(o.getD() < this.d)
                return 1;
            return 0;
        }
    }
}
