package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.qo.proprietor.UserInformQO;

import java.util.Map;

/**
 * 通知消息接口
 *
 * @author ling
 * @since 2020-11-11 15:47
 */
public interface IUserInformService extends IService<UserInformEntity> {
    Map<String,Object> findList(UserInformQO userInformQO);

}
