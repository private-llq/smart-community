package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: app权限返回数据
 * @author: Hu
 * @create: 2021-08-16 15:36
 **/
@Data
public class ControlVO implements Serializable {
    private Integer accessLevel;
    private Long communityId;
    private Long houseId;
    private List<ControlVO> permissions= new LinkedList<>();
}
