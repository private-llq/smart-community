package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityFunService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.mapper.CommunityFunMapper;
import com.jsy.community.qo.CommunityFunQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 社区趣事
 * @author: Hu
 * @create: 2020-12-09 10:51
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityFunServiceImpl extends ServiceImpl<CommunityFunMapper, CommunityFunEntity> implements ICommunityFunService {

    @Autowired
    private CommunityFunMapper communityFunMapper;

    @Override
    public Map<String,Object> findList(CommunityFunQO communityFunQO) {
        Map<String,Object> map = new HashMap<>();
        if (communityFunQO.getSize()==0||communityFunQO.getSize()==null)
            communityFunQO.setSize(10l);
        QueryWrapper<CommunityFunEntity> wrapper = new QueryWrapper<CommunityFunEntity>();
        if (communityFunQO.getHeadline()!=null&&!"".equals(communityFunQO.getHeadline())) {
            wrapper.like("description", communityFunQO.getHeadline());
        }
        IPage<CommunityFunEntity>  page = communityFunMapper.selectPage(new Page<CommunityFunEntity>(communityFunQO.getPage(), communityFunQO.getSize()),wrapper);
        List<CommunityFunEntity> list = page.getRecords();

        long total = page.getTotal();
        map.put("list",list);
        map.put("total",total);
        return map;
    }

}
