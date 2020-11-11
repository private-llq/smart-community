package com.jsy.community.qo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: 查询入参基础类
 * @Author chq459799974
 * @Date 2020/11/11 9:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseQO implements Serializable {

    @ApiModelProperty("分页查询当前页")
	private Long page;

	@ApiModelProperty("分页查询每页数据条数")
	private Long size;

}
