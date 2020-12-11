package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 缴费户号
 * </p>
 *
 * @author lihao
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_pay_house_owner")
@ApiModel(value="PayHouseOwner对象", description="缴费户号")
public class PayHouseOwnerEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "户组id")
    private Long groupId;

    @ApiModelProperty(value = "户号")
    private String payNumber;

    @ApiModelProperty(value = "缴费单位")
    private String payCompany;

    @ApiModelProperty(value = "缴费时间")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal payExpen;

    @ApiModelProperty(value = "户主余额")
    private BigDecimal payBalance;

    @ApiModelProperty(value = "缴费类型 0 水费 1 电费 2燃气费")
    private Integer type;


}
