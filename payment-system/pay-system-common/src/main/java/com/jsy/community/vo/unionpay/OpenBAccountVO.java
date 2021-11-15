package com.jsy.community.vo.unionpay;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联开B端账户返参
 * @Date: 2021/4/16 17:00
 * @Version: 1.0
 **/
@Data
public class OpenBAccountVO extends UnionPayBaseVO implements Serializable {

    /**
     * 登记ID
     */
    private String regId;

    /**
     * 随机校验码
     */
    private String randomValidCode;

    /**
     * 上传目录路径
     */
    private String uploadFolderPath;

    /**
     * 确认金金额
     */
    private String confirmAmt;

    /**
     * 确认金收款方开户行行名
     */
    private String bankName;

    /**
     * 确认金收款方电子联行号
     */
    private String elecBankNo;

    /**
     * 确认金收款方支行名称
     */
    private String elecBankName;

    /**
     * 确认金收款方账户名称
     */
    private String bankAcctName;

    /**
     * 确认金收款方账号
     */
    private String bankAcctNo;
}
