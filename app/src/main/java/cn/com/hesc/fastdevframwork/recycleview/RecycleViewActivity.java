package cn.com.hesc.fastdevframwork.recycleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import cn.com.hesc.fastdevframwork.R;
import cn.com.hesc.fastdevframwork.recycleview.delagate.Info_msg;
import cn.com.hesc.fastdevframwork.recycleview.delagate.MsgCommingItemDelagate;
import cn.com.hesc.fastdevframwork.recycleview.delagate.MsgRcDateDelagate;
import cn.com.hesc.fastdevframwork.recycleview.delagate.MsgSendItemDelagate;
import cn.com.hesc.recycleview.Refrush_More_RecycleView;
import cn.com.hesc.recycleview.recycleadapter.RecycleCommonAdapter;
import cn.com.hesc.recycleview.recycleadapter.RecycleMultiItemTypeAdapter;
import cn.com.hesc.recycleview.recycleadapter.itemview.ViewHolder;

public class RecycleViewActivity extends AppCompatActivity {

    private Refrush_More_RecycleView recycleview;
    private RecycleCommonAdapter<Person> mPerSonRecycleCommonAdapter;
    protected RecycleMultiItemTypeAdapter<Info_msg> mRecycleMultiItemTypeAdapter;
    private ArrayList<Person>  persons = new ArrayList<>();
    private ArrayList<Info_msg>  msgs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        recycleview = (Refrush_More_RecycleView)findViewById(R.id.recycleview);
        //初始化列表组件，可以设置纵向和横向2种方式，分割线的高度，分隔线的颜色
        recycleview.initView(1,1, R.color.articlecontent);
        recycleview.getRecyclerView().setHasFixedSize(true);
        //初始化适配器--单一，Person是我自定义的一个类，是为了演示使用
        mPerSonRecycleCommonAdapter = new RecycleCommonAdapter<Person>(this,recycleview.getRecyclerView(),persons,true,R.layout.signalitem) {
            @Override
            public void conver(ViewHolder viewHandler, Person item, int position) {
                viewHandler.setText(R.id.name,"姓名 "+item.getName());
                viewHandler.setText(R.id.age,"年龄 "+item.getAge()+"岁");
                viewHandler.setText(R.id.phone,"电话 "+item.getPhone());
            }
        };
        //初始化多样式适配器
        mRecycleMultiItemTypeAdapter = new RecycleMultiItemTypeAdapter<>(this,msgs,recycleview.getRecyclerView(),true);
        mRecycleMultiItemTypeAdapter.addItemViewDelegate(new MsgSendItemDelagate());
        mRecycleMultiItemTypeAdapter.addItemViewDelegate(new MsgCommingItemDelagate());
        mRecycleMultiItemTypeAdapter.addItemViewDelegate(new MsgRcDateDelagate());
        mRecycleMultiItemTypeAdapter.showFootView(false);//实时聊天类的，去除加载更多的视图

        recycleview.setEventListener(new Refrush_More_RecycleView.EventListener() {
            @Override
            public void onRefrushListener() {
            }

            @Override
            public void onMoreListener(int lastPosition) {

            }

            @Override
            public void onItemClick(View v, int position) {

            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        });

    }

    public void showSignal(View view){
        for (int i = 0; i < 10; i++) {
            Person p = new Person();
            p.setName("张"+i);
            Random random=new Random();
            p.setAge(random.nextInt(40));
            p.setPhone("1399999124"+i);
            persons.add(p);
        }
        recycleview.setRecycleCommonAdapter(mPerSonRecycleCommonAdapter);
    }

    public void showMultiple(View view){
        for (int i = 0; i < 20; i++) {
            Info_msg inmsg = new Info_msg();
            if(i == 10) {
                inmsg.setUserId("system");
                inmsg.setCreatetime(System.currentTimeMillis());
            }else
                inmsg.setUserId(i%2 == 0?"a":"b");
            inmsg.setInfo(i%2 == 0?"你好，天气不错":"是的，适合出去旅游");
            msgs.add(inmsg);
        }
        recycleview.setRecycleMultiItemTypeAdapter(mRecycleMultiItemTypeAdapter);
    }
}
