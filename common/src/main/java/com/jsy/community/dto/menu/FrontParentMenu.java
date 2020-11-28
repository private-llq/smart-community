package com.jsy.community.dto.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author lihao
 * @ClassName FrontParentMenu
 * @Date 2020/11/17  10:10
 * @Description TODO
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrontParentMenu implements Serializable {
	
	private Long id;
	
	private Long parentId;
	
	@ApiModelProperty(value = "菜单名")
	private String menuName;
	
	private List<FrontChildMenu> childMenus;
}
