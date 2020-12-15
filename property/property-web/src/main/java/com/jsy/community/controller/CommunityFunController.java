package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommunityFunService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.CommunityFunQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-09 10:53
 **/
@Api(tags = "社区趣事控制器")
@RestController
@RequestMapping("/communityfun")
@ApiJSYController
public class CommunityFunController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICommunityFunService communityFunService;


    @ApiOperation("分页查询所有社区趣事")
    @PostMapping("/list")
    public CommonResult<Map> list(@RequestBody CommunityFunQO communityFunQO) {
        Map<String, Object> map = communityFunService.findList(communityFunQO);
        return CommonResult.ok(map);
    }
    @ApiOperation("新增")
    @PostMapping("/save")
    public CommonResult save(@RequestBody CommunityFunEntity communityFunEntity) {
        communityFunEntity.setStatus(1);
        communityFunEntity.setId(SnowFlake.nextId());
        return  communityFunService.save(communityFunEntity)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }

    @ApiOperation("新增缩略图")
    @PostMapping("/smallImge")
    public CommonResult upload(@RequestBody MultipartFile file) {
        String upload = MinioUtils.upload(file, "smallimge");
        return  CommonResult.ok(upload);
    }
    @ApiOperation("新增封面图片")
    @PostMapping("/coverImge")
    public CommonResult coverImge(@RequestBody MultipartFile file) {
        String upload = MinioUtils.upload(file, "coverimge");
        return  CommonResult.ok(upload);
    }
    @ApiOperation("新增内容图片")
    @PostMapping("/contentImge")
    public CommonResult content(@RequestBody MultipartFile file) {
        String upload = MinioUtils.upload(file, "contentimge");
        return  CommonResult.ok(upload);
    }
    @ApiOperation("修改")
    @PutMapping("/update")
    public CommonResult update(@RequestBody CommunityFunEntity communityFunEntity) {
        return communityFunService.updateById(communityFunEntity)? CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public CommonResult delete(@ApiParam("社区趣事id")
                                   @RequestParam Long id) {
        return communityFunService.removeById(id)? CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }

    @ApiOperation("查询一条---表单回填")
    @DeleteMapping("/findOne")
    public CommonResult findOne(@ApiParam("社区趣事id")
                               @RequestParam Long id) {
        CommunityFunEntity communityFunEntity = communityFunService.getById(id);
        return  CommonResult.ok(communityFunEntity);
    }

    @ApiOperation("上线")
    @GetMapping("/popUpOnline")
    public CommonResult popUpOnline(@ApiParam("社区趣事id")
                                      @RequestParam Long id) {
        CommunityFunEntity byId = communityFunService.getById(id);
        byId.setStatus(2);
        byId.setStartTime(LocalDateTime.now());
        communityFunService.updateById(byId);
        return  CommonResult.ok();
    }

    @ApiOperation("下线")
    @GetMapping("/tapeOut")
    public CommonResult tapeOut(@ApiParam("社区趣事id")
                                  @RequestParam Long id) {
        CommunityFunEntity byId = communityFunService.getById(id);
        byId.setOutTime(LocalDateTime.now());
        byId.setStatus(1);
        communityFunService.updateById(byId);
        return  CommonResult.ok();
    }


}
