package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.MembersQO;
import com.jsy.community.qo.UserHouseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.ControlVO;
import com.jsy.community.vo.UserHouseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 我的房屋
 * @author: Hu
 * @create: 2021-08-17 14:49
 **/
@RequestMapping("/user/house")
@Api(tags = "用户房屋")
@RestController
@ApiJSYController
@Login
public class UserHouseController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserHouseService userHouseService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Description: 我的房屋
     * @author: Hu
     * @since: 2021/8/17 14:52
     * @Param:
     * @return:
     */
    @ApiOperation("我的房屋")
    @PostMapping("details")
    public CommonResult details(@RequestBody UserHouseQO userHouseQO){
        Integer status = userService.userIsRealAuth(UserUtils.getUserId());
        if (status!=null){
            if (status==0||status.equals(0)){
                return CommonResult.error(40001,"未实名认证");
            }
        }
        //查询所有权限
        ControlVO controlVO = UserUtils.getPermissions(UserUtils.getUserId(), redisTemplate);

        for (ControlVO permission : controlVO.getPermissions()) {
            //找到当前房间权限
            if (userHouseQO.getHouseId().equals(permission.getHouseId())){
                //如果等于1代表业主
                if (permission.getAccessLevel()==1){
                    return CommonResult.ok(userHouseService.userHouseDetails(userHouseQO, UserUtils.getUserId()));
                } else {
                    //如果等于2代表家属
                    if (permission.getAccessLevel()==2){
                        return CommonResult.ok(userHouseService.memberHouseDetails(userHouseQO, UserUtils.getUserId()));
                    } else {
                        //如果等于3代表租户
                        if (permission.getAccessLevel()==3){
                            return CommonResult.ok(userHouseService.lesseeHouseDetails(userHouseQO, UserUtils.getUserId()));
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @Description: 房屋认证
     * @author: Hu
     * @since: 2021/8/17 14:52
     * @Param:
     * @return:
     */
    @ApiOperation("房屋认证")
    @PostMapping("attestation")
    public CommonResult attestation(@RequestBody UserHouseQO userHouseQO){
        userHouseService.attestation(userHouseQO, UserUtils.getUserId());
        return CommonResult.ok();
    }

    /**
     * @Description: 编辑查询 and 切换房屋查询
     * @author: Hu
     * @since: 2021/8/17 14:52
     * @Param:
     * @return:
     */
    @ApiOperation("编辑查询 and 切换房屋查询")
    @GetMapping("selectHouse")
    public CommonResult update(@RequestParam Long communityId){
        List<UserHouseVO> houseMemberVOS = userHouseService.selectHouse(communityId, UserUtils.getUserId());
        return CommonResult.ok(houseMemberVOS);
    }

    /**
     * @Description: 家属或者租客更新
     * @author: Hu
     * @since: 2021/8/17 14:52
     * @Param:
     * @return:
     */
    @ApiOperation("家属或者租客更新")
    @PutMapping("members/save")
    public CommonResult membersUpdate(@RequestBody MembersQO membersQO){
        ValidatorUtils.validateEntity(membersQO,MembersQO.MembersVerify.class);
        userHouseService.membersSave(membersQO, UserUtils.getUserId());
        return CommonResult.ok();
    }
    /**
     * @Description: 业主家属删除接口
     * @author: Hu
     * @since: 2021/8/17 14:52
     * @Param:
     * @return:
     */
    @ApiOperation("业主家属删除接口")
    @DeleteMapping("members/delete")
    public CommonResult membersDelete(@RequestParam String ids){
        userHouseService.membersDelete(ids, UserUtils.getUserId());
        return CommonResult.ok();
    }




}
