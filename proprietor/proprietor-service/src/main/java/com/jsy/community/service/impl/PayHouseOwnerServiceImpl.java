package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayHouseOwnerService;
import com.jsy.community.entity.PayHouseOwnerEntity;
import com.jsy.community.mapper.PayHouseOwnerMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 缴费户号 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@Service
public class PayHouseOwnerServiceImpl extends ServiceImpl<PayHouseOwnerMapper, PayHouseOwnerEntity> implements IPayHouseOwnerService {

}
