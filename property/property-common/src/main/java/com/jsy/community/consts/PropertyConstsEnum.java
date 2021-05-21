package com.jsy.community.consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 物业端枚举
 * @since 2021-04-16 11:03
 **/
public interface PropertyConstsEnum {
	
	/**
	* @Description: 物业端用户类型枚举
	 * @Author: chq459799974
	 * @Date: 2021/4/24
	**/
	enum RoleTypeEnum {
		SUPER_ADMIN("超级管理员", 1),
		NORMAL_ADMIN("普通用户", 2);
		private String name;
		private Integer code;
		
		RoleTypeEnum(String name, Integer code) {
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
		
		public static final List<Map<String, Object>> ROLE_TYPE_LIST = new ArrayList<>();
		public static final Map<Integer, String> ROLE_TYPE_MAP = new HashMap<>();
		
		static {
			for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", roleTypeEnum.getCode());
				map.put("name", roleTypeEnum.getName());
				ROLE_TYPE_LIST.add(map);
				ROLE_TYPE_MAP.put(roleTypeEnum.getCode(), roleTypeEnum.getName());
			}
		}
	}
	
	/**
	* @Description: 物业端账单缴费状态枚举
	 * @Author: chq459799974
	 * @Date: 2021/4/24
	**/
	enum OrderStatusEnum {
		ORDER_STATUS_UNPAID("未缴费", 0),
		ORDER_STATUS_PAID("已缴费", 1);
		private String name;
		private Integer code;
		
		OrderStatusEnum(String name, Integer code) {
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
		
		public static final List<Map<String, Object>> ORDER_STATUS_LIST = new ArrayList<>();
		public static final Map<Integer, String> ORDER_STATUS_MAP = new HashMap<>();
		
		static {
			for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", orderStatusEnum.getCode());
				map.put("name", orderStatusEnum.getName());
				ORDER_STATUS_LIST.add(map);
				ORDER_STATUS_MAP.put(orderStatusEnum.getCode(), orderStatusEnum.getName());
			}
		}
	}
}
