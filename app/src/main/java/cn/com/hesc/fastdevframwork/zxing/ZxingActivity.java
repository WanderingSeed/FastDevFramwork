package cn.com.hesc.fastdevframwork.zxing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.zxinglibrary.android.CaptureActivity;
import cn.com.hesc.zxinglibrary.bean.ZxingConfig;
import cn.com.hesc.zxinglibrary.common.Constant;
import cn.com.hesc.zxinglibrary.encode.CodeCreator;

public class ZxingActivity extends AppCompatActivity {

    private final int READERCODE = 0;
    private TextView showstr;
    private EditText inputstr;
    private ImageView showercode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);
        showstr = (TextView)findViewById(R.id.showstr);
        inputstr = (EditText)findViewById(R.id.inputstr);
        showercode = (ImageView)findViewById(R.id.showercode);
    }

    public void readErCode(View view){
        Intent intent = new Intent(ZxingActivity.this, CaptureActivity.class);
        /*ZxingConfig是配置类
         *可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(false);//是否扫描条形码 默认为true
        config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为淡蓝色
        config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, READERCODE);
    }

    public void generatorErCode(View view){
        String str = inputstr.getText().toString();
        if(TextUtils.isEmpty(str)){
            Toast.makeText(this,"输入点信息再生成二维码吧",Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = null;
        try {
            bitmap = CodeCreator.createQRCode(str, 400, 400, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            showercode.setImageBitmap(bitmap);
        }

    }

    public void generatorErCodeWithLogo(View view){
        String str = inputstr.getText().toString();
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(this, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = null;
        try {
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            bitmap = CodeCreator.createQRCode(str, 400, 400, logo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            showercode.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case READERCODE:
                if(resultCode == RESULT_OK){
                    if (data != null) {

                        String content = data.getStringExtra(Constant.CODED_CONTENT);
                        showstr.setText("扫描结果为：" + content);
                    }
                }
                break;
        }
    }
}
