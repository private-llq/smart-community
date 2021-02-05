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

import java.util.*;

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
        if (communityFunQO.getSize()==0||communityFunQO.getSize()==null){
            communityFunQO.setSize(10L);
        }
        QueryWrapper<CommunityFunEntity> wrapper = new QueryWrapper<CommunityFunEntity>().eq("status",1);
        if (communityFunQO.getHeadline()!=null&&!"".equals(communityFunQO.getHeadline())) {
            wrapper.like("title_name", communityFunQO.getHeadline());
        }
        wrapper.orderByDesc("start_time");

        IPage<CommunityFunEntity> page = selectCommunityFunMapper.selectPage(new Page<CommunityFunEntity>(communityFunQO.getPage(), communityFunQO.getSize()),wrapper);
//        List<CommunityFunEntity> list = page.getRecords();
//        list.sort(Comparator.comparing(CommunityFunEntity::getCreateTime));
//        Collections.sort(page.getRecords(),new Comparator<CommunityFunEntity>() {
//            @Override
//            public int compare(CommunityFunEntity o1, CommunityFunEntity o2) {
//                if(o1.getCreateTime() != null && o2.getCreateTime() != null){
//                    if(o1.getCreateTime().isAfter(o2.getCreateTime())) {
//                        return -1;
//                    }
//                    return 1;
//                }
//                return 0;
//            }
//        });



        long total = page.getTotal();
        map.put("list",page);
        map.put("total",total);
        return map;
    }

    @Override
    public CommunityFunEntity findFunOne(Long id) {
        return selectCommunityFunMapper.selectById(id);

    }

    @Override
    public void saveViewCount(Long id) {
        CommunityFunEntity communityFunEntity = selectCommunityFunMapper.selectById(id);
        if (communityFunEntity.getViewCount()!=null){
            communityFunEntity.setViewCount(communityFunEntity.getViewCount()+1);
        }else {
            communityFunEntity.setViewCount(1);
        }
        selectCommunityFunMapper.updateById(communityFunEntity);
    }
}
