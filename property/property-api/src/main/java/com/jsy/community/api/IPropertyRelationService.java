package com.jsy.community.api;

import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.admin.AdminInfoVo;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业成员查询接口
 * @author: Hu
 * @create: 2021-03-05 11:20
 **/
public interface IPropertyRelationService {
    /**
     * @Description: 成员列表
     * @author: Hu
     * @since: 2021/3/9 16:25
     * @Param:
     * @return:
     */
    Map list(BaseQO<PropertyRelationQO> baseQO,Long communityId);

    /**
     * @Description: 房屋下拉框
     * @author: Hu
     * @since: 2021/3/9 16:25
     * @Param:
     * @return:
     */
    List getHouseId(BaseQO<RelationListQO> baseQO, AdminInfoVo adminInfoVo);
    /**
     * @Description: 楼栋下拉框
     * @author: Hu
     * @since: 2021/3/9 16:25
     * @Param:
     * @return:
     */
    List getBuildingId(BaseQO<RelationListQO> baseQO,AdminInfoVo adminInfoVo);
    /**
     * @Description: 单元下拉框
     * @author: Hu
     * @since: 2021/3/9 16:25
     * @Param:
     * @return:
     */
    List getUnitId(BaseQO<RelationListQO> baseQO,AdminInfoVo adminInfoVo);

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/7/21 16:35
     * @Param:
     * @return:
     */
    void save(HouseMemberEntity houseMemberEntity);

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/7/21 16:35
     * @Param:
     * @return:
     */
    void update(HouseMemberEntity houseMemberEntity);

    /**
     * @Description: 单查
     * @author: Hu
     * @since: 2021/7/21 16:35
     * @Param:
     * @return:
     */
    HouseMemberEntity findOne(Long id);

    /**
     * @Description: 迁入
     * @author: Hu
     * @since: 2021/7/23 17:26
     * @Param:
     * @return:
     */
    void immigration(Long id);

    /**
     * @Description: 迁出
     * @author: Hu
     * @since: 2021/7/23 17:26
     * @Param:
     * @return:
     */
    void emigration(Long id);

    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/7/23 17:54
     * @Param:
     * @return:
     */
    void pageList(BaseQO<HouseMemberQO> baseQO);
}
