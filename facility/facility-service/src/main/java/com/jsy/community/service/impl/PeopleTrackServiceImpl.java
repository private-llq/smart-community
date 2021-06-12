package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPeopleTrackService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PeopleTrackEntity;
import com.jsy.community.mapper.PeopleTrackMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PeopleTrackQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lihao
 * @since 2021-04-26
 */
@DubboService(version = Const.version, group = Const.group_facility)
public class PeopleTrackServiceImpl extends ServiceImpl<PeopleTrackMapper, PeopleTrackEntity> implements IPeopleTrackService {
	
	@Autowired
	private PeopleTrackMapper peopleTrackMapper;
	
	@Override
	public void insertPeopleTrack(PeopleTrackEntity peopleTrackEntity) {
		peopleTrackEntity.setId(SnowFlake.nextId());
		peopleTrackMapper.insert(peopleTrackEntity);
	}
	
	@Override
	public PageInfo<PeopleTrackEntity> listPeopleTrack(BaseQO<PeopleTrackQO> qo) {
		Page<PeopleTrackEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,qo);
		List<PeopleTrackEntity> peopleTrackEntityList = peopleTrackMapper.listPeopleTrack(qo,page);
		
		PageInfo<PeopleTrackEntity> info = new PageInfo<>();
		BeanUtils.copyProperties(page,info);
		info.setRecords(peopleTrackEntityList);
		return info;
	}
}
