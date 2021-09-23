package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.VoteOptionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投票选项
 * @author: Hu
 * @create: 2021-09-23 10:55
 **/
public interface PropertyVoteOptionMapper extends BaseMapper<VoteOptionEntity> {

    /**
     * @Description: 批量新增
     * @author: Hu
     * @since: 2021/9/23 13:58
     * @Param:
     * @return:
     */
    void saveAll(@Param("list") List<VoteOptionEntity> list);
}
