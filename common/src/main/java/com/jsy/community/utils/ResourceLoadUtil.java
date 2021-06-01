package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author chq459799974
 * @description 加载静态资源文件等 可封装不同返回格式
 * @since 2021-02-24 10:50
 **/
@Slf4j
public class ResourceLoadUtil {
	
	//linux文件绝对路径
	private static final String OS_LINUX_PATH = "/mnt/db/smart-community/file/";
	
	//项目模块绝对路径
	public static String getClassesPath() {
		return ResourceLoadUtil.class.getResource("/").getPath().replaceFirst("/","");
	}
	
	public static JSONObject loadJSONResource(String templateName){
		try {
			FileReader fileInputStream;
			if(System.getProperty("os.name").startsWith("Win")){
				fileInputStream = new FileReader(new File(getClassesPath() + templateName));
//				fileInputStream = new FileReader(new File("D:/" + templateName));
			}else{
				fileInputStream = new FileReader(new File(OS_LINUX_PATH + templateName));
			}
			BufferedReader reader = new BufferedReader(fileInputStream);
			StringBuffer sb = new StringBuffer();
			String str;
			while((str = reader.readLine()) != null){
				sb.append(str);
			}
			return JSONObject.parseObject(sb.toString());
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(loadJSONResource("sys_default_content.json").getString("avatar"));
	}
}
