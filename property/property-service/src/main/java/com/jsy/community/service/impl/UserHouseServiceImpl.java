package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.UserHouseMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业主房屋认证 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
@Service
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouseEntity> implements IUserHouseService {

}
