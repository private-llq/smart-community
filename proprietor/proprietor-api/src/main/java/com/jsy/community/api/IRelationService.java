package com.jsy.community.api;

import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.RelationQo;

import java.util.List;

/**
 * 家属信息
 */
public interface IRelationService {
    Boolean addRelation(RelationQo relationQo);


    List<UserEntity> selectID(Long id);
}
