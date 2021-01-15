package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jsy.community.api.IUserUroraTagsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserUroraTagsEntity;
import com.jsy.community.mapper.UserUroraTagsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author chq459799974
 * @description 用户极光推送tags实现类
 * @since 2021-01-14 11:39
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class IUserUroraTagsServiceImpl implements IUserUroraTagsService {
	
	@Autowired
	private UserUroraTagsMapper userUroraTagsMapper;
	
	/**
	* @Description: 查询(有前台接口)
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.UserUroraTagsEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	**/
	@Override
	public UserUroraTagsEntity queryUroraTags(String uid){
		UserUroraTagsEntity userUroraTagsEntity = userUroraTagsMapper.selectOne(new QueryWrapper<UserUroraTagsEntity>()
			.select("community_tags").eq("uid", uid));
		if(userUroraTagsEntity != null){
			//设置包含全部类型的uroraTags
			userUroraTagsEntity.setUroraTags(userUroraTagsEntity.getCommunityTags());
		}
		return userUroraTagsEntity;
	}
	
	/**
	* @Description: 创建极光推送tags(后台调用)
	 * @Param: [userUroraTagsEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	**/
	@Override
	public boolean createUroraTags(UserUroraTagsEntity userUroraTagsEntity){
		return userUroraTagsMapper.insert(userUroraTagsEntity) == 1;
	}
	
	/**
	* @Description: 追加(只支持单个)(后台调用)
	 * @Param: [userUroraTagsEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	**/
	@Override
	public boolean appendTags(UserUroraTagsEntity userUroraTagsEntity){
		if(StringUtils.isEmpty(userUroraTagsEntity.getCommunityTags()) || userUroraTagsEntity.getCommunityTags().contains(",")
		  || userUroraTagsEntity.getCommunityTags().contains(" ")){
			return false;
		}
		return userUroraTagsMapper.appendTags(userUroraTagsEntity) == 1;
	}
	
	/**
	* @Description: 删除(可批量，逗号分隔)(后台调用)
	 * @Param: [paramsEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	**/
	public boolean deleteTags(UserUroraTagsEntity paramsEntity){
		UserUroraTagsEntity dataEntity = userUroraTagsMapper.selectOne(new QueryWrapper<UserUroraTagsEntity>()
			.select("community_tags").eq("uid", paramsEntity.getUid()));
		if(dataEntity == null){
			return false;
		}
		/* 删除社区tags */
		if(!StringUtils.isEmpty(paramsEntity.getCommunityTags())
			&& !StringUtils.isEmpty(dataEntity.getCommunityTags())){
			paramsEntity.setCommunityTags(dealTags(paramsEntity.getCommunityTags(),dataEntity.getCommunityTags()));
		}
		/* 若后期有其他类型tags也这样同上处理 */
		
		//更新tags
		String uid = paramsEntity.getUid();
		paramsEntity.setUid(null);
		return userUroraTagsMapper.update(paramsEntity,new UpdateWrapper<UserUroraTagsEntity>().eq("uid",uid)) == 1;
	}
	
	/**
	 * 从原本的tags - dataTags 中，剔除要删除的tags - paramTags
	 */
	private String dealTags(String paramTags,String dataTags){
		String[] paramArr = paramTags.split(",");
		String[] dataArr = dataTags.split(",");
		Set<String> paramSet = new HashSet<>(Arrays.asList(paramArr));
		Set<String> dataSet = new HashSet<>(Arrays.asList(dataArr));
		paramSet.remove("all");//不允许删除系统tag all
		dataSet.removeAll(paramSet);
		return dataSet.toString().replace("[", "").replace("]", "").replace(", ",",");
	}
	
}
