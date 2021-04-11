package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.IBannerService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConsts;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.BannerMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * banner轮播图 服务实现类
 * @author qq459799974
 * @since 2020-11-16
 */
@DubboService(version = Const.version, group = Const.group_property)
public class BannerServiceImpl extends ServiceImpl<BannerMapper, BannerEntity> implements IBannerService {
	
	@Autowired
	private BannerMapper bannerMapper;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserService adminUserService;
	
	/**
	* @Description: 轮播图入库
	 * @Param: [bannerEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@Override
	public boolean addBanner(BannerEntity bannerEntity){
		if(bannerEntity.getPosition() == null){
			bannerEntity.setPosition(1);
		}
		bannerEntity.setId(SnowFlake.nextId());
		if(PropertyConsts.BANNER_PUB_TYPE_DRAFT.equals(bannerEntity.getPublishType())){
			bannerEntity.setStatus(null);//草稿无 发布/撤销 状态
		}else if(PropertyConsts.BANNER_PUB_TYPE_PUBLISH.equals(bannerEntity.getPublishType())){
			//发布需要排序，保存草稿不用
			List<Integer> sorts = bannerMapper.queryBannerSortByCommunityId(bannerEntity.getCommunityId());
			Integer sort = findSort(sorts);
			bannerEntity.setSort(sort);
			//设置发布时间、发布人、状态
			bannerEntity.setPublishTime(LocalDateTime.now());
			bannerEntity.setPublishBy(bannerEntity.getCreateBy());
			bannerEntity.setStatus(PropertyConsts.BANNER_STATUS_PUBLISH);
		}
		int result = bannerMapper.insert(bannerEntity);
		return result > 0;
	}
	
	//查找排序空位
	private Integer findSort(List<Integer> sorts){
		if(CollectionUtils.isEmpty(sorts)){
			return 1; //集合无元素(社区无轮播图) 返回首位 1
		}
		Collections.sort(sorts);
		Integer temp = sorts.get(0); //把集合中第一个(最小)元素置为被比较的初始数字
		for(Integer current : sorts){
			if(current.equals(temp)){
				temp = temp +1;
				continue;
			}
			return temp; //返回中间空位
		}
		return temp; //返回末位
	}
	
	/**
	 * @Description: 轮播图 分页查询
	 * @Param: [bannerQO]
	 * @Return: java.util.List<com.jsy.community.vo.BannerVO>
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@Override
	public PageInfo<BannerEntity> queryBannerPage(BaseQO<BannerEntity> baseQO){
		BannerEntity query = baseQO.getQuery();
		Page<BannerEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper<BannerEntity> queryWrapper = new QueryWrapper<>();
		if(query.getId() != null){ //查详情
			queryWrapper.select("id,title,url,type,content");
			queryWrapper.eq("id",query.getId());
		}else{
			if(PropertyConsts.BANNER_PUB_TYPE_DRAFT.equals(query.getPublishType())){ //查草稿
				queryWrapper.select("id,title,url,type,content,create_by,create_time");
				queryWrapper.orderByDesc("create_time");
			}else if(PropertyConsts.BANNER_PUB_TYPE_PUBLISH.equals(query.getPublishType())){ // 查已发布
				queryWrapper.select("id,title,url,content,click,sort,status,publish_by,publish_time,create_by,create_time,update_by,update_time");
				if(query.getPublishDateStart() != null){
					queryWrapper.ge("publish_time",query.getPublishDateStart());
				}
				if(query.getPublishDateEnd() != null){
					queryWrapper.le("publish_time",query.getPublishDateEnd());
				}
				if(query.getStatus() != null){
					queryWrapper.eq("status",query.getStatus());
				}
				queryWrapper.orderByDesc("status");
				queryWrapper.orderByAsc("sort");
			}
			if(query.getPosition() != null){
				queryWrapper.eq("position",query.getPosition());
			}
			if(!StringUtils.isEmpty(query.getTitle())){
				queryWrapper.like("title",query.getTitle());
			}
			if(!StringUtils.isEmpty(query.getContent())){
				queryWrapper.like("content",query.getContent());
			}
			if(query.getCreateDateStart() != null){
				queryWrapper.ge("create_time",query.getCreateDateStart());
			}
			if(query.getCreateDateEnd() != null){
				queryWrapper.le("create_time",query.getCreateDateEnd());
			}
			queryWrapper.eq("publish_type",query.getPublishType());
		}
		queryWrapper.eq("community_id",query.getCommunityId());
		Page<BannerEntity> pageData = bannerMapper.selectPage(page, queryWrapper);
		//补创建人和更新人和发布人姓名
		Set<String> createUidSet = new HashSet<>();
		Set<String> updateUidSet = new HashSet<>();
		Set<String> publishUidSet = new HashSet<>();
		for(BannerEntity bannerEntity : pageData.getRecords()){
			createUidSet.add(bannerEntity.getCreateBy());
			updateUidSet.add(bannerEntity.getUpdateBy());
			publishUidSet.add(bannerEntity.getPublishBy());
		}
		Map<String, Map<String,String>> createUserMap = adminUserService.queryNameByUidBatch(createUidSet);
		Map<String, Map<String,String>> updateUserMap = adminUserService.queryNameByUidBatch(updateUidSet);
		Map<String, Map<String,String>> publishUserMap = adminUserService.queryNameByUidBatch(publishUidSet);
		for(BannerEntity bannerEntity : pageData.getRecords()){
			bannerEntity.setCreateBy(createUserMap.get(bannerEntity.getCreateBy()) == null ? null : createUserMap.get(bannerEntity.getCreateBy()).get("name"));
			bannerEntity.setUpdateBy(updateUserMap.get(bannerEntity.getUpdateBy()) == null ? null : updateUserMap.get(bannerEntity.getUpdateBy()).get("name"));
			bannerEntity.setPublishBy(publishUserMap.get(bannerEntity.getPublishBy()) == null ? null : publishUserMap.get(bannerEntity.getPublishBy()).get("name"));
		}
		PageInfo<BannerEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
//	/**
//	* @Description: 轮播图 批量删除
//	 * @Param: [bannerQO]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2020/11/16
//	**/
//	@Override
//	public boolean deleteBannerBatch(Long[] ids){
//		int result = bannerMapper.deleteBatchIds(Arrays.asList(ids));
//		return result > 0;
//	}
	
