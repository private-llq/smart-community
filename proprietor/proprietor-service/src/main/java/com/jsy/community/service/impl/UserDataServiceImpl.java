package com.jsy.community.service.impl;

import com.jsy.community.api.ISignatureService;
import com.jsy.community.api.IUserDataService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.signature.SignatureUserDTO;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.mapper.UserDataMapper;
import com.jsy.community.mapper.UserIMMapper;
import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.utils.CallUtil;
import com.jsy.community.utils.imutils.open.StringUtils;
import com.jsy.community.vo.UserDataVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
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

    @Autowired
    private UserIMMapper userIMMapper;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ISignatureService signatureService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER)
    private IBaseUserInfoRpcService baseUserInfoRpcService;


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
        //在这里如果头像和昵称都没更新的话，是不需要更新签章信息的
        if (StringUtils.isEmpty(userDataQO.getAvatarUrl()) && StringUtils.isEmpty(userDataQO.getNickname())) {
            return;
        }
        //同步修改签章用户信息
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setUuid(userId);
        if (!StringUtils.isEmpty(userDataQO.getAvatarUrl())) {
            signatureUserDTO.setImage(userDataQO.getAvatarUrl());
        }
        if (!StringUtils.isEmpty(userDataQO.getNickname())) {
            signatureUserDTO.setNickName(userDataQO.getNickname());
        }
        boolean b = signatureService.updateUser(signatureUserDTO);
        if (!b) {
            log.error("更新个人资料，签章用户同步失败，相关账户：" + userId + "，新个人资料：" + userDataQO.toString());
            throw new ProprietorException(JSYError.INTERNAL.getCode(), "签章板块个人资料同步失败，请联系管理员");
        }
        // 同步聊天头像昵称
        List<String> strings = userIMMapper.selectByUid(Collections.singleton(userId));
        if (strings.size() > 0) {
            CallUtil.updateUserInfo(strings.get(0), userDataQO.getNickname(), userDataQO.getAvatarUrl());
            log.info("同步聊天头像昵称成功：userid -> {},nickName -> {},image -> {}", strings.get(0), userDataQO.getNickname(), userDataQO.getAvatarUrl());
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
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(userId);
        UserDataVO userDataVO = new UserDataVO();
        // UserDataVO userDataVO = userDataMapper.selectUserDataOne(userId);
        if (userDetail == null) {
            UserDataVO dataVO = new UserDataVO();
            dataVO.setAvatarUrl("");
            dataVO.setNickname("");
            dataVO.setBirthdayTime("");
            return dataVO;
        } else {
            userDataVO.setNickname(userDetail.getNickName() == null ? "" : userDetail.getNickName());
            userDataVO.setAvatarUrl(userDetail.getAvatarThumbnail() == null ? "" : userDetail.getAvatarThumbnail());
            userDataVO.setBirthdayTime(userDetail.getBirthday() == null ? "" : userDetail.getBirthday());
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



