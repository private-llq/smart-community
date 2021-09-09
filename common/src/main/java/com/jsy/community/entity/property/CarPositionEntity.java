package com.jsy.community.entity.property;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * <p>
 * 车位
 * </p>
 *
 * @author Arli
 * @since 2021-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_car_position")
//@ContentRowHeight(value = 8)
//@ContentFontStyle(fontHeightInPoints=4)
public class CarPositionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 所属小区
     */
    @ExcelIgnore
    private Long communityId;

    /**
     * 所属业主
     */
    @ExcelIgnore
    private String uid;

    /**
     * 关联车位类型id
     */
    @ExcelIgnore
    private Long typeId;

    /**
     * 关联车位类型
     */
    @ExcelProperty("关联车位类型")
    @TableField(exist = false)//数据库没有
    private String typeCarPosition;
    /**
     * 车位号
     */
    @ExcelProperty("车位号")
    private String carPosition;

    /**
     * 逻辑删除
     */
    @ExcelIgnore
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @ExcelIgnore
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 车位状态（0空置，1业主自用，2租赁）
     */
//    @ExcelProperty("车位状态(0空置,1业主自用,2租赁)")
//    @ColumnWidth(value = 43)
    @ExcelIgnore
    private Integer carPosStatus;

    /**
     * 产权面积
     */
    @ExcelProperty("产权面积(m²)")
    private Double area;

    /**
     * 所属房屋
     */
//    @ExcelProperty("所属房屋")
//    @ColumnWidth(value = 23)
    @ExcelIgnore
    private String belongHouse;

    /**
     * 业主电话
     */
//    @ExcelProperty("业主电话")
//    @ColumnWidth(value = 23)
    @ExcelIgnore
    private String ownerPhone;

//    @ExcelProperty("用户姓名")
    @ExcelIgnore
    private String userName;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;

    /**
     * 绑定状态(0未绑定1,绑定)
     */
//    @ExcelProperty("绑定状态(0未绑定,1绑定)")
//    @ColumnWidth(value = 23)
    @ExcelIgnore
//    @ExplicitConstraint(source = {"aaa1", "aaa2", "aaa3"})
//    @CellStyle(fontStyle = @FontStyle(color = IndexedColors.LIGHT_BLUE, size = 14))

    private Integer bindingStatus;

    /**
     * 起始时间
     */
//    @ExcelProperty("起始时间")
    @ExcelIgnore
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime beginTime;

    /**
     * 到期时间
     */
//    @ExcelProperty("到期时间")
    @ExcelIgnore
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endTime;
    /**
     * 售价
     */
    @ExcelProperty("售价")
    private BigDecimal price;
    /**
     * 月租
     */
    @ExcelProperty("月租")
    private  BigDecimal     monthlyPrice;
    /**
     * 房屋id
     */
    @ExcelIgnore
    private  Long     houseId;

}
