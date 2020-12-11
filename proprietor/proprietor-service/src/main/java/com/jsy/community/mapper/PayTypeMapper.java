package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PayTypeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 缴费类型 Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2020-12-11
 */
public interface PayTypeMapper extends BaseMapper<PayTypeEntity> {
	
	List<Long> getPayTypeIds(Long id);
	
	void insertToMiddle(@Param("id") Long id, @Param("i") Long i);
	
	Integer  selectMiddle(@Param("id") Long id, @Param("dataId") Long dataId);
}
