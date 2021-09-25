package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 物业人脸同步记录表实体
 * @Date: 2021/9/24 11:30
 * @Version: 1.0
 **/
@Data
@TableName("t_property_face_sync_record")
public class PropertyFaceSyncRecordEntity extends BaseEntity {
    // t_porperty_face的id
    private Long porpertyFaceId;

    // 社区ID
    private Long communityId;

    // 人脸地址
    private String faceUrl;

    // 设备序列号
    private String facilityId;

}
