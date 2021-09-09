package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.CarOperationService;
import com.jsy.community.api.ICarPositionTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.property.CarOperationLogQO;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.CarOperationLogVO;
import com.jsy.community.vo.property.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@ApiJSYController
@RequestMapping("/CarOperation")
@RestController
@Api(tags = "车辆日志模块")
public class CarOperationController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private CarOperationService carOperationService;


    @ApiOperation("分页查询车辆操作日志")
    @Login
    @RequestMapping(value = "/selectCarOperationLogPag", method = RequestMethod.POST)
    public CommonResult<PageVO> selectCarOperationLogPag(@RequestBody CarOperationLogQO qo) {

        PageVO  pageVO = carOperationService.selectCarOperationLogPag(qo);

        return CommonResult.ok(pageVO, "查询成功");
    }


















}
