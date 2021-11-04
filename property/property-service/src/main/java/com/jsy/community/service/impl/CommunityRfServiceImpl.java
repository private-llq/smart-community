package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CommunityRfService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CommunityRfEntity;
import com.jsy.community.mapper.CommunityRfMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 门禁卡服务实现
 * @Date: 2021/11/3 16:32
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityRfServiceImpl extends ServiceImpl<CommunityRfMapper, CommunityRfEntity> implements CommunityRfService {
}
