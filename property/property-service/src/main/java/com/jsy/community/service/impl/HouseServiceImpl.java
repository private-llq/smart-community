package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
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
import org.springframework.util.StringUtils;

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
	
	@Autowired
	private ICommunityService communityService;
	
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
	 * 新增楼栋入参检查和处理
	 */
	private void checkAndDealParams(HouseEntity houseEntity){
		
		//根据type校验参数
		switch (houseEntity.getType()){
			case BusinessConst.BUILDING_TYPE_BUILDING :
				if(StringUtils.isEmpty(houseEntity.getBuilding())){
					throw new PropertyException("楼栋名称不能为空");
				}
				houseEntity.setFloor("");
				houseEntity.setDoor("");
				break;
			case BusinessConst.BUILDING_TYPE_UNIT :
				if(StringUtils.isEmpty(houseEntity.getUnit())){
					throw new PropertyException("单元名称不能为空");
				}
				houseEntity.setFloor("");
				houseEntity.setDoor("");
				break;
			case BusinessConst.BUILDING_TYPE_FLOOR :
				if(StringUtils.isEmpty(houseEntity.getFloor())){
					throw new PropertyException("楼层名称不能为空");
				}
				houseEntity.setDoor("");
				break;
			case BusinessConst.BUILDING_TYPE_DOOR :
				if(StringUtils.isEmpty(houseEntity.getDoor())){
					throw new PropertyException("房间名称不能为空");
				}
		}
	}
	
	/**
	 * @Description: 新增楼栋(单元/楼层/房间等)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	 **/
	public boolean addHouse(HouseEntity houseEntity){
		
		//查询社区模式
		Integer communityMode = communityService.getCommunityMode(houseEntity.getCommunityId());
		if(communityMode == null || communityMode < 1 || communityMode > 4){
			log.error("社区不存在或社区模式错误：" + String.valueOf(communityMode) + " 社区id：" + houseEntity.getCommunityId());
			throw new PropertyException("社区不存在或社区模式数据有误，请联系管理员");
		}
		
		//检查处理入参
		checkAndDealParams(houseEntity);
		
		//根据社区模式判断是否是顶级 如果是顶级pid置为0
		if(( (BusinessConst.COMMUNITY_MODE_BUILDING_UNIT.equals(communityMode) || BusinessConst.COMMUNITY_MODE_BUILDING.equals(communityMode))
			&& BusinessConst.BUILDING_TYPE_BUILDING == houseEntity.getType())
			|| ( (BusinessConst.COMMUNITY_MODE_UNIT_BUILDING.equals(communityMode) || BusinessConst.COMMUNITY_MODE_UNIT.equals(communityMode))
			&& BusinessConst.BUILDING_TYPE_UNIT == houseEntity.getType())
		){
			houseEntity.setPid(0L);
		}else if(houseEntity.getPid() == 0){
			throw new PropertyException("非顶级单位pid不能为0");
		}
		
		//设置id和保存
		houseEntity.setId(SnowFlake.nextId());
		int result;
		if(houseEntity.getPid() == 0L){
			result = houseMapper.insert(houseEntity);
		}else{
			//检查pid是否存在、社区是否相同、层级是否是新增目标上一级
			HouseEntity parentHouse = houseMapper.selectOne(new QueryWrapper<HouseEntity>().select("community_id,type").eq("id", houseEntity.getPid()));
			if(parentHouse == null){
				throw new PropertyException("父级单位不存在，新增失败");
			}else if(!houseEntity.getCommunityId().equals(parentHouse.getCommunityId())){
				throw new PropertyException("父级单位非本小区，新增失败");
			}else{
				checkLevelRelation(communityMode,houseEntity.getType(),parentHouse.getType());
			}
			//若类型是房间，生成唯一code
			if(BusinessConst.BUILDING_TYPE_DOOR == houseEntity.getType()){
				houseEntity.setCode(UUID.randomUUID().toString().replace("-",""));
			}
			result = houseMapper.addSub(houseEntity);
		}
		return result == 1;
	}
	
	/**
	 * 检查层级关系(不包含顶级)
	 */
	private void checkLevelRelation(Integer communityMode, Integer type, Integer pType){
		//新增房间，直接判断与楼层关系
		if(BusinessConst.BUILDING_TYPE_DOOR == type){
			checkCommonType(type,pType);
			return;
		}
		//社区模式
		switch (communityMode.intValue()){
			//社区模式1 层级1 2 3 4  直接判断与楼层关系
			case 1:
				checkCommonType(type,pType);
				break;
			//社区模式2 层级2 1 3 4  除去顶级和末级，只剩层级1 3
			case 2:
				if(BusinessConst.BUILDING_TYPE_BUILDING == type){
					if(pType - type != 1){
						throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
					}
				}else if(BusinessConst.BUILDING_TYPE_FLOOR == type){
					if(type - pType != 2){
						throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
					}
				}
				break;
			//社区模式3 层级1 3 4  除去顶级和末级，只剩层级3
			case 3:
				if(type - pType != 2){
					throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
				}
				break;
			//社区模式4 层级2 3 4  直接判断关系
			case 4:
				checkCommonType(type,pType);
		}
	}
	
	/**
	 * 新增楼层和房间通用type校检
	 */
	private void checkCommonType(Integer type, Integer pType){
		if(type - pType != 1){
			throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
		}
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
		int result = houseMapper.update(houseEntity,new QueryWrapper<HouseEntity>()
			.eq("id",houseEntity.getId())
			.eq("type",houseEntity.getType()));
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
