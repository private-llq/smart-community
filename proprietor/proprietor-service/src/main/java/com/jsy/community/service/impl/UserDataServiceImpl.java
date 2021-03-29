package com.jsy.community.service.impl;

import com.jsy.community.api.IUserDataService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.mapper.UserDataMapper;
import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.vo.UserDataVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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
     
     @Autowired
     private UserAuthMapper userAuthMapper;

    @Override
    public void updateUserData(UserDataQO userDataQO, String userId) {
        userDataMapper.updateUserData(userDataQO,userId);
    }

    @Override
    public UserDataVO selectUserDataOne(String userId) {
        return userDataMapper.selectUserDataOne(userId);
    }
    
    /**
    * @Description: 账号安全状态查询
     * @Param: [uid]
     * @Return: java.util.Map<java.lang.String,java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/3/29
    **/
    @Override
    public Map<String,String> querySafeStatus(String uid){
        Map<String, String> returnMap = userAuthMapper.querySafeStatus(uid);
        returnMap.put("mobile",returnMap.get("mobile").substring(0, 3).concat("****").concat(returnMap.get("mobile").substring(7, 11)));
        return returnMap;
    }
}



