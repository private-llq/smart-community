package com.jsy.community.vo.property;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 收费项目房屋关联返回数据
 * @author: Hu
 * @create: 2021-09-06 09:49
 **/
@Data
public class FeeRuleHouseVO implements Serializable {
    /**
     * 关联id
     */
    private Long id;
    /**
     * 楼栋
     */
    private Long building;
    /**
     * 单元
     */
    private Long unit;
    /**
     * 楼层
     */
    private Long floor;
    /**
     * 房屋编号
     */
    private Long door;
    /**
     * 建筑面积
     */
    private Long buildArea;
    /**
     * 实用面积
     */
    private Long practicalArea;

}
