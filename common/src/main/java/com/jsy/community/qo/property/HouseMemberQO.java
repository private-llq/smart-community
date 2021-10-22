package com.jsy.community.qo.property;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-07-23 17:30
 **/
@Data
public class HouseMemberQO implements Serializable {
    /**
     * 社区id
     */
    private Long communityId;
    /**
     * 用户状态1迁入2迁出
     */
    private Integer status;

    /**
     * 名字、手机号、房号模糊查询
     */
    private String key;

    /**
     * 与业主关系 1.业主 6.亲属，7租户
     */
    private Integer relation;

    /**
     * 房间id
     */
    private Long houseId;

    /**
     * 标签
     */
    private Integer tally;
    
    /**
     * 住户姓名或者手机或者房号
     */
    private String nameOrMobileOrDoor;
}
