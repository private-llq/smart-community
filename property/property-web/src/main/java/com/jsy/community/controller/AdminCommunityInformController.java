package com.jsy.community.controller;

import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IAdminCommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.Permit;
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
// @ApiJSYController
public class AdminCommunityInformController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IAdminCommunityInformService communityInformService;

    /**
     * (物业端)新增推送通知消息
     * @param qo 新增推送消息
     * @return 返回是否新增成功
     */
    @PostMapping("/add")
    @ApiOperation("添加社区推送通知消息")
    @businessLog(operation = "新增",content = "新增了【社区推送通知消息】")
    @Permit("community:property:community:inform:add")
    public CommonResult<Boolean> addPushInform(@RequestBody PushInformQO qo) {
        qo.setPushTarget(1);
        if (qo.getPushTag() == null) {
            // 默认关闭推送
            qo.setPushTag(0);
        }
        ValidatorUtils.validateEntity(qo, PushInformQO.AddPushInformValidateGroup.class);
        qo.setUid(UserUtils.getId());
        AdminInfoVo adminInfo = UserUtils.getAdminInfo();
        if (adminInfo.getCommunityId() == null) {
            qo.setAcctId(adminInfo.getCompanyId());
            qo.setAcctName(adminInfo.getCompanyName());
        } else {
            qo.setAcctId(adminInfo.getCommunityId());
            qo.setAcctName(adminInfo.getCommunityName());
        }
        return communityInformService.addPushInform(qo) ? CommonResult.ok("添加成功!") : CommonResult.error("添加失败!");
    }

    /**
     * (物业端)删除通知消息 [管理员]
     * @param id 消息id
     * @return 返回修改成功值
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除推送通知消息")
    @businessLog(operation = "删除",content = "删除了【社区推送通知消息】")
    @Permit("community:property:community:inform:delete")
    public CommonResult<Boolean> deletePushInform(@RequestParam Long id) {
        return communityInformService.deletePushInform(id, UserUtils.getId()) ? CommonResult.ok("删除成功!") : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     *@Author: Pipi
     *@Description: (物业端)更新置顶状态
     *@Param: request:
     *@Param: id: 消息ID
     *@Return: com.jsy.community.vo.CommonResult<java.lang.Boolean>
     *@Date: 2021/4/20 15:11
     **/
    @PostMapping("/updateTopState")
    @ApiOperation("更新置顶状态")
    @businessLog(operation = "编辑",content = "更新了【社区消息置顶状态】")
    @Permit("community:property:community:inform:updateTopState")
    public CommonResult<Boolean> updateTopState(@RequestBody OldPushInformQO qo) {
        qo.setUpdateBy(UserUtils.getId());
        ValidatorUtils.validateEntity(qo, OldPushInformQO.UpdateTopStateValidate.class);
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
    @PostMapping("/updatePushState")
    @ApiOperation("更新发布状态")
    @businessLog(operation = "编辑",content = "更新了【社区消息发布状态】")
    @Permit("community:property:community:inform:updatePushState")
    public CommonResult<Boolean> updatePushState(@RequestBody OldPushInformQO qo) {
        qo.setUpdateBy(UserUtils.getId());
        ValidatorUtils.validateEntity(qo, OldPushInformQO.UpdatePushStateValidate.class);
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
    @PostMapping("/list")
    @ApiOperation("按条件查询公告列表")
    @Permit("community:property:community:inform:list")
    public CommonResult<?> listInform(@RequestBody BaseQO<PushInformQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new PushInformQO());
        }

        /*if (CollectionUtil.isNotEmpty(UserUtils.getAdminCommunityIdList())) {
            baseQO.getQuery().setCommunityIds(UserUtils.getAdminUserInfo().getCommunityIdList());
        }else {
            List<Long> communityIds = new ArrayList<>();
            communityIds.add(UserUtils.getAdminCommunityId());
            baseQO.getQuery().setCommunityIds(communityIds);
        }*/
        return CommonResult.ok(communityInformService.queryInformList(baseQO));
    }

    /**
     * (业主端)查询 通知消息 从轮播消息点进去之后的显示界面 分页查询
     * 本小区的通知消息
     * @param qo    查询参数 其中pushInform仅仅包含communityId
     * @return      返回查询结果
     */
    @PostMapping(value = "/page", produces = "application/json;charset=utf-8")
    @ApiOperation("查询社区通知消息")
    @Permit("community:property:community:inform:page")
    public CommonResult<?> listCommunityInform(@RequestBody BaseQO<OldPushInformQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if (qo.getQuery() == null) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        ValidatorUtils.validateEntity(qo.getQuery(), OldPushInformQO.CommunityPushInformValidate.class);
        qo.getQuery().setUid(UserUtils.getId());
        return CommonResult.ok(communityInformService.queryCommunityInform(qo), "查询成功!");
    }

    /**
     *@Author: Pipi
     *@Description:  (物业端)获取单条消息详情
     *@Param: id: 消息ID
     *@Return: com.jsy.community.vo.CommonResult<?>
     *@Date: 2021/4/20 16:22
     **/
    @GetMapping("/getDetatil")
    @ApiOperation("(物业端)获取单条消息详情")
    @Permit("community:property:community:inform:getDetatil")
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
    @PutMapping("/updateDetail")
    @ApiOperation("(物业端)更新消息接口")
    @businessLog(operation = "编辑",content = "更新了【社区消息接口】")
    @Permit("community:property:community:inform:updateDetail")
    public CommonResult updateDetail(@RequestBody PushInformQO qo) {
        ValidatorUtils.validateEntity(qo, PushInformQO.UpdateDetailValidate.class);
        qo.setUid(UserUtils.getId());
        AdminInfoVo adminInfo = UserUtils.getAdminInfo();
        if (adminInfo.getCommunityId() == null) {
            qo.setAcctId(adminInfo.getCompanyId());
            qo.setAcctName(adminInfo.getCompanyName());
        } else {
            qo.setAcctId(adminInfo.getCommunityId());
            qo.setAcctName(adminInfo.getCommunityName());
        }
        return communityInformService.updatePushInform(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败");
    }


}
