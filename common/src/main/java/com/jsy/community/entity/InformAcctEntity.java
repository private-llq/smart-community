package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 推送消息与推送者关系表
 * @Date: 2021/7/23 15:22
 * @Version: 1.0
 **/
@TableName(value = "t_push_inform_acct")
public class InformAcctEntity implements Serializable {

    // 主键
    private String id;

    // 推送消息id
    private String informId;

    // 推送消息账号id;可能是社区id也有可能是其他推送号id
    private String acctId;

    // 推送消息号名称;可能是社区名也有可能是其他推送号的名称
    private String acctName;

    // 推送消息号头像
    private String acctAvatar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInformId() {
        return informId;
    }

    public void setInformId(String informId) {
        this.informId = informId;
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getAcctAvatar() {
        return acctAvatar;
    }

    public void setAcctAvatar(String acctAvatar) {
        this.acctAvatar = acctAvatar;
    }

    public InformAcctEntity() {
    }

    public InformAcctEntity(String id, String informId, String acctId, String acctName, String acctAvatar) {
        this.id = id;
        this.informId = informId;
        this.acctId = acctId;
        this.acctName = acctName;
        this.acctAvatar = acctAvatar;
    }
}
