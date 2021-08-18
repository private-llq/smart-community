package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.UserHouseQO;
import com.jsy.community.utils.UserUtils;
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
        ControlVO controlVO = UserUtils.getPermissions(UserUtils.getUserId(), redisTemplate);
        if (userHouseQO.getHouseId().equals(controlVO.getHouseId())){
            if (controlVO.getAccessLevel().equals(1)){
                UserHouseVO houseVO = userHouseService.userHouseDetails(userHouseQO, UserUtils.getUserId());
                return CommonResult.ok(houseVO);
            }else {
                UserHouseVO houseVO = userHouseService.memberHouseDetails(userHouseQO, UserUtils.getUserId(),UserUtils.getUserInfo().getMobile());
                return CommonResult.ok(houseVO);
            }
        }else {
            for (ControlVO vo : controlVO.getPermissions()) {
                if (vo.getHouseId().equals(userHouseQO.getHouseId())){
                    if (vo.getAccessLevel().equals(1)){
                        UserHouseVO houseVO = userHouseService.userHouseDetails(userHouseQO, UserUtils.getUserId());
                        return CommonResult.ok(houseVO);
                    } else {
                        UserHouseVO houseVO = userHouseService.memberHouseDetails(userHouseQO, UserUtils.getUserId(),UserUtils.getUserInfo().getMobile());
                        return CommonResult.ok(houseVO);
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
    @PutMapping("members/update")
    public CommonResult membersUpdate(@RequestBody UserHouseQO userHouse){
        userHouseService.membersUpdate(userHouse, UserUtils.getUserId());
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
