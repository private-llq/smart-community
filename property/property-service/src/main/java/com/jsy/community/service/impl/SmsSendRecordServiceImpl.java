package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ISmsSendRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.SmsSendRecordEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.SmsSendRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.SmsSendRecordQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信发送记录
 * @author: DKS
 * @create: 2021-09-08 17:17
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class SmsSendRecordServiceImpl extends ServiceImpl<SmsSendRecordMapper, SmsSendRecordEntity> implements ISmsSendRecordService {
    
    @Autowired
    private SmsSendRecordMapper smsSendRecordMapper;
    
    @Autowired
    private CommunityMapper communityMapper;
	
	/**
	 * @Description: 新增短信发送记录
	 * @Param: [smsSendRecordEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021-09-08
	 **/
	@Override
	public boolean addSmsSendRecord(SmsSendRecordEntity smsSendRecordEntity) {
		int row;
		smsSendRecordEntity.setId(SnowFlake.nextId());
		row = smsSendRecordMapper.insert(smsSendRecordEntity);
		return row == 1;
	}
	
	/**
	 *@Author: DKS
	 *@Description: 批量新增短信发送记录
	 *@Param: [smsSendRecordEntity]
	 *@Return: Integer
	 *@Date: 2021/9/8 17:22
	 **/
	@Override
	public Integer saveSmsSendRecord(List<SmsSendRecordEntity> smsSendRecordEntityList) {
		return smsSendRecordMapper.saveSmsSendRecord(smsSendRecordEntityList);
	}
	
	/**
	 * @Description: 分页查询短信发送记录
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsSendRecordEntity>>
	 * @Author: DKS
	 * @Date: 2021/09/08
	 **/
	@Override
	public PageInfo<SmsSendRecordEntity> querySmsSendRecord(BaseQO<SmsSendRecordQO> baseQO, List<String> adminCommunityIdList) {
		SmsSendRecordQO query = baseQO.getQuery();
		Page<SmsSendRecordEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<SmsSendRecordEntity> queryWrapper = new QueryWrapper<>();
		//是否查手机号
		if (StringUtils.isNotBlank(query.getMobile())) {
			queryWrapper.eq("mobile", query.getMobile());
		}
		//是否查状态
		if (query.getStatus() != null) {
			queryWrapper.eq("status", query.getStatus());
		}
		//是否查小区
		if (query.getCommunityId() != null) {
			queryWrapper.eq("community_id", query.getCommunityId());
		} else {
			queryWrapper.in("community_id", adminCommunityIdList);
		}
		queryWrapper.orderByDesc("create_time");
		Page<SmsSendRecordEntity> pageData = smsSendRecordMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		// 补充小区名称
		for (SmsSendRecordEntity smsSendRecordEntity : pageData.getRecords()) {
			CommunityEntity communityEntity = communityMapper.selectById(smsSendRecordEntity.getCommunityId());
			smsSendRecordEntity.setCommunityName(communityEntity.getName());
			smsSendRecordEntity.setStatusName(smsSendRecordEntity.getStatus() == 1 ? "成功" : "失败");
		}
		PageInfo<SmsSendRecordEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
}