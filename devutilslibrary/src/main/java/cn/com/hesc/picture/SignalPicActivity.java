package cn.com.hesc.picture;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.picture.multiplepic.MultiplePicView;
import cn.com.hesc.tools.DisplayUtils;


public class SignalPicActivity extends AppCompatActivity {

    private MultiplePicView multiplePic;
    private LinearLayout picmul;
    private String picPath = "",temppic = "";
    private Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signal_pic);

        multiplePic = new MultiplePicView(this);
        multiplePic.setRightTopTitle("确定");
        multiplePic.setLimitChCount(1);
        multiplePic.getShowCounttv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCropPic();
            }
        });
        picmul = (LinearLayout)findViewById(R.id.picmul);
        picmul.addView(multiplePic);

        it = getIntent();

    }

    private void toCropPic() {
        List<String> pic = multiplePic.getChoicePics();
        if(pic != null && pic.size() > 0){

            DisplayUtils dl = new DisplayUtils(SignalPicActivity.this);
            picPath = pic.get(0);
            File file = new File(pic.get(0));
            temppic = file.getParent() + file.getName() + "-temp.jpg";
            if(copyFile(file,temppic)) {
                File temp = new File(temppic);
                Intent intent = new Intent("com.android.camera.action.CROP");
                Uri uri;
                //如果android7.0以上的系统，需要做个判断
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    uri = FileProvider.getUriForFile(SignalPicActivity.this, "cn.com.hesc.devutilslibrary.fileProvider", temp);//7.0
                } else {
                    uri = Uri.fromFile(temp); //7.0以下
                }

                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", dl.getWidth());
                intent.putExtra("outputY", dl.getWidth());
                // 设置为true直接返回bitmap
                intent.putExtra("return-data", false);
                // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//            intent.putExtra("output", uri); // 转入目标文件
                intent.putExtra("noFaceDetection", true);

                //添加Uri读写权限,否则7.0以上版本无法读写Uri路径文件 
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION |Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(intent, 1);
            }
        }
    }

    /**
     * 根据文件路径拷贝文件
     * @param src 源文件
     * @param destPath 目标文件路径
     * @return boolean 成功true、失败false
     */
    public boolean copyFile(File src, String destPath) {
        boolean result = false;
        if ((src == null) || (destPath== null)) {
            return result;
        }
        File dest= new File(destPath);
        if (dest!= null && dest.exists()) {
            dest.delete(); // delete file
        }
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Bundle bundle = new Bundle();
                    bundle.putString("picPath",temppic);
                    it.putExtras(bundle);
                    SignalPicActivity.this.setResult(RESULT_OK,it);
                    SignalPicActivity.this.finish();
                }
                break;
        }
    }
}
