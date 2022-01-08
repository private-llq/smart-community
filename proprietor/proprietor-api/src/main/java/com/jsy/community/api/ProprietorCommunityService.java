package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.ControlVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 社区接口
 * @since 2020-11-20 09:06
 **/
public interface ProprietorCommunityService extends IService<CommunityEntity> {
	/**
	* @Description: 社区查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	PageInfo<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO);

	/**
	 * 通过社区名称和城市id查询相关的社区数据 服务提供者
	 * @author YuLF
	 * @since  2020/11/23 11:21
	 * @Param  communityEntity 	必要参数实体
	 * @return 返回通过社区名称和城市id查询结果
	 */
    List<CommunityEntity> getCommunityByName(CommunityQO communityQO);
	
    /**
    * @Description: 小区定位
     * @Param: [uid, location]
     * @Return: com.jsy.community.entity.CommunityEntity
     * @Author: chq459799974
     * @Date: 2020/11/25
    **/
    CommunityEntity locateCommunity(String uid,Map<String,Double> location);


    /**
     * @Description: 小区定位
     * @author: Hu
     * @since: 2021/11/3 15:56
     * @Param:
	 */
	CommunityEntity locateCommunityV2(String uid, Map<String,Double> location);

	/**
	 * @author: Pipi
	 * @description: 附近的小区,20条数据
	 * @param location: 经纬度
	 * @return: {@link List< CommunityEntity>}
	 * @date: 2022/1/8 14:54
	 **/
	List<CommunityEntity> nearbyCommunity(Map<String, Double> location);
	
	/**
	 * @Description: 根据社区id批量查询社区名
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	 **/
	Map<String,Map<String,Object>> queryCommunityNameByIdBatch(Collection<Long> ids);
	
	/**
	* @Description: id单查社区
	 * @Param: [id]
	 * @Return: com.jsy.community.entity.CommunityEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	CommunityEntity queryCommunityById(Long id);
	
	/**
	 * @Description: 查询所有小区id
	 * @Param: []
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/6/26
	 **/
	List<Long> queryAllCommunityIdList();

	/**
	 * @Description: 查询当前小区的物业公司
	 * @author: Hu
	 * @since: 2021/8/20 15:12
	 * @Param:
	 * @return:
	 */
	PropertyCompanyEntity getCompany(Long id);


	/**
	 * @Description: 查询家属或者租户或对应的小区
	 * @author: Hu
	 * @since: 2021/9/23 9:17
	 * @Param:
	 * @return:
	 */
	CommunityEntity getCommunity(ControlVO controlVO);
}
