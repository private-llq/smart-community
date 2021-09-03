package com.jsy.community.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.entity.shop.ShopImgEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @Author: Pipi
 * @Description: 房屋租赁记录表服务实现
 * @Date: 2021/8/31 14:48
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_lease)
public class AssetLeaseRecordServiceImpl extends ServiceImpl<AssetLeaseRecordMapper, AssetLeaseRecordEntity> implements AssetLeaseRecordService {

    @Autowired
    private AssetLeaseRecordMapper assetLeaseRecordMapper;

    @Autowired
    private ShopLeaseMapper shopLeaseMapper;

    @Autowired
    private HouseLeaseMapper houseLeaseMapper;

    @Autowired
    private ShopImgMapper shopImgMapper;

    @Autowired
    private LeaseOperationRecordMapper leaseOperationRecordMapper;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ProprietorUserService userService;

    /**
     * @param assetLeaseRecordEntity : 房屋租赁记录表实体
     * @author: Pipi
     * @description: 新增租赁签约记录
     * @return: java.lang.Integer
     * @date: 2021/8/31 16:01
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addLeaseRecord(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        // 检查租户是否实名认证
        Integer integer = userService.userIsRealAuth(assetLeaseRecordEntity.getTenantUid());
        if (integer <= 0) {
            throw new LeaseException(JSYError.NO_REAL_NAME_AUTH);
        }
        // 检查申请是否已经存在
        QueryWrapper<AssetLeaseRecordEntity> assetLeaseRecordEntityQueryWrapper = new QueryWrapper<>();
        assetLeaseRecordEntityQueryWrapper.eq("asset_id", assetLeaseRecordEntity.getAssetId());
        assetLeaseRecordEntityQueryWrapper.eq("tenant_uid", assetLeaseRecordEntity.getTenantUid());
        AssetLeaseRecordEntity RecordExistEntity = assetLeaseRecordMapper.selectOne(assetLeaseRecordEntityQueryWrapper);
        if (RecordExistEntity != null) {
            throw new LeaseException("签约申请已经存在,请不要重复发起");
        }
        assetLeaseRecordEntity.setId(SnowFlake.nextId());
        assetLeaseRecordEntity.setDeleted(0);
        assetLeaseRecordEntity.setCreateTime(LocalDateTime.now());
        assetLeaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        // 查询资产信息
        if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
            // 房屋
            QueryWrapper<HouseLeaseEntity> leaseEntityQueryWrapper = new QueryWrapper<>();
            leaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
            HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(leaseEntityQueryWrapper);
            if (houseLeaseEntity == null) {
                throw new LeaseException("该房屋不存在!");
            }
            assetLeaseRecordEntity.setHomeOwnerUid(houseLeaseEntity.getUid());
            assetLeaseRecordEntity.setImageId(houseLeaseEntity.getHouseImageId());
            assetLeaseRecordEntity.setTitle(houseLeaseEntity.getHouseTitle());
            assetLeaseRecordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
            assetLeaseRecordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
            assetLeaseRecordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
            assetLeaseRecordEntity.setCommunityId(houseLeaseEntity.getHouseCommunityId());
            assetLeaseRecordEntity.setPrice(houseLeaseEntity.getHousePrice());
        } else if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
            // 商铺
            // 查商铺信息
            QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
            shopLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
            ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
            // 查商铺图片
            QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
            shopImgEntityQueryWrapper.eq("shop_id", assetLeaseRecordEntity.getAssetId());
            shopImgEntityQueryWrapper.last("limit 1");
            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
            if (shopImgEntity == null) {
                throw new LeaseException("该商铺不存在!");
            }
            assetLeaseRecordEntity.setHomeOwnerUid(shopLeaseEntity.getUid());
            assetLeaseRecordEntity.setImageId(shopImgEntity.getId());
            assetLeaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
            assetLeaseRecordEntity.setAdvantageId(shopLeaseEntity.getShopFacility());
            assetLeaseRecordEntity.setCommunityId(shopLeaseEntity.getCommunityId());
            assetLeaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
        } else {
            throw new LeaseException("请传递正确的资产类型;1:商铺;2:房屋");
        }
        //写入租赁操作数据
        LeaseOperationRecordEntity leaseOperationRecordEntity = new LeaseOperationRecordEntity();
        leaseOperationRecordEntity.setAssetType(assetLeaseRecordEntity.getAssetType());
        leaseOperationRecordEntity.setAssetLeaseRecordId(assetLeaseRecordEntity.getId());
        leaseOperationRecordEntity.setOperation(assetLeaseRecordEntity.getOperation());
        leaseOperationRecordEntity.setId(SnowFlake.nextId());
        leaseOperationRecordEntity.setDeleted(0);
        leaseOperationRecordEntity.setCreateTime(LocalDateTime.now());
        leaseOperationRecordMapper.insert(leaseOperationRecordEntity);
        // 写入租赁数据
        return assetLeaseRecordMapper.insert(assetLeaseRecordEntity);
    }

    /**
     * @param assetLeaseRecordEntity : 房屋租赁记录表实体
     * @param uid                    : 登录用户uid
     * @author: Pipi
     * @description: 停止签约(租客取消 / 房东拒绝)
     * @return: java.lang.Integer
     * @date: 2021/9/3 10:30
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer operationContract(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        Integer result = 0;
        switch (assetLeaseRecordEntity.getOperationType()) {
            case 2:
                // 房东接受申请
                result = acceptingApply(assetLeaseRecordEntity, uid);
                break;
            case 7:
                // 租客取消申请
                result = cancelApply(assetLeaseRecordEntity, uid);
                break;
            case 8:
                // 8房东拒绝申请
                result = rejectionApply(assetLeaseRecordEntity, uid);
                break;
            case 9:
                // 租客再次申请
                result = reapply(assetLeaseRecordEntity, uid);
                break;
            default:
                throw new LeaseException("未知操作!");
        }
        return result;
    }

    /**
     * @author: Pipi
     * @description: 房东接受申请
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid: 登录用户uid
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:17
     **/
    public Integer acceptingApply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("home_owner_uid", uid);
        ArrayList<Integer> processStatusList = new ArrayList<>();
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.RELAUNCH.getCode());
        queryWrapper.in("operation", processStatusList);
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.ACCEPTING_APPLICATIONS.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @author: Pipi
     * @description: 租客取消申请
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid: 登录用户uid
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:25
     **/
    public Integer cancelApply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("tenant_uid", uid);
        ArrayList<Integer> processStatusList = new ArrayList<>();
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.ACCEPTING_APPLICATIONS.getCode());
        queryWrapper.in("operation", processStatusList);
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.CANCELLATION_REQUEST.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @author: Pipi
     * @description: 房东拒绝申请
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid: 登录用户uid
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:29
     **/
    public Integer rejectionApply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("home_owner_uid", uid);
        ArrayList<Integer> processStatusList = new ArrayList<>();
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.RELAUNCH.getCode());
        queryWrapper.in("operation", processStatusList);
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.REJECTION_OF_APPLICATION.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @author: Pipi
     * @description: 再次发起签约
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid: 登录用户uid
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:35
     **/
    public Integer reapply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("tenant_uid", uid);
        queryWrapper.eq("operation", BusinessEnum.ContractingProcessStatusEnum.REJECTION_OF_APPLICATION.getCode());
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.RELAUNCH.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @author: Pipi
     * @description:  写入租赁操作数据
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @return: void
     * @date: 2021/9/3 14:22
     **/
    private void addLeaseOperationRecord(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        LeaseOperationRecordEntity leaseOperationRecordEntity = new LeaseOperationRecordEntity();
        leaseOperationRecordEntity.setAssetType(assetLeaseRecordEntity.getAssetType());
        leaseOperationRecordEntity.setAssetLeaseRecordId(assetLeaseRecordEntity.getId());
        leaseOperationRecordEntity.setOperation(assetLeaseRecordEntity.getOperation());
        leaseOperationRecordEntity.setId(SnowFlake.nextId());
        leaseOperationRecordEntity.setDeleted(0);
        leaseOperationRecordEntity.setCreateTime(LocalDateTime.now());
        leaseOperationRecordMapper.insert(leaseOperationRecordEntity);
    }

    /**
     * @param baseQO : 分页查询条件
     * @param uid : 登录用户uid
     * @author: Pipi
     * @description: 分页查询签约列表
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/2 14:38
     **/
    @Override
    public PageInfo<AssetLeaseRecordEntity> pageContractList(BaseQO<AssetLeaseRecordEntity> baseQO, String uid) {
        Page<AssetLeaseRecordEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        PageInfo<AssetLeaseRecordEntity> pageInfo = new PageInfo<>();
        AssetLeaseRecordEntity query = baseQO.getQuery();
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_type", query.getAssetType());
        if (query.getIdentityType() == 1) {
            // 房东
            queryWrapper.eq("home_owner_uid", uid);
        } else if (query.getIdentityType() == 2) {
            // 租客
            queryWrapper.eq("tenant_uid", uid);
        } else {
            throw new LeaseException("查询用户身份不明确!");
        }
        page = assetLeaseRecordMapper.selectPage(page, queryWrapper);
        BeanUtils.copyProperties(page, pageInfo);
        if (!CollectionUtils.isEmpty(pageInfo.getRecords())) {
            for (AssetLeaseRecordEntity record : pageInfo.getRecords()) {
                // 资产优势标签
                List<Long> advantageId = MyMathUtils.analysisTypeCode(record.getAdvantageId());
                if (!CollectionUtils.isEmpty(advantageId)) {
                    record.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                }
                if (StringUtils.isNotBlank(record.getTypeCode())) {
                    record.setHouseType(HouseHelper.parseHouseType(record.getTypeCode()));
                }
            }
        }
        return pageInfo;
    }
}
