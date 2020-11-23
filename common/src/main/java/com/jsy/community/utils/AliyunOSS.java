package com.jsy.community.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 对象存储配置
 *
 * @author ling
 * @since 2020-11-20 15:43
 */
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "jsy.oss.aliyun")
@Data
public class AliyunOSS {
	/**
	 * 访问OSS的域名
	 */
	private String endPoint;
	
	private String accessKeyId;
	
	private String accessKeySecret;
	
	private String bucketName;
	
	public String upload(String path, MultipartFile file) throws IOException {
		// <yourObjectName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
		String objectName = path + file.getOriginalFilename();
		
		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
		
		// 上传文件到指定的存储空间（bucketName）并将其保存为指定的文件名称（objectName）。
		ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(file.getBytes()));
		
		// 关闭OSSClient。
		ossClient.shutdown();
		
		return objectName;
	}
}
