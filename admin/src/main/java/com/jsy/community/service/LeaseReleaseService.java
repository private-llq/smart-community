package com.jsy.community.service;


import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.LeaseReleasePageQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.LeaseReleaseInfoVO;

public interface LeaseReleaseService {
    /**
     * 商铺和房屋租赁信息发布列表
     *
     * @param baseQO 分页条件和查询条件
     * @return
     */
    PageInfo<AssetLeaseRecordEntity> queryLeaseReleasePage(BaseQO<LeaseReleasePageQO> baseQO);

    LeaseReleaseInfoVO queryLeaseHouseInfo(Long id, Integer type);
}
