package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: Pipi
 * @Description: 物业人员人脸表实体
 * @Date: 2021/9/24 11:22
 * @Version: 1.0
 **/
@Data
@TableName("t_property_face")
public class PropertyFaceEntity extends BaseEntity {
    // 社区id
    private Long communityId;

    // 姓名
    @NotBlank(groups = {AddFaceValidate.class}, message = "姓名不能为空")
    private String realName;

    // 电话
    @NotBlank(groups = {AddFaceValidate.class}, message = "电话不能为空")
    private String mobile;

    // 人脸地址
    @NotBlank(groups = {AddFaceValidate.class}, message = "人脸地址不能为空")
    private String faceUrl;

    // 人脸启用状态;1:启用;2:禁用
    @NotNull(groups = {AddFaceValidate.class}, message = "人脸启用状态不能为空;1:启用;2:禁用")
    private Integer faceEnableStatus;

    // 人脸删除状态;0:未删除;1:已删除
    private Integer faceDeleted;

    /**
     * 物业人脸新增验证组
     */
    public interface AddFaceValidate{}

}
