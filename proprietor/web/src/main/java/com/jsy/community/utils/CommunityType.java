package com.jsy.community.utils;


/**
 * 查询类型定义枚举类，
 * 后续增加（修改）一种查询类型，只增加（修改）对应的枚举 和查询类型对应的查询方法 并实现查询方法 ，不用改动Controller
 * @author YuLF
 * @version 1.0
 * @date 2020/11/13 14:11
 */
public enum CommunityType {

    //社区查询类型-1 查询对应Service方法getAllCommunity
    COMMUNITY_TYPE(1, "getAllCommunity"),

    //单元查询类型-2 查询对应Service方法getAllUnitFormCommunity - 通过社区id查询他下面的所有单元
    UNIT_TYPE(2, "getAllUnitFormCommunity"),

    //楼栋查询类型-3 查询对应Service方法getAllBuildingFormUnit - 通过单元id查询他下面的所有楼栋
    BUILDING_TYPE(3, "getAllBuildingFormUnit"),

    //楼层查询类型-4 查询对应Service方法getAllFloorFormBuilding - 通过楼栋id查询他下面的所有楼层
    FLOOR_TYPE(4, "getAllFloorFormBuilding"),

    //门牌查询类型-5 查询对应Service方法getAllDoorFormFloor - 通过楼层id查询他下面的所有门牌
    DOOR_TYPE(5, "getAllDoorFormFloor");

    private final int value;
    private final String serviceMethod;

    CommunityType(int value, String serviceMethod) {
        this.value = value;
        this.serviceMethod = serviceMethod;
    }

    /**
     * 根据传入的 查询类型 返回对应的枚举类型
     * @param queryType 查询类型
     * @return          返回枚举类型
     */
    public static CommunityType valueOf(int queryType) {
        CommunityType resultCommunityType = null;
        CommunityType[] var1 = values();
        for(CommunityType communityType : var1){
            if(communityType.value == queryType){
                resultCommunityType = communityType;
            }
        }
        return resultCommunityType;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
    public String method() {
        return this.serviceMethod;
    }
    public int value() {
        return this.value;
    }

}
