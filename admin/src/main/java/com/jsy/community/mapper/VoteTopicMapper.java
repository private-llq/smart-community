package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.VoteTopicEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: com.jsy.community
 * @description: 投票题目
 * @author: Hu
 * @create: 2021-09-23 10:56
 **/
@Mapper
public interface VoteTopicMapper extends BaseMapper<VoteTopicEntity> {
}
