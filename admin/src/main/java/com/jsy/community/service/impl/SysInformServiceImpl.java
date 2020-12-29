package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.mapper.SysInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysInformQO;
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
public class SysInformServiceImpl extends ServiceImpl<SysInformMapper, SysInformEntity> implements ISysInformService {

	@Resource
	private SysInformMapper sysInformMapper;


	@Override
	public boolean add(SysInformQO sysInformQO) {
		SysInformEntity sysInformEntity = new SysInformEntity();
		BeanUtils.copyProperties(sysInformQO, sysInformEntity);
		sysInformEntity.setId(SnowFlake.nextId());
		return sysInformMapper.insert(sysInformEntity) > 0;
	}

	@Override
	public boolean update(SysInformQO sysInformQO, Long informId) {
		SysInformEntity sysInformEntity = new SysInformEntity();
		BeanUtils.copyProperties(sysInformQO, sysInformEntity);
		sysInformEntity.setId(informId);
		return updateById(sysInformEntity);
	}

	@Override
	public boolean delete(Long informId) {
		return sysInformMapper.deleteById(informId) > 0;
	}

	@Override
	public List<SysInformEntity> query(BaseQO<SysInformQO> baseQO) {
		baseQO.setPage((baseQO.getPage() - 1 ) * baseQO.getSize());
		return sysInformMapper.query(baseQO);
	}

	@Override
	public boolean deleteBatchByIds(List<Long> informIds) {
		return sysInformMapper.deleteBatchIds(informIds) > 0 ;
	}
}