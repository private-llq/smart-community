package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.vo.BannerVO;

import java.util.List;

/**
 * <p>
 * banner轮播图 服务类
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-16
 */
public interface IBannerService extends IService<BannerEntity> {
	/**
	 * @Description: 轮播图入库
	 * @Param: [bannerEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	boolean addBanner(BannerEntity bannerEntity);
	/**
	* @Description: 轮播图 列表查询
	 * @Param: [bannerQO]
	 * @Return: java.util.List<com.jsy.community.vo.BannerVO>
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	List<BannerVO> queryBannerList(BannerQO bannerQO);
	
	/**
	* @Description: 轮播图 批量删除
	 * @Param: [bannerQO]
	 * @Return: boolean
	 * @Author: chq45799974
	 * @Date: 2020/11/16
	**/
	boolean deleteBannerBatch(Long[] ids);
}
