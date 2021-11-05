package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CommunityRfEntity;
import com.jsy.community.entity.property.CommunityRfSycRecordEntity;

/**
 * @Author: Pipi
 * @Description: 门禁卡服务
 * @Date: 2021/11/3 16:30
 * @Version: 1.0
 **/
public interface CommunityRfService extends IService<CommunityRfEntity> {

    /**
     * @author: Pipi
     * @description: 添加门禁卡 
     * @param rfEntity:
     * @return: java.lang.Integer
     * @date: 2021/11/5 14:25
     **/
    Integer addRf(CommunityRfEntity rfEntity);
}
