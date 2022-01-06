package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.mapper.ComplainMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ComplainQO;
import com.jsy.community.service.IComplainService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * @program: com.jsy.community
 * @description:  物业意见反馈
 * @author: DKS
 * @create: 2021-10-27 15:58
 **/
@Service
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, ComplainEntity> implements IComplainService {
    
    @Resource
    private ComplainMapper complainMapper;
    
    /**
     * @Description: 意见反馈条件查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.ComplainEntity>
     * @Author: DKS
     * @Date: 2021/10/27
     **/
    @Override
    public PageInfo<ComplainEntity> queryComplain(BaseQO<ComplainQO> baseQO) {
        ComplainQO query = baseQO.getQuery();
        Page<ComplainEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<ComplainEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("source", query.getSource());
        //是否查类型
        if (query.getType() != null) {
            queryWrapper.eq("type", query.getType());
        }
        queryWrapper.orderByDesc("create_time");
        Page<ComplainEntity> pageData = complainMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        for (ComplainEntity complainEntity : pageData.getRecords()) {
            // 补充类型名称
            complainEntity.setTypeName(complainEntity.getType() == 1 ? "投诉" : "建议");
            complainEntity.setIdStr(String.valueOf(complainEntity.getId()));
        }
        PageInfo<ComplainEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     * @Description: 意见反馈详情查询
     * @author: DKS
     * @since: 2021/10/27 16:00
     * @Param: java.util.Long
     * @return: com.jsy.community.entity.ComplainEntity
     */
    @Override
    public ComplainEntity getDetailComplain(Long id) {
        ComplainEntity complainEntity = complainMapper.selectById(id);
        complainEntity.setTypeName(complainEntity.getType() == 1 ? "投诉" : "建议");
        return complainEntity;
    }
    
    /**
     * @Description: 意见反馈删除
     * @author: DKS
     * @since: 2021/10/27 16:10
     * @Param: id
     * @return: boolean
     */
    @Override
    public boolean deleteComplain(Long id) {
        int row = complainMapper.deleteById(id);
        return row == 1;
    }
}
