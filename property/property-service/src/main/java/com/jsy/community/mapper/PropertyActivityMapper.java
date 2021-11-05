package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.ActivityEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @program: com.jsy.community
 * @description: 社区活动
 * @author: Hu
 * @create: 2021-09-23 10:04
 **/
public interface PropertyActivityMapper extends BaseMapper<ActivityEntity> {
	
	/**
	 * @Description: 批量修改活动状态
	 * @author: DKS
	 * @since: 2021/11/3 16:17
	 * @Param: java.set.Set
	 * @return: void
	 */
	void updateByDataId(@Param("ids") Set<Long> ids, @Param("activityStatus") int activityStatus);
}
