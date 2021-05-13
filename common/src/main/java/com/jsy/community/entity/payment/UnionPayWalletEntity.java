package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Pipi
 * @Description: 用户银联钱包表
 * @Date: 2021/4/10 11:24
 * @Version: 1.0
 **/
@ApiModel("用户银联钱包表")
@Data
@TableName("t_user_union_pay_wallet")
public class UnionPayWalletEntity implements Serializable {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户uid")
    private String uid;

    @ApiModelProperty(value = "银联用户中心uid")
    private String userUuid;

    @ApiModelProperty(value = "钱包户名,与银行账户户名一致")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String mobileNo;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "银行卡号")
    private String bankAcctNo;

    @ApiModelProperty(value = "用户银联钱包id")
    private String walletId;

    @ApiModelProperty(value = "认证类型,0：姓名身份证2要素,1：银行卡3要素,2：运营商3要素")
    private Integer authType;

    @ApiModelProperty(value = "是否激活,0-不激活,1-激活,默认激活")
    private Integer activeStatus;

    @ApiModelProperty(value = "密码密文：使用encryptType指定的方式进行加密（最终密文是base64编码的字符串）")
    private String encryptPwd;

    @ApiModelProperty(value = "加密类型：1：H5密码键盘加密（密码键盘先使用公钥加密，然后自身再加密）。2：非H5加密。（加密控件先使用公钥加密，然后控件自身再加密）。")
    private Integer encryptType;

    @ApiModelProperty(value = "用户联系电话")
    private String userTelNo;

    @ApiModelProperty(value = "用户电子邮箱")
    private String userEmail;

    @ApiModelProperty(value = "用户地址")
    private String userAddr;

    @ApiModelProperty(value = "性别,M：男,F：女")
    private String userSex;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "删除标记,可用于注销,0:未删除,1:已删除")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
