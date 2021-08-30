package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.VisitorFaceSyncRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitorFaceSyncRecordEntity;
import com.jsy.community.mapper.VisitorFaceSyncRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 访客人脸同步记录表服务实现
 * @Date: 2021/8/24 10:43
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class VisitorFaceSyncRecordServiceImpl extends ServiceImpl<VisitorFaceSyncRecordMapper, VisitorFaceSyncRecordEntity> implements VisitorFaceSyncRecordService {

    @Autowired
    private VisitorFaceSyncRecordMapper visitorFaceSyncRecordMapper;
    /**
     * @param recordEntityList :
     * @author: Pipi
     * @description: 批量新增访客人脸同步记录
     * @return: java.lang.Integer
     * @date: 2021/8/24 10:48
     **/
    @Override
    public Integer batchAddRecord(List<VisitorFaceSyncRecordEntity> recordEntityList) {
        return visitorFaceSyncRecordMapper.batchInsertRecord(recordEntityList);
    }
}
