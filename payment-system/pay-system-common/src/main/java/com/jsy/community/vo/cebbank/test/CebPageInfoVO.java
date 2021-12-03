package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/23 19:48
 * @Version: 1.0
 **/
@Data
public class CebPageInfoVO implements Serializable {
     private Integer pageSize;
     private Integer totalReco;
     private Integer totalPage;
     private Integer currentPage;
     private Integer currentRec;
     private Integer nextRec;
     private Integer nextPage;
     private Integer prePage;
     private Integer stRec;
     private Integer cond;
}
