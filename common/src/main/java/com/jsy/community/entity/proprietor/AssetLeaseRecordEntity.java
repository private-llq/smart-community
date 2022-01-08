package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@EqualsAndHashCode(callSuper = false)
public class AssetLeaseRecordEntity extends BaseEntity {
    // 资产ID
    @NotNull(groups = {InitContractValidate.class, LandlordContractListValidate.class, LandlordInitiatedContractValidate.class}, message = "资产ID不能为空")
    private Long assetId;

    // 资产类型;1:商铺;2:房屋
    @NotNull(groups = {InitContractValidate.class, LandlordContractListValidate.class, LandlordInitiatedContractValidate.class}, message = "资产类型不能为空;1:商铺;2:房屋")
    @Range(min = 1, max = 2, message = "资产类型值超出范围;1:商铺;2:房屋")
    private Integer assetType;

    // 业主uid
    @NotBlank(groups = {LandlordInitiatedContractValidate.class, CancelContractValidate.class, CompleteContractValidate.class}, message = "房东uid不能为空")
    private String homeOwnerUid;

    // 租客uid
    @NotBlank(groups = {CompleteContractValidate.class}, message = "房东uid不能为空")
    private String tenantUid;

    // 签约操作状态;1:(租客)发起租赁申请;2:接受申请;3:拟定合同;4:等待支付房租;5:支付完成;6:完成签约;7:取消申请;8:拒绝申请;9:重新发起;31:(房东)发起签约/重新发起;32:取消发起;
    private Integer operation;

    // 图片路径
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

    // 省份id(房屋)
    private  Long provinceId;

    // 市id(共有)
    private  Long cityId;

    // 区域id(共有)
    private  Long areaId;

    // 详细地址(房屋)
    private  String address;

    // 楼层(共有)
    private  String floor;

    // 合同编号
    @NotBlank(groups = {LandlordInitiatedContractValidate.class, CompleteContractValidate.class, CancelContractValidate.class, BlockchainSuccessfulValidate.class}, message = "合同编号不能为空")
    private String conId;

    // 合同名字
    private String conName;

    // 上链状态;1：签署中的合同;2：签署完成的合同，并未上链完成的合同;4：区块链信息上链完毕
    private Integer blockStatus;

    // 发起方(甲方)
    private String initiator;

    // 签约方(乙方)
    private String signatory;

    // 合同开始时间
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(groups = {LandlordInitiatedContractValidate.class}, message = "合同开始时间不能为空")
    private LocalDate startDate;

    // 合同结束时间
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(groups = {LandlordInitiatedContractValidate.class}, message = "合同结束时间不能为空")
    private LocalDate endDate;

    // 租客已读标识;0:未读;1:已读
    private Integer readMark;

    // 身份类型;1:房东;2:租客
    @NotNull(groups = {ContractListValidate.class, OperationContractValidate.class, ContractDetailValidate.class}, message = "身份类型不能为空;1:房东;2:租客")
    @Range(min = 1, max = 2, message = "身份类型超出范围;1:房东;2:租客")
    @TableField(exist = false)
    private Integer identityType;

    // 房屋租售优势标签
    @TableField(exist = false)
    private Map<String, Long> houseAdvantageCode;

    // 房屋户型文本：1.四室一厅、2.二室一厅...
    @TableField(exist = false)
    private String houseType;

    // 操作类型;7:租客取消申请;8房东拒绝申请;9:租客再次申请;2房东接受申请;3:房东点击拟定合同;6:完成签约;
    @TableField(exist = false)
    @NotNull(groups = {OperationContractValidate.class, LandlordInitiatedContractValidate.class}, message = "操作类型;7:租客取消申请;8房东拒绝申请;9:租客再次申请;2房东接受申请;3:房东点击拟定合同;6:完成签约;")
    private Integer operationType;

    // 房东该资产签约条数
    @TableField(exist = false)
    private Integer contractNumber;

    // 签约状态;1:未签约;2:签约中;3已签约;4已过期
    @TableField(exist = false)
    @NotNull(groups = {LandlordContractListValidate.class}, message = "签约状态不能为空;1:未签约;2:签约中;3已签约")
    @Range(min = 1, max = 3, message = "签约状态传值超出范围;1:未签约;2:签约中;3已签约")
    private Integer contractStatus;

    // 租客姓名
    @TableField(exist = false)
    private String realName;

    // 租客头像
    @TableField(exist = false)
    private String avatarUrl;

    // 租客电话
    @TableField(exist = false)
    private String tenantPhone;

    // 租客身份证号码
    @TableField(exist = false)
    private String tenantIdCard;

    // 房东姓名
    @TableField(exist = false)
    private String landlordName;

    // 房东电话
    @TableField(exist = false)
    private String landlordPhone;

    // 房屋完整地址
    @TableField(exist = false)
    private String fullAddress;

    // 进度数;1:(租客)发起租赁申请;2:签约合同;3:租客支付房租;4:完成签约
    @TableField(exist = false)
    private Integer progressNumber;

    // 倒计时终点
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(exist = false)
    private LocalDateTime countdownFinish;
    
    @ApiModelProperty(value = "合同类型名称")
    @TableField(exist = false)
    private String contractTypeName;
    
    @ApiModelProperty(value = "发起方电话")
    @TableField(exist = false)
    private String initiatorMobile;
    
    @ApiModelProperty(value = "签约方电话")
    @TableField(exist = false)
    private String signatoryMobile;
    
    @ApiModelProperty(value = "合同签约状态名称:1:未签约;2:签约中;3已签约;4已过期")
    @TableField(exist = false)
    private String contractStatusName;
    
    /**
     * 社区名称
     */
    @TableField(exist = false)
    private String communityName;
    
    /**
     * 状态编号
     */
    @TableField(exist = false)
    private String LeaseStatusName;
    
    /**
     * 房源类型名称
     */
    @TableField(exist = false)
    private String typeName;

    /**
     * 发起签约验证组
     */
    public interface InitContractValidate{}

    /**
     * 签约列表验证组
     */
    public interface ContractListValidate{}

    /**
     * 停止签约(取消/拒绝)验证组
     */
    public interface OperationContractValidate {}

    /**
     * 房东资产签约列表验证组
     */
    public interface LandlordContractListValidate {}

    /**
     * 签约详情验证组
     */
    public interface ContractDetailValidate {}

    /**
     * 房东发起签约/重新发起签约验证组
     */
    public interface LandlordInitiatedContractValidate {}

    /**
     * 完成签约验证组
     */
    public interface CompleteContractValidate {}

    /**
     * 房东取消发起签约验证组
     */
    public interface CancelContractValidate {}

    /**
     * 区块链上链成功通知验证组
     */
    public interface BlockchainSuccessfulValidate {}
}