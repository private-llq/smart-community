package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.config.ExcelUtils;
import com.jsy.community.constant.Const;
import com.jsy.community.api.ICarTemporaryOrderService;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.OrderQO;
import com.jsy.community.qo.property.CarOrderQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.SelectMoney3Vo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import com.jsy.community.qo.property.CarTemporaryOrderQO;
import com.jsy.community.qo.property.CarTemporaryQO;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.io.IOException;
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
        Long communityId = UserUtils.getAdminCommunityId();
        Page<CarOrderEntity> listPage =  iCarTemporaryOrder.selectCarOrder(baseQO,communityId);
        return CommonResult.ok(listPage,"查询成功");
    }


    @ApiOperation("今日订单数和今日金额")
    @PostMapping("/selectMoney")
    @Login
    public CommonResult selectMoney() {
        Long communityId = UserUtils.getAdminCommunityId();
        Map<String,Object>  map = iCarTemporaryOrder.selectMoney(communityId);
        return CommonResult.ok(map,"查询成功");
    }

    @ApiOperation("导出模板")
    @PostMapping("/carTemporaryOrderExport")
    @ResponseBody
    public void downLoadFile(@RequestBody CarOrderQO carOrderQO, HttpServletResponse response) throws IOException {
        Long communityId = UserUtils.getAdminCommunityId();

        if (carOrderQO.getType()==2){
            List<CarTemporaryOrderQO>  list = iCarTemporaryOrder.selectCarOrderList(carOrderQO,communityId);
            ExcelUtils.exportModule("月租订单", response, CarTemporaryOrderQO.class, list, 2);

        }else {
            List<CarTemporaryQO>  list = iCarTemporaryOrder.selectTemporaryQOList(carOrderQO,communityId);
            ExcelUtils.exportModule("临时订单", response, CarTemporaryQO.class, list, 2);
        }

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
