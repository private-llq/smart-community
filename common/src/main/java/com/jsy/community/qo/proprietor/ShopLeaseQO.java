package com.jsy.community.qo.proprietor;

import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author lihao
 * @ClassName ShopLeaseQO
 * @Date 2020/12/21  10:46
 * @Description TODO
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="商铺租赁收参对象", description="接收前端参数")
public class ShopLeaseQO extends BaseQO {
	
	@ApiModelProperty("详细地址")
	private String address;
	
	@ApiModelProperty("小区名")
	private String communityName;
	
}
