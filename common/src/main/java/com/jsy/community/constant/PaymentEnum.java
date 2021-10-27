package com.jsy.community.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 支付相关枚举
 * @since 2021-01-06 17:22
 **/
public interface PaymentEnum {
	
	/**
	 * 支付相关询类型大字典
	 */
	Map<String,List<Map<String, Object>>> sourceMap = new HashMap<>();
	
	/**
	* @Description: 交易类型
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	enum TradeTypeEnum {
		TRADE_TYPE_EXPEND("支出", 1),
		TRADE_TYPE_INCOME("收入", 2);
		private String name;
		private Integer index;
		TradeTypeEnum(String name, Integer index) {
			this.name = name;
			this.index = index;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getIndex() {
			return index;
		}
		
		public void setIndex(Integer index) {
			this.index = index;
		}
		
		@Override
		public String toString() {
			return this.index+"_"+this.name;
		}
		
		public static final List<Map<String, Object>> tradeTypeList = new ArrayList<>();
		public static final Map<Integer, String> tradeTypeMap = new HashMap<>();
		static {
			for(TradeTypeEnum tradeTypeEnum : TradeTypeEnum.values()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("index", tradeTypeEnum.getIndex());
				map.put("name", tradeTypeEnum.getName());
				tradeTypeList.add(map);
				tradeTypeMap.put(tradeTypeEnum.getIndex(), tradeTypeEnum.getName());
			}
		}
	}
	
	/**
	* @Description: 交易来源
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	enum TradeFromEnum {
		TRADE_FROM_RMB("充值提现", 1),
		TRADE_FROM_SHOPPING("商城购物", 2),
		TRADE_FROM_LIVING_EXPENSES("水电缴费", 3),
		TRADE_FROM_MANAGEMENT("物业管理", 4),
		TRADE_FROM_RENT("房屋租金", 5),
		TRADE_FROM_REDBAG("红包", 6),
		TRADE_FROM_REDBAG_BACK("红包退回", 7),
		TRADE_FROM_PARKING_PAYMENT("停车缴费", 8),
		HOUSE_RENT_PAYMENT("房屋租赁", 9),
		TEMPORARY_CAR_PAYMENT("临时车辆缴费", 10),
		OTHER("其他缴费",11);
		private String name;
		private Integer index;
		TradeFromEnum(String name, Integer index) {
			this.name = name;
			this.index = index;
		}

		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getIndex() {
			return index;
		}
		
		public void setIndex(Integer index) {
			this.index = index;
		}
		
		@Override
		public String toString() {
			return this.index+"_"+this.name;
		}
		
		public static final List<Map<String, Object>> tradeFromList = new ArrayList<>();
		public static final Map<Integer, String> tradeFromMap = new HashMap<>();
		static {
			for(TradeFromEnum tradeFromEnum : TradeFromEnum.values()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("index", tradeFromEnum.getIndex());
				map.put("name", tradeFromEnum.getName());
				tradeFromList.add(map);
				tradeFromMap.put(tradeFromEnum.getIndex(), tradeFromEnum.getName());
			}
			sourceMap.put("tradeFrom",tradeFromList);
		}

		public static String getCode(Integer code){
			TradeFromEnum[] values = TradeFromEnum.values();
			for(TradeFromEnum c : values){
				if( c.index.equals(code) ){
					return c.name;
				}
			}
			return OTHER.name;
		}

		public static String getName(Integer index) {
			for (TradeFromEnum value : TradeFromEnum.values()) {
				if (index.equals(value.getIndex())) {
					return value.getName();
				}
			}
			return null;
		}
	}
	
	/**
	 * @Description: 订单支付交易状态
	 * @Author: chq459799974
	 * @Date: 2021/1/6
	 **/
	enum TradeStatusEnum {
		ORDER_PLACED("待支付",1),
		ORDER_COMPLETED("支付完成",2);
		private String name;
		private Integer index;
		TradeStatusEnum(String name, Integer index) {
			this.name = name;
			this.index = index;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		@Override
		public String toString() {
			return this.index+"_"+this.name;
		}
	}
	
	/**
	* @Description: 提现申请处理状态
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	enum WAStatusEnum {
		WA_PENDING("待处理", 1),
		WA_PROCESSING("处理中", 2),
		WA_REJECTED("已拒绝", 3),
		WA_COMPLETED("已完成", 4);
		private String name;
		private Integer index;
		WAStatusEnum(String name, Integer index) {
			this.name = name;
			this.index = index;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getIndex() {
			return index;
		}
		
		public void setIndex(Integer index) {
			this.index = index;
		}
		
		@Override
		public String toString() {
			return this.index+"_"+this.name;
		}
		
		public static final List<Map<String, Object>> wAStatusList = new ArrayList<>();
		public static final Map<Integer, String> wAStatusMap = new HashMap<>();
		static {
			for(WAStatusEnum wAStatusEnum : WAStatusEnum.values()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("index", wAStatusEnum.getIndex());
				map.put("name", wAStatusEnum.getName());
				wAStatusList.add(map);
				wAStatusMap.put(wAStatusEnum.getIndex(), wAStatusEnum.getName());
			}
		}
	}
	
	/**
	* @Description: 币种
	 * @Author: chq459799974
	 * @Date: 2021/1/18
	**/
	enum CurrencyEnum {
		CURRENCY_CNY("CNY", 1);
		private String name;
		private Integer index;
		CurrencyEnum(String name, Integer index) {
			this.name = name;
			this.index = index;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getIndex() {
			return index;
		}
		
		public void setIndex(Integer index) {
			this.index = index;
		}
		
		@Override
		public String toString() {
			return this.index+"_"+this.name;
		}
	}

	/**
	 *@Author: Pipi
	 *@Description: 银联支付短信模板编码
	 *@Param: null:
	 *@Return:
	 *@Date: 2021/4/12 17:10
	 **/
	enum SmsTmpltCode {
		COMMON("COMMON", "1"),
		REGIST("REGIST", "2"),
		ACTIVATE("ACTIVATE", "3"),
		TRADE("TRADE", "4");

		private String name;
		private String index;

		SmsTmpltCode(String name, String index) {
			this.name = name;
			this.index = index;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public static String getName(String index) {
			for (SmsTmpltCode value : SmsTmpltCode.values()) {
				if (index.equals(value.getIndex())) {
					return value.getName();
				}
			}
			return null;
		}
	}

	/**
	 *@Author: Pipi
	 *@Description: 银联支付短信业务类型
	 *@Param: null:
	 *@Return:
	 *@Date: 2021/4/12 17:16
	 **/
	enum SmsTmpltBusinessType {
		BIND("绑定第三方用户", 1),
		RESET("重置密码", 2),
		CHANGE("变更手机号", 3),
		OPEN_ACCOUNT("开户", 4),
		SIGN_UP("银行卡签约", 5),
		TRADE("交易", 6);

		private String name;
		private Integer index;

		SmsTmpltBusinessType(String name, Integer index) {
			this.name = name;
			this.index = index;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public static String getName(Integer index) {
			for (SmsTmpltBusinessType value : SmsTmpltBusinessType.values()) {
				if (index.equals(value.getIndex())) {
					return value.getName();
				}
			}
			return null;
		}
	}
}
