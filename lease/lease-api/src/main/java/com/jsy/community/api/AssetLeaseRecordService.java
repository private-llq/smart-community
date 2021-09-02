package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;

/**
 * @Author: Pipi
 * @Description: 房屋租赁记录表服务
 * @Date: 2021/8/31 14:47
 * @Version: 1.0
 **/
public interface AssetLeaseRecordService extends IService<AssetLeaseRecordEntity> {

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @author: Pipi
     * @description: 新增租赁签约记录
     * @return: java.lang.Integer
     * @date: 2021/8/31 16:01
     **/
    Integer addLeaseRecord(AssetLeaseRecordEntity assetLeaseRecordEntity);
}
