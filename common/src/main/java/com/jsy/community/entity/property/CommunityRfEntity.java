package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 门禁卡表实体
 * @Date: 2021/11/3 14:57
 * @Version: 1.0
 **/
@Data
@TableName("t_community_rf")
public class CommunityRfEntity extends BaseEntity {

    // 门禁卡号,正常应该是10位
    private  String rfNum;

    // 社区ID
    private  Long communityId;

    // 房屋ID
    private  Long houseId;

    // 姓名
    private  String name;

    // 电话
    private  String mobile;

    // 启动状态;0:禁用;1:启用
    private  Integer enableStatus;

    // 同步(下发)状态;0:未同步;1:已同步
    private  Integer sycStatus;
}
