package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社区楼栋 Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-11-20
 */
public interface HouseMapper extends BaseMapper<HouseEntity> {
	
	List<Long> getSubIdList(List<Long> list);
	
	int addHouse(@Param("houseEntity") HouseEntity houseEntity);

	/**
	 * 按社区ID获取 社区名称和 当前社区住户房间数量
	 * @author YuLF
	 * @since  2020/12/3 11:06
	 * @Param  communityId   社区id
	 * @return				 返回社区名称和 当前社区住户房间数量
	 */
    Map<String, Object> getCommunityNameAndUserAmountById(long communityId);
}
