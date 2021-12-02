package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.lease.HouseRecentEntity;
import com.jsy.community.qo.BaseQO;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * 房屋最近浏览提供类
 *
 * @author YuLF
 * @since 2020-2-19 09:21
 */
public interface IHouseRecentService extends IService<HouseRecentEntity> {


    /**
     * 保存用户访问租赁最近浏览的 数据
     * @param result 租赁详情接口执行的结果
     * @param uid    用户id
     */
    @Async(BusinessConst.LEASE_ASYNC_POOL)
    void saveLeaseBrowse(Object result, String uid);

    /**
     * 房屋最近浏览
     *
     * @param leaseType 浏览数据类型：0出租房屋、1商铺
     * @param qo        分页参数
     * @param uid       用户id
     * @return 返回房屋列表
     */
    List<HouseRecentEntity> recentBrowseList(Integer leaseType, BaseQO<Object> qo, String uid);

    /**
     * 清空用户最近浏览
     *
     * @param type   浏览数据类型：0出租房屋、1商铺
     * @param userId 用户id
     * @return 返回影响行数
     */
    Boolean clearRecentBrowse(Integer type, String userId);
}
