package com.jsy.community.utils.imutils.entity;

/**
 * @author lxjr
 * @date 2021/8/18 14:13
 */
public class Links {
    private String url;
    private String desc;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "Links{" +
                "url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Links() {
    }

    public Links(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }
}
