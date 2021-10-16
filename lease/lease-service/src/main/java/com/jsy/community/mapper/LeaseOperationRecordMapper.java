package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.qo.lease.LeaseReleasePageQO;
import com.jsy.community.vo.lease.LeaseReleasePageVO;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Pipi
 * @Description: 租赁操作记录表Mapper
 * @Date: 2021/8/31 14:52
 * @Version: 1.0
 **/
public interface LeaseOperationRecordMapper extends BaseMapper<LeaseOperationRecordEntity> {
    /**
     * 查询租赁信息发布记录
     * @param qo 查询条件
     * @param page 分页条件
     * @return
     */
    Page<LeaseReleasePageVO> queryLeaseReleasePage(@Param("qo") LeaseReleasePageQO qo, @Param("page") Page<LeaseReleasePageVO> page);
}
