package cn.com.hesc.picture.multiplepic;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.devutilslibrary.R;

/**
 * ProjectName: FastDev-master
 * ClassName: MultiplePicAdapter
 * Description: 图片多选的实现适配器
 * Author: liujunlin
 * Date: 2016-09-20 10:20
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MultiplePicAdapter extends CommonAdapter<String>{

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static ArrayList<String> mSelectedImage = new ArrayList<>();
    private int choiceCount = 0;
    /**大于0的话图片有选择数量限制，等于0无限制*/
    private int limitChCount = 0;
    private OnChoiceCountListener mOnChoiceCountListener;


    /**
     * 文件夹路径
     */
    private String mDirPath;

    public MultiplePicAdapter(Context context, List<String> mDatas, int itemLayoutId,
                       String dirPath){
        super(context, mDatas, itemLayoutId);
        this.mDirPath = dirPath;
    }

    public interface OnChoiceCountListener{

        void onGetChoiceCount(int count);
    }

    public void setOnChoiceCountListener(OnChoiceCountListener onChoiceCountListener) {
        mOnChoiceCountListener = onChoiceCountListener;
    }

    public int getChoiceCount() {
        return choiceCount;
    }

    public void setChoiceCount(int choiceCount) {
        this.choiceCount = choiceCount;
    }

    public int getLimitChCount() {
        return limitChCount;
    }

    public void setLimitChCount(int limitChCount) {
        this.limitChCount = limitChCount;
    }

    @Override
    public void convert(final ViewHolder helper, final String item)
    {
        //设置no_pic
        helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
        //设置no_selected
        helper.setImageResource(R.id.id_item_select,
                R.drawable.picture_unselected);
        //设置图片
        helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final ImageView mSelect = helper.getView(R.id.id_item_select);

        mImageView.setColorFilter(null);
        //设置ImageView的点击事件
        mImageView.setOnClickListener(new View.OnClickListener()
        {
            //选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v)
            {

                // 已经选择过该图片
                if (mSelectedImage.contains(mDirPath + "/" + item))
                {
                    mSelectedImage.remove(mDirPath + "/" + item);
                    mSelect.setImageResource(R.drawable.picture_unselected);
                    mImageView.setColorFilter(null);
                    if(choiceCount > 0){
                        choiceCount = choiceCount - 1;
                    }
                } else
                // 未选择该图片
                {
                    if(limitChCount > 0){
                        if(choiceCount < limitChCount){
                            mSelectedImage.add(mDirPath + "/" + item);
                            mSelect.setImageResource(R.drawable.pictures_selected);
                            mImageView.setColorFilter(Color.parseColor("#77000000"));
                            choiceCount = choiceCount + 1;
                        }else{
                            Toast.makeText(mContext,"亲，最多可选"+limitChCount+"张",Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }else{
                        mSelectedImage.add(mDirPath + "/" + item);
                        mSelect.setImageResource(R.drawable.pictures_selected);
                        mImageView.setColorFilter(Color.parseColor("#77000000"));
                        choiceCount = choiceCount + 1;
                    }
                }

                mOnChoiceCountListener.onGetChoiceCount(choiceCount);

            }
        });

        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(mDirPath + "/" + item))
        {
            mSelect.setImageResource(R.drawable.pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        }

    }

    /**
     * 获取选中的图片
     * @return
     */
    public static ArrayList<String> getmSelectedImage() {
        return mSelectedImage;
    }

    public static void setmSelectedImage(ArrayList<String> mSelectedImage) {
        MultiplePicAdapter.mSelectedImage = mSelectedImage;
    }
}
