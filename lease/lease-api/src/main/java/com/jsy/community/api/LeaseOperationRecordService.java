package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.LeaseReleasePageQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.LeaseReleasePageVO;

/**
 * @Author: Pipi
 * @Description: 租赁操作记录表服务
 * @Date: 2021/8/31 14:53
 * @Version: 1.0
 **/
public interface LeaseOperationRecordService extends IService<LeaseOperationRecordEntity> {

    /**
     * 商铺和房屋租赁信息发布列表
     *
     * @param baseQO 分页条件和查询条件
     * @return
     */
    PageInfo<LeaseReleasePageVO> queryLeaseReleasePage(BaseQO<LeaseReleasePageQO> baseQO);
}
