package cn.com.hesc.ddpushlibrary.ddpush.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.ddpush.im.v1.client.appserver.Pusher;

//import android.util.Log;

/**
 * Created by pubinfo on 2016/3/7.
 */
public class DDPushMagnager {
    public static boolean enableCheckService=false;
    public final static String key="serverMsg";

    /**
     * 设置推送服务器地址信息
     * @param context
     * @param serverMsg
     * @throws Exception
     */
    public static void setPush(@Nullable Context context, @Nullable PushServerMsg serverMsg)throws Exception {
        try {
            Intent startSrv = new Intent(context, OnlineService.class);
            if (serverMsg!=null) {
                startSrv.putExtra(key, serverMsg);
            }
            startSrv.putExtra("CMD", "RESET_LOGIN");
            context.startService(startSrv);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 用查看当前实际连接服务器地址信息
     * @param context
     * @throws Exception
     */
    public static void setPush(@Nullable Context context)throws Exception {
        try {
            Intent startSrv = new Intent(context, OnlineService.class);
            startSrv.putExtra("CMD", "TOAST_MSG");
            context.startService(startSrv);
        }catch (Exception e){
            throw e;
        }
    }
    /**
     * 当在线程中调用
     * @param context
     * @param serverip  推送服务端ip
     * @param pushport  推送服务端的推送端口
     * @param username  用户名，必须唯一
     * @param msg        推送信息
     * @param timeout   超时，milliseconds   1000表示1s
     * @return           发送是否成功，实际测试有时不准确
     * @throws Exception
     */
    public static boolean pushMsg(@Nullable Context context, @Nullable String serverip, int pushport, @Nullable String username, String msg, int timeout)throws Exception {
        boolean result=false;
        Pusher pusher = null;
        try {
            byte[] uuidbytes = Util.md5Byte(username.toString());
            byte[] msgbytes = msg.toString().getBytes("UTF-8");
            if (timeout<5000){
                timeout=5000;
            }
            pusher = new Pusher(serverip, pushport, timeout);
            result = pusher.push0x20Message(uuidbytes, msgbytes);
        } catch (Exception e) {
            throw e;
        } finally {
            if (pusher != null) {
                try {
                    pusher.close();
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public static boolean pushMsg(@Nullable Context context, @Nullable String serverip, int pushport, @Nullable String[] username, String msg, int timeout)throws Exception {
        boolean flag=false;
        for (String user : username){
            flag=pushMsg(context,serverip,pushport,user,msg,5000);
        }
        return flag;
    }
    /**
     * 简单的线程发送信息，不包含是否重发的判断
     * @param context
     * @param serverip  推送服务端ip
     * @param pushport  推送服务端的推送端口
     * @param username  用户名，必须唯一
     * @param msg        推送信息
     * @throws Exception
     */
    public static void pushMsgSimpleTests(@Nullable Context context, @Nullable String serverip, int pushport, @Nullable String username, String msg)throws Exception {
        try {
            Thread t = new Thread(new send0x20Task(context, serverip, pushport, new String[]{username}, msg));
            t.start();
        }catch (Exception e) {
            throw e;
        }
    }
    public static void pushMsgSimpleTests(@Nullable Context context, @Nullable String serverip, int pushport, @Nullable String[] username, String msg)throws Exception {
        try {
            Thread t = new Thread(new send0x20Task(context, serverip, pushport, username, msg));
            t.start();
        }catch (Exception e) {
            throw e;
        }
    }
    static class send0x20Task implements Runnable {
        private Context context;
        private String serverIp;
        private int port;
        private String[] username;
        private String msg;

        public send0x20Task(Context context, String serverIp, int port, String[] username, String msg) {
            this.context = context;
            this.serverIp = serverIp;
            this.port = port;
            this.username = username;
            this.msg = msg;
        }

        public void run() {
            boolean result=false;
            try {
                result= pushMsg(context,serverIp,port,username,msg,5000);
            }catch (Exception e){
                e.printStackTrace();
            }
            Intent startSrv = new Intent(context, OnlineService.class);
            startSrv.putExtra("CMD", "TOAST");
//            if (result) {
//                startSrv.putExtra("TEXT", "自定义信息发送成功");
//            } else {
//                startSrv.putExtra("TEXT", "发送失败！格式有误");
//            }
            context.startService(startSrv);
        }
    }
//    /**
//     * 以下方法未测试，提供思路，可在外部自己实现
//     */
//    /**
//     * 启动服务
//     * @param context
//     */
//
//    public synchronized static void startService(@Nullable Context context){
//        String name=OnlineService.class.getName();
//        if (!isServiceWork(context,OnlineService.class.getName())){
//            Intent startSrv = new Intent(context, OnlineService.class);
//            context.startService(startSrv);
//        }else{
//           // Log.i("DDPushMagnager", "service is running");
//        }
//    }
//
//    /**
//     * 停止服务
//     * @param context
//     */
//    public synchronized static void stopService(@Nullable Context context){
//        try {
//            Intent startSrv = new Intent(context, OnlineService.class);
//            startSrv.putExtra("CMD", "STOP");
//            context.startService(startSrv);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 检查服务是否运行中，需要设置enableCheckService为true
//     * @param context
//     */
//    public static void checkService(@Nullable Context context){
//        try {
//            if (enableCheckService) {
//                startService(context);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    /**
//     * 判断某个服务是否正在运行的方法
//     *
//     * @param mContext
//     * @param serviceName
//     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
//     * @return true代表正在运行，false代表服务没有正在运行
//     */
//    public static boolean isServiceWork(@Nullable Context mContext, String serviceName) {
//        boolean isWork = false;
//        try {
//            if (mContext!=null) {
//                ActivityManager myAM = (ActivityManager) mContext
//                        .getSystemService(Context.ACTIVITY_SERVICE);
//                List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
//                if (myList.size() > 0) {
//                    for (int i = 0; i < myList.size(); i++) {
//                        String mName = myList.get(i).service.getClassName().toString();
//                        if (mName.equals(serviceName)) {
//                            isWork = true;
//                            break;
//                        }
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return isWork;
//    }
}
