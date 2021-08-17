package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserHouseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class UserHouseController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserHouseService userHouseService;

    /**
     * @Description: 我的房屋
     * @author: Hu
     * @since: 2021/8/17 14:52
     * @Param:
     * @return:
     */
    @ApiOperation("我的房屋")
    @GetMapping("details")
    public CommonResult details(@RequestParam Long communityId,@RequestParam Long houseId){
        UserHouseVO houseVO = userHouseService.userHouseDetails(communityId,houseId, UserUtils.getUserId());
        return CommonResult.ok(houseVO);
    }

    /**
     * @Description: 房屋认证
     * @author: Hu
     * @since: 2021/8/17 14:52
     * @Param:
     * @return:
     */
    @ApiOperation("房屋认证")
    @GetMapping("attestation")
    public CommonResult attestation(@RequestParam Long communityId,@RequestParam Long houseId){
        userHouseService.attestation(communityId,houseId, UserUtils.getUserId());
        return CommonResult.ok();
    }

    /**
     * @Description: 我的房屋编辑
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




}
