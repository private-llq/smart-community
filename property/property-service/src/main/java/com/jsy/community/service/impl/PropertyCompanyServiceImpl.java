package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyCompanyService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.admin.AdminUserCompanyEntity;
import com.jsy.community.mapper.AdminUserCompanyMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @program: com.jsy.community
 * @description: 物业公司
 * @author: Hu
 * @create: 2021-08-20 15:06
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyCompanyServiceImpl extends ServiceImpl<PropertyCompanyMapper, PropertyCompanyEntity> implements IPropertyCompanyService {
    @Resource
    private PropertyCompanyMapper propertyCompanyMapper;
    
    @Resource
    private AdminUserCompanyMapper adminUserCompanyMapper;


    /**
     * @Description: 查询当前小区的物业公司
     * @author: Hu
     * @since: 2021/8/20 15:09
     * @Param: [id]
     * @return: com.jsy.community.entity.PropertyCompanyEntity
     */
    @Override
    public PropertyCompanyEntity findOne(Long id) {
        return propertyCompanyMapper.selectOne(new QueryWrapper<PropertyCompanyEntity>().select("id,\n" +
                "\t`name`,\n" +
                "\t`profile`,\n" +
                "\tpicture,\n" +
                "\tdeleted,\n" +
                "\tcreate_time,\n" +
                "\tupdate_time ").eq("id",id));
    }
    
    /**
     * @author: DKS
     * @description: 根据物业公司id获取物业公司名称
     * @param companyId :
     * @return: com.jsy.community.entity.PropertyCompanyEntity
     * @date: 2021/8/25 16:32
     **/
    @Override
    public String getCompanyNameByCompanyId(Long companyId) {
        return propertyCompanyMapper.selectCompanyNameByCompanyId(companyId);
    }
    
    /**
     * @author: DKS
     * @description: 物业端-系统设置-短信配置
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/1 11:50
     **/
    @Override
    public Boolean updateSMSConfig(PropertyCompanyEntity propertyCompanyEntity) {
        int i = propertyCompanyMapper.updateById(propertyCompanyEntity);
        return i == 1;
    }
    
    /**
     * @author: DKS
     * @description: 通过物业公司id查询物业公司详情
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/1 11:50
     **/
    @Override
    public PropertyCompanyEntity selectCompany(Long companyId) {
        return propertyCompanyMapper.selectOne(new QueryWrapper<PropertyCompanyEntity>().select("*").eq("id", companyId));
    }
    
    /**
     * @Description: 修改物业公司
     * @author: DKS
     * @since: 2021/12/13 17:05
     * @Param: [propertyCompanyEntity]
     * @return: void
     */
    @Override
    public void updatePropertyCompany(PropertyCompanyEntity propertyCompanyEntity) {
        propertyCompanyMapper.updateById(propertyCompanyEntity);
    }
    
    /**
     * @Description: 根据uid查询物业公司id
     * @author: DKS
     * @since: 2021/12/21 15:21
     * @Param: [uid]
     * @return: java.lang.Long
     */
    @Override
    public Long getPropertyCompanyIdByUid(String uid) {
        AdminUserCompanyEntity entity = adminUserCompanyMapper.selectOne(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", uid).eq("deleted", 0));
        return entity.getCompanyId();
    }
}
