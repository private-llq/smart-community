package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyCarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.mapper.PropertyCarMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CommunityFunQO;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:54
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyCarServiceImpl extends ServiceImpl<PropertyCarMapper, CarEntity> implements IPropertyCarService {
    @Autowired
    private PropertyCarMapper propertyCarMapper;

    @Override
    public PageInfo findList(BaseQO<CommunityFunQO> baseQO) {


        return null;
    }
}
