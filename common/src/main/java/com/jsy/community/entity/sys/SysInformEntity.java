package com.jsy.community.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 系统消息实体类
 * @author YuLF
 * @since 2020-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_inform")
@ApiModel( description="接收系统消息的实体类")
public class SysInformEntity extends BaseEntity {

    @ApiModelProperty(value = "通知消息头(标题)")
    private String title;

    @ApiModelProperty(value = "通知消息头(副标题)")
    private String subTitle;

    @ApiModelProperty(value = "通知消息体(内容)")
    private String content;

    @ApiModelProperty(value = "0代表消息未启用，1代表消息启用")
    private Integer enabled;

    @ApiModelProperty(value = "消息浏览次数")
    private Integer browseCount;


}
