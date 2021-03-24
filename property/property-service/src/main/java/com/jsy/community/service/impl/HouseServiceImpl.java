package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PropertyEnum;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.utils.CommonUtils;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.property.ProprietorVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

	private static final Map<String,String> RELATION_MSG_MAP = new HashMap<>(){{
		put("proprietor","业主信息");
		put("userHouse","APP业主房屋板块");
		put("houseMember","房间成员");
		put("houseLease","APP租房板块");
	}};
	
	@Autowired
	private HouseMapper houseMapper;
	
	@Autowired
	private ICommunityService communityService;
	
//	/**
//	 * @Description: 查询子级楼栋(单元/楼层/房间等)
//	 * @Param: [baseQO]
//	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>>
//	 * @Author: chq459799974
//	 * @Date: 2020/11/20
//	 **/
//	public PageInfo<HouseEntity> queryHousePage(BaseQO<HouseQO> baseQO){
//		Page<HouseEntity> page = new Page<>();
//		MyPageUtils.setPageAndSize(page, baseQO); //设置分页参数
//		QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.select("id,code,community_id,building,unit,floor,door,pid,type,comment");
//		HouseQO query = baseQO.getQuery();
//		if(query.getId() == null){
//			return null;
//		}
//		queryWrapper.eq("pid",query.getId());
////		queryWrapper.eq("community_id",query.getCommunityId());
//		Page<HouseEntity> houseEntityPage = houseMapper.selectPage(page, queryWrapper);
//		PageInfo<HouseEntity> pageInfo = new PageInfo<>();
//		BeanUtils.copyProperties(houseEntityPage,pageInfo);
//		return pageInfo;
//	}
	
//	/**
//	 * 新增楼栋入参检查和处理
//	 */
//	private void checkAndDealParams(HouseEntity houseEntity){
//
//		//根据type校验参数
//		switch (houseEntity.getType()){
//			case BusinessConst.BUILDING_TYPE_BUILDING :
//				if(StringUtils.isEmpty(houseEntity.getBuilding())){
//					throw new PropertyException("楼栋名称不能为空");
//				}
//				houseEntity.setFloor("");
//				houseEntity.setDoor("");
//				break;
//			case BusinessConst.BUILDING_TYPE_UNIT :
//				if(StringUtils.isEmpty(houseEntity.getUnit())){
//					throw new PropertyException("单元名称不能为空");
//				}
//				houseEntity.setFloor("");
//				houseEntity.setDoor("");
//				break;
//			case BusinessConst.BUILDING_TYPE_FLOOR :
//				if(StringUtils.isEmpty(houseEntity.getFloor())){
//					throw new PropertyException("楼层名称不能为空");
//				}
//				houseEntity.setDoor("");
//				break;
//			case BusinessConst.BUILDING_TYPE_DOOR :
//				if(StringUtils.isEmpty(houseEntity.getDoor())){
//					throw new PropertyException("房间名称不能为空");
//				}
//		}
//	}
	
