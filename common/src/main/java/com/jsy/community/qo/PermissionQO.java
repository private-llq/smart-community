package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 权限
 * @author: DKS
 * @since: 2021/12/6 14:37
 */
@Data
public class PermissionQO implements Serializable {
    private List<Long> permitIds;
    private Long roleId;
    private Long uid;
    
    private String account;
}
