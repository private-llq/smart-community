package com.jsy.community.qo;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CarMonthlyDelayQO implements Serializable {

   private Long communityId;
   private String uid;
   private Integer type;
   private Integer dayNum;
   private BigDecimal fee;
}
