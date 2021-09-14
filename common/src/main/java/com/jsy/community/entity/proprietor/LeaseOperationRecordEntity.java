package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Pipi
 * @Description: 租赁操作记录表实体
 * @Date: 2021/8/31 9:59
 * @Version: 1.0
 **/
@Data
@TableName("t_lease_operation_record")
@EqualsAndHashCode(callSuper = false)
public class LeaseOperationRecordEntity extends BaseEntity {
    // 资产类型;1:房屋;2:商铺
    private Integer assetType;
    // 资产租赁记录表id
    private Long assetLeaseRecordId;
    // 签约操作状态;1:(租客)发起租赁申请;2:接受申请;3:拟定合同;4:等待支付房租;5:支付完成;6:完成签约;7:取消申请;8:拒绝申请;9:重新发起;31:(房东)发起签约/重新发起;32:取消发起;
    private Integer operation;
}
