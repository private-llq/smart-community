package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.SmsTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsTemplateMapper extends BaseMapper<SmsTemplateEntity> {
	
	/**
	 * @Description: 根据短信分类id查询是否存在短信模板
	 * @author: DKS
	 * @since: 2021/12/8 12:00
	 * @Param: java.lang.Long
	 * @return: com.jsy.community.entity.SmsTemplateEntity
	 */
	Integer selectSmsTemplateBySmsTypeId(Long smsTypeId);
}
