package com.jsy.community.vo.property;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 推送消息返参
 * @Date: 2021/7/23 16:05
 * @Version: 1.0
 **/
public class PushInfromVO implements Serializable {

    private String id;

    // 推送消息标题
    private String pushTitle;

    // 推送消息内容
    private String pushMsg;

    // 推送者ID
    private List<String> acctId;

    // 推送者名称
    private List<String> acctName;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getAcctId() {
        return acctId;
    }

    public void setAcctId(List<String> acctId) {
        this.acctId = acctId;
    }

    public List<String> getAcctName() {
        return acctName;
    }

    public void setAcctName(List<String> acctName) {
        this.acctName = acctName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public PushInfromVO() {
    }

    public PushInfromVO(String id, String pushTitle, String pushMsg, List<String> acctId, List<String> acctName, LocalDateTime createTime) {
        this.id = id;
        this.pushTitle = pushTitle;
        this.pushMsg = pushMsg;
        this.acctId = acctId;
        this.acctName = acctName;
        this.createTime = createTime;
    }
}
