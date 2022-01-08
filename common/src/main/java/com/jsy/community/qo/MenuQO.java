package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 菜单
 * @author: DKS
 * @since: 2021/12/6 14:23
 */
@Data
public class MenuQO implements Serializable {
    private List<Long> menuIds;
    private Long roleId;
    private Long uid;
}
