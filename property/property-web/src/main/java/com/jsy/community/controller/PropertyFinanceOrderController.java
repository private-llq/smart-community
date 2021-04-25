package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:35
 **/
@Api(tags = "物业房间账单")
@RestController
@RequestMapping("/financeOrder")
@ApiJSYController
@Login
public class PropertyFinanceOrderController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @ApiOperation("查询房屋所有未缴账单")
    @GetMapping("/houseCost")
    @Login
    public CommonResult houseCost(@RequestParam("houseId") Long houseId){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        Map<String, Object> map=propertyFinanceOrderService.houseCost(userInfo,houseId);
        return CommonResult.ok(map);
    }
    @ApiOperation("查询一条已交账单详情")
    @GetMapping("/getOrderNum")
    @Login
    public CommonResult getOrderNum(@RequestParam("orderNum") String orderNum){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        PropertyFinanceOrderVO propertyFinanceOrderVO=propertyFinanceOrderService.getOrderNum(userInfo,orderNum);
        return CommonResult.ok(propertyFinanceOrderVO);
    }
    
    
    /**
     * @Description: 分页查询已缴费 (缴费模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/4/24
     **/
    @ApiOperation("分页查询已缴费")
    @PostMapping("paid")
    public CommonResult queryPaid(@RequestBody BaseQO<PropertyFinanceOrderEntity> baseQO){
        if(baseQO.getQuery() == null){
            baseQO.setQuery(new PropertyFinanceOrderEntity());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.queryPaid(baseQO),"查询成功");
    }
    
    /**
    * @Description: 分页查询 (财务模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    @ApiOperation("分页查询")
    @PostMapping("page")
    public CommonResult queryUnionPage(@RequestBody BaseQO<PropertyFinanceOrderEntity> baseQO){
        if(baseQO.getQuery() == null){
            baseQO.setQuery(new PropertyFinanceOrderEntity());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.queryPage(baseQO),"查询成功");
    }

    /**
     *@Author: Pipi
     *@Description: 分页获取结算单的账单列表
     *@Param: baseQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/24 11:42
     **/
    @Login
    @ApiOperation("分页获取结算单的账单列表")
    @PostMapping("/getPageByStatemenNum")
    public CommonResult getPageByStatemenNum(@RequestBody BaseQO<StatementNumQO> baseQO) {
        ValidatorUtils.validatePageParam(baseQO);
        if (baseQO.getQuery() == null) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        ValidatorUtils.validateEntity(baseQO.getQuery());
        return CommonResult.ok(propertyFinanceOrderService.queryPageByStatemenNum(baseQO),"查询成功");
    }

}
