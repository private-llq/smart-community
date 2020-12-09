package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ISelectInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.UserInformQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-07 16:42
 **/
@RequestMapping("/inform")
@Api(tags = "查询通知消息已读未读")
@RestController
@ApiJSYController
public class SelectInformController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ISelectInformService selectInformService;

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
        Map<String,Object> map = selectInformService.findList(userInformQO);
        return CommonResult.ok(map);
    }

    /**
     * @Description: 查询所有未读通知消息人员
     * @author: Hu
     * @since: 2020/12/4 14:46
     * @Param:
     * @return:
     */
    @ApiOperation("所有未读通知人员")
    @PostMapping("/notList")
    public CommonResult notList(@RequestBody UserInformQO userInformQO){
        System.out.println(userInformQO);
        selectInformService.findNotList(userInformQO);
        return CommonResult.ok();
    }
}
