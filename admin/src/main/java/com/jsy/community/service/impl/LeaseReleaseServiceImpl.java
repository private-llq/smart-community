package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.LeaseOperationRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.LeaseReleasePageQO;
import com.jsy.community.service.LeaseReleaseService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.lease.LeaseReleasePageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeaseReleaseServiceImpl implements LeaseReleaseService {

    @Resource
    private LeaseOperationRecordMapper leaseOperationRecordMapper;
    @Resource
    private CommunityMapper communityMapper;

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
        List<CommunityEntity> communityList = communityMapper.selectBatchIds(collect);
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
