package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Auth;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
    
    @PostMapping("test")
//    @Login
    @Auth
    public String test(@RequestBody BaseQO<UserEntity> qo) {
        ValidatorUtils.validateEntity(qo);
//        UserInfoVo userInfo = UserUtils.getUserInfo();
        UserUtils.getUserId();
        System.out.println("111111111111....");
        if(true){
           throw new DuplicateKeyException("测试数据重复异常");
        }
        return "success...";
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
     * 【用户】业主信息登记
     * @param proprietorQO  参数实体
     * @author YuLF
     * @since  2020/11/15 11:29
     * @return              返回是否登记成功
     */
    @Login
    @PostMapping("register")
    @ApiOperation("业主信息登记")
    public CommonResult<Boolean> proprietorRegister(@RequestBody  ProprietorQO proprietorQO) {
        String userId = UserUtils.getUserId();
        //1.数据填充  新增业主信息时，必须要携带当前用户的uid  业主id首次不需要新增，只有在审核房屋通过时，业主id才会改变
        proprietorQO.setUid(userId);
        proprietorQO.setHouseholderId(null);
        //2.验证业主信息登记必填项
        ValidatorUtils.validateEntity(proprietorQO, ProprietorQO.ProprietorRegister.class);
        //2.1 验证业主信息实体里面的房屋实体id是否为空，如果为空 说明前端并没有选择所属房屋
        if (proprietorQO.getHouseEntityList() == null || proprietorQO.getHouseEntityList().isEmpty()) {
            throw new ProprietorException(1, "缺失房屋登记信息!");
        }
        //验证房屋第一个id和社区id参数值是否正确 至少需要登记一个房屋

        //3.有填登记车辆信息的情况下
        if (proprietorQO.getHasCar()) {
            if (null == proprietorQO.getCarEntityList()  || proprietorQO.getCarEntityList().isEmpty()) {
                throw new ProprietorException(1, "缺失车辆登记信息!");
            }
            //通过uid 查询t_user_auth表的用户手机号码
            String userMobile = iUserAuthService.selectContactById(proprietorQO.getUid());
            //t_user_auth 表中用户没有注册
            if(userMobile == null){
                throw new ProprietorException(JSYError.FORBIDDEN.getCode(), "用户不存在!");
            }
            //验证所有车辆信息 验证成功没有抛异常 则把当前这个对象的一些社区id 所属人 手机号码 设置进去 方便后续车辆登记
            for (CarEntity carEntity : proprietorQO.getCarEntityList()){
                ValidatorUtils.validateEntity(carEntity, CarEntity.proprietorCarValidated.class);
                carEntity.setCommunityId(proprietorQO.getHouseEntityList().get(0).getCommunityId());
                carEntity.setUid(userId);
                carEntity.setOwner(proprietorQO.getRealName());
                carEntity.setContact(userMobile);
            }
        }
        //登记业主相关信息
        return userService.proprietorRegister(proprietorQO) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
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
    public CommonResult<Boolean> proprietorUpdate(@RequestBody ProprietorQO proprietorQO) {
        ValidatorUtils.validateEntity(proprietorQO, ProprietorQO.proprietorUpdateValid.class);
		//3.更新业主信息
        proprietorQO.setUid(UserUtils.getUserId());
        if( proprietorQO.getHasCar() == null ){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "必须指定hasCar!");
        }
        if( proprietorQO.getHouseEntityList() == null || proprietorQO.getHouseEntityList().isEmpty() ){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "房屋未指定!");
        }
        return userService.proprietorUpdate(proprietorQO) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    /**
     * 查询业主及家属信息
     * @author YuLF
     * @since  2020/12/18 11:39
     */
    @Login
    @ApiOperation("业主信息及业主家属信息查询接口")
    @PostMapping("query")
    public CommonResult<UserInfoVo> proprietorQuery(@RequestParam Long houseId) {
        String userId = UserUtils.getUserId();
        UserInfoVo userInfoVo = userService.proprietorQuery(userId, houseId);
        return CommonResult.ok(userInfoVo);
    }

    /**
     * 业主详情接口
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
    
}
