package com.jsy.community.vo;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SelectMoney3Vo implements Serializable {
    /**
     * 某天
     */
    private String day;
    /**
     * 1:临时 2：包月
     */
    private Integer type;

    /**
     * 临时金额
     */
    private BigDecimal money1;

    /**
     * 包月金额
     */
    private BigDecimal money2;


}
