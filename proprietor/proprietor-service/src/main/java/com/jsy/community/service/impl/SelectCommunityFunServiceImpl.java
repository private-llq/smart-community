package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ISelectCommunityFunService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.mapper.SelectCommunityFunMapper;
import com.jsy.community.qo.proprietor.SelectCommunityFunQO;
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
    /**
     * @Description: 分页查询所有趣事
     * @author: Hu
     * @since: 2021/2/23 17:29
     * @Param:
     * @return:
     */
    @Override
    public Map<String,Object> findList(SelectCommunityFunQO communityFunQO) {
        Map<String,Object> map = new HashMap<>();
        if (communityFunQO.getSize()==0||communityFunQO.getSize()==null){
            communityFunQO.setSize(10L);
        }
        QueryWrapper<CommunityFunEntity> wrapper = new QueryWrapper<CommunityFunEntity>().eq("status",1);
        if (communityFunQO.getHeadline()!=null&&!"".equals(communityFunQO.getHeadline())) {
            wrapper.like("title_name", communityFunQO.getHeadline());
        }
        wrapper.orderByDesc("start_time");

        IPage<CommunityFunEntity> page = selectCommunityFunMapper.selectPage(new Page<CommunityFunEntity>(communityFunQO.getPage(), communityFunQO.getSize()),wrapper);
        List<CommunityFunEntity> records = page.getRecords();
        long total = page.getTotal();
        map.put("list",records);
        map.put("total",total);
        return map;
    }

    /**
     * @Description: 查询一条趣事详情
     * @author: Hu
     * @since: 2021/2/23 17:29
     * @Param:
     * @return:
     */
    @Override
    public CommunityFunEntity findFunOne(Long id) {
        return selectCommunityFunMapper.selectById(id);

    }

    /**
     * @Description: 浏览量
     * @author: Hu
     * @since: 2021/2/23 17:28
     * @Param:
     * @return:
     */
    @Override
    public void saveViewCount(Long id) {
        CommunityFunEntity communityFunEntity = selectCommunityFunMapper.selectById(id);
            communityFunEntity.setViewCount(communityFunEntity.getViewCount()+1);
        selectCommunityFunMapper.updateById(communityFunEntity);
    }
}