	/**
	* @Description: 轮播图 删除
	 * @Param: [id,communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/11
	**/
	@Override
	public boolean delBanner(Long id, Long communityId){
		BannerEntity entity = bannerMapper.selectOne(new QueryWrapper<BannerEntity>().select("id,publish_type,status").eq("id", id).eq("community_id",communityId));
		if(entity == null){
			throw new PropertyException(JSYError.BAD_REQUEST.getCode(),"数据不存在");
		}
		//草稿或 已发布且已撤销 可以删除 , 已发布未撤销 不能删除
		if(PropertyConsts.BANNER_PUB_TYPE_PUBLISH.equals(entity.getPublishType())
			&& PropertyConsts.BANNER_STATUS_PUBLISH.equals(entity.getStatus())){
			throw new PropertyException(JSYError.BAD_REQUEST.getCode(),"发布中无法删除");
		}
		return bannerMapper.deleteById(id) == 1;
	}
	
	/**
	 * @Description: 轮播图 修改
	 * @Param: [bannerQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/29
	 **/
	@Override
	public boolean updateBanner(BannerQO bannerQO){
		BannerEntity entity = bannerMapper.selectOne(new QueryWrapper<BannerEntity>().select("id,publish_type").eq("id", bannerQO.getId()).eq("community_id",bannerQO.getCommunityId()));
		if(entity == null){
			throw new PropertyException(JSYError.BAD_REQUEST.getCode(),"数据不存在");
		}
		BannerEntity bannerEntity = new BannerEntity();
		bannerEntity.setStatus(bannerQO.getStatus()); //已发布中的撤销、重新发布操作
		if(PropertyConsts.BANNER_PUB_TYPE_DRAFT.equals(entity.getPublishType())){
			if(PropertyConsts.BANNER_PUB_TYPE_PUBLISH.equals(bannerQO.getPublishType())){
				//草稿 ==> 发布
				bannerEntity.setPublishBy(bannerQO.getOperator());
				bannerEntity.setPublishTime(LocalDateTime.now());
				bannerEntity.setStatus(PropertyConsts.BANNER_STATUS_PUBLISH); //不能直接把草稿发布为已撤销状态
			}else{
				//草稿无状态
				bannerEntity.setStatus(null);
//				bannerEntity.setUpdateBy(bannerQO.getOperator());//A1.修改草稿(不发布) 添加修改人
			}
		}else if(PropertyConsts.BANNER_PUB_TYPE_PUBLISH.equals(entity.getPublishType())){
			if(PropertyConsts.BANNER_PUB_TYPE_DRAFT.equals(bannerQO.getPublishType())){
				//发布 ==> 草稿
				throw new PropertyException(JSYError.BAD_REQUEST.getCode(),"已发布不能修改为草稿");
			}
//			bannerEntity.setUpdateBy(bannerQO.getOperator());//A2.修改已发布的 添加修改人
		}
		bannerEntity.setUpdateBy(bannerQO.getOperator());//B.凡是有udpate操作就添加修改人
		bannerEntity.setId(bannerQO.getId());
		bannerEntity.setTitle(bannerQO.getTitle());
		bannerEntity.setUrl(bannerQO.getUrl());
		bannerEntity.setType(bannerQO.getType());
		bannerEntity.setContent(bannerQO.getContent());
		bannerEntity.setPublishType(bannerQO.getPublishType());
		return bannerMapper.updateById(bannerEntity) == 1;
	}

}
