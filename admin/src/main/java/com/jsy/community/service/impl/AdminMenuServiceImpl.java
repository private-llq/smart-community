package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.mapper.AdminMenuMapper;
import com.jsy.community.service.IAdminMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-24
 */
@Service
public class AdminMenuServiceImpl extends ServiceImpl<AdminMenuMapper, AdminMenuEntity> implements IAdminMenuService {

}
