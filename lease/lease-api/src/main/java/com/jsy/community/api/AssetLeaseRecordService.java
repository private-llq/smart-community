package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;
import java.util.Map;

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

    /**
     * @author: Pipi
     * @description: 对签约进行操作(租客取消申请/房东拒绝申请/租客再次申请/房东接受申请)
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid: 登录用户uid
     * @return: java.lang.Integer
     * @date: 2021/9/3 10:30
     **/
    Integer operationContract(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid);

    /**
     * @author: Pipi
     * @description: 分页查询签约列表
     * @param assetLeaseRecordEntity: 查询条件
     * @param uid: 登录用户uid
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/2 14:38
     **/
    Map<String, List<AssetLeaseRecordEntity>> pageContractList(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid);

    /**
     * @author: Pipi
     * @description: 房东查看单个资产的签约列表
     * @param assetLeaseRecordEntity: 查询条件
     * @return: java.util.List<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/6 14:53
     **/
    List<AssetLeaseRecordEntity> landlordContractList(AssetLeaseRecordEntity assetLeaseRecordEntity);

    /**
     * @author: Pipi
     * @description: 查询签约详情
     * @param assetLeaseRecordEntity: 查询条件
     * @param uid: 登录用户uid
     * @return: com.jsy.community.entity.proprietor.AssetLeaseRecordEntity
     * @date: 2021/9/6 17:39
     **/
    AssetLeaseRecordEntity contractDetail(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid);
}
