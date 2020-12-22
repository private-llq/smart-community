package com.jsy.community.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.CommunityVO;
import com.jsy.community.vo.sys.SysInformVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/inform")
@Api(tags = "社区通知消息")
@RestController
@ApiJSYController
public class UserInformController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserInformService userInformService;



    /**
     * @Description: 保存已读人员到数据库
     * @author: Hu
     * @since: 2020/12/4 14:46
     * @Param:
     * @return:
     */
    @ApiOperation("添加已读通知人员")
    @PostMapping("/add")
    @Login
    public CommonResult save(@RequestBody UserInformEntity userInformEntity) {
        String userId = UserUtils.getUserId();
        userInformEntity.setId(SnowFlake.nextId());
        return userInformService.save(userInformEntity)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }

    /**
     * @author YuLF
     * @since  2020/12/21 14:52
     */
    @Login
    @PostMapping("/totalList")
    @ApiOperation("用户社区总未读消息列表查看")
    public CommonResult<List<CommunityVO>> totalCommunityInformList(@RequestBody BaseQO<?> baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        List<CommunityVO> list = userInformService.totalCommunityInformList(UserUtils.getUserId(), baseQO.getPage() , baseQO.getSize());
        return CommonResult.ok(list);
    }

    /**
     * @author YuLF
     * @since  2020/12/21 14:52
     */
    @Login
    @GetMapping("/sys/details")
    @ApiOperation("用户系统消息详情查看")
    public CommonResult<SysInformEntity> totalSysInformList(@RequestParam Long informId){
        //根据id验证系统消息是否存在
        if(!userInformService.sysInformExist(informId)){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "系统消息不存在!");
        }
        return CommonResult.ok(userInformService.totalSysInformList(informId, UserUtils.getUserId()));
    }

    @Login
    @GetMapping("/sys/list")
    @ApiOperation("系统消息主页列表")
    public CommonResult<List<SysInformVO>> userSysInformList(@RequestParam Long page, @RequestParam Long size){
        BaseQO<SysInformVO> baseQO = new BaseQO<>();
        baseQO.setPage(page);
        baseQO.setSize(size);
        ValidatorUtils.validatePageParam(baseQO);
        return CommonResult.ok(userInformService.userSysInformList(baseQO, UserUtils.getUserId()));
    }

}
