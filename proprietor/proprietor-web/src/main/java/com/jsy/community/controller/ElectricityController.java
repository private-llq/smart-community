package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IElectricityService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.ElectricityQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description: 电费前端控制器
 * @author: Hu
 * @create: 2020-12-11 09:28
 **/
@Api(tags = "生活缴费前端控制器")
@RestController
@RequestMapping("/electricity")
@ApiJSYController
public class ElectricityController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IElectricityService electricityService;


    /**
     * 添加缴费记录
     * @param electricityQO
     * @return
     */
    @ApiOperation("生活缴费")
    @PutMapping("/add")
    @Login
    public CommonResult add(@RequestBody ElectricityQO electricityQO){

        String userId = UserUtils.getUserId();
        System.out.println(electricityQO);
        electricityQO.setUserID(userId);
        electricityService.add(electricityQO);
        return CommonResult.ok();
    }

}
