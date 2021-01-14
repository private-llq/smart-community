package com.jsy.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.mapper.UserThirdPlatformMapper;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.UserThirdPlatformQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService carService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserHouseService userHouseService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IRelationService relationService;
    
    @Autowired
    private IUserAccountService userAccountService;

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private UserThirdPlatformMapper userThirdPlatformMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private UserUtils userUtils;
    
    private long expire = 60*60*24*7; //暂时

    @Autowired
    private IHouseService houseService;
    
    @Autowired
    private ICommunityService communityService;
    
    @Autowired
    private IAlipayService alipayService;

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
    
    private UserInfoVo queryUserInfo(String uid){
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
    public UserInfoVo login(LoginQO qo) {
        String uid = userAuthService.checkUser(qo);
        return queryUserInfo(uid);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String register(RegisterQO qo) {
        commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
    
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        // 业主数据(user表)
        UserEntity user = new UserEntity();
        user.setUid(uuid);
        user.setId(SnowFlake.nextId());
        user.setRegId(qo.getRegId());

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
        //添加业主(user表)
        save(user);
        //添加账户(user_auth表)
        userAuthService.save(userAuth);
        //创建金钱账户
        userAccountService.createUserAccount(uuid);
        return uuid;
    }
    
    /**
     * 调用三方接口获取会员信息(走后端备用)(返回三方平台唯一id)
     */
    private String getUserInfoFromThirdPlatform(UserThirdPlatformQO userThirdPlatformQO){
        String thirdUid = null;
        switch (userThirdPlatformQO.getThirdPlatformType()){
            case Const.ThirdPlatformConsts.ALIPAY :
                //若前端传递了accessToken，尝试取userid
                if(!StringUtils.isEmpty(userThirdPlatformQO.getAccessToken())){
                    thirdUid = alipayService.getUserid(userThirdPlatformQO.getAccessToken());
                }
                //若前端传递的accessToekn没取到userid，用前端传递的authCode从三方取accessToken再取userid
                if(StringUtils.isEmpty(thirdUid) && !StringUtils.isEmpty(userThirdPlatformQO.getAuthCode())){
                    String accessToken = alipayService.getAccessToken(userThirdPlatformQO.getAuthCode());
                    if(StringUtils.isEmpty(accessToken)){ //第一步取accessToekn就失败了直接退出
                        break;
                    }
                    thirdUid = alipayService.getUserid(accessToken);
                }
                break;
            case Const.ThirdPlatformConsts.WECHAT :
                break;
            case Const.ThirdPlatformConsts.QQ :
                break;
        }
        if(StringUtils.isEmpty(thirdUid)){
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
    public UserAuthVo thirdPlatformLogin(UserThirdPlatformQO userThirdPlatformQO){
        //获取三方uid
        String thirdPlatformUid = getUserInfoFromThirdPlatform(userThirdPlatformQO);
        userThirdPlatformQO.setThirdPlatformId(thirdPlatformUid);
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
            .eq("third_platform_id", userThirdPlatformQO.getThirdPlatformId())
            .eq("third_platform_type",userThirdPlatformQO.getThirdPlatformType()));
        if(entity != null){
            //返回token
            UserInfoVo userInfoVo = queryUserInfo(entity.getUid());
            return createAuthVoWithToken(userInfoVo);
        }
        throw new ProprietorException("尚未绑定手机");
    }
    
    /**
     * @Description: 三方绑定手机
     * @Param: [userThirdPlatformQO]
     * @Return: com.jsy.community.vo.UserAuthVo
     * @Author: chq459799974
     * @Date: 2021/1/12
     **/
    @Transactional(rollbackFor = Exception.class)
    public UserAuthVo bindThirdPlatform(UserThirdPlatformQO userThirdPlatformQO){
        //获取三方uid
        String thirdPlatformUid = getUserInfoFromThirdPlatform(userThirdPlatformQO);
        userThirdPlatformQO.setThirdPlatformId(thirdPlatformUid);
        //手机验证码验证 不过报错
        commonService.checkVerifyCode(userThirdPlatformQO.getMobile(), userThirdPlatformQO.getCode());
        //查询是否注册
        String uid = userAuthService.queryUserIdByMobile(userThirdPlatformQO.getMobile());
        //若没有注册 立即注册
        if(StringUtils.isEmpty(uid)){
            RegisterQO registerQO = new RegisterQO();
            registerQO.setAccount(userThirdPlatformQO.getMobile());
            registerQO.setCode(userThirdPlatformQO.getCode());
            uid = register(registerQO);
        }
        //三方登录表入库
        UserThirdPlatformEntity userThirdPlatformEntity = new UserThirdPlatformEntity();
        BeanUtils.copyProperties(userThirdPlatformQO,userThirdPlatformEntity);
        userThirdPlatformEntity.setId(SnowFlake.nextId());
        userThirdPlatformEntity.setUid(uid);//把uid设置进三方登录表关联上
        userThirdPlatformMapper.insert(userThirdPlatformEntity);
        
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUid(uid);
        return createAuthVoWithToken(userInfoVo);
    }
    
    
    
    /**
     * 页面登记 业主信息
     * @author YuLF
     * @since  2020/12/18 11:39
     * @param proprietorQo 登记实体参数
     * @return             返回是否登记成功
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Boolean proprietorRegister(ProprietorQO proprietorQo) {
        //把参数对象里面的值赋值给UserEntity  使用Mybatis plus的insert需要 Entity里面写的表名
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(proprietorQo, userEntity);
        //添加业主信息 由于在注册时会像t_user表插入一条空记录为用户的id，这里直接做更新操作，
        int count = userMapper.update(userEntity, new UpdateWrapper<UserEntity>().eq("uid", proprietorQo.getUid()));
        if (count == 0) {
            return false;
        }
        //业主登记时有填写车辆信息的情况下，新增车辆
        if (proprietorQo.getHasCar()) {
            carService.addProprietorCarForList(proprietorQo.getCarEntityList());
        }
        //t_user_house 中插入当前这条记录 为了让物业审核
		userHouseService.saveUserHouse(userEntity.getUid(), proprietorQo.getHouseEntityList());
        return true;
    }


    /**
     * 【用户】业主更新信息  id等于null 或者 id == 0 表示需要新增的数据
     * @author YuLF
     * @Param proprietorQO        需要更新 实体参数
     * @since 2020/11/27 15:03
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean proprietorUpdate(ProprietorQO qo) {
        //========================================== 业主车辆 =========================================================
        //如果有车辆的情况下 更新或新增 车辆信息
        if(qo.getHasCar()){
            //如果有需要新增的数据 id == null 或者 == 0 就是需要新增 只要有一个条件满足即返回true
            List<CarEntity> carEntityList = qo.getCarEntityList();
            boolean b = carEntityList.stream().anyMatch(w -> w.getId() == null || w.getId() == 0);
            //为车辆信息设置基本信息
            carEntityList.forEach(e ->{
                e.setOwner(qo.getRealName());
                e.setUid(qo.getUid());
                e.setCommunityId(qo.getHouseEntityList().get(0).getCommunityId());
                e.setContact(qo.getMobile());
            });
            //新增车辆信息
            if(b){
                //从提交的List中取出Id == null 并且ID == 0的数据 重新组成一个List 代表需要新增的数据
                List<CarEntity> any = carEntityList.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
                //从更新车辆的集合中 移除 需要 新增的数据
                carEntityList.removeAll(any);
                //新增数据需要设置id
                any.forEach( e -> {
                    e.setId(SnowFlake.nextId());
                });
                //批量新增车辆
                carService.addProprietorCarForList(any);
            }
            //批量更新车辆信息
            carService.updateBatchById(carEntityList);
        }
        //========================================== 业主房屋 =========================================================
        List<UserHouseEntity> houseList = qo.getHouseEntityList();
        //验证房屋信息是否有需要新增的数据
        boolean b = houseList.stream().anyMatch(w -> w.getId() == null || w.getId() == 0);
        //如果存在需要新增的房屋的数据
        if(b){
            List<UserHouseEntity> any = houseList.stream().filter(w -> w.getId() == null || w.getId() == 0).collect(Collectors.toList());
            houseList.removeAll(any);
            //为房屋添加基本信息 uid、id、
            any.forEach(e -> {
                e.setId(SnowFlake.nextId());
                e.setUid(qo.getUid());
            });
            //批量新增房屋信息
            userHouseService.addHouseBatch(any);
        }
        //批量更新房屋信息
        userHouseService.updateBatchById(houseList);
        //========================================== 业主 =========================================================
        //业主信息更新
        return userMapper.proprietorUpdate(qo) > 0;
    }


    /**
     * 根据业主id查询业主信息及业主家属信息
     * @author YuLF
     * @since  2020/12/10 16:25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVo proprietorQuery(String userId, Long houseId) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid",userId).select("real_name,sex,detail_address,avatar_url"));
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtil.copyProperties(userEntity, userInfoVo);
        //业主家属查询
        List<HouseMemberEntity> houseMemberEntities = relationService.selectID(userId, houseId);
        userInfoVo.setProprietorMembers(houseMemberEntities);
        //业主房屋查询
        List<HouseVo> houseVos = userMapper.queryUserHouseById(userId, houseId);
        userInfoVo.setProprietorHouses(houseVos);
        return userInfoVo;
    }
    
    /**
    * @Description: 查询业主所有小区的房屋
     * @Param: [uid]
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Author: chq459799974
     * @Date: 2020/12/16
    **/
    @Override
    public List<HouseEntity> queryUserHouseList(String uid){
        
        //步骤一
        /* t_user_house */
        List<UserHouseEntity> userHouseList = userHouseService.queryUserHouses(uid);
        if(CollectionUtils.isEmpty(userHouseList)){
            return null;
        }
        HashSet<Long> communityIdSet = new HashSet<>();
        LinkedList<Long> houseIdList = new LinkedList<>();
        for(UserHouseEntity userHouseEntity : userHouseList){
            communityIdSet.add(userHouseEntity.getCommunityId());
            houseIdList.add(userHouseEntity.getHouseId());
        }
        
        //步骤二
        //查社区id,房间id,楼栋id,地址拼接
        //补buildingId如果pid!=0 继续往上查
        /* t_house */
        List<HouseEntity> houses = houseService.queryHouses(houseIdList);
        //组装buildingId
        for(HouseEntity tempEntity : houses){
            //递归查父节点，组装楼栋级节点id进buildingId
            setBuildingId(tempEntity);
        }
        
        //步骤三
        //查小区名
        /* t_community */
        Map<String, Map<String,Object>> communityMap = communityService.queryCommunityNameByIdBatch(communityIdSet);
        for(HouseEntity userHouseEntity : houses){
            userHouseEntity.setCommunityName(String.valueOf(communityMap.get(BigInteger.valueOf(userHouseEntity.getCommunityId())).get("name")));
        }
        return houses;
    }
    
    private HouseEntity setBuildingId(HouseEntity tempEntity){
        Long pid = 0L; //id和pid相同的问题数据导致死循环
        HouseEntity parent = houseService.getParent(tempEntity);
        if(parent != null && parent.getPid() != 0 && pid != parent.getPid()){
            pid = tempEntity.getPid();
            HouseEntity houseEntity = setBuildingId(parent);
            tempEntity.setBuildingId(houseEntity.getBuildingId());
        }
        return parent;
    }
    
    /**
     * 业主详情查看
     * @param userId	    用户ID
     * @Param houseId       房屋ID
     * @author YuLF
     * @since  2020/12/18 11:39
     * @return			返回业主详情信息
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
     * @Return: java.util.Map<java.lang.String,java.lang.String>
     * @Author: chq459799974
     * @Date: 2020/12/23
     **/
    @Override
    public Map<String,String> getAccess(String uid, Long communityId){
        Map<String, String> returnMap = new HashMap<>();
        //获取登录用户手机号
        String mobile = userMapper.queryUserMobileByUid(uid);
        //检查身份
        if(canGetLongAccess(uid,communityId,mobile)){
            //刷新通用权限并返回最新数据 //TODO 目前是返回一个token 后期根据硬件接口需要修改
            String access = setUserLongAccess(uid);
            returnMap.put("access",access);
        }else{
            returnMap.put("msg","当前用户不是小区业主或家属");
        }
        return returnMap;
    }
    
    /**
     * @Description: 查询用户是否存在
     * @Param: [uid]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: chq459799974
     * @Date: 2021/1/13
     **/
    public Map<String,Object> checkUserAndGetUid(String uid){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isEmpty(uid)){
            map.put("exists",false);
            return map;
        }
        Integer count = userMapper.selectCount(new QueryWrapper<UserEntity>().eq("uid", uid));
        if(count == 1){
            map.put("exists",true);
            map.put("uid",uid);
        }else{
            map.put("exists",false);
        }
        return map;
    }
    
    //查询身份(是不是小区业主或家属)
    private boolean canGetLongAccess(String uid, Long communityId, String mobile){
        if(userHouseService.hasHouse(uid,communityId)
            || relationService.isHouseMember(mobile,communityId)){
            return true;
        }
        return false;
    }
    
    //设置不过期门禁
    private String setUserLongAccess(String uid){
        String token = UUID.randomUUID().toString().replace("-", "");
        VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
        visitorEntryVO.setToken(token);
        redisTemplate.opsForValue().set("UEntry:" + uid, JSON.toJSONString(visitorEntryVO));
        return token;
    }
    
}
