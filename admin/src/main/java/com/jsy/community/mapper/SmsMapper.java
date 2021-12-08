package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.SmsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 短信配置
 * @author: DKS
 * @since: 2021/12/6 11:20
 */
@Mapper
public interface SmsMapper extends BaseMapper<SmsEntity> {

}
