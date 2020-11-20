package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * @author ling
 * @since 2020-11-19 16:50
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {
}
