package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 业主房屋认证
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_house")
@ApiModel(value="UserHouse对象", description="业主房屋认证")
public class UserHouseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long uid;

    @ApiModelProperty(value = "社区id")
    private Long communityId;

    @ApiModelProperty(value = "楼栋ID")
    private Long houseId;

    @ApiModelProperty(value = "是否通过审核")
    private Integer checkStatus;



}
