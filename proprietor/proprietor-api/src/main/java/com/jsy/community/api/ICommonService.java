package com.jsy.community.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.MedicalSvTpCardHeadInfo;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.vo.CommonResult;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @param page     当前页
	 * @param pageSize 页码
	 * @return     返回社区集合
     */
	List<Map<String, Object>> getAllCommunityFormCityId(Long id,  Integer page, Integer pageSize);

    /**
     * 根据社区id 查询下面的所有单元 或 所有楼栋
	 * @author YuLF
	 * @since  2020/12/8 16:39
     * @param id                社区id
     * @param page              当前页
	 * @param pageSize			当前分页最大条数
	 * @return      			返回单元或楼栋集合
     */
//    List<Map<String, Object>> getBuildingOrUnitByCommunityId(Long id,  Integer page, Integer pageSize);
	Map<String,List<Map<String, Object>>> getBuildingOrUnitByCommunityId(Long id,  Integer page, Integer pageSize);

    /**
     * 根据 楼栋id | 查询下一级的数据
	 * 楼栋id 就是 查下面所有 单元  如果单元列表为空 则按楼栋id查楼层 】
	 * @author YuLF
	 * @since  2020/12/8 16:39
     * @param id    			楼栋id 或 单元id
	 * @param page				当前页
	 * @param pageSize			每页显示条数
	 * @return      			返回单元 集合 或者 房屋集合
     */
//	List<Map<String, Object>> getUnitOrFloorById(Long id,  Integer page, Integer pageSize);
	Map<String,List<Map<String, Object>>> getUnitOrFloorById2(Long id,  Integer page, Integer pageSize);


//    /**
//     * 根据单元id查询所有门牌号
//     * @author YuLF
//     * @since  2020/12/8 16:39
//     * @Param  id   单元id
//     */
//	List<Map<String, Object>> getFloorByUnitId(Long id,  Integer page, Integer pageSize);



	
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
	
	/**
	* @Description: 区域模糊查询
	 * @Param: [searchStr]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/22
	**/
	List<RegionEntity> vagueQueryArea(String searchStr,Integer cityId);

	//天气假数据
	JSONObject getTempWeather();
	
	//天气详情假数据
	JSONObject getTempWeatherDetails();
	
	/**
	* @Description: 首页天气
	 * @Param: [cityName]
	 * @Return: com.alibaba.fastjson.JSONObject
	 * @Author: chq459799974
	 * @Date: 2021/2/25
	**/
	JSONObject getWeather(String cityName);
	
	/**
	 * @Description: 天气详情整合接口
	 * @Param: [cityName]
	 * @Return: com.alibaba.fastjson.JSONObject
	 * @Author: chq459799974
	 * @Date: 2020/2/25
	 **/
	JSONObject getWeatherDetails(String cityName);
	
	/**
	 * 全文搜索 数据库 数据导入 Elasticsearch
	 * @return		返回数据集合
	 */
	List<FullTextSearchEntity> fullTextSearchEntities();


	/**
	 * 异步添加app全文搜索热词
	 * @param hotKey 	用户搜索词
	 * @author YuLF
	 * @since  2021/3/10 13:48
	 */
	@Async(BusinessConst.PROPRIETOR_ASYNC_POOL)
	void addFullTextSearchHotKey( String hotKey );

	/**
	 * 清理全文搜索热词
	 * @param hotKeyActiveDay 	热词保持活跃天数
	 * @author YuLF
	 * @since  2021/3/10 15:45
	 */
	@Async(BusinessConst.PROPRIETOR_ASYNC_POOL)
	void cleanHotKey(Integer hotKeyActiveDay);

	/**
	 * app全文搜索 热词获取
	 * @param num 获取热词数量
	 * @return		返回热词数量
	 */
	Set<Object> getFullTextSearchHotKey(Integer num);

	/**
	 * @author: Pipi
	 * @description: 通过区域名称模糊匹配区域
	 * @param regionName: 区域名称
	 * @return: {@link RegionEntity}
	 * @date: 2022/1/4 15:25
	 **/
	RegionEntity queryRegionByName(String regionName);


	/**
	 * 通过楼层文本 和 单元id或楼栋id 查下面所有房屋
	 * @param id 			楼栋id或单元id
	 * @param floor			楼层文本
	 * @return				返回属于该楼层的 房屋
	 */
    List<Map<String, Object>> getHouseByFloor(Long id, String floor);
}
