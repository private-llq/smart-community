package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CommunityRfService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CommunityRfEntity;
import com.jsy.community.entity.property.CommunityRfSycRecordEntity;
import com.jsy.community.mapper.CommunityRfMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 门禁卡服务实现
 * @Date: 2021/11/3 16:32
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityRfServiceImpl extends ServiceImpl<CommunityRfMapper, CommunityRfEntity> implements CommunityRfService {

    /**
     * @param rfEntity :
     * @author: Pipi
     * @description: 添加门禁卡
     * @return: java.lang.Integer
     * @date: 2021/11/5 14:25
     **/
    @Override
    public Integer addRf(CommunityRfEntity rfEntity) {
        rfEntity.setId(SnowFlake.nextId());
        return null;
    }
}
