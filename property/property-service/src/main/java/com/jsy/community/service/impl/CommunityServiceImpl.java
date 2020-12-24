package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.mapper.CommunityMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 社区 服务实现类
 *
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityEntity> implements ICommunityService {
	
	@Autowired
	private CommunityMapper communityMapper;
	
	@Override
	public List<CommunityEntity> listCommunityByName(String query,Integer areaId) {
		QueryWrapper<CommunityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("area_id",areaId).like("name", query).or().like("detail_address", query);
		return communityMapper.selectList(wrapper);
	}
	
	@Override
	public List<CommunityEntity> listCommunityByAreaId(Long areaId) {
		QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("area_id", areaId);
		return communityMapper.selectList(queryWrapper);
	}
	
	@Override
	@Transactional
	@LcnTransaction
	public void addCommunityEntity() {
		CommunityEntity communityEntity = new CommunityEntity();
		communityEntity.setId(234L);
		communityEntity.setName("测试分布式事物");
		communityMapper.insert(communityEntity);
	}
}
