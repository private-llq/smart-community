package com.jsy.community.service.impl;
import java.util.ArrayList;
import java.util.Date;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.dto.signature.SignatureUserDTO;
import com.jsy.community.entity.SmsEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.SmsMapper;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.proprietor.AddPasswordQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.MobileCodePayPasswordQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.SmsUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.imutils.open.StringUtils;
import com.jsy.community.vo.proprietor.ThirdPlatformInfoVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.BaseThirdPlatform;
import com.zhsj.base.api.rpc.IThirdRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import org.springframework.integration.redis.util.RedisLockRegistry;

@DubboService(version = Const.version, group = Const.group)
@Slf4j
@RefreshScope
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuthEntity> implements IUserAuthService {

//	@Resource
//	private RedisLockRegistry redisLockRegistry;

    @Resource
    private ICommonService commonService;

    @Resource
    private IUserAccountService userAccountService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserAuthMapper userAuthMapper;

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private SmsMapper smsMapper;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ISignatureService signatureService;

    @Value(value = "${jsy.third-platform-domain:http://www.jsy.com}")
    private String callbackUrl;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IThirdRpcService thirdRpcService;

    @Override
    public List<UserAuthEntity> getList(boolean a) {
        return list();
    }

    /**
     * @Description: ????????????????????????????????????
     * @author: Hu
     * @since: 2021/10/16 10:27
     * @Param:
     * @return:
     */
    @Override
    public UserAuthEntity selectByIsWeChat(String userId) {
        return userAuthMapper.selectOne(new QueryWrapper<UserAuthEntity>().eq("uid", userId));
    }


    /**
     * @Description: ????????????????????????
     * @author: Hu
     * @since: 2021/10/18 11:03
     * @Param: [id]
     * @return: void
     */
    @Override
    public void updateByOpenId(Long id) {
        userAuthMapper.updateByOpenId(id);
    }

    /**
     * @Description: ????????????openid
     * @author: Hu
     * @since: 2021/10/16 10:29
     * @Param:
     * @return:
     */
    @Override
    public void updateByWechat(UserAuthEntity userAuthEntity) {
        userAuthMapper.updateById(userAuthEntity);
    }

    /**
     * @Description: ??????????????????????????????????????????
     * @author: Hu
     * @since: 2021/10/13 14:46
     * @Param:
     * @return:
     */
    @Override
    public UserAuthEntity selectByPayPassword(String uid) {
        return userAuthMapper.selectOne(new QueryWrapper<UserAuthEntity>().eq("uid", uid));
    }

    @Override
    public String queryUserIdByMobile(String mobile) {
        return baseMapper.queryUserIdByMobile(mobile);
    }

    @Override
    public String checkUser(LoginQO qo) {
        if (RegexUtils.isMobile(qo.getAccount())) {
            if (StrUtil.isNotEmpty(qo.getCode())) {
                // ?????????????????????
                if (!"15178805536".equals(qo.getAccount())) {
                    commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
                }
                return baseMapper.queryUserIdByMobile(qo.getAccount());
            } else {
                return checkUserByPassword(qo, "mobile");
            }
        } else if (RegexUtils.isEmail(qo.getAccount())) {
            if (StrUtil.isNotEmpty(qo.getCode())) {
                // ?????????????????????
                return null;
            } else {
                return checkUserByPassword(qo, "email");
            }
        } else {
            return checkUserByPassword(qo, "username");
        }
    }

    private String checkUserByPassword(LoginQO qo, String field) {
        UserAuthEntity entity = baseMapper.queryUserByField(qo.getAccount(), field);
        if (entity == null) {
            throw new ProprietorException("?????????????????????????????????????????????????????????");
        }
        String password = SecureUtil.sha256(qo.getPassword() + entity.getSalt());
        if (password.equals(entity.getPassword())) {
            return entity.getUid();
        } else {
            throw new ProprietorException("??????????????????");
        }
    }

    @Override
    public boolean addPassword(String uid, AddPasswordQO qo) {
        if (!qo.getPassword().equals(qo.getConfirmPassword())) {
            throw new ProprietorException("???????????????");
        }

        String salt = RandomUtil.randomString(8);
        String encryptedPassword = SecureUtil.sha256(qo.getPassword() + salt);

        UserAuthEntity entity = new UserAuthEntity();
        entity.setPassword(encryptedPassword);
        entity.setSalt(salt);

        LambdaQueryWrapper<UserAuthEntity> update = new LambdaQueryWrapper<>();
        update.eq(UserAuthEntity::getUid, uid);
        return update(entity, update);
    }

    @Override
    public boolean addPayPassword(String uid, AddPasswordQO qo) {
        if (!qo.getPayPassword().equals(qo.getConfirmPayPassword())) {
            throw new ProprietorException("???????????????");
        }
        //?????????????????????????????????
        QueryWrapper<UserAuthEntity> eq = new QueryWrapper<UserAuthEntity>().select("*").eq("uid", uid);
        UserAuthEntity userAuthEntity = userAuthMapper.selectOne(eq);
        if (userAuthEntity == null) {
            return false;
        }
        if (!StringUtils.isEmpty(userAuthEntity.getPayPassword())) {
            if (StringUtils.isEmpty(qo.getOldPayPassword())) {
                throw new ProprietorException("???????????????????????????");
            }
            if (!userAccountService.checkPayPassword(uid, qo.getOldPayPassword())) {
                throw new ProprietorException("?????????????????????");
            }
        }
        return updatePayPassword(qo.getPayPassword(), uid);
    }

    private boolean updatePayPassword(String payPassword, String uid) {
        String salt = RandomUtil.randomString(8);
        String encryptedPassword = SecureUtil.sha256(payPassword + salt);

        UserAuthEntity entity = new UserAuthEntity();
        entity.setPayPassword(encryptedPassword);
        entity.setPaySalt(salt);

        LambdaQueryWrapper<UserAuthEntity> update = new LambdaQueryWrapper<>();
        update.eq(UserAuthEntity::getUid, uid);
        return update(entity, update);
    }

    @Override
    public boolean checkUserExists(String account, String field) {
        return baseMapper.checkUserExists(account, field) != null;
    }

    @Override
    public boolean resetPassword(ResetPasswordQO qo) {
        UserAuthEntity entity;
        if (RegexUtils.isMobile(qo.getAccount())) {
            entity = baseMapper.queryUserByField(qo.getAccount(), "mobile");
        } else if (RegexUtils.isEmail(qo.getAccount())) {
            entity = baseMapper.queryUserByField(qo.getAccount(), "email");
        } else {
            entity = baseMapper.queryUserByField(qo.getAccount(), "username");
        }

        if (entity == null) {
            throw new ProprietorException("??????????????????");
        }

        UserAuthEntity update = new UserAuthEntity();
        update.setId(entity.getId());
        update.setPassword(SecureUtil.sha256(qo.getPassword() + entity.getSalt()));

        return updateById(update);
    }

    /**
     * @Description: ???????????????
     * @Param: [newMobile, uid]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/1/29
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeMobile(String newMobile, String uid) {
        userAuthMapper.changeMobile(newMobile, uid);
        userMapper.changeMobile(newMobile, uid);
        //????????????????????????
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setTelephone(newMobile);
        signatureUserDTO.setUuid(uid);
        boolean b = signatureService.updateUser(signatureUserDTO);
        if (!b) {
            log.error("????????????????????????????????????????????????????????????" + uid + "??????????????????" + newMobile);
            throw new ProprietorException(JSYError.INTERNAL.getCode(), "??????????????????????????????????????????????????????");
        }
    }

    /**
     * ????????????ID?????? t_user_auth ???????????????
     *
     * @return ????????????????????????
     * @author YuLF
     * @Param id  ??????ID
     * @since 2020/12/2 13:55
     */
    @Override
    public String selectContactById(String id) {
        QueryWrapper<UserAuthEntity> eq = new QueryWrapper<UserAuthEntity>().select("mobile").eq("uid", id).eq("deleted", 0);
        UserAuthEntity userAuthEntity = userAuthMapper.selectOne(eq);
        //?????????????????????
        if (userAuthEntity == null) {
            return null;
        }
        return userAuthEntity.getMobile();
    }

    /**
     * ?????? ?????????????????? ????????????????????????
     *
     * @param account ?????????
     */
    @Override
    public void sendPayPasswordVerificationCode(String account) {
        //?????????????????????
        SmsEntity smsEntity = smsMapper.selectOne(new QueryWrapper<SmsEntity>().eq("deleted", 0));
        ConstClasses.AliYunDataEntity.setConfig(smsEntity);
        String code = SmsUtil.sendVcode(account, BusinessConst.SMS_VCODE_LENGTH_DEFAULT, smsEntity.getSmsSign());
        //5???????????????
        redisTemplate.opsForValue().set("vProprietorCodePayPassword" + account, code, 5, TimeUnit.MINUTES);
    }

    /**
     * ??????????????????????????? ??????????????????
     *
     * @param qo      ??????????????????????????????
     * @param account ?????????
     * @param uid     ??????id
     */
    @Override
    public void updatePayPasswordByMobileCode(MobileCodePayPasswordQO qo, String account, String uid) {
        //??????????????????????????????????????????
        if (!qo.getPayPassword().equals(qo.getConfirmPayPassword())) {
            throw new ProprietorException("?????????????????????????????????");
        }
        // ??????????????????????????????????????????
        boolean b = checkMobileVerificationCode(account, qo.getCode());
        if (!b) {
            throw new ProprietorException("????????????????????????");
        }
        //???????????????????????????
        redisTemplate.delete("vProprietorCodePayPassword" + account);
        //??????????????????
        updatePayPassword(qo.getPayPassword(), uid);
    }

    /**
     * @param id : ??????id
     * @author: Pipi
     * @description: ??????????????????????????????
     * @return: {@link ThirdPlatformInfoVO}
     * @date: 2021/12/16 16:21
     **/
    @Override
    public List<ThirdPlatformInfoVO> queryThirdPlatformInfo(Long id) {
        List<BaseThirdPlatform> baseThirdPlatforms = thirdRpcService.allBindThird(id);
        ArrayList<ThirdPlatformInfoVO> thirdPlatformInfoVOS = new ArrayList<>();
        ThirdPlatformInfoVO wechat = new ThirdPlatformInfoVO();
        wechat.setId(id);
        wechat.setThirdPlatformType("WECHAT");
        wechat.setThirdPlatformTypeString("??????");
        wechat.setThirdPlatformBindStatus(false);
        ThirdPlatformInfoVO ios = new ThirdPlatformInfoVO();
        ios.setId(id);
        ios.setThirdPlatformType("IOS");
        ios.setThirdPlatformTypeString("IOS");
        ios.setThirdPlatformBindStatus(false);
        ThirdPlatformInfoVO alipay = new ThirdPlatformInfoVO();
        alipay.setId(id);
        alipay.setThirdPlatformType("ALIPAY");
        alipay.setThirdPlatformTypeString("?????????");
        alipay.setThirdPlatformBindStatus(false);
        if (!CollectionUtils.isEmpty(baseThirdPlatforms)) {
            for (BaseThirdPlatform baseThirdPlatform : baseThirdPlatforms) {
                if ("WECHAT".equals(baseThirdPlatform.getThirdPlatformType())) {
                    wechat.setThirdPlatformId(baseThirdPlatform.getThirdPlatformId());
                    wechat.setNickName(baseThirdPlatform.getNickName());
                    wechat.setAvatarUrl(baseThirdPlatform.getAvatarUrl());;
                    wechat.setIsDeleted(baseThirdPlatform.getIsDeleted());
                    wechat.setUtcUpdate(baseThirdPlatform.getUtcUpdate());
                    wechat.setUtcCreate(baseThirdPlatform.getUtcCreate());
                    wechat.setThirdPlatformBindStatus(true);
                }
                if ("ALIPAY".equals(baseThirdPlatform.getThirdPlatformType())) {
                    alipay.setThirdPlatformId(baseThirdPlatform.getThirdPlatformId());
                    alipay.setNickName(baseThirdPlatform.getNickName());
                    alipay.setAvatarUrl(baseThirdPlatform.getAvatarUrl());;
                    alipay.setIsDeleted(baseThirdPlatform.getIsDeleted());
                    alipay.setUtcUpdate(baseThirdPlatform.getUtcUpdate());
                    alipay.setUtcCreate(baseThirdPlatform.getUtcCreate());
                    alipay.setThirdPlatformBindStatus(true);
                }
                if ("IOS".equals(baseThirdPlatform.getThirdPlatformType())) {
                    ios.setThirdPlatformId(baseThirdPlatform.getThirdPlatformId());
                    ios.setNickName(baseThirdPlatform.getNickName());
                    ios.setAvatarUrl(baseThirdPlatform.getAvatarUrl());;
                    ios.setIsDeleted(baseThirdPlatform.getIsDeleted());
                    ios.setUtcUpdate(baseThirdPlatform.getUtcUpdate());
                    ios.setUtcCreate(baseThirdPlatform.getUtcCreate());
                    ios.setThirdPlatformBindStatus(true);
                }
            }
        }
        thirdPlatformInfoVOS.add(wechat);
        thirdPlatformInfoVOS.add(ios);
        thirdPlatformInfoVOS.add(alipay);
        return thirdPlatformInfoVOS;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param account    ?????????
     * @param mobileCode ?????????
     * @return
     */
    private boolean checkMobileVerificationCode(String account, String mobileCode) {
        //???????????????
        String code = redisTemplate.opsForValue().get("vProprietorCodePayPassword" + account);
        if (!StringUtils.isEmpty(code)) {
            return code.equals(mobileCode);
        }
        return false;
    }


}
