package com.jsy.community.vo.unionpay;

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
public class BEndAccountOpeningVO extends UnionPayBaseVO implements Serializable {

    /**
     * 统一信息用代码证
     */
    private String bizLicNo;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 是否己开户登记
     */
    private String openStatus;

    /**
     * 钱包列表
     */
    private List<WalletVO> list;
}
