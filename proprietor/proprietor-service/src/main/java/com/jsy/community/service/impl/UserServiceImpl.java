package com.jsy.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.config.ProprietorTopicNameEntity;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.face.xu.XUFaceEditPersonDTO;
import com.jsy.community.dto.signature.SignatureUserDTO;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.UserThirdPlatformQO;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.utils.*;
import com.jsy.community.utils.hardware.xu.XUFaceUtil;
import com.jsy.community.utils.CallUtil;
import com.jsy.community.utils.imutils.entity.ImResponseEntity;
import com.jsy.community.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 业主实现
 *
 * @author ling
 * @since 2020-11-11 18:12
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements ProprietorUserService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserUtils userUtils;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserAuthService userAuthService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService carService;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private CommunityMapper communityMapper;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserHouseService userHouseService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IRelationService relationService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private PropertyUserService propertyUserService;

    @Autowired
    private IUserAccountService userAccountService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserThirdPlatformMapper userThirdPlatformMapper;

    @Autowired
    private IUserUroraTagsService userUroraTagsService;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private HouseMemberMapper houseMemberMapper;

    @Autowired
    private ProprietorCommunityService communityService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IAlipayService alipayService;

    @Autowired
    private UserIMMapper userIMMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseInfoService houseInfoService;

    @Value("${H5Url}")
    private String H5Url;

    @Autowired
    private ISignatureService signatureService;

    private long expire = 60 * 60 * 24 * 7; //暂时


    /**
     * @Description: 注册聊天账号
     * @author: Hu
     * @since: 2021/9/9 14:06
     * @Param:
     * @return:
     */
    public static String getImId() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer(10);
        for (int j = 1; j <= 10; j++) {
            int i = r.nextInt(10);
            if (j == 1 || (j >= 8 && j <= 10)) {
                while (i == 0) {
                    i = r.nextInt(10);
                }
            }
            sb.append(i).toString();
        }
        return sb.toString();
    }

    /**
     * 创建用户token
     */
    @Override
    public UserAuthVo createAuthVoWithToken(UserInfoVo userInfoVo) {
        Date expireDate = new Date(new Date().getTime() + expire * 1000);
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(LocalDateTimeUtil.of(expireDate));
        userInfoVo.setIsBindMobile(1);
        userAuthVo.setUserInfo(userInfoVo);
        String token = userUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(userInfoVo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(token);

        return userAuthVo;
    }

    /**
     * 查询用户信息
     */
    @Override
    public UserInfoVo queryUserInfo(String uid) {
        UserEntity user = baseMapper.queryUserInfoByUid(uid);
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

    /**
     * @Description: 更新用户极光ID
     * @Param: [regId, uid]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2021/3/31
     **/
    @Override
    public boolean updateUserRegId(String regId, String uid) {
        return userMapper.updateUserRegId(regId, uid) == 1;
    }

    /**
     * 登录
     */
    @Override
    public UserInfoVo login(LoginQO qo) {
        String uid = userAuthService.checkUser(qo);
        return queryUserInfo(uid);
    }


    /**
     * 注册
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String register(RegisterQO qo) {
//        commonService.checkVerifyCode(qo.getAccount(), qo.getCode());

        String uuid = UserUtils.randomUUID();

        // 组装user表数据
        UserEntity user = new UserEntity();
        user.setUid(uuid);
        user.setAvatarUrl("https://i.postimg.cc/1XgLJbXJ/home.jpg");
        user.setNickname("E-home@" + qo.getAccount());
        user.setId(SnowFlake.nextId());

        // 账户数据(user_auth表)
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUid(uuid);
        userAuth.setId(SnowFlake.nextId());
        if (RegexUtils.isMobile(qo.getAccount())) { //手机注册
            userAuth.setMobile(qo.getAccount());
            user.setMobile(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // 邮箱注册
            userAuth.setEmail(qo.getAccount());
        } else { //用户名注册
            userAuth.setUsername(qo.getAccount());
        }
        try {
            //添加业主(user表)
            save(user);
            //添加账户(user_auth表)
            userAuthService.save(userAuth);
        } catch (DuplicateKeyException e) {
            throw new ProprietorException("该用户已注册");
        }
        //创建金钱账户(t_user_account表)
        boolean userAccountResult = userAccountService.createUserAccount(uuid);
        if (!userAccountResult) {
            log.error("用户账户创建失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //创建极光推送tags(t_user_urora_tags表)
        UserUroraTagsEntity userUroraTagsEntity = new UserUroraTagsEntity();
        userUroraTagsEntity.setUid(uuid);
        userUroraTagsEntity.setCommunityTags("all"); //给所有用户加一个通用tag，用于全体消息推送
        boolean uroraTagsResult = userUroraTagsService.createUroraTags(userUroraTagsEntity);
        if (!uroraTagsResult) {
            log.error("用户极光推送tags设置失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //创建imID(t_user_im表)
        String imId = getImId();
        UserIMEntity userIMEntity = new UserIMEntity();
        userIMEntity.setImPassword("999999999");
        userIMEntity.setUid(uuid);
        userIMEntity.setImId(imId);
        userIMMapper.insert(userIMEntity);
        //调用聊天创建账号
        ImResponseEntity responseEntity = PushInfoUtil.registerUser(userIMEntity.getImId(), userIMEntity.getImPassword(), user.getNickname(), user.getAvatarUrl());
        if (responseEntity.getErr_code() != 0) {
            log.error("；聊天用户创建失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //推送房屋消息
        List<HouseInfoEntity> houseInfoEntities = houseInfoService.selectList(qo.getAccount());
        Map<String, Object> map = new HashMap<>();
        map.put("type", 9);
        if (houseInfoEntities.size() != 0) {
            for (HouseInfoEntity houseInfoEntity : houseInfoEntities) {
                //推送消息
                PushInfoUtil.PushPublicMsg(
                        imId,
                        "房屋管理",
                        houseInfoEntity.getTitle(),
                        H5Url + "?id=" + houseInfoEntity.getId() + "mobile=" + qo.getAccount(),
                        houseInfoEntity.getContent(),
                        map,
                        BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());
            }
        }

        //创建签章用户(远程调用)
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setUuid(uuid);
        signatureUserDTO.setImId(imId);
        if (RegexUtils.isMobile(qo.getAccount())) { //手机注册
            signatureUserDTO.setTelephone(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // 邮箱注册
            signatureUserDTO.setEmail(qo.getAccount());
        } else { //苹果三方登录等无手机号注册
            return uuid;
        }
//        String avatar = ResourceLoadUtil.loadJSONResource("/sys_default_content.json").getString("avatar");
        signatureUserDTO.setImage(user.getAvatarUrl());
        signatureUserDTO.setNickName(user.getNickname());
        boolean signUserResult = signatureService.insertUser(signatureUserDTO);
        if (!signUserResult) {
            log.error("签章用户创建失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        // 同步聊天头像昵称
        CallUtil.updateUserInfo(imId, user.getNickname(), user.getAvatarUrl());
        log.info("同步聊天头像昵称成功：userid -> {},nickName -> {},image -> {}", imId, user.getNickname(), user.getAvatarUrl());

        //如果当前注册用户有房屋，则更新房屋信息
        List<HouseMemberEntity> mobile = houseMemberMapper.selectList(new QueryWrapper<HouseMemberEntity>().eq("mobile", qo.getAccount()).eq("relation",1));
        Set<Long> ids = null;
        Set<Long> houseIds = null;
        if (mobile.size() != 0) {
            ids = new HashSet<>();
            houseIds = new HashSet<>();
            for (HouseMemberEntity houseMemberEntity : mobile) {
                ids.add(houseMemberEntity.getId());
                houseIds.add(houseMemberEntity.getHouseId());
            }
            houseMemberMapper.updateByMobile(ids, uuid);
            userHouseService.updateMobile(houseIds, uuid);
        }

        return uuid;
    }

    /**
     * 注册v2
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String registerV2(RegisterQO qo) {
//        commonService.checkVerifyCode(qo.getAccount(), qo.getCode());

        String uuid = UserUtils.randomUUID();

        // 组装user表数据
        UserEntity user = new UserEntity();
        user.setId(SnowFlake.nextId());
        user.setUid(uuid);
        user.setAvatarUrl("https://i.postimg.cc/1XgLJbXJ/home.jpg");
        user.setNickname("E-home@" + qo.getAccount());
        user.setRealName(qo.getName());

        // 账户数据(user_auth表)
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUid(uuid);
        userAuth.setId(SnowFlake.nextId());
        if (RegexUtils.isMobile(qo.getAccount())) { //手机注册
            userAuth.setMobile(qo.getAccount());
            user.setMobile(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // 邮箱注册
            userAuth.setEmail(qo.getAccount());
        } else { //用户名注册
            userAuth.setUsername(qo.getAccount());
        }
        try {
            //添加业主(user表)
            save(user);

            //添加账户(user_auth表)
            userAuthService.save(userAuth);

        } catch (DuplicateKeyException e) {
            throw new ProprietorException("该用户已注册");
        }
        //创建金钱账户(t_user_account表)
        boolean userAccountResult = userAccountService.createUserAccount(uuid);
        if (!userAccountResult) {
            log.error("用户账户创建失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //创建极光推送tags(t_user_urora_tags表)
        UserUroraTagsEntity userUroraTagsEntity = new UserUroraTagsEntity();
        userUroraTagsEntity.setUid(uuid);
        userUroraTagsEntity.setCommunityTags("all"); //给所有用户加一个通用tag，用于全体消息推送
        boolean uroraTagsResult = userUroraTagsService.createUroraTags(userUroraTagsEntity);
        if (!uroraTagsResult) {
            log.error("用户极光推送tags设置失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //创建imID(t_user_im表)
        String imId = getImId();
        UserIMEntity userIMEntity = new UserIMEntity();
        userIMEntity.setUid(uuid);
        userIMEntity.setImPassword("999999999");
        userIMEntity.setImId(imId);
        userIMMapper.insert(userIMEntity);

        //调用签章创建
        ImResponseEntity responseEntity = PushInfoUtil.registerUser(userIMEntity.getImId(), userIMEntity.getImPassword(), user.getNickname(), user.getAvatarUrl());
        if (responseEntity.getErr_code() != 0) {
            log.error("；聊天用户创建失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //创建签章用户(远程调用)
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setUuid(uuid);
        signatureUserDTO.setImId(imId);
        if (RegexUtils.isMobile(qo.getAccount())) { //手机注册
            signatureUserDTO.setTelephone(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // 邮箱注册
            signatureUserDTO.setEmail(qo.getAccount());
        } else { //苹果三方登录等无手机号注册
            return uuid;
        }
        String avatar = ResourceLoadUtil.loadJSONResource("/sys_default_content.json").getString("avatar");
        signatureUserDTO.setImage(avatar);
        signatureUserDTO.setNickName(user.getNickname());
        boolean signUserResult = signatureService.insertUser(signatureUserDTO);
        if (!signUserResult) {
            log.error("签章用户创建失败，用户创建失败，相关账户：" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        // 同步聊天头像昵称
        CallUtil.updateUserInfo(imId, user.getNickname(), avatar);
        log.info("同步聊天头像昵称成功：userid -> {},nickName -> {},image -> {}", imId, user.getNickname(), avatar);
        return uuid;
    }

    /**
     * 调用三方接口获取会员信息(走后端备用)(返回三方平台唯一id)
     */
    private String getUserInfoFromThirdPlatform(UserThirdPlatformQO userThirdPlatformQO) {
        String thirdUid = null;
        switch (userThirdPlatformQO.getThirdPlatformType()) {
            case Const.ThirdPlatformConsts.ALIPAY:
                //若前端传递了accessToken，尝试取userid
                if (!StringUtils.isEmpty(userThirdPlatformQO.getAccessToken())) {
                    thirdUid = alipayService.getUserid(userThirdPlatformQO.getAccessToken());
                }
                //若前端传递的accessToekn没取到userid，用前端传递的authCode从三方取accessToken再取userid
                if (StringUtils.isEmpty(thirdUid) && !StringUtils.isEmpty(userThirdPlatformQO.getAuthCode())) {
                    String accessToken = alipayService.getAccessToken(userThirdPlatformQO.getAuthCode());
                    if (StringUtils.isEmpty(accessToken)) { //第一步取accessToekn就失败了直接退出
                        break;
                    }
                    thirdUid = alipayService.getUserid(accessToken);
                }
                break;
            case Const.ThirdPlatformConsts.WECHAT:
                break;
            case Const.ThirdPlatformConsts.QQ:
                break;
            default:
        }
        if (StringUtils.isEmpty(thirdUid)) {
            throw new ProprietorException("三方平台uid获取失败 三方登录失败");
        }
        return thirdUid;
    }

    /**
     * @Description: 三方登录
     * @Param: [userThirdPlatformQO]
     * @Return: com.jsy.community.vo.UserAuthVo
     * @Author: chq459799974
     * @Date: 2021/1/12
     **/
    @Override
    public UserAuthVo thirdPlatformLogin(UserThirdPlatformQO userThirdPlatformQO) {
//        Map<String, Object> returnMap = new HashMap<>();
        //获取三方uid
        String thirdPlatformUid = getUserInfoFromThirdPlatform(userThirdPlatformQO);
        userThirdPlatformQO.setThirdPlatformId(thirdPlatformUid);
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("third_platform_id", userThirdPlatformQO.getThirdPlatformId())
                .eq("third_platform_type", userThirdPlatformQO.getThirdPlatformType()));
        if (entity == null) { //首次授权
            UserThirdPlatformEntity userThirdPlatformEntity = new UserThirdPlatformEntity();
            userThirdPlatformEntity.setThirdPlatformType(userThirdPlatformQO.getThirdPlatformType());
            userThirdPlatformEntity.setThirdPlatformId(thirdPlatformUid);
            userThirdPlatformEntity.setId(SnowFlake.nextId());
            userThirdPlatformMapper.insert(userThirdPlatformEntity);
//            returnMap.put("exists",false);
//            returnMap.put("data",userThirdPlatformEntity.getId());
            return createBindMobile(userThirdPlatformEntity.getId());
        } else {
            if (!StringUtils.isEmpty(entity.getUid())) {
                //登录成功
                UserInfoVo userInfoVo = queryUserInfo(entity.getUid());
                UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", entity.getUid()));
                if (userEntity != null) {
                    userInfoVo.setMobile(userEntity.getMobile());
                }
                userInfoVo.setIdCard(null);
                UserAuthVo userAuthVo = createAuthVoWithToken(userInfoVo);
//                returnMap.put("exists",true);
//                returnMap.put("data",userAuthVo);
                return userAuthVo;
            } else {
                //非首次授权，但未绑定手机
//                //继续返回id
//                returnMap.put("exists",false);
//                returnMap.put("data",entity.getId());
                return createBindMobile(entity.getId());
            }
//            return returnMap;
        }
    }

    /**
     * @Description: 三方绑定手机
     * @Param: [userThirdPlatformQO]
     * @Return: com.jsy.community.vo.UserAuthVo
     * @Author: chq459799974
     * @Date: 2021/1/12
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAuthVo bindThirdPlatform(UserThirdPlatformQO userThirdPlatformQO) {
        Long id = Long.valueOf(userThirdPlatformQO.getThirdPlatformId());
        //手机验证码验证 不过报错
        commonService.checkVerifyCode(userThirdPlatformQO.getMobile(), userThirdPlatformQO.getCode());
        //获取三方uid
//        String thirdPlatformUid = getUserInfoFromThirdPlatform(userThirdPlatformQO);
//        userThirdPlatformQO.setThirdPlatformId(thirdPlatformUid);
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>().eq("id", id));
        if (entity == null) {
            throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(), "数据不存在，请重新授权登录");
        }
        userThirdPlatformQO.setThirdPlatformId(entity.getThirdPlatformId());
        //查询是否注册
        String uid = userAuthService.queryUserIdByMobile(userThirdPlatformQO.getMobile());
        //若没有注册 立即注册
        if (StringUtils.isEmpty(uid)) {
            RegisterQO registerQO = new RegisterQO();
            registerQO.setAccount(userThirdPlatformQO.getMobile());
            registerQO.setCode(userThirdPlatformQO.getCode());
            uid = register(registerQO);
            //三方登录表入库
            UserThirdPlatformEntity userThirdPlatformEntity = new UserThirdPlatformEntity();
            BeanUtils.copyProperties(userThirdPlatformQO, userThirdPlatformEntity);
            userThirdPlatformEntity.setId(SnowFlake.nextId());
            userThirdPlatformEntity.setUid(uid);//把uid设置进三方登录表关联上
            userThirdPlatformMapper.insert(userThirdPlatformEntity);
        } else {
            //更新uid
            UserThirdPlatformEntity userThirdPlatformEntity = new UserThirdPlatformEntity();
            userThirdPlatformEntity.setUid(uid);
            userThirdPlatformMapper.update(userThirdPlatformEntity, new UpdateWrapper<UserThirdPlatformEntity>().eq("id", id));
        }
//        UserInfoVo userInfoVo = new UserInfoVo();
//        userInfoVo.setUid(uid);
        //登录成功
        UserInfoVo userInfoVo = queryUserInfo(uid);
        userInfoVo.setMobile(userThirdPlatformQO.getMobile());
        userInfoVo.setIdCard(null);
        return createAuthVoWithToken(userInfoVo);
    }

    public UserAuthVo createBindMobile(Long id) {
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(null);
        UserInfoVo vo = new UserInfoVo();
        vo.setThirdPlatformId(id);
        vo.setIsBindMobile(0);
        userAuthVo.setUserInfo(vo);
//        String token = userUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(vo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(null);
        return userAuthVo;
    }


    /**
     * 添加社区硬件权限
     */
    private void setCommunityHardwareAuth(ProprietorQO proprietorQO) {
        //TODO 根据uid查询所有房屋已审核社区 or 一个小区一次认证
//        List<Long> communityIds = xxxxxx.getUserCommunitys(proprietorQO.getUid());
        //TODO 获取对应社区的硬件服务器id、地址等相关数据 待设计，确认业务登记操作需要增加的权限

        //执行调用 目前仅测试人脸机器
        XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
        xuFaceEditPersonDTO.setCustomId(proprietorQO.getIdCard());
        xuFaceEditPersonDTO.setName(proprietorQO.getRealName());
        xuFaceEditPersonDTO.setGender(proprietorQO.getSex());
        xuFaceEditPersonDTO.setPic(Base64Util.netFileToBase64(proprietorQO.getFaceUrl()));
        XUFaceUtil.editPerson(xuFaceEditPersonDTO);
    }


    /**
     * 【用户】业主更新房屋认证信息  id等于null 或者 id == 0 表示需要新增的数据
     * 新方法至 -> updateImprover
     *
     * @author YuLF
     * @Param proprietorQO        需要更新 实体参数
     * @since 2020/11/27 15:03
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @Deprecated
    public Boolean proprietorUpdate(ProprietorQO qo) {
        updateCar(qo);
        //========================================== 业主房屋 =========================================================
        List<UserHouseQo> houseList = qo.getHouses();
        //验证房屋信息是否有需要新增的数据
        boolean hasAddHouse = houseList.stream().anyMatch(w -> w.getId() == null || w.getId() == 0);
        //如果存在需要新增的房屋的数据
        if (hasAddHouse) {
            List<UserHouseQo> any = houseList.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
            houseList.removeAll(any);
            //批量新增房屋信息
            userHouseService.addHouseBatch(any, qo.getUid());
        }
        //批量更新房屋信息
        houseList.forEach(h -> userHouseService.update(h, qo.getUid()));
        //========================================== 业主 =========================================================
        //业主信息更新 userMapper.proprietorUpdate(qo)
        //通常 已经到这一步说明上面的任务没有抛出任何异常 返回值  为冗余字段
        return true;
    }


    /**
     * 更新车辆信息 如果 id = 0 | id = null 则新增
     *
     * @param qo 请求参数
     */
    private void updateCar(ProprietorQO qo) {
        //========================================== 业主车辆 =========================================================
        //如果有车辆的情况下 更新或新增 车辆信息
        if (qo.getHasCar()) {
            //如果有需要新增的数据 id == null 或者 == 0 就是需要新增 只要有一个条件满足即返回true
            List<CarQO> cars = qo.getCars();
            //用户是否有需要新增的车辆？默认为false
            AtomicBoolean hasAddCar = new AtomicBoolean(false);
            cars.forEach(e -> {
                //如果参数id为null 或者 参数id为0 则表明这条数据是需要新增
                if (e.getId() == null || e.getId() == 0) {
                    //新增数据需要设置id
                    hasAddCar.set(true);
                }
            });
            //新增车辆信息
            if (hasAddCar.get()) {
                //从提交的List中取出Id == null 并且ID == 0的数据 重新组成一个List 代表需要新增的数据
                List<CarQO> any = cars.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
                //从更新车辆的集合中 移除 需要 新增的数据
                cars.removeAll(any);
                //循环添加id
                for (CarQO carQO : any) {
                    carQO.setId(SnowFlake.nextId());
                }
                //批量新增车辆
                carService.addProprietorCarForList(any, qo.getUid());
                //循环保存车辆到es

                rabbitTemplate.convertAndSend("exchange_car_topics", "queue.car.insert", getInsertElasticsearchCar(qo.getUid(), qo.getHouses(), any));

            }
            //批量更新车辆信息
            cars.forEach(c -> {
                        carService.update(c, qo.getUid());

                    }
            );
            if (cars != null && cars.size() != 0) {
                rabbitTemplate.convertAndSend("exchange_car_topics", "queue.car.update", getUpdateElasticsearchCar(cars));
            }
        }
    }

    /**
     * @Description: 添加车辆到es的qo封装方法
     * @author: Hu
     * @since: 2021/3/26 13:43
     * @Param:
     * @return:
     */
    public List<ElasticsearchCarQO> getInsertElasticsearchCar(String uid, List<UserHouseQo> qo, List<CarQO> carQO) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", uid));
        HouseEntity entity = houseService.getById(qo.get(0).getHouseId());
        LinkedList<ElasticsearchCarQO> list = new LinkedList<>();
        ElasticsearchCarQO elasticsearchCarQO = null;
        for (CarQO car : carQO) {
            elasticsearchCarQO = new ElasticsearchCarQO();
            elasticsearchCarQO.setId(car.getId());
            elasticsearchCarQO.setCommunityId(qo.get(0).getCommunityId());
            elasticsearchCarQO.setCarPlate(car.getCarPlate());
            elasticsearchCarQO.setCarType(car.getCarType());
            elasticsearchCarQO.setCarTypeText(BusinessEnum.CarTypeEnum.getCode(car.getCarType()));
            elasticsearchCarQO.setOwner(userEntity.getRealName());
            elasticsearchCarQO.setIdCard(userEntity.getIdCard());
            elasticsearchCarQO.setMobile(userEntity.getMobile());
            elasticsearchCarQO.setOwnerType(1);
            elasticsearchCarQO.setOwnerTypeText("用户");
            elasticsearchCarQO.setRelationshipId(userEntity.getUid());
            elasticsearchCarQO.setHouseId(qo.get(0).getHouseId());
            elasticsearchCarQO.setBuilding(entity.getBuilding());
            elasticsearchCarQO.setFloor(String.valueOf(entity.getFloor()));
            elasticsearchCarQO.setUnit(entity.getUnit());
            elasticsearchCarQO.setNumber(entity.getNumber());
            elasticsearchCarQO.setHouseType(entity.getHouseType());
            elasticsearchCarQO.setHouseTypeText(entity.getHouseType() == 1 ? "商铺" : "住宅");
            elasticsearchCarQO.setCreateTime(LocalDateTime.now());
            list.add(elasticsearchCarQO);
        }
        return list;
    }

    /**
     * @Description: 修改车辆到es的qo封装方法
     * @author: Hu
     * @since: 2021/3/26 13:43
     * @Param:
     * @return:
     */
    public List<ElasticsearchCarQO> getUpdateElasticsearchCar(List<CarQO> carQO) {
        ElasticsearchCarQO elasticsearchCarQO = null;
        LinkedList<ElasticsearchCarQO> list = new LinkedList<>();
        for (CarQO qo : carQO) {
            elasticsearchCarQO = new ElasticsearchCarQO();
            elasticsearchCarQO.setId(qo.getId());
            elasticsearchCarQO.setCarPlate(qo.getCarPlate());
            elasticsearchCarQO.setCarType(qo.getCarType());
            elasticsearchCarQO.setCarTypeText(BusinessEnum.CarTypeEnum.getCode(qo.getCarType()));
            list.add(elasticsearchCarQO);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateImprover(ProprietorQO qo) {
        //========================================== 1.业主房屋 =========================================================
        List<UserHouseQo> houseList = qo.getHouses();

        List<UserHouseQo> any = null;

        UserEntity userEntity = null;
        //用户提交的房屋信息不为空
        if (CollectionUtil.isNotEmpty(houseList)) {
            //用来找用户提交的房屋信息 和 物业用户所属的房屋信息 差集的集合
            List<UserHouseQo> tmpQos = new ArrayList<>(houseList);
            log.error("入参houseList" + houseList);
            //1.通过uid拿到业主的身份证
            userEntity = queryUserDetailByUid(qo.getUid());
            //通过 业主身份证 拿到 业主表的 该业主的所有 房屋id + 社区id
            List<UserHouseQo> resHouseList = userMapper.getProprietorInfo(userEntity.getIdCard());
            if (CollectionUtil.isEmpty(resHouseList)) {
                throw new ProprietorException("物业没有添加您的房屋信息!");
            }
            //取出 业主房屋信息 和 物业信息的差异集合  如果 differenceList 不为空 业主提交了 物业没有认证的房屋信息
            //剔除带了id的数据，带id目的是修改，差集只对比新增数据
            ArrayList<UserHouseQo> forInsert = new ArrayList<>();
            for (UserHouseQo userHouseQo : tmpQos) {
                if (userHouseQo.getId() == null || userHouseQo.getId() == 0) {
                    forInsert.add(userHouseQo);
                }
            }
            List<UserHouseQo> differenceList = differenceSet(forInsert, resHouseList);
            log.error("差集" + differenceList);
            log.error("差集size：" + differenceList.size());
            if (CollectionUtil.isEmpty(differenceList)) {
                //操作用户所在房屋
                //id==null || id == 0 就是需要新增的 验证房屋信息是否有需要新增的数据
                any = houseList.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(any)) {
                    houseList.removeAll(any);
                    //批量新增房屋信息
                    userHouseService.addHouseBatch(any, qo.getUid());
                }
                //批量更新房屋信息
                houseList.forEach(h -> userHouseService.update(h, qo.getUid()));
            } else {
                //存在差集 用户提交了 在业主表中他没有的房子
                List<String> houseAddressNames = differenceList.stream().map(UserHouseQo::getHouseAddress).collect(Collectors.toList());
                throw new ProprietorException("物业没有给您认证该房屋：" + houseAddressNames);
            }
        }
        updateCar(qo);
        //没有添加房屋
        if (CollectionUtils.isEmpty(any)) {
            return true;
        }
        Set<Long> communityIds = new HashSet<>();
        for (UserHouseQo userHouseQo : any) {
            communityIds.add(userHouseQo.getCommunityId());
        }
        //下发人脸数据
        setFaceUrlToCommunityMachine(userEntity, communityIds);
        return true;
    }

    /**
     * 向小区设备下发人脸数据
     */
    private void setFaceUrlToCommunityMachine(UserEntity userEntity, Set<Long> communityIds) {
        //查询用户实名信息
        if (userEntity.getIsRealAuth() == null || userEntity.getIsRealAuth() != 2) {
            // 用户实名认证未人脸认证 放弃后续操作
            return;
        }
        if (StringUtils.isEmpty(userEntity.getFaceUrl())) {
            //数据异常，停止房屋相关操作
            throw new ProprietorException("用户已实人认证，人脸图片不存在！");
        }
        // todo 已经不适配小区项目的新代码逻辑了,当这个业务重整的时候,可以适配修改这里的代码
        JSONObject pushMap = new JSONObject();
        pushMap.put("operator", "editPerson");
        pushMap.put("uid", userEntity.getUid());
        pushMap.put("faceUrl", userEntity.getFaceUrl());
        pushMap.put("communityIdSet", communityIds);

        pushMap.put("sex", userEntity.getSex());
        pushMap.put("realName", userEntity.getRealName());
        rabbitTemplate.convertAndSend(ProprietorTopicNameEntity.exFaceXu, ProprietorTopicNameEntity.topicFaceXuServer, pushMap);
    }


    /**
     * 根据两个集合的两个属性 对比 差异 并取出 有差异的对象
     * 通过 社区 id 和 房屋id 对比两个集合中的差集
     *
     * @param source 源集合
     * @param target 目标集合
     * @return 返回差异集合
     */
    private static List<UserHouseQo> differenceSet(List<UserHouseQo> source, List<UserHouseQo> target) {
        List<UserHouseQo> list = new ArrayList<>(8);
        source.forEach(so -> {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            for (UserHouseQo ta : target) {
                if (Objects.equals(so.getCommunityId(), ta.getCommunityId()) && Objects.equals(so.getHouseId(), ta.getHouseId())) {
                    atomicBoolean.set(true);
                    break;
                }
            }
            if (atomicBoolean.get() == Boolean.FALSE) {
                list.add(so);
            }
        });
        return list;
    }

    /**
     * 根据业主id查询业主信息及业主家属信息
     *
     * @author YuLF
     * @since 2020/12/10 16:25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVo proprietorQuery(String userId, Long houseId) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId).select("real_name,sex,detail_address,avatar_url"));
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtil.copyProperties(userEntity, userInfoVo);
        //业主家属查询
        List<RelationVO> houseMemberEntities = relationService.selectID(userId, houseId);
        userInfoVo.setProprietorMembers(houseMemberEntities);
        //业主房屋查询
        List<HouseVo> houseVos = userMapper.queryUserHouseById(userId, houseId);
        userInfoVo.setProprietorHouses(houseVos);
        return userInfoVo;
    }

    /**
     * @Description: 查询用户社区(房屋已认证的)
     * @Param: [uid]
     * @Return: java.util.Collection<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author: chq459799974
     * @Date: 2021/3/31
     **/
    @Override
    public Collection<Map<String, Object>> queryUserHousesOfCommunity(String uid) {
        //查所有房屋已认证的小区
        Set<Long> communityIds = userHouseService.queryUserHousesOfCommunityIds(uid);
        if (CollectionUtils.isEmpty(communityIds)) {
            return null;
        }
        //查小区名称
        Map<String, Map<String, Object>> communityIdAndName = communityService.queryCommunityNameByIdBatch(communityIds);
        return communityIdAndName.values();
    }

    @Override
    public UserEntity getUser(String tenantUid) {
        return userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", tenantUid));
    }

    /**
     * @Description: 查询用户社区(家属租客的)
     * @Param: [uid]
     * @Return: java.util.Collection<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author: chq459799974
     * @Date: 2021/3/31
     **/
    @Override
    public Collection<Map<String, Object>> queryRelationHousesOfCommunity(String uid) {
        //查所有房屋已认证的小区
        Set<Long> communityIds = userHouseService.queryRelationHousesOfCommunityIds(uid);
        if (CollectionUtils.isEmpty(communityIds)) {
            return null;
        }
        //查小区名称
        Map<String, Map<String, Object>> communityIdAndName = communityService.queryCommunityNameByIdBatch(communityIds);
        return communityIdAndName.values();
    }

    @Override
    public Collection<Map<String, Object>> queryCommunityUserList(String uid) {
        //查所有房屋已认证的小区
        Set<Long> communityIds = userHouseService.queryUserHousesOfCommunityIds(uid);
        if (CollectionUtils.isEmpty(communityIds)) {
            return null;
        }
        //查小区名称
        Map<String, Map<String, Object>> communityIdAndName = communityService.queryCommunityNameByIdBatch(communityIds);
        return communityIdAndName.values();
    }

    /**
     * @Description: 查询业主所有小区的房屋
     * @Param: [uid]
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Author: chq459799974
     * @Date: 2020/12/16
     **/
    @Override
    public List<HouseEntity> queryUserHouseList(String uid) {

        //步骤一
        /* t_user_house */
        List<UserHouseEntity> userHouseList = userHouseService.queryUserHouses(uid);
        if (CollectionUtils.isEmpty(userHouseList)) {
            return null;
        }
        HashSet<Long> communityIdSet = new HashSet<>();
        LinkedList<Long> houseIdList = new LinkedList<>();
        for (UserHouseEntity userHouseEntity : userHouseList) {
            communityIdSet.add(userHouseEntity.getCommunityId());
            houseIdList.add(userHouseEntity.getHouseId());
        }

        //步骤二
        //查社区id,房间id,楼栋id,地址拼接
        //补buildingId如果pid!=0 继续往上查
        /* t_house */
        List<HouseEntity> houses = houseService.queryHouses(houseIdList);
        //组装buildingId
        for (HouseEntity tempEntity : houses) {
            //递归查父节点，组装楼栋级节点id进buildingId
            setBuildingId(tempEntity);
        }

        //步骤三
        //查小区名、业主姓名
        /* t_community *//* t_user */
        Map<String, Map<String, Object>> communityMap = communityService.queryCommunityNameByIdBatch(communityIdSet);
        UserInfoVo userInfoVo = userMapper.selectUserInfoById(uid);
        for (HouseEntity userHouseEntity : houses) {
            Map<String, Object> map = communityMap.get(BigInteger.valueOf(userHouseEntity.getCommunityId()));
            userHouseEntity.setCommunityName(map == null ? null : String.valueOf(map.get("name")));
            userHouseEntity.setOwner(userInfoVo.getRealName());
        }
        return houses;
    }


    /**
     * @Description: 解除微信绑定
     * @author: Hu
     * @since: 2021/10/18 10:57
     * @Param: [registerQO, userId]
     * @return: void
     */
    @Override
    @Transactional
    public void relieveBindingWechat(RegisterQO registerQO, String userId) {
        UserAuthEntity authEntity = userAuthService.selectByIsWeChat(userId);
        if (authEntity != null) {
            if (StringUtils.isEmpty(authEntity.getOpenId())) {
                throw new ProprietorException("当前用户并未绑定微信！");
            }

            //删除微信三方登录绑定
            userThirdPlatformMapper.delete(new QueryWrapper<UserThirdPlatformEntity>().eq("third_platform_id", authEntity.getOpenId()));

            //设置userAuth表的openId为null
            userAuthService.updateByOpenId(authEntity.getId());
        }
    }

    /**
     * @Description: 用户绑定微信
     * @author: Hu
     * @since: 2021/10/15 10:05
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public String bindingWechat(String userId, String openid) {
        UserAuthEntity userAuthEntity = userAuthService.selectByIsWeChat(userId);
        if (userAuthEntity != null) {
            if (userAuthEntity.getOpenId() != null) {
                throw new ProprietorException("当前用户已绑定微信！");
            }
            //设置用户openid
            userAuthEntity.setOpenId(openid);
            userAuthService.updateByWechat(userAuthEntity);
            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));

            return userEntity.getRealName();
        }
        return null;
    }

    /**
     * @Description: 查询当前用户所有身份的房屋信息
     * @author: Hu
     * @since: 2021/9/29 16:08
     * @Param: [userId, permissions]
     * @return: java.util.List<com.jsy.community.entity.HouseEntity>
     */
    @Override
    public List<HouseEntity> queryUserHouseListAll(String userId) {
        //步骤一
        /* t_user_house */
        List<HouseMemberEntity> entityList = houseMemberMapper.selectList(new QueryWrapper<HouseMemberEntity>().eq("uid", userId));
        if (entityList.size() == 0) {
            return null;
        }
        HashSet<Long> communityIdSet = new HashSet<>();
        LinkedList<Long> houseIdList = new LinkedList<>();
        for (HouseMemberEntity permission : entityList) {
            communityIdSet.add(permission.getCommunityId());
            houseIdList.add(permission.getHouseId());
        }

        //步骤二
        //查社区id,房间id,楼栋id,地址拼接
        //补buildingId如果pid!=0
        /* t_house */
        List<HouseEntity> houses = houseService.queryHouses(houseIdList);
        //组装buildingId
        for (HouseEntity tempEntity : houses) {
            //递归查父节点，组装楼栋级节点id进buildingId
            setBuildingId(tempEntity);
        }

        //步骤三
        //查小区名、业主姓名
        /* t_community *//* t_user */
        Map<String, Map<String, Object>> communityMap = communityService.queryCommunityNameByIdBatch(communityIdSet);
        UserInfoVo userInfoVo = userMapper.selectUserInfoById(userId);
        for (HouseEntity userHouseEntity : houses) {
            Map<String, Object> map = communityMap.get(BigInteger.valueOf(userHouseEntity.getCommunityId()));
            userHouseEntity.setCommunityName(map == null ? null : String.valueOf(map.get("name")));
            userHouseEntity.setOwner(userInfoVo.getRealName());
        }
        return houses;
    }

    private HouseEntity setBuildingId(HouseEntity tempEntity) {
        Long pid = 0L; //id和pid相同的问题数据导致死循环
        HouseEntity parent = houseService.getParent(tempEntity);
        if (parent != null && parent.getPid() != null && parent.getPid() != 0 && pid != parent.getPid()) {
            pid = tempEntity.getPid();
            HouseEntity houseEntity = setBuildingId(parent);
            tempEntity.setBuildingId(houseEntity.getBuildingId());
        } else {
            tempEntity.setBuildingId(tempEntity.getPid());
        }
        return parent;
    }

    /**
     * 业主详情查看
     *
     * @param userId 用户ID
     * @return 返回业主详情信息
     * @Param houseId       房屋ID
     * @author YuLF
     * @since 2020/12/18 11:39
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVo proprietorDetails(String userId) {
        //1.查出用户姓名信息
        UserInfoVo userInfo = userMapper.selectUserInfoById(userId);
        //2.查出用户房屋信息
        List<HouseVo> userHouses = userHouseService.queryUserHouseList(userId);
        //3.查出用户家属
        //List<HouseMemberEntity> houseMemberEntities = relationService.selectID(userId, houseId);
        //4.查出用户车辆信息
        List<CarEntity> carEntities = carService.queryUserCarById(userId);
        userInfo.setProprietorCars(carEntities);
        userInfo.setProprietorHouses(userHouses);
        //userInfo.setProprietorMembers(houseMemberEntities);
        return userInfo;
    }

    /**
     * @Description: 小区业主/家属获取门禁权限
     * @Param: [uid, communityId]
     * @Return: java.util.Map<java.lang.String, java.lang.String>
     * @Author: chq459799974
     * @Date: 2020/12/23
     **/
    @Override
    public Map<String, String> getAccess(String uid, Long communityId) {
        Map<String, String> returnMap = new HashMap<>();
        //获取登录用户手机号
        String mobile = userMapper.queryUserMobileByUid(uid);
        //检查身份
        if (canGetLongAccess(uid, communityId, mobile)) {
            //刷新通用权限并返回最新数据 //TODO 目前是返回一个token 后期根据硬件接口需要修改
            String access = setUserLongAccess(uid);
            returnMap.put("access", access);
        } else {
            returnMap.put("msg", "当前用户不是小区业主或家属");
        }
        return returnMap;
    }

    //查询身份(是不是小区业主或家属)
    private boolean canGetLongAccess(String uid, Long communityId, String mobile) {
        if (userHouseService.hasHouse(uid, communityId)
                || relationService.isHouseMember(mobile, communityId)) {
            return true;
        }
        return false;
    }

    //设置不过期门禁
    private String setUserLongAccess(String uid) {
        String token = UUID.randomUUID().toString().replace("-", "");
        VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
        visitorEntryVO.setToken(token);
        redisTemplate.opsForValue().set("UEntry:" + uid, JSON.toJSONString(visitorEntryVO));
        return token;
    }

    /**
     * @Description: 查询用户是否存在
     * @Param: [uid]
     * @Return: java.util.Map<java.lang.String, java.lang.Object>
     * @Author: chq459799974
     * @Date: 2021/1/13
     **/
    @Override
    public Map<String, Object> checkUserAndGetUid(String uid) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(uid)) {
            map.put("exists", false);
            return map;
        }
        Integer count = userMapper.selectCount(new QueryWrapper<UserEntity>().eq("uid", uid));
        if (count == 1) {
            map.put("exists", true);
            map.put("uid", uid);
        } else {
            map.put("exists", false);
        }
        return map;
    }

    /**
     * @Description: 单表查询用户信息
     * @Param: [uid]
     * @Return: com.jsy.community.entity.UserEntity
     * @Author: chq459799974
     * @Date: 2021/1/20
     **/
    @Override
    public UserEntity queryUserDetailByUid(String uid) {
        return userMapper.selectOne(new QueryWrapper<UserEntity>().select("*").eq("uid", uid));
    }


    @Override
    public UserEntity getRealAuthAndHouseId(String uid) {
        UserEntity userEntity = new UserEntity();
        userEntity.setIsRealAuth(userMapper.getRealAuthStatus(uid));
        //拿到用户的最新房屋id
        userEntity.setHouseId(userMapper.getLatestHouseId(uid));
        return userEntity;
    }

    @Override
    public UserInfoVo getUserAndMemberInfo(String uid, Long houseId) {
        //1.查出用户姓名、用户性别、
        UserInfoVo userInfo = userMapper.selectUserNameAndHouseAddr(uid);
        //2.拿到房屋地址组成的字符串 如两江新区幸福广场天王星B栋1801 和 房屋所在社区id
        UserInfoVo userInfoVo = userMapper.selectHouseAddr(houseId);
        if (Objects.nonNull(userInfoVo)) {
            userInfo.setDetailAddress(userInfoVo.getDetailAddress());
            userInfo.setCommunityId(userInfoVo.getCommunityId());
            userInfo.setHouseId(houseId);
        }
        List<RelationVO> houseMemberEntities = relationService.selectID(uid, houseId);
        userInfo.setProprietorMembers(houseMemberEntities);
        return userInfo;
    }

    /**
     * 新方法：proprietorDetails
     *
     * @param communityId 社区id
     * @param houseId     房屋id
     * @param userId      用户id
     * @return 返回用户详情信息
     */
    @Override
    @Deprecated
    public UserInfoVo userInfoDetails(Long communityId, Long houseId, String userId) {
        UserInfoVo vo = new UserInfoVo();
        //1.根据房屋id查出当前房屋信息：
        HouseVo houseVo = userMapper.getHouseInfoById(houseId);
        vo.setProprietorHouses(Collections.singletonList(houseVo));
        //2.根据社区id和用户id查出所有的车辆信息：车牌、车辆类型、行驶证图片
        List<CarEntity> carEntities = carService.getAllCarById(communityId, userId);
        vo.setProprietorCars(carEntities);
        return vo;
    }

    /**
     * @Description: 实名认证后修改用户信息
     * @Param: [userEntity]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/3/2
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserAfterRealnameAuth(UserEntity userEntity) {
        //改本地库
        int result = userMapper.update(userEntity, new UpdateWrapper<UserEntity>().eq("uid", userEntity.getUid()));
        if (result != 1) {
            log.error("实名信息修改失败，用户：" + userEntity.getUid());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //通知签章服务
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setUuid(userEntity.getUid());
        signatureUserDTO.setIdCardName(userEntity.getRealName());
        signatureUserDTO.setIdCardNumber(userEntity.getIdCard());
        signatureUserDTO.setIdCardAddress(userEntity.getDetailAddress());
        if (!StringUtils.isEmpty(userEntity.getAvatarUrl())) {
            signatureUserDTO.setImage(userEntity.getAvatarUrl());
        }
        if (!StringUtils.isEmpty(userEntity.getNickname())) {
            signatureUserDTO.setNickName(userEntity.getNickname());
        }
        boolean b = signatureService.realNameUpdateUser(signatureUserDTO);
        //根据手机更新成员表uid
        userHouseService.updateMobileUser(userEntity.getUid());
        if (!b) {
            log.error("签章用户实名同步失败，用户：" + userEntity.getUid());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        // 同步聊天信息
        List<String> strings = userIMMapper.selectByUid(Collections.singleton(userEntity.getUid()));
        if (strings.size() > 0) {
            if (!StringUtils.isEmpty(userEntity.getAvatarUrl()) || !StringUtils.isEmpty(userEntity.getNickname())) {
                CallUtil.updateUserInfo(strings.get(0), userEntity.getNickname(), userEntity.getAvatarUrl());
                log.info("同步聊天头像昵称成功：userid -> {},nickName -> {},image -> {}",
                        strings.get(0), userEntity.getNickname(), userEntity.getAvatarUrl());
            }
        }
    }

    /**
     * @Description: uids批量查询 uid-姓名映射
     * @Param: [uids]
     * @Return: java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.lang.String>>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    @Override
    public Map<String, Map<String, String>> queryNameByUidBatch(Collection<String> uids) {
        if (CollectionUtils.isEmpty(uids) || (uids.size() == 1 && uids.contains(null))) {
            return new HashMap<>();
        }
        return userMapper.queryNameByUidBatch(uids);
    }


    @Override
    public void deleteCar(String userId, Long id) {
        carMapper.delete(new QueryWrapper<CarEntity>().eq("uid", userId).eq("id", id));
        rabbitTemplate.convertAndSend("exchange_car_topics", "queue.car.delete", id);
    }

    /**
     * @Description: 在固定的uid范围内筛选姓名满足模糊匹配条件的uid
     * @Param: [uids, nameLike]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    @Override
    public List<String> queryUidOfNameLike(List<String> uids, String nameLike) {
        return userMapper.queryUidOfNameLike(uids, nameLike);
    }

    /**
     * @Description: 删除业主人脸
     * @author: Hu
     * @since: 2021/8/24 16:59
     * @Param: [userId]
     * @return: void
     */
    @Override
    public void deleteFaceAvatar(String userId) {
        userMapper.deleteFaceAvatar(userId);
    }


    //保存人脸
    @Override
    @Transactional
    public void saveFace(String userId, String faceUrl) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));
        if (userEntity != null) {
            userEntity.setFaceUrl(faceUrl);
            userMapper.updateById(userEntity);
            if (userEntity.getFaceEnableStatus() == 1) {
                // 修改人脸之后,查询相关社区及相关社区设备列表
                QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userId);
                List<HouseMemberEntity> houseMemberEntities = houseMemberMapper.selectList(queryWrapper);
                if (!CollectionUtils.isEmpty(houseMemberEntities)) {
                    List<Long> communityIds = houseMemberEntities.stream().map(HouseMemberEntity::getCommunityId).collect(Collectors.toList());
                    // 然后同步人脸
                    propertyUserService.saveFace(userEntity, communityIds);
                }
            }
        }
    }

    @Override
    public String getFace(String userId) {
        UserEntity entity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));
        if (entity != null) {
            return entity.getFaceUrl();
        }
        throw new ProprietorException("当前用户不存在！");
    }

    @Override
    public Integer userIsRealAuth(String userId) {
        return userMapper.getRealAuthStatus(userId);
    }

    /**
     * @Description: 获取当前登录用户权限
     * @author: Hu
     * @since: 2021/8/16 15:14
     * @Param: [communityId, uid]
     * @return: void
     */
    @Override
    public ControlVO control(Long communityId, String uid) {
        ControlVO controlVO = new ControlVO();
        ControlVO control = null;
        List<UserHouseEntity> list = userHouseService.selectUserHouse(communityId, uid);
        List<HouseMemberEntity> memberEntities = houseMemberMapper.selectList(new QueryWrapper<HouseMemberEntity>().eq("community_id", communityId).eq("uid", uid));

        if (uid.equals("00000tourist")) {
            controlVO.setAccessLevel(5);
            controlVO.setCommunityId(communityId);
            controlVO.setHouseId(null);
            return controlVO;
        }

        if (list.size() != 0) {
            UserHouseEntity entity = list.get(0);
            controlVO.setAccessLevel(1);
            controlVO.setCommunityId(entity.getCommunityId());
            controlVO.setHouseId(entity.getHouseId());
            if (memberEntities != null) {
                for (HouseMemberEntity memberEntity : memberEntities) {
                    if (memberEntity.getRelation() == 1) {
                        control = new ControlVO();
                        control.setAccessLevel(1);
                        control.setCommunityId(memberEntity.getCommunityId());
                        control.setHouseId(memberEntity.getHouseId());
                        controlVO.getPermissions().add(control);
                    }
                    if (memberEntity.getRelation() == 6) {
                        control = new ControlVO();
                        control.setAccessLevel(2);
                        control.setCommunityId(memberEntity.getCommunityId());
                        control.setHouseId(memberEntity.getHouseId());
                        controlVO.getPermissions().add(control);
                    }
                    if (memberEntity.getRelation() == 7) {
                        control = new ControlVO();
                        control.setAccessLevel(3);
                        control.setCommunityId(memberEntity.getCommunityId());
                        control.setHouseId(memberEntity.getHouseId());
                        controlVO.getPermissions().add(control);
                    }
                }
            }
            return controlVO;
        } else {
            if (memberEntities.size() != 0) {
                //查询当前角色最高权限
                for (HouseMemberEntity memberEntity : memberEntities) {
                    if (memberEntity.getRelation() == 6) {
                        controlVO.setAccessLevel(2);
                        controlVO.setCommunityId(memberEntity.getCommunityId());
                        controlVO.setHouseId(memberEntity.getHouseId());
                        for (HouseMemberEntity userHouseEntity : memberEntities) {
                            control = new ControlVO();
                            control.setHouseId(userHouseEntity.getHouseId());
                            control.setCommunityId(userHouseEntity.getCommunityId());
                            if (memberEntity.getRelation().equals(6)) {
                                control.setAccessLevel(2);
                            } else {
                                control.setAccessLevel(3);
                            }
                            controlVO.getPermissions().add(control);
                        }
                        return controlVO;
                    }
                }
                for (HouseMemberEntity memberEntity : memberEntities) {
                    if (memberEntity.getRelation() == 7) {
                        controlVO.setAccessLevel(3);
                        controlVO.setCommunityId(memberEntity.getCommunityId());
                        controlVO.setHouseId(memberEntity.getHouseId());
                        for (HouseMemberEntity userHouseEntity : memberEntities) {
                            control = new ControlVO();
                            control.setHouseId(userHouseEntity.getHouseId());
                            control.setCommunityId(userHouseEntity.getCommunityId());
                            if (memberEntity.getRelation().equals(6)) {
                                control.setAccessLevel(2);
                            } else {
                                control.setAccessLevel(3);
                            }
                            controlVO.getPermissions().add(control);
                        }
                        return controlVO;
                    }
                }
            }
        }

        controlVO.setAccessLevel(4);
        controlVO.setCommunityId(communityId);
        controlVO.setHouseId(null);
        return controlVO;
    }


}
