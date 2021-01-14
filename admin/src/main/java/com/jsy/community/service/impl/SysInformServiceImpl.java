package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.mapper.SysInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.service.ISysInformService;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 系统消息业务类
 * @author YuLF
 * @since 2020-12-21 10:29
 **/
@Slf4j
@Service
public class SysInformServiceImpl extends ServiceImpl<SysInformMapper, PushInformEntity> implements ISysInformService {

	@Resource
	private SysInformMapper sysInformMapper;


	@Override
	public boolean add(PushInformQO sysInformQo) {
		PushInformEntity sysInformEntity = PushInformEntity.getInstance();
		BeanUtils.copyProperties(sysInformQo, sysInformEntity);
		sysInformEntity.setId(SnowFlake.nextId());
		return sysInformMapper.insert(sysInformEntity) > 0;
	}


	@Override
	public boolean delete(Long informId) {
		return sysInformMapper.deleteById(informId) > 0;
	}

	@Override
	public List<PushInformEntity> query(BaseQO<PushInformQO> baseQo) {
		baseQo.setPage((baseQo.getPage() - 1 ) * baseQo.getSize());
		return sysInformMapper.query(baseQo);
	}

	@Override
	public boolean deleteBatchByIds(List<Long> informIds) {
		return sysInformMapper.deleteBatchIds(informIds) > 0 ;
	}
}
