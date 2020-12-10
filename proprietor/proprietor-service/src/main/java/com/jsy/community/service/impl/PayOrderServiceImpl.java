package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayOrderEntity;
import com.jsy.community.mapper.PayOrderMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrderEntity> implements IPayOrderService {

}
