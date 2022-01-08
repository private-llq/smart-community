package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 缴费类别VO
 * @Date: 2021/11/12 17:40
 * @Version: 1.0
 **/
@Data
public class CebCategoryVO implements Serializable {
    private String type;
    private String typeName;
    private String citycode;
    private String cityName;
    private String sort;
    private String picUrlClient;
    private String paymentType;
}
