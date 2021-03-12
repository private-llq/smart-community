package com.jsy.community.utils;


/**
 * 查询类型定义枚举类，
 * 后续增加（修改）一种查询类型，只增加（修改）对应的枚举 和查询类型对应的查询方法 并实现查询方法 ，不用改动Controller
 * @author YuLF
 * @version 1.0
 * @date 2020/11/13 14:11
 */
public enum CommunityType {

    //【查询城市下面的所有社区】社区查询类型-1 查询对应Service方法getAllCommunity
    COMMUNITY_TYPE(1, "getAllCommunityFormCityId"),

    //【查询社区下面的 楼栋或单元动态sql方法】
    COMMUNITY_BUILDING_TYPE(2, "getBuildingOrUnitByCommunityId"),

    //【根据楼栋id 查询 下面所有单元   或者 根据单元id 查询单元下面所有房屋】
    BUILDING_UNIT_TYPE(3, "getUnitOrHouseById"),

    //【根据单元id 查询下面所有房屋】
    SINGLE_TYPE(4, "getDoorByUnitId");

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

    @Override
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
