package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @return
 * @Description 业主通知消息是否已读
 * @Date 2020/12/4 10:06
 * @Param
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("社区通知消息实体类")
@TableName("t_user_inform")
public class UserInformEntity extends BaseEntity {
    @ApiModelProperty(value = "通知消息ID")
    private Long informId;
    @ApiModelProperty(value = "业主ID")
    private Long userId;
    @ApiModelProperty(value = "通知消息是否已读，1表示已读，0表示未读")
    private Integer informStatus;
    @ApiModelProperty(value = "房间ID")
    private Long houseId;


}
