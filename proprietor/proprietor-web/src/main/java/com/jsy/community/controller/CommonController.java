package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommonService;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.CommunityType;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

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

    @Resource
    private RestHighLevelClient elasticsearchClient;

    /**
     * @author YuLF
     * @since  2021/11/9 17:58
     */
    @ApiOperation("社区区域查询接口")
    @GetMapping("/community")
	@SuppressWarnings("unchecked")
    @Login
    public CommonResult<?> queryZone(@RequestParam Long id, @RequestParam(required = false, defaultValue = "1") Integer houseLevelMode,
                                     @RequestParam Integer queryType,
                                     @RequestParam(required = false, defaultValue = "1")Integer page,
                                     @RequestParam(required = false, defaultValue = "10")Integer pageSize) {
        //通过查询类型ID找到对应的 服务方法
        CommunityType communityType = CommunityType.valueOf(queryType);
        //当枚举类并没有这个查询类型时，抛出400请求参数错误异常
        if (communityType == null) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "查询社区ID不能为空!");
        }
        try {
            //调用 用查询类型ID找到的 对应的查询方法
            Method commonZoneApi = ICommonService.class.getDeclaredMethod(communityType.method(), Long.class, Integer.class, Integer.class, Integer.class);
            //根据社区层级结构id 和 传过来的id 判断用户的社区 具体层级结构
            Object invoke = commonZoneApi.invoke(commonService, id, houseLevelMode, page, pageSize);
            if (invoke == null) {
                return CommonResult.ok(null);
            }
            return CommonResult.ok((List<Map<String, Object>>) invoke);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("com.jsy.community.controller.CommonController.queryZone：{}", e.getMessage());
        	//如果出现异常，说明服务并不能调通
            return CommonResult.error(JSYError.NOT_IMPLEMENTED);
        }
    }


    /**
     * app 全文搜索
     * @param size  搜索离text最相近条数
     * @param text  搜索文本
     * @author YuLF
     * @since  2021/3/9 16:58
     */
    @Login
    @ApiOperation("App全文搜索")
    @GetMapping("/search")
    public CommonResult<List<JSONObject>> search(@RequestParam( required = false, defaultValue = "10") Integer size, @RequestParam String text)  {
        if( Objects.nonNull(text) && text.length() > BusinessConst.HOT_KEY_MAX_NUM  ){
            throw new JSYException(" 搜索文本太长了! ");
        }
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(BusinessConst.FULL_TEXT_SEARCH_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //排除某些字段
        String[] excludes = {"esId","searchTitle"};
        String[] includes = {"*"};
        searchSourceBuilder.fetchSource(includes, excludes);
        //查询条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("searchTitle", text));
        searchSourceBuilder.size(size);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            throw new JSYException(" 没有找到任何数据! ");
        }
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<JSONObject> list = new ArrayList<>(hits.length);
        for( SearchHit hit : hits){
            String sourceAsString = hit.getSourceAsString();
            list.add(JSON.parseObject(sourceAsString, JSONObject.class));
        }
        //搜索词添加至Redis热词排行里面
        commonService.addFullTextSearchHotKey( text );
        return CommonResult.ok(list);
    }

    /**
     * @author YuLF
     * @since  2021/3/10 14:46
     */
    @Login
    @ApiOperation("App全文搜索热词推荐 热搜榜")
    @GetMapping("/hotKey")
    public Set<Object> getHotKey(@RequestParam( required = false, defaultValue = "10")Integer num ){
        if( num < 1 || num > BusinessConst.HOT_KEY_MAX_NUM){
            num = 20;
        }
        return commonService.getFullTextSearchHotKey(num);
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
            Method queryMethod;
            Object invoke;
            //不带参
            if(BusinessEnum.RegionQueryTypeEnum.regionQueryTypeMap.get(queryType) == 2){
                queryMethod = ICommonService.class.getDeclaredMethod(queryMethodName);
                invoke = queryMethod.invoke(commonService);
            }else{
                //带参
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
    
//    @IpLimit(prefix = "weatherNow", second = 30, count = 1, desc = "获取首页天气")
    @ApiOperation("首页天气")
    @GetMapping("weatherNow")
    public CommonResult<JSONObject> getWeatherNow(@RequestParam double lon,@RequestParam double lat){
//        JSONObject weather = commonService.getWeather(lon, lat);
        //TODO 天气接口未购买，临时用假数据
        JSONObject weather = commonService.getTempWeather();
        return CommonResult.ok(weather);
    }
    
//    @IpLimit(prefix = "weatherDetails", second = 30, count = 1, desc = "获取天气详情")
    @ApiOperation("天气详情")
    @GetMapping("weatherDetails")
    public CommonResult<JSONObject> getWeatherNowDetails(@RequestParam double lon,@RequestParam double lat){
//        JSONObject weather = commonService.getWeatherDetails(lon, lat);
        //TODO 天气接口未购买，临时用假数据
        JSONObject weather = commonService.getTempWeatherDetails();
        return CommonResult.ok(weather);
    }


    
}
