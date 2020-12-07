package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.mapper.UserInformMapper;
import com.jsy.community.qo.proprietor.UserInformQO;
import com.jsy.community.vo.UserInformVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通知消息接口实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-12-4
 */
@DubboService(version = Const.version, group = Const.group)
public class UserInformServiceImpl extends ServiceImpl<UserInformMapper, UserInformEntity> implements IUserInformService {

    @Autowired
    private UserInformMapper userInformMapper;
    @Override
    public Map<String, Object> findList(UserInformQO userInformQO) {

        Page<UserInformQO> page = new Page<>(userInformQO.getPage(),userInformQO.getSize());
//        MyPageUtils.setPageAndSize(page,userInformQO);
        Map<String, Object> map = new HashMap<>();
        List<UserInformVO> list = userInformMapper.findList(userInformQO);
        for (UserInformVO userInformVO : list) {
            System.out.println(userInformVO);
        }
        Long total = userInformMapper.findCount(userInformQO);
        map.put("total",total);
        map.put("list",list);
        return map;
    }
}
