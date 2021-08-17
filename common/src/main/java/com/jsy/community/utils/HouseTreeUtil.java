package com.jsy.community.utils;

import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.HouseEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DKS
 * @description 楼栋、单元、房屋树形机构
 * @since 2021/8/13  13:46
 **/
public class HouseTreeUtil {
	
	public List<HouseEntity> menuCommon;
	public List<Object> list = new ArrayList<>();
	
	public List<Object> menuList(List<HouseEntity> menu){
		this.menuCommon = menu;
		for (HouseEntity x : menu) {
			Map<String,Object> mapArr = new LinkedHashMap<>();
			if(x.getPid().equals(0L)){
				mapArr.put("id", x.getId());
				mapArr.put("idStr", String.valueOf(x.getId()));
				mapArr.put("type", x.getType());
				mapArr.put("building", x.getBuilding());
				mapArr.put("pid", x.getPid());
				mapArr.put("pidStr", String.valueOf(x.getPid()));
				mapArr.put("childList", menuChild(x.getId()));
				list.add(mapArr);
			}
		}
		return list;
	}
	
	public List<Object> menuChild(Long id){
		List<Object> lists = new ArrayList<>();
		for(HouseEntity a:menuCommon){
			Map<String,Object> childArray = new LinkedHashMap<>();
			if(a.getPid().equals(id)){
				childArray.put("id", a.getId());
				childArray.put("idStr", String.valueOf(a.getId()));
				if (BusinessConst.BUILDING_TYPE_UNIT == a.getType()) {
					childArray.put("unit", a.getUnit());
				} else if (BusinessConst.BUILDING_TYPE_DOOR == a.getType()) {
					childArray.put("door", a.getDoor());
				}
				childArray.put("pid", a.getPid());
				childArray.put("pidStr", String.valueOf(a.getPid()));
				childArray.put("childList", menuChild(a.getId()));
				lists.add(childArray);
			}
		}
		return lists;
	}
}
