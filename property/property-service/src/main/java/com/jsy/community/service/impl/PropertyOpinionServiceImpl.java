package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyOpinionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PropertyOpinionEntity;
import com.jsy.community.mapper.PropertyOpinionMapper;
import com.jsy.community.vo.admin.AdminInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-11 11:15
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyOpinionServiceImpl extends ServiceImpl<PropertyOpinionMapper, PropertyOpinionEntity> implements IPropertyOpinionService {
    @Autowired
    private PropertyOpinionMapper propertyOpinionMapper;

    @Override
    public Integer selectCount(AdminInfoVo userInfo) {
        List<PropertyOpinionEntity> list = propertyOpinionMapper.selectList(new QueryWrapper<PropertyOpinionEntity>()
                .eq("uid", userInfo.getUid())
                .ge("create_time", LocalDate.now())
                .le("create_time", LocalDate.now().plusDays(1)));
        if (list!=null){

            return list.size();
        }
        return 0;
    }

    @Override
    public void insetOne(PropertyOpinionEntity propertyOpinionEntity, AdminInfoVo userInfo) {
        propertyOpinionEntity.setUid(userInfo.getUid());
        propertyOpinionEntity.setCommunityId(userInfo.getCommunityId());
        propertyOpinionMapper.insert(propertyOpinionEntity);
    }
}
