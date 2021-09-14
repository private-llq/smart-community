package com.jsy.community.qo.property;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ContentRowHeight(53)
public class CarTemporaryOrderQO implements Serializable {
    /**
     * 车位号
     */
    @ColumnWidth(9)
    @ExcelProperty("车位号")
    private String carPosition;
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
     *  1线上支付，2线下支付
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
     *  0未支付，1已支付
     */
    @ColumnWidth(9)
    @ExcelProperty("支付状态(0未支付，1已支付)")
    private Integer orderStatus;

}
