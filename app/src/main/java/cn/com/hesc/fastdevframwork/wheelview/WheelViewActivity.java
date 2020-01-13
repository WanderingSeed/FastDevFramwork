package cn.com.hesc.fastdevframwork.wheelview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.materialdialogs.HescMaterialDialog;
import cn.com.wx.wheelview.views.AddressWheelView;
import cn.com.wx.wheelview.widget.WheelViewGroup;

public class WheelViewActivity extends AppCompatActivity {

    private WheelViewGroup wheelViewGroup;
    private LinearLayout addwheelgroup;
    private HescMaterialDialog hescMaterialDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);
        addwheelgroup = (LinearLayout)findViewById(R.id.addwheelgroup) ;
    }

    //一个轮子
    public void oneWheelView(View view){
        wheelViewGroup = new WheelViewGroup(this);
        wheelViewGroup.setWheelCount(1);
        //第一层数据初始化
        List<String> strings = new ArrayList<>();
        strings.add("张三很多字很多字放不下了怎么办");
        strings.add("李四很多字很多字放不下了怎么办");
        strings.add("王五很多字很多字放不下了怎么办");
        strings.add("赵六很多字很多字放不下了怎么办");
        wheelViewGroup.showData(wheelViewGroup.getFirstwheelview(),strings);
        addwheelgroup.removeAllViews();
        addwheelgroup.addView(wheelViewGroup);
    }

    //二个轮子
    public void twoWheelView(View view){
        wheelViewGroup = new WheelViewGroup(this);
        wheelViewGroup.setWheelCount(2);

        //第一层数据初始化
        List<String> strings = new ArrayList<>();
        strings.add("张三很多字很多字放不下了怎么办");
        strings.add("李四很多字很多字放不下了怎么办");
        strings.add("王五很多字很多字放不下了怎么办");
        strings.add("赵六很多字很多字放不下了怎么办");
        wheelViewGroup.showData(wheelViewGroup.getFirstwheelview(),strings);

        //第二层数据初始化
        HashMap<String,List<String>> map = new HashMap<>();
        //保存二层数据，为三层提供服务
        List<String> secondmap = new ArrayList<>();
        List<String> strings1 = new ArrayList<>();
        strings1.add("张三1很多字很多字放不下了怎么办");
        strings1.add("张三2很多字很多字放不下了怎么办");
        map.put("张三很多字很多字放不下了怎么办",strings1);
        List<String> strings2 = new ArrayList<>();
        strings2.add("李四1很多字很多字放不下了怎么办");
        strings2.add("李四2很多字很多字放不下了怎么办");
        map.put("李四很多字很多字放不下了怎么办",strings2);
        List<String> strings3 = new ArrayList<>();
        strings3.add("王五1很多字很多字放不下了怎么办");
        strings3.add("王五2很多字很多字放不下了怎么办");
        map.put("王五很多字很多字放不下了怎么办",strings3);
        List<String> strings4 = new ArrayList<>();
        strings4.add("赵六1很多字很多字放不下了怎么办");
        strings4.add("赵六2很多字很多字放不下了怎么办");
        map.put("赵六很多字很多字放不下了怎么办",strings4);
        //二层数据由一层决定
        secondmap = map.get(strings.get(wheelViewGroup.getFirstChoose()));
        wheelViewGroup.showData(wheelViewGroup.getSecondwheelview(),secondmap);
        //一、二层联动
        wheelViewGroup.setJoin(wheelViewGroup.getFirstwheelview(),wheelViewGroup.getSecondwheelview());
        wheelViewGroup.setJoinData(wheelViewGroup.getFirstwheelview(),map);

        addwheelgroup.removeAllViews();
        addwheelgroup.addView(wheelViewGroup);
    }

    //三个轮子
    public void threeWheelView(View view){
        wheelViewGroup = new WheelViewGroup(this);
        wheelViewGroup.setWheelCount(3);

        //第一层数据初始化
        List<String> strings = new ArrayList<>();
        strings.add("张三很多字很多字放不下了怎么办");
        strings.add("李四很多字很多字放不下了怎么办");
        strings.add("王五很多字很多字放不下了怎么办");
        strings.add("赵六很多字很多字放不下了怎么办");
        wheelViewGroup.showData(wheelViewGroup.getFirstwheelview(),strings);

        //第二层数据初始化
        HashMap<String,List<String>> map = new HashMap<>();
        //保存二层数据，为三层提供服务
        List<String> secondmap = new ArrayList<>();
        List<String> strings1 = new ArrayList<>();
        strings1.add("张三1很多字很多字放不下了怎么办");
        strings1.add("张三2很多字很多字放不下了怎么办");
        map.put("张三很多字很多字放不下了怎么办",strings1);
        List<String> strings2 = new ArrayList<>();
        strings2.add("李四1很多字很多字放不下了怎么办");
        strings2.add("李四2很多字很多字放不下了怎么办");
        map.put("李四很多字很多字放不下了怎么办",strings2);
        List<String> strings3 = new ArrayList<>();
        strings3.add("王五1很多字很多字放不下了怎么办");
        strings3.add("王五2很多字很多字放不下了怎么办");
        map.put("王五很多字很多字放不下了怎么办",strings3);
        List<String> strings4 = new ArrayList<>();
        strings4.add("赵六1很多字很多字放不下了怎么办");
        strings4.add("赵六2很多字很多字放不下了怎么办");
        map.put("赵六很多字很多字放不下了怎么办",strings4);
        //二层数据由一层决定
        secondmap = map.get(strings.get(wheelViewGroup.getFirstChoose()));
        wheelViewGroup.showData(wheelViewGroup.getSecondwheelview(),secondmap);
        //一、二层联动
        wheelViewGroup.setJoin(wheelViewGroup.getFirstwheelview(),wheelViewGroup.getSecondwheelview());
        wheelViewGroup.setJoinData(wheelViewGroup.getFirstwheelview(),map);
        //第三层数据初始化
        HashMap<String,List<String>> map1 = new HashMap<>();
        List<String> strings11 = new ArrayList<>();
        strings11.add("张三11111很多字很多字放不下了怎么办");
        strings11.add("张三21111很多字很多字放不下了怎么办");
        map1.put("张三1很多字很多字放不下了怎么办",strings11);
        List<String> strings22 = new ArrayList<>();
        strings22.add("张三222222200很多字很多字放不下了怎么办");
        strings22.add("张三2222222211很多字很多字放不下了怎么办");
        map1.put("张三2很多字很多字放不下了怎么办",strings22);

        List<String> strings22222 = new ArrayList<>();
        strings22222.add("李四11111很多字很多字放不下了怎么办");
        strings22222.add("李四21111很多字很多字放不下了怎么办");
        map1.put("李四1很多字很多字放不下了怎么办",strings22222);
        List<String> strings222 = new ArrayList<>();
        strings222.add("李四222222200很多字很多字放不下了怎么办");
        strings222.add("李四2222222211很多字很多字放不下了怎么办");
        map1.put("李四2很多字很多字放不下了怎么办",strings222);

        List<String> strings33 = new ArrayList<>();
        strings33.add("王五11111很多字很多字放不下了怎么办");
        strings33.add("王五21111很多字很多字放不下了怎么办");
        map1.put("王五1很多字很多字放不下了怎么办",strings33);
        List<String> strings44 = new ArrayList<>();
        strings44.add("王五222222200很多字很多字放不下了怎么办");
        strings44.add("王五2222222211很多字很多字放不下了怎么办");
        map1.put("王五2很多字很多字放不下了怎么办",strings44);

        //三层数据由二层决定
        String secondkey = secondmap.get(wheelViewGroup.getSecondChoose());
        wheelViewGroup.showData(wheelViewGroup.getThirdwheelview(),map1.get(secondkey));
        //加第三层
        wheelViewGroup.setJoin(wheelViewGroup.getSecondwheelview(),wheelViewGroup.getThirdwheelview());
        wheelViewGroup.setJoinData(wheelViewGroup.getSecondwheelview(),map1);

        addwheelgroup.removeAllViews();
        addwheelgroup.addView(wheelViewGroup);
    }

    public void oneWheelViewDialog(View view){
        wheelViewGroup = new WheelViewGroup(this);
        wheelViewGroup.setWheelCount(1);
        //第一层数据初始化
        final List<String> strings = new ArrayList<>();
        strings.add("张三很多字很多字放不下了怎么办");
        strings.add("李四很多字很多字放不下了怎么办");
        strings.add("王五很多字很多字放不下了怎么办");
        strings.add("赵六很多字很多字放不下了怎么办");
        wheelViewGroup.showData(wheelViewGroup.getFirstwheelview(),strings);

        hescMaterialDialog = new HescMaterialDialog(this);
        hescMaterialDialog.showCustomViewDialog("一联选择", wheelViewGroup, "确定", "取消", new HescMaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);
                int index = wheelViewGroup.getFirstChoose();
                Toast.makeText(WheelViewActivity.this,"选择了:"+strings.get(index),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });

    }
    public void twoWheelViewDialog(View view){
        wheelViewGroup = new WheelViewGroup(this);
        wheelViewGroup.setWheelCount(2);

        //第一层数据初始化
        final List<String> strings = new ArrayList<>();
        strings.add("张三很多字很多字放不下了怎么办");
        strings.add("李四很多字很多字放不下了怎么办");
        strings.add("王五很多字很多字放不下了怎么办");
        strings.add("赵六很多字很多字放不下了怎么办");
        wheelViewGroup.showData(wheelViewGroup.getFirstwheelview(),strings);

        //第二层数据初始化
        final HashMap<String,List<String>> map = new HashMap<>();
        //保存二层数据，为三层提供服务
        List<String> secondmap = new ArrayList<>();
        List<String> strings1 = new ArrayList<>();
        strings1.add("张三1张三1张三1张三1张三1张三1张三1");
        strings1.add("张三2");
        map.put("张三很多字很多字放不下了怎么办",strings1);
        List<String> strings2 = new ArrayList<>();
        strings2.add("李四1");
        strings2.add("李四2");
        map.put("李四很多字很多字放不下了怎么办",strings2);
        List<String> strings3 = new ArrayList<>();
        strings3.add("王五1");
        strings3.add("王五2");
        map.put("王五很多字很多字放不下了怎么办",strings3);
        List<String> strings4 = new ArrayList<>();
        strings4.add("赵六1");
        strings4.add("赵六2");
        map.put("赵六很多字很多字放不下了怎么办",strings4);
        //二层数据由一层决定
        secondmap = map.get(strings.get(wheelViewGroup.getFirstChoose()));
        wheelViewGroup.showData(wheelViewGroup.getSecondwheelview(),secondmap);
        //一、二层联动
        wheelViewGroup.setJoin(wheelViewGroup.getFirstwheelview(),wheelViewGroup.getSecondwheelview());
        wheelViewGroup.setJoinData(wheelViewGroup.getFirstwheelview(),map);


        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("类型选择").customView(wheelViewGroup, true)
                .positiveText("确定").negativeText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();
        materialDialog.show();




//        hescMaterialDialog = new HescMaterialDialog(this);
//        hescMaterialDialog.showCustomViewDialog("二联选择", wheelViewGroup, "确定", "取消", new HescMaterialDialog.ButtonCallback() {
//            @Override
//            public void onPositive(HescMaterialDialog dialog) {
//                super.onPositive(dialog);
//                int index = wheelViewGroup.getFirstChoose();
//                String firststr = strings.get(index);
//                int sindex = wheelViewGroup.getSecondChoose();
//                String secondstr = map.get(firststr).get(sindex);
//                Toast.makeText(WheelViewActivity.this,"选择了:"+firststr+"---"+secondstr,Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNegative(HescMaterialDialog dialog) {
//                super.onNegative(dialog);
//            }
//        });
    }
    public void threeWheelViewDialog(View view){
        wheelViewGroup = new WheelViewGroup(this);
        wheelViewGroup.setWheelCount(3);

        //第一层数据初始化
        final List<String> strings = new ArrayList<>();
        strings.add("张三");
        strings.add("李四");
        strings.add("王五");
        strings.add("赵六");
        wheelViewGroup.showData(wheelViewGroup.getFirstwheelview(),strings);

        //第二层数据初始化
        final HashMap<String,List<String>> map = new HashMap<>();
        //保存二层数据，为三层提供服务
        List<String> secondmap = new ArrayList<>();
        List<String> strings1 = new ArrayList<>();
        strings1.add("张三1");
        strings1.add("张三2");
        map.put("张三",strings1);
        List<String> strings2 = new ArrayList<>();
        strings2.add("李四1");
        strings2.add("李四2");
        map.put("李四",strings2);
        List<String> strings3 = new ArrayList<>();
        strings3.add("王五1");
        strings3.add("王五2");
        map.put("王五",strings3);
        List<String> strings4 = new ArrayList<>();
        strings4.add("赵六1");
        strings4.add("赵六2");
        map.put("赵六",strings4);
        //二层数据由一层决定
        secondmap = map.get(strings.get(wheelViewGroup.getFirstChoose()));
        wheelViewGroup.showData(wheelViewGroup.getSecondwheelview(),secondmap);
        //一、二层联动
        wheelViewGroup.setJoin(wheelViewGroup.getFirstwheelview(),wheelViewGroup.getSecondwheelview());
        wheelViewGroup.setJoinData(wheelViewGroup.getFirstwheelview(),map);
        //第三层数据初始化
        final HashMap<String,List<String>> map1 = new HashMap<>();
        List<String> strings11 = new ArrayList<>();
        strings11.add("张三11111");
        strings11.add("张三21111");
        map1.put("张三1",strings11);
        List<String> strings22 = new ArrayList<>();
        strings22.add("张三222222200");
        strings22.add("张三2222222211");
        map1.put("张三2",strings22);

        List<String> strings22222 = new ArrayList<>();
        strings22222.add("李四11111");
        strings22222.add("李四21111");
        map1.put("李四1",strings22222);
        List<String> strings222 = new ArrayList<>();
        strings222.add("李四222222200");
        strings222.add("李四2222222211");
        map1.put("李四2",strings222);

        List<String> strings33 = new ArrayList<>();
        strings33.add("王五11111");
        strings33.add("王五21111");
        map1.put("王五1",strings33);
        List<String> strings44 = new ArrayList<>();
        strings44.add("王五222222200");
        strings44.add("王五2222222211");
        map1.put("王五2",strings44);

        //三层数据由二层决定
        String secondkey = secondmap.get(wheelViewGroup.getSecondChoose());
        wheelViewGroup.showData(wheelViewGroup.getThirdwheelview(),map1.get(secondkey));
        //加第三层
        wheelViewGroup.setJoin(wheelViewGroup.getSecondwheelview(),wheelViewGroup.getThirdwheelview());
        wheelViewGroup.setJoinData(wheelViewGroup.getSecondwheelview(),map1);

        hescMaterialDialog = new HescMaterialDialog(this);
        hescMaterialDialog.showCustomViewDialog("三联选择", wheelViewGroup, "确定", "取消", new HescMaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);
                int index = wheelViewGroup.getFirstChoose();
                String firststr = strings.get(index);
                int sindex = wheelViewGroup.getSecondChoose();
                String secondstr = map.get(firststr).get(sindex);
                int tindex = wheelViewGroup.getThirdChoose();
                String thirdstr = map1.get(secondstr).get(tindex);
                Toast.makeText(WheelViewActivity.this,"选择了:"+firststr+"---"+secondstr+"##"+thirdstr,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
    }

    public void addressview(View view){
        final AddressWheelView addressWheelView = new AddressWheelView(this);
        hescMaterialDialog = new HescMaterialDialog(this);
        hescMaterialDialog.showCustomViewDialog("三联地址选择", addressWheelView, "确定", "取消", new HescMaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(HescMaterialDialog dialog) {
                super.onPositive(dialog);
                try{
                    int index = addressWheelView.getWheelViewGroup().getFirstChoose();
                    int sindex = addressWheelView.getWheelViewGroup().getSecondChoose();
                    int tindex = addressWheelView.getWheelViewGroup().getThirdChoose();
                    Toast.makeText(WheelViewActivity.this,"选择了:"+index+"@@@"+sindex+"##"+tindex,Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNegative(HescMaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
    }
}
