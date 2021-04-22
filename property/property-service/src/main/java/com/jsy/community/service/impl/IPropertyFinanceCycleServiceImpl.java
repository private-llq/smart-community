package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceCycleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceCycleEntity;
import com.jsy.community.mapper.PropertyFinanceCycleMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业财务结算周期表服务实现
 * @Date: 2021/4/22 9:29
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceCycleServiceImpl extends ServiceImpl<PropertyFinanceCycleMapper, PropertyFinanceCycleEntity> implements IPropertyFinanceCycleService {

    @Autowired
    private PropertyFinanceCycleMapper cycleMapper;

    /**
     *@Author: Pipi
     *@Description: 根据号数获取需要结算的社区ID列表
     *@Param: date: 号数
     *@Return: java.util.List<java.lang.Long>
     *@Date: 2021/4/22 10:01
     **/
    @Override
    public List<Long> needStatementCommunityId(Integer date) {
        return cycleMapper.queryCommunityIdByStartDate(date);
    }
}
