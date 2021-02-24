package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PicUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserInfoVo;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        if( Objects.isNull(userEntity) || !Objects.equals(userEntity.getIsRealAuth(), BusinessConst.CERTIFIED)){
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
     * 用户基本信息 [我的房屋] -> 编辑资料 点击后的信息
     * @author YuLF
     * @Param  cid      社区id
     * @Param  hid      房屋id
     * @since  2021/2/23 17:22
     */
    @Login
    @ApiOperation("我的房屋详情")
    @ApiImplicitParams({@ApiImplicitParam(name="cid", value = "社区id", paramType = "query" , dataType="Long", dataTypeClass=Long.class),
    @ApiImplicitParam( name = "hid", value = "房屋id", paramType = "query", dataType = "Long", dataTypeClass = Long.class)})
    @GetMapping("/info/details")
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
     * 在2021.2.23 此方法已过期
     * 新的方法参见当前类
     * userInfo()
     * userInfoDetails()
     * 业主详情接口
     * @author YuLF
     * @since  2020/12/18 11:39
     */
    @Login
    @Deprecated
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
    
    @ApiOperation("身份证照片识别")
    @PostMapping("idCard/distinguish")
    public CommonResult distinguishIdCard(MultipartFile file,@RequestParam String type){
    	if(PicContentUtil.ID_CARD_PIC_SIDE_FACE.equals(type) || PicContentUtil.ID_CARD_PIC_SIDE_BACK.equals(type)){
            Map<String, Object> returnMap = PicContentUtil.getIdCardPicContent(Base64Util.fileToBase64Str(file), type);
            return returnMap != null ? CommonResult.ok(PicContentUtil.getIdCardPicContent(Base64Util.fileToBase64Str(file),type)) : CommonResult.error("识别失败");
	    }else{
    		return CommonResult.error("缺少身份证正反面参数");
	    }
    }

    //TODO 用户实名认证
    
}
