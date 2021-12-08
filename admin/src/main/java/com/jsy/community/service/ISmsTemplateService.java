package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsTemplateEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SmsTemplateQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * @Description: 短信模板
 * @author: DKS
 * @since: 2021/12/8 11:33
 */
public interface ISmsTemplateService extends IService<SmsTemplateEntity>{
    /**
     * @Description: 新增短信模板
     * @author: DKS
     * @since: 2021/12/8 11:33
     * @Param: [SmsTemplateEntity]
     * @return: boolean
     */
    boolean addSmsTemplate(SmsTemplateEntity smsTemplateEntity);
    
    /**
     * @Description: 修改短信模板
     * @author: DKS
     * @since: 2021/12/8 11:33
     * @Param: [smsTemplateEntity]
     * @return: boolean
     */
    boolean updateSmsTemplate(SmsTemplateEntity smsTemplateEntity);
    
    /**
     * @Description: 删除短信模板
     * @author: DKS
     * @since: 2021/12/8 11:33
     * @Param: [id]
     * @return: boolean
     */
    boolean deleteSmsTemplate(Long id);
    
    /**
     * @Description: 查询短信模板列表
     * @author: DKS
     * @since: 2021/12/8 11:33
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.SmsTemplateEntity>
     */
    List<SmsTemplateEntity> selectSmsTemplate();
    
    /**
     * @Description: 短信模板分页查询
     * @author: DKS
     * @since: 2021/12/8 11:51
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsTemplateEntity>
     */
    PageInfo<SmsTemplateEntity> querySmsTemplatePage(BaseQO<SmsTemplateQO> baseQO);
}
