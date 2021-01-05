package com.jsy.community.qo.sys;

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
@ApiModel( description="接收系统消息的对象类")
public class SysInformQO implements Serializable {

    @ApiModelProperty(value = "通知消息头(标题)")
    @Length(groups = {addSysInformValidate.class}, min = 1, max = 32, message = "消息标题长度1~32")
    @NotBlank( groups = {addSysInformValidate.class}, message = "请输入一个标题!")
    private String title;

    @ApiModelProperty(value = "通知消息头(副标题)")
    @Length(groups = {addSysInformValidate.class}, min = 1, max = 128, message = "消息副标题长度1~128")
    @NotBlank( groups = {addSysInformValidate.class}, message = "请输入一个副标题说明!")
    private String subTitle;


    @ApiModelProperty(value = "通知消息体(内容)")
    @Length(groups = {addSysInformValidate.class}, min = 1, max = 5000, message = "消息标题长度1~5000")
    @NotBlank( groups = {addSysInformValidate.class}, message = "请输入一个通知消息内容!")
    private String content;

    @ApiModelProperty(value = "0代表消息未启用，1代表消息启用")
    @NotNull( groups = {addSysInformValidate.class}, message = "请选择消息是否需要启用!")
    private Integer enabled;


    /**
     * 添加系统消息接口
     */
    public interface addSysInformValidate{}



}
