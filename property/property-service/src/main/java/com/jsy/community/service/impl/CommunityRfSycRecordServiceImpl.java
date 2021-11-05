package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CommunityRfSycRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CommunityRfSycRecordEntity;
import com.jsy.community.mapper.CommunityRfSycRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 门禁卡同步记录服务实现
 * @Date: 2021/11/3 16:34
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityRfSycRecordServiceImpl extends ServiceImpl<CommunityRfSycRecordMapper, CommunityRfSycRecordEntity> implements CommunityRfSycRecordService {
}
