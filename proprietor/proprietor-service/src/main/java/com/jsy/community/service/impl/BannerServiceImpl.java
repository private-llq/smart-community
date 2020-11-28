package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IBannerService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.mapper.BannerMapper;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.returnVO.BannerRVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * banner轮播图 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-16
 */
@DubboService(version = Const.version, group = Const.group)
public class BannerServiceImpl extends ServiceImpl<BannerMapper, BannerEntity> implements IBannerService {
	
	@Autowired
	private BannerMapper bannerMapper;
	
	/**
	 * @Description: 轮播图 列表查询
	 * @Param: [bannerQO]
	 * @Return: java.util.List<com.jsy.community.returnVO.BannerRVO>
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	public List<BannerRVO> queryBannerList(BannerQO bannerQO){
		QueryWrapper<BannerEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id,position,sort,url,description");
		queryWrapper.eq("community_id",bannerQO.getCommunityId());
		queryWrapper.eq("position",bannerQO.getPosition());
		queryWrapper.orderByAsc("sort");
		List<BannerEntity> entityList = bannerMapper.selectList(queryWrapper);
		List<BannerRVO> returnList = new ArrayList<>(entityList.size());
		BannerRVO bannerRVO = null;
		for(BannerEntity bannerEntity : entityList){
			bannerRVO = new BannerRVO();
			BeanUtils.copyProperties(bannerEntity, bannerRVO);
			bannerRVO.setDesc(bannerEntity.getDescription());
			returnList.add(bannerRVO);
		}
		return returnList;
	}
	
}
