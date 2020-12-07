package com.jsy.community.job;

import com.jsy.community.utils.MinioUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

/**
 * @author lihao
 * @ClassName TimeTask
 * @Date 2020/12/4  14:52
 * @Description TODO
 * @Version 1.0
 **/
public class TimeTask implements Job {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("开始执行任务");
		// 1. 获取redis中存的图片名称
		Set<String> menu_img_all = redisTemplate.opsForSet().members("menu_img_all");
		Set<String> menu_img_part = redisTemplate.opsForSet().members("menu_img_part");
		Set<String> car_img_all = redisTemplate.opsForSet().members("car_img_all");
		Set<String> car_img_part = redisTemplate.opsForSet().members("car_img_part");
		
		Set<String> menu_difference = redisTemplate.opsForSet().difference("menu_img_part","menu_img_all");
		Set<String> car_difference = redisTemplate.opsForSet().difference("car_img_part","car_img_all");
		Set<String> banner_difference = redisTemplate.opsForSet().difference("banner_img_part","banner_img_all");
		
		if (menu_difference != null) {
			// 2. 删除差值图片
			for (String s : menu_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("menu_img_all");
					redisTemplate.delete("menu_img_part"); // 删除redis
					System.out.println("删除的无用图片："+s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		if (car_difference != null) {
			for (String s : car_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("car_img_all");
					redisTemplate.delete("car_img_part"); // 删除redis
					System.out.println("删除的无用图片："+s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		if (banner_difference != null) {
			for (String s : banner_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("banner_img_all");
					redisTemplate.delete("banner_img_part"); // 删除redis
					System.out.println("删除的无用图片："+s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		System.out.println("没有可删除的图片");
	}
}
