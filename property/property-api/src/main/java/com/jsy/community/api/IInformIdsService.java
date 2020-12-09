package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.InformIdsEntity;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-08 14:00
 **/
public interface IInformIdsService extends IService<InformIdsEntity> {
    /**
     * @Description: 添加所有收到通知消息的id
     * @author: Hu
     * @since: 2020/12/8 14:10
     * @Param:
     * @return:
     */
    void addIds(InformIdsEntity informIdsEntity);
}
