package com.jsy.community.api;

import com.jsy.community.qo.BaseQO;
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
    Map list(BaseQO<PropertyRelationQO> baseQO);

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
}
