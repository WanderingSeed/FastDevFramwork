package cn.com.hesc.picture.multiplepic;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import cn.com.hesc.devutilslibrary.R;

/**
 * ProjectName: FastDev-master
 * ClassName: ListImageDirPopupWindow
 * Description: TODO
 * Author: liujunlin
 * Date: 2016-09-20 10:29
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder>{
    private ListView mListDir;

    public ListImageDirPopupWindow(int width, int height,
                                   List<ImageFloder> datas, View convertView)
    {
        super(convertView, width, height, true, datas);
    }

    @Override
    public void initViews()
    {
        mListDir = (ListView) findViewById(R.id.id_list_dir);
        mListDir.setAdapter(new CommonAdapter<ImageFloder>(context, mDatas,
                R.layout.list_dir_item)
        {
            @Override
            public void convert(ViewHolder helper, ImageFloder item)
            {
                helper.setText(R.id.id_dir_item_name, item.getName());
                helper.setImageByUrl(R.id.id_dir_item_image,
                        item.getFirstImagePath());
                helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
            }
        });
    }

    public interface OnImageDirSelected
    {
        void selected(ImageFloder floder);
    }

    private OnImageDirSelected mImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
    {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents()
    {
        mListDir.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                if (mImageDirSelected != null)
                {
                    mImageDirSelected.selected(mDatas.get(position));
                }
            }
        });
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params)
    {
        // TODO Auto-generated method stub
    }
}
