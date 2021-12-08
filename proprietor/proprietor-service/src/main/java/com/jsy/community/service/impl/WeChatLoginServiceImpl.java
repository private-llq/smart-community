package com.jsy.community.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.UserIMMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.mapper.UserThirdPlatformMapper;
import com.jsy.community.qo.proprietor.BindingMobileQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.vo.LoginVo;
import com.zhsj.baseweb.support.LoginUser;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @program: com.jsy.community
 * @description: 三方微信登录
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
    private UserIMMapper userIMMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private IUserUroraTagsService userUroraTagsService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserAuthService userAuthService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ProprietorUserService userService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER)
    private IBaseAuthRpcService baseAuthRpcService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserThirdPlatformMapper userThirdPlatformMapper;



    /**
     * @Description: 微信三方登录v2
     * @author: Hu
     * @since: 2021/12/8 16:35
     * @Param: [code]
     * @return: com.jsy.community.vo.UserAuthVo
     */
    @Override
    public UserAuthVo loginV2(String code) {
        UserAuthVo userAuthVo = new UserAuthVo();
        LoginVo loginVo = baseAuthRpcService.weChatLoginEHome(code);
        userAuthVo.setToken(loginVo.getToken().getToken());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(loginVo.getToken().getExpiredTime()/1000, 0, ZoneOffset.ofHours(8));
        userAuthVo.setExpiredTime(localDateTime);
        userAuthVo.setUserInfo(userService.getUserInfoVo(loginVo));
        return userAuthVo;
    }


    /**
     * @Description: 微信三方绑定手机
     * @author: Hu
     * @since: 2021/12/8 16:42
     * @Param: [bindingMobileQO, loginUser]
     * @return: com.jsy.community.vo.UserAuthVo
     */
    @Override
    public UserAuthVo bindingMobileV2(BindingMobileQO bindingMobileQO,LoginUser loginUser) {
        UserAuthVo authVo = new UserAuthVo();
        LoginVo loginVo = baseAuthRpcService.weChatBindPhone(loginUser.getToken(), bindingMobileQO.getMobile(), bindingMobileQO.getCode());
        authVo.setToken(loginVo.getToken().getToken());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(loginVo.getToken().getExpiredTime()/1000, 0, ZoneOffset.ofHours(8));
        authVo.setExpiredTime(localDateTime);
        authVo.setUserInfo(userService.getUserInfoVo(loginVo));
        return authVo;
    }

    /**
     * @Description: 登录
     * @author: Hu
     * @since: 2021/5/21 13:59
     * @Param: [openid]
     * @return: com.jsy.community.vo.UserAuthVo
     */
    @Override
    public UserAuthVo login(String openid) {
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("third_platform_id", openid).eq("third_platform_type",2));
        if(entity != null&&entity.getUid()!=null){
            //返回token
            UserInfoVo userInfoVo = queryUserInfo(entity.getUid());
            userInfoVo.setIdCard(null);
            return createAuthVoWithToken(userInfoVo);
        }
        if (entity!=null){
            UserAuthVo userAuthVo = createBindMobile(entity.getId());
            userAuthVo.setToken("");
            return userAuthVo;
        }
        UserThirdPlatformEntity platformEntity = new UserThirdPlatformEntity();
        platformEntity.setThirdPlatformId(openid);
        platformEntity.setThirdPlatformType(2);
        platformEntity.setId(SnowFlake.nextId());
        userThirdPlatformMapper.insert(platformEntity);
        return createBindMobile(platformEntity.getId());
    }




    /**
     * @Description: 不绑定手机登录
     * @author: Hu
     * @since: 2021/6/7 11:36
     * @Param: [sub]
     * @return: com.jsy.community.vo.UserAuthVo
     */
    @Override
    public UserAuthVo loginNotMobile(String sub) {
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("third_platform_id", sub).eq("third_platform_type",4));
        if(entity != null&&entity.getUid()!=null){
            //返回token
            UserInfoVo userInfoVo = queryUserInfo(entity.getUid());
            userInfoVo.setIdCard(null);
            return createAuthVoWithToken(userInfoVo);
        }
        if (entity != null){
            return createNotBindMobile(entity.getId());
        }
        UserThirdPlatformEntity platformEntity = new UserThirdPlatformEntity();
        platformEntity.setThirdPlatformId(sub);
        platformEntity.setThirdPlatformType(4);
        platformEntity.setId(SnowFlake.nextId());
        userThirdPlatformMapper.insert(platformEntity);
        return createNotBindMobile(platformEntity.getId());
    }
    /**
     * @Description: ios登录
     * @author: Hu
     * @since: 2021/6/1 10:15
     * @Param: [sub]
     * @return: com.jsy.community.vo.UserAuthVo
     */
    @Override
    public UserAuthVo IosLogin(String sub) {
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("third_platform_id", sub).eq("third_platform_type",4));
        if(entity != null&&entity.getUid()!=null){
            //返回token
            UserInfoVo userInfoVo = queryUserInfo(entity.getUid());
            userInfoVo.setIdCard(null);
            return createAuthVoWithToken(userInfoVo);
        }
        if (entity!=null){
            return createBindMobile(entity.getId());
        }
        UserThirdPlatformEntity platformEntity = new UserThirdPlatformEntity();
        platformEntity.setThirdPlatformId(sub);
        platformEntity.setThirdPlatformType(4);
        platformEntity.setId(SnowFlake.nextId());
        userThirdPlatformMapper.insert(platformEntity);
        return createBindMobile(platformEntity.getId());
    }

    /**
     * @Description: 绑定手机
     * @author: Hu
     * @since: 2021/5/21 13:59
     * @Param: [bindingMobileQO]
     * @return: com.jsy.community.vo.UserAuthVo
     */
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
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectById(bindingMobileQO.getThirdPlatformId());
        entity.setUid(uid);
        userThirdPlatformMapper.updateById(entity);
        UserInfoVo userInfoVo = queryUserInfo(uid);
        userInfoVo.setMobile(bindingMobileQO.getMobile());
        userInfoVo.setUid(uid);
        return createAuthVoWithToken(userInfoVo);
    }



    /**
     * @Description: 创建登录所需的UserAuthVo
     * @author: Hu
     * @since: 2021/5/21 14:00
     * @Param:
     * @return:
     */
    public UserAuthVo createAuthVoWithToken(UserInfoVo userInfoVo){
        Date expireDate = new Date(new Date().getTime() + expire * 1000);
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(LocalDateTimeUtil.of(expireDate));
        userInfoVo.setIsBindMobile(1);
        userAuthVo.setUserInfo(userInfoVo);
        String token = UserUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(userInfoVo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(token);
        return userAuthVo;
    }
    /**
     * @Description: 不绑定手机也能登陆的
     * @author: Hu
     * @since: 2021/5/21 14:00
     * @Param:
     * @return:
     */
    public UserAuthVo createNotBindMobile(Long id){
        Date expireDate = new Date(new Date().getTime() + expire * 1000);
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(LocalDateTimeUtil.of(expireDate));
        UserInfoVo vo = new UserInfoVo();
        vo.setThirdPlatformId(id);
        vo.setIsBindMobile(0);
        userAuthVo.setUserInfo(vo);
        String token = UserUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(vo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(token);
        return userAuthVo;
    }

    /**
     * @Description: 返回没有绑定手机的UserAuthVo
     * @author: Hu
     * @since: 2021/5/21 14:00
     * @Param:
     * @return:
     */
    public UserAuthVo createBindMobile(Long id){
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(null);
        UserInfoVo vo = new UserInfoVo();
        vo.setThirdPlatformId(id);
        vo.setIsBindMobile(0);
        userAuthVo.setUserInfo(vo);
//        String token = UserUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(vo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(null);
        return userAuthVo;
    }

    /**
     * 查询用户信息
     */
    private UserInfoVo queryUserInfo(String uid) {
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
        if (userUroraTagsEntity != null) {
            userInfoVo.setUroraTags(userUroraTagsEntity.getUroraTags());
        }
        //查询用户imId
        UserIMEntity userIMEntity = userIMMapper.selectOne(new QueryWrapper<UserIMEntity>().select("im_id,im_password").eq("uid", uid));
        if (userIMEntity != null) {
            userInfoVo.setImId(userIMEntity.getImId());
            userInfoVo.setImPassword(userIMEntity.getImPassword());
        }
        //查询用户是否绑定微信
        UserThirdPlatformEntity platformEntity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>().eq("uid", uid).eq("third_platform_type", 2));
        if (platformEntity != null) {
            userInfoVo.setIsBindWechat(1);
        } else {
            userInfoVo.setIsBindWechat(0);
        }
        //查询用户是否设置支付密码
        UserAuthEntity userAuthEntity = userAuthService.selectByPayPassword(uid);
        if (userAuthEntity != null) {
            if (userAuthEntity.getPayPassword() != null) {
                userInfoVo.setIsBindPayPassword(1);
            } else {
                userInfoVo.setIsBindPayPassword(0);
            }

            if (userAuthEntity.getOpenId() != null) {
                userInfoVo.setIsBindWechat(1);
            } else {
                userInfoVo.setIsBindWechat(0);
            }
        }
        UserThirdPlatformEntity userThirdPlatformEntity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>().eq("uid", uid).eq("third_platform_type", 5));
        if (userThirdPlatformEntity != null) {
            userInfoVo.setIsBindAlipay(1);
        } else {
            userInfoVo.setIsBindAlipay(0);
        }
        return userInfoVo;
    }

}
