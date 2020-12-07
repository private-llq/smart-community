package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-07 09:50
 **/
@ApiModel("消息已读状态查询")
public class UserInformQO implements Serializable {

    @ApiModelProperty("查询页数")
    private Integer pageNo=2;

    @ApiModelProperty("查询每页条数")
    private Integer pageSize=10;

    @ApiModelProperty("按通知ID查询")
    private Long informId;

    @ApiModelProperty("按客户名称模糊查询")
    private String userName;

    @ApiModelProperty("按已读状态查询,1已读0未读,默认1")
    private Integer informStatus=1;

    @ApiModelProperty("按社区ID查询")
    private Long communityId;

    @ApiModelProperty("按房间ID查询")
    private Long houseId;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getInformId() {
        return informId;
    }

    public void setInformId(Long informId) {
        this.informId = informId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getInformStatus() {
        return informStatus;
    }

    public void setInformStatus(Integer informStatus) {
        this.informStatus = informStatus;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }
}
