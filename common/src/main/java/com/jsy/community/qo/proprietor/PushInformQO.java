package com.jsy.community.qo.proprietor;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 推送消息接参
 * @Date: 2021/7/23 10:13
 * @Version: 1.0
 **/
@Data
public class PushInformQO implements Serializable {

    @NotNull(groups = UpdateDetailValidate.class, message = "消息id不能为空")
    private Long id;

    private String uid;

    // 推送消息标题
    @NotBlank(groups = {AddPushInformValidateGroup.class, UpdateDetailValidate.class}, message = "请填写推送消息标题")
    private String pushTitle;

    // 推送消息内容
    @NotBlank(groups = {AddPushInformValidateGroup.class, UpdateDetailValidate.class}, message = "请填写推送消息内容")
    private String pushMsg;

    // 推送目标：0表示推送至所有社区、1则是具体某个社区(保留上一版逻辑,这里可以直接默认为1)
    private Integer pushTarget;

    // 需要推送的社区id列表
    @NotEmpty(groups = {AddPushInformValidateGroup.class, UpdateDetailValidate.class}, message = "请选择推送社区")
    private List<Long> communityIds;

    // 推送开关,0关闭推送,1开启推送
    private Integer pushTag;

    // 新增验证组
    public interface AddPushInformValidateGroup{}
    // 更新验证组
    public interface UpdateDetailValidate{}
}
