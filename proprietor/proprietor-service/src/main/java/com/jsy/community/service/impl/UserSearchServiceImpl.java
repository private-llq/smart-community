package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserSearchService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserSearchEntity;
import com.jsy.community.mapper.UserSearchMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-16 16:50
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserSearchServiceImpl extends ServiceImpl<UserSearchMapper, UserSearchEntity> implements IUserSearchService {
    @Autowired
    private UserSearchMapper userSearchMapper;

    @Override
    public void addSearchHotKey(String userId, String text) {
        UserSearchEntity entity = userSearchMapper.selectOne(new QueryWrapper<UserSearchEntity>()
                .eq("uid", userId)
        );
        if (entity==null){
            UserSearchEntity searchEntity = new UserSearchEntity();
            searchEntity.setId(SnowFlake.nextId());
            searchEntity.setUid(userId);
            searchEntity.setSearchRecord(text);
            userSearchMapper.insert(searchEntity);
        }else {
            entity.setSearchRecord(entity.getSearchRecord()+","+text);
            userSearchMapper.updateById(entity);
        }
    }

    @Override
    public String[] searchUserKey(String userId, Integer num) {
        UserSearchEntity entity = userSearchMapper.selectOne(new QueryWrapper<UserSearchEntity>().eq("uid", userId));
        if (entity==null){
            return new String[0];
        }
        String[] split = entity.getSearchRecord().split(",");

        if (split.length<=num){
            return split;
        }else {
            return Arrays.copyOf(split, num);
        }

    }

}
