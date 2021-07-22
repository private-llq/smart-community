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
    private String communityId;
    // 社区名称
    private String name;
    // 社区地址
    private String address;
    // 物业名称
    private String propertyName;
    // 物业ID
    private String propertyId;
    // 社区类型;1:小区;2:出租公寓
    private Integer communityType;

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
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

    public Integer getCommunityType() {
        return communityType;
    }

    public void setCommunityType(Integer communityType) {
        this.communityType = communityType;
    }

    public PropertyCommunityListVO(String communityId, String name, String address, String propertyName, String propertyId, Integer communityType) {
        this.communityId = communityId;
        this.name = name;
        this.address = address;
        this.propertyName = propertyName;
        this.propertyId = propertyId;
        this.communityType = communityType;
    }
}
