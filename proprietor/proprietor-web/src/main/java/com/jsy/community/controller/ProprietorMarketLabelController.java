package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IProprietorMarketLabelService;
import com.jsy.community.api.IProprietorMarketService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ProprietorMarketLabelEntity;
import com.jsy.community.qo.proprietor.ProprietorMarketQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "社区集市-商品标签")
@RestController
@RequestMapping("/marketLabel")
@ApiJSYController
public class ProprietorMarketLabelController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IProprietorMarketLabelService labelService;

    /**
     * @Description: 新增社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:40
     **/
    @PostMapping("/addMarketLabel")
    @ApiOperation("新增社区集市商品标签")
    @Login
    public CommonResult addMarketLable(@RequestBody ProprietorMarketLabelEntity labelEntity){
        System.out.println(labelEntity);
        boolean b = labelService.addMarketLabel(labelEntity);
        return CommonResult.ok("添加成功");
    }

    /**
     * @Description: 修改社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    @PostMapping("/updateMarketLabel")
    @ApiOperation("修改社区集市商品标签")
    @Login
    public CommonResult updateMarketLabel(@RequestBody ProprietorMarketLabelEntity labelEntity){
        boolean b = labelService.updateMarketLabel(labelEntity);
        return CommonResult.ok("修改成功");
    }
    /**
     * @Description: 删除社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    @DeleteMapping("/deleteMarketLabel")
    @ApiOperation("删除社区集市商品标签")
    @Login
    public CommonResult deleteMarketLabel(@RequestParam("id")Long id){
        boolean b = labelService.deleteMarketLabel(id);
        return CommonResult.ok("删除成功");
    }

    /**
     * @Description: 查询社区集市商品标签
     * @Param: [labelEntity]
     * @Return: List
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    @GetMapping("/selectMarketLabel")
    @ApiOperation("查询社区集市商品标签")
    @Login
    public CommonResult selectMarketLabel(){
        List<ProprietorMarketLabelEntity> list = labelService.selectMarketLabel();
        return CommonResult.ok(list,"查询成功");
    }




}
