package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ISelectCommunityFunService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.qo.CommunityFunQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 业主端的社区趣事查询接口
 * @author: Hu
 * @create: 2020-12-09 17:04
 **/
@Api(tags = "社区趣事控制器")
@RestController
@RequestMapping("/communityfun")
@ApiJSYController
public class SelectCommunityFunController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ISelectCommunityFunService selectCommunityFunService;

    /**
     * @Description: 分页查询所有趣事
     * @author: Hu
     * @since: 2021/2/23 17:30
     * @Param:
     * @return:
     */
    @ApiOperation("分页查询社区趣事")
    @PostMapping("/findList")
    public CommonResult<Map> list(@RequestBody CommunityFunQO communityFunQO) {
        Map<String, Object> map = selectCommunityFunService.findList(communityFunQO);
        return CommonResult.ok(map);
    }
    /**
     * @Description: 查询一条社区趣事详情，并添加浏览量
     * @author: Hu
     * @since: 2021/2/23 17:30
     * @Param:
     * @return:
     */
    @ApiOperation("查询一条社区趣事详情传入id")
    @GetMapping("/findFunOne")
    public CommonResult<CommunityFunEntity> findOne(@RequestParam("id")Long id) {
        CommunityFunEntity communityFunEntity=selectCommunityFunService.findFunOne(id);
        selectCommunityFunService.saveViewCount(id);
        return CommonResult.ok(communityFunEntity);
    }
}
