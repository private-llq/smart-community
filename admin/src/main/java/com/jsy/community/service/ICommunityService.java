package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.CommunityPropertyListVO;

import java.util.List;

/**
 * @author chq459799974
 * @description 社区接口
 * @since 2020-11-20 09:06
 **/
public interface ICommunityService extends IService<CommunityEntity> {
    /**
     * @Description: 社区新增
     * @Param: [communityEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    boolean addCommunity(CommunityEntity communityEntity);

    /**
     * @param communityEntity:
     * @description: 物业端更新社区信息
     * @return: java.lang.Integer
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    Integer updateCommunity(CommunityEntity communityEntity);

    /**
     * @Description: 社区查询
     * @Param: [baseQO]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CommunityEntity>
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    PageInfo<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO);

    /**
     * @Description: 删除角色
     * @Param: [id]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    boolean delCommunity(Long id);

    /**
     * @Description: 社区列表查询
     * @Param: []
     * @Return: java.util.List<com.jsy.community.entity.sys.PropertyCompanyEntity>
     * @Author: DKS
     * @Date: 2021/10/19
     **/
    List<CommunityEntity> queryCommunityList();

    /**
     * 查询小区名字和物业公司名字以及小区id
     *
     * @return
     */
    List<CommunityPropertyListVO> queryCommunityAndPropertyList();
}
