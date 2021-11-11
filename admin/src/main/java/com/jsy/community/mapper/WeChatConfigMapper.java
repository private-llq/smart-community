package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CompanyPayConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: com.jsy.community
 * @description: 微信支付配置
 * @author: DKS
 * @create: 2021-11-10 14:02
 **/
@Mapper
public interface WeChatConfigMapper extends BaseMapper<CompanyPayConfigEntity> {
}
