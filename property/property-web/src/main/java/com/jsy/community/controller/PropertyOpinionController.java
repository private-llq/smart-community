package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyOpinionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PropertyOpinionEntity;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PicUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: com.jsy.community
 * @description:  物业意见反馈
 * @author: Hu
 * @create: 2021-04-11 11:10
 **/
@Api(tags = "物业意见反馈")
@RestController
@RequestMapping("/propertyOpinion")
@ApiJSYController
public class PropertyOpinionController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyOpinionService propertyOpinionService;

    @ApiOperation("创建意见反馈")
    @PostMapping("/create")
    @businessLog(operation = "新增",content = "新增了【意见反馈】")
    @Permit("community:property:propertyOpinion:create")
    public CommonResult create(@RequestBody PropertyOpinionEntity propertyOpinionEntity){
        ValidatorUtils.validateEntity(propertyOpinionEntity,PropertyOpinionEntity.PropertyOpinionValidated.class);
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        Integer count = propertyOpinionService.selectCount(userInfo);
        if (count==3){
            return CommonResult.error("超过当天最大意见反馈！");
        }
        propertyOpinionService.insetOne(propertyOpinionEntity,userInfo);
        return CommonResult.ok();
    }
    @ApiOperation("上传图片")
    @PostMapping("/upload")
    @Permit("community:property:propertyOpinion:upload")
    public CommonResult upload(@RequestParam("files")MultipartFile[] files){
        if (files.length>5){
            return CommonResult.error("图片不能超过5张！");
        }
        StringBuilder str=new StringBuilder();
        if (!PicUtil.checkSizeAndTypeBatch(files,5*1024)){
            return CommonResult.ok("图片格式有误！");
        }
        for (int i=0;i<files.length;i++) {
            String upload = MinioUtils.upload(files[i], "opinionimg");
            str.append(upload);
            if (i!=files.length-1){
                str.append(",");
            }
        }
        return CommonResult.ok(str.toString().split(","));
    }
}
