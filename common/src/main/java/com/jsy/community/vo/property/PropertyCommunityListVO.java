package com.jsy.community.vo.property;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 物业端社区列表返参
 * @Date: 2021/7/22 11:35
 * @Version: 1.0
 **/
public class PropertyCommunityListVO implements Serializable {
    // 社区ID
    private Long id;
    private String idStr;
    // 社区名称
    private String name;
    // 社区地址
    private String address;
    // 物业名称
    private String propertyName;
    // 物业ID
    private String propertyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PropertyCommunityListVO{");
        sb.append("id=").append(id);
        sb.append(", idStr='").append(idStr).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", propertyName='").append(propertyName).append('\'');
        sb.append(", propertyId='").append(propertyId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
