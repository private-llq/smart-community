package com.jsy.community.mapper;

import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.HouseTypeVo;
import com.jsy.community.vo.PropertyRelationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-05 11:21
 **/
@Mapper
public interface PropertyRelationMapper {
    List<PropertyRelationVO> list(@Param("query") PropertyRelationQO query, @Param("page") Long page, @Param("size") Long size);
    Long getTotal(@Param("query") PropertyRelationQO query, @Param("page") Long page, @Param("size") Long size);

    List<HouseTypeVo> getHouseId(@Param("query")RelationListQO query, @Param("page")Long page,@Param("size")Long size);

    List<HouseTypeVo> getBuildingId(@Param("query")RelationListQO query, @Param("page")Long page,@Param("size")Long size);

    List<HouseTypeVo> getUnitId(@Param("query")RelationListQO query, @Param("page")Long page,@Param("size")Long size);
}
