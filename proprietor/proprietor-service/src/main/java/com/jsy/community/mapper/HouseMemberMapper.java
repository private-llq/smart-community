package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.vo.HouseMembersQO;
import com.jsy.community.vo.MembersVO;
import com.jsy.community.vo.UserHouseVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.*;

/**
 * <p>
 * 房间成员表 Mapper 接口
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-23
 */
public interface HouseMemberMapper extends BaseMapper<HouseMemberEntity> {

//	@Update("update t_house_member set is_confirm = 1 where id = #{id}")
//	int confirmJoin(@Param("id")Long id);
	
	/**
	* @Description: 房间ID 查 成员List
	 * @Param: [houseId]
	 * @Return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@Select("select id,householder_id,community_id,house_id,name,sex,mobile,identification_type,id_card from t_house_member where house_id = #{houseId} and person_type = 1")
	List<HouseMemberEntity> queryByHouseId(@Param("houseId") Long houseId);
	
	/**
	* @Description: ids批量查询
	 * @Param: [list]
	 * @Return: java.util.Map<java.lang.Long,com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@MapKey("id")
	Map<Long,HouseMemberEntity> queryByIdsBatch(Collection<Long> list);


	/**
	 * @Description: 批量新增
	 * @author: Hu
	 * @since: 2021/8/18 9:23
	 * @Param:
	 * @return:
	 */
    void saveBatch(@Param("save") LinkedList<HouseMemberEntity> save);

    /**
     * @Description: 查询当前登录人员信息
     * @author: Hu
     * @since: 2021/8/19 16:51
     * @Param:
     * @return:
     */
    UserHouseVO selectLoginUser(@Param("uid") String uid, @Param("communityId") Long communityId, @Param("houseId") Long houseId, @Param("relation") int relation);

    /**
     * @Description: 条件查询当前房屋下所有的成员
     * @author: Hu
     * @since: 2021/8/19 16:52
     * @Param:
     * @return:
     */
	List<MembersVO> selectRelation(@Param("communityId") Long communityId, @Param("houseId") Long houseId, @Param("relation") int relation);

	/**
	 * @Description: 修改成员表uid
	 * @author: Hu
	 * @since: 2021/10/12 15:00
	 * @Param:
	 * @return:
	 */
    void updateByUid(@Param("uid") String uid, @Param("mobile") String mobile);

	/**
	 * @Description: 根据id修改成员表id
	 * @author: Hu
	 * @since: 2021/10/12 15:00
	 * @Param:
	 * @return:
	 */
	void updateByMobile(@Param("ids") Set<Long> ids,@Param("uid") String uid);

	/**
	 * @Description: 养老导入家人列表接口
	 * @author: Hu
	 * @since: 2021/12/2 15:34
	 * @Param:
	 * @return:
	 */
	List<HouseMembersQO> selectMembers(@Param("uid") String userId);
}
