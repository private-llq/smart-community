package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayFamilyService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayFamilyEntity;
import com.jsy.community.mapper.PayFamilyMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 缴费户号 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class PayFamilyServiceImpl extends ServiceImpl<PayFamilyMapper, PayFamilyEntity> implements IPayFamilyService {

}
