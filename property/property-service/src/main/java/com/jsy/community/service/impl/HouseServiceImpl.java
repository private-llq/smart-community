package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 社区楼栋 服务实现类
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-20
 */
@DubboService(version = Const.version, group = Const.group_property)
public class HouseServiceImpl extends ServiceImpl<HouseMapper, HouseEntity> implements IHouseService {

	@Autowired
	private HouseMapper houseMapper;
	
	/**
	 * @Description: 查询子级楼栋(单元/楼层/房间等)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	 **/
	public PageInfo<HouseEntity> queryHousePage(BaseQO<HouseQO> baseQO){
		Page<HouseEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO); //设置分页参数
		QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id,code,community_id,building,unit,floor,door,pid,type,comment");
		HouseQO query = baseQO.getQuery();
		if(query.getId() == null){
			return null;
		}
		queryWrapper.eq("pid",query.getId());
//		queryWrapper.eq("community_id",query.getCommunityId());
		Page<HouseEntity> houseEntityPage = houseMapper.selectPage(page, queryWrapper);
		PageInfo<HouseEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(houseEntityPage,pageInfo);
		return pageInfo;
	}
	
	/**
	 * @Description: 新增楼栋(单元/楼层/房间等)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	 **/
	public boolean addHouse(HouseEntity houseEntity){
		if(Const.HouseTypeConsts.DOOR.equals(houseEntity.getType())){
			houseEntity.setCode(UUID.randomUUID().toString().replace("-",""));
		}
		houseEntity.setId(SnowFlake.nextId());
		int result = houseMapper.addHouse(houseEntity);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 删除楼栋(单元/楼层/房间等)
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	 **/
	public boolean deleteHouse(Long id){
		//TODO 级联删除下级 or 存在下级不允许删除？暂时级联下级
		List<Long> idList = new LinkedList<>(); // 级联出的要删除的id
		idList.add(id);
		List<Long> subIdList = houseMapper.getSubIdList(Arrays.asList(id));
		setDeleteIds(idList, subIdList);
		int result = houseMapper.deleteBatchIds(idList);
		if(result > 0){
			return true;
		}
		return false;
	}

	//组装需要删除的数据
	private void setDeleteIds(List<Long> idList, List<Long> subIdList) {
		if(!CollectionUtils.isEmpty(subIdList)){
			subIdList.removeAll(idList);
			idList.addAll(subIdList);
			setDeleteIds(idList,houseMapper.getSubIdList(subIdList));
		}
	}
	
	/**
	 * @Description: 修改楼栋(单元/楼层/房间等)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	 **/
	public boolean updateHouse(HouseEntity houseEntity){
		//只有当前类型(type)对应的名称和备注能修改
		houseEntity.setCommunityId(null);
		houseEntity.setCode(null);
		houseEntity.setPid(null);
		switch (houseEntity.getType()){
			case 2:
				houseEntity.setBuilding(null);
				houseEntity.setFloor(null);
				houseEntity.setDoor(null);
				break;
			case 3:
				houseEntity.setBuilding(null);
				houseEntity.setUnit(null);
				houseEntity.setDoor(null);
				break;
			case 4:
				houseEntity.setBuilding(null);
				houseEntity.setUnit(null);
				houseEntity.setFloor(null);
				break;
		}
		int result = houseMapper.update(houseEntity,new QueryWrapper<HouseEntity>().eq("id",houseEntity.getId()).eq("type",houseEntity.getType()));
		if(result == 1){
			return true;
		}
		return false;
	}

	/**
	 * 通过社区ID查出所有 楼栋、单元、楼层、未被登记的门牌
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @Param  communityId	社区ID
	 */
	@Override
	public List<HouseEntity> getCommunityArchitecture(long communityId) {
		return houseMapper.getCommunityArchitecture(communityId);
	}
	
	
	/**
	 * 按社区ID获取 社区名称和 当前社区住户房间数量
	 * @author YuLF
	 * @since  2020/12/3 11:06
	 * @Param  communityId   社区id
	 * @return				 返回社区名称和 当前社区住户房间数量
	 */
	@Override
	public Map<String, Object> getCommunityNameAndUserAmountById(long communityId) {
		return houseMapper.getCommunityNameAndUserAmountById(communityId);
	}

	/**
	 * 按社区ID获取 社区名称 社区用户名和社区用户uid
	 * @author YuLF
	 * @since  2020/12/7 11:06
	 * @param communityId 			社区id
	 * @return						返回社区名称和 当前社区所有住户名称，住户uid
	 */
	@Override
	public List<UserEntity> getCommunityNameAndUserInfo(long communityId) {
		return houseMapper.getCommunityNameAndUserInfo(communityId);
	}

}
