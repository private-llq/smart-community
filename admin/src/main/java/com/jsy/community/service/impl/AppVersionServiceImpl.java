package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.AppVersionEntity;
import com.jsy.community.mapper.AppVersionMapper;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.IAppVersionService;
import com.jsy.community.utils.SnowFlake;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author DKS
 * @description APP版本Service实现类
 * @since 2021-11-11 15:23
 **/
@Service
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper,AppVersionEntity> implements IAppVersionService {
	
	@Resource
	private AppVersionMapper appVersionMapper;
	
	/**
	 * @Description: 查询APP版本列表 1.安卓 2.IOS
	 * @author: DKS
	 * @since: 2021/11/13 13:59
	 * @Param: [sysType, sysVersion]
	 * @return: java.util.List<com.jsy.community.entity.AppVersionEntity>
	 */
	@Override
	public List<AppVersionEntity> queryAppVersionList(Integer sysType,String sysVersion){
		QueryWrapper queryWrapper = new QueryWrapper<>();
		queryWrapper.select("*");
		if(sysType != null){
			queryWrapper.eq("sys_type",sysType);
		}
		if(!StringUtils.isEmpty(sysVersion)){
			queryWrapper.eq("sys_version",sysVersion);
		}
		queryWrapper.orderByDesc("create_time");
		return appVersionMapper.selectList(queryWrapper);
	}
	
	/**
	 * @Description: 添加APP版本
	 * @author: DKS
	 * @since: 2021/11/13 13:59
	 * @Param: [appVersionEntity]
	 * @return: void
	 */
	@Override
	public void addAppVersion(AppVersionEntity appVersionEntity){
		try {
			appVersionEntity.setId(SnowFlake.nextId());
			appVersionMapper.insert(appVersionEntity);
		}catch (DuplicateKeyException e){
			throw new AdminException("app版本已存在");
		}
	}
	
	/**
	 * @Description: 查询APP版本详情 1.安卓 2.IOS
	 * @author: DKS
	 * @since: 2021/11/13 13:59
	 * @Param: [sysType, sysVersion]
	 * @return: com.jsy.community.entity.AppVersionEntity
	 */
	@Override
	public AppVersionEntity queryAppVersion(Integer sysType, String sysVersion) {
		QueryWrapper<AppVersionEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("sys_type", sysType).eq("deleted", 0);
		if(!StringUtils.isEmpty(sysVersion)){
			queryWrapper.eq("sys_version",sysVersion);
		}
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		AppVersionEntity appVersionEntity = appVersionMapper.selectOne(queryWrapper);
		appVersionEntity.setSysVersionNumber(appVersionEntity.getSysVersion().replace(".",""));
		return appVersionEntity;
	}
}
