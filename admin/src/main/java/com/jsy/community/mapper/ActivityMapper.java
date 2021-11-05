package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.ActivityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 活动管理
 * @author: DKS
 * @create: 2021-11-3 10:00
 **/
@Mapper
public interface ActivityMapper extends BaseMapper<ActivityEntity> {
	/**
	 * @Description: 批量新增活动
	 * @author: DKS
	 * @since: 2021/11/3 15:58
	 * @Param: java.util.List<com.jsy.community.entity.proprietor.ActivityEntity>
	 * @return: java.util.int
	 */
	int addActivityEntities(@Param("list") List<ActivityEntity> list);
}
