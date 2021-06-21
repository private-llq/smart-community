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
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate redisTemplate;

    private final String COMMUNITY_FUN_COUNT="community_fun_count:";
    private final String LOCK="community_fun_lock";


    /**
     * @Description: 分页查询所有趣事
     * @author: Hu
     * @since: 2021/5/21 13:51
     * @Param: [communityFunQO]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
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
//        wrapper.eq("community_id",communityFunQO.getCommunityId());
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
     * @since: 2021/5/21 13:51
     * @Param: [id]
     * @return: com.jsy.community.entity.CommunityFunEntity
     */
    @Override
    public CommunityFunEntity findFunOne(Long id) {
        return selectCommunityFunMapper.selectOne(new QueryWrapper<CommunityFunEntity>().eq("id",id).eq("status",1));

    }



    /**
     * @Description: 浏览量
     * @author: Hu
     * @since: 2021/5/21 13:51
     * @Param: [id]
     * @return: void
     */
    @Override
    public Integer saveViewCount(Long id) {
        Object count = redisTemplate.opsForValue().get(COMMUNITY_FUN_COUNT + id);
        if (count!=null){
            int anInt = Integer.parseInt(String.valueOf(count))+1;
            redisTemplate.opsForValue().set(COMMUNITY_FUN_COUNT + id,String.valueOf(anInt));
            return anInt;
        }
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(LOCK, "111",10, TimeUnit.SECONDS);
        if (lock){
            CommunityFunEntity communityFunEntity = selectCommunityFunMapper.selectById(id);
            communityFunEntity.setViewCount(communityFunEntity.getViewCount()+1);
            redisTemplate.opsForValue().set(COMMUNITY_FUN_COUNT + id,String.valueOf(communityFunEntity.getViewCount()));
            redisTemplate.delete(LOCK);
            return communityFunEntity.getViewCount();
        }else {
            saveViewCount(id);
        }
        return 1;
    }
}
