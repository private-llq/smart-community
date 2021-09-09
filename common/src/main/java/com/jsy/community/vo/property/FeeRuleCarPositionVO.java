package com.jsy.community.vo.property;

import com.jsy.community.vo.BaseVO;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 收费项目车位关联返回数据
 * @author: Hu
 * @create: 2021-09-06 10:16
 **/
@Data
public class FeeRuleCarPositionVO extends BaseVO {
    /**
     * 车位编号
     */
    private String carPosition;
    /**
     * 房屋地址
     */
    private String houseSite;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;

}
