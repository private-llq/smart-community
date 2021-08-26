package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IVoteService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jsy.community
 * @description:  业主投票
 * @author: Hu
 * @create: 2021-08-23 16:51
 **/
@RequestMapping("vote")
@RestController
@Login
@ApiJSYController
public class VoteController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IVoteService voteService;


    @ApiOperation("分页查询")
    @PostMapping("/list")
    public CommonResult list(@RequestBody BaseQO<VoteEntity> baseQO){
        return CommonResult.ok(voteService.list(baseQO));
    }


    @ApiOperation("查询详情")
    @GetMapping("/getVote")
    public CommonResult getVote(@RequestParam Long id){
        return CommonResult.ok(voteService.getVote(id));
    }


    @ApiOperation("投票进度")
    @GetMapping("/getPlan")
    public CommonResult getPlan(@RequestParam Long id){
        return CommonResult.ok(voteService.getPlan(id));
    }




}
