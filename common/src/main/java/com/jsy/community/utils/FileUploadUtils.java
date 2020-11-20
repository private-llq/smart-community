package com.jsy.community.utils;

import com.jsy.community.exception.JSYException;
import com.jsy.community.vo.FileVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传工具类
 *
 * @author ling
 * @since 2020-11-20 15:33
 */
@Configuration
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class FileUploadUtils {
	
	@Value("${jsy.upload.type:dfs}")
	private String uploadType;
	
	private static Map<Integer, String> paths = new HashMap<>(16);
	
	static {
		paths.put(1, "/avatar/");
	}
	
	@Resource
	private AliyunOSS aliyunOSSUtils;
	
	public FileVo upload(Integer type, MultipartFile file) {
		String url = null;
		String path = paths.get(type);
		if (path == null) {
			throw new JSYException("文件上传类型错误");
		}
		if (uploadType.equals("oss")) {
			try {
				url = aliyunOSSUtils.upload(path, file);
			} catch (IOException e) {
				e.printStackTrace();
				throw new JSYException("上传文件失败");
			}
		} else {
			// 分布式文件系统
		}
		
		FileVo vo = new FileVo();
		vo.setUrl(url);
		vo.setFileName(file.getOriginalFilename());
		vo.setPath(path + file.getOriginalFilename());
		vo.setSize(file.getSize());
		
		return vo;
	}
	
	public void delete() {
	
	}
	
}
