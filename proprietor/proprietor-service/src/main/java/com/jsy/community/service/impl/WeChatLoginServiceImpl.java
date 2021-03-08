package com.jsy.community.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserIMEntity;
import com.jsy.community.entity.UserThirdPlatformEntity;
import com.jsy.community.entity.UserUroraTagsEntity;
import com.jsy.community.mapper.UserIMMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.mapper.UserThirdPlatformMapper;
import com.jsy.community.qo.proprietor.BindingMobileQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-08 10:52
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class WeChatLoginServiceImpl implements IWeChatLoginService {

    private long expire = 60*60*24*7;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserIMMapper userIMMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private IUserUroraTagsService userUroraTagsService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserAuthService userAuthService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserService userService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserThirdPlatformMapper userThirdPlatformMapper;

    @Override
    public CommonResult login(String openid) {
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("third_platform_id", openid).eq("third_platform_type",2));
        if(entity != null&&entity.getUid()!=null){
            //返回token
            UserInfoVo userInfoVo = queryUserInfo(entity.getUid());
            return CommonResult.ok(createAuthVoWithToken(userInfoVo));
        }
        if (entity!=null){
            return CommonResult.error(entity.getId()+"");
        }
        UserThirdPlatformEntity platformEntity = new UserThirdPlatformEntity();
        platformEntity.setThirdPlatformId(openid);
        platformEntity.setThirdPlatformType(2);
        platformEntity.setId(SnowFlake.nextId());
        userThirdPlatformMapper.insert(platformEntity);
        return CommonResult.error(platformEntity.getId()+"");
    }
    @Override
    @Transactional
    public UserAuthVo bindingMobile(BindingMobileQO bindingMobileQO){
        //手机验证码验证 不过报错
        commonService.checkVerifyCode(bindingMobileQO.getMobile(), bindingMobileQO.getCode());
        //查询是否注册
        String uid = userAuthService.queryUserIdByMobile(bindingMobileQO.getMobile());
        //若没有注册 立即注册
        if(StringUtils.isEmpty(uid)){
            RegisterQO registerQO = new RegisterQO();
            registerQO.setAccount(bindingMobileQO.getMobile());
            registerQO.setCode(bindingMobileQO.getCode());
            uid = userService.register(registerQO);
        }
        //三方登录表入库
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectById(bindingMobileQO.getId());
        entity.setUid(uid);
        userThirdPlatformMapper.updateById(entity);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUid(uid);
        return createAuthVoWithToken(userInfoVo);
    }



    /**
     * 创建用户token
     */
    public UserAuthVo createAuthVoWithToken(UserInfoVo userInfoVo){
        Date expireDate = new Date(new Date().getTime() + expire * 1000);
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(LocalDateTimeUtil.of(expireDate));
        userAuthVo.setUserInfo(userInfoVo);
        String token = userUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(userInfoVo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(token);
        return userAuthVo;
    }

    /**
     * 查询用户信息
     */
    private UserInfoVo queryUserInfo(String uid){
        UserEntity user = userMapper.queryUserInfoByUid(uid);
        if (user == null || user.getDeleted() == 1) {
            throw new ProprietorException("账号不存在");
        }
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(user, userInfoVo);
        // 刷新省市区
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
        //查询极光推送标签
        UserUroraTagsEntity userUroraTagsEntity = userUroraTagsService.queryUroraTags(uid);
        if(userUroraTagsEntity != null){
            userInfoVo.setUroraTags(userUroraTagsEntity.getUroraTags());
        }
        //查询用户imId
        UserIMEntity userIMEntity = userIMMapper.selectOne(new QueryWrapper<UserIMEntity>().select("im_id").eq("uid", uid));
        if(userIMEntity != null){
            userInfoVo.setImId(userIMEntity.getImId());
        }
        return userInfoVo;
    }
}
