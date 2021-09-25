package com.jsy.community.api;

import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.entity.proprietor.VoteUserEntity;
import com.jsy.community.qo.BaseQO;

import java.util.List;
import java.util.Map;

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
    Map<String, Object> list(BaseQO<VoteEntity> baseQO, Long adminCommunityId);

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
    List<VoteUserEntity> getOne(Long id);

    /**
     * @Description: 查图表
     * @author: Hu
     * @since: 2021/9/23 10:45
     * @Param:
     * @return:
     */
    Map<String, Object> getChart(Long id);

    /**
     * @Description: 删除或撤销
     * @author: Hu
     * @since: 2021/9/23 14:28
     * @Param:
     * @return:
     */
    void delete(Long id);
}
