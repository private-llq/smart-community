package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyMarketCategoryService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "社区集市")
@RestController
@RequestMapping("/marketCategory")
@ApiJSYController
public class PropertyMarketCategoryController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyMarketCategoryService categoryService;

    @PostMapping("/addMarketCategory")
    @ApiOperation("社区集市新增商品分类")
    @Login
    public CommonResult addMarketCategory(@RequestBody ProprietorMarketCategoryEntity categoryEntity){
        boolean b = categoryService.addMarketCategory(categoryEntity);
        return CommonResult.ok("添加成功");
    }

    @PostMapping("/updateMarketCategory")
    @ApiOperation("社区集市修改商品分类")
    @Login
    public CommonResult updateMarketCategory(@RequestBody ProprietorMarketCategoryEntity categoryEntity){

        boolean b = categoryService.updateMarketCategory(categoryEntity);
        return CommonResult.ok("修改成功");
    }

    @DeleteMapping("/deleteMarketCategory")
    @ApiOperation("社区集市删除商品分类")
    @Login
    public CommonResult deleteMarketCategory(@RequestParam("id") Long id){
        boolean b = categoryService.deleteMarketCategory(id);
        return CommonResult.ok("删除成功");
    }

    @GetMapping("/selectMarketCategory")
    @ApiOperation("社区集市查询商品分类")
    @Login
    public CommonResult selectMarketCategory(){

        List<ProprietorMarketCategoryEntity> list = categoryService.selectMarketCategory();
        return CommonResult.ok(list,"查询成功");
    }

}
