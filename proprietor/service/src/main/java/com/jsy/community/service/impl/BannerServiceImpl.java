package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IBannerService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.BannerMapper;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.vo.BannerVO;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * banner轮播图 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-16
 */
@DubboService(version = Const.version, group = Const.group)
public class BannerServiceImpl extends ServiceImpl<BannerMapper, BannerEntity> implements IBannerService {
	
	@Autowired
	private BannerMapper bannerMapper;
	
	/**
	* @Description: 轮播图入库
	 * @Param: [bannerEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@Override
	public boolean addBanner(BannerEntity bannerEntity){
		int result = bannerMapper.insert(bannerEntity);
		if(result > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 轮播图 列表查询
	 * @Param: [bannerQO]
	 * @Return: java.util.List<com.jsy.community.vo.BannerVO>
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	public List<BannerVO> queryBannerList(BannerQO bannerQO){
		QueryWrapper<BannerEntity> queryWrapper = new QueryWrapper<BannerEntity>();
		queryWrapper.select("id,position,sort,url,description");
		queryWrapper.eq("community_id",bannerQO.getCommunityId());
		queryWrapper.eq("position",bannerQO.getPosition());
		queryWrapper.orderByAsc("sort");
		List<BannerEntity> entityList = bannerMapper.selectList(queryWrapper);
		List<BannerVO> returnList = new ArrayList<>(entityList.size());
		BannerVO bannerVO = null;
		for(BannerEntity bannerEntity : entityList){
			bannerVO = new BannerVO();
			BeanUtils.copyProperties(bannerEntity,bannerVO);
			returnList.add(bannerVO);
		}
		return returnList;
	}
	
	/**
	* @Description: 轮播图 批量删除
	 * @Param: [bannerQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@Override
	public boolean deleteBannerBatch(BannerQO bannerQO){
		if(CollectionUtils.isEmpty(bannerQO.getIds())){
			return false;
		}
		int result = bannerMapper.deleteBatchIds(bannerQO.getIds());
		if(result > 0){
			return true;
		}
		return false;
	}
	
}
