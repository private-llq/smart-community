package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
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
	
	/**
	* @Description: 查询下级house
	 * @Param: [list]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	List<Long> getSubIdList(List<Long> list);
	
	/**
	* @Description: 新增次级楼宇信息
	 * @Param: [houseEntity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/1/21
	**/
	int addSub(@Param("houseEntity") HouseEntity houseEntity);

	/**
	 * 按社区ID获取 社区名称和 当前社区住户房间数量
	 * @author YuLF
	 * @since  2020/12/3 11:06
	 * @Param  communityId   社区id
	 * @return				 返回社区名称和 当前社区住户房间数量
	 */
    Map<String, Object> getCommunityNameAndUserAmountById(long communityId);


	/**
	 * 按社区ID获取 社区名称 社区用户名和社区用户uid
	 * @author YuLF
	 * @since  2020/12/7 11:06
	 * @param communityId 			社区id
	 * @return						返回社区名称和 当前社区所有住户名称，住户uid
	 */
    List<UserEntity> getCommunityNameAndUserInfo(long communityId);



	/**
	 * 通过社区ID查出所有 楼栋、单元、楼层、未被登记的门牌
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @Param  communityId	社区ID
	 */
    List<HouseEntity> getCommunityArchitecture(@Param("communityId") long communityId);
}
