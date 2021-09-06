package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.mapper.CarOrderMapper;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = Const.version, group = Const.group_property)
public class CarOrderServiceImpl extends ServiceImpl<CarOrderMapper, CarOrderEntity> implements ICarOrderService {
}
