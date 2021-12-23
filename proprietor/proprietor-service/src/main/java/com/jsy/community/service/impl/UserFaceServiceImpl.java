package com.jsy.community.service.impl;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PropertyUserService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserFaceService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserFaceEntity;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.UserFaceMapper;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import jodd.util.StringUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/12/22 15:26
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserFaceServiceImpl extends ServiceImpl<UserFaceMapper, UserFaceEntity> implements UserFaceService {
    @Autowired
    private UserFaceMapper userFaceMapper;

    @Autowired
    private HouseMemberMapper houseMemberMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private PropertyUserService propertyUserService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;
    /**
     * @param uid : 用户uid
     * @author: Pipi
     * @description: 查询用户人脸
     * @return: {@link UserFaceEntity}
     * @date: 2021/12/22 15:34
     **/
    @Override
    public UserFaceEntity queryByUid(String uid) {
        QueryWrapper<UserFaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return userFaceMapper.selectOne(queryWrapper);
    }

    /**
     * @param faceUrl
     * @param uid
     * @author: Pipi
     * @description: 保存用户人脸
     * @return: {@link Integer}
     * @date: 2021/12/22 15:36
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveUserFace(String faceUrl, String uid) {
        // 查询人脸数据记录
        UserFaceEntity faceEntityRecord = queryByUid(uid);
        // 增加新的人脸记录
        UserFaceEntity userFaceEntity = new UserFaceEntity();
        userFaceEntity.setFaceUrl(faceUrl);
        userFaceEntity.setUid(uid);
        userFaceEntity.setId(SnowFlake.nextId());
        if (faceEntityRecord != null) {
            userFaceEntity.setFaceEnableStatus(faceEntityRecord.getFaceEnableStatus());
            // 删除原有的人脸记录
            userFaceMapper.deleteById(faceEntityRecord.getId());
        } else {
            userFaceEntity.setFaceEnableStatus(1);
        }
        if (userFaceEntity.getFaceEnableStatus() == 1) {
            // 下发人脸数据到设备
            syncUserFace(faceUrl, uid);
        }
        return true;
    }

    /**
     * @param uid : 用户uid
     * @author: Pipi
     * @description: 删除用户人脸
     * @return: {@link Boolean}
     * @date: 2021/12/22 18:52
     **/
    @Override
    public Boolean deleteUserFace(String uid) {
        UserFaceEntity userFaceEntity = queryByUid(uid);
        if (userFaceEntity == null) {
            return true;
        }
        // 删除人脸
        userFaceMapper.deleteById(userFaceEntity.getId());
        if (userFaceEntity.getFaceEnableStatus() == 1) {
            // 人脸原本为启用状态,删除机器人脸数据
            // 查询所有相关的社区
            QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", uid);
            List<HouseMemberEntity> houseMemberEntities = houseMemberMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(houseMemberEntities)) {
                List<Long> communityIds = houseMemberEntities.stream().map(HouseMemberEntity::getCommunityId).collect(Collectors.toList());
                UserEntity userEntity = new UserEntity();
                userEntity.setUid(uid);
                for (Long communityId : communityIds) {
                    propertyUserService.deleteFace(userEntity, communityId);
                }
            }
        }
        return true;
    }

    /**
     * @author: Pipi
     * @description: 下发人脸数据
     * @param faceUrl: 人脸url
         * @param uid: 用户uid
     * @return:
     * @date: 2021/12/22 18:44
     **/
    public void syncUserFace(String faceUrl, String uid) {
        // 查询所有相关的社区
        QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        List<HouseMemberEntity> houseMemberEntities = houseMemberMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(houseMemberEntities)) {
            List<Long> communityIds = houseMemberEntities.stream().map(HouseMemberEntity::getCommunityId).collect(Collectors.toList());
            UserEntity userEntity = new UserEntity();
            userEntity.setUid(uid);
            userEntity.setFaceUrl(faceUrl);
            UserDetail userDetail = baseUserInfoRpcService.getUserDetail(uid);
            RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(uid);
            if (userDetail != null) {
                userEntity.setMobile(userDetail.getPhone());
            }
            if (idCardRealInfo != null) {
                userEntity.setRealName(idCardRealInfo.getIdCardName());
            }
            if (StringUtil.isNotBlank(userEntity.getMobile())) {
                // 下发数据
                propertyUserService.syncFace(userEntity, communityIds);
            }
        }

    }
}
