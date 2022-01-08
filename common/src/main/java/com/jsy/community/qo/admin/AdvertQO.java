package com.jsy.community.qo.admin;

import com.jsy.community.qo.BaseQO;
import lombok.Data;

/**
 * @author xrq
 * @version 1.0
 * @Description:
 * @date 2021/12/27 9:30
 */
@Data
public class AdvertQO extends BaseQO {
    private String advertId;
    private Integer displayPosition;
}
