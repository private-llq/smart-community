package com.jsy.community.vo.property;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 收费项目车位关联返回数据
 * @author: Hu
 * @create: 2021-09-06 10:16
 **/
@Data
public class FeeRuleCarPositionVO implements Serializable {
    /**
     * 关联表id
     */
    private Long id;
    /**
     * 车位编号
     */
    private Long carPosition;
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
     * 用户姓名
     */
    private Long name;
    /**
     * 手机号
     */
    private Long mobile;

}
