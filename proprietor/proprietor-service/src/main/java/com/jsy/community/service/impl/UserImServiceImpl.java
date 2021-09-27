package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserImService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserIMEntity;
import com.jsy.community.mapper.UserIMMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * @program: com.jsy.community
 * @description: im
 * @author: Hu
 * @create: 2021-09-25 15:29
 **/
@DubboService(version = Const.version, group = Const.group)
public class UserImServiceImpl extends ServiceImpl<UserIMMapper, UserIMEntity> implements IUserImService {

    @Autowired
    private UserIMMapper userIMMapper;

    @Override
    public List<UserIMEntity> selectUidAll(Set<String> uidAll) {
        return userIMMapper.selectList(new QueryWrapper<UserIMEntity>().in("uid",uidAll));
    }

    @Override
    public UserIMEntity selectUid(String uid) {
        return userIMMapper.selectOne(new QueryWrapper<UserIMEntity>().eq("uid",uid));
    }
}
