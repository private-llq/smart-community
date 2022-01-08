package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.AdminUserRoleService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminRoleCompanyEntity;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import com.jsy.community.mapper.AdminRoleCompanyMapper;
import com.jsy.community.mapper.AdminUserRoleMapper;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DubboService(version = Const.version, group = Const.group_property)
public class AdminUserRoleServiceImpl extends ServiceImpl<AdminUserRoleMapper, AdminUserRoleEntity> implements AdminUserRoleService {
    @Resource
    private AdminUserRoleMapper adminUserRoleMapper;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseRoleRpcService baseRoleRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    @Autowired
    private AdminRoleCompanyMapper adminRoleCompanyMapper;

    /**
     * @author: DKS
     * @description:
     * @param userId: 用户id
     * @param commpanyId: 物业公司id
     * @return: {@link Long}
     * @date: 2021/12/23 17:49
     **/
    @Override
    public Long selectRoleIdByUserId(String userId, Long commpanyId) {
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(userId);
        if (userDetail != null) {
            List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.COMMUNITY_ADMIN);
            if (!CollectionUtils.isEmpty(permitRoles)) {
                Set<Long> roleIdSet = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
                QueryWrapper<AdminRoleCompanyEntity> adminRoleCompanyEntityQueryWrapper = new QueryWrapper<>();
                adminRoleCompanyEntityQueryWrapper.eq("company_id", commpanyId);
                adminRoleCompanyEntityQueryWrapper.in("role_id", roleIdSet);
                adminRoleCompanyEntityQueryWrapper.last("limit 1");
                AdminRoleCompanyEntity adminRoleCompanyEntity = adminRoleCompanyMapper.selectOne(adminRoleCompanyEntityQueryWrapper);
                return adminRoleCompanyEntity.getRoleId();
            }
        }
        return null;
    }
}
