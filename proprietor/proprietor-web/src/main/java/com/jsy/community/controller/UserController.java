package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.RealnameBlinkInitQO;
import com.jsy.community.qo.RealnameBlinkQueryQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.ControlVO;
import com.jsy.community.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.client.methods.HttpGet;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;


/**
 * 业主控制器
 *
 * @author ling
 * @since 2020-11-11 15:47
 */
@RequestMapping("user")
@Api(tags = "用户控制器")
@RestController
@ApiJSYController
public class UserController {
    @DubboReference(version = Const.version, group = Const.group, check = false, timeout = 10000)
    private IUserAuthService userAuthService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserService userService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService carService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserAuthService iUserAuthService;
    
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserUroraTagsService userUroraTagsService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserHouseService userHouseService;

    
    private static final String BUCKETNAME_ID_CARD = "id-card"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
    
    @Resource
    private RedisTemplate redisTemplate;
    
    @ApiOperation("更新极光regId")
    @Login
    @PutMapping("regId")
    public CommonResult updateUserRegId(@RequestParam String regId){
        //TODO 苹果过审用 防止报错暂时生成随机串
        regId = UUID.randomUUID().toString().replace("-","");
        boolean result = userService.updateUserRegId(regId, UserUtils.getUserId());
        return result ? CommonResult.ok("离线推送设备id设置成功") : CommonResult.error(JSYError.INTERNAL.getCode(),"离线推送设备id设置失败");
    }
    
    /**
    * @Description: 业主或亲属 获取/刷新 门禁权限
     * @Param: [communityId]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2020/12/23
    **/
    @GetMapping("longAccess")
    @ApiOperation("业主或亲属 获取/刷新 门禁权限")
    @Login
    public CommonResult getLongAccess(@RequestParam("communityId") Long communityId){
        Map<String, String> returnMap = userService.getAccess(UserUtils.getUserId(), communityId);
        return returnMap.get("access") != null ? CommonResult.ok(returnMap,"获取成功") : CommonResult.error(JSYError.INTERNAL.getCode(),returnMap.get("msg"));
    }

    /**
     * 用户基本信息 [我的房屋] 点击后的信息
     * @author YuLF
     * @since  2021/2/23 17:22
     */
    @Login
    @ApiOperation("我的房屋")
    @GetMapping("/info")
    public CommonResult<?> userInfo(@RequestParam(required = false) Long houseId){
        String uid = UserUtils.getUserId();
        if( Objects.nonNull(houseId) ){
            return switchHouse(houseId);
        }
        UserEntity userEntity = userService.getRealAuthAndHouseId(uid);
        //未实名认证
        if( Objects.isNull(userEntity) || Objects.equals(userEntity.getIsRealAuth(), BusinessConst.NO_REAL_NAME_AUTH)){
            throw new JSYException(JSYError.NO_REAL_NAME_AUTH);
        }
        //未认证房屋
        if( Objects.isNull(userEntity.getHouseId()) ){
            throw new JSYException(JSYError.NO_AUTH_HOUSE);
        }
        //根据用户id和房屋id 查出用户姓名、用户地址、和家属信息
        UserInfoVo userInfoVo = userService.getUserAndMemberInfo(uid, userEntity.getHouseId());
        return CommonResult.ok(userInfoVo);
    }


    /**
     * @author YuLF
     * @since  2021/3/15 14:34
     */
    @Login
    @ApiOperation("切换房屋选择房屋业主信息及业主家属信息查询接口")
    @GetMapping("switchHouse")
    public CommonResult<UserInfoVo> switchHouse(@RequestParam Long houseId) {
        return CommonResult.ok(userService.proprietorQuery(UserUtils.getUserId(), houseId));
    }
    
    

    /**
     * @Description: 删除业主车辆
     * @author: Hu
     * @since: 2021/7/8 14:00
     * @Param: [houseId]
     * @return: com.jsy.community.vo.CommonResult<com.jsy.community.vo.UserInfoVo>
     */
    @Login
    @ApiOperation("切换房屋选择房屋业主信息及业主家属信息查询接口")
    @DeleteMapping("delCar")
    public CommonResult deleteCar(@RequestParam Long id) {
        userService.deleteCar(UserUtils.getUserId(), id);
        return CommonResult.ok();
    }


