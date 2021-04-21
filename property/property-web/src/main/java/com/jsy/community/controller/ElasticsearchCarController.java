package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Desensitization;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IElasticsearchCarService;
import com.jsy.community.aspectj.DesensitizationType;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ElasticsearchCarSearchQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-26 09:26
 **/
@Api(tags = "社区车辆控制器")
@RestController
@RequestMapping("/car")
@ApiJSYController
public class ElasticsearchCarController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IElasticsearchCarService elasticsearchCarService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @ApiOperation("分页查询所有车辆")
    @PostMapping("/list")
    @Login
    @Desensitization(type = {DesensitizationType.PHONE,DesensitizationType.ID_CARD}, field = {"mobile","idCard"})
    public CommonResult list(@RequestBody BaseQO<ElasticsearchCarSearchQO> baseQO) {
        return CommonResult.ok(elasticsearchCarService.searchData(baseQO));
    }

    @ApiOperation("车辆类型查询")
    @GetMapping("/catType")
    @Login
    public CommonResult catType() {
        return CommonResult.ok(BusinessEnum.CarTypeEnum.CAR_TYPE_LIST);
    }


//    @ApiOperation("车辆批量更新")
//    @PostMapping("/updateAll")
//    @Login
//    public CommonResult updateAll(@RequestBody TestQO testQO) {
//        List<ElasticsearchCarQO> cars = testQO.getCars();
//        for (ElasticsearchCarQO elasticsearchCarQO : cars) {
//            ElasticsearchCarUtil.insertData(elasticsearchCarQO,restHighLevelClient);
//        }
//        return CommonResult.ok();
//    }
}
