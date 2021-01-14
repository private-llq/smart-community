package com.jsy.community.api;

import com.jsy.community.entity.UserUroraTagsEntity;

/**
 * @author chq459799974
 * @description 用户极光推送tags
 * @since 2021-01-14 13:23
 **/
public interface IUserUroraTagsService {
	
	/**
	 * @Description: 查询(有前台接口)
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.UserUroraTagsEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	 **/
	UserUroraTagsEntity queryUroraTags(String uid);
	
	/**
	 * @Description: 创建极光推送tags(后台调用)
	 * @Param: [userUroraTagsEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	 **/
	boolean createUroraTags(UserUroraTagsEntity userUroraTagsEntity);
	
	/**
	 * @Description: 追加(只支持单个)(后台调用)
	 * @Param: [userUroraTagsEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	 **/
	boolean appendTags(UserUroraTagsEntity userUroraTagsEntity);
	
	/**
	 * @Description: 删除(可批量，逗号分隔)(后台调用)
	 * @Param: [paramsEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/14
	 **/
	boolean deleteTags(UserUroraTagsEntity paramsEntity);
	
}
