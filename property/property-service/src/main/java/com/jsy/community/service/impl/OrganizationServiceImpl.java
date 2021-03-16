package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IOrganizationService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.OrganizationEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.OrganizationMapper;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.OrganizationVO;
import com.jsy.community.vo.TreeCommunityVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2021-03-15
 */
@DubboService(version = Const.version, group = Const.group_property)
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, OrganizationEntity> implements IOrganizationService {
	
	@Resource
	private OrganizationMapper organizationMapper;
	
	@Resource
	private CommunityMapper communityMapper;
	
	@Override
	public TreeCommunityVO listOrganization(Long communityId) {
		// 1. 查询所有组织
		QueryWrapper<OrganizationEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		List<OrganizationEntity> allOrganization = organizationMapper.selectList(wrapper);
		List<OrganizationVO> allOrganizationVO = new ArrayList<>();
		
		for (OrganizationEntity organizationEntity : allOrganization) {
			OrganizationVO organizationVO = new OrganizationVO();
			BeanUtils.copyProperties(organizationEntity, organizationVO);
			allOrganizationVO.add(organizationVO);
		}
		
		// 2. 获取所有根节点
		ArrayList<OrganizationVO> parents = new ArrayList<>();
		for (OrganizationVO organizationVO : allOrganizationVO) {
			// pid为0 就是根节点
			if (organizationVO.getPid() == 0) {
				parents.add(organizationVO);
			}
		}
		
		// 3. 根据sort字段进行排序
		Collections.sort(parents, order());
		
		// 4. 为根节点设置子节点，getClild是递归调用的
		for (OrganizationVO parent : parents) {
			List<OrganizationVO> childList = getChild(parent.getId(), allOrganizationVO);
			parent.setChildren(childList);
		}
		
		
		CommunityEntity communityEntity = communityMapper.selectById(communityId);
		TreeCommunityVO treeCommunityVO = new TreeCommunityVO();
		treeCommunityVO.setCommunityId(communityId).setCommunityName(communityEntity.getName()).setOrganizationVOList(parents);
		return treeCommunityVO;
	}
	
	@Override
	public void addOrganization(OrganizationEntity organizationEntity) {
		// 判断是否已经存在同名组织机构
		QueryWrapper<OrganizationEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", organizationEntity.getCommunityId());
		List<OrganizationEntity> organizationEntityList = organizationMapper.selectList(wrapper);
		if (organizationEntityList != null && organizationEntityList.size() > 0) {
			for (OrganizationEntity entity : organizationEntityList) {
				if (organizationEntity.getName().equals(entity.getName())) {
					throw new PropertyException("已存在同名组织机构，请重新添加");
				}
			}
		}
		
		organizationEntity.setId(SnowFlake.nextId());
		organizationMapper.insert(organizationEntity);
	}
	
	@Override
	public void deleteOrganization(Long id, Long communityId) {
		QueryWrapper<OrganizationEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id", id).eq("community_id", communityId);
		OrganizationEntity entity = organizationMapper.selectOne(queryWrapper);
		if (entity == null) {
			throw new PropertyException("您要删除的组织不存在，请重新选择");
		}
		
		QueryWrapper<OrganizationEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("pid", id).eq("community_id", communityId);
		List<OrganizationEntity> list = organizationMapper.selectList(wrapper);
		if (!CollectionUtils.isEmpty(list)) {
			throw new PropertyException("\'" + entity.getName() + "\'" + "已有下级节点,不可删除");
		}
		
		// TODO: 2021/3/16 判断是否有属于该机构的操作员
		
		
		organizationMapper.deleteById(id);
	}
	
	@Override
	public OrganizationEntity getOrganizationById(Long id, Long communityId) {
		QueryWrapper<OrganizationEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", id).eq("community_id", communityId);
		OrganizationEntity one = organizationMapper.selectOne(wrapper);
		if (one==null) {
			return new OrganizationEntity();
		}
		
		// 根据父id获取节点名称
		Long pid = one.getPid();
		QueryWrapper<OrganizationEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id", pid).eq("community_id", communityId);
		OrganizationEntity parent = organizationMapper.selectOne(queryWrapper);
		
		if (parent != null) {
			one.setParentName(parent.getName());
		} else {
			// 如果他没有父节点，那么父节点就展示他的社区名
			CommunityEntity communityEntity = communityMapper.selectById(communityId);
			one.setParentName(communityEntity.getName());
		}
		
		return one;
	}
	
	@Override
	public void updateOrganization(OrganizationEntity organization) {
		// 不可选择自己或自己的子集成为自己的父级
		// 1. 获取自己的子集
		QueryWrapper<OrganizationEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("pid", organization.getId());
		List<OrganizationEntity> childList = organizationMapper.selectList(wrapper);
		
		if (!CollectionUtils.isEmpty(childList)) {
			for (OrganizationEntity child : childList) {
				if (child.getId().equals(organization.getPid())||organization.getPid().equals(organization.getId())) {
					throw new PropertyException("不可选择自己或自己的子集成为自己的父级");
				}
			}
		}
		
		// 2. 判断是否有同名
		QueryWrapper<OrganizationEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id", organization.getId()).eq("community_id", organization.getCommunityId());
		OrganizationEntity one = organizationMapper.selectOne(queryWrapper);
		if (one == null) {
			throw new PropertyException("该小区没有此组织");
		}
		
		if (organization.getName().equals(one.getName())) {
			throw new PropertyException("您小区已存在同名组织，请重新修改");
		}
		organizationMapper.updateById(organization);
	}
	
	
	/*
	 * 排序,根据sort排序
	 */
	public Comparator<OrganizationVO> order() {
		Comparator<OrganizationVO> comparator = new Comparator<OrganizationVO>() {
			@Override
			public int compare(OrganizationVO o1, OrganizationVO o2) {
				if (!o1.getSort().equals(o2.getSort())) {
					return o1.getSort() - o2.getSort();
				}
				return 0;
			}
		};
		return comparator;
	}
	
	
	/**
	 * 获取子节点
	 *
	 * @param id            父节点id
	 * @param allDepartment 所有节点列表
	 * @return 每个根节点下，所有子菜单列表
	 */
	public List<OrganizationVO> getChild(Long id, List<OrganizationVO> allDepartment) {
		//子节点
		List<OrganizationVO> childList = new ArrayList<OrganizationVO>();
		for (OrganizationVO nav : allDepartment) {
			// 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
			//相等说明：为该根节点的子节点。
			if (id.equals(nav.getPid())) {
				childList.add(nav);
			}
		}
		//递归
		for (OrganizationVO nav : childList) {
			nav.setChildren(getChild(nav.getId(), allDepartment));
		}
		
		//排序
		Collections.sort(childList, order());
		
		//如果节点下没有子节点，返回一个空List（递归退出）
		if (childList.size() == 0) {
			return new ArrayList<OrganizationVO>();
		}
		return childList;
	}
}
