package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseFavoriteService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.mapper.HouseFavoriteMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author YuLF
 * @since 2020-12-29 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class IHouseFavoriteServiceImpl extends ServiceImpl<HouseFavoriteMapper, HouseFavoriteEntity> implements IHouseFavoriteService {

    @Resource
    private HouseFavoriteMapper houseFavoriteMapper;




}
