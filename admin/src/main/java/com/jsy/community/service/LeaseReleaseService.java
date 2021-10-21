package com.jsy.community.service;


import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.LeaseReleasePageQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.LeaseReleaseInfoVO;
import com.jsy.community.vo.admin.LeaseReleasePageVO;

public interface LeaseReleaseService {
    /**
     * 商铺和房屋租赁信息发布列表
     *
     * @param baseQO 分页条件和查询条件
     * @return
     */
    PageInfo<LeaseReleasePageVO> queryLeaseReleasePage(BaseQO<LeaseReleasePageQO> baseQO);

    LeaseReleaseInfoVO queryLeaseHouseInfo(Long id, Integer type);
}
