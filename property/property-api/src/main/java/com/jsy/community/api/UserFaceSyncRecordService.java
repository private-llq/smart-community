package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserFaceSyncRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 用户人脸同步记录表服务
 * @Date: 2021/8/19 17:31
 * @Version: 1.0
 **/
public interface UserFaceSyncRecordService extends IService<UserFaceSyncRecordEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增用户人脸同步记录
     * @param recordEntities:
     * @return: void
     * @date: 2021/8/20 16:01
     **/
    void batchInsertSyncRecord(List<UserFaceSyncRecordEntity> recordEntities);
}
