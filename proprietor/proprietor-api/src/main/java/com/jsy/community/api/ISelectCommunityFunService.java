package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.qo.proprietor.SelectCommunityFunQO;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 业主端的社区趣事查询接口
 * @author: Hu
 * @create: 2020-12-09 17:05
 **/
public interface ISelectCommunityFunService extends IService<CommunityFunEntity> {
    /**
     * @Description: 分页查询所有趣事
     * @author: Hu
     * @since: 2021/2/23 17:29
     * @Param:
     * @return:
     */
    Map<String, Object> findList(SelectCommunityFunQO communityFunQO);

    /**
     * @Description: 查询一条趣事详情
     * @author: Hu
     * @since: 2021/2/23 17:29
     * @Param:
     * @return:
     */
    CommunityFunEntity findFunOne(Long id);

    /**
     * @Description: 浏览量
     * @author: Hu
     * @since: 2021/2/23 17:28
     * @Param:
     * @return:
     */
    Integer saveViewCount(Long id);
}
