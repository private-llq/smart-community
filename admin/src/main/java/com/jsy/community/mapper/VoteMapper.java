package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.VoteEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: com.jsy.community
 * @description: 投票问卷
 * @author: DKS
 * @create: 2021-11-08 10:23
 **/
@Mapper
public interface VoteMapper extends BaseMapper<VoteEntity> {
}
