package com.jsy.community.consts;

/**
 * @author chq459799974
 * @description 业主端枚举
 * @since 2021-05-08 11:03
 **/
public interface ProprietorConstsEnum {
	
//	/**
//	* @Description: 物业端账单缴费状态枚举
//	 * @Author: chq459799974
//	 * @Date: 2021/4/24
//	**/
//	enum OrderStatusEnum {
//		ORDER_STATUS_UNPAID("未缴费", 0),
//		ORDER_STATUS_PAID("已缴费", 1);
//		private String name;
//		private Integer code;
//
//		OrderStatusEnum(String name, Integer code) {
//			this.name = name;
//			this.code = code;
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		public void setName(String name) {
//			this.name = name;
//		}
//
//		public Integer getCode() {
//			return code;
//		}
//
//		public void setCode(Integer code) {
//			this.code = code;
//		}
//
//		@Override
//		public String toString() {
//			return this.code + "_" + this.name;
//		}
//
//		public static final List<Map<String, Object>> ORDER_STATUS_LIST = new ArrayList<>();
//		public static final Map<Integer, String> ORDER_STATUS_MAP = new HashMap<>();
//
//		static {
//			for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
//				HashMap<String, Object> map = new HashMap<>();
//				map.put("code", orderStatusEnum.getCode());
//				map.put("name", orderStatusEnum.getName());
//				ORDER_STATUS_LIST.add(map);
//				ORDER_STATUS_MAP.put(orderStatusEnum.getCode(), orderStatusEnum.getName());
//			}
//		}
//	}
}
