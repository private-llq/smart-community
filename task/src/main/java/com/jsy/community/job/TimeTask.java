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
 * @Description
 * @Version 1.0
 **/
public class TimeTask implements Job {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("开始执行任务");
		
		Set<String> menu_difference = redisTemplate.opsForSet().difference("menu_img_part", "menu_img_all");
		Set<String> car_difference = redisTemplate.opsForSet().difference("car_img_part", "car_img_all");
		Set<String> banner_difference = redisTemplate.opsForSet().difference("banner_img_part", "banner_img_all");
		Set<String> repair_difference = redisTemplate.opsForSet().difference("repair_img_part", "repair_img_all");
		Set<String> shop_difference = redisTemplate.opsForSet().difference("shop_img_part", "shop_img_all");
		Set<String> shop_comment_difference = redisTemplate.opsForSet().difference("repair_comment_img_part", "repair_comment_img_all");

		if (menu_difference != null) {
			// 2. 删除差值图片
			for (String s : menu_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("menu_img_all");
					redisTemplate.delete("menu_img_part"); // 删除redis
					System.out.println("删除的无用图片：" + s);
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
					System.out.println("删除的无用图片：" + s);
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
					System.out.println("删除的无用图片：" + s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		if (repair_difference != null) {
			for (String s : repair_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("repair_img_all");
					redisTemplate.delete("repair_img_part"); // 删除redis
					System.out.println("删除的无用图片：" + s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		if (shop_comment_difference != null) {
			for (String s : shop_comment_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("repair_comment_img_all");
					redisTemplate.delete("repair_comment_img_part"); // 删除redis
					System.out.println("删除的无用图片：" + s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		if (shop_difference != null) {
			for (String s : shop_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("shop_img_all");
					redisTemplate.delete("shop_img_part"); // 删除redis
					System.out.println("删除的无用图片：" + s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		
		
		Set<String> head_difference = redisTemplate.opsForSet().difference("shop_head_img_part", "shop_head_img_all");
		Set<String> middle_difference = redisTemplate.opsForSet().difference("shop_middle_img_part", "shop_middle_img_all");
		Set<String> other_difference = redisTemplate.opsForSet().difference("shop_other_img_part", "shop_other_img_all");
		if (head_difference != null) {
			// 2. 删除差值图片
			for (String s : head_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("shop_head_img_all");
					redisTemplate.delete("shop_head_img_part"); // 删除redis
					System.out.println("删除的无用图片：" + s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		if (middle_difference != null) {
			// 2. 删除差值图片
			for (String s : middle_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("shop_middle_img_all");
					redisTemplate.delete("shop_middle_img_part"); // 删除redis
					System.out.println("删除的无用图片：" + s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		if (other_difference != null) {
			// 2. 删除差值图片
			for (String s : other_difference) {
				try {
					MinioUtils.removeFile(s);
					redisTemplate.delete("shop_other_img_all");
					redisTemplate.delete("shop_other_img_part"); // 删除redis
					System.out.println("删除的无用图片：" + s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("删除失败");
				}
			}
		}
		System.out.println("没有可删除的图片");
	}
}
