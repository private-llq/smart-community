package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFeeRuleRelevanceEntity;
import com.jsy.community.qo.property.FeeRuleRelevanceQO;
import com.jsy.community.vo.property.FeeRuleCarPositionVO;
import com.jsy.community.vo.property.FeeRuleHouseVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 收费项目关联目标
 * @author: Hu
 * @create: 2021-09-06 09:44
 **/
public interface PropertyFeeRuleRelevanceMapper extends BaseMapper<PropertyFeeRuleRelevanceEntity> {
    /**
     * @Description: 批量新增收费项目关联目标
     * @author: Hu
     * @since: 2021/9/6 14:07
     * @Param:
     * @return:
     */
    void save(@Param("list") List<PropertyFeeRuleRelevanceEntity> list);

    /**
     * @Description: 查询所有关联房屋
     * @author: Hu
     * @since: 2021/9/6 14:24
     * @Param:
     * @return:
     */
    List<FeeRuleHouseVO> selectHouse(FeeRuleRelevanceQO feeRuleRelevanceQO);

    /**
     * @Description: 查询所有关联车位
     * @author: Hu
     * @since: 2021/9/6 14:25
     * @Param:
     * @return:
     */
    List<FeeRuleCarPositionVO> selectCarPosition(FeeRuleRelevanceQO feeRuleRelevanceQO);

    /**
     * @Description: 查询关联表中关联该项目的所有id
     * @author: Hu
     * @since: 2021/9/7 15:08
     * @Param:
     * @return:
     */
    @Select("select relevance_id from t_property_fee_rule_relevance where rule_id = #{id}")
    List<String> selectFeeRuleList(Long id);

}
