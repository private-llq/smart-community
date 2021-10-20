package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.SysOpLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author DKS
 * @description 大后台用户操作日志
 * @since 2021/10/20  10:57
 **/
@Mapper
public interface SysOpLogMapper extends BaseMapper<SysOpLogEntity> {

}
