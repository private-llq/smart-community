package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: B端开户列表返参
 * @Date: 2021/5/10 9:10
 * @Version: 1.0
 **/
@Data
@ApiModel("B端开户列表返参")
public class BEndAccountOpeningVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty(" 统一信息用代码证")
    private String bizLicNo;

    @ApiModelProperty("企业名称")
    private String companyName;

    @ApiModelProperty("是否己开户登记")
    private String openStatus;

    @ApiModelProperty("钱包列表")
    private List<WalletVO> list;
}
