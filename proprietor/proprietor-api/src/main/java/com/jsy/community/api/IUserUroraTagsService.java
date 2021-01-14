package com.jsy.community.api;

import com.jsy.community.entity.UserUroraTagsEntity;

/**
 * @author chq459799974
 * @description 用户极光推送tags
 * @since 2021-01-14 13:23
 **/
public interface IUserUroraTagsService {
	
	UserUroraTagsEntity queryUroraTags(String uid);
	
	boolean createUroraTags(UserUroraTagsEntity userUroraTagsEntity);
	
	boolean appendTags(UserUroraTagsEntity userUroraTagsEntity);
	
	boolean deleteTags(UserUroraTagsEntity paramsEntity);
	
}
