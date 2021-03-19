package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyComplaintsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PropertyComplaintsEntity;
import com.jsy.community.mapper.PropertyComplaintsMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description: 物业投诉受理
 * @author: Hu
 * @create: 2021-03-19 13:37
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyComplaintsServiceImpl extends ServiceImpl<PropertyComplaintsMapper, PropertyComplaintsEntity> implements IPropertyComplaintsService {
    @Autowired
    private PropertyComplaintsMapper propertyComplaintsMapper;
    /**
     * @Description: 分页查询物业投诉接口
     * @author: Hu
     * @since: 2021/3/19 14:05
     * @Param:
     * @return:
     */
    @Override
    public PageInfo findList(BaseQO<PropertyComplaintsQO> baseQO) {
        PageInfo<Object> pageInfo = new PageInfo<>();
        QueryWrapper<PropertyComplaintsEntity> wrapper = new QueryWrapper<>();
        PropertyComplaintsQO qoQuery = baseQO.getQuery();
        if (qoQuery.getComplainTimeOut()!=null) {
            qoQuery.setComplainTimeOut(qoQuery.getComplainTimeOut().plusDays(1));
        }
        Page page =propertyComplaintsMapper.findList(new Page<PropertyComplaintsEntity>(baseQO.getPage(),baseQO.getSize()),baseQO.getQuery());


        BeanUtils.copyProperties(page,pageInfo);
        return pageInfo;
    }
}
