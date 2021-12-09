package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SmsPurchaseRecordEntity;
import com.jsy.community.mapper.SmsPurchaseRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SmsPurchaseRecordQO;
import com.jsy.community.service.ISmsPurchaseRecordService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信购买记录
 * @author: DKS
 * @create: 2021-12-09 15:30
 **/
@Slf4j
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@Service
public class SmsPurchaseRecordServiceImpl extends ServiceImpl<SmsPurchaseRecordMapper, SmsPurchaseRecordEntity> implements ISmsPurchaseRecordService {
    
    @Resource
    private SmsPurchaseRecordMapper smsPurchaseRecordMapper;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;
	
	/**
	 * @Description: 查询短信购买记录
	 * @Param: [smsPurchaseRecordQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
	 * @Author: DKS
	 * @Date: 2021/12/9
	 **/
	@Override
	public PageInfo<SmsPurchaseRecordEntity> querySmsPurchaseRecord(BaseQO<SmsPurchaseRecordQO> baseQO) {
		SmsPurchaseRecordQO query = baseQO.getQuery();
		Page<SmsPurchaseRecordEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<SmsPurchaseRecordEntity> queryWrapper = new QueryWrapper<>();
		// 查状态
		if (query.getStatus() != null) {
			queryWrapper.eq("status", query.getStatus());
		}
		// 查关键字
		if (StringUtils.isNotBlank(query.getKeyword())) {
			// 通过关键字模糊查询mobile返回用户uid
			PageVO<UserDetail> userDetailPageVO = baseUserInfoRpcService.queryUser(query.getKeyword(), "", 0, 999999999);
			List<Long> uidList = new ArrayList<>();
			for (UserDetail userDetail : userDetailPageVO.getData()) {
				uidList.add(userDetail.getId());
			}
			if (uidList.size() > 0) {
				queryWrapper.in("pay_by", uidList);
			} else {
				return new PageInfo<>();
			}
		}
		queryWrapper.orderByDesc("create_time");
		Page<SmsPurchaseRecordEntity> pageData = smsPurchaseRecordMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		// 补充数据
		for (SmsPurchaseRecordEntity smsPurchaseRecordEntity : pageData.getRecords()) {
			// 补充状态
			smsPurchaseRecordEntity.setStatusName(smsPurchaseRecordEntity.getStatus() == 1 ? "已付款" : "未付款");
			// 补充支付账号
			if (smsPurchaseRecordEntity.getPayBy() != null) {
				smsPurchaseRecordEntity.setPayByPhone(baseUserInfoRpcService.getUserDetail(Long.parseLong(smsPurchaseRecordEntity.getPayBy())).getPhone());
			}
		}
		PageInfo<SmsPurchaseRecordEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
	
	/**
	 * @Description: 批量删除短信购买记录
	 * @author: DKS
	 * @since: 2021/12/9 17:07
	 * @Param: [ids]
	 * @return: boolean
	 */
	@Override
	public boolean deleteIds(List<Long> ids) {
		return smsPurchaseRecordMapper.deleteBatchIds(ids) > 0;
	}
}