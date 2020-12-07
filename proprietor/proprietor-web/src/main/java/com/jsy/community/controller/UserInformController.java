package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.proprietor.UserInformQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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
    public CommonResult save(@RequestBody UserInformEntity userInformEntity) {
        return userInformService.save(userInformEntity)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }

    /**
     * @Description: 查询所有所读通知消息人员
     * @author: Hu
     * @since: 2020/12/4 14:46
     * @Param:
     * @return:
     */
    @ApiOperation("所有已读通知人员")
    @PostMapping("/list")
    public CommonResult list(@RequestBody UserInformQO userInformQO){
        System.out.println(userInformQO);
        Map<String,Object> map = userInformService.findList(userInformQO);
        return CommonResult.ok(map);
    }




}
