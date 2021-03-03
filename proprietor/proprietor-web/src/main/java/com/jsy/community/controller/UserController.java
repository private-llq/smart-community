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
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.RealnameBlinkInitQO;
import com.jsy.community.qo.RealnameBlinkQueryQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserInfoVo;
import io.swagger.annotations.*;
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
    
    private static final String BUCKETNAME_ID_CARD = "id-card"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
    
    @Resource
    private RedisTemplate redisTemplate;

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
    public CommonResult<?> userInfo(){
        String uid = UserUtils.getUserId();
        UserEntity userEntity = userService.getRealAuthAndHouseId(uid);
        //未实名认证
        if( Objects.isNull(userEntity) || Objects.equals(userEntity.getIsRealAuth(), BusinessConst.NO_REAL_NAME_AUTH)){
            throw new JSYException(JSYError.NO_REAL_NAME_AUTH);
        }
        //未认证房屋
        if( Objects.isNull(userEntity.getHouseholderId()) ){
            throw new JSYException(JSYError.NO_AUTH_HOUSE);
        }
        //根据用户id和房屋id 查出用户姓名、用户地址、和家属信息
        UserInfoVo userInfoVo = userService.getUserAndMemberInfo(uid, userEntity.getHouseholderId());
        return CommonResult.ok(userInfoVo);
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





    /**
     * 【用户】业主更新信息
     *  用户操作更新 非所有字段 只能更新 [昵称、头像地址、性别、用户所在省份ID、用户所在城市ID、用户所在区ID、用户所在详细地址]
     * @author YuLF
     * @Param userEntity        需要更新 实体参数
     * @return 返回更新成功!
     * @since 2020/11/27 15:03
     */
    @Login
	@ApiOperation("业主信息更新")
    @PutMapping("update")
    public CommonResult<Boolean> proprietorUpdate(@RequestBody ProprietorQO qo) {
		//3.更新业主房屋信息和车辆信息
        qo.setUid(UserUtils.getUserId());
        if( Objects.isNull(qo.getHasCar()) ){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "必须指定hasCar!");
        }
        //如果有车 则批量验证车辆信息
        if( qo.getHasCar() ){
            qo.getCars().forEach( car ->  ValidatorUtils.validateEntity(car, CarQO.CarValidated.class));
        }
        if( CollectionUtils.isEmpty(qo.getHouses()) ){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "房屋未指定!");
        }
        //房屋数据业务唯一id、房屋id、社区id边界有效性验证
        qo.getHouses().forEach( house -> ValidatorUtils.validateEntity( house, UserHouseQo.UpdateHouse.class ));
        return userService.proprietorUpdate(qo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    /**
     * 获取证件类型常量
     * @author YuLF
     * @since  2021/2/25 11:42
     */
    @Login
    @ApiOperation("证件类型常量")
    @GetMapping("/identificationType")
    public Map<Integer, String> getIdentificationType(){
        return BusinessEnum.IdentificationType.getKv();
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
        return CommonResult.ok(MinioUtils.upload(faceAvatar, BusinessConst.FAVE_AVATAR_BUCKET_NAME));
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
        String userId = UserUtils.getUserId();
        UserInfoVo userInfoVo = userService.proprietorQuery(userId, houseId);
        return CommonResult.ok(userInfoVo);
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
        UserInfoVo userInfoVo = userService.proprietorDetails(UserUtils.getUserId());
        return CommonResult.ok(userInfoVo);
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
            //实名认证后修改用户信息
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
                String filePath = MinioUtils.upload(byteData,"face-url");
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

//    @RequestMapping("/test")
//    public String test(){
//        HttpGet httpGet = MyHttpUtils.httpGetWithoutParams("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF");
//        byte[] byteData = (byte[]) MyHttpUtils.exec(httpGet,MyHttpUtils.ANALYZE_TYPE_BYTE);
//        String filePath = MinioUtils.upload(byteData,"face-url");
//        return filePath;
//    }
}
