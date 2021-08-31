package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 房屋租赁记录表实体
 * @Date: 2021/8/31 10:03
 * @Version: 1.0
 **/
@Data
@TableName("t_house_lease_record")
public class HouseLeaseRecordEntity extends BaseEntity {
    // 房屋ID
    private Long assetsId;
    // 业主uid
    private Long homeOwnerUid;
    // 租客uid
    private Long tenantUid;
    // 房屋图片id
    private String houseImageId;
    // 房屋标题
    private String houseTitle;
    // 房屋优势标签
    private Long houseAdvantageId;
    // 房型code：四室一厅、二室一厅...别墅000000 如040202代表着4室2厅2卫
    private String houseTypeCode;
    // 房屋朝向、不常改，对应的数值，1.东.2.西 3.南 4.北. 5.东南 6. 东北 7.西北 8.西南
    private Integer houseDirectionId;
    // 社区id
    private Long houseCommunityId;
    // 房屋价格/元
    private Double housePrice;
}
