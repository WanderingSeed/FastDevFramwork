package cn.com.hesc.fastdevframwork.settting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.tools.SysSettingUtils;

public class OpenSettingActivity extends AppCompatActivity {

    private SysSettingUtils mSysSettingUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_setting);

        mSysSettingUtils = new SysSettingUtils(this);
    }

    public void openGps(View view){
        mSysSettingUtils.openGPSSetting();
    }

    public void openNotificationSetting(View view){
        mSysSettingUtils.openNotificationSetting();
    }

    public void openApplicationSetting(View view){
        mSysSettingUtils.openApplicationSetting();
    }
}
