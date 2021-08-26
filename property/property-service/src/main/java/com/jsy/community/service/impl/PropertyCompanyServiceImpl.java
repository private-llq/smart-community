package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyCompanyService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.mapper.PropertyCompanyMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description: 物业公司
 * @author: Hu
 * @create: 2021-08-20 15:06
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyCompanyServiceImpl extends ServiceImpl<PropertyCompanyMapper, PropertyCompanyEntity> implements IPropertyCompanyService {
    @Autowired
    private PropertyCompanyMapper propertyCompanyMapper;


    /**
     * @Description: 查询当前小区的物业公司
     * @author: Hu
     * @since: 2021/8/20 15:09
     * @Param: [id]
     * @return: com.jsy.community.entity.PropertyCompanyEntity
     */
    @Override
    public PropertyCompanyEntity findOne(Long id) {
        return propertyCompanyMapper.selectOne(new QueryWrapper<PropertyCompanyEntity>().select("id,\n" +
                "\t`name`,\n" +
                "\t`describe`,\n" +
                "\tpicture,\n" +
                "\tdeleted,\n" +
                "\tcreate_time,\n" +
                "\tupdate_time ").eq("id",id));
    }
}
