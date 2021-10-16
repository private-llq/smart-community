package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.LeaseOperationRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.mapper.LeaseOperationRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.LeaseReleasePageQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.lease.LeaseReleasePageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: Pipi
 * @Description: 租赁操作记录表服务实现
 * @Date: 2021/8/31 14:54
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_lease)
public class LeaseOperationRecordServiceImpl extends ServiceImpl<LeaseOperationRecordMapper, LeaseOperationRecordEntity> implements LeaseOperationRecordService {

    @Autowired
    private LeaseOperationRecordMapper leaseOperationRecordMapper;

    @DubboReference(version = Const.version, group = Const.group)
    private ICommunityService iCommunityService;

    /**
     * 商铺和房屋租赁信息发布列表
     *
     * @param baseQO 分页条件和查询条件
     * @return
     */
    @Override
    public PageInfo<LeaseReleasePageVO> queryLeaseReleasePage(BaseQO<LeaseReleasePageQO> baseQO) {
        LeaseReleasePageQO query = baseQO.getQuery();
        Page<LeaseReleasePageVO> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        // 分页查询
        Page<LeaseReleasePageVO> pageData = leaseOperationRecordMapper.queryLeaseReleasePage(query, page);
        if (pageData.getRecords().size() == 0) {
            return new PageInfo<>();
        }
        // 查询小区信息
        List<LeaseReleasePageVO> records = pageData.getRecords();
        Set<Long> collect = records.stream().map(LeaseReleasePageVO::getTCommunityId).collect(Collectors.toSet());
        List<CommunityEntity> communityList = iCommunityService.queryCommunityBatch(new ArrayList<>(collect));
        Map<Long, CommunityEntity> communityMap = communityList.stream().collect(Collectors.toMap(CommunityEntity::getId, Function.identity()));
        // 填充额外信息
        records.stream().peek(r -> {
            // 填充小区信息
            if (r.getTCommunityId() != null) {
                CommunityEntity communityEntity = communityMap.get(r.getTCommunityId());
                if (communityEntity != null) {
                    r.setCommunity(communityEntity.getName());
                }
            }
            // 填充租赁状态
            r.setLeaseStatus(leaseStatus(r.getTLeaseStatus()));
        }).collect(Collectors.toList());
        PageInfo<LeaseReleasePageVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }

    public static String leaseStatus(Integer leaseTypeI) {
        if (leaseTypeI == null) {
            return "";
        }
        return leaseTypeI == 0 ? "未出租" : "已出租";
    }

}
