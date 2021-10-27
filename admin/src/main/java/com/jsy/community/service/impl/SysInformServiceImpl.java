package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.InformAcctEntity;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.InformAcctMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.mapper.SysInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.service.ISysInformService;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.property.PushInfromVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 系统消息业务类
 * @author YuLF
 * @since 2020-12-21 10:29
 **/
@Slf4j
@Service
public class SysInformServiceImpl extends ServiceImpl<SysInformMapper, PushInformEntity> implements ISysInformService {

	@Resource
	private SysInformMapper sysInformMapper;

	@Resource
	private CommunityMapper communityMapper;
	
	@Resource
	private PropertyCompanyMapper companyMapper;
	
	@Resource
	private InformAcctMapper informAcctMapper;

	@Override
	public boolean add(OldPushInformQO qo) {
		PushInformEntity sysInformEntity = PushInformEntity.getInstance();
		BeanUtils.copyProperties(qo, sysInformEntity);
		sysInformEntity.setId(SnowFlake.nextId());
		ElasticsearchImportProvider.elasticOperationSingle(sysInformEntity.getId(), RecordFlag.INFORM, Operation.INSERT, sysInformEntity.getPushTitle(), sysInformEntity.getAcctAvatar());
		return sysInformMapper.insert(sysInformEntity) > 0;
	}


	@Override
	public boolean delete(Long informId) {
		ElasticsearchImportProvider.elasticOperationSingle(informId, RecordFlag.INFORM, Operation.DELETE, null, null);
		return sysInformMapper.deleteById(informId) > 0;
	}

	@Override
	public List<PushInformEntity> query(BaseQO<OldPushInformQO> baseQo) {
		baseQo.setPage((baseQo.getPage() - 1 ) * baseQo.getSize());
		return sysInformMapper.query(baseQo);
	}

	@Override
	public boolean deleteBatchByIds(List<Long> informIds) {
		informIds.forEach( i -> ElasticsearchImportProvider.elasticOperationSingle(i, RecordFlag.INFORM, Operation.DELETE, null, null));
		return sysInformMapper.deleteBatchIds(informIds) > 0 ;
	}
	
	/**
	 * 添加大后台推送消息
	 * @param qo    接收消息参数的对象
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean addPushInform(PushInformQO qo){
		// 组装消息数据
		PushInformEntity entity = PushInformEntity.getInstance();
//        entity.setAcctId("");
//        entity.setAcctName();
		entity.setPushTitle(qo.getPushTitle());
		entity.setPushMsg(qo.getPushMsg());
		entity.setPushTarget(qo.getPushTarget());
		entity.setPushObject(qo.getPushObject());
		entity.setPushState(1);
		entity.setPushTag(qo.getPushTag());
		entity.setInformType("站内");
		entity.setCreateBy(qo.getUid());
		entity.setBrowseCount(0L);
		entity.setPublishBy(qo.getUid());
		entity.setPublishTime(LocalDateTime.now());
		entity.setCreateTime(LocalDateTime.now());
		entity.setId(SnowFlake.nextId());
		int insert = sysInformMapper.insert(entity);
		if (insert > 0) {
			// 消息新增成功时
			insetInformAcct(qo, entity.getId());
		}
		return insert > 0;
	}
	
	/**
	 * @author: DKS
	 * @description: 新增推送消息与推送者关系数据
	 * @param qo: 推送消息qo
	 * @param informId: 推送消息id
	 * @return: void
	 * @date: 2021/10/27 11:00
	 **/
	private void insetInformAcct(PushInformQO qo, Long informId) {
		List<InformAcctEntity> informAcctEntities = new ArrayList<>();
		// 组装消息与推送号的关系数据
		InformAcctEntity informAcctEntity = new InformAcctEntity();
		informAcctEntity.setId(String.valueOf(SnowFlake.nextId()));
		informAcctEntity.setInformId(String.valueOf(informId));
		informAcctEntity.setAcctId("0");
		informAcctEntity.setAcctName("纵横世纪");
		informAcctEntity.setPushObject(qo.getPushObject());
		informAcctEntities.add(informAcctEntity);
		//当某个推送号有新消息发布时：用户之前已经删除的 推送号 又会被拉取出来 同时通知有未读消息
		//清除推送消息屏蔽表
		sysInformMapper.clearPushDel(0L);
		if (CollectionUtil.isNotEmpty(informAcctEntities)) {
			// 新增消息与推送号的关系数据
			informAcctMapper.insertBatch(informAcctEntities);
		}
	}
	
	/**
	 *
	 * 逻辑删除 推送消息
	 * @param id    推送消息id
	 * @return      返回删除成功
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean deletePushInform(Long id, String updateAdminId) {
		//物理删除：删除该条社区消息之前 先删除 所有用户已读消息记录
		sysInformMapper.delUserReadInform(id);
		ElasticsearchImportProvider.elasticOperationSingle(id, RecordFlag.INFORM, Operation.DELETE, null, null);
		// 逻辑删除消息信息,更新操作员
		Integer integer = sysInformMapper.updateDeleted(id, updateAdminId);
		return integer > 0;
	}
	
	/**
	 *@Author: DKS
	 *@Description: 获取单条消息详情
	 *@Param: id: 消息ID
	 *@Return: com.jsy.community.entity.PushInformEntity
	 *@Date: 2021/10/27 14:32
	 **/
	@Override
	public PushInfromVO getDetail(Long id) {
		PushInformEntity pushInformEntity = sysInformMapper.selectById(id);
		PushInfromVO pushInfromVO = new PushInfromVO();
		if (pushInformEntity == null) {
			return pushInfromVO;
		}
		BeanUtils.copyProperties(pushInformEntity, pushInfromVO);
		// 补充推送对象
		List<String> pushObjectNames = new ArrayList<>();
		List<Long> list = MyMathUtils.analysisTypeCode(pushInformEntity.getPushObject(), 3);
		for (Long aLong : list) {
			if (aLong == 1) {
				pushObjectNames.add("物业");
			} else if (aLong == 2) {
				pushObjectNames.add("小区");
			} else if (aLong == 4) {
				pushObjectNames.add("商家");
			}
		}
		pushInfromVO.setPushObjectName(pushObjectNames);
		return pushInfromVO;
	}
}
