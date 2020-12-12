package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 户号组
 * </p>
 *
 * @author lihao
 * @since 2020-12-10
 */
@Data
//@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
@TableName("t_pay_group")
@ApiModel(value="PayGroup对象", description="户号组")
public class PayGroupEntity extends BaseEntity {

//    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业主id")
    private String uid;

    @ApiModelProperty(value = "户组名")
    private String name;

    @ApiModelProperty(value = "1我家，2父母，3房东，4朋友，5其他")
    private Integer type;

}
