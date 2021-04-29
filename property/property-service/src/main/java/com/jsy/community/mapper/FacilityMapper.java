package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.hk.FacilityQO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2021-03-13
 */
public interface FacilityMapper extends BaseMapper<FacilityEntity> {
	
	
	List<FacilityEntity> listFacility(@Param("qo") FacilityQO facilityQO, @Param("page") Page<FacilityEntity> info);
	
	void insertFacilityStatus(@Param("id") long id, @Param("status") Integer status, @Param("facilityHandle") Integer facilityHandle, @Param("facilityId") Long facilityId, @Param("facilityAlarmHandle") int facilityAlarmHandle);
	
	/**
	 * @return int
	 * @Author 91李寻欢
	 * @Description 根据设备id获取唯一用户句柄
	 * @Date 2021/4/23 18:42
	 * @Param [id]
	 **/
	@Select("select facility_handle from t_facility_status where facility_id = #{id}")
	int selectFacilityHandle(Long id);
	
	@Select("select status from t_facility_status where facility_id = #{id}")
	int getStatus(Long id);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 根据设备id删除中间表
	 * @Date 2021/4/22 17:17
	 * @Param [id]
	 **/
	@Delete("delete from t_facility_status where facility_id = #{id}")
	void deleteMiddleFacility(Long id);
	
	List<Long> selectId(int communityId);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 根据设备id集合删除中间表
	 * @Date 2021/4/22 17:17
	 * @Param [ids]
	 **/
	void deleteMiddleFacilityIds(List<Long> ids);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 根据id更新设备状态
	 * @Date 2021/4/23 9:55
	 * @Param [id, status]
	 **/
	void updateMiddleStatus(@Param("id") Long id, @Param("status") Integer status);
	
	/**
	 * @return int
	 * @Author 91李寻欢
	 * @Description 根据设备id查询他的唯一布防句柄
	 * @Date 2021/4/23 15:48
	 * @Param [id]
	 **/
	@Select("select facility_alarm_handle from t_facility_status where facility_id = #{id}")
	int getAlarmHandle(Long id);
	
	/**
	 * @return int
	 * @Author 91李寻欢
	 * @Description 根据设备id查询他的唯一用户句柄
	 * @Date 2021/4/23 16:04
	 * @Param [id]
	 **/
	@Select("select facility_handle from t_facility_status where facility_id = #{id}")
	int getLoginHandle(Long id);
	
	/**
	 * @return java.util.List<java.lang.Long>
	 * @Author 91李寻欢
	 * @Description 根据设备分类id查询其下设备的id集合
	 * @Date 2021/4/23 17:16
	 * @Param [typeId]
	 **/
	List<Long> getFacilityIdByTypeId(Long typeId);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 根据设备id更新设备状态表的状态
	 * @Date 2021/4/23 18:53
	 * @Param [online, id]
	 **/
	void updateStatusByFacilityId(int online, Long id);
}