//	/**
//	 * @Description: 新增楼栋（老）（单元/楼层/房间等）
//	 * @Param: [houseEntity]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2020/11/20
//	 **/
//	public boolean addHouse2(HouseEntity houseEntity){
//
//		//查询社区模式
//		Integer communityMode = communityService.getCommunityMode(houseEntity.getCommunityId());
//		if(communityMode == null || communityMode < 1 || communityMode > 4){
//			log.error("社区不存在或社区模式错误：" + String.valueOf(communityMode) + " 社区id：" + houseEntity.getCommunityId());
//			throw new PropertyException("社区不存在或社区模式数据有误，请联系管理员");
//		}
//
//		//检查处理入参
//		checkAndDealParams(houseEntity);
//
//		//根据社区模式判断是否是顶级 如果是顶级pid置为0
//		if(( (BusinessConst.COMMUNITY_MODE_BUILDING_UNIT.equals(communityMode) || BusinessConst.COMMUNITY_MODE_BUILDING.equals(communityMode))
//			&& BusinessConst.BUILDING_TYPE_BUILDING == houseEntity.getType())
//			|| ( (BusinessConst.COMMUNITY_MODE_UNIT_BUILDING.equals(communityMode) || BusinessConst.COMMUNITY_MODE_UNIT.equals(communityMode))
//			&& BusinessConst.BUILDING_TYPE_UNIT == houseEntity.getType())
//		){
//			houseEntity.setPid(0L);
//		}else if(houseEntity.getPid() == 0){
//			throw new PropertyException("非顶级单位pid不能为0");
//		}
//
//		//设置id和保存
//		houseEntity.setId(SnowFlake.nextId());
//		int result;
//		if(houseEntity.getPid() == 0L){
//			result = houseMapper.insert(houseEntity);
//		}else{
//			//检查pid是否存在、社区是否相同、层级是否是新增目标上一级
//			HouseEntity parentHouse = houseMapper.selectOne(new QueryWrapper<HouseEntity>().select("community_id,type").eq("id", houseEntity.getPid()));
//			if(parentHouse == null){
//				throw new PropertyException("父级单位不存在，新增失败");
//			}else if(!houseEntity.getCommunityId().equals(parentHouse.getCommunityId())){
//				throw new PropertyException("父级单位非本小区，新增失败");
//			}else{
//				checkLevelRelation(communityMode,houseEntity.getType(),parentHouse.getType());
//			}
//			//若类型是房间，生成唯一code
//			if(BusinessConst.BUILDING_TYPE_DOOR == houseEntity.getType()){
//				houseEntity.setCode(UUID.randomUUID().toString().replace("-",""));
//			}
//			result = houseMapper.addSub(houseEntity);
//		}
//		return result == 1;
//	}
	
//	/**
//	 * 检查层级关系(不包含顶级)
//	 */
//	private void checkLevelRelation(Integer communityMode, Integer type, Integer pType){
//		//新增房间，直接判断与楼层关系
//		if(BusinessConst.BUILDING_TYPE_DOOR == type){
//			checkCommonType(type,pType);
//			return;
//		}
//		//社区模式
//		switch (communityMode.intValue()){
//			//社区模式1 层级1 2 3 4  直接判断与楼层关系
//			case 1:
//				checkCommonType(type,pType);
//				break;
//			//社区模式2 层级2 1 3 4  除去顶级和末级，只剩层级1 3
//			case 2:
//				if(BusinessConst.BUILDING_TYPE_BUILDING == type){
//					if(pType - type != 1){
//						throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
//					}
//				}else if(BusinessConst.BUILDING_TYPE_FLOOR == type){
//					if(type - pType != 2){
//						throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
//					}
//				}
//				break;
//			//社区模式3 层级1 3 4  除去顶级和末级，只剩层级3
//			case 3:
//				if(type - pType != 2){
//					throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
//				}
//				break;
//			//社区模式4 层级2 3 4  直接判断关系
//			case 4:
//				checkCommonType(type,pType);
//		}
//	}
//
//	/**
//	 * 新增楼层和房间通用type校检
//	 */
//	private void checkCommonType(Integer type, Integer pType){
//		if(type - pType != 1){
//			throw new PropertyException("父级单位与新增对象层级关系不对，新增失败");
//		}
//	}
	
//	/**
//	 * @Description: 删除楼栋(单元/楼层/房间等)
//	 * @Param: [id]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2020/11/20
//	 **/
//	public boolean deleteHouse(Long id){
//		//TODO 级联删除下级 or 存在下级不允许删除？暂时级联下级
//		List<Long> idList = new LinkedList<>(); // 级联出的要删除的id
//		idList.add(id);
//		List<Long> subIdList = houseMapper.getSubIdList(Arrays.asList(id));
//		setDeleteIds(idList, subIdList);
//		int result = houseMapper.deleteBatchIds(idList);
//		if(result > 0){
//			return true;
//		}
//		return false;
//	}
//
//	//组装需要删除的数据
//	private void setDeleteIds(List<Long> idList, List<Long> subIdList) {
//		if(!CollectionUtils.isEmpty(subIdList)){
//			subIdList.removeAll(idList);
//			idList.addAll(subIdList);
//			setDeleteIds(idList,houseMapper.getSubIdList(subIdList));
//		}
//	}
	
