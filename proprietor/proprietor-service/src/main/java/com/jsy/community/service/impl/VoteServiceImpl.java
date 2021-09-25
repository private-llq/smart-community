package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVoteService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.entity.proprietor.VoteOptionEntity;
import com.jsy.community.entity.proprietor.VoteTopicEntity;
import com.jsy.community.entity.proprietor.VoteUserEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.VoteQO;
import com.jsy.community.utils.PushInfoUtil;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private UserIMMapper userIMMapper;



    /**
     * @Description: 业主投票
     * @author: Hu
     * @since: 2021/8/24 10:15
     * @Param: [voteQO]
     * @return: void
     */
    @Override
    @Transactional
    public void userVote(VoteQO voteQO, String uid) {
        VoteUserEntity voteUserEntity =null;
        LinkedList<VoteUserEntity> list1 = new LinkedList<>();
        List<VoteUserEntity> list = voteUserMapper.selectList(new QueryWrapper<VoteUserEntity>().eq("uid", uid).eq("vote_id", voteQO.getId()));
        if (list.size()==0){
            for (Long option : voteQO.getOptions()) {
                voteUserEntity = new VoteUserEntity();
                voteUserEntity.setId(SnowFlake.nextId());
                voteUserEntity.setUid(uid);
                voteUserEntity.setVoteId(voteQO.getId());
                voteUserEntity.setTopicId(voteQO.getTopicId());
                voteUserEntity.setOptionId(option);
                list1.add(voteUserEntity);
            }
            voteUserMapper.save(list1);
        }else {
            throw new ProprietorException("你已经投过票了哦!");
        }
        VoteEntity voteEntity = voteMapper.selectById(voteQO.getId());
        Set<String> total = voteUserMapper.getUserTotal(voteQO.getId());
        if (total.size()==voteEntity.getTotal()){
            Map  map = null;
            List<String> imId = userIMMapper.selectByUid(total);
            for (String im : imId) {
                map = new HashMap<>();
                map.put("type",2);
                map.put("dataId",voteEntity.getId());
                //推送消息
                PushInfoUtil.PushPublicTextMsg(
                        im,
                        "活动投票",
                        voteEntity.getTheme(),
                        null,
                        "投票时间说明" +
                                voteEntity.getBeginTime()+"————"+voteEntity.getOverTime(),map);
            }
        }

    }

    /**
     * @Description: 投票进度
     * @author: Hu
     * @since: 2021/8/24 9:34
     * @Param: [id]
     * @return: java.util.List<com.jsy.community.entity.proprietor.VoteOptionEntity>
     */
    @Override
    public Map<String, Object> getPlan(Long id) {
        VoteEntity voteEntity = voteMapper.selectById(id);
        Map<String, Object> map = new HashMap<>();
        Set<String> set = voteUserMapper.getUserTotal(id);
        if (voteEntity!=null){
            List<VoteOptionEntity> list = voteOptionMapper.getPlan(id);
            map.put("choose",voteEntity.getChoose());
            map.put("total",voteEntity.getTotal());
            map.put("list",list);
            map.put("haveTotal",set.size());
        }
        return map;
    }


    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/8/24 9:34
     * @Param: [id]
     * @return: com.jsy.community.entity.proprietor.VoteEntity
     */
    @Override
    public VoteEntity getVote(Long id,String uid) {
        //已投过票的答案id集合
        String str = null;
        Map<Long, Object> map = null;
        VoteEntity voteEntity = voteMapper.selectById(id);
        VoteTopicEntity topicEntity = voteTopicMapper.selectOne(new QueryWrapper<VoteTopicEntity>().eq("vote_id", id));
        if (voteEntity!=null){
            List<VoteUserEntity> userEntities = voteUserMapper.selectList(new QueryWrapper<VoteUserEntity>().eq("uid", uid).eq("vote_id", id));
            if (userEntities.size()!=0){
                voteEntity.setStatus(1);
                str=new String();
                map = new HashMap<>();
                for (VoteUserEntity userEntity : userEntities) {
                    str+=userEntity.getOptionId()+",";
                    map.put(userEntity.getOptionId(),userEntity);
                }
                topicEntity.setOptionsIds(str);
            }else {
                voteEntity.setStatus(0);
            }
            List<VoteOptionEntity> voteList = voteOptionMapper.selectList(new QueryWrapper<VoteOptionEntity>().eq("vote_id", id));
            if (voteEntity.getStatus()==1){
                for (VoteOptionEntity entity : voteList) {
                    if (map.get(entity.getId())!=null){
                        entity.setStatus(1);
                    }else{
                        entity.setStatus(0);
                    }
                }
            }
            topicEntity.setOptions(voteList);
            voteEntity.setVoteTopicEntity(topicEntity);
            return voteEntity;
        }

        throw new ProprietorException("当前活动不存在或者已结束！");
    }


    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/8/24 9:33
     * @Param: [baseQO]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String,Object> list(BaseQO<VoteEntity> baseQO) {
        Map<String,Object> map=new HashMap<>();
        Page<VoteEntity> page = voteMapper.selectPage(new Page<VoteEntity>(baseQO.getPage(), baseQO.getSize()), new QueryWrapper<VoteEntity>()
                .eq("community_id", baseQO.getQuery().getCommunityId()).in("vote_status",1,2));
        map.put("total",page.getTotal());
        map.put("list",page.getRecords());
        return map;
    }


}
