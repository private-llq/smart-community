package com.jsy.community.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;
/**
* @Description: 判断图片格式
 * @Author: chq459799974
 * @Date: 2020/12/2
**/
public class PicUtil {
	public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = "unknown";
 
    /**
     * byte数组转换成16进制字符串
     */
    public static String bytesToHexString(byte[] src){
           StringBuilder stringBuilder = new StringBuilder();
           if (src == null || src.length <= 0) {
               return null;
           }
           for (int i = 0; i < src.length; i++) {
               int v = src[i] & 0xFF;
               String hv = Integer.toHexString(v);
               if (hv.length() < 2) {
                   stringBuilder.append(0);
               }
               stringBuilder.append(hv);
           }
           return stringBuilder.toString();
       }
 
    /**
     * 根据文件流判断图片类型
     */
    public static String getPicType(FileInputStream fis) {
        //读取文件的前几个字节来判断图片格式
        byte[] b = new byte[4];
        try {
            fis.read(b, 0, b.length);
            String type = bytesToHexString(b).toUpperCase();
            if (type.contains("FFD8FF")) {
                return TYPE_JPG;
            } else if (type.contains("89504E47")) {
                return TYPE_PNG;
            } else if (type.contains("47494638")) {
                return TYPE_GIF;
            } else if (type.contains("424D")) {
                return TYPE_BMP;
            }else{
                return TYPE_UNKNOWN;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    /**
     * 判断是否是图片
     */
    public static boolean isPic(MultipartFile[] files) throws IOException{
    	for (MultipartFile file : files) {
    		File tempFile = new File(file.getOriginalFilename());
    		FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
    		String fileType = PicUtil.getPicType(new FileInputStream(tempFile));
    		if (tempFile.exists()) {
    			tempFile.delete();
    		}
    		if(PicUtil.TYPE_UNKNOWN.equals(fileType)){
    			return false;
    		}
		}
    	return true;
    }
    
    public static void main(String[] args) throws IOException {
    	//System.out.println("格式： " + getPicType(new FileInputStream(new File("C:/Users/Administrator/Desktop/祖传手艺人要啥打啥.mp4"))));
    }
}
