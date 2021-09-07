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
import com.jsy.community.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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

    @Resource
    private RedisTemplate<String, String> redisTemplate;

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
            assetLeaseRecordEntity.setHomeOwnerUid(shopLeaseEntity.getUid());
            assetLeaseRecordEntity.setCommunityId(shopLeaseEntity.getCommunityId());
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
            // 查询房屋图片url
            List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
            // 写入房屋信息
            recordEntity.setImageUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
            recordEntity.setTitle(houseLeaseEntity.getHouseTitle());
            recordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
            recordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
            recordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
            recordEntity.setPrice(houseLeaseEntity.getHousePrice());
            recordEntity.setProvinceId(houseLeaseEntity.getHouseProvinceId());
            recordEntity.setCityId(houseLeaseEntity.getHouseCityId());
            recordEntity.setAreaId(houseLeaseEntity.getHouseAreaId());
            recordEntity.setAddress(houseLeaseEntity.getHouseAddress());
            recordEntity.setFloor(houseLeaseEntity.getHouseFloor());
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
            assetLeaseRecordEntity.setImageUrl(shopImgEntity != null ? shopImgEntity.getImgUrl() : null);
            assetLeaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
            assetLeaseRecordEntity.setAdvantageId(shopLeaseEntity.getShopFacility());
            assetLeaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
            assetLeaseRecordEntity.setSummarize(shopLeaseEntity.getSummarize());
            assetLeaseRecordEntity.setCityId(shopLeaseEntity.getCityId());
            assetLeaseRecordEntity.setAreaId(shopLeaseEntity.getAreaId());
            assetLeaseRecordEntity.setFloor(shopLeaseEntity.getFloor());
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
                // 查询状态为1,9的资产信息
                List<Long> assetIds = new ArrayList<>();
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getOperation() == 1 || record.getOperation() == 9) {
                        assetIds.add(record.getAssetId());
                    }
                }
                Map<Long, ShopLeaseEntity> shopEntityMap = new HashMap<>();
                Map<Long, HouseLeaseEntity> houseEntityMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(assetIds)) {
                    if (assetLeaseRecordEntity.getAssetType() == 1) {
                        // 商铺
                        QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                        shopLeaseEntityQueryWrapper.in("id", assetIds);
                        List<ShopLeaseEntity> shopLeaseEntities = shopLeaseMapper.selectList(shopLeaseEntityQueryWrapper);
                        if (!CollectionUtils.isEmpty(shopLeaseEntities)) {
                            for (ShopLeaseEntity shopLeaseEntity : shopLeaseEntities) {
                                // 查商铺图片
                                QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                                shopImgEntityQueryWrapper.eq("shop_id", shopLeaseEntity.getId());
                                shopImgEntityQueryWrapper.last("limit 1");
                                ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                                shopLeaseEntity.setShopShowImg(shopImgEntity == null ? null : shopImgEntity.getImgUrl());
                                shopEntityMap.put(shopLeaseEntity.getId(), shopLeaseEntity);
                            }
                        }
                    } else {
                        // 房屋
                        QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                        houseLeaseEntityQueryWrapper.in("id", assetIds);
                        List<HouseLeaseEntity> houseLeaseEntities = houseLeaseMapper.selectList(houseLeaseEntityQueryWrapper);
                        if (!CollectionUtils.isEmpty(houseLeaseEntities)) {
                            for (HouseLeaseEntity houseLeaseEntity : houseLeaseEntities) {
                                // 查询房屋图片
                                List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                                houseLeaseEntity.setHouseImgUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                                // 查出 房屋标签 ...
                                List<Long> advantageId = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseAdvantageId());
                                if (!CollectionUtils.isEmpty(advantageId)) {
                                    houseLeaseEntity.setHouseAdvantageMap(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                                }
                                // 房屋类型 code转换为文本 如 4室2厅1卫
                                houseLeaseEntity.setHouseTypeStr(HouseHelper.parseHouseType(houseLeaseEntity.getHouseTypeCode()));
                                // 房屋朝向
                                houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                                houseEntityMap.put(houseLeaseEntity.getId(), houseLeaseEntity);
                            }
                        }
                    }
                }

                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (assetLeaseRecordEntity.getAssetType() == 1 && shopEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 9)) {
                        // 商铺
                        record.setImageUrl(shopEntityMap.get(record.getAssetId()).getShopShowImg());
                        record.setTitle(shopEntityMap.get(record.getAssetId()).getTitle());
                        record.setAdvantageId(shopEntityMap.get(record.getAssetId()).getShopFacility());
                        record.setPrice(shopEntityMap.get(record.getAssetId()).getMonthMoney());
                        record.setSummarize(shopEntityMap.get(record.getAssetId()).getSummarize());
                    } else if (assetLeaseRecordEntity.getAssetType() == 2 && houseEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 9)) {
                        // 房屋
                        record.setImageUrl(houseEntityMap.get(record.getAssetId()).getHouseImgUrl());
                        record.setTitle(houseEntityMap.get(record.getAssetId()).getHouseTitle());
                        record.setAdvantageId(houseEntityMap.get(record.getAssetId()).getHouseAdvantageId());
                        record.setTypeCode(houseEntityMap.get(record.getAssetId()).getHouseTypeCode());
                        record.setDirectionId(houseEntityMap.get(record.getAssetId()).getHouseDirectionId());
                        record.setPrice(houseEntityMap.get(record.getAssetId()).getHousePrice());
                        record.setHouseAdvantageCode(houseEntityMap.get(record.getAssetId()).getHouseAdvantageMap());
                        record.setHouseType(houseEntityMap.get(record.getAssetId()).getHouseTypeStr());
                    }
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
                // 查询操作类型为1,8,9的资产信息
                List<Long> assetIds = new ArrayList<>();
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9) {
                        assetIds.add(record.getAssetId());
                    }
                }
                Map<Long, ShopLeaseEntity> shopEntityMap = new HashMap<>();
                Map<Long, HouseLeaseEntity> houseEntityMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(assetIds)) {
                    if (assetLeaseRecordEntity.getAssetType() == 1) {
                        // 商铺
                        QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                        shopLeaseEntityQueryWrapper.in("id", assetIds);
                        List<ShopLeaseEntity> shopLeaseEntities = shopLeaseMapper.selectList(shopLeaseEntityQueryWrapper);
                        if (!CollectionUtils.isEmpty(shopLeaseEntities)) {
                            for (ShopLeaseEntity shopLeaseEntity : shopLeaseEntities) {
                                // 查商铺图片
                                QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                                shopImgEntityQueryWrapper.eq("shop_id", shopLeaseEntity.getId());
                                shopImgEntityQueryWrapper.last("limit 1");
                                ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                                shopLeaseEntity.setShopShowImg(shopImgEntity == null ? null : shopImgEntity.getImgUrl());
                                shopEntityMap.put(shopLeaseEntity.getId(), shopLeaseEntity);
                            }
                        }
                    } else {
                        // 房屋
                        QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                        houseLeaseEntityQueryWrapper.in("id", assetIds);
                        List<HouseLeaseEntity> houseLeaseEntities = houseLeaseMapper.selectList(houseLeaseEntityQueryWrapper);
                        if (!CollectionUtils.isEmpty(houseLeaseEntities)) {
                            for (HouseLeaseEntity houseLeaseEntity : houseLeaseEntities) {
                                // 查询房屋图片
                                List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                                houseLeaseEntity.setHouseImgUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                                // 查出 房屋标签 ...
                                List<Long> advantageId = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseAdvantageId());
                                if (!CollectionUtils.isEmpty(advantageId)) {
                                    houseLeaseEntity.setHouseAdvantageMap(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                                }
                                // 房屋类型 code转换为文本 如 4室2厅1卫
                                houseLeaseEntity.setHouseTypeStr(HouseHelper.parseHouseType(houseLeaseEntity.getHouseTypeCode()));
                                // 查出房屋朝向
                                houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                                houseEntityMap.put(houseLeaseEntity.getId(), houseLeaseEntity);
                            }
                        }
                    }
                }
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (assetLeaseRecordEntity.getAssetType() == 1 && shopEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9)) {
                        // 商铺
                        record.setImageUrl(shopEntityMap.get(record.getAssetId()).getShopShowImg());
                        record.setTitle(shopEntityMap.get(record.getAssetId()).getTitle());
                        record.setAdvantageId(shopEntityMap.get(record.getAssetId()).getShopFacility());
                        record.setPrice(shopEntityMap.get(record.getAssetId()).getMonthMoney());
                        record.setSummarize(shopEntityMap.get(record.getAssetId()).getSummarize());
                    } else if (assetLeaseRecordEntity.getAssetType() == 2 && houseEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9)) {
                        // 房屋
                        record.setImageUrl(houseEntityMap.get(record.getAssetId()).getHouseImgUrl());
                        record.setTitle(houseEntityMap.get(record.getAssetId()).getHouseTitle());
                        record.setAdvantageId(houseEntityMap.get(record.getAssetId()).getHouseAdvantageId());
                        record.setTypeCode(houseEntityMap.get(record.getAssetId()).getHouseTypeCode());
                        record.setDirectionId(houseEntityMap.get(record.getAssetId()).getHouseDirectionId());
                        record.setPrice(houseEntityMap.get(record.getAssetId()).getHousePrice());
                        record.setHouseAdvantageCode(houseEntityMap.get(record.getAssetId()).getHouseAdvantageMap());
                        record.setHouseType(houseEntityMap.get(record.getAssetId()).getHouseTypeStr());
                    }
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

    /**
     * @param assetLeaseRecordEntity : 查询条件
     * @author: Pipi
     * @description: 房东查看单个资产的签约列表
     * @return: java.util.List<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/6 14:53
     **/
    @Override
    public List<AssetLeaseRecordEntity> landlordContractList(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        // 如果是未签约,要单独查资产信息,图片,这个时候要判断资产是房屋 还是商铺
        List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.landlordContractListByAssetId(assetLeaseRecordEntity.getAssetId(),
                assetLeaseRecordEntity.getAssetType(),
                assetLeaseRecordEntity.getHomeOwnerUid(),
                assetLeaseRecordEntity.getContractStatus()
        );
        if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
            if (assetLeaseRecordEntity.getContractStatus() == 1) {
                if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
                    ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
                    // 商铺
                    QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                    shopLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
                    shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
                    // 查商铺图片
                    QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                    shopImgEntityQueryWrapper.eq("shop_id", assetLeaseRecordEntity.getAssetId());
                    shopImgEntityQueryWrapper.last("limit 1");
                    ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                    for (AssetLeaseRecordEntity leaseRecordEntity : assetLeaseRecordEntities) {
                        leaseRecordEntity.setImageUrl(shopImgEntity == null ? null : shopImgEntity.getImgUrl());
                        leaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
                        leaseRecordEntity.setSummarize(shopLeaseEntity.getSummarize());
                        leaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
                    }
                } else {
                    // 房屋
                    HouseLeaseEntity houseLeaseEntity = new HouseLeaseEntity();
                    QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                    houseLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
                    houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                    List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                    // 朝向
                    houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                    // 查出 房屋标签 ...
                    List<Long> advantageId = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseAdvantageId());
                    if (!CollectionUtils.isEmpty(advantageId)) {
                        houseLeaseEntity.setHouseAdvantageMap(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                    }
                    for (AssetLeaseRecordEntity leaseRecordEntity : assetLeaseRecordEntities) {
                        leaseRecordEntity.setImageUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                        leaseRecordEntity.setTitle(houseLeaseEntity.getHouseTitle());
                        leaseRecordEntity.setPrice(houseLeaseEntity.getHousePrice());
                        leaseRecordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
                        leaseRecordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
                        leaseRecordEntity.setHouseType(HouseHelper.parseHouseType(houseLeaseEntity.getHouseTypeCode()));
                        leaseRecordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
                        leaseRecordEntity.setHouseAdvantageCode(houseLeaseEntity.getHouseAdvantageMap());
                    }
                }
            } else {
                if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
                    for (AssetLeaseRecordEntity leaseRecordEntity : assetLeaseRecordEntities) {
                        // 查出 房屋标签 ...
                        List<Long> advantageId = MyMathUtils.analysisTypeCode(leaseRecordEntity.getAdvantageId());
                        if (!CollectionUtils.isEmpty(advantageId)) {
                            leaseRecordEntity.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                        }
                        // 房屋类型 code转换为文本 如 4室2厅1卫
                        leaseRecordEntity.setHouseType(HouseHelper.parseHouseType(leaseRecordEntity.getTypeCode()));
                        // 朝向
                        leaseRecordEntity.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(leaseRecordEntity.getDirectionId())));
                    }
                }
            }

        }
        return assetLeaseRecordEntities;
    }

    /**
     * @param assetLeaseRecordEntity : 查询条件
     * @param uid : 登录用户uid
     * @author: Pipi
     * @description: 查询签约详情
     * @return: com.jsy.community.entity.proprietor.AssetLeaseRecordEntity
     * @date: 2021/9/6 17:39
     **/
    @Override
    public AssetLeaseRecordEntity contractDetail(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        if (assetLeaseRecordEntity.getIdentityType() == 1) {
            // 房东
            queryWrapper.eq("home_owner_uid", uid);
        } else {
            // 租客
            queryWrapper.eq("tenant_uid", uid);
        }
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (leaseRecordEntity == null) {
            // 为空直接返回
            return leaseRecordEntity;
        }
        if (leaseRecordEntity.getOperation() == 1 || leaseRecordEntity.getOperation() == 9) {
            // 记录表里面没有资产数据,要单独查
            if (leaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
                // 商铺
                ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
                // 商铺
                QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                shopLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
                // 查商铺图片
                QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                shopImgEntityQueryWrapper.eq("shop_id", leaseRecordEntity.getAssetId());
                shopImgEntityQueryWrapper.last("limit 1");
                ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                leaseRecordEntity.setImageUrl(shopImgEntity.getImgUrl());
                leaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
                leaseRecordEntity.setSummarize(shopLeaseEntity.getSummarize());
                leaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
                leaseRecordEntity.setFloor(shopLeaseEntity.getFloor());
            } else {
                // 房屋
                HouseLeaseEntity houseLeaseEntity = new HouseLeaseEntity();
                QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                houseLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                // 朝向
                houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                // 查出 房屋标签 ...
                List<Long> advantageId = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseAdvantageId());
                if (!CollectionUtils.isEmpty(advantageId)) {
                    houseLeaseEntity.setHouseAdvantageMap(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                }
                // 房屋类型 code转换为文本 如 4室2厅1卫
                houseLeaseEntity.setHouseTypeStr(HouseHelper.parseHouseType(houseLeaseEntity.getHouseTypeCode()));
                leaseRecordEntity.setImageUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                leaseRecordEntity.setTitle(houseLeaseEntity.getHouseTitle());
                leaseRecordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
                leaseRecordEntity.setHouseAdvantageCode(houseLeaseEntity.getHouseAdvantageMap());
                leaseRecordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
                leaseRecordEntity.setHouseType(houseLeaseEntity.getHouseTypeStr());
                leaseRecordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
                leaseRecordEntity.setPrice(houseLeaseEntity.getHousePrice());
                leaseRecordEntity.setProvinceId(houseLeaseEntity.getHouseProvinceId());
                leaseRecordEntity.setCityId(houseLeaseEntity.getHouseCityId());
                leaseRecordEntity.setAreaId(houseLeaseEntity.getHouseAreaId());
                leaseRecordEntity.setFloor(houseLeaseEntity.getHouseFloor());
                if (assetLeaseRecordEntity.getIdentityType() == 2) {
                    String province = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseProvinceId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseProvinceId());
                    String city = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseCityId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseCityId());
                    String area = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseAreaId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseAreaId());
                    String fullAddress = province + city + area + houseLeaseEntity.getHouseAddress();
                    leaseRecordEntity.setFullAddress(fullAddress);
                }
            }
        } else {
            // 记录表里面有资产数据
            if (leaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
                // 房屋
                // 朝向
                leaseRecordEntity.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(leaseRecordEntity.getDirectionId())));
                // 查出 房屋标签 ...
                List<Long> advantageId = MyMathUtils.analysisTypeCode(leaseRecordEntity.getAdvantageId());
                if (!CollectionUtils.isEmpty(advantageId)) {
                    leaseRecordEntity.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                }
                leaseRecordEntity.setHouseType(HouseHelper.parseHouseType(leaseRecordEntity.getTypeCode()));
                if (assetLeaseRecordEntity.getIdentityType() == 2) {
                    String province = redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getProvinceId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getProvinceId());
                    String city = redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getCityId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getCityId());
                    String area = redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getAreaId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getAreaId());
                    String fullAddress = province + city + area + leaseRecordEntity.getAddress();
                    leaseRecordEntity.setFullAddress(fullAddress);
                }
            }
        }
        if (assetLeaseRecordEntity.getIdentityType() == 1) {
            // 房东,查看租客信息
            UserInfoVo userInfoVo = userService.proprietorDetails(leaseRecordEntity.getTenantUid());
            leaseRecordEntity.setRealName(userInfoVo.getRealName());
            leaseRecordEntity.setTenantPhone(userInfoVo.getMobile());
            leaseRecordEntity.setTenantIdCard(userInfoVo.getIdCard());
        } else {
            // 租客,查看房东信息
            UserInfoVo userInfoVo = userService.proprietorDetails(leaseRecordEntity.getHomeOwnerUid());
            leaseRecordEntity.setLandlordName(userInfoVo.getRealName());
            leaseRecordEntity.setLandlordPhone(userInfoVo.getMobile());
        }
        if (leaseRecordEntity.getOperation() == 1 ||leaseRecordEntity.getOperation() == 7 ||leaseRecordEntity.getOperation() == 8 ||leaseRecordEntity.getOperation() == 9) {
            leaseRecordEntity.setProgressNumber(1);
        } else if (leaseRecordEntity.getOperation() == 2 || leaseRecordEntity.getOperation() == 3) {
            leaseRecordEntity.setProgressNumber(2);
        } else if (leaseRecordEntity.getOperation() == 4 || leaseRecordEntity.getOperation() == 5) {
            leaseRecordEntity.setProgressNumber(3);
        } else if (leaseRecordEntity.getOperation() == 6) {
            leaseRecordEntity.setProgressNumber(4);
        }
        return leaseRecordEntity;
    }

    /**
     * @author: Pipi
     * @description: 设置签约合同相关信息
     * @param assetLeaseRecordEntity: 签约实体
     * @return: java.lang.Integer
     * @date: 2021/9/7 10:18
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer setContractNo(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_type", assetLeaseRecordEntity.getAssetType());
        queryWrapper.eq("asset_id", assetLeaseRecordEntity.getAssetId());
        queryWrapper.eq("home_owner_uid", assetLeaseRecordEntity.getHomeOwnerUid());
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        AssetLeaseRecordEntity assetLeaseRecordEntity1 = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (assetLeaseRecordEntity1 != null) {
            assetLeaseRecordEntity1.setConId(assetLeaseRecordEntity.getConId());
            assetLeaseRecordEntity1.setStartDate(assetLeaseRecordEntity.getStartDate());
            assetLeaseRecordEntity1.setEndDate(assetLeaseRecordEntity.getEndDate());
            assetLeaseRecordEntity1.setConName(assetLeaseRecordEntity.getConName());
            assetLeaseRecordEntity1.setInitiator(assetLeaseRecordEntity.getInitiator());
            assetLeaseRecordEntity1.setSignatory(assetLeaseRecordEntity.getSignatory());
            assetLeaseRecordEntity1.setOperation(BusinessEnum.ContractingProcessStatusEnum.CONTRACT_PREPARATION.getCode());
            addLeaseOperationRecord(assetLeaseRecordEntity1);
            return assetLeaseRecordMapper.updateById(assetLeaseRecordEntity1);
        } else {
            return 0;
        }
    }
}