//	/**
//	 * @Description: 修改楼栋(单元/楼层/房间等)
//	 * @Param: [houseEntity]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2020/11/20
//	 **/
//	public boolean updateHouse(HouseEntity houseEntity){
//		//只有当前类型(type)对应的名称和备注能修改
//		houseEntity.setCommunityId(null);
//		houseEntity.setCode(null);
//		houseEntity.setPid(null);
//		switch (houseEntity.getType()){
//			case 2:
//				houseEntity.setBuilding(null);
//				houseEntity.setFloor(null);
//				houseEntity.setDoor(null);
//				break;
//			case 3:
//				houseEntity.setBuilding(null);
//				houseEntity.setUnit(null);
//				houseEntity.setDoor(null);
//				break;
//			case 4:
//				houseEntity.setBuilding(null);
//				houseEntity.setUnit(null);
//				houseEntity.setFloor(null);
//				break;
//		}
//		int result = houseMapper.update(houseEntity,new QueryWrapper<HouseEntity>()
//			.eq("id",houseEntity.getId())
//			.eq("type",houseEntity.getType()));
//		if(result == 1){
//			return true;
//		}
//		return false;
//	}
	
	//========================================= 基础增删改查 开始 ========================================================
	/**
	 * @Description: 新增楼栋、单元、房屋（改）(根据物业端原型)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/8
	 **/
	@Transactional(rollbackFor = Exception.class)
	public boolean addHouse(HouseEntity houseEntity){
		int addResult = 0;
		//生成id
		long id = SnowFlake.nextId();
		houseEntity.setId(id);
		if(BusinessConst.BUILDING_TYPE_BUILDING == houseEntity.getType()){
			//设置对应名称
			houseEntity.setBuilding(houseEntity.getName());
			//设置顶级pid
			houseEntity.setPid(0L);
			//处理子级单元列表
			if(!CollectionUtils.isEmpty(houseEntity.getUnitIdList())){
				//绑定单元 修改单元的pid,building字段
				houseMapper.unitBindBuilding(houseEntity.getUnitIdList(), houseEntity);
			}
			//直接新增
			addResult = houseMapper.insert(houseEntity);
		}else if(BusinessConst.BUILDING_TYPE_UNIT == houseEntity.getType()){
			//设置对应名称
			houseEntity.setUnit(houseEntity.getName());
			//设置顶级pid 单元新增时是pid是0，若新增楼栋绑定了该单元，单元pid置为楼栋id
			houseEntity.setPid(0L);
			//直接新增
			addResult = houseMapper.insert(houseEntity);
		}else if(BusinessConst.BUILDING_TYPE_DOOR == houseEntity.getType()){
			//查询父级是否存在
			Integer pidExists = houseMapper.selectCount(new QueryWrapper<HouseEntity>()
				.eq("id", houseEntity.getPid())
				.eq("community_id", houseEntity.getCommunityId())
				.and(wrapper -> wrapper.eq("type",BusinessConst.BUILDING_TYPE_BUILDING).or().eq("type",BusinessConst.BUILDING_TYPE_UNIT))
			);
			// queryWrapper.and(wrapper -> wrapper.like("unit",query.getUnit()).or().like("number",query.getUnit()));
			if(pidExists != 1){
				throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"楼栋/单元 不存在");
			}
			houseEntity.setDoor(houseEntity.getNumber()); //TODO 后期可删除，但需要先修改依赖了t_house表door字段的代码
			houseEntity.setCode(UUID.randomUUID().toString().replace("-",""));
			addResult = houseMapper.addRoom(houseEntity);
		}
		return addResult == 1;
	}
	
	/**
	* @Description: 修改楼栋、单元、房屋（改）(根据物业端原型)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/9
	**/
	@Transactional(rollbackFor = Exception.class)
	public boolean updateHouse(HouseEntity houseEntity){
		//判断类型
		HouseEntity entity = houseMapper.queryHouseByAnyProperty("id", houseEntity.getId());
		if(entity == null){
			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"数据不存在");
		}
		//置空不能自行修改项(条件修改里面有的)
		houseEntity.setBuilding(null);
		houseEntity.setUnit(null);
		houseEntity.setDoor(null);
		//若类型为 楼栋 ，绑定单元做全删权增
		if(BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()){
			//禁止手动修改楼栋pid
			houseEntity.setPid(null);
			//解绑原有单元
			houseMapper.unitUnBindBuilding(houseEntity.getId());
			//绑定新单元列表
			if(!CollectionUtils.isEmpty(houseEntity.getUnitIdList())){
				houseMapper.unitBindBuilding(houseEntity.getUnitIdList(),entity);
			}
			//若楼栋名称有修改，同步修改单元和房屋冗余的楼栋名称
			if(!StringUtils.isEmpty(houseEntity.getName())){
				//设置对应名称
				entity.setBuilding(houseEntity.getName());
				houseEntity.setBuilding(houseEntity.getName());
				//查询子级单元id，子级房屋id，整合到一起
				List<Long> subUnitIdList = houseMapper.getSubIdList(Arrays.asList(houseEntity.getId()));
				if(!CollectionUtils.isEmpty(subUnitIdList)){
					List<Long> subRoomIdList = houseMapper.getSubIdList(subUnitIdList);
					subUnitIdList.addAll(subRoomIdList);
					//同步修改子节点
					houseMapper.updateSub(subUnitIdList,entity);
				}
			}
		//若类型为 单元，且单元名称有修改，同步修改房屋冗余的单元名称
		}else if(BusinessConst.BUILDING_TYPE_UNIT == entity.getType()){
			//禁止手动修改单元pid
			houseEntity.setPid(null);
			if(!StringUtils.isEmpty(houseEntity.getName())){
				//设置对应名称
				entity.setUnit(houseEntity.getName());
				houseEntity.setUnit(houseEntity.getName());
				//查询子级房屋id
				List<Long> subRoomIdList = houseMapper.getSubIdList(Arrays.asList(houseEntity.getId()));
				if(!CollectionUtils.isEmpty(subRoomIdList)){
					//同步修改子节点
					houseMapper.updateSub(subRoomIdList,entity);
				}
			}
		}
		return houseMapper.updateHouse(houseEntity) == 1;
	}
	
	/**
	* @Description: 查询楼栋、单元、房屋（改）(根据物业端原型)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/12
	**/
	public PageInfo<HouseEntity> queryHouse(BaseQO<HouseQO> baseQO){
		HouseQO query = baseQO.getQuery();
		Page<HouseEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("type",query.getType());
		queryWrapper.eq("community_id",query.getCommunityId());
		setQueryWrapper(queryWrapper,query);
		//是否查详情
		if(query.getId() != null){
			queryWrapper.eq("id",query.getId());
		}
		//是否有电梯
		if(query.getHasElevator() != null){
			queryWrapper.eq("has_elevator",query.getHasElevator());
		}
		//楼栋和单元均不为空 只取单元id
		if(query.getBuildingId() != null && query.getUnitId() != null){
			queryWrapper.eq("pid",query.getUnitId());
		}else if(query.getBuildingId() != null && 0L != query.getBuildingId()){  //楼栋不为空，单元空  取楼栋id及其所有子级单元id
			List<Long> pidList = new ArrayList<>();
			//房屋直接挂楼栋
			pidList.add(query.getBuildingId());
			//房屋挂单元，查询单元idList并添加
			List<Long> unitIdList = houseMapper.getSubIdList(Arrays.asList(query.getBuildingId()));
			if(!CollectionUtils.isEmpty(unitIdList)){
				pidList.addAll(unitIdList);
			}
			queryWrapper.in("pid",pidList);
		}else if(query.getUnitId() != null){  //单元不为空，楼栋空 只取单元id
			queryWrapper.eq("pid",query.getUnitId());
		}
		queryWrapper.orderByDesc("create_time");
		Page<HouseEntity> pageData = houseMapper.selectPage(page, queryWrapper);
		if(!CollectionUtils.isEmpty(pageData.getRecords())){
			//查询类型为楼栋，再查出已绑定单元数
			if(BusinessConst.BUILDING_TYPE_BUILDING == query.getType()){
				List<Long> paramList = new ArrayList<>();
				for(HouseEntity houseEntity : pageData.getRecords()){
					paramList.add(houseEntity.getId());
				}
				Map<Long, Map<String,Long>> bindMap = houseMapper.queryBindUnitCountBatch(paramList);
				for(HouseEntity houseEntity : pageData.getRecords()){
					Map<String, Long> countMap = bindMap.get(houseEntity.getId());
					houseEntity.setBindUnitCount(countMap != null ? countMap.get("count") : null);
				}
				//若查详情，查出已绑定单元id
				if(query.getId() != null){
					CollectionUtils.firstElement(pageData.getRecords()).setUnitIdList(houseMapper.queryBindUnitList(query.getId()));
				}
			}
			//查询类型为房屋，设置房屋类型、房产类型、装修情况、户型
			else if(BusinessConst.BUILDING_TYPE_DOOR == query.getType()){
				for(HouseEntity houseEntity : pageData.getRecords()){
					houseEntity.setHouseTypeStr(PropertyEnum.HouseTypeEnum.HOUSE_TYPE_MAP.get(houseEntity.getHouseType()));
					houseEntity.setPropertyTypeStr(PropertyEnum.PropertyTypeEnum.PROPERTY_TYPE_MAP.get(houseEntity.getPropertyType()));
					houseEntity.setDecorationStr(PropertyEnum.DecorationEnum.DECORATION_MAP.get(houseEntity.getDecoration()));
					if("00000000".equals(houseEntity.getHouseTypeCode())){
						houseEntity.setHouseTypeCodeStr("单间配套");
						continue;
					}
					setHouseTypeCodeStr(houseEntity);
				}
			}
		}
		PageInfo<HouseEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
	//设置通用查询条件
	private void setQueryWrapper(QueryWrapper<HouseEntity> queryWrapper,HouseQO query){
//		if(!StringUtils.isEmpty(query.getNumber())){
//			queryWrapper.like("number",query.getNumber());
//		}
		if(!StringUtils.isEmpty(query.getBuilding())){
			queryWrapper.and(wrapper -> wrapper.like("building",query.getBuilding()).or().like("number",query.getBuilding()));
		}
		if(!StringUtils.isEmpty(query.getUnit())){
			queryWrapper.and(wrapper -> wrapper.like("unit",query.getUnit()).or().like("number",query.getUnit()));
		}
		if(!StringUtils.isEmpty(query.getDoor())){
			queryWrapper.and(wrapper -> wrapper.like("door",query.getDoor()).or().like("number",query.getDoor()));
		}
	}
	
	//设置户型
	private void setHouseTypeCodeStr(HouseEntity houseEntity){
		String houseTypeCode = houseEntity.getHouseTypeCode();
		if(houseTypeCode.length() != 8){
			houseEntity.setHouseTypeCodeStr(houseEntity.getHouseTypeCode());
			return;
		}
		String bedRoom = Integer.valueOf(houseTypeCode.substring(0, 2)) + "室";
		String livingRoom = Integer.valueOf(houseTypeCode.substring(2, 4)) + "厅";
		String kitchen = Integer.valueOf(houseTypeCode.substring(4, 6)) + "厨";
		String toilet = Integer.valueOf(houseTypeCode.substring(6, 8)) + "卫";
		houseEntity.setHouseTypeCodeStr(bedRoom.concat(livingRoom).concat(kitchen).concat(toilet));
	}
	
	/**
	* @Description: 删除楼栋/单元/房屋
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/15
	**/
	public boolean deleteHouse(Long id){
		HouseEntity entity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("id",id));
		if(entity == null){
			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"数据不存在");
		}
		//删除楼栋/单元(有下级依赖不允许删除)
		//删除房屋(有依赖数据不允许删除)
		if(BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()
			|| BusinessConst.BUILDING_TYPE_UNIT == entity.getType()){
			Integer count = houseMapper.selectCount(new QueryWrapper<HouseEntity>().eq("pid", id));
			if(count > 0){
				throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"存在下级单元或房屋，不允许删除");
			}
		}else if(BusinessConst.BUILDING_TYPE_DOOR == entity.getType()){
			List<Map<String,Object>> countList = houseMapper.verifyRelevance(id);
			List<String> errorList = new ArrayList<>(countList.size());
			for(Map<String,Object> map : countList){
					if(Integer.valueOf(map.get("count").toString()) > 0){
						errorList.add(RELATION_MSG_MAP.get(map.get("item")));
					}
				}
			if(errorList.size() > 0){
				throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),errorList + " 存在关联数据，不允许删除");
			}
		}
		return houseMapper.deleteById(id) == 1;
	}
	//========================================= 基础增删改查 结束 ========================================================
	
	/**
	 * 通过社区ID查出所有 楼栋、单元、楼层、未被登记的门牌
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @Param  communityId	社区ID
	 */
	@Override
	public List<HouseEntity> getCommunityHouseNumber(long communityId) {
		return houseMapper.getCommunityHouseNumber(communityId);
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



	@Override
	public List<ProprietorVO> getCommunityHouseById(Long communityId) {
		return houseMapper.getCommunityHouseById(communityId);
	}

}
