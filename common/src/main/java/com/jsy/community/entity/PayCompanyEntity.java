package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @return
 * @Author lihao
 * @Description 缴费单位
 * @Date 2020/12/11 11:01
 * @Param
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_pay_company")
@ApiModel(value="PayCompany对象", description="缴费单位")
public class PayCompanyEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "缴费类型id")
    private Long typeId;
    
    @ApiModelProperty(value = "市区id")
    private Long region_id;

    @ApiModelProperty(value = "缴费单位")
    private String name;

}
