package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAppVersionService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppVersionEntity;
import com.jsy.community.mapper.AppVersionMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author chq459799974
 * @description APP版本查询Service实现类
 * @since 2021-05-31 13:57
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper,AppVersionEntity> implements IAppVersionService {
	
	@Autowired
	private AppVersionMapper aPPVersionMapper;
	
	/**
	* @Description: 查询APP版本列表 1.安卓 2.IOS
	 * @Param: [sysType,sysVersion]
	 * @Return: java.util.List<com.jsy.community.entity.AppVersionEntity>
	 * @Author: chq459799974
	 * @Date: 2021/5/31
	**/
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
		return aPPVersionMapper.selectList(queryWrapper);
	}
	
	/**
	* @Description: 添加APP版本
	 * @Param: [appVersionEntity]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/12
	**/
	@Override
	public void addAppVersion(AppVersionEntity appVersionEntity){
		QueryWrapper<AppVersionEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("sys_version", appVersionEntity.getSysVersion());
		queryWrapper.eq("sys_type", appVersionEntity.getSysType());
		AppVersionEntity entity = aPPVersionMapper.selectOne(queryWrapper);
		if (entity == null) {
			appVersionEntity.setId(SnowFlake.nextId());
			aPPVersionMapper.insert(appVersionEntity);
		} else {
			entity.setPaySupport(appVersionEntity.getPaySupport());
			int i = aPPVersionMapper.updateById(entity);
		}
		/*try {
			appVersionEntity.setId(SnowFlake.nextId());
			aPPVersionMapper.insert(appVersionEntity);
		}catch (DuplicateKeyException e){
			throw new ProprietorException("app版本已存在");
		}*/
	}

	/**
	 * @param appVersionEntity :
	 * @author: Pipi
	 * @description: 更新版本信息
	 * @return:
	 * @date: 2021/12/27 15:15
	 **/
	@Override
	public Boolean updateAppVersion(AppVersionEntity appVersionEntity) {
		QueryWrapper<AppVersionEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("sys_version", appVersionEntity.getSysVersion());
		queryWrapper.eq("sys_type", appVersionEntity.getSysType());
		AppVersionEntity entity = aPPVersionMapper.selectOne(queryWrapper);
		if (entity == null) {
			return false;
		}
		if (appVersionEntity.getPaySupport() != null) {
			entity.setPaySupport(appVersionEntity.getPaySupport());
			int i = aPPVersionMapper.updateById(entity);
			if (i == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 查询APP版本详情 1.安卓 2.IOS
	 * @Param: [sysType]
	 * @Return: com.jsy.community.entity.AppVersionEntity
	 * @Author: DKS
	 * @Date: 2021/9/22
	 **/
	@Override
	public AppVersionEntity queryAppVersion(Integer sysType, String sysVersion) {
		QueryWrapper<AppVersionEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("sys_type", sysType).eq("deleted", 0);
		if(!StringUtils.isEmpty(sysVersion)){
			queryWrapper.eq("sys_version",sysVersion);
		}
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		AppVersionEntity appVersionEntity = aPPVersionMapper.selectOne(queryWrapper);
		appVersionEntity.setSysVersionNumber(appVersionEntity.getSysVersion().replace(".",""));
		return appVersionEntity;
	}
}
