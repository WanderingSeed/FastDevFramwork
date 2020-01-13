package cn.com.hesc.picture.multiplepic;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;


/**
 * ProjectName: FastDev-master
 * ClassName: MultiplePic
 * Description: 封装一个可以多选图片的视图，类似微信选择图片界面，任意Activity、fragment都可以加载
 *
 * Author: liujunlin
 * Date: 2016-09-20 09:51
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MultiplePicView extends FrameLayout implements ListImageDirPopupWindow.OnImageDirSelected
        ,View.OnClickListener {

    private Context mContext;
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    private GridView mGirdView;
    private MultiplePicAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
    private RelativeLayout mBottomLy;
    private TextView mChooseDir;
    private TextView mImageCount;
    private Button mImageCountBtn;
    /**对应文件夹下图片的个数*/
    int totalCount = 0;
    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private TextView showCounttv;
    private ImageView imageView;
    /**大于0的话图片有选择数量限制，等于0无限制*/
    private int limitChCount = 0;
    private ProgressDialog mProgressDialog;
    private boolean isShowBtn = false;
    private int allchoosepic = 0;
    /**展示右上的标题*/
    private String rightTopTitle = "完成";


    public MultiplePicView(Context context) {
        super(context);
        mContext = context;
        initView();
        getImages();
        initEvent();
    }

    public MultiplePicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        getImages();
        initEvent();
    }

    public int getLimitChCount() {
        return limitChCount;
    }

    public void setLimitChCount(int limitChCount) {
        this.limitChCount = limitChCount;
        showCounttv.setText(rightTopTitle);
    }

    public String getRightTopTitle() {
        return rightTopTitle;
    }

    public void setRightTopTitle(String rightTopTitle) {
        this.rightTopTitle = rightTopTitle;
    }

    public boolean isShowBtn() {
        return isShowBtn;
    }

    public void setShowBtn(boolean showBtn) {
        isShowBtn = showBtn;
        if(isShowBtn){
            mImageCountBtn.setVisibility(VISIBLE);
            showCounttv.setVisibility(GONE);
        }else{
            mImageCountBtn.setVisibility(GONE);
            showCounttv.setVisibility(VISIBLE);
        }
    }

    public Button getImageCountBtn() {
        return mImageCountBtn;
    }

    public TextView getShowCounttv() {
        return showCounttv;
    }

    private void initView() {
        View mainView = LayoutInflater.from(mContext).inflate(R.layout.multiplepic,null);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainView.setLayoutParams(rl);
        mGirdView = (GridView) mainView.findViewById(R.id.id_gridViewchoice);
        mChooseDir = (TextView) mainView.findViewById(R.id.multiple_choose_dirchoice);
        mImageCount = (TextView) mainView.findViewById(R.id.multiple_total_countchoice);
        mBottomLy = (RelativeLayout) mainView.findViewById(R.id.multiple_bottom_lychoice);
        imageView = (ImageView)mainView.findViewById(R.id.multiplebackimageview);
        imageView.setOnClickListener(this);
        showCounttv = (TextView)mainView.findViewById(R.id.multipleshowcounttv);
        mImageCountBtn = (Button)mainView.findViewById(R.id.multipleshowcountbtn);
        addView(mainView);

        //提醒系统刷新媒体库
        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages()
    {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(mContext, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(mContext, null, "正在加载...");

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = mContext.getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");

                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext())
                {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath))
                    {
                        continue;
                    } else
                    {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    String[] paths = parentFile.list(new FilenameFilter()
                    {
                        @Override
                        public boolean accept(File dir, String filename)
                        {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    });

                    if(null == paths )
                        continue;

                    int picSize = paths.length;
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    //展示时间最近的
                    if(null == mImgDir)
                        mImgDir = parentFile;

                    //展示图片最多的文件夹
//                    if (picSize > mPicsSize)
//                    {
//                        mPicsSize = picSize;
//                        mImgDir = parentFile;
//                    }
                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();

            if(mAdapter!=null && limitChCount>0)
                mAdapter.setLimitChCount(limitChCount);

            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View()
    {
        if (mImgDir == null)
        {
            Toast.makeText(mContext, "没有图片",
                    Toast.LENGTH_SHORT).show();
            return;
        }

//        mImgs = Arrays.asList(mImgDir.list());
        String[] paths = mImgDir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        });
        mImgs = Arrays.asList(paths);

        Collections.reverse(mImgs);
//        arraySort(mImgDir.getAbsolutePath()+"/",(String[]) mImgs.toArray());

        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MultiplePicAdapter(mContext, mImgs,
                R.layout.grid_item_multiple, mImgDir.getAbsolutePath());
        mAdapter.setOnChoiceCountListener(new MultiplePicAdapter.OnChoiceCountListener() {
            @Override
            public void onGetChoiceCount(int count) {

                if(getChoicePics().size() == 0){
                    mImageCountBtn.setEnabled(false);
                }else{
                    mImageCountBtn.setEnabled(true);
                }

                if(limitChCount > 0){
                    if(limitChCount == 1){
                        showCounttv.setText(rightTopTitle);
                    }else{
                        showCounttv.setText(rightTopTitle+"("+getChoicePics().size()+"/"+limitChCount+")");
                    }

                    mAdapter.setChoiceCount(getChoicePics().size());
                    mImageCountBtn.setText(getChoicePics().size() == 0?rightTopTitle:rightTopTitle+"("+getChoicePics().size()+"/"+limitChCount+")");
                }
                else {
                    showCounttv.setText(rightTopTitle + "("+getChoicePics().size() + ")");
                    mImageCountBtn.setText(rightTopTitle+"("+getChoicePics().size()+")");
                }
            }
        });
        mAdapter.getmSelectedImage().clear();
        mGirdView.setAdapter((ListAdapter) mAdapter);
        mImageCount.setText(totalCount + "张");
    };

    private void arraySort(final String dirpath, String[] arr){
        Arrays.sort(arr, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                File file = new File(dirpath+o1);
                File file1 = new File(dirpath+o2);
                long filel = file.lastModified();
                long file1l = file1.lastModified();
                if(file.exists() && file1.exists()){
                    if(file.lastModified() > file1.lastModified())
                        return 1;
                    else if(file.lastModified() < file1.lastModified())
                        return -1;
                }else{
                    Log.e("file","no exist");
                }


                return 0;
            }
        });
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw()
    {
        mScreenHeight = getHeight();
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(mContext)
                .inflate(R.layout.list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss()
            {

                int color = 0x00000000;
                Drawable drawable = new ColorDrawable(color);
                setForeground(drawable);
            }
        });

        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    private void initEvent()
    {

        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
                //将背景虚化
                int color = 0xcc000000;
                Drawable drawable = new ColorDrawable(color);
                setForeground(drawable);

            }
        });
    }

    @Override
    public void selected(ImageFloder floder)
    {

        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));

