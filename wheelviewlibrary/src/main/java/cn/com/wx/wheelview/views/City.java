package cn.com.wx.wheelview.views;

import java.io.Serializable;
import java.util.List;

/**
 * created by liujunlin on 2019/4/30 09:18
 */
public class City implements Serializable {
    String code;
    String value;

    List<County> counties;

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

    public List<County> getCounties() {
        return counties;
    }

    public void setCounties(List<County> counties) {
        this.counties = counties;
    }

    @Override
    public String toString() {
        return "City{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", counties=" + counties +
                '}';
    }
}
