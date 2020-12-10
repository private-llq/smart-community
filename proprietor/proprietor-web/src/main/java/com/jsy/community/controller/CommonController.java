package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommonService;
import com.jsy.community.constant.BusinessEnum;
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

    @ApiOperation("社区区域查询接口")
    @GetMapping("/community")
	@SuppressWarnings("unchecked")
    @Login
    public CommonResult<?> queryZone(@RequestParam Integer id, @RequestParam Integer houseLevelMode, @RequestParam Integer queryType) {
        //通过查询类型ID找到对应的 服务方法
        CommunityType communityType = CommunityType.valueOf(queryType);
        //当枚举类并没有这个查询类型时，抛出400请求参数错误异常
        if (communityType == null) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "查询社区ID不能为空!");
        }
        try {
            //调用 用查询类型ID找到的 对应的查询方法
            Method commonZoneApi = ICommonService.class.getDeclaredMethod(communityType.method(), Integer.class, Integer.class);
            //根据社区层级结构id 和 传过来的id 判断用户的社区 具体层级结构
            Object invoke = commonZoneApi.invoke(commonService, id,houseLevelMode);
            if (invoke == null) {
                return CommonResult.ok(null);
            }
            return CommonResult.ok((List<Map<String, Object>>) invoke);
        } catch (Exception e) {
            log.error("com.jsy.community.controller.CommonController.queryZone：{}", e.getMessage());
            e.printStackTrace();
        	//如果出现异常，说明服务并不能调通
            return CommonResult.error(JSYError.NOT_IMPLEMENTED);
        }
    }




	@ApiOperation("查询下级省市区、查询城市等")
    @GetMapping("/region")
    public CommonResult<?> queryRegion(@RequestParam Integer queryType,Integer regionNumber,String searchStr) {
        String queryMethodName = BusinessEnum.RegionQueryTypeEnum.regionQueryNameMap.get(queryType);
        if(queryMethodName == null){
            return CommonResult.error(JSYError.REQUEST_PARAM);
        }
        try {
            //调用 用查询类型ID找到的 对应的查询方法
            Method queryMethod = null;
            Object invoke = null;
            if(BusinessEnum.RegionQueryTypeEnum.regionQueryTypeMap.get(queryType) == 2){//不带参
                queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName);
                invoke = queryMethod.invoke(commonService);
            }else{//带参
                Class paramType = BusinessEnum.RegionQueryTypeEnum.regionQueryClassTypeMap.get(queryType);
                queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName,paramType);
                if(paramType.equals(Integer.class)){
                    invoke = queryMethod.invoke(commonService,regionNumber);
                }else{
                    invoke = queryMethod.invoke(commonService,searchStr);
                }
            }
            if (invoke == null) {
                return CommonResult.ok(null);
            }
            return CommonResult.ok(invoke);
        } catch (Exception e) {
            log.error("com.jsy.community.controller.CommonController.queryRegion：{}", e.getMessage());
            //如果出现异常，说明服务并不能调通
            return CommonResult.error(JSYError.NOT_FOUND);
        }
    }
    
}
