package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 生活缴费组实体类
 * @author: Hu
 * @since: 2021/2/26 11:24
 * @Param:
 * @return:
 */
@Data
@TableName("t_pay_group")
@ApiModel(value="PayGroup对象", description="户号组")
public class PayGroupEntity extends BaseEntity {

    @ApiModelProperty(value = "业主id")
    private String uid;

    @ApiModelProperty(value = "户组名")
    private String name;

    @ApiModelProperty(value = "1我家，2父母，3房东，4朋友，5其他")
    private Integer type;

}
