package com.jsy.community.api;

import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.VoteQO;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 业主投票
 * @author: Hu
 * @create: 2021-08-23 16:52
 **/
public interface IVoteService {
    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/8/23 16:57
     * @Param:
     * @return:
     */
    Map<String,Object> list(BaseQO<VoteEntity> baseQO);

    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/8/23 17:03
     * @Param:
     * @return:
     */
    VoteEntity getVote(Long id,String uid);

    /**
     * @Description: 投票进度
     * @author: Hu
     * @since: 2021/8/23 17:11
     * @Param:
     * @return:
     */
    Map<String, Object> getPlan(Long id);

    /**
     * @Description: 业主投票
     * @author: Hu
     * @since: 2021/8/24 10:15
     * @Param:
     * @return:
     */
    void userVote(VoteQO voteQO,String uid);
}
