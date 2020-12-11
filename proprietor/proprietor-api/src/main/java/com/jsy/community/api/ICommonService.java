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
	 * @author YuLF
	 * @since  2020/12/8 16:39
     * @param id   传入的城市id
     * @return     返回社区集合
     */
	List<Map<String, Object>> getAllCommunityFormCityId(Integer id,Integer houseLevelMode);

    /**
     * 根据社区id和社区层级结构 查询下面的所有单元 或 所有楼栋
	 * @author YuLF
	 * @since  2020/12/8 16:39
     * @param id                社区id
	 * @param houseLevelMode    社区层级结构id
     * @return      			返回单元或楼栋集合
     */
    List<Map<String, Object>> getBuildingOrUnitByCommunityId(Integer id, Integer houseLevelMode);

    /**
     * 根据社区层级结构 和 单元id|楼栋id 查询下一级的数据
	 * 比如 层级结构 为单元楼栋 那就是根据单元id查询下面所有楼栋  如果是楼栋单元 那就是根据楼栋id查询下面的所有单元  如果是单楼栋 那就根据楼栋id查询楼层 单单元也是查询楼层】
	 * @author YuLF
	 * @since  2020/12/8 16:39
     * @param id    单元id
     * @return      返回楼栋集合
     */
	List<Map<String, Object>> getBuildingOrUnitOrFloorById(Integer id, Integer houseLevelMode);

    /**
     * 根据楼层id查询所有门牌号
     * @author YuLF
     * @since  2020/12/8 16:39
     * @Param  id   楼层id
     */
	List<Map<String, Object>> getAllDoorFormFloor(Integer id, Integer houseLevelMode);
	
	/**
	 * @Description: 根据区域编号获取子区域 (中国编号为100000)
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/13
	 **/
	List<RegionEntity> getSubRegion(Integer id);
	
	/**
	* @Description: 获取城市字典
	 * @Param: []
	 * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/19
	**/
	Map<String,RegionEntity> getCityMap();
	
	/**
	 * @Description: 获取城市列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/18
	 **/
	List<RegionEntity> getCityList();
	
	/**
	* @Description: 获取推荐城市
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/19
	**/
	List<RegionEntity> getHotCityList();
	
	/**
	 * 校验验证码，失败抛异常
	 *
	 * @param account 账号
	 * @param code    验证码
	 */
	void checkVerifyCode(String account, String code);
	
	/**
	* @Description: 城市模糊查询
	 * @Param: [searchStr]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	**/
	List<RegionEntity> vagueQueryCity(String searchStr);
}
