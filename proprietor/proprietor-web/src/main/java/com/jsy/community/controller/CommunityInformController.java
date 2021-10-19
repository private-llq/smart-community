package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.lease.HouseLeaseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jsy.inform.initial.count}")
    private Integer informInitializeCount;

    @Value("${jsy.inform.lease.initial.count}")
    private Integer leaseInformInitializeCount;


    /**
     * 属于社区主页  通知消息轮播的接口 条数有限制
     */
    @Login
    @GetMapping("/rotation")
//    @Cacheable(value = "inform:rotation", key = "#communityId", unless = "#result.data == null or #result.data.size() == 0", condition = "#communityId > 0", cacheManager = "redisCacheManager")
    @ApiOperation("社区轮播消息")
    public CommonResult<List<PushInformEntity>> rotationCommunityInform(@RequestParam Long communityId) {
        return CommonResult.ok(communityInformService.rotationCommunityInform(informInitializeCount, communityId));
    }
    
    /**
     * 属于社区主页  最新租约消息轮播的接口 条数有限制
     */
    @Login
    @GetMapping("/latest")
//    @Cacheable(value = "inform:latest",  unless = "#result.data == null or #result.data.size() == 0", cacheManager = "redisCacheManager")
    @ApiOperation("社区租赁最新消息")
    public CommonResult<List<HouseLeaseVO>> leaseLatestInform() {
        return CommonResult.ok(communityInformService.leaseLatestInform(leaseInformInitializeCount));
    }

    /**
     * 推送消息详情查看
     */
    @Login
    @GetMapping("/details")
    @ApiOperation("社区推送消息详情查看")
    public CommonResult<PushInformEntity> detailsCommunityInform(@RequestParam Long informId) {
        return CommonResult.ok(communityInformService.detailsCommunityInform(informId, UserUtils.getUserId()));
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
    public CommonResult<?> listCommunityInform(@RequestBody BaseQO<OldPushInformQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if (qo.getQuery() == null) {
            qo.setQuery(new OldPushInformQO());
        }
        ValidatorUtils.validateEntity(qo.getQuery(), OldPushInformQO.CommunityPushInformValidate.class);
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(communityInformService.queryCommunityInform(qo), "查询成功!");
    }




    /**
     * 推送消息号 左滑动 删除
     * 这种删除 推送号消息 删除之后 第二次及以后 用户拉取消息列表就不会再被拉取、
     * 当该推送号 有新消息推送时 用户才会再次拉取到
     */
    @Login
    @DeleteMapping("/clear")
    @ApiOperation("社区推送消息推送号删除")
    public CommonResult<Boolean> delPushInformAcct(@RequestParam Long acctId) {
        communityInformService.delPushInformAcct(acctId, UserUtils.getUserId());
        //在删除推送号消息时 不需要返回结果 同时即使删除失败 后端不会进行任何操作，也不会影响到前端，
       return CommonResult.ok();
    }



    /**
     * 推送消息号 标记为已读
     */
    @Login
    @PostMapping("/clear/unread")
    @ApiOperation("社区推送号清除未读")
    public CommonResult<Boolean> clearUnreadInform(@RequestBody List<Long> acctIds) {
        communityInformService.clearUnreadInform(acctIds, UserUtils.getUserId());
        //失败的情况 只有在 数据访问层 出现错误，即交给ExceptionHandler处理
        return CommonResult.ok("已标记消息为已读!");
    }




}
