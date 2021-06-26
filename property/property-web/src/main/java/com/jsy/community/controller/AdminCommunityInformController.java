package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAdminCommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


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
     * (物业端)新增推送通知消息
     * @param qo 新增推送消息
     * @return 返回是否新增成功
     */
    @Login
    @PostMapping("/add")
    @ApiOperation("添加社区推送通知消息")
    public CommonResult<Boolean> addPushInform(HttpServletRequest request, @RequestBody PushInformQO qo) {
        ValidatorUtils.validateEntity(qo, PushInformQO.AddPushInformValidate.class);
        /*if (qo.getPushState() == 0 && qo.getTopState() == 1) {
            throw new JSYException(400, "参数错误!草稿不能置顶!");
        }*/
        qo.setCreateBy(UserUtils.getAdminUserInfo().getUid());
        // 设置推送用户信息
        qo.setAcctId(UserUtils.getAdminCommunityId());
        return communityInformService.addPushInform(qo) ? CommonResult.ok("添加成功!") : CommonResult.error("添加失败!");
    }

    /**
     * (物业端)删除通知消息 [管理员]
     * @param id 消息id
     * @return 返回修改成功值
     */
    @Login
    @DeleteMapping("/delete")
    @ApiOperation("删除推送通知消息")
    public CommonResult<Boolean> deletePushInform(HttpServletRequest request, @RequestParam Long id) {
        return communityInformService.deletePushInform(id, UserUtils.getAdminUserInfo().getUid()) ? CommonResult.ok("删除成功!") : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     *@Author: Pipi
     *@Description: (物业端)更新置顶状态
     *@Param: request:
     *@Param: id: 消息ID
     *@Return: com.jsy.community.vo.CommonResult<java.lang.Boolean>
     *@Date: 2021/4/20 15:11
     **/
    @Login
    @PostMapping("/updateTopState")
    @ApiOperation("更新置顶状态")
    public CommonResult<Boolean> updateTopState(HttpServletRequest request, @RequestBody PushInformQO qo) {
        qo.setUpdateBy(UserUtils.getAdminUserInfo().getUid());
        ValidatorUtils.validateEntity(qo, PushInformQO.UpdateTopStateValidate.class);
        return communityInformService.updateTopState(qo) ? CommonResult.ok("操作成功!") : CommonResult.error("操作失败!");
    }

    /**
     *@Author: Pipi
     *@Description: (物业端)更新发布状态
     *@Param: request:
     *@Param: qo:
     *@Return: com.jsy.community.vo.CommonResult<java.lang.Boolean>
     *@Date: 2021/4/20 15:55
     **/
    @Login
    @PostMapping("/updatePushState")
    @ApiOperation("更新发布状态")
    public CommonResult<Boolean> updatePushState(HttpServletRequest request, @RequestBody PushInformQO qo) {
        qo.setUpdateBy(UserUtils.getAdminUserInfo().getUid());
        ValidatorUtils.validateEntity(qo, PushInformQO.UpdatePushStateValidate.class);
        if (qo.getPushState() < 1) {
            // 发布状态只能在发布与撤销间反复横跳
            throw new JSYException(JSYError.REQUEST_PARAM);
        }
        return communityInformService.updatePushState(qo) ? CommonResult.ok("操作成功!") : CommonResult.error("操作失败!");
    }

    /**
     *@Author: Pipi
     *@Description: (物业端)公告列表
     *@Param: qo: 
     *@Return: com.jsy.community.vo.CommonResult<?>
     *@Date: 2021/4/20 13:33
     **/
    @Login
    @PostMapping("/list")
    @ApiOperation("按条件查询公告列表")
    public CommonResult<?> listInform(@RequestBody BaseQO<PushInformQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if (qo.getQuery() == null) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        ValidatorUtils.validateEntity(qo.getQuery(), PushInformQO.PropertyInformListValidate.class);
        qo.getQuery().setAcctId(UserUtils.getAdminCommunityId());
//        List<PushInformEntity> pushInformEntities = communityInformService.queryInformList(qo);
        return CommonResult.ok(communityInformService.queryInformList(qo));
    }

    /**
     * (业主端)查询 通知消息 从轮播消息点进去之后的显示界面 分页查询
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
        qo.getQuery().setUid(UserUtils.getAdminUserInfo().getUid());
        return CommonResult.ok(communityInformService.queryCommunityInform(qo), "查询成功!");
    }

    /**
     *@Author: Pipi
     *@Description:  (物业端)获取单条消息详情
     *@Param: id: 消息ID
     *@Return: com.jsy.community.vo.CommonResult<?>
     *@Date: 2021/4/20 16:22
     **/
    @Login
    @GetMapping("/getDetatil")
    @ApiOperation("(物业端)获取单条消息详情")
    public CommonResult<?> getDetatil(@RequestParam Long id) {
        return CommonResult.ok(communityInformService.getDetail(id));
    }

    /**
     *@Author: Pipi
     *@Description: 更新消息接口
     *@Param: qo:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/20 16:32
     **/
    @Login
    @PutMapping("/updateDetail")
    @ApiOperation("(物业端)更新消息接口")
    public CommonResult updateDetail(HttpServletRequest request, @RequestBody PushInformQO qo) {
        ValidatorUtils.validateEntity(PushInformQO.UpdateDetailValidate.class);
        qo.setUpdateBy(UserUtils.getAdminUserInfo().getUid());
        // 设置推送用户信息
        qo.setAcctId(UserUtils.getAdminCommunityId());
        return communityInformService.updatePushInform(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败");
    }
}
