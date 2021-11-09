package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityHardWareEntity;

import java.util.List;
import java.util.Set;

/**
 * @Author: Pipi
 * @Description: 社区扫描设备(扫脸机)Mapper
 * @Date: 2021/8/18 10:14
 * @Version: 1.0
 **/
public interface CommunityHardWareMapper extends BaseMapper<CommunityHardWareEntity> {
    /**
     * @author: Pipi
     * @description: 查询社区的设备列表
     * @param communityId: 社区ID
     * @return: java.util.List<com.jsy.community.entity.CommunityHardWareEntity>
     * @date: 2021/9/24 15:10
     **/
    List<CommunityHardWareEntity> selectAllByCommunityId(Long communityId);

    /**
     * @author: Pipi
     * @description: 查询多个社区的设备列表
     * @param communityIds:
     * @return: java.util.List<com.jsy.community.entity.CommunityHardWareEntity>
     * @date: 2021/10/8 18:00
     **/
    List<CommunityHardWareEntity> selectAllByCommunityIds(List<Long> communityIds);

    /**
     * @author: Pipi
     * @description: 查询社区下的设备序列号列表
     * @param communityId: 社区ID
     * @return: java.util.List<java.lang.String>
     * @date: 2021/11/8 10:33
     **/
    Set<String> selectListHardWareIdByCommunityId(Long communityId);
}
