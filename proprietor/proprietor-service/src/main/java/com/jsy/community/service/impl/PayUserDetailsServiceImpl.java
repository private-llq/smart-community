package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayUserDetailsService;
import com.jsy.community.entity.PayUserDetailsEntity;
import com.jsy.community.mapper.PayUserDetailsMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 户主详情表 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@Service
public class PayUserDetailsServiceImpl extends ServiceImpl<PayUserDetailsMapper, PayUserDetailsEntity> implements IPayUserDetailsService {

}
