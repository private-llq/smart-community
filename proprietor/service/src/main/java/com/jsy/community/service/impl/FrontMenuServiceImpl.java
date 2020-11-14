package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFrontMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FrontMenuEntity;
import com.jsy.community.mapper.FrontMenuMapper;
import com.jsy.community.qo.BaseQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-14
 */
@DubboService(version = Const.version, group = Const.group)
public class FrontMenuServiceImpl extends ServiceImpl<FrontMenuMapper, FrontMenuEntity> implements IFrontMenuService {
	
	@Autowired
	private FrontMenuMapper frontMenuMapper;
	
	@Override
	public Integer saveMenu(FrontMenuEntity menuEntity) {
		return frontMenuMapper.insert(menuEntity);
	}
	
	@Override
	public Integer updateMenu(FrontMenuEntity menuEntity) {
		return frontMenuMapper.updateById(menuEntity);
	}
	
	@Override
	public List<FrontMenuEntity> listFrontMenu(BaseQO<FrontMenuEntity> baseQO) {
		Page<FrontMenuEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		QueryWrapper<FrontMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.eq("menu_name",baseQO.getQuery().getMenuName()).or().eq()
		return null;
	}
}
