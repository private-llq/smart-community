package com.jsy.community.api;

import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-05 11:20
 **/
public interface IPropertyRelationService {
    List list(BaseQO<PropertyRelationQO> baseQO);

}
