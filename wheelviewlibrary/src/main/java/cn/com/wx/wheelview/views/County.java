package cn.com.wx.wheelview.views;

import java.io.Serializable;

/**
 * created by liujunlin on 2019/4/30 09:26
 */
public class County implements Serializable {
    String code;
    String value;

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

    @Override
    public String toString() {
        return "County{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
