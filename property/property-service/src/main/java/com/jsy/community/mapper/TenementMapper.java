package com.jsy.community.mapper;

import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.HouseTypeVo;
import com.jsy.community.vo.PropertyTenementVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-10 14:35
 **/
@Mapper
public interface TenementMapper {
    List<HouseTypeVo> getHouseId(RelationListQO query, Long page, Long size);

    List getBuildingId(RelationListQO query, Long page, Long size);

    List getUnitId(RelationListQO query, Long page, Long size);

    Long getTotal(PropertyRelationQO query, Long page, Long size);

    List<PropertyTenementVO> list(PropertyRelationQO query, Long page, Long size);
}
