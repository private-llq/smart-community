package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PropertyEnum;
import com.jsy.community.entity.HouseBuildingTypeEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.HouseBuildingTypeMapper;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseBuildingTypeQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.property.ProprietorVO;
import jodd.util.StringUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
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

    private static final Map<String, String> RELATION_MSG_MAP = new HashMap<>() {{
        put("proprietor", "业主信息");
        put("userHouse", "APP业主房屋板块");
        put("houseMember", "房间成员");
        put("houseLease", "APP租房板块");
    }};

    @Autowired
    private HouseMapper houseMapper;
    
    @Autowired
    private HouseBuildingTypeMapper houseBuildingTypeMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IAdminUserService adminUserService;


//	@Autowired
//	private ICommunityService communityService;

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addHouse(HouseEntity houseEntity) {
        int addResult = 0;
        //生成id
        long id = SnowFlake.nextId();
        houseEntity.setId(id);
        if (BusinessConst.BUILDING_TYPE_BUILDING == houseEntity.getType()) {
            //设置对应名称
            houseEntity.setBuilding(houseEntity.getName());
            //设置顶级pid
            houseEntity.setPid(0L);
            //处理子级单元列表
//            if (!CollectionUtils.isEmpty(houseEntity.getUnitIdList())) {
                //绑定单元 修改单元的pid,building字段
//                houseMapper.unitBindBuilding(houseEntity.getUnitIdList(), houseEntity);
//            }
            //直接新增
            addResult = houseMapper.insert(houseEntity);
        } else if (BusinessConst.BUILDING_TYPE_UNIT == houseEntity.getType()) {
            //设置对应名称
            houseEntity.setUnit(houseEntity.getName());
            //设置顶级pid 单元新增时是pid是0，若新增楼栋绑定了该单元，单元pid置为楼栋id
//            houseEntity.setPid(0L);
            if (houseEntity.getPid() == 0 || houseEntity.getPid() == null){
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "缺少楼栋");
            }
            //直接新增
            addResult = houseMapper.insert(houseEntity);
            //新增之后更新单元数据
            HouseEntity pHouseEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("id", houseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
            pHouseEntity.setId(houseEntity.getId());
            houseMapper.unitBindBuildingUpdate(pHouseEntity);
        } else if (BusinessConst.BUILDING_TYPE_DOOR == houseEntity.getType()) {
            //查询父级是否存在
            Integer pidExists = houseMapper.selectCount(new QueryWrapper<HouseEntity>()
                .eq("id", houseEntity.getPid())
                .eq("community_id", houseEntity.getCommunityId())
                .and(wrapper -> wrapper.eq("type", BusinessConst.BUILDING_TYPE_BUILDING).or().eq("type", BusinessConst.BUILDING_TYPE_UNIT))
            );
            // queryWrapper.and(wrapper -> wrapper.like("unit",query.getUnit()).or().like("number",query.getUnit()));
            if (pidExists != 1) {
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "楼栋/单元 不存在");
            }
            //查询该小区是否已添加该房屋
            List<HouseEntity> allHouse = houseMapper.getAllHouse(houseEntity.getCommunityId());
            for (HouseEntity entity : allHouse) {
                if (entity.getDoor().equals(houseEntity.getName()) && entity.getFloor().equals(houseEntity.getFloor()) && entity.getPid().equals(houseEntity.getPid())) {
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "房屋已添加，请勿重复添加");
                }
            }
            //查询父级楼栋总层数更新到房屋 并 判断所属层数是否超过楼宇层数
            HouseEntity bHouseEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("id", houseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
            //pid是楼栋
            if (bHouseEntity.getPid() == 0){
                houseEntity.setTotalFloor(bHouseEntity.getTotalFloor());
                if (houseEntity.getFloor() > bHouseEntity.getTotalFloor()) {
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "房屋楼层不能高于楼栋总楼层");
                }
            } else {
                //pid是单元
                HouseEntity uHouseEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("id", bHouseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
                houseEntity.setTotalFloor(uHouseEntity.getTotalFloor());
                if (houseEntity.getFloor() > uHouseEntity.getTotalFloor()) {
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "房屋楼层不能高于楼栋总楼层");
                }
            }
