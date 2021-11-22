package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * @author 公共
 * @since  2021/2/7 9:06
 */
@RequestMapping("/common")
@Api(tags = "公共控制器")
@Slf4j
@RestController
@ApiJSYController
public class CommonController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

	@ApiOperation("查询下级省市区、查询城市等")
    @GetMapping("/region")
    public CommonResult<?> queryRegion(@RequestParam Integer queryType,Integer regionNumber,String searchStr) {
        String queryMethodName = BusinessEnum.RegionQueryTypeEnum.regionQueryNameMap.get(queryType);
        if(queryMethodName == null){
            return CommonResult.error(JSYError.REQUEST_PARAM);
        }
        try {
            //调用 用查询类型ID找到的 对应的查询方法
            Method queryMethod;
            Object invoke = null;
            //不带参
            if(BusinessEnum.RegionQueryTypeEnum.regionQueryTypeMap.get(queryType) == 2){
                queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName);
                invoke = queryMethod.invoke(commonService);
            }else{
                //带参
                Class paramType = BusinessEnum.RegionQueryTypeEnum.regionQueryClassTypeMap.get(queryType);
                if(paramType.equals(Integer.class)){
                    queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName,paramType);
                    invoke = queryMethod.invoke(commonService,regionNumber);
                }else if(paramType.equals(String.class)){
                    queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName,paramType);
                    invoke = queryMethod.invoke(commonService,searchStr);
                }else if(paramType.equals(Object.class)){
                    queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName,String.class,Integer.class);
                    invoke = queryMethod.invoke(commonService,searchStr,regionNumber);
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
