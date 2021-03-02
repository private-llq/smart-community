package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 缴费单位实体类
 * @author: Hu
 * @since: 2021/2/26 11:25
 * @Param:
 * @return:
 */
@Data
@TableName("t_pay_company")
@ApiModel(value="PayCompany对象", description="缴费单位")
public class PayCompanyEntity extends BaseEntity {

    @ApiModelProperty(value = "缴费类型id")
    private Long typeId;
    
    @ApiModelProperty(value = "市区id")
    private Long regionId;

    @ApiModelProperty(value = "缴费单位")
    private String name;

}
