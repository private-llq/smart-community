package com.jsy.community.api;

import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;

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
    Map list(BaseQO<PropertyRelationQO> baseQO,Long communityId);
}
