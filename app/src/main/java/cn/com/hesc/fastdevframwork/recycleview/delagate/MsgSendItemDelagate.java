package cn.com.hesc.fastdevframwork.recycleview.delagate;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.moreshapeimageview.BubbleImageView;
import cn.com.hesc.recycleview.recycleadapter.itemview.ItemViewDelegate;
import cn.com.hesc.recycleview.recycleadapter.itemview.ViewHolder;
import cn.com.hesc.tools.SdcardInfo;

/**
 * ProjectName: ImmediateChatLibrary
 * ClassName: MsgSendItemDelagate
 * Description: 聊天界面的发送代理类
 * Author: liujunlin
 * Date: 2017-03-02 11:53
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MsgSendItemDelagate implements ItemViewDelegate<Info_msg> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.main_chat_send_msg;
    }

    @Override
    public boolean isForViewType(Info_msg item, int position) {
        Log.e("MsgSendItemDelagate",String.valueOf("a".equals(item.getUserId()) && !"system".equals(item.getUserId())));
        return "a".equals(item.getUserId()) && !"system".equals(item.getUserId()) ;
    }

    @Override
    public void convert(ViewHolder holder, Info_msg info_msg, int position) {
        holder.setText(R.id.chat_send_name,"小军");
        holder.setText(R.id.chat_send_content,info_msg.getInfo());
        String picPath = SdcardInfo.getInstance().getSdcardpath()+"/test1.jpg";
//        holder.setImageBitmap(R.id.sendimg, BitmapFactory.decodeFile(picPath));
        BubbleImageView imageView = (BubbleImageView)holder.getView(R.id.sendimg);
        Glide.with(holder.getConvertView().getContext()).load(picPath).asBitmap().placeholder(R.drawable.defaultpic).into(imageView);




    }

    private String getTime(String string){
        if(TextUtils.isEmpty(string))
            return "未知";
        Long t = Long.parseLong(string);
        if(t < 1000){
            return "1″";
        }else if(t<60000){
            return t/1000 + "″";
        }else{
            int min = (int) ((t/1000)/60);
            return  min+ "′" + (t/1000 - min*60) + "″";
        }
    }

}
