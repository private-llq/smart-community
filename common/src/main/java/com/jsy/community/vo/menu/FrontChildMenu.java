package com.jsy.community.vo.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author lihao
 * @ClassName FrontChildMenu
 * @Date 2020/11/17  10:10
 * @Description TODO
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrontChildMenu implements Serializable {
	
	private Long id;
	
	private Long parentId;
	
	@ApiModelProperty(value = "菜单名")
	private String menuName;
}
