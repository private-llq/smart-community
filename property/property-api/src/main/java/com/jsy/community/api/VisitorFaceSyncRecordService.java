package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitorFaceSyncRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 访客人脸同步记录表服务
 * @Date: 2021/8/24 10:42
 * @Version: 1.0
 **/
public interface VisitorFaceSyncRecordService extends IService<VisitorFaceSyncRecordEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增访客人脸同步记录
     * @param recordEntityList:
     * @return: java.lang.Integer
     * @date: 2021/8/24 10:48
     **/
    Integer batchAddRecord(List<VisitorFaceSyncRecordEntity> recordEntityList);
}
