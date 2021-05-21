package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceStatementRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;
import com.jsy.community.entity.property.PropertyFinanceStatementRecordEntity;
import com.jsy.community.mapper.PropertyFinanceStatementMapper;
import com.jsy.community.mapper.PropertyFinanceStatementRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业财务-结算操作记录服务实现
 * @Date: 2021/4/24 10:39
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceStatementRecordServiceImpl extends ServiceImpl<PropertyFinanceStatementRecordMapper, PropertyFinanceStatementRecordEntity> implements IPropertyFinanceStatementRecordService {

    @Autowired
    private PropertyFinanceStatementMapper statementMapper;

    /**
     *@Author: Pipi
     *@Description: 根据结算单号查询操作记录列表
     *@Param: statementNum:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceStatementRecordEntity>
     *@Date: 2021/4/24 10:50
     **/
    @Override
    public PropertyFinanceStatementEntity statementRecordList(String statementNum) {
        QueryWrapper<PropertyFinanceStatementEntity> statementEntityQueryWrapper = new QueryWrapper<>();
        statementEntityQueryWrapper.eq("statement_num", statementNum);
        PropertyFinanceStatementEntity statementEntity = statementMapper.selectOne(statementEntityQueryWrapper);
        if (statementEntity == null) {
            return statementEntity;
        }
        QueryWrapper<PropertyFinanceStatementRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("statement_num", statementNum);
        queryWrapper.last("order by create_time asc");
        List<PropertyFinanceStatementRecordEntity> recordEntities = baseMapper.selectList(queryWrapper);
        statementEntity.setRecordEntities(recordEntities);
        return statementEntity;
    }
}
