package com.jsy.community.api;

import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-10 14:35
 **/
public interface ITenementService {
    Map list(BaseQO<PropertyRelationQO> baseQO);

    List getHouseId(BaseQO<RelationListQO> baseQO);

    List getBuildingId(BaseQO<RelationListQO> baseQO);

    List getUnitId(BaseQO<RelationListQO> baseQO);
}
