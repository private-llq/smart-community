package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.*;
import com.jsy.community.config.ProprietorTopicNameEntity;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
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
import com.jsy.community.utils.imutils.entity.ImResponseEntity;
import com.jsy.community.vo.*;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.BaseThirdPlatform;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.rpc.IThirdRpcService;
import com.zhsj.base.api.vo.LoginVo;
import com.zhsj.base.api.vo.ThirdBindStatusVo;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.baseweb.support.LoginUser;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
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
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * ????????????
 *
 * @author ling
 * @since 2020-11-11 18:12
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements ProprietorUserService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

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


    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseInfoService houseInfoService;

    @Value("${H5Url}")
    private String H5Url;

    @Autowired
    private ISignatureService signatureService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseAuthRpcService baseAuthRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    @DubboReference(version = com.zhsj.im.chat.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.im.chat.api.constant.RpcConst.Rpc.Group.GROUP_IM_CHAT, check=false)
    private IImChatPublicPushRpcService iImChatPublicPushRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IThirdRpcService thirdRpcService;

    private long expire = 60 * 60 * 24 * 7; //??????


    /**
     * @Description: ??????????????????
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
     * ????????????token
     */
    @Override
    public UserAuthVo createAuthVoWithToken(UserInfoVo userInfoVo) {
        Date expireDate = new Date(new Date().getTime() + expire * 1000);
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(LocalDateTimeUtil.of(expireDate));
        userInfoVo.setIsBindMobile(1);
        userAuthVo.setUserInfo(userInfoVo);
        String token = UserUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(userInfoVo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(token);

        return userAuthVo;
    }

    /***
     * @author: Pipi
     * @description: ????????????????????????
     * @param uid: ??????uid
     * @return: {@link String}
     * @date: 2021/11/22 17:01
     **/
    @Override
    public String getUroraTags(String uid) {
        //????????????????????????
        UserUroraTagsEntity userUroraTagsEntity = userUroraTagsService.queryUroraTags(uid);
        if (userUroraTagsEntity != null) {
            return userUroraTagsEntity.getUroraTags();
        }
        return null;
    }

    /**
     * ??????????????????
     */
    @Override
    public UserInfoVo queryUserInfo(String uid) {
        UserInfoVo userInfoVo = new UserInfoVo();
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(uid);
        if (ObjectUtil.isNotNull(userDetail)) {
            ThirdBindStatusVo thirdBindStatus = baseUserInfoRpcService.getThirdBindStatus(userDetail.getId());
            if (ObjectUtil.isNotNull(thirdBindStatus)) {
                userInfoVo.setIsBindAlipay(thirdBindStatus.getAliPayBind() ? 1 : 0);
                userInfoVo.setIsBindWechat(thirdBindStatus.getWeChatBind() ? 1 : 0);
            }
            Boolean payPasswordStatus = baseUserInfoRpcService.getPayPasswordStatus(userDetail.getId());
            userInfoVo.setIsBindPayPassword(payPasswordStatus ? 1 : 0);
            UserImVo eHomeUserIm = baseUserInfoRpcService.getEHomeUserIm(userDetail.getId());
            if (ObjectUtil.isNotNull(eHomeUserIm)) {
                userInfoVo.setImId(eHomeUserIm.getImId());
                userInfoVo.setImPassword(eHomeUserIm.getPassword());
            }
        } else {
            throw new ProprietorException(JSYError.ACCOUNT_NOT_EXISTS);
        }
        /*UserEntity user = baseMapper.queryUserInfoByUid(uid);
        if (user == null || user.getDeleted() == 1) {
            throw new ProprietorException("???????????????");
        }


        BeanUtils.copyProperties(user, userInfoVo);
        // ???????????????
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (user.getProvinceId() != null) {
            userInfoVo.setProvince(ops.get("RegionSingle:" + user.getProvinceId().toString()));
        }
        if (user.getCityId() != null) {
            userInfoVo.setCity(ops.get("RegionSingle:" + user.getCityId().toString()));
        }
        if (user.getAreaId() != null) {
            userInfoVo.setArea(ops.get("RegionSingle:" + user.getAreaId().toString()));
        }*/
        //????????????????????????
        UserUroraTagsEntity userUroraTagsEntity = userUroraTagsService.queryUroraTags(uid);
        if (userUroraTagsEntity != null) {
            userInfoVo.setUroraTags(userUroraTagsEntity.getUroraTags());
        }
        //????????????imId
       /* UserIMEntity userIMEntity = userIMMapper.selectOne(new QueryWrapper<UserIMEntity>().select("im_id,im_password").eq("uid", uid));
        if (userIMEntity != null) {
            userInfoVo.setImId(userIMEntity.getImId());
            userInfoVo.setImPassword(userIMEntity.getImPassword());
        }*/
        //??????????????????????????????
        /*UserThirdPlatformEntity platformEntity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>().eq("uid", uid).eq("third_platform_type", 2));
        if (platformEntity != null) {
            userInfoVo.setIsBindWechat(1);
        } else {
            userInfoVo.setIsBindWechat(0);
        }*/
        //????????????????????????????????????
        /*UserAuthEntity userAuthEntity = userAuthService.selectByPayPassword(uid);
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
        }*/
        /*UserThirdPlatformEntity userThirdPlatformEntity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>().eq("uid", uid).eq("third_platform_type", 5));
        if (userThirdPlatformEntity != null) {
            userInfoVo.setIsBindAlipay(1);
        } else {
            userInfoVo.setIsBindAlipay(0);
        }*/
        return userInfoVo;
    }

    /**
     * @param qo :
     * @author: Pipi
     * @description: ??????????????????
     * @return: {@link UserAuthVo}
     * @date: 2021/11/27 9:43
     **/
    @Override
    public UserAuthVo queryUserInfoV2(LoginQO qo) {
        LoginVo loginVo;
        // ??????????????????????????????
        if (StrUtil.isEmpty(qo.getCode())) {
            loginVo = baseAuthRpcService.loginEHome(qo.getAccount(), qo.getPassword(), "PHONE_PWD");
        } else {
            loginVo = baseAuthRpcService.loginEHome(qo.getAccount(), qo.getCode(), "PHONE_CODE");
        }
        // ????????????????????????????????????????????????
        ThirdBindStatusVo thirdBindStatus = baseUserInfoRpcService.getThirdBindStatus(loginVo.getUserInfo().getId());
        List<BaseThirdPlatform> baseThirdPlatforms = thirdRpcService.allBindThird(loginVo.getUserInfo().getId());
        Integer bindIosStatus = 0;
        if (!CollectionUtils.isEmpty(baseThirdPlatforms)) {
            List<BaseThirdPlatform> ios = baseThirdPlatforms.stream().filter(baseThirdPlatform -> baseThirdPlatform.getThirdPlatformType().equals("IOS")).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ios)) {
                bindIosStatus = 1;
            }
        }
        // ??????????????????????????????????????????????????????
        Boolean payPasswordStatus = baseUserInfoRpcService.getPayPasswordStatus(loginVo.getUserInfo().getId());
        Boolean passwordStatus = baseUserInfoRpcService.getLoginPasswordStatus(loginVo.getUserInfo().getId(), qo.getAccount());
        // ??????????????????????????????????????????
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(loginVo.getUserInfo().getId());
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setIsBindMobile(loginVo.getUserInfo().getPhone() != null ? 1 : 0);
        if (thirdBindStatus != null) {
            userInfoVo.setIsBindWechat(thirdBindStatus.getWeChatBind() ? 1 : 0);
            userInfoVo.setIsBindAlipay(thirdBindStatus.getAliPayBind() ? 1 : 0);
        }
        userInfoVo.setIsBindPayPassword(payPasswordStatus != null && payPasswordStatus ? 1 : 0);
        userInfoVo.setIsBindPassword(passwordStatus != null && passwordStatus ? 1 : 0);
        userInfoVo.setIsBindIos(bindIosStatus);
        userInfoVo.setMobile(loginVo.getUserInfo().getPhone());
        userInfoVo.setUid(loginVo.getUserInfo().getAccount());
        userInfoVo.setUroraTags(getUroraTags(userInfoVo.getUid()));
        userInfoVo.setImId(loginVo.getUserIm().getImId());
        userInfoVo.setImPassword(loginVo.getUserIm().getPassword());
        userInfoVo.setNickname(loginVo.getUserInfo().getNickName());
        userInfoVo.setAvatarUrl(loginVo.getUserInfo().getAvatarThumbnail());
        userInfoVo.setSex(loginVo.getUserInfo().getSex());
        if (idCardRealInfo != null) {
            userInfoVo.setBirthday(idCardRealInfo.getIdCardBirthday());
            userInfoVo.setRealName(idCardRealInfo.getIdCardName());
            userInfoVo.setIdCard(idCardRealInfo.getIdCardNumber());
        }
        userInfoVo.setIsRealAuth(loginVo.getUserInfo().getRealAuthType() == 2 ? 1 : 0);

        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setToken(loginVo.getToken().getToken());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(loginVo.getToken().getExpiredTime()/1000, 0, ZoneOffset.ofHours(8));
        userAuthVo.setExpiredTime(localDateTime);
        userAuthVo.setUserInfo(userInfoVo);
        return userAuthVo;
    }

    /**
     * @Description: ??????????????????ID
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
     * ??????
     */
    @Override
    public UserInfoVo login(LoginQO qo) {
        String uid = userAuthService.checkUser(qo);
        return queryUserInfo(uid);
    }


    /**
     * ??????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String register(RegisterQO qo) {
//        commonService.checkVerifyCode(qo.getAccount(), qo.getCode());

        String uuid = UserUtils.randomUUID();

        // ??????user?????????
        UserEntity user = new UserEntity();
        user.setUid(uuid);
        user.setAvatarUrl("https://i.postimg.cc/1XgLJbXJ/home.jpg");
//        user.setNickname("E-home@" + qo.getAccount());
        user.setNickname(qo.getAccount());
        user.setId(SnowFlake.nextId());

        // ????????????(user_auth???)
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUid(uuid);
        userAuth.setId(SnowFlake.nextId());
        if (RegexUtils.isMobile(qo.getAccount())) { //????????????
            userAuth.setMobile(qo.getAccount());
            user.setMobile(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // ????????????
            userAuth.setEmail(qo.getAccount());
        } else { //???????????????
            userAuth.setUsername(qo.getAccount());
        }
        try {
            //????????????(user???)
            save(user);
            //????????????(user_auth???)
            userAuthService.save(userAuth);
        } catch (DuplicateKeyException e) {
            throw new ProprietorException("??????????????????");
        }
        //??????????????????(t_user_account???)
        boolean userAccountResult = userAccountService.createUserAccount(uuid);
        if (!userAccountResult) {
            log.error("???????????????????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //??????????????????tags(t_user_urora_tags???)
        UserUroraTagsEntity userUroraTagsEntity = new UserUroraTagsEntity();
        userUroraTagsEntity.setUid(uuid);
        userUroraTagsEntity.setCommunityTags("all"); //??????????????????????????????tag???????????????????????????
        boolean uroraTagsResult = userUroraTagsService.createUroraTags(userUroraTagsEntity);
        if (!uroraTagsResult) {
            log.error("??????????????????tags???????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //??????imID(t_user_im???)
        String imId = getImId();
        UserIMEntity userIMEntity = new UserIMEntity();
        userIMEntity.setImPassword("999999999");
        userIMEntity.setUid(uuid);
        userIMEntity.setImId(imId);
        userIMMapper.insert(userIMEntity);
        //????????????????????????
        ImResponseEntity responseEntity = PushInfoUtil.registerUser(userIMEntity.getImId(), userIMEntity.getImPassword(), user.getNickname(), user.getAvatarUrl());
        if (responseEntity.getErr_code() != 0) {
            log.error("??????????????????????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //??????????????????
        List<HouseInfoEntity> houseInfoEntities = houseInfoService.selectList(qo.getAccount());
        Map<String, Object> map = new HashMap<>();
        map.put("type", 9);
        if (houseInfoEntities.size() != 0) {
            for (HouseInfoEntity houseInfoEntity : houseInfoEntities) {
                //????????????
                PushInfoUtil.PushPublicMsg(
                        iImChatPublicPushRpcService,
                        imId,
                        "????????????",
                        houseInfoEntity.getTitle(),
                        H5Url + "?id=" + houseInfoEntity.getId() + "mobile=" + qo.getAccount(),
                        houseInfoEntity.getContent(),
                        map,
                        BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());
            }
        }

        //??????????????????(????????????)
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setUuid(uuid);
        signatureUserDTO.setImId(imId);
        if (RegexUtils.isMobile(qo.getAccount())) { //????????????
            signatureUserDTO.setTelephone(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // ????????????
            signatureUserDTO.setEmail(qo.getAccount());
        } else { //???????????????????????????????????????
            return uuid;
        }
//        String avatar = ResourceLoadUtil.loadJSONResource("/sys_default_content.json").getString("avatar");
        signatureUserDTO.setImage(user.getAvatarUrl());
        signatureUserDTO.setNickName(user.getNickname());
        boolean signUserResult = signatureService.insertUser(signatureUserDTO);
        if (!signUserResult) {
            log.error("???????????????????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        // ????????????????????????
        CallUtil.updateUserInfo(imId, user.getNickname(), user.getAvatarUrl());
        log.info("?????????????????????????????????userid -> {},nickName -> {},image -> {}", imId, user.getNickname(), user.getAvatarUrl());

        //?????????????????????????????????????????????????????????
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
     * ??????v2
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String registerV2(RegisterQO qo) {
//        commonService.checkVerifyCode(qo.getAccount(), qo.getCode());

        String uuid = UserUtils.randomUUID();

        // ??????user?????????
        UserEntity user = new UserEntity();
        user.setId(SnowFlake.nextId());
        user.setUid(uuid);
        user.setAvatarUrl("https://i.postimg.cc/1XgLJbXJ/home.jpg");
//        user.setNickname("E-home@" + qo.getAccount());
        user.setNickname(qo.getAccount());
        user.setRealName(qo.getName());

        // ????????????(user_auth???)
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUid(uuid);
        userAuth.setId(SnowFlake.nextId());
        if (RegexUtils.isMobile(qo.getAccount())) { //????????????
            userAuth.setMobile(qo.getAccount());
            user.setMobile(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // ????????????
            userAuth.setEmail(qo.getAccount());
        } else { //???????????????
            userAuth.setUsername(qo.getAccount());
        }
        try {
            //????????????(user???)
            save(user);

            //????????????(user_auth???)
            userAuthService.save(userAuth);

        } catch (DuplicateKeyException e) {
            throw new ProprietorException("??????????????????");
        }
        //??????????????????(t_user_account???)
        boolean userAccountResult = userAccountService.createUserAccount(uuid);
        if (!userAccountResult) {
            log.error("???????????????????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //??????????????????tags(t_user_urora_tags???)
        UserUroraTagsEntity userUroraTagsEntity = new UserUroraTagsEntity();
        userUroraTagsEntity.setUid(uuid);
        userUroraTagsEntity.setCommunityTags("all"); //??????????????????????????????tag???????????????????????????
        boolean uroraTagsResult = userUroraTagsService.createUroraTags(userUroraTagsEntity);
        if (!uroraTagsResult) {
            log.error("??????????????????tags???????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //??????imID(t_user_im???)
        String imId = getImId();
        UserIMEntity userIMEntity = new UserIMEntity();
        userIMEntity.setUid(uuid);
        userIMEntity.setImPassword("999999999");
        userIMEntity.setImId(imId);
        userIMMapper.insert(userIMEntity);

        //??????????????????
        ImResponseEntity responseEntity = PushInfoUtil.registerUser(userIMEntity.getImId(), userIMEntity.getImPassword(), user.getNickname(), user.getAvatarUrl());
        if (responseEntity.getErr_code() != 0) {
            log.error("??????????????????????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //??????????????????(????????????)
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setUuid(uuid);
        signatureUserDTO.setImId(imId);
        if (RegexUtils.isMobile(qo.getAccount())) { //????????????
            signatureUserDTO.setTelephone(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // ????????????
            signatureUserDTO.setEmail(qo.getAccount());
        } else { //???????????????????????????????????????
            return uuid;
        }
        String avatar = ResourceLoadUtil.loadJSONResource("/sys_default_content.json").getString("avatar");
        signatureUserDTO.setImage(avatar);
        signatureUserDTO.setNickName(user.getNickname());
        boolean signUserResult = signatureService.insertUser(signatureUserDTO);
        if (!signUserResult) {
            log.error("???????????????????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        // ????????????????????????
        CallUtil.updateUserInfo(imId, user.getNickname(), avatar);
        log.info("?????????????????????????????????userid -> {},nickName -> {},image -> {}", imId, user.getNickname(), avatar);
        return uuid;
    }

    /***
     * @author: Pipi
     * @description: ???????????????, ??????????????????????????????, ??????????????????????????????
     * @param qo :
     * @return: {@link String}
     * @date: 2021/11/23 10:55
     **/
    @Override
    @LcnTransaction
    public UserAuthVo registerV3(RegisterQO qo) {
        //??????????????????????????????
        LoginVo loginVo = baseAuthRpcService.eHomeUserPhoneRegister(qo.getAccount(), qo.getPassword(), qo.getCode());
        String uid = loginVo.getUserInfo().getAccount();
        String imId = loginVo.getUserIm().getImId();
        //??????????????????tags(t_user_urora_tags???)
       /* UserUroraTagsEntity userUroraTagsEntity = new UserUroraTagsEntity();
        userUroraTagsEntity.setUid(uid);
        userUroraTagsEntity.setCommunityTags("all"); //??????????????????????????????tag???????????????????????????
        boolean uroraTagsResult = userUroraTagsService.createUroraTags(userUroraTagsEntity);
        if (!uroraTagsResult) {
            log.error("??????????????????tags???????????????????????????????????????????????????" + qo.getAccount());
            throw new ProprietorException(JSYError.INTERNAL);
        }*/

        //??????????????????
        List<HouseInfoEntity> houseInfoEntities = houseInfoService.selectList(qo.getAccount());
        Map<String, Object> map = new HashMap<>();
        map.put("type", 9);
        if (!houseInfoEntities.isEmpty()) {
            for (HouseInfoEntity houseInfoEntity : houseInfoEntities) {
                //????????????
                PushInfoUtil.PushPublicMsg(
                        iImChatPublicPushRpcService,
                        imId,
                        "????????????",
                        houseInfoEntity.getTitle(),
                        H5Url + "?id=" + houseInfoEntity.getId() + "mobile=" + qo.getAccount(),
                        houseInfoEntity.getContent(),
                        map,
                        BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());
            }
        }

        //?????????????????????????????????????????????????????????
        List<HouseMemberEntity> mobile = houseMemberMapper.selectList(new QueryWrapper<HouseMemberEntity>().eq("mobile", qo.getAccount()).eq("relation",1));
        Set<Long> ids = null;
        Set<Long> houseIds = null;
        if (!mobile.isEmpty()) {
            ids = new HashSet<>();
            houseIds = new HashSet<>();
            for (HouseMemberEntity houseMemberEntity : mobile) {
                ids.add(houseMemberEntity.getId());
                houseIds.add(houseMemberEntity.getHouseId());
            }
            houseMemberMapper.updateByMobile(ids, uid);
            userHouseService.updateMobile(houseIds, uid);
        }

        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUid(uid);
        userInfoVo.setIsBindMobile(1);

        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setToken(loginVo.getToken().getToken());
        userAuthVo.setExpiredTime(LocalDateTime.ofEpochSecond(loginVo.getToken().getExpiredTime()/1000, 0, ZoneOffset.ofHours(8)));
        userAuthVo.setUserInfo(userInfoVo);
        return userAuthVo;
    }

    /**
     * ????????????????????????????????????(???????????????)(????????????????????????id)
     */
    private String getUserInfoFromThirdPlatform(UserThirdPlatformQO userThirdPlatformQO) {
        String thirdUid = null;
        switch (userThirdPlatformQO.getThirdPlatformType()) {
            case Const.ThirdPlatformConsts.ALIPAY:
                //??????????????????accessToken????????????userid
                if (!StringUtils.isEmpty(userThirdPlatformQO.getAccessToken())) {
                    thirdUid = alipayService.getUserid(userThirdPlatformQO.getAccessToken());
                }
                //??????????????????accessToekn?????????userid?????????????????????authCode????????????accessToken??????userid
                if (StringUtils.isEmpty(thirdUid) && !StringUtils.isEmpty(userThirdPlatformQO.getAuthCode())) {
                    String accessToken = alipayService.getAccessToken(userThirdPlatformQO.getAuthCode());
                    if (StringUtils.isEmpty(accessToken)) { //????????????accessToekn????????????????????????
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
            throw new ProprietorException("????????????uid???????????? ??????????????????");
        }
        return thirdUid;
    }


    /**
     * @Description: ????????????v2
     * @author: Hu
     * @since: 2021/12/7 15:33
     * @Param: [userThirdPlatformQO]
     * @return: com.jsy.community.vo.UserAuthVo
     */
    @Override
    public UserAuthVo thirdPlatformLoginV2(UserThirdPlatformQO userThirdPlatformQO) {
        UserAuthVo userAuthVo = new UserAuthVo();
        LoginVo loginVo = baseAuthRpcService.aliPayLoginEHome(userThirdPlatformQO.getAuthCode());
        userAuthVo.setToken(loginVo.getToken().getToken());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(loginVo.getToken().getExpiredTime()/1000, 0, ZoneOffset.ofHours(8));
        userAuthVo.setExpiredTime(localDateTime);
        userAuthVo.setExpiredTime(localDateTime);
        userAuthVo.setUserInfo(getUserInfoVo(loginVo));
        return userAuthVo;
    }

    public UserInfoVo getUserInfoVo(LoginVo loginVo){
        ThirdBindStatusVo thirdBindStatus = new ThirdBindStatusVo();
        thirdBindStatus.setWeChatBind(false);
        thirdBindStatus.setAliPayBind(false);
        Boolean payPasswordStatus = false;
        Boolean passwordStatus = false;
        Integer bindIosStatus = 0;
        RealInfoDto idCardRealInfo = new RealInfoDto();
        if (loginVo.getUserInfo().getId() != null) {
            // ????????????????????????????????????????????????
            thirdBindStatus = baseUserInfoRpcService.getThirdBindStatus(loginVo.getUserInfo().getId());
            List<BaseThirdPlatform> baseThirdPlatforms = thirdRpcService.allBindThird(loginVo.getUserInfo().getId());
            if (!CollectionUtils.isEmpty(baseThirdPlatforms)) {
                List<BaseThirdPlatform> ios = baseThirdPlatforms.stream().filter(baseThirdPlatform -> baseThirdPlatform.getThirdPlatformType().equals("IOS")).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(ios)) {
                    bindIosStatus = 1;
                }
            }
            // ??????????????????????????????????????????????????????
            payPasswordStatus = baseUserInfoRpcService.getPayPasswordStatus(loginVo.getUserInfo().getId());
            passwordStatus = baseUserInfoRpcService.getLoginPasswordStatus(loginVo.getUserInfo().getId(), loginVo.getUserInfo().getPhone());
            // ??????????????????????????????????????????
            idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(loginVo.getUserInfo().getId());
        }

        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setIsBindMobile(loginVo.getUserInfo().getPhone() != null ? 1 : 0);
        if (thirdBindStatus != null) {
            userInfoVo.setIsBindWechat(thirdBindStatus.getWeChatBind() ? 1 : 0);
            userInfoVo.setIsBindAlipay(thirdBindStatus.getAliPayBind() ? 1 : 0);
        }

        if (userInfoVo.getIsBindMobile()==1){
            userInfoVo.setIsBindPayPassword(payPasswordStatus != null && payPasswordStatus ? 1 : 0);
            userInfoVo.setIsBindPassword(passwordStatus != null && passwordStatus ? 1 : 0);
            userInfoVo.setIsBindIos(bindIosStatus);
            userInfoVo.setMobile(loginVo.getUserInfo().getPhone());
            userInfoVo.setUroraTags(getUroraTags(userInfoVo.getUid()));
            userInfoVo.setNickname(loginVo.getUserInfo().getNickName());
            userInfoVo.setAvatarUrl(loginVo.getUserInfo().getAvatarThumbnail());
            userInfoVo.setSex(loginVo.getUserInfo().getSex());
            if (idCardRealInfo != null) {
                userInfoVo.setBirthday(idCardRealInfo.getIdCardBirthday());
                userInfoVo.setRealName(idCardRealInfo.getIdCardName());
                userInfoVo.setIdCard(idCardRealInfo.getIdCardNumber());
            }
            userInfoVo.setIsRealAuth(loginVo.getUserInfo().getRealAuthType() == 2 ? 1 : 0);

            userInfoVo.setUid(loginVo.getUserInfo().getAccount());
            userInfoVo.setImId(loginVo.getUserIm().getImId());
            userInfoVo.setImPassword(loginVo.getUserIm().getPassword());
        }

        return userInfoVo;
    }

    /**
     * @Description: ????????????
     * @Param: [userThirdPlatformQO]
     * @Return: com.jsy.community.vo.UserAuthVo
     * @Author: chq459799974
     * @Date: 2021/1/12
     **/
    @Override
    public UserAuthVo thirdPlatformLogin(UserThirdPlatformQO userThirdPlatformQO) {
//        Map<String, Object> returnMap = new HashMap<>();
        //????????????uid
        String thirdPlatformUid = getUserInfoFromThirdPlatform(userThirdPlatformQO);
        userThirdPlatformQO.setThirdPlatformId(thirdPlatformUid);
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("third_platform_id", userThirdPlatformQO.getThirdPlatformId())
                .eq("third_platform_type", userThirdPlatformQO.getThirdPlatformType()));
        if (entity == null) { //????????????
            UserThirdPlatformEntity userThirdPlatformEntity = new UserThirdPlatformEntity();
            userThirdPlatformEntity.setThirdPlatformType(userThirdPlatformQO.getThirdPlatformType());
            userThirdPlatformEntity.setThirdPlatformId(thirdPlatformUid);
            userThirdPlatformEntity.setId(SnowFlake.nextId());
            userThirdPlatformMapper.insert(userThirdPlatformEntity);
//            returnMap.put("exists",false);
//            returnMap.put("data",userThirdPlatformEntity.getUserId());
            return createBindMobile(userThirdPlatformEntity.getId());
        } else {
            if (!StringUtils.isEmpty(entity.getUid())) {
                //????????????
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
                //????????????????????????????????????
//                //????????????id
//                returnMap.put("exists",false);
//                returnMap.put("data",entity.getUserId());
                return createBindMobile(entity.getId());
            }
//            return returnMap;
        }
    }


    /**
     * @Description: ????????????????????????v2
     * @author: Hu
     * @since: 2021/12/7 15:52
     * @Param: [userThirdPlatformQO]
     * @return: com.jsy.community.vo.UserInfoVo
     */
    @Override
    public UserAuthVo bindThirdPlatformV2(UserThirdPlatformQO userThirdPlatformQO, LoginUser loginUser) {
        UserAuthVo authVo = new UserAuthVo();
        LoginVo loginVo = baseAuthRpcService.weChatBindPhone(loginUser.getToken(), userThirdPlatformQO.getMobile(), userThirdPlatformQO.getCode());
        authVo.setToken(loginVo.getToken().getToken());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(loginVo.getToken().getExpiredTime()/1000, 0, ZoneOffset.ofHours(8));
        authVo.setExpiredTime(localDateTime);
        authVo.setUserInfo(getUserInfoVo(loginVo));
        return authVo;
    }

    /**
     * @Description: ??????????????????
     * @Param: [userThirdPlatformQO]
     * @Return: com.jsy.community.vo.UserAuthVo
     * @Author: chq459799974
     * @Date: 2021/1/12
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAuthVo bindThirdPlatform(UserThirdPlatformQO userThirdPlatformQO) {
        Long id = Long.valueOf(userThirdPlatformQO.getThirdPlatformId());
        //????????????????????? ????????????
        commonService.checkVerifyCode(userThirdPlatformQO.getMobile(), userThirdPlatformQO.getCode());
        //????????????uid
//        String thirdPlatformUid = getUserInfoFromThirdPlatform(userThirdPlatformQO);
//        userThirdPlatformQO.setThirdPlatformId(thirdPlatformUid);
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>().eq("id", id));
        if (entity == null) {
            throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(), "???????????????????????????????????????");
        }
        userThirdPlatformQO.setThirdPlatformId(entity.getThirdPlatformId());
        //??????????????????
        String uid = userAuthService.queryUserIdByMobile(userThirdPlatformQO.getMobile());
        //??????????????? ????????????
        if (StringUtils.isEmpty(uid)) {
            RegisterQO registerQO = new RegisterQO();
            registerQO.setAccount(userThirdPlatformQO.getMobile());
            registerQO.setCode(userThirdPlatformQO.getCode());
            uid = register(registerQO);
            //?????????????????????
            UserThirdPlatformEntity userThirdPlatformEntity = new UserThirdPlatformEntity();
            BeanUtils.copyProperties(userThirdPlatformQO, userThirdPlatformEntity);
            userThirdPlatformEntity.setId(SnowFlake.nextId());
            userThirdPlatformEntity.setUid(uid);//???uid?????????????????????????????????
            userThirdPlatformMapper.insert(userThirdPlatformEntity);
        } else {
            //??????uid
            UserThirdPlatformEntity userThirdPlatformEntity = new UserThirdPlatformEntity();
            userThirdPlatformEntity.setUid(uid);
            userThirdPlatformMapper.update(userThirdPlatformEntity, new UpdateWrapper<UserThirdPlatformEntity>().eq("id", id));
        }
//        UserInfoVo userInfoVo = new UserInfoVo();
//        userInfoVo.setUid(uid);
        //????????????
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
//        String token = UserUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(vo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(null);
        return userAuthVo;
    }

    /**
     * ??????????????????????????????????????????  id??????null ?????? id == 0 ???????????????????????????
     * ???????????? -> updateImprover
     *
     * @author YuLF
     * @Param proprietorQO        ???????????? ????????????
     * @since 2020/11/27 15:03
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @Deprecated
    public Boolean proprietorUpdate(ProprietorQO qo) {
        updateCar(qo);
        //========================================== ???????????? =========================================================
        List<UserHouseQo> houseList = qo.getHouses();
        //????????????????????????????????????????????????
        boolean hasAddHouse = houseList.stream().anyMatch(w -> w.getId() == null || w.getId() == 0);
        //??????????????????????????????????????????
        if (hasAddHouse) {
            List<UserHouseQo> any = houseList.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
            houseList.removeAll(any);
            //????????????????????????
            userHouseService.addHouseBatch(any, qo.getUid());
        }
        //????????????????????????
        houseList.forEach(h -> userHouseService.update(h, qo.getUid()));
        //========================================== ?????? =========================================================
        //?????????????????? userMapper.proprietorUpdate(qo)
        //?????? ??????????????????????????????????????????????????????????????? ?????????  ???????????????
        return true;
    }


    /**
     * ?????????????????? ?????? id = 0 | id = null ?????????
     *
     * @param qo ????????????
     */
    private void updateCar(ProprietorQO qo) {
        //========================================== ???????????? =========================================================
        //??????????????????????????? ??????????????? ????????????
        if (qo.getHasCar()) {
            //?????????????????????????????? id == null ?????? == 0 ?????????????????? ????????????????????????????????????true
            List<CarQO> cars = qo.getCars();
            //????????????????????????????????????????????????false
            AtomicBoolean hasAddCar = new AtomicBoolean(false);
            cars.forEach(e -> {
                //????????????id???null ?????? ??????id???0 ????????????????????????????????????
                if (e.getId() == null || e.getId() == 0) {
                    //????????????????????????id
                    hasAddCar.set(true);
                }
            });
            //??????????????????
            if (hasAddCar.get()) {
                //????????????List?????????Id == null ??????ID == 0????????? ??????????????????List ???????????????????????????
                List<CarQO> any = cars.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
                //??????????????????????????? ?????? ?????? ???????????????
                cars.removeAll(any);
                //????????????id
                for (CarQO carQO : any) {
                    carQO.setId(SnowFlake.nextId());
                }
                //??????????????????
                carService.addProprietorCarForList(any, qo.getUid());
                //?????????????????????es

                rabbitTemplate.convertAndSend("exchange_car_topics", "queue.car.insert", getInsertElasticsearchCar(qo.getUid(), qo.getHouses(), any));

            }
            //????????????????????????
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
     * @Description: ???????????????es???qo????????????
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
            elasticsearchCarQO.setOwnerTypeText("??????");
            elasticsearchCarQO.setRelationshipId(userEntity.getUid());
            elasticsearchCarQO.setHouseId(qo.get(0).getHouseId());
            elasticsearchCarQO.setBuilding(entity.getBuilding());
            elasticsearchCarQO.setFloor(String.valueOf(entity.getFloor()));
            elasticsearchCarQO.setUnit(entity.getUnit());
            elasticsearchCarQO.setNumber(entity.getNumber());
            elasticsearchCarQO.setHouseType(entity.getHouseType());
            elasticsearchCarQO.setHouseTypeText(entity.getHouseType() == 1 ? "??????" : "??????");
            elasticsearchCarQO.setCreateTime(LocalDateTime.now());
            list.add(elasticsearchCarQO);
        }
        return list;
    }

    /**
     * @Description: ???????????????es???qo????????????
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
        //========================================== 1.???????????? =========================================================
        List<UserHouseQo> houseList = qo.getHouses();

        List<UserHouseQo> any = null;

        UserEntity userEntity = null;
        //????????????????????????????????????
        if (CollectionUtil.isNotEmpty(houseList)) {
            //???????????????????????????????????? ??? ????????????????????????????????? ???????????????
            List<UserHouseQo> tmpQos = new ArrayList<>(houseList);
            log.error("??????houseList" + houseList);
            //1.??????uid????????????????????????
            RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(qo.getUid());
            userEntity.setIsRealAuth(0);
            userEntity.setUid(qo.getUid());
            // TODO: 2021/12/17  ????????????????????????????????????
            userEntity.setFaceUrl("");
            if (idCardRealInfo != null) {
                userEntity.setIdCard(idCardRealInfo.getIdCardNumber());
                userEntity.setIsRealAuth(2);
                userEntity.setRealName(idCardRealInfo.getIdCardName());
            }
            UserDetail userDetail = baseUserInfoRpcService.getUserDetail(qo.getUid());
            if (userDetail != null) {
                userEntity.setSex(userDetail.getSex());
            }
            // userEntity = queryUserDetailByUid(qo.getUid());
            //?????? ??????????????? ?????? ???????????? ?????????????????? ??????id + ??????id
            List<UserHouseQo> resHouseList = userMapper.getProprietorInfo(userEntity.getIdCard());
            if (CollectionUtil.isEmpty(resHouseList)) {
                throw new ProprietorException("????????????????????????????????????!");
            }
            //?????? ?????????????????? ??? ???????????????????????????  ?????? differenceList ????????? ??????????????? ?????????????????????????????????
            //????????????id???????????????id?????????????????????????????????????????????
            ArrayList<UserHouseQo> forInsert = new ArrayList<>();
            for (UserHouseQo userHouseQo : tmpQos) {
                if (userHouseQo.getId() == null || userHouseQo.getId() == 0) {
                    forInsert.add(userHouseQo);
                }
            }
            List<UserHouseQo> differenceList = differenceSet(forInsert, resHouseList);
            log.error("??????" + differenceList);
            log.error("??????size???" + differenceList.size());
            if (CollectionUtil.isEmpty(differenceList)) {
                //????????????????????????
                //id==null || id == 0 ????????????????????? ????????????????????????????????????????????????
                any = houseList.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(any)) {
                    houseList.removeAll(any);
                    //????????????????????????
                    userHouseService.addHouseBatch(any, qo.getUid());
                }
                //????????????????????????
                houseList.forEach(h -> userHouseService.update(h, qo.getUid()));
            } else {
                //???????????? ??????????????? ?????????????????????????????????
                List<String> houseAddressNames = differenceList.stream().map(UserHouseQo::getHouseAddress).collect(Collectors.toList());
                throw new ProprietorException("????????????????????????????????????" + houseAddressNames);
            }
        }
        updateCar(qo);
        //??????????????????
        if (CollectionUtils.isEmpty(any)) {
            return true;
        }
        Set<Long> communityIds = new HashSet<>();
        for (UserHouseQo userHouseQo : any) {
            communityIds.add(userHouseQo.getCommunityId());
        }
        //??????????????????
        setFaceUrlToCommunityMachine(userEntity, communityIds);
        return true;
    }

    /**
     * ?????????????????????????????????
     */
    private void setFaceUrlToCommunityMachine(UserEntity userEntity, Set<Long> communityIds) {
        //????????????????????????
        if (userEntity.getIsRealAuth() == null || userEntity.getIsRealAuth() != 2) {
            // ????????????????????????????????? ??????????????????
            return;
        }
        if (StringUtils.isEmpty(userEntity.getFaceUrl())) {
            //???????????????????????????????????????
            throw new ProprietorException("????????????????????????????????????????????????");
        }
        // todo ????????????????????????????????????????????????,??????????????????????????????,?????????????????????????????????
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
     * ????????????????????????????????? ?????? ?????? ????????? ??????????????????
     * ?????? ?????? id ??? ??????id ??????????????????????????????
     *
     * @param source ?????????
     * @param target ????????????
     * @return ??????????????????
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
     * ????????????id???????????????????????????????????????
     *
     * @author YuLF
     * @since 2020/12/10 16:25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVo proprietorQuery(String userId, Long houseId) {
        // UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId).select("real_name,sex,detail_address,avatar_url"));
        UserInfoVo userInfoVo = new UserInfoVo();
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(userId);
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(userId);
        if (userDetail != null) {
            userInfoVo.setSex(userDetail.getSex());
            userInfoVo.setAvatarUrl(userDetail.getAvatarThumbnail());
        }
        if (idCardRealInfo != null) {
            userInfoVo.setRealName(idCardRealInfo.getIdCardName());
        }
        // BeanUtil.copyProperties(userEntity, userInfoVo);
        //??????????????????
        List<RelationVO> houseMemberEntities = relationService.selectID(userId, houseId);
        userInfoVo.setProprietorMembers(houseMemberEntities);
        //??????????????????
        List<HouseVo> houseVos = userMapper.queryUserHouseById(userId, houseId);
        userInfoVo.setProprietorHouses(houseVos);
        return userInfoVo;
    }

    /**
     * @Description: ??????????????????(??????????????????)
     * @Param: [uid]
     * @Return: java.util.Collection<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author: chq459799974
     * @Date: 2021/3/31
     **/
    @Override
    public Collection<Map<String, Object>> queryUserHousesOfCommunity(String uid) {
        //?????????????????????????????????
        Set<Long> communityIds = userHouseService.queryUserHousesOfCommunityIds(uid);
        if (CollectionUtils.isEmpty(communityIds)) {
            return null;
        }
        //???????????????
        Map<String, Map<String, Object>> communityIdAndName = communityService.queryCommunityNameByIdBatch(communityIds);
        return communityIdAndName.values();
    }

    @Override
    public UserEntity getUser(String tenantUid) {
        return userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", tenantUid));
    }

    /**
     * @Description: ??????????????????(???????????????)
     * @Param: [uid]
     * @Return: java.util.Collection<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author: chq459799974
     * @Date: 2021/3/31
     **/
    @Override
    public Collection<Map<String, Object>> queryRelationHousesOfCommunity(String uid) {
        //?????????????????????????????????
        Set<Long> communityIds = userHouseService.queryRelationHousesOfCommunityIds(uid);
        if (CollectionUtils.isEmpty(communityIds)) {
            return null;
        }
        //???????????????
        Map<String, Map<String, Object>> communityIdAndName = communityService.queryCommunityNameByIdBatch(communityIds);
        return communityIdAndName.values();
    }

    @Override
    public Collection<Map<String, Object>> queryCommunityUserList(String uid) {
        //?????????????????????????????????
        Set<Long> communityIds = userHouseService.queryUserHousesOfCommunityIds(uid);
        if (CollectionUtils.isEmpty(communityIds)) {
            return null;
        }
        //???????????????
        Map<String, Map<String, Object>> communityIdAndName = communityService.queryCommunityNameByIdBatch(communityIds);
        return communityIdAndName.values();
    }

    /**
     * @Description: ?????????????????????????????????
     * @Param: [uid]
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Author: chq459799974
     * @Date: 2020/12/16
     **/
    @Override
    public List<HouseEntity> queryUserHouseList(String uid) {

        //?????????
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

        //?????????
        //?????????id,??????id,??????id,????????????
        //???buildingId??????pid!=0 ???????????????
        /* t_house */
        List<HouseEntity> houses = houseService.queryHouses(houseIdList);
        //??????buildingId
        for (HouseEntity tempEntity : houses) {
            //??????????????????????????????????????????id???buildingId
            setBuildingId(tempEntity);
        }

        //?????????
        //???????????????????????????
        /* t_community *//* t_user */
        Map<String, Map<String, Object>> communityMap = communityService.queryCommunityNameByIdBatch(communityIdSet);
        // UserInfoVo userInfoVo = userMapper.selectUserInfoById(uid);
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(uid);
        for (HouseEntity userHouseEntity : houses) {
            Map<String, Object> map = communityMap.get(BigInteger.valueOf(userHouseEntity.getCommunityId()));
            userHouseEntity.setCommunityName(map == null ? null : String.valueOf(map.get("name")));
            if (idCardRealInfo != null) {
                userHouseEntity.setOwner(idCardRealInfo.getIdCardName());
            }
        }
        return houses;
    }


    /**
     * @Description: ??????????????????
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
                throw new ProprietorException("?????????????????????????????????");
            }

            //??????????????????????????????
            userThirdPlatformMapper.delete(new QueryWrapper<UserThirdPlatformEntity>().eq("third_platform_id", authEntity.getOpenId()));

            //??????userAuth??????openId???null
            userAuthService.updateByOpenId(authEntity.getId());
        }
    }

    /**
     * @Description: ??????????????????
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
                throw new ProprietorException("??????????????????????????????");
            }
            //????????????openid
            userAuthEntity.setOpenId(openid);
            userAuthService.updateByWechat(userAuthEntity);
            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));

            return userEntity.getRealName();
        }
        return null;
    }

    /**
     * @Description: ?????????????????????????????????????????????
     * @author: Hu
     * @since: 2021/9/29 16:08
     * @Param: [userId, permissions]
     * @return: java.util.List<com.jsy.community.entity.HouseEntity>
     */
    @Override
    public List<HouseEntity> queryUserHouseListAll(String userId) {
        //?????????
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

        //?????????
        //?????????id,??????id,??????id,????????????
        //???buildingId??????pid!=0
        /* t_house */
        List<HouseEntity> houses = houseService.queryHouses(houseIdList);
        //??????buildingId
        for (HouseEntity tempEntity : houses) {
            //??????????????????????????????????????????id???buildingId
            setBuildingId(tempEntity);
        }

        //?????????
        //???????????????????????????
        /* t_community *//* t_user */
        Map<String, Map<String, Object>> communityMap = communityService.queryCommunityNameByIdBatch(communityIdSet);
        // UserInfoVo userInfoVo = userMapper.selectUserInfoById(userId);
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(userId);
        for (HouseEntity userHouseEntity : houses) {
            Map<String, Object> map = communityMap.get(BigInteger.valueOf(userHouseEntity.getCommunityId()));
            userHouseEntity.setCommunityName(map == null ? null : String.valueOf(map.get("name")));
            if (idCardRealInfo != null) {
                userHouseEntity.setOwner(idCardRealInfo.getIdCardName());
            }
        }
        return houses;
    }

    private HouseEntity setBuildingId(HouseEntity tempEntity) {
        Long pid = 0L; //id???pid????????????????????????????????????
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
     * ??????????????????
     *
     * @param userId ??????ID
     * @return ????????????????????????
     * @Param houseId       ??????ID
     * @author YuLF
     * @since 2020/12/18 11:39
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVo proprietorDetails(String userId) {
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(userId);
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(userId);
        //1.????????????????????????
        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setIsRealAuth(0);
        if (userDetail != null) {
            userInfo.setUid(userDetail.getAccount());
            userInfo.setSex(userDetail.getSex());
            userInfo.setMobile(userDetail.getPhone());
        }
        if (idCardRealInfo != null) {
            userInfo.setRealName(idCardRealInfo.getIdCardName());
            userInfo.setIdCard(idCardRealInfo.getIdCardNumber());
            userInfo.setIsRealAuth(1);
        }
        //2.????????????????????????
        List<HouseVo> userHouses = userHouseService.queryUserHouseList(userId);
        //3.??????????????????
        //List<HouseMemberEntity> houseMemberEntities = relationService.selectID(userId, houseId);
        //4.????????????????????????
        List<CarEntity> carEntities = carService.queryUserCarById(userId);
        userInfo.setProprietorCars(carEntities);
        userInfo.setProprietorHouses(userHouses);
        //userInfo.setProprietorMembers(houseMemberEntities);
        return userInfo;
    }

    /**
     * @Description: ????????????/????????????????????????
     * @Param: [uid, communityId]
     * @Return: java.util.Map<java.lang.String, java.lang.String>
     * @Author: chq459799974
     * @Date: 2020/12/23
     **/
    @Override
    public Map<String, String> getAccess(String uid, Long communityId) {
        Map<String, String> returnMap = new HashMap<>();
        //???????????????????????????
        String mobile = userMapper.queryUserMobileByUid(uid);
        //????????????
        if (canGetLongAccess(uid, communityId, mobile)) {
            //??????????????????????????????????????? //TODO ?????????????????????token ????????????????????????????????????
            String access = setUserLongAccess(uid);
            returnMap.put("access", access);
        } else {
            returnMap.put("msg", "???????????????????????????????????????");
        }
        return returnMap;
    }

    //????????????(??????????????????????????????)
    private boolean canGetLongAccess(String uid, Long communityId, String mobile) {
        if (userHouseService.hasHouse(uid, communityId)
                || relationService.isHouseMember(mobile, communityId)) {
            return true;
        }
        return false;
    }

    //?????????????????????
    private String setUserLongAccess(String uid) {
        String token = UUID.randomUUID().toString().replace("-", "");
        VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
        visitorEntryVO.setToken(token);
        redisTemplate.opsForValue().set("UEntry:" + uid, JSON.toJSONString(visitorEntryVO));
        return token;
    }

    /**
     * @Description: ????????????????????????
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
     * @Description: ????????????????????????
     * @Param: [uid]
     * @Return: com.jsy.community.entity.UserEntity
     * @Author: chq459799974
     * @Date: 2021/1/20
     **/
    @Override
    public UserEntity queryUserDetailByUid(String uid) {
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(uid);
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(uid);
        UserEntity userEntity = new UserEntity();
        if (userDetail != null) {
            userEntity.setIsRealAuth(0);
            userEntity.setUid(uid);
            userEntity.setMobile(userDetail.getPhone());
        }
        if (idCardRealInfo != null) {
            userEntity.setRealName(idCardRealInfo.getIdCardName());
            userEntity.setIsRealAuth(1);
            userEntity.setIdCard(idCardRealInfo.getIdCardNumber());
        }
        return userEntity;
    }


    @Override
    public UserEntity getRealAuthAndHouseId(String uid) {
        UserEntity userEntity = new UserEntity();
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(uid);
        if (idCardRealInfo != null) {
            userEntity.setIsRealAuth(2);
        } else {
            userEntity.setIsRealAuth(0);
        }
        //???????????????????????????id
        userEntity.setHouseId(userMapper.getLatestHouseId(uid));
        return userEntity;
    }

    @Override
    public UserInfoVo getUserAndMemberInfo(String uid, Long houseId) {
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(uid);
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(uid);
        //1.????????????????????????????????????
        // UserInfoVo userInfo = userMapper.selectUserNameAndHouseAddr(uid);
        UserInfoVo userInfo = new UserInfoVo();
        if (idCardRealInfo != null) {
            userInfo.setRealName(idCardRealInfo.getIdCardName());
        }
        if (userDetail != null) {
            userDetail.setSex(userDetail.getSex());
        }
        //2.???????????????????????????????????? ????????????????????????????????????B???1801 ??? ??????????????????id
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
     * ????????????proprietorDetails
     *
     * @param communityId ??????id
     * @param houseId     ??????id
     * @param userId      ??????id
     * @return ????????????????????????
     */
    @Override
    @Deprecated
    public UserInfoVo userInfoDetails(Long communityId, Long houseId, String userId) {
        UserInfoVo vo = new UserInfoVo();
        //1.????????????id???????????????????????????
        HouseVo houseVo = userMapper.getHouseInfoById(houseId);
        vo.setProprietorHouses(Collections.singletonList(houseVo));
        //2.????????????id?????????id?????????????????????????????????????????????????????????????????????
        List<CarEntity> carEntities = carService.getAllCarById(communityId, userId);
        vo.setProprietorCars(carEntities);
        return vo;
    }

    /**
     * @Description: ?????????????????????????????????
     * @Param: [userEntity]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/3/2
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserAfterRealnameAuth(UserEntity userEntity) {
        //????????????
        int result = userMapper.update(userEntity, new UpdateWrapper<UserEntity>().eq("uid", userEntity.getUid()));
        if (result != 1) {
            log.error("????????????????????????????????????" + userEntity.getUid());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        //??????????????????
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
        //???????????????????????????uid
        userHouseService.updateMobileUser(userEntity.getUid());
        if (!b) {
            log.error("??????????????????????????????????????????" + userEntity.getUid());
            throw new ProprietorException(JSYError.INTERNAL);
        }
        // ??????????????????
        List<String> strings = userIMMapper.selectByUid(Collections.singleton(userEntity.getUid()));
        if (strings.size() > 0) {
            if (!StringUtils.isEmpty(userEntity.getAvatarUrl()) || !StringUtils.isEmpty(userEntity.getNickname())) {
                CallUtil.updateUserInfo(strings.get(0), userEntity.getNickname(), userEntity.getAvatarUrl());
                log.info("?????????????????????????????????userid -> {},nickName -> {},image -> {}",
                        strings.get(0), userEntity.getNickname(), userEntity.getAvatarUrl());
            }
        }
    }

    /**
     * @Description: uids???????????? uid-????????????
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
     * @Description: ????????????uid????????????????????????????????????????????????uid
     * @Param: [uids, nameLike]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    @Override
    public List<String> queryUidOfNameLike(List<String> uids, String nameLike) {
        return userMapper.queryUidOfNameLike(uids, nameLike);
    }

    @Override
    public Integer userIsRealAuth(String userId) {
        if (baseUserInfoRpcService.getIdCardRealInfo(userId) == null) {
            return 0;
        } else {
            return 2;
        }
        // return userMapper.getRealAuthStatus(userId);
    }

    /**
     * @Description: ??????????????????????????????
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

        if ("00000tourist".equals(uid)) {
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
                //??????????????????????????????
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
