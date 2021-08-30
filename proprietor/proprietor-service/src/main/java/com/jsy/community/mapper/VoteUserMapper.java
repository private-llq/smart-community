package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.VoteUserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedList;
import java.util.Set;

/**
 * @program: com.jsy.community
 * @description: 投票-业主选项
 * @author: Hu
 * @create: 2021-08-23 16:48
 **/
public interface VoteUserMapper extends BaseMapper<VoteUserEntity> {
    /**
     * @Description: 查询所有已经投票了的人
     * @author: Hu
     * @since: 2021/8/24 9:59
     * @Param:
     * @return:
     */
    @Select("select uid from t_vote_user where vote_id=#{id}")
    Set<String> getUserTotal(@Param("id") Long id);

    /**
     * @Description: 业主投票投票
     * @author: Hu
     * @since: 2021/8/24 10:26
     * @Param:
     * @return:
     */
    void save(@Param("list") LinkedList<VoteUserEntity> list);
}
