package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: B端用户钱包表实体
 * @Date: 2021/5/11 11:48
 * @Version: 1.0
 **/
@Data
@ApiModel("B端用户钱包表实体")
@TableName("t_user_b_union_pay_wallet")
public class BUnionPayWalletEntity extends BaseEntity {

    @ApiModelProperty("用户uid")
    private String uid;

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("企业名称")
    private String companyName;

    @ApiModelProperty("营业执照号")
    private String bizLicNo;

    @ApiModelProperty("法人名称")
    private String legalName;

    @ApiModelProperty("银行账户类型;0：对公银行账户；1：对私银行卡；")
    private Integer bankAcctType;
}
