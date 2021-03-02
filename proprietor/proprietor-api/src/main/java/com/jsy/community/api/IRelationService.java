package com.jsy.community.api;

import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserHouseEntity;
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
    void addRelation(RelationQo relationQo);


    /**
     * @Description: 通过业主id查询家属信息
     * @author: Hu
     * @since: 2020/12/10 16:36
     * @Param:
     * @return:
     */
    List<HouseMemberEntity> selectID(String id,Long houseId);
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
    /**
     * 修改一个家属信息和汽车信息
     * @param relationQo
     * @return
     */
    void updateUserRelationDetails(RelationQo relationQo);
    
    /**
    * @Description: 判断是否是指定小区家属
     * @Param: [mobile, communityId]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/12/23
    **/
    boolean isHouseMember(String mobile,Long communityId);

   /**
    * @Description: 删除家属信息
    * @author: Hu
    * @since: 2020/12/25 14:46
    * @Param:
    * @return:
    */
    void deleteHouseMemberCars(Long id);

    /**
     * @Description: 房间验证
     * @author: Hu
     * @since: 2020/12/25 14:46
     * @Param:
     * @return:
     */
    UserHouseEntity getHouse(RelationQo relationQo);
}
