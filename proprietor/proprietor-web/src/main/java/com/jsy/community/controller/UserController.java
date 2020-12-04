package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;


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
    @Login
    public String test(@RequestBody BaseQO<UserEntity> qo) {
        ValidatorUtils.validateEntity(qo);
        UserInfoVo userInfo = UserUtils.getUserInfo();
        UserUtils.getUserId();
        System.out.println("111111111111....");
        return "success...";
    }

    /**
     * 【用户】业主信息登记
     * @param proprietorQO  参数实体
     * @return              返回是否登记成功
     */
    @PostMapping("proprietorRegister")
    @ApiOperation("业主信息登记")
    @Login
    public CommonResult<Boolean> proprietorRegister(@RequestBody  ProprietorQO proprietorQO) {
        //获取用户id信息
		String uid = UserUtils.getUserId() ;
        //1.数据填充  新增业主信息时，必须要携带当前用户的uid  业主id首次不需要新增，只有在审核房屋通过时，业主id才会改变
        proprietorQO.setUid(uid);
        proprietorQO.setHouseholderId(null);
        //2.验证业主信息登记必填项
        ValidatorUtils.validateEntity(proprietorQO, ProprietorQO.ProprietorRegister.class);
        //2.验证业主信息实体里面的房屋实体id是否为空，如果为空 说明前端并没有选择所属房屋
        if (proprietorQO.getHouseEntity() == null || proprietorQO.getHouseEntity().getId() == null
                || proprietorQO.getHouseEntity().getId() == 0
                || proprietorQO.getHouseEntity().getCommunityId() == null
                || proprietorQO.getHouseEntity().getCommunityId() == 0) {
            throw new ProprietorException(1, "缺失房屋id或社区id!!");
        }
        //3.有填登记车辆信息的情况下
        if (proprietorQO.getHasCar()) {
            if (null == proprietorQO.getCarEntityList()) {
                throw new ProprietorException(1, "车辆信息未填写!");
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
                carEntity.setCommunityId(proprietorQO.getHouseEntity().getCommunityId());
                carEntity.setUid(uid);
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
    @PutMapping("proprietorUpdate")
    public CommonResult<Boolean> proprietorUpdate(@RequestBody ProprietorQO proprietorQO) {
        //2.参数效验,判断用户能更新的字段 是否都是空 如果都是空，则不进入数据数据访问层
        ValidatorUtils.validateEntity(proprietorQO, ProprietorQO.proprietorUpdateValid.class);
		//3.更新业主信息
        proprietorQO.setUid(UserUtils.getUserId());
        return userService.proprietorUpdate(proprietorQO) > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    private boolean isEmpty(String str){
        return str == null || str.trim().equals("") || "null".equals(str) || "undefined".equals(str);
    }

}
