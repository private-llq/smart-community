package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.UserFaceSyncRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserFaceSyncRecordEntity;
import com.jsy.community.mapper.UserFaceSyncRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 用户人脸同步记录表服务实现
 * @Date: 2021/8/19 17:32
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class UserFaceSyncRecordServiceImpl extends ServiceImpl<UserFaceSyncRecordMapper, UserFaceSyncRecordEntity> implements UserFaceSyncRecordService {
    @Autowired
    private UserFaceSyncRecordMapper userFaceSyncRecordMapper;

    /**
     * @param recordEntities :
     * @author: Pipi
     * @description: 批量新增用户人脸同步记录
     * @return: void
     * @date: 2021/8/20 16:01
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertSyncRecord(List<UserFaceSyncRecordEntity> recordEntities) {
        userFaceSyncRecordMapper.insertBatchRecord(recordEntities);
    }
}
