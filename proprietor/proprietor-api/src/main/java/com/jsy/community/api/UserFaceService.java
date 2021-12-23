package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserFaceEntity;

/**
 * @Author: Pipi
 * @Description: 用户人脸数据服务
 * @Date: 2021/12/22 15:24
 * @Version: 1.0
 **/
public interface UserFaceService extends IService<UserFaceEntity> {

    /**
     * @author: Pipi
     * @description: 查询用户人脸
     * @param uid: 用户uid
     * @return: {@link UserFaceEntity}
     * @date: 2021/12/22 15:34
     **/
    UserFaceEntity queryByUid(String uid);

    /**
     * @param faceUrl
     * @param uid
     * @author: Pipi
     * @description: 保存用户人脸
     * @return: {@link Integer}
     * @date: 2021/12/22 15:36
     **/
    Boolean saveUserFace(String faceUrl, String uid);

    /**
     * @author: Pipi
     * @description: 删除用户人脸
     * @param uid: 用户uid
     * @return: {@link Boolean}
     * @date: 2021/12/22 18:52
     **/
    Boolean deleteUserFace(String uid);
}
