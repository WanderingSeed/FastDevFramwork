package cn.com.hesc.fastdevframwork.metrialdialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.materialdialogs.HescMaterialDialog;
import cn.com.hesc.tools.ToastUtils;

public class DialogActivity extends AppCompatActivity {

    private HescMaterialDialog materialDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
    }

    public void openIndeterminate(View view){
        materialDialog = new HescMaterialDialog(this);
        materialDialog.showIndeterminateCircleProgressDialog("标题","这是环形无限滚动等待",true);
    }

    public void openIndeterminateHorizontal(View view){
        materialDialog = new HescMaterialDialog(this);
        materialDialog.showIndeterminateHorizontalProgressDialog("标题","这是横向无限滚动等待",true);
    }

    public void customDialog(View view){
        materialDialog = new HescMaterialDialog(this);
        materialDialog.showCustomViewDialog("自定义对话框", R.layout.customdialog, "确定", "取消", new HescMaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);
                View view1 = materialDialog.getCustomView();
                EditText userinput = (EditText) view1.findViewById(R.id.userinput);
                Toast.makeText(DialogActivity.this,"你输入了:"+userinput.getText().toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
    }

    public void confirm(View view){
        materialDialog = new HescMaterialDialog(this);
        materialDialog.showConfirmDialog("退出系统", "确定退出系统吗", "确定", "再想想", new HescMaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
    }

    public void signalitem(View view){
        String[] items = new String[]{"公务员","事业单位","私企","个体"};
        materialDialog = new HescMaterialDialog(this);
        materialDialog.showSignalDialog("单选", "确定", "取消", items, 0, new HescMaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);

                if(materialDialog.getMultiItems().size() > 0)
                    Toast.makeText(DialogActivity.this,"你选择了:"+materialDialog.getMultiItems().get(0),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
    }

    public void multipeitem(View view){
        String[] items = new String[]{"公务员","事业单位","私企","个体"};
        materialDialog = new HescMaterialDialog(this);
        materialDialog.showMultiDialog("多选", "确定", "取消", items, null, new HescMaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);
                String str = "";
                List<String> items = materialDialog.getMultiItems();
                if(items!=null){
                    for (String item:items) {
                        str += item;
                    }
                }
                Toast.makeText(DialogActivity.this,"你选择了:"+str,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
    }

    public void thirdbtn(View view){
        materialDialog = new HescMaterialDialog(this);
        materialDialog.showThirdConfirmDialog("题目", "内容", "确定", "不确定", "放弃", new HescMaterialDialog.ThirdButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);
                ToastUtils.showShort(DialogActivity.this,"sure");
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
                ToastUtils.showShort(DialogActivity.this,"no sure");
            }

            @Override
            public void onNeutral(HescMaterialDialog dialog) {
                super.onNeutral(dialog);
                ToastUtils.showShort(DialogActivity.this,"cancel sure");
            }
        });
    }
}
