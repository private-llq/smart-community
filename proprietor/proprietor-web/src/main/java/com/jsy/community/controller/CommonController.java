package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.IUserSearchService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.CommunityType;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author 公共
 * @since  2021/2/7 9:06
 */
@RequestMapping("/common")
@Api(tags = "公共控制器")
@Slf4j
@RestController
// @ApiJSYController
public class CommonController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserSearchService userSearchService;

    @LoginIgnore
    @GetMapping("community2")
    // @Permit("community:proprietor:common:community2")
    public CommonResult test(@RequestParam Long id,@RequestParam Integer queryType){
        Integer page = 0;
        Integer size = 10;
        switch (queryType){
	        case 1:
	        	return CommonResult.ok(commonService.getAllCommunityFormCityId(id,page,size));
            case 2:
                return CommonResult.ok(commonService.getBuildingOrUnitByCommunityId(id,page,size));
            case 3:
                return CommonResult.ok(commonService.getUnitOrFloorById2(id,page,size));
        }
        return CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }
    
    /**
     * @author YuLF
     * @since  2021/11/9 17:58
     */
    @ApiOperation("社区区域查询接口 社区~楼层")
    @GetMapping("/community")
	@SuppressWarnings("unchecked")
    // @Permit("community:proprietor:common:community")
    public CommonResult<?> queryZone(@RequestParam Long id,
                                     @RequestParam Integer queryType,
                                     @RequestParam(required = false, defaultValue = "1")Integer page,
                                     @RequestParam(required = false, defaultValue = "10")Integer size) {
        //验证分页参数
        page = ValidatorUtils.isInteger(page) ? page : 1;
        size = ValidatorUtils.isInteger(size) ? size : 10;

        //通过查询类型ID找到对应的 服务方法
        CommunityType communityType = CommunityType.valueOf(queryType);
        //当枚举类并没有这个查询类型时，抛出400请求参数错误异常
        if (communityType == null) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "查询ID不能为空!");
        }
        try {
            //调用 用查询类型ID找到的 对应的查询方法
            Method commonZoneApi = ICommonService.class.getDeclaredMethod(communityType.method(), Long.class, Integer.class, Integer.class);
            //根据社区层级结构id 和 传过来的id 判断用户的社区 具体层级结构
            Object invoke = commonZoneApi.invoke(commonService, id, (page - 1) * size, size);
            if ( Objects.isNull(invoke) ) {
                return CommonResult.ok(null);
            }
            return CommonResult.ok((List<Map<String, Object>>) invoke);
        } catch (Exception e) {
            log.error("com.jsy.community.controller.CommonController.queryZone：{}", e.getMessage());
        	//如果出现异常，说明服务并不能调通
            return CommonResult.error(JSYError.NOT_IMPLEMENTED);
        }
    }

    @GetMapping("/getHouse")
    @ApiImplicitParams(
            value = {@ApiImplicitParam(name = "floor", value = "楼层文本"),@ApiImplicitParam( name = "id", value = "单元或楼栋id")}
    )
    @ApiOperation("通过楼层文本查下面所有房屋")
    // @Permit("community:proprietor:common:getHouse")
    public CommonResult<List<Map<String, Object>>> getHouseByFloor(@RequestParam Long id, @RequestParam String floor){
        return CommonResult.ok(commonService.getHouseByFloor( id, floor ));
    }

    /**
     * @author YuLF
     * @since  2021/3/10 14:46
     */
    @ApiOperation("App全文搜索热词推荐 热搜榜")
    @GetMapping("/hotKey")
    // @Permit("community:proprietor:common:hotKey")
    public Set<Object> getHotKey(@RequestParam( required = false, defaultValue = "10")Integer num ){
        if( num < 1 || num > BusinessConst.HOT_KEY_MAX_NUM){
            num = 20;
        }
        return commonService.getFullTextSearchHotKey(num);
    }

    /**
     * @Description: 查询个人搜索词汇
     * @author: Hu
     * @since: 2021/4/16 17:01
     * @Param:
     * @return:
     */
    @ApiOperation("App全文搜索个人词汇")
    @GetMapping("/getUserKey")
    // @Permit("community:proprietor:common:getUserKey")
    public CommonResult getUserKey(@RequestParam("num")Integer num ){
        if (num==0||num==null){
            num=10;
        }
        String userId = UserUtils.getUserId();
        String[] key = userSearchService.searchUserKey(userId, num);
        return CommonResult.ok(key);
    }
    /**
     * @Description: 删除个人搜索词汇
     * @author: Hu
     * @since: 2021/4/16 17:01
     * @Param:
     * @return:
     */
    @ApiOperation("App删除全文搜索个人词汇")
    @DeleteMapping("/deleteUserKey")
    // @Permit("community:proprietor:common:deleteUserKey")
    public CommonResult deleteUserKey(){
        String userId = UserUtils.getUserId();
        userSearchService.deleteUserKey(userId);
        return CommonResult.ok();
    }

    @LoginIgnore
	@ApiOperation("查询下级省市区、查询城市等")
    @GetMapping("/region")
    // @Permit("community:proprietor:common:region")
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

    /**
     * @author: Pipi
     * @description: 获取城市名称
     * @param regionName: 城市名称
     * @return: {@link CommonResult<?>}
     * @date: 2022/1/4 14:20
     **/
    @LoginIgnore
    @GetMapping("/v2/getRegionName")
    public CommonResult<?> getRegionName(@RequestParam String regionName) {
        if (StringUtil.isNotBlank(regionName)) {
            RegionEntity regionEntity = commonService.queryRegionByName(regionName);
            if (regionEntity == null) {
                return CommonResult.ok("重庆市", null);
            } else {
                return CommonResult.ok(regionEntity.getName(), null);
            }
        } else {
            return CommonResult.ok("重庆市", null);
        }
    }
    
    @LoginIgnore
    //    @IpLimit(prefix = "weatherNow", second = 30, count = 1, desc = "获取首页天气，调用限制用于经纬度接口，经纬度方式做不了缓存，由前端做缓存")
    @ApiOperation("首页天气")
    @GetMapping("weatherNow")
    // @Permit("community:proprietor:common:weatherNow")
    public CommonResult<JSONObject> getWeatherNow(@RequestParam String cityName){
        //真实数据
        JSONObject weather = commonService.getWeather(cityName);
        //假数据
//        JSONObject weather = commonService.getTempWeather();
        return CommonResult.ok(weather);
    }
    
    @LoginIgnore
    //    @IpLimit(prefix = "weatherDetails", second = 30, count = 1, desc = "获取天气详情，调用限制用于经纬度接口，经纬度方式做不了缓存，由前端做缓存")
    @ApiOperation("天气详情")
    @GetMapping("weatherDetails")
    // @Permit("community:proprietor:common:weatherDetails")
    public CommonResult<JSONObject> getWeatherNowDetails(@RequestParam String cityName){
        //真实数据
        JSONObject weather = commonService.getWeatherDetails(cityName);
        //假数据
//        JSONObject weather = commonService.getTempWeatherDetails();
        return CommonResult.ok(weather);
    }


    
}
