package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.AssetLeaseRecordService;
import com.jsy.community.api.IHouseConstService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.api.ProprietorUserService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.entity.shop.ShopImgEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

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
//            assetLeaseRecordEntity.setImageId(houseLeaseEntity.getHouseImageId());
//            assetLeaseRecordEntity.setTitle(houseLeaseEntity.getHouseTitle());
//            assetLeaseRecordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
//            assetLeaseRecordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
//            assetLeaseRecordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
//            assetLeaseRecordEntity.setPrice(houseLeaseEntity.getHousePrice());
            assetLeaseRecordEntity.setCommunityId(houseLeaseEntity.getHouseCommunityId());
        } else if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
            // 商铺
            // 查商铺信息
            QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
            shopLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
            ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
            if (shopLeaseEntity == null) {
                throw new LeaseException("该商铺不存在!");
            }
            // 查商铺图片
//            QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
//            shopImgEntityQueryWrapper.eq("shop_id", assetLeaseRecordEntity.getAssetId());
//            shopImgEntityQueryWrapper.last("limit 1");
//            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
            assetLeaseRecordEntity.setHomeOwnerUid(shopLeaseEntity.getUid());
//            assetLeaseRecordEntity.setImageId(shopImgEntity != null ? shopImgEntity.getId() : null);
//            assetLeaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
//            assetLeaseRecordEntity.setAdvantageId(shopLeaseEntity.getShopFacility());
            assetLeaseRecordEntity.setCommunityId(shopLeaseEntity.getCommunityId());
