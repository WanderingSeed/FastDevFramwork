package cn.com.hesc.fastdevframwork.recycleview.delagate;

import java.io.Serializable;

/**
 * ProjectName: ImmediateChatLibrary
 * ClassName: Info_msg
 * Description: 聊天信息类-信息会话表
 * Author: liujunlin
 * Date: 2017-03-02 09:25
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class Info_msg implements Serializable{
    private String id;

    //聊天组织id
    private String groupId;

    //消息
    private String info;

    //发送人id
    private String userId;

    //接收人id
    private String touserId;

    private String state;

    //创建时间
    private Long createtime;

    //聊天界面最早的一条记录的时间点
    private Long lastchattime;

    //聊天界面最新的一条记录的时间点
    private Long newchattime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Long getLastchattime() {
        return lastchattime;
    }

    public void setLastchattime(Long lastchattime) {
        this.lastchattime = lastchattime;
    }

    public Long getNewchattime() {
        return newchattime;
    }

    public void setNewchattime(Long newchattime) {
        this.newchattime = newchattime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTouserId() {
        return touserId;
    }

    public void setTouserId(String touserId) {
        this.touserId = touserId;
    }
}
