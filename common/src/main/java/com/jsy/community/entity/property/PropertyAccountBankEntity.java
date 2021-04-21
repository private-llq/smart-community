package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 社区账户实体类
 * @since 2021-04-20 17:41
 **/
@Data
@TableName("t_property_account_bank")
public class PropertyAccountBankEntity implements Serializable {
	private Long id;
	private Long communityId;//社区ID
	private String accountName;//开户账户名称
	private String bankName;//银行名称
	private String bankCity;//开户行所在城市
	private String bankBranchName;//开户支行名称
	private String bankNo;//银行卡号
}
