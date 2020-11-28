package com.jsy.community.qo.proprietor;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


/**
 * 数据传输对象
 * 这个类的作用主要用于接收 和社区消息相关的改 前端参数
 * @author YuLF
 * @since 2020-11-28 13:36
 */
@Data
@ApiModel("社区消息接收参数对象")
public class CommunityInformQO implements Serializable {


    @NotNull(groups = {updateCommunityInformValidate.class}, message = "id不合法")
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "社区通知消息体(内容)")
    private String content;

    @ApiModelProperty(value = "0代表消息未启用，1代表消息启用")
    @Range(  groups = {updateCommunityInformValidate.class}, min = 0, max = 1, message = "社区消息启用状态不正确")
    private Integer enabled;

    @ApiModelProperty(value = "社区通知消息状态，0紧急，1重要，2一般")
    @Range(  groups = {updateCommunityInformValidate.class}, min = 0, max = 2, message = "消息状态不正确")
    private Integer state;

    @ApiModelProperty(value = "社区通知消息头(标题)")
    private String title;


    /**
     * 更新车辆前端参数验证接口
     */
    public interface updateCommunityInformValidate{}

}
