package com.jsy.community.qo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CarChargeQO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收费设置id
     */
    private String uuid;

    /**
     * 社区id
     */
    private String communityId;

    /**
     * 入场时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //Json才需要时区
    private LocalDateTime inTime;
    /**
     * 出场时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //Json才需要时区
    private LocalDateTime reTime;

    /**
     * 车牌颜色
     */
    private String carColor;
}
