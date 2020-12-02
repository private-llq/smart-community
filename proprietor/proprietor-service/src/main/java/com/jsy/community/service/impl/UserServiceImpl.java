package com.jsy.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 业主实现
 *
 * @author ling
 * @since 2020-11-11 18:12
 */
@DubboService(version = Const.version, group = Const.group)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserAuthService userAuthService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommonService commonService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService carService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserHouseService userHouseService;

    @Override
    public UserInfoVo login(LoginQO qo) {
        Long uid = userAuthService.checkUser(qo);
        UserEntity user = baseMapper.queryUserInfoByUid(uid);
        if (user.getDeleted() == 1) {
            throw new ProprietorException("账号不存在");
        }

        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtil.copyProperties(user, userInfoVo);

        // 设置省市区
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (user.getProvinceId() != null) {
            userInfoVo.setProvince(ops.get("RegionSingle:" + user.getProvinceId().toString()));
        }
        if (user.getCityId() != null) {
            userInfoVo.setCity(ops.get("RegionSingle:" + user.getCityId().toString()));
        }
        if (user.getAreaId() != null) {
            userInfoVo.setArea(ops.get("RegionSingle:" + user.getAreaId().toString()));
        }

        return userInfoVo;
    }

    @Override
    public UserInfoVo register(RegisterQO qo) {
        commonService.checkVerifyCode(qo.getAccount(), qo.getCode());

        // 添加业主(user表插入一条空数据)
        UserEntity user = new UserEntity();
        save(user);

        // 添加认证数据(user_auth表uid、手机号)
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUid(user.getId());
        if (RegexUtils.isMobile(qo.getAccount())) {
            userAuth.setMobile(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) {
            userAuth.setEmail(qo.getAccount());
        } else {
            userAuth.setUsername(qo.getAccount());
        }
        userAuthService.save(userAuth);

        UserInfoVo vo = new UserInfoVo();
        vo.setId(user.getId());
        vo.setSex(0);
        vo.setIsRealAuth(0);
        return vo;
    }

    /**
     * 页面登记 业主信息
     *
     * @param proprietorQO 登记实体参数
     * @return             返回是否登记成功
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Boolean proprietorRegister(ProprietorQO proprietorQO) {
        //把参数对象里面的值赋值给UserEntity  使用Mybatis plus的insert需要 Entity里面写的表名
        UserEntity userEntity = new UserEntity();
        BeanUtil.copyProperties(proprietorQO, userEntity);
        //添加业主信息 由于在注册时会像t_user表插入一条空记录为用户的id，这里直接做更新操作，
        int count = userMapper.update(userEntity, new UpdateWrapper<UserEntity>().eq("id", proprietorQO.getId()));
        if (count == 0) {
            return false;
        }
        //业主登记时有填写车辆信息的情况下，新增车辆
        if (proprietorQO.getHasCar()) {
            carService.addProprietorCar(proprietorQO.getCarEntityList());
        }
        //t_user_house 中插入当前这条记录 为了让物业审核
		userHouseService.saveUserHouse(userEntity.getId(), userEntity.getHouseEntity().getCommunityId(), userEntity.getHouseEntity().getId());
        return true;
    }


    /**
     * 【用户】业主更新信息
     * @author YuLF
     * @Param proprietorQO        需要更新 实体参数
     * @return 返回更新成功!
     * @since 2020/11/27 15:03
     */
    @Override
    public Integer proprietorUpdate(ProprietorQO proprietorQO) {
        return userMapper.proprietorUpdate(proprietorQO);
    }
}
