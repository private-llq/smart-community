package com.jsy.community.mapper;

import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.vo.PropertyTenementVO;
import org.apache.ibatis.annotations.Mapper;

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
    Long getTotal(PropertyRelationQO query, Long page, Long size);

    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/4/21 17:04
     * @Param:
     * @return:
     */
    List<PropertyTenementVO> list(PropertyRelationQO query, Long page, Long size);
}
