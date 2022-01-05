package com.jsy.community.controller;

import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyMarketQO;
import com.jsy.community.service.IMarketService;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.proprietor.ProprietorMarketVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "社区集市")
@RestController
@RequestMapping("/market")
// @ApiJSYController
public class MarketController {
    @Resource
    private IMarketService marketService;
    
    /**
     * @Description: 删除发布的商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/11/1-10:18
     **/
    @DeleteMapping("/deleteBlacklist")
    @ApiOperation("社区集市删除商品")
    @Permit("community:admin:market:deleteBlacklist")
    public CommonResult deleteBlacklist(@RequestParam("id") Long id){

        boolean b = marketService.deleteBlacklist(id);
        return CommonResult.ok("删除成功");
    }
    
    /**
     * @Description: 查询单条商品详情
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/11/1-10:36
     **/
    @GetMapping("/SelectOneMarket")
    @ApiOperation("查询单条商品详情")
    @Permit("community:admin:market:SelectOneMarket")
    public CommonResult SelectOneMarket(@RequestParam("id") Long id){
      ProprietorMarketVO marketVO =  marketService.findOne(id);
        return CommonResult.ok(marketVO,"查询成功");
    }


    /**
     * @Description: 查询所有已发布的商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/11/1-10:44
     **/
    @PostMapping("/selectMarketAllPage")
    @ApiOperation("社区集市所有已发布商品")
    @Permit("community:admin:market:selectMarketAllPage")
    public CommonResult selectMarketAllPage(@RequestBody  BaseQO<PropertyMarketQO> baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        if (baseQO.getQuery() == null){
            baseQO.setQuery(new PropertyMarketQO());
        }
        Map<String,Object> map =  marketService.selectMarketAllPage(baseQO);

        return CommonResult.ok(map,"查询成功");

    }


    /**
     * @Description: 查询黑名单的商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/11/1-11:00
     **/
    @PostMapping("/selectMarketBlacklist")
    @ApiOperation("查询黑名单的商品")
    @Permit("community:admin:market:selectMarketBlacklist")
    public CommonResult selectMarketBlacklist(@RequestBody  BaseQO baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        Map<String,Object> map =  marketService.selectMarketBlacklist(baseQO);
        return CommonResult.ok(map,"查询成功");
    }

    /**
     * @Description: 修改屏蔽商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @author: DKS
     * @since: 2021/11/1 10:56
     **/
    @PostMapping("/updateShield")
    @ApiOperation("修改屏蔽商品")
    @Permit("community:admin:market:updateShield")
    public CommonResult updateShield(@RequestParam("id")Long id,@RequestParam("shield") Integer shield){
        boolean b = marketService.updateShield(id,shield);
        return CommonResult.ok("修改成功");
    }
}
