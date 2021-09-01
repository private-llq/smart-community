package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.LeaseOperationRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.mapper.LeaseOperationRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 租赁操作记录表服务实现
 * @Date: 2021/8/31 14:54
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class LeaseOperationRecordServiceImpl extends ServiceImpl<LeaseOperationRecordMapper, LeaseOperationRecordEntity> implements LeaseOperationRecordService {
}
