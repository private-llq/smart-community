package com.jsy.community.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: 公共查询(省市区,小区等)常量接口定义
 * @Author: chq459799974
 * @Date: 2020/11/18
**/
public interface CommonQueryConsts {
	/**
	 * 房间成员查询类型 - 查询成员
	 */
	Integer QUERY_HOUSE_MEMBER = 1;
	/**
	 * 房间成员查询类型 - 查询邀请
	 */
	Integer QUERY_HOUSE_MEMBER_INVITATION = 2;
	
	/**
	 * 资源类查询类型
	 */
	Map<String,List<Map<String, Object>>> sourceMap = new HashMap<>();
	
	/**
	 * 省市区查询类型
	 */
	enum RegionQueryTypeEnum {
		SUB("getSubRegion", 1),
		CITY_MAP("getCityMap", 2),
		HOT_CITY("getHotCityList",3);
		private String name;
		private Integer code;
		RegionQueryTypeEnum(String name, Integer code) {
	        this.name = name;  
	        this.code = code;
	    }
	    
	    public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override  
	    public String toString() {
	        return this.code+"_"+this.name;
	    }
		
		public static final Map<Integer, String> regionQueryTypeMap = new HashMap<>();
		static {
			for(RegionQueryTypeEnum regionQueryTypeEnum : RegionQueryTypeEnum.values()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", regionQueryTypeEnum.getCode());
				map.put("name", regionQueryTypeEnum.getName());
				regionQueryTypeMap.put(regionQueryTypeEnum.getCode(), regionQueryTypeEnum.getName());
			}
		}
	}
	
	/**
	 * 车辆类型
	 */
	enum CarTypeEnum {
		TINY("微型车", 1),
		SMALL("小型车", 2),
		COMPACT("紧凑型车",3),
		MIDDLE("中型车",4),
		ML("中大型车",5);
		private String name;
		private Integer code;
		CarTypeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code+"_"+this.name;
		}
		
		public static final List<Map<String, Object>> carTypeList = new ArrayList<>();
		public static final Map<Integer, String> carTypeMap = new HashMap<>();
		static {
			for(CarTypeEnum regionQueryTypeEnum : CarTypeEnum.values()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", regionQueryTypeEnum.getCode());
				map.put("name", regionQueryTypeEnum.getName());
				carTypeList.add(map);
				carTypeMap.put(regionQueryTypeEnum.getCode(), regionQueryTypeEnum.getName());
			}
			sourceMap.put("carType",carTypeList);
		}
	}
	
}
