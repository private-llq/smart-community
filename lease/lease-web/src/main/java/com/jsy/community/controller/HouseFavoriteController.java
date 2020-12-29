package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseFavoriteService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuLF
 * @since 2020-12-29 17:06
 */
@Slf4j
@ApiJSYController
@RestController
@RequestMapping("/favorite")
@Api(tags = "房屋租售收藏控制器")
public class HouseFavoriteController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseFavoriteService iHouseFavoriteService;



}
