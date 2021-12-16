package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminRoleCompanyEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: DKS
 * @Description: 角色和物业公司关联表Mapper
 * @Date: 2021/12/16 15:18
 * @Version: 1.0
 **/
@Mapper
public interface AdminRoleCompanyMapper extends BaseMapper<AdminRoleCompanyEntity> {
}
