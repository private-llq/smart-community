package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 用户人脸同步记录表实体
 * @Date: 2021/8/19 17:29
 * @Version: 1.0
 **/
@Data
@TableName("t_user_face_sync_record")
public class UserFaceSyncRecordEntity extends BaseEntity {
    // t_user用户uid
    private String uid;
    // 社区ID
    private Long communityId;
    // 人脸地址
    private String faceUrl;
    // 设备序列号
    private String facilityId;
}
