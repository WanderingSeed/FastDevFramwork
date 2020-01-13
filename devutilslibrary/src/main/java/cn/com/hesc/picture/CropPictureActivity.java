package cn.com.hesc.picture;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.photoview.PhotoView;

/**
 * 裁剪图片,可做头像设置或者别的设置功能
 */
public class CropPictureActivity extends AppCompatActivity {

    private PhotoView mPhotoView;
    private String picPath;
    private String changPicPath;
    private Intent intent;
    private TextView savepic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_picture);


        mPhotoView = (PhotoView)findViewById(R.id.showimg);
        picPath = getIntent().getExtras().getString("picpath");
        if(TextUtils.isEmpty(picPath))
            picPath = "";
        if(!picPath.contains("http") && !picPath.contains("https")){
            picPath = Uri.fromFile(new File(picPath)).toString();
        }
        intent = getIntent();
        initMenu();
    }

    private void initMenu() {
        Glide.with(this).load(picPath).into(mPhotoView);
        savepic = (TextView)findViewById(R.id.savepic);
        savepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择图片
                if(TextUtils.isEmpty(changPicPath)){
                    Intent it = new Intent(CropPictureActivity.this,SignalPicActivity.class);
                    startActivityForResult(it,1);
                }else{
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString("picpath",changPicPath);
                    intent.putExtras(bundle);
                    CropPictureActivity.this.setResult(RESULT_OK,intent);
                    CropPictureActivity.this.finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    changPicPath = data.getExtras().getString("picPath");
                    if(!TextUtils.isEmpty(changPicPath)){
                        Glide.with(CropPictureActivity.this).load(changPicPath).into(mPhotoView);
                        savepic.setText("保存");
                    }

                }
                break;
        }
    }
}
