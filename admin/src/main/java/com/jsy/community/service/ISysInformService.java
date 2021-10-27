package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.vo.property.PushInfromVO;

import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-21 11:39
 */
public interface ISysInformService extends IService<PushInformEntity> {

    /**
     * 添加系统推送消息
     * @author YuLF
     * @param sysInformQo 系统请求参数
     * @since  2021/1/13 17:37
     * @return  返回是否添加成功
     */
    boolean add(OldPushInformQO sysInformQo);

    /**
     * 根据消息id删除
     * @param informId   消息id
     * @return           返回删除成功
     */
    boolean delete(Long informId);

    List<PushInformEntity> query(BaseQO<OldPushInformQO> baseQo);

    boolean deleteBatchByIds(List<Long> informIds);
    
    /**
     * 新增
     * @param qo  参数实体
     * @return    返回新增 insert 行数 > 0 的结果
     */
    Boolean addPushInform(PushInformQO qo);
	
	/**
	 * 根据id删除社区推送通知消息
	 * @param id            推送消息id
	 * @return              返回删除是否成功
	 */
	Boolean deletePushInform(Long id, String updateAdminId);
	
	/**
	 *@Author: DKS
	 *@Description: 获取单条消息详情
	 *@Param: id: 消息ID
	 *@Return: com.jsy.community.entity.PushInformEntity
	 *@Date: 2021/10/27 14:31
	 **/
	PushInfromVO getDetail(Long id);
}
