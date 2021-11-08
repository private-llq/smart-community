package com.jsy.community.service;

import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.entity.proprietor.VoteUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 投票问卷
 * @author: DKS
 * @create: 2021-11-08 10:29
 **/
public interface IVoteService {
    /**
     * @Description: 分页查询
     * @author: DKS
     * @since: 2021/11/8 10:54
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.VoteEntity>
     */
    PageInfo<VoteEntity> list(BaseQO<VoteEntity> baseQO);
    
    /**
     * @Description: 新增投票问卷
     * @author: DKS
     * @since: 2021/11/8 11:15
     * @Param: [voteEntity]
     * @return: void
     */
    void saveBy(VoteEntity voteEntity);
    
    /**
     * @Description: 投票问卷详情
     * @author: DKS
     * @since: 2021/11/8 11:14
     * @Param: [id]
     * @return: java.util.List<com.jsy.community.entity.proprietor.VoteUserEntity>
     */
    List<VoteUserEntity> getOne(Long id);
    
    /**
     * @Description: 查图表
     * @author: DKS
     * @since: 2021/11/8 11:13
     * @Param: [id]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> getChart(Long id);
    
    /**
     * @Description: 删除或撤销
     * @author: DKS
     * @since: 2021/11/8 11:12
     * @Param: [id]
     * @return: boolean
     */
    boolean delete(Long id);
    
    /**
     * @Description: 查询一条详情
     * @author: DKS
     * @since: 2021/11/8 16:41
     * @Param: [id]
     * @return: com.jsy.community.entity.proprietor.VoteEntity
     */
    VoteEntity getVote(Long id);
}
