package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyAdvanceDepositService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.PropertyAdvanceDepositMapper;
import com.jsy.community.mapper.ProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    
    /**
     * @Description: 预存款充值余额
     * @Param: [propertyAdvanceDepositEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Override
    public boolean addRechargePropertyAdvanceDeposit(PropertyAdvanceDepositEntity propertyAdvanceDepositEntity){
        int row = 0;
        // 查询手机号绑定房屋的id
        List<Long> houseIdList = proprietorMapper.queryBindHouseBymobile(propertyAdvanceDepositEntity.getMobile(), propertyAdvanceDepositEntity.getCommunityId());
        for (Long houseId : houseIdList) {
            if (houseId == propertyAdvanceDepositEntity.getHouse_id()) {
                propertyAdvanceDepositEntity.setId(SnowFlake.nextId());
                row = propertyAdvanceDepositMapper.insert(propertyAdvanceDepositEntity);
            }
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
            throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(),"请传入id！");
        }
        PropertyAdvanceDepositEntity entity = propertyAdvanceDepositMapper.selectById(propertyAdvanceDepositEntity.getId());
        if (entity.getBalance().add(propertyAdvanceDepositEntity.getBalance()).compareTo(BigDecimal.ZERO) == -1) {
            throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(),"余额不足！");
        } else {
            propertyAdvanceDepositEntity.setBalance(entity.getBalance().add(propertyAdvanceDepositEntity.getBalance()));
        }
        propertyAdvanceDepositEntity.setUpdateTime(LocalDateTime.now());
        int row = propertyAdvanceDepositMapper.updateById(propertyAdvanceDepositEntity);
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
            queryWrapper.like("house_id",query.getHouseId());
        }
        queryWrapper.orderByDesc("create_time");
        Page<PropertyAdvanceDepositEntity> pageData = propertyAdvanceDepositMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        // 补充房屋地址
        for (PropertyAdvanceDepositEntity propertyAdvanceDepositEntity : pageData.getRecords()) {
            HouseEntity houseEntity = houseMapper.selectById(propertyAdvanceDepositEntity.getHouse_id());
            if (houseEntity != null) {
                propertyAdvanceDepositEntity.setAddress(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
            }
        }
        // 补充真实姓名、电话
        for (PropertyAdvanceDepositEntity propertyAdvanceDepositEntity : pageData.getRecords()) {
            ProprietorEntity proprietorEntity = proprietorMapper.queryNameAndMobileByHouseId(propertyAdvanceDepositEntity.getHouse_id(), propertyAdvanceDepositEntity.getCommunityId());
            if (proprietorEntity != null) {
                propertyAdvanceDepositEntity.setRealName(proprietorEntity.getRealName());
                propertyAdvanceDepositEntity.setMobile(proprietorEntity.getMobile());
            }
        }
        PageInfo<PropertyAdvanceDepositEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
}

