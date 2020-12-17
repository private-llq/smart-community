package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.vo.CommonResult;
import com.jsy.lease.api.IHouseConstService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @GetMapping()
    @ApiOperation("房屋常量查询根据id")
    public CommonResult<Map<String, List<HouseLeaseConstEntity>>> all(@RequestBody Long[] ids) {
        Map<String, List<HouseLeaseConstEntity>> map = new HashMap<>();
        for( Long id : ids ){
            List<HouseLeaseConstEntity> houseConstListByType = houseConstService.getHouseConstListByType(String.valueOf(id));
            if( houseConstListByType == null || houseConstListByType.isEmpty() ){
                continue;
            }
            map.put(houseConstListByType.get(0).getAnnotation(), houseConstListByType);
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
    @ApiOperation("根据发布源类型获取其标签")
    public CommonResult getTag(@ApiParam("发布源类型") Integer id){
        List<HouseLeaseConstEntity> list = houseConstService.getTag(id);
        return CommonResult.ok(list);
    }
    
    
    
    
    
    
}
