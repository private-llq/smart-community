package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommunityFunService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.qo.CommunityFunQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
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

    private final String[] img ={"jpg","png","jpeg","gif"};

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICommunityFunService communityFunService;


    @ApiOperation("分页查询所有社区趣事")
    @PostMapping("/list")
    @Login
    public CommonResult<Map> list(@RequestBody CommunityFunQO communityFunQO) {
        Map<String, Object> map = communityFunService.findList(communityFunQO);
        return CommonResult.ok(map);
    }
    @ApiOperation("新增")
    @PostMapping("/save")
    @Login
    public CommonResult save(@RequestBody CommunityFunEntity communityFunEntity) {
        ValidatorUtils.validateEntity(communityFunEntity, CommunityFunEntity.CommunityFunValidated.class);
        String userId = UserUtils.getUserId();
        communityFunEntity.setStatus(2);
        communityFunEntity.setUid(userId);
        communityFunEntity.setId(SnowFlake.nextId());
        communityFunService.insetOne(communityFunEntity);
        return  CommonResult.ok();
    }

    @ApiOperation("新增缩略图")
    @PostMapping("/smallImge")
    @Login
    public CommonResult upload(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "smallimge");
        return  CommonResult.ok(upload);
    }

    @ApiOperation("新增封面图片")
    @PostMapping("/coverImge")
    @Login
    public CommonResult coverImge(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "coverimge");
        return  CommonResult.ok(upload);
    }

    @ApiOperation("新增内容图片")
    @PostMapping("/contentImge")
    @Login
    public CommonResult content(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "contentimge");
        return  CommonResult.ok(upload);
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    @Login
    public CommonResult update(@RequestBody CommunityFunEntity communityFunEntity) {
        ValidatorUtils.validateEntity(communityFunEntity, CommunityFunEntity.CommunityFunValidated.class);
        communityFunService.updateOne(communityFunEntity);
        return CommonResult.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete")
    @Login
    public CommonResult delete(@ApiParam("社区趣事id")
                               @RequestParam("id") Long id) {
        communityFunService.deleteById(id);
        return CommonResult.ok();
    }

    @ApiOperation("查询一条---表单回填")
    @GetMapping("/findOne")
    @Login
    public CommonResult findOne(@ApiParam("社区趣事id")
                                    @RequestParam("id") Long id) {
        CommunityFunEntity communityFunEntity = communityFunService.selectOne(id);
        return  CommonResult.ok(communityFunEntity);
    }

    @ApiOperation("上线")
    @GetMapping("/popUpOnline")
    @Login
    public CommonResult popUpOnline(@ApiParam("社区趣事id")
                                        @RequestParam("id") Long id) {
        communityFunService.popUpOnline(id);
        return  CommonResult.ok();
    }

    @ApiOperation("下线")
    @GetMapping("/tapeOut")
    @Login
    public CommonResult tapeOut(@ApiParam("社区趣事id")
                                @RequestParam Long id) {
        communityFunService.tapeOut(id);
        return  CommonResult.ok();
    }


}
