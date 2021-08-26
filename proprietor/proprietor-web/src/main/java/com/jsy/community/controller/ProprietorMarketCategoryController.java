package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IProprietorMarketCategoryService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "社区集市-商品分类")
@RestController
@RequestMapping("/marketCategory")
@ApiJSYController
public class ProprietorMarketCategoryController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IProprietorMarketCategoryService categoryService;

    /**
     * @Description: 新增社区集市商品类别
     * @Param: [categoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:40
     **/
    @PostMapping("/addMarketCategory")
    @ApiOperation("新增社区集市商品类别")
    @Login
    public CommonResult addMarketCategory(@RequestBody ProprietorMarketCategoryEntity  categoryEntity){
        System.out.println(categoryEntity);
        boolean b = categoryService.addMarketCategory(categoryEntity);
        return CommonResult.ok("添加成功");
    }

    /**
     * @Description: 修改社区集市商品类别
     * @Param: [categoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    @PostMapping("/updateMarketCategory")
    @ApiOperation("修改社区集市商品类别")
    @Login
    public CommonResult updateMarketCategory(@RequestBody ProprietorMarketCategoryEntity categoryEntity){
        boolean b = categoryService.updateMarketCategory(categoryEntity);
        return CommonResult.ok("修改成功");
    }
    /**
     * @Description: 删除社区集市商品类别
     * @Param: [categoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    @DeleteMapping("/deleteMarketCategory")
    @ApiOperation("删除社区集市商品类别")
    @Login
    public CommonResult deleteMarketCategory(@RequestParam("id")Long id){
        boolean b = categoryService.deleteMarketCategory(id);
        return CommonResult.ok("删除成功");
    }

    /**
     * @Description: 查询社区集市商品类别
     * @Param: [categoryEntity]
     * @Return: List
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    @GetMapping("/selectMarketCategory")
    @ApiOperation("查询社区集市商品类别")
    @Login
    public CommonResult selectMarketcategory(@RequestParam("communityId") Long communityId){
        List<ProprietorMarketCategoryEntity> list = categoryService.selectMarketCategory(communityId);
        return CommonResult.ok(list,"查询成功");
    }

}
