package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserFaceSyncRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 用户人脸同步记录表Mapper
 * @Date: 2021/8/19 17:30
 * @Version: 1.0
 **/
public interface UserFaceSyncRecordMapper extends BaseMapper<UserFaceSyncRecordEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增用户人脸同步记录
     * @param recordEntities:
     * @return: java.lang.Integer
     * @date: 2021/8/20 16:03
     **/
    Integer insertBatchRecord(List<UserFaceSyncRecordEntity> recordEntities);
}
