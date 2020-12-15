package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ISelectCommunityFunService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.mapper.SelectCommunityFunMapper;
import com.jsy.community.qo.CommunityFunQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 业主端的社区趣事查询接口
 * @author: Hu
 * @create: 2020-12-09 17:07
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class SelectCommunityFunServiceImpl extends ServiceImpl<SelectCommunityFunMapper, CommunityFunEntity> implements ISelectCommunityFunService {
    @Autowired
    private SelectCommunityFunMapper selectCommunityFunMapper;
    @Override
    public Map<String,Object> findList(CommunityFunQO communityFunQO) {
        Map<String,Object> map = new HashMap<>();
        if (communityFunQO.getSize()==0||communityFunQO.getSize()==null)
            communityFunQO.setSize(10l);
        QueryWrapper<CommunityFunEntity> wrapper = new QueryWrapper<CommunityFunEntity>().eq("status",2);
        if (communityFunQO.getHeadline()!=null&&!"".equals(communityFunQO.getHeadline())) {
            wrapper.like("title", communityFunQO.getHeadline());
        }
        IPage<CommunityFunEntity> page = selectCommunityFunMapper.selectPage(new Page<CommunityFunEntity>(communityFunQO.getPage(), communityFunQO.getSize()),wrapper);
        List<CommunityFunEntity> list = page.getRecords();

        long total = page.getTotal();
        map.put("list",list);
        map.put("total",total);
        return map;
    }
}
