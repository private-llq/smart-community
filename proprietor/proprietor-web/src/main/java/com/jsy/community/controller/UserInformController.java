package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.CommunityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/inform")
@Api(tags = "社区通知消息")
@RestController
@ApiJSYController
public class UserInformController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserInformService userInformService;



    /**
     * @Description: 保存已读人员到数据库
     * @author: Hu
     * @since: 2020/12/4 14:46
     * @Param:
     * @return:
     */
    @ApiOperation("添加已读通知人员")
    @PostMapping("/add")
    @Login
    public CommonResult save(@RequestBody UserInformEntity userInformEntity) {
        String userId = UserUtils.getUserId();
        userInformEntity.setUid(userId);
        return userInformService.save(userInformEntity)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }

    @Login
    @PostMapping("/totalList")
    @ApiOperation("用户社区总未读消息列表查看")
    public CommonResult<List<CommunityVO>> totalCommunityInformList(@RequestBody BaseQO<?>  baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        List<CommunityVO> list = userInformService.totalCommunityInformList(UserUtils.getUserId(), baseQO.getPage() , baseQO.getSize());
        return CommonResult.ok(list);
    }


}
