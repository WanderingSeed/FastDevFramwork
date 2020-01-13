package cn.com.wx.wheelview.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Xml;
import android.widget.FrameLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.com.wx.wheelview.widget.WheelViewGroup;

/**
 * created by liujunlin on 2019/4/30 08:46
 */
public class AddressWheelView extends FrameLayout {

    private WheelViewGroup wheelViewGroup;
    private Context mContext;

    public AddressWheelView(@NonNull Context context) {
        super(context);
        mContext = context;
        initData();
    }

    private void initData() {
        try {
            List<Province> provinces = readXML(mContext);

            wheelViewGroup = new WheelViewGroup(mContext);
            wheelViewGroup.setWheelCount(3);

            //第一层数据初始化
            List<String> strings = new ArrayList<>();
            List<String> secondmap = new ArrayList<>();
            List<String> thdmap = new ArrayList<>();
            HashMap<String,List<String>> map = new HashMap<>();
            HashMap<String,List<String>> cmap = new HashMap<>();
            for (int i = 0; i < provinces.size(); i++) {
                Province province = provinces.get(i);
                strings.add(province.getValue());

                //第二层数据初始化
                List<String> secstring = new ArrayList<>();
                List<City> cities = province.getCities();
                for (int j = 0; j < cities.size(); j++) {
                    City city = cities.get(j);
                    secstring.add(city.getValue());

                    List<County> counties = city.getCounties();
                    //第三层数据初始化
                    List<String> thdstring = new ArrayList<>();
                    for (int k = 0; k < counties.size(); k++) {
                        County county = counties.get(k);
                        thdstring.add(county.getValue());
                    }
                    cmap.put(city.getValue(),thdstring);
                }
                map.put(province.getValue(),secstring);
            }
            //第一层数据
            wheelViewGroup.showData(wheelViewGroup.getFirstwheelview(),strings);
            //二层数据由一层决定
            secondmap = map.get(strings.get(wheelViewGroup.getFirstChoose()));
            wheelViewGroup.showData(wheelViewGroup.getSecondwheelview(),secondmap);
            //一、二层联动
            wheelViewGroup.setJoin(wheelViewGroup.getFirstwheelview(),wheelViewGroup.getSecondwheelview());
            wheelViewGroup.setJoinData(wheelViewGroup.getFirstwheelview(),map);
            //三层数据由二层决定
            String secondkey = secondmap.get(wheelViewGroup.getSecondChoose());
            wheelViewGroup.showData(wheelViewGroup.getThirdwheelview(),cmap.get(secondkey));
            //加第三层
            wheelViewGroup.setJoin(wheelViewGroup.getSecondwheelview(),wheelViewGroup.getThirdwheelview());
            wheelViewGroup.setJoinData(wheelViewGroup.getSecondwheelview(),cmap);

            addView(wheelViewGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Province> readXML(Context context) {
        try {
            InputStream inStream = context.getAssets().open("province_city.xml");
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(inStream, "UTF-8");
            int eventType = xmlPullParser.getEventType();//产生第一个事件
            Province province = null;
            List<Province> provinces = null;
            List<City> cities = null;
            City city = null;
            List<County> counties = null;
            County county = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
                        provinces = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG://开始元素事件
                        String tagName = xmlPullParser.getName();
                        if (tagName.equalsIgnoreCase("province")) {
                            province= new Province();
                            cities = new ArrayList<>();
                            province.setCode(xmlPullParser.getAttributeValue(null, "code"));
                            province.setValue(xmlPullParser.getAttributeValue(null, "value"));
                        } else if (tagName.equalsIgnoreCase("city")) {
                            city = new City();
                            counties = new ArrayList<>();
                            city.setCode(xmlPullParser.getAttributeValue(null, "code"));
                            city.setValue(xmlPullParser.getAttributeValue(null, "value"));
                        }else if(tagName.equalsIgnoreCase("county")){
                            county = new County();
                            county.setCode(xmlPullParser.getAttributeValue(null, "code"));
                            county.setValue(xmlPullParser.getAttributeValue(null, "value"));
                        }
                        break;
                    case XmlPullParser.END_TAG://结束元素事件
                        if (xmlPullParser.getName().equalsIgnoreCase("province") && province != null) {
                            province.setCities(cities);
                            provinces.add(province);
                        }else if(xmlPullParser.getName().equalsIgnoreCase("city") && city != null){
                            cities.add(city);
                            city.setCounties(counties);
                        }else if(xmlPullParser.getName().equalsIgnoreCase("county") && county != null){
                            counties.add(county);
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
            return provinces;
        } catch(NumberFormatException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WheelViewGroup getWheelViewGroup() {
        return wheelViewGroup;
    }
}
