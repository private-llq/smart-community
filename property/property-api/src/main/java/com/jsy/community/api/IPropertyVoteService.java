package com.jsy.community.api;

import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.qo.BaseQO;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 业主投票
 * @author: Hu
 * @create: 2021-09-23 10:03
 **/
public interface IPropertyVoteService {
    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/9/23 10:44
     * @Param:
     * @return:
     */
    List<VoteEntity> list(BaseQO<VoteEntity> baseQO, Long adminCommunityId);

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/9/23 10:44
     * @Param:
     * @return:
     */
    void saveBy(VoteEntity voteEntity);

    /**
     * @Description: 查详情
     * @author: Hu
     * @since: 2021/9/23 10:45
     * @Param:
     * @return:
     */
    void getOne(Long id);

    /**
     * @Description: 查图表
     * @author: Hu
     * @since: 2021/9/23 10:45
     * @Param:
     * @return:
     */
    void getChart(Long id);
}
