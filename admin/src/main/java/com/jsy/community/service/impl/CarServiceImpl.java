package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.CarQO;
import com.jsy.community.service.ICarService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 住户信息 服务实现类
 * </p>
 *
 * @author DKS
 * @since 2021-10-22
 */
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, CarEntity> implements ICarService {

    @Resource
    private HouseMapper houseMapper;
    
    @Resource
    private CarMapper carMapper;
    
    @Resource
    private PropertyCompanyMapper propertyCompanyMapper;
    
    @Resource
    private CommunityMapper communityMapper;
    
    @Resource
    private CarPositionMapper carPositionMapper;

    /**
     * @Description: 【住户】条件查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.CarEntity>
     * @Author: DKS
     * @Date: 2021/10/22
     **/
    @Override
    public PageInfo<CarEntity> queryCar(BaseQO<CarQO> baseQO) {
        CarQO query = baseQO.getQuery();
        Page<CarEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<CarEntity> queryWrapper = new QueryWrapper<>();
        //是否查小区
        if (query.getCommunityId() != null) {
            queryWrapper.eq("community_id", query.getCommunityId());
        }
        //是否查姓名/手机/车牌号
        if (StringUtils.isNotBlank(query.getNameOrMobileOrCarNumber())) {
            queryWrapper.like("owner", query.getNameOrMobileOrCarNumber());
            queryWrapper.or().like("contact", query.getNameOrMobileOrCarNumber());
            queryWrapper.or().like("car_plate", query.getNameOrMobileOrCarNumber());
        }
        queryWrapper.orderByDesc("create_time");
        Page<CarEntity> pageData = carMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        for (CarEntity carEntity : pageData.getRecords()) {
            // 补充物业公司名称
            CommunityEntity communityEntity = communityMapper.selectById(carEntity.getCommunityId());
            if (communityEntity != null) {
                PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(communityEntity.getPropertyId());
                carEntity.setCompanyName(companyEntity.getName());
                // 补充社区名称
                carEntity.setCommunityName(communityEntity.getName());
            }
            // 补充车位号
            CarPositionEntity carPositionEntity = carPositionMapper.selectById(carEntity.getCarPositionId());
            if (carPositionEntity != null) {
                carEntity.setCarPositionText(carPositionEntity.getCarPosition());
                // 补充房屋信息
                HouseEntity houseEntity = houseMapper.selectById(carPositionEntity.getHouseId());
                if (houseEntity != null) {
                    carEntity.setHouseAddress(houseEntity.getBuilding() + "-" + houseEntity.getUnit() + "-" + houseEntity.getDoor());
                }
            }
            // 补充车辆类型名称
            carEntity.setTypeText(carEntity.getType() == 1 ? "临时" : carEntity.getType() == 2 ? "月租" : carEntity.getType() == 3 ? "业主" : "");
        }
        PageInfo<CarEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     * @Description: 查询住户
     * @Param: HouseEntity
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.CarEntity>
     * @Author: DKS
     * @Date: 2021/10/26 10:46
     **/
    @Override
    public List<CarEntity> queryExportCarExcel(CarQO carQO) {
        List<CarEntity> carEntities;
        QueryWrapper<CarEntity> queryWrapper = new QueryWrapper<>();
        //是否查小区
        if (carQO.getCommunityId() != null) {
            queryWrapper.eq("community_id", carQO.getCommunityId());
        }
        //是否查姓名/手机/车牌号
        if (StringUtils.isNotBlank(carQO.getNameOrMobileOrCarNumber())) {
            queryWrapper.like("owner", carQO.getNameOrMobileOrCarNumber());
            queryWrapper.or().like("contact", carQO.getNameOrMobileOrCarNumber());
            queryWrapper.or().like("car_plate", carQO.getNameOrMobileOrCarNumber());
        }
        queryWrapper.orderByDesc("create_time");
        carEntities = carMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(carEntities)) {
            return new ArrayList<>();
        }
        for (CarEntity carEntity : carEntities) {
            // 补充物业公司名称
            CommunityEntity communityEntity = communityMapper.selectById(carEntity.getCommunityId());
            if (communityEntity != null) {
                PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(communityEntity.getPropertyId());
                carEntity.setCompanyName(companyEntity.getName());
                // 补充社区名称
                carEntity.setCommunityName(communityEntity.getName());
            }
            // 补充车位号
            CarPositionEntity carPositionEntity = carPositionMapper.selectById(carEntity.getCarPositionId());
            if (carPositionEntity != null) {
                carEntity.setCarPositionText(carPositionEntity.getCarPosition());
                // 补充房屋信息
                HouseEntity houseEntity = houseMapper.selectById(carPositionEntity.getHouseId());
                if (houseEntity != null) {
                    carEntity.setHouseAddress(houseEntity.getBuilding() + "-" + houseEntity.getUnit() + "-" + houseEntity.getDoor());
                }
            }
            // 补充车辆类型名称
            carEntity.setTypeText(carEntity.getType() == 1 ? "临时" : carEntity.getType() == 2 ? "月租" : carEntity.getType() == 3 ? "业主" : "");
        }
        return carEntities;
    }
}
