package com.jsy.community.vo.menu;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lihao
 * @ClassName AppMenuVO
 * @Date 2021/3/23  15:15
 * @Description
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "AppMenuVO对象", description = "物业端菜单对象")
public class AppMenuVO implements Serializable {
	private Long communityId;
	
	private Long menuId;
	
	private Integer sort;
}
