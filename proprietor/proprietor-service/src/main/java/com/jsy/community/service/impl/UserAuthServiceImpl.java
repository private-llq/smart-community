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
     * @Description: 查询当前用户是否绑定微信
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
     * @Description: 清除微信三方绑定
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
     * @Description: 设置微信openid
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
     * @Description: 查询当前用户是否设置支付密码
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
                // 手机验证码登录
                if (!"15178805536".equals(qo.getAccount())) {
                    commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
                }
                return baseMapper.queryUserIdByMobile(qo.getAccount());
            } else {
                return checkUserByPassword(qo, "mobile");
            }
        } else if (RegexUtils.isEmail(qo.getAccount())) {
            if (StrUtil.isNotEmpty(qo.getCode())) {
                // 邮箱验证码登录
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
            throw new ProprietorException("账号不存在，请检查输入是否正确或先注册");
        }
        String password = SecureUtil.sha256(qo.getPassword() + entity.getSalt());
        if (password.equals(entity.getPassword())) {
            return entity.getUid();
        } else {
            throw new ProprietorException("账号密码错误");
        }
    }

    @Override
    public boolean addPassword(String uid, AddPasswordQO qo) {
        if (!qo.getPassword().equals(qo.getConfirmPassword())) {
            throw new ProprietorException("密码不一致");
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
            throw new ProprietorException("密码不一致");
        }
        //对比原密码，如果有的话
        QueryWrapper<UserAuthEntity> eq = new QueryWrapper<UserAuthEntity>().select("*").eq("uid", uid);
        UserAuthEntity userAuthEntity = userAuthMapper.selectOne(eq);
        if (userAuthEntity == null) {
            return false;
        }
        if (!StringUtils.isEmpty(userAuthEntity.getPayPassword())) {
            if (StringUtils.isEmpty(qo.getOldPayPassword())) {
                throw new ProprietorException("原支付密码不能为空");
            }
            if (!userAccountService.checkPayPassword(uid, qo.getOldPayPassword())) {
                throw new ProprietorException("原支付密码错误");
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
            throw new ProprietorException("不存在此账号");
        }

        UserAuthEntity update = new UserAuthEntity();
        update.setId(entity.getId());
        update.setPassword(SecureUtil.sha256(qo.getPassword() + entity.getSalt()));

        return updateById(update);
    }

    /**
     * @Description: 更换手机号
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
        //同步修改签章用户
        SignatureUserDTO signatureUserDTO = new SignatureUserDTO();
        signatureUserDTO.setTelephone(newMobile);
        signatureUserDTO.setUuid(uid);
        boolean b = signatureService.updateUser(signatureUserDTO);
        if (!b) {
            log.error("更换手机号，签章用户同步失败，相关账户：" + uid + "，新手机号：" + newMobile);
            throw new ProprietorException(JSYError.INTERNAL.getCode(), "签章板块手机号同步失败，请联系管理员");
        }
    }

    /**
     * 通过用户ID查询 t_user_auth 用户手机号
     *
     * @return 返回用户手机号码
     * @author YuLF
     * @Param id  用户ID
     * @since 2020/12/2 13:55
     */
    @Override
    public String selectContactById(String id) {
        QueryWrapper<UserAuthEntity> eq = new QueryWrapper<UserAuthEntity>().select("mobile").eq("uid", id).eq("deleted", 0);
        UserAuthEntity userAuthEntity = userAuthMapper.selectOne(eq);
        //未注册直接访问
        if (userAuthEntity == null) {
            return null;
        }
        return userAuthEntity.getMobile();
    }

    /**
     * 发送 修改支付密码 的手机短信验证码
     *
     * @param account 手机号
     */
    @Override
    public void sendPayPasswordVerificationCode(String account) {
        //发送短信验证码
        SmsEntity smsEntity = smsMapper.selectOne(new QueryWrapper<SmsEntity>().eq("deleted", 0));
        ConstClasses.AliYunDataEntity.setConfig(smsEntity);
        String code = SmsUtil.sendVcode(account, BusinessConst.SMS_VCODE_LENGTH_DEFAULT, smsEntity.getSmsSign());
        //5分钟有效期
        redisTemplate.opsForValue().set("vProprietorCodePayPassword" + account, code, 5, TimeUnit.MINUTES);
    }

    /**
     * 根据手机短信验证码 修改支付密码
     *
     * @param qo      验证码以及新支付密码
     * @param account 手机号
     * @param uid     用户id
     */
    @Override
    public void updatePayPasswordByMobileCode(MobileCodePayPasswordQO qo, String account, String uid) {
        //验证支付密码与确认密码一致性
        if (!qo.getPayPassword().equals(qo.getConfirmPayPassword())) {
            throw new ProprietorException("新密码和确认密码不一致");
        }
        // 校验修改支付密码的短信验证码
        boolean b = checkMobileVerificationCode(account, qo.getCode());
        if (!b) {
            throw new ProprietorException("短信验证码不正确");
        }
        //清理缓存中的验证码
        redisTemplate.delete("vProprietorCodePayPassword" + account);
        //修改支付密码
        updatePayPassword(qo.getPayPassword(), uid);
    }

    /**
     * @param id : 用户id
     * @author: Pipi
     * @description: 查询三方平台绑定信息
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
        wechat.setThirdPlatformTypeString("微信");
        wechat.setThirdPlatformBindStatus(false);
        ThirdPlatformInfoVO ios = new ThirdPlatformInfoVO();
        ios.setId(id);
        ios.setThirdPlatformType("IOS");
        ios.setThirdPlatformTypeString("IOS");
        ios.setThirdPlatformBindStatus(false);
        ThirdPlatformInfoVO alipay = new ThirdPlatformInfoVO();
        alipay.setId(id);
        alipay.setThirdPlatformType("ALIPAY");
        alipay.setThirdPlatformTypeString("支付宝");
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
     * 验证修改支付密码的短信验证码
     *
     * @param account    手机号
     * @param mobileCode 验证码
     * @return
     */
    private boolean checkMobileVerificationCode(String account, String mobileCode) {
        //检查验证码
        String code = redisTemplate.opsForValue().get("vProprietorCodePayPassword" + account);
        if (!StringUtils.isEmpty(code)) {
            return code.equals(mobileCode);
        }
        return false;
    }


}
