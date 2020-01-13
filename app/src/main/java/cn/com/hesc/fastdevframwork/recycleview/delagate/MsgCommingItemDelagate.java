package cn.com.hesc.fastdevframwork.recycleview.delagate;

import android.text.TextUtils;
import android.util.Log;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.recycleview.recycleadapter.itemview.ItemViewDelegate;
import cn.com.hesc.recycleview.recycleadapter.itemview.ViewHolder;


/**
 * ProjectName: ImmediateChatLibrary
 * ClassName: MsgCommingItemDelagate
 * Description: 私聊界面消息收取代理类
 * Author: liujunlin
 * Date: 2017-03-02 14:08
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class MsgCommingItemDelagate implements ItemViewDelegate<Info_msg> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.main_chat_from_msg;
    }

    @Override
    public boolean isForViewType(Info_msg item, int position) {
        Log.e("MsgCommingItemDelagate",String.valueOf((!"b".equals(item.getUserId()) && !"system".equals(item.getUserId()))));
        return ("b".equals(item.getUserId()) && !"system".equals(item.getUserId()));
    }

    @Override
    public void convert(ViewHolder holder, Info_msg info_msg, int position) {
        holder.setText(R.id.chat_from_name,"小强");
        holder.setText(R.id.chat_from_content,info_msg.getInfo());
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
