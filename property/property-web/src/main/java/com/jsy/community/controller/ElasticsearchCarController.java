package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IElasticsearchCarService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ElasticsearchCarSearchQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-26 09:26
 **/
@Api(tags = "社区趣事控制器")
@RestController
@RequestMapping("/car")
@ApiJSYController
public class ElasticsearchCarController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IElasticsearchCarService elasticsearchCarService;

    @ApiOperation("分页查询所有车辆")
    @PostMapping("/list")
//    @Login
    public CommonResult list(@RequestBody BaseQO<ElasticsearchCarSearchQO> baseQO) {
        elasticsearchCarService.searchData(baseQO);
        return CommonResult.ok();
    }
}
