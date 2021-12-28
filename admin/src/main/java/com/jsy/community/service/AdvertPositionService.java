package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdvertPositionEntity;
import com.jsy.community.qo.admin.AddAdvertPositionQO;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告位置服务层
 * @date 2021/12/25 16:05
 */
public interface AdvertPositionService extends IService<AdvertPositionEntity> {
    boolean insertPosition(AddAdvertPositionQO qo);
}
