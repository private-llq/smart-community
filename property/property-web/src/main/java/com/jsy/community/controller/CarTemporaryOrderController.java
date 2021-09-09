package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.config.ExcelUtils;
import com.jsy.community.constant.Const;
import com.jsy.community.api.ICarTemporaryOrderService;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.CarTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarTrackQO;
import com.jsy.community.qo.property.CarOrderQO;
import com.jsy.community.qo.property.CarTemporaryOrderQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "停车收费设置")
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
    public void downLoadFile(@RequestBody CarOrderQO carOrderQO,HttpServletResponse response) throws IOException {
        Long communityId = UserUtils.getAdminCommunityId();
        List<CarTemporaryOrderQO>  list = iCarTemporaryOrder.selectCarOrderList(carOrderQO,communityId);
        System.out.println(list);
        ExcelUtils.exportModule("1111", response, CarTemporaryOrderQO.class, list, 2);

    }

}
