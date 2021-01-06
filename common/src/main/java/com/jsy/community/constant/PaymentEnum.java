package com.jsy.community.constant;

/**
 * @author chq459799974
 * @description 支付相关枚举
 * @since 2021-01-06 17:22
 **/
public interface PaymentEnum {
	
	/**
	 * @Description: 支付相关-交易状态
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
	 * @Description: 交易类型
	 * @Author: chq459799974
	 * @Date: 2021/1/6
	 **/
	enum TradeTypeEnum {
		PAYMENT("支付",1),
		WITHDRAWAL("提现",2);
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
	}
	
	/**
	 * @Description: 交易名称
	 * @Author: chq459799974
	 * @Date: 2021/1/6
	 **/
	enum TradeNameEnum {
		RENT_PAYMENT("租金支付",1),
		RENT_WITHDRAWAL("提现",2);
		private String name;
		private Integer index;
		TradeNameEnum(String name, Integer index) {
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
	
}
