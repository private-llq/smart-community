package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.HouseLeaseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/lease")
@Api(tags = "房屋租售控制器")
@Slf4j
@RestController
@ApiJSYController
public class HouseLeaseController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IHouseLeaseService iHouseLeaseService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommonService iCommonService;

    @ApiOperation("新增房屋租售")
    @PostMapping()//addHouseLease
    @Login
    public CommonResult<Boolean> addLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQO) {
        //新增参数常规效验
        ValidatorUtils.validateEntity(houseLeaseQO, HouseLeaseQO.addLeaseSaleHouse.class);
        houseLeaseQO.setUid(UserUtils.getUserId());
        //房屋常量数值有效性边界效验
        //1.验证房屋出租类型id 数值是否在可取范围之内 数据库内 type = 10 的就是租房类型  普通住宅还是别墅 之类...
        //ValidatorUtils.validFieldVal(iCommonService.getHouseConstListByType("10"), "houseConstType", houseLeaseQO.getHouseLeasetypeId());
        //2.验证房屋出租方式id 数值是否在可取范围之内 数据库内 type = 11 的就是租房方式  整租还是合租之类...
        //ValidatorUtils.validFieldVal(iCommonService.getHouseConstListByType("11"), "houseConstType", houseLeaseQO.getHouseLeaseymodeId());
        //3.验证房屋出租押金方式id 数值是否在可取范围之内 数据库内 type = 1  的就是租房押金  押一付一 还是 押一付三之类...
        //ValidatorUtils.validFieldVal(iCommonService.getHouseConstListByType("1"), "houseConstType", houseLeaseQO.getHouseLeasedepositId());
        //4.验证房屋类型id 数值是否在可取范围之内 数据库内 type = 2  的就是租房押金  五室二厅一卫 还是 三室二厅一卫...
        //ValidatorUtils.validFieldVal(iCommonService.getHouseConstListByType("2"), "houseConstType", houseLeaseQO.getHouseTypeId());
        //5.验证房屋装修方式id 数值是否在可取范围之内 数据库内 type = 3  的就是装修风格  简约、欧美、古典...
        //ValidatorUtils.validFieldVal(iCommonService.getHouseConstListByType("3"), "houseConstType", houseLeaseQO.getHouseStyleId());
        //5.验证房屋房源类型id 数值是否在可取范围之内 数据库内 type = 12   73不限(默认) 74可短租 75邻地铁  76压一付一  77配套齐全  78精装修 79南北通透  80有阳台...
        //ValidatorUtils.validFieldVal(iCommonService.getHouseConstListByType("12"), "houseConstType", houseLeaseQO.getHouseStyleId());
        //参数效验完成 新增
        iHouseLeaseService.addLeaseSaleHouse(houseLeaseQO);
        return CommonResult.ok();
    }

    @ApiOperation("删除房屋租售")
    @DeleteMapping()//delHouseLease
    @Login
    public CommonResult<Boolean> delLeaseHouse(@RequestParam Long rowGuid) {
        return iHouseLeaseService.delLeaseHouse(rowGuid, UserUtils.getUserId()) ? CommonResult.ok() : CommonResult.error(1, "数据不存在");
    }


    @ApiOperation("查询房屋出租数据")
    @GetMapping()//queryHouseLeaseByList
    @Login
    public CommonResult<Boolean> queryHouseLeaseByList(@RequestParam BaseQO<HouseLeaseQO> houseLeaseQO) {
        //验证 分页参数合法性
        ValidatorUtils.validatePageParam(houseLeaseQO);
        List<HouseLeaseVO> res = iHouseLeaseService.queryHouseLeaseByList(houseLeaseQO);
        return null;
    }




}