//            houseEntity.setDoor(houseEntity.getNumber()); //TODO 后期可删除，但需要先修改依赖了t_house表door字段的代码
            houseEntity.setDoor(houseEntity.getName());
            houseEntity.setCode(UUID.randomUUID().toString().replace("-", ""));
            houseEntity.setCreateTime(LocalDateTime.now());
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHouse(HouseEntity houseEntity) {
        //判断类型
        HouseEntity entity = houseMapper.queryHouseByAnyProperty("id", houseEntity.getId());
        if (entity == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "数据不存在");
        }
        //置空不能自行修改项(条件修改里面有的)
        houseEntity.setBuilding(null);
        houseEntity.setUnit(null);
        houseEntity.setDoor(null);
        //若类型为 楼栋 ，绑定单元做全删权增
        if (BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()) {
            //禁止手动修改楼栋pid
            houseEntity.setPid(null);
            //解绑原有单元
            houseMapper.unitUnBindBuilding(houseEntity.getId());
            //绑定新单元列表
            if (!CollectionUtils.isEmpty(houseEntity.getUnitIdList())) {
                houseMapper.unitBindBuilding(houseEntity.getUnitIdList(), entity);
            }
            //若楼栋名称或楼宇分类或总楼层有修改，同步修改单元和房屋冗余的楼栋名称
            if (!StringUtils.isEmpty(houseEntity.getName()) || !StringUtils.isEmpty(houseEntity.getTotalFloor())) {
                //设置对应名称
                if (!StringUtils.isEmpty(houseEntity.getName())) {
                    entity.setBuilding(houseEntity.getName());
                    houseEntity.setBuilding(houseEntity.getName());
                }
                if (!StringUtils.isEmpty(houseEntity.getTotalFloor())) {
                    entity.setTotalFloor(houseEntity.getTotalFloor());
                }
                //查询子级单元id，子级房屋id，整合到一起
                List<Long> subUnitIdList = houseMapper.getSubIdList(Arrays.asList(houseEntity.getId()));
                if (!CollectionUtils.isEmpty(subUnitIdList)) {
                    List<Long> subRoomIdList = houseMapper.getSubIdList(subUnitIdList);
                    subUnitIdList.addAll(subRoomIdList);
                    //同步修改子节点
                    houseMapper.updateSub(subUnitIdList, entity);
                }
            }
            //若类型为 单元，且单元名称有修改，同步修改房屋冗余的单元名称
        } else if (BusinessConst.BUILDING_TYPE_UNIT == entity.getType()) {
            //禁止手动修改单元pid
            houseEntity.setPid(null);
            if (!StringUtils.isEmpty(houseEntity.getName())) {
                //设置对应名称
                entity.setUnit(houseEntity.getName());
                houseEntity.setUnit(houseEntity.getName());
                //查询子级房屋id
                List<Long> subRoomIdList = houseMapper.getSubIdList(Arrays.asList(houseEntity.getId()));
                if (!CollectionUtils.isEmpty(subRoomIdList)) {
                    //同步修改子节点
                    houseMapper.updateSub(subRoomIdList, entity);
                }
            }
            //若类型为房屋，且要更改房屋所属的单元，同步修改单元的名称
        } else if (BusinessConst.BUILDING_TYPE_DOOR == entity.getType()) {
            //查询要改单元下的更改房屋楼层是否已经存在
            List<HouseEntity> houseEntities = houseMapper.queryBindDoorList(houseEntity.getPid());
            for (HouseEntity houseUnitEntity : houseEntities) {
                if (houseUnitEntity.getDoor().equals(houseEntity.getName()) && houseUnitEntity.getFloor().equals(houseEntity.getFloor()) && houseUnitEntity.getPid().equals(houseEntity.getPid())) {
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "要更改房屋已存在，修改失败");
                }
            }
            if (!StringUtils.isEmpty(houseEntity.getName())) {
                houseEntity.setDoor(houseEntity.getName());
            }
            if (houseEntity.getPid() != 0) {
                //查新所属单元信息,楼栋信息
                HouseEntity unitEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("id", houseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
                HouseEntity buildingEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("id", unitEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
                if (houseEntity.getFloor() > unitEntity.getTotalFloor() || houseEntity.getFloor() > buildingEntity.getTotalFloor()) {
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "房屋楼层不能高于楼栋总楼层");
                }
                //设置对应单元名称
                houseEntity.setUnit(unitEntity.getUnit());
                houseEntity.setBuilding(buildingEntity.getBuilding());
                houseEntity.setTotalFloor(buildingEntity.getTotalFloor());
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
    @Override
    public PageInfo<HouseEntity> queryHouse(BaseQO<HouseQO> baseQO) {
        HouseQO query = baseQO.getQuery();
        Page<HouseEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == query.getType()) {
            queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_UNIT);
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == query.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == query.getType()) {
	        queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_DOOR);
        } else {
            queryWrapper.eq("type", query.getType());
        }
        queryWrapper.eq("community_id", query.getCommunityId());
        setQueryWrapper(queryWrapper, query);
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == query.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == query.getType()) {
            //当类型为单元查楼栋，房屋查单元，房屋查楼栋单元，传入id为需要查询的pid
            queryWrapper.eq("pid", query.getId());
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == query.getType()) {
            //通过楼栋id查询子集所有单元id
            List<Long> unitIdList = houseMapper.getSubIdList(Arrays.asList(query.getId()));
            if (!CollectionUtils.isEmpty(unitIdList)) {
                queryWrapper.in("pid", unitIdList);
            }
        } else {
            //是否查详情
            if (query.getId() != null) {
                queryWrapper.eq("id", query.getId());
            }
        }
        //是否有电梯