    /**
     * 新的方法请调用 我的详情 GetMapping("details")
     * GetMapping("/info/details")
     * 用户基本信息 [我的房屋] -> 编辑资料 点击后的信息
     * @author YuLF
     * @Param  cid      社区id
     * @Param  hid      房屋id
     * @since  2021/2/23 17:22
     */
    @Login
    @Deprecated
    @ApiOperation("我的房屋详情")
    @ApiImplicitParams({@ApiImplicitParam(name="cid", value = "社区id", paramType = "query" , dataType="Long", dataTypeClass=Long.class),
    @ApiImplicitParam( name = "hid", value = "房屋id", paramType = "query", dataType = "Long", dataTypeClass = Long.class)})
    public CommonResult<UserInfoVo> userInfoDetails(@RequestParam Long cid, @RequestParam Long hid){
        return CommonResult.ok(userService.userInfoDetails(cid, hid, UserUtils.getUserId()));
    }


    // TODO 接口已经废弃,不使用了,暂未彻底删除
    /**
     * 【用户】业主更新信息
     * PutMapping("update")
     *  用户操作更新  新方法至 -> updateImprover
     * @author YuLF
     * @Param userEntity        需要更新 实体参数
     * @return 返回更新成功!
     * @since 2020/11/27 15:03
     */
//    @Login
//    @Deprecated
//	@ApiOperation("业主信息更新")
//    public CommonResult<Boolean> proprietorUpdate(@RequestBody ProprietorQO qo) {
//		//3.更新业主房屋信息和车辆信息
//        qo.setUid(UserUtils.getUserId());
//        if( Objects.isNull(qo.getHasCar()) ){
//            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "必须指定hasCar!");
//        }
//        //如果有车 则批量验证车辆信息
//        if( qo.getHasCar() ){
//            qo.getCars().forEach( car ->  ValidatorUtils.validateEntity(car, CarQO.CarValidated.class));
//        }
//        if( CollectionUtils.isEmpty(qo.getHouses()) ){
//            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "房屋未指定!");
//        }
//        //房屋数据业务唯一id、房屋id、社区id边界有效性验证
//        qo.getHouses().forEach( house -> ValidatorUtils.validateEntity( house, UserHouseQo.UpdateHouse.class ));
//        return userService.proprietorUpdate(qo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
//    }


