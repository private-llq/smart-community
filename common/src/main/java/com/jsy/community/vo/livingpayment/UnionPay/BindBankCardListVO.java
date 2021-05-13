package com.jsy.community.vo.livingpayment.UnionPay;

import lombok.Data;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 绑定的银行卡返参列表
 * @Date: 2021/4/15 16:15
 * @Version: 1.0
 **/
@Data
public class BindBankCardListVO {
    List<BindBankCardVO> rowList;
}