//        if (query.getHasElevator() != null) {
//            queryWrapper.eq("has_elevator", query.getHasElevator());
//        }
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == query.getType()) {
            queryWrapper.like("unit",query.getName());
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == query.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == query.getType()) {
            queryWrapper.like("door",query.getName());
        } else {
            //是否查关键字
            if (query.getName() != null) {
                if (BusinessConst.BUILDING_TYPE_BUILDING == query.getType()) {
                    queryWrapper.like("building",query.getName());
                } else if (BusinessConst.BUILDING_TYPE_UNIT == query.getType()) {
                    queryWrapper.like("unit",query.getName());
                } else if (BusinessConst.BUILDING_TYPE_DOOR == query.getType()) {
                    queryWrapper.like("door",query.getName());
                }
            }
        }
        //是否查楼栋层数
        if (query.getTotalFloor() != null) {
            queryWrapper.eq("total_floor",query.getTotalFloor());
        }
        //是否查楼宇分类
        if (query.getBuildingType() != null) {
            queryWrapper.eq("building_type",query.getBuildingType());
        }
        //楼栋和单元均不为空 只取单元id
        if (query.getBuildingId() != null && query.getUnitId() != null) {
            queryWrapper.eq("pid", query.getUnitId());
        } else if (query.getBuildingId() != null && 0L != query.getBuildingId()) {  //楼栋不为空，单元空  取楼栋id及其所有子级单元id
            List<Long> pidList = new ArrayList<>();
            //房屋直接挂楼栋
            pidList.add(query.getBuildingId());
            //房屋挂单元，查询单元idList并添加
            List<Long> unitIdList = houseMapper.getSubIdList(Arrays.asList(query.getBuildingId()));
            if (!CollectionUtils.isEmpty(unitIdList)) {
                pidList.addAll(unitIdList);
            }
            queryWrapper.in("pid", pidList);
        } else if (query.getUnitId() != null) {  //单元不为空，楼栋空 只取单元id
            queryWrapper.eq("pid", query.getUnitId());
        }
        queryWrapper.orderByDesc("create_time");
        Page<HouseEntity> pageData = houseMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        //查询类型为楼栋，再查出已绑定单元数
        if (BusinessConst.BUILDING_TYPE_BUILDING == query.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : pageData.getRecords()) {
                paramList.add(houseEntity.getId());
            }
            Map<Long, Map<String, Long>> bindMap = houseMapper.queryBindUnitCountBatch(paramList);
            for (HouseEntity houseEntity : pageData.getRecords()) {
                Map<String, Long> countMap = bindMap.get(houseEntity.getId());
                houseEntity.setBindUnitCount(countMap != null ? countMap.get("count") : null);
            }
            //若查详情，查出已绑定单元id
            if (query.getId() != null) {
                CollectionUtils.firstElement(pageData.getRecords()).setUnitIdList(houseMapper.queryBindUnitList(query.getId()));
            }
            List<Long> param = new ArrayList<>();
            for (HouseEntity houseEntity : pageData.getRecords()) {
                param.add(houseEntity.getBuildingType());
            }
            //补楼宇分类名称
            Map<Long, Map<String,String>> houseBuildingTypeMap = houseBuildingTypeMapper.queryHouseBuildingType(param);
            for (HouseEntity houseEntity : pageData.getRecords()) {
                if (houseEntity.getBuildingType() != null) {
                    Map<String, String> countMap = houseBuildingTypeMap.get(houseEntity.getBuildingType());
                    houseEntity.setBuildingTypeName(countMap == null ? "" : countMap.get("propertyTypeName"));
                } else {
                    houseEntity.setBuildingTypeName("");
                }
            }
        }
        //查询类型为房屋，设置房屋类型、房产类型、装修情况、户型
        else if (BusinessConst.BUILDING_TYPE_DOOR == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == query.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : pageData.getRecords()) {
                houseEntity.setHouseTypeStr(PropertyEnum.HouseTypeEnum.HOUSE_TYPE_MAP.get(houseEntity.getHouseType()));
                // houseEntity.setPropertyTypeStr(PropertyEnum.PropertyTypeEnum.PROPERTY_TYPE_MAP.get(houseEntity.getPropertyType()));
                houseEntity.setDecorationStr(PropertyEnum.DecorationEnum.DECORATION_MAP.get(houseEntity.getDecoration()));
                if ("00000000".equals(houseEntity.getHouseTypeCode())) {
                    houseEntity.setHouseTypeCodeStr("单间配套");
                    continue;
                }
                setHouseTypeCodeStr(houseEntity);
                // 查询房屋的时候补充楼栋id
                if (houseEntity != null) {
                    HouseEntity unitEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
                        .eq("id", houseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
                    if (unitEntity != null) {
                        if (unitEntity.getPid().equals(0)) {
                            houseEntity.setBuildingId(unitEntity.getId());
                            houseEntity.setBuildingIdStr(String.valueOf(unitEntity.getId()));
                        } else {
                            HouseEntity buildingEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
                                .eq("id", unitEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
                            if (buildingEntity != null) {
                                houseEntity.setBuildingId(buildingEntity.getId());
                                houseEntity.setBuildingIdStr(String.valueOf(buildingEntity.getId()));
                            }
                        }
                    }
                }
                paramList.add(houseEntity.getId());
            }
            //查询住户数量
            Map<Long, Map<String, Long>> bindMap = houseMapper.selectHouseNumberCount(paramList);
            for (HouseEntity houseEntity : pageData.getRecords()) {
                Map<String, Long> countMap = bindMap.get(BigInteger.valueOf(houseEntity.getId()));
                houseEntity.setHouseNumber(countMap != null ? countMap.get("count") : 0L);
                houseEntity.setStatus(houseEntity.getHouseNumber() == 0 ? "空置" : "入住");
            }
        }
        //补创建人和更新人姓名
        Set<String> createUidSet = new HashSet<>();
        Set<String> updateUidSet = new HashSet<>();
        for (HouseEntity houseEntity : pageData.getRecords()) {
            createUidSet.add(houseEntity.getCreateBy());
            updateUidSet.add(houseEntity.getUpdateBy());
        }
        Map<String, Map<String, String>> createUserMap = adminUserService.queryNameByUidBatch(createUidSet);
        Map<String, Map<String, String>> updateUserMap = adminUserService.queryNameByUidBatch(updateUidSet);
        for (HouseEntity houseEntity : pageData.getRecords()) {
            houseEntity.setCreateBy(createUserMap.get(houseEntity.getCreateBy()) == null ? null : createUserMap.get(houseEntity.getCreateBy()).get("name"));
            houseEntity.setUpdateBy(updateUserMap.get(houseEntity.getUpdateBy()) == null ? null : updateUserMap.get(houseEntity.getUpdateBy()).get("name"));
            // 补充pidStr
            houseEntity.setPidStr(String.valueOf(houseEntity.getPid()));
        }
        PageInfo<HouseEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }

    //设置通用查询条件
    private void setQueryWrapper(QueryWrapper<HouseEntity> queryWrapper, HouseQO query) {
//		if(!StringUtils.isEmpty(query.getNumber())){
//			queryWrapper.like("number",query.getNumber());
//		}
        if (!StringUtils.isEmpty(query.getBuilding())) {
            queryWrapper.and(wrapper -> wrapper.like("building", query.getBuilding()).or().like("number", query.getBuilding()));
        }
        if (!StringUtils.isEmpty(query.getUnit())) {
            queryWrapper.and(wrapper -> wrapper.like("unit", query.getUnit()).or().like("number", query.getUnit()));
        }
        if (!StringUtils.isEmpty(query.getDoor())) {
            queryWrapper.and(wrapper -> wrapper.like("door", query.getDoor()).or().like("number", query.getDoor()));
        }
    }

    //设置户型
    private void setHouseTypeCodeStr(HouseEntity houseEntity) {
        String houseTypeCode = houseEntity.getHouseTypeCode();
        if (houseTypeCode == null || houseTypeCode.length() != 8) {
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
     * @Param: [id, communityId]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/3/15
     **/
    @Override
    public boolean deleteHouse(Long id, Long communityId) {
        HouseEntity entity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("id", id).eq("community_id", communityId));
        if (entity == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "数据不存在");
        }
        //删除楼栋/单元(有下级依赖不允许删除)
        //删除房屋(有依赖数据不允许删除)
        if (BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()
                || BusinessConst.BUILDING_TYPE_UNIT == entity.getType()) {
            Integer count = houseMapper.selectCount(new QueryWrapper<HouseEntity>().eq("pid", id));
            if (count > 0) {
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "存在下级单元或房屋，不允许删除");
            }
        } else if (BusinessConst.BUILDING_TYPE_DOOR == entity.getType()) {
            List<Map<String, Object>> countList = houseMapper.verifyRelevance(id);
            List<String> errorList = new ArrayList<>(countList.size());
            for (Map<String, Object> map : countList) {
                if (Integer.valueOf(map.get("count").toString()) > 0) {
                    errorList.add(RELATION_MSG_MAP.get(map.get("item")));
                }
            }
            if (errorList.size() > 0) {
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), errorList + " 存在关联数据，不允许删除");
            }
        }
        return houseMapper.deleteById(id) == 1;
    }
    
    /**
     * @Description: 【楼宇房屋】批量删除
     * @Param: [ids]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/12
     **/
    @Override
    public boolean deletesHouse(List<Long> ids) {
        for (Long id : ids) {
            List<Map<String, Object>> countList = houseMapper.verifyRelevance(id);
            List<String> errorList = new ArrayList<>(countList.size());
            for (Map<String, Object> map : countList) {
                if (Integer.valueOf(map.get("count").toString()) > 0) {
                    errorList.add(RELATION_MSG_MAP.get(map.get("item")));
                }
            }
            if (errorList.size() > 0) {
                HouseEntity houseEntity = houseMapper.selectById(id);
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "房屋" + houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor() + "," + errorList + " 存在关联数据，不允许删除");
            }
        }
        int result = houseMapper.deleteBatchIds(ids);
        if(result > 0){
            return true;
        }
        return false;
    }
    //========================================= 基础增删改查 结束 ========================================================
    
    /**
     * @Description: 新增楼宇分类
     * @Param: [houseBuildingTypeEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/05
     **/
    @Override
    public boolean addHouseBuildingType(HouseBuildingTypeEntity houseBuildingTypeEntity){
        HouseBuildingTypeEntity entity = houseBuildingTypeMapper.selectOne(new QueryWrapper<>(houseBuildingTypeEntity).eq("property_type_name", houseBuildingTypeEntity.getPropertyTypeName()));
        if (entity!=null) {
            throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(),"该楼宇分类已存在，请勿重复添加！");
        }
        houseBuildingTypeEntity.setId(SnowFlake.nextId());
        int row = houseBuildingTypeMapper.insert(houseBuildingTypeEntity);
        return row == 1;
    }
    
    /**
     * @Description: 修改楼宇分类
     * @Param: [houseBuildingTypeEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/05
     **/
    @Override
    public boolean updateHouseBuildingType(HouseBuildingTypeEntity houseBuildingTypeEntity){
        if (houseBuildingTypeEntity.getId() == null) {
            throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(),"请传入id！");
        }
        houseBuildingTypeEntity.setUpdateTime(LocalDateTime.now());
        int row = houseBuildingTypeMapper.updateById(houseBuildingTypeEntity);
        return row == 1;
    }
    
    /**
     * @Description: 删除楼宇分类
     * @Param: [id, communityId]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/08/05
     **/
    @Override
    public boolean deleteHouseBuildingType(Long id, Long communityId){
        HouseBuildingTypeEntity entity = houseBuildingTypeMapper.selectOne(new QueryWrapper<HouseBuildingTypeEntity>().eq("id", id).eq("community_id", communityId));
        if (entity == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "楼宇分类数据不存在");
        }
        List<HouseEntity> houseEntities = houseMapper.selectList(new QueryWrapper<HouseEntity>().eq("building_type", entity.getId()).eq("deleted", 0));
        if (houseEntities.size() > 0) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "该楼宇分类存在楼宇，请删除楼宇后再进行操作");
        }
        return houseBuildingTypeMapper.deleteById(id) == 1;
    }
    
    /**
     * @Description: 查询楼宇分类
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseBuildingTypeEntity>
     * @Author: DKS
     * @Date: 2021/08/05
     **/
    @Override
    public PageInfo<HouseBuildingTypeEntity> queryHouseBuildingType(BaseQO<HouseBuildingTypeQO> baseQO) {
        HouseBuildingTypeQO query = baseQO.getQuery();
        Page<HouseBuildingTypeEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<HouseBuildingTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", query.getCommunityId());
        //是否查详情
        if (query.getId() != null) {
            queryWrapper.eq("id", query.getId());
        }
        //是否查楼宇分类关键字
        if (query.getPropertyTypeName() != null) {
            queryWrapper.like("property_type_name",query.getPropertyTypeName());
        }
        queryWrapper.orderByDesc("create_time");
        Page<HouseBuildingTypeEntity> pageData = houseBuildingTypeMapper.selectPage(page, queryWrapper);
        PageInfo<HouseBuildingTypeEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    
    /**
     * 通过社区ID查出所有 楼栋、单元、楼层、未被登记的门牌
     *
     * @author YuLF
     * @Param communityId    社区ID
     * @since 2020/11/26 9:38
     */
    @Override
    public List<HouseEntity> getCommunityHouseNumber(long communityId) {
        return houseMapper.getCommunityHouseNumber(communityId);
    }

    /**
     * @Author: Pipi
     * @Description: 通过社区ID查询出所有楼栋和单元
     * @Param: communityId:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/5/19 15:34
     */
    @Override
    public List<HouseEntity> getBuildingAndUnitList(long communityId) {
        return houseMapper.getBuildingAndUnitList(communityId);
    }

    /**
     * 按社区ID获取 社区名称和 当前社区住户房间数量
     *
     * @author YuLF
     * @Param communityId   社区id
     * @return 返回社区名称和 当前社区住户房间数量
     * @since 2020/12/3 11:06
     */
    @Override
    public Map<String, Object> getCommunityNameAndUserAmountById(long communityId) {
        return houseMapper.getCommunityNameAndUserAmountById(communityId);
    }

    /**
     * 按社区ID获取 社区名称 社区用户名和社区用户uid
     *
     * @param communityId 社区id
     * @author YuLF
     * @return 返回社区名称和 当前社区所有住户名称，住户uid
     * @since 2020/12/7 11:06
     */
    @Override
    public List<UserEntity> getCommunityNameAndUserInfo(long communityId) {
        return houseMapper.getCommunityNameAndUserInfo(communityId);
    }


    @Override
    public List<ProprietorVO> getCommunityHouseById(Long communityId) {
        return houseMapper.getCommunityHouseById(communityId);
    }

    /**
     * @Description: 小区下所有房间
     * @author: Hu
     * @since: 2021/4/24 14:42
     * @Param:
     * @return:
     */
    @Override
    public List<HouseEntity> selectHouseAll(Long communityId) {
        return houseMapper.selectHouseAll(communityId);
    }

    /**
     * @Description: ids 批量查 id-entity对应关系
     * @Param: [ids]
     * @Return: java.util.Map<java.lang.Long, com.jsy.community.entity.HouseEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    @Override
    public Map<Long, HouseEntity> queryIdAndHouseMap(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids) || (ids.size() == 1 && ids.contains(null))) {
            return new HashMap<>(1);
        }
        return houseMapper.queryIdAndHouseMap(ids);
    }

    /**
     * @Author: Pipi
     * @Description: excel导入时, 批量新增房屋数据
     * @Param: houseEntityList:
     * @Return: java.lang.Integer
     * @Date: 2021/5/21 14:26
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveHouseBatch(List<HouseEntity> houseEntityList, Long communityId, String uid) {
        // 需要验证社区下这个编号的楼栋或者单元是否存在,如果不存在,则新增楼栋或者单元
        // 先将楼栋编号和单元编号拿出来,放入到set里面
        HashMap<String, HouseEntity> buildingNumMap = new HashMap<>();
        HashMap<String, HouseEntity> unitNumMap = new HashMap<>();
        // 此次新增的所有房屋
        List<HouseEntity> addHouseEntityList = new ArrayList<>();
        for (HouseEntity houseEntity : houseEntityList) {
            // 楼栋
            if (!buildingNumMap.containsKey(houseEntity.getBuilding())) {
                HouseEntity buildingHouseEntity = new HouseEntity();
//                buildingHouseEntity.setNumber(houseEntity.getBuildingNumber());
                buildingHouseEntity.setCommunityId(communityId);
                buildingHouseEntity.setBuilding(houseEntity.getBuilding());
                buildingHouseEntity.setTotalFloor(houseEntity.getTotalFloor());
                buildingHouseEntity.setPid(0L);
                buildingHouseEntity.setType(1);
//                buildingHouseEntity.setPropertyType(houseEntity.getPropertyType());
                buildingHouseEntity.setCreateBy(uid);
                buildingHouseEntity.setId(SnowFlake.nextId());
                buildingHouseEntity.setDeleted(0L);
                buildingHouseEntity.setCreateTime(LocalDateTime.now());
                buildingHouseEntity.setUpdateTime(LocalDateTime.now());
                buildingNumMap.put(houseEntity.getBuilding(), buildingHouseEntity);
            }
            // 单元
            if (!unitNumMap.containsKey(houseEntity.getUnit())) {
                HouseEntity unitHouseEntity = new HouseEntity();
//                unitHouseEntity.setNumber(houseEntity.getUnitNumber());
                unitHouseEntity.setCommunityId(communityId);
                unitHouseEntity.setBuilding(houseEntity.getBuilding());
                unitHouseEntity.setTotalFloor(houseEntity.getTotalFloor());
                unitHouseEntity.setUnit(houseEntity.getUnit());
                unitHouseEntity.setType(2);
//                unitHouseEntity.setPropertyType(houseEntity.getPropertyType());
                unitHouseEntity.setCreateBy(uid);
//                unitHouseEntity.setBuildingNumber(houseEntity.getBuildingNumber());
                unitHouseEntity.setId(SnowFlake.nextId());
                unitHouseEntity.setDeleted(0L);
                unitHouseEntity.setCreateTime(LocalDateTime.now());
                unitHouseEntity.setUpdateTime(LocalDateTime.now());
                unitNumMap.put(houseEntity.getBuilding() + houseEntity.getUnit(), unitHouseEntity);
            }
            // 房屋
            HouseEntity addHouseEntity = new HouseEntity();
//            addHouseEntity.setNumber(houseEntity.getNumber());
            addHouseEntity.setCommunityId(communityId);
            addHouseEntity.setBuilding(houseEntity.getBuilding());
            addHouseEntity.setTotalFloor(houseEntity.getTotalFloor());
            addHouseEntity.setUnit(houseEntity.getUnit());
            addHouseEntity.setDoor(houseEntity.getDoor());
//            addHouseEntity.setUnitNumber(houseEntity.getUnitNumber());
            addHouseEntity.setFloor(houseEntity.getFloor());
            addHouseEntity.setType(4);
            addHouseEntity.setBuildArea(houseEntity.getBuildArea());
            addHouseEntity.setPracticalArea(houseEntity.getPracticalArea());
//            addHouseEntity.setHouseType(houseEntity.getHouseType());
//            addHouseEntity.setPropertyType(houseEntity.getPropertyType());
//            addHouseEntity.setDecoration(houseEntity.getDecoration());
            addHouseEntity.setComment(houseEntity.getComment());
            addHouseEntity.setCreateBy(uid);
            addHouseEntity.setId(SnowFlake.nextId());
            addHouseEntity.setDeleted(0L);
            addHouseEntity.setCreateTime(LocalDateTime.now());
            addHouseEntity.setUpdateTime(LocalDateTime.now());
            addHouseEntityList.add(addHouseEntity);
        }
        // 将社区下的所有楼栋和单元全部查出来
        List<HouseEntity> allExistHouseEntities = houseMapper.getBuildingAndUnitList(communityId);
        HashSet<String> buildingNumSet = new HashSet<>();
        HashSet<String> unitNumSet = new HashSet<>();
        if (CollectionUtil.isNotEmpty(allExistHouseEntities)) {
            for (HouseEntity houseEntity : allExistHouseEntities) {
                if (houseEntity.getType() == 1) {
                    buildingNumSet.add(houseEntity.getBuilding());
                }
                if (houseEntity.getType() == 2) {
                    unitNumSet.add(houseEntity.getBuilding() + houseEntity.getUnit());
                }
            }
        }
        // 需要添加的楼栋实体
        List<HouseEntity> addBuildingHouseEntityList = new ArrayList<>();
        for (String building : buildingNumMap.keySet()) {
            if (!buildingNumSet.contains(building)) {
                addBuildingHouseEntityList.add(buildingNumMap.get(building));
            }
        }
        // 需要添加的单元实体
        List<HouseEntity> addUnitHouseEntityList = new ArrayList<>();
        allExistHouseEntities.addAll(addBuildingHouseEntityList);
        for (String number : unitNumMap.keySet()) {
            if (!unitNumSet.contains(number)) {
                // 新增单元,比对原有的楼栋和新增的楼栋,获取楼栋id
                for (HouseEntity houseEntity : allExistHouseEntities) {
                    if (houseEntity.getType() == 1) {
                        if (houseEntity.getBuilding().equals(unitNumMap.get(number).getBuilding())) {
                            HouseEntity unitHouseEntity = unitNumMap.get(number).setPid(houseEntity.getId());
                            addUnitHouseEntityList.add(unitHouseEntity);
                        }
                    }
                }
            }
        }
        allExistHouseEntities.addAll(addUnitHouseEntityList);
        // 给每个新增的房屋设置pid
        for (HouseEntity houseEntity : addHouseEntityList) {
            for (HouseEntity allExistHouseEntity : allExistHouseEntities) {
                if (allExistHouseEntity.getType() == 2) {
                    if ((houseEntity.getBuilding() + houseEntity.getUnit()).equals(allExistHouseEntity.getBuilding() + allExistHouseEntity.getUnit())) {
                        houseEntity.setPid(allExistHouseEntity.getId());
                    }
                }
            }
        }
        // 最终需要增加的单元和楼栋数据
        addUnitHouseEntityList.addAll(addBuildingHouseEntityList);
        // 最终需要增加的单元,楼栋,房屋的数据集合
        addUnitHouseEntityList.addAll(addHouseEntityList);
        return houseMapper.saveHouseBatch(addUnitHouseEntityList);
    }
    
    /**
     *@Author: DKS
     *@Description: excel导入时,批量新增楼栋数据
     *@Param: houseEntityList:
     *@Return: java.lang.Integer
     *@Date: 2021/8/10 11:26
     **/
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveBuildingBatch(List<HouseEntity> houseEntityList, Long communityId, String uid) {
        // 需要验证社区下这个编号的楼栋是否存在,如果不存在,则新增楼栋
        // 先将楼栋名称拿出来,放入到set里面
        HashMap<String, HouseEntity> buildingNumMap = new HashMap<>();
        for (HouseEntity houseEntity : houseEntityList) {
            // 楼栋
            if (!buildingNumMap.containsKey(houseEntity.getBuilding())) {
                HouseEntity buildingHouseEntity = new HouseEntity();
                buildingHouseEntity.setCommunityId(communityId);
                buildingHouseEntity.setBuilding(houseEntity.getBuilding());
                buildingHouseEntity.setTotalFloor(houseEntity.getTotalFloor());
                buildingHouseEntity.setPid(0L);
                buildingHouseEntity.setType(1);
                buildingHouseEntity.setBuildingType(houseEntity.getBuildingType());
                buildingHouseEntity.setCreateBy(uid);
                buildingHouseEntity.setId(SnowFlake.nextId());
                buildingHouseEntity.setDeleted(0L);
                buildingHouseEntity.setCreateTime(LocalDateTime.now());
                buildingHouseEntity.setUpdateTime(LocalDateTime.now());
                buildingNumMap.put(houseEntity.getBuilding(), buildingHouseEntity);
            }
        }
        // 将社区下的所有楼栋全部查出来
        List<HouseEntity> allExistBuildingEntities = houseMapper.getBuildingList(communityId);
        HashSet<String> buildingNumSet = new HashSet<>();
        if (CollectionUtil.isNotEmpty(allExistBuildingEntities)) {
            for (HouseEntity houseEntity : allExistBuildingEntities) {
                if (houseEntity.getType() == 1) {
                    buildingNumSet.add(houseEntity.getBuilding());
                }
            }
        }
        // 需要添加的楼栋实体
        List<HouseEntity> addBuildingEntityList = new ArrayList<>();
        for (String building : buildingNumMap.keySet()) {
            if (!buildingNumSet.contains(building)) {
                addBuildingEntityList.add(buildingNumMap.get(building));
            }
        }
        return houseMapper.saveBuildingBatch(addBuildingEntityList);
    }

    /**
     * @Author: Pipi
     * @Description: 查询小区的所有房间
     * @Param: communityId:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/5/21 17:16
     */
    @Override
    public List<HouseEntity> getAllHouse(Long communityId) {
        return houseMapper.getAllHouse(communityId);
    }


    /**
     * @Description: 查询所有房间
     * @author: Hu
     * @since: 2021/8/6 16:22
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.HouseEntity>
     */
    @Override
    public List<HouseEntity> selectAll(Long communityId) {
        return houseMapper.selectList(new QueryWrapper<HouseEntity>().eq("community_id",communityId).eq("type",4));
    }

    /**
    * @Description: 检查楼栋单元数据真实性
     * @Param: [buildingId, unitId, communityId]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021-07-24
    **/
    public void checkBuildingAndUnit(Long buildingId, Long unitId, Long communityId){
        //楼栋和单元都要检查
        if(buildingId != null && unitId != null){
            List<Long> list = new ArrayList<>();
            list.add(buildingId);
            list.add(unitId);
            //查出两条数据
            List<HouseEntity> buildingAndUnit = houseMapper.selectList(new QueryWrapper<HouseEntity>().select("id,pid,type")
                .in("id",list)
                .eq("community_id",communityId)
            );
            //检查数据
            if(CollectionUtils.isEmpty(buildingAndUnit)){
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"未找到楼栋及单元");
            }else if(buildingAndUnit.size() == 1){  //只找到
                if(buildingAndUnit.get(0).getType().equals(BusinessConst.BUILDING_TYPE_BUILDING)){
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"未找到单元");
                }else if(buildingAndUnit.get(0).getType().equals(BusinessConst.BUILDING_TYPE_UNIT)){
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"未找到楼栋");
                }
            }
            HouseEntity data1 = buildingAndUnit.get(0);
            HouseEntity data2 = buildingAndUnit.get(1);
            //检查pid关联
            if(data1.getType().equals(BusinessConst.BUILDING_TYPE_BUILDING) && data2.getType().equals(BusinessConst.BUILDING_TYPE_UNIT)){
                //数据1是楼栋，数据2是单元
                if(!data2.getPid().equals(data1.getId())){
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"楼栋与单元无关联");
                }
            }else if(data1.getType().equals(BusinessConst.BUILDING_TYPE_UNIT) && data2.getType().equals(BusinessConst.BUILDING_TYPE_BUILDING)){
                //数据1是单元，数据2是楼栋
                if(!data1.getPid().equals(data2.getId())){
                    throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"楼栋与单元无关联");
                }
            }else{
                log.error("数据检查错误，接收到楼栋ID：" + buildingId + "单元ID：" + unitId + "社区：" + communityId);
                throw new PropertyException(JSYError.INTERNAL.getCode(),"后台数据有误，请联系管理员");
            }
        }else if(buildingId != null){  //只检查楼栋
            Integer count = houseMapper.selectCount(new QueryWrapper<HouseEntity>().eq("id",buildingId).eq("community_id",communityId));
            if(count < 1){
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"未找到楼栋");
            }
        }else if(unitId != null){   //只检查单元
            Integer count = houseMapper.selectCount(new QueryWrapper<HouseEntity>().eq("id",unitId).eq("community_id",communityId));
            if(count < 1){
                throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"未找到单元");
            }


        }
    }
    
    /**
     * @Description: 查询楼栋、单元、房屋导出数据
     * @Param: HouseEntity
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
     * @Author: DKS
     * @Date: 2021/8/9
     **/
    @Override
    public List<HouseEntity> queryExportHouseExcel(HouseEntity entity) {
        List<HouseEntity> houseEntities;
        QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == entity.getType()) {
            queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_UNIT);
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == entity.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_DOOR);
        } else {
            queryWrapper.eq("type", entity.getType());
        }
        queryWrapper.eq("community_id", entity.getCommunityId());
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == entity.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            //当类型为单元查楼栋，房屋查单元，房屋查楼栋单元，传入id为需要查询的pid
            queryWrapper.eq("pid", entity.getId());
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType()) {
            //通过楼栋id查询子集所有单元id
            List<Long> unitIdList = houseMapper.getSubIdList(Arrays.asList(entity.getId()));
            if (!CollectionUtils.isEmpty(unitIdList)) {
                queryWrapper.in("pid", unitIdList);
            }
        } else {
            //是否查详情
            if (entity.getId() != null) {
                queryWrapper.eq("id", entity.getId());
            }
        }
        //是否有电梯
