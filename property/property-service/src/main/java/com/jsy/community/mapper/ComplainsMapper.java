package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.vo.ComplainVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 15:50
 **/
public interface ComplainsMapper extends BaseMapper<ComplainEntity> {
    /**
     * @Description: 查询所有投诉信息
     * @author: Hu
     * @since: 2020/12/23 17:01
     * @Param:
     * @return:
     * @param page
     * @param size
     * @param query
     */
    List<ComplainVO> listAll(@Param("page") Long page,@Param("size") Long size,@Param("query")PropertyComplaintsQO query);
}
