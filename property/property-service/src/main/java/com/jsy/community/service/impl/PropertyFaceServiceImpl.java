package com.jsy.community.service.impl;
import java.time.LocalDateTime;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PropertyException;
import com.jsy.community.api.PropertyFaceService;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.face.xu.XUFaceEditPersonDTO;
import com.jsy.community.entity.CommunityHardWareEntity;
import com.jsy.community.entity.UserFaceSyncRecordEntity;
import com.jsy.community.entity.property.PropertyFaceEntity;
import com.jsy.community.entity.property.PropertyFaceSyncRecordEntity;
import com.jsy.community.mapper.CommunityHardWareMapper;
import com.jsy.community.mapper.PropertyFaceMapper;
import com.jsy.community.mapper.PropertyFaceSyncRecordMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Pipi
 * @Description: 物业人员人脸表服务实现
 * @Date: 2021/9/24 11:27
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyFaceServiceImpl extends ServiceImpl<PropertyFaceMapper, PropertyFaceEntity> implements PropertyFaceService {

    @Autowired
    private PropertyFaceMapper propertyFaceMapper;

    @Autowired
    private CommunityHardWareMapper hardWareMapper;

    @Autowired
    private PropertyFaceSyncRecordMapper syncRecordMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param propertyFaceEntity :
     * @param communityId        :
     * @author: Pipi
     * @description: 物业人脸操作(启用 / 禁用人脸)
     * @return: java.lang.Integer
     * @date: 2021/9/24 14:50
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer faceOpration(PropertyFaceEntity propertyFaceEntity, Long communityId) {
        // 查询物业人脸信息
        QueryWrapper<PropertyFaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", propertyFaceEntity.getId());
        queryWrapper.eq("face_deleted", 0);
        queryWrapper.eq("community_id", communityId);
        PropertyFaceEntity faceEntity = propertyFaceMapper.selectOne(queryWrapper);
        if (faceEntity == null) {
            throw new PropertyException("没有找到物业人脸信息");
        }
        if (faceEntity.getFaceEnableStatus() == propertyFaceEntity.getFaceEnableStatus()) {
            throw new PropertyException("人脸已经启用/禁用,请勿重复操作");
        }
        // 查询设备
        List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
        Set<String> hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
        // 删除原有的同步记录
        QueryWrapper<PropertyFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();
        recordEntityQueryWrapper.eq("porperty_face_id", propertyFaceEntity.getId());
        recordEntityQueryWrapper.eq("community_id", communityId);
        syncRecordMapper.delete(recordEntityQueryWrapper);
        // 设置人脸启用状态
        faceEntity.setFaceEnableStatus(propertyFaceEntity.getFaceEnableStatus());
        // 更新人脸启用状态
        int updateById = propertyFaceMapper.updateById(faceEntity);
        if (updateById == 1) {
            XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
            if (propertyFaceEntity.getFaceEnableStatus() == 1) {
                // 启用操作
                xuFaceEditPersonDTO.setOperator("editPerson");
                xuFaceEditPersonDTO.setName(faceEntity.getRealName());
                xuFaceEditPersonDTO.setPersonType(0);
                xuFaceEditPersonDTO.setTempCardType(0);
                xuFaceEditPersonDTO.setPicURI(faceEntity.getFaceUrl());
                // 新增同步记录
                List<PropertyFaceSyncRecordEntity> faceSyncRecordEntities = new ArrayList<>();
                for (String hardwareId : hardwareIds) {
                    PropertyFaceSyncRecordEntity propertyFaceSyncRecordEntity = new PropertyFaceSyncRecordEntity();
                    propertyFaceSyncRecordEntity.setPorpertyFaceId(faceEntity.getId());
                    propertyFaceSyncRecordEntity.setCommunityId(communityId);
                    propertyFaceSyncRecordEntity.setFaceUrl(faceEntity.getFaceUrl());
                    propertyFaceSyncRecordEntity.setFacilityId(hardwareId);
                    propertyFaceSyncRecordEntity.setId(SnowFlake.nextId());
                    propertyFaceSyncRecordEntity.setDeleted(0);
                    propertyFaceSyncRecordEntity.setCreateTime(LocalDateTime.now());
                    faceSyncRecordEntities.add(propertyFaceSyncRecordEntity);
                }
                syncRecordMapper.batchInsertSyncRecord(faceSyncRecordEntities);
            } else {
                // 禁用操作
                xuFaceEditPersonDTO.setOperator("DelPerson");
            }
            xuFaceEditPersonDTO.setCustomId(faceEntity.getMobile());
            xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
            xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
            rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU, TopicExConfig.TOPIC_FACE_XU_SERVER, JSON.toJSONString(xuFaceEditPersonDTO));
        }
        return updateById;
    }

    /**
     * @param propertyFaceEntity :
     * @param communityId        :
     * @author: Pipi
     * @description: 删除物业人脸
     * @return: java.lang.Integer
     * @date: 2021/9/24 16:15
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteFace(PropertyFaceEntity propertyFaceEntity, Long communityId) {
        // 查询物业人脸信息
        QueryWrapper<PropertyFaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", propertyFaceEntity.getId());
        queryWrapper.eq("face_deleted", 0);
        PropertyFaceEntity faceEntity = propertyFaceMapper.selectOne(queryWrapper);
        if (faceEntity == null) {
            throw new PropertyException("没有找到物业人脸信息或已删除");
        }
        // 设置人脸删除状态
        faceEntity.setFaceDeleted(1);
        // 更新人脸删除状态
        int updateById = propertyFaceMapper.updateById(faceEntity);
        // 查询设备
        List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
        Set<String> hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
        // 删除原有的同步记录
        QueryWrapper<PropertyFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();
        recordEntityQueryWrapper.eq("porperty_face_id", propertyFaceEntity.getId());
        recordEntityQueryWrapper.eq("community_id", communityId);
        syncRecordMapper.delete(recordEntityQueryWrapper);
        if (!CollectionUtils.isEmpty(communityHardWareEntities) && updateById == 1) {
            // 删除小区设备的人脸照片
            XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
            xuFaceEditPersonDTO.setOperator("DelPerson");
            xuFaceEditPersonDTO.setCustomId(faceEntity.getMobile());
            xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
            xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
            rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU, TopicExConfig.TOPIC_FACE_XU_SERVER, JSON.toJSONString(xuFaceEditPersonDTO));
        }
        return updateById;
    }

    /**
     * @param propertyFaceEntity :
     * @param communityId   :
     * @author: Pipi
     * @description: 新增物业人脸
     * @return: java.lang.Integer
     * @date: 2021/9/24 16:53
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addFace(PropertyFaceEntity propertyFaceEntity, Long communityId) {
        // 查询物业人脸信息
        QueryWrapper<PropertyFaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", communityId);
        queryWrapper.eq("real_name", propertyFaceEntity.getRealName());
        queryWrapper.eq("mobile", propertyFaceEntity.getMobile());
        PropertyFaceEntity faceEntity = propertyFaceMapper.selectOne(queryWrapper);
        Integer result = 0;
        if (faceEntity == null) {
            // 新增
            faceEntity = new PropertyFaceEntity();
            faceEntity.setCommunityId(communityId);
            faceEntity.setRealName(propertyFaceEntity.getRealName());
            faceEntity.setMobile(propertyFaceEntity.getMobile());
            faceEntity.setFaceUrl(propertyFaceEntity.getFaceUrl());
            faceEntity.setFaceEnableStatus(propertyFaceEntity.getFaceEnableStatus());
            faceEntity.setFaceDeleted(0);
            faceEntity.setDeleted(0);
            faceEntity.setId(SnowFlake.nextId());
            faceEntity.setCreateTime(LocalDateTime.now());
            result = propertyFaceMapper.insert(faceEntity);
        } else {
            // 更新
            faceEntity.setFaceUrl(propertyFaceEntity.getFaceUrl());
            faceEntity.setFaceEnableStatus(propertyFaceEntity.getFaceEnableStatus());
            faceEntity.setFaceDeleted(0);
            faceEntity.setDeleted(0);
            result = propertyFaceMapper.updateById(faceEntity);
            // 删除原有的同步记录
            QueryWrapper<PropertyFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();
            recordEntityQueryWrapper.eq("porperty_face_id", propertyFaceEntity.getId());
            recordEntityQueryWrapper.eq("community_id", communityId);
            syncRecordMapper.delete(recordEntityQueryWrapper);
        }
        // 查询设备
        if (propertyFaceEntity.getFaceEnableStatus() == 1) {
            List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
            if (result == 1 && !CollectionUtils.isEmpty(communityHardWareEntities)) {
                Set<String> hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
                // 新增同步记录
                List<PropertyFaceSyncRecordEntity> faceSyncRecordEntities = new ArrayList<>();
                for (String hardwareId : hardwareIds) {
                    PropertyFaceSyncRecordEntity propertyFaceSyncRecordEntity = new PropertyFaceSyncRecordEntity();
                    propertyFaceSyncRecordEntity.setPorpertyFaceId(faceEntity.getId());
                    propertyFaceSyncRecordEntity.setCommunityId(communityId);
                    propertyFaceSyncRecordEntity.setFaceUrl(faceEntity.getFaceUrl());
                    propertyFaceSyncRecordEntity.setFacilityId(hardwareId);
                    propertyFaceSyncRecordEntity.setId(SnowFlake.nextId());
                    propertyFaceSyncRecordEntity.setDeleted(0);
                    propertyFaceSyncRecordEntity.setCreateTime(LocalDateTime.now());
                    faceSyncRecordEntities.add(propertyFaceSyncRecordEntity);
                }
                syncRecordMapper.batchInsertSyncRecord(faceSyncRecordEntities);
                // 启用人脸
                XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
                xuFaceEditPersonDTO.setOperator("editPerson");
                xuFaceEditPersonDTO.setName(faceEntity.getRealName());
                xuFaceEditPersonDTO.setPersonType(0);
                xuFaceEditPersonDTO.setTempCardType(0);
                xuFaceEditPersonDTO.setPicURI(faceEntity.getFaceUrl());
                xuFaceEditPersonDTO.setCustomId(faceEntity.getMobile());
                xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
                xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
                rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU, TopicExConfig.TOPIC_FACE_XU_SERVER, JSON.toJSONString(xuFaceEditPersonDTO));
            }
        }
        return result;
    }
}
