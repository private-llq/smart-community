package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.PeopleHistoryService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PeopleHistoryEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 人员进出记录控制器
 * @Date: 2021/8/27 10:18
 * @Version: 1.0
 **/
@RestController
@ApiJSYController
@RequestMapping("/peopleHistory")
public class PeopleHistoryController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private PeopleHistoryService peopleHistoryService;

    /**
     * @author: Pipi
     * @description: 分页查询人员进出记录
     * @param baseQO: 查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/27 10:23
     **/
    @Login
    @PostMapping("/v2/pagePeopleHistory")
    public CommonResult pagePeopleHistory(@RequestBody BaseQO<PeopleHistoryEntity> baseQO) {
        return CommonResult.ok(peopleHistoryService.pagePeopleHistory(baseQO));
    }
}
