package cn.com.hesc.fastdevframwork.recycleview.delagate;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.recycleview.recycleadapter.itemview.ItemViewDelegate;
import cn.com.hesc.recycleview.recycleadapter.itemview.ViewHolder;
import cn.com.hesc.tools.TimeUtils;

/**
 * 私聊界面的日期代理
 */
public class MsgRcDateDelagate implements ItemViewDelegate<Info_msg> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.main_chat_time_msg;
    }

    @Override
    public boolean isForViewType(Info_msg item, int position) {
        return "system".equals(item.getUserId());
    }

    @Override
    public void convert(ViewHolder holder, Info_msg info_msg, int position) {
        long cur = System.currentTimeMillis();
        String today = TimeUtils.longToString(cur,"yyyy-MM-dd");
        long dayFirst = TimeUtils.stringtoLong(today + " 00:00:00");
        long dayLast = TimeUtils.stringtoLong(today + " 23:59:59");
        String format = "";
        if(info_msg.getCreatetime() > dayFirst && info_msg.getCreatetime() < dayLast)
            format = "HH:mm";
        else
            format = "MM-dd HH:mm";
        holder.setText(R.id.timete, TimeUtils.longToString(info_msg.getCreatetime(),format));
    }
}
