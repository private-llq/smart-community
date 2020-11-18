package com.jsy.community.api;

import com.jsy.community.entity.RegionEntity;

import java.util.List;
import java.util.Map;

/**
 * 公共的
 * @author ling
 * @since 2020-11-13 14:58
 */
public interface ICommonService {

    /**
     * 根据城市id查询下面所有社区
     * @param id   传入的城市id
     * @return     返回社区集合
     */
    List<Map> getAllCommunity(Integer id);

    /**
     * 根据社区id查询下面的所有单元
     * @param id    社区id
     * @return      返回单元集合
     */
    List<Map> getAllUnitFormCommunity(Integer id);

    /**
     * 根据单元id查询所有楼栋
     * @param id    单元id
     * @return      返回楼栋集合
     */
    List<Map> getAllBuildingFormUnit(Integer id);

    /**
     * 根据楼栋id查询楼层
     * @param id    楼栋id
     * @return      返回楼层集合
     */
    List<Map> getAllFloorFormBuilding(Integer id);

    /**
     * 根据楼层查询所有门牌号
     * @param id    楼层id
     * @return      返回门牌集合
     */
    List<Map> getAllDoorFormFloor(Integer id);
	
	/**
	 * @Description: 获取城市列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/18
	 **/
	List<RegionEntity> getCityList();
	
	/**
	 * @Description: 根据区域编号获取子区域 (中国编号为100000)
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/13
	 **/
	List<RegionEntity> getSubRegion(Integer id);

	/**
	 * 校验验证码，失败抛异常
	 *
	 * @param account 账号
	 * @param code    验证码
	 */
	void checkVerifyCode(String account, String code);
}
