package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyCarService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:46
 **/
@Api(tags = "车辆信息")
@RestController
@RequestMapping("/car")
@ApiJSYController
public class PropertyCarController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyCarService propertyCarService;


}
