package com.jsy.community.qo.proprietor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 推送消息接参
 * @Date: 2021/7/23 10:13
 * @Version: 1.0
 **/
public class PushInformQO implements Serializable {

    private String uid;

    // 推送消息标题
    @NotBlank(groups = AddPushInformValidateGroup.class, message = "请填写推送消息标题")
    private String pushTitle;

    // 推送消息内容
    @NotBlank(groups = AddPushInformValidateGroup.class, message = "请填写推送消息内容")
    private String pushMsg;

    // 推送目标：0表示推送至所有社区、1则是具体某个社区(保留上一版逻辑,这里可以直接默认为1)
    private Integer pushTarget;

    // 需要推送的社区id列表
    @NotEmpty(groups = AddPushInformValidateGroup.class, message = "请选择推送社区")
    private List<Long> communityIds;

    // 推送开关,0关闭推送,1开启推送
    private Integer pushTag;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushMsg() {
        return pushMsg;
    }

    public void setPushMsg(String pushMsg) {
        this.pushMsg = pushMsg;
    }

    public Integer getPushTarget() {
        return pushTarget;
    }

    public void setPushTarget(Integer pushTarget) {
        this.pushTarget = pushTarget;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Integer getPushTag() {
        return pushTag;
    }

    public void setPushTag(Integer pushTag) {
        this.pushTag = pushTag;
    }

    public PushInformQO() {
    }

    public PushInformQO(String uid, @NotBlank(groups = AddPushInformValidateGroup.class, message = "请填写推送消息标题") String pushTitle, @NotBlank(groups = AddPushInformValidateGroup.class, message = "请填写推送消息内容") String pushMsg, Integer pushTarget, @NotEmpty(groups = AddPushInformValidateGroup.class, message = "请选择推送社区") List<Long> communityIds, Integer pushTag) {
        this.uid = uid;
        this.pushTitle = pushTitle;
        this.pushMsg = pushMsg;
        this.pushTarget = pushTarget;
        this.communityIds = communityIds;
        this.pushTag = pushTag;
    }

    public interface AddPushInformValidateGroup{}
}
