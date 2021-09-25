package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFaceEntity;

/**
 * @Author: Pipi
 * @Description: 物业人员人脸表服务
 * @Date: 2021/9/24 11:27
 * @Version: 1.0
 **/
public interface PropertyFaceService extends IService<PropertyFaceEntity> {

    /**
     * @author: Pipi
     * @description: 物业人脸操作(启用/禁用人脸)
     * @param propertyFaceEntity:
     * @param communityId:
     * @return: java.lang.Integer
     * @date: 2021/9/24 14:50
     **/
    Integer faceOpration(PropertyFaceEntity propertyFaceEntity, Long communityId);

    /**
     * @author: Pipi
     * @description: 删除物业人脸 
     * @param propertyFaceEntity: 
     * @param communityId: 
     * @return: java.lang.Integer
     * @date: 2021/9/24 16:22
     **/
    Integer deleteFace(PropertyFaceEntity propertyFaceEntity, Long communityId);

    /**
     * @author: Pipi
     * @description: 新增物业人脸 
     * @param propertyFaceEntity: 
     * @param communityId:
     * @return: java.lang.Integer
     * @date: 2021/9/24 16:53
     **/
    Integer addFace(PropertyFaceEntity propertyFaceEntity, Long communityId);
}
