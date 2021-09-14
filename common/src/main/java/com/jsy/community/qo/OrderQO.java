package com.jsy.community.qo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderQO implements Serializable {
   private Integer type;
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
   private LocalDateTime startTime;
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
   private LocalDateTime endTime;
   private Long communityId;
}
