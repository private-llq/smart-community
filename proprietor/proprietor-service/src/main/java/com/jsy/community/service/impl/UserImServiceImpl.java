package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserImService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserIMEntity;
import com.jsy.community.mapper.UserIMMapper;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * @program: com.jsy.community
 * @description: im
 * @author: Hu
 * @create: 2021-09-25 15:29
 **/
@DubboService(version = Const.version, group = Const.group)
public class UserImServiceImpl extends ServiceImpl<UserIMMapper, UserIMEntity> implements IUserImService {

    @Autowired
    private UserIMMapper userIMMapper;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService userInfoRpcService;

    @Override
    public List<UserIMEntity> selectUidAll(Set<String> uidAll) {
        return userIMMapper.selectList(new QueryWrapper<UserIMEntity>().in("uid",uidAll));
    }

    @Override
    public UserIMEntity selectUid(String uid) {
        UserIMEntity userIMEntity = new UserIMEntity();
        UserImVo eHomeUserIm = userInfoRpcService.getEHomeUserIm(uid);
        if (eHomeUserIm != null) {
            userIMEntity.setImId(eHomeUserIm.getImId());
        }
//        return userIMMapper.selectOne(new QueryWrapper<UserIMEntity>().eq("uid",uid));
        return userIMEntity;
    }
}
