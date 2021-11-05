package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 门禁卡同步记录实体
 * @Date: 2021/11/3 15:41
 * @Version: 1.0
 **/
@Data
@TableName("t_community_rf_syc_record")
public class CommunityRfSycRecordEntity extends BaseEntity {

    // 门禁卡号
    private String rfNum;

    // 设备唯一序列号
    private String hardwareId;

    // 社区ID
    private Long communityId;
}
