package cn.com.hesc.fastdevframwork.recycleview;

import java.io.Serializable;

/**
 * 例子数据
 * created by liujunlin on 2018/8/16 10:30
 */
public class Person implements Serializable{
        String name;
        int age;
        String phone;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
}
