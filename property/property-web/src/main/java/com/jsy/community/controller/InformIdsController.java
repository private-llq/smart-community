package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IInformIdsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.InformIdsEntity;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description: 收到推送消息用户接口添加ids到数据库
 * @author: Hu
 * @create: 2020-12-08 13:58
 **/
@Api(tags = "收到推送消息用户接口")
@RestController
@RequestMapping("/inform")
// @ApiJSYController
public class InformIdsController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IInformIdsService iInformIdsService;

    /**
     * @Description: 添加所有收到通知消息的id
     * @author: Hu
     * @since: 2020/12/8 14:10
     * @Param:
     * @return:
     */
    @LoginIgnore
    @ApiOperation("所有收到通知用户")
    @PostMapping("/addIds")
    @Permit("community:property:inform:addIds")
    public CommonResult list(@RequestBody InformIdsEntity informIdsEntity){
        iInformIdsService.addIds(informIdsEntity);
        return CommonResult.ok();
    }

}
