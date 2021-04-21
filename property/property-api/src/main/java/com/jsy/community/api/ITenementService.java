package com.jsy.community.api;

import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业租户查询
 * @author: Hu
 * @create: 2021-03-10 14:35
 **/
public interface ITenementService {
    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/4/21 17:03
     * @Param:
     * @return:
     */
    Map list(BaseQO<PropertyRelationQO> baseQO);

    /**
     * @Description: 房屋
     * @author: Hu
     * @since: 2021/4/21 17:03
     * @Param:
     * @return:
     */
    List getHouseId(BaseQO<RelationListQO> baseQO);

    /**
     * @Description: 楼栋
     * @author: Hu
     * @since: 2021/4/21 17:03
     * @Param:
     * @return:
     */
    List getBuildingId(BaseQO<RelationListQO> baseQO);

    /**
     * @Description: 单元
     * @author: Hu
     * @since: 2021/4/21 17:03
     * @Param:
     * @return:
     */
    List getUnitId(BaseQO<RelationListQO> baseQO);
}
