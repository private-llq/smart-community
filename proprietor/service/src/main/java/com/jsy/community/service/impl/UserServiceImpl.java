package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 业主实现
 *
 * @author ling
 * @date 2020-11-11 18:12
 */
@DubboService(version = Const.version, group = Const.group)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {
}
