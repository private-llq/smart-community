package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVoteService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.entity.proprietor.VoteOptionEntity;
import com.jsy.community.mapper.VoteMapper;
import com.jsy.community.mapper.VoteOptionMapper;
import com.jsy.community.mapper.VoteTopicMapper;
import com.jsy.community.mapper.VoteUserMapper;
import com.jsy.community.qo.BaseQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 业主投票
 * @author: Hu
 * @create: 2021-08-23 16:52
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class VoteServiceImpl extends ServiceImpl<VoteMapper, VoteEntity> implements IVoteService {

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private VoteOptionMapper voteOptionMapper;

    @Autowired
    private VoteUserMapper voteUserMapper;

    @Autowired
    private VoteTopicMapper voteTopicMapper;


    @Override
    public List<VoteOptionEntity> getPlan(Long id) {
//        return voteOptionMapper.getPlan(id);
        return null;
    }

    @Override
    public VoteEntity getVote(Long id) {
        VoteEntity voteEntity = voteMapper.selectById(id);
        if (voteEntity!=null){
            List<VoteOptionEntity> voteList = voteOptionMapper.selectList(new QueryWrapper<VoteOptionEntity>().eq("vote_id", id));
            voteEntity.setOptions(voteList);
            return voteEntity;
        }
        throw new ProprietorException("当前活动不存在或者已结束！");
    }

    @Override
    public Map<String,Object> list(BaseQO<VoteEntity> baseQO) {
        Map<String,Object> map=new HashMap<>();
        Page<VoteEntity> page = voteMapper.selectPage(new Page<VoteEntity>(baseQO.getPage(), baseQO.getSize()), new QueryWrapper<VoteEntity>().eq("community_id", baseQO.getQuery().getCommunityId()));
        map.put("total",page.getTotal());
        map.put("list",page.getRecords());
        return map;
    }


}
