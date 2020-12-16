package com.jsy.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IRelationService relationService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private UserUtils userUtils;
    
    private long expire = 60*60*24*7; //暂时

    @Autowired
    private IUserHouseService userHouseService;
    
    @Autowired
    private IHouseService houseService;
    
    @Autowired
    private ICommunityService communityService;

    @Override
    public UserAuthVo createAuthVoWithToken(UserInfoVo userInfoVo){
        Date expireDate = new Date(new Date().getTime() + expire * 1000);
        UserAuthVo userAuthVo = new UserAuthVo();
        userAuthVo.setExpiredTime(LocalDateTimeUtil.of(expireDate));
        userAuthVo.setUserInfo(userInfoVo);
        String token = userUtils.setRedisTokenWithTime("Login", JSONObject.toJSONString(userInfoVo), expire, TimeUnit.SECONDS);
        userAuthVo.setToken(token);
        return userAuthVo;
    }
    
    @Override
    public UserInfoVo login(LoginQO qo) {
        String uid = userAuthService.checkUser(qo);
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

        return userInfoVo;
    }

    @Override
    public String register(RegisterQO qo) {
        commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
    
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        // 业主数据(user表)
        UserEntity user = new UserEntity();
        user.setUid(uuid);
        user.setId(SnowFlake.nextId());

        // 账户数据(user_auth表)
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUid(uuid);
        if (RegexUtils.isMobile(qo.getAccount())) { //手机注册
            userAuth.setMobile(qo.getAccount());
            user.setMobile(qo.getAccount());
        } else if (RegexUtils.isEmail(qo.getAccount())) { // 邮箱注册
            userAuth.setEmail(qo.getAccount());
        } else { //用户名注册
            userAuth.setUsername(qo.getAccount());
        }
        //添加业主(user表)
        save(user);
        //添加账户(user_auth表)
        userAuthService.save(userAuth);
        return uuid;
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
        BeanUtils.copyProperties(proprietorQO, userEntity);
        //添加业主信息 由于在注册时会像t_user表插入一条空记录为用户的id，这里直接做更新操作，
        int count = userMapper.update(userEntity, new UpdateWrapper<UserEntity>().eq("uid", proprietorQO.getUid()));
        if (count == 0) {
            return false;
        }
        //业主登记时有填写车辆信息的情况下，新增车辆
        if (proprietorQO.getHasCar()) {
            carService.addProprietorCar(proprietorQO.getCarEntityList());
        }
        //t_user_house 中插入当前这条记录 为了让物业审核
		userHouseService.saveUserHouse(userEntity.getUid(), proprietorQO.getHouseEntityList());
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

    /**
     * 根据业主id查询业主信息及业主家属信息
     * @author YuLF
     * @since  2020/12/10 16:25
     */
    @Transactional
    @Override
    public UserInfoVo proprietorQuery(String userId) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid",userId).select("real_name,sex,detail_address,avatar_url"));
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtil.copyProperties(userEntity, userInfoVo);
        List<HouseMemberEntity> houseMemberEntities = relationService.selectID(userId);
        userInfoVo.setProprietorMembers(houseMemberEntities);
        return userInfoVo;
    }
    
    /**
    * @Description: 查询业主所有小区的房屋
     * @Param: [uid]
     * @Return: java.util.List<com.jsy.community.entity.UserHouseEntity>
     * @Author: chq459799974
     * @Date: 2020/12/16
    **/
    @Override
    public List<UserHouseEntity> queryUserHouseList(String uid){
        List<Long> houseIds = userHouseService.queryUserHouseIds(uid);
        if(CollectionUtils.isEmpty(houseIds)){
            return null;
        }
        List<UserHouseEntity> houses = houseService.queryUserHouses(houseIds);
        HashSet<Long> communityIdSet = new HashSet<>();
        for(UserHouseEntity userHouseEntity : houses){
            communityIdSet.add(userHouseEntity.getCommunityId());
        }
        Map<Long, Map<Long,String>> communityMap = communityService.queryCommunityNameByIdBatch(communityIdSet);
        for(UserHouseEntity userHouseEntity : houses){
            userHouseEntity.setCommunityName(communityMap.get(BigInteger.valueOf(userHouseEntity.getCommunityId())).get("name"));
        }
        return houses;
    }
}
