package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.utils.PageInfo;
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
	* @Description: 轮播图分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.BannerEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/10
	**/
	PageInfo<BannerEntity> queryBannerPage(BaseQO<BannerEntity> baseQO);
	
	/**
	* @Description: 轮播图 发布中列表查询(拖动排序用)
	 * @Param: [communityId]
	 * @Return: java.util.List<com.jsy.community.entity.BannerEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	List<BannerEntity> queryBannerListOnShowByCommunityId(Long communityId);
	
//	/**
//	* @Description: 轮播图 批量删除
//	 * @Param: [bannerQO]
//	 * @Return: boolean
//	 * @Author: chq45799974
//	 * @Date: 2020/11/16
//	**/
//	boolean deleteBannerBatch(Long[] ids);
	
	/**
	* @Description: 轮播图 删除
	 * @Param: [id,communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/11
	**/
	boolean delBanner(Long id, Long communityId);
	
	/**
	* @Description: 轮播图 修改
	 * @Param: [bannerQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/29
	**/
	boolean updateBanner(BannerQO bannerQO);
	
	/**
	* @Description: 轮播图 修改排序
	 * @Param: [idList, communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	boolean changeSorts(List<Long> idList,Long communityId);

}
