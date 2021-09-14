package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.constant.Const;
import com.jsy.community.api.ICarTemporaryOrderService;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.CarTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarTrackQO;
import com.jsy.community.qo.OrderQO;
import com.jsy.community.qo.property.CarOrderQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.SelectMoney3Vo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Api(tags = "订单查询")
@RestController
@RequestMapping("/carTemporaryOrder")
@ApiJSYController
public class CarTemporaryOrderController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarTemporaryOrderService iCarTemporaryOrder;

    @ApiOperation("查询订单管理")
    @PostMapping("/selectCarOrder")
    @Login
    public CommonResult selectCarOrder(@RequestBody BaseQO<CarOrderQO> baseQO) {
       Page<CarOrderEntity> listPage =  iCarTemporaryOrder.selectCarOrder(baseQO);
        return CommonResult.ok(listPage,"查询成功");
    }


    @ApiOperation("今日订单数和今日金额")
    @PostMapping("/selectMoney")
    @Login
    public CommonResult selectMoney() {
        Map<String,Object>  map = iCarTemporaryOrder.selectMoney();
        return CommonResult.ok(map,"查询成功");
    }



    /****************************************************收款统计***********************************************************/

    /**
     *  简单模式
     *  包含月租和临时金额
     *
     * 1：今日 2：最近5天 3：最近10天 4：本月 5：指定时间段
     * @return
     */
    @GetMapping("/selectMoney2")
    @Login
    public CommonResult selectMoney2Day() {
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        List<Map<String, BigDecimal>> maps = iCarTemporaryOrder.selectMoney2(adminCommunityId);
        return CommonResult.ok(maps,"查询成功");
    }

    /**
     * 图表模式
     */
    @PostMapping("/selectMoney3")
    @Login
    public CommonResult selectMoney3Day(@RequestBody OrderQO orderQO) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        orderQO.setCommunityId(adminCommunityId);
        List<SelectMoney3Vo> list = iCarTemporaryOrder.selectMoney3(orderQO);
        return CommonResult.ok(list,"查询成功");
    }



}
