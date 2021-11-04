package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.service.IMarketCategoryService;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "社区集市")
@RestController
@RequestMapping("/marketCategory")
@ApiJSYController
public class MarketCategoryController {
    @Resource
    private IMarketCategoryService categoryService;
    
    /**
     * @Description: 新增社区集市商品类别
     * @author: DKS
     * @since: 2021/11/2 11:08
     * @Param: [categoryEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/addMarketCategory")
    @ApiOperation("社区集市新增商品分类")
    @Login
    public CommonResult addMarketCategory(@RequestBody ProprietorMarketCategoryEntity categoryEntity){
        return CommonResult.ok(categoryService.addMarketCategory(categoryEntity) ? "添加成功" : "添加失败");
    }
    
    /**
     * @Description: 修改社区集市商品类别
     * @author: DKS
     * @since: 2021/11/2 11:51
     * @Param: [categoryEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/updateMarketCategory")
    @ApiOperation("社区集市修改商品分类")
    @Login
    public CommonResult updateMarketCategory(@RequestBody ProprietorMarketCategoryEntity categoryEntity){
        return CommonResult.ok(categoryService.updateMarketCategory(categoryEntity) ? "修改成功" : "修改失败");
    }
    
    /**
     * @Description: 删除社区集市商品类别
     * @author: DKS
     * @since: 2021/11/2 11:54
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/deleteMarketCategory")
    @ApiOperation("社区集市删除商品分类")
    @Login
    public CommonResult deleteMarketCategory(@RequestParam("id") Long id){
        return CommonResult.ok(categoryService.deleteMarketCategory(id) ? "删除成功" : "删除失败");
    }
    
    /**
     * @Description: 查询社区集市商品类别列表
     * @author: DKS
     * @since: 2021/11/2 11:57
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/selectMarketCategory")
    @ApiOperation("社区集市查询商品分类")
    @Login
    public CommonResult selectMarketCategory(){
        List<ProprietorMarketCategoryEntity> list = categoryService.selectMarketCategory();
        return CommonResult.ok(list,"查询成功");
    }
}
