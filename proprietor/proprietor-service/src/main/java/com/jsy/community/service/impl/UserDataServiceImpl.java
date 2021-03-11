package com.jsy.community.service.impl;

import com.jsy.community.api.IUserDataService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.UserDataMapper;
import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.vo.UserDataVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description: 用户个人信息
 * @author: Hu
 * @create: 2021-03-11 13:39
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserDataServiceImpl implements IUserDataService {

     @Autowired
     private UserDataMapper userDataMapper;

    @Override
    public void updateUserData(UserDataQO userDataQO, String userId) {
        userDataMapper.updateUserData(userDataQO,userId);
    }

    @Override
    public UserDataVO selectUserDataOne(String userId) {
        return userDataMapper.selectUserDataOne(userId);
    }
}



