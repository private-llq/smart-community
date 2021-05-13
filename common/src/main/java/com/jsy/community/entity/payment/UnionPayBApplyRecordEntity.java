package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: B端用户开户申请记录实体
 * @Date: 2021/5/10 15:13
 * @Version: 1.0
 **/
@Data
@ApiModel("B端用户开户申请记录实体")
    @TableName("t_union_pay_b_apply_record")
public class UnionPayBApplyRecordEntity extends BaseEntity {

    @ApiModelProperty("用户ID")
    private String uid;

    @ApiModelProperty("前端跳转的url")
    private String jumpUrl;

    @ApiModelProperty("凭据")
    private String ticket;

    @ApiModelProperty("注册号")
    private String registerNo;

    @ApiModelProperty("操作类型,0:获取凭据,1:创建注册登记,2:提交材料成功,3:材料初审不通过,4:复核不通过,5:复核通过")
    private Integer operationType;

    @ApiModelProperty("注册状态:01-已发送注册链接email；02-已验证邮箱（已验证注册链接）；03-认证材料待提交；04-材料自动检查不通过；05-认证材料已提交（待审核）；06-初审通过；07-初审不通过；08-复审通过；09-复审不通过；10-注册完成（流程完结，添加用户成功、开户成功）；11-注册失败；12-取消注册（流程完结，取消注册）；21-申请待提交（子商户注册时使用）；22-已提交申请（子商户注册时使用）；23-已验证手机（子商户注册时使用，验证手机号完成之后进入03认证材料待提交）")
    private String regStatus;

    @ApiModelProperty("注册使用的电话号码")
    private String mobileNo;

    @ApiModelProperty("确认金状态")
    private String confirmAmtStatus;

    @ApiModelProperty("确认金金额")
    private BigDecimal confirmAmt;

    @ApiModelProperty("取消原因内容")
    private String unpassReasonContent;

}
