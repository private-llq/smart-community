package com.jsy.community.qo.property;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ContentRowHeight(53)
public class CarTemporaryQO implements Serializable {
    /**
     *  商户单号
     */
    @ColumnWidth(35)
    @ExcelProperty("商户单号")
    private String orderNum;
    /**
     *  车牌号
     */
    @ColumnWidth(9)
    @ExcelProperty("车牌号")
    private String carPlate;
    /**
     *  支付金额
     */
    @ColumnWidth(9)
    @ExcelProperty("支付金额")
    private BigDecimal money;

    /**
     *  周期开始时间
     */
    @ColumnWidth(20)
    @ExcelProperty("周期开始时间")
    private Date beginTime;
    /**
     *  周期结束时间
     */
    @ColumnWidth(20)
    @ExcelProperty("周期结束时间")
    private Date overTime;
    /**
     *  支付时间
     */
    @ColumnWidth(20)
    @ExcelProperty("支付时间")
    private Date orderTime;

    /**
     *  支付方式 1线上支付，2线下支付
     */
    @ColumnWidth(9)
    @ExcelProperty("支付方式(1线上支付，2线下支付)")
    private Integer payType;
    /**
     *  账单抬头
     */
    @ColumnWidth(15)
    @ExcelProperty("账单抬头")
    private String rise;
    /**
     *  支付状态 0未支付，1已支付
     */
    @ColumnWidth(9)
    @ExcelProperty("支付状态(0未支付，1已支付)")
    private Integer orderStatus;

    /**
     * 停车时长
     */
    @ColumnWidth(20)
    @ExcelProperty("停车时长")
    private String stopCarTime;

}
