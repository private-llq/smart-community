package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 访客人脸同步记录表实体
 * @Date: 2021/8/24 10:22
 * @Version: 1.0
 **/
@Data
@TableName("t_visitor_face_sync_record")
public class VisitorFaceSyncRecordEntity extends BaseEntity {
    // 访客ID
    private Long visitorId;
    // 社区ID
    private Long communityId;
    // 人脸地址
    private String faceUrl;
    // 设备序列号
    private String facilityId;
}
