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
	 * 省市区查询类型
	 */
	enum RegionQueryTypeEnum {
		SUB("getSubRegion", 1),
		CITY_MAP("getCityMap", 2);
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
	
}
