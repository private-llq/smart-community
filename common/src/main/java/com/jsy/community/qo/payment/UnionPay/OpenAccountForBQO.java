package com.jsy.community.qo.payment.UnionPay;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Pipi
 * @Description: 银联支付B端开户接参
 * @Date: 2021/4/16 11:06
 * @Version: 1.0
 **/
@Data
public class OpenAccountForBQO implements Serializable {

    @ApiModelProperty("原申请ID-重新提交申请时上送")
    private String origRegId;

    @ApiModelProperty("原随机验证码-重新提交申请时上送")
    private String origRandomValidCode;

    @ApiModelProperty("企业名称-必填")
    @NotBlank(message = "企业名称不能为空")
    private String companyName;

    @ApiModelProperty("营业执照编号-必填")
    @NotBlank(message = "营业执照编号不能为空")
    private String bizLicNo;

    // 营业执照有效日期开始日期字符串-必填(接口上送时使用)
    private String bizLicStartDate;

    @ApiModelProperty("营业执照有效日期开始日期-必填")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "营业执照有效日期开始日期不能为空")
    private Date bizLicStartTime;

    // 营业执照有效日期结束日期字符串-必填(接口上送时使用)
    private String bizLicEndDate;

    @ApiModelProperty("营业执照有效日期结束日期-必填")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "营业执照有效日期结束日期不能为空")
    private Date bizLicEndTime;

    @ApiModelProperty("申请人类型,1:法定代表人,2:代理人-必填")
    @NotBlank(message = "申请人类型不能为空")
    @Range(min = 1, max = 2, message = "申请人类型超出范围,1:法定代表人,2:代理人")
    private String applicantType;

    @ApiModelProperty("法人姓名-必填")
    @NotBlank(message = "法人姓名不能为空")
    private String legalName;

    @ApiModelProperty("法人身份证号码-必填")
    @NotBlank(message = "法人身份证号码不能为空")
    private String legalIdCard;

    // 法人身份证有效日期开始日期字符串-必填(接口上送时使用)
    private String legalIdCardStartDate;

    @ApiModelProperty("法人身份证有效日期开始日期-必填")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "法人身份证有效日期开始日期不能为空")
    private Date legalIdCardStartTime;

    // 法人身份证有效日期结束日期字符串-必填(接口上送时使用)
    private String legalIdCardEndDate;

    @ApiModelProperty("法人身份证有效日期结束日期-必填")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "法人身份证有效日期结束日期不能为空")
    private Date legalIdCardEndTime;

    @ApiModelProperty("法人手机号码-必填")
    @NotBlank(message = "法人手机号码不能为空")
    private String legalPhoneNum;

    @ApiModelProperty("银行账户类型,0:对公,1:对私-必填")
    @NotNull(message = "银行账户类型不能为空")
    @Range(min = 0, max = 1, message = "银行账户类型超出范围,0:对公,1:对私")
    private String bankAcctType;

    @ApiModelProperty("结算账户-必填")
    @NotBlank(message = "结算账户不能为空")
    private String settAcctNo;

    @ApiModelProperty("结算账户名称-必填")
    @NotBlank(message = "结算账户名称不能为空")
    private String settAcctName;

    @ApiModelProperty("3位或8位的开户行号-必填")
    @NotBlank(message = "3位或8位的开户行号不能为空")
    private String bankNo;

    @ApiModelProperty("企业联系人-必填")
    @NotBlank(message = "企业联系人不能为空")
    private String contactName;

    @ApiModelProperty("企业联系人手机号-必填")
    @NotBlank(message = "企业联系人手机号不能为空")
    private String contactPhoneNum;

    @ApiModelProperty("代理人姓名-条件必填")
    private String agentName;

    @ApiModelProperty("代理人身份证号码-条件必填")
    private String agentIdCard;

    // 代理人身份证有效日期开始日期字符串(接口上送时使用)
    private String agentIdCardStartDate;

    @ApiModelProperty("代理人身份证有效日期开始日期-条件必填")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date agentIdCardStartTime;

    // 代理人身份证有效日期结束日期字符串(接口上送时使用)
    private String agentIdCardEndDate;

    @ApiModelProperty("代理人身份证有效日期结束日期-条件必填")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date agentIdCardEndTime;

    @ApiModelProperty("代理人手机号-条件必填")
    private String agentPhoneNum;

    @ApiModelProperty("营业执照（扫描件）文件名")
    private String licFileName;

    @ApiModelProperty("法人身份证（扫描件）文件名")
    private String legalIdCardFileName;

    @ApiModelProperty("代理人身份证（扫描件）文件名")
    private String agentIdCardFileName;

    @ApiModelProperty("代理人授权书文件名")
    private String agentGrantFileName;
}
