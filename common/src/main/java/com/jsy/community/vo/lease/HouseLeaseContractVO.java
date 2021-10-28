package com.jsy.community.vo.lease;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: 房屋拟定合同的部分预填参数
 * @Date: 2021/10/27 15:57
 * @Version: 1.0
 **/
@Data
public class HouseLeaseContractVO implements Serializable {

    // 乙方姓名
    private String partyB;

    // 乙方联系方式
    private String telB;

    // 乙方身份证
    private String idCardB;

    // 房屋坐落地（地址）
    private String address;

    // 房屋建筑面积
    private Double builtupArea;

    // 房屋装修情况
    private String decorationLevel;

    // 房屋设施设备
    private String facilities;

    // 租房用途
    private String purpose;

    // 每月租金
    private BigDecimal monthlyRent;

    // 每月租金大写
    private String monthlyRentInWords;

    // 租金支付方式;选项ABCD（[A：月付]， [B：季度付]， [C：半年付]，[D：年付]）
    private String paymentOptions;
}
