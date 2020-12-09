package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IInformIdsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.InformIdsEntity;
import com.jsy.community.mapper.InformIdsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-08 14:01
 **/
@DubboService(version = Const.version, group = Const.group)
public class InformIdsServiceImpl extends ServiceImpl<InformIdsMapper, InformIdsEntity> implements IInformIdsService {

    @Autowired
    private InformIdsMapper informIdsMapper;

    /**
     * @Description: 添加所有收到通知消息的id
     * @author: Hu
     * @since: 2020/12/8 14:10
     * @Param:
     * @return:
     */
    @Override
    public void addIds(InformIdsEntity informIdsEntity) {
        informIdsMapper.insert(informIdsEntity);
    }
}
