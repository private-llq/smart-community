package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarEquipmentManageService;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarEquipmentManageEntity;
import com.jsy.community.mapper.CarEquipmentManageMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEquipMentQO;
import com.jsy.community.qo.property.CarEquipmentManageQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarEquipmentManageServiceImpl extends ServiceImpl<CarEquipmentManageMapper, CarEquipmentManageEntity> implements ICarEquipmentManageService {
    @Autowired
    private CarEquipmentManageMapper manageMapper;
    /**
     * @Description: 分页查询设备管理数据
     * @Param: [baseQO, communityId]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/8/10-11:51
     **/
    @Override
    public Map<String, Object> equipmentPage(BaseQO<CarEquipmentManageEntity> baseQO, Long communityId) {
        if (baseQO.getSize() == null || baseQO.getSize() == 0){
            baseQO.setSize(10l);
        }
        CarEquipmentManageEntity query = baseQO.getQuery();

        Long page  = baseQO.getPage() ;
        if (page == 0){
            page++;
        }
        page =(baseQO.getPage()-1)*baseQO.getSize();

        query.setCommunityId(communityId);

        List<CarEquipmentManageEntity>  carEquipment  =  manageMapper.equipmentPage(page,baseQO.getSize(),query);
        Long total =  manageMapper.findTotal(query);
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",carEquipment);
        return map;
    }


    /**
     * @Description: 添加设备管理
     * @Param: [carEquipMentQO, adminCommunityId, uid]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/10-11:52
     **/
    @Override
    public boolean addEquipment(CarEquipMentQO carEquipMentQO, Long adminCommunityId, String uid) {
        CarEquipmentManageEntity manageEntity = new CarEquipmentManageEntity();
        manageEntity.setCommunityId(adminCommunityId);
        manageEntity.setUid(uid);
        manageEntity.setId(SnowFlake.nextId());
        BeanUtils.copyProperties(carEquipMentQO,manageEntity);
        return manageMapper.insert(manageEntity) ==1 ;
    }

    /**
     * @Description: 删除设备管理
     * @Param: [id, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/10-11:52
     **/
    @Override
    public boolean deleteEquipment(Long id, Long adminCommunityId) {
        QueryWrapper<CarEquipmentManageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id)
                .eq("community_id",adminCommunityId);
        return  manageMapper.delete(queryWrapper)==1;
    }

    /**
     * @Description: 查询设备管理
     * @Param: [communityId]
     * @Return: java.util.List<com.jsy.community.entity.property.CarEquipmentManageEntity>
     * @Author: Tian
     * @Date: 2021/8/10-c
     **/
    @Override
    public List<CarEquipmentManageEntity> equipmentList(Long communityId) {
        QueryWrapper<CarEquipmentManageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id",communityId);
        List<CarEquipmentManageEntity> list = manageMapper.selectList(queryWrapper);
        return  list;
    }

    @Override
    public boolean updateEquipment(CarEquipMentQO carEquipMentQO, Long adminCommunityId, String userId) {
        System.out.println(carEquipMentQO);
        QueryWrapper<CarEquipmentManageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",carEquipMentQO.getId())
        .eq("community_id",adminCommunityId);
        CarEquipmentManageEntity manageEntity = new CarEquipmentManageEntity();
        BeanUtils.copyProperties(carEquipMentQO,manageEntity);
        manageMapper.update(manageEntity,queryWrapper);
        return false;
    }

    @Override
    public CarEquipmentManageEntity equipmentOne(String camId) {
        QueryWrapper<CarEquipmentManageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("equipment_number",camId);
        System.out.println(camId);
        CarEquipmentManageEntity carEquipmentManageEntity = manageMapper.selectOne(queryWrapper);
        System.out.println(carEquipmentManageEntity+"1111");
        return carEquipmentManageEntity;
    }


}
