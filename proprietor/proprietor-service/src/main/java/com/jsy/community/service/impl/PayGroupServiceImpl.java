package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayGroupEntity;
import com.jsy.community.mapper.PayGroupMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 户号组 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class PayGroupServiceImpl extends ServiceImpl<PayGroupMapper, PayGroupEntity> implements IPayGroupService {

}
