package com.jsy.community.qo;

import com.gnete.api.GneteObject;

/**
 * User: chenhf
 * Date: 2018/10/31
 * Time: 16:55
 */
public class BizRequestContentQO extends GneteObject {
    private static final long serialVersionUID = 6067857272612452701L;
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Hello,".concat(name).concat(",your age is " + age);
    }
}
