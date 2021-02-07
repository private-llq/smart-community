package com.jsy.community.qo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户流水QO
 * @since 2021-02-07
 **/
@ApiModel("用户账户流水QO")
@Data
public class UserAccountRecordQO implements Serializable {
	
	@ApiModelProperty(value = "用户ID")
	private String uid;
	
	@ApiModelProperty(value = "交易类型1.收入2.支出")
	private Integer tradeType;

}
