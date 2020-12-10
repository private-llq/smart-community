package com.jsy.community.api;

import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.RelationQo;

import java.util.List;

/**
 * 家属信息
 */
public interface IRelationService {
    /**
     * @Description: 添加家属
     * @author: Hu
     * @since: 2020/12/10 16:36
     * @Param:
     * @return:
     */
    Boolean addRelation(RelationQo relationQo);


    /**
     * @Description: 通过业主id查询家属信息
     * @author: Hu
     * @since: 2020/12/10 16:36
     * @Param:
     * @return:
     */
    List<HouseMemberEntity> selectID(String id);
}
