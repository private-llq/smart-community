package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFaceSyncRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业人脸同步记录表Mapper
 * @Date: 2021/9/24 11:38
 * @Version: 1.0
 **/
public interface PropertyFaceSyncRecordMapper extends BaseMapper<PropertyFaceSyncRecordEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增同步记录
     * @param recordEntities:
     * @return: java.lang.Integer
     * @date: 2021/9/24 11:52
     **/
    Integer batchInsertSyncRecord(List<PropertyFaceSyncRecordEntity> recordEntities);
}
