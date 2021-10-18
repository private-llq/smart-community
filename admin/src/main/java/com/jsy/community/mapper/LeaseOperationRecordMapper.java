package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.qo.admin.LeaseReleasePageQO;
import com.jsy.community.vo.admin.LeaseReleasePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LeaseOperationRecordMapper extends BaseMapper<LeaseOperationRecordEntity> {
    /**
     * 查询租赁信息发布记录
     * @param qo 查询条件
     * @param page 分页条件
     * @return
     */
    Page<LeaseReleasePageVO> queryLeaseReleasePage(@Param("qo") LeaseReleasePageQO qo, @Param("page") Page<LeaseReleasePageVO> page);
}
