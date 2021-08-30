package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PeopleHistoryEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 人员进出记录表服务
 * @Date: 2021/8/25 16:12
 * @Version: 1.0
 **/
public interface PeopleHistoryService extends IService<PeopleHistoryEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增人员进出记录
     * @param jsonString: 人员进出记录数据
     * @param communityId: 社区ID
     * @return: java.lang.Integer
     * @date: 2021/8/25 16:16
     **/
    Integer batchAddPeopleHistory(String jsonString, Long communityId);

    /**
     * @author: Pipi
     * @description: 分页查询人员进出记录
     * @param baseQO: 查询条件
     * @return: java.util.List<com.jsy.community.entity.PeopleHistoryEntity>
     * @date: 2021/8/27 10:24
     **/
    PageInfo<PeopleHistoryEntity> pagePeopleHistory(BaseQO<PeopleHistoryEntity> baseQO);
}