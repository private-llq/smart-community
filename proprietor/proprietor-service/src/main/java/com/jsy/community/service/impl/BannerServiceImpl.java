package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IBannerService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.mapper.BannerMapper;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.vo.BannerVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * banner轮播图 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-16
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class BannerServiceImpl extends ServiceImpl<BannerMapper, BannerEntity> implements IBannerService {
	
	@Autowired
	private BannerMapper bannerMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * @Description: 轮播图 列表查询
	 * @Param: [bannerQO]
	 * @Return: java.util.List<com.jsy.community.vo.BannerVO>
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	public List<BannerVO> queryBannerList(BannerQO bannerQO){
		QueryWrapper<BannerEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id,position,sort,url,description");
		queryWrapper.eq("community_id",bannerQO.getCommunityId());
		queryWrapper.eq("position",bannerQO.getPosition());
		queryWrapper.orderByAsc("sort");
		List<BannerEntity> entityList = bannerMapper.selectList(queryWrapper);
		List<BannerVO> returnList = new ArrayList<>(entityList.size());
		BannerVO bannerVO = null;
		for(BannerEntity bannerEntity : entityList){
			bannerVO = new BannerVO();
			BeanUtils.copyProperties(bannerEntity, bannerVO);
			bannerVO.setDesc(bannerEntity.getDescription());
			returnList.add(bannerVO);
		}

		return returnList;
	}
	
	/**
	 * @Description: 添加点击量到redis
	 * @Param: [id]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/30
	 **/
	@Override
	public void clickUp(Long id){
		redisTemplate.opsForHash().increment("Banner:clickCount",String.valueOf(id),1);
	}
	/**
	* @Description: 每5分组刷点击量到mysql
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/30
	**/
	@Scheduled(cron = "0 */1 * * * ?")
	public void refreshClickCount(){
		log.info("轮播图刷新点击量定时任务执行"+ LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
		Map<Long,Long> entries = redisTemplate.opsForHash().entries("Banner:clickCount");
		if(entries.size() > 0){
			redisTemplate.opsForHash().delete("Banner:clickCount",redisTemplate.opsForHash().keys("Banner:clickCount").toArray());
			bannerMapper.refreshClickCount(entries);
		}
	}
	
	
}
