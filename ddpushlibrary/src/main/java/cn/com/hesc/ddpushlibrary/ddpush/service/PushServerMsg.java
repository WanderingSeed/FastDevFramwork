package cn.com.hesc.ddpushlibrary.ddpush.service;

import java.io.Serializable;

/**
 * Created by pubinfo on 2016/3/7.
 */
public class PushServerMsg implements Serializable {
    //服务ip
    public String serverip="";
    //连接端口
    public String serverport="";
    //推送端口
    public String pushport="";
    //用户名 必须唯一
    public String username="";
    //范围1-255，不得使用0
    public int appid=1;
}