    @Login
    @ApiOperation("业主信息更新")
    @PutMapping("/info/update")
    public CommonResult<Boolean> updateImprover(@RequestBody ProprietorQO qo) {
        //3.更新业主房屋信息和车辆信息
        qo.setUid(UserUtils.getUserId());
        if( Objects.isNull(qo.getHasCar()) ){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "必须指定hasCar!");
        }
        //如果有车 则批量验证车辆信息
        if( qo.getHasCar() ){
            qo.getCars().forEach( car ->  ValidatorUtils.validateEntity(car, CarQO.CarValidated.class));
        }
        if( !CollectionUtils.isEmpty(qo.getHouses()) ){
            //房屋数据业务唯一id、房屋id、社区id边界有效性验证
            qo.getHouses().forEach( house -> ValidatorUtils.validateEntity( house, UserHouseQo.UpdateHouseImprover.class ));
        }
        return userService.updateImprover(qo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    /**
     * 获取证件类型常量
     * @author YuLF
     * @since  2021/2/25 11:42
     */
    @Login
    @ApiOperation("证件类型常量")
    @GetMapping("/identificationType")
    public CommonResult<Map<Integer, String>> getIdentificationType(){
        return CommonResult.ok(BusinessEnum.IdentificationType.getKv());
    }

    /**
     * @author YuLF
     * @since  2021/2/23 17:23
     */
    @Login
    @ApiOperation("业主头像上传")
    @PostMapping("uploadAvatar")
    public CommonResult<String> uploadAvatar(MultipartFile avatar) {
        PicUtil.imageQualified(avatar);
        return CommonResult.ok(MinioUtils.upload(avatar, BusinessConst.AVATAR_BUCKET_NAME));
    }


    /**
     * @author YuLF
     * @since  2021/2/23 17:23
     */
    @Login
    @ApiOperation("业主人脸头像上传")
    @PostMapping("uploadFaceAvatar")
    public CommonResult<String> uploadFaceAvatar(MultipartFile faceAvatar) {
        PicUtil.imageQualified(faceAvatar);
        return CommonResult.ok(MinioUtils.upload(faceAvatar, BusinessConst.FAVE_AVATAR_BUCKET_NAME), "上传成功!");
    }

    /**
     * 查询业主及家属信息
     * @author YuLF
     * @since  2020/12/18 11:39
     */
    @Login
    @ApiOperation("业主信息及业主家属信息查询接口")
    @GetMapping("query")
    public CommonResult<UserInfoVo> proprietorQuery(@RequestParam Long houseId) {
        return CommonResult.ok(userService.proprietorQuery( UserUtils.getUserId(), houseId), "查询成功!");
    }

    /**
     * 业主详情接口
     *
     * @author YuLF
     * @since  2020/12/18 11:39
     */
    @Login
    @ApiOperation("业主信息详情查询接口")
    @GetMapping("details")
    public CommonResult<UserInfoVo> details() {
        return CommonResult.ok(userService.proprietorDetails(UserUtils.getUserId()));
    }
    
    
    /**
    * @Description: 查询用户所有社区(房屋已认证的)
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult<java.util.Collection<java.util.Map<java.lang.String,java.lang.Object>>>
     * @Author: chq459799974
     * @Date: 2021/3/31
    **/
    @Login
    @ApiOperation("查询用户所有社区(房屋已认证的)")
    @GetMapping("communityList")
    public CommonResult<Collection<Map<String, Object>>> queryUserHousesOfCommunity(){
        return CommonResult.ok(userService.queryUserHousesOfCommunity(UserUtils.getUserId()));
    }
    
    /**
    * @Description: 查询业主所有社区的房屋
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.UserHouseEntity>>
     * @Author: chq459799974
     * @Date: 2020/12/16
    **/
    @Login
    @ApiOperation("查询业主所有社区的房屋")
    @GetMapping("houseList")
    public CommonResult<List<HouseEntity>> queryUserHouseList(){
        return CommonResult.ok(userService.queryUserHouseList(UserUtils.getUserId()));
    }

    /**
    * @Description: 查询用户极光推送tags
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/14
    **/
    @Login
    @ApiOperation("查询用户极光推送tags")
    @GetMapping("urora/tags")
    public CommonResult queryUroraTags(){
        return CommonResult.ok(userUroraTagsService.queryUroraTags(UserUtils.getUserId()));
    }
    
    /**
    * @Description: 身份证照片识别
     * @Param: [file, type]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/3/2
    **/
    @Login
    @ApiOperation("身份证照片识别")
    @PostMapping("idCard/distinguish")
    public CommonResult distinguishIdCard(MultipartFile file,@RequestParam String type){
        PicUtil.imageQualified(file);
    	if(PicContentUtil.ID_CARD_PIC_SIDE_FACE.equals(type) || PicContentUtil.ID_CARD_PIC_SIDE_BACK.equals(type)){
            Map<String, Object> returnMap = PicContentUtil.getIdCardPicContent(Base64Util.fileToBase64Str(file), type);
            if(returnMap != null){
                //上传图片
                String filePath = MinioUtils.upload(file, BUCKETNAME_ID_CARD);
                //redis暂存身份证照片，redis-hash     ID_CARD:uid:type(face/back) url
                redisTemplate.opsForHash().put("ID_CARD:" + UserUtils.getUserId(),type,filePath);
                return CommonResult.ok(returnMap);
            }else{
                return CommonResult.error("识别失败");
            }
	    }else{
    		return CommonResult.error("缺少身份证正反面参数");
	    }
    }

    /**
    * @Description: 眨眼版实人验证初始化
     * @Param: [realnameBlinkInitQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/3/2
    **/
    @Login
    @ApiOperation("眨眼版实人验证初始化")
    @PostMapping("realName/blink/init")
    public JSONObject initBlink(@RequestBody RealnameBlinkInitQO realnameBlinkInitQO){
        ValidatorUtils.validateEntity(realnameBlinkInitQO);
        System.out.println(realnameBlinkInitQO);
        JSONObject jsonObject = RealnameAuthUtils.initBlink(realnameBlinkInitQO);
        System.out.println("=========1========");
        System.out.println(jsonObject);
        System.out.println("==========2=======");
        return jsonObject;
    }
    
    /**
    * @Description: 眨眼版实人查询结果
     * @Param: [realnameBlinkQueryQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/3/2
    **/
    @Login
    @ApiOperation("眨眼版实人查询结果")
    @PostMapping("realName/blink/result")
    public CommonResult getBlinkResult(@RequestBody RealnameBlinkQueryQO realnameBlinkQueryQO){
        ValidatorUtils.validateEntity(realnameBlinkQueryQO);
        JSONObject blinkResult = RealnameAuthUtils.getBlinkResult(realnameBlinkQueryQO);
        System.out.println("=========1========");
        System.out.println(blinkResult);
        System.out.println("==========2=======");
        if("0000".equals(blinkResult.getString("code"))){
            if("T".equals(blinkResult.getString("passed"))){
            //后修改用户信息
                UserEntity userEntity = new UserEntity();
                userEntity.setUid(UserUtils.getUserId());
                userEntity.setRealName(blinkResult.getString("certName"));
                userEntity.setIdCard(blinkResult.getString("certNo"));
                userEntity.setDetailAddress(realnameBlinkQueryQO.getDetailAddress());
                userEntity.setIsRealAuth(BusinessConst.CERTIFIED_FULL);
                userEntity.setIdentificationType(BusinessConst.IDENTIFICATION_TYPE_IDCARD);
                //设置身份证照片
                Object idCardPicFace = redisTemplate.opsForHash().get("ID_CARD:" + UserUtils.getUserId(), PicContentUtil.ID_CARD_PIC_SIDE_FACE);
                userEntity.setIdCardPicFace(idCardPicFace != null ? String.valueOf(idCardPicFace) : null);
                Object idCardPicBack = redisTemplate.opsForHash().get("ID_CARD:" + UserUtils.getUserId(), PicContentUtil.ID_CARD_PIC_SIDE_BACK);
                userEntity.setIdCardPicBack(idCardPicBack != null ? String.valueOf(idCardPicBack) : null);
                //上传人脸url并设置
                HttpGet httpGet = MyHttpUtils.httpGetWithoutParams(blinkResult.getString("photoUrl"));
                byte[] byteData = (byte[]) MyHttpUtils.exec(httpGet,MyHttpUtils.ANALYZE_TYPE_BYTE);
                String filePath = MinioUtils.uploadPic(byteData,"face-url");
                userEntity.setFaceUrl(filePath);
                //更新信息
                userService.updateUserAfterRealnameAuth(userEntity);
                //删除redis暂存的身份证图片
                redisTemplate.delete("ID_CARD:" + UserUtils.getUserId());
            }else{
                CommonResult.error(JSYError.BAD_REQUEST.getCode(),"实名认证不通过");
            }
        }else{
            CommonResult.error(JSYError.INTERNAL.getCode(),"三方远程服务异常，请联系管理员");
        }
        return CommonResult.ok(blinkResult);
    }
    
    /**
    * @Description: 用户简单信息查询(单服务，单表)
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/5/10
    **/
    @Login
    @ApiOperation("用户简单信息查询(快接口)")
    @GetMapping("info/simple")
    public CommonResult getSimpleInfo(){
        UserEntity userEntity = userService.queryUserDetailByUid(UserUtils.getUserId());
        if(userEntity == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"用户不存在");
        }
        UserEntity returnEntity = new UserEntity();
        returnEntity.setIsRealAuth(userEntity.getIsRealAuth()); //实名 0.否 1.已实名 2.已实人
        return CommonResult.ok(returnEntity,"查询成功");
    }

