package com.jsy.community.vo.property;

import com.jsy.community.vo.BaseVO;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 收费项目房屋关联返回数据
 * @author: Hu
 * @create: 2021-09-06 09:49
 **/
@Data
public class FeeRuleHouseVO extends BaseVO {
    /**
     * 楼栋
     */
    private String building;
    /**
     * 单元
     */
    private String unit;
    /**
     * 楼层
     */
    private String floor;
    /**
     * 房屋编号
     */
    private String door;
    /**
     * 建筑面积
     */
    private Double buildArea;
    /**
     * 实用面积
     */
    private Double practicalArea;

}
