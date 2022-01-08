package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarEntranceService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEntranceQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "车禁模块-入场登记")
@RestController
@RequestMapping("/carEntrance")
// @ApiJSYController
public class CarEntranceController {

    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarEntranceService iCarEntranceService;


    @PostMapping("/selectCarEntrance")
    @ApiOperation("社区集市所有已发布商品")
    @Permit("community:property:carEntrance:selectCarEntrance")
    public CommonResult selectCarEntrance(@RequestBody BaseQO<CarEntranceQO> baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        if (baseQO.getQuery()==null){
            baseQO.setQuery(new CarEntranceQO());
        }
        Long communityId = UserUtils.getAdminCommunityId();
        Map<String,Object> map =  iCarEntranceService.selectCarEntrance(baseQO,communityId);

        return CommonResult.ok(map,"查询成功");

    }

}
