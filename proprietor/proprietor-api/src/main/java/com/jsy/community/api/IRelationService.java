package com.jsy.community.api;

import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.RelationQo;
import com.jsy.community.vo.RelationVO;

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
    /**
     * 查询业主下面的家属详情
     * @param RelationId
     * @return
     */
    RelationVO selectOne(Long RelationId, String userId);

    /**
     * 修改家属信息
     * @param houseMemberEntity
     * @return
     */
    void updateByRelationId(HouseMemberEntity houseMemberEntity);


    /**
     * 查询一条表单回填
     * @param relationId
     * @return
     */
    HouseMemberEntity updateFormBackFillId(Long relationId);
}
