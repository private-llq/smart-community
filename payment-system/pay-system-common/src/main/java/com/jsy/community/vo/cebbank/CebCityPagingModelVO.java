package com.jsy.community.vo.cebbank;

import com.jsy.community.vo.cebbank.CebCityModelListVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/23 16:41
 * @Version: 1.0
 **/
@Data
public class CebCityPagingModelVO implements Serializable {
    private CebCityModelListVO cityPagingModel;
    private String Message;
    private String code;
}
