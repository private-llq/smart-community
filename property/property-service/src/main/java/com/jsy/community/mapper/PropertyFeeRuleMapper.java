package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.vo.property.FeeRuleVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业缴费收费标准
 * @author: Hu
 * @create: 2021-04-20 15:57
 **/
public interface PropertyFeeRuleMapper extends BaseMapper<PropertyFeeRuleEntity> {
    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/8/4 14:37
     * @Param:
     * @return:
     */
    List<FeeRuleVO> findList(@Param("page") long page, @Param("size") Long size, @Param("query") FeeRuleQO query);

    /**
     * @Description: 查询条数
     * @author: Hu
     * @since: 2021/8/4 14:37
     * @Param:
     * @return:
     */
    Integer findTotal(FeeRuleQO query);
    
    /**
     * @Description: 查询项目名称
     * @author: DKS
     * @since: 2021/8/18 17:19
     * @Param:
     * @return:
     */
    @MapKey("id")
    Map<String, Map<String,Object>> selectFeeRuleIdName(@Param("list") Collection<Long> feeRuleIds);
    
    /**
     * @Description: 根据项目名称模糊查询所有项目id
     * @author: DKS
     * @since: 2021/9/4 11:02
     */
    List<Long> selectFeeRuleIdList(@Param("list")List<Long> communityIds, @Param("feeRuleName")String feeRuleName);
}
