package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserFaceEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 用户人脸数据Mapper
 * @Date: 2021/12/22 15:24
 * @Version: 1.0
 **/
public interface UserFaceMapper extends BaseMapper<UserFaceEntity> {

    /**
     * @author: Pipi
     * @description: 人脸管理查询人脸分页列表
     * @param userEntity: 用户实体
     * @param startNum: 分页起始数量
     * @param endNum: 分页结束数量
     * @return: java.util.List<com.jsy.community.entity.UserEntity>
     * @date: 2021/12/22 19:40
     **/
    List<UserFaceEntity> queryFacePageList(@Param("userEntity") UserEntity userEntity,
                                           @Param("uidList") Collection<String> uidList,
                                       @Param("startNum") Long startNum,
                                       @Param("endNum") Long endNum
                                           );
}
