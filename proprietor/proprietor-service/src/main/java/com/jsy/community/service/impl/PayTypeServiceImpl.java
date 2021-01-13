package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.entity.PayTypeEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.PayCompanyMapper;
import com.jsy.community.mapper.PayTypeMapper;
import com.jsy.community.mapper.RegionMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 缴费类型 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-12-11
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class PayTypeServiceImpl extends ServiceImpl<PayTypeMapper, PayTypeEntity> implements IPayTypeService {
	@Resource
	private PayTypeMapper payTypeMapper;
	
	@Resource
	private PayCompanyMapper companyMapper;
	
	@Resource
	private RegionMapper regionMapper;
	
	@Override
	public List<PayTypeEntity> getPayTypes(Long id) {
		// 根据城市id 获取其下 缴费类型id集合
		List<Long> list = regionMapper.getListPayTypeId(id);
		
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		// 根据id批量查询缴费类型
		return payTypeMapper.selectBatchIds(list);
	}
	
	@Override
	@Transactional
	public void addPayType(Long id, PayTypeEntity payType) {
		String name = payType.getName();
		QueryWrapper<PayTypeEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("name", name);
		PayTypeEntity payTypeEntity = payTypeMapper.selectOne(wrapper);
		if (payTypeEntity != null) {
			Long dataId = payTypeEntity.getId(); // 缴费类型id
			
			// 拿到这两个玩意去中间表查 如果有说明已经添加过了
			Integer count = payTypeMapper.selectMiddle(id, dataId);
			if (count > 0) {
				throw new JSYException("对不起,该城市的缴费类型已经添加过了");
			}
			payTypeMapper.insertToMiddle(id, payTypeEntity.getId()); // 设置关联关系
		} else {
			PayTypeEntity entity = new PayTypeEntity();
			BeanUtils.copyProperties(payType, entity);
			payTypeMapper.insert(entity); // 添加一个新缴费类型
			payTypeMapper.insertToMiddle(id, entity.getId()); // 设置关联关系
		}
	}

	@Override
	public List<PayCompanyEntity> selectPayCompany(Long type, Long cityId,String name) {
		QueryWrapper<PayCompanyEntity> wrapper = new QueryWrapper<PayCompanyEntity>()
				.eq("region_id", cityId)
				.eq("type_id", type);
		if (name!=null&&!name.equals("")){
			wrapper.like("name",name);
		}
		List<PayCompanyEntity> list = companyMapper.selectList(wrapper);
		return list;
	}

	@Override
	public PageInfo<PayCompanyEntity> getPayCompany(BaseQO<PayCompanyEntity> baseQO, Long type, Long cityId) {
		String cityName = baseQO.getQuery().getName();
		Page<PayCompanyEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		QueryWrapper<PayCompanyEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(cityName)) {
			wrapper.eq("type_id", type).eq("region_id", cityId).like("name", cityName);
		} else {
			wrapper.eq("type_id", type).eq("region_id", cityId);
		}
		companyMapper.selectPage(page, wrapper);
		PageInfo<PayCompanyEntity> info = new PageInfo<>();
		BeanUtils.copyProperties(page,info);
		return info;
	}
}
