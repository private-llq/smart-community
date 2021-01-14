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

    //【查询楼栋或单元动态sql方法】根据社区查询他下一级的结构是什么数据  单元楼栋  就查社区下面的所有单元  楼栋单元 就查社区下面的所有楼栋  单楼栋 就查所有楼栋 单单元就查所有单元
    COMMUNITY_BUILDING_TYPE(2, "getBuildingOrUnitByCommunityId"),

    //【根据楼栋id 查询 下面所有单元 或者 根据单元id 查询单元下面所有楼栋】
    BUILDING_UNIT_TYPE(3, "getBuildingOrUnitById"),

    //楼栋或单元ID查询下级楼层数据
    SINGLE_TYPE(4, "getFloorByBuildingOrUnitId"),

    //【根据楼层id查询门牌】 查询对应Service方法getAllDoorFormFloor - 通过楼层id查询他下面的所有门牌
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
