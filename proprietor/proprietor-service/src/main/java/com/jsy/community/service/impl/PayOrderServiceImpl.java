package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayOrderService;
import com.jsy.community.entity.PayOrderEntity;
import com.jsy.community.mapper.PayOrderMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrderEntity> implements IPayOrderService {

}
