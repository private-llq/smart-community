package com.jsy.community.mapper;

import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.vo.PropertyTenementVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:  物业租户查询
 * @author: Hu
 * @create: 2021-03-10 14:35
 **/
@Mapper
public interface TenementMapper {
    /**
     * @Description: 总条数
     * @author: Hu
     * @since: 2021/4/21 17:04
     * @Param:
     * @return:
     */
    Long getTotal(@Param("query") PropertyRelationQO query);

    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/4/21 17:04
     * @Param:
     * @return:
     */
    List<PropertyTenementVO> list(@Param("query") PropertyRelationQO query,@Param("page") Long page,@Param("size") Long size);
}
