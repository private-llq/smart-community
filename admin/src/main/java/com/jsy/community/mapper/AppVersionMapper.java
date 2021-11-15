package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.AppVersionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author DKS
 * @description APP版本Mapper
 * @since 2021-11-11 15:22
 **/
@Mapper
public interface AppVersionMapper extends BaseMapper<AppVersionEntity> {
}
