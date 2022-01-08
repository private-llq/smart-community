package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarPositionTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.property.UpdateCartPositionTypeQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.SelectCartPositionTypeVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 车位类型表 前端控制器
 * </p>
 *
 * @author Arli
 * @since 2021-08-05
 */
@RestController
@Api(tags = "车位类型模块")
@RequestMapping("/carPositionType")
// @ApiJSYController
public class CarPositionTypeController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionTypeService iCarPositionTypeService;

    @ApiOperation("新增车位类型")
    @CarOperation(operation = "新增车位类型")
    @RequestMapping(value = "/insterCartPositionType", method = RequestMethod.POST)
    @businessLog(operation = "新增",content = "新增了【车位类型】")
    @Permit("community:property:carPositionType:insterCartPositionType")
    public CommonResult<Boolean> insterCartPositionType(@RequestParam("description") String description) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        Boolean aBoolean = iCarPositionTypeService.insterCartPositionType(description,adminCommunityId);
        if (aBoolean) {
            return CommonResult.ok(aBoolean,"新增车位类型成功") ;
        }
        return CommonResult.ok(aBoolean,"新增车位类型失败") ;
    }

    @ApiOperation("修改车位类型")
    @CarOperation(operation = "修改车位类型")
    @RequestMapping(value = "/updateCartPositionType", method = RequestMethod.POST)
    @businessLog(operation = "编辑",content = "更新了【车位类型】")
    @Permit("community:property:carPositionType:updateCartPositionType")
    public CommonResult<Boolean> updateCartPositionType(@RequestBody UpdateCartPositionTypeQO qo) {
      boolean aBoolean=  iCarPositionTypeService.updateCartPositionType(qo);
        if (aBoolean) {
            return CommonResult.ok(aBoolean,"修改车位类型成功") ;
        }
        return CommonResult.ok(aBoolean,"修改车位类型失败") ;
    }

    @ApiOperation("查询小区车位类型集合")
    @RequestMapping(value = "/selectCartPositionType", method = RequestMethod.GET)
    @Permit("community:property:carPositionType:selectCartPositionType")
    public CommonResult<List<SelectCartPositionTypeVO>> selectCartPositionType() {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        List<SelectCartPositionTypeVO> vo = iCarPositionTypeService.selectCartPositionType(adminCommunityId);
        return CommonResult.ok(vo) ;
    }

    @ApiOperation("删除小区的车位分类")
    @CarOperation(operation = "删除小区的车位分类")
    @RequestMapping(value = "/deleteCartPositionType", method = RequestMethod.POST)
    @businessLog(operation = "删除",content = "删除了【车位类型】")
    @Permit("community:property:carPositionType:deleteCartPositionType")
    public CommonResult<Boolean> udeleteCartPositionType(@RequestParam("id")String id) {
        Boolean aBoolean= iCarPositionTypeService.deleteCartPositionType(id);
        if (aBoolean) {
            return CommonResult.ok(aBoolean,"删除车位类型成功") ;
        }
        return CommonResult.ok(aBoolean,"删除车位类型失败") ;
    }

}

