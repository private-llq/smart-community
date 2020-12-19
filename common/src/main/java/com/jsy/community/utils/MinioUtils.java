package com.jsy.community.utils;

import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.policy.PolicyType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Slf4j
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

	private static volatile MinioClient minioClient = null;

	private static final String[] allowSuffix = {"jpg","jpeg","png","bmp"};


	
	/**
	 * 文件上传
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String upload(MultipartFile file, String bucketName) {
		try {
			//获取minio客户端实例
			minioClient = getMinioClientInstance();
			//创建存储桶
			createBucket(bucketName);
			// 文件存储的目录结构
			String objectName = getRandomFileName(file.getOriginalFilename());
			// 存储文件
			minioClient.putObject(BUCKETNAME, objectName, file.getInputStream(), file.getContentType());
			//返回路径
			String filePath = ENDPOINT + ":" + PROT + "/" + BUCKETNAME + "/" + objectName;
			return filePath;
		} catch (Exception e) {
			throw new JSYException("上传失败,MinioUtils.upload()方法出现异常：" + e.getMessage());
		}
	}

	private static  String getRandomFileName(String fileName){
		return UUID.randomUUID().toString() + "-" + fileName;
	}

	/**
	 * 根据存储名称创建存储桶目录
	 * @param bucketName 		文件目录名称
	 */
	private static void createBucket(String bucketName){
		try {
			minioClient = getMinioClientInstance();
			// 存储桶
			BUCKETNAME = bucketName;
			//存入bucket不存在则创建
			if (!minioClient.bucketExists(BUCKETNAME)) {
				minioClient.makeBucket(BUCKETNAME);
				minioClient.setBucketPolicy(BUCKETNAME, "*", PolicyType.READ_WRITE);
			}
		} catch (Exception e) {
			log.error("com.jsy.community.utils.MinioUtils.createBucket：{}", "创建存储目录失败!:"+e.getMessage());
			throw new JSYException(JSYError.NOT_IMPLEMENTED+e.getMessage());
		}
	}


	/**
	 *  批量上传图片文件
	 * @author YuLF
	 * @since  2020/12/9 17:45
	 * @Param  files		文件数组
	 * @Param  bucketName	存储文件的目录名称
	 * @return		返回文件访问地址数组
	 */
	public static String[] uploadForBatch(MultipartFile[] files, String bucketName){
		//上传文件之后的结果路径
		String[] resAddress = new String[files.length];
		int index = 0;
		try {
			minioClient = getMinioClientInstance();
			createBucket(bucketName);
			for( MultipartFile file : files ){
				if( file != null && !file.isEmpty() && isImage(file.getOriginalFilename())){
					//1.对文件名随机
					String randomFileName = getRandomFileName(file.getOriginalFilename());
					//2.存储文件
					minioClient.putObject(bucketName, randomFileName, file.getInputStream(), file.getContentType());
					//3.获得文件访问路径
					resAddress[index] = ENDPOINT + ":" + PROT + "/" + bucketName + "/" + randomFileName;
					index++;
				}
			}
		} catch (Exception e) {
			log.error("com.jsy.community.utils.MinioUtils.uploadForBatch：{}", e.getMessage());
			throw new JSYException("批量上传文件失败!"+e.getMessage());
		}
		return resAddress;
	}

	private static boolean isImage(String name){
		return FilenameUtils.isExtension(name, allowSuffix);
	}

	/**
	 *  懒加载当用到 MinioClient 时 创建实例，只创建一次
	 * @author YuLF
	 * @since  2020/12/9 17:29
	 * @return			返回MinioClient
	 */
	private static MinioClient getMinioClientInstance() throws Exception {
		if(minioClient == null){
			synchronized (MinioUtils.class){
				if(minioClient == null){
					minioClient = new MinioClient(ENDPOINT, PROT, ACCESSKEY, SECRETKET);
				}
			}
		}
		return minioClient;
	}

	/**
	 * 下载文件
	 *
	 * @return
	 * @throws Exception
	 */
	public static MinioClient getFile() throws Exception {
		return getMinioClientInstance();
	}
	
	/**
	 * 删除文件
	 *
	 * @param filePath 路径
	 * @return
	 * @throws Exception
	 */
	public static String removeFile(String filePath) throws Exception {
		MinioClient minioClient = new MinioClient(ENDPOINT, PROT, ACCESSKEY, SECRETKET);
		String[] split = filePath.split("/");
		BUCKETNAME = split[3];
		String objectName = split[4];
		ObjectStat objectStat = minioClient.statObject(BUCKETNAME, objectName);
		if (objectStat != null) {
			minioClient.removeObject(BUCKETNAME, objectName);
			return "删除成功";
		} else {
			return "删除文件不存在!";
		}
	}
}
