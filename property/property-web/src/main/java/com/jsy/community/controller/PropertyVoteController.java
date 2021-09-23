package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyVoteService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:  业主投票
 * @author: Hu
 * @create: 2021-09-23 10:01
 **/
@RestController
@RequestMapping("/property/vote")
@ApiJSYController
@Login
public class PropertyVoteController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyVoteService propertyVoteService;

    @ApiOperation("查询列表")
    @PostMapping("/list")
    public CommonResult list(@RequestBody BaseQO<VoteEntity> baseQO){
        List<VoteEntity> list = propertyVoteService.list(baseQO, UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public CommonResult save(@RequestBody VoteEntity voteEntity){
        propertyVoteService.saveBy(voteEntity);
        return CommonResult.ok();
    }

    @ApiOperation("查详情")
    @GetMapping("/getOne")
    public CommonResult getOne(@RequestParam Long id){
        propertyVoteService.getOne(id);
        return CommonResult.ok();
    }

    @ApiOperation("查图表")
    @GetMapping("/getChart")
    public CommonResult getChart(@RequestParam Long id){
        propertyVoteService.getChart(id);
        return CommonResult.ok();
    }


}
