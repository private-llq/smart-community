package com.jsy.community.vo.property;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-07-24 14:02
 **/
@Data
public class HouseMemberVO implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 房间id
     */
    private String houseId;

    /**
     * 业主姓名
     */
    private String name;

    /**
     * APPName
     */
    private String appName;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 房屋地址
     */
    private String houseSite;

    /**
     * 与业主关系 0.临时，1.业主，6.亲属，7租户
     */
    private Integer relation;
    /**
     * 与业主关系 0.临时，1.业主，6.亲属，7租户
     */
    private String relationName;

    /**
     * 租户有效时间
     */
    private LocalDate validTime;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

}
