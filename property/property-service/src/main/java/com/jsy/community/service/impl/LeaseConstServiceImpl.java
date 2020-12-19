package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ILeaseConstService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.mapper.LeaseConstMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;


@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class LeaseConstServiceImpl extends ServiceImpl<LeaseConstMapper, HouseLeaseConstEntity> implements ILeaseConstService {
	

}