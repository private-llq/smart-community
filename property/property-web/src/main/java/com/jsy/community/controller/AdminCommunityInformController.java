package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAdminCommunityInformService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;


/**
 * 物业
 * @author YuLF
 * @date 2020/11/16 10:56
 */
@Api(tags = "社区消息控制器")
@RestController
@RequestMapping("/community/inform")
@ApiJSYController
public class AdminCommunityInformController {



    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IAdminCommunityInformService communityInformService;



    /**
     * TODO: 按物业原型修改发送公告
     * 新增推送通知消息
     * @param qo 新增推送消息
     * @return 返回是否新增成功
     */
    @Login
    @PostMapping()
    @ApiOperation("添加社区推送通知消息")
    public CommonResult<Boolean> addPushInform(@RequestBody PushInformQO qo) {
        ValidatorUtils.validateEntity(qo, PushInformQO.AddPushInformValidate.class);
        //TODO 动态获取社区id、社区名称、社区头像url set到qo
        qo.setAcctId(71534928127631370L);
        qo.setAcctName("智慧社区");
        qo.setAcctAvatar("https://f11.baidu.com/it/u1=1438590271&u2=290097440&fm=76");
        //推送目标为1 推送至某个社区 0 为所有社区
        qo.setPushTarget(BusinessConst.ONE);
        return communityInformService.addPushInform(qo) ? CommonResult.ok("添加成功!") : CommonResult.error("添加失败!");
    }


    /**
     * TODO: 按物业原型修改删除发送公告
     * 删除通知消息 [管理员]
     *
     * @param id 消息id
     * @return 返回修改成功值
     */
    @Login
    @DeleteMapping()
    @ApiOperation("删除推送通知消息")
    public CommonResult<Boolean> deletePushInform(@RequestParam Long id) {
        return communityInformService.deletePushInform(id) ? CommonResult.ok("删除成功!") : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    /**
     * TODO: 按物业原型修改查询发送公告
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
        ValidatorUtils.validateEntity(qo.getQuery(), PushInformQO.CommunityPushInformValidate.class);
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(communityInformService.queryCommunityInform(qo), "查询成功!");
    }

}
