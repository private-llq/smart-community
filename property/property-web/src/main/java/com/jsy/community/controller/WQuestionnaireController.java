package com.jsy.community.controller;


import com.jsy.community.api.IWQuestionnaireService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.property.*;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.PageVO;
import com.jsy.community.vo.property.SelectQuestionnaireAllVO;
import com.jsy.community.vo.property.SelectQuestionnaireStatisticsVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  调查问卷控制器
 * </p>
 *
 * @author Arli
 * @since 2021-08-17
 */

@RequestMapping("/w-questionnaire")
// @ApiJSYController
@RestController
@Api(tags = "调查问卷")
public class WQuestionnaireController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IWQuestionnaireService iwQuestionnaireService;


    @ApiOperation("新增调查问卷")
    @PostMapping("/insterQuestionnaire")
    @Permit("community:property:w-questionnaire:insterQuestionnaire")
    public CommonResult<Boolean> insterQuestionnaire(@RequestBody InsterQuestionnaireQO qo) {

        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        Boolean aBoolean=iwQuestionnaireService.insterQuestionnaire(qo,adminCommunityId);
        return CommonResult.ok(true,"新增成功");

    }

    @ApiOperation("分页查询调查问卷")
    @PostMapping("/selectQuestionnaire")
    @Permit("community:property:w-questionnaire:selectQuestionnaire")
    public CommonResult<PageVO> selectQuestionnaire(@RequestBody SelectQuestionnaireQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        PageVO pageVO=iwQuestionnaireService.selectQuestionnaire(qo,adminCommunityId);
        return CommonResult.ok(pageVO,"查询成功");
    }

    @ApiOperation("修改发布状态")
    @PostMapping("/updateReleaseStatus")
    @Permit("community:property:w-questionnaire:updateReleaseStatus")
    public CommonResult<Boolean> updateReleaseStatus(@RequestBody UpdateReleaseStatusQO qo) {
        Boolean    b= iwQuestionnaireService.updateReleaseStatus(qo);
        return CommonResult.ok(b,"修改成功");
    }

    @ApiOperation("分页查询用户看得到的问卷列表")
    @PostMapping("/selectQuestionnaireListByUser")
    @Permit("community:property:w-questionnaire:selectQuestionnaireListByUser")
    public CommonResult<PageVO> selectQuestionnaireListByUser(@RequestBody SelectQuestionnaireListByUserQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        String userId = UserUtils.getUserId();
        PageVO    pageVO= iwQuestionnaireService.selectQuestionnaireListByUser(adminCommunityId,userId,qo);

        return CommonResult.ok(pageVO,"查询成功");
    }


    @ApiOperation("根据问卷id查询问卷相关数据（用户）")
    @PostMapping("/selectQuestionnaireAll")
    @Permit("community:property:w-questionnaire:selectQuestionnaireAll")
    public CommonResult<SelectQuestionnaireAllVO> selectQuestionnaireAll(String id) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        SelectQuestionnaireAllVO   vo=  iwQuestionnaireService.selectQuestionnaireAll(id,adminCommunityId);
        return CommonResult.ok(vo,"查询成功");
    }

    @ApiOperation("用户的提交调查问卷答案")
    @PostMapping("/insterAnswer")
    @Permit("community:property:w-questionnaire:insterAnswer")
    public CommonResult<Boolean> insterAnswer(@RequestBody InsterAnswerQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        String userUuid = UserUtils.getUserId();
        Boolean  aBoolean=  iwQuestionnaireService.insterAnswer(adminCommunityId,userUuid,qo);
        return CommonResult.ok(aBoolean,"提交成功");
    }

    @ApiOperation("查询调查问卷的统计情况")
    @PostMapping("/selectQuestionnaireStatistics")
    @Permit("community:property:w-questionnaire:selectQuestionnaireStatistics")
    public CommonResult<SelectQuestionnaireStatisticsVO> selectQuestionnaireStatistics(String id) {
        SelectQuestionnaireStatisticsVO vo  =iwQuestionnaireService.selectQuestionnaireStatistics(id);
        return CommonResult.ok(vo,"查询成功");
    }








}

