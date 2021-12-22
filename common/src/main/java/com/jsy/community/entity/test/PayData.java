package com.jsy.community.entity.test;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lihao
 * @ClassName PayData
 * @Date 2020/12/10  16:18
 * @Description
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PayData implements Serializable {
	private Integer id;
	
	@ApiModelProperty(value = "户主姓名")
	private String name;
	
	@ApiModelProperty(value = "户主地址")
	private String address;
	
	@ApiModelProperty(value = "户主户号")
	private String number;
	
	@ApiModelProperty(value = "缴费单位")
	private String company;
	
	@ApiModelProperty(value = "应缴金额")
	private BigDecimal payExpen;
	
	@ApiModelProperty(value = "户主余额")
	private BigDecimal payBalance;
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public void setPayExpen(BigDecimal payExpen) {
		this.payExpen = payExpen;
	}
	
	public void setPayBalance(BigDecimal payBalance) {
		this.payBalance = payBalance;
	}
}
