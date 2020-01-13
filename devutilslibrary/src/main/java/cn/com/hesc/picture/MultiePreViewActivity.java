package cn.com.hesc.picture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;
import cn.com.hesc.photoview.PhotoView;
import cn.com.hesc.picture.multiplepic.MutipleTouchViewPager;
import cn.com.hesc.tools.BitMapUtils;
import cn.com.hesc.tools.ToastUtils;

/**
 * ClassName: MultiePreViewActivity
 * Description: 图片预览界面，可编辑
 * Author: liujunlin
 * Date: 2017/8/3 9:00 
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class MultiePreViewActivity extends AppCompatActivity implements View.OnClickListener {

    private MutipleTouchViewPager pager;
    private ArrayList<String> pics = new ArrayList<>();
    private TextView counttitle,modifytextview;
    private ImageView backimageview;
    private ImageView deleteimageview;
    private int curentpoisition = 0;
    private GuidePagerAdapter adapter;
    private boolean modify = false;
    /**设置是否可编辑，默认可以编辑*/
    private boolean editEable = true;

    private FrameLayout modifypiclayout;
    private ModifyImageView modifyImageView;
    private ImageView white,black,red,green,blue;
    private TextView cancel,ok;
    private File srcfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fk_multie_pre_view);

        pics = getIntent().getExtras().getStringArrayList("pics");
        editEable = getIntent().getExtras().getBoolean("isedit",true);
        modify =  getIntent().getExtras().getBoolean("modify",false);
        init();
    }

    private void init() {
        pager = (MutipleTouchViewPager) findViewById(R.id.framwork_contentPager);
        counttitle = (TextView)findViewById(R.id.counttitle);
        counttitle.setText("1/"+pics.size());
        // ViewPager设置数据适配器，这个类似于使用ListView时用的adapter
        adapter = new GuidePagerAdapter(pics);
        pager.setAdapter(adapter);
        pager.clearAnimation();
        // 为Viewpager添加事件监听器 OnPageChangeListener
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                curentpoisition = position;
                counttitle.setText((position+1) + "/" + pics.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        backimageview = (ImageView)findViewById(R.id.backimageview);
        backimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnPics();
            }
        });

        deleteimageview = (ImageView)findViewById(R.id.delete);
        if(!editEable)
            deleteimageview.setVisibility(View.GONE);
        deleteimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pics.size() == 1){
                    pics.clear();
                    returnPics();
                }else{
                    pics.remove(curentpoisition);
                    adapter.notifyDataSetChanged();

                    if(curentpoisition == 0){
                        counttitle.setText((curentpoisition+1) + "/" + pics.size());
                    }else if(curentpoisition == pics.size()-1){
                        counttitle.setText(pics.size() + "/" + pics.size());
                    }else{
                        counttitle.setText((curentpoisition+1) + "/" + pics.size());
                    }
                }
            }
        });
        modifypiclayout = (FrameLayout)findViewById(R.id.modifypiclayout);
        modifytextview = (TextView)findViewById(R.id.modifytextview);
        if(modify) modifytextview.setVisibility(View.VISIBLE);
        modifytextview.setOnClickListener(this);
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

    @Override
    public void onBackPressed() {
        returnPics();
    }

    @Override
    public void onClick(View v) {
        Paint paint =  modifyImageView.getPaint();
        if(v == modifytextview){
            modifypiclayout.setVisibility(View.VISIBLE);
            String path = pics.get(curentpoisition);
            if("http".contains(path) || "https".contains(path)){
                ToastUtils.showLong(this,"暂不支持编辑网络图片");
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
        else if(v == white){
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
                    Log.e("paint color end",modifyImageView.getPaint().getColor()+"");

                    pics.set(curentpoisition,descfile);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.showLong(MultiePreViewActivity.this,"图片合成失败，请重试");
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

    // ViewPager 适配器
    class GuidePagerAdapter extends PagerAdapter implements View.OnClickListener {

        private List<String> mViews;

        public GuidePagerAdapter(List<String> views) {
            mViews = views;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(MultiePreViewActivity.this, R.layout.viewpage_item, null);
            PhotoView pageNumTV = (PhotoView) view.findViewById(R.id.showpic);
            String picpath = mViews.get(position);
            Glide.with(MultiePreViewActivity.this).load(picpath).placeholder(R.drawable.preloading).into(pageNumTV);
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void onClick(View v) {

        }
    }


    private void returnPics(){
        Intent it = getIntent();
        Bundle bl = new Bundle();
        bl.putStringArrayList("piclist", pics);
        it.putExtra("bundle", bl);
        MultiePreViewActivity.this.setResult(RESULT_OK, it);
        MultiePreViewActivity.this.finish();
    }
}
