package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 角色
 * @author: DKS
 * @since: 2021/12/6 14:07
 */
@Data
public class RoleQO implements Serializable {
    private String name;
    private String remark;
    private Long createUid;
    
    private List<Long> roleIds;
    private Long uid;
    private Long updateUid;
}
