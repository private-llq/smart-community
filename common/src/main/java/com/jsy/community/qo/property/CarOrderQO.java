package com.jsy.community.qo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.qo.BaseQO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarOrderQO extends BaseQO {

    /**
     *  周期开始时间
     */
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //Json才需要时区
    private LocalDateTime beginTime;
    /**
     *  周期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //Json才需要时区
    private LocalDateTime overTime;

    /**
     *  车牌号
     */
    private String carPlate;

    /**
     *  0未支付，1已支付
     */
    private Integer orderStatus;

    /**
     *  1临时收费，2月租收费
     */
    private Integer type;

    /**
     * @Description: 导出字段  月租0  1临时
     * @Param:
     * @Return:
     * @Author: Tian
     * @Date: 2021/9/9-15:46
     **/
    private Integer state;

}
