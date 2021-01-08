package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YuLF
 * @date 2020/11/16 10:56
 */
@Api(tags = "社区消息控制器")
@RestController
@RequestMapping("/community/inform")
@Slf4j
@Login
@ApiJSYController
public class CommunityInformController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityInformService communityInformService;


    /**
     * 属于社区主页  通知消息轮播的接口 条数有限制
     */
    @Login
    @GetMapping("/rotation")
    @ApiOperation("社区轮播消息")
    public CommonResult<List<PushInformEntity>> rotationCommunityInform(@RequestParam Long communityId) {
        //页面起始页查询社区消息的初始条数 暂定10
        //@Value("${jsy.community-inform.initial.count}")
        Integer initialInformCount = 10;
        return CommonResult.ok(communityInformService.rotationCommunityInform(initialInformCount, communityId));
    }

    /**
     * 推送消息详情查看
     */
    @Login
    @GetMapping("/details")
    @ApiOperation("社区推送消息详情查看")
    public CommonResult<PushInformEntity> detailsCommunityInform(@RequestParam Long acctId, @RequestParam Long informId) {
        return CommonResult.ok(communityInformService.detailsCommunityInform(acctId, informId, UserUtils.getUserId()));
    }


    /**
     * 查询 通知消息 从轮播消息点进去之后的显示界面 分页查询
     * 本小区的通知消息
     * @param qo    查询参数 其中pushInform仅仅包含communityId
     * @return      返回查询结果
     */
    @Login
    @PostMapping(value = "/page", produces = "application/json;charset=utf-8")
    @ApiOperation("查询社区通知消息")
    public CommonResult<?> listCommunityInform(@RequestBody BaseQO<PushInformQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if (qo.getQuery() == null) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        ValidatorUtils.validateEntity(qo.getQuery(), PushInformQO.communityPushInformValidate.class);
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(communityInformService.queryCommunityInform(qo), "查询成功!");
    }




    /**
     * 推送消息号 左滑动 删除
     * 这种删除
     */
    @Login
    @DeleteMapping("/del")
    @ApiOperation("社区推送消息推送号删除")
    public CommonResult<Boolean> delPushInformAcct(@RequestParam Long acctId) {
       return null;
    }





}
