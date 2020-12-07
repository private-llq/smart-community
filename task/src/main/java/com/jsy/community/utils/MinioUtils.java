package com.jsy.community.utils;

import io.minio.MinioClient;
import io.minio.policy.PolicyType;
import netscape.javascript.JSException;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class MinioUtils {
	//ip
	private static final String ENDPOINT = "http://222.178.212.29";
	//端口
	private static final int PROT = 9000;
	//ACCESS_KEY
	private static final String ACCESSKEY = "minio";
	//SECRET_KEY
	private static final String SECRETKET = "minimini";
	//存储桶名称
	private static String BUCKETNAME = null;
	
	/**
	 * 文件上传
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String upload(MultipartFile file, String bucketName) {
		try {
			MinioClient minioClient = new MinioClient(ENDPOINT, PROT, ACCESSKEY, SECRETKET);
			// 存储桶
			BUCKETNAME = bucketName;
			//存入bucket不存在则创建
			if (!minioClient.bucketExists(BUCKETNAME)) {
				minioClient.makeBucket(BUCKETNAME);
				minioClient.setBucketPolicy(BUCKETNAME, "*", PolicyType.READ_WRITE);
			}
			String fileName = file.getOriginalFilename();
			// 文件存储的目录结构
			String uuid = UUID.randomUUID().toString();
			String objectName = uuid + "-" + fileName;
			// 存储文件
			minioClient.putObject(BUCKETNAME, objectName, file.getInputStream(), file.getContentType());
			//返回路径
			String filePath = ENDPOINT + ":" + PROT + "/" + BUCKETNAME + "/" + objectName;
			return filePath;
		} catch (Exception e) {
			throw new JSException("上传失败,MinioUtils.upload()方法出现异常：" + e.getMessage());
		}
	}
	
	/**
	 * 下载文件
	 *
	 * @return
	 * @throws Exception
	 */
	public static MinioClient getFile() throws Exception {
		MinioClient minioClient = new MinioClient(ENDPOINT, PROT, ACCESSKEY, SECRETKET);
		return minioClient;
	}
	
	/**
	 * 删除文件
	 *
	 * @param filePath 路径   http://222.178.212.29:9000/app-menu-img/ccf58b1f-0b24-4c7d-9681-b2b4db2b4450-Chrysanthemum.jpg
	 * @return
	 * @throws Exception
	 */
	public static void removeFile(String filePath) throws Exception {
		MinioClient minioClient = new MinioClient(ENDPOINT, PROT, ACCESSKEY, SECRETKET);
		String[] split = filePath.split("/");
		BUCKETNAME = split[3];
		String objectName = split[4];
		minioClient.removeObject(BUCKETNAME, objectName);
	}
}
