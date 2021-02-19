package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseRecentService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;


/**
 * @author YuLF
 * @since 2020-12-26 13:55
 */
@Slf4j
@ApiJSYController
@RestController
@Api(tags = "租赁最近浏览控制器")
@RequestMapping("/house/browse")
public class HouseRecentController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseRecentService houseRecentService;


}
