package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.lease.HouseRecentEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

/**
 * 房屋最近浏览提供类
 * @author YuLF
 * @since 2020-2-19 09:21
 */
public interface IHouseRecentService extends IService<HouseRecentEntity> {


    /**
     * 保存用户访问租赁最近浏览的 数据
     * @param result    租赁详情接口执行的结果
     * @param uid       用户id
     */
    @Async(BusinessConst.LEASE_ASYNC_POOL)
    void saveLeaseBrowse(Object result, String uid);
}
