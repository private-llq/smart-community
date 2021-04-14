package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.VisitorHistoryEntity;
import org.apache.ibatis.annotations.Param;


/**
 * @author chq459799974
 * @description 访客进出记录Mapper
 * @since 2021-04-13 13:44
 **/
public interface VisitorHistoryMapper extends BaseMapper<VisitorHistoryEntity> {

	Page<VisitorHistoryEntity> queryPage(Page<VisitorHistoryEntity> page, @Param("query") VisitorHistoryEntity query);
	
}
