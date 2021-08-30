package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PeopleHistoryService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.PeopleHistoryEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.mapper.PeopleHistoryMapper;
import com.jsy.community.mapper.PropertyRelationMapper;
import com.jsy.community.mapper.VisitorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/8/25 16:13
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class PeopleHistoryServiceImpl extends ServiceImpl<PeopleHistoryMapper, PeopleHistoryEntity> implements PeopleHistoryService {

    @Autowired
    private PeopleHistoryMapper peopleHistoryMapper;

    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private PropertyRelationMapper propertyRelationMapper;

    /**
     * @author: Pipi
     * @description: 批量新增人员进出记录
     * @param jsonString: 人员进出记录数据
     * @param communityId: 社区ID
     * @return: java.lang.Integer
     * @date: 2021/8/25 16:16
     **/
    @Override
    public Integer batchAddPeopleHistory(String jsonString, Long communityId) {
        log.info("同步人员进出记录");
        List<PeopleHistoryEntity> historyEntities = JSON.parseArray(jsonString, PeopleHistoryEntity.class);
        // 做出筛选条件
        // 判断电话号码和身份证号存在情况,如果同时存在,取电话号码查,如果只任意一个存在,取存在的这一个查
        Set<String> mobileSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(historyEntities)) {
            for (PeopleHistoryEntity historyEntity : historyEntities) {
                mobileSet.add(historyEntity.getMobile());
            }
        }
        // ================== 查询可能的用户信息 ==================================
        QueryWrapper<HouseMemberEntity> houseMemberEntityQueryWrapper = new QueryWrapper<>();
        houseMemberEntityQueryWrapper.eq("community_id", communityId);
        houseMemberEntityQueryWrapper.in("mobile", mobileSet);
        List<HouseMemberEntity> houseMemberEntities = propertyRelationMapper.selectList(houseMemberEntityQueryWrapper);
        Set<String> needRemoveMobileSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(houseMemberEntities)) {
            for (HouseMemberEntity houseMemberEntity : houseMemberEntities) {
                needRemoveMobileSet.add(houseMemberEntity.getMobile());
                for (PeopleHistoryEntity historyEntity : historyEntities) {
                    // 用户信息电话不为空且两个电话号码相同
                    if (StringUtils.isNotBlank(houseMemberEntity.getMobile()) && houseMemberEntity.getMobile().equals(historyEntity.getMobile()))
                    {
                        historyEntity.setName(houseMemberEntity.getName());
                        // 身份类型为空或者原有身份类型权限小于新的身份类型
                        if (historyEntity.getIdentityType() == null || BusinessEnum.RelationshipEnum.getNameCode(historyEntity.getIdentityType()) > houseMemberEntity.getRelation()) {
                            historyEntity.setIdentityType(BusinessEnum.RelationshipEnum.getCodeName(houseMemberEntity.getRelation()));
                        }
                    }
                }
            }
        }
        mobileSet.removeAll(needRemoveMobileSet);
        // ================== 查询可能的访客信息 ==================================
        QueryWrapper<VisitorEntity> visitorEntityQueryWrapper = new QueryWrapper<>();
        visitorEntityQueryWrapper.eq("community_id", communityId);
        visitorEntityQueryWrapper.in("contact", mobileSet);
        List<VisitorEntity> visitorEntities = visitorMapper.selectList(visitorEntityQueryWrapper);
        if (!CollectionUtils.isEmpty(visitorEntities)) {
            for (VisitorEntity visitorEntity : visitorEntities) {
                for (PeopleHistoryEntity historyEntity : historyEntities) {
                    // 访客信息电话不为空且两个电话号码相同
                    if (StringUtils.isNotBlank(visitorEntity.getContact()) && historyEntity.getMobile().equals(visitorEntity.getContact()) && historyEntity.getIdentityType() == null) {
                        historyEntity.setName(visitorEntity.getName());
                        historyEntity.setIdentityType("访客");
                    }
                }
            }
        }
        return peopleHistoryMapper.insertBatch(historyEntities);
    }

    /**
     * @param baseQO : 查询条件
     * @author: Pipi
     * @description: 分页查询人员进出记录
     * @return: java.util.List<com.jsy.community.entity.PeopleHistoryEntity>
     * @date: 2021/8/27 10:24
     **/
    @Override
    public PageInfo<PeopleHistoryEntity> pagePeopleHistory(BaseQO<PeopleHistoryEntity> baseQO) {
        Page<PeopleHistoryEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<PeopleHistoryEntity> queryWrapper = new QueryWrapper<>();
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new PeopleHistoryEntity());
        }
        PeopleHistoryEntity query = baseQO.getQuery();
        if (StringUtils.isNotBlank(query.getSearchText())) {
            queryWrapper.like("mobile", query.getSearchText());
            queryWrapper.or().like("name", query.getSearchText());
        }
        if (query.getOpenStatus() != null) {
            if (query.getOpenStatus() == 1) {
                queryWrapper.eq("verify_status", 1);
            } else {
                queryWrapper.ne("verify_status", 1);
            }
        }
        page = peopleHistoryMapper.selectPage(page, queryWrapper);
        PageInfo<PeopleHistoryEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(page, pageInfo);
        if (!CollectionUtils.isEmpty(pageInfo.getRecords())) {
            for (PeopleHistoryEntity record : pageInfo.getRecords()) {
                if (record.getVerifyStatus() == 1) {
                    record.setVerifyStatusStr("正常");
                } else {
                    record.setVerifyStatusStr("失败");
                }
                if (record.getAccessType() == 1) {
                    record.setAccessTypeStr("二维码");
                } else {
                    record.setAccessTypeStr("人脸");
                }
            }
        }
        return pageInfo;
    }
}
