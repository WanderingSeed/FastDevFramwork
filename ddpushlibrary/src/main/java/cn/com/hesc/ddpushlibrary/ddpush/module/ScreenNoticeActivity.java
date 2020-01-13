package cn.com.hesc.ddpushlibrary.ddpush.module;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import cn.com.hesc.ddpushlibrary.R;


/**
 * 用于屏幕黑屏显示的提示框，仿QQ
 */
public class ScreenNoticeActivity extends Activity {
    private static OnGetViewListener onGetViewListener;
    public static interface OnGetViewListener{
        public abstract View onGetView(final Activity activity, View contentView);
    }
    public View defaultContentView;
    public ViewGroup defaultScreenContentView;
    View customView;
    PowerManager.WakeLock wl;
    KeyguardManager.KeyguardLock  kl;
    android.support.v7.app.AlertDialog ad;
    android.app.AlertDialog ad2;
    boolean firstShow=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            defaultScreenContentView =  new RelativeLayout(this);
            defaultScreenContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            setContentView(defaultScreenContentView);
        }catch (Exception e){
            e.printStackTrace();
        }
        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

//        this.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "INFO");

        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("INFO");
        firstShow=true;
        callLock();
        //releaseLock();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        dismiss(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        firstShow=true;
        setIntent(intent);
        releaseLock();
        callLock();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (this) {
            if (firstShow) {
                firstShow=false;
                Intent intent = getIntent();
                int icon = -1;
                String title = null;
                String msg = null;
                if (intent != null) {
                    icon = intent.getIntExtra("icon", -1);
                    title = intent.getStringExtra("title");
                    msg = intent.getStringExtra("msg");
                }

                View tempView = onGetViewListener != null ? onGetViewListener.onGetView(this, customView) : null;
                if (tempView != null) {
                    if (tempView != customView) {
                        defaultScreenContentView.removeAllViews();
                        if (defaultScreenContentView != null) {
                            tempView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            defaultScreenContentView.addView(tempView);
                            customView = tempView;
                        }
                    }
                } else {
                    try {
                        int dialogStyle = intent.getIntExtra("dialogStyle", -1);
                        if (dialogStyle <= 0) {
                            if (ad2 == null) {
                                android.app.AlertDialog.Builder builder = null;
                                builder = new android.app.AlertDialog.Builder(this);
                                builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismiss(ScreenNoticeActivity.this);
                                    }
                                });
                                ad2 = builder.create();
                                ad2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        dismiss(ScreenNoticeActivity.this);
                                    }
                                });
                                ad2.setCanceledOnTouchOutside(true);
                            }
                            if (icon > 0) {
                                ad2.setIcon(icon);
                            }
                            if (title != null) {
                                ad2.setTitle(title);
                            }
                            if (msg != null) {
                                ad2.setMessage(msg);
                            }
                            if (ad2 != null && !ad2.isShowing()) {
                                ad2.show();
                            }
                        } else {
                            if (ad == null) {
                                //AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.AppTheme_Dialog_Alert);
                                android.support.v7.app.AlertDialog.Builder builder = null;
                                builder = new android.support.v7.app.AlertDialog.Builder(this, dialogStyle);
                                builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismiss(ScreenNoticeActivity.this);
                                    }
                                });
                                ad = builder.create();
                                ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        dismiss(ScreenNoticeActivity.this);
                                    }
                                });
                                ad.setCanceledOnTouchOutside(true);
                            }
                            if (icon > 0) {
                                ad.setIcon(icon);
                            }
                            if (title != null) {
                                ad.setTitle(title);
                            }
                            if (msg != null) {
                                ad.setMessage(msg);
                            }
                            if (ad != null && !ad.isShowing()) {
                                ad.show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        releaseLock();
        customView=null;
        super.onDestroy();
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void callLock(){
        try {
            if (wl!=null)
                wl.acquire(); // wake up the screen
            if (kl!=null)
                kl.disableKeyguard();// dismiss the keyguard
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void releaseLock(){
        try {
            if (wl!=null)
                wl.release();
            if (kl!=null)
                kl.reenableKeyguard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OnGetViewListener getOnGetViewListener() {
        return onGetViewListener;
    }

    private static void setOnGetViewListener(OnGetViewListener onGetViewListener) {
        ScreenNoticeActivity.onGetViewListener = onGetViewListener;
    }

    public static void dismiss(Activity activity){
        try {
            if (activity!=null){
                activity.finish();
                if (activity instanceof ScreenNoticeActivity){
                    //取消关闭动画，避免闪屏
                    activity.overridePendingTransition(0, 0);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 使用自定义布局，在onGetViewListener处理界面
     * @param activity
     * @param onGetViewListener
     */
    public static void show(Activity activity, OnGetViewListener onGetViewListener){
        if (activity!=null){
            setOnGetViewListener(onGetViewListener);
            showIntent(activity,-1,null,null,-1);
        }
    }

    /**
     * 只能在使用aar调用模式下使用此方法
     * @param activity
     * @param msg
     */
    public static void show(Activity activity, String msg){
        if (activity!=null){
            setOnGetViewListener(null);
            showIntent(activity,-1,null,msg, R.style.AppTheme_Dialog_Alert);
        }
    }
    /**
     *
     * @param activity
     * @param msg
     * @param dialogStyle  Theme.AppCompat.Light.Dialog.Alert
     */
    public static void show(Activity activity, String msg, int dialogStyle){
        if (activity!=null){
            setOnGetViewListener(null);
            showIntent(activity,-1,null,msg,dialogStyle);
        }
    }

    /**
     * 只能在使用aar调用模式下使用此方法
     * @param activity    不可为null
     * @param icon        图标的资源id
     * @param title       标题
     * @param msg         提示信息
     */
    public static void show(Activity activity, int icon, String title, String msg){
        if (activity!=null){
            setOnGetViewListener(null);
            showIntent(activity, icon, title, msg,R.style.AppTheme_Dialog_Alert);
        }
    }

    /**
     *
     * @param activity    不可为null
     * @param icon        图标的资源id
     * @param title       标题
     * @param msg         提示信息
     * @param dialogStyle  Theme.AppCompat.Light.Dialog.Alert
     */
    public static void show(Activity activity, int icon, String title, String msg, int dialogStyle){
        if (activity!=null){
            setOnGetViewListener(null);
            showIntent(activity, icon, title, msg,dialogStyle);
        }
    }

    /**
     *
     * @param activity    不可为null
     * @param icon        图标的资源id
     * @param title       标题
     * @param msg         提示信息
     */
    private synchronized static void showIntent(Activity activity, int icon, String title, String msg, int dialogStyle){
        if (activity!=null) {
            Intent tempIntent = new Intent(activity, ScreenNoticeActivity.class);
            tempIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            if (icon > 0) {
                tempIntent.putExtra("icon", icon);
            }
            if (title != null) {
                tempIntent.putExtra("title", title);
            }
            if (msg != null) {
                tempIntent.putExtra("msg", msg);
            }
            tempIntent.putExtra("dialogStyle",dialogStyle);
            activity.startActivity(tempIntent);
        }
    }
}
