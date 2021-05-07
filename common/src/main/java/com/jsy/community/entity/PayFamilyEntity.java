package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 户号实体类
 * @author: Hu
 * @since: 2021/2/26 11:25
 * @Param:
 * @return:
 */
@Data
@TableName("t_pay_family")
@ApiModel(value="PayFamily对象", description="缴费户号")
public class PayFamilyEntity extends BaseEntity {

    @ApiModelProperty(value = "户组id")
    private Long groupId;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "户号")
    private String familyName;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "缴费单位")
    private Long companyId;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "缴费类型 0 水费 1 电费 2燃气费")
    private Long typeId;

}
