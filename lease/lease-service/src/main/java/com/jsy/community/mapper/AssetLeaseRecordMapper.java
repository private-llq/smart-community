package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 房屋租赁记录表Mapper
 * @Date: 2021/8/31 14:46
 * @Version: 1.0
 **/
public interface AssetLeaseRecordMapper extends BaseMapper<AssetLeaseRecordEntity> {

    /**
     * @author: Pipi
     * @description: 房东查看单个资产的签约列表
     * @param assetId: 资产ID
     * @param assetType: 资产类型;1:商铺;2:房屋
     * @param contractStatus: 签约状态;1:未签约;2:签约中;3已签约
     * @return: java.util.List<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/6 14:58
     **/
    List<AssetLeaseRecordEntity> landlordContractListByAssetId(@Param("assetId") Long assetId,
                                                               @Param("assetType") Integer assetType,
                                                               @Param("homeOwnerUid") String homeOwnerUid,
                                                               @Param("contractStatus") Integer contractStatus);

    /**
     * @author: Pipi
     * @description: 根据条件查列表
     * @param assetLeaseRecordEntity:
     * @return: java.util.List<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/25 13:38
     **/
    List<AssetLeaseRecordEntity> queryList(@Param("entity") AssetLeaseRecordEntity assetLeaseRecordEntity);

    /**
     * @author: Pipi
     * @description: 设置为过期
     * @param id:
     * @return: java.lang.Integer
     * @date: 2021/9/25 15:11
     **/
    Integer setExpiredById(Long id);
}
