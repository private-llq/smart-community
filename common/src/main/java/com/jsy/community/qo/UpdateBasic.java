package com.jsy.community.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lxjr
 * @date 2021/10/19 10:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBasic {
    private String imId;
    private String nickName;
    private String headImgSmallUrl;
    private String headImgMaxUrl;
}
