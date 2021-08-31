package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.api.IHouseConstService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author YuLF
 * @since 2020-12-16 14:28
 */
@Slf4j
@ApiJSYController
@RestController
@RequestMapping("/const")
@Api(tags = "房屋租售常量查询控制器")
public class HouseConstController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;

    /**
     * @Author lihao
     * @Description 配套设施常量类型
     * @Date 2021/1/13 15:55
     **/
    private static final Integer FACILITY_TYPE = 16;

    /**
     * @Author lihao
     * @Description 客流人群常量类型
     * @Date 2021/1/13 15:55
     **/
    private static final Integer PEOPLE_TYPE = 17;


    @PostMapping()
    @ApiOperation("房屋常量查询根据id")
    public CommonResult<Map<String, List<HouseLeaseConstEntity>>> all(@RequestBody Long[] ids) {
        Map<String, List<HouseLeaseConstEntity>> map = new HashMap<>();
        for (Long id : ids) {
            List<HouseLeaseConstEntity> houseConstListByType = houseConstService.getHouseConstListByType(String.valueOf(id));
            if (houseConstListByType == null || houseConstListByType.isEmpty()) {
                continue;
            }
            map.put(houseConstListByType.get(0).getHouseConstType(), houseConstListByType);
        }
        return CommonResult.ok(map);
    }


    /**
     * @return com.jsy.community.vo.CommonResult
     * @Author lihao
     * @Description 根据发布源类型获取其标签
     * @Date 2020/12/17 10:57
     * @Param [id]
     **/
    @GetMapping("/getTag")
    @ApiOperation("查询商铺标签")
    public CommonResult getTag() {
        Map<String, Object> list = houseConstService.getTag();
        return CommonResult.ok(list);
    }


    @ApiOperation("商铺类型标签查询")
    @GetMapping("/getShopType")
    public CommonResult getShopType() {
        String type = "7";
        List<HouseLeaseConstEntity> constEntityList = houseConstService.getHouseConstListByType(type);
        return CommonResult.ok(constEntityList);
    }

    @ApiOperation("商铺行业标签查询")
    @GetMapping("/getShopBusiness")
    public CommonResult getShopBusiness() {
        String type = "8";
        List<HouseLeaseConstEntity> constEntityList = houseConstService.getHouseConstListByType(type);
        return CommonResult.ok(constEntityList);
    }

    @ApiOperation("商铺发布时的配套设施和客流人群选项")
    @GetMapping("/getShopTags")
    public CommonResult getAddShopTags() {
        Map<String, Object> map = houseConstService.getAddShopTags(FACILITY_TYPE, PEOPLE_TYPE);
        return CommonResult.ok(map);
    }


}
