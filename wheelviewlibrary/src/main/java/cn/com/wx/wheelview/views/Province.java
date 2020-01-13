package cn.com.wx.wheelview.views;

import java.io.Serializable;
import java.util.List;

/**
 * created by liujunlin on 2019/4/30 09:12
 */
public class Province implements Serializable {
    String code;
    String value;
    List<City> cities;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "Province{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", cities=" + cities +
                '}';
    }
}
