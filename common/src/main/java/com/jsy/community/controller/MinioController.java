package com.jsy.community.controller;

import com.jsy.community.utils.MinioUtils;
import com.jsy.community.vo.CommonResult;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@RestController
@Api(tags = "分布式文件存储")
@Slf4j
public class MinioController {
	
//	@Resource
//	private StringRedisTemplate stringRedisTemplate;
	
	private static final String BUCKETNAME = "common"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	
	@ApiOperation("上传文件")
	@PostMapping("/uploadFile")
	public CommonResult uploadFile(@RequestParam(value = "file", required = false) MultipartFile file) {
		try {
			String str = MinioUtils.upload(file,BUCKETNAME);
//			redisTemplate.opsForSet().add("imgUp_part", str);// 最终上传时将图片地址再存入redis
//			stringRedisTemplate.opsForSet().add("imgUp_part", str);
			return CommonResult.ok(str);// str：上传成功后的地址
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CommonResult.error("上传失败");
	}
	
	
	@ApiOperation("删除文件")
	@GetMapping("/removeFile")
	public CommonResult removeFile(@RequestParam(value = "filePath", required = false) String filePath) {
		try {
			String str = MinioUtils.removeFile(filePath);
			return CommonResult.ok(str); //str：删除成功后的提示信息
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CommonResult.error("删除失败");
	}
	
	@ApiOperation("下载文件")
	@PostMapping("/downLoadFile")
	public CommonResult downLoadFile(@RequestParam("filePath") String filePath, HttpServletResponse response) throws Exception {
		MinioClient minioClient = MinioUtils.getFile();
		String[] split = filePath.split("/");
		String bucketName = split[3];
		String objectName = split[4];
		//判断是否存在
		ObjectStat objectStat = minioClient.statObject(bucketName, objectName);
		//存在，下载
		if (objectStat != null) {
			InputStream inputStream = minioClient.getObject(bucketName, objectName);
			ServletOutputStream outputStream = response.getOutputStream();
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, len);
			}
			outputStream.close();
			inputStream.close();
			return CommonResult.ok();
		} else {
			return CommonResult.error("下载失败");
		}
	}
}
