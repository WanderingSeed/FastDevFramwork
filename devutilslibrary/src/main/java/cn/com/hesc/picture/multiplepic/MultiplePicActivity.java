package cn.com.hesc.picture.multiplepic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.com.hesc.devutilslibrary.R;

/**
 * 自定义多选图片的activity
 * 通过引入MultiplePicView实现
 */
public class MultiplePicActivity extends AppCompatActivity {

    private MultiplePicView multiplePic;//多选图片的核心类视图
    private LinearLayout picmul;
    private final int defaultPicCount = 6;
    private int picCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fk_multiple_pic);

        multiplePic = new MultiplePicView(this);
        multiplePic.setRightTopTitle("确定");
        /**限制选择的张数，不设置默认无限选*/
        if(getIntent().getExtras()!=null) {
            picCount = getIntent().getExtras().getInt("piccount", defaultPicCount);
            multiplePic.setLimitChCount(picCount);
        }

        multiplePic.setShowBtn(true);
        multiplePic.getImageCountBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChoicePics();
            }
        });
        picmul = (LinearLayout)findViewById(R.id.picmul);
        picmul.addView(multiplePic);
    }

    @Override
    public void onBackPressed() {
        getChoicePics();
    }

    private void getChoicePics(){
        ArrayList<String> pics = multiplePic.getChoicePics();
        Intent it = getIntent();
        if(pics!=null && pics.size()>0){
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("pics",pics);
            it.putExtras(bundle);
            MultiplePicActivity.this.setResult(RESULT_OK,it);
            MultiplePicActivity.this.finish();
        }else{
            MultiplePicActivity.this.setResult(RESULT_CANCELED,it);
            MultiplePicActivity.this.finish();
        }
    }
}
