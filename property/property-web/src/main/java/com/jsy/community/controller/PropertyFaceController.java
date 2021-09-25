package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.PropertyFaceService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFaceEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Pipi
 * @Description: 物业人脸控制器
 * @Date: 2021/9/24 11:43
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/propertyFace")
@ApiJSYController
public class PropertyFaceController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private PropertyFaceService propertyFaceService;

    /**
     * @author: Pipi
     * @description: 物业人脸操作(启用/禁用人脸)
     * @param propertyFaceEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/24 11:59
     **/
    @Login
    @PostMapping("/v2/faceOpration")
    public CommonResult faceOpration(@RequestBody PropertyFaceEntity propertyFaceEntity) {
        if (propertyFaceEntity.getId() == null) {
            throw new JSYException(400, "物业人员ID不能为空");
        }
        if (propertyFaceEntity.getFaceEnableStatus() == null) {
            throw new JSYException(400, "人脸启用状态不能为空;1:启用;2:禁用");
        }
        Integer integer = propertyFaceService.faceOpration(propertyFaceEntity, UserUtils.getAdminCommunityId());
        return integer > 0 ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
    }

    /**
     * @author: Pipi
     * @description: 删除物业人脸
     * @param propertyFaceEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/24 11:59
     **/
    @Login
    @PostMapping("/v2/deleteFace")
    public CommonResult deleteFace(@RequestBody PropertyFaceEntity propertyFaceEntity) {
        if (propertyFaceEntity.getId() == null) {
            throw new JSYException(400, "物业人员ID不能为空");
        }
        Integer integer = propertyFaceService.deleteFace(propertyFaceEntity, UserUtils.getAdminCommunityId());
        return integer > 0 ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
    }

    /**
     * @author: Pipi
     * @description: 上传物业人脸
     * @param file:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/24 12:01
     **/
    @Login
    @PostMapping("/v2/uploadPropertyFace")
    public CommonResult uploadFace(MultipartFile file) {
        String upload = MinioUtils.upload(file, "property-face-url");
        return CommonResult.ok(upload);
    }

    /**
     * @author: Pipi
     * @description: 新增物业人脸
     * @param propertyFaceEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/24 12:00
     **/
    @Login
    @PostMapping("/v2/addFace")
    public CommonResult addFace(@RequestBody PropertyFaceEntity propertyFaceEntity) {
        ValidatorUtils.validateEntity(propertyFaceEntity, PropertyFaceEntity.AddFaceValidate.class);
        Integer integer = propertyFaceService.addFace(propertyFaceEntity, UserUtils.getAdminCommunityId());
        return integer > 0 ? CommonResult.ok("新增成功") : CommonResult.error("新增失败");
    }
}
