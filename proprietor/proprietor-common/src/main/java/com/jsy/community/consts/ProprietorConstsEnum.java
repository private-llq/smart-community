package com.jsy.community.consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 租赁签约进程状态枚举
     */
    enum ContractingProcessStatusEnum {
        INITIATE_CONTRACT("发起签约", 1),
        ACCEPTING_APPLICATIONS("接受申请", 2),
        CONTRACT_PREPARATION("拟定合同", 3),
        WAITING_TO_PAY_RENT("等待支付房租", 4),
        PAYMENT_COMPLETED("支付完成", 5),
        COMPLETE_CONTRACT("完成签约", 6),
        CANCELLATION_REQUEST("取消申请", 7),
        REJECTION_OF_APPLICATION("拒绝申请", 8),
        RELAUNCH("重新发起", 9);
		private String name;
		private Integer code;

        ContractingProcessStatusEnum(String name, Integer code) {
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
			for (ContractingProcessStatusEnum processStatusEnum : ContractingProcessStatusEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", processStatusEnum.getCode());
				map.put("name", processStatusEnum.getName());
				ORDER_STATUS_LIST.add(map);
				ORDER_STATUS_MAP.put(processStatusEnum.getCode(), processStatusEnum.getName());
			}
		}
	}
}
