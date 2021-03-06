package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IBindingPositionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.BindingPositionEntity;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "车辆绑定车位")
@RestController
@RequestMapping("BindingPosition")
// @ApiJSYController
public class BindingPositionController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IBindingPositionService bindingPositionService;
    /**
     * 包月车位车辆管理换绑业务，未绑定状态的车辆可以有多个，绑定车位的车辆只能有一个
     */

    /**
     * 新增未绑定状态的车辆
     */
    @PostMapping("saveBinding")
    @CarOperation(operation = "新增了【未绑定状态的车辆】")
    @Permit("community:property:BindingPosition:saveBinding")
    public CommonResult saveBinding(@RequestBody BindingPositionEntity bindingPositionEntity) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        bindingPositionEntity.setCommunityId(adminCommunityId);
        Integer integer= bindingPositionService.saveBinding(bindingPositionEntity);
        return CommonResult.ok(integer);
    }

    /**
     * 查询该车位下面所有的车辆信息 包含已绑定 和 未绑定
     */
    @PostMapping("selectBinding")
    @Permit("community:property:BindingPosition:selectBinding")
    public CommonResult selectBinding(@RequestBody BindingPositionEntity bindingPositionEntity) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        bindingPositionEntity.setCommunityId(adminCommunityId);
        List<BindingPositionEntity> bindingPositionEntitiesList= bindingPositionService.selectBinding(bindingPositionEntity);
        return CommonResult.ok(bindingPositionEntitiesList);
    }

    /**
     * 车位 换绑 车辆
     * 传参车牌号，车位
     */
    @PostMapping("binding")
    @CarOperation(operation = "换绑了【车位管理车辆】")
    @Permit("community:property:BindingPosition:binding")
    public CommonResult binding(@RequestBody BindingPositionEntity bindingPositionEntity) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        bindingPositionEntity.setCommunityId(adminCommunityId);
        bindingPositionService.binding(bindingPositionEntity);
        return CommonResult.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("deleteBinding")
    @CarOperation(operation = "删除了【车位管理车辆】")
    @Permit("community:property:BindingPosition:deleteBinding")
    public CommonResult deleteBinding(@RequestParam("uid") String uid) {

        bindingPositionService.deleteBinding(uid);
        return CommonResult.ok();
    }

}
