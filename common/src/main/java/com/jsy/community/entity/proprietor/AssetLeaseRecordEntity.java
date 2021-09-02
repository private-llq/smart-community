package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @Author: Pipi
 * @Description: 房屋租赁记录表实体
 * @Date: 2021/8/31 10:03
 * @Version: 1.0
 **/
@Data
@TableName("t_asset_lease_record")
public class AssetLeaseRecordEntity extends BaseEntity {
    // 资产ID
    @NotNull(groups = InitContractValidate.class, message = "资产ID不能为空")
    private Long assetId;

    // 资产类型;1:房屋;2:商铺
    @NotNull(groups = InitContractValidate.class, message = "资产类型不能为空;1:房屋;2:商铺")
    @Range(min = 1, max = 2, message = "资产类型值超出范围;1:房屋;2:商铺")
    private Integer assetType;

    // 业主uid
    private String homeOwnerUid;

    // 租客uid
    private String tenantUid;

    // 图片id
    private String imageId;

    // 标题
    private String title;

    // 优势标签
    private Long advantageId;

    // 房型code：四室一厅、二室一厅...别墅000000 如040202代表着4室2厅2卫
    private Integer typeCode;

    // 房屋朝向、不常改，对应的数值，1.东.2.西 3.南 4.北. 5.东南 6. 东北 7.西北 8.西南
    private Integer directionId;

    // 社区id
    private Long communityId;

    // 房屋价格/元
    private Double price;

    /**
     * 发起签约验证组
     */
    public interface InitContractValidate{}

}
