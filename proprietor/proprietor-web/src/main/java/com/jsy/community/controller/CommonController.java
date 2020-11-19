package com.jsy.community.controller;

import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.web.ApiProprietor;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@RequestMapping("/common")
@Api(tags = "公共控制器")
@Login( allowAnonymous = true)
@Slf4j
@RestController
@ApiProprietor
public class CommonController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommonService commonService;

    //http://localhost:8080/common/zone?id=(t_house的pid)&queryType=(暂定：1查所有社区，2传社区id查所有单元，3传单元id查所有楼栋，4传楼栋id查所有楼层，5传楼层id查所有门牌)
    //http://192.168.12.11:8080/common/zone?id=4&queryType=5   查id为4楼层下面的所有门牌
    @ApiOperation("社区区域查询接口")
    @GetMapping("/community")
	@SuppressWarnings("unchecked")
    public CommonResult<?> queryZone(@RequestParam(required = true) Integer id, @RequestParam(required = true) Integer queryType) {
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
	
	@ApiOperation("查询下级省市区列表 + 获取城市列表")
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
            if(CommonQueryConsts.RegionQueryTypeEnum.CITY_MAP.getCode().equals(queryType)){//查询城市列表不带参
                queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName);
                invoke = queryMethod.invoke(commonService);
            }else{
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
