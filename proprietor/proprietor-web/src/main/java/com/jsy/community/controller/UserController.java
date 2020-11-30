package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ProprietorQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("test")
    @ApiOperation("test")
    @Login
    public List<UserAuthEntity> test(@RequestBody BaseQO<UserEntity> qo) {
        ValidatorUtils.validateEntity(qo);

        return userAuthService.getList(true);
    }

    /**
     * 【用户】业主信息登记
     * @param userEntity    参数实体
     * @return              返回是否登记成功
     */
    @PostMapping("proprietorRegister")
    @ApiOperation("业主信息登记")
    public CommonResult<Boolean> proprietorRegister(@RequestBody UserEntity userEntity) {
        //获取用户id信息
//		Long uid = JwtUtils.getUserId();
        Long uid = 12L;
        //新增业主信息时，必须要携带当前用户的uid
        userEntity.setId(uid);
        //1.验证业主信息登记必填项
        ValidatorUtils.validateEntity(userEntity, UserEntity.ProprietorRegister.class);
        //2.验证业主信息实体里面的房屋实体id是否为空，如果为空 说明前端并没有选择所属房屋
        if (userEntity.getHouseEntity() == null || userEntity.getHouseEntity().getId() == null
                || userEntity.getHouseEntity().getId() == 0
                || userEntity.getHouseEntity().getCommunityId() == null
                || userEntity.getHouseEntity().getCommunityId() == 0) {
            throw new JSYException(1, "缺失房屋id或社区id!!");
        }
        //3.有填登记车辆信息的情况下
        if (userEntity.getHasCar()) {
            if (null == userEntity.getCarEntity()) {
                throw new JSYException(1, "车辆信息未填写!");
            }
            //验证车辆信息
            ValidatorUtils.validateEntity(userEntity.getCarEntity(), CarEntity.proprietorCarValidated.class);
            if (carService.carIsExist(userEntity.getCarEntity().getCarPlate())) {
                throw new JSYException(1, "车辆车牌已经登记存在!");
            }
            //由于在登记车辆时，数据库字段也需要 uid 和 社区id
            userEntity.getCarEntity().setUid(uid);
            userEntity.getCarEntity().setCommunityId(userEntity.getHouseEntity().getCommunityId());
        }
        //登记业主信息
        return userService.proprietorRegister(userEntity) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     * 【用户】业主更新信息
     *  用户操作更新 非所有字段 只能更新 [昵称、头像地址、性别、用户所在省份ID、用户所在城市ID、用户所在区ID、用户所在详细地址]
     * @author YuLF
     * @Param userEntity        需要更新 实体参数
     * @return 返回更新成功!
     * @since 2020/11/27 15:03
     */
	@ApiOperation("业主信息更新")
    @PutMapping("proprietorUpdate")
    public CommonResult<Boolean> proprietorUpdate(@RequestBody ProprietorQO proprietorQO) {
        //1.从JWT获取用户uid
        Long uid = 12L;
        //2.参数效验,判断用户能更新的字段 是否都是空 如果都是空，则不进入数据数据访问层
        ValidatorUtils.validateEntity(proprietorQO, ProprietorQO.proprietorUpdateValid.class);
		//3.更新业主信息
        proprietorQO.setId(uid);
        return userService.proprietorUpdate(proprietorQO) > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    private boolean isEmpty(String str){
        return str == null || str.trim().equals("") || "null".equals(str) || "undefined".equals(str);
    }

}
