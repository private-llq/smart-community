package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IWeChatService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.WeChatOrderEntity;
import com.jsy.community.mapper.WeChatMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description:  微信支付
 * @author: Hu
 * @create: 2021-01-26 14:26
 **/
@DubboService(version = Const.version, group = Const.group_payment)
public class WeChatServiceImpl extends ServiceImpl<WeChatMapper, WeChatOrderEntity> implements IWeChatService {
    @Autowired
    private WeChatMapper weChatMapper;

    @Override
    public void insertOrder(WeChatOrderEntity msg) {
        weChatMapper.insert(msg);
    }
}