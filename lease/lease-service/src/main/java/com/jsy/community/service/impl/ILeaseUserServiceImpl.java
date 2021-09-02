package com.jsy.community.service.impl;

import com.jsy.community.api.ILeaseUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.UserIMMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chq459799974
 * @description 租房用户相关服务实现类 暂时写到租房板块里
 * @since 2021-04-21 13:22
 **/
@DubboService(version = Const.version, group = Const.group_lease)
public class ILeaseUserServiceImpl implements ILeaseUserService {

    @Autowired
    private UserIMMapper userIMMapper;

    /**
     * @Description: 用户uid查imID
     * @Param: [uid]
     * @Return: java.lang.String
     * @Author: chq459799974
     * @Date: 2021/4/21
     **/
    @Override
    public String queryIMIdByUid(String uid) {
        return userIMMapper.queryIMIdByUid(uid);
    }
}
