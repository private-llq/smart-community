package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

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

    // 资产类型;1:商铺;2:房屋
    @NotNull(groups = {InitContractValidate.class, ContractListValidate.class}, message = "资产类型不能为空;1:商铺;2:房屋")
    @Range(min = 1, max = 2, message = "资产类型值超出范围;1:商铺;2:房屋")
    private Integer assetType;

    // 业主uid
    private String homeOwnerUid;

    // 租客uid
    private String tenantUid;

    // 签约操作状态;1:发起签约;2:接受申请;3:拟定合同;4:等待支付房租;5:支付完成;6:完成签约;7:取消申请;8:拒绝申请;9:重新发起
    private Integer operation;

    // 图片id
    private String imageUrl;

    // 标题
    private String title;

    // (商铺)概述
    private String summarize;

    // 优势标签
    private Long advantageId;

    // 房型code：四室一厅、二室一厅...别墅000000 如040202代表着4室2厅2卫
    private String typeCode;

    // 房屋朝向、不常改，对应的数值，1.东.2.西 3.南 4.北. 5.东南 6. 东北 7.西北 8.西南
    private String directionId;

    // 社区id
    private Long communityId;

    // 房屋价格/元
    private BigDecimal price;

    // 合同编号
    private String contractNo;

    // 合同开始时间
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime contractStartTime;

    // 合同结束时间
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime contractEndTime;

    // 身份类型;1:房东;2:租客
    @NotNull(groups = {ContractListValidate.class, OperationContractValidate.class}, message = "身份类型不能为空;1:房东;2:租客")
    @Range(min = 1, max = 2, message = "身份类型超出范围;1:房东;2:租客")
    @TableField(exist = false)
    private Integer identityType;

    // 房屋租售优势标签
    @TableField(exist = false)
    private Map<String, Long> houseAdvantageCode;

    // 房屋户型文本：1.四室一厅、2.二室一厅...
    @TableField(exist = false)
    private String houseType;

    // 操作类型;7:租客取消申请;8房东拒绝申请;9:租客再次申请;2房东接受申请
    @TableField(exist = false)
    @NotNull(groups = {OperationContractValidate.class}, message = "操作类型不能为空;2房东接受申请;7:租客取消申请;8房东拒绝申请;9:租客再次申请;")
    private Integer operationType;

    // 房东该资产签约条数
    @TableField(exist = false)
    private Integer contractNumber;

    /**
     * 发起签约验证组
     */
    public interface InitContractValidate{}

    /**
     * 签约列表验证组
     */
    public interface ContractListValidate{}

    /**
     * 停止签约(取消/拒绝)
     */
    public interface OperationContractValidate {}

}