    /**
     * @author: Pipi
     * @description:  业主解绑房屋
     * @param: userHouseEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/6/21 14:01
     **/
    @Login
    @ApiOperation("业主解绑房屋")
    @PostMapping("/untieHouse")
    public CommonResult untieHouse(@RequestBody UserHouseEntity userHouseEntity) {
        userHouseEntity.setUid(UserUtils.getUserId());
        ValidatorUtils.validateEntity(userHouseEntity, UserHouseEntity.UntieHouse.class);
        return userHouseService.untieHouse(userHouseEntity) ? CommonResult.ok("解绑成功!") : CommonResult.error("解绑失败!");
    }

    @Login
    @ApiOperation("获取权限")
    @GetMapping("/control")
    public CommonResult control(@RequestParam("communityId") Long communityId) {
        ControlVO control = userService.control(communityId, UserUtils.getUserId());
        savePermissions(UserUtils.getUserId(),control);
        return CommonResult.ok();
    }

    /**
     * @Description: 保存权限到redis
     * @author: Hu
     * @since: 2021/8/17 9:27
     * @Param:
     * @return:
     */
    public void savePermissions(String uid,ControlVO control){
        Object o = redisTemplate.opsForValue().get("Permissions" + uid);
        if (o!=null){
            redisTemplate.delete("Permissions"+uid);
        }
        redisTemplate.opsForValue().set("Permissions:" + uid,JSONObject.toJSONString(control));
    }
}
