package com.jsy.community.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统消息接收参数类对象
 * @author YuLF
 * @since 2020-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel( description="返回系统消息的对象类")
public class SysInformVO implements Serializable {

    @ApiModelProperty(value = "通知消息头(标题)")
    private String title;

    @ApiModelProperty(value = "通知消息头(副标题)")
    private String subTitle;

    @ApiModelProperty(value = "通知消息体(内容)")
    private String content;

    @ApiModelProperty(value = "0代表消息未启用，1代表消息启用")
    private Integer enabled;

    @ApiModelProperty(value = "消息是否已读")
    private boolean read = false;


}
