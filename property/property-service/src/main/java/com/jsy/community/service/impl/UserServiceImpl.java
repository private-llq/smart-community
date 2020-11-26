package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserService;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业主 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

}
