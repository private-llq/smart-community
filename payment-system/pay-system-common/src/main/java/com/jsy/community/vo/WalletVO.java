package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 钱包ID返参
 * @Date: 2021/5/10 9:12
 * @Version: 1.0
 **/
@Data
public class WalletVO implements Serializable {

    /**
     * 钱包ID
     */
    private String walletId;

    /**
     * 钱包名称
     */
    private String walletName;
}
