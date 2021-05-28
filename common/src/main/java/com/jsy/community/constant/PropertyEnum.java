package com.jsy.community.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 物业端相关枚举
 * @since 2021-03-12 10:23
 **/
public interface PropertyEnum {
	
	/**
	* @Description: 房屋类型枚举
	 * @Author: chq459799974
	 * @Date: 2021/3/12
	**/
	enum HouseTypeEnum{
		SHOP("商铺",1),
		HOUSE("住宅",2);
		private String name;
		private Integer code;
		HouseTypeEnum(String name, Integer code) {
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
		public static final Map<Integer, String> HOUSE_TYPE_MAP = new HashMap<>();
		static{
			for (HouseTypeEnum houseTypeEnum : HouseTypeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", houseTypeEnum.getCode());
				map.put("name", houseTypeEnum.getName());
				HOUSE_TYPE_MAP.put(houseTypeEnum.getCode(), houseTypeEnum.getName());
			}
		}

		public static Integer getCode(String name) {
			for (HouseTypeEnum value : HouseTypeEnum.values()) {
				if (value.getName().equals(name)) {
					return value.getCode();
				}
			}
			return null;
		}

		public static String getName(Integer code) {
			for (HouseTypeEnum value : HouseTypeEnum.values()) {
				if (value.getCode().equals(code)) {
					return value.getName();
				}
			}
			return null;
		}
	}
	
	/**
	 * @Description: 房产类型枚举
	 * @Author: chq459799974
	 * @Date: 2021/3/12
	 **/
	enum PropertyTypeEnum{
		T1("商品房",1),
		T2("房改房",2),
		T3("集资房",3),
		T4("经适房",4),
		T5("廉租房",5),
		T6("公租房",6),
		T7("安置房",7),
		T8("小产权房",8);
		private String name;
		private Integer code;
		PropertyTypeEnum(String name, Integer code) {
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
		public static final Map<Integer, String> PROPERTY_TYPE_MAP = new HashMap<>();
		static{
			for (PropertyTypeEnum propertyTypeEnum : PropertyTypeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", propertyTypeEnum.getCode());
				map.put("name", propertyTypeEnum.getName());
				PROPERTY_TYPE_MAP.put(propertyTypeEnum.getCode(), propertyTypeEnum.getName());
			}
		}

		public static Integer getCode(String name) {
			for (PropertyTypeEnum value : PropertyTypeEnum.values()) {
				if (value.getName().equals(name)) {
					return value.getCode();
				}
			}
			return null;
		}

		public static String getName(Integer code) {
			for (PropertyTypeEnum value : PropertyTypeEnum.values()) {
				if (value.getCode().equals(code)) {
					return value.getName();
				}
			}
			return null;
		}
	}
	
	/**
	 * @Description: 装修情况枚举
	 * @Author: chq459799974
	 * @Date: 2021/3/12
	 **/
	enum DecorationEnum{
		T1("样板间",1),
		T2("毛坯",2),
		T3("简装",3),
		T4("精装",4);
		private String name;
		private Integer code;
		DecorationEnum(String name, Integer code) {
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
		public static final Map<Integer, String> DECORATION_MAP = new HashMap<>();
		static{
			for (DecorationEnum decorationEnum : DecorationEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", decorationEnum.getCode());
				map.put("name", decorationEnum.getName());
				DECORATION_MAP.put(decorationEnum.getCode(), decorationEnum.getName());
			}
		}

		public static Integer getCode(String name) {
			for (DecorationEnum value : DecorationEnum.values()) {
				if (value.getName().equals(name)) {
					return value.getCode();
				}
			}
			return null;
		}

		public static String getName(Integer code) {
			for (DecorationEnum value : DecorationEnum.values()) {
				if (value.getCode().equals(code)) {
					return value.getName();
				}
			}
			return null;
		}
	}
	
}
