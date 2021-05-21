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
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.es.ElasticsearchCarUtil;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jsy.community
 * @description:  物业车辆查询
 * @author: Hu
 * @create: 2021-03-26 09:26
 **/
@Api(tags = "物业查询车辆控制器")
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
        AdminInfoVo info = UserUtils.getAdminUserInfo();

        return CommonResult.ok(elasticsearchCarService.searchData(baseQO,info));
    }

    @ApiOperation("车辆类型查询")
    @GetMapping("/catType")
    @Login
    public CommonResult catType() {
        return CommonResult.ok(BusinessEnum.CarTypeEnum.CAR_TYPE_LIST);
    }


    @ApiOperation("车辆批量更新")
    @PostMapping("/updateAll")
    public CommonResult updateAll() {
        elasticsearchCarService.updateCars();
        return CommonResult.ok();
    }

    @ApiOperation("全部删除")
    @PostMapping("/deleteAll")
    public CommonResult deleteAll() {
        ElasticsearchCarUtil.deleteDataAll(restHighLevelClient);
        return CommonResult.ok();
    }
}
