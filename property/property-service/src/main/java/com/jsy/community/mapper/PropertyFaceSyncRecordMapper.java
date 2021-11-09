package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFaceSyncRecordEntity;
import org.apache.ibatis.annotations.Param;

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

    /**
     * @author: Pipi
     * @description: 查询物业人脸信息
     * @param mobile: 电话号码
     * @param communityId: 社区ID
     * @return: com.jsy.community.entity.property.PropertyFaceSyncRecordEntity
     * @date: 2021/11/8 14:36
     **/
    PropertyFaceSyncRecordEntity queryByMobile(@Param("mobile") String mobile, @Param("communityId") Long communityId);
}
