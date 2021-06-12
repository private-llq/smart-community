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
 * @description:  物业成员查询接口
 * @author: Hu
 * @create: 2021-03-05 11:21
 **/
@Mapper
public interface PropertyRelationMapper {
    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/4/21 17:02
     * @Param:
     * @return:
     */
    List<PropertyRelationVO> list(@Param("query") PropertyRelationQO query, @Param("page") Long page, @Param("size") Long size);
    /**
     * @Description: 总条数
     * @author: Hu
     * @since: 2021/4/21 17:02
     * @Param:
     * @return:
     */
    Long getTotal(@Param("query") PropertyRelationQO query);

    /**
     * @Description: 房屋下拉框
     * @author: Hu
     * @since: 2021/4/21 17:02
     * @Param:
     * @return:
     */
    List<HouseTypeVo> getHouseId(@Param("query")RelationListQO query, @Param("page")Long page,@Param("size")Long size);

    /**
     * @Description: 楼栋下拉框
     * @author: Hu
     * @since: 2021/4/21 17:02
     * @Param:
     * @return:
     */
    List<HouseTypeVo> getBuildingId(@Param("query")RelationListQO query, @Param("page")Long page,@Param("size")Long size);

    /**
     * @Description: 单元下拉框
     * @author: Hu
     * @since: 2021/4/21 17:02
     * @Param:
     * @return:
     */
    List<HouseTypeVo> getUnitId(@Param("query")RelationListQO query, @Param("page")Long page,@Param("size")Long size);

}
