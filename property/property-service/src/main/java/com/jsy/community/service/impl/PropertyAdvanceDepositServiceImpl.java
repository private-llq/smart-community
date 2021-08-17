package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyAdvanceDepositRecordService;
import com.jsy.community.api.IPropertyAdvanceDepositService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.entity.property.PropertyAdvanceDepositRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.PropertyAdvanceDepositMapper;
import com.jsy.community.mapper.PropertyAdvanceDepositRecordMapper;
import com.jsy.community.mapper.ProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业预存款余额
 * @author: DKS
 * @create: 2021-08-11 16:15
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PropertyAdvanceDepositServiceImpl extends ServiceImpl<PropertyAdvanceDepositMapper, PropertyAdvanceDepositEntity> implements IPropertyAdvanceDepositService {
    
    @Autowired
    private PropertyAdvanceDepositMapper propertyAdvanceDepositMapper;
    
    @Autowired
    private HouseMapper houseMapper;
    
    @Autowired
    private ProprietorMapper proprietorMapper;
    
    @Autowired
    private PropertyAdvanceDepositRecordMapper propertyAdvanceDepositRecordMapper;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyAdvanceDepositRecordService iPropertyAdvanceDepositRecordService;
    
    /**
     * @Description: 预存款充值余额
     * @Param: [propertyAdvanceDepositEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Override
    public boolean addRechargePropertyAdvanceDeposit(PropertyAdvanceDepositEntity propertyAdvanceDepositEntity){
        PropertyAdvanceDepositEntity entity = propertyAdvanceDepositMapper.selectOne(new QueryWrapper<PropertyAdvanceDepositEntity>()
            .eq("house_id", propertyAdvanceDepositEntity.getHouseId()).eq("community_id", propertyAdvanceDepositEntity.getCommunityId()));
        if (entity != null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"房屋地址已存在！");
        }
        int row = 0;
        // 查询手机号绑定房屋的id
        List<Long> houseIdList = proprietorMapper.queryBindHouseByMobile(propertyAdvanceDepositEntity.getMobile(), propertyAdvanceDepositEntity.getCommunityId());
        for (Long houseId : houseIdList) {
            if (houseId.equals(propertyAdvanceDepositEntity.getHouseId())) {
                propertyAdvanceDepositEntity.setId(SnowFlake.nextId());
                row = propertyAdvanceDepositMapper.insert(propertyAdvanceDepositEntity);
            }
        }
        if (row == 1) {
            // 新增成功后，立即生成预存款变更明细记录
            PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
            propertyAdvanceDepositRecordEntity.setCommunityId(propertyAdvanceDepositEntity.getCommunityId());
            propertyAdvanceDepositRecordEntity.setType(2);
            propertyAdvanceDepositRecordEntity.setDepositAmount(propertyAdvanceDepositEntity.getBalance());
            propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositEntity.getBalance());
            propertyAdvanceDepositRecordEntity.setAdvanceDepositId(propertyAdvanceDepositEntity.getId());
            propertyAdvanceDepositRecordEntity.setComment(propertyAdvanceDepositEntity.getComment());
            propertyAdvanceDepositRecordEntity.setCreateBy(propertyAdvanceDepositEntity.getCreateBy());
            iPropertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
        }
        return row == 1;
    }
    
    /**
     * @Description: 修改预存款充值余额
     * @Param: [propertyAdvanceDepositEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Override
    public boolean updateRechargePropertyAdvanceDeposit(PropertyAdvanceDepositEntity propertyAdvanceDepositEntity){
        if (propertyAdvanceDepositEntity.getId() == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"请传入id！");
        }
        // 根据id查询当前余额是否充足
        PropertyAdvanceDepositEntity entity = propertyAdvanceDepositMapper.selectById(propertyAdvanceDepositEntity.getId());
        if (entity.getBalance().add(propertyAdvanceDepositEntity.getBalance()).compareTo(BigDecimal.ZERO) == -1) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"余额不足！");
        } else {
            propertyAdvanceDepositEntity.setBalanceRecord(propertyAdvanceDepositEntity.getBalance());
            propertyAdvanceDepositEntity.setBalance(entity.getBalance().add(propertyAdvanceDepositEntity.getBalance()));
        }
        propertyAdvanceDepositEntity.setUpdateTime(LocalDateTime.now());
        int row = propertyAdvanceDepositMapper.updateById(propertyAdvanceDepositEntity);
        if (row == 1) {
            // 充值成功后，立即生成预存款变更明细记录
            PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
            propertyAdvanceDepositRecordEntity.setCommunityId(propertyAdvanceDepositEntity.getCommunityId());
            propertyAdvanceDepositRecordEntity.setType(2);
            // 查最新一次记录并设置余额明细
            PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity1 = propertyAdvanceDepositRecordMapper.queryMaxCreateTimeRecord(
                propertyAdvanceDepositEntity.getId(), propertyAdvanceDepositEntity.getCommunityId());
            if (propertyAdvanceDepositEntity.getBalanceRecord().compareTo(BigDecimal.ZERO) == 1) {
                propertyAdvanceDepositRecordEntity.setDepositAmount(propertyAdvanceDepositEntity.getBalanceRecord());
            } else if (propertyAdvanceDepositEntity.getBalanceRecord().compareTo(BigDecimal.ZERO) == -1) {
                propertyAdvanceDepositRecordEntity.setPayAmount(propertyAdvanceDepositEntity.getBalanceRecord().abs());
            }
            propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositRecordEntity1.getBalanceRecord().add(propertyAdvanceDepositEntity.getBalanceRecord()));
            propertyAdvanceDepositRecordEntity.setAdvanceDepositId(propertyAdvanceDepositEntity.getId());
            propertyAdvanceDepositRecordEntity.setComment(propertyAdvanceDepositEntity.getComment());
            propertyAdvanceDepositRecordEntity.setUpdateBy(propertyAdvanceDepositEntity.getUpdateBy());
            iPropertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
        }
        return row == 1;
    }
    
    /**
     * @Description: 分页查询预存款余额
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyAdvanceDepositEntity>>
     * @Author: DKS
     * @Date: 2021/08/12
     **/
    @Override
    public PageInfo<PropertyAdvanceDepositEntity> queryPropertyAdvanceDeposit(BaseQO<PropertyAdvanceDepositQO> baseQO) {
        PropertyAdvanceDepositQO query = baseQO.getQuery();
        Page<PropertyAdvanceDepositEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<PropertyAdvanceDepositEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", query.getCommunityId());
        //是否查详情
        if (query.getId() != null) {
            queryWrapper.eq("id", query.getId());
        }
        //是否查房屋id
        if (query.getHouseId() != null) {
            queryWrapper.eq("house_id", query.getHouseId());
        }
//        else {
//            QueryWrapper<HouseEntity> houseQueryWrapper = new QueryWrapper<>();
//            houseQueryWrapper.like("building", query.getAddress()).or().like("unit", query.getAddress()).or().like("door", query.getAddress());
//            List<HouseEntity> houseEntities = houseMapper.selectList(houseQueryWrapper);
//            List<Long> houseIds = new ArrayList<>();
//            for (HouseEntity houseEntity : houseEntities) {
//                houseIds.add(houseEntity.getId());
//            }
//            if (houseIds.size() > 0) {
//                queryWrapper.in("house_id", houseIds);
//            }
//        }
        queryWrapper.orderByDesc("create_time");
        Page<PropertyAdvanceDepositEntity> pageData = propertyAdvanceDepositMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        // 补充房屋地址
        for (PropertyAdvanceDepositEntity propertyAdvanceDepositEntity : pageData.getRecords()) {
            HouseEntity houseEntity = houseMapper.selectById(propertyAdvanceDepositEntity.getHouseId());
            if (houseEntity != null) {
                propertyAdvanceDepositEntity.setAddress(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
            }
        }
        // 补充真实姓名、电话
        for (PropertyAdvanceDepositEntity propertyAdvanceDepositEntity : pageData.getRecords()) {
            ProprietorEntity proprietorEntity = proprietorMapper.queryNameAndMobileByHouseId(propertyAdvanceDepositEntity.getHouseId(), propertyAdvanceDepositEntity.getCommunityId());
            if (proprietorEntity != null) {
                propertyAdvanceDepositEntity.setRealName(proprietorEntity.getRealName());
                propertyAdvanceDepositEntity.setMobile(proprietorEntity.getMobile());
            }
        }
        PageInfo<PropertyAdvanceDepositEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     *@Author: DKS
     *@Description: 导入充值余额
     *@Param: excel:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/16 10:05
     **/
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveAdvanceDeposit(List<PropertyAdvanceDepositEntity> propertyAdvanceDepositEntityList, Long communityId, String uid) {
        // 需要添加的充值余额实体
        List<PropertyAdvanceDepositEntity> addPropertyAdvanceDepositEntityList = new ArrayList<>();
        // 需要修改的充值余额实体
        List<PropertyAdvanceDepositEntity> UpdatePropertyAdvanceDepositEntityList = new ArrayList<>();
        // 需要添加的预存款充值记录实体
        List<PropertyAdvanceDepositRecordEntity> addPropertyAdvanceDepositRecordEntityList = new ArrayList<>();
        for (PropertyAdvanceDepositEntity propertyAdvanceDepositEntity : propertyAdvanceDepositEntityList) {
            PropertyAdvanceDepositEntity entity = propertyAdvanceDepositMapper.selectOne(new QueryWrapper<PropertyAdvanceDepositEntity>()
                .eq("house_id", propertyAdvanceDepositEntity.getHouseId()).eq("community_id", communityId));
            if (entity == null) {
                // 批量新增
                PropertyAdvanceDepositEntity addPropertyAdvanceDepositEntity = new PropertyAdvanceDepositEntity();
                addPropertyAdvanceDepositEntity.setId(SnowFlake.nextId());
                addPropertyAdvanceDepositEntity.setCommunityId(communityId);
                addPropertyAdvanceDepositEntity.setHouseId(propertyAdvanceDepositEntity.getHouseId());
                addPropertyAdvanceDepositEntity.setBalance(propertyAdvanceDepositEntity.getReceivedAmount());
                addPropertyAdvanceDepositEntity.setComment(propertyAdvanceDepositEntity.getComment());
                addPropertyAdvanceDepositEntity.setDeleted(0);
                addPropertyAdvanceDepositEntity.setCreateBy(uid);
                addPropertyAdvanceDepositEntity.setCreateTime(LocalDateTime.now());
                addPropertyAdvanceDepositEntityList.add(addPropertyAdvanceDepositEntity);
                // 生成预存款变更明细记录
                PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
                propertyAdvanceDepositRecordEntity.setId(SnowFlake.nextId());
                propertyAdvanceDepositRecordEntity.setCommunityId(communityId);
                propertyAdvanceDepositRecordEntity.setType(2);
                propertyAdvanceDepositRecordEntity.setDepositAmount(propertyAdvanceDepositEntity.getReceivedAmount());
                propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositEntity.getReceivedAmount());
                propertyAdvanceDepositRecordEntity.setAdvanceDepositId(addPropertyAdvanceDepositEntity.getId());
                propertyAdvanceDepositRecordEntity.setComment(propertyAdvanceDepositEntity.getComment());
                propertyAdvanceDepositRecordEntity.setDeleted(0);
                propertyAdvanceDepositRecordEntity.setCreateBy(uid);
                propertyAdvanceDepositRecordEntity.setCreateTime(LocalDateTime.now());
                addPropertyAdvanceDepositRecordEntityList.add(propertyAdvanceDepositRecordEntity);
            } else {
                // 批量修改
                // 根据houseId查询预存款余额
                PropertyAdvanceDepositEntity entity1 = queryAdvanceDepositByHouseId(propertyAdvanceDepositEntity.getHouseId(), communityId);
                PropertyAdvanceDepositEntity updatePropertyAdvanceDepositEntity = new PropertyAdvanceDepositEntity();
                updatePropertyAdvanceDepositEntity.setHouseId(propertyAdvanceDepositEntity.getHouseId());
                updatePropertyAdvanceDepositEntity.setCommunityId(communityId);
                updatePropertyAdvanceDepositEntity.setBalance(entity1.getBalance().add(propertyAdvanceDepositEntity.getReceivedAmount()));
                updatePropertyAdvanceDepositEntity.setComment(propertyAdvanceDepositEntity.getComment());
                updatePropertyAdvanceDepositEntity.setUpdateBy(uid);
                updatePropertyAdvanceDepositEntity.setUpdateTime(LocalDateTime.now());
                UpdatePropertyAdvanceDepositEntityList.add(updatePropertyAdvanceDepositEntity);
                // 生成预存款变更明细记录
                PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
                // 查最新一次记录并设置余额明细
                PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity1 = propertyAdvanceDepositRecordMapper.queryMaxCreateTimeRecord(
                    entity1.getId(), communityId);
                propertyAdvanceDepositRecordEntity.setId(SnowFlake.nextId());
                propertyAdvanceDepositRecordEntity.setCommunityId(communityId);
                propertyAdvanceDepositRecordEntity.setType(2);
                if (propertyAdvanceDepositEntity.getReceivedAmount().compareTo(BigDecimal.ZERO) == 1) {
                    propertyAdvanceDepositRecordEntity.setDepositAmount(propertyAdvanceDepositEntity.getReceivedAmount());
                } else if (propertyAdvanceDepositEntity.getReceivedAmount().compareTo(BigDecimal.ZERO) == -1) {
                    propertyAdvanceDepositRecordEntity.setPayAmount(propertyAdvanceDepositEntity.getReceivedAmount().abs());
                }
                propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositRecordEntity1.getBalanceRecord().add(propertyAdvanceDepositEntity.getReceivedAmount()));
                propertyAdvanceDepositRecordEntity.setAdvanceDepositId(propertyAdvanceDepositRecordEntity1.getAdvanceDepositId());
                propertyAdvanceDepositRecordEntity.setComment(updatePropertyAdvanceDepositEntity.getComment());
                propertyAdvanceDepositRecordEntity.setDeleted(0);
                propertyAdvanceDepositRecordEntity.setUpdateBy(uid);
                propertyAdvanceDepositRecordEntity.setUpdateTime(LocalDateTime.now());
                addPropertyAdvanceDepositRecordEntityList.add(propertyAdvanceDepositRecordEntity);
            }
        }
        // 批量新增预存款充值
        Integer saveAdvanceDepositRow = 0;
        Integer updateAdvanceDepositRow = 0;
        if (addPropertyAdvanceDepositEntityList.size() > 0) {
            saveAdvanceDepositRow = propertyAdvanceDepositMapper.saveAdvanceDeposit(addPropertyAdvanceDepositEntityList);
        }
        // 批量修改预存款充值
        if (UpdatePropertyAdvanceDepositEntityList.size() > 0) {
            updateAdvanceDepositRow = propertyAdvanceDepositMapper.UpdateAdvanceDeposit(UpdatePropertyAdvanceDepositEntityList);
        }
        // 批量新增预存款充值明细记录
        propertyAdvanceDepositRecordMapper.saveAdvanceDepositRecord(addPropertyAdvanceDepositRecordEntityList);
        return saveAdvanceDepositRow + updateAdvanceDepositRow;
    }
    
    /**
     *@Author: DKS
     *@Description: 根据houseId查询预存款余额
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/16 10:11
     **/
    public PropertyAdvanceDepositEntity queryAdvanceDepositByHouseId (Long houseId, Long communityId) {
        return propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(houseId, communityId);
    }
}

