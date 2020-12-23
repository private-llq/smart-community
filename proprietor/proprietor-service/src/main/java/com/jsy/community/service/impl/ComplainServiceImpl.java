package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IComplainService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.mapper.ComplainMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 11:16
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, ComplainEntity> implements IComplainService {

    @Autowired
    private ComplainMapper complainMapper;
    /**
     * @Description: 查询用户所有的投诉建议
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    @Override
    public List<ComplainEntity> selectUserIdComplain(String userId) {
        return complainMapper.selectList(new QueryWrapper<ComplainEntity>().eq("uid",userId));
    }
}
