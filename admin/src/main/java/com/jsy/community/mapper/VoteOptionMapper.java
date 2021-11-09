package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.VoteOptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投票选项
 * @author: Hu
 * @create: 2021-09-23 10:55
 **/
@Mapper
public interface VoteOptionMapper extends BaseMapper<VoteOptionEntity> {

    /**
     * @Description: 批量新增
     * @author: Hu
     * @since: 2021/9/23 13:58
     * @Param:
     * @return:
     */
    void saveAll(@Param("list") List<VoteOptionEntity> list);
    
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
