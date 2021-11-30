package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IVoteService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.VoteQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: com.jsy.community
 * @description:  业主投票
 * @author: Hu
 * @create: 2021-08-23 16:51
 **/
@RequestMapping("vote")
@RestController
@Login
// @ApiJSYController
public class VoteController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IVoteService voteService;


    @ApiOperation("分页查询")
    @PostMapping("/list")
    public CommonResult list(@RequestBody BaseQO<VoteEntity> baseQO){
        return CommonResult.ok(voteService.list(baseQO));
    }

    @ApiOperation("分页查询")
    @PostMapping("/file")
    public CommonResult list(@RequestParam MultipartFile file){
        String upload = MinioUtils.upload(file, "vote");
        return CommonResult.ok(upload);
    }


    @ApiOperation("查询详情")
    @GetMapping("/getVote")
    public CommonResult getVote(@RequestParam Long id){
        return CommonResult.ok(voteService.getVote(id,UserUtils.getUserId()));
    }


    @ApiOperation("投票进度")
    @GetMapping("/getPlan")
    public CommonResult getPlan(@RequestParam Long id){
        return CommonResult.ok(voteService.getPlan(id));
    }

    @ApiOperation("业主投票")
    @PostMapping("/userVote")
    public CommonResult userVote(@RequestBody VoteQO voteQO){
        if (voteQO.getChoose()==1){
            if (voteQO.getOptions().size()>1){
                return CommonResult.error("当前问题只能单选哦！");
            }
        }
        voteService.userVote(voteQO, UserUtils.getUserId());
        return CommonResult.ok();
    }




}
