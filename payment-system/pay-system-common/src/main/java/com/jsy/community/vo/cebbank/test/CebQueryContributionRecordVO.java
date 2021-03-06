package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询缴费记录VO
 * @Date: 2021/11/13 14:48
 * @Version: 1.0
 **/
@Data
public class CebQueryContributionRecordVO implements Serializable {
    // 返回对象
    private CebQueryContributionRecordVO hkPaymentRecordsModel;
    private List<CebContributionRecordsModelVO> recordsModel;
    private CebPageInfoVO pageInfo;

}
