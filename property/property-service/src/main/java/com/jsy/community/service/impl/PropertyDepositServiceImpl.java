package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyDepositService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.PropertyDepositEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.CarPositionMapper;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.PropertyDepositMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyDepositQO;
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

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @program: com.jsy.community
 * @description: 物业押金账单
 * @author: DKS
 * @create: 2021-08-10 17:35
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PropertyDepositServiceImpl extends ServiceImpl<PropertyDepositMapper, PropertyDepositEntity> implements IPropertyDepositService {
    
    @Autowired
    private PropertyDepositMapper propertyDepositMapper;
    
    @Autowired
    private HouseMapper houseMapper;
    
    @Autowired
    private CommunityMapper communityMapper;
    
    @Autowired
    private CarPositionMapper carPositionMapper;
    
    /**
     * @Description: 新增物业押金账单
     * @Param: [propertyDepositEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Override
    public boolean addPropertyDeposit(PropertyDepositEntity propertyDepositEntity){
        propertyDepositEntity.setId(SnowFlake.nextId());
        int row = propertyDepositMapper.insert(propertyDepositEntity);
        return row == 1;
    }
    
    /**
     * @Description: 修改物业押金账单
     * @Param: [propertyDepositEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Override
    public boolean updatePropertyDeposit(PropertyDepositEntity propertyDepositEntity){
        if (propertyDepositEntity.getId() == null) {
            throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(),"请传入id！");
        }
        propertyDepositEntity.setUpdateTime(LocalDateTime.now());
        int row = propertyDepositMapper.updateById(propertyDepositEntity);
        return row == 1;
    }
    
    /**
     * @Description: 删除物业押金账单
     * @Param: [id, communityId]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Override
    public boolean deletePropertyDeposit(Long id, Long communityId){
        PropertyDepositEntity entity = propertyDepositMapper.selectOne(new QueryWrapper<PropertyDepositEntity>().eq("id", id).eq("community_id", communityId));
        if (entity == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "物业押金账单数据不存在");
        }
        return propertyDepositMapper.deleteById(id) == 1;
    }
    
    /**
     * @Description: 分页查询物业押金账单
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyDepositEntity>>
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Override
    public PageInfo<PropertyDepositEntity> queryPropertyDeposit(BaseQO<PropertyDepositQO> baseQO) {
        PropertyDepositQO query = baseQO.getQuery();
        Page<PropertyDepositEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<PropertyDepositEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", query.getCommunityId());
        //是否查详情
        if (query.getId() != null) {
            queryWrapper.eq("id", query.getId());
        }
        //是否查关联类型
        if (query.getDepositType() != null) {
            queryWrapper.eq("deposit_type",query.getDepositType());
        }
        //是否查关联目标
        if (query.getDepositTargetId() != null) {
            queryWrapper.eq("deposit_target_id",query.getDepositTargetId());
        }
        //是否查收费项目关键字
        if (query.getPayService() != null) {
            queryWrapper.like("pay_service",query.getPayService());
        }
        queryWrapper.orderByDesc("create_time");
        Page<PropertyDepositEntity> pageData = propertyDepositMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        // 补充关联类型名称、状态名称
        for (PropertyDepositEntity propertyDepositEntity : pageData.getRecords()) {
            propertyDepositEntity.setDepositTypeName(propertyDepositEntity.getDepositType() == 1 ? "房屋" : "车位");
            propertyDepositEntity.setStatusName(propertyDepositEntity.getStatus() == 1 ? "待支付" : propertyDepositEntity.getStatus() == 2 ? "已支付" : "已退回");
        }
        // 补充关联目标名称
        for (PropertyDepositEntity propertyDepositEntity : pageData.getRecords()) {
            // 如果关联类型为1.房屋
            if (propertyDepositEntity.getDepositType() == 1) {
                HouseEntity houseEntity = houseMapper.selectById(propertyDepositEntity.getDepositTargetId());
                if (houseEntity != null) {
                    propertyDepositEntity.setDepositTargetIdName(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
                }
            } else if (propertyDepositEntity.getDepositType() == 2) {
                // 如果关联类型为2.车位
                CarPositionEntity carPositionEntity = carPositionMapper.selectById(propertyDepositEntity.getDepositTargetId());
                if (carPositionEntity != null) {
                    propertyDepositEntity.setDepositTargetIdName(carPositionEntity.getCarPosition());
                }
            }
            // 补充小区名
            CommunityEntity communityEntity = communityMapper.selectById(propertyDepositEntity.getCommunityId());
            if (communityEntity != null) {
                propertyDepositEntity.setCommunityName(communityEntity.getName());
            }
        }
        PageInfo<PropertyDepositEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     * @Description: 通过id获取押金凭证数据
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/20
     **/
    @Override
    public PropertyDepositEntity getDepositById(Long id) {
        PropertyDepositEntity propertyDepositEntity = propertyDepositMapper.selectById(id);
        // 如果关联类型为1.房屋
        if (propertyDepositEntity.getDepositType() == 1) {
            HouseEntity houseEntity = houseMapper.selectById(propertyDepositEntity.getDepositTargetId());
            if (houseEntity != null) {
                propertyDepositEntity.setDepositTargetIdName(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
            }
            propertyDepositEntity.setDepositTypeName("房屋");
        } else if (propertyDepositEntity.getDepositType() == 2) {
            // 如果关联类型为2.车位
            CarPositionEntity carPositionEntity = carPositionMapper.selectById(propertyDepositEntity.getDepositTargetId());
            if (carPositionEntity != null) {
                propertyDepositEntity.setDepositTargetIdName(carPositionEntity.getCarPosition());
            }
            propertyDepositEntity.setDepositTypeName("车位");
        }
        // 补充小区名
        CommunityEntity communityEntity = communityMapper.selectById(propertyDepositEntity.getCommunityId());
        if (communityEntity != null) {
            propertyDepositEntity.setCommunityName(communityEntity.getName());
        }
        if (propertyDepositEntity.getDepositVoucher() == null) {
            // 补充凭证号:目标id+时间戳（8）+押金id最后4位+随机码3位-->17+8+4+3=32位
            String subTargetId = String.valueOf(propertyDepositEntity.getDepositTargetId());
            String timestamp = String.valueOf(System.currentTimeMillis());
            String subTimestamp = timestamp.substring(timestamp.length() - 8);
            String idStr = String.valueOf(id);
            String subIdStr = idStr.substring(idStr.length() - 4);
            String subRandomStr = UUID.randomUUID().toString().substring(0, 3);
            propertyDepositEntity.setDepositVoucher(subTargetId + subTimestamp + subIdStr + subRandomStr);
            // 凭证号存数据库
            propertyDepositMapper.updateById(propertyDepositEntity);
        }
        return propertyDepositEntity;
    }
}

