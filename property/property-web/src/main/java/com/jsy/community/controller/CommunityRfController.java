package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.CommunityRfService;
import com.jsy.community.api.CommunityRfSycRecordService;
import com.jsy.community.constant.Const;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 门禁卡控制器
 * @Date: 2021/11/3 16:55
 * @Version: 1.0
 **/
@RestController
@ApiJSYController
@RequestMapping("/rf")
public class CommunityRfController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private CommunityRfService rfService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private CommunityRfSycRecordService rfSycRecordService;
}
