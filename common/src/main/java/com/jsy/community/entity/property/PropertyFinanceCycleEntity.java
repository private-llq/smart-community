package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 物业财务结算周期表实体
 * @Date: 2021/4/22 9:09
 * @Version: 1.0
 **/
@Data
@TableName("t_property_finance_cycle")
@ApiModel("物业财务结算周期表实体")
public class PropertyFinanceCycleEntity extends BaseEntity {

    @ApiModelProperty("社区主键")
    private Long communityId;

    @ApiModelProperty("结算日期开始时间,具体几号")
    private Integer startDate;

    @ApiModelProperty("结算日期结束时间,具体几号")
    private Integer endDate;

}
