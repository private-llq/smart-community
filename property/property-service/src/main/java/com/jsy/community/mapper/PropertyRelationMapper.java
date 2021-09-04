package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.HouseTypeVo;
import com.jsy.community.vo.PropertyRelationVO;
import com.jsy.community.vo.property.HouseMemberVO;
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
public interface PropertyRelationMapper extends BaseMapper<HouseMemberEntity> {
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

    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/7/24 14:09
     * @Param:
     * @return:
     */
    List<HouseMemberVO> pageList(@Param("page") Long page, @Param("size")Long size, @Param("query")HouseMemberQO query);

    /**
     * @Description: 总条数
     * @author: Hu
     * @since: 2021/4/21 17:02
     * @Param:
     * @return:
     */
    Long pageListTotal(@Param("query") HouseMemberQO query);

    /**
     * @Description: 批量迁出
     * @author: Hu
     * @since: 2021/7/24 15:28
     * @Param:
     * @return:
     */
    void emigrations(Long[] ids);

    /**
     * @Description: 批量删除
     * @author: Hu
     * @since: 2021/8/3 17:20
     * @Param:
     * @return:
     */
    void deletes(@Param("longAry") Long[] longAry);

    /**
     * @Description: 导出成员信息表
     * @author: Hu
     * @since: 2021/8/31 15:37
     * @Param:
     * @return:
     */
    List<HouseMemberVO> queryExportRelationExcel(@Param("query") HouseMemberQO houseMemberQO);

    /**
     * @Description: 导入批量新增
     * @author: Hu
     * @since: 2021/9/4 11:03
     * @Param:
     * @return:
     */
    void saveList(@Param("list") List<HouseMemberEntity> entityList);
}
