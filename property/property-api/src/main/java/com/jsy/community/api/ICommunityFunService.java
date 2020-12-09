package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.qo.CommunityFunQO;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-09 10:49
 **/
public interface ICommunityFunService extends IService<CommunityFunEntity> {
    Map<String,Object> findList(CommunityFunQO communityFunQO);


}
