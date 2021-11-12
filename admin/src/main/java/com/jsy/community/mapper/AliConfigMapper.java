package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PayConfigureEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: com.jsy.community
 * @description: 支付宝支付配置
 * @author: DKS
 * @create: 2021-11-10 14:05
 **/
@Mapper
public interface AliConfigMapper extends BaseMapper<PayConfigureEntity> {
}
