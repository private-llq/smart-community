package com.jsy.community.service.impl;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.AssetLeaseRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.mapper.AssetLeaseRecordMapper;
import com.jsy.community.mapper.HouseLeaseMapper;
import com.jsy.community.mapper.ShopLeaseMapper;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Pipi
 * @Description: 房屋租赁记录表服务实现
 * @Date: 2021/8/31 14:48
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class AssetLeaseRecordServiceImpl extends ServiceImpl<AssetLeaseRecordMapper, AssetLeaseRecordEntity> implements AssetLeaseRecordService {

    @Autowired
    private ShopLeaseMapper shopLeaseMapper;

    @Autowired
    private HouseLeaseMapper houseLeaseMapper;

    /**
     * @param assetLeaseRecordEntity : 房屋租赁记录表实体
     * @author: Pipi
     * @description: 新增租赁签约记录
     * @return: java.lang.Integer
     * @date: 2021/8/31 16:01
     **/
    @Override
    public Integer addLeaseRecord(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        assetLeaseRecordEntity.setId(SnowFlake.nextId());
        assetLeaseRecordEntity.setAssetId(0L);
        assetLeaseRecordEntity.setAssetType(0);
        assetLeaseRecordEntity.setHomeOwnerUid("");
        assetLeaseRecordEntity.setTenantUid("");
        assetLeaseRecordEntity.setImageId("");
        assetLeaseRecordEntity.setTitle("");
        assetLeaseRecordEntity.setAdvantageId(0L);
        assetLeaseRecordEntity.setTypeCode(0);
        assetLeaseRecordEntity.setDirectionId(0);
        assetLeaseRecordEntity.setCommunityId(0L);
        assetLeaseRecordEntity.setPrice(0.0D);
        assetLeaseRecordEntity.setId(0L);
        assetLeaseRecordEntity.setIdStr("");
        assetLeaseRecordEntity.setDeleted(0);
        assetLeaseRecordEntity.setCreateTime(LocalDateTime.now());
        assetLeaseRecordEntity.setUpdateTime(LocalDateTime.now());

        return null;
    }
}
