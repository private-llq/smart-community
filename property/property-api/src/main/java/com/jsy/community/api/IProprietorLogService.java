package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.log.ProprietorLog;

/**
 * <p>
 * 业主操作日志 服务类
 * </p>
 *
 * @author jsy
 * @since 2021-01-22
 */
public interface IProprietorLogService extends IService<ProprietorLog> {
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 保存业主操作记录
	 * @Date 2021/1/23 13:37
	 * @Param [proprietorLog]
	 **/
	void saveProprietorLog(ProprietorLog proprietorLog);
}
