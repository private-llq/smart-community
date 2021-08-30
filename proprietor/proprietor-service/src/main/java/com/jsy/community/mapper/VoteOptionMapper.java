package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.VoteOptionEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投票答案
 * @author: Hu
 * @create: 2021-08-23 16:48
 **/
public interface VoteOptionMapper extends BaseMapper<VoteOptionEntity> {
    /**
     * @Description: 查询投票进度
     * @author: Hu
     * @since: 2021/8/24 9:42
     * @Param:
     * @return:
     */
    @Select("SELECT\n" +
            "\ttv.`code`,\n" +
            "\tcount( tu.option_id ) AS number,\n" +
            "\ttv.content \n" +
            "FROM\n" +
            "\tt_vote_option tv\n" +
            "\tLEFT JOIN t_vote_user tu ON tv.id = tu.option_id \n" +
            "WHERE\n" +
            "\ttv.vote_id = #{id} \n" +
            "GROUP BY\n" +
            "\ttv.`code`,\n" +
            "\ttv.content")
    List<VoteOptionEntity> getPlan(Long id);
}
