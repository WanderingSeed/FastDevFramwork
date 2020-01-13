package cn.com.hesc.picture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.tools.BitMapUtils;
import cn.com.hesc.tools.ToastUtils;

public class ModifyPicActivity extends AppCompatActivity implements View.OnClickListener {

    private ModifyImageView modifyImageView;
    private ImageView white,black,red,green,blue;
    private TextView cancel,ok;
    private File srcfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pic);

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

        String path = getIntent().getExtras().getString("path");
        if("http".contains(path) || "https".contains(path)){
            ToastUtils.showLong(this,"暂不支持编辑在线图片");
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
            ModifyPicActivity.this.setResult(RESULT_CANCELED);
            ModifyPicActivity.this.finish();
        }else if(v == ok){
            //将修改好的图片进行保存为文件
            if(srcfile != null && srcfile.exists()){
                String name = srcfile.getName();
                String filename = name.substring(0,name.lastIndexOf("."));
                String stufix = name.substring(name.lastIndexOf("."));
                try {
                    Bitmap bitmap = Bitmap.createBitmap(modifyImageView.getDrawingCache());
                    modifyImageView.setDrawingCacheEnabled(false);
                    String descfile = srcfile.getParentFile().getPath()+"/"+filename+"-temp"+stufix;
                    BitMapUtils.saveFile(bitmap,descfile,100);
                    Intent it = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString("path",descfile);
                    it.putExtras(bundle);
                    ModifyPicActivity.this.setResult(RESULT_OK,it);
                    ModifyPicActivity.this.finish();

                }catch (Exception e){
                    e.printStackTrace();
                }


            }else{
                ModifyPicActivity.this.setResult(RESULT_CANCELED);
                ModifyPicActivity.this.finish();
            }
        }
    }
}