//        if (query.getHasElevator() != null) {
//            queryWrapper.eq("has_elevator", query.getHasElevator());
//        }
        //是否查关键字
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == entity.getType()) {
            queryWrapper.like("unit",entity.getName());
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == entity.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            queryWrapper.like("door",entity.getName());
        } else {
            //是否查关键字
            if (entity.getName() != null) {
                if (BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()) {
                    queryWrapper.like("building",entity.getName());
                } else if (BusinessConst.BUILDING_TYPE_UNIT == entity.getType()) {
                    queryWrapper.like("unit",entity.getName());
                } else if (BusinessConst.BUILDING_TYPE_DOOR == entity.getType()) {
                    queryWrapper.like("door",entity.getName());
                }
            }
        }
        //是否查楼栋层数
        if (entity.getTotalFloor() != null) {
            queryWrapper.eq("total_floor",entity.getTotalFloor());
        }
        //是否查楼宇分类
        if (entity.getBuildingType() != null) {
            queryWrapper.eq("building_type",entity.getBuildingType());
        }
//        //楼栋和单元均不为空 只取单元id
//        if (query.getBuildingId() != null && query.getUnitId() != null) {
//            queryWrapper.eq("pid", query.getUnitId());
//        } else if (query.getBuildingId() != null && 0L != query.getBuildingId()) {  //楼栋不为空，单元空  取楼栋id及其所有子级单元id
//            List<Long> pidList = new ArrayList<>();
//            //房屋直接挂楼栋
//            pidList.add(query.getBuildingId());
//            //房屋挂单元，查询单元idList并添加
//            List<Long> unitIdList = houseMapper.getSubIdList(Arrays.asList(query.getBuildingId()));
//            if (!CollectionUtils.isEmpty(unitIdList)) {
//                pidList.addAll(unitIdList);
//            }
//            queryWrapper.in("pid", pidList);
//        } else if (query.getUnitId() != null) {  //单元不为空，楼栋空 只取单元id
//            queryWrapper.eq("pid", query.getUnitId());
//        }
        queryWrapper.orderByDesc("create_time");
        houseEntities = houseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(houseEntities)) {
            return houseEntities;
        }
        //查询类型为楼栋，再查出已绑定单元数
        if (BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : houseEntities) {
                paramList.add(houseEntity.getId());
            }
            Map<Long, Map<String, Long>> bindMap = houseMapper.queryBindUnitCountBatch(paramList);
            for (HouseEntity houseEntity : houseEntities) {
                Map<String, Long> countMap = bindMap.get(houseEntity.getId());
                houseEntity.setBindUnitCount(countMap != null ? countMap.get("count") : null);
            }
            //若查详情，查出已绑定单元id
            if (entity.getId() != null) {
                CollectionUtils.firstElement(houseEntities).setUnitIdList(houseMapper.queryBindUnitList(entity.getId()));
            }
            List<Long> param = new ArrayList<>();
            for (HouseEntity houseEntity : houseEntities) {
                param.add(houseEntity.getBuildingType());
            }
            //补楼宇分类名称
            Map<Long, Map<String,String>> houseBuildingTypeMap = houseBuildingTypeMapper.queryHouseBuildingType(param);
            for (HouseEntity houseEntity : houseEntities) {
                if (houseEntity.getBuildingType() != null) {
                    Map<String, String> countMap = houseBuildingTypeMap.get(houseEntity.getBuildingType());
                    houseEntity.setBuildingTypeName(countMap == null ? "" : countMap.get("propertyTypeName"));
                } else {
                    houseEntity.setBuildingTypeName("");
                }
            }
        }
        //查询类型为房屋，设置房屋类型、房产类型、装修情况、户型
        else if (BusinessConst.BUILDING_TYPE_DOOR == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : houseEntities) {
                houseEntity.setHouseTypeStr(PropertyEnum.HouseTypeEnum.HOUSE_TYPE_MAP.get(houseEntity.getHouseType()));
                // houseEntity.setPropertyTypeStr(PropertyEnum.PropertyTypeEnum.PROPERTY_TYPE_MAP.get(houseEntity.getPropertyType()));
                houseEntity.setDecorationStr(PropertyEnum.DecorationEnum.DECORATION_MAP.get(houseEntity.getDecoration()));
                if ("00000000".equals(houseEntity.getHouseTypeCode())) {
                    houseEntity.setHouseTypeCodeStr("单间配套");
                    continue;
                }
                setHouseTypeCodeStr(houseEntity);
	            // 查询房屋的时候补充楼栋id
	            if (houseEntity != null) {
		            HouseEntity unitEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
			            .eq("id", houseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
		            if (unitEntity != null) {
			            if (unitEntity.getPid().equals(0)) {
				            houseEntity.setBuildingId(unitEntity.getId());
                            houseEntity.setBuildingIdStr(String.valueOf(unitEntity.getId()));
			            } else {
				            HouseEntity buildingEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
					            .eq("id", unitEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
				            if (buildingEntity != null) {
					            houseEntity.setBuildingId(buildingEntity.getId());
                                houseEntity.setBuildingIdStr(String.valueOf(buildingEntity.getId()));
				            }
			            }
		            }
	            }
                houseEntity.setPidStr(String.valueOf(houseEntity.getPid()));
                paramList.add(houseEntity.getId());
            }
            //查询住户数量
            Map<Long, Map<String, Long>> bindMap = houseMapper.selectHouseNumberCount(paramList);
            for (HouseEntity houseEntity : houseEntities) {
                Map<String, Long> countMap = bindMap.get(houseEntity.getId());
                houseEntity.setHouseNumber(countMap != null ? countMap.get("count") : 0L);
                houseEntity.setStatus(houseEntity.getHouseNumber() == 0 ? "空置" : "入住");
            }
        }
        //补创建人和更新人姓名
        Set<String> createUidSet = new HashSet<>();
        Set<String> updateUidSet = new HashSet<>();
        for (HouseEntity houseEntity : houseEntities) {
            createUidSet.add(houseEntity.getCreateBy());
            updateUidSet.add(houseEntity.getUpdateBy());
        }
        Map<String, Map<String, String>> createUserMap = adminUserService.queryNameByUidBatch(createUidSet);
        Map<String, Map<String, String>> updateUserMap = adminUserService.queryNameByUidBatch(updateUidSet);
        for (HouseEntity houseEntity : houseEntities) {
            houseEntity.setCreateBy(createUserMap.get(houseEntity.getCreateBy()) == null ? null : createUserMap.get(houseEntity.getCreateBy()).get("name"));
            houseEntity.setUpdateBy(updateUserMap.get(houseEntity.getUpdateBy()) == null ? null : updateUserMap.get(houseEntity.getUpdateBy()).get("name"));
        }
        return houseEntities;
    }
    
    /**
     * @Description: 查询楼宇分类
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseBuildingTypeEntity>
     * @Author: DKS
     * @Date: 2021/08/10
     **/
    @Override
    public List<HouseBuildingTypeEntity> selectHouseBuildingType(Long communityId) {
        return houseBuildingTypeMapper.selectHouseBuildingTypeName(communityId);
    }
    
    /**
     * @Description: 批量查询楼宇分类id
     * @Author: DKS
     * @Date: 2021/08/10
     **/
    @Override
    public Map<String, Map<String,Long>> queryHouseBuildingTypeId(List<String> buildingTypeNames) {
        return houseBuildingTypeMapper.queryHouseBuildingTypeId(buildingTypeNames);
    }
	
	/**
	 * @Description: 查询社区下所有楼栋
	 * @author: DKS
	 * @since: 2021/8/10 14:22
	 * @Param: communityId
	 * @return: java.util.List<com.jsy.community.entity.HouseEntity>
	 */
	@Override
	public List<HouseEntity> selectAllBuilding(Long communityId) {
		return houseMapper.getBuildingList(communityId);
	}
    
    /**
     * @Description: 查询小区下所有楼栋、单元、房屋
     * @author: DKS
     * @since: 2021/8/13 14:08
     * @Param: communityId
     * @return: java.util.List<com.jsy.community.entity.HouseEntity>
     */
    @Override
    public List<HouseEntity> selectAllBuildingUnitDoor(Long communityId) {
	    return houseMapper.selectAllBuildingUnitDoor(communityId);
    }


    /**
     * @Description: 查询当前小区所有房屋地址
     * @author: Hu
     * @since: 2021/9/1 14:23
     * @Param: [adminCommunityId]
     * @return: java.util.List<com.jsy.community.entity.HouseEntity>
     */
    @Override
    public List<HouseEntity> getHouse(Long adminCommunityId) {
        return houseMapper.selectList(new QueryWrapper<HouseEntity>().select("id,concat(building,unit,door) as address").eq("community_id",adminCommunityId).eq("type",4));
    }

    /**
     * 根据楼栋id和社区id查询楼栋名字集合
     * @param strings
     * @return
     */
    @Override
    public List<String> selectBuildingNameByIdList(List<String> strings, Long communityId) {
        System.out.println(strings.toString()+communityId);
        List<String> list=new ArrayList<>();
        for (String string : strings) {
            long l = Long.parseLong(string);
            HouseEntity houseEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("community_id", communityId).eq("id", l));
            String building = houseEntity.getBuilding();
            list.add(building);
        }
        return list;
    }

    /**
     * @param houseEntity : 查询条件-名称模糊查询
     * @author: Pipi
     * @description: 通用房屋名称搜索
     * @return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @date: 2021/11/11 9:42
     **/
    @Override
    public List<HouseEntity> commonQueryHouse(HouseEntity houseEntity) {
        QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", houseEntity.getCommunityId());
        queryWrapper.eq("type", houseEntity.getType());
        if (StringUtil.isNotBlank(houseEntity.getName())) {
            queryWrapper.and(wrapper ->
                    wrapper.like("building", houseEntity.getName())
                            .or().like("unit", houseEntity.getName())
                            .or().like("floor", houseEntity.getName())
                            .or().like("door", houseEntity.getName())
            );
        }
        if (houseEntity.getId() != null) {
            queryWrapper.eq("pid", houseEntity.getId());
        }
        List<HouseEntity> houseEntities = houseMapper.selectList(queryWrapper);
        return houseEntities;
    }
}
