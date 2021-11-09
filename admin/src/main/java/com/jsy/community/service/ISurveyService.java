package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.vo.sys.SurveyVo;

/**
 * <p>
 * 社区 服务类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-25
 */
public interface ISurveyService extends IService<SurveyVo> {
	
	/**
	 * @Description: 获取大后台概况
	 * @author: DKS
	 * @since: 2021/11/9 10:58
	 * @Param: []
	 * @return: com.jsy.community.vo.sys.SurveyVo
	 */
	SurveyVo getSurvey();
}