//            assetLeaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
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
     * @description: 对签约进行操作(租客取消申请/房东拒绝申请/租客再次申请/房东接受申请)
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
                // 房东接受申请后,资产不能编辑,所以在这里将房屋信息写入记录表
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
     * @description: 房东接受申请;房东接受申请后,资产不能编辑,所以在这里将房屋信息写入记录表;
     * 房屋:整租不能同时接受多个申请,合租和单间出租可以
     * 商铺:不能同时接受多个申请
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
        // 查询资产信息
        if (recordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
            // 2:房屋
            // 查询房屋信息
            QueryWrapper<HouseLeaseEntity> leaseEntityQueryWrapper = new QueryWrapper<>();
            leaseEntityQueryWrapper.eq("id", recordEntity.getAssetId());
            HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(leaseEntityQueryWrapper);
            if (houseLeaseEntity == null) {
                throw new LeaseException("该房屋已不存在!");
            }
            if (houseLeaseEntity.getHouseLeasemodeId() == 2) {
                // 整租,检查是否存在多个签约
                checkMultipleApply(assetLeaseRecordEntity.getId(), recordEntity.getAssetId());
            }
            // 写入房屋信息
            recordEntity.setImageId(houseLeaseEntity.getHouseImageId());
            recordEntity.setTitle(houseLeaseEntity.getHouseTitle());
            recordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
            recordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
            recordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
            recordEntity.setPrice(houseLeaseEntity.getHousePrice());
        } else {
            // 1:商铺
            // 商铺同房屋整租;检查是否存在多个签约
            checkMultipleApply(assetLeaseRecordEntity.getId(), recordEntity.getAssetId());
            // 查询商铺信息
            QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
            shopLeaseEntityQueryWrapper.eq("id", recordEntity.getAssetId());
            ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
            if (shopLeaseEntity == null) {
                throw new LeaseException("该商铺已不存在!");
            }
            // 查商铺图片
            QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
            shopImgEntityQueryWrapper.eq("shop_id", recordEntity.getAssetId());
            shopImgEntityQueryWrapper.last("limit 1");
            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
            // 写入商铺信息
            assetLeaseRecordEntity.setImageId(shopImgEntity != null ? shopImgEntity.getId() : null);
            assetLeaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
            assetLeaseRecordEntity.setAdvantageId(shopLeaseEntity.getShopFacility());
            assetLeaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
        }
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
     * @description: 检查是否有多个签约;
     * @param id: 当前签约ID
     * @param assetId: 资产id
     * @return: java.lang.Boolean
     * @date: 2021/9/4 14:20
     **/
    private void checkMultipleApply(Long id, Long assetId) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_id", assetId);
        queryWrapper.ne("id", id);
        List<Integer> processStatusList = new ArrayList<>(Arrays.asList(2, 3, 4, 5));
        queryWrapper.in("operation", processStatusList);
        queryWrapper.last("limit 0,1");
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (assetLeaseRecordEntity != null) {
            throw new LeaseException("该房屋存在多个签约");
        }
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
     * @param assetLeaseRecordEntity : 查询条件
     * @param uid : 登录用户uid
     * @author: Pipi
     * @description: 分页查询签约列表
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/2 14:38
     **/
    @Override
    public Map<String, List<AssetLeaseRecordEntity>> pageContractList(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        HashMap<String, List<AssetLeaseRecordEntity>> hashMap = new HashMap<>();
        // 已签约
        Map<Long, AssetLeaseRecordEntity> contractedMap = new HashMap<>();
        // 签约中
        Map<Long, AssetLeaseRecordEntity> underContractMap = new HashMap<>();
        // 未签约
        Map<Long, AssetLeaseRecordEntity> notContractedMap = new HashMap<>();
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_type", assetLeaseRecordEntity.getAssetType());
        if (assetLeaseRecordEntity.getIdentityType() == 1) {
            // 房东
            queryWrapper.eq("home_owner_uid", uid);
            List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    // 资产优势标签
                    List<Long> advantageId = MyMathUtils.analysisTypeCode(record.getAdvantageId());
                    if (!CollectionUtils.isEmpty(advantageId)) {
                        record.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                    }
                    if (StringUtils.isNotBlank(record.getTypeCode())) {
                        record.setHouseType(HouseHelper.parseHouseType(record.getTypeCode()));
                    }
                    switch (record.getOperation()) {
                        case 1:
                            // 发起签约->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                record.setContractNumber(1);
                                notContractedMap.put(record.getAssetId(), record);
                            } else {
                                notContractedMap.get(record.getAssetId()).setContractNumber(notContractedMap.get(record.getAssetId()).getContractNumber() + 1);
                            }
                            break;
                        case 2:
                            // 接受申请->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 3:
                            // 拟定合同->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 4:
                            // 等待支付房租->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 5:
                            // 支付完成->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 6:
                            // 完成签约->已签约
                            if (!contractedMap.containsKey(record.getAssetId())) {
                                contractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 7:
                            // 取消申请->不在房东列表展示
                            break;
                        case 8:
                            // 拒绝申请->不在房东列表展示
                            break;
                        case 9:
                            // 重新发起->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                record.setContractNumber(1);
                                notContractedMap.put(record.getAssetId(), record);
                            } else {
                                notContractedMap.get(record.getAssetId()).setContractNumber(notContractedMap.get(record.getAssetId()).getContractNumber() + 1);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } else if (assetLeaseRecordEntity.getIdentityType() == 2) {
            // 租客
            queryWrapper.eq("tenant_uid", uid);
            List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    switch (record.getOperation()) {
                        case 1:
                            // 发起签约->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 2:
                            // 接受申请->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 3:
                            // 拟定合同->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 4:
                            // 等待支付房租->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 5:
                            // 支付完成->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 6:
                            // 完成签约->已签约
                            if (!contractedMap.containsKey(record.getAssetId())) {
                                contractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 7:
                            // 取消申请->不在租客列表展示
                            break;
                        case 8:
                            // 拒绝申请->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 9:
                            // 重新发起->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } else {
            throw new LeaseException("查询用户身份不明确!");
        }
        // 组装已签约
        if (!CollectionUtils.isEmpty(contractedMap)) {
            for (Long assetId : contractedMap.keySet()) {
                if (hashMap.containsKey("contracted")) {
                    hashMap.get("contracted").add(contractedMap.get(assetId));
                } else {
                    List<AssetLeaseRecordEntity> assetLeaseRecordEntities = new ArrayList<>();
                    assetLeaseRecordEntities.add(contractedMap.get(assetId));
                    hashMap.put("contracted", assetLeaseRecordEntities);
                }
            }
        }
        // 组装签约中
        if (!CollectionUtils.isEmpty(underContractMap)) {
            for (Long assetId : underContractMap.keySet()) {
                if (hashMap.containsKey("underContract")) {
                    hashMap.get("underContract").add(underContractMap.get(assetId));
                } else {
                    List<AssetLeaseRecordEntity> assetLeaseRecordEntities = new ArrayList<>();
                    assetLeaseRecordEntities.add(underContractMap.get(assetId));
                    hashMap.put("underContract", assetLeaseRecordEntities);
                }
            }
        }
        // 组装未签约
        if (!CollectionUtils.isEmpty(notContractedMap)) {
            for (Long assetId : notContractedMap.keySet()) {
                if (hashMap.containsKey("notContracted")) {
                    hashMap.get("notContracted").add(notContractedMap.get(assetId));
                } else {
                    List<AssetLeaseRecordEntity> assetLeaseRecordEntities = new ArrayList<>();
                    assetLeaseRecordEntities.add(notContractedMap.get(assetId));
                    hashMap.put("notContracted", assetLeaseRecordEntities);
                }
            }
        }
        return hashMap;
    }
}
