package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件上传成功
 *
 * @author ling
 * @since 2020-11-19 15:15
 */
@Data
public class FileVo {
	@ApiModelProperty("文件ID")
	private Long id;
	
	@ApiModelProperty("文件路径，包含域名")
	private String url;
	
	@ApiModelProperty("文件名")
	private String fileName;
	
	@ApiModelProperty("文件路径")
	private String path;
	
	@ApiModelProperty("文件大小，单位Byte")
	private Integer size;
}
