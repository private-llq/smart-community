package com.jsy.community.service.impl;

import com.jsy.community.api.ILeaseUserService;
import com.jsy.community.constant.Const;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author chq459799974
 * @description 租房用户相关服务实现类 暂时写到租房板块里
 * @since 2021-04-21 13:22
 **/
@DubboService(version = Const.version, group = Const.group_lease)
public class ILeaseUserServiceImpl implements ILeaseUserService {

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER)
    private IBaseUserInfoRpcService userInfoRpcService;

    /**
     * @Description: 用户uid查imID
     * @Param: [uid]
     * @Return: java.lang.String
     * @Author: chq459799974
     * @Date: 2021/4/21
     **/
    @Override
    public String queryIMIdByUid(String uid) {
        return userInfoRpcService.getEHomeUserIm(uid).getImId();
    }
}
