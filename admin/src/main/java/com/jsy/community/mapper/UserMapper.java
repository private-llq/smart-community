package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ling
 * @since 2020-11-19 16:50
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
