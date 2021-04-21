package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.mapper.HouseMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 楼栋房屋
 * @since 2020-12-16 14:10
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class HouseServiceImpl extends ServiceImpl<HouseMapper,HouseEntity> implements IHouseService {
	
	@Autowired
	private HouseMapper houseMapper;
	
	/**
	* @Description: 查询房间
	 * @Param: [list]
	 * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	@Override
	public List<HouseEntity> queryHouses(Collection<Long> list){
		return houseMapper.queryHouses(list);
	}
	
	/**
	* @Description: 查找父节点
	 * @Param: [tempEntity]
	 * @Return: com.jsy.community.entity.HouseEntity
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	@Override
	public HouseEntity getParent(HouseEntity tempEntity){
		return houseMapper.getParent(tempEntity);
	}
	
	/**
	* @Description: ids批量查询房屋
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@Override
	public Map<String, Map<String,Object>> queryHouseByIdBatch(Collection<Long> ids){
		return houseMapper.queryHouseByIdBatch(ids);
	}
	
}
