package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.mapper.RepairOrderMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 报修订单信息 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-08
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrderEntity> implements IRepairOrderService {

}
