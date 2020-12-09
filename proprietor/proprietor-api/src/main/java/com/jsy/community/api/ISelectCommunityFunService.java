package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.qo.CommunityFunQO;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 业主端的社区趣事查询接口
 * @author: Hu
 * @create: 2020-12-09 17:05
 **/
public interface ISelectCommunityFunService extends IService<CommunityFunEntity> {
    Map<String, Object> findList(CommunityFunQO communityFunQO);
}
