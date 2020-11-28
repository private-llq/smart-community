package com.jsy.community.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 业务相关枚举
 * @since 2020-11-28 13:47
 **/
public interface BusinessEnum {
	
	/**
	 * 资源类查询类型大字典
	 */
	Map<String,List<Map<String, Object>>> sourceMap = new HashMap<>();
	
	/**
	* @Description: 省市区查询类型
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
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
	* @Description: 车辆类型
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
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
		public static final String REGEX_CAR_TYPE = "^[1-5]{1}";
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
	
	/**
	* @Description: 审核状态枚举
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
	enum CheckStatusEnum {
		UN_CHECK("未审核", 1),
		PASS("通过", 2),
		UNPASS("拒绝", 3);
		private String name;
		private Integer code;
		
		CheckStatusEnum(String name, Integer code) {
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
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> checkStatusList = new ArrayList<>();
		public static final Map<Integer, String> checkStatusMap = new HashMap<>();
		
		static {
			for (CheckStatusEnum checkStatusEnum : CheckStatusEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", checkStatusEnum.getCode());
				map.put("name", checkStatusEnum.getName());
				checkStatusList.add(map);
				checkStatusMap.put(checkStatusEnum.getCode(), checkStatusEnum.getName());
			}
			sourceMap.put("checkStatus",checkStatusList);
		}
	}
	
	/**
	* @Description: 来访事由枚举
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
	enum VisitReasonEnum {
		NORMAL("一般来访", 1),
		APPLICATOIN("应聘来访", 2),
		FRIEND("走亲访友", 3),
		BUSINESS("客户来访", 4);
		private String name;
		private Integer code;
		
		VisitReasonEnum(String name, Integer code) {
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
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> visitReasonList = new ArrayList<>();
		public static final Map<Integer, String> visitReasonMap = new HashMap<>();
		
		static {
			for (VisitReasonEnum visitReasonEnum : VisitReasonEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", visitReasonEnum.getCode());
				map.put("name", visitReasonEnum.getName());
				visitReasonList.add(map);
				visitReasonMap.put(visitReasonEnum.getCode(), visitReasonEnum.getName());
			}
			sourceMap.put("visitReason",visitReasonList);
		}
	}
}
