package com.jsy.community.service.impl;

import com.jsy.community.api.ISignatureService;
import com.jsy.community.api.IUserDataService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.signature.SignatureUserDTO;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.mapper.UserDataMapper;
import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.utils.imutils.open.StringUtils;
import com.jsy.community.vo.UserDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 用户个人信息
 * @author: Hu
 * @create: 2021-03-11 13:39
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserDataServiceImpl implements IUserDataService {

    @Autowired
    private UserDataMapper userDataMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ISignatureService signatureService;


    /**
     * @Description: 修改个人资料
     * @author: Hu
     * @since: 2021/5/21 13:57
     * @Param: [userDataQO, userId]
     * @return: void
     */
    @Override
    public void updateUserData(UserDataQO userDataQO, String userId) {
        userDataMapper.updateUserData(userDataQO, userId);
        //同步修改签章用户信息
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setUuid(userId);
        //在这里如果头像和昵称都没更新的话，是不需要更新签章信息的
        if (!StringUtils.isEmpty(userDataQO.getAvatarUrl())) {
            signatureUserDTO.setImage(userDataQO.getAvatarUrl());
        } else if (!StringUtils.isEmpty(userDataQO.getNickname())) {
            signatureUserDTO.setNickName(userDataQO.getNickname());
        } else {
            return;
        }
        boolean b = signatureService.updateUser(signatureUserDTO);
        if (!b) {
            log.error("更新个人资料，签章用户同步失败，相关账户：" + userId + "，新个人资料：" + userDataQO.toString());
            throw new ProprietorException(JSYError.INTERNAL.getCode(), "签章板块个人资料同步失败，请联系管理员");
        }
    }


    /**
     * @Description: 查询一条信息
     * @author: Hu
     * @since: 2021/5/21 13:57
     * @Param: [userId]
     * @return: com.jsy.community.vo.UserDataVO
     */
    @Override
    public UserDataVO selectUserDataOne(String userId) {
        UserDataVO userDataVO = userDataMapper.selectUserDataOne(userId);
        if (userDataVO == null) {
            UserDataVO dataVO = new UserDataVO();
            dataVO.setAvatarUrl("");
            dataVO.setNickname("");
            dataVO.setBirthdayTime("");
            return dataVO;
        }
        if (userDataVO.getNickname() == null) {
            userDataVO.setNickname("");
        }
        if (userDataVO.getAvatarUrl() == null) {
            userDataVO.setAvatarUrl("");
        }
        if (userDataVO.getBirthdayTime() == null) {
            userDataVO.setBirthdayTime("");
        }
        return userDataVO;
    }

    /**
     * @Description: 账号安全状态查询
     * @Param: [uid]
     * @Return: java.util.Map<java.lang.String, java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/3/29
     **/
    @Override
    public Map<String, String> querySafeStatus(String uid) {
        Map<String, String> returnMap = userAuthMapper.querySafeStatus(uid);
        returnMap.put("mobile", returnMap.get("mobile").substring(0, 3).concat("****").concat(returnMap.get("mobile").substring(7, 11)));
        return returnMap;
    }
}



