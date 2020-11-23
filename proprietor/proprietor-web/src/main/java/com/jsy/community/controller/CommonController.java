package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonService;
import com.jsy.community.constant.CommonQueryConsts;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.CommunityType;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@RequestMapping("/common")
@Api(tags = "公共控制器")
@Slf4j
@RestController
@ApiJSYController
public class CommonController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommonService commonService;

    //(暂定：1传城市id查所有社区，2传社区id查所有单元，3传单元id查所有楼栋，4传楼栋id查所有楼层，5传楼层id查所有门牌)
    @ApiOperation("社区区域查询接口")
    @GetMapping("/community")
	@SuppressWarnings("unchecked")
    public CommonResult<?> queryZone(@RequestParam Integer id, @RequestParam Integer queryType) {
        //查询结果Map
        Map<Integer, String> zoneCodeName = null;
        //通过查询类型ID找到对应的 服务方法
        CommunityType communityType = CommunityType.valueOf(queryType);
        //当枚举类并没有这个查询类型时，抛出400请求参数错误异常
        if (communityType == null) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "查询社区ID不能为空!");
        }
        try {
            //调用 用查询类型ID找到的 对应的查询方法
            Method commonZoneApi = ICommonService.class.getDeclaredMethod(communityType.method(), Integer.class);
            //传入用户提供的 社区/单元/楼栋/楼层/ id 查询该级下一级的数据 如 传入某个单元id 查出这个单元下面的所有楼栋
            Object invoke = commonZoneApi.invoke(commonService, id);
            if (invoke == null) {
                return CommonResult.ok(null);
            }
            return CommonResult.ok((List<Map>) invoke);
        } catch (Exception e) {
            log.error("com.jsy.community.controller.CommonController.queryZone：{}", e.getMessage());
        	//如果出现异常，说明服务并不能调通
            return CommonResult.error(JSYError.NOT_FOUND);
        }
    }
	
	@ApiOperation("查询下级省市区、查询城市等")
    @GetMapping("/region")
    public CommonResult<?> queryRegion(@RequestParam(required = true) Integer queryType,Integer regionNumber) {
        String queryMethodName = CommonQueryConsts.RegionQueryTypeEnum.regionQueryTypeMap.get(queryType);
        if(queryMethodName == null){
            return CommonResult.error(JSYError.REQUEST_PARAM);
        }
        try {
            //调用 用查询类型ID找到的 对应的查询方法
            Method queryMethod = null;
            Object invoke = null;
            if(!CommonQueryConsts.RegionQueryTypeEnum.SUB.getCode().equals(queryType)){//不带参
                queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName);
                invoke = queryMethod.invoke(commonService);
            }else{//带参
                queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName,Integer.class);
                invoke = queryMethod.invoke(commonService,regionNumber);
            }
            if (invoke == null) {
                return CommonResult.ok(null);
            }
            return CommonResult.ok(invoke);
        } catch (Exception e) {
            log.error("com.jsy.community.controller.CommonController.queryZone：{}", e.getMessage());
            //如果出现异常，说明服务并不能调通
            return CommonResult.error(JSYError.NOT_FOUND);
        }
    }

}
