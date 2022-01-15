package com.jsy.community.controller;

import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICommunityFunService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CommunityFunOperationQO;
import com.jsy.community.qo.property.CommunityFunQO;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业社区趣事
 * @author: Hu
 * @create: 2020-12-09 10:53
 **/
@Api(tags = "社区趣事控制器")
@RestController
@RequestMapping("/communityfun")
// @ApiJSYController
public class CommunityFunController {

    private final String[] img ={"jpg","png","jpeg"};

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICommunityFunService communityFunService;


    @ApiOperation("分页查询所有社区趣事")
    @PostMapping("/list")
    @Permit("community:property:communityfun:list")
    public CommonResult list(@RequestBody BaseQO<CommunityFunQO> baseQO) {
        PageInfo pageInfo = communityFunService.findList(baseQO);
        return CommonResult.ok(pageInfo);
    }
    @ApiOperation("新增")
    @PostMapping("/save")
    @businessLog(operation = "新增",content = "新增了【物业社区趣事】")
    @Permit("community:property:communityfun:save")
    public CommonResult save(@RequestBody CommunityFunOperationQO communityFunOperationQO) {
        ValidatorUtils.validateEntity(communityFunOperationQO, CommunityFunOperationQO.CommunityFunOperationValidated.class);
        AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
        communityFunService.insetOne(communityFunOperationQO,adminInfoVo);
        return  CommonResult.ok();
    }

    @ApiOperation("新增缩略图")
    @PostMapping("/smallImge")
    @Permit("community:property:communityfun:smallImge")
    public CommonResult upload(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "smallimge");
        return  CommonResult.ok(upload,"上传成功");
    }

    @ApiOperation("新增封面图片")
    @PostMapping("/coverImge")
    @Permit("community:property:communityfun:coverImge")
    public CommonResult coverImge(@RequestParam("file") MultipartFile file) throws IOException {
        if (PicUtil.isPic(file)){
            String originalFilename = file.getOriginalFilename();
            String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            File tempFile = new File(file.getOriginalFilename());
            try {
                Thumbnails.of(file.getInputStream())
                        .scale(0.25f)
                        .toFile(tempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!Arrays.asList(img).contains(s)) {
                return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
            }
            String upload = MinioUtils.upload(file, "coverimge");
            FileInputStream input = new FileInputStream(tempFile);
            MultipartFile multipartFile =new MockMultipartFile("file", tempFile.getName(), "image/png", IOUtils.toByteArray(input));
            String upload2 = MinioUtils.upload(multipartFile, "coverimge");
            Map<String, String> map = new HashMap<>();
            map.put("coverImge",upload);
            map.put("smallImge",upload2);
            return  CommonResult.ok(map,"上传成功");
        }
        return CommonResult.error("上传失败");

    }

    @ApiOperation("新增内容图片")
    @PostMapping("/contentImge")
    @Permit("community:property:communityfun:contentImge")
    public CommonResult content(@RequestParam("file") MultipartFile file){
        if (PicUtil.isPic(file)){
            String originalFilename = file.getOriginalFilename();
            String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            if (!Arrays.asList(img).contains(s)) {
                return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
            }
            String upload = MinioUtils.upload(file, "contentimge");
            return  CommonResult.ok(upload,"上传成功");
        }
        return  CommonResult.error("上传失败");
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    @businessLog(operation = "编辑",content = "更新了【物业社区趣事】")
    @Permit("community:property:communityfun:update")
    public CommonResult update(@RequestBody CommunityFunOperationQO communityFunOperationQO) {
        ValidatorUtils.validateEntity(communityFunOperationQO, CommunityFunOperationQO.CommunityFunOperationValidated.class);
        AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
        communityFunService.updateOne(communityFunOperationQO,adminInfoVo);
        return CommonResult.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete")
    @businessLog(operation = "删除",content = "删除了【物业社区趣事】")
    @Permit("community:property:communityfun:delete")
    public CommonResult delete(@ApiParam("社区趣事id")
                               @RequestParam("id") Long id) {
        communityFunService.deleteById(id);
        return CommonResult.ok();
    }

    @ApiOperation("查询一条---表单回填")
    @GetMapping("/findOne")
    @Permit("community:property:communityfun:findOne")
    public CommonResult findOne(@ApiParam("社区趣事id")
                                    @RequestParam("id") Long id) {
        CommunityFunEntity communityFunEntity = communityFunService.selectOne(id);
        return  CommonResult.ok(communityFunEntity);
    }

    @ApiOperation("上线")
    @GetMapping("/popUpOnline")
    @Permit("community:property:communityfun:popUpOnline")
    public CommonResult popUpOnline(@ApiParam("社区趣事id")
                                        @RequestParam("id") Long id) {
        AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
        communityFunService.popUpOnline(id,adminInfoVo);
        return  CommonResult.ok();
    }

    @ApiOperation("下线")
    @GetMapping("/tapeOut")
    @Permit("community:property:communityfun:tapeOut")
    public CommonResult tapeOut(@ApiParam("社区趣事id")
                                @RequestParam Long id) {
        communityFunService.tapeOut(id);
        return  CommonResult.ok();
    }


}