//        arraySort(mImgDir.getAbsolutePath()+"/",(String[]) mImgs.toArray());
        Collections.reverse(mImgs);

        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MultiplePicAdapter(mContext, mImgs,
                R.layout.grid_item_multiple, mImgDir.getAbsolutePath());
        if(mAdapter!=null && limitChCount>0) {
            mAdapter.setLimitChCount(limitChCount);
            mAdapter.setChoiceCount(getChoicePics().size());
        }
        mAdapter.setOnChoiceCountListener(new MultiplePicAdapter.OnChoiceCountListener() {
            @Override
            public void onGetChoiceCount(int count) {

                if(getChoicePics().size() == 0){
                    mImageCountBtn.setEnabled(false);
                }else{
                    mImageCountBtn.setEnabled(true);
                }

                if(limitChCount > 0) {
                    if(limitChCount == 1){
                        showCounttv.setText(rightTopTitle);
                    }else
                        showCounttv.setText(rightTopTitle+"("+getChoicePics().size() + "/" + limitChCount+")");
                    mImageCountBtn.setText(getChoicePics().size() == 0?rightTopTitle:rightTopTitle+"("+getChoicePics().size() + "/" + limitChCount+")");
                    mAdapter.setChoiceCount(getChoicePics().size());
                }
                else {
                    showCounttv.setText(rightTopTitle + "("+getChoicePics().size() + ")");
                    mImageCountBtn.setText(rightTopTitle+"("+getChoicePics().size() + ")");
                }
            }
        });
        mGirdView.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();

    }

    @Override
    public void onClick(View v) {
        if(v == imageView){
            getChoicePics();
        }
    }

    /**
     * 获取选择到的图片路径信息
     * @return
     */
    public ArrayList<String> getChoicePics(){
        return  mAdapter.getmSelectedImage();
    }

    public void setChoicePics(ArrayList<String> pics){
        mAdapter.setmSelectedImage(pics);
        mAdapter.notifyDataSetChanged();
    }

}
