package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
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
    private String version;
    private String picName;
    private String paymentType;
    private String pictureUrl;
    private String paymentWay;
    private String CebPaymentItemsList;
    private String cornerMarker;
    private String howView;
}
