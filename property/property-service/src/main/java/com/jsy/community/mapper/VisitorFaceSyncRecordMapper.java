package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitorFaceSyncRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 访客人脸同步记录表Mapper
 * @Date: 2021/8/24 10:25
 * @Version: 1.0
 **/
public interface VisitorFaceSyncRecordMapper extends BaseMapper<VisitorFaceSyncRecordEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增访客人脸同步记录
     * @param recordEntities:
     * @return: java.lang.Integer
     * @date: 2021/8/24 10:35
     **/
    Integer batchInsertRecord(List<VisitorFaceSyncRecordEntity> recordEntities);
}
