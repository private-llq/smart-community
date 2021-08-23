package com.jsy.community.qo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 查询入参基础类
 * @Author chq459799974
 * @Date 2020/11/11 9:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseQO<T> implements Serializable {
	
	@ApiModelProperty("分页查询当前页")
	private Long page=1L;
	
	@ApiModelProperty("分页查询每页数据条数")
	private Long size=10L;

	private T query;

}
