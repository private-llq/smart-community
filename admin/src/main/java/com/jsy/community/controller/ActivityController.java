package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.IActivityService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @program: com.jsy.community
 * @description: 活动管理
 * @author: DKS
 * @create: 2021-11-3 10:00
 **/
@RestController
@RequestMapping("/activity")
@ApiJSYController
@Login
public class ActivityController {

    @Resource
    private IActivityService propertyActivityService;
    
    /**
     * @Description: 活动管理分页查询
     * @author: DKS
     * @since: 2021/11/3 11:00
     * @Param: [baseQO]
     * @return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.ActivityEntity>>
     */
    @ApiOperation("查询列表")
    @PostMapping("/list")
    public CommonResult<PageInfo<ActivityEntity>> list(@RequestBody BaseQO<ActivityEntity> baseQO){
        return CommonResult.ok(propertyActivityService.list(baseQO));
    }
    
    /**
     * @Description: 新增活动
     * @author: DKS
     * @since: 2021/11/3 14:22
     * @Param: [activityEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    public CommonResult save(@RequestBody ActivityEntity activityEntity){
        return CommonResult.ok(propertyActivityService.saveBy(activityEntity) ? "新增成功" : "新增失败");
    }
    
    /**
     * @Description: 活动管理查询详情
     * @author: DKS
     * @since: 2021/11/3 14:21
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("活动管理查询详情")
    @GetMapping("/getOne")
    public CommonResult getOne(@RequestParam Long id){
        return CommonResult.ok(propertyActivityService.getOne(id));
    }
    
    /**
     * @Description: 修改活动
     * @author: DKS
     * @since: 2021/11/3 14:26
     * @Param: [activityEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("编辑")
    @PutMapping("/update")
    public CommonResult update(@RequestBody ActivityEntity activityEntity){
        return CommonResult.ok(propertyActivityService.update(activityEntity) ? "编辑成功" : "编辑失败");
    }
    
    /**
     * @Description: 报名详情分页查询
     * @author: DKS
     * @since: 2021/11/3 14:33
     * @Param: [baseQO]
     * @return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.ActivityUserEntity>>
     */
    @ApiOperation("报名详情分页查询")
    @PostMapping("/detail/page")
    public CommonResult<PageInfo<ActivityUserEntity>> detailPage(@RequestBody BaseQO<ActivityUserEntity> baseQO){
        return CommonResult.ok(propertyActivityService.detailPage(baseQO));
    }
    
    /**
     * @Description: 上传轮播图
     * @author: DKS
     * @since: 2021/11/3 14:35
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传图片")
    @PostMapping("/file")
    public CommonResult file(@RequestParam MultipartFile[] file){
        String[] votes = MinioUtils.uploadForBatch(file, "sys-activity");
        return CommonResult.ok(votes);
    }
}
